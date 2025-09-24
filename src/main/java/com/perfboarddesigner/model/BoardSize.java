package com.perfboarddesigner.model;

public enum BoardSize {
    SIZE_50x70("50x70mm", 50, 70),
    SIZE_70x90("70x90mm", 70, 90),
    SIZE_100x100("100x100mm", 100, 100),
    SIZE_100x160("100x160mm", 100, 160),
    SIZE_160x100("160x100mm", 160, 100),
    CUSTOM("Custom", 0, 0);

    private final String displayName;
    private final double width;
    private final double height;

    BoardSize(String displayName, double width, double height) {
        this.displayName = displayName;
        this.width = width;
        this.height = height;
    }

    public String getDisplayName() { return displayName; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public boolean isCustom() { return this == CUSTOM; }
}
