package com.perfboarddesigner.model;

public enum BoardType {
    PERFORATED("Perforated/Dot Board"),
    STRIPBOARD("Stripboard"),
    MIXED("Mixed Dot-Strip Board");

    private final String displayName;

    BoardType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}