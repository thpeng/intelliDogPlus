package intelliDOG.ai.framework;

import intelliDOG.ai.bots.IBot;
import intelliDOG.ai.utils.BarChartProb;
import intelliDOG.ai.utils.Statistics;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * The Game class is the game controller for games running over the intelliDOG
 * framework only
 */
public class Game {

	private Board board;
	private byte playerOnTurn;
	private IBot[] bots;
	private String[] playerNames = new String[] {"Player 1", "Player 2", "Player 3", "Player 4"};
	private boolean[] botGaveUp = new boolean[4];
	private int [][] cards;
	private Stack<Integer> cardStock;
	private List<Integer> usedCards;
	private boolean isSituation;
	private int maxTurns = 0;
	private boolean newRound = false; 
	
	private Stack<Move> moveStack;
	private GameWindow gw;
	private BarChartProb pc; 
	
	public Game(IBot[] bots, GameWindow gw, BarChartProb b, long timeout){
		this.board = new Board();
		this.playerOnTurn = Players.P1;
		this.bots = bots;
		this.cards = new int[4][6];
		this.gw = gw;
		if(this.gw != null){
			this.gw.setTimeout(timeout);
			this.playerNames = this.gw.getPlayerNames();
		}
		this.usedCards = new ArrayList<Integer>();
		this.moveStack = new Stack<Move>(); 
		this.pc = b; 
	}
	
	
	public Game(IBot[] bots){
		this(bots, null, null, 0);
	}
	
	
	/**
	 * Constructor to initialize a specific game situation
	 * @param bots
	 * @param gw
	 * @param timeout
	 * @param board current board
	 * @param cards initial cards for each player
	 * @param currentPlayer player who makes the next turn
	 */
	public Game(IBot[] bots, GameWindow gw, BarChartProb b, long timeout, byte[] board, int[][] cards, byte currentPlayer, List<Integer> usedCards){
		this.board = new Board(board, currentPlayer); 
		this.bots = bots;
	
		this.playerOnTurn = currentPlayer; 
		this.cards = cards; 
		this.gw = gw;
		if(this.gw != null){
			this.gw.setTimeout(timeout);
			this.playerNames = this.gw.getPlayerNames();
		}
		this.usedCards = usedCards;
		this.moveStack = new Stack<Move>();
		isSituation = true; 
		this.pc = b; 
	}
	
	public void run(){
		long startTime = System.nanoTime();
		
		//initially shuffle cards
		this.cardStock = shuffleCards();
		if(!isSituation){
			this.usedCards.clear();
		}
		
		int count = 0; 
		for(int i=0; i<cards[0].length; i++)
		{
			if(cards[3][i] != -1) //this player must have as many cards as the player with the most cards (so he is one of the player's with the most cards!)
				count++; 
		}
		int start = playerOnTurn - 1; //this is used to not get out of cycle when not player 1 is starting (only on situation creation)
		int cardsToDistribute = 6;
		int cardsCountBack = 0;
		
		int turnCount = 0;
		boolean lastPlayerGaveUp = false; 
		
		//while not game is won
		while(hasWon() == 0)
		{
			//if a max nr. of turns is set and it is exceeded then the game is also finished!
			if(maxTurns > 0 && turnCount++ > maxTurns){
				break;
			}

			if(isSituation)
			{
				cardsToDistribute = count;
				cardsCountBack = cardsToDistribute;
				cardsToDistribute--; 
				isSituation = false;
				cleanMoveList(); 
				
				for(int card : usedCards){
					this.cardStock.remove(new Integer(card));
				}
				for(int i = 0; i < cards.length; i++){
					for(int j = 0; j < cards[i].length; j++){
						this.cardStock.remove(new Integer(cards[i][j]));
					}
				}
				// Call updateBotInformation here because it won't be called in exchange cards
				for(int i=0; i<4; i++)
					updateBotInformation(bots[i].getBotBoard(), bots[i].getInformationGatherer(), -1 , null);
				
				if(gw != null && count > 0){
					gw.update(board.getBoard(), cards, playerOnTurn, false);
				}
			}
			//give cards to players
			if(cardsCountBack == 0)
			{
				newRound = true; 
				cleanMoveList();
				
				if(cardsToDistribute <= 1){ cardsToDistribute = 6; }
				
				cardsCountBack = cardsToDistribute;
				updateBotGaveUp(null);
				distributeCards(cardsToDistribute--);
				
				if(gw != null){
					gw.updateOnNewRound(cards);
				}				
				
				// FIXME: analyzeMoves gets called every time!
				// update bot information before exchange cards
				for(int i=0; i<4; i++)
					updateBotInformation(bots[i].getBotBoard(), bots[i].getInformationGatherer(), -1 , null);
				
				
				//exchange cards
				for(int i = 0; i < 2; i++)
				{
					byte partner = Rules.getInstance().getPartnerForPlayer(bots[i].getPlayer());
					int exCard = bots[i].exchangeCard();
					int exCardPartner = bots[partner-1].exchangeCard(); 
					bots[i].getInformationGatherer().setExchangedCard(exCardPartner);
					bots[partner-1].getInformationGatherer().setExchangedCard(exCard);
					if(gw != null){
						gw.addMessage(playerNames[i] + " gave card " + Cards.CARDNAMES.get(exCard) + " to his partner " + playerNames[partner - 1]);
						gw.addMessage(playerNames[partner - 1] + " gave card " + Cards.CARDNAMES.get(exCardPartner) + " to his partner " + playerNames[i]);
					}
					for(int j = 0; j < cards[i].length; j++){
						if(cards[i][j] == exCard){ 
							cards[i][j] = exCardPartner;
							break;
						}
					}
					for(int j = 0; j < cards[partner - 1].length; j++){
						if(cards[partner - 1][j] == exCardPartner){ 
							cards[partner - 1][j] = exCard; 
							break;
						}
					}
				}
				
				
				
				if(gw != null){
					gw.updateOnNewRound(cards);
				}
			}
			
			boolean allGaveUp = true;
			
			for(int i = start; i < 4; i++){
				//if game is won break;
				if(hasWon() != 0){ break; }

				updateBotInformation(bots[i].getBotBoard(), bots[i].getInformationGatherer(), i==0? 4 :(i)/*(i==0?3:(i-1))*/, (moveStack.isEmpty() || lastPlayerGaveUp || newRound?null:moveStack.peek()));
				newRound = false; 
				if(pc != null  && pc.getPlayer() == (i+1)) {
					bots[i].getInformationGatherer().calcEntropy/*2*/(); 
					
					pc.updateChart(i, bots[i].getInformationGatherer().getProb(), bots[i].getInformationGatherer().getProbSubjective(),
								bots[i].getInformationGatherer().getCardsLeft(), bots[i].getInformationGatherer().getEntropy());
					
					
				}
				
				Move move = bots[i].makeMove();
				if(move == null){
					updateBotGaveUp(bots[i]); 
					lastPlayerGaveUp = true;
					
					//bot gives up or gave already up.
					takeCardsAway(); //take the other cards away
					if(gw != null){
						gw.addMessage(playerNames[this.playerOnTurn-1] + ": gave up");
					}
					this.playerOnTurn = getNextPlayer(this.playerOnTurn);
					this.board.setPlayerOnTurn(this.playerOnTurn);
				}else{
					this.board.makeMove(move);
					removeCard(move.getCard());
					lastPlayerGaveUp = false;
					
					if(bots[i].getInformationGatherer().probEnabled() && 
							bots[i].getInformationGatherer().getExchangedCard()[1] == (move.getCard() > 52 ? 100 : move.getCard()) )
					{
						// index 1: reset the card that I received from my partner
						bots[i].getInformationGatherer().updateExchangedCard(1);
						// index 0: reset the card my partner gave to me
						bots[Rules.getInstance().getPartnerForPlayer(bots[i].getPlayer())-1].getInformationGatherer().updateExchangedCard(0);
					}
					
					moveStack.push(move);
					if(gw != null){
						gw.addMessage(playerNames[this.playerOnTurn-1] + " played card: " + Cards.CARDNAMES.get(move.getCard()) + printMove(move).toString());
					}
					this.playerOnTurn = getNextPlayer(this.playerOnTurn);
					this.board.setPlayerOnTurn(this.playerOnTurn);
					allGaveUp = false;
				}
				if(gw != null){
					gw.update(board.getBoard(), cards, this.playerOnTurn, 
							(botGaveUp[this.playerOnTurn - 1]) 
							|| (cardsCountBack == 1 && i == 3));
				}
			}
			start = 0;
			
			if(allGaveUp){
				cardsCountBack = 0;
			}else{
				cardsCountBack -= 1;
			} 
		}
		//clean up..
		//System.out.println("Team " + hasWon() + " has won!");
		//System.out.println("Game time = " + ((System.nanoTime() - startTime) / 1000000 ) + " msecs");
		int pc1 = Rules.getInstance().nrOfPiecesInHeavenOfPlayer(this.board.getBoard(), Players.P1);
		pc1 += Rules.getInstance().nrOfPiecesInHeavenOfPlayer(this.board.getBoard(), Players.P3);
		int pc2 = Rules.getInstance().nrOfPiecesInHeavenOfPlayer(this.board.getBoard(), Players.P2);
		pc2 += Rules.getInstance().nrOfPiecesInHeavenOfPlayer(this.board.getBoard(), Players.P4);
		double t = (System.nanoTime() - startTime) / 1000000.0 / 1000.0;
		
		Statistics.setGameStats(hasWon(), pc1, pc2, t, moveStack.size());
	
	}
	
	public StringBuffer printMove(Move move)
	{
		StringBuffer buffer = new StringBuffer(); 
		for(int j=0; j<move.getPositions().length; j+= 2)
		{
			buffer.append("  s: " + move.getPositions()[j] + ", ");
			buffer.append("t: " + move.getPositions()[j + 1]);
			if(j != move.getPositions().length - 2)
				buffer.append(" / ");
		}
		return buffer; 
	}
	
	public void cleanMoveList()
	{
		for(int i=0; i<bots.length; i++)
			bots[i].getInformationGatherer().cleanStoredMoves(); 
	}
	
	/**
	 * This method will update the information for the bot on turn. (Before he will do the next move)
	 * @param bb The <class>BotBoard</class> of the bot on turn.
	 * @param ig The <class>InformationGatherer</class> of the bot on turn.
	 */
	private void updateBotInformation(BotBoard bb, InformationGatherer ig, int moveOfPlayer, Move lastMove) {
		bb.setBoard(getBoardArray());
		ig.setCardsForPlayer(cards[ig.getMyPlayer() - 1], playerOnTurn);
		//sets all player's cards on all player's informationgatherers 
		//(only cheating bot(s) will use the information about other player's cards from there)
		for(int i=0; i<4; i++){
			updateAllBotInformation(bots[i].getBotBoard(), bots[i].getInformationGatherer());
		}
		ig.distributeUsedCards(usedCards);
		
		if(moveOfPlayer != -1)
			for(int i=0; i<bots.length; i++)
				bots[i].getInformationGatherer().storeLastMove(moveOfPlayer, lastMove); 

		// FIXME: don't analyze if bot gave up
		if(ig.probEnabled()) 
		{
			ig.analyzeLastMoves(bb);
		}
	}

	/**
	 * This method will update the information for all bots. 
	 * @param bb The <class>BotBoard</class> of the bot on turn.
	 * @param ig The <class>InformationGatherer</class> of the bot on turn.
	 */
	private void updateAllBotInformation(BotBoard bb, InformationGatherer ig) {
		//bb.setBoard(getBoardArray());
		byte p = playerOnTurn; 
		
		for(int i = 0; i<4; i++)
		{
			ig.setCardsForPlayer(cards[p - 1], p);
			p = bb.getNextPlayer(p); 	
		}
	}
	
	/**
	 * Method that will update all bot's <class>InformationGatherers</class>
	 * when a bot had to give up
	 * @param bot the bot that gave up
	 */
	private void updateBotGaveUp(IBot bot)
	{
		if(bot == null)
		{
			for(int i=0; i<bots.length; i++)
			{
				bots[i].getInformationGatherer().cleanBotsGaveUp(); 
			}
			this.botGaveUp = new boolean[4];
		}
		else
		{
			for(int i=0; i<bots.length; i++)
			{
				bots[i].getInformationGatherer().setBotGaveUp(bot.getPlayer());
				if(bots[i].equals(bot)){
					this.botGaveUp[i] = true;
				}
			}
			if(bot.getInformationGatherer().probEnabled())
			{
				// index 1: reset the card that I received from my partner
				bot.getInformationGatherer().updateExchangedCard(1);
				// index 0: reset the card my partner gave to me
				bots[Rules.getInstance().getPartnerForPlayer(bot.getPlayer())-1].getInformationGatherer().updateExchangedCard(0);
			}
		}
	}
	
	
	/**
	 * Determines if a team has won and when yes which one it is.
	 * @return 0, if no one has won / 1, if team one (P1 & P3) has won / 2, if team two (P2 & P4) has won
	 */
	public int hasWon(){
		if(Rules.getInstance().allPiecesInHeavenOfPlayer(getBoardArray(), Players.P1)
				&& Rules.getInstance().allPiecesInHeavenOfPlayer(getBoardArray(), Players.P3)){
			return 1;
		}
		if(Rules.getInstance().allPiecesInHeavenOfPlayer(getBoardArray(), Players.P2)
				&& Rules.getInstance().allPiecesInHeavenOfPlayer(getBoardArray(), Players.P4)){
			return 2;
		}
		return 0;
	}
	
	/**
	 * returns the player that is on turn after the given player
	 * @param currentPlayer the player actually on turn
	 * @return the next player on turn
	 */
	private byte getNextPlayer(byte currentPlayer){
		byte nextPlayer = (byte)((currentPlayer + 1) % 5);
		return nextPlayer == 0 ? 1 : nextPlayer;
	}
	
	/**
	 * This method takes a player's card's away if he has to give up.
	 */
	private void takeCardsAway(){
		boolean hadCards = cards[playerOnTurn - 1][0] != -1;
		for(int i = 0; i < 6; i++){
			if(cards[playerOnTurn - 1][i] == -1){
				break;
			}else{
				usedCards.add(cards[playerOnTurn - 1][i]);
				if(gw != null){
					gw.addMessage("    " + Cards.CARDNAMES.get(cards[playerOnTurn - 1][i]));
				}
				cards[playerOnTurn - 1][i] = -1;
			}
		}
		if(gw != null && hadCards){
			gw.addMessage(" and threw away");
		}
	}
	
	/**
	 * gets a copy of the current board's byte array
	 * @return a copy of the Boards board
	 */
	private byte[] getBoardArray(){
		byte[] boardArray = this.board.getBoard();
		byte[] boardArrayCopy = new byte[80];
		for(int i = 0; i < 80; i++){
			boardArrayCopy[i] = boardArray[i];
		}
		return boardArrayCopy;
	}
	
	/**
	 * Method that will distribute the cards to players
	 * @param nrOfCards how many cards shall be distributed per player
	 */
	private void distributeCards(int nrOfCards){
		if( (nrOfCards * 4) > this.cardStock.size())
		{
			this.cardStock = shuffleCards();
			this.usedCards.clear();
		}
		for(int i = 0; i < nrOfCards; i++){
			for(int j = 0; j < 4; j++){
				this.cards[j][i] = this.cardStock.pop();
			}
		}
		for(int i = nrOfCards; i < 6; i++){
			for(int j = 0; j < 4; j++){
				this.cards[j][i] = -1;
			}
		}
	}
	
	/**
	 * generate card stack with each card 2 times and joker 8 times
	 * @return a stack with all cards shuffled
	 */
	private Stack<Integer> shuffleCards(){
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
		Stack<Integer> cards = new Stack<Integer>();
		//fill stack with cards from the two sorted decks
		for(int i = 0; i < 112; i++){
			//randomly choose a card from the sorted ones and push it on our unsorted stack
			int pos = (int)(Math.random() * (sortedCards.size() - 1) + 0.5);
			cards.push(sortedCards.get(pos));
			sortedCards.remove(pos); //remove the used card from the sorted ones
		}
		return cards;
	}
	
	/**
	 * remove the card used for the last move from the players cards.
	 * @param usedCard the card that was used for the move
	 */
	private void removeCard(int usedCard){
		//remove used card
		int usedCardPos = 0;
		usedCards.add(usedCard);
		int[] myCardsOld = new int[cards[playerOnTurn - 1].length];
		for(int i = 0; i < cards[playerOnTurn - 1].length; i++){
			if(cards[this.playerOnTurn - 1][i] == (usedCard >= 53 ? Cards.JOKER : usedCard)){ usedCardPos = i; }
			myCardsOld[i] = cards[playerOnTurn - 1][i];
		}
		for(int i = usedCardPos; i < cards[playerOnTurn - 1].length - 1; i++){
			cards[playerOnTurn - 1][i] = myCardsOld[i + 1];
		}
		cards[playerOnTurn - 1][cards[playerOnTurn - 1].length - 1] = -1;
	}

	/**
	 * @return the usedCards
	 */
	public List<Integer> getUsedCards() {
		return usedCards;
	}
	
	/**
	 * This method will reset the state of a game.
	 * It can be used to play multiple games with the same game object.
	 */
	public void reset(){
		this.board = new Board();
		this.playerOnTurn = Players.P1;
		this.cards = new int[4][6];
		this.usedCards = new ArrayList<Integer>();
		this.moveStack = new Stack<Move>();
		if(this.gw != null){
			gw.clearMessages();
		}
	}
	
	/**
	 * This method will reset the state of a game to the specified situation.
	 * It can be used to play multiple games with the same game object.
	 */
	public void resetToSituation(byte[] board, int[][] cards, byte currentPlayer, List<Integer> usedCards){
		this.board = new Board(board, currentPlayer);
		this.playerOnTurn = currentPlayer;
		this.cards = cards; 
		this.usedCards = usedCards;
		this.moveStack = new Stack<Move>();
		isSituation = true; 
		if(this.gw != null){
			gw.clearMessages();
		}
	}


	/**
	 * @param maxTurns the max nr of turns allowed in a game to set
	 */
	public void setMaxTurns(int maxTurns) {
		this.maxTurns = maxTurns;
	}
}
