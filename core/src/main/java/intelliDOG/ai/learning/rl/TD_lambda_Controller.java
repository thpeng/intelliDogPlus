package intelliDOG.ai.learning.rl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import intelliDOG.ai.bots.TD_lambda_Bot;
import intelliDOG.ai.framework.BotBoard;
import intelliDOG.ai.framework.Move;
import intelliDOG.ai.framework.Rules;
import intelliDOG.ai.learning.ann.Ann;
import intelliDOG.ai.learning.ann.NetAnn;

public class TD_lambda_Controller {

	private Ann ann;
	private double beta = 0.8;
	private double gamma = 0.9;
	private double betaIncrease = 0.0;
	private boolean learning = true;
	
	private List<Double> values;
	private List<Integer> rewards;
	private int rewardWin;
	private int rewardLoss;
	private int rewardOwnPieceInHeaven;
	private int rewardPartnerPieceInHeaven;
	private int rewardOpponentPieceInHeaven;
	
	private State currentState;
	private State afterState;
	
	private int stateType;
	
	public static final int STATE_TYPE_STANDARD = 0;
	public static final int STATE_TYPE_NORMALIZED = 1;
	
	
	private static TD_lambda_Controller instance;
	
	private TD_lambda_Controller(){
		values = new ArrayList<Double>();
		rewards = new ArrayList<Integer>();
	}
	
	/**
	 * get the only instance of the Rules class
	 * This is the thread-safe but yet performant implementation of a singleton.
	 * @return return an instance of the Rules class
	 */
	public synchronized static TD_lambda_Controller getInstance() {
        if (instance == null) {
        	synchronized (TD_lambda_Controller.class) {
				if(instance == null){
					instance = new TD_lambda_Controller();
				}
			}
        }
        return instance;
    }
	
	
	public void init(int inputNeurons, int hiddenNeurons, int outputNeurons, double alpha, double beta, double gamma, double lambda, 
			double alphaDecrease, double betaIncrease, double momentum, int rewardWin, int rewardLoss, 
			int rewardOwnPieceInHeaven, int rewardPartnerPieceInHeaven, int rewardOpponentPieceInHeaven, int stateType){
		this.beta = beta;
		this.gamma = gamma;
		this.betaIncrease = betaIncrease;
		this.rewardWin = rewardWin;
		this.rewardLoss = rewardLoss;
		this.rewardOwnPieceInHeaven = rewardOwnPieceInHeaven;
		this.rewardPartnerPieceInHeaven = rewardPartnerPieceInHeaven;
		this.rewardOpponentPieceInHeaven = rewardOpponentPieceInHeaven;
		this.stateType = stateType;
		//init neural net
		this.ann = new NetAnn(inputNeurons, hiddenNeurons, outputNeurons, alpha, momentum, gamma, lambda, alphaDecrease); 
	}
	
	/**
	 * Method used to load a configuration with all parameters and the neural net from file(s)
	 * @param fileName the file where the config to load is saved
	 */
	public void load(Properties p){
		//Load ann
		this.ann = new NetAnn();
		this.ann.load(p.getProperty("NNFileName"));
		this.ann.setProperties(p);
		
		//load beta
		this.beta = Double.parseDouble(p.getProperty("beta"));
		this.beta = Double.parseDouble(p.getProperty("beta"));
		
		//load rewards
		this.rewardWin = Integer.parseInt(p.getProperty("rewardWin"));
		this.rewardLoss = Integer.parseInt(p.getProperty("rewardLoss"));
		this.rewardOwnPieceInHeaven = Integer.parseInt(p.getProperty("rewardOwnPieceInHeaven"));
		this.rewardPartnerPieceInHeaven = Integer.parseInt(p.getProperty("rewardPartnerPieceInHeaven"));
		this.rewardOpponentPieceInHeaven = Integer.parseInt(p.getProperty("rewardOpponentPieceInHeaven"));
		
		//load state type
		this.stateType = Integer.parseInt(p.getProperty("stateType"));
	}
	
	/**
	 * Method used to load a configuration, mainly the neural net from file and resetting the parameters.
	 */
	public void load(String fileName, double alpha, double beta, double gamma, double lambda, 
			double alphaDecrease, double betaIncrease, double momentum, int rewardWin, int rewardLoss, 
			int rewardOwnPieceInHeaven, int rewardPartnerPieceInHeaven, int rewardOpponentPieceInHeaven){
		//TODO: implement this!
	}
	
	public void save(String fileNameEnding, int saveCycle, int alphaDecreaseCycle, int betaIncreaseCycle){
		Properties p = this.ann.getProperties();
		//add beta properties
		p.setProperty("beta", Double.toString(this.beta));
		p.setProperty("betaIncrease", Double.toString(this.betaIncrease));
		
		//add rewards to properties
		p.setProperty("rewardWin", Integer.toString(this.rewardWin));
		p.setProperty("rewardLoss", Integer.toString(this.rewardLoss));
		p.setProperty("rewardOwnPieceInHeaven", Integer.toString(this.rewardOwnPieceInHeaven));
		p.setProperty("rewardPartnerPieceInHeaven", Integer.toString(this.rewardPartnerPieceInHeaven));
		p.setProperty("rewardOpponentPieceInHeaven", Integer.toString(this.rewardOpponentPieceInHeaven));
		
		//add state type
		p.setProperty("stateType", Integer.toString(this.stateType));
		
		//add cycle properties
		p.setProperty("saveCycle", Integer.toString(saveCycle));
		p.setProperty("alphaDecreaseCycle", Integer.toString(alphaDecreaseCycle));
		p.setProperty("betaIncreaseCycle", Integer.toString(betaIncreaseCycle));
		
		
		//create time stamp for saved NeuralNet file name
		Date dt = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd__HH_mm_ss_S");
		String dateString = sdf.format(dt);
		//add filename to p
		String fileNameNN = "RL/" + dateString + "__" + fileNameEnding + ".nnet";
		p.setProperty("NNFileName", fileNameNN);
		//create folder to save into if it not already exists
		File f = new File("RL");
		if(!f.exists())
		{
			f.mkdir();
		}
		//save neural net
		this.ann.save(fileNameNN);
		//save properties with (same) time stamp
		try {
			FileOutputStream fos = new FileOutputStream("RL/" + dateString + "__" + fileNameEnding +".properties"); 
			p.store(fos, null);
			fos.close(); 
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void decreaseAlpha(){
		if(this.ann instanceof NetAnn){
			((NetAnn)this.ann).decreaseAlpha();
		}
	}
	
	public void increaseBeta(){
		this.beta += this.betaIncrease;
	}

	public Move makeMove(TD_lambda_Bot bot){
		//get cards
		int[] cards = bot.getInformationGatherer().getMyCards();
		//get board
		BotBoard bb = bot.getBotBoard();
		byte[] board = bb.getBoard();
		//get myplayer
		byte myPlayer = bot.getPlayer();
		
		//create the current state
		if(stateType == TD_lambda_Controller.STATE_TYPE_STANDARD){
			currentState = new StandardState(cards, board, myPlayer);
		}else{
			currentState = new NormalizedState(cards, board, myPlayer);
		}
		//get the output for the current state
		double currentStateValue = this.ann.getValue(currentState);
		
		//get possible moves
		List<Move> possible = bb.getAllPossibleMoves(myPlayer);
		
		//if we can not move!
		if(possible.size() == 0){
			return null;
		}
		int winningPos = -1;
		//for each possible move 
		for(int i = 0; i < possible.size(); i++){
			//TODO: try to use multiple (preferably 4) ply search!
			
			//simulate it.
			bb.makeMove(possible.get(i), myPlayer);
			
			//generate a the after state
			if(stateType == TD_lambda_Controller.STATE_TYPE_STANDARD){
				afterState = new StandardState(bot.getInformationGatherer().getMyCards(), bb.getBoard(), myPlayer);
			}else{
				afterState = new NormalizedState(bot.getInformationGatherer().getMyCards(), bb.getBoard(), myPlayer);
			}
			
			//get the value for each simulated state
			values.add(ann.getValue(afterState));
			
			//get the reward for the execution of that action
			rewards.add(calculateReward(bb, myPlayer));
			
			//if we would win through this move, remember for execution!
			if(isWinState(bb, myPlayer)){
				winningPos = i;
				break;
			}
			
			//undo the move.
			bb.undoMove(myPlayer);
		}//end for
		
		//choose the best after state according to the actual policy
		int indexOfBest = 0; //This shall point to the best move / value;
		
		if(winningPos != -1){ //we can win, so let us win!
			indexOfBest = winningPos;
		}else if(possible.size() != 1){ //if only one move is possible the best move will have index 0 and we don't have to evaluate it!
			if(this.beta < 0.0 || !this.isLearning()){
				//Exploitation policy
				Iterator<Double> valueIterator = values.iterator();
				int counter = 0;
				double actualValue = 0.0;
				double highestValue = 0.0;
				while(valueIterator.hasNext()){
					actualValue = valueIterator.next();
					if(actualValue > highestValue){
						highestValue = actualValue;
						indexOfBest = counter;
					}
					counter++;
				}
			}else{
				double[] sectors = new double[values.size()];
				//exploration
				Iterator<Double> valueIterator = values.iterator();
				double sumSectorValues = 0.0;
				while(valueIterator.hasNext()){
					sumSectorValues += Math.pow(this.beta, valueIterator.next());
				}
				valueIterator = values.iterator();
				int counter = 0;
				double sectorEnd = 0.0;
				while(valueIterator.hasNext()){
					sectors[counter] = sectorEnd + (Math.pow(this.beta, valueIterator.next()) / sumSectorValues);
					sectorEnd = sectors[counter];
					counter++;
				}
				
				Random r = new Random();
				double rand = r.nextDouble();
				
				valueIterator = values.iterator();
				counter = 0;
				double l = 0.0;
				while(valueIterator.hasNext()){
					if(l < rand && rand <= sectors[counter]){
						indexOfBest = counter;
						break;
					}
					l = sectors[counter];
					counter++;
				}
			}
		}
		
		if(this.learning){
			//TD Error
			double td = rewards.get(indexOfBest) + (gamma * values.get(indexOfBest)) - currentStateValue;
			//output error
			double outpErr = values.get(indexOfBest) - currentStateValue; 
			//update the neural net
			ann.updateWeights(td, outpErr);
		}
		
		values.clear();
		rewards.clear();
		
		return possible.get(indexOfBest);
	}


	private int calculateReward(BotBoard bb, byte myPlayer) {
		int reward = 0;
		if(Rules.getInstance().allPiecesInHeavenOfPlayer(bb.getBoard(), myPlayer)
				&& Rules.getInstance().allPiecesInHeavenOfPlayer(bb.getBoard(), Rules.getInstance().getPartnerForPlayer(myPlayer))){
			reward += this.rewardWin;
		}else{
			reward += this.rewardLoss;
		}
		reward += Rules.getInstance().nrOfPiecesInHeavenOfPlayer(bb.getBoard(), myPlayer) * this.rewardOwnPieceInHeaven;
		reward += Rules.getInstance().nrOfPiecesInHeavenOfPlayer(bb.getBoard(), Rules.getInstance().getPartnerForPlayer(myPlayer)) * this.rewardPartnerPieceInHeaven;
		reward += Rules.getInstance().nrOfPiecesInHeavenOfPlayer(bb.getBoard(), bb.getNextPlayer(myPlayer)) * this.rewardOpponentPieceInHeaven;
		reward += Rules.getInstance().nrOfPiecesInHeavenOfPlayer(bb.getBoard(), bb.getLastPlayer(myPlayer)) * this.rewardOpponentPieceInHeaven;
		
		return reward;
	}
	
	private boolean isWinState(BotBoard bb, byte myPlayer){
		return Rules.getInstance().allPiecesInHeavenOfPlayer(bb.getBoard(), myPlayer)
		&& Rules.getInstance().allPiecesInHeavenOfPlayer(bb.getBoard(), Rules.getInstance().getPartnerForPlayer(myPlayer));
	}

	/**
	 * @return the learning
	 */
	public boolean isLearning() {
		return learning;
	}


	/**
	 * @param learning the learning to set
	 */
	public void setLearning(boolean learning) {
		this.learning = learning;
	}

	
}
