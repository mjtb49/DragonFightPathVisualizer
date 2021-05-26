package matthewbolan.enderdragonpaths.commands;

import com.mojang.brigadier.CommandDispatcher;
import matthewbolan.enderdragonpaths.DragonFightDebugger;
import matthewbolan.enderdragonpaths.render.RendererGroup;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class DragonCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
            literal("dragon").then(
                    literal("tracer").then(
                            literal("front").then(
                                    argument("ticks", integer())
                                            .executes(c -> {
                                                        DragonFightDebugger.setTracerRenderOptions(RendererGroup.RenderOption.RENDER_FRONT, getInteger(c, "ticks"));
                                                        return 1;
                                                    }
                                            )
                            ).executes(c -> {
                                        DragonFightDebugger.setTracerRenderOptions(RendererGroup.RenderOption.RENDER_FRONT);
                                        return 1;
                                    }
                            )
                        ).then(
                            literal("back").then(
                                    argument("ticks", integer())
                                            .executes(c -> {
                                                        DragonFightDebugger.setTracerRenderOptions(RendererGroup.RenderOption.RENDER_BACK, getInteger(c, "ticks"));
                                                        return 1;
                                                    }
                                            )
                            ).executes(c -> {
                                        DragonFightDebugger.setTracerRenderOptions(RendererGroup.RenderOption.RENDER_BACK);
                                        return 1;
                                    }
                            )
                        ).then(
                            literal("none").executes(c -> {
                                    DragonFightDebugger.setTracerRenderOptions(RendererGroup.RenderOption.NONE, 0);
                                    return 1;
                                }
                            )
                        ).then(
                            argument("ticks", integer())
                                    .executes(c -> {
                                                DragonFightDebugger.setTracerRenderOptions(getInteger(c, "ticks"));
                                                return 1;
                                            }
                                    )
                        )
                ).then(
                    literal("graph").then(
                            literal("front").executes(c -> {
                                        DragonFightDebugger.setGraphRenderOption(RendererGroup.RenderOption.RENDER_FRONT);
                                        return 1;
                                    }
                            )
                        )
                        .then(
                                literal("back").executes(c -> {
                                            DragonFightDebugger.setGraphRenderOption(RendererGroup.RenderOption.RENDER_BACK);
                                            return 1;
                                        }
                                )
                        ).then(
                            literal("none").executes(c -> {
                                        DragonFightDebugger.setGraphRenderOption(RendererGroup.RenderOption.NONE);
                                        return 1;
                                    }
                            )
                        )
                ).then(
                    literal("path").then(
                            literal("front").executes(c -> {
                                DragonFightDebugger.setPathRenderOption(RendererGroup.RenderOption.RENDER_FRONT);
                                return 1;
                            }
                        )
                    )
                            .then(
                                    literal("back").executes(c -> {
                                                DragonFightDebugger.setPathRenderOption(RendererGroup.RenderOption.RENDER_BACK);
                                                return 1;
                                            }

                                    )).then(
                            literal("none").executes(c -> {
                                        DragonFightDebugger.setPathRenderOption(RendererGroup.RenderOption.NONE);
                                        return 1;
                                    }
                            )
                    )
            ).then(
                    literal("target").then(
                            literal("front").executes(c -> {
                                        DragonFightDebugger.setTargetRenderOption(RendererGroup.RenderOption.RENDER_FRONT);
                                        return 1;
                                    }
                            )
                    )
                            .then(
                                    literal("back").executes(c -> {
                                                DragonFightDebugger.setTargetRenderOption(RendererGroup.RenderOption.RENDER_BACK);
                                                return 1;
                                            }
                                    )
                            ).then(
                            literal("none").executes(c -> {
                                        DragonFightDebugger.setTargetRenderOption(RendererGroup.RenderOption.NONE);
                                        return 1;
                                    }
                            )
                    )
            ).then(
                    literal("help").executes(c -> {
                        //TODO
                        if (MinecraftClient.getInstance().player != null)
                            MinecraftClient.getInstance().player.sendMessage(new LiteralText("TO DO").formatted(Formatting.RED), false);
                        return 1;
                    })
            ).then(
                    literal("restoreDefaults").executes(c -> {
                        DragonFightDebugger.setTargetRenderOption(RendererGroup.RenderOption.RENDER_FRONT);
                        DragonFightDebugger.setPathRenderOption(RendererGroup.RenderOption.RENDER_FRONT);
                        DragonFightDebugger.setGraphRenderOption(RendererGroup.RenderOption.RENDER_BACK);
                        DragonFightDebugger.setTracerRenderOptions(RendererGroup.RenderOption.RENDER_FRONT, 200);
                        return 1;
                    })
            )
        );
    }
}
