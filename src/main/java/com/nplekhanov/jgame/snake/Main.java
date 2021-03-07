package com.nplekhanov.jgame.snake;

import com.nplekhanov.jgame.Vector2Int;
import com.nplekhanov.swing.InteractiveCanvas;

import java.awt.Dimension;

public class Main {
    public static void main(String[] args) {
        InteractiveCanvas.start(60, new Dimension(800, 800), new SnakeGame(new Vector2Int(16, 16)));
    }
}
