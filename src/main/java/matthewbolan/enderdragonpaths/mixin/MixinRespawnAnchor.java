package matthewbolan.enderdragonpaths.mixin;

import matthewbolan.enderdragonpaths.util.ExplosionTracker;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(RespawnAnchorBlock.class)
public class MixinRespawnAnchor extends Block {

    public MixinRespawnAnchor(Settings settings) {
        super(settings);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        if (!world.isClient() && world.getDimensionRegistryKey() == DimensionType.THE_END_REGISTRY_KEY) {
            ExplosionTracker.addBedPosition(pos);
        }
    }
}
