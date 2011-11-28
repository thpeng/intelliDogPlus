package intelliDOG.ai.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import intelliDOG.ai.framework.Cards;
import intelliDOG.ai.framework.InformationGatherer;
import intelliDOG.ai.framework.Players;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test for InformationGatherer
 *
 */
public class InformationGathererTest {

	private InformationGatherer iGatherer = null;
	
	@Before
	public void setUp() throws Exception {
		iGatherer = new InformationGatherer(Players.P1); 
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSetUsedCards_ACE() {
		
		iGatherer.setUsedCards(Cards.HEARTS_ACE);
		iGatherer.setUsedCards(Cards.CLUBS_ACE);
		iGatherer.setUsedCards(Cards.DIAMONDS_ACE);
		iGatherer.setUsedCards(Cards.SPADES_ACE);
		
		int[] usedCards = iGatherer.getUsedCards(); 
		
		// pos 0 is ACE
		assertEquals(4, usedCards[0]); 
		
		for(int i=0; i<usedCards.length; i++)
		{
			if(i == 0)
				assertEquals(4, usedCards[0]); 
			else
				assertEquals(0, usedCards[i]); 
		}
		
		// test restore used cards
		iGatherer.restoreUsedCards(Cards.HEARTS_ACE); 
		usedCards = iGatherer.getUsedCards(); 
		
		// pos 0 is ACE
		assertEquals(3, usedCards[0]); 
		
		for(int i=0; i<usedCards.length; i++)
		{
			if(i == 0)
				assertEquals(3, usedCards[0]); 
			else
				assertEquals(0, usedCards[i]); 
		}
	}
	
	@Test
	public void testSetUsedCards_TWO() {
		
		iGatherer.setUsedCards(Cards.HEARTS_TWO);
		iGatherer.setUsedCards(Cards.CLUBS_ACE);
		iGatherer.setUsedCards(Cards.DIAMONDS_ACE);
		iGatherer.setUsedCards(Cards.HEARTS_ACE);
		
		int[] usedCards = iGatherer.getUsedCards(); 
		
		// pos 1 is TWO
		assertEquals(1, usedCards[1]); 
		
		for(int i=0; i<usedCards.length; i++)
		{
			if(i == 0)
				assertEquals(3, usedCards[i]);
			else if(i == 1)
				assertEquals(1, usedCards[i]);
			else
				assertEquals(0, usedCards[i]); 
		}
		
		// test restore used cards
		iGatherer.restoreUsedCards(Cards.HEARTS_TWO); 
		usedCards = iGatherer.getUsedCards(); 
		
		assertEquals(0, usedCards[1]); 
		
		for(int i=0; i<usedCards.length; i++)
		{
			if(i == 0)
				assertEquals(3, usedCards[0]); 
			else
				assertEquals(0, usedCards[i]); 
		}
	}

	
	@Test
	public void testSetUsedCards_THREE() {
		
		iGatherer.setUsedCards(Cards.DIAMONDS_TWO);
		iGatherer.setUsedCards(Cards.DIAMONDS_THREE);
		iGatherer.setUsedCards(Cards.DIAMONDS_FOUR);
		iGatherer.setUsedCards(Cards.DIAMONDS_FIVE);
		iGatherer.setUsedCards(Cards.HEARTS_TWO);
		iGatherer.setUsedCards(Cards.CLUBS_ACE);
		iGatherer.setUsedCards(Cards.JOKER_AS_ACE);
		
		
		int[] usedCards = iGatherer.getUsedCards(); 
		
		// pos 2 is THREE
		assertEquals(2, usedCards[1]); 
		
		for(int i=0; i<usedCards.length; i++)
		{
			if(i == 0)
				assertEquals(1, usedCards[i]);
			else if(i == 1)
				assertEquals(2, usedCards[i]);
			else if(i == 2)
				assertEquals(1, usedCards[i]); 
			else if(i == 3)
				assertEquals(1, usedCards[i]);
			else if(i == 4)
				assertEquals(1, usedCards[i]);
			else if(i == 13)
				assertEquals(1, usedCards[i]);
			else
				assertEquals(0, usedCards[i]); 
		}
	}


	@Test
	public void testSetUsedCards_FOUR() {
		
		iGatherer.setUsedCards(Cards.DIAMONDS_ACE);
		iGatherer.setUsedCards(Cards.HEARTS_FOUR);
		iGatherer.setUsedCards(Cards.DIAMONDS_FOUR);
		iGatherer.setUsedCards(Cards.DIAMONDS_FOUR);
		iGatherer.setUsedCards(Cards.CLUBS_FOUR);
		iGatherer.setUsedCards(Cards.CLUBS_FOUR);
		iGatherer.setUsedCards(Cards.SPADES_FOUR);
		
		
		int[] usedCards = iGatherer.getUsedCards(); 
		
		assertEquals(6, usedCards[3]); 
		
		for(int i=0; i<usedCards.length; i++)
		{
			if(i == 0)
				assertEquals(1, usedCards[i]);
			else if(i == 3)
				assertEquals(6, usedCards[i]);
			else
				assertEquals(0, usedCards[i]); 
		}
	}

	@Test
	public void testSetUsedCards_FIVE() {
		
		iGatherer.setUsedCards(Cards.DIAMONDS_FIVE);
		iGatherer.setUsedCards(Cards.HEARTS_FIVE);
		iGatherer.setUsedCards(Cards.CLUBS_FIVE);
		iGatherer.setUsedCards(Cards.SPADES_FIVE);
		iGatherer.setUsedCards(Cards.DIAMONDS_FIVE);
		iGatherer.setUsedCards(Cards.HEARTS_FIVE);
		iGatherer.setUsedCards(Cards.CLUBS_FIVE);		
		
		int[] usedCards = iGatherer.getUsedCards(); 
		
		assertEquals(7, usedCards[4]); 
		
		for(int i=0; i<usedCards.length; i++)
		{
		    if(i == 4)
				assertEquals(7, usedCards[i]);
			else
				assertEquals(0, usedCards[i]); 
		}
	}
	
	@Test
	public void testSetUsedCards_SIX() {
		
		iGatherer.setUsedCards(Cards.DIAMONDS_SIX);
		iGatherer.setUsedCards(Cards.HEARTS_SIX);
		iGatherer.setUsedCards(Cards.CLUBS_SIX);
		iGatherer.setUsedCards(Cards.SPADES_SIX);
		iGatherer.setUsedCards(Cards.DIAMONDS_SIX);
		iGatherer.setUsedCards(Cards.HEARTS_SIX);
		iGatherer.setUsedCards(Cards.CLUBS_SIX);
		// joker are added to usedCards[13]
		iGatherer.setUsedCards(Cards.JOKER_AS_SIX);
		iGatherer.setUsedCards(Cards.JOKER_AS_SIX);
		iGatherer.setUsedCards(Cards.JOKER_AS_SIX);
		
		int[] usedCards = iGatherer.getUsedCards(); 
		
		assertEquals(7, usedCards[5]); 
		
		for(int i=0; i<usedCards.length; i++)
		{
		    if(i == 5)
				assertEquals(7, usedCards[i]);
		    else if(i == 13)
				assertEquals(3, usedCards[i]);
			else
				assertEquals(0, usedCards[i]); 
		}
	}
	
	
	@Test
	public void testSetUsedCards_SEVEN() {
		
		iGatherer.setUsedCards(Cards.DIAMONDS_SEVEN);
		iGatherer.setUsedCards(Cards.HEARTS_SEVEN);
		iGatherer.setUsedCards(Cards.CLUBS_SEVEN);
		
		iGatherer.setUsedCards(Cards.JOKER_AS_SEVEN);
		iGatherer.setUsedCards(Cards.JOKER_AS_SEVEN);
		iGatherer.setUsedCards(Cards.JOKER_AS_SEVEN);
		
		int[] usedCards = iGatherer.getUsedCards(); 
		
		assertEquals(3, usedCards[6]); 
		
		for(int i=0; i<usedCards.length; i++)
		{
		    if(i == 6)
				assertEquals(3, usedCards[i]);
		    else if(i == 13)
				assertEquals(3, usedCards[i]);
			else
				assertEquals(0, usedCards[i]); 
		}
	}
	
	@Test
	public void testSetUsedCards_EIGHT() {
		
		iGatherer.setUsedCards(Cards.DIAMONDS_EIGHT);
		iGatherer.setUsedCards(Cards.HEARTS_EIGHT);
		iGatherer.setUsedCards(Cards.CLUBS_EIGHT);
		iGatherer.setUsedCards(Cards.SPADES_EIGHT);
		iGatherer.setUsedCards(Cards.DIAMONDS_EIGHT);
		iGatherer.setUsedCards(Cards.HEARTS_EIGHT);
		iGatherer.setUsedCards(Cards.CLUBS_EIGHT);
		iGatherer.setUsedCards(Cards.SPADES_EIGHT);
		
		int[] usedCards = iGatherer.getUsedCards(); 
		
		assertEquals(8, usedCards[7]); 
		
		for(int i=0; i<usedCards.length; i++)
		{
		    if(i == 7)
				assertEquals(8, usedCards[i]);
			else
				assertEquals(0, usedCards[i]); 
		}
	}
	
	@Test
	public void testSetUsedCards_NINE() {
		
		iGatherer.setUsedCards(Cards.DIAMONDS_NINE);
		iGatherer.setUsedCards(Cards.HEARTS_NINE);
		iGatherer.setUsedCards(Cards.CLUBS_NINE);
		
		int[] usedCards = iGatherer.getUsedCards(); 
		
		assertEquals(3, usedCards[8]); 
		
		for(int i=0; i<usedCards.length; i++)
		{
		    if(i == 8)
				assertEquals(3, usedCards[i]);
			else
				assertEquals(0, usedCards[i]); 
		}
	}
	
	@Test
	public void testSetUsedCards_TEN() {
	
		iGatherer.setUsedCards(Cards.DIAMONDS_TEN);
		
		iGatherer.setUsedCards(Cards.DIAMONDS_NINE);
		iGatherer.setUsedCards(Cards.HEARTS_NINE);
		iGatherer.setUsedCards(Cards.CLUBS_NINE);
		
		iGatherer.setUsedCards(Cards.JOKER_AS_TEN);
		
		
		int[] usedCards = iGatherer.getUsedCards(); 
		
		assertEquals(1, usedCards[9]); 
		
		for(int i=0; i<usedCards.length; i++)
		{
		    if(i == 9)
				assertEquals(1, usedCards[i]);
			else if(i == 8)
				assertEquals(3, usedCards[i]); 
			 else if(i == 13)
					assertEquals(1, usedCards[i]);
			else
				assertEquals(0, usedCards[i]); 
		}
	}
	
	@Test
	public void testSetUsedCards_JACK() {
	
		iGatherer.setUsedCards(Cards.DIAMONDS_JACK);
		iGatherer.setUsedCards(Cards.HEARTS_JACK);
		iGatherer.setUsedCards(Cards.SPADES_JACK);
		iGatherer.setUsedCards(Cards.CLUBS_JACK);
		
		iGatherer.setUsedCards(Cards.DIAMONDS_NINE);
		iGatherer.setUsedCards(Cards.HEARTS_NINE);
		iGatherer.setUsedCards(Cards.CLUBS_NINE);
		
		int[] usedCards = iGatherer.getUsedCards(); 
		
		assertEquals(4, usedCards[10]); 
		
		for(int i=0; i<usedCards.length; i++)
		{
		    if(i == 10)
				assertEquals(4, usedCards[i]);
			else if(i == 8)
				assertEquals(3, usedCards[i]); 
			else
				assertEquals(0, usedCards[i]); 
		}
	}
	
	@Test
	public void testSetUsedCards_QUEEN() {
	
		iGatherer.setUsedCards(Cards.DIAMONDS_QUEEN);
		iGatherer.setUsedCards(Cards.HEARTS_QUEEN);
		iGatherer.setUsedCards(Cards.SPADES_QUEEN);
		iGatherer.setUsedCards(Cards.CLUBS_QUEEN);
		iGatherer.setUsedCards(Cards.DIAMONDS_QUEEN);
		iGatherer.setUsedCards(Cards.HEARTS_QUEEN);
		iGatherer.setUsedCards(Cards.SPADES_QUEEN);
		iGatherer.setUsedCards(Cards.CLUBS_QUEEN);
		iGatherer.setUsedCards(Cards.DIAMONDS_QUEEN);
		iGatherer.setUsedCards(Cards.HEARTS_QUEEN);
		iGatherer.setUsedCards(Cards.SPADES_QUEEN);
		iGatherer.setUsedCards(Cards.CLUBS_QUEEN);
		
		iGatherer.setUsedCards(Cards.DIAMONDS_NINE);
		iGatherer.setUsedCards(Cards.HEARTS_NINE);
		iGatherer.setUsedCards(Cards.CLUBS_NINE);
		
		int[] usedCards = iGatherer.getUsedCards(); 
		
		assertEquals(8, usedCards[11]); 
		
		for(int i=0; i<usedCards.length; i++)
		{
		    if(i == 11)
				assertEquals(8, usedCards[i]);
			else if(i == 8)
				assertEquals(3, usedCards[i]); 
			else
				assertEquals(0, usedCards[i]); 
		}
	}
	
	@Test
	public void testSetUsedCards_KING() {
	
		iGatherer.setUsedCards(Cards.DIAMONDS_KING);
		iGatherer.setUsedCards(Cards.HEARTS_KING);
		iGatherer.setUsedCards(Cards.SPADES_KING);
		iGatherer.setUsedCards(Cards.CLUBS_KING);
		iGatherer.setUsedCards(Cards.DIAMONDS_KING);
		
		iGatherer.setUsedCards(Cards.DIAMONDS_NINE);
		iGatherer.setUsedCards(Cards.HEARTS_NINE);
		iGatherer.setUsedCards(Cards.CLUBS_NINE);
		
		int[] usedCards = iGatherer.getUsedCards(); 
		
		assertEquals(5, usedCards[12]); 
		
		for(int i=0; i<usedCards.length; i++)
		{
		    if(i == 12)
				assertEquals(5, usedCards[i]);
			else if(i == 8)
				assertEquals(3, usedCards[i]); 
			else
				assertEquals(0, usedCards[i]); 
		}
	}
	
	
	@Test
	public void testSetUsedCards_JOCKER() {
	
		iGatherer.setUsedCards(Cards.JOKER_AS_ACE);
		iGatherer.setUsedCards(Cards.JOKER_AS_FIVE);
		iGatherer.setUsedCards(Cards.JOKER_AS_EIGHT);
		iGatherer.setUsedCards(Cards.JOKER_AS_JACK);
		iGatherer.setUsedCards(Cards.JOKER_AS_QUEEN);
		
		
		iGatherer.setUsedCards(Cards.DIAMONDS_NINE);
		iGatherer.setUsedCards(Cards.HEARTS_NINE);
		iGatherer.setUsedCards(Cards.CLUBS_NINE);
		
		int[] usedCards = iGatherer.getUsedCards(); 
		
		assertEquals(5, usedCards[13]); 
		
		for(int i=0; i<usedCards.length; i++)
		{
		    if(i == 13)
				assertEquals(5, usedCards[i]);
			else if(i == 8)
				assertEquals(3, usedCards[i]); 
			else
				assertEquals(0, usedCards[i]); 
		}
	}
	
	@Test(expected= AssertionError.class)
	public void testSetUsedCards_NEG() {
		
		iGatherer.setUsedCards(-2);
		iGatherer.setUsedCards(-1);
		iGatherer.setUsedCards(-1000);
		
		int[] usedCards = iGatherer.getUsedCards(); 
		
		for(int i=0; i<usedCards.length; i++)
		{
				assertEquals(0, usedCards[i]); 
		}
	}
	
	
	@Test
	public void testRestoreUsedCards() {
		
		for(int i=1; i<14; i++) 
		{
			iGatherer.setUsedCards(i);
			iGatherer.restoreUsedCards(i); 
			
			int[] usedCards = iGatherer.getUsedCards(); 
		
			assertEquals(0, usedCards[i]); 
		}
		
		for(int i=1; i<9; i++)
		{
			iGatherer.setUsedCards(Cards.CLUBS_ACE);
		}
		
		int[] usedCards = iGatherer.getUsedCards(); 
		
		assertEquals(8, usedCards[0]); 
		
		for(int i=8; i>=0; i--) 
		{
			iGatherer.restoreUsedCards(Cards.CLUBS_ACE);	
			
			usedCards = iGatherer.getUsedCards(); 
			assertEquals(i-1, usedCards[0]); 
		}
	}

	
	@Test
	public void testDistributeUsedCards() {
		
		List<Integer> l = new ArrayList<Integer>(); 
		l.add(1); // ace
		l.add(1); // ace
		l.add(2); // two
		
		//for(int i=0; i<14; i++)
		//	l.add(1); 
		
		iGatherer.distributeUsedCards(l); 
		
		int[] usedCards  = iGatherer.getUsedCards(); 
		
		//for(int i=0; i<usedCards.length; i++)
		assertEquals(2, usedCards[0]);
		assertEquals(1, usedCards[1]);
		
	}

	@Test
	public void testDistributeUsedCards2() {
		
		List<Integer> l = new ArrayList<Integer>(); 
		
		for(int i=1; i<14; i++)
		{
			l.add(i); 
		}
		
		iGatherer.distributeUsedCards(l); 
		
		int[] usedCards  = iGatherer.getUsedCards(); 
		
		for(int i=1; i<14; i++)
			assertEquals(1, usedCards[i-1]);
		
	}
	
	@Test
	public void testDistributeUsedCards3() {
		
		List<Integer> l = new ArrayList<Integer>(); 
		l.add(Cards.HEARTS_JACK);
		l.add(Cards.SPADES_JACK);
		
		l.add(Cards.HEARTS_QUEEN); 
		l.add(Cards.SPADES_QUEEN); 
		
		l.add(Cards.CLUBS_ACE); 
		l.add(Cards.CLUBS_SEVEN); 
		l.add(Cards.DIAMONDS_FOUR); 
		
		iGatherer.distributeUsedCards(l); 
		
		int[] usedCards  = iGatherer.getUsedCards(); 
		
		assertEquals(2, usedCards[10]); // jack
		assertEquals(2, usedCards[11]);
		assertEquals(1, usedCards[0]);
		assertEquals(1, usedCards[6]);
		assertEquals(1, usedCards[3]); // four
	}
	
	

	@Test
	public void testAllGone1() {
		
		for(int i=0; i<8; i++)
			iGatherer.setUsedCards(Cards.CLUBS_ACE);
		
		assertEquals(true, iGatherer.allGone(Cards.CLUBS_ACE));
		assertEquals(true, iGatherer.allGone(Cards.HEARTS_ACE)); 
		assertEquals(true, iGatherer.allGone(Cards.DIAMONDS_ACE)); 
		assertEquals(true, iGatherer.allGone(Cards.SPADES_ACE)); 
	}

	@Test
	public void testAllGone2() {
		
		for(int i=0; i<14; i++)
			assertEquals(false, iGatherer.allGone(i)); 
		
		for(int i=0; i<7; i++)
			iGatherer.setUsedCards(Cards.JOKER_AS_ACE);
		
		for(int i=0; i<10; i++)
			iGatherer.setUsedCards(Cards.HEARTS_KING);
		
		
		assertEquals(false, iGatherer.allGone(Cards.JOKER_AS_ACE));
		assertEquals(true, iGatherer.allGone(Cards.HEARTS_KING));
		
		// from two up to king and joker is false
		for(int i=1; i<12; i++)
			assertEquals(false, iGatherer.allGone(i)); 
		
	}
	
	@Test
	public void testGetNumberOfCards()
	{
		int[] cards = new int[6]; 
		byte playerOnTurn = 1; 
		
		// 1 card
		int i = 0; 
		for(i=0; i<1; i++)
			cards[i] = Cards.HEARTS_FIVE; 
		
		fillEmptyCards(cards, i); 
		
		iGatherer.setCardsForPlayer(cards, playerOnTurn);
		assertEquals(1, iGatherer.getNumberOfCards(playerOnTurn)); 
		
		// 2 card
		for(i=0; i<2; i++)
			cards[i] = Cards.HEARTS_FIVE; 
		
		iGatherer.setCardsForPlayer(cards, playerOnTurn);
		assertEquals(2, iGatherer.getNumberOfCards(playerOnTurn)); 
		
		
		playerOnTurn = 2; 
		// 3 card
		for(i=0; i<3; i++)
			cards[i] = i; 
		
		iGatherer.setCardsForPlayer(cards, playerOnTurn);
		assertEquals(3, iGatherer.getNumberOfCards(playerOnTurn)); 
		
		
		playerOnTurn = 3; 
		// 4 card
		for(i=0; i<4; i++)
			cards[i] = Cards.JOKER; 
		
		iGatherer.setCardsForPlayer(cards, playerOnTurn);
		assertEquals(4, iGatherer.getNumberOfCards(playerOnTurn)); 
		
		
		playerOnTurn = 4; 
		// 5 card
		for(i=0; i<5; i++)
			cards[i] = i; 
		
		iGatherer.setCardsForPlayer(cards, playerOnTurn);
		assertEquals(5, iGatherer.getNumberOfCards(playerOnTurn)); 

		 
		// 6 card
		for(i=0; i<6; i++)
			cards[i] = i; 
		
		iGatherer.setCardsForPlayer(cards, playerOnTurn);
		assertEquals(6, iGatherer.getNumberOfCards(playerOnTurn)); 
		
	}
	
	// private helper function to fill rest of cards array with -1
	private void fillEmptyCards(int[] cards, int startIndex)
	{
		for(int start = startIndex; start < 6; start++)
			cards[start] = -1; 
	}
}
