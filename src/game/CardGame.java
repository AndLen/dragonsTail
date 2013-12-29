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
    private final Queue<Card> hand;

    public CardGame() {
        board = new CopyOnWriteArrayList<List<Card>>();
        hand = new LinkedList<Card>();
    }

    public void dealGame(CardPanel panel) {
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
                        Thread.sleep(100);
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
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };

        }
        hand.addAll(cardList);
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
            if (firstMoved.getSuit() != lastInRow.getSuit()) {
                //Valid move
                while (toMoveTop < from.size()) {
                    to.add(from.remove(toMoveTop));
                }
                return "";

            } else {
                return "You can  only move onto a card of the same suit or different colour.";
            }
        } else {
            return firstMoved.getRank().name() + " is not one higher than " + lastInRow.getRank().name();
        }
    }
}
