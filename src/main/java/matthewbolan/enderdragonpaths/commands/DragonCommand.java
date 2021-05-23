package matthewbolan.enderdragonpaths.commands;

import com.mojang.brigadier.CommandDispatcher;
import matthewbolan.enderdragonpaths.DragonFightDebugger;
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
                            argument("ticks", integer())
                                    .executes(c -> {
                                        DragonFightDebugger.setTracerTicks(getInteger(c, "ticks"));
                                        return 1;
                                    })
                    )
            ).then(
                    literal("togglegraph").executes(c -> {
                        DragonFightDebugger.toggleGraph();
                        return 1;
                    })
            ).then(
                    literal("togglepaths").executes(c -> {
                        DragonFightDebugger.togglePaths();
                        return 1;
                    })
            ).then(
                    literal("toggletargets").executes(c -> {
                        DragonFightDebugger.toggleTargets();
                        return 1;
                    })
            ).then(
                    literal("help").executes(c -> {
                        if (MinecraftClient.getInstance().player != null)
                            MinecraftClient.getInstance().player.sendMessage(new LiteralText("TO DO").formatted(Formatting.RED), false);
                        return 1;
                    })
            )
        );
    }
}
