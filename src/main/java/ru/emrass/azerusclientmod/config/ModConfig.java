package ru.emrass.azerusclientmod.config;


import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import ru.emrass.azerusclientmod.AzerusClientMod;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Config(modid = "azerusclientmod",category = "")
public class ModConfig
{
    private static final String PREFIX = "config." + "azerusclientmod";

    @Config.Name("Счётчик возрождения мобов")
    @Config.Comment("Категория конфигов счётчика возрождения мобов")
    public static final MobsCooldown MOBS_COOLDOWN = new MobsCooldown();

    public static class MobsCooldown {
        @Config.Name("Отображать счётчик")
        public boolean isShowMobsCooldown = true;
        @Config.Name("Размер счётчика")
        @Config.Comment("Задаёт размер счётчика\nРекомендуемое значение - 1.0")
        public float counterSize = 1.0F;

        @Config.Name("Список мобов")
        @Config.Comment("Список имен мобов и время их возрождения в минутах.")
        public String[] mobsRespawningTime = {
                "Восставший маг:3",
                "Восставший волшебник:3"
        };

        @Config.Name("Групирование одинаковых мобов")
        @Config.Comment("Будут ли одинаковые мобы группироваться в одну строчку в счётчике.")
        public boolean mobStacking = true;
    }



    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equals(AzerusClientMod.MODID)) {
            ConfigManager.sync(AzerusClientMod.MODID, Config.Type.INSTANCE);
            updateMobsRespawningTime();
        }
    }

    public static void updateMobsRespawningTime() {
        AzerusClientMod.mobsRespawningTime.clear();
        String[] items = MOBS_COOLDOWN.mobsRespawningTime;

        for (String item : items ) {
            Pattern pattern = Pattern.compile("(.+):(\\d+)");
            Matcher matcher = pattern.matcher(item);
            if (matcher.find()) {
                AzerusClientMod.mobsRespawningTime.put(matcher.group(1), new Date(Integer.parseInt(matcher.group(2)) * 60000L));
            }
        }
    }
}