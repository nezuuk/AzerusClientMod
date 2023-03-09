package ru.emrass.azerusclientmod;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;
import ru.emrass.azerusclientmod.DropMobs.DropDisplay;
import ru.emrass.azerusclientmod.DropMobs.MobTimer;
import ru.emrass.azerusclientmod.ServerInfo.RenderInfo;
import ru.emrass.azerusclientmod.config.ModConfig;

import javax.xml.crypto.Data;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Mod(modid = AzerusClientMod.MODID, clientSideOnly = true, useMetadata = true)

public class AzerusClientMod {
    public static final String MODID = "azerusclientmod";

    private AzerusClientMod instance;
    private Minecraft mc = Minecraft.getMinecraft();
    private DropDisplay dropDisplay;

    public static String currentServerName;
    public static String serverTime;
    private ModConfig modConfig;
    public static Configuration config;
    private long tick = 0;
    private MobTimer mobTimer;
    public static final Map<String, Date> mobsRespawningTime = new HashMap();

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event){
        instance = this;
        dropDisplay = new DropDisplay(this);
        modConfig = new ModConfig();
        mobTimer = new MobTimer();
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(dropDisplay);
        MinecraftForge.EVENT_BUS.register(modConfig);
        MinecraftForge.EVENT_BUS.register(mobTimer);
        config = new Configuration(event.getSuggestedConfigurationFile());
        try {

        } catch (Exception e) {
            event.getModLog().error("Failed loading config");
        } finally {
            if (config.hasChanged()) {
                config.save();
            }
        }
    }

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.TEXT) {
            MobTimer.onRenderGameOverlay();
            if(mc.player != null) {
                GL11.glPushMatrix();
                GL11.glScalef(ModConfig.MOBS_COOLDOWN.counterSize, ModConfig.MOBS_COOLDOWN.counterSize, ModConfig.MOBS_COOLDOWN.counterSize);
                mc.fontRenderer.drawStringWithShadow("Сервер: " + AzerusClientMod.currentServerName, event.getResolution().getScaledWidth() - 210, event.getResolution().getScaledHeight() - 30, 0xFFFFFF);
                mc.fontRenderer.drawStringWithShadow("Время работы: " + AzerusClientMod.serverTime, event.getResolution().getScaledWidth() - 210, event.getResolution().getScaledHeight() - 20, 0xFFFFFF);
                GL11.glPopMatrix();
            }
        }
    }



    @Mod.EventHandler
    public void init(FMLInitializationEvent event){ ModConfig.updateMobsRespawningTime();}

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        tick++;
        if (tick % 200 == 0){
            if(Minecraft.getMinecraft().player != null)
                Minecraft.getMinecraft().player.sendChatMessage("/lag");

        }
    }
}
