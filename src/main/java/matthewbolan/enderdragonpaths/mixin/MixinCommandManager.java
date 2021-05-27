package matthewbolan.enderdragonpaths.mixin;

import com.mojang.brigadier.CommandDispatcher;
import matthewbolan.enderdragonpaths.commands.PrintBedDamageCommand;
import matthewbolan.enderdragonpaths.commands.DragonCommand;
import matthewbolan.enderdragonpaths.commands.PrintDragonYDeltaCommand;
import matthewbolan.enderdragonpaths.commands.SetDamageThresholdCommand;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// getString(ctx, "string")
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
// word()
// literal("foo")
// argument("bar", word())
// Import everything


@Mixin(CommandManager.class)
public class MixinCommandManager {
    @Shadow @Final private CommandDispatcher<ServerCommandSource> dispatcher;

    @Inject(method = "<init>(Lnet/minecraft/server/command/CommandManager$RegistrationEnvironment;)V", at = @At("RETURN"))
    public void CommandManager(CommandManager.RegistrationEnvironment environment, CallbackInfo ci) {
        DragonCommand.register(dispatcher);
        PrintBedDamageCommand.register(dispatcher);
        SetDamageThresholdCommand.register(dispatcher);
        PrintDragonYDeltaCommand.register(dispatcher);
    }
}
