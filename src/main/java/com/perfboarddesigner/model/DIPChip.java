package com.perfboarddesigner.model;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Circle;
import javafx.scene.control.Label;

public class DIPChip extends Component {
    private ComponentOrientation orientation;
    private DIPPackage dipPackage;
    private static final double MM_TO_PIXEL = 6.0;
    private double scaleFactor = 1.0;

    public DIPChip(String name, double x, double y, String type, DIPPackage dipPackage, ComponentOrientation orientation) {
        super(name, x, y, type, dipPackage.getPinCount());
        this.dipPackage = dipPackage;
        this.orientation = orientation;
    }

    @Override
    public Pane render() {
        Pane chipPane = new Pane();
        
        // DIP package dimensions (dynamic based on pin count)
        double chipWidth = 22 * MM_TO_PIXEL * scaleFactor;  // Standard DIP width
        double chipHeight = (dipPackage.getPinsPerSide() * 2.54 + 2) * MM_TO_PIXEL * scaleFactor; // Dynamic height
        
        if (orientation == ComponentOrientation.VERTICAL) {
            // Swap dimensions for vertical
            double temp = chipWidth;
            chipWidth = chipHeight;
            chipHeight = temp;
        }
        
        // Main chip body
        Rectangle body = new Rectangle(0, 0, chipWidth, chipHeight);
        body.setFill(Color.DARKSLATEGRAY);
        body.setStroke(Color.BLACK);
        body.setStrokeWidth(1 * scaleFactor);
        body.setArcWidth(2 * scaleFactor);
        body.setArcHeight(2 * scaleFactor);
        chipPane.getChildren().add(body);
        
        // Pin 1 indicator (small circle)
        Circle pin1Indicator = new Circle(chipWidth * 0.15, chipHeight * 0.15, 2 * scaleFactor);
        pin1Indicator.setFill(Color.WHITE);
        chipPane.getChildren().add(pin1Indicator);
        
        // Notch indicator (small rectangle)
        Rectangle notch = new Rectangle(chipWidth * 0.4, 0, chipWidth * 0.2, 3 * scaleFactor);
        notch.setFill(Color.DARKGRAY);
        chipPane.getChildren().add(notch);
        
        // Draw pins
        renderPins(chipPane, chipWidth, chipHeight);
        
        // Label
        Label chipLabel = new Label(dipPackage.getDisplayName());
        chipLabel.setLayoutX(chipWidth/2 - (dipPackage.getDisplayName().length() * 2));
        chipLabel.setLayoutY(chipHeight/2 - 5);
        chipLabel.setStyle("-fx-font-size: " + (7 * scaleFactor) + "px; -fx-text-fill: white; -fx-font-weight: bold;");
        chipPane.getChildren().add(chipLabel);
        
        // Apply rotation
        if (orientation != ComponentOrientation.HORIZONTAL) {
            chipPane.setRotate(orientation.getAngleDegrees());
        }
        
        chipPane.setPrefSize(chipWidth, chipHeight);
        return chipPane;
    }
    
    private void renderPins(Pane pane, double chipWidth, double chipHeight) {
        double pinWidth = 1 * scaleFactor;
        double pinLength = 3 * scaleFactor;
        
        if (orientation == ComponentOrientation.HORIZONTAL || orientation == ComponentOrientation.DIAGONAL_UP || orientation == ComponentOrientation.DIAGONAL_DOWN) {
            // Horizontal layout - pins on left and right
            for (int i = 0; i < dipPackage.getPinsPerSide(); i++) {
                double y = (chipHeight / (dipPackage.getPinsPerSide() + 1)) * (i + 1);
                
                // Left side pins
                Rectangle leftPin = new Rectangle(-pinLength, y - pinWidth/2, pinLength, pinWidth);
                leftPin.setFill(Color.SILVER);
                pane.getChildren().add(leftPin);
                
                // Right side pins
                Rectangle rightPin = new Rectangle(chipWidth, y - pinWidth/2, pinLength, pinWidth);
                rightPin.setFill(Color.SILVER);
                pane.getChildren().add(rightPin);
            }
        } else {
            // Vertical layout - pins on top and bottom
            for (int i = 0; i < dipPackage.getPinsPerSide(); i++) {
                double x = (chipWidth / (dipPackage.getPinsPerSide() + 1)) * (i + 1);
                
                // Top pins
                Rectangle topPin = new Rectangle(x - pinWidth/2, -pinLength, pinWidth, pinLength);
                topPin.setFill(Color.SILVER);
                pane.getChildren().add(topPin);
                
                // Bottom pins
                Rectangle bottomPin = new Rectangle(x - pinWidth/2, chipHeight, pinWidth, pinLength);
                bottomPin.setFill(Color.SILVER);
                pane.getChildren().add(bottomPin);
            }
        }
    }

    // Getters and setters
    public ComponentOrientation getOrientation() { return orientation; }
    public void setOrientation(ComponentOrientation orientation) { this.orientation = orientation; }
    public double getScaleFactor() { return scaleFactor; }
    public void setScaleFactor(double scaleFactor) { this.scaleFactor = scaleFactor; }
    public DIPPackage getDipPackage() { return dipPackage; }
    public void setDipPackage(DIPPackage dipPackage) { this.dipPackage = dipPackage; }
}
