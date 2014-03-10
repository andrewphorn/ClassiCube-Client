package com.mojang.minecraft;

import com.mojang.minecraft.player.Player;

public class SelectionBoxData {
    public byte id;
    public String name;
    public ColorCache color;
    public CustomAABB bounds;

    public SelectionBoxData(byte ID, String Name, ColorCache Color, CustomAABB Bounds) {
        this.id = ID;
        this.name = Name;
        this.bounds = Bounds;
        this.color = Color;
    }

    public final float distanceSquared0(Player player) {
        float dx = player.x - bounds.x0;
        float dy = player.y - bounds.y0;
        float dz = player.z - bounds.z0;
        return dx * dx + dy * dy + dz * dz;
    }

    public final float distanceSquared1(Player player) {
        float dx = player.x - bounds.x1;
        float dy = player.y - bounds.y1;
        float dz = player.z - bounds.z1;
        return dx * dx + dy * dy + dz * dz;
    }
}
