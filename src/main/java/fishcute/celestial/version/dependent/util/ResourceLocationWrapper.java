package fishcute.celestial.version.dependent.util;

import net.minecraft.resources.ResourceLocation;

public class ResourceLocationWrapper {
    public ResourceLocation resourceLocation;
    public ResourceLocationWrapper(String i) {
        this.resourceLocation = new ResourceLocation(i);
    }
    public ResourceLocationWrapper(ResourceLocation resourceLocation) {
        this.resourceLocation = resourceLocation;
    }
}
