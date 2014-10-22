package com.mojang.minecraft;

public enum ThirdPersonMode {
    NONE,
    BACK_FACING,
    FRONT_FACING {
        @Override
        public ThirdPersonMode next() {
            return values()[0]; // rollover to the first element
        };
    };
    
    public ThirdPersonMode next() {
        // No bounds checking required here, because the last instance overrides
        return values()[ordinal() + 1];
    }
}
