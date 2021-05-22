package matthewbolan.enderdragonpaths;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import matthewbolan.enderdragonpaths.render.Color;
import matthewbolan.enderdragonpaths.render.Line;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.boss.dragon.phase.Phase;
import matthewbolan.enderdragonpaths.paths.PathRenderer;
import matthewbolan.enderdragonpaths.render.RenderQueue;
import net.minecraft.util.Pair;
import matthewbolan.enderdragonpaths.render.Renderer;
import net.minecraft.util.math.Vec3d;

import java.util.concurrent.ConcurrentLinkedQueue;


public class DragonFightDebugger implements ModInitializer {

	private static ConcurrentLinkedQueue<Renderer> TARGETS = new ConcurrentLinkedQueue<>();
	private static ConcurrentLinkedQueue<Renderer> DRAGON_HEAD_SPOTS = new ConcurrentLinkedQueue<>();
	private static ConcurrentLinkedQueue<Renderer> GRAPHCOMPONENTS = new ConcurrentLinkedQueue<>();
	private static ConcurrentLinkedQueue<Pair<Path,Integer>> PATHS = new ConcurrentLinkedQueue<>();

	public static void submitPath(Path path, Phase phase) {
		PATHS.add(new Pair<>(path, phase.getType().getTypeId()));
	}

	public static void submitElement(Renderer r) {
		GRAPHCOMPONENTS.add(r);
	}

	public static void submitTarget(Renderer r) {
		TARGETS.add(r);
	}

	public static void submitHeadPosition(Renderer r) {
		DRAGON_HEAD_SPOTS.add(r);
	}

	@Override
	public void onInitialize() {
		RenderQueue.get().add("hand", matrixStack -> {
			RenderSystem.pushMatrix();
			RenderSystem.multMatrix(matrixStack.peek().getModel());
			GlStateManager.disableTexture();

			for (Renderer r: GRAPHCOMPONENTS) {
				r.render();
			}

			GlStateManager.disableDepthTest();

			for (Renderer r: TARGETS) {
				while (TARGETS.size() > 1)
					TARGETS.remove();
				r.render();
			}

			for (Renderer r: DRAGON_HEAD_SPOTS) {
				while (DRAGON_HEAD_SPOTS.size() > 200) {
					DRAGON_HEAD_SPOTS.remove();
				}
				r.render();
			}

			for(Pair<Path,Integer> pair: PATHS) {
				if (PATHS.size() > 1)
					PATHS.remove(pair);
				PathRenderer.renderPath(pair.getLeft(), pair.getRight());
			}
			RenderSystem.popMatrix();
		});

	}

	public static void clearGraph() {
		GRAPHCOMPONENTS.clear();
	}

	public static void clearAll() {
		TARGETS.clear();
		GRAPHCOMPONENTS.clear();
		PATHS.clear();
		DRAGON_HEAD_SPOTS.clear();
	}
}
