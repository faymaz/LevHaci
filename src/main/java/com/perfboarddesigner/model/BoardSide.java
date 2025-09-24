package com.perfboarddesigner.model;

public enum BoardSide {
    SINGLE("One Side"),
    DOUBLE("Double Side");

    private final String displayName;

    BoardSide(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}