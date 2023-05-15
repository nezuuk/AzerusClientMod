package ru.emrass.azerusclientmod.DropMobs;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;
import ru.emrass.azerusclientmod.config.ModConfig;

import java.util.*;

public class MobDeaths {
    public List<MobDeath> mobs = new ArrayList<MobDeath>();

    public void add(String name, TextFormatting nameColor, Date deathAt, Date respawningTime, String uuid, String server) {
        if (!findByUUID(uuid)) {
            mobs.add(new MobDeath(name, nameColor, deathAt, respawningTime, uuid, server));
            Minecraft.getMinecraft().player.sendChatMessage("/gc [VX] SRV(" + getStringWithoutColors(server) + ") NAME(" + name + ") ID(" + uuid + ") COL(" + DropDisplay.colorToString(nameColor) + ") CD(" + respawningTime.getTime() + ")");

        }
    }
    public static String getStringWithoutColors(String styledString) { return styledString.replaceAll("�.", ""); }
    public HashMap<String, HashMap<String, List<MobDeath>>> getGroups() {
        HashMap<String, List<MobDeath>> serverGroups = groupByServer();
        HashMap<String, HashMap<String, List<MobDeath>>> groups = new HashMap<String, HashMap<String, List<MobDeath>>>();

        for (Map.Entry<String, List<MobDeath>> server : serverGroups.entrySet()) {
            HashMap<String, List<MobDeath>> groupedMobs = groupByName(server.getValue());
            groups.put(server.getKey(), groupedMobs);
        }

        return groups;
    }

    private HashMap<String, List<MobDeath>> groupByServer() {
        HashMap<String, List<MobDeath>> groups = new HashMap<String, List<MobDeath>>();

        for (MobDeath mob: mobs) {
            if (!groups.containsKey(mob.server)) {
                groups.put(mob.server, Arrays.asList(mob));
            } else {
                List<MobDeath> arr = new ArrayList<MobDeath>(groups.get(mob.server));
                arr.add(mob);
                groups.put(mob.server, arr);
            }
        }
        return groups;
    }

    private static HashMap<String, List<MobDeath>> groupByName(List<MobDeath> mobes) {
        HashMap<String, List<MobDeath>> groups = new HashMap<String, List<MobDeath>>();

        for (MobDeath mob: mobes) {
            if (!groups.containsKey(mob.name)) {
                groups.put(mob.name, Arrays.asList(mob));
            } else {
                List<MobDeath> arr = new ArrayList<MobDeath>(groups.get(mob.name));
                arr.add(mob);
                groups.put(mob.name, arr);
            }
        }
        return groups;
    }

    public static MobDeath getMinLastTimeMob (List<MobDeath> mobes) {
        MobDeath min = mobes.get(0);
        for(MobDeath mob: mobes) {
            if ((System.currentTimeMillis() - mob.deathAt.getTime()) > (System.currentTimeMillis() - min.deathAt.getTime())) {
                min = mob;
            }
        }
        return min;
    }

    public boolean findByUUID(String uuid) {
        for (MobDeath mob : mobs) {
            if (mob.uuid.equals(uuid)) return true;
        }
        return false;
    }

    public void remove(int index) {
        mobs.remove(index);
    }

    public void removeAll() {
        mobs.clear();
    }

    public void removeByServer(String server) {
        mobs.removeIf(mob -> mob.server.equals(server));
    }

    public void removeIrrelevant() {
        mobs.removeIf(mob -> (mob.respawningTime.getTime() - (new Date(System.currentTimeMillis()).getTime() - mob.deathAt.getTime())) <= 0);
    }

    public void removeByUUID(String uuid) {
        mobs.removeIf(mob -> mob.uuid.equals(uuid));
    }
}
