package gui;

import game.CardGame;
import game.StorageManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;

/**
 * Created by Andrew on 28/12/13.
 */
public class CardFrame implements ActionListener, WindowListener {
    private JFrame frame;
    private CardPanel panel;
    private CardGame game;
    private Object lock = new Object();
    private String howToPlay = "The aim of the game is to fill each of the top 8 piles (i.e. the top row of the screen) from Ace to King in a particular suit.\n" +
            " For example, you may choose to put the Ace of Hearts on one pile, and then follow it with the Two up to the King of Hearts to complete the pile.\n\n" +
            "A card on the board (i.e. the 8 columns of cards) may be moved individually onto another card in a different column if the two cards are in order,\n" +
            "and are either the same suit or different coloured suits. Multiple cards may be moved together if they are in order, the same suit, and are being\n" +
            "moved to a card of the same suit that is also in order.\n " +
            "You may also move any card into an empty column (i.e. one which has been cleared of all cards)\n" +
            "Note: \"in order\" in this context means the cards are in descending order\n\n" +
            "The left-most column of cards is the \"Dragon Tail\". It is unique in that any card from the Deck may be moved onto it, regardless\n" +
            "of suit or rank. This is only the case when moving a card from the deck - not from any other column or top pile.\n\n" +
            "To ease gameplay, one can double-click a card to send it to the appropriate top pile where possible. For example, double-clicking a 5 of Hearts\n" +
            "will move it ontop of a 4 of Hearts in a pile if one exists. In addition, double-clicking the deck will send the top card in the deck to the\n" +
            "Dragon's Tail if there is no appropriate pile for it.\n\n" +
            "A range of keyboard shortcuts are available (see the Menu), including undo, which is useful for fixing misclicks.\n";

    public CardFrame() {
        frame = new JFrame("Dragon's Tail");
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.addWindowListener(this);

        JMenuBar menuBar = new JMenuBar();
        frame.add(menuBar, BorderLayout.PAGE_START);
        JMenu jMenu = new JMenu("Menu");
        menuBar.add(jMenu);

        JMenuItem restartMenuItem = new JMenuItem("Restart");
        jMenu.add(restartMenuItem);
        restartMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_R, InputEvent.CTRL_MASK));
        restartMenuItem.addActionListener(this);

        JMenuItem helpMenuItem = new JMenuItem("Help");
        jMenu.add(helpMenuItem);
        helpMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_H, InputEvent.CTRL_MASK));
        helpMenuItem.addActionListener(this);

        JMenuItem undoMenuItem = new JMenuItem("Undo");
        jMenu.add(undoMenuItem);
        undoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK));
        undoMenuItem.addActionListener(this);

        JMenuItem statMenuItem = new JMenuItem("Stats");
        jMenu.add(statMenuItem);
        statMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
        statMenuItem.addActionListener(this);

    }

    public static void main(String args[]) throws InvocationTargetException, InterruptedException {
        new Thread() {
            public void run() {
                CardFrame frame = new CardFrame();
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                frame.restartGame();
            }
        }.start();
//        final CardFrame[] frame = new CardFrame[1];
//
//
//        frame[0].startGame();
    }

    public static void showStats() {
        final JFrame frame = new JFrame("Stats");
        frame.setLayout(new BorderLayout());

        final JPanel textPanel = createStatText();
        frame.add(textPanel);
        JButton reset = new JButton("Reset");
        reset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StorageManager.reset();
                frame.remove(textPanel);
                frame.add(createStatText());
                frame.pack();
            }
        });
        frame.add(reset, BorderLayout.PAGE_END);
        frame.add(textPanel);
        frame.pack();
        frame.setVisible(true);
    }

    private static JPanel createStatText() {
        JPanel panel = new JPanel();
        StringBuilder sb = new StringBuilder();
        sb.append("Wins:" + StorageManager.getWins() + " ");
        sb.append("Losses: " + StorageManager.getLosses() + " ");
        double ratio = StorageManager.getRatio() * 100;
        sb.append("W/L Ratio: " + new DecimalFormat("#.##").format(ratio) + " %" + "\n\n");
        int bestTime = StorageManager.getBestTime();
        sb.append("Best Time: " + (bestTime == Integer.MAX_VALUE ? "N/A" : bestTime / 1000 + " s") + "\n\n");
        int lowestMoves = StorageManager.getLowestMoves();
        sb.append("Lowest # Moves: " + (lowestMoves == Integer.MAX_VALUE ? "N/A" : lowestMoves + " moves"));
        JTextArea textArea = new JTextArea(sb.toString(), 5, 30);
        textArea.setEditable(false);
        panel.add(textArea);
        return panel;
    }

    public void restartGame() {
        //Make a new thread to ensure we don't run it on the EDT

        new Thread() {
            public void run() {
                if (panel != null) {
                    panel.removeComponentListener(panel);
                    panel.removeMouseMotionListener(panel);
                    panel.removeMouseListener(panel);
                    frame.remove(panel);
                }

                game = new CardGame(CardFrame.this);
                panel = new CardPanel(game);

                frame.add(panel, BorderLayout.CENTER);
                frame.pack();
                frame.setVisible(true);
                frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
                //  frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                startGame();
            }

        }.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equals("Restart")) {
            StorageManager.loss();
            restartGame();
        } else if (command.equals("Help")) {
            showHelp();
        } else if (command.equals("Undo")) {
            synchronized (lock) {
                game.undo();
                panel.repaint();
            }
        } else if (command.equals("Stats")) {
            showStats();
        }
    }

    private void showHelp() {
        JFrame frame = new JFrame("How to Play");
        JPanel panel = new JPanel();
        JTextArea textArea = new JTextArea(howToPlay, 20, 50);
        textArea.setEditable(false);
        panel.add(textArea);
        // panel.setPreferredSize(new Dimension(300, 500));

        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
    }

    private void startGame() {
        game.dealGame(panel);
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        StorageManager.loss();
    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }
}
