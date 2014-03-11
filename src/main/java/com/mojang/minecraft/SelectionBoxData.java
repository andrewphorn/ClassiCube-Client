package com.mojang.minecraft;

import com.mojang.minecraft.physics.CustomAABB;
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
        float dx = player.x - bounds.maxX;
        float dy = player.y - bounds.maxY;
        float dz = player.z - bounds.maxZ;
        return dx * dx + dy * dy + dz * dz;
    }

    public final float distanceSquared1(Player player) {
        float dx = player.x - bounds.minX;
        float dy = player.y - bounds.minY;
        float dz = player.z - bounds.minZ;
        return dx * dx + dy * dy + dz * dz;
    }
}
