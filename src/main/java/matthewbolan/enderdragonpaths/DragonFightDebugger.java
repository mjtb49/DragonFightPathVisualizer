package matthewbolan.enderdragonpaths;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import matthewbolan.enderdragonpaths.render.*;
import matthewbolan.enderdragonpaths.util.ExplosionTracker;
import matthewbolan.enderdragonpaths.util.PathFinder;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.phase.Phase;
import net.minecraft.util.math.Vec3d;

public class DragonFightDebugger implements ModInitializer {

	private static final RendererGroup<Crosshair> TARGETS = new RendererGroup<>(1, RendererGroup.RenderOption.RENDER_FRONT);
	private static final RendererGroup<Cube> CLOSEST_TO_PLAYER = new RendererGroup<>(1, RendererGroup.RenderOption.RENDER_FRONT);
	private static final RendererGroup<Cube> CLOSEST_TO_DRAGON = new RendererGroup<>(1, RendererGroup.RenderOption.RENDER_FRONT);
	private static final RendererGroup<Line> DRAGON_TRACER = new RendererGroup<>(200, RendererGroup.RenderOption.RENDER_FRONT);
	private static final RendererGroup<Renderer> GRAPH_COMPONENTS = new RendererGroup<>(300, RendererGroup.RenderOption.RENDER_BACK);
	private static final RendererGroup<PathRenderer> PATHS = new RendererGroup<>(1, RendererGroup.RenderOption.RENDER_FRONT);
	private static RendererGroup<PathRenderer> FUTURE_PATHS = new RendererGroup<>(4, RendererGroup.RenderOption.RENDER_FRONT);
	private static final PathFinder PATH_FINDER = new PathFinder(null);
	private static EnderDragonEntity enderDragonEntity = null;

	public static void submitPath(Path path, Phase phase) {
		PATHS.addRenderer(new PathRenderer(path, phase.getType().getTypeId()));
	}

	public static void submitElement(Renderer r) {
		GRAPH_COMPONENTS.addRenderer(r);
	}

	public static void submitClosestToDragon(Cube r) {
		CLOSEST_TO_DRAGON.addRenderer(r);
	}

	public static void submitClosestToPlayer(Cube r) {
		CLOSEST_TO_PLAYER.addRenderer(r);
	}

	public static void submitTarget(Crosshair r) {
		TARGETS.addRenderer(r);
	}

	public static void submitHeadPosition(Line r) {
		DRAGON_TRACER.addRenderer(r);
	}

	@Override
	public void onInitialize() {
		RenderQueue.get().add("hand", matrixStack -> {
			RenderSystem.pushMatrix();
			RenderSystem.multMatrix(matrixStack.peek().getModel());
			GlStateManager.disableTexture();
			GlStateManager.disableDepthTest();

			GRAPH_COMPONENTS.render();
			FUTURE_PATHS.render();
			PATHS.render();
			TARGETS.render();
			DRAGON_TRACER.render();
			CLOSEST_TO_PLAYER.render();
			CLOSEST_TO_DRAGON.render();
			TextRenderer.render();

			GlStateManager.disableDepthTest();

			RenderSystem.popMatrix();

		});
	}

	public static void setTracerRenderOptions(RendererGroup.RenderOption option, int ticks) {
		DRAGON_TRACER.setRenderOption(option);
		DRAGON_TRACER.setSizeCap(ticks);
	}

	public static void setTracerRenderOptions(RendererGroup.RenderOption option) {
		DRAGON_TRACER.setRenderOption(option);
	}

	public static void setTracerRenderOptions(int ticks) {
		DRAGON_TRACER.setSizeCap(ticks);
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

	public static void setClosestNodesRenderOptions(RendererGroup.RenderOption option) {
		CLOSEST_TO_PLAYER.setRenderOption(option);
		CLOSEST_TO_DRAGON.setRenderOption(option);
	}

	public static void setFutureRenderOption(RendererGroup.RenderOption option) {FUTURE_PATHS.setRenderOption(option);}

	public static void setEnderDragonEntity(EnderDragonEntity enderDragonEntity) {
		PATH_FINDER.setDragonEntity(enderDragonEntity);
		DragonFightDebugger.enderDragonEntity = enderDragonEntity;
	}

	public static void setFuturePaths(Vec3d pos) {
		if (enderDragonEntity == null)
			System.err.println("Tried to set a future path with no dragon!");
		else
			FUTURE_PATHS = PATH_FINDER.getPotentialPathsFromNearestNode(pos, FUTURE_PATHS.getRenderOption());
	}

	public static void clearGraph() {
		GRAPH_COMPONENTS.clear();
		TextRenderer.clear();
	}

	public static void clearAll() {
		TARGETS.clear();
		GRAPH_COMPONENTS.clear();
		PATHS.clear();
		DRAGON_TRACER.clear();
		FUTURE_PATHS.clear();
		CLOSEST_TO_DRAGON.clear();
		CLOSEST_TO_PLAYER.clear();
		TextRenderer.clear();
		ExplosionTracker.resetBedPositions();
	}
}
