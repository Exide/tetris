package org.arabellan.common;

public class Platform {

    public boolean isLinux() {
        return System.getProperty("os.name").toLowerCase().contains("linux");
    }

    public boolean isLinux(String name) {
        return name.contains("linux");
    }

    public boolean isMacOS() {
        return System.getProperty("os.name").toLowerCase().contains("mac");
    }

    public boolean isMacOS(String name) {
        return name.contains("osx") || name.contains("mac");
    }

    public boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("windows");
    }

    public boolean isWindows(String name) {
        return name.contains("windows");
    }

    public boolean is64Bit() {
        String architecture = System.getProperty("os.arch");
        return architecture.equals("amd64") || architecture.equals("x86_64");
    }

    public boolean is32Bit() {
        return !is64Bit();
    }
}
