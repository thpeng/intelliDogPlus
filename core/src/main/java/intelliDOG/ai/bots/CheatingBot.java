package intelliDOG.ai.bots;

import intelliDOG.ai.evaluators.Evaluator;
import intelliDOG.ai.evaluators.SimpleEvaluatorV5;
import intelliDOG.ai.framework.BotBoard;
import intelliDOG.ai.framework.InformationGatherer;
import intelliDOG.ai.framework.Move;
import intelliDOG.ai.utils.DebugMsg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;


/**
 * 
 * The CheatingBot behaves like the Alpha-Beta Bot except he is able to play with perfect information.
 * He can look into the cards of all other players.
 *
 */
public class CheatingBot implements IBot {

	public final int MAXDEPTH = 4; 
	private DebugMsg msg = DebugMsg.getInstance();
	Evaluator evaluator = new SimpleEvaluatorV5();  
	private Move highestMove = null;
	private byte currentPlayer; 
	private int maxdepth = MAXDEPTH; 
	private InformationGatherer ig;
	private BotBoard bb;
	
	/**
	 * The constructor for the CheatingBot
	 * @param bb The <class>BotBoard</class> for this bot
	 * @param ig The <class>InformationGatherer</class> for this bot
	 */
	public CheatingBot(BotBoard bb, InformationGatherer ig){
		this.bb = bb;
		this.ig = ig;
		//msg.addItemForWhiteList(this); 
	}
	
	
	int AlphaBeta(int alpha, int beta, int depth, boolean is_max_node)
	{
		int result = 0; 
		int score = 0; 
		int succScore = 0; 
		List<Move> possible = null; 
		
		HashMap<Integer, ArrayList<Integer> > dupMoveMap = new HashMap<Integer, ArrayList<Integer>>(); 
		HashMap<Integer, Integer> indexEvalMap = new HashMap<Integer, Integer>(); 

		if(depth == 0 || bb.terminalState(currentPlayer))  
		{
			result = evaluator.evaluate(bb.getBoard(), currentPlayer, ig.getCardsForPlayer(currentPlayer), 1); 
			msg.debug(this, "player who's evaluating: " + currentPlayer + " - score: " + result); 
			return result; 
		}
		
		// Check if currentPlayer is able to move. currentPlayer gives up in case "possible" is empty
		if( !ig.getPlayerGaveUp(currentPlayer))
		{
			possible = bb.getAllPossibleMoves(currentPlayer);
			
			// sort cards in descending order to perform a move with a joker at the very end. 
			Collections.sort(possible, new MoveComperator()); 
			
			initIgnoreDups((ArrayList<Move>)possible, dupMoveMap);
			
			if(possible.size() == 0)
				ig.setBotGaveUp(currentPlayer); 
		}
		
		// ignore a player that gave up. Return the value that follows next 
		if(ig.getPlayerGaveUp(currentPlayer))
		{
			msg.debug(this, "player gave up: "+ currentPlayer); 
			currentPlayer = bb.getNextPlayer(currentPlayer);
			int jmpScore = AlphaBeta(alpha, beta, depth-1, !is_max_node);
			currentPlayer = bb.getLastPlayer(currentPlayer); 
			return jmpScore; 
		}
				
						
		if(is_max_node)
		{
			boolean copyValue = false; 
			score = Integer.MIN_VALUE; 
		
			msg.debugLegalMoves(this, possible); 

			for(int i=0; i< possible.size(); i++ )
			{
				
				// in case there exist duplicates for this move: save evaluation value 
				if(dupMoveMap.containsKey(i))
					copyValue = true; 
				
				if( !indexEvalMap.containsKey(i))
				{
					msg.debug(this,"makeMove MAX: player "+ currentPlayer + " with this move: "); 
					msg.debugMove(this, possible.get(i)); 

					bb.makeMove(possible.get(i), currentPlayer); 
					currentPlayer = bb.getNextPlayer(currentPlayer);

					succScore = AlphaBeta(alpha, beta, depth-1, !is_max_node); 
				
					if(copyValue)
					{
						copyValue = false; 
						ArrayList<Integer> dupList = new ArrayList<Integer>();
						dupList = dupMoveMap.get(i); 

						for(int index = 0; index < dupList.size(); index++)
							indexEvalMap.put(dupList.get(index), succScore);
					}

					if(succScore > score)
					{
						score = succScore;
						byte tmp = bb.getLastPlayer(currentPlayer);

						// only set highest move for my player and at outermost level
						if(ig.getMyPlayer() == tmp &&  depth == maxdepth ) {
							highestMove = possible.get(i);
							msg.debug(this, "PL: " + ig.getMyPlayer() + " - tmp:  " + tmp + " , card: " + highestMove.getCard() + " , " +highestMove.getPositions()[0] + " --> " + highestMove.getPositions()[1]);  
						}
					}
					if(score > alpha) alpha = score; 
					if(alpha >= beta)
					{
						msg.debug(this, "MAX CUTOFF: score: " + score); 
						currentPlayer = bb.getLastPlayer(currentPlayer);
						bb.undoMove(currentPlayer);
						return score; 
					}
					currentPlayer = bb.getLastPlayer(currentPlayer);
					bb.undoMove(currentPlayer);
				}
			}// end for possibleMoves
			
		// min node	
		}else {
			
			boolean copyValue = false; 
			score = Integer.MAX_VALUE; 
		
			for(int i=0; i< possible.size(); i++ )
			{
				if(dupMoveMap.containsKey(i))
					copyValue = true; 
				
				if(!indexEvalMap.containsKey(i))
				{
					
					msg.debug(this,"makeMove MIN: player "+ currentPlayer + " with this move: ");
					msg.debugMove(this, possible.get(i)); 

					bb.makeMove(possible.get(i), currentPlayer);
					currentPlayer = bb.getNextPlayer(currentPlayer);

					succScore = AlphaBeta(alpha, beta, depth-1, !is_max_node); 
				
					if(copyValue)
					{
						copyValue = false; 
						ArrayList<Integer> dupList = new ArrayList<Integer>();
						dupList = dupMoveMap.get(i); 

						for(int index = 0; index < dupList.size(); index++)
							indexEvalMap.put(dupList.get(index), succScore);
					}

					if(succScore < score)
					{
						score = succScore;
					}
					if(score < beta) beta = score; 
					if(beta <= alpha) 
					{
						msg.debug(this, "MIN CUTOFF: score: " + score); 
						currentPlayer = bb.getLastPlayer(currentPlayer);
						bb.undoMove(currentPlayer);
						return score;
					}
					currentPlayer = bb.getLastPlayer(currentPlayer);
					bb.undoMove(currentPlayer);
				}
			}// end for
		}	
		return score; 
	}

	@Override
	public Move makeMove() {
		boolean is_max_node = true;
		boolean[] saveGaveUp = new boolean[4]; 
		
		highestMove = null; 
		
		if(evaluator == null)
		{
			evaluator = new SimpleEvaluatorV5(); 
		}
		
		currentPlayer = ig.getMyPlayer(); 
		// FIXME: set depth according to the number of cards (check cards for every player and take the fewest amount)
		
		int count = 100; 
		byte tmpPlayer = currentPlayer; 
		
		for(int i=1; i<=4; i++)
		{
			int tmp = ig.getNumberOfCards(tmpPlayer);
			tmpPlayer = bb.getNextPlayer(tmpPlayer); 
			
			// set smallest value but ignore player who gave up
			if(tmp < count && tmp != 0)
				count = tmp; 
		}
		
		// don't set depth if count is greater than 3 (otherwise actual depth would be 12!!!)
		if(count <= 2)
		{
			maxdepth = count * 4; 
			msg.debug(this, "New maxdepth: " + maxdepth); 
		
		} else
		{
			msg.debug(this, "Normal depth: " + maxdepth);
		}
		System.arraycopy(ig.getBotsGaveUp(), 0, saveGaveUp, 0, saveGaveUp.length); 
		int highest = AlphaBeta(Integer.MIN_VALUE, Integer.MAX_VALUE, maxdepth, is_max_node); 
		ig.setBotsGaveUp(saveGaveUp); 
		
		if(highestMove != null) {
			msg.debug(this, "Player: " + currentPlayer + ", Karte: " + highestMove.getCard() + ", Bewertung: " + highest + ", s: " + highestMove.getPositions()[0] + ", t: " + highestMove.getPositions()[1]);
		}
		
		// restore maxdepth
		maxdepth = MAXDEPTH; 
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
		this.evaluator = eval; 
	}
	
	private void initIgnoreDups(ArrayList<Move> possMoves, HashMap<Integer, ArrayList<Integer> > dupMap )
	{
		boolean[] done = new boolean[possMoves.size()];
		
		// loop through all possible moves but ignore last entry
		for(int i=0; i<possMoves.size()-1; i++)
		{
			ArrayList<Integer> dupList = new ArrayList<Integer>(); 
			
			if(done[i] == false)
			{
				for(int j=i+1; j<possMoves.size(); j++)
				{
					if(possMoves.get(i).sameMove(possMoves.get(j)))
					{

						done[j] = true; // mark inserted index as done
						dupList.add(j); // add index of duplicate move
					}
				}
			} // end done == false
			
			// before starting outer loop for next entry: add list (if not empty) to map
			if( !dupList.isEmpty())
			{
				dupMap.put(i, dupList); 
				
				// debug
				//for(int x = 0; x < dupList.size(); x++)
				//	msg.debug(this, dupList.get(x).toString()); 
			}
		}
	}
	
	private void sortPossibleMoves(ArrayList<Move> moves)
	{
		Collections.sort(moves, new MoveComperator()); 
	}
	
	class MoveComperator implements Comparator
	{
		
		/**
		 * @param a first object to be compared
		 * @param b second object to be compared
		 * @return -1 if a>b, 0 if a=b, +1 if a<b
		 */
		public int compare( Object a, Object b)
		{
			Move val1 = (Move)a;
			Move val2 = (Move)b;
			if(val1.getCard() > val2.getCard())
			  return 1; 
			else if(val1.getCard() < val2.getCard())
			  return -1; 
			else 
			  return 0; 
		  }
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
