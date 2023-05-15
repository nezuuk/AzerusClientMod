package ru.emrass.azerusclientmod.DropMobs;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import ru.emrass.azerusclientmod.AzerusClientMod;
import ru.emrass.azerusclientmod.config.ModConfig;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DropDisplay {
    private AzerusClientMod mod;
    public static Minecraft mc = Minecraft.getMinecraft();

    public static MobDeaths mobDeaths = new MobDeaths();

    public static String lastAttackEntityPlayer;

    public static boolean ignoreServerInfo = false;
    public DropDisplay(AzerusClientMod mod) {
        this.mod = mod;
    }

    @SubscribeEvent
    public void onEntityKill(LivingDeathEvent event) {
        final List<ItemStack> entityDrop = new ArrayList<ItemStack>();
        final List<Entity> entities = (List<Entity>) Minecraft.getMinecraft().world.getLoadedEntityList();
        for (final Entity entity : entities) {
            if (entity instanceof EntityItem) {
                entity.setEntityInvulnerable(true);
                ItemStack item = ((EntityItem) entity).getItem();
                if (entity.ticksExisted != 0) {
                    continue;
                }
                entityDrop.add(item);
            }
        }
        if (!entityDrop.isEmpty()) {
            final EntityPlayerSP player = Minecraft.getMinecraft().player;
            player.sendMessage((ITextComponent) new TextComponentString("Дроп с " + getOnlyMobName(event.getEntity())));
            for (final ItemStack item : entityDrop) {
                final String serializedNBT = item.serializeNBT().toString();
                final ITextComponent itemString = new TextComponentString(item.getDisplayName() + " (x" + item.getCount() + ")").setStyle(new Style().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, (ITextComponent) new TextComponentString(serializedNBT))).setColor(getItemColor(item)));
                player.sendMessage(itemString);
            }
        }
    }

    public static String getOnlyMobName(final Entity mob) {
        final String fullName = mob.getDisplayName().getFormattedText();
        return fullName.replaceAll("§4\\[.*] ", "").replaceAll(" §c\\[.*]", "");
    }
    public static TextFormatting getItemColor(ItemStack item) {
        String itemName = item.getDisplayName();
        itemName = itemName.replaceAll("§c\\+\\d", "");

        TextFormatting nameColor = TextFormatting.WHITE;

        if (itemName.matches(".*§4.*")) { nameColor = TextFormatting.DARK_RED; }
        else if (itemName.matches(".*§6.*")) { nameColor = TextFormatting.GOLD; }
        else if (itemName.matches(".*§e.*")) { nameColor = TextFormatting.YELLOW; }
        else if (itemName.matches(".*§2.*")) { nameColor = TextFormatting.DARK_GREEN; }
        else if (itemName.matches(".*§a.*")) { nameColor = TextFormatting.GREEN; }
        else if (itemName.matches(".*§1.*")) { nameColor = TextFormatting.DARK_BLUE; }
        else if (itemName.matches(".*§9.*")) { nameColor = TextFormatting.BLUE; }
        else if (itemName.matches(".*§d.*")) { nameColor = TextFormatting.LIGHT_PURPLE; }
        else if (itemName.matches(".*§5.*")) { nameColor = TextFormatting.DARK_PURPLE; }
        return nameColor;
    }


    @SubscribeEvent
    public static void onChat(ClientChatReceivedEvent e) {
        if (ModConfig.MOBS_COOLDOWN.isShowMobsCooldown) {
            String msg = e.getMessage().getFormattedText();

//            if (msg.matches(".*Фенрир.*") && msg.matches(".*я перевоплощусь и вернусь.*")) {
//                String mobName = "Фенрир";
//
//                assert AzerusHelper.mobsRespawningTime != null;
//                for (Map.Entry entry : AzerusHelper.mobsRespawningTime.entrySet()) {
//                    if (mobName.matches(".*" + entry.getKey() + ".*")) {
//                        Calendar currentTime = Calendar.getInstance();
//                        currentTime.setTimeInMillis(System.currentTimeMillis());
//                        String timeUUID = String.valueOf(currentTime.get(Calendar.SECOND));
//                        mobDeaths.add(
//                                (String) entry.getKey(),
//                                TextFormatting.DARK_RED,
//                                new Date(System.currentTimeMillis()),
//                                (Date) entry.getValue(),
//                                timeUUID,
//                                AzerusHelper.currentServerName
//                        );
//                    }
//                }
//            } else
            if (msg.matches(".*Перезагрузка сервера\\..*")) {
                mobDeaths.removeByServer(AzerusClientMod.currentServerName);
            }
            if (msg.matches(".*Ваши данные были успешно загружены.*")) {
                new java.util.Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        ignoreServerInfo = true;
                        Minecraft.getMinecraft().player.sendChatMessage("/lag");
                    }
                }, 300);
            } else if (msg.matches(".*Рабочее время: .*")) {
                Pattern patternH = Pattern.compile(".* (\\d\\d)h.*");
                Matcher matcherH = patternH.matcher(msg);
                int hours = matcherH.find() ? Integer.parseInt(matcherH.group(1)) : 0;

                Pattern patternM = Pattern.compile(".*(\\d\\d)m.*");
                Matcher matcherM = patternM.matcher(msg);
                int minutes = matcherM.find() ? Integer.parseInt(matcherM.group(1)) : 0;

                Pattern patternS = Pattern.compile(".*(\\d\\d)s.*");
                Matcher matcherS = patternS.matcher(msg);
                int seconds = matcherS.find() ? Integer.parseInt(matcherS.group(1)) : 0;

                int ms = (hours * 3600000) + (minutes * 60000) + (seconds * 1000);
                for (MobDeath mob : mobDeaths.mobs) {
                    if ((mob.respawningTime.getTime() - (new Date(System.currentTimeMillis()).getTime() - mob.deathAt.getTime())) > ms) {
                        mobDeaths.removeByUUID(mob.uuid);
                    }
                }

                if (ignoreServerInfo) e.setCanceled(true);

                new java.util.Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        ignoreServerInfo = false;
                    }
                }, 1000);
            }
        }
    }
//

    public static TextFormatting stringToColor(String string) {
        switch (string) {
            case("RED"): return TextFormatting.RED;
            case("DARK_RED"): return TextFormatting.DARK_RED;
            case("GOLD"): return TextFormatting.GOLD;
            default: return TextFormatting.WHITE;
        }
    }

    public static String colorToString(TextFormatting color) {
        if (color == TextFormatting.RED) return "RED";
        else if (color == TextFormatting.DARK_RED) return "DARK_RED";
        else if (color == TextFormatting.GOLD) return "GOLD";
        else return "WHITE";
    }

}
