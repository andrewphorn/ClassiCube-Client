package com.mojang.minecraft.gui;

public final class GenerateLevelScreen extends GuiScreen {

    private GuiScreen parent;

    public GenerateLevelScreen(GuiScreen parent) {
        this.parent = parent;
    }

    private int type = 0; // 0 = Terrain, 1 = Flat
    private int size = 1; // 0 = Small, 1 = Normal, 2 = Huge

    @Override
    protected final void onButtonClick(Button button) {
        if (button.id == 5) {
            minecraft.setCurrentScreen(parent);
        } else if (button.id == 4) {
            // Generate
            if (type == 0) {
                minecraft.generateLevel(size);
            } else {
                minecraft.generateFlatLevel(size);
            }
            minecraft.setCurrentScreen(null);
            minecraft.grabMouse();
        } else if (button.id == 0) {
            // Alter type.
            if (type == 1) {
                type = 0;
            } else {
                type = 1;
            }

            if (type == 1) {
                button.text = "Type: Flat";
            } else {
                button.text = "Type: Terrain";
            }
        } else if (button.id == 1) {
            // Alter size.
            if (size == 2) {
                size = 0;
            } else {
                size++;
            }

            if (size == 0) {
                button.text = "Size: Small";
            } else if (size == 1) {
                button.text = "Size: Normal";
            } else if (size == 2) {
                button.text = "Size: Huge";
            }
        }
    }

    @Override
    public final void onOpen() {
        buttons.clear();
        buttons.add(new Button(0, width / 2 - 100, height / 4, "Type: Terrain"));
        buttons.add(new Button(1, width / 2 - 100, height / 4 + 24, "Size: Normal"));

        buttons.add(new Button(4, width / 2 - 100, height / 4 + 96, "Generate Level"));
        buttons.add(new Button(5, width / 2 - 100, height / 4 + 120, "Cancel"));
    }

    @Override
    public final void render(int var1, int var2) {
        drawFadingBox(0, 0, width, height, 1610941696, -1607454624);
        drawCenteredString(fontRenderer, "Generate new level", width / 2, 40, 16777215);
        super.render(var1, var2);
    }
}
