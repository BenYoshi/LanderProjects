package pkg364gameofwar;

import java.io.Serializable;

/**
 * @author Benny Ioschikhes
 */
public class Card implements Comparable<Card>, Serializable{

    enum Rank implements Serializable {

        TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN,
        JACK, QUEEN, KING, ACE;

        @Override
        public String toString() {
            String name = "";
            if (ordinal() == 0) {
                name = "A";
            } else if (ordinal() < 11) {
                name = ordinal() + 1 + "";
            } else if (ordinal() == 11) {
                name = "J";
            } else if (ordinal() == 12) {
                name = "Q";
            } else if (ordinal() == 12) {
                name = "K";
            }

            return name;
        }

    }

    enum Suit implements Serializable {

        CLUBS, DIAMONDS, HEARTS, SPADES;

        @Override
        public String toString() {
            String name = "";

            switch (ordinal()) {
                case 0:
                    name = "♣";
                    break;
                case 1:
                    name = "♠";
                    break;
                case 2:
                    name = "♦";
                    break;
                case 3:
                    name = "♥";
                    break;
            }

            return name;
        }

    }

    final Rank rank;
    final Suit suit;

    private Card(Rank rank, Suit suit) {
        this.rank = rank;
        this.suit = suit;
    }

    @Override
    public String toString() {
        return rank + " of " + suit;
    }

    public static Card[] createDeck() {
        Card[] deck = new Card[52];
        int i = 0;

        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                deck[i++] = new Card(rank, suit);
            }
        }

        return deck;
    }

    @Override
    public int compareTo(Card that) {
        return rank.ordinal() - that.rank.ordinal();
    }
}
