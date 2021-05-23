package matthewbolan.enderdragonpaths;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import matthewbolan.enderdragonpaths.util.BedDamageSettings;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.boss.dragon.phase.Phase;
import matthewbolan.enderdragonpaths.util.PathRenderer;
import matthewbolan.enderdragonpaths.render.RenderQueue;
import net.minecraft.util.Pair;
import matthewbolan.enderdragonpaths.render.Renderer;

import java.util.concurrent.ConcurrentLinkedQueue;

public class DragonFightDebugger implements ModInitializer {

	private static ConcurrentLinkedQueue<Renderer> TARGETS = new ConcurrentLinkedQueue<>();
	private static ConcurrentLinkedQueue<Renderer> DRAGON_HEAD_SPOTS = new ConcurrentLinkedQueue<>();
	private static ConcurrentLinkedQueue<Renderer> GRAPHCOMPONENTS = new ConcurrentLinkedQueue<>();
	private static ConcurrentLinkedQueue<Pair<Path,Integer>> PATHS = new ConcurrentLinkedQueue<>();
	private static int tracerTicks = 200;
	private static RenderOption renderTracer = RenderOption.RENDER_FRONT;
	private static RenderOption renderPaths = RenderOption.RENDER_FRONT;
	private static RenderOption renderTargets = RenderOption.RENDER_FRONT;
	private static RenderOption renderGraph = RenderOption.RENDER_BACK;

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

	public enum RenderOption {
		RENDER_FRONT,
		RENDER_BACK,
		NONE
	}

	@Override
	public void onInitialize() {
		RenderQueue.get().add("hand", matrixStack -> {
			RenderSystem.pushMatrix();
			RenderSystem.multMatrix(matrixStack.peek().getModel());
			GlStateManager.disableTexture();
			GlStateManager.disableDepthTest();

			if (renderGraph != RenderOption.NONE) {
				if (renderGraph == RenderOption.RENDER_BACK)
					GlStateManager.enableDepthTest();
				for (Renderer r : GRAPHCOMPONENTS) {
					r.render();
				}
			}
			GlStateManager.disableDepthTest();

			if (renderTargets == RenderOption.RENDER_BACK)
				GlStateManager.enableDepthTest();
			for (Renderer r: TARGETS) {
				while (TARGETS.size() > 1)
					TARGETS.remove();
				if (renderTargets != RenderOption.NONE)
					r.render();
			}
			GlStateManager.disableDepthTest();

			if (renderTracer == RenderOption.RENDER_BACK)
				GlStateManager.enableDepthTest();
			for (Renderer r: DRAGON_HEAD_SPOTS) {
				while (tracerTicks >= 0 && DRAGON_HEAD_SPOTS.size() > tracerTicks) {
					DRAGON_HEAD_SPOTS.remove();
				}
				if (renderTracer !=  RenderOption.NONE)
					r.render();
			}
			GlStateManager.disableDepthTest();

			if (renderPaths == RenderOption.RENDER_BACK)
				GlStateManager.enableDepthTest();
			for(Pair<Path,Integer> pair: PATHS) {
				if (PATHS.size() > 1)
					PATHS.remove(pair);
				if (renderPaths != RenderOption.NONE)
					PathRenderer.renderPath(pair.getLeft(), pair.getRight());
			}
			GlStateManager.disableDepthTest();

			RenderSystem.popMatrix();

		});
	}

	public static void setTracerRenderOptions(RenderOption option, int ticks) {
		renderTracer = option;
		if (option != RenderOption.NONE)
			tracerTicks = ticks;
	}

	public static void setTracerRenderOptions(RenderOption option) {
		renderTracer = option;
	}

	public static void setTracerRenderOptions(int ticks) {
		tracerTicks = ticks;
	}

	public static void setPathRenderOption(RenderOption option) {
		renderPaths = option;
	}

	public static void setGraphRenderOption(RenderOption option) {
		renderGraph = option;
	}

	public static void setTargetRenderOption(RenderOption option) {
		renderTargets = option;
	}

	public static void clearGraph() {
		GRAPHCOMPONENTS.clear();
	}

	public static void clearAll() {
		TARGETS.clear();
		GRAPHCOMPONENTS.clear();
		PATHS.clear();
		DRAGON_HEAD_SPOTS.clear();
		BedDamageSettings.resetBedPositions();
	}
}
