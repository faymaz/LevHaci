package com.perfboarddesigner.model;

import com.perfboarddesigner.Constants;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class Board {
    private BoardType type;
    private BoardSide side;
    private Pane boardPane;
    private List<Line> jumpers;
    private double widthMm;
    private double heightMm;
    private double holeSpacingMm;
    // Removed MM_TO_PIXEL and MARGIN_MM, now in Constants.java
    private double scaleFactor = 1.0;
    private Line tempJumper; // For preview during drag

    public Board(BoardType type, BoardSide side, double widthMm, double heightMm, double holeSpacingMm) {
        this.type = type;
        this.side = side;
        this.widthMm = widthMm;
        this.heightMm = heightMm;
        this.holeSpacingMm = holeSpacingMm;
        this.boardPane = new Pane();
        this.jumpers = new ArrayList<>();
        renderBoard();
    }

    public void renderBoard() {
        boardPane.getChildren().clear();
        double pixelWidth = widthMm * Constants.MM_TO_PIXEL;
        double pixelHeight = heightMm * Constants.MM_TO_PIXEL;
        double marginPx = Constants.MARGIN_MM * Constants.MM_TO_PIXEL;
        double innerWidthMm = widthMm - (2 * Constants.MARGIN_MM);
        double innerHeightMm = heightMm - (2 * Constants.MARGIN_MM);
        double spacing = holeSpacingMm * (Constants.MM_TO_PIXEL / 2.54) * scaleFactor;
        double holeRadius = 2.5 * scaleFactor;

        int cols = (int) (innerWidthMm / holeSpacingMm);
        int rows = (int) (innerHeightMm / holeSpacingMm);

        // Draw outer board rectangle
        Rectangle outerRect = new Rectangle(0, 0, pixelWidth, pixelHeight);
        outerRect.setFill(Color.LIGHTGREEN);
        outerRect.setStroke(Color.BLACK);
        outerRect.setStrokeWidth(1 * scaleFactor);
        boardPane.getChildren().add(outerRect);

        // Draw background for double-sided board
        if (side == BoardSide.DOUBLE) {
            Rectangle blueBackground = new Rectangle(0, 0, pixelWidth, pixelHeight);
            blueBackground.setFill(Color.BLUE);
            boardPane.getChildren().add(0, blueBackground);
        }

        // Draw holes inside margins
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double x = marginPx + (j * spacing);
                double y = marginPx + (i * spacing);
                Circle hole = new Circle(holeRadius, Color.GRAY);
                hole.setStroke(Color.BLACK);
                hole.setStrokeWidth(1 * scaleFactor);
                hole.setLayoutX(x);
                hole.setLayoutY(y);
                boardPane.getChildren().add(hole);
            }
        }

        // Draw strips based on board type
        if (type == BoardType.STRIPBOARD) {
            for (int i = 0; i < rows; i += 2) {
                double y = marginPx + (i * spacing);
                Line strip = new Line(marginPx, y, pixelWidth - marginPx, y);
                strip.setStroke(Color.rgb(184, 115, 51));
                strip.setStrokeWidth(3 * scaleFactor);
                boardPane.getChildren().add(strip);
            }
        } else if (type == BoardType.MIXED) {
            for (int j = 0; j < cols; j += 3) {
                double x = marginPx + (j * spacing);
                Line strip = new Line(x, marginPx, x, pixelHeight - marginPx);
                strip.setStroke(Color.rgb(184, 115, 51));
                strip.setStrokeWidth(3 * scaleFactor);
                boardPane.getChildren().add(strip);
            }
        }

        boardPane.setPrefSize(pixelWidth, pixelHeight);
    }

    public void startJumper(double xMm, double yMm) {
        tempJumper = new Line();
        tempJumper.setStartX(getPixelX(xMm));
        tempJumper.setStartY(getPixelY(yMm));
        tempJumper.setStroke(Color.RED);
        tempJumper.setStrokeWidth(1.5 * scaleFactor);
        boardPane.getChildren().add(tempJumper);
    }

    public void updateTempJumper(double xMm, double yMm) {
        if (tempJumper != null) {
            tempJumper.setEndX(getPixelX(xMm));
            tempJumper.setEndY(getPixelY(yMm));
        }
    }

    public void finishJumper(double xMm, double yMm) {
        if (tempJumper != null) {
            tempJumper.setEndX(getPixelX(xMm));
            tempJumper.setEndY(getPixelY(yMm));
            jumpers.add(tempJumper);
            tempJumper = null;
        }
    }

    public void clearJumpers() {
        for (Line jumper : jumpers) {
            boardPane.getChildren().remove(jumper);
        }
        jumpers.clear();
    }

    private double getPixelX(double xMm) {
        double marginPx = Constants.MARGIN_MM * Constants.MM_TO_PIXEL;
        double spacingPx = holeSpacingMm * (Constants.MM_TO_PIXEL / 2.54) * scaleFactor;
        int col = (int) ((xMm - Constants.MARGIN_MM) / holeSpacingMm);
        return marginPx + (col * spacingPx);
    }

    private double getPixelY(double yMm) {
        double marginPx = Constants.MARGIN_MM * Constants.MM_TO_PIXEL;
        double spacingPx = holeSpacingMm * (Constants.MM_TO_PIXEL / 2.54) * scaleFactor;
        int row = (int) ((yMm - Constants.MARGIN_MM) / holeSpacingMm);
        return marginPx + (row * spacingPx);
    }

    public Pane getBoardPane() { return boardPane; }
    public BoardType getType() { return type; }
    public void setType(BoardType type) { this.type = type; renderBoard(); }
    public BoardSide getSide() { return side; }
    public void setSide(BoardSide side) { this.side = side; renderBoard(); }
    public void setDimensions(double widthMm, double heightMm, double holeSpacingMm) {
        this.widthMm = widthMm;
        this.heightMm = heightMm;
        this.holeSpacingMm = holeSpacingMm;
        renderBoard();
    }
    public void setScaleFactor(double scaleFactor) {
        this.scaleFactor = scaleFactor;
        renderBoard();
    }
    public List<Line> getJumpers() { return jumpers; }
}