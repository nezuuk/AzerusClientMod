package ru.emrass.azerusclientmod.DropMobs;

import net.minecraft.util.text.TextFormatting;

import java.util.Date;

public class MobDeath {
    public String name;
    public TextFormatting nameColor;
    public Date deathAt;
    public Date respawningTime;
    public String uuid;
    public String server;

    public MobDeath(String name, TextFormatting nameColor, Date deathAt, Date respawningTime, String uuid, String server) {
        this.name = name;
        this.nameColor = nameColor;
        this.deathAt = deathAt;
        this.respawningTime = respawningTime;
        this.uuid = uuid;
        this.server = server;
    }
}
