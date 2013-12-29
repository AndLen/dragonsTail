package gui;

import game.CardGame;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Andrew on 28/12/13.
 */
public class CardFrame {
    private final JFrame frame;
    private final CardPanel panel;
    private final CardGame game;

    public CardFrame(){
        game = new CardGame();
        frame = new JFrame("Dragon's Tail");
        panel = new CardPanel(game);
        frame.add(panel);
        frame.setLayout(new GridLayout());
        frame.pack();
        frame.setVisible(true);

    }

    public static void main(String args[]){
        new CardFrame();
    }
}
