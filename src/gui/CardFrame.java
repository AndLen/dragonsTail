package gui;

import game.CardGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by Andrew on 28/12/13.
 */
public class CardFrame implements WindowListener, ActionListener {
    private JFrame frame;
    private CardPanel panel;
    private CardGame game;

    public CardFrame() {
        game = new CardGame(this);
        frame = new JFrame("Dragon's Tail");
        frame.setLayout(new BorderLayout());
        frame.addWindowListener(this);
        panel = new CardPanel(game);
        frame.add(panel, BorderLayout.CENTER);
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

        frame.add(panel, BorderLayout.CENTER);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.pack();
        frame.setVisible(true);
        startGame();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equals("Restart")) {
            game.restart();
        } else if (command.equals("Help")) {
            showHelp();
        }
    }

    private void showHelp() {
        JFrame frame = new JFrame("How to Play");
        JPanel panel = new JPanel();
        JTextArea textArea = new JTextArea("Text",20,50);
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
