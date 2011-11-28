package intelliDOG.ai.test;

import intelliDOG.ai.bots.ThreeStepBot;
import intelliDOG.ai.framework.BotBoard;
import intelliDOG.ai.framework.InformationGatherer;
import intelliDOG.ai.utils.DebugMsg;
import org.junit.Before;
import org.junit.Test;

/**
 * Some tests for the ThreeStepBot
 */
public class ThreeStepBotTest
{
	byte[] board = null;
	static DebugMsg msg = DebugMsg.getInstance();
	
	
	/**
	 * initialize
	 */
	@Before
	public void setUp()
	{
		board = new byte[80];
//		enable to verify results manually
//		msg.addItemForWhiteList(this);
//		msg.addItemForWhiteList(ThreeStepBot.class.getCanonicalName());
//		msg.addItemForWhiteList(BotBoard.class.getCanonicalName());
		
	}
	/**
	 * help method
	 * @param player who's turn it is 
	 * @param mycards the cards on the hand 
	 */
	private void doTest(byte player, int[] mycards)
	{
		InformationGatherer ig = new InformationGatherer(player);
		ig.setCardsForPlayer(mycards, player);
		BotBoard b = new BotBoard(board, ig);
		ThreeStepBot bot = new ThreeStepBot(b,ig);
		bot.makeMove();
		
		
	}
	/**move backwards and then in */
	@Test
	public void testUse4wisely()
	{
		byte player = 1;
		board[59] = 1; 
		int[] mycards = new int[]{10,4,-1,-1,-1,-1};
		doTest(player, mycards);
	}
}
