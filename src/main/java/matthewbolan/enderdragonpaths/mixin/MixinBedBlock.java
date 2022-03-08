package matthewbolan.enderdragonpaths.mixin;

import matthewbolan.enderdragonpaths.util.ExplosionTracker;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BedBlock.class)
public class MixinBedBlock extends HorizontalFacingBlock {

    protected MixinBedBlock(Settings settings) {
        super(settings);
    }

    @Inject(method = "onPlaced(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;)V", at = @At("TAIL"))
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack, CallbackInfo ci) {
        if (!world.isClient() && world.getDimensionRegistryKey() == DimensionType.THE_END_REGISTRY_KEY) {
            BlockPos blockPos = pos.offset(state.get(FACING));
            ExplosionTracker.addBedPosition(blockPos);
        }
    }
}
