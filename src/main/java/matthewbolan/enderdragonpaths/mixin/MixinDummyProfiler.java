package matthewbolan.enderdragonpaths.mixin;

import matthewbolan.enderdragonpaths.render.RenderQueue;
import net.minecraft.util.profiler.DummyProfiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DummyProfiler.class)
public abstract class MixinDummyProfiler {

    @Inject(method = "swap(Ljava/lang/String;)V", at = @At("HEAD"))
    private void swap(String type, CallbackInfo ci) {
        RenderQueue.get().onRender(type);
    }

}
