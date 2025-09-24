package com.perfboarddesigner.model;

public enum ComponentOrientation {
    HORIZONTAL("Horizontal", 0),
    VERTICAL("Vertical", 90),
    DIAGONAL_UP("Diagonal Up", 45),
    DIAGONAL_DOWN("Diagonal Down", -45);

    private final String displayName;
    private final double angleDegrees;

    ComponentOrientation(String displayName, double angleDegrees) {
        this.displayName = displayName;
        this.angleDegrees = angleDegrees;
    }

    public String getDisplayName() { return displayName; }
    public double getAngleDegrees() { return angleDegrees; }
}
