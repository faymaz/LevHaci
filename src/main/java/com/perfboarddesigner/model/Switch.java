package com.perfboarddesigner.model;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;

public class Switch extends Component {
    private String switchType; // "PushButton", "Toggle", "Slide", "Rotary"
    private boolean state; // true = pressed/on, false = released/off
    private boolean momentary; // true for momentary switches, false for latching
    private static final double MM_TO_PIXEL = 6.0;

    public Switch(String name, double x, double y, String type, int pins, String switchType, boolean momentary) {
        super(name, x, y, type, pins);
        this.switchType = switchType;
        this.momentary = momentary;
        this.state = false;
    }

    @Override
    public Pane render() {
        Pane switchPane = new Pane();
        
        switch (switchType) {
            case "PushButton":
                renderPushButton(switchPane);
                break;
            case "Toggle":
                renderToggleSwitch(switchPane);
                break;
            case "Slide":
                renderSlideSwitch(switchPane);
                break;
            case "Rotary":
                renderRotarySwitch(switchPane);
                break;
            default:
                renderPushButton(switchPane);
        }

        // Control panel
        VBox controlBox = new VBox(5);
        controlBox.setLayoutY(getBodyHeight() + 10);
        
        Label typeLabel = new Label(switchType + " Switch");
        
        ComboBox<String> typeCombo = new ComboBox<>(FXCollections.observableArrayList(
            "PushButton", "Toggle", "Slide", "Rotary"));
        typeCombo.setValue(switchType);
        typeCombo.setOnAction(e -> {
            this.switchType = typeCombo.getValue();
            refreshSwitch(switchPane, controlBox);
        });
        
        CheckBox momentaryCheck = new CheckBox("Momentary");
        momentaryCheck.setSelected(momentary);
        momentaryCheck.setOnAction(e -> this.momentary = momentaryCheck.isSelected());
        
        CheckBox stateCheck = new CheckBox("State (On/Pressed)");
        stateCheck.setSelected(state);
        stateCheck.setOnAction(e -> {
            this.state = stateCheck.isSelected();
            refreshSwitch(switchPane, controlBox);
        });
        
        controlBox.getChildren().addAll(typeLabel, typeCombo, momentaryCheck, stateCheck);
        switchPane.getChildren().add(controlBox);

        switchPane.setPrefSize(Math.max(getBodyWidth(), 120), getBodyHeight() + 120);
        return switchPane;
    }
    
    private void renderPushButton(Pane pane) {
        double diameter = 12 * MM_TO_PIXEL;
        
        // Button body (round)
        Circle body = new Circle(diameter/2, diameter/2, diameter/2);
        body.setFill(state ? Color.RED : Color.LIGHTGRAY);
        body.setStroke(Color.BLACK);
        body.setStrokeWidth(2);
        pane.getChildren().add(body);
        
        // Button actuator (smaller circle)
        Circle actuator = new Circle(diameter/2, diameter/2, diameter/4);
        actuator.setFill(state ? Color.DARKRED : Color.GRAY);
        actuator.setStroke(Color.BLACK);
        pane.getChildren().add(actuator);
        
        // Four pins (for tactile switch)
        double pinOffset = diameter * 0.3;
        double pinSize = 2;
        
        Rectangle pin1 = new Rectangle(-pinSize/2, pinOffset, pinSize, pinSize);
        Rectangle pin2 = new Rectangle(diameter - pinSize/2, pinOffset, pinSize, pinSize);
        Rectangle pin3 = new Rectangle(-pinSize/2, diameter - pinOffset, pinSize, pinSize);
        Rectangle pin4 = new Rectangle(diameter - pinSize/2, diameter - pinOffset, pinSize, pinSize);
        
        pin1.setFill(Color.SILVER);
        pin2.setFill(Color.SILVER);
        pin3.setFill(Color.SILVER);
        pin4.setFill(Color.SILVER);
        
        pane.getChildren().addAll(pin1, pin2, pin3, pin4);
    }
    
    private void renderToggleSwitch(Pane pane) {
        double width = 8 * MM_TO_PIXEL;
        double height = 12 * MM_TO_PIXEL;
        
        // Switch body
        Rectangle body = new Rectangle(0, 0, width, height);
        body.setFill(Color.DARKGRAY);
        body.setStroke(Color.BLACK);
        body.setStrokeWidth(1);
        pane.getChildren().add(body);
        
        // Toggle lever
        double leverX = width / 2;
        double leverY = state ? height * 0.3 : height * 0.7;
        Circle lever = new Circle(leverX, leverY, width * 0.15);
        lever.setFill(Color.WHITE);
        lever.setStroke(Color.BLACK);
        pane.getChildren().add(lever);
        
        // Connection line
        Line connection = new Line(leverX, height/2, leverX, leverY);
        connection.setStroke(Color.BLACK);
        connection.setStrokeWidth(2);
        pane.getChildren().add(connection);
        
        // Three pins (common, NO, NC)
        double pinSpacing = width / 4;
        for (int i = 0; i < 3; i++) {
            Rectangle pin = new Rectangle(pinSpacing * (i + 0.5), height, 1, 6);
            pin.setFill(Color.SILVER);
            pane.getChildren().add(pin);
        }
    }
    
    private void renderSlideSwitch(Pane pane) {
        double width = 15 * MM_TO_PIXEL;
        double height = 6 * MM_TO_PIXEL;
        
        // Switch body
        Rectangle body = new Rectangle(0, 0, width, height);
        body.setFill(Color.DARKGRAY);
        body.setStroke(Color.BLACK);
        body.setStrokeWidth(1);
        pane.getChildren().add(body);
        
        // Slide actuator
        double slideWidth = width * 0.2;
        double slideX = state ? width - slideWidth - 2 : 2;
        Rectangle slide = new Rectangle(slideX, 1, slideWidth, height - 2);
        slide.setFill(Color.WHITE);
        slide.setStroke(Color.BLACK);
        pane.getChildren().add(slide);
        
        // Pins
        double pinSpacing = width / 4;
        for (int i = 0; i < 3; i++) {
            Rectangle pin = new Rectangle(pinSpacing * (i + 1) - 0.5, height, 1, 4);
            pin.setFill(Color.SILVER);
            pane.getChildren().add(pin);
        }
    }
    
    private void renderRotarySwitch(Pane pane) {
        double diameter = 15 * MM_TO_PIXEL;
        
        // Switch body
        Circle body = new Circle(diameter/2, diameter/2, diameter/2);
        body.setFill(Color.DARKGRAY);
        body.setStroke(Color.BLACK);
        body.setStrokeWidth(2);
        pane.getChildren().add(body);
        
        // Rotation indicator
        double angle = state ? 45 : 0; // degrees
        double indicatorLength = diameter * 0.3;
        double radians = Math.toRadians(angle);
        double endX = diameter/2 + indicatorLength * Math.cos(radians);
        double endY = diameter/2 + indicatorLength * Math.sin(radians);
        
        Line indicator = new Line(diameter/2, diameter/2, endX, endY);
        indicator.setStroke(Color.WHITE);
        indicator.setStrokeWidth(3);
        pane.getChildren().add(indicator);
        
        // Multiple pins around the circumference (simplified to 4)
        for (int i = 0; i < 4; i++) {
            double pinAngle = i * 90; // degrees
            double pinRadians = Math.toRadians(pinAngle);
            double pinX = diameter/2 + (diameter/2 + 3) * Math.cos(pinRadians) - 0.5;
            double pinY = diameter/2 + (diameter/2 + 3) * Math.sin(pinRadians) - 0.5;
            
            Rectangle pin = new Rectangle(pinX, pinY, 1, 4);
            pin.setFill(Color.SILVER);
            pane.getChildren().add(pin);
        }
    }
    
    private void refreshSwitch(Pane switchPane, VBox controlBox) {
        switchPane.getChildren().clear();
        render();
        switchPane.getChildren().add(controlBox);
    }
    
    private double getBodyWidth() {
        switch (switchType) {
            case "Slide": return 15 * MM_TO_PIXEL;
            case "Rotary": return 15 * MM_TO_PIXEL;
            case "Toggle": return 8 * MM_TO_PIXEL;
            default: return 12 * MM_TO_PIXEL; // PushButton
        }
    }
    
    private double getBodyHeight() {
        switch (switchType) {
            case "Slide": return 6 * MM_TO_PIXEL;
            case "Rotary": return 15 * MM_TO_PIXEL;
            case "Toggle": return 12 * MM_TO_PIXEL;
            default: return 12 * MM_TO_PIXEL; // PushButton
        }
    }

    // Getters and setters
    public String getSwitchType() { return switchType; }
    public void setSwitchType(String switchType) { this.switchType = switchType; }
    public boolean getState() { return state; }
    public void setState(boolean state) { this.state = state; }
    public boolean isMomentary() { return momentary; }
    public void setMomentary(boolean momentary) { this.momentary = momentary; }
}
