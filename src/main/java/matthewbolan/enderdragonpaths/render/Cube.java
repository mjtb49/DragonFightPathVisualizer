package matthewbolan.enderdragonpaths.render;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class Cube extends Cuboid {

    public Cube() {
        this(BlockPos.ORIGIN, Color.WHITE);
    }

    public Cube(BlockPos pos) {
        this(pos, Color.WHITE);
    }

    public Cube(BlockPos pos, Color color) {
        super(pos, new Vec3i(1, 1, 1), color);
    }

    public Cube(Vec3d pos, Color color) {
        super(pos, new Vec3i(1, 1, 1), color);
    }


    @Override
    public BlockPos getPos() {
        return new BlockPos(this.start.getX(), this.start.getY(),  this.start.getZ()) ;
    }

}
