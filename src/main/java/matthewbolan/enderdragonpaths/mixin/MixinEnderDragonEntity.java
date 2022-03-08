package matthewbolan.enderdragonpaths.mixin;

import matthewbolan.enderdragonpaths.DragonFightDebugger;
import matthewbolan.enderdragonpaths.util.HackyWorkaround;
import matthewbolan.enderdragonpaths.util.ExplosionTracker;
import matthewbolan.enderdragonpaths.util.DragonTracker;
import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.boss.dragon.phase.PhaseManager;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;
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

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

@Mixin(EnderDragonEntity.class)
public abstract class MixinEnderDragonEntity extends LivingEntity implements HackyWorkaround {

   @Shadow @Final
   private PhaseManager phaseManager;

   @Shadow @Final
   private PathNode[] pathNodes;

   @Shadow @Final
   private int[] pathNodeConnections;

   @Shadow @Final public EnderDragonPart partHead;
   @Shadow @Final private EnderDragonPart[] parts;

   @Shadow public abstract boolean damage(DamageSource source, float amount);

   @Shadow @Final private @Nullable EnderDragonFight fight;

   @Shadow public abstract EnderDragonPart[] getBodyParts();

   private boolean graphInitialized;
   private boolean isComputingFakePaths;
   private boolean lastDirection = true;

   private Vec3d last = null;
   private float lastHealth = 200.0f;
   private double lastYPos = 0.0f;

   public MixinEnderDragonEntity(EntityType<? extends EnderDragonEntity> entityType, World world) {
      super(entityType, world);
   }

   @Inject(method = "<init>(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/World;)V", at = @At("TAIL"))
   public void EnderDragonEntity(EntityType<? extends EnderDragonEntity> entityType, World world, CallbackInfo ci) {
      //original sin
      if (!world.isClient())
         DragonFightDebugger.setEnderDragonEntity((EnderDragonEntity) (Object) this);
   }

   @Inject(method = "tickMovement()V", at = @At("TAIL"))
   public void tickMovement(CallbackInfo ci) {

      //Highlight Node Closest to dragon
      handleClosestTargets();

      //Damage Tracking
      if (!this.world.isClient()) {
         if (lastHealth > this.getHealth()) {
            float damage = lastHealth - this.getHealth();
            if (MinecraftClient.getInstance().player != null && DragonTracker.shouldPrintDamageDone())
               MinecraftClient.getInstance().player.sendMessage(new LiteralText("Dragon took " + damage + " damage").formatted(Formatting.RED), false);
         }
         lastHealth = this.getHealth();
      }

      //Target Tracking
      //TODO find out if this thing rounding target position is correct
      Vec3d target = phaseManager.getCurrent().getTarget();
      if (target != null && !this.world.isClient()) {
         double x = target.getX();
         double y = target.getY();
         double z = target.getZ();
         Cube cube = new Cube(new Vec3d(x, y, z), Color.ORANGE);
         DragonFightDebugger.submitTarget(cube);
      }

      //Tracer
      if (!this.world.isClient()) {
         //TODO work out how we want to show server client desync
         double x = this.partHead.getX();
         double y = this.partHead.getY();
         double z = this.partHead.getZ();
         Vec3d newPos = new Vec3d(x, y, z);
         if (last != null) {
            DragonFightDebugger.submitHeadPosition(new Line(newPos, last, Color.PURPLE));
         }
         last = newPos;
      }



      ConcurrentLinkedQueue<BlockPos> explosionPositions = ExplosionTracker.getBedPositions();
      List<EndCrystalEntity> crystals = this.world.getNonSpectatingEntities(EndCrystalEntity.class, this.getBoundingBox().expand(10.0D));
      boolean printPositions = (crystals.size() + explosionPositions.size()) > 1;
      if (!this.world.isClient()) {
         //Crystal Explosion Damage Computing
         for (EndCrystalEntity crystal: crystals) {
            Vec3d explosionCenter = new Vec3d(crystal.getX(), crystal.getY(), crystal.getZ());
            if (ExplosionTracker.shouldPrintDamage()) {
               double power = 6.0;
               float[] bestDamage = new float[]{0,0};
               for (EnderDragonPart part : parts) {
                  double y = (MathHelper.sqrt(part.squaredDistanceTo(explosionCenter)) / (power * 2.0));
                  if (y <= 1.0D) {
                     double exposure = Explosion.getExposure(explosionCenter, part);
                     float[] damage = computeDamage(part, power, exposure, explosionCenter);
                     if (damage[0] >  bestDamage[0])
                        bestDamage = damage;
                  }
               }
               if ((int) bestDamage[0] >= ExplosionTracker.getDamageThreshold()) {
                  if (printPositions) {
                     if (MinecraftClient.getInstance().player != null)
                        MinecraftClient.getInstance().player.sendMessage(new LiteralText(bestDamage[0] + " damage (" + (bestDamage[1] - bestDamage[0]) + " blocked) at time " + this.age).formatted(Formatting.AQUA), false);
                  } else {
                     if (MinecraftClient.getInstance().player != null)
                        MinecraftClient.getInstance().player.sendMessage(new LiteralText(bestDamage[0] + " damage (" + (bestDamage[1] - bestDamage[0]) + " blocked) at time " + this.age + " from " + (int)crystal.getX() + " " + (int)crystal.getY() + " " + (int)crystal.getZ()).formatted(Formatting.AQUA), false);
                  }
               }
            }
         }

         //Block Explosion Damage Computing
         for (BlockPos explosionPos : explosionPositions) {
            if (!(this.world.getBlockState(explosionPos).getBlock() instanceof BedBlock) && !(this.world.getBlockState(explosionPos).getBlock() instanceof RespawnAnchorBlock)) {
               explosionPositions.remove(explosionPos);
            } else {
               boolean printDamage = ExplosionTracker.shouldPrintDamage();
               if (explosionPos != null && printDamage) {
                  Vec3d explosionCenter = Vec3d.ofCenter(explosionPos);
                  double power = 5.0;
                  float[] bestDamage = new float[]{0,0};
                  for (EnderDragonPart part : parts) {
                     double y = (MathHelper.sqrt(part.squaredDistanceTo(explosionCenter)) / (power * 2.0));
                     if (y <= 1.0D) {
                        double exposure = 0.0D;
                        if (this.world.getBlockState(explosionPos).getBlock() instanceof BedBlock) {
                           BlockPos foot = getFoot(explosionPos);
                           BlockState headState = world.getBlockState(explosionPos);
                           BlockState footState = world.getBlockState(foot);
                           world.setBlockState(explosionPos, Blocks.AIR.getDefaultState(), 16 + 128);
                           world.setBlockState(foot, Blocks.AIR.getDefaultState(), 16 + 128);
                           exposure = Explosion.getExposure(explosionCenter, part);
                           world.setBlockState(explosionPos, headState, 16 + 128);
                           world.setBlockState(foot, footState, 16 + 128);
                        } else if (this.world.getBlockState(explosionPos).getBlock() instanceof RespawnAnchorBlock) {
                           BlockState anchorState = world.getBlockState(explosionPos);
                           world.setBlockState(explosionPos, Blocks.AIR.getDefaultState(), 16 + 128);
                           exposure = Explosion.getExposure(explosionCenter, part);
                           world.setBlockState(explosionPos, anchorState, 16 + 128);
                        }
                        float[] damage = computeDamage(part, power, exposure, explosionCenter);
                        if (damage[0] >  bestDamage[0])
                           bestDamage = damage;
                     }
                  }
                  if ((int) bestDamage[0] >= ExplosionTracker.getDamageThreshold()) {
                     if (printPositions) {
                        if (MinecraftClient.getInstance().player != null)
                           MinecraftClient.getInstance().player.sendMessage(new LiteralText(bestDamage[0] + " damage (" + (bestDamage[1] - bestDamage[0]) + " blocked) at time " + this.age).formatted(Formatting.AQUA), false);
                     } else {
                        if (MinecraftClient.getInstance().player != null)
                           MinecraftClient.getInstance().player.sendMessage(new LiteralText(bestDamage[0] + " damage (" + (bestDamage[1] - bestDamage[0]) + " blocked) at time " + this.age + " from " + explosionPos.getX() + " " + explosionPos.getY() + " " + explosionPos.getZ()).formatted(Formatting.AQUA), false);
                     }
                  }
               }
            }
         }
      }

      //Y movement tracking
     if (!this.world.isClient()) {
        if (this.getVelocity() != null  && MinecraftClient.getInstance().player != null && DragonTracker.shouldPrintYDelta()) {
           //MinecraftClient.getInstance().player.sendMessage(new LiteralText("Velocity internal " + this.getVelocity().getY() + " at time " + this.age).formatted(Formatting.LIGHT_PURPLE), false);
           MinecraftClient.getInstance().player.sendMessage(new LiteralText("Position Delta " + (this.getY() - this.lastYPos) + " at time " + this.age).formatted(Formatting.GREEN), false);
           MinecraftClient.getInstance().player.sendMessage(new LiteralText("Y " + this.getY() + " at time " + this.age).formatted(Formatting.GREEN), false);
           MinecraftClient.getInstance().player.sendMessage(new LiteralText("Facing " + (this.getRotationClient().y) + " at time " + this.age).formatted(Formatting.GREEN), false);
           if (this.partHead != null)
              MinecraftClient.getInstance().player.sendMessage(new LiteralText("Damage done at " + this.partHead.getX() + " " + this.partHead.getY() + " " + this.partHead.getZ()).formatted(Formatting.GREEN), false);
        }
        this.lastYPos = this.getY();
     }
   }

   @Inject(method = "findPath(IILnet/minecraft/entity/ai/pathing/PathNode;)Lnet/minecraft/entity/ai/pathing/Path;", at = @At("RETURN"))
   public void findPath(int i, int j, PathNode node, CallbackInfoReturnable<Path> ci) {
      if (!isComputingFakePaths)
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

         //TODO should this be feature?
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


   //@Inject(method = "canSee(Lnet/minecraft/entity/Entity;)Z", at = @At("RETURN"), cancellable = true)
   @Override
   public boolean canSee(Entity entity) {
      if (!DragonTracker.shouldLetDragonSee()) {
         return false;
      }
      else return super.canSee(entity);
   }

   private BlockPos getFoot(BlockPos head) {
      BlockState state = world.getBlockState(head);
      if (state.getBlock() instanceof BedBlock) {
         return head.offset(state.get(BedBlock.FACING).getOpposite());
      }
      return null;
   }

   private void handleClosestTargets() {
      if (graphInitialized) {
         double bestPlayerDistance = Double.MAX_VALUE;
         double bestDragonDistance = Double.MAX_VALUE;
         Cube dragonCube = new Cube();
         Cube playerCube = new Cube();
         for (int i = 0; i < 24; i++) {
            if (pathNodes[i].getPos().getSquaredDistance(new BlockPos(this.getPos())) < bestDragonDistance) {
               dragonCube = new Cube(pathNodes[i].getPos(), Color.GREEN);
               bestDragonDistance = pathNodes[i].getPos().getSquaredDistance(new BlockPos(this.getPos()));
            }
            if (MinecraftClient.getInstance().player != null && pathNodes[i].getPos().getSquaredDistance(new BlockPos(MinecraftClient.getInstance().player.getPos())) < bestPlayerDistance) {
               playerCube = new Cube(pathNodes[i].getPos(), Color.PINK);
               bestPlayerDistance = pathNodes[i].getPos().getSquaredDistance(new BlockPos(MinecraftClient.getInstance().player.getPos()));
            }
         }
         DragonFightDebugger.submitClosestToDragon(dragonCube);
         DragonFightDebugger.submitClosestToPlayer(playerCube);
      }
   }

   public float[] computeDamage(EnderDragonPart part, double power, double exposure, Vec3d center) {
      double powerTimes2 = 2.0 * power;
      //for (EnderDragonPart part : parts) {
         double y = (MathHelper.sqrt(part.squaredDistanceTo(center)) / powerTimes2);
         if (y <= 1.0D) {
            //removing and re-adding explosives for exposure calculation
            //double exposure = Explosion.getExposure(power, part);
            double ae = (1.0D - y) * exposure;
            float damage = (float) ((ae * ae + ae) / 2.0D * 7.0D * powerTimes2 + 1.0D);
            if (part != this.partHead)
               damage = ((int) damage) / 4.0F + Math.min(damage, 1.0F);
            else
               damage = (float) (int) damage;

            ae = (1.0D - y);
            float unexposedDamage = (float) ((ae * ae + ae) / 2.0D * 7.0D * powerTimes2 + 1.0D);
            if (part != this.partHead)
               unexposedDamage = ((int) unexposedDamage) / 4.0F + Math.min(unexposedDamage, 1.0F);
            else
               unexposedDamage = (float) (int) unexposedDamage;

            return new float[] {damage, unexposedDamage};
         }
         return new float[] {0, 0};
      //}
   }

   @Override
   public boolean isComputingFakePaths() {
      return isComputingFakePaths;
   }

   @Override
   public void setComputingFakePaths(boolean b) {
      isComputingFakePaths = b;
   }
}