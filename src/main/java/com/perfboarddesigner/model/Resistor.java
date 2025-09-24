package com.perfboarddesigner.model;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.TextField;

public class Resistor {
    private String value; // e.g., "1k Ohm", "470 Ohm"
    private double lengthMm;
    private double widthMm;
    private String type; // e.g., "ThroughHole"
    private int pinCount;
    private double resistanceOhms; // Numeric resistance in ohms
    private static final double MM_TO_PIXEL = 6.0; // 1mm = 6 pixels

    public Resistor(String value, double lengthMm, double widthMm, String type, int pinCount, double resistanceOhms) {
        this.value = value;
        this.lengthMm = lengthMm;
        this.widthMm = widthMm;
        this.type = type;
        this.pinCount = pinCount;
        this.resistanceOhms = resistanceOhms;
    }

    public Pane render() {
        Pane resistorPane = new Pane();
        double pixelLength = lengthMm * MM_TO_PIXEL;
        double pixelWidth = widthMm * MM_TO_PIXEL;
        double bandWidth = pixelLength / 12; // Smaller bands (1/12 of length)
        double gapWidth = pixelLength / 24; // Gap between bands for distinction

        // Draw resistor body (rectangle)
        Rectangle body = new Rectangle(0, 0, pixelLength, pixelWidth);
        body.setFill(Color.LIGHTGRAY);
        body.setStroke(Color.BLACK);
        resistorPane.getChildren().add(body);

        // Calculate and render color bands
        renderBands(resistorPane, pixelLength, pixelWidth, bandWidth, gapWidth);

        // Draw pins (simple lines for ThroughHole)
        if ("ThroughHole".equals(type) && pinCount >= 2) {
            double pinLength = pixelLength / 4;
            Rectangle pin1 = new Rectangle(-pinLength, pixelWidth / 2 - 0.5, pinLength, 1);
            pin1.setFill(Color.SILVER);
            Rectangle pin2 = new Rectangle(pixelLength, pixelWidth / 2 - 0.5, pinLength, 1);
            pin2.setFill(Color.SILVER);
            resistorPane.getChildren().addAll(pin1, pin2);
        }

        // Add value field below the resistor
        TextField valueField = new TextField(value.replace(" Ohm", ""));
        valueField.setPrefWidth(pixelLength);
        valueField.textProperty().addListener((obs, oldVal, newVal) -> {
            this.value = newVal + " Ohm";
            this.resistanceOhms = parseResistance(newVal);
            resistorPane.getChildren().clear();
            resistorPane.getChildren().add(body);
            renderBands(resistorPane, pixelLength, pixelWidth, bandWidth, gapWidth);
            if ("ThroughHole".equals(type) && pinCount >= 2) {
                double pinLength = pixelLength / 4;
                Rectangle pin1 = new Rectangle(-pinLength, pixelWidth / 2 - 0.5, pinLength, 1);
                pin1.setFill(Color.SILVER);
                Rectangle pin2 = new Rectangle(pixelLength, pixelWidth / 2 - 0.5, pinLength, 1);
                pin2.setFill(Color.SILVER);
                resistorPane.getChildren().addAll(pin1, pin2);
            }
            resistorPane.getChildren().add(valueField); // Re-add the text field
        });
        resistorPane.getChildren().add(valueField);

        resistorPane.setPrefSize(pixelLength + (pinCount >= 2 ? pixelLength / 2 : 0), pixelWidth + 25); // Extra space for text field
        return resistorPane;
    }

    private void renderBands(Pane pane, double pixelLength, double pixelWidth, double bandWidth, double gapWidth) {
        int[] bands = getColorBands(resistanceOhms);
        double currentX = 0;
        for (int i = 0; i < 4; i++) {
            Rectangle band = new Rectangle(currentX, 0, bandWidth, pixelWidth);
            band.setFill(getColorForBand(bands[i]));
            pane.getChildren().add(band);
            currentX += bandWidth + (i < 3 ? gapWidth : 0);
        }
    }

    private int[] getColorBands(double resistanceOhms) {
        int[] bands = new int[4];
        double value = resistanceOhms;

        // Handle multiplier for kΩ and MΩ
        if (value >= 1000000) {
            value /= 1000000; // Convert to MΩ
            bands[2] = 6; // Blue for ×1000000
        } else if (value >= 1000) {
            value /= 1000; // Convert to kΩ
            bands[2] = 3; // Orange for ×1000
        } else {
            bands[2] = 0; // Black for ×1
        }

        // First two digits (handle fractional values by rounding)
        int firstDigit = (int) (value / 10);
        int secondDigit = (int) (value % 10);
        bands[0] = firstDigit;
        bands[1] = secondDigit;

        // Tolerance (gold for ±5%)
        bands[3] = 10; // Gold

        return bands;
    }

    private Color getColorForBand(int bandValue) {
        switch (bandValue) {
            case 0: return Color.BLACK;
            case 1: return Color.BROWN;
            case 2: return Color.RED;
            case 3: return Color.ORANGE;
            case 4: return Color.YELLOW;
            case 5: return Color.GREEN;
            case 6: return Color.BLUE;
            case 7: return Color.VIOLET;
            case 8: return Color.GRAY;
            case 9: return Color.WHITE;
            case 10: return Color.GOLD; // Tolerance
            default: return Color.GRAY;
        }
    }

    private double parseResistance(String valueStr) {
        try {
            double value = Double.parseDouble(valueStr.replace(" Ohm", "").replace(",", ".").replace(" ", ""));
            if (valueStr.toLowerCase().contains("k")) value *= 1000;
            else if (valueStr.toLowerCase().contains("m")) value *= 1000000;
            return value;
        } catch (NumberFormatException e) {
            return 1000; // Default to 1k if invalid
        }
    }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
}