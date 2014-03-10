package com.mojang.minecraft;

public enum OperatingSystem {
    LINUX("linux", 0), SOLARIS("solaris", 1), WINDOWS("windows", 2), MAC_OS_X("macos", 3), UNKNOWN(
            "unknown", 4);

    public final String folderName;
    public final int id;

    public static final OperatingSystem[] values = new OperatingSystem[] { LINUX, SOLARIS, WINDOWS,
            MAC_OS_X, UNKNOWN };

    private OperatingSystem(String folderName, int id) {
        this.folderName = folderName;
        this.id = id;
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
