package com.perfboarddesigner.model;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;

public class Transistor extends Component {
    private String transistorType; // "NPN", "PNP", "NMOS", "PMOS"
    private String packageType; // "TO-92", "TO-220", "SOT-23"
    private String model; // e.g., "2N2222", "BC547"
    private static final double MM_TO_PIXEL = 6.0;

    public Transistor(String name, double x, double y, String type, int pins, String transistorType, String packageType, String model) {
        super(name, x, y, type, pins);
        this.transistorType = transistorType;
        this.packageType = packageType;
        this.model = model;
    }

    @Override
    public Pane render() {
        Pane transistorPane = new Pane();
        
        if ("TO-92".equals(packageType)) {
            renderTO92(transistorPane);
        } else if ("TO-220".equals(packageType)) {
            renderTO220(transistorPane);
        } else {
            renderSOT23(transistorPane);
        }

        // Control panel
        VBox controlBox = new VBox(5);
        controlBox.setLayoutY(getBodyHeight() + 5);
        
        Label typeLabel = new Label(transistorType + " Transistor");
        
        ComboBox<String> typeCombo = new ComboBox<>(FXCollections.observableArrayList("NPN", "PNP", "NMOS", "PMOS"));
        typeCombo.setValue(transistorType);
        typeCombo.setOnAction(e -> this.transistorType = typeCombo.getValue());
        
        TextField modelField = new TextField(model);
        modelField.setPrefWidth(80);
        modelField.textProperty().addListener((obs, oldVal, newVal) -> this.model = newVal);
        
        controlBox.getChildren().addAll(typeLabel, typeCombo, new Label("Model:"), modelField);
        transistorPane.getChildren().add(controlBox);

        transistorPane.setPrefSize(getBodyWidth() + 20, getBodyHeight() + 100);
        return transistorPane;
    }
    
    private void renderTO92(Pane pane) {
        // Half-circle body (flat side down)
        double radius = 8 * MM_TO_PIXEL / 2;
        Circle body = new Circle(radius, radius, radius);
        body.setFill(Color.BLACK);
        body.setStroke(Color.DARKGRAY);
        body.setStrokeWidth(1);
        pane.getChildren().add(body);
        
        // Flat side indicator
        Line flatSide = new Line(0, radius * 2, radius * 2, radius * 2);
        flatSide.setStroke(Color.DARKGRAY);
        flatSide.setStrokeWidth(2);
        pane.getChildren().add(flatSide);
        
        // Three pins (Collector, Base, Emitter)
        double pinSpacing = radius * 2 / 3;
        for (int i = 0; i < 3; i++) {
            double pinX = pinSpacing * (i + 0.5);
            Rectangle pin = new Rectangle(pinX - 0.5, radius * 2, 1, 10);
            pin.setFill(Color.SILVER);
            pane.getChildren().add(pin);
        }
        
        // Pin labels
        Label collectorLabel = new Label("C");
        collectorLabel.setLayoutX(pinSpacing * 0.5 - 3);
        collectorLabel.setLayoutY(radius * 2 + 12);
        collectorLabel.setTextFill(Color.BLACK);
        collectorLabel.setStyle("-fx-font-size: 8px;");
        
        Label baseLabel = new Label("B");
        baseLabel.setLayoutX(pinSpacing * 1.5 - 3);
        baseLabel.setLayoutY(radius * 2 + 12);
        baseLabel.setTextFill(Color.BLACK);
        baseLabel.setStyle("-fx-font-size: 8px;");
        
        Label emitterLabel = new Label("E");
        emitterLabel.setLayoutX(pinSpacing * 2.5 - 3);
        emitterLabel.setLayoutY(radius * 2 + 12);
        emitterLabel.setTextFill(Color.BLACK);
        emitterLabel.setStyle("-fx-font-size: 8px;");
        
        pane.getChildren().addAll(collectorLabel, baseLabel, emitterLabel);
    }
    
    private void renderTO220(Pane pane) {
        double width = 10 * MM_TO_PIXEL;
        double height = 15 * MM_TO_PIXEL;
        
        // Main body
        Rectangle body = new Rectangle(0, 0, width, height);
        body.setFill(Color.BLACK);
        body.setStroke(Color.DARKGRAY);
        body.setStrokeWidth(1);
        pane.getChildren().add(body);
        
        // Heat sink tab
        Rectangle tab = new Rectangle(width * 0.2, 0, width * 0.6, height * 0.3);
        tab.setFill(Color.SILVER);
        tab.setStroke(Color.DARKGRAY);
        pane.getChildren().add(tab);
        
        // Three pins
        double pinSpacing = width / 4;
        for (int i = 0; i < 3; i++) {
            double pinX = pinSpacing * (i + 1) - 1;
            Rectangle pin = new Rectangle(pinX, height, 2, 8);
            pin.setFill(Color.SILVER);
            pane.getChildren().add(pin);
        }
    }
    
    private void renderSOT23(Pane pane) {
        double width = 3 * MM_TO_PIXEL;
        double height = 3 * MM_TO_PIXEL;
        
        // Main body (small SMD package)
        Rectangle body = new Rectangle(0, 0, width, height);
        body.setFill(Color.DARKGREEN);
        body.setStroke(Color.BLACK);
        body.setStrokeWidth(1);
        pane.getChildren().add(body);
        
        // SMD pads (simplified)
        double padSize = width / 6;
        for (int i = 0; i < 3; i++) {
            Rectangle pad = new Rectangle(i < 2 ? i * width * 0.7 : width * 0.35, 
                                        i < 2 ? height : -padSize/2, 
                                        padSize, padSize);
            pad.setFill(Color.SILVER);
            pane.getChildren().add(pad);
        }
    }
    
    private double getBodyWidth() {
        switch (packageType) {
            case "TO-220": return 10 * MM_TO_PIXEL;
            case "SOT-23": return 3 * MM_TO_PIXEL;
            default: return 8 * MM_TO_PIXEL; // TO-92
        }
    }
    
    private double getBodyHeight() {
        switch (packageType) {
            case "TO-220": return 15 * MM_TO_PIXEL;
            case "SOT-23": return 3 * MM_TO_PIXEL;
            default: return 8 * MM_TO_PIXEL; // TO-92
        }
    }

    // Getters and setters
    public String getTransistorType() { return transistorType; }
    public void setTransistorType(String transistorType) { this.transistorType = transistorType; }
    public String getPackageType() { return packageType; }
    public void setPackageType(String packageType) { this.packageType = packageType; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
}
