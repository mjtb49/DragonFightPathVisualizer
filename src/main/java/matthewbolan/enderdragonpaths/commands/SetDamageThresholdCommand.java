package matthewbolan.enderdragonpaths.commands;

import com.mojang.brigadier.CommandDispatcher;
import matthewbolan.enderdragonpaths.util.ExplosionTracker;
import net.minecraft.server.command.ServerCommandSource;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class SetDamageThresholdCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal("setDamageThreshold").then(
                        argument("threshold", integer()).executes( c-> {
                                    ExplosionTracker.setDamageThreshold(getInteger(c, "threshold"));
                                    return  1;
                                }
                        )
                )
        );
    }
}
