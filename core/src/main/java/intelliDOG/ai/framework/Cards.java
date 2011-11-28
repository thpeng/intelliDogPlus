package intelliDOG.ai.framework;

import java.util.HashMap;

/**
 * Abstract class Cards, holding all card's values
 */
public abstract class Cards {

	public static final int NO_CARD = -1;
	public static final int JOKER = 100;

	public static final int HEARTS_ACE = 1;
	public static final int HEARTS_TWO = 2;
	public static final int HEARTS_THREE = 3;
	public static final int HEARTS_FOUR = 4;
	public static final int HEARTS_FIVE = 5;
	public static final int HEARTS_SIX = 6;
	public static final int HEARTS_SEVEN = 7;
	public static final int HEARTS_EIGHT = 8;
	public static final int HEARTS_NINE = 9;
	public static final int HEARTS_TEN = 10;
	public static final int HEARTS_JACK = 11;
	public static final int HEARTS_QUEEN = 12;
	public static final int HEARTS_KING = 13;
	
	public static final int SPADES_ACE = 14;
	public static final int SPADES_TWO = 15;
	public static final int SPADES_THREE = 16;
	public static final int SPADES_FOUR = 17;
	public static final int SPADES_FIVE = 18;
	public static final int SPADES_SIX = 19;
	public static final int SPADES_SEVEN = 20;
	public static final int SPADES_EIGHT = 21;
	public static final int SPADES_NINE = 22;
	public static final int SPADES_TEN = 23;
	public static final int SPADES_JACK = 24;
	public static final int SPADES_QUEEN = 25;
	public static final int SPADES_KING = 26;
	
	public static final int DIAMONDS_ACE = 27;
	public static final int DIAMONDS_TWO = 28;
	public static final int DIAMONDS_THREE = 29;
	public static final int DIAMONDS_FOUR = 30;
	public static final int DIAMONDS_FIVE = 31;
	public static final int DIAMONDS_SIX = 32;
	public static final int DIAMONDS_SEVEN = 33;
	public static final int DIAMONDS_EIGHT = 34;
	public static final int DIAMONDS_NINE = 35;
	public static final int DIAMONDS_TEN = 36;
	public static final int DIAMONDS_JACK = 37;
	public static final int DIAMONDS_QUEEN = 38;
	public static final int DIAMONDS_KING = 39;
	
	public static final int CLUBS_ACE = 40;
	public static final int CLUBS_TWO = 41;
	public static final int CLUBS_THREE = 42;
	public static final int CLUBS_FOUR = 43;
	public static final int CLUBS_FIVE = 44;
	public static final int CLUBS_SIX = 45;
	public static final int CLUBS_SEVEN = 46;
	public static final int CLUBS_EIGHT = 47;
	public static final int CLUBS_NINE = 48;
	public static final int CLUBS_TEN = 49;
	public static final int CLUBS_JACK = 50;
	public static final int CLUBS_QUEEN = 51;
	public static final int CLUBS_KING = 52;
	
	public static final int JOKER_AS_ACE = 53;
	public static final int JOKER_AS_TWO = 54;
	public static final int JOKER_AS_THREE = 55;
	public static final int JOKER_AS_FOUR = 56;
	public static final int JOKER_AS_FIVE = 57;
	public static final int JOKER_AS_SIX = 58;
	public static final int JOKER_AS_SEVEN = 59;
	public static final int JOKER_AS_EIGHT = 60;
	public static final int JOKER_AS_NINE = 61;
	public static final int JOKER_AS_TEN = 62;
	public static final int JOKER_AS_JACK = 63;
	public static final int JOKER_AS_QUEEN = 64;
	public static final int JOKER_AS_KING = 65;
	
	public static final HashMap<Integer, String> CARDNAMES = new HashMap<Integer, String>();
	
	
	static{
		CARDNAMES.put(NO_CARD, "no card");
		CARDNAMES.put(JOKER, "Joker");
		CARDNAMES.put(HEARTS_ACE, "Hearts Ace");
		CARDNAMES.put(HEARTS_TWO, "Hearts Two");
		CARDNAMES.put(HEARTS_THREE, "Hearts Three");
		CARDNAMES.put(HEARTS_FOUR, "Hearts Four");
		CARDNAMES.put(HEARTS_FIVE, "Hearts Five");
		CARDNAMES.put(HEARTS_SIX, "Hearts Six");
		CARDNAMES.put(HEARTS_SEVEN, "Hearts Seven");
		CARDNAMES.put(HEARTS_EIGHT, "Hearts Eight");
		CARDNAMES.put(HEARTS_NINE, "Hearts Nine");
		CARDNAMES.put(HEARTS_TEN, "Hearts Ten");
		CARDNAMES.put(HEARTS_JACK, "Hearts Jack");
		CARDNAMES.put(HEARTS_QUEEN, "Hearts Queen");
		CARDNAMES.put(HEARTS_KING, "Hearts King");
		CARDNAMES.put(SPADES_ACE, "Spades Ace");
		CARDNAMES.put(SPADES_TWO, "Spades Two");
		CARDNAMES.put(SPADES_THREE, "Spades Three");
		CARDNAMES.put(SPADES_FOUR, "Spades Four");
		CARDNAMES.put(SPADES_FIVE, "Spades Five");
		CARDNAMES.put(SPADES_SIX, "Spades Six");
		CARDNAMES.put(SPADES_SEVEN, "Spades Seven");
		CARDNAMES.put(SPADES_EIGHT, "Spades Eight");
		CARDNAMES.put(SPADES_NINE, "Spades Nine");
		CARDNAMES.put(SPADES_TEN, "Spades Ten");
		CARDNAMES.put(SPADES_JACK, "Spades Jack");
		CARDNAMES.put(SPADES_QUEEN, "Spades Queen");
		CARDNAMES.put(SPADES_KING, "Spades King");
		CARDNAMES.put(DIAMONDS_ACE, "Diamonds Ace");
		CARDNAMES.put(DIAMONDS_TWO, "Diamonds Two");
		CARDNAMES.put(DIAMONDS_THREE, "Diamonds Three");
		CARDNAMES.put(DIAMONDS_FOUR, "Diamonds Four");
		CARDNAMES.put(DIAMONDS_FIVE, "Diamonds Five");
		CARDNAMES.put(DIAMONDS_SIX, "Diamonds Six");
		CARDNAMES.put(DIAMONDS_SEVEN, "Diamonds Seven");
		CARDNAMES.put(DIAMONDS_EIGHT, "Diamonds Eight");
		CARDNAMES.put(DIAMONDS_NINE, "Diamonds Nine");
		CARDNAMES.put(DIAMONDS_TEN, "Diamonds Ten");
		CARDNAMES.put(DIAMONDS_JACK, "Diamonds Jack");
		CARDNAMES.put(DIAMONDS_QUEEN, "Diamonds Queen");
		CARDNAMES.put(DIAMONDS_KING, "Diamonds King");
		CARDNAMES.put(CLUBS_ACE, "Clubs Ace");
		CARDNAMES.put(CLUBS_TWO, "Clubs Two");
		CARDNAMES.put(CLUBS_THREE, "Clubs Three");
		CARDNAMES.put(CLUBS_FOUR, "Clubs Four");
		CARDNAMES.put(CLUBS_FIVE, "Clubs Five");
		CARDNAMES.put(CLUBS_SIX, "Clubs Six");
		CARDNAMES.put(CLUBS_SEVEN, "Clubs Seven");
		CARDNAMES.put(CLUBS_EIGHT, "Clubs Eight");
		CARDNAMES.put(CLUBS_NINE, "Clubs Nine");
		CARDNAMES.put(CLUBS_TEN, "Clubs Ten");
		CARDNAMES.put(CLUBS_JACK, "Clubs Jack");
		CARDNAMES.put(CLUBS_QUEEN, "Clubs Queen");
		CARDNAMES.put(CLUBS_KING, "Clubs King");
		CARDNAMES.put(JOKER_AS_ACE, "Joker as Ace");
		CARDNAMES.put(JOKER_AS_TWO, "Joker as Two");
		CARDNAMES.put(JOKER_AS_THREE, "Joker as Three");
		CARDNAMES.put(JOKER_AS_FOUR, "Joker as Four");
		CARDNAMES.put(JOKER_AS_FIVE, "Joker as Five");
		CARDNAMES.put(JOKER_AS_SIX, "Joker as Six");
		CARDNAMES.put(JOKER_AS_SEVEN, "Joker as Seven");
		CARDNAMES.put(JOKER_AS_EIGHT, "Joker as Eight");
		CARDNAMES.put(JOKER_AS_NINE, "Joker as Nine");
		CARDNAMES.put(JOKER_AS_TEN, "Joker as Ten");
		CARDNAMES.put(JOKER_AS_JACK, "Joker as Jack");
		CARDNAMES.put(JOKER_AS_QUEEN, "Joker as Queen");
		CARDNAMES.put(JOKER_AS_KING, "Joker as King");

	}
	
}
