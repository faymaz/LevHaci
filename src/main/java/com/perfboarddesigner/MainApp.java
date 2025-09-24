package com.perfboarddesigner;

import com.perfboarddesigner.model.*;
import javafx.application.Application;
import javafx.collections.FXCollections; // Added import
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.application.HostServices;

public class MainApp extends Application {
    private Board board;
    private ComponentManager componentManager;
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

        // Board size selector
        ComboBox<BoardSize> sizeCombo = new ComboBox<>(FXCollections.observableArrayList(BoardSize.values()));
        sizeCombo.setCellFactory(lv -> new ListCell<BoardSize>() {
            @Override protected void updateItem(BoardSize item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.getDisplayName());
            }
        });
        sizeCombo.setButtonCell(new ListCell<BoardSize>() {
            @Override protected void updateItem(BoardSize item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.getDisplayName());
            }
        });
        sizeCombo.setValue(BoardSize.SIZE_100x100);

        TextField widthField = new TextField("100"); // Default 100mm
        TextField heightField = new TextField("100"); // Default 100mm
        TextField spacingField = new TextField("2.54"); // Default 2.54mm spacing (standard perfboard)
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
        
        // Component management buttons
        CheckBox gridSnapCheck = new CheckBox("Grid Snap");
        gridSnapCheck.setSelected(true);
        gridSnapCheck.setOnAction(e -> componentManager.setGridSnapEnabled(gridSnapCheck.isSelected()));
        
        Button deleteComponentBtn = new Button("Delete Selected");
        deleteComponentBtn.setOnAction(e -> componentManager.removeSelectedComponent());
        
        Button clearComponentsBtn = new Button("Clear All Components");
        clearComponentsBtn.setOnAction(e -> componentManager.clearAllComponents());
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

        // Split controls into multiple rows for better layout
        HBox boardControlsPanel = new HBox(10, new Label("Board Type:"), typeCombo, new Label("Side:"), sideCombo, 
                new Label("Size:"), sizeCombo,
                sizeLabel, new Label("W:"), widthField, new Label("H:"), heightField,
                spacingLabel, spacingField, scaleLabel, scaleField);
        
        HBox wireControlsPanel = new HBox(10,
                jumperLabel, new Label("X1:"), x1Field, new Label("Y1:"), y1Field,
                new Label("X2:"), x2Field, new Label("Y2:"), y2Field,
                addJumperButton, extendButton, clearWiresButton);
                
        HBox componentControlsPanel = new HBox(10,
                gridSnapCheck, deleteComponentBtn, clearComponentsBtn);
        
        VBox selectionPanel = new VBox(5, boardControlsPanel, wireControlsPanel, componentControlsPanel);
        selectionPanel.setPadding(new Insets(10));

        componentPanel = new VBox(10, new Label("Components:")); // Initialize here
        updateResistorDisplay(); // Initial display

        board = new Board(typeCombo.getValue(), sideCombo.getValue(), Double.parseDouble(widthField.getText()), 
                Double.parseDouble(heightField.getText()), Double.parseDouble(spacingField.getText()));
        componentManager = new ComponentManager(board);
        
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
        sizeCombo.valueProperty().addListener((obs, old, newVal) -> {
            if (newVal != null && !newVal.isCustom()) {
                widthField.setText(String.valueOf(newVal.getWidth()));
                heightField.setText(String.valueOf(newVal.getHeight()));
                updateBoardDimensions(widthField, heightField, spacingField, scaleField);
            }
            // Enable/disable custom fields
            widthField.setDisable(!newVal.isCustom());
            heightField.setDisable(!newVal.isCustom());
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

        // Create two-column layout
        // Left column: Components panel with scroll
        ScrollPane componentScroll = new ScrollPane(componentPanel);
        componentScroll.setFitToWidth(true);
        componentScroll.setPrefWidth(280);
        componentScroll.setMaxWidth(280);
        componentScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        componentScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        componentScroll.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-width: 1;");
        
        VBox leftColumn = new VBox(5);
        leftColumn.setPrefWidth(280);
        leftColumn.setMaxWidth(280);
        leftColumn.getChildren().add(componentScroll);
        
        // Right column: Controls and board
        VBox rightColumn = new VBox(10);
        rightColumn.getChildren().addAll(selectionPanel, boardScroll);
        
        HBox root = new HBox(15);
        root.getChildren().addAll(leftColumn, rightColumn);

        // Create menu bar
        MenuBar menuBar = createMenuBar(primaryStage);
        
        VBox mainLayout = new VBox(menuBar, root);
        mainLayout.setPadding(new Insets(10));

        Scene scene = new Scene(mainLayout, 1200, 750);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private MenuBar createMenuBar(Stage primaryStage) {
        MenuBar menuBar = new MenuBar();
        
        // Help menu
        Menu helpMenu = new Menu("Help");
        
        MenuItem aboutItem = new MenuItem("About");
        aboutItem.setOnAction(e -> showAboutDialog());
        
        MenuItem githubItem = new MenuItem("GitHub Page");
        githubItem.setOnAction(e -> {
            try {
                getHostServices().showDocument("https://github.com/faymaz/Levhaci");
            } catch (Exception ex) {
                showAlert("Error", "Could not open GitHub page: " + ex.getMessage());
            }
        });
        
        helpMenu.getItems().addAll(aboutItem, new SeparatorMenuItem(), githubItem);
        menuBar.getMenus().add(helpMenu);
        
        return menuBar;
    }
    
    private void showAboutDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Levhacı");
        alert.setHeaderText("Levhacı - PerfBoard Design Tool");
        
        String content = "Levhacı v1.0\n\n" +
                        "Levhacı is a specialized perfboard (protoboard) design tool\n" +
                        "developed for electronic circuit designers.\n\n" +
                        "'Levhacı' is a Turkish word meaning 'board maker'.\n" +
                        "With this program you can design circuits on perfboards\n" +
                        "using various electronic components.\n\n" +
                        "Features:\n" +
                        "• Various perfboard types (Perforated, Stripboard, Mixed)\n" +
                        "• Single and double-sided board options\n" +
                        "• Resistors, capacitors, LEDs, diodes, transistors support\n" +
                        "• DIP4 to DIP40 integrated circuit packages\n" +
                        "• Grid alignment system\n" +
                        "• Real-size component views\n\n" +
                        "Developer: github.com/faymaz\n" +
                        "Open source project: MIT License";
        
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
        
        Label headerLabel = new Label("Components Library");
        headerLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #2c3e50;");
        componentPanel.getChildren().add(headerLabel);
        
        // Create tabs or sections for different components with better organization
        VBox resistorSection = createResistorSection();
        VBox capacitorSection = createCapacitorSection();
        VBox transistorSection = createTransistorSection();
        VBox switchSection = createSwitchSection();
        VBox potentiometerSection = createPotentiometerSection();
        VBox diodeSection = createDiodeSection();
        VBox ledSection = createLEDSection();
        VBox chipSection = createChipSection();
        
        // Add separators and proper spacing
        componentPanel.setSpacing(10);
        componentPanel.getChildren().addAll(
            createSeparator(),
            createSectionHeader("Resistors"),
            resistorSection,
            createSeparator(),
            createSectionHeader("Capacitors"), 
            capacitorSection,
            createSeparator(),
            createSectionHeader("Diodes"),
            diodeSection,
            createSeparator(),
            createSectionHeader("LEDs"),
            ledSection,
            createSeparator(),
            createSectionHeader("Transistors"),
            transistorSection,
            createSeparator(),
            createSectionHeader("Switches"),
            switchSection,
            createSeparator(),
            createSectionHeader("Potentiometers"),
            potentiometerSection,
            createSeparator(),
            createSectionHeader("IC Chips"),
            chipSection
        );
    }
    
    private Label createSectionHeader(String text) {
        Label header = new Label(text);
        header.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: #34495e; -fx-padding: 3 0 2 0;");
        return header;
    }
    
    private Region createSeparator() {
        Region separator = new Region();
        separator.setPrefHeight(1);
        separator.setMaxHeight(1);
        separator.setStyle("-fx-background-color: #bdc3c7;");
        return separator;
    }
    
    private VBox createResistorSection() {
        VBox section = new VBox(5);
        TextField valueField = new TextField("1k");
        valueField.setPrefWidth(60);
        
        // Grid units selector (2-5 holes for resistors)
        ComboBox<Integer> gridUnitsCombo = new ComboBox<>(FXCollections.observableArrayList(2, 3, 4, 5));
        gridUnitsCombo.setValue(2); // Default 2 holes
        gridUnitsCombo.setPrefWidth(60);

        // Orientation selector (all directions)
        ComboBox<ComponentOrientation> orientationCombo = new ComboBox<>(FXCollections.observableArrayList(ComponentOrientation.values()));
        orientationCombo.setCellFactory(lv -> new ListCell<ComponentOrientation>() {
            @Override protected void updateItem(ComponentOrientation item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.getDisplayName());
            }
        });
        orientationCombo.setButtonCell(new ListCell<ComponentOrientation>() {
            @Override protected void updateItem(ComponentOrientation item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.getDisplayName());
            }
        });
        orientationCombo.setValue(ComponentOrientation.HORIZONTAL);
        orientationCombo.setPrefWidth(100);
        
        Button addResistorBtn = new Button("Add to Board");
        
        // Use compact SVG-style component (smaller for preview)
        SVGComponent resistor = new SVGComponent("Resistor", 0, 0, "ThroughHole", 2, "resistor.svg", "1kΩ", 45, 12);
        resistor.setGridUnits(2);
        resistor.setScaleFactor(0.7); // Smaller preview
        
        section.getChildren().addAll(
            resistor.render(), 
            new Label("Value:"), valueField,
            new Label("Grid Units (2-5):"), gridUnitsCombo,
            new Label("Orientation:"), orientationCombo,
            addResistorBtn
        );

        addResistorBtn.setOnAction(e -> {
            SVGComponent newResistor = new SVGComponent("Resistor", 0, 0, "ThroughHole", 2, "resistor.svg", valueField.getText() + "Ω", 45, 12);
            newResistor.setGridUnits(gridUnitsCombo.getValue());
            newResistor.setOrientation(orientationCombo.getValue());
            componentManager.addComponent(newResistor, 50, 50);
        });

        // Update preview when settings change
        Runnable updatePreview = () -> {
            SVGComponent newResistor = new SVGComponent("Resistor", 0, 0, "ThroughHole", 2, "resistor.svg", valueField.getText() + "Ω", 45, 12);
            newResistor.setGridUnits(gridUnitsCombo.getValue());
            newResistor.setOrientation(orientationCombo.getValue());
            newResistor.setScaleFactor(0.7); // Keep preview small
            section.getChildren().set(0, newResistor.render());
        };
        
        valueField.textProperty().addListener((obs, oldVal, newVal) -> updatePreview.run());
        gridUnitsCombo.valueProperty().addListener((obs, oldVal, newVal) -> updatePreview.run());
        orientationCombo.valueProperty().addListener((obs, oldVal, newVal) -> updatePreview.run());
        
        return section;
    }
    
    private VBox createCapacitorSection() {
        VBox section = new VBox(5);
        SVGComponent capacitor = new SVGComponent("Capacitor", 0, 0, "ThroughHole", 2, "capacitor.svg", "100µF", 35, 16);
        capacitor.setScaleFactor(0.7); // Smaller preview
        Button addCapBtn = new Button("Add to Board");
        addCapBtn.setOnAction(e -> {
            SVGComponent newCap = new SVGComponent("Capacitor", 0, 0, "ThroughHole", 2, "capacitor.svg", "100µF", 35, 16);
            componentManager.addComponent(newCap, 80, 50);
        });
        section.getChildren().addAll(capacitor.render(), addCapBtn);
        return section;
    }
    
    private VBox createTransistorSection() {
        VBox section = new VBox(5);
        SVGComponent transistor = new SVGComponent("Transistor", 0, 0, "ThroughHole", 3, "transistor.svg", "2N2222", 22, 20);
        transistor.setScaleFactor(0.7); // Smaller preview
        Button addTransBtn = new Button("Add to Board");
        addTransBtn.setOnAction(e -> {
            SVGComponent newTrans = new SVGComponent("Transistor", 0, 0, "ThroughHole", 3, "transistor.svg", "2N2222", 22, 20);
            componentManager.addComponent(newTrans, 180, 50);
        });
        section.getChildren().addAll(transistor.render(), addTransBtn);
        return section;
    }
    
    private VBox createSwitchSection() {
        VBox section = new VBox(5);
        
        // Push Button
        SVGComponent pushButton = new SVGComponent("Push Button", 0, 0, "ThroughHole", 4, "pushbutton.svg", "PUSH", 20, 20);
        pushButton.setScaleFactor(0.7); // Smaller preview
        Button addPushBtn = new Button("Add Push Button");
        addPushBtn.setOnAction(e -> {
            SVGComponent newPush = new SVGComponent("Push Button", 0, 0, "ThroughHole", 4, "pushbutton.svg", "PUSH", 20, 20);
            componentManager.addComponent(newPush, 210, 50);
        });
        
        // Toggle Switch
        SVGComponent toggleSwitch = new SVGComponent("Toggle Switch", 0, 0, "ThroughHole", 3, "toggle.svg", "ON/OFF", 18, 25);
        toggleSwitch.setScaleFactor(0.7); // Smaller preview
        Button addToggleBtn = new Button("Add Toggle");
        addToggleBtn.setOnAction(e -> {
            SVGComponent newToggle = new SVGComponent("Toggle Switch", 0, 0, "ThroughHole", 3, "toggle.svg", "ON/OFF", 18, 25);
            componentManager.addComponent(newToggle, 240, 50);
        });
        
        section.getChildren().addAll(
            new Label("Push Button:"), pushButton.render(), addPushBtn,
            new Label("Toggle Switch:"), toggleSwitch.render(), addToggleBtn
        );
        return section;
    }
    
    private VBox createPotentiometerSection() {
        VBox section = new VBox(5);
        SVGComponent pot = new SVGComponent("Potentiometer", 0, 0, "ThroughHole", 3, "potentiometer.svg", "10kΩ", 25, 25);
        pot.setScaleFactor(0.7); // Smaller preview
        Button addPotBtn = new Button("Add to Board");
        addPotBtn.setOnAction(e -> {
            SVGComponent newPot = new SVGComponent("Potentiometer", 0, 0, "ThroughHole", 3, "potentiometer.svg", "10kΩ", 25, 25);
            componentManager.addComponent(newPot, 270, 50);
        });
        section.getChildren().addAll(pot.render(), addPotBtn);
        return section;
    }
    
    private VBox createDiodeSection() {
        VBox section = new VBox(5);
        SVGComponent diode = new SVGComponent("Diode", 0, 0, "ThroughHole", 2, "diode.svg", "1N4007", 40, 14);
        diode.setScaleFactor(0.7); // Smaller preview
        Button addDiodeBtn = new Button("Add to Board");
        addDiodeBtn.setOnAction(e -> {
            SVGComponent newDiode = new SVGComponent("Diode", 0, 0, "ThroughHole", 2, "diode.svg", "1N4007", 40, 14);
            componentManager.addComponent(newDiode, 120, 50);
        });
        section.getChildren().addAll(diode.render(), addDiodeBtn);
        return section;
    }
    
    private VBox createLEDSection() {
        VBox section = new VBox(5);
        SVGComponent led = new SVGComponent("LED", 0, 0, "ThroughHole", 2, "led.svg", "Red", 25, 20);
        led.setScaleFactor(0.7); // Smaller preview
        Button addLEDBtn = new Button("Add to Board");
        addLEDBtn.setOnAction(e -> {
            SVGComponent newLED = new SVGComponent("LED", 0, 0, "ThroughHole", 2, "led.svg", "Red", 25, 20);
            componentManager.addComponent(newLED, 150, 50);
        });
        section.getChildren().addAll(led.render(), addLEDBtn);
        return section;
    }
    
    private VBox createChipSection() {
        VBox section = new VBox(5);
        
        // DIP package selector
        ComboBox<DIPPackage> dipPackageCombo = new ComboBox<>(FXCollections.observableArrayList(DIPPackage.values()));
        dipPackageCombo.setCellFactory(lv -> new ListCell<DIPPackage>() {
            @Override protected void updateItem(DIPPackage item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.getFullDisplayName());
            }
        });
        dipPackageCombo.setButtonCell(new ListCell<DIPPackage>() {
            @Override protected void updateItem(DIPPackage item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.getDisplayName());
            }
        });
        dipPackageCombo.setValue(DIPPackage.DIP8); // 555 timer as default
        dipPackageCombo.setPrefWidth(120);
        
        // Orientation selector
        ComboBox<ComponentOrientation> chipOrientationCombo = new ComboBox<>(FXCollections.observableArrayList(
            ComponentOrientation.HORIZONTAL, ComponentOrientation.VERTICAL));
        chipOrientationCombo.setCellFactory(lv -> new ListCell<ComponentOrientation>() {
            @Override protected void updateItem(ComponentOrientation item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.getDisplayName());
            }
        });
        chipOrientationCombo.setButtonCell(new ListCell<ComponentOrientation>() {
            @Override protected void updateItem(ComponentOrientation item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.getDisplayName());
            }
        });
        chipOrientationCombo.setValue(ComponentOrientation.HORIZONTAL);
        chipOrientationCombo.setPrefWidth(100);
        
        // Preview with smaller scale for component panel
        DIPChip dipChip = new DIPChip("DIP IC", 0, 0, "ThroughHole", DIPPackage.DIP8, ComponentOrientation.HORIZONTAL);
        dipChip.setScaleFactor(0.6); // Smaller for preview
        
        Button addChipBtn = new Button("Add to Board");
        
        addChipBtn.setOnAction(e -> {
            DIPChip newChip = new DIPChip("DIP IC", 0, 0, "ThroughHole", dipPackageCombo.getValue(), chipOrientationCombo.getValue());
            componentManager.addComponent(newChip, 300, 50);
        });
        
        // Update preview when settings change
        Runnable updatePreview = () -> {
            DIPChip newChip = new DIPChip("DIP IC", 0, 0, "ThroughHole", dipPackageCombo.getValue(), chipOrientationCombo.getValue());
            newChip.setScaleFactor(0.6); // Keep preview small
            section.getChildren().set(0, newChip.render());
        };
        
        dipPackageCombo.valueProperty().addListener((obs, oldVal, newVal) -> updatePreview.run());
        chipOrientationCombo.valueProperty().addListener((obs, oldVal, newVal) -> updatePreview.run());
        
        section.getChildren().addAll(
            dipChip.render(),
            new Label("Package:"), dipPackageCombo,
            new Label("Orientation:"), chipOrientationCombo,
            addChipBtn
        );
        return section;
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