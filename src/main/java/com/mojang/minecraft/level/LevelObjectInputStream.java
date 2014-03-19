package com.mojang.minecraft.level;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.util.HashSet;
import java.util.Set;

import com.mojang.minecraft.LogUtil;

public final class LevelObjectInputStream extends ObjectInputStream {

    private final Set<String> classes = new HashSet<>();

    public LevelObjectInputStream(InputStream inputStream) throws IOException {
        super(inputStream);
        classes.add("Player$1");
        classes.add("Creeper$1");
        classes.add("Skeleton$1");
    }

    @Override
    protected final ObjectStreamClass readClassDescriptor() {
        try {
            ObjectStreamClass var1 = super.readClassDescriptor();
            return classes.contains(var1.getName()) ? ObjectStreamClass.lookup(Class.forName(var1
                    .getName())) : var1;
        } catch (Exception ex) {
            LogUtil.logError("Error reading class descriptor from LevelObjectInputStream", ex);
            return null;
        }
    }
}
