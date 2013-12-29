package game;

import java.util.*;

import static game.CardMoveResult.*;

/**
 * Created by Andrew on 28/12/13.
 */
public class CardGame {
    private final List<List<Card>> board;
    private final Queue<Card> hand;

    public CardGame() {
        List<Card> cardList = new ArrayList<Card>();
        makePack(cardList);
        makePack(cardList);
        Collections.shuffle(cardList);

        board = new ArrayList<List<Card>>();
        //Make 8 piles
        for (int i = 0; i < 8; i++) {
            board.add(new ArrayList<Card>());
        }
        //Deal to 8 piles
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board.get(j).add(cardList.remove(0));
            }
        }
        hand = new LinkedList<Card>();
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
