package matthewbolan.enderdragonpaths.commands;

import com.mojang.brigadier.CommandDispatcher;
import matthewbolan.enderdragonpaths.util.DragonTracker;
import net.minecraft.server.command.ServerCommandSource;

import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static com.mojang.brigadier.arguments.BoolArgumentType.getBool;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class PrintDragonYDeltaCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal("printYDelta").then(
                        argument("shouldPrintYDelta", bool()).executes( c-> {
                                    DragonTracker.setShouldPrintYDelta(getBool(c, "shouldPrintYDelta"));
                                    return  1;
                                }
                        )
                )
        );
    }
}
