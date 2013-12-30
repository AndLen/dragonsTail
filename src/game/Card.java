package game;

/**
 * Created by Andrew on 28/12/13.
 */
public class Card {
    private final Suit suit;
    private final Rank rank;

    public Card(Suit suit, Rank rank) {

        this.suit = suit;
        this.rank = rank;
    }

    public Suit getSuit() {
        return suit;
    }

    public Rank getRank() {
        return rank;
    }

    public String toString() {
        return rank.name() + suit.name();
    }

    public String shortToString() {
        return "" + rank.name().charAt(0) + suit.name().charAt(0);
    }

    public boolean matchesAndOneAbove(Card other) {
        return (other.getSuit() == this.suit && this.rank.ordinal() == other.getRank().ordinal() + 1);
    }

    public enum Suit {
        SPADES, CLUBS, DIAMONDS, HEARTS;
    }

    public enum Rank {
        ACE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING;
    }
}
