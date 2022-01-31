package matthewbolan.enderdragonpaths.mixin;

import matthewbolan.enderdragonpaths.util.DragonTracker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.boss.dragon.phase.HoldingPatternPhase;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HoldingPatternPhase.class)
public class MixinHoldingPatternPhase {

    @Shadow private boolean field_7044;

    private boolean lastValue = true;
    private boolean lastValueExists = false;

    @Inject(method = "serverTick()V", at = @At("TAIL"))
    public void serverTick(CallbackInfo ci) {
        if (!lastValueExists) {
            lastValueExists = true;
            lastValue = !field_7044;
        }
        if (this.field_7044 != lastValue) {
            if (DragonTracker.shouldPrintClockwise() && MinecraftClient.getInstance().player != null) {
                if (this.field_7044) {
                    MinecraftClient.getInstance().player.sendMessage(new LiteralText("Dragon now headed clockwise (looking down)").formatted(Formatting.AQUA), false);
                } else {
                    MinecraftClient.getInstance().player.sendMessage(new LiteralText("Dragon now headed counterclockwise (looking down)").formatted(Formatting.LIGHT_PURPLE), false);
                }
            }
            lastValue = this.field_7044;
        }
    }

}
