package gui;

import game.Card;
import game.CardGame;
import game.CardMove;
import game.StorageManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
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
    private static double CARD_X_GAP;
    private static double CARD_Y_NO_OVERLAP;
    private static double Y_BOARD_OFFSET;
    private static double X_BOARD_OFFSET;
    private static double DECK_Y;
    private static String ERROR;
    private final CardGame game;
    private final Object lock = new Object();
    //Moving cards
    private CardMove activeMove = null;
    private int activeX = -1;
    private int activeY = -1;
    private boolean successfulPaint = false;
    private BufferedImage lastImage;

    public CardPanel(CardGame game) {
        this.game = game;
        this.setPreferredSize(new Dimension(1000, 1000));
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.addComponentListener(this);
        //Set up initial constants
        componentResized(null);
    }

    public void paint(Graphics gOriginal) {
        //Double buffering and caching for dragging
        lastImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = lastImage.createGraphics();
        g.setColor(BACKGROUND_GREEN);
        g.fillRect(0, 0, this.getWidth(), this.getHeight());

        renderTopRow(g);
        final List<List<Card>> gameBoard = game.getBoard();
        renderBoard(g, gameBoard);
        //Lets render this in the dragged event instead to lower load
        //renderDragCards(g, gameBoard);
        renderDeck(g);
        renderError(g);
        successfulPaint = true;
        gOriginal.drawImage(lastImage, 0, 0, null);
        g.dispose();
    }

    private void renderError(Graphics2D g) {
        if (ERROR != null) {
            g.setFont(new Font("TimesRoman", Font.PLAIN, 30));
            FontMetrics fontMetrics = g.getFontMetrics();
            float xLeft = (float) (CARD_WIDTH + CARD_X_GAP);

            float x = (getWidth() - xLeft - fontMetrics.stringWidth(ERROR)) / 2;
            x += xLeft;
            float y = (float) (DECK_Y + CARD_HEIGHT / 2);
            g.setColor(Color.RED);
            g.drawString(ERROR, x, y);
        }
    }

    private void renderDeck(Graphics2D g) {
        Queue<Card> deck = game.getDeck();
        Card topCard = deck.size() == 0 ? null : deck.peek();

        double x = X_BOARD_OFFSET;
        double y = DECK_Y;

        g.setColor(Color.white);
        g.drawString(deck.size() + " cards in deck", (float) x, (float) y - 1);

        if (topCard != null && topCard.isHidden()) {
            topCard = null;
        }
        renderCard(topCard, g, x, y);
    }

    private void renderDragCards(Graphics2D g, List<List<Card>> gameBoard) {
        if (activeMove != null && activeX != -1 && activeY != -1) {
            if (activeMove.getMoveType() == CardMove.MOVE_TYPE.FROM_DECK) {
                renderCard(game.getDeck().peek(), g, activeX - (CARD_WIDTH / 2), activeY);
            } else if (activeMove.getMoveType() == CardMove.MOVE_TYPE.FROM_PILE) {
                int index = activeMove.getBoardIndexFrom();
                List<Card> pile = game.getTopRow().get(index);
                renderCard(pile.get(pile.size() - 1), g, activeX - (CARD_WIDTH / 2), activeY);
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

        if (gameBoard.size() == 0) {
            //Game not inited yet
            return;
        }
        /**Render the dragon's tail - special case due to size **/
        List<Card> dragonsTail = gameBoard.get(0);
        int indexToStartFrom = indexToRenderFrom(dragonsTail, true);

        //Render cards we can show
        for (int i = indexToStartFrom; i < dragonsTail.size(); i++) {
            Card toRender = dragonsTail.get(i);
            if (!toRender.isHidden()) {
                renderCard(toRender, g, x, y);
            }
            y += CARD_Y_NO_OVERLAP;
        }
        if (indexToStartFrom > 0) {
            g.setColor(Color.white);
            g.drawString(indexToStartFrom + (indexToStartFrom == 1 ? " card hidden" : " cards hidden"), (float) X_BOARD_OFFSET, (float) Y_BOARD_OFFSET - 1);
        }
        y = Y_BOARD_OFFSET;
        x += CARD_WIDTH + CARD_X_GAP;
        /** Done rendering dragon's tail**/

        //Render other cards
        for (int i = 1; i < gameBoard.size(); i++) {
            List<Card> cards = gameBoard.get(i);
            int indexToRenderFrom = indexToRenderFrom(cards, false);
            for (int j = indexToRenderFrom; j < cards.size(); j++) {
                Card toRender = cards.get(j);
                if (!toRender.isHidden()) {
                    renderCard(cards.get(j), g, x, y);
                }
                y += CARD_Y_NO_OVERLAP;
            }
            if (indexToRenderFrom > 0) {
                g.setColor(Color.white);
                g.drawString(indexToRenderFrom + (indexToRenderFrom == 1 ? " card hidden" : " cards hidden"), (float) x, (float) Y_BOARD_OFFSET - 1);
            }
            y = Y_BOARD_OFFSET;
            x += CARD_WIDTH + CARD_X_GAP;
        }
    }

    private int indexToRenderFrom(List<Card> cards, boolean dragonsTail) {
        double maxSize = dragonsTail ? DECK_Y : getHeight();
        //Required as when game is started, maxSize = 0..otherwise doesn't render..odd?
        maxSize = maxSize > 10 ? maxSize - 10 : 10;
        double cardsYSize = Y_BOARD_OFFSET + (cards.size() * CARD_Y_NO_OVERLAP) + (CARD_HEIGHT - CARD_Y_NO_OVERLAP);
        int indexToStartFrom = 0;
        while (cardsYSize > maxSize) {
            cardsYSize -= CARD_Y_NO_OVERLAP;
            indexToStartFrom++;
        }
        return indexToStartFrom;
    }

    private void renderTopRow(Graphics2D g) {
        List<List<Card>> topRow = game.getTopRow();
        double x = X_BOARD_OFFSET;
        double y = CARD_Y_NO_OVERLAP;
        for (List<Card> cards : topRow) {
            if (cards.size() > 0) {
                Card toRender = cards.get(cards.size() - 1);
                //If top of pile isn't hidden, render that
                if (!toRender.isHidden()) {
                    renderCard(toRender, g, x, y);

                }//Otherwise, render the 2nd highest if it exists
                else if (cards.size() > 1) {
                    renderCard(cards.get(cards.size() - 2), g, x, y);
                }//Only one card in pile, so render empty.
                else {
                    renderCard(null, g, x, y);
                }

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

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        ImageManager.renderSVG(card, g);

        //Revert changes.
        g.setTransform(oldTransform);
    }

    @Override
    public void componentResized(ComponentEvent e) {
        double screenWidth = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        CARD_X_GAP = getWidth() / 64;
        CARD_WIDTH = (getWidth() - CARD_X_GAP) / 9;
        CARD_WIDTH = Math.min(CARD_WIDTH, screenWidth * 0.065);

        X_BOARD_OFFSET = (getWidth() - (CARD_WIDTH * 8) - (CARD_X_GAP * 7)) / 2;

        CARD_HEIGHT = CARD_WIDTH / (CARD_IMAGE_WIDTH / CARD_IMAGE_HEIGHT);
        CARD_Y_NO_OVERLAP = CARD_HEIGHT / 4;
        Y_BOARD_OFFSET = CARD_Y_NO_OVERLAP * 2 + CARD_HEIGHT;
        DECK_Y = getHeight() - CARD_Y_NO_OVERLAP - CARD_HEIGHT;
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
        try {
            if (e.getX() < X_BOARD_OFFSET || e.getX() > (getWidth() - X_BOARD_OFFSET)) {
                //Clicked off the board, nothing to do with us.
                return;
            }
            System.out.println(System.currentTimeMillis() - LAST_PRESS);
            //Was 200
            if ((System.currentTimeMillis() - LAST_PRESS) < 400) {
                NUMBER_CLICKS++;
            } else {
                NUMBER_CLICKS = 0;
            }
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
        } finally {
            this.repaint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        //Don't want multiple threads in here really
        synchronized (lock) {
            try {
                if (e.getX() < X_BOARD_OFFSET || e.getX() > (getWidth() - X_BOARD_OFFSET)) {
                    //Clicked off the board, nothing to do with us.
                    if (activeMove != null) activeMove.unhideCards(game);
                    activeMove = null;
                    return;
                }

                if (NUMBER_CLICKS == 1) {
                    doubleClick(e);
                    NUMBER_CLICKS = 0;
                } else if (clickedOnDeck(e)) {
                    //Do nothing

                } else if (e.getY() < Y_BOARD_OFFSET) {
                    processMoveToTopRow(e);
                } else {
                    processMoveToBoard(e);
                }
                if (activeMove != null) activeMove.unhideCards(game);
                activeMove = null;
            } finally {
                this.repaint();
            }
            if (game.hasWon()) {
                int elapsedTime = (int) (System.currentTimeMillis() - game.getStartTime());
                StorageManager.win(elapsedTime, game.getNumMoves());
                String[] options = new String[]{"Play Again", "Quit"};
                CardFrame.showStats();
                int result = JOptionPane.showOptionDialog(this, "Congratulations! You have won.\n Time: " + elapsedTime + " s\nMoves: " + game.getNumMoves(), "Win!", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                if (result == JOptionPane.YES_OPTION) {
                    game.restart();

                } else {
                    System.exit(0);
                }
            }
        }
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
                activeMove = new CardMove(index, col, game);
            } else {
                storeError("Cannot move that card, cards beneath it are not the same suit or correctly ordered");
            }
        }
    }

    private void processMoveFromDeck() {
        //Can assume did click on the deck.
        final Queue<Card> deck = game.getDeck();
        if (!deck.isEmpty()) {
            activeMove = new CardMove(game);
        }
    }

    private void processMoveFromTopRow(MouseEvent e) {
        int col = findCol(e.getX());
        activeMove = new CardMove(col, game);
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

        //Compensate for hidden cards
        index += indexToRenderFrom(cards, isDragonsTail);

        if (index >= cards.size()) {
            index = cards.size() - 1;
        }

        System.out.println("Index: " + index);
        return index;
    }

    private boolean clickedOnDeck(MouseEvent e) {
        return (findCol(e.getX()) == 0 && e.getY() >= DECK_Y);
    }

    private void doubleClick(MouseEvent e) {
        System.out.println("DOUBLE CLICK");
        if (clickedOnDeck(e)) {
            if (!game.getDeck().isEmpty()) {
                Card toSendUp = game.getDeck().peek();
                int pileIndex = pileIndexToSendTo(toSendUp);
                if (pileIndex != -1) {
                    CardMove toMove = new CardMove(game);
                    toMove.cardReleased(pileIndex, true);
                    String result = toMove.makeMove(game);
                    processMoveResult(result, toMove);
                } else {
                    //Send to dragon's tail
                    CardMove toMove = new CardMove(game);
                    toMove.cardReleased(0, false);
                    String result = toMove.makeMove(game);
                    processMoveResult(result, toMove);
                }
            }
        } else if (e.getY() > Y_BOARD_OFFSET) {
            int col = findCol(e.getX());
            List<Card> cardList = game.getBoard().get(col);
            Card toSendUp = cardList.get(cardList.size() - 1);
            int pileIndex = pileIndexToSendTo(toSendUp);
            System.out.println("sending to pile " + pileIndex);
            if (pileIndex != -1) {
                CardMove toMove = new CardMove(cardList.size() - 1, col, game);
                toMove.cardReleased(pileIndex, true);
                String result = toMove.makeMove(game);
                processMoveResult(result, toMove);
            } else {
                storeError("Cannot send that to the top");
            }
        }
    }

    private int pileIndexToSendTo(Card toSendUp) {
        List<List<Card>> topRow = game.getTopRow();
        for (int i = 0; i < topRow.size(); i++) {
            List<Card> pile = topRow.get(i);
            if (toSendUp.getRank() == Card.Rank.ACE) {
                //find empty
                if (pile.isEmpty()) {
                    return i;
                }
            } else if (!pile.isEmpty()) {
                Card topOfPile = pile.get(pile.size() - 1);
                if (toSendUp.matchesAndOneAbove(topOfPile)) {
                    return i;
                }
            }
        }
        return -1;
    }

    private void processMoveToBoard(MouseEvent e) {
        int col = findCol(e.getX());
        List<Card> possibleCards = game.getBoard().get(col);

        if (activeMove != null) {
            activeMove.cardReleased(col, false);
            System.out.println("Moving:" + activeMove);
            String result = activeMove.makeMove(game);
            processMoveResult(result, activeMove);
        }

    }

    private void processMoveResult(String result, CardMove move) {
        if (result.isEmpty()) {
            game.getHistory().push(move);
        } else if (!result.equals("ONTO_SELF")) {
            storeError(result);
        }
    }

    private void processMoveToTopRow(MouseEvent e) {
        if (activeMove != null) {
            int col = findCol(e.getX());
            activeMove.cardReleased(col, true);
            String result = activeMove.makeMove(game);
            processMoveResult(result, activeMove);
        }
    }

    private void storeError(final String error) {
        //Render and disappear it
        new Thread() {
            public void run() {
                ERROR = error;
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (error.equals(ERROR)) {
                    ERROR = null;
                    CardPanel.this.repaint();
                }
            }
        }.start();
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
        BufferedImage copyImage = new BufferedImage(lastImage.getWidth(),lastImage.getHeight(),BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = copyImage.createGraphics();
        g.drawImage(lastImage,0,0,null);
        renderDragCards(g, game.getBoard());
        Graphics panelGraphics = getGraphics();
        panelGraphics.drawImage(copyImage,0,0,null);
        panelGraphics.dispose();
        g.dispose();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    public boolean isReady() {
        return successfulPaint;
    }
}
