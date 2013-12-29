package game;

import gui.CardPanel;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import static game.CardMoveResult.*;

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
                    board.get(j).add(cardList.remove(0));
                    panel.repaint();
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                for (int j = 7; j >= 0; j--) {
                    board.get(j).add(cardList.remove(0));
                    panel.repaint();
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

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

    public CardMoveResult moveCardOntoCard(Card moving, Card destination, int destRow, int moveRow) {
        if (moving.getRank().ordinal() == destination.getRank().ordinal() + 1) {
            if (moving.getSuit() != destination.getSuit()) {
                //Valid move
                board.get(destRow).add(moving);
                board.get(moveRow).remove(board.get(moveRow).size() - 1);
                return FINE;

            } else {
                return WRONG_SUIT;
            }
        } else {
            return WRONG_NUM;
        }

    }
}
