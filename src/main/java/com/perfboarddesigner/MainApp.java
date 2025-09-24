package com.perfboarddesigner;

import com.perfboarddesigner.model.*;
import javafx.application.Application;
import javafx.collections.FXCollections; // Added import
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class MainApp extends Application {
    private Board board;
    private TextField scaleField;
    private TextField x1Field, y1Field, x2Field, y2Field;
    private Button extendButton;
    private double startX, startY;
    private VBox componentPanel; // Declare as class field

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Levhacı (PerfBoard Designer)");

        ComboBox<BoardType> typeCombo = new ComboBox<>(FXCollections.observableArrayList(BoardType.values()));
        typeCombo.setCellFactory(lv -> new ListCell<BoardType>() {
            @Override protected void updateItem(BoardType item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.getDisplayName());
            }
        });
        typeCombo.setValue(BoardType.PERFORATED);

        ComboBox<BoardSide> sideCombo = new ComboBox<>(FXCollections.observableArrayList(BoardSide.values()));
        sideCombo.setCellFactory(lv -> new ListCell<BoardSide>() {
            @Override protected void updateItem(BoardSide item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.getDisplayName());
            }
        });
        sideCombo.setValue(BoardSide.SINGLE);

        TextField widthField = new TextField("100"); // Default 100mm
        TextField heightField = new TextField("100"); // Default 100mm
        TextField spacingField = new TextField("2.88"); // Default 2.88mm spacing
        scaleField = new TextField("1.0"); // Default scale factor
        x1Field = new TextField("5"); // Start x (default 5mm)
        y1Field = new TextField("5"); // Start y (default 5mm)
        x2Field = new TextField("10"); // End x (default 10mm)
        y2Field = new TextField("10"); // End y (default 10mm)
        widthField.setPrefWidth(50);
        heightField.setPrefWidth(50);
        spacingField.setPrefWidth(50);
        scaleField.setPrefWidth(50);
        x1Field.setPrefWidth(50);
        y1Field.setPrefWidth(50);
        x2Field.setPrefWidth(50);
        y2Field.setPrefWidth(50);
        Label sizeLabel = new Label("Size (mm):");
        Label spacingLabel = new Label("Spacing (mm):");
        Label scaleLabel = new Label("Scale:");
        Label jumperLabel = new Label("Jumper (mm):");

        Button addJumperButton = new Button("Add Wire");
        extendButton = new Button("Extend Last Wire");
        Button clearWiresButton = new Button("Clear Wires");
        addJumperButton.setOnAction(e -> {
            board.startJumper(Double.parseDouble(x1Field.getText()), Double.parseDouble(y1Field.getText()));
            board.finishJumper(Double.parseDouble(x2Field.getText()), Double.parseDouble(y2Field.getText()));
        });
        extendButton.setOnAction(e -> {
            if (!board.getJumpers().isEmpty()) {
                board.updateTempJumper(Double.parseDouble(x2Field.getText()), Double.parseDouble(y2Field.getText()));
                board.finishJumper(Double.parseDouble(x2Field.getText()), Double.parseDouble(y2Field.getText()));
            }
        });
        clearWiresButton.setOnAction(e -> board.clearJumpers());

        HBox selectionPanel = new HBox(10, new Label("Board Type:"), typeCombo, new Label("Side:"), sideCombo, 
                sizeLabel, new Label("W:"), widthField, new Label("H:"), heightField,
                spacingLabel, spacingField, scaleLabel, scaleField,
                jumperLabel, new Label("X1:"), x1Field, new Label("Y1:"), y1Field,
                new Label("X2:"), x2Field, new Label("Y2:"), y2Field,
                addJumperButton, extendButton, clearWiresButton);
        selectionPanel.setPadding(new Insets(10));

        componentPanel = new VBox(10, new Label("Components:")); // Initialize here
        updateResistorDisplay(); // Initial display

        board = new Board(typeCombo.getValue(), sideCombo.getValue(), Double.parseDouble(widthField.getText()), 
                Double.parseDouble(heightField.getText()), Double.parseDouble(spacingField.getText()));
        ScrollPane boardScroll = new ScrollPane(board.getBoardPane());
        boardScroll.setFitToWidth(true);
        boardScroll.setFitToHeight(true);

        // Listeners for updates
        typeCombo.valueProperty().addListener((obs, old, newVal) -> {
            board.setType(newVal);
            updateBoardDimensions(widthField, heightField, spacingField, scaleField);
        });
        sideCombo.valueProperty().addListener((obs, old, newVal) -> {
            board.setSide(newVal);
            updateBoardDimensions(widthField, heightField, spacingField, scaleField);
        });
        widthField.textProperty().addListener((obs, old, newVal) -> updateBoardDimensions(widthField, heightField, spacingField, scaleField));
        heightField.textProperty().addListener((obs, old, newVal) -> updateBoardDimensions(widthField, heightField, spacingField, scaleField));
        spacingField.textProperty().addListener((obs, old, newVal) -> updateBoardDimensions(widthField, heightField, spacingField, scaleField));
        scaleField.textProperty().addListener((obs, old, newVal) -> updateBoardDimensions(widthField, heightField, spacingField, scaleField));

        // Mouse event handling for interactive wire placement
        board.getBoardPane().setOnMousePressed(e -> {
            startX = e.getX() / Constants.MM_TO_PIXEL;
            startY = e.getY() / Constants.MM_TO_PIXEL;
            board.startJumper(startX, startY);
        });

        board.getBoardPane().setOnMouseDragged(e -> {
            board.updateTempJumper(e.getX() / Constants.MM_TO_PIXEL, e.getY() / Constants.MM_TO_PIXEL);
        });

        board.getBoardPane().setOnMouseReleased(e -> {
            board.finishJumper(e.getX() / Constants.MM_TO_PIXEL, e.getY() / Constants.MM_TO_PIXEL);
        });

        HBox root = new HBox(10);
        root.getChildren().addAll(componentPanel, boardScroll);

        VBox mainLayout = new VBox(selectionPanel, root);
        mainLayout.setPadding(new Insets(10));

        Scene scene = new Scene(mainLayout, 900, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void updateBoardDimensions(TextField widthField, TextField heightField, TextField spacingField, TextField scaleField) {
        try {
            double width = Double.parseDouble(widthField.getText());
            double height = Double.parseDouble(heightField.getText());
            double spacing = Double.parseDouble(spacingField.getText());
            double scale = Double.parseDouble(scaleField.getText());
            if (width > 0 && height > 0 && spacing > 0 && scale > 0) {
                board.setDimensions(width, height, spacing);
                board.setScaleFactor(scale);
            }
        } catch (NumberFormatException ex) {
            // Ignore invalid input
        }
    }

    private void updateResistorDisplay() {
        componentPanel.getChildren().clear();
        componentPanel.getChildren().add(new Label("Components:"));
        TextField valueField = new TextField("1k");
        valueField.setPrefWidth(100);
        Resistor resistor = new Resistor("1k Ohm", 20, 5, "ThroughHole", 2, parseResistance("1k"));
        componentPanel.getChildren().add(resistor.render());
        componentPanel.getChildren().add(new Label("Enter Value (e.g., 1, 10k, 100M):"));
        componentPanel.getChildren().add(valueField);

        valueField.textProperty().addListener((obs, oldVal, newVal) -> {
            double resistanceOhms = parseResistance(newVal);
            Resistor newResistor = new Resistor(newVal + " Ohm", 20, 5, "ThroughHole", 2, resistanceOhms);
            componentPanel.getChildren().set(1, newResistor.render()); // Replace the resistor
        });
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

    public static void main(String[] args) {
        launch(args);
    }
}