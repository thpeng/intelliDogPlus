package intelliDOG.ai.bots;

import intelliDOG.ai.evaluators.Evaluator;
import intelliDOG.ai.evaluators.SimpleEvaluatorV5;
import intelliDOG.ai.framework.BotBoard;
import intelliDOG.ai.framework.InformationGatherer;
import intelliDOG.ai.framework.Move;
import intelliDOG.ai.framework.Players;
import intelliDOG.ai.utils.DebugMsg;

import java.util.List;
/**
 * TwoStepBot - spawns all immediate successor states and their successor
 * The second step is weighted as 25% compared to the first step
 */
public class TwoStepBot implements IBot {
	
	private DebugMsg msg = DebugMsg.getInstance();
	private Evaluator t1 = new SimpleEvaluatorV5();
	private Evaluator t2 = null;
	Move highestMove = null;
	int highest = Integer.MIN_VALUE; 
	
	private InformationGatherer ig;
	private BotBoard bb;
	
	/**
	 * The constructor for the TwoStepBot
	 * @param bb The <class>BotBoard</class> for this bot
	 * @param ig The <class>InformationGatherer</class> for this bot
	 */
	public TwoStepBot(BotBoard bb, InformationGatherer ig){
		this.bb = bb;
		this.ig = ig;
	}
	
	/**
	 * This is the Default Constructor for the TwoStepBot
	 * It is used to provide compatibility with the Bodesuri Framework
	 * Don't use this constructor outside of the Bodesuri Framework!
	 */
	public TwoStepBot(){	
		this.ig = new InformationGatherer(Players.EMPTY);
		this.bb = new BotBoard(new byte[80], this.ig);
	}

	/**
	 * search recursively for a better move, walk trough all tree nodes
	 * @param rootMove the immediate successor of the current state.
	 * @param value sum of all previous values
	 * @param depth the depth search, at maximum 6.
	 */
	private void calculateBestMove(Move rootMove,int value, int depth)
	{
		//get all possbible moves
		List<Move> moves = bb.getAllPossibleMoves(ig.getMyPlayer());
		msg.debug(this, "value: " + value + " depth: " + depth);
		if(moves.size() == 1 && depth == 0)
		{
			highestMove = moves.get(0);
			return;
		}
		//do we have moves left to evaluate
		if(moves.size() == 0 || depth ==2)
		{
			//now, we are on a leaf of the search tree
			//look if we have a better sum of values
			if(value > highest)
			{
				msg.debug(this, "found better move with old: " + highest + " new: " + value);
				highest = value;
				this.highestMove = rootMove;
			}
			return;
		}
		else
		{
			//for each moves in moves
			for(Move m : moves)
			{
				//calculate the fading
				float fading = (1f/5);
				Evaluator current = null; 
				if(depth == 0)
				{
					//decide the root move
					rootMove = m;
					//on the root move, fading must be 1
					fading = 1; 
					current = t1; 
				}
				else
				{
					current = t2; 
				}
				bb.makeMove(m, ig.getMyPlayer());
				int result = (int) (fading * t2.evaluate(bb.getBoard(), ig.getMyPlayer(), ig.getMyCards(), (float) (1/ Math.pow(2, depth))));
				msg.debug(this,"result is: " +result + " value was: " +value + " thats a sum of: " + (value + result) + "");
				calculateBestMove(rootMove, value + result, depth + 1);

				bb.undoMove(ig.getMyPlayer());
			}
		}
	}
	
	@Override
	public Move makeMove() {
		highest = Integer.MIN_VALUE; 
		highestMove = null;
		calculateBestMove(null,0,0);
		return highestMove;
	}
	
	@Override
	public BotBoard getBotBoard() {
		return this.bb;
	}

	@Override
	public InformationGatherer getInformationGatherer() {
		return this.ig;
	}

	@Override
	public byte getPlayer() {
		return this.ig.getMyPlayer();
	}
	
	@Override
	public void setEvaluator(Evaluator eval)
	{
		this.t1 = eval; 
	}
	
	/**
	 * help method for testing purposes
	 * @return the highest value
	 */
	public int getHighestValue()
	{
		return this.highest;
	}

	@Override
	public int exchangeCard()
	{	
		int rand = (int)Math.floor(Math.random()* ig.getNumberOfCards(getPlayer())); 
		int c = ig.getMyCards()[rand];   
		ig.removCard(c);
		assert c != -1; 
		return c; 
	}
	
	public void setEvaluator(Evaluator t1, Evaluator t2)
	{
		this.t1 = t1; 
		this.t2 = t2; 
	}

}
