package matthewbolan.enderdragonpaths.mixin;

import com.mojang.brigadier.CommandDispatcher;
import matthewbolan.enderdragonpaths.DragonFightDebugger;
import matthewbolan.enderdragonpaths.commands.DragonCommand;
import matthewbolan.enderdragonpaths.render.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// getString(ctx, "string")
import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
// word()
import static com.mojang.brigadier.arguments.StringArgumentType.word;
// literal("foo")
import static net.minecraft.server.command.CommandManager.literal;
// argument("bar", word())
import static net.minecraft.server.command.CommandManager.argument;
// Import everything
import static net.minecraft.server.command.CommandManager.*;

@Mixin(CommandManager.class)
public class CommandManagerMixin {
    @Shadow @Final private CommandDispatcher<ServerCommandSource> dispatcher;

    @Inject(method = "<init>(Lnet/minecraft/server/command/CommandManager$RegistrationEnvironment;)V", at = @At("RETURN"))
    public void CommandManager(CommandManager.RegistrationEnvironment environment, CallbackInfo ci) {
        DragonCommand.register(dispatcher);
    }
}
