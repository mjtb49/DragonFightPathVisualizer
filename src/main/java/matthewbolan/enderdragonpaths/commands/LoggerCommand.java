package matthewbolan.enderdragonpaths.commands;

import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static com.mojang.brigadier.arguments.BoolArgumentType.getBool;
import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;
import com.mojang.brigadier.CommandDispatcher;
import matthewbolan.enderdragonpaths.util.BedTracker;
import matthewbolan.enderdragonpaths.util.DragonTracker;
import net.minecraft.server.command.ServerCommandSource;

public class LoggerCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                literal("logger").then(
                        literal("computedBedDamage").then(
                                literal("setShouldPrint").then(
                                        argument("shouldPrint", bool()).executes( c -> {
                                            BedTracker.setShouldPrintDamage(getBool(c, "shouldPrint"));
                                            return 1;
                                        })
                            )
                        ).then(
                                literal("setDamageThreshhold").then(
                                        argument("damageThreshold", integer()).executes( c -> {
                                            BedTracker.setDamageThreshold(getInteger(c, "damageThreshold"));
                                            return 1;
                                        })
                                )
                        )
                ).then(
                        literal("damageDone").then(
                                argument("shouldPrint", bool()).executes( c -> {
                                    DragonTracker.setShouldPrintDamageDone(getBool(c, "shouldPrint"));
                                    return 1;
                                })
                        )
                ).then(
                        literal("holdingPatternDirection").then(
                                argument("shouldPrint", bool()).executes( c -> {
                                    DragonTracker.setShouldPrintClockwise(getBool(c, "shouldPrint"));
                                    return 1;
                                })
                        )
                ).then(
                        literal("dragonYDelta").then(
                                argument("shouldPrint", bool()).executes( c -> {
                                    DragonTracker.setShouldPrintYDelta(getBool(c, "shouldPrint"));
                                    return 1;
                                })
                        )
                )
        );
    }
}
