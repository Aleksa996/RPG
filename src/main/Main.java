package main;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        //CREATE WINDOW
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("RPG");
        GamePanel gamePanel = new GamePanel();
        window.add(gamePanel);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);
        //INIT GAME
        gamePanel.setUpGame();
        gamePanel.startGameThread();
    }
}