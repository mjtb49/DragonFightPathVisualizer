package matthewbolan.enderdragonpaths.mixin;

import jdk.nashorn.internal.codegen.CompilerConstants;
import matthewbolan.enderdragonpaths.DragonFightDebugger;
import matthewbolan.enderdragonpaths.render.Cuboid;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.boss.dragon.phase.PhaseManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
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

//@Mixin(ChunkRandom.class)
//public class ExampleMixin extends Random {
//	@Overwrite
//	public long setCarverSeed(long worldseed, int x, int z) {
//		//long forcedSeed = 1969577705059038035L;
//		//long forcedSeed = worldseed; //max of 68!
//		this.setSeed(worldseed);
//		return worldseed;
//	}
//}

@Mixin(EnderDragonEntity.class)
public class EnderDragonEntityMixin {

   @Shadow @Final
   private PhaseManager phaseManager;

   @Shadow @Final
   private PathNode[] pathNodes;

   @Shadow @Final
   private int[] pathNodeConnections;

   @Shadow @Final public EnderDragonPart partHead;
   private boolean graphInitialized;
   //private static final Color GRAY = new Color(50,50,50);
   //private static final Color ORANGE = new Color(255,126,0);
   private static final double o = 0.5;
   private Vec3d last = null;

   @Inject(method = "tickMovement()V", at = @At("HEAD"))
   public void tickMovement(CallbackInfo ci) {
      Vec3d target = phaseManager.getCurrent().getTarget();
      if (target != null) {
         double x = target.getX();
         double y = target.getY();
         double z = target.getZ();
         Cube cube = new Cube(new BlockPos(x, y, z), Color.ORANGE);
         DragonFightDebugger.submitTarget(cube);
      }
      if (target != null) {
         double x = this.partHead.getX();
         double y = this.partHead.getEyeY();
         double z = this.partHead.getZ();
         Vec3d newPos = new Vec3d(x, y, z);
         if (last != null) {
            DragonFightDebugger.submitHeadPosition(new Line(newPos, last, Color.PURPLE));
         }
         last = newPos;
      }
   }

   @Inject(method = "findPath(IILnet/minecraft/entity/ai/pathing/PathNode;)Lnet/minecraft/entity/ai/pathing/Path;", at = @At("RETURN"))
   public void findPath(int i, int j, PathNode node, CallbackInfoReturnable<Path> ci) {
      DragonFightDebugger.submitPath(ci.getReturnValue(), phaseManager.getCurrent());
   }

   @Inject(method = "getNearestPathNodeIndex()I", at = @At(value = "TAIL"))
   public void getNearestPathNodeIndex(CallbackInfoReturnable<Integer> cir) {
      if(!graphInitialized) {
         DragonFightDebugger.clearGraph();
         for (int i = 0; i < 24; i++) {
            Cube coob = new Cube(pathNodes[i].getPos(), Color.GRAY);
            for (int j = i; j < 24; j++) {
               if ((pathNodeConnections[i] & (1 << j)) != 0) {
                  if ((pathNodeConnections[j] & (1 << i)) == 0) {
                     System.err.println("graph is actually a digraph!");
                  }
                  BlockPos node1 = pathNodes[i].getPos();
                  BlockPos node2 = pathNodes[j].getPos();
                  Line line = new Line(new Vec3d(node1.getX() + o, node1.getY() + o, node1.getZ() + o),
                          new Vec3d(node2.getX() + o, node2.getY() + o, node2.getZ() + o), Color.GRAY);
                  DragonFightDebugger.submitElement(line);
               }
            }
            DragonFightDebugger.submitElement(coob);
         }
      }
      graphInitialized = true;
   }
}