package com.mojang.minecraft.item;

import com.mojang.minecraft.Entity;
import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.render.TextureManager;

public class TakeEntityAnim extends Entity {
    private static final long serialVersionUID = 1L;

    private int time = 0;

    private Entity item;

    private Entity player;

    private float xorg;

    private float yorg;
    private float zorg;

    public TakeEntityAnim(Level level, Entity item, Entity player) {
        super(level);

        this.item = item;
        this.player = player;

        setSize(1F, 1F);

        xorg = item.x;
        yorg = item.y;
        zorg = item.z;
    }

    @Override
    public void render(TextureManager textureManager, float delta) {
        item.render(textureManager, delta);
    }

    @Override
    public void tick() {
        time++;

        if (time >= 3) {
            remove();
        }

        // TODO: Is this right?
        float distance = (distance = time / 3F) * distance;

        xo = item.xo = item.x;
        yo = item.yo = item.y;
        zo = item.zo = item.z;

        x = item.x = xorg + (player.x - xorg) * distance;
        y = item.y = yorg + (player.y - 1F - yorg) * distance;
        z = item.z = zorg + (player.z - zorg) * distance;

        setPos(x, y, z);
    }
}
