package game;

/**
 * Created by Andrew on 28/12/13.
 */
public class Card {
    public Suit getSuit() {
        return suit;
    }

    public Rank getRank() {
        return rank;
    }

    private final Suit suit;
    private final Rank rank;

    public Card(Suit suit, Rank rank) {

        this.suit = suit;
        this.rank = rank;
    }

    public enum Suit {
        SPADES, CLUBS, DIAMONDS, HEARTS;
    }

    public enum Rank {
        ACE,TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING;
    }

    public String toString() {
        return rank.name() + suit.name();
    }

    public String shortToString(){
        return "" + rank.name().charAt(0) + suit.name().charAt(0);
    }
}
