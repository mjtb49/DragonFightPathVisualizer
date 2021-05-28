package matthewbolan.enderdragonpaths.render;

import matthewbolan.enderdragonpaths.render.Renderer;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import matthewbolan.enderdragonpaths.render.Color;
import matthewbolan.enderdragonpaths.render.Cube;
import matthewbolan.enderdragonpaths.render.Line;

import java.lang.annotation.Documented;
import java.util.List;

public class PathRenderer extends Renderer {
    final static double o = 0.5d;
    private final Path path;
    private final Color color;

    public PathRenderer(Path path, Color color) {
        this.path = path;
        this.color = color;
    }

    public PathRenderer(Path path, int phaseId) {
        this.path = path;
        switch (phaseId) {
            case 0:
                //holding pattern
                color = Color.WHITE;
                break;
            case 1:
                //strafing
                color = Color.RED;
                break;
            case 2:
                //landing approach
                color = Color.BLUE;
                break;
            case 4:
                //takeoff
                color = Color.GREEN;
                break;
            default:
                //should never run
                color = Color.PURPLE;
        }
    }

    @Override
    public void render() {
        render(this.path, this.color);
    }

    @Override
    public BlockPos getPos() {
        return path.getTarget();
    }

    public static void render(Path path, Color color) {
        List<PathNode> nodeList = path.getNodes();
        for (int i = 0; i < nodeList.size(); i++) {
            PathNode node = nodeList.get(i);
            if (node != null) {
                //System.out.println("Trying to draw a cube at " + node.getPos());
                Cube cube;
                if (i == path.getCurrentNodeIndex() - 1)
                    cube = new Cube(node.getPos(), Color.YELLOW);
                else cube = new Cube(node.getPos(), color);
                cube.render();
            }
        }
        for (int i = 0; i < nodeList.size() - 1; i++) {
            PathNode node1 = nodeList.get(i);
            PathNode node2 = nodeList.get(i+1);
            if (node1 != null && node2 != null) {
                //System.out.println("Trying to draw a line from " + node1.getPos() + " " +node2.getPos());
                Line line = new Line(new Vec3d(node1.x + o, node1.y + o, node1.z + o),
                        new Vec3d(node2.x + o, node2.y + o, node2.z + o), color);
                line.render();
            }
        }
    }
}
