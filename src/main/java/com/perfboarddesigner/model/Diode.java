package com.perfboarddesigner.model;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;

public class Diode extends Component {
    private String diodeType; // "Standard", "Zener", "Schottky", "Bridge"
    private String model; // e.g., "1N4007", "1N5819"
    private double forwardVoltage; // Forward voltage drop
    private static final double MM_TO_PIXEL = 6.0;

    public Diode(String name, double x, double y, String type, int pins, String diodeType, String model, double forwardVoltage) {
        super(name, x, y, type, pins);
        this.diodeType = diodeType;
        this.model = model;
        this.forwardVoltage = forwardVoltage;
    }

    @Override
    public Pane render() {
        Pane diodePane = new Pane();
        double bodyLength = 6 * MM_TO_PIXEL; // 6mm length (smaller)
        double bodyWidth = 2.5 * MM_TO_PIXEL; // 2.5mm width (smaller)
        
        if ("ThroughHole".equals(this.type)) {
            renderThroughHoleDiode(diodePane, bodyLength, bodyWidth);
        } else {
            renderSMDDiode(diodePane, bodyLength, bodyWidth);
        }

        // Control panel
        VBox controlBox = new VBox(5);
        controlBox.setLayoutY(bodyWidth + 10);
        
        Label typeLabel = new Label(diodeType + " Diode");
        
        ComboBox<String> typeCombo = new ComboBox<>(FXCollections.observableArrayList(
            "Standard", "Zener", "Schottky", "Bridge"));
        typeCombo.setValue(diodeType);
        typeCombo.setOnAction(e -> {
            this.diodeType = typeCombo.getValue();
            refreshDiode(diodePane, controlBox, bodyLength, bodyWidth);
        });
        
        Label modelLabel = new Label("Model: " + model);
        Label voltageLabel = new Label("Vf: " + forwardVoltage + "V");
        
        controlBox.getChildren().addAll(typeLabel, typeCombo, modelLabel, voltageLabel);
        diodePane.getChildren().add(controlBox);

        diodePane.setPrefSize(Math.max(bodyLength + 20, 100), bodyWidth + 80);
        return diodePane;
    }
    
    private void renderThroughHoleDiode(Pane pane, double bodyLength, double bodyWidth) {
        double centerY = bodyWidth / 2;
        
        // Draw diode body (cylinder/rectangle)
        Rectangle body = new Rectangle(0, 0, bodyLength, bodyWidth);
        body.setFill(getBodyColor());
        body.setStroke(Color.BLACK);
        body.setStrokeWidth(1);
        pane.getChildren().add(body);
        
        // Draw diode symbol inside
        double symbolStart = bodyLength * 0.3;
        double symbolEnd = bodyLength * 0.7;
        double symbolHeight = bodyWidth * 0.6;
        double symbolTop = (bodyWidth - symbolHeight) / 2;
        
        // Anode triangle (left side)
        Polygon triangle = new Polygon();
        triangle.getPoints().addAll(new Double[]{
            symbolStart, symbolTop + symbolHeight/2, // tip
            symbolStart + symbolHeight/2, symbolTop, // top
            symbolStart + symbolHeight/2, symbolTop + symbolHeight // bottom
        });
        triangle.setFill(Color.WHITE);
        triangle.setStroke(Color.BLACK);
        triangle.setStrokeWidth(0.5);
        pane.getChildren().add(triangle);
        
        // Cathode line (right side)
        Line cathodeLine = new Line(symbolEnd, symbolTop, symbolEnd, symbolTop + symbolHeight);
        cathodeLine.setStroke(Color.BLACK);
        cathodeLine.setStrokeWidth(2);
        pane.getChildren().add(cathodeLine);
        
        // Special markings for different diode types
        if ("Zener".equals(diodeType)) {
            // Add small Z-shaped marking
            Line zLine1 = new Line(symbolEnd - 2, symbolTop - 2, symbolEnd + 2, symbolTop - 2);
            Line zLine2 = new Line(symbolEnd + 2, symbolTop - 2, symbolEnd - 2, symbolTop + 2);
            zLine1.setStroke(Color.BLACK);
            zLine2.setStroke(Color.BLACK);
            pane.getChildren().addAll(zLine1, zLine2);
        } else if ("Schottky".equals(diodeType)) {
            // Add S-shaped marking
            Line sLine1 = new Line(symbolEnd - 2, symbolTop + symbolHeight + 2, symbolEnd - 1, symbolTop + symbolHeight + 2);
            Line sLine2 = new Line(symbolEnd + 1, symbolTop + symbolHeight + 2, symbolEnd + 2, symbolTop + symbolHeight + 2);
            sLine1.setStroke(Color.BLACK);
            sLine2.setStroke(Color.BLACK);
            pane.getChildren().addAll(sLine1, sLine2);
        }
        
        // Cathode band (white stripe)
        Rectangle cathodeStripe = new Rectangle(bodyLength * 0.85, 0, bodyLength * 0.1, bodyWidth);
        cathodeStripe.setFill(Color.WHITE);
        cathodeStripe.setStroke(Color.DARKGRAY);
        pane.getChildren().add(cathodeStripe);
        
        // Pins
        if (this.pins >= 2) {
            double pinLength = bodyLength / 4;
            Rectangle pin1 = new Rectangle(-pinLength, centerY - 0.5, pinLength, 1);
            pin1.setFill(Color.SILVER);
            Rectangle pin2 = new Rectangle(bodyLength, centerY - 0.5, pinLength, 1);
            pin2.setFill(Color.SILVER);
            pane.getChildren().addAll(pin1, pin2);
        }
    }
    
    private void renderSMDDiode(Pane pane, double bodyLength, double bodyWidth) {
        bodyLength = 4 * MM_TO_PIXEL; // Smaller for SMD
        bodyWidth = 2 * MM_TO_PIXEL;
        
        // SMD body
        Rectangle body = new Rectangle(0, 0, bodyLength, bodyWidth);
        body.setFill(Color.DARKGRAY);
        body.setStroke(Color.BLACK);
        body.setStrokeWidth(1);
        pane.getChildren().add(body);
        
        // Cathode marking
        Rectangle cathodeStripe = new Rectangle(bodyLength * 0.8, 0, bodyLength * 0.2, bodyWidth);
        cathodeStripe.setFill(Color.WHITE);
        pane.getChildren().add(cathodeStripe);
        
        // SMD pads
        Rectangle pad1 = new Rectangle(-2, bodyWidth * 0.2, 2, bodyWidth * 0.6);
        Rectangle pad2 = new Rectangle(bodyLength, bodyWidth * 0.2, 2, bodyWidth * 0.6);
        pad1.setFill(Color.SILVER);
        pad2.setFill(Color.SILVER);
        pane.getChildren().addAll(pad1, pad2);
    }
    
    private Color getBodyColor() {
        switch (diodeType) {
            case "Zener": return Color.LIGHTBLUE;
            case "Schottky": return Color.LIGHTGRAY;
            case "Bridge": return Color.DARKBLUE;
            default: return Color.BLACK; // Standard diode
        }
    }
    
    private void refreshDiode(Pane diodePane, VBox controlBox, double bodyLength, double bodyWidth) {
        diodePane.getChildren().clear();
        if ("ThroughHole".equals(this.type)) {
            renderThroughHoleDiode(diodePane, bodyLength, bodyWidth);
        } else {
            renderSMDDiode(diodePane, bodyLength, bodyWidth);
        }
        diodePane.getChildren().add(controlBox);
    }

    // Getters and setters
    public String getDiodeType() { return diodeType; }
    public void setDiodeType(String diodeType) { this.diodeType = diodeType; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public double getForwardVoltage() { return forwardVoltage; }
    public void setForwardVoltage(double forwardVoltage) { this.forwardVoltage = forwardVoltage; }
}
