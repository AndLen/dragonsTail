package game;

import gui.CardFrame;
import gui.CardPanel;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Andrew on 28/12/13.
 */
public class CardGame {
    //Actually uses copy on write arraylists to prevent concurrent modification from threading
    //(swing vs logic)
    private final List<List<Card>> board;
    private final Queue<Card> deck;
    private final List<List<Card>> topRow;
    private final CardFrame cardFrame;
    private final Stack<CardMove> history;
    private long startTime;

    public CardGame(CardFrame cardFrame) {
        this.cardFrame = cardFrame;
        board = new CopyOnWriteArrayList<List<Card>>();
        deck = new LinkedList<Card>();
        topRow = new CopyOnWriteArrayList<List<Card>>();
        history = new Stack<CardMove>();
    }

    public void dealGame(CardPanel panel) {
        while (!panel.isReady()) {
            try {
                //Wait for graphics to render once before we deal.
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //Ensures we get to see the deck being dealt
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        startTime = System.currentTimeMillis();
        for (int i = 0; i < 8; i++) {
            topRow.add(new CopyOnWriteArrayList<Card>());

        }

        List<Card> cardList = new ArrayList<Card>();
        makePack(cardList);
        makePack(cardList);
        Collections.shuffle(cardList);

        //Make 8 piles
        for (int i = 0; i < 8; i++) {
            board.add(new CopyOnWriteArrayList<Card>());
        }
        //Deal to 8 piles
        for (int i = 0; i < 8; i++) {
            if (i % 2 == 0) {
                for (int j = 0; j < 8; j++) {
                    Card toAdd = cardList.remove(0);
                    board.get(j).add(toAdd);
                    panel.repaint();
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                for (int j = 7; j >= 0; j--) {
                    Card toAdd = cardList.remove(0);
                    board.get(j).add(toAdd);
                    panel.repaint();
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        deck.addAll(cardList);
    }

    private void makePack(List<Card> cardList) {
        for (Card.Suit suit : Card.Suit.values()) {
            for (Card.Rank rank : Card.Rank.values()) {
                cardList.add(new Card(suit, rank));
            }
        }
    }

    public List<List<Card>> getBoard() {
        return Collections.unmodifiableList(board);
    }

    //Should combine this and below method

    public String moveCardOntoCardFromDeck(int boardIndexTo) {
        if (boardIndexTo == 0) {
            //Moving onto dragon's tail, anything goes.
            board.get(0).add(deck.poll());
            return "";
        } else {
            String result = moveCardOntoCard(Arrays.asList(deck.peek()), boardIndexTo, 0);
            if (result.isEmpty()) {
                deck.poll();
            }
            return result;
        }
    }

    public String moveCardOntoCardFromTopRow(int boardIndexFrom, int boardIndexTo) {
        List<Card> from = topRow.get(boardIndexFrom);
        String result = moveCardOntoCard(from, boardIndexTo, from.size() - 1);
        if (result.isEmpty()) {
            from.remove(from.size() - 1);
        }
        return result;
    }

    public String moveCardOntoCardFromBoard(int toMoveTop, int boardIndexFrom, int boardIndexTo) {
        List<Card> from = board.get(boardIndexFrom);
        String result = moveCardOntoCard(from, boardIndexTo, toMoveTop);
        if (result.isEmpty()) {
            while (from.size() > toMoveTop) {
                from.remove(toMoveTop);
            }
        }
        return result;
    }

    private String moveCardOntoCard(List<Card> from, int boardIndexTo, int listIndexFrom) {
        List<Card> to = board.get(boardIndexTo);
        Card firstToMove = from.get(listIndexFrom);
        System.out.println("TO: " + to.toString());
        if (to.isEmpty()) {
            //Means we can move anything there.
            for (int i = listIndexFrom; i < from.size(); i++) {
                to.add(from.get(i));
            }
            return "";
        }
        Card lastInRow = to.get(to.size() - 1);

        if (firstToMove == lastInRow) {
            //Don't want to move onto self, nor display warning
            return "ONTO_SELF";
        }
        if (firstToMove.getRank().ordinal() == lastInRow.getRank().ordinal() - 1) {
            if (listIndexFrom < from.size() - 1) {
                //Needs to match suit with one it's landing on too
                if (firstToMove.getSuit() == lastInRow.getSuit()) {
                    for (int i = listIndexFrom; i < from.size(); i++) {
                        to.add(from.get(i));
                    }
                    return "";
                } else {
                    return "Can only move onto the same suit when moving multiple cards.";
                }

            } else {
                if (suitMoveIsValid(firstToMove.getSuit(), lastInRow.getSuit())) {
                    //Valid move
                    to.add(from.get(listIndexFrom));
                    return "";

                } else {
                    return "You can only move onto a card of the same suit or different colour.";
                }
            }
        } else {
            return firstToMove.getRank().name() + " is not one lower than " + lastInRow.getRank().name();
        }


    }

    private boolean suitMoveIsValid(Card.Suit from, Card.Suit to) {
        switch (to) {
            case HEARTS:
                return from != Card.Suit.DIAMONDS;
            case DIAMONDS:
                return from != Card.Suit.HEARTS;
            case CLUBS:
                return from != Card.Suit.SPADES;
            case SPADES:
                return from != Card.Suit.CLUBS;
            default:
                return false;
        }
    }

    public boolean isValidNewMove(int col, int index) {
        List<Card> cardList = board.get(col);
        for (int i = index + 1; i < cardList.size(); i++) {
            Card prev = cardList.get(i - 1);
            Card toCheck = cardList.get(i);
            if (toCheck.getSuit().ordinal() != prev.getSuit().ordinal() || toCheck.getRank().ordinal() != prev.getRank().ordinal() - 1) {
                return false;
            }
        }
        return true;
    }

    public List<List<Card>> getTopRow() {
        return Collections.unmodifiableList(topRow);
    }

    public String moveCardOntoTopRowFromDeck(int boardIndexTo) {
        String result = moveCardOntoTopRow(Arrays.asList(deck.peek()), boardIndexTo, 0);
        if (result.isEmpty()) {
            deck.poll();
        }
        return result;
    }

    public String moveCardOntoTopRowFromRow(int boardIndexFrom, int boardIndexTo) {
        List<Card> from = topRow.get(boardIndexFrom);
        String result = moveCardOntoTopRow(from, boardIndexTo, from.size()-1);
        if (result.isEmpty()) {
            from.remove(from.size() - 1);
        }
        return result;

    }

    public String moveCardOntoTopRowFromBoard(int toMoveTop, int boardIndexFrom, int boardIndexTo) {
        List<Card> from = board.get(boardIndexFrom);
        String result = moveCardOntoTopRow(from, boardIndexTo, toMoveTop);
        if (result.isEmpty()) {
            from.remove(from.size() - 1);
        }
        return result;
    }

    private String moveCardOntoTopRow(List<Card> from, int boardIndexTo, int listIndexFrom) {

        List<Card> pile = topRow.get(boardIndexTo);
        Card fromTop = from.get(listIndexFrom);
        if (pile.size() == 0) {
            if (fromTop.getRank() == Card.Rank.ACE) {
                pile.add(from.get(listIndexFrom));
                return "";
            } else {
                return "Can only move an Ace to an empty pile.";
            }
        } else {
            Card pileTop = pile.get(pile.size() - 1);
            //Want reference equality
            if (pileTop == fromTop) {
                //Don't want to move onto self, nor display warning
                return "ONTO_SELF";
            }
            if (listIndexFrom != from.size() - 1) {
                return "Can only move the bottom card to the top row.";
            }
            if (fromTop.getSuit().ordinal() == pileTop.getSuit().ordinal()) {
                if (fromTop.getRank().ordinal() == pileTop.getRank().ordinal() + 1) {
                    pile.add(from.get(listIndexFrom));
                    return "";
                } else {
                    return fromTop.getRank().name() + " is not one higher than " + pileTop.getRank().name();
                }

            } else {
                return "Can only move to a pile of the same suit";
            }
        }
    }

    public Queue<Card> getDeck() {
        return deck;
    }

    public boolean hasWon() {

        for (List<Card> pile : topRow) {
            if (pile.size() != 13) {
                return false;
            }
        }
        return true;
    }

    public void restart() {
        cardFrame.restartGame();
    }

    public Stack<CardMove> getHistory() {
        return history;
    }

    public void undo() {
        System.out.println(history);
        if (!history.isEmpty()) {
            CardMove toUndo = history.pop();
            toUndo.undo(this);

        }

    }

    public long getStartTime() {
        return startTime;
    }

    public int getNumMoves() {
        //Handy
        return history.size();
    }
}
