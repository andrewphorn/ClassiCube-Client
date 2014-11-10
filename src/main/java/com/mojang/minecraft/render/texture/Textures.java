package com.mojang.minecraft.render.texture;

public final class Textures {

    public static final String PARTICLES = "/particles.png",
            TERRAIN = "/terrain.png",
            LOADING_BACKGROUND = "/dirt.png",
            MAP_SIDE = "/rock.png",
            MAP_EDGE = "/water.png",
            FONT = "/default.png",
            GUI = "/gui/gui.png",
            ICONS = "/gui/icons.png",
            ARROWS = "/item/arrows.png",
            ARMOR_PLATE = "/armor/plate.png",
            SHEEP_FUR = "/mob/sheep_fur.png",
            CLOUDS = "/clouds.png",
            RAIN = "/rain.png",
            SNOW = "/snow.png",
            MOB_HUMANOID = "/char.png",
            MOB_CHICKEN = "/mob/chicken.png",
            MOB_CREEPER = "/mob/creeper.png",
            MOB_CROC = "/mob/croc.png",
            MOB_PIG = "/mob/pig.png",
            MOB_PRINTER = "/mob/printer.png",
            MOB_SHEEP = "/mob/sheep.png",
            MOB_SKELETON = "/mob/skeleton.png",
            MOB_SPIDER = "/mob/spider.png",
            MOB_ZOMBIE = "/mob/zombie.png";

    public static String forModel(String modelName) {
        return "/mob/" + modelName + ".png";
    }
}
