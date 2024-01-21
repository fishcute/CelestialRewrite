package fishcute.celestial.version.dependent.util;

import fishcute.celestial.version.dependent.Vector;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.Level;

public class LevelWrapper {
    public ClientLevel level;
    public LevelWrapper(ClientLevel l) {
        this.level = l;
    }

    public Vector getSkyColor(float tickDelta) {
        return Vector.fromVec(level.getSkyColor(Minecraft.getInstance().gameRenderer.getMainCamera().getPosition(), tickDelta));
    }

    public float[] getSunriseColor(float tickDelta) {
        return Minecraft.getInstance().level.effects().getSunriseColor(this.getTimeOfDay(tickDelta), tickDelta);
    }

    public float getTimeOfDay(float tickDelta) {
        return this.level.getTimeOfDay(tickDelta);
    }
    public float getSunAngle(float tickDelta) {
        return level.getSunAngle(tickDelta);
    }
    public double getHorizonHeight() {
        return this.level.getLevelData().getHorizonHeight(this.level);
    }
    public boolean hasGround() {
        return this.level.effects().hasGround();
    }
}
