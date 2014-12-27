package com.mojang.minecraft;

import java.io.File;

public enum OperatingSystem {
    LINUX("linux", 0), SOLARIS("solaris", 1), WINDOWS("windows", 2) {
        public File getMinecraftFolder(String home, String folder) {
            String appData = System.getenv("APPDATA");

            if (appData != null) {
                return new File(appData, folder + '/');
            } else {
                return new File(home, folder + '/');
            }
        }
    }, MAC_OS_X("macos", 3) {
        public File getMinecraftFolder(String home, String folder) {
            return new File(home, "Library/Application Support/" + folder);
        }
    }, UNKNOWN("unknown", 4);

    public static final OperatingSystem[] values = new OperatingSystem[]{LINUX, SOLARIS, WINDOWS,
            MAC_OS_X, UNKNOWN};
    public final String folderName;
    public final int id;

    private OperatingSystem(String folderName, int id) {
        this.folderName = folderName;
        this.id = id;
    }

    /* Windows and OSX override this */
    public File getMinecraftFolder(String home, String folder) {
        return new File(home, folder + '/');
    }

    public static OperatingSystem detect() {
        String s = System.getProperty("os.name").toLowerCase();
        if (s.contains("win")) {
            return OperatingSystem.WINDOWS;
        }
        if (s.contains("mac")) {
            return OperatingSystem.MAC_OS_X;
        }
        if (s.contains("solaris") || s.contains("sunos")) {
            return OperatingSystem.SOLARIS;
        }
        if (s.contains("linux") || s.contains("unix")) {
            return OperatingSystem.LINUX;
        }
        return OperatingSystem.UNKNOWN;
    }
}
