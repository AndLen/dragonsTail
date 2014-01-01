package gui;

import game.CardGame;
import game.StorageManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by Andrew on 28/12/13.
 */
public class CardFrame implements ActionListener, WindowListener {
    private JFrame frame;
    private CardPanel panel;
    private CardGame game;
    private Object lock = new Object();

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

    private void showStats() {
        JFrame frame = new JFrame("Stats");
        JPanel panel = new JPanel();
        StringBuilder sb = new StringBuilder();
        sb.append("Wins:" + StorageManager.getWins() + " ");
        sb.append("Losses: " + StorageManager.getLosses() + " ");
        sb.append("W/L Ratio: " + StorageManager.getRatio()*100 + " %" + "\n");
        int bestTime = StorageManager.getBestTime();
        sb.append("Best Time: " + (bestTime == Integer.MAX_VALUE ? "N/A" : bestTime/1000 + " s") + "\n");
        int lowestMoves = StorageManager.getLowestMoves();
        sb.append("Lowest # Moves: " + (lowestMoves == Integer.MAX_VALUE ? "N/A" : lowestMoves + " moves") + "\n");
        JTextArea textArea = new JTextArea(sb.toString(), 20, 50);
        textArea.setEditable(false);
        panel.add(textArea);

        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
    }

    private void showHelp() {
        JFrame frame = new JFrame("How to Play");
        JPanel panel = new JPanel();
        JTextArea textArea = new JTextArea("Text", 20, 50);
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
