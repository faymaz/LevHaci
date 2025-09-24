package com.perfboarddesigner.model;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.TextField;
import javafx.scene.control.Slider;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class Potentiometer extends Component {
    private String value; // e.g., "10k", "100k"
    private double resistanceOhms;
    private double wiperPosition; // 0.0 to 1.0 (percentage)
    private String potType; // "Linear", "Logarithmic"
    private static final double MM_TO_PIXEL = 6.0;

    public Potentiometer(String name, double x, double y, String type, int pins, String value, String potType) {
        super(name, x, y, type, pins);
        this.value = value;
        this.potType = potType;
        this.resistanceOhms = parseResistance(value);
        this.wiperPosition = 0.5; // Start at middle position
    }

    @Override
    public Pane render() {
        Pane potPane = new Pane();
        double diameter = 15 * MM_TO_PIXEL;
        
        // Main body (circular)
        Circle body = new Circle(diameter/2, diameter/2, diameter/2);
        body.setFill(Color.DARKGREEN);
        body.setStroke(Color.BLACK);
        body.setStrokeWidth(2);
        potPane.getChildren().add(body);
        
        // Shaft (center circle)
        Circle shaft = new Circle(diameter/2, diameter/2, diameter/8);
        shaft.setFill(Color.DARKGRAY);
        shaft.setStroke(Color.BLACK);
        shaft.setStrokeWidth(1);
        potPane.getChildren().add(shaft);
        
        // Wiper position indicator
        double angle = wiperPosition * 270 - 135; // -135° to +135° (270° total)
        double radians = Math.toRadians(angle);
        double indicatorLength = diameter * 0.3;
        double endX = diameter/2 + indicatorLength * Math.cos(radians);
        double endY = diameter/2 + indicatorLength * Math.sin(radians);
        
        Line indicator = new Line(diameter/2, diameter/2, endX, endY);
        indicator.setStroke(Color.WHITE);
        indicator.setStrokeWidth(2);
        potPane.getChildren().add(indicator);
        
        // Three pins (CCW, Wiper, CW)
        double pin1X = diameter/2 - diameter/3;
        double pin1Y = diameter + 5;
        double pin2X = diameter/2;
        double pin2Y = diameter + 5;
        double pin3X = diameter/2 + diameter/3;
        double pin3Y = diameter + 5;
        
        Rectangle pin1 = new Rectangle(pin1X - 0.5, pin1Y, 1, 8);
        Rectangle pin2 = new Rectangle(pin2X - 0.5, pin2Y, 1, 8);
        Rectangle pin3 = new Rectangle(pin3X - 0.5, pin3Y, 1, 8);
        
        pin1.setFill(Color.SILVER);
        pin2.setFill(Color.SILVER);
        pin3.setFill(Color.SILVER);
        
        potPane.getChildren().addAll(pin1, pin2, pin3);
        
        // Pin labels
        Label pin1Label = new Label("1");
        pin1Label.setLayoutX(pin1X - 3);
        pin1Label.setLayoutY(pin1Y + 10);
        pin1Label.setStyle("-fx-font-size: 8px;");
        
        Label pin2Label = new Label("2");
        pin2Label.setLayoutX(pin2X - 3);
        pin2Label.setLayoutY(pin2Y + 10);
        pin2Label.setStyle("-fx-font-size: 8px;");
        
        Label pin3Label = new Label("3");
        pin3Label.setLayoutX(pin3X - 3);
        pin3Label.setLayoutY(pin3Y + 10);
        pin3Label.setStyle("-fx-font-size: 8px;");
        
        potPane.getChildren().addAll(pin1Label, pin2Label, pin3Label);

        // Control panel
        VBox controlBox = new VBox(5);
        controlBox.setLayoutY(diameter + 25);
        
        Label typeLabel = new Label(potType + " Potentiometer");
        
        TextField valueField = new TextField(value);
        valueField.setPrefWidth(80);
        valueField.textProperty().addListener((obs, oldVal, newVal) -> {
            this.value = newVal;
            this.resistanceOhms = parseResistance(newVal);
        });
        
        Label positionLabel = new Label("Wiper Position: " + String.format("%.0f%%", wiperPosition * 100));
        
        Slider positionSlider = new Slider(0, 1, wiperPosition);
        positionSlider.setPrefWidth(120);
        positionSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            this.wiperPosition = newVal.doubleValue();
            positionLabel.setText("Wiper Position: " + String.format("%.0f%%", wiperPosition * 100));
            
            // Update the visual indicator
            updateWiperIndicator(potPane, diameter);
        });
        
        Label resistanceLabel = new Label("Current Resistance: " + getCurrentResistanceString());
        
        // Update resistance display when wiper moves
        positionSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            resistanceLabel.setText("Current Resistance: " + getCurrentResistanceString());
        });
        
        controlBox.getChildren().addAll(typeLabel, new Label("Value:"), valueField, 
                                      positionLabel, positionSlider, resistanceLabel);
        potPane.getChildren().add(controlBox);

        potPane.setPrefSize(Math.max(diameter + 20, 140), diameter + 150);
        return potPane;
    }
    
    private void updateWiperIndicator(Pane potPane, double diameter) {
        // Remove old indicator
        potPane.getChildren().removeIf(node -> node instanceof Line && 
            ((Line) node).getStroke() == Color.WHITE);
        
        // Add new indicator
        double angle = wiperPosition * 270 - 135;
        double radians = Math.toRadians(angle);
        double indicatorLength = diameter * 0.3;
        double endX = diameter/2 + indicatorLength * Math.cos(radians);
        double endY = diameter/2 + indicatorLength * Math.sin(radians);
        
        Line indicator = new Line(diameter/2, diameter/2, endX, endY);
        indicator.setStroke(Color.WHITE);
        indicator.setStrokeWidth(2);
        
        // Insert indicator at correct position (after body, before controls)
        potPane.getChildren().add(3, indicator);
    }
    
    private String getCurrentResistanceString() {
        double currentResistance = resistanceOhms * wiperPosition;
        
        if (currentResistance >= 1000000) {
            return String.format("%.1fMΩ", currentResistance / 1000000);
        } else if (currentResistance >= 1000) {
            return String.format("%.1fkΩ", currentResistance / 1000);
        } else {
            return String.format("%.0fΩ", currentResistance);
        }
    }
    
    private double parseResistance(String valueStr) {
        try {
            String cleanValue = valueStr.toLowerCase().replace("ω", "").replace("ohm", "").trim();
            double value = Double.parseDouble(cleanValue.replaceAll("[km]", ""));
            
            if (valueStr.toLowerCase().contains("k")) value *= 1000;
            else if (valueStr.toLowerCase().contains("m")) value *= 1000000;
            
            return value;
        } catch (NumberFormatException e) {
            return 10000; // Default to 10k
        }
    }

    // Getters and setters
    public String getValue() { return value; }
    public void setValue(String value) { 
        this.value = value;
        this.resistanceOhms = parseResistance(value);
    }
    public double getWiperPosition() { return wiperPosition; }
    public void setWiperPosition(double wiperPosition) { 
        this.wiperPosition = Math.max(0.0, Math.min(1.0, wiperPosition));
    }
    public String getPotType() { return potType; }
    public void setPotType(String potType) { this.potType = potType; }
    public double getCurrentResistance() { return resistanceOhms * wiperPosition; }
}
