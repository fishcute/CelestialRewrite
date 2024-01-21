package fishcute.celestial.mixin;

import fishcute.celestial.version.dependent.VRenderSystem;
import fishcute.celestial.version.independent.VersionSky;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Biome.class)
public class BiomeMixin {
    @Inject(method = "getFogColor", at = @At("RETURN"), cancellable = true)
    private void getFogColor(CallbackInfoReturnable<Integer> info) {
        info.setReturnValue(VersionSky.getFogColor(info.getReturnValue(), VRenderSystem.getBiomeFogColor((Biome) (Object) this)));
    }
    @Inject(method = "getSkyColor", at = @At("RETURN"), cancellable = true)
    private void getSkyColor(CallbackInfoReturnable<Integer> info) {
        info.setReturnValue(VersionSky.getSkyColor(info.getReturnValue()));
    }
}
