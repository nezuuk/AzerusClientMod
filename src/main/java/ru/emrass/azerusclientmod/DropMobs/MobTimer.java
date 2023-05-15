package ru.emrass.azerusclientmod.DropMobs;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import ru.emrass.azerusclientmod.AzerusClientMod;
import ru.emrass.azerusclientmod.config.ModConfig;
import sun.text.resources.FormatData;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static ru.emrass.azerusclientmod.AzerusClientMod.getMobColor;
import static ru.emrass.azerusclientmod.DropMobs.DropDisplay.*;

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
        Date timeToRespawn = new Date(mob.respawningTime.getTime() - (System.currentTimeMillis() - mob.deathAt.getTime()) - 10800000L);

        String counterString = (new TextComponentString(mob.server + " "))
                .appendSibling((new TextComponentString(mob.name + (stackCount > 1 ? (" (" + stackCount + ")") : ""))))
                .appendSibling((new TextComponentString(" - " + (new SimpleDateFormat("HH:mm:ss").format(timeToRespawn)))))
                .getFormattedText();


        Minecraft mc = Minecraft.getMinecraft();
        GL11.glPushMatrix();
        GL11.glScalef(ModConfig.MOBS_COOLDOWN.counterSize, ModConfig.MOBS_COOLDOWN.counterSize, ModConfig.MOBS_COOLDOWN.counterSize);
        mc.fontRenderer.drawString(counterString, 5, (10 * index), ModConfig.customize.TimerColor, false);
        GL11.glPopMatrix();
    }
    @SubscribeEvent
    public void onClientChatMsg(ClientChatReceivedEvent e) {
        ITextComponent ex = e.getMessage();

        if (ex.toString().contains("Нагрузка")) e.setCanceled(true);
        if (ex.toString().contains("Нагрузка") && ex.toString().contains("Время работы:")) {
            AzerusClientMod.serverTime = ex.getFormattedText().substring(35).replace("§e","§" + ModConfig.customize.OnlineServerColor);
            e.setCanceled(true);
        }
        if (ex.toString().contains("Нагрузка") && ex.toString().contains("Имя сервера:")) {
            AzerusClientMod.currentServerName = ex.getFormattedText().substring(34).replace("§b","§" + ModConfig.customize.ServerColor);
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
                        (String)"§"+ModConfig.customize.NameMobs + entry.getKey(),
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
