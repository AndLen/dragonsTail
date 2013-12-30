package gui;

import game.CardGame;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by Andrew on 28/12/13.
 */
public class CardFrame {
    private JFrame frame;
    private CardPanel panel;
    private CardGame game;

    public CardFrame() {
        game = new CardGame(this);
        frame = new JFrame("Dragon's Tail");
        panel = new CardPanel(game);
        frame.add(panel);
        frame.setLayout(new GridLayout());
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String args[]) throws InvocationTargetException, InterruptedException {
        final CardFrame[] frame = new CardFrame[1];
        SwingUtilities.invokeAndWait(new Runnable() {  //Note 1
            public void run() {
                frame[0] = new CardFrame();
            }
        });

        frame[0].startGame();
    }

    public void restartGame() {
        panel.removeComponentListener(panel);
        panel.removeMouseMotionListener(panel);
        panel.removeMouseListener(panel);
        frame.remove(panel);

        game = new CardGame(this);
        panel = new CardPanel(game);

        frame.add(panel);
        frame.setLayout(new GridLayout());
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.pack();
        frame.setVisible(true);
        startGame();
    }

    private void startGame() {
        game.dealGame(panel);
    }
}
