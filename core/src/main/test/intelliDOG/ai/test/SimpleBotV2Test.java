package intelliDOG.ai.test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import intelliDOG.ai.bots.CheatingBot;
import intelliDOG.ai.bots.SimpleBotV2;
import intelliDOG.ai.evaluators.SimpleEvaluatorV5;
import intelliDOG.ai.framework.BotBoard;
import intelliDOG.ai.framework.Cards;
import intelliDOG.ai.framework.InformationGatherer;
import intelliDOG.ai.framework.Players;
import intelliDOG.ai.utils.DebugMsg;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SimpleBotV2Test {

	byte[] board = null;
	static DebugMsg msg = DebugMsg.getInstance();
	private InformationGatherer ig; 
	
	/**
	 * initialize
	 */
	@Before
	public void setUp()
	{
		board = new byte[80];
	}
	
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testExchangeCards1()
	{
		byte player = Players.P1; 
		
		int[] p1cards = new int[]{3,5,1,7,10,12};
		
		ig = new InformationGatherer(player);
		ig.setCardsForPlayer(p1cards, Players.P1); 
	
		BotBoard b = new BotBoard(board, ig);
		SimpleBotV2 bot = new SimpleBotV2(b,ig);
		int e1 = bot.exchangeCard();
		assertTrue(e1 != 1); // don't play only ace if no pawns on board 
	}
	
	@Test
	public void testExchangeCards2()
	{
		byte player = Players.P1; 
		
		int[] p1cards = new int[]{3,5,1,7,10,100};
		
		ig = new InformationGatherer(player);
		ig.setCardsForPlayer(p1cards, Players.P1); 
	
		BotBoard b = new BotBoard(board, ig);
		SimpleBotV2 bot = new SimpleBotV2(b,ig);
		int e1 = bot.exchangeCard();
		assertTrue(e1 == 1); // give ace 
	}
	
	@Test
	public void testExchangeCards3()
	{
		byte player = Players.P1; 
		board[0] = 5; 
		int[] p1cards = new int[]{3,5,1,7,10,4};
		
		ig = new InformationGatherer(player);
		ig.setCardsForPlayer(p1cards, Players.P1); 
	
		BotBoard b = new BotBoard(board, ig);
		SimpleBotV2 bot = new SimpleBotV2(b,ig);
		int e1 = bot.exchangeCard();
		assertTrue(e1 == 1); // give ace because i'm on the board but not my partner
	}
	
	@Test
	public void testExchangeCards4()
	{
		byte player = Players.P1; 
		board[0] = 5; 
		int[] p1cards = new int[]{100,5,1,7,10,100};
		
		ig = new InformationGatherer(player);
		ig.setCardsForPlayer(p1cards, Players.P1); 
	
		BotBoard b = new BotBoard(board, ig);
		SimpleBotV2 bot = new SimpleBotV2(b,ig);
		int e1 = bot.exchangeCard();
		assertTrue(e1 == 100); // give second joker
	}
	
	@Test
	public void testExchangeCards5()
	{
		byte player = Players.P1; 
		board[32] = 5; 
		int[] p1cards = new int[]{100,4,3,1,13,-1};
		
		ig = new InformationGatherer(player);
		ig.setCardsForPlayer(p1cards, Players.P1); 
	
		BotBoard b = new BotBoard(board, ig);
		SimpleBotV2 bot = new SimpleBotV2(b,ig);
		int e1 = bot.exchangeCard();
		assertTrue(e1 == 4); // give 4: best move for partner
	}
	
	@Test
	public void testExchangeCards6()
	{
		byte player = Players.P1; 
		board[59] = 1; // +nine fields: last field in heaven 
		board[26] = 3; // 8 fields before heaven field
		int[] p1cards = new int[]{100,4,3,1,9,13}; // save 9
		
		ig = new InformationGatherer(player);
		ig.setCardsForPlayer(p1cards, Players.P1); 
	
		BotBoard b = new BotBoard(board, ig);
		SimpleBotV2 bot = new SimpleBotV2(b,ig);
		int e1 = bot.exchangeCard();
		assertTrue(e1 != 9); // save 9
		assertTrue(e1 != 100); // save joker
	}
	
	@Test
	public void testExchangeCards7()
	{
		byte player = Players.P1; 
		
		int[] p1cards = new int[]{3,5,-1,-1,-1,-1};
		int[] p2cards = new int[]{2,6,-1,-1,-1,-1};
		int[] p3cards = new int[]{8,9,-1,-1,-1,-1};
		int[] p4cards = new int[]{2,4,-1,-1,-1,-1};
		
		ig = new InformationGatherer(player);
		ig.setCardsForPlayer(p1cards, Players.P1); 
	
		BotBoard b = new BotBoard(board, ig);
		SimpleBotV2 bot = new SimpleBotV2(b,ig);
		int count = 0; 
		for(int i=0; i<ig.getMyCards().length; i++)
		{
			if(ig.getMyCards()[i] == -1)
				count++; 
		}
		
		int count2 = 0; 
		int e1 = bot.exchangeCard();
		assertTrue(e1 != -1); 
		
		for(int i=0; i<ig.getMyCards().length; i++)
		{
			if(ig.getMyCards()[i] == -1)
				count2++; 
		}
		assertTrue(count+1 == count2); 
		assertEquals(4, count); 
		assertEquals(5, count2); 
		
		ig.setExchangedCard(Cards.CLUBS_EIGHT); 
		
		count2 = 0; 
		for(int i=0; i<ig.getMyCards().length; i++)
		{
			if(ig.getMyCards()[i] == -1)
				count2++; 
		}
		assertTrue(count == count2);
		assertEquals(4, count); 
	}
	
	@Test
	public void testExchangeCards8()
	{
		byte player = Players.P4; 
		board[76] = 5; 
		board[78] = 5;
		board[79] = 5; 
	
		board[25] = 2; 
		board[68] = 5; 
		board[70] = 5;
		board[71] = 5; 
		int[] p4cards = new int[]{4,3,-1,-1,-1,-1}; 
		// p2 has queen and joker
		ig = new InformationGatherer(player);
		ig.setCardsForPlayer(p4cards, player); 
	
		BotBoard b = new BotBoard(board, ig);
		SimpleBotV2 bot = new SimpleBotV2(b,ig);
		int e1 = bot.exchangeCard();
 
		assertTrue(e1 == 4); 
	}
	
	@Test
	public void testExchangeCards9()
	{
		byte player = Players.P4; 
		
		board[78] = 5;
		board[79] = 5; 
		board[46] = 4; 
		
		board[50] = 2; 
		board[14] = 2; 
		board[70] = 5;
		board[71] = 5; 
		int[] p4cards = new int[]{Cards.HEARTS_THREE,Cards.JOKER,Cards.CLUBS_THREE,-1,-1,-1}; 
		// p2 has queen and joker
		ig = new InformationGatherer(player);
		ig.setCardsForPlayer(p4cards, player); 
	
		BotBoard b = new BotBoard(board, ig);
		SimpleBotV2 bot = new SimpleBotV2(b,ig);
		int e1 = bot.exchangeCard();
 
		assertTrue(e1 == 3); 
	}
	
	@Test
	public void testExchangeCards10()
	{
		byte player = Players.P2; 
		
		board[14] = 2;
		board[16] = 5; 
		board[71] = 5; 
		
		int[] p4cards = new int[]{Cards.HEARTS_FOUR,Cards.JOKER,-1,-1,-1,-1}; 
		// p2 has queen and joker
		ig = new InformationGatherer(player);
		ig.setCardsForPlayer(p4cards, player); 
	
		BotBoard b = new BotBoard(board, ig);
		SimpleBotV2 bot = new SimpleBotV2(b,ig);
		int e1 = bot.exchangeCard();
 
		assertTrue(e1 == 4); 
	}
	
	@Test
	public void testExchangeCards11()
	{
		byte player = Players.P4; 
		
		board[14] = 2;
		board[16] = 5; 
		board[71] = 5; 
		board[22] = 4; 
		
		int[] p4cards = new int[]{Cards.HEARTS_FOUR,Cards.JOKER,-1,-1,-1,-1}; 
		// p2 has queen and joker
		ig = new InformationGatherer(player);
		ig.setCardsForPlayer(p4cards, player); 
	
		BotBoard b = new BotBoard(board, ig);
		SimpleBotV2 bot = new SimpleBotV2(b,ig);
		int e1 = bot.exchangeCard();
 
		assertTrue(e1 == 4); 
	}
}
