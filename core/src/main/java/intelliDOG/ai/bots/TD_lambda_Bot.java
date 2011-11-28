package intelliDOG.ai.bots;

import intelliDOG.ai.evaluators.Evaluator;
import intelliDOG.ai.framework.BotBoard;
import intelliDOG.ai.framework.InformationGatherer;
import intelliDOG.ai.framework.Move;
import intelliDOG.ai.learning.rl.TD_lambda_Controller;
import intelliDOG.ai.utils.DebugMsg;

public class TD_lambda_Bot implements IBot {
	
	private DebugMsg msg = DebugMsg.getInstance();

	private InformationGatherer ig;
	private BotBoard bb;
	
	public TD_lambda_Bot(BotBoard bb, InformationGatherer ig){
		this.bb = bb;
		this.ig = ig;
	}

	@Override
	public int exchangeCard() {
		int rand = (int)Math.floor(Math.random()* ig.getNumberOfCards(getPlayer())); 
		int c = ig.getMyCards()[rand];   
		ig.removCard(c);
		assert c != -1; 
		return c; 
	}

	@Override
	public BotBoard getBotBoard() {
		return bb;
	}

	@Override
	public InformationGatherer getInformationGatherer() {
		return ig;
	}

	@Override
	public byte getPlayer() {
		return ig.getMyPlayer();
	}

	@Override
	public Move makeMove() {
		return TD_lambda_Controller.getInstance().makeMove(this);
	}

	@Override
	public void setEvaluator(Evaluator eval) {
		
	}

}
