package gui;

import game.Card;
import game.CardGame;
import game.CardMove;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.util.List;
import java.util.Queue;

/**
 * Created by Andrew on 28/12/13.
 */
public class CardPanel extends JPanel implements ComponentListener, MouseListener, MouseMotionListener {
    private static final Color BACKGROUND_GREEN = new Color(0, 150, 0);
    private static final double CARD_IMAGE_WIDTH = 224.25;
    private static final double CARD_IMAGE_HEIGHT = 312.8125;
    protected static double CARD_WIDTH;
    protected static double CARD_HEIGHT;
    private static int NUMBER_CLICKS = 0;
    private static long LAST_PRESS = System.currentTimeMillis();
    private static int DRAGONS_TAIL_HIDDEN_CARDS = 0;
    private static double CARD_X_GAP;
    private static double CARD_Y_NO_OVERLAP;
    private static double Y_BOARD_OFFSET;
    private static double X_BOARD_OFFSET;
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

        renderTopRow(g);
        final List<List<Card>> gameBoard = game.getBoard();
        renderBoard(g, gameBoard);
        //Render cards being dragged if we can
        renderDragCards(g, gameBoard);
        renderDeck(g);
    }

    private void renderDeck(Graphics2D g) {
        Queue<Card> deck = game.getDeck();
        Card topCard = deck.peek();
        double x = X_BOARD_OFFSET;
        double y = this.getHeight() - CARD_Y_NO_OVERLAP - CARD_HEIGHT;

        g.setColor(Color.white);
        g.drawString(deck.size() + " cards remaining", (float) x, (float) y - 1);
        renderCard(topCard, g, x, y);
    }

    private void renderDragCards(Graphics2D g, List<List<Card>> gameBoard) {
        if (activeMove != null && activeX != -1 && activeY != -1) {
            if (activeMove.getMoveType() == CardMove.MOVE_TYPE.FROM_DECK) {
                renderCard(game.getDeck().peek(), g, activeX - (CARD_WIDTH / 2), activeY);
            } else {
                List<Card> beingDragged = gameBoard.get(activeMove.getBoardIndexFrom());
                double currY = activeY;
                for (int i = activeMove.getToMoveTop(); i < beingDragged.size(); i++) {

                    //Render so the top of cards are centered on the mouse
                    renderCard(beingDragged.get(i), g, activeX - (CARD_WIDTH / 2), currY);
                    currY += CARD_Y_NO_OVERLAP;
                }
            }
        }
    }

    private void renderBoard(Graphics2D g, List<List<Card>> gameBoard) {
        double x = X_BOARD_OFFSET;
        double y = Y_BOARD_OFFSET;

        /**Render the dragon's tail - special case due to size **/
        double maxY = getHeight() - CARD_Y_NO_OVERLAP * 2 - CARD_HEIGHT;
        List<Card> dragonsTail = gameBoard.get(0);
        double tailYSize = Y_BOARD_OFFSET + (dragonsTail.size() * CARD_Y_NO_OVERLAP) + (CARD_HEIGHT - CARD_Y_NO_OVERLAP);

        if (tailYSize > maxY) {
            DRAGONS_TAIL_HIDDEN_CARDS = 0;
            while (tailYSize > maxY) {
                tailYSize -= CARD_Y_NO_OVERLAP;
                DRAGONS_TAIL_HIDDEN_CARDS++;
            }
            //Render cards we can show
            for (int i = DRAGONS_TAIL_HIDDEN_CARDS; i < dragonsTail.size(); i++) {
                renderCard(dragonsTail.get(i), g, x, y);
                y += CARD_Y_NO_OVERLAP;
            }
            g.setColor(Color.white);
            g.drawString(CardPanel.DRAGONS_TAIL_HIDDEN_CARDS + " cards hidden", (float) X_BOARD_OFFSET, (float) Y_BOARD_OFFSET - 1);

        } else {
            //Render as normal.
            for (Card card : dragonsTail) {
                renderCard(card, g, x, y);
                y += CARD_Y_NO_OVERLAP;
            }
        }
        y = Y_BOARD_OFFSET;
        x += CARD_WIDTH + CARD_X_GAP;
        /** Done rendering dragon's tail**/

        //Render other cards normally
        for (int i = 1; i < gameBoard.size(); i++) {
            for (Card card : gameBoard.get(i)) {
                renderCard(card, g, x, y);
                y += CARD_Y_NO_OVERLAP;
            }
            y = Y_BOARD_OFFSET;
            x += CARD_WIDTH + CARD_X_GAP;
        }
    }

    private void renderTopRow(Graphics2D g) {
        List<List<Card>> topRow = game.getTopRow();
        double x = X_BOARD_OFFSET;
        double y = CARD_Y_NO_OVERLAP;
        for (List<Card> cards : topRow) {
            if (cards.size() > 0) {
                renderCard(cards.get(cards.size() - 1), g, x, y);

            } else {
                renderCard(null, g, x, y);
            }
            x += CARD_WIDTH + CARD_X_GAP;
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
        CARD_WIDTH = Math.min(CARD_WIDTH, 100);

        X_BOARD_OFFSET = (getWidth() - (CARD_WIDTH * 8) - (CARD_X_GAP * 7)) / 2;

        CARD_HEIGHT = CARD_WIDTH / (CARD_IMAGE_WIDTH / CARD_IMAGE_HEIGHT);
        CARD_Y_NO_OVERLAP = CARD_HEIGHT / 4;
        Y_BOARD_OFFSET = CARD_Y_NO_OVERLAP * 2 + CARD_HEIGHT;
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
        LAST_PRESS = System.currentTimeMillis();
        if (e.getY() < Y_BOARD_OFFSET) {
            processMoveFromTopRow(e);
        } else if (clickedOnDeck(e)) {
            processMoveFromDeck();
        } else {
            processMoveFromBoard(e);
        }


        System.out.println(activeMove);
        activeX = e.getX();
        activeY = e.getY();
        this.repaint();
    }

    private void processMoveFromBoard(MouseEvent e) {
        int col = findCol(e.getX());
        List<Card> possibleCards = game.getBoard().get(col);
        int index = findCardIndex(possibleCards, e.getY(), col == 0);
        if (index == -1) {
            //Indicates none found.
            return;
        }

        if (possibleCards.get(index) != null) {
            if (game.isValidNewMove(col, index)) {
                activeMove = new CardMove(index, col);
            } else {
                JOptionPane.showMessageDialog(this, "Cannot move that card, cards beneath it are not the same suit or correctly ordered");
            }
        }
    }

    private void processMoveFromDeck() {
        //Can assume did click on the deck.
        final Queue<Card> deck = game.getDeck();
        if (!deck.isEmpty()) {
            activeMove = new CardMove();
        }
    }

    private void processMoveFromTopRow(MouseEvent e) {

    }

    private int findCol(int xPressed) {
        int col = (int) ((xPressed - X_BOARD_OFFSET) / (CARD_WIDTH + CARD_X_GAP));
        System.out.println("Col: " + col);
        return col;
    }

    private int findCardIndex(List<Card> cards, int yPressed, boolean isDragonsTail) {
        //y-coord of the end of the last card in the pile

        int endY = (int) (Y_BOARD_OFFSET + (CARD_HEIGHT - CARD_Y_NO_OVERLAP) + (cards.size() * CARD_Y_NO_OVERLAP));
        if (yPressed > endY || yPressed < CARD_Y_NO_OVERLAP) {
            //i.e. not clicked on a card
            return -1;
        }

        int index = (int) ((yPressed - Y_BOARD_OFFSET) / (CARD_Y_NO_OVERLAP));

        //If it's a dragon's tail, we need to compensate for the cards hidden.
        if (isDragonsTail) {
            index += DRAGONS_TAIL_HIDDEN_CARDS;
        }

        if (index >= cards.size()) {
            index = cards.size() - 1;
        }

        System.out.println("Index: " + index);
        return index;
    }

    private boolean clickedOnDeck(MouseEvent e) {
        return (findCol(e.getX()) == 0 && e.getY() > (getHeight() - CARD_Y_NO_OVERLAP * 2 - CARD_HEIGHT));
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getX() < X_BOARD_OFFSET || e.getX() > (getWidth() - X_BOARD_OFFSET)) {
            //Clicked off the board, nothing to do with us.
            return;
        }
        System.out.println(System.currentTimeMillis() - LAST_PRESS);
        if ((System.currentTimeMillis() - LAST_PRESS) < 125) {
            NUMBER_CLICKS++;
        } else {
            NUMBER_CLICKS = 0;
        }
        if (NUMBER_CLICKS == 2) {
            doubleClick(e);
            NUMBER_CLICKS = 0;
        } else if (e.getY() < Y_BOARD_OFFSET) {
            processMoveToTopRow(e);
        } else {
            processMoveToBoard(e);
        }

        activeMove = null;
        this.repaint();
    }

    //TODO: Empty columns
    private void doubleClick(MouseEvent e) {
        System.out.println("DOUBLE CLICK");
        if (e.getY() > Y_BOARD_OFFSET) {
            int col = findCol(e.getX());
            List<Card> cardList = game.getBoard().get(col);
            Card toSendUp = cardList.get(cardList.size() - 1);
            List<List<Card>> topRow = game.getTopRow();

            System.out.println(toSendUp);
            //Find where it fits and send it there
            for (List<Card> pile : topRow) {
                if (toSendUp.getRank() == Card.Rank.ACE) {
                    //find empty
                    if (pile.isEmpty()) {
                        pile.add(cardList.remove(cardList.size() - 1));
                        return;
                    }
                } else if (!pile.isEmpty()) {
                    Card topOfPile = pile.get(pile.size() - 1);
                    if (toSendUp.matchesAndOneAbove(topOfPile)) {
                        pile.add(cardList.remove(cardList.size() - 1));
                        return;
                    }
                }
            }
            JOptionPane.showMessageDialog(this, "Cannot send that to the top");
        }
    }

    private void processMoveToBoard(MouseEvent e) {
        int col = findCol(e.getX());
        List<Card> possibleCards = game.getBoard().get(col);
        int index = findCardIndex(possibleCards, e.getY(), col == 0);
        if (index != -1) {

            if (possibleCards.get(index) != null) {
                activeMove.cardReleased(col, false);
                System.out.println("Moving:" + activeMove);
                String result = activeMove.makeMove(game);
                if (!result.isEmpty() && !result.equals("ONTO_SELF")) {
                    JOptionPane.showMessageDialog(this, result);
                }
            }
        }
    }

    private void processMoveToTopRow(MouseEvent e) {
        int col = findCol(e.getX());
        activeMove.cardReleased(col, true);
        String result = activeMove.makeMove(game);
        if (!result.isEmpty() && !result.equals("ONTO_SELF")) {
            JOptionPane.showMessageDialog(this, result);
        }
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
