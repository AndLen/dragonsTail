package gui;

import game.Card;
import game.CardGame;

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
    private Card activeCard = null;
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
        if (activeCard != null && activeX != -1 && activeY != -1) {
            renderCard(activeCard, g, activeX, activeY);
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

        int col = (int) ((e.getX() - CARD_X_GAP) / (CARD_WIDTH + CARD_X_GAP));
        System.out.println("Col: " + col);
        List<Card> possibleCards = game.getBoard().get(col);

        //y-coord of the end of the last card in the pile
        int endY = (int) (CARD_HEIGHT + (possibleCards.size() * CARD_Y_NO_OVERLAP));
        if (e.getY() > endY || e.getY() < CARD_Y_NO_OVERLAP) {
            //i.e. not clicked on a card
            return;
        }

        int index = (int) ((e.getY() - CARD_Y_NO_OVERLAP) / (CARD_Y_NO_OVERLAP));

        System.out.println("Index: " + index);

        if (index >= possibleCards.size()) {
            index = possibleCards.size() - 1;
        }
        if (possibleCards.get(index) != null) {
            activeCard = possibleCards.get(index);
        }

        System.out.println(activeCard);
        this.repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        activeCard = null;
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
        if (activeCard != null) {
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
