package matthewbolan.enderdragonpaths.render;

import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class Crosshair extends Renderer {
    private final Vec3d pos;
    private final Line[] hairs = new Line[3];
    public Crosshair() {
        this(BlockPos.ORIGIN, 0.5, Color.WHITE);
    }

    public Crosshair(BlockPos pos) {
        this(pos, 0.5, Color.WHITE);
    }

    public Crosshair(Vec3d pos) {
        this(pos, 0.5, Color.WHITE);
    }

    public Crosshair(BlockPos start, double radius, Color color) {
        this(new Vec3d(start.getX(), start.getY(), start.getZ()), radius, color);
    }

    public Crosshair(Vec3d pos, double radius, Color color) {
        this.pos = pos;
        this.hairs[0] =  new Line(this.pos.add(-radius, 0, 0), this.pos.add(radius, 0, 0), color);
        this.hairs[1] =  new Line(this.pos.add(0, -radius, 0), this.pos.add(0, radius, 0), color);
        this.hairs[2] =  new Line(this.pos.add(0, 0, -radius), this.pos.add(0, 0, radius), color);
    }
    @Override
    public void render() {
        if(this.pos == null || this.hairs == null)return;

        for(Line edge: this.hairs) {
            if(edge == null)continue;
            edge.render();
        }
    }

    @Override
    public BlockPos getPos() {
        Vec3d center = this.pos;
        return new BlockPos(center.x, center.y, center.z);
    }
}
