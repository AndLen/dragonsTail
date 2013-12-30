package game;

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

    public CardGame() {
        board = new CopyOnWriteArrayList<List<Card>>();
        deck = new LinkedList<Card>();
        topRow = new CopyOnWriteArrayList<List<Card>>();
    }

    public void dealGame(CardPanel panel) {
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
                    System.out.print(toAdd + " ");
                    board.get(j).add(toAdd);
                    panel.repaint();
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                for (int j = 7; j >= 0; j--) {
                    Card toAdd = cardList.remove(0);
                    System.out.print(toAdd + " ");
                    board.get(j).add(toAdd);
                    panel.repaint();
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            ;

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

    public String moveCardOntoCard(int toMoveTop, int boardIndexFrom, int boardIndexTo) {
        List<Card> from = board.get(boardIndexFrom);
        List<Card> to = board.get(boardIndexTo);
        Card firstMoved = from.get(toMoveTop);
        Card lastInRow = to.get(to.size() - 1);
        if (firstMoved.getRank().ordinal() == lastInRow.getRank().ordinal() - 1) {
            if (suitMoveIsValid(firstMoved.getSuit(), lastInRow.getSuit())) {
                //Valid move
                while (toMoveTop < from.size()) {
                    to.add(from.remove(toMoveTop));
                }
                return "";

            } else {
                return "You can only move onto a card of the same suit or different colour.";
            }
        } else {
            return firstMoved.getRank().name() + " is not one lower than " + lastInRow.getRank().name();
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

    public String moveCardOntoTopRow(int toMoveTop, int boardIndexFrom, int boardIndexTo) {
        List<Card> pile = topRow.get(boardIndexTo);
        List<Card> from = board.get(boardIndexFrom);
        Card fromTop = from.get(toMoveTop);
        if (pile.size() == 0) {
            if (fromTop.getRank() == Card.Rank.ACE) {
                pile.add(from.remove(toMoveTop));
            } else {
                return "Can only move an Ace to an empty pile.";
            }
        } else {
            Card pileTop = pile.get(pile.size() - 1);


            if (toMoveTop != from.size() - 1) {
                return "Can only move the bottom card to the top row.";
            }
            if (fromTop.getSuit().ordinal() == pileTop.getSuit().ordinal()) {
                if (fromTop.getRank().ordinal() == pileTop.getRank().ordinal() + 1) {
                    pile.add(from.remove(toMoveTop));
                } else {
                    return fromTop.getRank().name() + " is not one higher than " + pileTop.getRank().name();
                }

            } else {
                return "Can only move to a pile of the same suit";
            }
        }
        return "";
    }

    public Queue<Card> getDeck() {
        return deck;
    }
}
