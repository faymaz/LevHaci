package com.perfboarddesigner.model;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;

public class LED extends Component {
    private String ledColor; // "Red", "Green", "Blue", "Yellow", "White", "RGB"
    private String packageType; // "5mm", "3mm", "SMD", "High-Power"
    private double forwardVoltage; // Forward voltage
    private int brightness; // Brightness level (0-100)
    private static final double MM_TO_PIXEL = 6.0;

    public LED(String name, double x, double y, String type, int pins, String ledColor, String packageType, double forwardVoltage) {
        super(name, x, y, type, pins);
        this.ledColor = ledColor;
        this.packageType = packageType;
        this.forwardVoltage = forwardVoltage;
        this.brightness = 50; // Default brightness
    }

    @Override
    public Pane render() {
        Pane ledPane = new Pane();
        
        switch (packageType) {
            case "5mm":
                render5mmLED(ledPane);
                break;
            case "3mm":
                render3mmLED(ledPane);
                break;
            case "SMD":
                renderSMDLED(ledPane);
                break;
            case "High-Power":
                renderHighPowerLED(ledPane);
                break;
            default:
                render5mmLED(ledPane);
        }

        // Control panel
        VBox controlBox = new VBox(5);
        controlBox.setLayoutY(getBodyHeight() + 10);
        
        Label typeLabel = new Label(ledColor + " LED (" + packageType + ")");
        
        ComboBox<String> colorCombo = new ComboBox<>(FXCollections.observableArrayList(
            "Red", "Green", "Blue", "Yellow", "White", "RGB"));
        colorCombo.setValue(ledColor);
        colorCombo.setOnAction(e -> {
            this.ledColor = colorCombo.getValue();
            refreshLED(ledPane, controlBox);
        });
        
        ComboBox<String> packageCombo = new ComboBox<>(FXCollections.observableArrayList(
            "5mm", "3mm", "SMD", "High-Power"));
        packageCombo.setValue(packageType);
        packageCombo.setOnAction(e -> {
            this.packageType = packageCombo.getValue();
            refreshLED(ledPane, controlBox);
        });
        
        Label voltageLabel = new Label("Vf: " + forwardVoltage + "V");
        Label brightnessLabel = new Label("Brightness: " + brightness + "%");
        
        controlBox.getChildren().addAll(typeLabel, new Label("Color:"), colorCombo, 
                                      new Label("Package:"), packageCombo, voltageLabel, brightnessLabel);
        ledPane.getChildren().add(controlBox);

        ledPane.setPrefSize(Math.max(getBodyWidth(), 120), getBodyHeight() + 120);
        return ledPane;
    }
    
    private void render5mmLED(Pane pane) {
        double diameter = 4 * MM_TO_PIXEL; // Slightly smaller for better fit
        double centerX = diameter / 2;
        double centerY = diameter / 2;
        
        // LED dome (transparent/colored)
        Circle dome = new Circle(centerX, centerY, diameter / 2);
        dome.setFill(getLEDColor().deriveColor(0, 1, 1, 0.3)); // Semi-transparent
        dome.setStroke(Color.GRAY);
        dome.setStrokeWidth(1);
        pane.getChildren().add(dome);
        
        // LED core (bright center when on)
        Circle core = new Circle(centerX, centerY, diameter / 6);
        core.setFill(brightness > 0 ? getLEDColor() : Color.DARKGRAY);
        pane.getChildren().add(core);
        
        // Flat edge indicator (cathode side)
        Line flatEdge = new Line(centerX + diameter/2 - 2, centerY - diameter/3, 
                                centerX + diameter/2 - 2, centerY + diameter/3);
        flatEdge.setStroke(Color.DARKGRAY);
        flatEdge.setStrokeWidth(2);
        pane.getChildren().add(flatEdge);
        
        // Pins (anode longer, cathode shorter)
        if (this.pins >= 2) {
            // Anode (longer pin - left)
            Rectangle anodePin = new Rectangle(centerX - 8, diameter, 1, 12);
            anodePin.setFill(Color.SILVER);
            
            // Cathode (shorter pin - right)  
            Rectangle cathodePin = new Rectangle(centerX + 8, diameter, 1, 8);
            cathodePin.setFill(Color.SILVER);
            
            pane.getChildren().addAll(anodePin, cathodePin);
            
            // Pin labels
            Label anodeLabel = new Label("+");
            anodeLabel.setLayoutX(centerX - 10);
            anodeLabel.setLayoutY(diameter + 14);
            anodeLabel.setStyle("-fx-font-size: 8px; -fx-text-fill: red;");
            
            Label cathodeLabel = new Label("-");
            cathodeLabel.setLayoutX(centerX + 6);
            cathodeLabel.setLayoutY(diameter + 14);
            cathodeLabel.setStyle("-fx-font-size: 8px; -fx-text-fill: black;");
            
            pane.getChildren().addAll(anodeLabel, cathodeLabel);
        }
    }
    
    private void render3mmLED(Pane pane) {
        double diameter = 2.5 * MM_TO_PIXEL; // Smaller
        double centerX = diameter / 2;
        double centerY = diameter / 2;
        
        // Similar to 5mm but smaller
        Circle dome = new Circle(centerX, centerY, diameter / 2);
        dome.setFill(getLEDColor().deriveColor(0, 1, 1, 0.3));
        dome.setStroke(Color.GRAY);
        dome.setStrokeWidth(1);
        pane.getChildren().add(dome);
        
        Circle core = new Circle(centerX, centerY, diameter / 8);
        core.setFill(brightness > 0 ? getLEDColor() : Color.DARKGRAY);
        pane.getChildren().add(core);
        
        // Pins
        if (this.pins >= 2) {
            Rectangle anodePin = new Rectangle(centerX - 6, diameter, 0.5, 10);
            Rectangle cathodePin = new Rectangle(centerX + 6, diameter, 0.5, 6);
            anodePin.setFill(Color.SILVER);
            cathodePin.setFill(Color.SILVER);
            pane.getChildren().addAll(anodePin, cathodePin);
        }
    }
    
    private void renderSMDLED(Pane pane) {
        double width = 2 * MM_TO_PIXEL;
        double height = 1.25 * MM_TO_PIXEL;
        
        // SMD body
        Rectangle body = new Rectangle(0, 0, width, height);
        body.setFill(Color.WHITE);
        body.setStroke(Color.BLACK);
        body.setStrokeWidth(0.5);
        pane.getChildren().add(body);
        
        // LED chip (small rectangle in center)
        Rectangle chip = new Rectangle(width * 0.3, height * 0.3, width * 0.4, height * 0.4);
        chip.setFill(brightness > 0 ? getLEDColor() : Color.DARKGRAY);
        pane.getChildren().add(chip);
        
        // Cathode marking (small triangle)
        Polygon cathodeMarking = new Polygon();
        cathodeMarking.getPoints().addAll(new Double[]{
            0.0, 0.0,
            width * 0.2, 0.0,
            0.0, height * 0.2
        });
        cathodeMarking.setFill(Color.LIGHTGRAY);
        pane.getChildren().add(cathodeMarking);
        
        // SMD pads
        Rectangle pad1 = new Rectangle(-1, height * 0.2, 1, height * 0.6);
        Rectangle pad2 = new Rectangle(width, height * 0.2, 1, height * 0.6);
        pad1.setFill(Color.SILVER);
        pad2.setFill(Color.SILVER);
        pane.getChildren().addAll(pad1, pad2);
    }
    
    private void renderHighPowerLED(Pane pane) {
        double size = 8 * MM_TO_PIXEL; // Smaller high-power LED
        
        // Heat sink base
        Rectangle heatSink = new Rectangle(0, 0, size, size);
        heatSink.setFill(Color.DARKGRAY);
        heatSink.setStroke(Color.BLACK);
        heatSink.setStrokeWidth(1);
        pane.getChildren().add(heatSink);
        
        // LED die (center)
        Rectangle die = new Rectangle(size * 0.25, size * 0.25, size * 0.5, size * 0.5);
        die.setFill(brightness > 0 ? getLEDColor() : Color.GRAY);
        die.setStroke(Color.BLACK);
        pane.getChildren().add(die);
        
        // Lens (optional)
        Circle lens = new Circle(size / 2, size / 2, size * 0.3);
        lens.setFill(Color.TRANSPARENT);
        lens.setStroke(Color.LIGHTGRAY);
        lens.setStrokeWidth(1);
        pane.getChildren().add(lens);
        
        // Multiple pins for high power
        if (this.pins >= 2) {
            for (int i = 0; i < 4; i++) {
                Rectangle pin = new Rectangle(size * 0.1 + i * size * 0.25, size, 2, 6);
                pin.setFill(Color.SILVER);
                pane.getChildren().add(pin);
            }
        }
    }
    
    private Color getLEDColor() {
        switch (ledColor.toLowerCase()) {
            case "red": return Color.RED;
            case "green": return Color.LIME;
            case "blue": return Color.BLUE;
            case "yellow": return Color.YELLOW;
            case "white": return Color.WHITE;
            case "rgb": return Color.MAGENTA; // Multi-color representation
            default: return Color.RED;
        }
    }
    
    private double getBodyWidth() {
        switch (packageType) {
            case "3mm": return 2.5 * MM_TO_PIXEL;
            case "SMD": return 2 * MM_TO_PIXEL;
            case "High-Power": return 8 * MM_TO_PIXEL;
            default: return 4 * MM_TO_PIXEL; // 5mm (updated)
        }
    }
    
    private double getBodyHeight() {
        switch (packageType) {
            case "3mm": return 2.5 * MM_TO_PIXEL;
            case "SMD": return 1.25 * MM_TO_PIXEL;
            case "High-Power": return 8 * MM_TO_PIXEL;
            default: return 4 * MM_TO_PIXEL; // 5mm (updated)
        }
    }
    
    private void refreshLED(Pane ledPane, VBox controlBox) {
        ledPane.getChildren().clear();
        switch (packageType) {
            case "5mm": render5mmLED(ledPane); break;
            case "3mm": render3mmLED(ledPane); break;
            case "SMD": renderSMDLED(ledPane); break;
            case "High-Power": renderHighPowerLED(ledPane); break;
        }
        ledPane.getChildren().add(controlBox);
    }

    // Getters and setters
    public String getLedColor() { return ledColor; }
    public void setLedColor(String ledColor) { this.ledColor = ledColor; }
    public String getPackageType() { return packageType; }
    public void setPackageType(String packageType) { this.packageType = packageType; }
    public double getForwardVoltage() { return forwardVoltage; }
    public void setForwardVoltage(double forwardVoltage) { this.forwardVoltage = forwardVoltage; }
    public int getBrightness() { return brightness; }
    public void setBrightness(int brightness) { this.brightness = Math.max(0, Math.min(100, brightness)); }
}
