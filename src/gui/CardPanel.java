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
    protected static double CARD_WIDTH = 50;
    protected static double CARD_HEIGHT = 100;
    private static final double CARD_IMAGE_WIDTH = 224.25;
    private static final double CARD_IMAGE_HEIGHT = 312.8125;
    private static double CARD_X_GAP = 10;
    private static double CARD_Y_NO_OVERLAP = 10;

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


                x += CARD_WIDTH + CARD_X_GAP;
            }
            x = CARD_X_GAP;
            y += CARD_Y_NO_OVERLAP;
        }
        if (activeCard != null && activeX != -1 && activeY != -1) {
            renderCard(activeCard, g, activeX, activeY);
        }
    }

    private void renderCard(Card card, Graphics2D g, double x, double y) {
        //Make necessary adjustments so SVG is drawn correctly
        AffineTransform oldTransform = g.getTransform();

        double xScale = CARD_WIDTH / CARD_IMAGE_WIDTH;
        double yScale = CARD_HEIGHT /CARD_IMAGE_HEIGHT;
        g.translate(x,y);
        g.scale(xScale, yScale);

        g.setColor(Color.black);
        //Border around card.
       // g.drawRoundRect(0,0,(int)CARD_WIDTH,(int)CARD_HEIGHT,10,10);
        ImageManager.renderSVG(card, g);
        //Revert changes.
        g.setTransform(oldTransform);
    }

    @Override
    public void componentResized(ComponentEvent e) {
        CARD_Y_NO_OVERLAP = getHeight()/25;

        CARD_WIDTH = (getWidth()-CARD_Y_NO_OVERLAP) / 9;
        CARD_X_GAP = getWidth() / 64;
        CARD_HEIGHT = CARD_WIDTH/(CARD_IMAGE_WIDTH/CARD_IMAGE_HEIGHT);
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
        int col = (int) (e.getX() / (CARD_WIDTH + CARD_X_GAP));
        List<Card> possibleCards = game.getBoard().get(0);
        int maxIndex = (int) (e.getY() - (CARD_HEIGHT - CARD_Y_NO_OVERLAP) / CARD_Y_NO_OVERLAP);
        int minIndex = (int) (e.getY() / CARD_Y_NO_OVERLAP);
        Card clickedCard = null;
        for (int i = maxIndex; i >= minIndex; i--) {
            if (possibleCards.size() > i && possibleCards.get(i) != null) {
                clickedCard = possibleCards.get(i);
                break;
            }
        }
        if (clickedCard != null) {
            activeCard = clickedCard;
        }
        System.out.println(clickedCard);
        activeCard = possibleCards.get(0);
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
