package matthewbolan.enderdragonpaths;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.boss.dragon.phase.Phase;
import matthewbolan.enderdragonpaths.paths.PathRenderer;
import matthewbolan.enderdragonpaths.render.RenderQueue;
import net.minecraft.util.Pair;
import matthewbolan.enderdragonpaths.render.Renderer;

import java.util.concurrent.ConcurrentLinkedQueue;


public class DragonFightDebugger implements ModInitializer {

	private static ConcurrentLinkedQueue<Renderer> GRAPHCOMPONENTS =new ConcurrentLinkedQueue<>();
	private static ConcurrentLinkedQueue<Pair<Path,Integer>> PATHS =new ConcurrentLinkedQueue<Pair<Path,Integer>>();

	public static void submitPath(Path path, Phase phase) {
		PATHS.add(new Pair<>(path, phase.getType().getTypeId()));
	}

	public static void submitElement(Renderer r) {
		GRAPHCOMPONENTS.add(r);
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
		GRAPHCOMPONENTS.clear();
		PATHS.clear();
	}
}
