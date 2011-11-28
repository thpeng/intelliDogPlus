package intelliDOG.ai.framework;

import intelliDOG.ai.utils.DebugMsg;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * The BotBoard is created with a game situation and can generate all possible moves for it for one bot
 *
 */
public class BotBoard {
	private byte [] board;
	private InformationGatherer ig;
	private Rules rules = Rules.getInstance();
	
	private Stack<Move> moveStack = new Stack<Move>();
	
	private DebugMsg msg = DebugMsg.getInstance();
	
	/**
	 * This is the constructor for the BotBoard.
	 * @param board a byte[] with length 80, representing the board. [see documentation for description of its structure]
	 * @param ig the InformationGatherer holding the card information.
	 */
	public BotBoard(byte [] board, InformationGatherer ig){
		assert board.length == 80;
		this.board = board;
		this.ig = ig;
	}
	
	/**
	 * This method collects all possible moves for a player
	 * @return a list with all moves
	 */
	public List<Move> getAllPossibleMoves(byte playerOnTurn){
		
		List<Move> legal_moves = new ArrayList<Move>(); 
		
		if(ig.getPlayerGaveUp(playerOnTurn))
		{
			return legal_moves; 
		}
		
		int[] cards = ig.getCardsForPlayer(playerOnTurn);
		
		msg.debug(this,"Player on Turn: " + playerOnTurn); 
		msg.debugCards(this,cards); 
		
		
		
		int pieces[];
		pieces = rules.getPiecesInGameForPlayer(board, playerOnTurn);
		msg.debugPieces(this,pieces);
		
	    //if(pieces.isEmpty()) then only check for Ace and King or Joker
		// Saves a lot of calculations...
		if(pieces.length == 0)
		{
			if( ! canGoToHomefield(cards)) 
			{
				return legal_moves; // gives up
			} else {
				msg.debug(this,"must have either ace, king or joker...");
				legal_moves = checkKingAce(cards, playerOnTurn); 
				
				assert legal_moves.size() != 0; // should never be 0
				msg.debugLegalMoves(this, (ArrayList<Move>)legal_moves);
			
				return legal_moves; 
			}
		}
		
		//new optimisation (don't consider cards multiple times!)
		int[] cardsToCalc = new int[cards.length];
		int[] cardsToCopy = new int[cards.length];
		for(int i = 0; i < cards.length; i++){
			cardsToCalc[i] = -1;
			cardsToCopy[i] = -1;
		}
		boolean [] alreadyCalc = new boolean[13];
		boolean joker = false;
		
		int calcCount = 0;
		int copyCount = 0;
		for(int i = 0; i < cards.length; i++){
			if(cards[i] == -1){ break; }
			if(cards[i] == 100){
				joker = true;
			}else{
				if(alreadyCalc[cards[i] % 13]){
					cardsToCopy[copyCount++] = cards[i];
				}else{
					cardsToCalc[calcCount++] = cards[i];
					alreadyCalc[cards[i] % 13] = true;
				}
			}
		}
		//end new
		
		
		// The player has at least one piece on the board
		//for all cards in my hand call the getpossiblemovesforcard method.
		for(int card = 0; card < cardsToCalc.length; card++)
		{
				// catch case if card is -1. All following cards will be -1 anyways
				if(cardsToCalc[card] == -1){ break; } 
				
				legal_moves.addAll(getPossibleMovesForCard(cardsToCalc[card], playerOnTurn));
				
			} // end for 

		if(legal_moves.size() != 0) {msg.debugLegalMoves(this, (ArrayList<Move>)legal_moves);}
		
		//copy moves that were not calculated
		List<Move> copiedMoves = new ArrayList<Move>();
		//copy not yet added moves
		for(int i = 0; i < cardsToCopy.length; i++){
			if(cardsToCopy[i] == -1) { break; }
			for(Move m : legal_moves){
				if(m.getCard() % 13 == cardsToCopy[i] % 13){
					copiedMoves.add(m.copy(cardsToCopy[i]));
				}
			}
		}
		
		legal_moves.addAll(copiedMoves);
		//end copy moves
		
		//add moves for joker (if we have a joker)
		if(joker){
			if(pieces.length == 0){ //saving calculations
				byte playerToMoveWith = playerOnTurn;
				if(rules.allPiecesInHeavenOfPlayer(board, playerOnTurn)){
					playerToMoveWith = rules.getPartnerForPlayer(playerOnTurn);
				}
				return checkKingAce(new int[] {Cards.JOKER, -1, -1, -1, -1, -1}, playerToMoveWith);
			}else{
				legal_moves.addAll(getPossibleMovesForJokerOptimized(playerOnTurn, alreadyCalc));
			}
		}
		//end joker
		
		//moves for joker are not copied as a player shall always use another card instead of joker for the same action
		//TODO: perhaps overwork this as it could be that anyone would prefer to not play a card that was exchanged by the partner before the joker.
		
		return legal_moves;
	}
	
	
	/**
	 * this method calculates all possible moves for one card
	 * @param card the card to use
	 * @param playerOnTurn the player on turn
	 * @return a List of all possible <class>Move</class>s for this card/situation
	 */
	public List<Move> getPossibleMovesForCard(int card, byte playerOnTurn){
		ArrayList<Move> legal_moves = new ArrayList<Move>();
		
		int pieces[];
		byte playerToMoveWith = playerOnTurn;
		if(rules.allPiecesInHeavenOfPlayer(board, playerOnTurn)){
			playerToMoveWith = rules.getPartnerForPlayer(playerOnTurn);
			pieces = rules.getMovablePiecesInGameForPlayer(board, playerToMoveWith, card);
		}else{
			pieces = rules.getMovablePiecesInGameForPlayer(board, playerOnTurn, card);
		}
		
		if(isNormalCard(card)) { // normal card
			return getPossibleMovesForNormalCard(pieces, card, playerToMoveWith);
		} else if( (card % 13) == 7) {  // Seven
			return getPossibleMovesForSeven(pieces, card, playerToMoveWith);
		} else if( (card % 13) == 4) {  // Four
			return getPossibleMovesForFour(pieces, card, playerToMoveWith);
		} else if( (card % 13) == 1) {  // Ace
			return getPossibleMovesForAce(pieces, card, playerToMoveWith);
		} else if( (card % 13) == 11) {  // Jack
			return getPossibleMovesForJack(pieces, card, playerToMoveWith);
		} else if( (card % 13) == 0) { // King
			return getPossibleMovesForKing(pieces, card, playerToMoveWith);
		}
		assert(card != Cards.JOKER );   //when having a joker it should've been catched before to use the optimized method...
			
		return legal_moves;
	}
	
	/**
	 * this method gets all possible moves for a joker, but returns only
	 * possibilities which have not been calculated before
	 * @param playerToMoveWith the player to calculate the moves for
	 * @param alreadyCalc a boolean array indicating if the moves for a card type have already been calculated
	 * @return a List of all possible <class>Move</class>s for this card/situation
	 */
	public List<Move> getPossibleMovesForJokerOptimized(byte playerToMoveWith, boolean[] alreadyCalc){
		assert alreadyCalc.length == 13;
		List<Move> legal_moves = new ArrayList<Move>();
		for(int i = 0; i < 13; i++){
			if(!alreadyCalc[i]){
				legal_moves.addAll(getPossibleMovesForCard(Cards.JOKER_AS_ACE + (i == 0 ? 12 : i - 1), playerToMoveWith));
			}
		}
		return legal_moves;
	}
	
	/**
	 * Method to get the possible moves for a normal card (all except:
	 * ace, four, jack, seven and king)
	 * @param pieces the pieces of the player
	 * @param card the card to use
	 * @param playerToMoveWith the player to move with
	 * @return a List of all possible <class>Move</class>s for this card/situation
	 */
	private List<Move> getPossibleMovesForNormalCard(int[] pieces, int card, byte playerToMoveWith){
		ArrayList<Move> legal_moves = new ArrayList<Move>();
		assert card != 100;
		
		// try with all pieces
		for(int p = 0; p < pieces.length; p++)
		{	
			// add normal move to list with possible moves:
			//try to go to heaven
			if(rules.isRulesConform(board, card, pieces[p], playerToMoveWith, true)){
				byte[] positions = new byte[2];
				positions[0] = (byte)pieces[p];
				positions[1] = (byte)(rules.getTargetPosInHeaven(board, pieces[p], card, playerToMoveWith)); 
				Move move = new Move(card, positions);
				legal_moves.add(move); 
			}
			//not going to heaven (also if possible)
			if(rules.isRulesConform(board, card, pieces[p], playerToMoveWith, false))
			{
				byte[] positions = new byte[2];
				positions[0] = (byte)pieces[p];
				positions[1] = (byte)((pieces[p] + (card % 13)) % 64 ); 
				Move move = new Move(card, positions);
				legal_moves.add(move); 
			}
		}// end for pieces
		
		return legal_moves;
	}
	
	/**
	 * gets the possible moves for an ace
	 * @param pieces the pieces of the player
	 * @param card the card to use
	 * @param playerToMoveWith the player to move with
	 * @return a List of all possible <class>Move</class>s for this card/situation
	 */
	private List<Move> getPossibleMovesForAce(int[] pieces, int card, byte playerToMoveWith){
		ArrayList<Move> legal_moves = new ArrayList<Move>();
		assert !(card % 13 != Cards.HEARTS_ACE || card == 100);
		
		// Use Ace to get out
		// StartPos is -1 in case the player isn't on the board yet.
		// homefield of player 1
		int home = 0; 
		try {
			home = rules.getHomePositionForPlayer(playerToMoveWith); 
		} catch(Exception e)  {	}

		//start with ace (to reduce complexity in depth search, the joker as ace will not be considered to start with)
		if(card != Cards.JOKER_AS_ACE && rules.isRulesConform(board, card, -1 , home , playerToMoveWith, false))
		{
			byte[] positions = new byte[2];
			positions[0] = -1;
			positions[1] = (byte)(home);
			Move move = new Move(card, positions);
			legal_moves.add(move);
		}
		//try with all pieces
		for(int p = 0; p<pieces.length; p++)
		{	
			// Use Ace to move 11
			if(rules.isRulesConform(board, card, pieces[p] , (pieces[p] + 11) % 64 , playerToMoveWith, false))
			{
				byte[] positions = new byte[2];
				positions[0] = (byte)pieces[p];
				positions[1] = (byte)((pieces[p] + 11) % 64); // move 11 fields
				Move move = new Move(card, positions);
				legal_moves.add(move);
			}
			
			// Use Ace to move 1
			if(rules.isRulesConform(board, card, pieces[p] , (pieces[p] + 1) % 64 , playerToMoveWith, false))
			{
				byte[] positions = new byte[2];
				positions[0] = (byte)pieces[p];
				positions[1] = (byte)((pieces[p] + 1) % 64); // move 1 field
				Move move = new Move(card, positions);
				legal_moves.add(move); 
			}
			//Try to get to heaven with ace as 11
			int targetPosInHeaven11 = rules.getTargetPosInHeavenForAceAsEleven(board, pieces[p], playerToMoveWith);
			if(rules.isRulesConform(board, card, pieces[p] , targetPosInHeaven11 , playerToMoveWith, true)){
				byte[] positions = new byte[2];
				positions[0] = (byte)pieces[p];
				positions[1] = (byte)targetPosInHeaven11;
				Move move = new Move(card, positions);
				legal_moves.add(move); 
			}
			//Try to get to heaven with ace as 1
			int targetPosInHeaven = rules.getTargetPosInHeaven(board, pieces[p], card, playerToMoveWith);
			if(rules.isRulesConform(board, card, pieces[p] , targetPosInHeaven , playerToMoveWith, true)){
				byte[] positions = new byte[2];
				positions[0] = (byte)pieces[p];
				positions[1] = (byte)targetPosInHeaven;
				Move move = new Move(card, positions);
				legal_moves.add(move); 
			}
		}// end for pieces
		
		return legal_moves;
	}

	/**
	 * gets the possible moves for a four
	 * @param pieces the pieces of the player
	 * @param card the card to use
	 * @param playerToMoveWith the player to move with
	 * @return a List of all possible <class>Move</class>s for this card/situation
	 */
	private List<Move> getPossibleMovesForFour(int[] pieces, int card, byte playerToMoveWith){
		ArrayList<Move> legal_moves = new ArrayList<Move>();
		assert !(card % 13 != Cards.HEARTS_FOUR || card == 100);
		
		// try with all pieces
		for(int p = 0; p<pieces.length; p++)
		{	
			// forward do not go to heaven (also if possible)
			if(rules.isRulesConform(board, card, pieces[p], (pieces[p] + 4) % 64 , playerToMoveWith, false))
			{
				byte[] positions = new byte[2];
				positions[0] = (byte)pieces[p];
				positions[1] = (byte)((pieces[p]+ 4) % 64); 
				Move move = new Move(card, positions);
				legal_moves.add(move);  
			}
			//forward try to get to heaven
			int targetPosInHeaven = rules.getTargetPosInHeaven(board, pieces[p], card, playerToMoveWith);
			if(rules.isRulesConform(board, card, pieces[p], targetPosInHeaven, playerToMoveWith, true))
			{
				byte[] positions = new byte[2];
				positions[0] = (byte)pieces[p];
				positions[1] = (byte)targetPosInHeaven; 
				Move move = new Move(card, positions);
				legal_moves.add(move);  
			}
			// backwards
			int targetPos = pieces[p] - 4;
			if(targetPos < 0){ targetPos += 64; }
			if(rules.isRulesConform(board, card, pieces[p], targetPos , playerToMoveWith, false))
			{
				byte[] positions = new byte[2];
				positions[0] = (byte)pieces[p];
				positions[1] = (byte)targetPos;
				Move move = new Move(card, positions);
				legal_moves.add(move);
			}
		}// end for pieces
		
		return legal_moves;
	}
	
	/**
	 * gets the possible moves for a seven
	 * @param pieces the pieces of the player
	 * @param card the card to use
	 * @param playerToMoveWith the player to move with
	 * @return a List of all possible <class>Move</class>s for this card/situation
	 */
	private List<Move> getPossibleMovesForSeven(int[] pieces, int card, byte playerToMoveWith){
		//ArrayList<Move> legal_moves = new ArrayList<Move>();
		assert !(card % 13 != Cards.HEARTS_SEVEN || card == 100);
		// try with all pieces
		
		byte [] heavenPieces = rules.getPiecesInHeavenOfPlayer(this.board, playerToMoveWith);
		boolean goOnWithPartner = true;
		int heavenCount = 0;
		for(int i = 0; i < heavenPieces.length; i++){
			if(heavenPieces[i] == Players.ANY_SAVE){ heavenCount += 1; }
		}
		if(heavenCount == 3 && heavenPieces[0] == Players.EMPTY){
			int[] lastPiece = rules.getMovablePiecesInGameForPlayer(board, playerToMoveWith, card);
			if(lastPiece.length == 1){
				int[] cards = ig.getCardsForPlayer(playerToMoveWith);
				for(int i = 0; i < cards.length; i++){
					if(cards[i] != -1 && cards[i] % 13 != 7){
						int targetPos = rules.getTargetPosInHeaven(board, lastPiece[0], cards[i], playerToMoveWith);
						if(targetPos == 64 + ((playerToMoveWith - 1) * 4)){
							goOnWithPartner = false;
						}
					}
				}
				
			}
		}
		//TODO: test if we have three in heaven and can't reach the last position with a card we've got actually.
		//TODO: give over cards, then if there are three in heaven test if it is possible to get to heaven with 4th with not 7 cards.
		//TODO: when yes don't go on with partner pieces, otherwise do it!
		
		//TODO: Test the distance between the pieces and use seven split such that the order doesn't matter.
		return getPossibleMovesForSevenSplit(rules, card, 7, pieces, 0, new int[7], new int[7], new boolean[7], playerToMoveWith, heavenPieces, new boolean[pieces.length], goOnWithPartner, false);
	}
	
	//* @param considerPartner is true when we want to add moves where we can go to heaven and go on with partner pieces
	/**
	 * recursive method to get all possible moves with a seven
	 * @param card the concrete card to use (HEARTS_SEVEN, CLUBS_SEVEN, etc.)
	 * @param points remaining points
	 * @param pieces the pieces of the playerToMoveWith
	 * @param actualPosition the actual position in the startPositions and targetPositions
	 * @param startPositions the collected startPositions for the actual move
	 * @param targetPositions the collected targetPositions for the actual move
	 * @param forHeaven a boolean array with containing the information if we will go to heaven with a part-move 
	 * @param playerToMoveWith the player to move with
	 * @param heavenPieces an array collecting information about heavenPositions of the player to move with (if we go to heaven with a part move this will be updated)
	 * @param visited an array to remember already used pieces
	 * @param goOnWithPartnerIfPossible is true when it is allowed to go to heaven with own pieces and then go on with the partner in one turn with the 7
	 * @param forPartner is true if moving with partner's pieces from now on
	 * @return an <class>ArrayList</class> with all possible <class>Move</Move>s 
	 */
	private List<Move> getPossibleMovesForSevenSplit(Rules rules, int card, int points, int[] pieces, int actualPosition, int[] startPositions, 
			int[] targetPositions, boolean[] forHeaven, byte playerToMoveWith, byte[] heavenPieces, boolean[] visited, 
			boolean goOnWithPartnerIfPossible, boolean forPartner){
		
		ArrayList<Move> legal_moves = new ArrayList<Move>();
		
		boolean allInHeaven = true;
		for(int i = 0; i < heavenPieces.length; i++){
			if(heavenPieces[i] != 5){
				allInHeaven = false;
				break;
			}
		}
		try{
			if(allInHeaven && rules.allPiecesInHeavenOfPlayer(board, rules.getPartnerForPlayer(playerToMoveWith))){
				allInHeaven = false;
			}
		}catch(Exception ex){
			msg.debug(this, ex.getMessage());
			return legal_moves;
		}
		
		boolean allVisited = true;
		for(int i = 0; i < visited.length; i++){
			if(!visited[i]){ 
				allVisited = false;
				break;
			}
		}
			
		//there are no more pieces, but there are points left!
		if(points != 0 && (allVisited && !allInHeaven)){
			return legal_moves;
		}
		
		//termination conditions
		if(points == 0 || (allVisited && !allInHeaven)){
			int[] startPos = new int[actualPosition];
			int[] targetPos = new int[actualPosition];
			boolean[] forHeav = new boolean[actualPosition];
			for(int i = 0; i < actualPosition; i++){
				startPos[i] = startPositions[i];
				targetPos[i] = targetPositions[i];
				forHeav[i] = forHeaven[i];
			}
			byte partnerPlayer = 0;
			if(forPartner){
				try {
					partnerPlayer = rules.getPartnerForPlayer(playerToMoveWith);
				} catch (Exception e) {
					msg.debug(this, e.getMessage());
					assert false;
				}
			}
			
			if(rules.isRulesConform(board, startPos, targetPos, forPartner ? partnerPlayer : playerToMoveWith, forHeav)){
				byte[] positions = new byte[actualPosition * 2];
				
				for(int i = 0; i < actualPosition; i++){
					positions[2*i] = (byte)startPositions[i];
					positions[(2*i)+1] = (byte)targetPositions[i];
				}
				
				Move move = new Move(card, positions);
				legal_moves.add(move);
				}
			return legal_moves;
		}
		
		//go on with partner pieces if not all pieces of actual player are in heaven now and there are points left
		if(goOnWithPartnerIfPossible && allInHeaven){
			try{
				byte partnerPlayer = rules.getPartnerForPlayer(playerToMoveWith);
				int[] partnerPieces = rules.getPiecesInGameForPlayer(board, partnerPlayer);
				byte[] partnerHeavenPieces = rules.getPiecesInHeavenOfPlayer(board, partnerPlayer);
				legal_moves.addAll(getPossibleMovesForSevenSplit(rules, card, points, partnerPieces, actualPosition, startPositions, targetPositions, forHeaven, partnerPlayer, partnerHeavenPieces, new boolean[partnerPieces.length], goOnWithPartnerIfPossible, true));
			}catch(Exception ex){
				msg.debug(this, ex.getMessage());
				return legal_moves;
			}
		}
		for(int j = 0; j < pieces.length; j++){
			if(visited[j]){ continue; } //don't try to move with an already used piece again!
			int oldPosition = actualPosition;
			for(int i = points; i > 0; i--){
				//normal move
				assert actualPosition != startPositions.length;
				
				if(pieces[j] < 64){
					startPositions[actualPosition] = pieces[j];
					targetPositions[actualPosition] = (pieces[j] + i) % 64;
					forHeaven[actualPosition++] = false;
				}
				
				visited[j] = true;
				legal_moves.addAll(getPossibleMovesForSevenSplit(rules, card, points - i, pieces, actualPosition, startPositions, targetPositions, forHeaven, playerToMoveWith, heavenPieces, visited, goOnWithPartnerIfPossible, forPartner));
				actualPosition = oldPosition;
				
				//move to heaven
				int targetPosInHeaven = rules.getTargetPosInHeaven(board, pieces[j], i, playerToMoveWith);
				if(targetPosInHeaven != -1){
					startPositions[actualPosition] = pieces[j];
					targetPositions[actualPosition] = targetPosInHeaven;
					forHeaven[actualPosition++] = true;
					int heavenPos = targetPosInHeaven - 60 - (4 * playerToMoveWith);
					byte oldHeavenOccupant = heavenPieces[heavenPos];
					heavenPieces[heavenPos] = 5;
					legal_moves.addAll(getPossibleMovesForSevenSplit(rules, card, points - i, pieces, actualPosition, startPositions, targetPositions, forHeaven, playerToMoveWith, heavenPieces, visited, goOnWithPartnerIfPossible, forPartner));
					heavenPieces[heavenPos] = oldHeavenOccupant;
				}
				
				actualPosition = oldPosition;
				visited[j] = false;
			}
			/*
			//TODO: find a way that the order of the pieces doesn't matter anymore
			//TODO: this is only a rough thing because only the first piece is not more considered after all possible moves with it are done.
			if(points == 7){ //when all possibilities for this piece are tried out, then do not consider it again!
				visited[j] = true;
			}
			*/
		}
		return legal_moves;
	}
	
	/**
	 * gets the possible moves for a jack
	 * @param pieces the pieces of the player
	 * @param card the card to use
	 * @param playerToMoveWith the player to move with
	 * @return a List of all possible <class>Move</class>s for this card/situation
	 */
	private List<Move> getPossibleMovesForJack(int[] pieces, int card, byte playerToMoveWith){
		ArrayList<Move> legal_moves = new ArrayList<Move>();
		assert !(card % 13 != Cards.HEARTS_JACK || card == 100);
		byte nextPlayer = playerToMoveWith;
		int [] othersPieces1 = new int[0];
		int [] othersPieces2 = new int[0];
		int [] othersPieces3 = new int[0];
		
		try {
			nextPlayer = getNextPlayer(nextPlayer);
			othersPieces1 = rules.getMovablePiecesInGameForPlayer(this.board, nextPlayer, card);
			nextPlayer = getNextPlayer(nextPlayer);
			othersPieces2 = rules.getMovablePiecesInGameForPlayer(this.board, nextPlayer, card);
			nextPlayer = getNextPlayer(nextPlayer);
			othersPieces3 = rules.getMovablePiecesInGameForPlayer(this.board, nextPlayer, card);
		} catch (Exception e) {
			return legal_moves;
		}
		
		//try with all pieces
		for(int p = 0; p<pieces.length; p++)
		{	
			for(int i = 0; i < othersPieces1.length; i++){
				if(rules.isRulesConform(this.board, card, pieces[p], othersPieces1[i], playerToMoveWith, false)){
					byte[] positions = new byte[2];
					positions[0] = (byte)pieces[p];
					positions[1] = (byte)othersPieces1[i];
					Move move = new Move(card, positions);
					legal_moves.add(move);
				}
			}
			for(int i = 0; i < othersPieces2.length; i++){
				if(rules.isRulesConform(this.board, card, pieces[p], othersPieces2[i], playerToMoveWith, false)){
					byte[] positions = new byte[2];
					positions[0] = (byte)pieces[p];
					positions[1] = (byte)othersPieces2[i];
					Move move = new Move(card, positions);
					legal_moves.add(move);
				}
			}
			for(int i = 0; i < othersPieces3.length; i++){
				if(rules.isRulesConform(this.board, card, pieces[p], othersPieces3[i], playerToMoveWith, false)){
					byte[] positions = new byte[2];
					positions[0] = (byte)pieces[p];
					positions[1] = (byte)othersPieces3[i];
					Move move = new Move(card, positions);
					legal_moves.add(move);
				}
			}
		}
		return legal_moves;
	}
	
	/**
	 * gets the possible moves for a king
	 * @param pieces the pieces of the player
	 * @param card the card to use
	 * @param playerToMoveWith the player to move with
	 * @return a List of all possible <class>Move</class>s for this card/situation
	 */
	private List<Move> getPossibleMovesForKing(int[] pieces, int card, byte playerToMoveWith){
		ArrayList<Move> legal_moves = new ArrayList<Move>();
		//assert !(card % 13 != Cards.HEARTS_KING || card == 100); //TODO:clarify this assertion
		// almost same as Ace
		// Use King to get out
		// StartPos is -1 in case the player isn't on the board yet.
		int home = 0; 
		try {
			home = rules.getHomePositionForPlayer(playerToMoveWith); 
		} catch(Exception e)  {	}
		//start with king
		if(rules.isRulesConform(board, card, -1, playerToMoveWith, false))
		{
			byte[] positions = new byte[2];
			positions[0] = -1;
			positions[1] = (byte)(home);
			Move move = new Move(card, positions);
			legal_moves.add(move);  
		}

		//try with all pieces
		for(int p = 0; p<pieces.length; p++)
		{	
			// Use King to move but do not go to heaven (also if possible)
			if( (pieces.length != 0) && rules.isRulesConform(board, card, pieces[p], playerToMoveWith, false))
			{
				byte[] positions = new byte[2];
				positions[0] = (byte)pieces[p];
				positions[1] = (byte)((pieces[p] + 13) % 64); // move 13 fields
				Move move = new Move(card, positions);
				legal_moves.add(move);
			}
			//try to get to heaven with king
			if( (pieces.length != 0) && rules.isRulesConform(board, card, pieces[p], playerToMoveWith, true))
			{
				byte[] positions = new byte[2];
				positions[0] = (byte)pieces[p];
				positions[1] = (byte)(rules.getTargetPosInHeaven(board, pieces[p], card, playerToMoveWith)); // move 13 fields
				Move move = new Move(card, positions);
				legal_moves.add(move);
			}
			
		}// end for pieces
		
		return legal_moves;
	}
	
	/** 
	 * Searchs through all cards for a King, Ace or Joker
	 * That are the cards to go to the homefield
	 * @param cards input cards
	 * @return true if the player has a card to get out, false otherwise
	 */
	private boolean canGoToHomefield(int[] cards)
	{
		for(int i=0; i<cards.length; i++) 
			if(cards[i]%13 == 1 || cards[i]%13 == 0 || cards[i] == Cards.JOKER)
				return true; 
		
		return false; 
	}
	
	/**
	 * Method that will find all possibilities to go out of home with the given cards
	 * @param myCards the cards to search in
	 * @return all possible moves to go out of home
	 */
	private List<Move> checkKingAce(int[] myCards, byte playerOnTurn) {
		
		ArrayList<Move> legal_moves = new ArrayList<Move>(); 
		
		for(int index=0; index<myCards.length; index++)
		{
			int cardType = myCards[index] % 13; 
			
			if( cardType == 1 || cardType == 0 || myCards[index] == 100) 
			{  // Ace or King or Joker

				int home = 0; 
				try {
					home = rules.getHomePositionForPlayer(playerOnTurn); 
				} catch(Exception e)  {	}
				//for King
				if(cardType == 0){
					if(rules.isRulesConform(board, myCards[index], -1, playerOnTurn, false))
					{
						Move move = new Move(myCards[index], new byte[] {-1, (byte)home});
						legal_moves.add(move); 
					}
				}
				//when using Joker we are using it as an ace to get out of home.
				else if(rules.isRulesConform(board, myCards[index] == 100 ? Cards.JOKER_AS_ACE : myCards[index], -1 , home , playerOnTurn, false))
				{
					Move move = new Move(myCards[index] == 100 ? Cards.JOKER_AS_ACE : myCards[index], new byte[] {-1, (byte)home});
					legal_moves.add(move); 
				}
			}
		} // end for
		return legal_moves; 
	}
	
	
	public BotBoard[] getAllSuccessors(){
		//TODO: implement this method!
		return null;
	}
	
	public BotBoard[] getSuccessorsForCard(int card){
		//TODO: implement this method!
		return null;
	}
	
	

	/**
	 * This method will execute the move given over on the board
	 * and push it to a move stack
	 * @param move The <class>Move</class> to execute
	 * @param playerOnTurn the player to move with
	 */
	public void makeMove(Move move, byte playerOnTurn)
	{
		int[] cards = ig.getCardsForPlayer(playerOnTurn);
		// First perform the move on the board
		byte[] positions = move.getPositions();
		assert this.board[positions[1]] != Players.ANY_SAVE;
		
		if(move.getCard() % 13 == Cards.HEARTS_JACK){
			move.setHits(new byte[]{this.board[positions[1]], positions[1]});
			byte oldPiece = this.board[positions[0]];
			this.board[positions[0]] = this.board[positions[1]];
			this.board[positions[1]] = oldPiece;
			move.setWasProtected(new boolean[] {false});
			
		}else if(move.getCard() % 13 == Cards.HEARTS_SEVEN){
			
			byte[][] hits = new byte[positions.length/2][16];
			boolean [] wasProtected = new boolean[positions.length / 2];
			int hit_count = 0;  
			
			for(int i = 0; i < positions.length; i+=2, hit_count++)
			{	
				
				byte oldPiece = board[positions[i]];
				if(positions[i] < 64 && board[positions[i]] == Players.ANY_SAVE){
					switch(positions[i]){
						//HOMEPOS_P1
						case 0:
							oldPiece = Players.P1;
							break;
						//HOMEPOS_P2
						case 16:
							oldPiece = Players.P2;
							break;
						//HOMEPOS_P3
						case 32:
							oldPiece = Players.P3;
							break;
						//HOMEPOS_P4
						case 48:
							oldPiece = Players.P4;
							break;
					}
				}
				if(positions[i+1] >= 64){
					// changed oldPiece to playerOnTurn - otherwise it's 5 and asserts
					int homePosition = rules.getHomePositionForPlayer(/*playerOnTurn*/ oldPiece); 
					
					int hitPos = 0;
					for(int j = positions[i]+1; j <= homePosition; j++)
					{
						if(this.board[j] != 0){
							//if(this.board[j] != oldPiece){
								hits[hit_count][hitPos++] = this.board[j];
								hits[hit_count][hitPos++] = (byte)j;
							//}
							this.board[j] = 0;
						}
					}
				}else if(positions[i] <= positions[i+1]){
					
					int hitPos = 0;
					for(int j = positions[i] + 1; j <= positions[i+1]; j++)
					{	
						if(this.board[j] != 0){
							if(j <= positions[i+1] /*&& this.board[j] != oldPiece */)
							{
								hits[hit_count][hitPos++] = this.board[j];
								hits[hit_count][hitPos++] = (byte)j;
							}
							this.board[j] = 0;
							if(j == positions[i+1]){
								this.board[j] = j > 63 ? Players.ANY_SAVE : oldPiece;
							}
						}
					}
				}else{
					int hitPos = 0;
					for(int j = positions[i] + 1; j <= 63; j++)
					{
						if(this.board[j] != 0){
						//	if(this.board[j] != oldPiece){
							hits[hit_count][hitPos++] = this.board[j];
							hits[hit_count][hitPos++] = (byte)j;
						//	}
							this.board[j] = 0;
						}
					}
					
					for(int j = 0; j <= positions[i+1]; j++){
						if(this.board[j] != 0){
							if(j <= positions[i+1] /* && this.board[j] != oldPiece */){
								hits[hit_count][hitPos++] = this.board[j];
								hits[hit_count][hitPos++] = (byte)j;
							}
							this.board[j] = 0;
						}
					}
				}
				wasProtected[i/2] = this.board[positions[i]] == Players.ANY_SAVE;
				this.board[positions[i]] = 0;
				this.board[positions[i+1]] = positions[i+1] > 63 ? Players.ANY_SAVE : oldPiece;
			}
			move.setWasProtected(wasProtected);
			move.set7Hits(hits); 
			
		}else {
			byte oldPiece = 0;
			if(positions[0] != -1){
				oldPiece = board[positions[0]];
				if(positions[0] < 64 && board[positions[0]] == Players.ANY_SAVE){
					switch(positions[0]){
						//HOMEPOS_P1
						case 0:
							oldPiece = Players.P1;
							break;
						//HOMEPOS_P2
						case 16:
							oldPiece = Players.P2;
							break;
						//HOMEPOS_P3
						case 32:
							oldPiece = Players.P3;
							break;
						//HOMEPOS_P4
						case 48:
							oldPiece = Players.P4;
							break;
					}
				}
				move.setWasProtected(new boolean[] {this.board[positions[0]] == Players.ANY_SAVE});
				this.board[positions[0]] = 0;
			}
			if(this.board[positions[1]] != Players.EMPTY){
				move.setHits(new byte[] {this.board[positions[1]], positions[1]});
			}
			this.board[positions[1]] = (positions[0] == -1 || positions[1] > 63) ? Players.ANY_SAVE : oldPiece;
		}
		//next player is on turn 
		//playerOnTurn = getNextPlayer(playerOnTurn); 
		//remove used card 
		int usedCardPos = 0; 
		int[] myCardsOld = new int[cards.length]; 
		for(int i = 0; i < cards.length; i++){ 
		int usedCard = move.getCard(); 
		if(cards[i] == (usedCard >= 53 ? Cards.JOKER : usedCard)){ usedCardPos = i; } 
			myCardsOld[i] = cards[i]; 
		} 
		for(int i = usedCardPos; i < cards.length - 1; i++){ 
			cards[i] = myCardsOld[i + 1]; 
		} 
		cards[cards.length - 1] = -1; 
		// Then put it on the stack 
		moveStack.push(move); 
	}

	/**
	 * this method will pop the last move from the stack
	 * and undo it (if there is one).
	 * @param playerOnTurn the player for which to undo the move
	 */
	public void undoMove(byte playerOnTurn)
	{
		// pop last move and perform it on the board
		if( !moveStack.isEmpty())
		{
			int[] cards = ig.getCardsForPlayer(playerOnTurn);
			Move lastMove = moveStack.pop();
			//this.playerOnTurn = getLastPlayer(this.playerOnTurn);
			//reset positions
			byte[] positions = lastMove.getPositions();
			
			byte[][] hits7 = lastMove.get7Hits(); 
			
			for(int i = positions.length - 1, c = positions.length/2; i >= 0; i-=2, c--)
			{
				byte oldPiece = playerOnTurn;
				if(positions[i - 1] != -1){
					
					if(rules.allPiecesInHeavenOfPlayer(this.board, playerOnTurn)){
						switch(oldPiece){
							case Players.P1:
								oldPiece = positions[i] >=64 && positions[i] < 68 ? oldPiece : rules.getPartnerForPlayer(oldPiece);
								break;
							case Players.P2:
								oldPiece = positions[i] >=68 && positions[i] < 72 ? oldPiece : rules.getPartnerForPlayer(oldPiece);
								break;
							case Players.P3:
								oldPiece = positions[i] >=72 && positions[i] < 76 ? oldPiece : rules.getPartnerForPlayer(oldPiece);
								break;
							case Players.P4:
								oldPiece = positions[i] >=76 && positions[i] < 80 ? oldPiece : rules.getPartnerForPlayer(oldPiece);
								break;
						}
					}
					this.board[positions[i - 1]] = lastMove.getwasProtected()[i/2] ? Players.ANY_SAVE : oldPiece; 
				}
				this.board[positions[i]] = 0;
				
				
				// Restore cards for 7 (revert each move and it's appropriate hits)
				if(lastMove.getCard()%13 == 7)
				{
					if(hits7.length != 0)
					{
						for(int j = 0; j < hits7[c-1].length; j+=2)
						{		
							if(hits7[c-1][j] != 0)
								this.board[hits7[c-1][j+1]] = hits7[c-1][j];			
						}
					}
				}
			} // end position
			
		
			if(lastMove.getCard()%13 != 7)
			{
				//reset hit pieces
				byte[] hits = lastMove.getHits();
				for(int i = 0; i < hits.length; i+=2)
				{		
					this.board[hits[i+1]] = hits[i];			
				}
			}
			
			//readd card
			int usedCard = lastMove.getCard() >= 53 ? Cards.JOKER : lastMove.getCard();
			for(int i = 0; i < cards.length; i++)
			{
				if(cards[i] == -1){
					cards[i] = usedCard;
					break;
				}
			}
		}
	}
	
	
	/**
	 * This version of makeMove doesn't push the move on the stack as well as it doesn't consider
	 * the cards. It is only used by the InformationGatherer to simulate a certain situation.
	 * @param playerOnTurn
	 * @param move
	 */
	public void makeMove(byte playerOnTurn, Move move)
	{
		
		if(move != null)
		{
			byte[] positions = move.getPositions();
			assert this.board[positions[1]] != Players.ANY_SAVE;

			if(move.getCard() % 13 == Cards.HEARTS_JACK){
				move.setHits(new byte[]{this.board[positions[1]], positions[1]});
				byte oldPiece = this.board[positions[0]];
				this.board[positions[0]] = this.board[positions[1]];
				this.board[positions[1]] = oldPiece;
				move.setWasProtected(new boolean[] {false});

			}else if(move.getCard() % 13 == Cards.HEARTS_SEVEN){

				byte[][] hits = new byte[positions.length/2][16];
				boolean [] wasProtected = new boolean[positions.length / 2];
				int hit_count = 0;  

				for(int i = 0; i < positions.length; i+=2, hit_count++)
				{	

					byte oldPiece = board[positions[i]];
					if(positions[i] < 64 && board[positions[i]] == Players.ANY_SAVE){
						switch(positions[i]){
						//HOMEPOS_P1
						case 0:
							oldPiece = Players.P1;
							break;
							//HOMEPOS_P2
						case 16:
							oldPiece = Players.P2;
							break;
							//HOMEPOS_P3
						case 32:
							oldPiece = Players.P3;
							break;
							//HOMEPOS_P4
						case 48:
							oldPiece = Players.P4;
							break;
						}
					}
					if(positions[i+1] >= 64){
						// changed oldPiece to playerOnTurn - otherwise it's 5 and asserts
						int homePosition = rules.getHomePositionForPlayer(/*playerOnTurn*/ oldPiece); 

						int hitPos = 0;
						for(int j = positions[i]+1; j <= homePosition; j++)
						{
							if(this.board[j] != 0){
								//if(this.board[j] != oldPiece){
								hits[hit_count][hitPos++] = this.board[j];
								hits[hit_count][hitPos++] = (byte)j;
								//}
								this.board[j] = 0;
							}
						}
					}else if(positions[i] <= positions[i+1]){

						int hitPos = 0;
						for(int j = positions[i] + 1; j <= positions[i+1]; j++)
						{	
							if(this.board[j] != 0){
								if(j <= positions[i+1] /*&& this.board[j] != oldPiece */)
								{
									hits[hit_count][hitPos++] = this.board[j];
									hits[hit_count][hitPos++] = (byte)j;
								}
								this.board[j] = 0;
								if(j == positions[i+1]){
									this.board[j] = j > 63 ? Players.ANY_SAVE : oldPiece;
								}
							}
						}
					}else{
						int hitPos = 0;
						for(int j = positions[i] + 1; j <= 63; j++)
						{
							if(this.board[j] != 0){
								//	if(this.board[j] != oldPiece){
								hits[hit_count][hitPos++] = this.board[j];
								hits[hit_count][hitPos++] = (byte)j;
								//	}
								this.board[j] = 0;
							}
						}

						for(int j = 0; j <= positions[i+1]; j++){
							if(this.board[j] != 0){
								if(j <= positions[i+1] /* && this.board[j] != oldPiece */){
									hits[hit_count][hitPos++] = this.board[j];
									hits[hit_count][hitPos++] = (byte)j;
								}
								this.board[j] = 0;
							}
						}
					}
					wasProtected[i/2] = this.board[positions[i]] == Players.ANY_SAVE;
					this.board[positions[i]] = 0;
					this.board[positions[i+1]] = positions[i+1] > 63 ? Players.ANY_SAVE : oldPiece;
				}
				move.setWasProtected(wasProtected);
				move.set7Hits(hits); 

			}else {
				byte oldPiece = 0;
				if(positions[0] != -1){
					oldPiece = board[positions[0]];
					if(positions[0] < 64 && board[positions[0]] == Players.ANY_SAVE){
						switch(positions[0]){
						//HOMEPOS_P1
						case 0:
							oldPiece = Players.P1;
							break;
							//HOMEPOS_P2
						case 16:
							oldPiece = Players.P2;
							break;
							//HOMEPOS_P3
						case 32:
							oldPiece = Players.P3;
							break;
							//HOMEPOS_P4
						case 48:
							oldPiece = Players.P4;
							break;
						}
					}
					move.setWasProtected(new boolean[] {this.board[positions[0]] == Players.ANY_SAVE});
					this.board[positions[0]] = 0;
				}
				if(this.board[positions[1]] != Players.EMPTY){
					move.setHits(new byte[] {this.board[positions[1]], positions[1]});
				}
				this.board[positions[1]] = (positions[0] == -1 || positions[1] > 63) ? Players.ANY_SAVE : oldPiece;
			}
		}
	}
	/**
	 * this method will undo a certain move on the board. This method doesn't restore
	 * the cards of the player. It is only used by the InformationGatherer to revert to a certain state 
	 * in the InformationGatherer in order to reproduce the move.
	 * @param playerOnTurn the player for which to undo the move
	 * @param move the move to perform
	 */
	public void undoMove(byte playerOnTurn, Move move)
	{
		if(move != null)
		{
			byte[] positions = move.getPositions();
			
			byte[][] hits7 = move.get7Hits(); 
			
			for(int i = positions.length - 1, c = positions.length/2; i >= 0; i-=2, c--)
			{
				byte oldPiece = playerOnTurn;
				if(positions[i - 1] != -1){
					
					if(rules.allPiecesInHeavenOfPlayer(this.board, playerOnTurn)){
						switch(oldPiece){
							case Players.P1:
								oldPiece = positions[i] >=64 && positions[i] < 68 ? oldPiece : rules.getPartnerForPlayer(oldPiece);
								break;
							case Players.P2:
								oldPiece = positions[i] >=68 && positions[i] < 72 ? oldPiece : rules.getPartnerForPlayer(oldPiece);
								break;
							case Players.P3:
								oldPiece = positions[i] >=72 && positions[i] < 76 ? oldPiece : rules.getPartnerForPlayer(oldPiece);
								break;
							case Players.P4:
								oldPiece = positions[i] >=76 && positions[i] < 80 ? oldPiece : rules.getPartnerForPlayer(oldPiece);
								break;
						}
					}
					// FIXME: assert because wasProtected is not set with jack in case undo move is first called before makeMove. 
					// that's only the case in canGoToHeaven and couldHitEnemy
					if(move.getwasProtected().length==0)   
						this.board[positions[i - 1]] = oldPiece; 
					else
						this.board[positions[i - 1]] = move.getwasProtected()[i/2] ? Players.ANY_SAVE : oldPiece; 
				}
				this.board[positions[i]] = 0;
				
				
				// Restore cards for 7 (revert each move and it's appropriate hits)
				if(move.getCard()%13 == 7)
				{
					if(hits7.length != 0)
					{
						for(int j = 0; j < hits7[c-1].length; j+=2)
						{		
							if(hits7[c-1][j] != 0)
								this.board[hits7[c-1][j+1]] = hits7[c-1][j];			
						}
					}
				}
			} // end position
			
		
			if(move.getCard()%13 != 7)
			{
				//reset hit pieces
				byte[] hits = move.getHits();
				for(int i = 0; i < hits.length; i+=2)
				{		
					this.board[hits[i+1]] = hits[i];			
				}
			}
		}
	}
		
	/**
	 * method to decide if a card is a normal card
	 * normal in this case means:
	 * two, three, five, six, eight, nine, ten or queen
	 * @param card the card to check
	 * @return true if it is a normal card, false if not
	 */
	private boolean isNormalCard(int card)
	{
		if(card == 100) return false; 
		
		int type = card % 13; 
		return ( (type == 2) || (type == 3) || (type == 5) || (type == 6) 
				|| (type >= 8 && type <= 10) || (type == 12) )  ; 
	}
	
	public String toString()
	{
		StringBuffer buffer = new StringBuffer(); 
		
		for(int i=0; i<board.length; i++) 
			buffer.append((board[i] + ",")); 
	
		buffer.append("\n");
		return buffer.toString(); 
	}

	/**
	 * @return the board
	 */
	public byte[] getBoard() {
		return board;
	}

	/**
	 * returns the player that is on turn after the given player
	 * @param currentPlayer the player actually on turn
	 * @return the next player on turn
	 */
	public byte getNextPlayer(byte currentPlayer){
		byte nextPlayer = (byte)((currentPlayer + 1) % 5);
		return nextPlayer == 0 ? 1 : nextPlayer;
	}
	
	/**
	 * returns the player that is on turn before the given player
	 * @param currentPlayer the player actually on turn
	 * @return the last player on turn
	 */
	public byte getLastPlayer(byte currentPlayer){
		byte nextPlayer = (byte)(currentPlayer - 1);
		return nextPlayer == 0 ? 4 : nextPlayer;
	}

	/**
	 * @param board the board to set
	 */
	public void setBoard(byte[] board) {
		this.moveStack.clear();
		this.board = board;
	}
	
	/**
	 * Check if all player of one team are in heaven
	 * @param player player to check
	 * @return true if all pawns of player and his partner are in heaven, false otherwise
	 */
	public boolean terminalState(byte player)
	{
		byte partner = rules.getPartnerForPlayer(player); 
		
		if(rules.allPiecesInHeavenOfPlayer(this.board, player) &&
				rules.allPiecesInHeavenOfPlayer(this.board, partner))
			return true; 
		else
			return false; 
	}
	

}
