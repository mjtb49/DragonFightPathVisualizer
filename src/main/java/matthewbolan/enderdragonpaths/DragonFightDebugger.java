package matthewbolan.enderdragonpaths;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import matthewbolan.enderdragonpaths.render.Cube;
import matthewbolan.enderdragonpaths.render.Line;
import matthewbolan.enderdragonpaths.util.BedDamageSettings;
import matthewbolan.enderdragonpaths.render.RendererGroup;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.boss.dragon.phase.Phase;
import matthewbolan.enderdragonpaths.render.PathRenderer;
import matthewbolan.enderdragonpaths.render.RenderQueue;
import matthewbolan.enderdragonpaths.render.Renderer;

public class DragonFightDebugger implements ModInitializer {

	private static final RendererGroup<Cube> TARGETS = new RendererGroup<>(1, RendererGroup.RenderOption.RENDER_FRONT);
	private static final RendererGroup<Line> DRAGON_HEAD_SPOTS = new RendererGroup<>(200, RendererGroup.RenderOption.RENDER_FRONT);
	private static final RendererGroup<Renderer> GRAPH_COMPONENTS = new RendererGroup<>(300, RendererGroup.RenderOption.RENDER_BACK);
	private static final RendererGroup<PathRenderer> PATHS = new RendererGroup<>(1, RendererGroup.RenderOption.RENDER_FRONT);

	public static void submitPath(Path path, Phase phase) {
		PATHS.addRenderer(new PathRenderer(path, phase.getType().getTypeId()));
	}

	public static void submitElement(Renderer r) {
		GRAPH_COMPONENTS.addRenderer(r);
	}

	public static void submitTarget(Cube r) {
		TARGETS.addRenderer(r);
	}

	public static void submitHeadPosition(Line r) {
		DRAGON_HEAD_SPOTS.addRenderer(r);
	}

	@Override
	public void onInitialize() {
		RenderQueue.get().add("hand", matrixStack -> {
			RenderSystem.pushMatrix();
			RenderSystem.multMatrix(matrixStack.peek().getModel());
			GlStateManager.disableTexture();
			GlStateManager.disableDepthTest();

			GRAPH_COMPONENTS.render();
			PATHS.render();
			TARGETS.render();
			DRAGON_HEAD_SPOTS.render();

			GlStateManager.disableDepthTest();

			RenderSystem.popMatrix();

		});
	}

	public static void setTracerRenderOptions(RendererGroup.RenderOption option, int ticks) {
		DRAGON_HEAD_SPOTS.setRenderOption(option);
		DRAGON_HEAD_SPOTS.setSizeCap(ticks);
	}

	public static void setTracerRenderOptions(RendererGroup.RenderOption option) {
		DRAGON_HEAD_SPOTS.setRenderOption(option);
	}

	public static void setTracerRenderOptions(int ticks) {
		DRAGON_HEAD_SPOTS.setSizeCap(ticks);
	}

	public static void setPathRenderOption(RendererGroup.RenderOption option) {
		PATHS.setRenderOption(option);
	}

	public static void setGraphRenderOption(RendererGroup.RenderOption option) {
		GRAPH_COMPONENTS.setRenderOption(option);
	}

	public static void setTargetRenderOption(RendererGroup.RenderOption option) {
		TARGETS.setRenderOption(option);
	}

	public static void clearGraph() {
		GRAPH_COMPONENTS.clear();
	}

	public static void clearAll() {
		TARGETS.clear();
		GRAPH_COMPONENTS.clear();
		PATHS.clear();
		DRAGON_HEAD_SPOTS.clear();
		BedDamageSettings.resetBedPositions();
	}
}
