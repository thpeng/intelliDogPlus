package intelliDOG.ai.bots;

import intelliDOG.ai.evaluators.Evaluator;
import intelliDOG.ai.evaluators.SimpleEvaluatorV5;
import intelliDOG.ai.framework.BotBoard;
import intelliDOG.ai.framework.InformationGatherer;
import intelliDOG.ai.framework.Move;
import intelliDOG.ai.framework.Players;
import intelliDOG.ai.framework.Rules;
import intelliDOG.ai.utils.DebugMsg;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;


/**
 * 
 * This class runs a simulation for a certain move but gathers first all necessary 
 * information that are needed to run the simulation. 
 *
 */
public class MCSimulation {

	/**
	 * Deal a separate set of cards for every simulation
	 */
	public static final int DEAL_CARDS_AGAIN = 0;
	/**
	 * Deal all cards at the beginning and reuse them for all simulations
	 */
	public static final int DEAL_CARDS_ONCE = 1;
	/**
	 * Use objective probability as a reference
	 */
	public static final int USE_OBJ_PROB = 2;
	/**
	 * Use objective subjective as a reference
	 */
	public static final int USE_SUBJ_PROB = 3;
	/**
	 * Use the Tournament Selection algorithm to assign cards to opponent players
	 */
	public static final int TOURNAMENT_SELECTION = 4;
	/**
	 * Use the Roulette Wheel algorithm to assign cards to opponent players
	 */
	public static final int ROULETTE_SELECTION = 5;

	/**
	 * This flag indicates if the simulation runs randomly. The default is false, what means it uses a heuristic
	 */
	private boolean random = false; 
	
	/**
	 * This flag indicates if cards are only dealt once for all simulations or every time a simulation starts
	 */
	private int card_policy = 0; 
	
	/**
	 * This flag indicates if the objective or subjective probability is used as a reference for assigning cards to opponent players
	 */
	private int prob_policy = 2; 
	
	/**
	 * This flag indicates if cards are assigned according to Tournament selection or with Roulette Wheel Selection
	 */
	private int card_selection_policy = 4; 
	
	/**
	 * Set maximal number of loops that are allowed before the restrictions for 
	 * selecting a card are reduced in order to make it easier to select a card. 
	 */
	private int MAX_LOOPS = 3; 
	
	//int nbrStuck = 0; 
	//int remove = 0, check = 0; 
	
	/**
	 * Instance of the InformationGatherer
	 */
	private InformationGatherer ig;
	
	/**
	 * An instance of the board
	 */
	private BotBoard bb; 
	
	/**
	 * Instance of Evaluator V5 in order to evaluate the state after running a simulation
	 */
	private Evaluator se = new SimpleEvaluatorV5(); 
	
	/**
	 * Instance for printing debug output
	 */
	DebugMsg msg = DebugMsg.getInstance(); 
	
	/**
	 * Current player who is running the simulation
	 */
	private byte myPlayer; 
	
	/**
	 * This list contains another list with Wrappers. 
	 */
	private ArrayList<ArrayList<Wrapper> > distributionList; 
	
	/**
	 * This list will contain all known cards at this time
	 */
	ArrayList<Integer> knownCards = null; 
	
	/**
	 * List consisting of all cards that haven't been played yet
	 */
	ArrayList<Integer> unknownCards = null; 
	
	/**
	 * Temporary array for cards that is used while assigning open card positions to opponent players
	 */
	private int[][] cards = new int[4][6];
	
	/**
	 * Contains the number of cards that have to be set for each player 
	 */
	private int[] nbrCards; 

	/**
	 * The same Random object is used during the whole simulation  
	 */
	private Random r = null; 

	/**
	 * Array to save my cards from the InformationGatherer because they will be modified during the simulation
	 */
	private int[] saveMyCards; 
	
	/**
	 * Array to save the information about bots that gave up from the InformationGatherer because that will be modified during the simulation
	 */
	private boolean[] saveGU;
	
	/**
	 * Array to save the board because it will be modified during the simulation
	 */
	private byte[] saveBoard;
	
	/**
	 * Array to save the available cards (unknownCards) because they will be modified during the simulation
	 */
	private int[] saveAvailable;

	/**
	 *  All available cards (unknown cards) will also be stored in an array in order to simplify the process 
	 */
	private int[] cardsAvailable;
	
	/**
	 * This list contains all pre-calculated card distributions in order to quickly assign generated cards to opponent players
	 */
	private ArrayList<int[][]> allDealtCards = new ArrayList<int[][]>(); 
	
	/** 
	 * Stands for the number of simulations to run
	 */
	private int nbrSim = 0; 
	
	
	/**
	 * The constructor initializes the simulation object with all necessary information
	 * @param ig InformationGatherer that belongs to the player
	 * @param b the current board
	 */
	public MCSimulation(InformationGatherer ig, BotBoard b)
	{
		this.ig = ig;
		this.bb = b; 
		myPlayer = ig.getMyPlayer(); 
		saveMyCards = new int[6]; 
		System.arraycopy(ig.getCardsForPlayer(myPlayer), 0, saveMyCards, 0, ig.getCardsForPlayer(myPlayer).length);
		saveGU = new boolean[4]; 
		System.arraycopy(ig.getBotsGaveUp(), 0, saveGU, 0, saveGU.length);
		saveBoard = new byte[80]; 
		System.arraycopy(bb.getBoard(), 0, saveBoard, 0, saveBoard.length);
	}
	
	/**
	 * The Constructor initializes the InformationGatherer and the BotBoard and precalculates the unknown cards   
	 * @param ig InformationGatherer of the current player
	 * @param b BotBoard that consists of the current state
	 */
	public MCSimulation(InformationGatherer ig, BotBoard b, int nbrSim, int deal_cards_policy, int prob_policy, int cardSelectionPolicy) {
		this.ig = ig; 
		this.bb = b; 
		this.prob_policy = prob_policy;
		this.card_policy = deal_cards_policy;
		this.card_selection_policy = cardSelectionPolicy; 
		this.nbrSim = nbrSim; 
		
		distributionList = new ArrayList<ArrayList<Wrapper> >(); 
		distributionList.add(new ArrayList<Wrapper>());
		distributionList.add(new ArrayList<Wrapper>());
		distributionList.add(new ArrayList<Wrapper>());
		myPlayer = ig.getMyPlayer(); 
		initKnownCards();
		initCardDistribution(); 
		setNbrOfCards();
		
		MAX_LOOPS = nbrCards[0]; 
		for(int i=1; i<nbrCards.length; i++)
		{
			if(MAX_LOOPS <nbrCards[i])
				MAX_LOOPS = nbrCards[i]; 
		}
		MAX_LOOPS = (MAX_LOOPS>2 ? MAX_LOOPS-=1 : MAX_LOOPS); 
		
		unknownCards = getCardDistribution(knownCards); 
		initSaveState(); 
		r = new Random(); 
		if(this.card_policy == DEAL_CARDS_ONCE)
		{
			this.allDealtCards.clear(); 

			// deal cards nbrSim times and store them
			for(int i=0; i<nbrSim; i++)
			{	
				dealToPlayers(cardsAvailable);				
				System.arraycopy(saveAvailable, 0, cardsAvailable, 0, saveAvailable.length);
			}
		}
		//msg.addItemForWhiteList(this); 
	}
	
	
	/**
	 * Stores all important information because they will be modified during the simulation. That simplifies the process of restoring the 
	 * initial state
	 */
	public void initSaveState()
	{
		saveMyCards = new int[6]; 
		System.arraycopy(ig.getCardsForPlayer(myPlayer), 0, saveMyCards, 0, ig.getCardsForPlayer(myPlayer).length);
		saveGU = new boolean[4]; 
		System.arraycopy(ig.getBotsGaveUp(), 0, saveGU, 0, saveGU.length);
		saveBoard = new byte[80]; 
		System.arraycopy(bb.getBoard(), 0, saveBoard, 0, saveBoard.length);

		cardsAvailable = initAvailableCards(unknownCards);

		saveAvailable = new int[14]; 
		System.arraycopy(cardsAvailable, 0, saveAvailable, 0, saveAvailable.length);
	}
	
	
	/**
	 * Starts a simulation for a given move
	 * @param move the move to simulate
	 * @param timeLimit stop the simulations after the limit is reached 
	 * @return returns the mean of all calculated values
	 */
	public double simulate(Move move , long timeLimit)
	{
		r = new Random(); 
		double value = 0;  

		int counter = 0; 
		long timestamp = System.currentTimeMillis(); 
		timestamp += timeLimit; 
		
		if(this.card_policy == DEAL_CARDS_ONCE)
		{	
			while(timestamp >= System.currentTimeMillis())
			{
				counter++; 

				// FIXME
				ig.setAllCards(allDealtCards.get(counter)); 
				int val = 0; 
				if(random)
					val = finishRandomGame(move);
				else
					val = finishGame(move);

				value += val; 
			}
			
		// DEAL_CARDS_AGAIN
		} else {
		
			while(timestamp >= System.currentTimeMillis())
			{
				counter++; 
				dealToPlayers(cardsAvailable);  
				int val = 0; 
				if(random)
					val = finishRandomGame(move);
				else
					val = finishGame(move);
				value += val; 
				ig.setCardsForPlayer(saveMyCards, myPlayer); 
				System.arraycopy(saveGU, 0, ig.getBotsGaveUp(), 0, saveGU.length);
				System.arraycopy(saveBoard, 0, bb.getBoard(), 0, saveBoard.length);
				System.arraycopy(saveAvailable, 0, cardsAvailable, 0, saveAvailable.length);
				// used to clear moveStack
				bb.setBoard(bb.getBoard()); 
			}
		}
		assert myPlayer == ig.getMyPlayer(); 
		return value / counter; 
	}
	
	
	/**
	 * Starts a simulation for a given move. The policy decides whether the cards are dealt only once and stored in order
	 * to reuse the values for all simulations or the cards are dealt for every simulation. 
	 *  
	 * @param nbrSim number of simulation before returning the result
	 * @param move the move to simulate
	 * @param policy DEAL_CARDS_ONCE or DEAL_CARDS_AGAIN  
	 * @return returns the mean of all calculated values
	 */
	public double simulate(int nbr, Move move)
	{
		double value = 0;  
		r = new Random(); 
		int rem = 0; 
	
		// FIXME: in case of using DEAL_CARDS_ONCE, nbrSim have to be defined in constructor (number of sample distribution to be generated)
		// It's up to the user to not define a smaller number for nbr in this method than he defined in ctor. 
		// FIXME: add check for that
		if(this.card_policy == DEAL_CARDS_ONCE)
		{		
			for(int i=0; i<nbr; i++)
			{
				rem++; 
				ig.setAllCards(allDealtCards.get(i)); 
				

				int val = 0; 
				if(random)
					val = finishRandomGame(move);
				else
					val = finishGame(move);

				value += val; 
				ig.setCardsForPlayer(saveMyCards, myPlayer); 
				System.arraycopy(saveGU, 0, ig.getBotsGaveUp(), 0, saveGU.length);
				System.arraycopy(saveBoard, 0, bb.getBoard(), 0, saveBoard.length);	
				// used to clear moveStack
				bb.setBoard(bb.getBoard()); 
			}
			
		// DEAL_CARDS_AGAIN
		} else {
		
			for(int i=0; i<nbr; i++)
			{
				rem++; 
				dealToPlayers(cardsAvailable); 
				int val = finishGame(move);

				value += val; 
				ig.setCardsForPlayer(saveMyCards, myPlayer); 
				System.arraycopy(saveGU, 0, ig.getBotsGaveUp(), 0, saveGU.length);
				System.arraycopy(saveBoard, 0, bb.getBoard(), 0, saveBoard.length);
				System.arraycopy(saveAvailable, 0, cardsAvailable, 0, saveAvailable.length);
				// used to clear moveStack
				bb.setBoard(bb.getBoard()); 
			}
		}
		assert myPlayer == ig.getMyPlayer(); 
		return (value/rem);  
	}
	
	/**
	 * Finish a round with the current move with the given heuristic and return the result
	 * @param move the first move of the player on turn
	 * @return the evaluation result from my point of view
	 */
	public int finishGame(Move move)
	{
		// My first move
		bb.makeMove(move, myPlayer); 
		
		if(hasWon(bb.getBoard(), myPlayer))
		{
			int res = se.evaluate(bb.getBoard(), ig.getMyPlayer(), /*ig.getMyCards()*/new int[]{-1,-1,-1,-1,-1,-1}, 1);
			return 	res; 
		}
		
		int score = 0; 
		byte currentPlayer = getNextPlayer(myPlayer); 
		
		int nbrCards=0; 
		for(int i=0; i<4; i++)
		{// FIXME: use k's as a measurment
			int count = ig.getNumberOfCards((byte)(i+1));
			if(count > nbrCards)
				nbrCards = count; 
		}
		while(nbrCards > 0)
		{
			for(int i=0; i<4; i++)
			{
				if(hasWon(bb.getBoard(), currentPlayer))
				{
					int res = se.evaluate(bb.getBoard(), ig.getMyPlayer(), /*ig.getMyCards()*/new int[]{-1,-1,-1,-1,-1,-1}, 1);
					return 	res; 
				}
				if(!ig.getPlayerGaveUp(currentPlayer))
				{
					List<Move> moves = bb.getAllPossibleMoves(currentPlayer);
					Move m = chooseMove(moves, currentPlayer);
					if(m == null)
						ig.setBotGaveUp(currentPlayer); 
					if(m != null && !ig.getPlayerGaveUp(currentPlayer))
					{
						bb.makeMove(m, currentPlayer);
					}
				}
				currentPlayer = getNextPlayer(currentPlayer); 
			}
			nbrCards--; 
		}
	
		// now evaluate state
		score = se.evaluate(bb.getBoard(), ig.getMyPlayer(), /*ig.getMyCards()*/new int[]{-1,-1,-1,-1,-1,-1}, 1);
		return score; 
	}

	/**
	 * Finish a round with the current move randomly and return the result
	 * @param move the first move of the player on turn
	 * @return the evaluation result from my point of view
	 */
	public int finishRandomGame(Move move)
	{
		//r = new Random(); 
		// My first move
		bb.makeMove(move, myPlayer); 
		if(hasWon(bb.getBoard(), myPlayer))
		{
			int res = se.evaluate(bb.getBoard(), ig.getMyPlayer(), new int[]{-1,-1,-1,-1,-1,-1}, 1);
			return 	res; 
		}
		int score = 0; 
		byte currentPlayer = getNextPlayer(myPlayer); 
		
		int nbrCards=0; 
		for(int i=0; i<4; i++)
		{// FIXME: use k's as a measurment
			int count = ig.getNumberOfCards((byte)(i+1));
			if(count > nbrCards)
				nbrCards = count; 
		}
		while(nbrCards > 0)
		{
			for(int i=0; i<4; i++)
			{
				if(hasWon(bb.getBoard(), currentPlayer))
				{
					int res = se.evaluate(bb.getBoard(), ig.getMyPlayer(), new int[]{-1,-1,-1,-1,-1,-1}, 1);
					return 	res; 
				}
				if(!ig.getPlayerGaveUp(currentPlayer))
				{
					List<Move> moves = bb.getAllPossibleMoves(currentPlayer);
					Move m = null; 
					
					if(!moves.isEmpty())
					{
						int index = r.nextInt(moves.size()); 
						m = moves.get(index);
					}
					if(m == null)
						ig.setBotGaveUp(currentPlayer); 
					if(m != null && !ig.getPlayerGaveUp(currentPlayer)) 
					{
						bb.makeMove(m, currentPlayer);
					}
				}
				currentPlayer = getNextPlayer(currentPlayer); 
			}
			nbrCards--; 
		}
		
		// now evaluate state
		score = se.evaluate(bb.getBoard(), ig.getMyPlayer(),  /*ig.getMyCards()*/new int[]{-1,-1,-1,-1,-1,-1}, 1);
		return score; 
	}
	
	/**
	 * Deal cards to all players according to the card distribution and the precalculated values in the list of Wrappers.
	 * @param tmp list of available cards that could be distributed
	 */
	public void dealToPlayers(int[] availableCards)
	{
		if(card_selection_policy == ROULETTE_SELECTION)
			rouletteWheelSelection(availableCards); 
		else if(card_selection_policy == TOURNAMENT_SELECTION)
			tournamentSelection(availableCards, 2); 
	}
	
	
	/**
	 * Deal cards to all players according to the card distribution by using Roulette Wheel Selection
	 * and the precalculated values in the list of Wrappers.
	 * @param tmp list of available cards that could be distributed
	 */
	private void rouletteWheelSelection(int[] tmp)
	{
		// fill cards[][] for every player
		cards = new int[4][6]; 
		
		boolean doneExCard = false; 
		byte player = getNextPlayer(myPlayer); 
		byte partner = Rules.getInstance().getPartnerForPlayer(myPlayer); 
		
		
		// first set exchanged card for partner
		if( !ig.getPlayerGaveUp(partner) && ig.getExchangedCard()[0] != -1)
		{
			int c = ig.getExchangedCard()[0];
			if(c == 100)
				cards[partner-1][0] = 100; 
			else if((c%13)==0)
				cards[partner-1][0] = 13; 
			else
				cards[partner-1][0] = (c%13);
			
			int index = getIndex(c); 
			assert index >= 0 && index <= 13; 
			
			tmp[index] -= 1;
			assert tmp[index] >= 0;
			doneExCard = true; 
		}
		
		for(int i=(byte)(player), wr = 0; i != myPlayer; i=player=(getNextPlayer(player)), wr++ )
		{		
			int loops = 0; 
			
			if(ig.getPlayerGaveUp((byte)(i)) || nbrCards[player-1] == 0 )
			{
				for(int j = 0; j < 6; j++)
					this.cards[i-1][j] = -1;
						
			}else
			{
				// start with index 1 in case I set the exchanged card to cards[0]
				for(int j= (doneExCard && i==partner ? 1 : 0); j< nbrCards[i-1]; j++)
				{
					boolean done = false; 
					loops = 0; 
					
					while(!done)
					{
						int c = selectCard(distributionList.get(wr), loops);

						if(c != -1 && tmp[c-1] > 0 && !hasEnough(c, i, loops))
						{
							cards[i-1][j] = (c==14?100:(c));  
							tmp[c-1] -= 1;  
							done = true; 
						}
						if(!done)
						{
							loops++; 
						}
					}
				}
		
				for(int j = nbrCards[i-1]; j < 6; j++){
					this.cards[i-1][j] = -1;
				}
			}
			ig.setCardsForPlayer(cards[i-1], (byte)(i)); 
		}
		if(card_policy == DEAL_CARDS_ONCE)
		{ 
			allDealtCards.add(ig.getAllCards());
			
			// debug
//			for(int i=0; i<ig.getAllCards().length; i++)
//			{
//				System.out.print("P: " + (i+1) + "["); 
//				int[] tmp2 = ig.getAllCards()[i]; 
//				for(int j=0; j<tmp2.length; j++)
//					System.out.print(tmp2[j] + (j==tmp2.length-1 ? "] " : ", ")); 
//				System.out.println(); 
//			}
//			System.out.println(); 
		}
	}
	
	/**
	 * Deal cards to all players according to the card distributions by using Tournament Selection
	 * and the precalculated values in the list of Wrappers.
	 * @param tmp list of available cards that could be distributed
	 */
	private void tournamentSelection(int[] tmp, int tournament_size)
	{
		cards = new int[4][6]; 
		
		boolean doneExCard = false; 
		byte player = getNextPlayer(myPlayer); 
		byte partner = Rules.getInstance().getPartnerForPlayer(myPlayer); 
		
		// first set exchanged card for partner
		if( !ig.getPlayerGaveUp(partner) && ig.getExchangedCard()[0] != -1)
		{
			int c = ig.getExchangedCard()[0];
			if(c == 100)
				cards[partner-1][0] = 100; 
			else if((c%13)==0)
				cards[partner-1][0] = 13; 
			else
				cards[partner-1][0] = (c%13);
			
			int index = getIndex(c); 
			assert index >= 0 && index <= 13; 
			
			tmp[index] -= 1;
			assert tmp[index] >= 0;
			doneExCard = true; 
		}
		
		for(int i=(byte)(player), wr = 0; i != myPlayer; i=player=(getNextPlayer(player)), wr++ )
		{		
			int loops = 0; 
			if(ig.getPlayerGaveUp((byte)(i)) || nbrCards[player-1] == 0 )
			{
				for(int j = 0; j < 6; j++)
					this.cards[i-1][j] = -1;
						
			}else
			{
				ArrayList<Wrapper> tmpList = distributionList.get(wr); 
				tournament_size = (tournament_size > tmpList.size() ? tmpList.size() : tournament_size); 
				
				for(int j= (doneExCard && i==partner ? 1 : 0), l=0; j< nbrCards[i-1]; j++, l++)
				{

					boolean done = false; 
					loops = 0; 
					
					while(!done)
					{
						double best = -1;
						int best_Card = -1;
						
						for (int c = 0; c < tournament_size; c++)
						{
						    int index = (int)(Math.random() * tmpList.size());
						    
						    if(tmpList.get(index).key > best)
						    {
						    	best = tmpList.get(index).key;
						    	best_Card = tmpList.get(index).pickRandom(); 
						    }
						}
						
						if(best_Card != -1 && tmp[best_Card-1] > 0 && !hasEnough(best_Card, i, loops))
						{
							cards[i-1][j] = (best_Card==14?100:(best_Card));  
							tmp[best_Card-1] -= 1;  
							done = true; 
						}
						if(!done)
						{
							loops++; 
						}
					}
				}
		
				for(int j = nbrCards[i-1]; j < 6; j++){
					this.cards[i-1][j] = -1;
				}
			}
			ig.setCardsForPlayer(cards[i-1], (byte)(i)); 
		}
		if(card_policy == DEAL_CARDS_ONCE)
		{ 
			allDealtCards.add(ig.getAllCards());
			
			// debug
//			for(int i=0; i<ig.getAllCards().length; i++)
//			{
//				System.out.print("P: " + (i+1) + "["); 
//				int[] tmp2 = ig.getAllCards()[i]; 
//				for(int j=0; j<tmp2.length; j++)
//					System.out.print(tmp2[j] + (j==tmp2.length-1 ? "] " : ", ")); 
//				System.out.println(); 
//			}
//			System.out.println(); 
		}
	}
	
	/**
	 * Tests if a player already holds enough of a kind of card. The number "loops" stands for the restriction to not
	 * allow more than one kind in players hand except MAX_LOOPS+2 is reached  
	 * @param card the card to test if the player already holds the card
	 * @param player the current player on turn
	 * @param loops number of loops
	 * @return true if the player holds the card, false otherwise
	 */
	public boolean hasEnough(int card, int player, int loops)
	{
		int[] cards = this.cards[player-1]; 
		int count = 0; 
		for(int i=0; i<cards.length && cards[i] != 0; i++)
		{
			if(cards[i] == card || (cards[i] == 100 && card == 14) )
				count++; 
		}
		boolean ret = loops > (MAX_LOOPS+2) ?  (count >= 2) :(count >=1);
		return ret; 
	}
	
	/**
	 * Clear the whole list of information about card distributions
	 */
	public void clearDistributionList() { 
		for(int i=0; i<distributionList.size(); i++)
			distributionList.get(i).clear(); 
	}
	
	/**
	 * Copy the used cards from the InformationGatherer to knownCards
	 */
	public void initKnownCards()
	{
		knownCards = new ArrayList<Integer>(ig.getUsedCardsList());
		for(int i=0; i<ig.getMyCards().length && ig.getMyCards()[i] != -1; i++)
			knownCards.add(ig.getMyCards()[i]); 
	}
	
	

	/**
	 * Calculate the cards that are available at the current time considering the cards
	 * my player holds
	 * @param knownCards list of cards that already were played. This information was taken from the InformationGatherer
	 * @return returns difference of all cards and the known cards
	 */
	// Calculate cardStock: cards that are available to randomly distribute to other players
	public ArrayList<Integer> getCardDistribution(ArrayList<Integer> knownCards) 
	{
		ArrayList<Integer> allCards = getAllCards(); 
		return getDifference(allCards, knownCards);
	}

	
	/**
	 * Calculate all cards in a game. That will be 112 cards
	 * @return return the set of all cards
	 */
	public ArrayList<Integer> getAllCards()
	{
		ArrayList<Integer> sortedCards = new ArrayList<Integer>(112);
		//add 8 jokers
		for(int i = 0; i < 8; i++){
			sortedCards.add(100);
		}
		//add each card twice
		for(int i = 1; i <= 52; i++){
			sortedCards.add(i);
			sortedCards.add(i);
		}
		return sortedCards; 
	}
	
	
	/**
	 * Calculate the difference of the cards of a standard dog game and the known cards that were already played
	 * @param allCards all cards in the game
	 * @param knownCards cards that are known to be played or my player currently holds
	 * @return return the set of cards that are still available in the game
	 */
	public ArrayList<Integer> getDifference(ArrayList<Integer> allCards, ArrayList<Integer> knownCards)
	{
		Collections.sort(allCards);
		Collections.sort(knownCards);
		
		ArrayList<Integer> usableCards = new ArrayList<Integer>(); 
		usableCards.addAll(allCards); 
		
		for(int i=0; i<allCards.size(); i++)
		{
			if( !knownCards.isEmpty() && ( allCards.get(i) == knownCards.get(0) || (allCards.get(i)==100 && knownCards.get(0)>52) ) )
			{
				knownCards.remove(0); 	
				usableCards.remove(allCards.get(i)); 
			}
		}
		return usableCards; 
	}
	
	/**
	 * Store the number of cards each player currently holds
	 */
	public void setNbrOfCards()
	{
		nbrCards = new int[4]; 
		nbrCards[0] = ig.getNumberOfCards((byte)Players.P1);
		nbrCards[1] = ig.getNumberOfCards((byte)Players.P2); 
		nbrCards[2] = ig.getNumberOfCards((byte)Players.P3); 
		nbrCards[3] = ig.getNumberOfCards((byte)Players.P4);  
	}
	
	/**
	 * Initialize the distribution of cards for every player
	 */
	public void initCardDistribution()
	{
		byte pl = ig.getMyPlayer(); 
		for(int i=0; i<distributionList.size(); i++)
		{
			initProbDistribution(distributionList.get(i), getNextPlayer(pl)-1);
			pl = getNextPlayer(pl); 
		}
	}

	/**
	 * Calculate the probability of a player's cards regarding the different kind of probabilities of the cards
	 * All keys of the list add up to 1 in order to perform tournament selection
	 * Every Wrapper object consists of a key and a list of the cards belonging to the key 
	 * @param c_dist list of distribution of cards for a given player
	 * @param player 
	 */
	public void initProbDistribution(ArrayList<Wrapper> c_dist, int player)
	{
		double sum = 0.0;
		double[] prob;
		
		if(prob_policy == USE_OBJ_PROB)
			prob = ig.getProb()[player];
		else // (prob_policy == USE_SUBJ_PROB)
			prob = ig.getProbSubjective()[player];
		
		int nbrEntry = 0; 
		
		boolean ignoreJoker = (prob[13]>=0.2 ? false : true); 
		
		for(int i=0; i<(ignoreJoker ? prob.length-1 : prob.length); i++)
		{
			if(prob[i] != 1.0 && prob[i] != 0.0)
			{
				int ind = c_dist.indexOf(new Wrapper((prob[i]))); 
				if(ind != -1)
					c_dist.get(ind).add(i+1); 
				else {
					Wrapper w = new Wrapper(prob[i]);
					w.add(i+1); 
					c_dist.add(w);
					nbrEntry++; 
				}
			}
		}

		Comparator comp = new Comp();
		Collections.sort(c_dist, comp);

		// Only normalize to one in case roulette wheel selection is chosen
		if(card_selection_policy == ROULETTE_SELECTION)
		{
			// for tournament selection
			for(int i=c_dist.size()-1, nbr=nbrEntry, cc=2; i>=0; i--, nbr--, cc++)
			{
				if(i==0)
				{
					//wr.get(i).key = 1.0/nbrEntry; 
					// just for checking that it adds up to 1.0
					double total = 0.0; 
					for(int j=nbrEntry; j>=1; j--)
						total += 1.0/(nbrEntry*j); 
					double mustBeOne = total + sum; 
					assert mustBeOne >= 0.98 || mustBeOne <= 1.02; 
					c_dist.get(i).key = total; 
				} else {
					// FIXME: (i==0) can be combined
					//double tmp = 1.0/nbrEntry/nbr;  
					double tmp = 1.0/nbrEntry/cc;
					double tmp2 = 1.0/nbrEntry - tmp; 
					sum += tmp2;
					c_dist.get(i).key = tmp2;
				}
			}
		}
	}

	
	/**
	 * 
	 * This class consists of a key and a list with cards that belong to that key. 
	 * The key symbols the percentage of the card distribution. It is not the real value but a value proportional to 
	 * the number of different Wrapper classes that exists. 
	 * 
	 */
	class Wrapper 
	{
		private double key; 
		private List<Integer> values = new ArrayList<Integer>();  

		@Override
		public boolean equals(Object o1){
			if ( this == o1 ) return true;
			if ( !(o1 instanceof Wrapper) ) return false;
			Wrapper that = (Wrapper)o1;
			return that.key == this.key; 
		}

		public Wrapper(double val) { this.key = val; }
		public void add(int key) { 
			values.add(key); 
		}

		public int getNbrElements() { return this.values.size(); }
		public int pickRandom() {
			int index = r.nextInt(values.size()); 
			int rand = values.get(index);
			return rand;	 
		}
		
		public void shakeList()
		{
			List<Integer> tmpValues = new ArrayList<Integer>(this.values);  
			List<Integer> values = new ArrayList<Integer>();  

			while(!tmpValues.isEmpty())
			{
				int index = 0; 
				if(tmpValues.size() !=1)
				{
					index = r.nextInt(tmpValues.size());
				}
				values.add(tmpValues.get(index));
				tmpValues.remove(index);
			}
			this.values = new ArrayList<Integer>(values); 
		}
	}

	/**
	 * Select a card for a player with the tournament selection 
	 * @param c_dist the list with stored cards and their keys
	 * @param loops number of loops
	 * @return return the card that was selected
	 */
	public int selectCard(ArrayList<Wrapper> c_dist, int loops)
	{
		double random = r.nextDouble(); 
		double offset = 0.0; 
		
		double total = 0.0;  
		int selection = -1; 
		
		if(loops < MAX_LOOPS /*&& wr.size() >= 4 */)
			offset = 0.2; 
//		else if(loops < MAX_LOOPS /*&& wr.get(0).values.size() > 3 */)
//			offset = 0.15; 
		
		for(int i=0; i<c_dist.size(); i++)
		{
			double d = c_dist.get(i).key;	
			if(i==0)
				d += offset; 
			
			total += d; 
			
			if(random <= total)
			{
				selection = c_dist.get(i).pickRandom();
				assert selection != -1; 
				break; 
			}
		} 
		return selection;   
	}
	
	/**
	 * Copy the list of unknown cards to an array that rather considers the value of the card than the kind
	 * @param unknownCards list of unknown cards
	 * @return array of unknown cards (information about kind of card is lost)  
	 */
	public int[] initAvailableCards(ArrayList<Integer> unknownCards)
	{
		int[] tmp = new int[14]; 
		for(int i=0; i<unknownCards.size(); i++)
		{
			int u = unknownCards.get(i);
			int index = (u%13); 
			if(u > 52) 
				index = 13;
			else if(index == 0) 
				index = 12; 
			else 
				index = index-1; 
			
			assert tmp[index] >= 0; 
			tmp[index] += 1;
			assert tmp[index] <= 8;
			assert tmp[index] >= 0; 
		}
		return tmp; 
	}
	
	/**
	 * Convert the card to match the index in the array
	 * @param c the card to be considered
	 * @return the index 
	 */
	public int getIndex(int c)
	{ 
		int index = c%13; 
		if(c == 100)
			return 13; 
		else if(index == 0)
			return 12; 
		else
			return (index-1); 
	}
	
	
	class Comp implements Comparator
	{
		@Override
		public int compare(Object arg0, Object arg1) {
			Wrapper val1 = (Wrapper)arg0;
			Wrapper val2 = (Wrapper)arg1;
			if(val1.key < val2.key)
				return 1; 
			else if(val1.key > val2.key)
				return -1; 
			else 
				return 0; 
		}
	}
	

	public byte getNextPlayer(byte currentPlayer){
		byte nextPlayer = (byte)((currentPlayer + 1) % 5);
		return nextPlayer == 0 ? 1 : nextPlayer;
	}
	
	
	/**
	 * Choose the best moves for a certain player. It will use the heuristics of EvaluatorV5
	 * @param moves list of moves
	 * @param player current player on turn
	 * @return return the best move according to the heuristic
	 */
	public Move chooseMove(List<Move> moves, byte player)
	{
		int highest = Integer.MIN_VALUE; 
		Move highestMove = null; 
		
		for(int i = 0; i < moves.size(); i++)
		{
			bb.makeMove(moves.get(i), player);
			
			int result = se.evaluate(bb.getBoard(), player, ig.getCardsForPlayer(player), 1);
			//if it's the new best store it 
			if(highest < result)
			{
				highest = result;
				highestMove = moves.get(i);
			}
			
			bb.undoMove(player);
		}
		return highestMove; 
	}
	
	/**
	 * Set the card distributions that will be used for the simulation 
	 * @param cards cards to deal
	 */
	public void setDealtCards(ArrayList<int[][]> cards)
	{
		this.allDealtCards = cards; // FIXME: copy? 
	}
	
	/**
	 * Return the dealt cards 
	 * @return Return the card distributions 
	 */
	public synchronized ArrayList<int[][]> getDealtCards()
	{
		ArrayList<int[][]> tmpList = new ArrayList<int[][]>(); 
		
		for(int i=0; i<this.allDealtCards.size(); i++)
		{
			tmpList.add(allDealtCards.get(i).clone());  
		}
		return tmpList; 
	}
	
	/** 
	 * Set this flag in case the simulation should finish a game randomly. This flag is not set by default. 
	 * That means the simulation would finish with the heuristic. 
	 * @param r if true, the simulation will be run randomly 
	 */
	public void setRandom(boolean r)
	{
		this.random = r; 
	}
	
	/**
	 * Return the card policy 
	 * @return the card policy that is currently active
	 */
	public int getCardPolicy()
	{
		return this.card_policy; 
	}
	
	/**
	 * Set card policy 
	 * @param cardPolicy
	 */
	public void setCardPolicy(int cardPolicy)
	{
		this.card_policy = cardPolicy; 
	}
	
	// remove - add to helper
	// FIXME
	
	/**
	 * Test if a player won the game
	 */
	public boolean hasWon(byte[] bb, byte player)
	{
		Rules rules = Rules.getInstance(); 
		
		if(rules.allPiecesInHeavenOfPlayer(bb, player)
				&& rules.allPiecesInHeavenOfPlayer(bb, rules.getPartnerForPlayer(player)))
		{
			return true;
		}
		return false; 
	}
	
	/** 
	 * Set the board 
	 * @param board current state of the game
	 */
	public void setBoard(BotBoard board)
	{
		this.bb = board; 
	}
}


