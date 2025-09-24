package com.perfboarddesigner.model;

import javafx.scene.layout.Pane;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;

public class SVGComponent extends Component {
    private String svgFileName;
    private String displayValue;
    private double widthPx;
    private double heightPx;
    private double scaleFactor = 1.0;
    private ComponentOrientation orientation = ComponentOrientation.HORIZONTAL;
    private int gridUnits = 1; // Kaç delik birimi kaplar

    public SVGComponent(String name, double x, double y, String type, int pins, String svgFileName, String displayValue, double widthPx, double heightPx) {
        super(name, x, y, type, pins);
        this.svgFileName = svgFileName;
        this.displayValue = displayValue;
        this.widthPx = widthPx;
        this.heightPx = heightPx;
        
        // Varsayılan grid unit ayarları (resimdeki gerçek ölçülere göre)
        if ("resistor.svg".equals(svgFileName)) {
            this.gridUnits = 2; // Resistor - 2 delik arası (yatay yerleştirme)
        } else if ("diode.svg".equals(svgFileName)) {
            this.gridUnits = 4; // Diode - 4 delik arası  
        } else if ("led.svg".equals(svgFileName)) {
            this.gridUnits = 2; // LED - 2 delik arası
        } else if ("capacitor.svg".equals(svgFileName)) {
            this.gridUnits = 3; // Capacitor - 3 delik arası
        } else if ("potentiometer.svg".equals(svgFileName)) {
            this.gridUnits = 3; // Potentiometer - 3 delik yan yana
        }
    }

    @Override
    public Pane render() {
        Pane componentPane = new Pane();
        
        // Apply scale factor to dimensions
        double scaledWidth = widthPx * scaleFactor;
        double scaledHeight = heightPx * scaleFactor;
        
        // Apply orientation changes
        if (orientation == ComponentOrientation.VERTICAL) {
            double temp = scaledWidth;
            scaledWidth = scaledHeight;
            scaledHeight = temp;
        }
        
        // Create component-specific visual representation
        switch (svgFileName) {
            case "resistor.svg":
                renderResistor(componentPane, scaledWidth, scaledHeight);
                break;
            case "diode.svg":
                renderDiode(componentPane, scaledWidth, scaledHeight);
                break;
            case "led.svg":
                renderLED(componentPane, scaledWidth, scaledHeight);
                break;
            case "capacitor.svg":
                renderCapacitor(componentPane, scaledWidth, scaledHeight);
                break;
            case "transistor.svg":
                renderTransistor(componentPane, scaledWidth, scaledHeight);
                break;
            case "pushbutton.svg":
                renderPushButton(componentPane, scaledWidth, scaledHeight);
                break;
            case "toggle.svg":
                renderToggleSwitch(componentPane, scaledWidth, scaledHeight);
                break;
            case "potentiometer.svg":
                renderPotentiometer(componentPane, scaledWidth, scaledHeight);
                break;
            default:
                renderGeneric(componentPane, scaledWidth, scaledHeight);
        }
        
        // Add value label
        if (displayValue != null && !displayValue.isEmpty()) {
            Label valueLabel = new Label(displayValue);
            valueLabel.setLayoutX(2);
            valueLabel.setLayoutY(scaledHeight + 2);
            valueLabel.setStyle("-fx-font-size: " + (7 * scaleFactor) + "px; -fx-text-fill: black; -fx-font-weight: bold;");
            componentPane.getChildren().add(valueLabel);
        }
        
        // Apply rotation to the entire component
        if (orientation != ComponentOrientation.HORIZONTAL) {
            componentPane.setRotate(orientation.getAngleDegrees());
        }
        
        componentPane.setPrefSize(scaledWidth, scaledHeight + 12 * scaleFactor);
        return componentPane;
    }
    
    private void renderResistor(Pane pane, double w, double h) {
        // Grid-based sizing (2.54mm perfboard spacing)
        double gridSpacing = 2.54 * scaleFactor;
        double actualWidth = gridUnits * gridSpacing * 6; // 6px per mm roughly
        
        // Body
        Rectangle body = new Rectangle(8 * scaleFactor, 4 * scaleFactor, actualWidth - 16 * scaleFactor, h - 8 * scaleFactor);
        body.setFill(Color.LIGHTYELLOW);
        body.setStroke(Color.BLACK);
        body.setStrokeWidth(0.5 * scaleFactor);
        pane.getChildren().add(body);
        
        // Color bands (proportional to body)
        double bandWidth = 2 * scaleFactor;
        double bodyStart = 8 * scaleFactor;
        double bodyLength = actualWidth - 16 * scaleFactor;
        
        Rectangle band1 = new Rectangle(bodyStart + bodyLength * 0.15, 4 * scaleFactor, bandWidth, h - 8 * scaleFactor);
        band1.setFill(Color.BROWN);
        Rectangle band2 = new Rectangle(bodyStart + bodyLength * 0.25, 4 * scaleFactor, bandWidth, h - 8 * scaleFactor);
        band2.setFill(Color.BLACK);
        Rectangle band3 = new Rectangle(bodyStart + bodyLength * 0.35, 4 * scaleFactor, bandWidth, h - 8 * scaleFactor);
        band3.setFill(Color.RED);
        Rectangle band4 = new Rectangle(bodyStart + bodyLength * 0.8, 4 * scaleFactor, bandWidth, h - 8 * scaleFactor);
        band4.setFill(Color.GOLD);
        
        pane.getChildren().addAll(band1, band2, band3, band4);
        
        // Leads (extend to grid points)
        Rectangle lead1 = new Rectangle(0, h/2 - 0.5 * scaleFactor, 8 * scaleFactor, 1 * scaleFactor);
        Rectangle lead2 = new Rectangle(actualWidth - 8 * scaleFactor, h/2 - 0.5 * scaleFactor, 8 * scaleFactor, 1 * scaleFactor);
        lead1.setFill(Color.SILVER);
        lead2.setFill(Color.SILVER);
        pane.getChildren().addAll(lead1, lead2);
    }
    
    private void renderDiode(Pane pane, double w, double h) {
        // Body
        Rectangle body = new Rectangle(6 * scaleFactor, 3 * scaleFactor, w - 12 * scaleFactor, h - 6 * scaleFactor);
        body.setFill(Color.BLACK);
        body.setStroke(Color.DARKGRAY);
        body.setStrokeWidth(0.5 * scaleFactor);
        pane.getChildren().add(body);
        
        // Cathode band
        Rectangle cathode = new Rectangle(w - 10 * scaleFactor, 3 * scaleFactor, 3 * scaleFactor, h - 6 * scaleFactor);
        cathode.setFill(Color.WHITE);
        pane.getChildren().add(cathode);
        
        // Leads
        Rectangle lead1 = new Rectangle(0, h/2 - 0.5 * scaleFactor, 6 * scaleFactor, 1 * scaleFactor);
        Rectangle lead2 = new Rectangle(w - 6 * scaleFactor, h/2 - 0.5 * scaleFactor, 6 * scaleFactor, 1 * scaleFactor);
        lead1.setFill(Color.SILVER);
        lead2.setFill(Color.SILVER);
        pane.getChildren().addAll(lead1, lead2);
    }
    
    private void renderLED(Pane pane, double w, double h) {
        // LED dome (simplified as rectangle with rounded appearance)
        Rectangle dome = new Rectangle(5 * scaleFactor, 2 * scaleFactor, w - 10 * scaleFactor, h - 8 * scaleFactor);
        dome.setFill(Color.RED.deriveColor(0, 1, 1, 0.4));
        dome.setStroke(Color.DARKRED);
        dome.setStrokeWidth(0.5 * scaleFactor);
        dome.setArcWidth(4 * scaleFactor);
        dome.setArcHeight(4 * scaleFactor);
        pane.getChildren().add(dome);
        
        // Core
        Rectangle core = new Rectangle(w/2 - 1 * scaleFactor, h/2 - 1 * scaleFactor, 2 * scaleFactor, 2 * scaleFactor);
        core.setFill(Color.RED);
        pane.getChildren().add(core);
        
        // Leads (different lengths)
        Rectangle anode = new Rectangle(w/2 - 3 * scaleFactor, h - 6 * scaleFactor, 1 * scaleFactor, 8 * scaleFactor);
        Rectangle cathode = new Rectangle(w/2 + 2 * scaleFactor, h - 6 * scaleFactor, 1 * scaleFactor, 6 * scaleFactor);
        anode.setFill(Color.SILVER);
        cathode.setFill(Color.SILVER);
        pane.getChildren().addAll(anode, cathode);
    }
    
    private void renderCapacitor(Pane pane, double w, double h) {
        // Body (cylindrical for electrolytic)
        Rectangle body = new Rectangle(4 * scaleFactor, 2 * scaleFactor, w - 8 * scaleFactor, h - 4 * scaleFactor);
        body.setFill(Color.DARKBLUE);
        body.setStroke(Color.BLACK);
        body.setStrokeWidth(0.5 * scaleFactor);
        body.setArcWidth(6 * scaleFactor);
        body.setArcHeight(6 * scaleFactor);
        pane.getChildren().add(body);
        
        // Positive marking
        Rectangle plusH = new Rectangle(8 * scaleFactor, 4 * scaleFactor, 4 * scaleFactor, 1 * scaleFactor);
        Rectangle plusV = new Rectangle(9.5 * scaleFactor, 2.5 * scaleFactor, 1 * scaleFactor, 4 * scaleFactor);
        plusH.setFill(Color.WHITE);
        plusV.setFill(Color.WHITE);
        pane.getChildren().addAll(plusH, plusV);
        
        // Leads
        Rectangle lead1 = new Rectangle(0, h/2 - 0.5 * scaleFactor, 4 * scaleFactor, 1 * scaleFactor);
        Rectangle lead2 = new Rectangle(w - 4 * scaleFactor, h/2 - 0.5 * scaleFactor, 4 * scaleFactor, 1 * scaleFactor);
        lead1.setFill(Color.SILVER);
        lead2.setFill(Color.SILVER);
        pane.getChildren().addAll(lead1, lead2);
    }
    
    private void renderTransistor(Pane pane, double w, double h) {
        // TO-92 package (half circle)
        Rectangle body = new Rectangle(2 * scaleFactor, 2 * scaleFactor, w - 4 * scaleFactor, h - 8 * scaleFactor);
        body.setFill(Color.BLACK);
        body.setStroke(Color.DARKGRAY);
        body.setStrokeWidth(0.5 * scaleFactor);
        body.setArcWidth(8 * scaleFactor);
        body.setArcHeight(8 * scaleFactor);
        pane.getChildren().add(body);
        
        // Flat side
        Rectangle flat = new Rectangle(2 * scaleFactor, h - 6 * scaleFactor, w - 4 * scaleFactor, 1 * scaleFactor);
        flat.setFill(Color.DARKGRAY);
        pane.getChildren().add(flat);
        
        // Three leads
        Rectangle lead1 = new Rectangle(4 * scaleFactor, h - 5 * scaleFactor, 0.5 * scaleFactor, 6 * scaleFactor);
        Rectangle lead2 = new Rectangle(w/2 - 0.25 * scaleFactor, h - 5 * scaleFactor, 0.5 * scaleFactor, 6 * scaleFactor);
        Rectangle lead3 = new Rectangle(w - 5 * scaleFactor, h - 5 * scaleFactor, 0.5 * scaleFactor, 6 * scaleFactor);
        lead1.setFill(Color.SILVER);
        lead2.setFill(Color.SILVER);
        lead3.setFill(Color.SILVER);
        pane.getChildren().addAll(lead1, lead2, lead3);
    }
    
    private void renderPushButton(Pane pane, double w, double h) {
        // Button body (square)
        Rectangle body = new Rectangle(2 * scaleFactor, 2 * scaleFactor, w - 4 * scaleFactor, h - 8 * scaleFactor);
        body.setFill(Color.LIGHTGRAY);
        body.setStroke(Color.BLACK);
        body.setStrokeWidth(1 * scaleFactor);
        pane.getChildren().add(body);
        
        // Button actuator (smaller square)
        Rectangle actuator = new Rectangle(w/2 - 3 * scaleFactor, h/2 - 3 * scaleFactor, 6 * scaleFactor, 6 * scaleFactor);
        actuator.setFill(Color.WHITE);
        actuator.setStroke(Color.DARKGRAY);
        actuator.setStrokeWidth(0.5 * scaleFactor);
        pane.getChildren().add(actuator);
        
        // Four pins
        Rectangle pin1 = new Rectangle(3 * scaleFactor, h - 4 * scaleFactor, 1 * scaleFactor, 4 * scaleFactor);
        Rectangle pin2 = new Rectangle(w - 4 * scaleFactor, h - 4 * scaleFactor, 1 * scaleFactor, 4 * scaleFactor);
        Rectangle pin3 = new Rectangle(3 * scaleFactor, -2 * scaleFactor, 1 * scaleFactor, 4 * scaleFactor);
        Rectangle pin4 = new Rectangle(w - 4 * scaleFactor, -2 * scaleFactor, 1 * scaleFactor, 4 * scaleFactor);
        pin1.setFill(Color.SILVER);
        pin2.setFill(Color.SILVER);
        pin3.setFill(Color.SILVER);
        pin4.setFill(Color.SILVER);
        pane.getChildren().addAll(pin1, pin2, pin3, pin4);
    }
    
    private void renderToggleSwitch(Pane pane, double w, double h) {
        // Switch body
        Rectangle body = new Rectangle(2 * scaleFactor, 4 * scaleFactor, w - 4 * scaleFactor, h - 8 * scaleFactor);
        body.setFill(Color.DARKGRAY);
        body.setStroke(Color.BLACK);
        body.setStrokeWidth(1 * scaleFactor);
        pane.getChildren().add(body);
        
        // Toggle lever (top part)
        Rectangle lever = new Rectangle(w/2 - 1 * scaleFactor, 0, 2 * scaleFactor, 6 * scaleFactor);
        lever.setFill(Color.WHITE);
        lever.setStroke(Color.BLACK);
        lever.setStrokeWidth(0.5 * scaleFactor);
        pane.getChildren().add(lever);
        
        // Three pins
        Rectangle pin1 = new Rectangle(4 * scaleFactor, h - 4 * scaleFactor, 1 * scaleFactor, 4 * scaleFactor);
        Rectangle pin2 = new Rectangle(w/2 - 0.5 * scaleFactor, h - 4 * scaleFactor, 1 * scaleFactor, 4 * scaleFactor);
        Rectangle pin3 = new Rectangle(w - 5 * scaleFactor, h - 4 * scaleFactor, 1 * scaleFactor, 4 * scaleFactor);
        pin1.setFill(Color.SILVER);
        pin2.setFill(Color.SILVER);
        pin3.setFill(Color.SILVER);
        pane.getChildren().addAll(pin1, pin2, pin3);
    }
    
    private void renderPotentiometer(Pane pane, double w, double h) {
        // Main body (circular-ish)
        Rectangle body = new Rectangle(2 * scaleFactor, 2 * scaleFactor, w - 4 * scaleFactor, h - 8 * scaleFactor);
        body.setFill(Color.DARKGREEN);
        body.setStroke(Color.BLACK);
        body.setStrokeWidth(1 * scaleFactor);
        body.setArcWidth(10 * scaleFactor);
        body.setArcHeight(10 * scaleFactor);
        pane.getChildren().add(body);
        
        // Shaft (center)
        Rectangle shaft = new Rectangle(w/2 - 2 * scaleFactor, h/2 - 2 * scaleFactor, 4 * scaleFactor, 4 * scaleFactor);
        shaft.setFill(Color.DARKGRAY);
        shaft.setStroke(Color.BLACK);
        shaft.setStrokeWidth(0.5 * scaleFactor);
        pane.getChildren().add(shaft);
        
        // Three pins
        Rectangle pin1 = new Rectangle(4 * scaleFactor, h - 4 * scaleFactor, 1 * scaleFactor, 4 * scaleFactor);
        Rectangle pin2 = new Rectangle(w/2 - 0.5 * scaleFactor, h - 4 * scaleFactor, 1 * scaleFactor, 4 * scaleFactor);
        Rectangle pin3 = new Rectangle(w - 5 * scaleFactor, h - 4 * scaleFactor, 1 * scaleFactor, 4 * scaleFactor);
        pin1.setFill(Color.SILVER);
        pin2.setFill(Color.SILVER);
        pin3.setFill(Color.SILVER);
        pane.getChildren().addAll(pin1, pin2, pin3);
    }
    
    private void renderGeneric(Pane pane, double w, double h) {
        Rectangle body = new Rectangle(0, 0, w, h);
        body.setFill(Color.LIGHTGRAY);
        body.setStroke(Color.BLACK);
        body.setStrokeWidth(1 * scaleFactor);
        pane.getChildren().add(body);
        
        Label typeLabel = new Label(getShortName());
        typeLabel.setLayoutX(2 * scaleFactor);
        typeLabel.setLayoutY(2 * scaleFactor);
        typeLabel.setStyle("-fx-font-size: " + (8 * scaleFactor) + "px; -fx-text-fill: black;");
        pane.getChildren().add(typeLabel);
    }
    
    private Color getComponentColor() {
        switch (svgFileName) {
            case "resistor.svg": return Color.LIGHTYELLOW;
            case "diode.svg": return Color.DARKGRAY;
            case "led.svg": return Color.RED.deriveColor(0, 1, 1, 0.3);
            case "capacitor.svg": return Color.BLUE;
            case "transistor.svg": return Color.BLACK;
            default: return Color.LIGHTGRAY;
        }
    }
    
    private String getShortName() {
        switch (svgFileName) {
            case "resistor.svg": return "R";
            case "diode.svg": return "D";
            case "led.svg": return "LED";
            case "capacitor.svg": return "C";
            case "transistor.svg": return "Q";
            case "pushbutton.svg": return "SW";
            case "toggle.svg": return "SW";
            case "potentiometer.svg": return "VR";
            default: return "?";
        }
    }

    // Getters and setters
    public String getSvgFileName() { return svgFileName; }
    public void setSvgFileName(String svgFileName) { this.svgFileName = svgFileName; }
    public String getDisplayValue() { return displayValue; }
    public void setDisplayValue(String displayValue) { this.displayValue = displayValue; }
    public double getWidthPx() { return widthPx; }
    public double getHeightPx() { return heightPx; }
    public double getScaleFactor() { return scaleFactor; }
    public void setScaleFactor(double scaleFactor) { this.scaleFactor = scaleFactor; }
    public ComponentOrientation getOrientation() { return orientation; }
    public void setOrientation(ComponentOrientation orientation) { this.orientation = orientation; }
    public int getGridUnits() { return gridUnits; }
    public void setGridUnits(int gridUnits) { this.gridUnits = gridUnits; }
}
