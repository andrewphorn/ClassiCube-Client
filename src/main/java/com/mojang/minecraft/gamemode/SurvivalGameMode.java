package com.mojang.minecraft.gamemode;

import com.mojang.minecraft.Minecraft;
import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.level.MobSpawner;
import com.mojang.minecraft.level.tile.Block;
import com.mojang.minecraft.mob.Mob;
import com.mojang.minecraft.player.Player;

public final class SurvivalGameMode extends GameMode {
    public SurvivalGameMode(Minecraft minecraft) {
        super(minecraft);
        this.minecraft = minecraft;
    }

    private int hitX;
    private int hitY;
    private int hitZ;
    private int hits;
    private int hardness;
    private int hitDelay;

    @Override
    public void apply(Level level) {
        super.apply(level);

        // spawner = new MobSpawner(level, this.minecraft.settings.Peaceful);
    }

    @Override
    public void stopSpawner(Level level) {
        this.spawner.HasStopped = true;
    }

    @Override
    public void hitBlock(int x, int y, int z, int side) {
        if (hitDelay > 0) {
            hitDelay--;
        } else if (x == hitX && y == hitY && z == hitZ) {
            int type = minecraft.level.getTile(x, y, z);

            if (type != 0) {
                Block block = Block.blocks[type];

                hardness = block.getHardness();

                block.spawnBlockParticles(minecraft.level, x, y, z, side, minecraft.particleManager);

                hits++;

                if (hits == hardness + 1) {
                    breakBlock(x, y, z);

                    hits = 0;
                    hitDelay = 5;
                }

            }
        } else {
            // TODO: I think if I don't set hits to 0 you can continue breaking
            // from where you left off.

            hits = 0;
            hitX = x;
            hitY = y;
            hitZ = z;
        }
    }

    @Override
    public boolean canPlace(int block) {
        return minecraft.player.inventory.removeResource(block);
    }

    @Override
    public void breakBlock(int x, int y, int z) {
        int block = minecraft.level.getTile(x, y, z);
        Block.blocks[block].onBreak(minecraft.level, x, y, z);

        super.breakBlock(x, y, z);
    }

    @Override
    public void hitBlock(int x, int y, int z) {
        int block = this.minecraft.level.getTile(x, y, z);

        if (block > 0 && Block.blocks[block].getHardness() == 0) {
            breakBlock(x, y, z);
        }
    }

    @Override
    public void resetHits() {
        this.hits = 0;
        this.hitDelay = 0;
    }

    @Override
    public void applyCracks(float time) {
        if (hits <= 0) {
            minecraft.levelRenderer.cracks = 0.0F;
        } else {
            minecraft.levelRenderer.cracks = (hits + time - 1.0F) / hardness;
        }
    }

    @Override
    public float getReachDistance() {
        return 4.0F;
    }

    @Override
    public boolean useItem(Player player, int type) {
        Block block = Block.blocks[type];
        if (block == Block.RED_MUSHROOM && minecraft.player.inventory.removeResource(type)) {
            player.health += 4;
            if (player.health > 30)
                player.health = 30;
            return true;
        } else if (block == Block.BROWN_MUSHROOM && minecraft.player.inventory.removeResource(type)) {
            player.health += 4;
            if (player.health > 15)
                player.health = 30;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void preparePlayer(Player player) {
        for (int i = 0; i < 49; i++) {
            if (i != Block.TNT.id)
                player.inventory.removeResource(i);
        }
        player.inventory.slots[8] = Block.TNT.id;
        player.inventory.count[8] = 20;

    }

    @Override
    public void spawnMob() {
        if (this.spawner.HasStopped)
            return;
        int area = this.spawner.level.width * this.spawner.level.length * spawner.level.height / 64
                / 64 / 64;

        if (spawner.level.random.nextInt(100) < area
                && this.spawner.level.countInstanceOf(Mob.class) < area * 10) {
            this.spawner.spawn(area, this.spawner.level.player, null);
        }

    }

    @Override
    public void prepareLevel(Level level) {
        this.spawner = new MobSpawner(level);

        minecraft.progressBar.setText("Spawning..");
    }
}
