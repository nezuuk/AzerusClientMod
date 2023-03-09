package ru.emrass.azerusclientmod.DropMobs;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import ru.emrass.azerusclientmod.AzerusClientMod;
import ru.emrass.azerusclientmod.config.ModConfig;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static ru.emrass.azerusclientmod.DropMobs.DropDisplay.getMobColor;
import static ru.emrass.azerusclientmod.DropMobs.DropDisplay.mobDeaths;

public class MobTimer {



    public static void onRenderGameOverlay() {
        int i = 0;
        for (MobDeath mob : mobDeaths.mobs) {
            i++;

            long ms = mob.respawningTime.getTime() - (new Date(System.currentTimeMillis()).getTime() - mob.deathAt.getTime());
            if (ms <= 0) {
                mobDeaths.removeIrrelevant();
                break;
            }

            renderMobCooldown(mob, i, 1);
        }

    }
    public static void renderMobCooldown(MobDeath mob, int index, int stackCount) {
        Date timeToRespawn = new Date(mob.respawningTime.getTime() - (new Date(System.currentTimeMillis()).getTime() - mob.deathAt.getTime()));

        String counterString = (new TextComponentString(mob.server + " "))
                .appendSibling((new TextComponentString(mob.name + (stackCount > 1 ? (" (" + stackCount + ")") : "")).setStyle(new Style().setColor(mob.nameColor))))
                .appendSibling((new TextComponentString(" - " + (new SimpleDateFormat("mm:ss")).format(timeToRespawn))))
//                        .appendSibling((new TextComponentString(" (" + mob.killedBy + ")").setStyle(new Style().setColor(TextFormatting.GRAY))))
                .getFormattedText();


        Minecraft mc = Minecraft.getMinecraft();
        GL11.glPushMatrix();
        GL11.glScalef(ModConfig.MOBS_COOLDOWN.counterSize, ModConfig.MOBS_COOLDOWN.counterSize, ModConfig.MOBS_COOLDOWN.counterSize);
        mc.fontRenderer.drawString(counterString, 5, (10 * index), 0xFFFF00, false);
        GL11.glPopMatrix();
    }
    @SubscribeEvent
    public void onClientChatMsg(ClientChatReceivedEvent e) {
        ITextComponent ex = e.getMessage();

        if (ex.toString().contains("Нагрузка")) e.setCanceled(true);
        if (ex.toString().contains("Нагрузка") && ex.toString().contains("Время работы:")) {
            AzerusClientMod.serverTime = ex.getFormattedText().substring(35).replace("§b","§e");
            e.setCanceled(true);
        }
        if (ex.toString().contains("Нагрузка") && ex.toString().contains("Имя сервера:")) {
            AzerusClientMod.currentServerName = ex.getFormattedText().substring(34).replace("§b","§e");
            e.setCanceled(true);
        }
    }
    @SubscribeEvent
    public void onKillEntity(LivingDeathEvent event){
        String mobName = event.getEntity().getName();
        String mobUUID = event.getEntity().getUniqueID().toString();
        TextFormatting nameColor = getMobColor(mobName);
        for (Map.Entry entry : AzerusClientMod.mobsRespawningTime.entrySet()) {
            if (mobName.matches(".*" + Pattern.quote(entry.getKey().toString()) + ".*")) {
                mobDeaths.add(
                        (String) entry.getKey(),
                        nameColor,
                        new Date(System.currentTimeMillis()),
                        (Date) entry.getValue(),
                        mobUUID,
                        AzerusClientMod.currentServerName
                );
            }
        }
    }
}
