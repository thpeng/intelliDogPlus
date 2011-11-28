package intelliDOG.ai.optimization.genetic;

import intelliDOG.ai.bots.IBot;
import intelliDOG.ai.bots.SimpleBot;
import intelliDOG.ai.bots.TwoStepBot;
import intelliDOG.ai.framework.BotBoard;
import intelliDOG.ai.framework.Game;
import intelliDOG.ai.framework.InformationGatherer;
import intelliDOG.ai.framework.Players;
import intelliDOG.ai.optimization.EvaluatorWrapper;
import intelliDOG.ai.utils.DebugMsg;
/**
 * The GameRunner executes games on different processors
 * @author tpeng
 */
public class GameRunner implements Runnable{

	private int w = 0; 
	private int g = 0; 
	private int from = 0;
	private int to; 
	private EvaluatorWrapper[][] gp = null;
	private Class<?> bot; 
	private boolean pairMode; 

	private DebugMsg msg = DebugMsg.getInstance(); 

	@Override
	public void run() {
		// TODO Auto-generated method stub
		msg.debug(this, "GameRunner entered from: "+from + " to: "+ to );
		playGames(gp, w, g);

	}
	/**
	 * set the details	 */
	public void setDetails(EvaluatorWrapper[][] gamePairs, int winsize,int gamePolicy, int from, int to, Class<?> b, boolean pairMode)
	{
		this.gp = gamePairs; 
		this.w = winsize; 
		this.to = to;
		this.from = from; 
		this.g = gamePolicy; 
		this.bot = b; 
		this.pairMode = pairMode; 

	}
	/** 
	 * play games on a separate processor
	 * @param gamePairs the pairs
	 * @param winsize best of win size
	 * @param gamePolicy the actual policy
	 */
	public void playGames(EvaluatorWrapper[][] gamePairs, int winsize, int gamePolicy)
	{
		msg.debug(this, "gamepairs length: " + gamePairs.length+ " winsize: "+ winsize+ " gamePolicy: "+ gamePolicy);
		
		IBot[] bots = setClass(bot); 
//		GameWindow gw =  new GameWindow();
		int firstPlayerwon = 0; 
		int secondPlayerwon = 0; 

		Game g = new Game(bots, null, null, 0l);
		g.setMaxTurns(2000);
		for(int i = from; i< to; i++)
		{
			if(!(firstPlayerwon  == winsize) && !(secondPlayerwon == winsize))
			{
				if(bot.getCanonicalName().contains("Simple"))
				{
					if(!pairMode)
					{
						bots[0].setEvaluator(gamePairs[i][0]);
						bots[1].setEvaluator(gamePairs[i][1]);
						bots[2].setEvaluator(gamePairs[i][0]);
						bots[3].setEvaluator(gamePairs[i][1]);
					}
					else
					{
						bots[0].setEvaluator(gamePairs[i][0]);
						bots[1].setEvaluator(gamePairs[i][1]);
						bots[2].setEvaluator(gamePairs[i][0].getMate());
						bots[3].setEvaluator(gamePairs[i][1].getMate());
					}
				}
				if(bot.getCanonicalName().contains("TwoStepBot"))
				{
					((TwoStepBot) bots[0]).setEvaluator(gamePairs[i][0],gamePairs[i][0].getMate());
					((TwoStepBot) bots[1]).setEvaluator(gamePairs[i][1], gamePairs[i][1].getMate());
					((TwoStepBot) bots[2]).setEvaluator(gamePairs[i][0],gamePairs[i][0].getMate());
					((TwoStepBot) bots[3]).setEvaluator(gamePairs[i][1],gamePairs[i][1].getMate());
				}

				g.run(); 
				int won = g.hasWon();
				gamePairs[i][0].played();
				gamePairs[i][1].played();
				g.reset(); 
				switch (won)
				{
				case 0:
					msg.debug(this,"round draw");
					break;
				case 1: 
					gamePairs[i][0].won();
					firstPlayerwon++; 
					msg.debug(this,"p1 won");
					break;
				case 2: 
					gamePairs[i][1].won();
					secondPlayerwon++; 
					msg.debug(this,"p2 won");
					break;
				default:
					throw new UnsupportedOperationException(" the value: " +won+ " is not defined");
				}
			}
			else
			{
				msg.debug(this,"game will be skipped");
			}
			if(i%gamePolicy== gamePolicy-1)
			{
				firstPlayerwon = 0;
				secondPlayerwon = 0; 
			}
						
		}
		gamePairs = null; 
		bots = null;
	}
	/**
	 * enable alternatives for the SimpleBot
	 * @param blueprint the class
	 * @return a concrete object in arrays
	 */
	public IBot[] setClass(Class<?> blueprint)
	{
		InformationGatherer ig1= new InformationGatherer(Players.P1);
		InformationGatherer ig2= new InformationGatherer(Players.P2);
		InformationGatherer ig3= new InformationGatherer(Players.P3);
		InformationGatherer ig4= new InformationGatherer(Players.P4);
		if(blueprint.getCanonicalName().contains("SimpleBot"))
		{
			IBot[] results = {new SimpleBot(new BotBoard(new byte[80], ig1), ig1),
					new SimpleBot(new BotBoard(new byte[80], ig2), ig2),
					new SimpleBot(new BotBoard(new byte[80], ig3), ig3),
					new SimpleBot(new BotBoard(new byte[80], ig4), ig4)};
			return results; 
		}
		else if(blueprint.getCanonicalName().contains("TwoStepBot"))
		{
			IBot[] results = {new TwoStepBot(new BotBoard(new byte[80], ig1), ig1),
					new TwoStepBot(new BotBoard(new byte[80], ig2), ig2),
					new TwoStepBot(new BotBoard(new byte[80], ig3), ig3),
					new TwoStepBot(new BotBoard(new byte[80], ig4), ig4)};
			return results; 
		}
		else
		{
			throw new IllegalArgumentException("class not found or not supported");
		} 
	}
}
