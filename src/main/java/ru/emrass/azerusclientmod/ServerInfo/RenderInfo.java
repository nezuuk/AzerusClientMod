package ru.emrass.azerusclientmod.ServerInfo;

import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;
import ru.emrass.azerusclientmod.AzerusClientMod;
import ru.emrass.azerusclientmod.config.ModConfig;



public class RenderInfo {
    private static Minecraft mc = Minecraft.getMinecraft();
    public static void onServerInfoOverlay() {
        if(mc.player != null) {
            GL11.glPushMatrix();
            GL11.glScalef(ModConfig.MOBS_COOLDOWN.counterSize, ModConfig.MOBS_COOLDOWN.counterSize, ModConfig.MOBS_COOLDOWN.counterSize);
            mc.fontRenderer.drawString(AzerusClientMod.serverTime, mc.displayWidth - AzerusClientMod.serverTime.length(), mc.displayHeight - 30, 0x6028ff, false);
            GL11.glPopMatrix();
        }
    }
}
