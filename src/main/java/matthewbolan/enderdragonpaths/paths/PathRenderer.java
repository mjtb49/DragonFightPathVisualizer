package matthewbolan.enderdragonpaths.paths;

import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.util.math.Vec3d;
import matthewbolan.enderdragonpaths.render.Color;
import matthewbolan.enderdragonpaths.render.Cube;
import matthewbolan.enderdragonpaths.render.Line;

import java.util.List;

public class PathRenderer {
    final static double o = 0.5d;

    final static Color RED = new Color(255,0,0);
    final static Color GREEN = new Color(0,255,0);
    final static Color BLUE = new Color(0,0,255);
    final static Color WHITE = new Color(255,255,255);
    final static Color YELLOW = new Color(255,255,51);
    final static Color PURPLE = new Color(153,50,204);
    public static void renderPath(Path path, int phaseId) {
        Color color;
        switch (phaseId) {
            case 0:
                //holding pattern
                color = WHITE;
                break;
            case 1:
                //strafing
                color = RED;
                break;
            case 2:
                //landing approach
                color = BLUE;
                break;
            case 4:
                //takeoff
                color = GREEN;
                break;
            default:
                //should never run
                color = PURPLE;
        }

        if (path == null) {
            return;
        }
        List<PathNode> nodeList = path.getNodes();
        for (int i = 0; i < nodeList.size(); i++) {
            PathNode node = nodeList.get(i);
            if (node != null) {
                //System.out.println("Trying to draw a cube at " + node.getPos());
                Cube cube;
                if (i == path.getCurrentNodeIndex() - 1)
                    cube = new Cube(node.getPos(), YELLOW);
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
