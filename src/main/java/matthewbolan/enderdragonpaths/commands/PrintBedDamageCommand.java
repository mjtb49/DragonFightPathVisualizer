package matthewbolan.enderdragonpaths.commands;

import com.mojang.brigadier.CommandDispatcher;
import matthewbolan.enderdragonpaths.util.BedDamageSettings;
import net.minecraft.server.command.ServerCommandSource;

import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static com.mojang.brigadier.arguments.BoolArgumentType.getBool;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class PrintBedDamageCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
            literal("printBedDamage").then(
                    argument("shouldPrintDamage", bool()).executes( c-> {
                                BedDamageSettings.setShouldPrintDamage(getBool(c, "shouldPrintDamage"));
                                return  1;
                            }
                    )
            )
        );
    }
}
