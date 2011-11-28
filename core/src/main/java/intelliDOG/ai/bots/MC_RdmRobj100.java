package intelliDOG.ai.bots;

import intelliDOG.ai.evaluators.Evaluator;
import intelliDOG.ai.evaluators.SimpleEvaluatorV5;
import intelliDOG.ai.framework.BotBoard;
import intelliDOG.ai.framework.InformationGatherer;
import intelliDOG.ai.framework.Move;
import intelliDOG.ai.utils.DebugMsg;
import intelliDOG.ai.utils.Helper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * 
 * This Bot performs a Monte Carlo Simulation to choose his best move. 
 * The cards are distributed according to the objective probability.
 * Equal moves are only calculated once what brings a speed benefit.
 * Finally the simulation calculates the best move for the Bot.     
 * He finishes a round with random moves and performs 100 simulations. 
 */
public class MC_RdmRobj100  implements IBot {

	Double bestScore = Double.NEGATIVE_INFINITY;
	Move bestMove = null; 
	
	private DebugMsg msg = DebugMsg.getInstance();
	private Helper helper = Helper.getInstance(); 
	Evaluator evaluator = new SimpleEvaluatorV5();  
	private InformationGatherer ig;

	long start = 0; 
	private BotBoard bb;
	MCSimulation sim = null; 

	public MC_RdmRobj100(BotBoard bb, InformationGatherer ig){
		this.bb = bb;
		this.ig = ig;
		//msg.addItemForWhiteList(this);  
	}

	
	@Override
	public Move makeMove() {
			
		HashMap<Integer, ArrayList<Integer> > dupMoveMap = new HashMap<Integer, ArrayList<Integer>>(); 
		ArrayList<Integer> dupIndex = new ArrayList<Integer>(); 
		
		bestScore = Double.NEGATIVE_INFINITY;
		bestMove = null; 
		
		List<Move> legalMoves = bb.getAllPossibleMoves(getPlayer()); 	
		if(legalMoves.isEmpty())
			return null; 
			
		// sort cards in descending order to perform a move with a joker at the very end. 
		Collections.sort(legalMoves, helper.createMoveComperator()); 
		helper.initIgnoreDups((ArrayList<Move>)legalMoves, dupMoveMap);

		Iterator<ArrayList<Integer> > iterator = dupMoveMap.values().iterator();
		while (iterator.hasNext()) 
		{
			ArrayList<Integer> list = iterator.next();
			for(int i=0; i<list.size(); i++)
				dupIndex.add(list.get(i)); 
		}
		
		List<Move> moves = new ArrayList<Move>(); 
		// remove duplicates from the moves
		for(int i=0; i<legalMoves.size(); i++)
		{
			if(!dupIndex.contains(i))
				moves.add(legalMoves.get(i)); 
		}
		
		if(moves.size() == 1)
			return moves.get(0); 
		
		int cpu = Runtime.getRuntime().availableProcessors();
		
		SimThread[] threads = new SimThread[cpu]; 
		List<Move>[] splitMoves = new List[cpu]; 
		
		long end = System.currentTimeMillis();
		sim = new MCSimulation(ig, bb, 100 , MCSimulation.DEAL_CARDS_ONCE, MCSimulation.USE_OBJ_PROB, MCSimulation.ROULETTE_SELECTION); 
		sim.setRandom(true); 
		
		if(moves.size() > 5 )
		{
			int p = 0; int distance = ( (moves.size()%cpu) == 0 ? moves.size()/cpu : (moves.size()/cpu)+1 ); 
			for(int i=0; i<cpu; i++)
			{
				int endpoint = ( (p+distance)>moves.size()?moves.size():(p+distance) ); 
				splitMoves[i] = new ArrayList<Move>(moves.subList(p, endpoint)); 
				p += distance;  
			}


			for(int i=0; i<cpu; i++)
			{
				threads[i] = new SimThread(splitMoves[i]); 
				threads[i].start();
			}

			for(int i=0; i<cpu; i++)
			{
				try {
					threads[i].join(); 
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			start += System.currentTimeMillis() - end;
			assert bestMove != null; 
			return bestMove; 
		}else
		{
			
			// moves.size < 5
			for(int i=0; i<moves.size(); i++)
			{
				double s = sim.simulate(100, moves.get(i));
				
				if(s > bestScore) {
					msg.debug(this, "NEW BEST SCORE: " + s + ", bestMove: " + moves.get(i).getPositions()[0] + " -> " + moves.get(i).getPositions()[1] + ", card: " + moves.get(i).getCard()); 
					bestScore = s; 
					bestMove = moves.get(i); 
				}	
			}
			return bestMove;
		}
	}	
	

	class SimThread extends Thread {
		
		private List<Move> moves = null;
		public SimThread(List<Move> moves) { this.moves = moves; }
		
		public void run() 
		{
			InformationGatherer tmpIg = ig.clone();
			MCSimulation mcSim = new MCSimulation(tmpIg, new BotBoard(bb.getBoard().clone(), tmpIg)); 
			mcSim.setDealtCards(sim.getDealtCards()); 
			mcSim.setCardPolicy(sim.getCardPolicy());  
			mcSim.setRandom(true); 
			
			for(int m = 0; m < moves.size(); m++)
			{
				double s = mcSim.simulate(100, moves.get(m));
			
				if(s > bestScore) 
				{
					bestScore = s;  
					bestMove = moves.get(m); 
				}
			}
		 }
	}
	public long getRuntime()
	{
		return (start/1000); 
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
		this.evaluator = eval; 
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
}


