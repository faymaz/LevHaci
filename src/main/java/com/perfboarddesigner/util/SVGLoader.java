package com.perfboarddesigner.util;

import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for loading and rendering SVG files as JavaFX components
 */
public class SVGLoader {
    
    // Cache for loaded SVG content
    private static final Map<String, String> svgCache = new HashMap<>();
    
    /**
     * Load SVG file from resources and return as JavaFX Pane
     * @param svgFileName Name of the SVG file (e.g., "resistor.svg")
     * @param width Desired width of the component
     * @param height Desired height of the component
     * @param scaleFactor Scale factor for resizing
     * @return Pane containing the SVG graphics
     */
    public static Pane loadSVG(String svgFileName, double width, double height, double scaleFactor) {
        try {
            String svgContent = loadSVGContent(svgFileName);
            if (svgContent != null) {
                return createSVGPane(svgContent, width, height, scaleFactor);
            }
        } catch (Exception e) {
            System.err.println("Error loading SVG: " + svgFileName + " - " + e.getMessage());
        }
        
        // Fallback to basic shape if SVG loading fails
        return createFallbackShape(svgFileName, width, height, scaleFactor);
    }
    
    /**
     * Load SVG content from resources
     */
    private static String loadSVGContent(String svgFileName) throws IOException {
        if (svgCache.containsKey(svgFileName)) {
            return svgCache.get(svgFileName);
        }
        
        InputStream inputStream = SVGLoader.class.getResourceAsStream("/components/" + svgFileName);
        if (inputStream == null) {
            System.out.println("SVG file not found: " + svgFileName + ", using fallback rendering");
            return null;
        }
        
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        
        String svgContent = content.toString();
        svgCache.put(svgFileName, svgContent);
        return svgContent;
    }
    
    /**
     * Create JavaFX Pane from SVG content
     */
    private static Pane createSVGPane(String svgContent, double width, double height, double scaleFactor) {
        Pane pane = new Pane();
        Group svgGroup = new Group();
        
        // Simple SVG path extraction (for basic SVG files)
        // This is a simplified implementation - for complex SVGs, use a proper SVG library
        String[] paths = extractSVGPaths(svgContent);
        
        for (String pathData : paths) {
            if (!pathData.isEmpty()) {
                SVGPath svgPath = new SVGPath();
                svgPath.setContent(pathData);
                svgPath.setFill(Color.DARKGRAY);
                svgPath.setStroke(Color.BLACK);
                svgPath.setStrokeWidth(0.5);
                
                // Scale the path
                svgPath.setScaleX(scaleFactor);
                svgPath.setScaleY(scaleFactor);
                
                svgGroup.getChildren().add(svgPath);
            }
        }
        
        pane.getChildren().add(svgGroup);
        pane.setPrefSize(width * scaleFactor, height * scaleFactor);
        
        return pane;
    }
    
    /**
     * Extract path data from SVG content (simplified implementation)
     */
    private static String[] extractSVGPaths(String svgContent) {
        // This is a very basic implementation
        // For production use, consider using a proper SVG parsing library
        java.util.List<String> paths = new java.util.ArrayList<>();
        
        String[] lines = svgContent.split("\n");
        for (String line : lines) {
            if (line.contains("<path") && line.contains("d=\"")) {
                int start = line.indexOf("d=\"") + 3;
                int end = line.indexOf("\"", start);
                if (end > start) {
                    paths.add(line.substring(start, end));
                }
            }
        }
        
        return paths.toArray(new String[0]);
    }
    
    /**
     * Create fallback shape when SVG loading fails
     */
    private static Pane createFallbackShape(String svgFileName, double width, double height, double scaleFactor) {
        // Return current SVGComponent rendering as fallback
        // This ensures the application continues to work even without SVG files
        return new Pane(); // Empty pane for now, SVGComponent will handle the rendering
    }
    
    /**
     * Check if SVG file exists in resources
     */
    public static boolean svgExists(String svgFileName) {
        InputStream inputStream = SVGLoader.class.getResourceAsStream("/components/" + svgFileName);
        return inputStream != null;
    }
}
