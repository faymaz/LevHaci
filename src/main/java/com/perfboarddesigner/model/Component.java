package com.perfboarddesigner.model;

import javafx.scene.layout.Pane;

public abstract class Component {
    protected String name;
    protected double x;
    protected double y;
    protected String type;
    protected int pins;

    public Component(String name, double x, double y, String type, int pins) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.type = type;
        this.pins = pins;
    }

    public abstract Pane render();
}