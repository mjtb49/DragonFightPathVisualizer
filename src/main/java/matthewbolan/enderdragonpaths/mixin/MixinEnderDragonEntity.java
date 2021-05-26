package matthewbolan.enderdragonpaths.mixin;

import matthewbolan.enderdragonpaths.DragonFightDebugger;
import matthewbolan.enderdragonpaths.util.BedDamageSettings;
import net.minecraft.block.BedBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.boss.dragon.phase.PhaseManager;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import net.minecraft.entity.ai.pathing.Path;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import matthewbolan.enderdragonpaths.render.Color;
import matthewbolan.enderdragonpaths.render.Cube;
import matthewbolan.enderdragonpaths.render.Line;

import java.util.concurrent.ConcurrentLinkedQueue;

@Mixin(EnderDragonEntity.class)
public abstract class MixinEnderDragonEntity extends LivingEntity {

   @Shadow @Final
   private PhaseManager phaseManager;

   @Shadow @Final
   private PathNode[] pathNodes;

   @Shadow @Final
   private int[] pathNodeConnections;

   @Shadow @Final public EnderDragonPart partHead;
   @Shadow @Final private EnderDragonPart[] parts;

   @Shadow public abstract boolean damage(DamageSource source, float amount);

   private boolean graphInitialized;

   private Vec3d last = null;
   private float lastHealth = 200.0f;
   private double lastYPos = 0.0f;

   public MixinEnderDragonEntity(EntityType<? extends EnderDragonEntity> entityType, World world) {
      super(entityType, world);
   }

   @Inject(method = "tickMovement()V", at = @At("TAIL"))
   public void tickMovement(CallbackInfo ci) {
      if (lastHealth > this.getHealth() && !this.world.isClient()) {
         float damage = lastHealth - this.getHealth();
         if (MinecraftClient.getInstance().player != null)
            MinecraftClient.getInstance().player.sendMessage(new LiteralText("Dragon took " + damage + " damage").formatted(Formatting.RED), false);
      }
      lastHealth = this.getHealth();

      Vec3d target = phaseManager.getCurrent().getTarget();
      if (target != null && !this.world.isClient()) {
         double x = target.getX();
         double y = target.getY();
         double z = target.getZ();
         Cube cube = new Cube(new BlockPos(x, y, z), Color.ORANGE);
         DragonFightDebugger.submitTarget(cube);
      }
      if (!this.world.isClient()) {
         //TODO work out how we want to show server client desync
         double x = this.partHead.getX();
         double y = this.partHead.getEyeY();
         double z = this.partHead.getZ();
         Vec3d newPos = new Vec3d(x, y, z);
         if (last != null) {
            DragonFightDebugger.submitHeadPosition(new Line(newPos, last, Color.PURPLE));
         }
         last = newPos;
      }

      ConcurrentLinkedQueue<BlockPos> bedPositions = BedDamageSettings.getBedPositions();
      if (!this.world.isClient()) {
         for (BlockPos bedPos : bedPositions) {
            if (!(this.world.getBlockState(bedPos).getBlock() instanceof BedBlock)) {
               bedPositions.remove(bedPos);
            } else {
               boolean printDamage = BedDamageSettings.shouldPrintDamage();
               if (bedPos != null && printDamage) {
                  Vec3d bedCenter = Vec3d.ofCenter(bedPos);
                  double powerTimes2 = 2.0 * 5.0;
                  float maxDamage = 0;
                  for (EnderDragonPart part : parts) {
                     double y = (MathHelper.sqrt(part.squaredDistanceTo(bedCenter)) / powerTimes2);
                     if (y <= 1.0D) {
                        //TODO compute exposure correctly, currently the bed head blocks the explosion
                        double exposure = 1; //Explosion.getExposure(bedCenter, part);
                        double ae = (1.0D - y) * exposure;
                        float damage = (float) ((ae * ae + ae) / 2.0D * 7.0D * powerTimes2 + 1.0D);
                        if (part != this.partHead)
                           damage = ((int) damage) / 4.0F + Math.min(damage, 1.0F);
                        else
                           damage = (float) (int) damage;
                        if (damage > maxDamage)
                           maxDamage = damage;
                     }
                  }
                  if ((int) maxDamage >= BedDamageSettings.getDamageThreshold()) {
                     if (bedPositions.size() == 1) {
                        if (MinecraftClient.getInstance().player != null)
                           MinecraftClient.getInstance().player.sendMessage(new LiteralText(maxDamage + " damage at time " + this.age).formatted(Formatting.AQUA), false);
                     } else {
                        if (MinecraftClient.getInstance().player != null)
                           MinecraftClient.getInstance().player.sendMessage(new LiteralText(maxDamage + " damage at time " + this.age + " from " + bedPos.getX() + " " + bedPos.getY() + " " + bedPos.getZ()).formatted(Formatting.AQUA), false);
                     }
                  }
               }
            }
         }
      }

      //if (!this.world.isClient()) {
      //   if (this.getVelocity() != null  && MinecraftClient.getInstance().player != null) {
      //      MinecraftClient.getInstance().player.sendMessage(new LiteralText("Velocity internal " + this.getVelocity().getY() + " at time " + this.age).formatted(Formatting.LIGHT_PURPLE), false);
      //      MinecraftClient.getInstance().player.sendMessage(new LiteralText("Position Delta " + (this.getY() - this.lastYPos) + " at time " + this.age).formatted(Formatting.GREEN), false);
      //      this.lastYPos = this.getY();
      //   }
      //}
   }

   @Inject(method = "findPath(IILnet/minecraft/entity/ai/pathing/PathNode;)Lnet/minecraft/entity/ai/pathing/Path;", at = @At("RETURN"))
   public void findPath(int i, int j, PathNode node, CallbackInfoReturnable<Path> ci) {
      DragonFightDebugger.submitPath(ci.getReturnValue(), phaseManager.getCurrent());
   }

   @Inject(method = "getNearestPathNodeIndex()I", at = @At(value = "TAIL"))
   public void getNearestPathNodeIndex(CallbackInfoReturnable<Integer> cir) {
      if(!graphInitialized) {
         DragonFightDebugger.clearGraph();

         //Render base graph
         for (int i = 0; i < 24; i++) {
            Cube coob = new Cube(pathNodes[i].getPos(), Color.GRAY);
            for (int j = i; j < 24; j++) {
               if ((pathNodeConnections[i] & (1 << j)) != 0) {
                  if ((pathNodeConnections[j] & (1 << i)) == 0) {
                     System.err.println("graph is actually a digraph!");
                  }
                  Line line = new Line(Vec3d.ofCenter(pathNodes[i].getPos()), Vec3d.ofCenter(pathNodes[j].getPos()), Color.GRAY);
                  DragonFightDebugger.submitElement(line);
               }
            }
            DragonFightDebugger.submitElement(coob);
         }

         //Render possible node paths
         //for (int j = 12; j < 20; j+=4) {
         //int j = 12; //EAST = 12, //WEST = 16
         //int k;
         //Cube coob = new Cube(pathNodes[j].getPos(), Color.PURPLE);
         //k = getDestinationIndex(j, true, true);
         //Line line1 = new Line(Vec3d.ofCenter(pathNodes[j].getPos()), Vec3d.ofCenter(pathNodes[k].getPos()), Color.PURPLE);
         //k = getDestinationIndex(j, true, false);
         //Line line2 = new Line(Vec3d.ofCenter(pathNodes[j].getPos()), Vec3d.ofCenter(pathNodes[k].getPos()), Color.ORANGE);
         //k = getDestinationIndex(j, false, true);
         //Line line3 = new Line(Vec3d.ofCenter(pathNodes[j].getPos()), Vec3d.ofCenter(pathNodes[k].getPos()), Color.GREEN);
         //k = getDestinationIndex(j, false, false);
         //Line line4 = new Line(Vec3d.ofCenter(pathNodes[j].getPos()), Vec3d.ofCenter(pathNodes[k].getPos()), Color.YELLOW);
         //DragonFightDebugger.submitElement(line1);
         //DragonFightDebugger.submitElement(line2);
         //DragonFightDebugger.submitElement(line3);
         //DragonFightDebugger.submitElement(line4);
         //DragonFightDebugger.submitElement(coob);
         //}

      }
      graphInitialized = true;
   }

   private int getDestinationIndex(int j, boolean clockwise, boolean swapDirections) {
      int k = j;
      if (swapDirections) {
         clockwise = !clockwise;
         k = j + 6;
      }

      if (clockwise) {
         ++k;
      } else {
         --k;
      }
      k %= 12;
      if (k < 0) {
         k += 12;
      }
      return k;
   }
}