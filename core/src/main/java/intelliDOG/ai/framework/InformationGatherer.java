package intelliDOG.ai.framework;

import intelliDOG.ai.evaluators.SimpleEvaluatorV5;
import intelliDOG.ai.utils.DebugMsg;
import intelliDOG.ai.utils.Helper;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * This class will gather all needed information for one bot.
 *
 */
public class InformationGatherer {

	private DebugMsg msg = DebugMsg.getInstance(); 
	private Helper helper = Helper.getInstance(); 
	private int cardsLeft = 112; 
	private int[][] cards;
	private byte myPlayer;
	private double[] entropy = new double[4]; 
	private ArrayList<Integer> listUsedCards = new ArrayList<Integer>(); 
	
	/**
	 * Stores the last four moves that were played
	 */
	private Move[]  moveList = new Move[4];
	
	/**
	 * Stores all cards that were played.
	 */
	private int[] usedCards; 
	
	/**
	 * Stores all card probabilities for every player from the point of view of myPlayer.
	 */
	private double[][] prob;  
	
	/**
	 * Stores all card probabilities for every player from the point of view of myPlayer.
	 * Additional knowledge about played cards are taken into consideration 
	 */
	private double[][] probSubjective;  
	
	/**
	 * Temporary array to store difference between prob and probSubjective 
	 */
	private double[][] tmpProb = new double[4][14]; 
	
	/**
	 * Array to indicate what players gave up.
	 */
	private boolean[] botsGaveUp = new boolean[] {false,false,false,false};
	
	/**
	 * This flag indicates that probability calculations are desired. 
	 */
	private boolean enableProbability = false; 
	
	/**
	 * This map contains user-defined values that will be added to subjective probability
	 */
	private HashMap<String, Double> percValues = new HashMap<String, Double>(); 
	
	/**
	 * Save exchanged card: 
	 * The first index stands for the card I gave to my partner.
	 * The second index stands for the card I received from my partner. 
	 */
	private int[] exchangedCard;
	
	private Rules rules = Rules.getInstance(); 
	
	public InformationGatherer(byte myPlayer){
		this.cards = new int[4][6];
		this.myPlayer = myPlayer;
		this.usedCards = new int[14];
		this.prob = new double[4][14];
		this.probSubjective = new double[4][14];
		this.exchangedCard = new int[]{-1,-1};
		// self defined values
		fillSDV(); 
		//msg.addItemForWhiteList(this); 
	}
	
	/**
	 * add the used card to the existing array. This array contains one entry for each
	 * card from 1,2,...,Q,K,A,J and increments the value by one until all eight cards are gone.
	 * @param u used card  
	 */
	public void setUsedCards(int u)
	{
		//assert u != Cards.JOKER;
		//assert u >= 0; //FIXME: STAR2BOT_V2 asserts 
		assert u > 0; //FIXME: STAR2BOT_V2 asserts 
	
		int index = (u%13); 
		
		if(u > 52) 
			index = 13;
		else if(index == 0) 
			index = 12; 
		else 
			index = index-1; 
	
		// FIXME: can happen because of bug # 23
		// should never be more than 8 
		if(usedCards[index] < 8)
			usedCards[index] += 1; 

		assert usedCards[index] <= 8; // FIXME: star2botv2 asserts
		
		cardsLeft -= 1; 
	}

	/**
	 * Decrement the value of this used card by one. That means, there is a additional card
	 * of this kind in the game.  
	 * @param u card to restore
	 */
	public void restoreUsedCards(int u)
	{
		int index = (u%13);
		if(u > 52)
			index = 13; 
		else if(index == 0)
			index = 12; 
		else
			index = index - 1; 
		
		if(u > 0)
		{
			usedCards[index] -= 1;
		}
		cardsLeft += 1;
		
	}

	/**
	 * This method is called from Game and updates the array of used cards for
	 * every bot. 
	 * @param l list containing all used cards in the game
	 */
	public void distributeUsedCards(List<Integer> l)
	{
		if(enableProbability)
		{
			usedCards  = new int[14]; 
			prob = new double[4][14];
			listUsedCards = new ArrayList<Integer>(l); 

			for(int i=0; i<l.size(); i++)
				setUsedCards(l.get(i));
		
			// add my cards to the list
			int[] myCards = getCardsForPlayer(myPlayer); 
			
			int nbrCards = 0; 
			for(int i=0; i<myCards.length && myCards[i] != -1; i++)
			{
				nbrCards++; 
				setUsedCards(myCards[i]); 
			}
			
			// add exchanged card
			if(exchangedCard[0] != -1) {
				nbrCards++; 
				setUsedCards(exchangedCard[0]); 
			}
			cardsLeft = 112 - l.size() - nbrCards;  
			calcProb();  
		}
	}
	
	/**
	 * Return length of probability array 
	 * @return length of prob
	 */
	public int getProbSize()
	{
		return prob.length; 
	}
	
	/**
	 * Calculate probability for every kind of card, how likely it is, that somebody
	 * would play this card.
	 * Every kind consists of 8 cards.    
	 */
	public void calcProb()
	{
//		msg.debug(this, " From point of view of player:  " + myPlayer + "  (cardsLeft): " + cardsLeft);
		
		byte player = getNextPlayer(myPlayer); 
		boolean copy = (getNumberOfCards(player) == getNumberOfCards(rules.getPartnerForPlayer(player)) ); 
		
		for(int count = 0; player != myPlayer; player = getNextPlayer(player), count++)
		{
			int checkNbrCards = 0; 
			int k = getNumberOfCards(player);
			if(k > 0 && !getPlayerGaveUp(player))
			{
				// copy values for second player
				if(copy && (count == 2) )
				{
					byte prev = rules.getPartnerForPlayer(player); 
				//	msg.debug(this, "copy for player: " + player);
					for(int i=0; i<prob[player-1].length; i++)
						prob[player-1][i] = prob[prev-1][i]; 	
				} else
				{
				//	msg.debug(this, "player: " + player);

					if(k != 1 && player == rules.getPartnerForPlayer(myPlayer) && exchangedCard[0] != -1 )
					{
						k -= 1; 
					}
					
					for(int i=0; i<prob[player-1].length; i++)
					{
						int nbrCards =  8 - usedCards[i];
						checkNbrCards += nbrCards;
						// FIXME: asserts 
						// assert ( (cardsLeft - nbrCards) >= k);
						
						if((cardsLeft - nbrCards) >= k)
						{ 
							long comb1 = combination(cardsLeft-nbrCards, k); 
							long comb2 = combination(cardsLeft, k); 
							double d = 1.0 - ((double)comb1/(double)comb2); 

							prob[player-1][i] = d; 
							if(d == 0.0)
								msg.debug(this, "card: " + (i+1) + " - " + d);
						}else {
							// k>n -ni --> P = 1
							prob[player-1][i] = 1.0; 
						}
					}
					//bug # 23?
					//assert checkNbrCards == cardsLeft; 
				}
				
				// consider exchanged card
				if(exchangedCard[0] != -1 && player == rules.getPartnerForPlayer(myPlayer))
				{
					int index = (exchangedCard[0]%13); 

					if(exchangedCard[0] > 52) 
						index = 13;
					else if(index == 0) 
						index = 12; 
					else 
						index = index-1; 

					prob[player-1][index] = 1.0; 	
				}
			}
			
		} // end for
		
		// set my cards
		int[] myCards = cards[myPlayer-1]; 

		for(int i=0; i<myCards.length && myCards[i] != -1; i++)
		{
			int index = myCards[i]%13; 

			if(myCards[i] > 52) 
				index = 13;
			else if(index == 0) 
				index = 12; 
			else 
				index = index-1; 

			prob[myPlayer-1][index] = 1.0; 
		}
	}

	
	/** 
	 * Verify if all cards of one kind (f.e. all kings) are played.
	 * @param c card to check
	 * @return true if all cards are played, false otherwise
	 */
	public boolean allGone(int c)
	{
		int index = c%13; 
		if(c > 52) 
			index = 13;
		else if(index == 0) 
			index = 12; 
		else 
			index = index-1;
		
		return usedCards[index]>= 8; 
		
		// FIXME: if all gone: prob is zero --> check for that
	}
	
	public double[][] getProb()
	{
		return prob; 
	}
	
	/**
	 * Return probability of card, how likely it is, that the enemy plays it
	 * @param c the card to be verified
	 * @return probability of this card
	 */
	public double getProb(int c)
	{
		return prob[getMyPlayer()-1][c-1]; // FIXME: use right player in bots
	}
	

	/**
	 * Test if a certain player gave up. 
	 */
	public boolean getPlayerGaveUp(byte player)
	{
		return botsGaveUp[player-1]; 
	}
	
	/**
	 * Mark player that he gave up.
	 * @param player who gave up
	 */
	public void setBotGaveUp(byte player)
	{
		botsGaveUp[player-1] = true; 
	}
	
	/**
	 * Players who gave up has to be cleaned, when starting a new round. 
	 * 
	 */
	public void cleanBotsGaveUp()
	{
		botsGaveUp = new boolean[]{false, false, false, false}; 
	}
	
	/**
	 * Return array botsGaveUp
	 */
	public boolean[] getBotsGaveUp()
	{
		return botsGaveUp; 
	}
	
	/**
	 * Set array botsGaveUp
	 */
	public void setBotsGaveUp(boolean[] b)
	{
		botsGaveUp = b; 
	}
	/**
	 * Return array of cards of my player
	 * @return cards of my player
	 */
	public int[] getMyCards(){
		return getCardsForPlayer(myPlayer); // FIXME: use systemArrayCopy
	}
	
	/**
	 * Return array of cards for any player
	 * @param playerOnTurn 
	 * @return cards for playerOnTurn
	 */
	public int[] getCardsForPlayer(byte playerOnTurn) {
		return this.cards[playerOnTurn - 1];
	}
	
	/**
	 * Set cards for a player
	 * @param cards 
	 * @param playerOnTurn
	 */
	public void setCardsForPlayer(int[] cards, byte playerOnTurn) {
		System.arraycopy(cards, 0, this.cards[playerOnTurn-1], 0, cards.length); 
	}

	/**
	 * Set cards for all players
	 * @param cards 
	 */
	public void setAllCards(int[][] cards) {
		for(int i=0; i<this.cards.length; i++)
			System.arraycopy(cards[i], 0, this.cards[i], 0, cards[i].length); 
	}
	
	/**
	 * Get all cards
	 */
	public int[][] getAllCards() {
		int[][] ret = new int[4][6]; 
		for(int i=0; i<this.cards.length; i++)
				System.arraycopy(this.cards[i], 0, ret[i], 0, cards[i].length); 
		return ret;  
	}
	
	/**
	 * @return return my player
	 */
	public byte getMyPlayer() {
		return myPlayer;
	}

	/**
	 * @param myPlayer the myPlayer to set
	 */
	public void setMyPlayer(byte myPlayer) {
		this.myPlayer = myPlayer;
	}
	

	/**
	 * verify if the player has still a joker in his set of cards
	 * @return true if the player has a joker, false otherwise
	 */
	public boolean hasJokerOrSeven()
	{
		int[] tmp = new int[6]; 
		tmp = getMyCards(); 
		
		for(int i=0; i<tmp.length; i++)
		{
			if(tmp[i] == 100 || (tmp[i]%13==7))
				return true; 
		}
		return false; 
	}
	
	/**
	 * Return used cards
	 * @return usedCards
	 */
	public int[] getUsedCards()
	{
		return usedCards; 
	}
	
	
	public ArrayList<Integer> getUsedCardsList()
	{
		return listUsedCards; 
	}
	
	public void addCardToUsedCardsList(int card)
	{
		this.listUsedCards.add(card); 
	}
	
	/**
	 * Count the number of cards for a specific player
	 */
	public int getNumberOfCards(byte player)
	{
		int[] tmpCards = getCardsForPlayer(player); 
		int count = 0; 
		
		for(int i=0; i<tmpCards.length; i++)
		{
			if(tmpCards[i] != -1)
				count++; 
			else
				break; 
		}
		return count; 
	}
	
	/**
	 * Set the exchanged card
	 * @param card to exchange
	 */
	public void setExchangedCard(int card)
	{
		exchangedCard[1] = card; 
		int[] tmpCards = getCardsForPlayer(myPlayer); 
		for(int i=0; i<tmpCards.length; i++)
		{
			if(tmpCards[i] == -1) {
				tmpCards[i] = card;
				break; 
			}
		} 
	}
	
	/**
	 * Return the number of cards that are not played yet
	 * @return number of cards left
	 */
	public int getCardsLeft()
	{
		return cardsLeft; 
	}
	
	/**
	 * Remove a card and set it to -1. This method is used to exchange a card between two players
	 * @param card card to remove
	 */
	public void removCard(int card)
	{
		exchangedCard[0] = card; 
		int[] tmpCards = getCardsForPlayer(myPlayer); 
		for(int i=0; i<tmpCards.length; i++)
		{
			if(tmpCards[i] == card) {
				tmpCards[i] = -1;
				break; 
			}
		}
	}
	
	/**
	 * Calculate factorial of a given number.
	 * @param n 
	 * @return the factorial of the number n
	 */
	public BigInteger factorial(BigInteger n)
    {
		BigInteger result = BigInteger.ONE; 
		if(n.compareTo(BigInteger.ONE) <= 0) // FIXME: (n <= 1) ? 1
			return result; 
		else
			result = n.multiply(factorial(n.subtract(BigInteger.ONE))); 
		return result; 
    }

	/**
	 * Calculate factorial of a given number.
	 * @param n 
	 * @return the factorial of the number n
	 */
    public BigInteger factorial(BigInteger n, int limit)
    {
    	BigInteger result = BigInteger.ONE; 
		if(n.compareTo(BigInteger.valueOf(limit)) == 0) 
			return result; 
		else
			result = n.multiply(factorial(n.subtract(BigInteger.ONE), limit)); 
		return result;
    }
    
    
    /**
     * This combination formula is used for calculation the probability 
     * @param n 
     * @param k
     * @return
     */
    public long combination(int n, int k)
    {
    	int limit = n-k;  
    	BigInteger bigN = BigInteger.valueOf(n);
    	BigInteger bigK = BigInteger.valueOf(k); 
    	BigInteger factorialN = factorial(bigN, limit);
    	BigInteger factorialK = factorial(bigK); 
    	BigInteger result = factorialN.divide(factorialK);  
    	return result.longValue(); 
    }
	 
    /**
     * Return the next player
     * @param player current player 
     * @return the next player on turn
     */
    private byte getNextPlayer(byte player)
    {
    	byte nextPlayer = (byte)((player + 1) % 5);
    	return (nextPlayer == 0 ? 1 : nextPlayer);
    }		
	 
    /**
     * Return the last player
     * @param currentPlayer current player
     * @return the last player on turn
     */
    public byte getLastPlayer(byte currentPlayer){
    	byte nextPlayer = (byte)(currentPlayer - 1);
    	return nextPlayer == 0 ? 4 : nextPlayer;
    }
	 
    /**
     * Update information about the exchanged card. Set the value to -1 to indicate the card was used
     * @param index
     */
    public void updateExchangedCard(int index)
    {
    	// set index 1 (card I received from partner) to -1 and
    	// set index 0 of my partner to -1 to signal that I used this card
    	assert index >= 0 && index <=1; 
    	exchangedCard[index] = -1;
    }

    /**
     * Return the exchangedCard array
     * @return the two cards that were exchanged
     */
    public int[] getExchangedCard()
    {
    	return exchangedCard; 
    }


    /**
     * If this flag is true it indicates that the probabilities are calculated. 
     * The calculations will be skipped in case the flag is false. 
     * @param b 
     */
    public void enableProbability(boolean b)
    {
    	this.enableProbability = b; 
    }

    /**
     * Return flag that indicates if probabilities should be calculated or not
     * @return Return flag enableProbability
     */
    public boolean probEnabled() 
    {
    	return this.enableProbability; 
    }
    
    /**
     * Calculate entropy
     */
    public void calcEntropy()
    {
    	if(enableProbability)
    	{
    		entropy = new double[4]; 
    		for(int i=0; i<prob.length; i++)
    		{
    			double e = 0.0;
    			boolean done = false; 
    			double[] tmpProb = this.prob[i]; 
    			for(int j=0; j<tmpProb.length; j++)
    			{
    				if(tmpProb[j] != 0.0)
    				{
    					done = true; 
    					e += (double)(tmpProb[j] * (double)((Math.log10(tmpProb[j])/ Math.log10(2))));  
    				}
    			}
    			
    			if(!done)
    			{
    				entropy[i] = 0.0; // gave up 
    			}else { 
    				entropy[i] = Math.abs(e);  
    			}
//    			msg.debug(this, "e "+(i+1) +": " + entropy[i]);
    		}
    	}
    }
    
    /**
     * Calculate entropy
     */
    public void calcEntropy2()
    {
    	if(enableProbability)
    	{
    		entropy = new double[4];
    		
    		for(int i=0; i<prob.length; i++)
    		{
    			double[] tmpProb = this.prob[i]; 
    			double[] p = new double[14]; 
        		double e = 0.0;
        		double sum = 0.0, test = 0.0; 
        			
    			// normalize to 0-1
    			for(int j=0; j<tmpProb.length; j++)
        			sum += tmpProb[j]; 

    			for(int j=0; j<tmpProb.length; j++)
    				p[j] = (tmpProb[j]/sum); 
    			 
    			 // just to test that sum adds up to 1
    			for(int j=0; j<p.length; j++)
    				test += p[j]; 
    			
    			 for(int j=0; j<p.length; j++)
    			 {
    				 if(p[j] != 0.0)
    					 e += (double)(p[j] * (double)((Math.log10(p[j]))/ Math.log10(2)));  
    			 }
    			 
    			 entropy[i] = Math.abs(e);
    			 
    			 System.out.println("original probs: "); 
    			 for(int j=0; j<tmpProb.length; j++)
    			 {
    				System.out.print(tmpProb[j] + ", ");  
    			 }
    			 System.out.println();
    			 System.out.println("converted probs: "); 
    			 for(int j=0; j<p.length; j++)
    			 {
    				System.out.print(p[j] + ", ");  
    			 }
    			 System.out.println(); 
    			 System.out.println("test: " + test); 
    			 System.out.println("entropy: P" + (i+1) +"  " + entropy[i]);
    			 
    			//msg.debug(this, "e "+(i+1) +": " + entropy[i]);
    		}
    	}
    }
    
    /**
     * Return array with all values for every player
     * @return entropy values for every player
     */
    public double[] getEntropy()
    {
    	return entropy; 
    }
    
    /**
     * Return array of subjective probabilities
     * @return returns probSubjective
     */
    public double[][] getProbSubjective()
    {
    	return this.probSubjective; 
    }
    
    
    /**
     * Set self-defined values for observations of other players moves in order to reduce the probability 
     * of some cards 
     */
    public void fillSDV()
    {
    	percValues.put("fourBack", 0.5); 
    	percValues.put("usedJokerToGoOut", 0.1); // reduce value almost to zero
    	percValues.put("usedJoker", 0.1); // reduce value almost to zero for the card that the player used the joker for
    	percValues.put("shouldGoToHeaven", 0.3);
    	percValues.put("shouldGoToHeavenWithSevenSplit", 0.3); 
    	percValues.put("hitEnemy", 0.3); 
    	percValues.put("playedCard", 0.9); 
    	percValues.put("isOnEnemyHomePos", 0.7); 
    }
    
    /**
     * Store information about the last move that was performed
     * @param moveOfPlayer player who did the move
     * @param lastMove the move that was executed
     */
    public void storeLastMove(int moveOfPlayer, Move lastMove)
    {	
    	int index = moveOfPlayer - getMyPlayer(); 
    	if(index < 0)
    		index = index+4; 
    	moveList[index] = lastMove; 
    }
    
    
    /**
     * After every new round the collected information are not valid anymore and need to be deleted. 
     */
    public void cleanStoredMoves()
    {
    	moveList = new Move[4]; 
    	// clean saved subjective probabilities that are not valid when a new round starts
    	this.probSubjective = new double[4][14];
    	for(int i=0; i<4; i++)
    		for(int j=0; j<probSubjective[i].length; j++)
    			probSubjective[i][j] = 1.0; 
    	
    	for(int i=0; i<4; i++)
    		for(int j=0; j<tmpProb[i].length; j++)
    			tmpProb[i][j] = 1.0; 
		
    }
    

    /**
     * Use last moves of other player to analyze his moves and to reduce possible cards he could hold
     * @param bb the current state of the board
     */
    // FIXME: don't consider exchanged card of partner
    public void analyzeLastMoves(BotBoard bb)
    {
    	// FIXME: Maybe he didn't give up, but played his last card!?
    	if(moveList != null)
    	{
    		byte player = getNextPlayer(getMyPlayer()); 
    		
    		for(int pIndex=1; pIndex<moveList.length; pIndex++, player = getNextPlayer(player))
    		{
    			Move move = moveList[pIndex]; 
    			int homePos = helper.getHomePositionForPlayer(player); 

    			if(move != null && this.botsGaveUp[player-1] == false && this.cards[player-1][0] != -1)
    			{
    				boolean isOnHomePos =  move.getPositions()[0] == homePos;
    				
    				if(move.getCard() > 52)	
    				{
    					usedJoker(move, player); 
    				}
    				// 1. look if he was on a homefield and should use card 4 to go back.
    				// Only reduce probability in case he could still hold a card to go into heaven.
    				if(isOnHomePos && move.getwasProtected().length != 0 && move.getwasProtected()[0])
    				{
    					fourBack(move, player); 
    				}	
    				
    				if( ((move.getCard()%13) != Cards.HEARTS_JACK) && 
    						(move.getwasProtected().length != 0 ? move.getwasProtected()[0]!= true: false ) &&
    						//!(helper.isProtected(bb.getBoard(), homePos)) &&
    						!helper.posIsInHeavenOfPlayer(move.getPositions()[1], player) &&
    						move.getPositions()[0] >= helper.getFarthestHeavenReachableField(player) && 
    						move.getPositions()[0] <= helper.getHomePositionForPlayer(player) && 
    						!helper.allPiecesInHeavenOfPlayer(bb.getBoard(), player) )
    				{
    					shouldGoToHeaven(move, player, bb);
    					
    				} 
    				if((move.getwasProtected().length != 0 && move.getwasProtected()[0]==true?
    						move.getCard()%13 != Cards.HEARTS_FOUR:
    						(move.getCard()%13) != Cards.HEARTS_JACK) && 
    						 move.getPositions()[0] != -1 && 
    						!helper.posIsInHeavenOfPlayer(move.getPositions()[1], player))
    				{
    					// could he hit an enemy? 
    					// don't consider token in case he put his piece on homefield
    					// or if he went to heaven (heaven is more worth than hit a piece!?)
    					couldHitEnemy(move, player, bb); 
    				}
    			
    				// Verify if the player had to move on an enemy's homefield
    				if(helper.isAHomePosition(move.getPositions()[1]))
    					isOnEnemyHomePos(move, player, bb); 
    				
    				// FIXME: Not implemented yet
    				if(helper.getPartnerForPlayer(myPlayer) == player)
    				{
    					
    				}
    				
    				// reduce probability of that card he just played
    				int card = move.getCard();

    				if(move.getCard() <= 52)
    				{
    					card = card%13; 
    					tmpProb[player-1][(card==0?12:card-1)] *= percValues.get("playedCard");
    				}
    			} 
    		} // end for each player pIndex
    		
    	}// move != null
    	
    	// FIXME: don't reduce exchanged card of my player
    	byte partner = helper.getPartnerForPlayer(myPlayer); 
    	if(exchangedCard[0] != -1)
    	{
    		int exCardIndex = exchangedCard[0]%13;
    		if(exCardIndex==0)
    			exCardIndex = 12; 
    		else if(exCardIndex==100)
    			exCardIndex = 13; 
    		else
    			exCardIndex = exCardIndex-1; 
    		
    		// don't adjust value of exchanged card in case it's not played yet
    		tmpProb[partner-1][exCardIndex] = 1.0;
    	}
    	
    	// copy values with actual prob
		for(int i=0; i<prob.length; i++)
			for(int j=0; j<prob[i].length; j++)
				probSubjective[i][j] = prob[i][j] * tmpProb[i][j]; 
    }
    
    /**
     * Reduce probability of card 4 if a player didn't go back four fields
     * @param move the last move
     * @param player the player on turn
     */
    public void fourBack(Move move, byte player)
    {
    	// only reduce card 4 in case the player didn't go 4 fields back and still holds either
    	// a Five, Six, Seven or Eight to a certain percentage
    	if(move.getPositions()[1] != helper.getFourFieldsBackOfHome(player) && 
    			(prob[player-1][4]>=0.2 || prob[player-1][5]>=0.2 || prob[player-1][6]>=0.2 || prob[player-1][7]>=0.2) )
			tmpProb[player-1][3] *= percValues.get("fourBack"); 
    }
    
    /**
     * In case a player used a joker, reduce the probability of the actual card
     * @param move the last move
     * @param player the player on turn
     */
    public void usedJoker(Move move, byte player)
    {
    	// used joker to go out: doesn't have a King/Ace
    	if(move.getPositions()[1] == helper.getHomePositionForPlayer(player))
    	{
    		tmpProb[player-1][0] *= percValues.get("usedJokerToGoOut");
    		tmpProb[player-1][12] *= percValues.get("usedJokerToGoOut");  
    	} else
    	{
    		// he doesn't have the actual card that he used the joker for
    		int card = move.getCard() % 13; 
    		tmpProb[player-1][(card==0?12:card-1)] *= percValues.get("usedJoker");; 
    	}
    	tmpProb[player-1][13] *= percValues.get("playedCard"); 
    }
    
    /**
     * Check if a player could hit another token
     * @param move the last move
     * @param player the player on turn
     * @param bb current state of the board
     */
    public void couldHitEnemy(Move move, byte player, BotBoard bb)
    {
    	if(move.getHits().length == 0 && move.get7Hits().length == 0 
    			&& move.getPositions()[0] <= 63 && move.getPositions()[0] != -1)
    	{ 
    		// currentPlayer != getNextPlayer(player): go one further, because for player I need to undo the move 
    		byte currentPlayer = -1;
    		int mListIndex = -1;
    		int count=1; 
    		for(mListIndex = 3 ; currentPlayer != player; mListIndex--, count++)
    		{
    			if(moveList[mListIndex] != null)
    			{
    				currentPlayer = (byte)(myPlayer-count);
    				if(currentPlayer <= 0)
    					currentPlayer = (byte)(currentPlayer + 4); 
    				bb.undoMove(currentPlayer, moveList[mListIndex]);
    			}
    		}
    		
    		byte[] bbArray = bb.getBoard(); 
    		
    		// check 13 fields in front of player if he was able to hit an enemy
    		int actPos = move.getPositions()[0];
    		byte partner = helper.getPartnerForPlayer(player); 
    		boolean hitPos = false; 
    		
    		for(int i=1 ; i<=13; i++)
    		{
    			if( !helper.isProtected(bbArray, (actPos+i)%64))
    			{
    				int pos = bbArray[actPos + i]%64; 
    				
    				if(pos != 0 && pos != partner)
    				{
    					if(i<=7)
    						hitPos = true; // check if hitPos is in range of seven
    					int card = i;  
    					if(card==11)
    						tmpProb[player-1][0] *= percValues.get("hitEnemy"); // Ace
    					else
    						tmpProb[player-1][(card%13==0?12:card-1)] *= percValues.get("hitEnemy"); 
    				}
    			}
    		}
    		if(hitPos)
    			tmpProb[player-1][6] *= percValues.get("hitEnemy"); 
		
    		currentPlayer = player;
    		for(mListIndex += 1; currentPlayer != myPlayer && mListIndex <= 3; mListIndex++, currentPlayer = getNextPlayer(currentPlayer))
    		{
    			if(moveList[mListIndex] != null)
    			{
    				bb.makeMove(currentPlayer, moveList[mListIndex]);
    			}
    		}
    	}
    }
    
    /**
     * Verifies if the player went to heaven in case he is 13 fields away
     * @param move the last move
     * @param player the player on turn
     * @param bb current state of the board
     */
    public void shouldGoToHeaven(Move move, byte player, BotBoard bb)
    {
    	// get first free field in heaven that he could go to
    	boolean done = false; 
    	int lastPos = helper.getFirstFreeFieldInHeavenOfPlayer(bb.getBoard(), player);
    	int firstFieldInHeaven = 60 + (player * 4); // is 68 for player 2

    	if(! (bb.getBoard()[helper.getHomePositionForPlayer(player)]==5))
    	{
    		while(lastPos != -1)
    		{
    			// first pos in heaven is free: he should have gone into heaven but didn't have the right card
    			done = true; 
    			int distance = (helper.distanceToHeaven(move.getPositions()[0], lastPos, player));  
    			if(distance != -1 && distance<=13)
    			{
    				if(distance == 11)
    					tmpProb[player-1][0] *= percValues.get("shouldGoToHeaven"); // Ace
    				else
    					tmpProb[player-1][distance-1] *=  percValues.get("shouldGoToHeaven"); 
    			}

    			lastPos = (lastPos != firstFieldInHeaven?lastPos-1: -1); 
    		}
    		if(done)
    			tmpProb[player-1][13] *=  percValues.get("shouldGoToHeaven"); // joker is also unlikely 

    	}
    	// in case !done: player didn't even have the possibility to try one heaven field: reason could be 
    	// that he would need a seven

    	// for P2: don't consider startPos >= 11
    	// situation: one token on 68 and can only move to 69
    	// then most narrow situation if he is on field 11
    	if(!done && move.getPositions()[0] >= helper.getHomePositionForPlayer(player) - 5)
    	{
    		// currentPlayer != getNextPlayer(player): go one further, because for player I need to undo the move 
    		byte currentPlayer = -1;
    		int mListIndex = -1;
    		int count=1; 
    		for(mListIndex = 3 ; currentPlayer != player; mListIndex--, count++)
    		{
    			if(moveList[mListIndex] != null)
    			{
    				currentPlayer = (byte)(myPlayer-count);
    				if(currentPlayer <= 0)
    					currentPlayer = (byte)(currentPlayer + 4); 
    				bb.undoMove(currentPlayer, moveList[mListIndex]);
    			}
    		}
    	
    		List<Move> moves = new ArrayList<Move>(); 
    		moves = bb.getPossibleMovesForJokerOptimized(player, new boolean[13]); 
    		List<Move> sortedMoves = Helper.getInstance().getSortedMoves(moves, player, 3, new SimpleEvaluatorV5(), bb, this); 
    		msg.debugLegalMoves(this, sortedMoves); 
    		if(sortedMoves != null)
    		{
    			boolean exit = false; 
    			for(int i=0; i<sortedMoves.size() && !exit; i++)
    			{
    				Move sTmp = sortedMoves.get(i); 
    				for(int j=1; j<sTmp.getPositions().length && !exit; j+= 2)
    				{
    					// only if at least one piece goes from outside into heaven: don't consider moves like 68->69
    					if(helper.posIsInHeavenOfPlayer((int)(sTmp.getPositions()[j]), player)
    							&& !helper.posIsInHeavenOfPlayer((int)(sTmp.getPositions()[j-1]), player))  
    					{
    						// he could have gone to heaven with 7-split
    						tmpProb[player-1][6] *= percValues.get("shouldGoToHeavenWithSevenSplit");
    						// joker
    						tmpProb[player-1][13] *= percValues.get("shouldGoToHeavenWithSevenSplit"); 
    						exit = true; 
    					}
    				}
    			}
    		}
    		currentPlayer = player;
    		for(mListIndex += 1; currentPlayer != myPlayer && mListIndex <= 3; mListIndex++, currentPlayer = getNextPlayer(currentPlayer))
    		{
    			if(moveList[mListIndex] != null)
    			{ 
    				bb.makeMove(currentPlayer, moveList[mListIndex]);
    			}
    		}
    	}
    }
    

    public void isOnEnemyHomePos(Move move, byte player, BotBoard bb)
    {
    	boolean done = false; 
    	byte tmpPlayer = getNextPlayer(player);  
    	for(;tmpPlayer != player && !done; tmpPlayer = getNextPlayer(tmpPlayer))
    	{
    		if(helper.getHomePositionForPlayer(tmpPlayer) == move.getPositions()[1]) 
    		{
    			done = true; 
    			try {
    				
    				if(helper.getPiecesInGameForPlayer(bb.getBoard(), tmpPlayer).length < 4)
    				{
    					// My player is on an enemy's home position and he has pawns off the board
    					// reduce all other cards than he played 
    					int cardIndex = (move.getCard() > 52 ? 13 : move.getCard()%13); 
    					cardIndex = (cardIndex==0?12:cardIndex-1); 
    					for(int i=0; i<cardIndex; i++)
    						if(i!=10) // ignore jack
    							tmpProb[player-1][i] *= percValues.get("isOnEnemyHomePos");
    					
    					if(cardIndex!=13)
    					{
    						for(int i=cardIndex+1; i<=13; i++)
    							if(i!=10)
    								tmpProb[player-1][i] *= percValues.get("isOnEnemyHomePos");
    					} else {
    						tmpProb[player-1][13] *= percValues.get("isOnEnemyHomePos");
    					}
    				}
    	    			
				} catch (Exception e) {
					e.printStackTrace();
					
				}
    		}
    	}
		
    }
    
    @Override
    public InformationGatherer clone()
    {
    	InformationGatherer copy = new InformationGatherer(this.myPlayer); 
    	copy.botsGaveUp = botsGaveUp.clone(); 
    	for(int j = 0; j < this.cards.length; j++){
			copy.cards[j] = this.cards[j].clone();
		}
    	copy.cardsLeft = this.cardsLeft; 
    	copy.enableProbability = this.enableProbability; 
    	copy.entropy = this.entropy.clone(); 
    	copy.exchangedCard = this.exchangedCard.clone(); 
    	copy.listUsedCards = new ArrayList<Integer>(this.listUsedCards); 
    	copy.moveList = this.moveList.clone(); 
    	copy.myPlayer = this.myPlayer; 
    	copy.percValues = new HashMap<String, Double>(this.percValues); 
    	for(int j = 0; j < this.prob.length; j++){
			copy.prob[j] = this.prob[j].clone();
		}
    	for(int j = 0; j < this.probSubjective.length; j++){
			copy.probSubjective[j] = this.probSubjective[j].clone();
		}
    	for(int j = 0; j < this.tmpProb.length; j++){
			copy.tmpProb[j] = this.tmpProb[j].clone();
		}
    	copy.usedCards = this.usedCards.clone(); 
    	
    	return copy; 
    }
}
