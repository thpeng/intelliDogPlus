package intelliDOG.ai.bots;

import intelliDOG.ai.evaluators.Evaluator;
import intelliDOG.ai.evaluators.SimpleEvaluatorV5;
import intelliDOG.ai.framework.BotBoard;
import intelliDOG.ai.framework.Cards;
import intelliDOG.ai.framework.InformationGatherer;
import intelliDOG.ai.framework.Move;
import intelliDOG.ai.framework.Players;
import intelliDOG.ai.framework.Rules;
import intelliDOG.ai.utils.DebugMsg;

import java.util.ArrayList;
import java.util.List;
/**
 * The SimpleBotV2 searches one step deep the possible game tree. 
 * The only difference to SimpleBot is the choice of the card to exchange. 
 * He uses a simple approach to determine the best card for his partner.
 */
public class SimpleBotV2 implements IBot {
	
	private DebugMsg msg = DebugMsg.getInstance();
	private Evaluator se = new SimpleEvaluatorV5();

	private InformationGatherer ig;
	private BotBoard bb;
	
	/**
	 * The constructor for the SimpleBot
	 * @param bb The <class>BotBoard</class> for this bot
	 * @param ig The <class>InformationGatherer</class> for this bot
	 */
	public SimpleBotV2(BotBoard bb, InformationGatherer ig){
		this.bb = bb;
		this.ig = ig;
	}
	
	/**
	 * This is the Default Constructor for the SimpleBot
	 * It is used to provide compatibility with the Bodesuri Framework
	 * Don't use this constructor outside of the Bodesuri Framework!
	 */
	public SimpleBotV2(){	
		this.ig = new InformationGatherer(Players.EMPTY);
		this.bb = new BotBoard(new byte[80], this.ig);
	}
	
	@Override
	public Move makeMove() {
		List<Move> possible = bb.getAllPossibleMoves(getPlayer());
		//reset
		Move highestMove = null;
		int highest = Integer.MIN_VALUE;
		//for each possible move..
		for(int i = 0; i < possible.size(); i++)
		{
			//..execute it
			bb.makeMove(possible.get(i), getPlayer());
			//..measure it
			int result = se.evaluate(bb.getBoard(), ig.getMyPlayer(), ig.getMyCards(), 1);
			//if it's the new best store it 
			if(highest < result)
			{
				highest = result;
				highestMove = possible.get(i);
			}
			//..finally, undo it again
			bb.undoMove(getPlayer());
		}
		if(highestMove != null){
			msg.debug(this, "Karte: " + highestMove.getCard() + ", Bewertung: " + highest + ", s: " + highestMove.getPositions()[0] + ", t: " + highestMove.getPositions()[1]);
		}
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
		this.se = eval; 
	}

	/**
	 * Performs a simple evaluation of the situation to determine the best card
	 * to give to the partner. 
	 */
	@Override
	public int exchangeCard()
	{	
		Rules rules = Rules.getInstance(); 
		byte partner = Rules.getInstance().getPartnerForPlayer(getPlayer()); 
		int[] pieces = rules.getMovablePiecesInGameForPlayer(bb.getBoard(), getPlayer(), 0);
		
		int[] cards = new int[6];
		boolean saveKingAce = false; 
		
		System.arraycopy(ig.getMyCards(), 0, cards, 0, ig.getMyCards().length);  

		// If I have two jokers then give one
		int joker = 0; 
		int ace = 0; 
		int king = 0; 
		for(int i=0; i<cards.length && cards[i] != -1; i++)
		{
			 if(cards[i] == 100)
				joker++;
			 if(cards[i]%13 == 1)
				 ace++; 
			 if(cards[i]%13 == 0)
				 king++; 
		}
		if(joker >= 2)
		{
			ig.removCard(100);
			return 100;
		}
		
		if(pieces.length == 0 && ((joker + ace + king)==1 ))
			saveKingAce = true; 
		
		int[] fakeCards = new int[]{-1,-1,-1,-1,-1,-1};
		int[] bestCards = new int[6]; 
		 
		for(int i=0; i<cards.length; i++)
		{
			if(saveKingAce && (cards[i]==100 || cards[i]%13==1 || cards[i]%13==0))
			{
					bestCards[i] = -2; 
			} else
			{
				int highestForCard = -1; 
				
				// FIXME: getPossibleMovesForCard() can't be used for a joker!
				List<Move> possible = new ArrayList<Move>(); 
				
				if(cards[i] != Cards.JOKER)
					possible = bb.getPossibleMovesForCard(cards[i], partner);
				
				for(int j=0; j<possible.size(); j++)
				{
					bb.makeMove(possible.get(j), partner); 
					// FIXME: evaluate once for partner and once for me: what's the optimum? 
					int result = se.evaluate(bb.getBoard(), partner, fakeCards, 1.0f); 
					if(result>highestForCard)
					{
						highestForCard = result;
					}
					bb.undoMove(partner); 
				}
				bestCards[i] = highestForCard;  
			}
		}// end for cards
		
		
		// Check if I need a card to go to heaven
		// remove the possibility to choose this card
		int firstHeavenField = (60+ (getPlayer() * 4)); 
		
		for(int i=0; i<pieces.length && pieces[i] < firstHeavenField; i++)
		{
			for(int j=0; j<cards.length && cards[j] != -1; j++)
			{
				boolean reachable = rules.isHeavenReachable(bb.getBoard(), pieces[i], cards[j], getPlayer());
				if(reachable)
				{
					bestCards[j] = -3; 
				}
			}
		}
	
		// loop through bestCards and search for highest value that doesn't narrow my possibilities
		int index = 0;
		boolean done = false; 
		int count = 0; 
		while(!done)
		{
			int highestVal = -100; 
			for(int i=count; i<bestCards.length && cards[i] != -1; i++)
			{
				if(bestCards[i] > highestVal && cards[i] != Cards.JOKER)
				{	
					highestVal = bestCards[i]; 
					index = i; 
					done = true; 
				}
			}
			count++; 
		}

		int bestCard = cards[index];
		
		// look for double cards: 
		if(bestCard == -1)
		{
			for(int i=0; i<bestCards.length-1 && bestCards[i] != -1; i++)
			{
				for(int j=i+1; j<bestCards.length; j++)
				{
					if(bestCards[i] == bestCards[j])
					{
						bestCard = cards[i]; 
						break; 
					}
				}
			}
		}
		assert bestCard != -1; 
		ig.removCard(bestCard);
		return bestCard;  
			
		// FIXME: estimate the value of the best cards for me
		// FIXME: Check if I need a card to hit an enemy - not done yet
	}
}
