package gui;

import game.CardGame;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by Andrew on 28/12/13.
 */
public class CardFrame {
    private final JFrame frame;
    private final CardPanel panel;
    private final CardGame game;

    public CardFrame() {
        game = new CardGame();
        frame = new JFrame("Dragon's Tail");
        panel = new CardPanel(game);
        frame.add(panel);
        frame.setLayout(new GridLayout());
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String args[]) throws InvocationTargetException, InterruptedException {
        final CardFrame[] frame = new CardFrame[1];
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                //This is far too hacky, fix it.
                frame[0] = new CardFrame();
            }
        });
        frame[0].startGame();

    }

    public void startGame() {
        game.dealGame(panel);
    }
}
