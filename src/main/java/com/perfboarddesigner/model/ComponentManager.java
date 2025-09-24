package com.perfboarddesigner.model;

import com.perfboarddesigner.Constants;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import java.util.ArrayList;
import java.util.List;

public class ComponentManager {
    private Board board;
    private List<PlacedComponent> placedComponents;
    private PlacedComponent selectedComponent;
    private boolean gridSnapEnabled = true;
    private double snapTolerance = 5.0; // pixels

    public ComponentManager(Board board) {
        this.board = board;
        this.placedComponents = new ArrayList<>();
    }

    public void addComponent(Component component, double x, double y) {
        // Apply current board scale to components
        if (component instanceof SVGComponent) {
            ((SVGComponent) component).setScaleFactor(board.getScaleFactor());
        } else if (component instanceof DIPChip) {
            ((DIPChip) component).setScaleFactor(board.getScaleFactor());
        }
        
        if (gridSnapEnabled) {
            // Snap to nearest grid point
            double[] snappedPos = snapToGrid(x, y);
            x = snappedPos[0];
            y = snappedPos[1];
        }
        
        PlacedComponent placedComponent = new PlacedComponent(component, x, y);
        placedComponents.add(placedComponent);
        
        // Add visual representation to board
        Pane componentPane = component.render();
        componentPane.setLayoutX(x);
        componentPane.setLayoutY(y);
        
        // Add drag functionality
        setupDragHandlers(componentPane, placedComponent);
        
        board.getBoardPane().getChildren().add(componentPane);
    }
    
    private void setupDragHandlers(Pane componentPane, PlacedComponent placedComponent) {
        final double[] mouseAnchor = new double[2];
        
        componentPane.setOnMousePressed((MouseEvent event) -> {
            mouseAnchor[0] = event.getSceneX();
            mouseAnchor[1] = event.getSceneY();
            selectedComponent = placedComponent;
            componentPane.toFront();
            event.consume();
        });
        
        componentPane.setOnMouseDragged((MouseEvent event) -> {
            if (selectedComponent == placedComponent) {
                double deltaX = event.getSceneX() - mouseAnchor[0];
                double deltaY = event.getSceneY() - mouseAnchor[1];
                
                double newX = placedComponent.getX() + deltaX;
                double newY = placedComponent.getY() + deltaY;
                
                if (gridSnapEnabled) {
                    double[] snappedPos = snapToGrid(newX, newY);
                    newX = snappedPos[0];
                    newY = snappedPos[1];
                }
                
                placedComponent.setX(newX);
                placedComponent.setY(newY);
                componentPane.setLayoutX(newX);
                componentPane.setLayoutY(newY);
                
                mouseAnchor[0] = event.getSceneX();
                mouseAnchor[1] = event.getSceneY();
                event.consume();
            }
        });
        
        componentPane.setOnMouseReleased((MouseEvent event) -> {
            selectedComponent = null;
            event.consume();
        });
    }
    
    private double[] snapToGrid(double x, double y) {
        // Get board info for proper offset calculation
        double boardX = board.getBoardPane().getLayoutX();
        double boardY = board.getBoardPane().getLayoutY();
        
        // Adjust coordinates relative to board
        double relativeX = x - boardX;
        double relativeY = y - boardY;
        
        // Convert to mm, snap to grid, convert back to pixels
        double xMm = relativeX / Constants.MM_TO_PIXEL;
        double yMm = relativeY / Constants.MM_TO_PIXEL;
        
        // Snap to hole spacing (2.54mm typically)
        double holeSpacing = 2.54; // Standard perfboard spacing
        double marginMm = Constants.MARGIN_MM;
        
        // Calculate grid position
        int gridX = (int) Math.round((xMm - marginMm) / holeSpacing);
        int gridY = (int) Math.round((yMm - marginMm) / holeSpacing);
        
        // Ensure minimum values
        gridX = Math.max(0, gridX);
        gridY = Math.max(0, gridY);
        
        // Convert back to mm and then pixels, then add board offset
        double snappedXMm = marginMm + (gridX * holeSpacing);
        double snappedYMm = marginMm + (gridY * holeSpacing);
        
        return new double[]{
            boardX + (snappedXMm * Constants.MM_TO_PIXEL),
            boardY + (snappedYMm * Constants.MM_TO_PIXEL)
        };
    }
    
    public void removeComponent(PlacedComponent component) {
        placedComponents.remove(component);
        // Remove from board pane as well
        board.getBoardPane().getChildren().removeIf(node -> 
            node.getUserData() == component);
    }
    
    public void removeSelectedComponent() {
        if (selectedComponent != null) {
            removeComponent(selectedComponent);
            selectedComponent = null;
        }
    }
    
    public void clearAllComponents() {
        for (PlacedComponent component : new ArrayList<>(placedComponents)) {
            removeComponent(component);
        }
        placedComponents.clear();
    }
    
    // Getters and setters
    public boolean isGridSnapEnabled() { return gridSnapEnabled; }
    public void setGridSnapEnabled(boolean gridSnapEnabled) { this.gridSnapEnabled = gridSnapEnabled; }
    public List<PlacedComponent> getPlacedComponents() { return new ArrayList<>(placedComponents); }
    public PlacedComponent getSelectedComponent() { return selectedComponent; }
    
    public static class PlacedComponent {
        private Component component;
        private double x, y;
        private double rotation = 0; // degrees
        
        public PlacedComponent(Component component, double x, double y) {
            this.component = component;
            this.x = x;
            this.y = y;
        }
        
        // Getters and setters
        public Component getComponent() { return component; }
        public double getX() { return x; }
        public void setX(double x) { this.x = x; }
        public double getY() { return y; }
        public void setY(double y) { this.y = y; }
        public double getRotation() { return rotation; }
        public void setRotation(double rotation) { this.rotation = rotation; }
    }
}
