package gui;

import game.Card;
import game.CardGame;
import game.CardMove;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.util.List;

/**
 * Created by Andrew on 28/12/13.
 */
public class CardPanel extends JPanel implements ComponentListener, MouseListener, MouseMotionListener {
    private static final Color BACKGROUND_GREEN = new Color(0, 150, 0);
    private static final double CARD_IMAGE_WIDTH = 224.25;
    private static final double CARD_IMAGE_HEIGHT = 312.8125;
    protected static double CARD_WIDTH;
    protected static double CARD_HEIGHT;
    private static double CARD_X_GAP;
    private static double CARD_Y_NO_OVERLAP;
    private final CardGame game;
    //Moving cards
    private CardMove activeMove = null;
    private int activeX = -1;
    private int activeY = -1;

    protected CardPanel(CardGame game) {
        this.game = game;
        this.setPreferredSize(new Dimension(500, 500));
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.addComponentListener(this);
        //Set up initial constants
        componentResized(null);
    }

    public void paint(Graphics gOriginal) {
        Graphics2D g = (Graphics2D) gOriginal;
        g.setColor(BACKGROUND_GREEN);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        g.setColor(Color.black);
        final List<List<Card>> gameBoard = game.getBoard();
        double x = CARD_X_GAP;
        double y = CARD_Y_NO_OVERLAP;
        for (List<Card> cards : gameBoard) {
            for (Card card : cards) {
                renderCard(card, g, x, y);
                y += CARD_Y_NO_OVERLAP;
            }
            y = CARD_Y_NO_OVERLAP;
            x += CARD_WIDTH + CARD_X_GAP;
        }
        if (activeMove != null && activeX != -1 && activeY != -1) {
            renderCard(gameBoard.get(activeMove.getBoardIndexFrom()).get(activeMove.getToMoveTop()), g, activeX, activeY);
        }
    }

    private void renderCard(Card card, Graphics2D g, double x, double y) {
        //Make necessary adjustments so SVG is drawn correctly
        AffineTransform oldTransform = g.getTransform();

        double xScale = CARD_WIDTH / CARD_IMAGE_WIDTH;
        double yScale = CARD_HEIGHT / CARD_IMAGE_HEIGHT;
        g.translate(x, y);
        g.scale(xScale, yScale);

        g.setColor(Color.black);
        ImageManager.renderSVG(card, g);
        //Revert changes.
        g.setTransform(oldTransform);
    }

    @Override
    public void componentResized(ComponentEvent e) {

        CARD_X_GAP = getWidth() / 64;
        CARD_WIDTH = (getWidth() - CARD_X_GAP) / 9;

        CARD_HEIGHT = CARD_WIDTH / (CARD_IMAGE_WIDTH / CARD_IMAGE_HEIGHT);
        CARD_Y_NO_OVERLAP = CARD_HEIGHT / 4;
    }

    @Override
    public void componentMoved(ComponentEvent e) {

    }

    @Override
    public void componentShown(ComponentEvent e) {

    }

    @Override
    public void componentHidden(ComponentEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

        int col = findCol(e.getX());
        List<Card> possibleCards = game.getBoard().get(col);
        int index = findCardIndex(possibleCards, e.getY());
        if (index == -1) {
            //Indicates none found.
            return;
        }

        if (possibleCards.get(index) != null) {
            activeMove = new CardMove(index, col);
        }

        System.out.println(activeMove);
        activeX = e.getX();
        activeY = e.getY();
        this.repaint();
    }

    private int findCol(int xPressed) {
        int col = (int) ((xPressed - CARD_X_GAP) / (CARD_WIDTH + CARD_X_GAP));
        System.out.println("Col: " + col);
        return col;
    }

    private int findCardIndex(List<Card> cards, int yPressed) {
        //y-coord of the end of the last card in the pile

        int endY = (int) (CARD_HEIGHT + (cards.size() * CARD_Y_NO_OVERLAP));
        if (yPressed > endY || yPressed < CARD_Y_NO_OVERLAP) {
            //i.e. not clicked on a card
            return -1;
        }

        int index = (int) ((yPressed - CARD_Y_NO_OVERLAP) / (CARD_Y_NO_OVERLAP));

        if (index >= cards.size()) {
            index = cards.size() - 1;
        }

        System.out.println("Index: " + index);
        return index;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        int col = findCol(e.getX());
        List<Card> possibleCards = game.getBoard().get(col);
        int index = findCardIndex(possibleCards, e.getY());
        if (index != -1) {

            if (possibleCards.get(index) != null) {
                activeMove.cardReleased(col);
                System.out.println("Moving:" + activeMove);
                String result = activeMove.makeMove(game);
                if (!result.isEmpty()) {
                    JOptionPane.showMessageDialog(this, result);
                }
            }
        }
        activeMove = null;
        this.repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (activeMove != null) {
            activeX = e.getX();
            activeY = e.getY();
        } else {
            activeX = -1;
            activeY = -1;
        }
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }
}
