package intelliDOG.ai.test;

import intelliDOG.ai.evaluators.SimpleEvaluatorV2;
import intelliDOG.ai.utils.DebugMsg;
import intelliDOG.ai.framework.BotBoard;
import static org.junit.Assert.*; 
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for the SEV2.
 * Again, test the calculation function against
 * the new weights
 */
public class SimpleEvaluatorV2Test {

	byte[] board = null;
	SimpleEvaluatorV2 testObject2 = new SimpleEvaluatorV2(); 
	DebugMsg msg = DebugMsg.getInstance();
	
	@Before
	public void setUp()
	{
		board = new byte[80];
		// enable to verify result
		// msg.addItemForWhiteList(testObject2);
		// msg.addItemForWhiteList(BotBoard.class.getCanonicalName());
		
	}
	/** test the calculation function**/
	@Test
	public void testDistance() throws Exception
	{
		byte player = 2; 
		board[15] = player;
		int result = testObject2.evaluate(board, player);
		assertEquals(616, result);
	}
	/** test the calculation function**/
	@Test
	public void testDistance2() throws Exception
	{
		byte player = 2; 
		board[71] = player;
		int result = testObject2.evaluate(board, player);
		assertEquals(760, result);
	}
	
}
