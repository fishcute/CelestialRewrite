package fishcute.celestial.version.dependent;

import fishcute.celestial.util.Util;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.CubicSampler;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import oshi.util.tuples.Pair;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class VMinecraftInstance {
    private static Minecraft minecraft = Minecraft.getInstance();
    public static boolean doesLevelExist() {
        return minecraft.level != null;
    }
    public static boolean doesPlayerExist() {
        return minecraft.player != null;
    }
    public static String getLevelPath() {
        return Minecraft.getInstance().level.dimension().location().getPath();
    }
    public static float getTickDelta() {
        return Minecraft.getInstance().getDeltaFrameTime();
    }
    public static Vector getPlayerEyePosition() {
        return Vector.fromVec(Minecraft.getInstance().player.getEyePosition(getTickDelta()));
    }
    public static void sendInfoMessage(String i) {
        minecraft.player.displayClientMessage(Component.literal(ChatFormatting.GRAY + i), false);
    }
    public static void sendErrorMessage(String i) {
        minecraft.player.displayClientMessage(Component.literal(ChatFormatting.RED + i), false);
    }
    public static void sendWarnMessage(String i) {
        minecraft.player.displayClientMessage(Component.literal(ChatFormatting.YELLOW + i), false);
    }
    public static InputStream getResource(String path) throws IOException {
        return Minecraft.getInstance().getResourceManager().getResource(new ResourceLocation(path)).get().open();
    }
    public static boolean isGamePaused() {
        return Minecraft.getInstance().isPaused();
    }
    public static void sendMessage(String text, boolean actionBar) {
        Minecraft.getInstance().player.displayClientMessage(Component.literal(text), actionBar);
    }
    public static double getPlayerX() {
        return Minecraft.getInstance().player.getX();
    }
    public static double getPlayerY() {
        return Minecraft.getInstance().player.getY();
    }
    public static double getPlayerZ() {
        return Minecraft.getInstance().player.getZ();
    }
    public static double getRainLevel() {
        return Minecraft.getInstance().level.getRainLevel(getTickDelta());
    }
    public static boolean isPlayerInWater() {
        return Minecraft.getInstance().player.isInWater();
    }
    public static long getGameTime() {
        return Minecraft.getInstance().level.getGameTime();
    }
    public static long getWorldTime() {
        return Minecraft.getInstance().level.dayTime();
    }
    public static float getStarBrightness() {
        return Minecraft.getInstance().level.getStarBrightness(getTickDelta());
    }
    public static float getTimeOfDay() {
        return Minecraft.getInstance().level.getTimeOfDay(getTickDelta());
    }
    public static float getViewXRot() {
        return Minecraft.getInstance().player.getViewXRot(getTickDelta());
    }
    public static float getViewYRot() {
        return Minecraft.getInstance().player.getViewYRot(getTickDelta());
    }

    public static BlockPos getPlayerBlockPosition() {
        return minecraft.player.blockPosition();
    }
    public static float getRenderDistance() {
        return Minecraft.getInstance().options.getEffectiveRenderDistance();
    }
    public static float getMoonPhase() {
        return Minecraft.getInstance().level.getMoonPhase();
    }
    public static float getSkyDarken() {
        return Minecraft.getInstance().level.getSkyDarken();
    }
    public static float getSkyFlashTime() {
        return Minecraft.getInstance().level.getSkyFlashTime();
    }
    public static float getThunderLevel() {
        return Minecraft.getInstance().level.getThunderLevel(getTickDelta());
    }
    public static float getSkyLight() {
        return Minecraft.getInstance().level.getBrightness(LightLayer.SKY, getPlayerBlockPosition());
    }
    public static float getBlockLight() {
        return Minecraft.getInstance().level.getBrightness(LightLayer.BLOCK, getPlayerBlockPosition());
    }
    public static float getBiomeTemperature() {
        return Minecraft.getInstance().level.getBiome(getPlayerBlockPosition()).value().getBaseTemperature();
    }
    public static float getBiomeDownfall() {
        return Minecraft.getInstance().level.getBiome(getPlayerBlockPosition()).value().getDownfall();
    }
    public static boolean getBiomeSnow() {
        return Minecraft.getInstance().level.getBiome(getPlayerBlockPosition()).value().coldEnoughToSnow(getPlayerBlockPosition());
    }
    public static boolean isRightClicking() {
        return Minecraft.getInstance().mouseHandler.isRightPressed();
    }
    public static boolean isLeftClicking() {
        return Minecraft.getInstance().mouseHandler.isLeftPressed();
    }
    public static ResourceLocation getMainHandItemKey() {
        return Registry.ITEM.getKey(Minecraft.getInstance().player.getMainHandItem().getItem());
    }
    public static String getMainHandItemNamespace() {
        return getMainHandItemKey().getNamespace();
    }
    public static String getMainHandItemPath() {
        return getMainHandItemKey().getPath();
    }

    static HashMap<Biome, Pair<String, String>> biomeNameMap = new HashMap<>();

    static void addToBiomeMap(Holder<Biome> b) {
        biomeNameMap.put(b.value(),
                new Pair<>(
                        b.unwrapKey().get().location().getNamespace() + ":" + b.unwrapKey().get().location().getPath(),
                        b.unwrapKey().get().location().getPath()
                ));
    }
    public static boolean equalToBiome(Vector position, String name) {
        Holder<Biome> b = Minecraft.getInstance().level.getBiome(position == null ? getPlayerBlockPosition() : position.toBlockPos());
        if (!biomeNameMap.containsKey(b.value()))
            addToBiomeMap(b);
        return biomeNameMap.get(b.value()).getA().equals(name) || biomeNameMap.get(b.value()).getB().equals(name);
    }
    public static double[] getBiomeSkyColor() {
        double[] c = new double[3];
        Util.getRealSkyColor = true;
        Vec3 vec = CubicSampler.gaussianSampleVec3(Minecraft.getInstance().player.position(), (ix, jx, kx) -> {
            return Vec3.fromRGB24((Minecraft.getInstance().level.getBiome(new BlockPos(ix, jx, kx)).value()).getSkyColor());
        });
        Util.getRealSkyColor = false;
        c[0] = vec.x;
        c[1] = vec.y;
        c[2] = vec.z;
        return c;
    }
    public static double[] getBiomeFogColor() {
        double[] c = new double[3];
        Util.getRealFogColor = true;
        Vec3 vec = CubicSampler.gaussianSampleVec3(Minecraft.getInstance().player.position(), (ix, jx, kx) -> {
            return Vec3.fromRGB24((Minecraft.getInstance().level.getBiome(new BlockPos(ix, jx, kx)).value()).getFogColor());
        });
        Util.getRealFogColor = false;
        c[0] = vec.x;
        c[1] = vec.y;
        c[2] = vec.z;
        return c;
    }
    public static double[] getBiomeWaterFogColor() {
        double[] c = new double[3];
        Util.getRealFogColor = true;
        Vec3 vec = CubicSampler.gaussianSampleVec3(Minecraft.getInstance().player.position(), (ix, jx, kx) -> {
            return Vec3.fromRGB24((Minecraft.getInstance().level.getBiome(new BlockPos(ix, jx, kx)).value()).getWaterFogColor());
        });
        Util.getRealFogColor = false;
        c[0] = vec.x;
        c[1] = vec.y;
        c[2] = vec.z;
        return c;
    }
    public static boolean disableFogChanges() {
        return Minecraft.getInstance().gameRenderer.getMainCamera().getFluidInCamera() !=
                FogType.NONE || Minecraft.getInstance().player.hasEffect(MobEffects.BLINDNESS);
    }
    public static boolean inWater() {
        return Minecraft.getInstance().gameRenderer.getMainCamera().getFluidInCamera() == FogType.NONE;
    }
}
