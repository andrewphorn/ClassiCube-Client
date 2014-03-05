package com.mojang.minecraft.gui;

public final class GenerateLevelScreen extends GuiScreen {

    private GuiScreen parent;

    public GenerateLevelScreen(GuiScreen var1) {
        parent = var1;
    }

    @Override
    protected final void onButtonClick(Button var1) {
        if (var1.id == 3) {
            minecraft.setCurrentScreen(parent);
        } else {
            minecraft.generateLevel(var1.id);
            minecraft.setCurrentScreen((GuiScreen) null);
            minecraft.grabMouse();
        }
    }

    @Override
    public final void onOpen() {
        buttons.clear();
        buttons.add(new Button(0, width / 2 - 100, height / 4, "Small"));
        buttons.add(new Button(1, width / 2 - 100, height / 4 + 24, "Normal"));
        buttons.add(new Button(2, width / 2 - 100, height / 4 + 48, "Huge"));
        buttons.add(new Button(3, width / 2 - 100, height / 4 + 120, "Cancel"));
    }

    @Override
    public final void render(int var1, int var2) {
        drawFadingBox(0, 0, width, height, 1610941696, -1607454624);
        drawCenteredString(fontRenderer, "Generate new level", width / 2, 40, 16777215);
        super.render(var1, var2);
    }
}
