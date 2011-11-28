package intelliDOG.ai.utils;

import intelliDOG.ai.bots.IBot;
import intelliDOG.ai.evaluators.Evaluator;
import intelliDOG.ai.evaluators.SimpleEvaluatorV5;
import intelliDOG.ai.framework.BotBoard;
import intelliDOG.ai.framework.Game;
import intelliDOG.ai.framework.InformationGatherer;
import intelliDOG.ai.framework.Players;
import intelliDOG.ai.learning.rl.TD_lambda_Controller;
import intelliDOG.ai.ui.GameWindowImpl;
import intelliDOG.ai.ui.IntelliDOGStarter;
import intelliDOG.ai.ui.IntelliDOGStarter.RLParametersWindow;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Properties;

import javax.swing.JFrame;

/**
 * the starting class for games over the intelliDOG framework
 *
 */
public class IntelliOnlyArena extends Thread{
	
	private IntelliDOGStarter ids;
	private byte[] board = new byte[80];
	private byte currentPlayer; 
	private int[][] cards = new int[4][6]; 
	private List<Integer> usedCards;
	
	private boolean isForLearning = false;
	private int alphaDecreaseCycle;
	private int rlSaveCycle;
	private int betaIncreaseCycle;
	private boolean learning;
	
	public IntelliOnlyArena(){}
	
	public IntelliOnlyArena(IntelliDOGStarter ids){
		this.ids = ids;
	}
	
	private static DebugMsg msg = DebugMsg.getInstance();
	
	
	public static void main(String[] args) {
		
		//msg.addItemForWhiteList(BoardWrapper.class.getCanonicalName());
		
		//TODO: make commandline switches available for setting bot types and nr of games to run and enable UI!
		IntelliOnlyArena ioa = new IntelliOnlyArena();
		ioa.start();
	}

	/**
	 * starts a game with standard options
	 * (one game, with gui, 500ms timeout, simplebot's vs. randombot's)
	 */
	public void startGame(){
		boolean ui = true;
		long timeout = 500;
		int nrOfGames = 1;
		
		String[] botClassNames = {"intelliDOG.ai.bots.SimpleBot", "intelliDOG.ai.bots.RandomBot", "intelliDOG.ai.bots.SimpleBot", "intelliDOG.ai.bots.RandomBot"};
		String[] botNames = {"SimpleBot1", "RandomBot1", "SimpleBot2", "RandomBot2"};
		String[] evaluators = {"Standard", "Standard", "Standard", "Standard"};
		
		startGame(ui, timeout, nrOfGames, botClassNames, botNames, evaluators);
	}
	
	/**
	 * starts games with the given parameters
	 * @param ui with ui?
	 * @param timeout timeout after each move (only relevant when ui=true)
	 * @param nrOfGames how many games that shall be played
	 * @param botClassNames the class names of the bots that shall play
	 * @param botNames the names of the bots
	 * @param evaluators the class names of the evaluators to use by the bots
	 */
	public void startGame(boolean ui, long timeout, int nrOfGames, String[] botClassNames, String [] botNames, String [] evaluators){
		assert botClassNames.length == 4 && botNames.length == 4;
		
		GameWindowImpl gw = null;
		
		if(ui) {gw = new GameWindowImpl(botNames);}
		
		IBot[] bots = new IBot[4];
		try {
			byte[][] boards = new byte[4][80];
			
			if(ids.isSituation()){
				initSituation(ids.getBoardForInitSit(), ids.getCardsForInitSit(), ids.getUsedCardsForInitSit());
				for(int i = 0; i < boards.length; i ++){
					boards[i] = board.clone();
				}
			}
			for(int i = 0; i < botClassNames.length; i++){
				if(botClassNames[i].contains("TD_lambda_Bot")){
					RLParametersWindow rlpw = ids.getRlpw();
					this.learning = rlpw.isLearningChecked();
					TD_lambda_Controller.getInstance().setLearning(this.learning);
					File f = rlpw.getFile(); 
					if(f != null){
						Properties p = new Properties();
						java.io.FileInputStream fis;
						try {
							fis = new java.io.FileInputStream(f);
							p.load(fis);
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						TD_lambda_Controller.getInstance().load(p);
						
						this.alphaDecreaseCycle = Integer.parseInt(p.getProperty("alphaDecreaseCycle"));
						this.betaIncreaseCycle = Integer.parseInt(p.getProperty("betaIncreaseCycle"));
						this.rlSaveCycle = Integer.parseInt(p.getProperty("saveCycle"));
						
					}else{
						TD_lambda_Controller.getInstance().init(rlpw.getNN_Input(), rlpw.getNN_Hidden(), rlpw.getNN_Output(), rlpw.getAlpha(), rlpw.getBeta(), 
							rlpw.getGamma(), rlpw.getLambda(), rlpw.getAlphaDec(), rlpw.getBetaInc(), rlpw.getMomentum(), rlpw.getRewardWon(), rlpw.getRewardLost(), 
							rlpw.getRewardOwnPieceInHeaven(), rlpw.getRewardPartnerPieceInHeaven(), rlpw.getRewardOpponentPieceInHeaven(), 
							rlpw.isNormalizedChecked() ? TD_lambda_Controller.STATE_TYPE_NORMALIZED : TD_lambda_Controller.STATE_TYPE_STANDARD);
						this.alphaDecreaseCycle = rlpw.getAlphaDecCyc();
						this.betaIncreaseCycle = rlpw.getBetaIncCyc();
						this.rlSaveCycle = rlpw.getSaveCycle();
						TD_lambda_Controller.getInstance().save("start", this.rlSaveCycle, this.alphaDecreaseCycle, this.betaIncreaseCycle);
					}
					isForLearning = true;
					break;
				}
			}
			
			InformationGatherer ig0 = new InformationGatherer(Players.P1);
			Class bot0 = Class.forName(botClassNames[0]);
			bots[0] = (IBot)bot0.getConstructor(BotBoard.class, InformationGatherer.class).newInstance(new BotBoard(boards[0], ig0), ig0);
			InformationGatherer ig1 = new InformationGatherer(Players.P2);
			Class bot1 = Class.forName(botClassNames[1]);
			bots[1] = (IBot)bot1.getConstructor(BotBoard.class, InformationGatherer.class).newInstance(new BotBoard(boards[1], ig1), ig1);
			InformationGatherer ig2 = new InformationGatherer(Players.P3);
			Class bot2 = Class.forName(botClassNames[2]);
			bots[2] = (IBot)bot2.getConstructor(BotBoard.class, InformationGatherer.class).newInstance(new BotBoard(boards[2], ig2), ig2);
			InformationGatherer ig3 = new InformationGatherer(Players.P4);
			Class bot3 = Class.forName(botClassNames[3]);
			bots[3] = (IBot)bot3.getConstructor(BotBoard.class, InformationGatherer.class).newInstance(new BotBoard(boards[3], ig3), ig3);

			if(ids.isSituation())
			{
				ig0.setCardsForPlayer(cards[0], Players.P1); 
				ig1.setCardsForPlayer(cards[1], Players.P2); 
				ig2.setCardsForPlayer(cards[2], Players.P3); 
				ig3.setCardsForPlayer(cards[3], Players.P4); 
			}
			
			for(int i = 0; i < 4; i++){
				if(!evaluators[i].equals("Standard")){
					if(evaluators[i].contains("File -")){
						File f = ids.showFileDialog("Set File for Evaluator of Bot " + (i + 1));
						if(f == null){ return; }
						FileInputStream fis = null;
						try{
							fis = new FileInputStream(f); 
							Properties p = new Properties();
							p.load(fis);
							SimpleEvaluatorV5 sev5 = new SimpleEvaluatorV5();
							try{
								sev5.loadFromProperties(p);
							}catch(Exception ex){
								ids.showPropertiesErrorDialog();
								return;
							}
							bots[i].setEvaluator(sev5);
						}catch(Exception e)
						{
							e.printStackTrace();
						}finally{
							try{
								if(fis != null){
									fis.close();
								}
							}catch(Exception ex){}
						}
					}else{
						Class eval = Class.forName(evaluators[i]);
						bots[i].setEvaluator((Evaluator)eval.newInstance());
					}
				}
			}
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Game g;
		BarChartProb chart = null; 
		
		// just enable prob calculation without creating a chart
		if(ids.isProbSelected())
		{
			for(int i=0; i<4; i++)
			{
				bots[i].getInformationGatherer().enableProbability(true); 
			}
		} 

		if(ids.isChartSelected())
		{
			chart = new BarChartProb("Card distribution", ids.probFromPointOfView());
			chart.pack();
			chart.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			chart.setVisible(true); 
		}
		
		
		if(ids.isSituation()){
			int[][] playingCards = new int[4][6];
			for(int i = 0; i < playingCards.length; i++){
				playingCards[i] = cards[i].clone();
			}
			g = new intelliDOG.ai.framework.Game(bots, gw, chart, timeout, board.clone(), cards, currentPlayer, usedCards);
		}else{
			g = new intelliDOG.ai.framework.Game(bots, gw, chart, timeout);
		}
		
		for(int i = 0; i < nrOfGames; i++){
			if(gw != null){ gw.setTitle("Game " + (i + 1) + " of " + nrOfGames); }

			g.run();
			ids.addGameStat(i + 1, Statistics.getWinningTeam(), Statistics.getPieceCount1(), Statistics.getPieceCount2(), Statistics.getTime(), Statistics.getMoves());
			
			if(i != nrOfGames - 1){
				if(ids.isSituation()){
					int[][] playingCards = new int[4][6];
					for(int j = 0; j < playingCards.length; j++){
						playingCards[j] = cards[j].clone();
					}
					g.resetToSituation(board.clone(), playingCards, currentPlayer, usedCards);
				}else{
					g.reset();
				}
				if(isForLearning){
					if(this.alphaDecreaseCycle != 0 && i % this.alphaDecreaseCycle == 0){ //decrease alpha when needed
						TD_lambda_Controller.getInstance().decreaseAlpha();
					}
					if(this.betaIncreaseCycle != 0 && i % this.betaIncreaseCycle == 0){ //decrease beta when needed
						TD_lambda_Controller.getInstance().increaseBeta();
					}
					if(this.rlSaveCycle != 0 && i % this.rlSaveCycle == 0 && learning){ //save intermediate state when needed
						TD_lambda_Controller.getInstance().save("cycle_" + i,  this.rlSaveCycle, this.alphaDecreaseCycle, this.betaIncreaseCycle);
					}
				}
			}else{
				if(isForLearning && learning){
					//save at end of training session!
					TD_lambda_Controller.getInstance().save("end_cycle" + i, this.rlSaveCycle, this.alphaDecreaseCycle, this.betaIncreaseCycle);
				}
			}
		}
	}
	
	@Override
	public void run(){
		if(ids == null){
			startGame();
		}else{
			startGame(ids.isGUISelected(), ids.getTimeout(), ids.getNrOfGames(),
				ids.getBotClassNames(), ids.getBotNames(), ids.getEvaluators());
		}
	}
	
	/**
	 * Initiate a specific situation on the board
	 */
	public void initSituation(byte [] board, int[][] cards, List<Integer> usedCards)
	{
		this.board = board;
		this.cards = cards;
		this.usedCards = usedCards;
		
		int[] count = new int[cards.length];
		
		for(int i = 0; i < cards.length; i++){
			for(int j = 0; j < cards[i].length; j++){
				if(cards[i][j] != -1){ count[i]++; }
			}
		}
		currentPlayer = Players.P1;
		
		for(int i = 1; i < count.length; i++){
			if(count[i - 1] < count[i]){
				currentPlayer = (byte)(i + 1);
				break;
			}
		}
		
		
		 
	}
	
	
}