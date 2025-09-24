package com.perfboarddesigner.model;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class Capacitor extends Component {
    private String value; // e.g., "100uF", "0.1uF"
    private String capacitorType; // "Electrolytic", "Ceramic", "Film"
    private double capacitanceF; // Capacitance in Farads
    private static final double MM_TO_PIXEL = 6.0; // 1mm = 6 pixels

    public Capacitor(String name, double x, double y, String type, int pins, String value, String capacitorType, double capacitanceF) {
        super(name, x, y, type, pins);
        this.value = value;
        this.capacitorType = capacitorType;
        this.capacitanceF = capacitanceF;
    }

    @Override
    public Pane render() {
        Pane capacitorPane = new Pane();
        double bodyWidth = 15 * MM_TO_PIXEL; // 15mm width
        double bodyHeight = 8 * MM_TO_PIXEL; // 8mm height
        
        if ("Electrolytic".equals(capacitorType)) {
            // Cylindrical electrolytic capacitor
            double radius = bodyHeight / 2;
            Arc body = new Arc(radius, radius, radius, radius, 0, 360);
            body.setType(ArcType.CHORD);
            body.setFill(getCapacitorColor());
            body.setStroke(Color.BLACK);
            body.setStrokeWidth(1);
            capacitorPane.getChildren().add(body);
            
            // Polarity marking (+/-)
            Line plusLine1 = new Line(radius - 5, radius - 10, radius + 5, radius - 10);
            Line plusLine2 = new Line(radius, radius - 15, radius, radius - 5);
            plusLine1.setStroke(Color.WHITE);
            plusLine2.setStroke(Color.WHITE);
            plusLine1.setStrokeWidth(2);
            plusLine2.setStrokeWidth(2);
            capacitorPane.getChildren().addAll(plusLine1, plusLine2);
            
            // Pins
            if (pins >= 2) {
                double pinLength = bodyWidth / 4;
                Rectangle pin1 = new Rectangle(-pinLength, radius - 0.5, pinLength, 1);
                pin1.setFill(Color.SILVER);
                Rectangle pin2 = new Rectangle(bodyWidth, radius - 0.5, pinLength, 1);
                pin2.setFill(Color.SILVER);
                capacitorPane.getChildren().addAll(pin1, pin2);
            }
            
        } else if ("Ceramic".equals(capacitorType)) {
            // Small ceramic disc capacitor
            bodyWidth = 8 * MM_TO_PIXEL;
            bodyHeight = 5 * MM_TO_PIXEL;
            
            Rectangle body = new Rectangle(0, 0, bodyWidth, bodyHeight);
            body.setFill(Color.LIGHTYELLOW);
            body.setStroke(Color.BLACK);
            body.setStrokeWidth(1);
            capacitorPane.getChildren().add(body);
            
            // Pins
            if (pins >= 2) {
                double pinLength = bodyWidth / 3;
                Rectangle pin1 = new Rectangle(-pinLength, bodyHeight / 2 - 0.5, pinLength, 1);
                pin1.setFill(Color.SILVER);
                Rectangle pin2 = new Rectangle(bodyWidth, bodyHeight / 2 - 0.5, pinLength, 1);
                pin2.setFill(Color.SILVER);
                capacitorPane.getChildren().addAll(pin1, pin2);
            }
            
        } else { // Film capacitor
            Rectangle body = new Rectangle(0, 0, bodyWidth, bodyHeight);
            body.setFill(Color.LIGHTBLUE);
            body.setStroke(Color.BLACK);
            body.setStrokeWidth(1);
            capacitorPane.getChildren().add(body);
            
            // Pins
            if (pins >= 2) {
                double pinLength = bodyWidth / 4;
                Rectangle pin1 = new Rectangle(-pinLength, bodyHeight / 2 - 0.5, pinLength, 1);
                pin1.setFill(Color.SILVER);
                Rectangle pin2 = new Rectangle(bodyWidth, bodyHeight / 2 - 0.5, pinLength, 1);
                pin2.setFill(Color.SILVER);
                capacitorPane.getChildren().addAll(pin1, pin2);
            }
        }

        // Value input field
        VBox controlBox = new VBox(5);
        controlBox.setLayoutY(bodyHeight + 5);
        
        Label typeLabel = new Label(capacitorType + " Capacitor");
        TextField valueField = new TextField(value);
        valueField.setPrefWidth(bodyWidth);
        
        valueField.textProperty().addListener((obs, oldVal, newVal) -> {
            this.value = newVal;
            this.capacitanceF = parseCapacitance(newVal);
        });
        
        controlBox.getChildren().addAll(typeLabel, valueField);
        capacitorPane.getChildren().add(controlBox);

        capacitorPane.setPrefSize(bodyWidth + (pins >= 2 ? bodyWidth / 2 : 0), bodyHeight + 60);
        return capacitorPane;
    }
    
    private Color getCapacitorColor() {
        switch (capacitorType) {
            case "Electrolytic": return Color.DARKBLUE;
            case "Ceramic": return Color.LIGHTYELLOW;
            case "Film": return Color.LIGHTBLUE;
            default: return Color.LIGHTGRAY;
        }
    }
    
    private double parseCapacitance(String valueStr) {
        try {
            String cleanValue = valueStr.toLowerCase().replaceAll("[^0-9.,upnm]", "");
            double value = Double.parseDouble(cleanValue.replaceAll("[upnm]", ""));
            
            if (valueStr.toLowerCase().contains("p")) value *= 1e-12; // pico
            else if (valueStr.toLowerCase().contains("n")) value *= 1e-9; // nano
            else if (valueStr.toLowerCase().contains("u")) value *= 1e-6; // micro
            else if (valueStr.toLowerCase().contains("m")) value *= 1e-3; // milli
            
            return value;
        } catch (NumberFormatException e) {
            return 1e-6; // Default to 1uF
        }
    }

    // Getters and setters
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
    public String getCapacitorType() { return capacitorType; }
    public void setCapacitorType(String capacitorType) { this.capacitorType = capacitorType; }
}
