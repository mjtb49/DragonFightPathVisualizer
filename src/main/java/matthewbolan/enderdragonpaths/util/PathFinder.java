package matthewbolan.enderdragonpaths.util;

import matthewbolan.enderdragonpaths.render.Color;
import matthewbolan.enderdragonpaths.render.PathRenderer;
import matthewbolan.enderdragonpaths.render.RendererGroup;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.util.math.Vec3d;

public class PathFinder {

    private EnderDragonEntity dragonEntity;

    public PathFinder(EnderDragonEntity dragonEntity) {
        this.dragonEntity = dragonEntity;
    }

    public void setDragonEntity(EnderDragonEntity dragonEntity) {
        this.dragonEntity = dragonEntity;
    }

    public RendererGroup<PathRenderer> getPotentialPathsFromNearestNode(Vec3d pos, RendererGroup.RenderOption option) {
        int i = dragonEntity.getNearestPathNodeIndex(pos.getX(), pos.getY(), pos.getZ());
        return getPathRendersFromIndex(i, option);
    }

    private RendererGroup<PathRenderer> getPathRendersFromIndex(int i, RendererGroup.RenderOption option) {
        RendererGroup<PathRenderer> paths =  new RendererGroup<>(4, option);
        ((HackyWorkaround) dragonEntity).setComputingFakePaths(true);
        paths.addRenderer(new PathRenderer(findPath(i, true, true), Color.AQUA));
        paths.addRenderer(new PathRenderer(findPath(i, true, false), Color.AQUA));
        paths.addRenderer(new PathRenderer(findPath(i, false, true), Color.PINK));
        paths.addRenderer(new PathRenderer(findPath(i, false, false), Color.PINK));
        ((HackyWorkaround) dragonEntity).setComputingFakePaths(false);
        return paths;
    }

    private Path findPath(int i, boolean clockwise, boolean swapDirections) {
        return dragonEntity.findPath(i,getDestinationIndex(i,clockwise,swapDirections),null);
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
