package intelliDOG.ai.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import intelliDOG.ai.framework.BotBoard;
import intelliDOG.ai.framework.Cards;
import intelliDOG.ai.framework.InformationGatherer;
import intelliDOG.ai.framework.Move;
import intelliDOG.ai.framework.Players;
import intelliDOG.ai.framework.Rules;
import intelliDOG.ai.utils.DebugMsg;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class BotBoardTest {
	private BotBoard board;
	private InformationGatherer ig;
	private byte[] boardArray;
	private DebugMsg msg = DebugMsg.getInstance();
	
	
	@Before
	public void setUp() throws Exception {
		boardArray = new byte[80];
		// enable to verify results
		//msg.addItemForWhiteList(this);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetAllPossibleMoves() {
		boardArray[63] = 2;
		ig = new InformationGatherer(Players.P2);
		ig.setCardsForPlayer(new int[] { 4, -1, -1, -1, -1, -1 }, Players.P2);
		board = new BotBoard(boardArray, ig);
		assertEquals(2, board.getAllPossibleMoves(Players.P2).size());
	}

	@Test
	public void testGetAllPossibleMoves2() {
		boardArray[68] = 5;
		boardArray[69] = 5;
		boardArray[70] = 5;
		boardArray[71] = 5;
		ig = new InformationGatherer(Players.P2);
		ig.setCardsForPlayer(new int[] { 4, 26, 8, -1, -1, -1 }, Players.P2);
		board = new BotBoard(boardArray, ig);
		assertEquals(1, board.getAllPossibleMoves(Players.P2).size());
		assertEquals(48, board.getAllPossibleMoves(Players.P2).get(0).getPositions()[1]);
	}

	@Test
	public void testGetAllPossibleMoves3() {
		// without protected 32
		boardArray[68] = 5;
		boardArray[69] = 5;
		boardArray[70] = 5;
		boardArray[71] = 5;

		boardArray[76] = 5;
		boardArray[79] = 5;
		boardArray[31] = 4;
		ig = new InformationGatherer(Players.P2);
		ig.setCardsForPlayer(new int[] { 1, 8, 8, 10, -1, -1 }, Players.P2);
		board = new BotBoard(boardArray, ig);
		assertEquals(7, board.getAllPossibleMoves(Players.P2).size());

		// with protected 32
		boardArray[32] = 5;
		board.setBoard(boardArray);
		assertEquals(2, board.getAllPossibleMoves(Players.P2).size());

		/*
		 * P4: 76 79 18 (danach 18 getauscht nach 31?)
		 * 
		 * 
		 * intelliDOG.ai.framework.Board: Cards: 1 8 8 10 -1 -1
		 * intelliDOG.ai.framework.Board: Pieces: 68 69 70 71
		 * intelliDOG.ai.framework.Board: Legal moves: card -> 1, positions ->
		 * s: -1, t: 48 card -> 1, positions -> s: 76, t: 77 card -> 1,
		 * positions -> s: 31, t: 42 card -> 1, positions -> s: 31, t: 32 card ->
		 * 8, positions -> s: 31, t: 39 card -> 34, positions -> s: 31, t: 39
		 * card -> 10, positions -> s: 31, t: 41 intelliDOG.ai.bots.SimpleBot:
		 * Karte: 8, Bewertung: 52, s: 31, t: 39 intelliDOG.ai.bots.SimpleBot:
		 * Karte: Herz Acht, konkrete Karte: Herz Acht
		 */

	}
	
	@Test
	public void testGetAllPossibleMoves4() {
		
		boardArray[46] = 4;
		boardArray[77] = 5;
		boardArray[79] = 5;
		ig = new InformationGatherer(Players.P4);
		ig.setCardsForPlayer(new int[] { 7, 5, 3, 11, 9, -1 }, Players.P4);
		board = new BotBoard(boardArray, ig);
		List<Move> possible = board.getAllPossibleMoves(Players.P4);
		msg.debug(this, "-------------------------GetAllPossibleMoves4-------------------------");
		msg.debugLegalMoves(this, possible);
		msg.debug(this, "-----------------------End GetAllPossibleMoves4-----------------------");
		
		assertEquals(7, possible.size());
		
		/*
		 * intelliDOG.ai.framework.Board: Player on Turn: 4
		 * intelliDOG.ai.framework.Board: Cards: 7 5 3 11 9 -1 
		 * intelliDOG.ai.framework.Board: Pieces: 77 79 46 
		 * Exception in thread "Bot SlauBotZwei" java.lang.RuntimeException: Es trat ein schwerer Fehler auf. Das Spiel wird beendet. (3)
		 */
		
	}
	
	@Test
	public void testGetAllPossibleMoves5() {
		
		boardArray[46] = 4;
		ig = new InformationGatherer(Players.P4);
		ig.setCardsForPlayer(new int[] { 7, -1, -1, -1, -1, -1 }, Players.P4);
		board = new BotBoard(boardArray, ig);
		List<Move> possible = board.getAllPossibleMoves(Players.P4);
		msg.debug(this, "-------------------------GetAllPossibleMoves5-------------------------");
		msg.debugLegalMoves(this, possible);
		msg.debug(this, "-----------------------End GetAllPossibleMoves5-----------------------");
		
		assertEquals(1, possible.size());
		
		/*
		 * intelliDOG.ai.framework.Board: Player on Turn: 4
		 * intelliDOG.ai.framework.Board: Cards: 7 -1 -1 -1 -1 -1 
		 * intelliDOG.ai.framework.Board: Pieces: 46 
		 * Exception in thread "Bot SlauBotZwei" java.lang.RuntimeException: Es trat ein schwerer Fehler auf. Das Spiel wird beendet. (1)
		 */
		
	}
	
	// tschui: FIXME - not implemented yet
	@Test
	public void testGetAllPossibleMoves6() {
		
		boardArray[45] = 4;
		ig = new InformationGatherer(Players.P4);
		ig.setCardsForPlayer(new int[] { 7, 9, 6, 10, 11, -1 }, Players.P4);
		board = new BotBoard(boardArray, ig);
		List<Move> possible = board.getAllPossibleMoves(Players.P4);
		msg.debug(this, "-------------------------GetAllPossibleMoves6-------------------------");
		msg.debugLegalMoves(this, possible);
		msg.debug(this, "-----------------------End GetAllPossibleMoves6-----------------------");
		assertEquals(6, possible.size());
		
		boardArray = new byte[80];
		boardArray[76] = 5;
		boardArray[77] = 5;
		boardArray[78] = 5;
		boardArray[79] = 5;
		boardArray[45] = 2;
		board.setBoard(boardArray);
		possible = board.getAllPossibleMoves(Players.P4);
		msg.debug(this, "------------------------GetAllPossibleMoves6 2------------------------");
		msg.debugLegalMoves(this, possible);
		msg.debug(this, "----------------------End GetAllPossibleMoves6 2----------------------");
		assertEquals(4, possible.size());
		
		
		boardArray = new byte[80];
		boardArray[47] = 4;
		boardArray[77] = 5;
		boardArray[78] = 5;
		boardArray[79] = 5;
		boardArray[45] = 2;
		board.setBoard(boardArray);
		possible = board.getAllPossibleMoves(Players.P4);
		msg.debug(this, "------------------------GetAllPossibleMoves6 3------------------------");
		msg.debugLegalMoves(this, possible);
		msg.debug(this, "----------------------End GetAllPossibleMoves6 3----------------------");
		assertEquals(6, possible.size()); //moving with every card possible + with seven to heaven and then going on with partner pieces
		
		/*
		 * intelliDOG.ai.framework.Board: Player on Turn: 4
		 * intelliDOG.ai.framework.Board: Cards: 7 9 6 10 11 -1 
		 * intelliDOG.ai.framework.Board: Pieces: 45 
		 * Exception in thread "Bot SlauBotZwei" java.lang.RuntimeException: Es trat ein schwerer Fehler auf. Das Spiel wird beendet. (1)
		 */
	}
	
	@Test
	public void testGetAllPossibleMoves7() {
		
		//finishing with own and going on with partner's pieces
		boardArray[47] = 4;
		boardArray[77] = 5;
		boardArray[78] = 5;
		boardArray[79] = 5;
		boardArray[45] = 2;
		ig = new InformationGatherer(Players.P4);
		ig.setCardsForPlayer(new int[] { 7, -1, -1, -1, -1, -1 }, Players.P4);
		board = new BotBoard(boardArray, ig);
		List<Move> possible = board.getAllPossibleMoves(Players.P4);
		msg.debug(this, "-------------------------GetAllPossibleMoves7-------------------------");
		msg.debugLegalMoves(this, possible);
		msg.debug(this, "-----------------------End GetAllPossibleMoves7-----------------------");
		assertEquals(2, possible.size());
	}
	
	@Test
	public void testGetAllPossibleMoves8() {
		boardArray[69] = 5;
		boardArray[70] = 5;
		boardArray[71] = 5;
		boardArray[39] = 2;
		ig = new InformationGatherer(Players.P2);
		ig.setCardsForPlayer(new int[] { 2, 7, 12, 7, 8, -1 }, Players.P2);
		board = new BotBoard(boardArray, ig);
		List<Move> possible = board.getAllPossibleMoves(Players.P2);
		assertEquals(5, possible.size());
		
		boardArray[48] = 5;
		board.setBoard(boardArray);
		possible = board.getAllPossibleMoves(Players.P2);
		assertEquals(4, possible.size());
	}

	@Test
	public void testGetPossibleMovesForCard() {
		boardArray[58] = 4;
		boardArray[0] = 5;
		ig = new InformationGatherer(Players.P4);
		ig.setCardsForPlayer(new int[] { Cards.SPADES_QUEEN, -1, -1, -1, -1, -1 }, Players.P4);
		board = new BotBoard(boardArray, ig);
		assertEquals(0, board.getPossibleMovesForCard(Cards.SPADES_QUEEN, Players.P4).size());
	}
	
	@Test
	public void testGetPossibleMovesForCard2() {
		
		boardArray[0] = 5;
		boardArray[8] = 1;
		ig = new InformationGatherer(Players.P1);
		ig.setCardsForPlayer(new int[] { Cards.CLUBS_SEVEN, -1, -1, -1, -1, -1 }, Players.P1);
		board = new BotBoard(boardArray, ig);
		List<Move> possible = board.getPossibleMovesForCard(Cards.CLUBS_SEVEN, Players.P1);
		msg.debug(this, "-----------------------ONE-----------------------");
		msg.debugLegalMoves(this, possible);
		int size = possible.size();
		assertEquals(14, size);  //14
		msg.debug(this, "---------------------END-ONE---------------------");
		
		
		/*intelliDOG.ai.framework.Board: Player on Turn: 2
		intelliDOG.ai.framework.Board: Cards: 2 7 2 -1 -1 -1 
		intelliDOG.ai.framework.Board: Pieces: 68*/ 
		
		
		/*
		boardArray[16] = 1;
		possible = board.getPossibleMovesForCard(Cards.CLUBS_SEVEN);
		msg.debug(this, "-----------------------TWO-----------------------");
		msg.debugLegalMoves(this, possible);
		size = possible.size();
		assertEquals(39, size); //39 old 21
		msg.debug(this, "---------------------END-TWO---------------------");
		
		boardArray[30] = 1;
		possible = board.getPossibleMovesForCard(Cards.CLUBS_SEVEN);
		msg.debug(this, "----------------------THREE----------------------");
		msg.debugLegalMoves(this, possible);
		size = possible.size();
		assertEquals(76, size); //76 old 28
		msg.debug(this, "--------------------END-THREE--------------------");
		*/
	}
	
	@Test
	public void testGetPossibleMovesForCard3(){
		
		boardArray[0] = 5;
		
		ig = new InformationGatherer(Players.P1);
		ig.setCardsForPlayer(new int[] { Cards.CLUBS_SEVEN, -1, -1, -1, -1, -1 }, Players.P1);
		board = new BotBoard(boardArray, ig);
		int[] pieces = new int[0];
		pieces = Rules.getInstance().getPiecesInGameForPlayer(this.board.getBoard(), Players.P1);
		byte [] heavenPieces = Rules.getInstance().getPiecesInHeavenOfPlayer(this.board.getBoard(), Players.P1);
		//ArrayList<Move> possible = board.getPossibleMovesForSevenSplit(Cards.CLUBS_SEVEN, 7, pieces, 0, new int[7], new int[7], new boolean[7], Players.P1, heavenPieces, new boolean[pieces.length]);
		List<Move> possible = board.getPossibleMovesForCard(Cards.CLUBS_SEVEN, Players.P1);
		int size = possible.size();
		assertEquals(1, size);
		
		//with two pieces
		boardArray[8] = 1;
		
		board.setBoard(boardArray);
		pieces = Rules.getInstance().getPiecesInGameForPlayer(this.board.getBoard(), Players.P1);
		heavenPieces = Rules.getInstance().getPiecesInHeavenOfPlayer(this.board.getBoard(), Players.P1);
		//possible = board.getPossibleMovesForSevenSplit(Cards.CLUBS_SEVEN, 7, pieces, 0, new int[7], new int[7], new boolean[7], Players.P1, heavenPieces, new boolean[pieces.length]);
		possible = board.getPossibleMovesForCard(Cards.CLUBS_SEVEN, Players.P1);
		msg.debug(this, "-----------------------RECURSIVE TWO-----------------------");
		msg.debugLegalMoves(this, possible);
		size = possible.size();
		assertEquals(14, size);
		msg.debug(this, "---------------------END RECURSIVE TWO---------------------");
		
		//with three pieces
		boardArray[14] = 1;
		
		board.setBoard(boardArray);
		pieces = Rules.getInstance().getPiecesInGameForPlayer(this.board.getBoard(), Players.P1);
		heavenPieces = Rules.getInstance().getPiecesInHeavenOfPlayer(this.board.getBoard(), Players.P1);
		//possible = board.getPossibleMovesForSevenSplit(Cards.CLUBS_SEVEN, 7, pieces, 0, new int[7], new int[7], new boolean[7], Players.P1, heavenPieces, new boolean[pieces.length]);
		possible = board.getPossibleMovesForCard(Cards.CLUBS_SEVEN, Players.P1);
		msg.debug(this, "-----------------------RECURSIVE THREE-----------------------");
		msg.debugLegalMoves(this, possible);
		size = possible.size();
		assertEquals(128, size); //<-- hope this is true!
		msg.debug(this, "---------------------END RECURSIVE THREE---------------------");
		
		//with four pieces
		boardArray[26] = 1;
		
		board.setBoard(boardArray);
		pieces = Rules.getInstance().getPiecesInGameForPlayer(this.board.getBoard(), Players.P1);
		heavenPieces = Rules.getInstance().getPiecesInHeavenOfPlayer(this.board.getBoard(), Players.P1);
		//possible = board.getPossibleMovesForSevenSplit(Cards.CLUBS_SEVEN, 7, pieces, 0, new int[7], new int[7], new boolean[7], Players.P1, heavenPieces, new boolean[pieces.length]);
		possible = board.getPossibleMovesForCard(Cards.CLUBS_SEVEN, Players.P1);
		msg.debug(this, "-----------------------RECURSIVE FOUR-----------------------");
		msg.debugLegalMoves(this, possible);
		size = possible.size();
		assertEquals(915, size); //<--hope this is true!
		msg.debug(this, "---------------------END RECURSIVE FOUR---------------------");
	}
	
	@Test
	public void testGetPossibleMovesForCard4(){
		
		boardArray[15] = 2;
		
		ig = new InformationGatherer(Players.P2);
		ig.setCardsForPlayer(new int[] { Cards.HEARTS_THREE, Cards.JOKER, Cards.HEARTS_NINE, Cards.CLUBS_ACE, -1, -1 }, Players.P2);
		board = new BotBoard(boardArray, ig);
		List<Move> possible; //= board.getPossibleMovesForCard(Cards.JOKER, Players.P2); --> does not work anymore for joker!
		/*msg.debug(this, "--------------------GetPossibleMovesForCard4-Joker--------------------");
		msg.debugLegalMoves(this, possible);
		msg.debug(this, "------------------End GetPossibleMovesForCard4-Joker------------------");
		int size = possible.size();
		assertEquals(20, size);*/
		int size;
		
		possible = board.getPossibleMovesForCard(Cards.CLUBS_ACE, Players.P2);
		msg.debug(this, "---------------------GetPossibleMovesForCard4-Ace---------------------");
		msg.debugLegalMoves(this, possible);
		msg.debug(this, "-------------------End GetPossibleMovesForCard4-Ace-------------------");
		size = possible.size();
		assertEquals(3, size);
		
		boardArray[16] = 5;
		board.setBoard(boardArray);
		//TODO: add tests for getpossiblemovesforjokeroptimized method!!!
		//same as above get possible move for card with joker does not work anymore! 
		/*possible = board.getPossibleMovesForCard(Cards.JOKER, Players.P2);
		msg.debug(this, "-------------------GetPossibleMovesForCard4-Joker 2-------------------");
		msg.debugLegalMoves(this, possible);
		msg.debug(this, "-----------------End GetPossibleMovesForCard4-Joker 2-----------------");
		size = possible.size();
		assertEquals(25, size);*/
		
		possible = board.getPossibleMovesForCard(Cards.CLUBS_ACE, Players.P2);
		msg.debug(this, "--------------------GetPossibleMovesForCard4-Ace 2--------------------");
		msg.debugLegalMoves(this, possible);
		msg.debug(this, "------------------End GetPossibleMovesForCard4-Ace 2------------------");
		size = possible.size();
		assertEquals(2, size);
	}
	
	//TODO: replace this test with a test for getpossiblemovesforjokeroptimized!
	/*@Test
	public void testGetPossibleMovesForCard5(){
		
		boardArray[15] = 2;
		
		ig = new InformationGatherer(Players.P2);
		ig.setCardsForPlayer(new int[] { Cards.HEARTS_THREE, Cards.JOKER, Cards.HEARTS_NINE, Cards.CLUBS_ACE, -1, -1 }, Players.P2);
		board = new BotBoard(boardArray, ig);
		List<Move> possible = board.getPossibleMovesForCard(Cards.JOKER, Players.P2);
		msg.debug(this, "--------------------GetPossibleMovesForCard5-Joker--------------------");
		msg.debugLegalMoves(this, possible);
		msg.debug(this, "------------------End GetPossibleMovesForCard5-Joker------------------");
		int size = possible.size();
		assertEquals(20, size);
	}*/

	
	@Test public void testMakeMove1() { 
		boardArray[79] = 5;
		boardArray[71] = 5;
		boardArray[15] = 2;
		boardArray[31] = 1;
		boardArray[32] = 5;
		boardArray[74] = 5;
		boardArray[75] = 5;
		
		byte[] expectedArray = new byte[80];
		for(int i = 0; i < expectedArray.length; i++){
			expectedArray[i] = boardArray[i];
		}
		expectedArray[48] = 5;
		
		ig = new InformationGatherer(Players.P4);
		ig.setCardsForPlayer(new int[] { Cards.SPADES_TWO, Cards.SPADES_QUEEN, Cards.CLUBS_NINE, 
				Cards.SPADES_KING,  Cards.SPADES_EIGHT, -1 }, Players.P4);
		board = new BotBoard(boardArray, ig);
		
		List<Move> possible = this.board.getAllPossibleMoves(Players.P4);
		
		assertEquals(1, possible.size());
		assertEquals(Cards.SPADES_KING, possible.get(0).getCard());
		assertEquals(-1, possible.get(0).getPositions()[0]);
		assertEquals(48, possible.get(0).getPositions()[1]);
		
		for (int i = 0; i < possible.size(); i++)
		{
			this.board.makeMove(possible.get(i), Players.P4);
		}
		assertArrayEquals(expectedArray, this.board.getBoard());
	}
	
	@Test public void testMakeMove2() { 
		boardArray[67] = 5;
		boardArray[71] = 5;
		boardArray[75] = 5;
		boardArray[41] = 4;
		boardArray[43] = 1;
		boardArray[46] = 4;
		boardArray[77] = 5;
		boardArray[61] = 3;
		
		
		byte[] expectedArray = new byte[80];
		for(int i = 0; i < expectedArray.length; i++){
			expectedArray[i] = boardArray[i];
		}
		expectedArray[16] = 5;
		
		ig = new InformationGatherer(Players.P2);
		ig.setCardsForPlayer(new int[] { Cards.HEARTS_SEVEN, Cards.DIAMONDS_SIX, Cards.HEARTS_ACE, 
				Cards.CLUBS_TEN, -1, -1 }, Players.P2);
		board = new BotBoard(boardArray, ig);
		
		List<Move> possible = this.board.getAllPossibleMoves(Players.P2);
		
		assertEquals(1, possible.size());
		assertEquals(Cards.HEARTS_ACE, possible.get(0).getCard());
		assertEquals(-1, possible.get(0).getPositions()[0]);
		assertEquals(16, possible.get(0).getPositions()[1]);
		
		for (int i = 0; i < possible.size(); i++)
		{
			this.board.makeMove(possible.get(i), Players.P2);
		}
		
		assertArrayEquals(expectedArray, this.board.getBoard());
		
	}
	
	@Test public void testMakeMove3() { 
		byte[] boardArray = new byte[80];
		boardArray[0] = 5;
		boardArray[62] = 1;
		boardArray[47] = 3;
		boardArray[42] = 4;
		boardArray[66] = 5; //heaven p1
		boardArray[67] = 5; //heaven p1
		boardArray[70] = 5; //heaven p2
		boardArray[71] = 5; //heaven p2
		boardArray[73] = 5; //heaven p3
		boardArray[75] = 5; //heaven p3
		//boardArray[79] = 5; //heaven p4
		
		
		/*byte[] expectedArray = new byte[80];
		for(int i = 0; i < expectedArray.length; i++){
			expectedArray[i] = boardArray[i];
		}
		expectedArray[16] = 5;*/
		
		ig = new InformationGatherer(Players.P4);
		ig.setCardsForPlayer(new int[] { Cards.SPADES_SEVEN, -1, -1, -1, -1, -1 }, Players.P4);
		board = new BotBoard(boardArray, ig);
		
		List<Move> possible = this.board.getAllPossibleMoves(Players.P4);
		
		assertEquals(2, possible.size());
		//assertEquals(Cards.HEARTS_ACE, possible.get(0).getCard());
		//assertEquals(-1, possible.get(0).getPositions()[0]);
		//assertEquals(16, possible.get(0).getPositions()[1]);
		
		for (int i = 0; i < possible.size(); i++)
		{
			this.board.makeMove(possible.get(i), Players.P4);
		}
		
		//assertArrayEquals(expectedArray, this.board.getBoard());
		
	}
	

	@Test
	public void testUndoMove1() {

		byte[] boardArray = new byte[80];
		boardArray[5] = Players.P1;
		boardArray[21] = 4;
		boardArray[37] = 3;
		boardArray[60] = 2;
		
		byte[] expectedArray = new byte[80];
		for(int i = 0; i < expectedArray.length; i++){
			expectedArray[i] = boardArray[i];
		}
		
		int[] mycards = new int[] { 6, 9, 11, -1, -1, -1 };
		ig = new InformationGatherer(Players.P1);
		ig.setCardsForPlayer(mycards, Players.P1);
		board = new BotBoard(boardArray, ig);
		
		//System.out.println(this.board);

		List<Move> possible = this.board.getAllPossibleMoves(Players.P1);

		assertEquals(5, possible.size());
		
		for (int i = 0; i < possible.size(); i++)
		{
			this.board.makeMove(possible.get(i), Players.P1);
			this.board.undoMove(Players.P1);
			assertArrayEquals(expectedArray, this.board.getBoard());
		}
		
		assertArrayEquals(expectedArray, this.board.getBoard());
		
	}
	
	@Test
	public void testUndoMove2() {

		byte[] boardArray = new byte[80];
		boardArray[71] = Players.ANY_SAVE;
		
		byte[] expectedArray = new byte[80];
		for(int i = 0; i < expectedArray.length; i++){
			expectedArray[i] = boardArray[i];
		}
		
		//Cards: 7 6 1 10
		int[] mycards = new int[] { 7, 6, 1, 10, -1, -1 };
		ig = new InformationGatherer(Players.P2);
		ig.setCardsForPlayer(mycards, Players.P2);
		board = new BotBoard(boardArray, ig);
		
		List<Move> possible = this.board.getAllPossibleMoves(Players.P2);

		assertEquals(1, possible.size());
		
		for (int i = 0; i < possible.size(); i++)
		{
			this.board.makeMove(possible.get(i), Players.P2);
			this.board.undoMove(Players.P2);
			assertArrayEquals(expectedArray, this.board.getBoard());
		}
		
		assertArrayEquals(expectedArray, this.board.getBoard());
		
	}
	
	@Test
	public void testUndoMove3() {
		
		testMakeMove1();
		
		byte[] expectedArray = new byte[80];
		expectedArray[79] = 5;
		expectedArray[71] = 5;
		expectedArray[15] = 2;
		expectedArray[31] = 1;
		expectedArray[32] = 5;
		expectedArray[74] = 5;
		expectedArray[75] = 5;
		
		this.board.undoMove(Players.P4);
		
		assertArrayEquals(expectedArray, this.board.getBoard());
		
	}
	
	@Test
	public void testUndoMove4() {
		
		testMakeMove2();
		
		byte[] expectedArray = new byte[80];
		expectedArray[67] = 5;
		expectedArray[71] = 5;
		expectedArray[75] = 5;
		expectedArray[41] = 4;
		expectedArray[43] = 1;
		expectedArray[46] = 4;
		expectedArray[77] = 5;
		expectedArray[61] = 3;
		
		this.board.undoMove(Players.P2);
		
		assertArrayEquals(expectedArray, this.board.getBoard());
	}
	
	@Test
	 public void testMakeAndUndoMove(){
		byte[] boardArray = new byte[80];
		 boardArray[37] = 1;
		 int[] mycards = new int[]{13,4,11,-1,-1,-1};
		 int[] expectedCards = new int[mycards.length];
		 for(int i = 0; i < expectedCards.length; i++){
			 expectedCards[i] = mycards[i];
		 }
		 
		ig = new InformationGatherer(Players.P1);
		ig.setCardsForPlayer(mycards, Players.P1);
		board = new BotBoard(boardArray, ig);
		List<Move> possible = board.getAllPossibleMoves(Players.P1);
		List<Move> possibleStageTwo = null;
		 
		 for(int i = 0; i < possible.size(); i++){
			 assertTrue(possible.get(i).getCard() == 13 || possible.get(i).getCard() == 4 || possible.get(i).getCard() == 11);
			 board.makeMove(possible.get(i), Players.P1);
			 
			//get cards from board
			 int[] cardsAfterFirstMove = new int[6];
			 for(int x = 0; x < 6; x++){
				 cardsAfterFirstMove[x] = ig.getCardsForPlayer(Players.P1)[x];
			 }
			 //board.setPlayerOnTurn(board.getMyPlayer());
			 possibleStageTwo = board.getAllPossibleMoves(Players.P1); 
			 for(int j = 0; j <possibleStageTwo.size(); j++){
				 assertTrue(possible.get(i).getCard() == 13 || possible.get(i).getCard() == 4 || possible.get(i).getCard() == 11);
				 
				 board.makeMove(possibleStageTwo.get(j), Players.P1);
				//get cards from board
				 int[] cardsAfterSecondMove = new int[6];
				 for(int x = 0; x < 6; x++){
					 cardsAfterSecondMove[x] = ig.getCardsForPlayer(Players.P1)[x];
				 }
				 
				 board.undoMove(Players.P1);
				 //assertArrayEquals(cardsAfterSecondMove, board.getMyCards());
			 }
			 board.undoMove(Players.P1);
			 
			 //assertArrayEquals(cardsAfterFirstMove, board.getMyCards());
		 }
		 java.util.Arrays.sort(expectedCards);
		 //boardCards
		 int[] boardCards = ig.getCardsForPlayer(Players.P1);
		 java.util.Arrays.sort(boardCards);
		 assertArrayEquals(expectedCards, boardCards);
	 }
	
	@Test
    public void testMakeAndUndoMove2() throws Exception
    {
        byte player = 1;
        boardArray[64] = 5;
        boardArray[65] = 5;
        boardArray[66] = 5;
        boardArray[67] = 5;
        boardArray[40] = 3;
        boardArray[58] = 2;
        boardArray[30] = 2;
        int[] mycards = new int[]{3,12,11,-1,-1,-1};
        ig = new InformationGatherer(player);
		ig.setCardsForPlayer(mycards, player);
		board = new BotBoard(boardArray, ig);
        List<Move> possible = board.getAllPossibleMoves(player);
       
        byte[] expectedArray = new byte[boardArray.length];
        for(int i = 0; i < boardArray.length; i++){
        	expectedArray[i] = boardArray[i];
        }
        
        Move highestMove = null;
        int highest = 0;
        for(int i = 0; i < possible.size(); i++)
        {
            board.makeMove(possible.get(i), player);
            //int result = eval.evaluate(b.getBoard(), b.getMyPlayer(), mycards);
            /*if(highest < result)
            {
                highest = result;
                highestMove = possible.get(i);
            }*/
            board.undoMove(player);
        }
        assertArrayEquals(expectedArray, boardArray);
    }
	
	@Test
	public void testMakeAndUndoMove3() throws Exception
	{
		byte player = 4;
		//player four
		boardArray[77] = 5;
		boardArray[78] = 5;
		boardArray[79] = 5;
		boardArray[76] = 5;
		//player two
		boardArray[68] = 5;
		boardArray[71] = 5;
		boardArray[16] = 5;
		boardArray[5] = 2;
		//player one
		boardArray[67] = 5;
		boardArray[66] = 5;
		//player three
		boardArray[74] = 5;
		boardArray[75] = 5;
		boardArray[13] = 3; 
		int[] mycards = new int[]{7,100,9,6,10,-1};
		ig = new InformationGatherer(player);
		ig.setCardsForPlayer(mycards, player);
		board = new BotBoard(boardArray, ig);
		
		List<Move> possible = board.getAllPossibleMoves(player);
		
		msg.debug(this, "--------------------testMakeAndUndoMove3--------------------");
		msg.debugLegalMoves(this, (ArrayList)possible);
		msg.debug(this, "------------------End testMakeAndUndoMove3------------------");
		
		//assertEquals(184, possible.size());
		assertEquals(102, possible.size()); //<- no idea!
		//TODO: overwork this test!
		
		//TODO: use this as time measurement method:
		//TODO: add time measure point here
		for(Move m : possible){
			this.board.makeMove(m, player);
			List<Move> poss2 = this.board.getAllPossibleMoves(player);
			for(Move m2 : poss2){
				this.board.makeMove(m2, player);
				List<Move> poss3 = this.board.getAllPossibleMoves(player);
				for(Move m3 : poss3){
					this.board.makeMove(m3, player);
					this.board.undoMove(player);
				}
				this.board.undoMove(player);
			}
			this.board.undoMove(player);
		}
		//TODO: add time end point here
	}
	
	@Test
	public void testMakeAndUndoMoveSeven()
	{
		boardArray[32] = 5; 
		boardArray[73] = 5;
		boardArray[74] = 5; 
		ig = new InformationGatherer(Players.P3);
		ig.setCardsForPlayer(new int[] { 33, -1, -1, -1, -1, -1 }, Players.P3);
		board = new BotBoard(boardArray, ig);
		
		byte[] expectedArray = new byte[80];
		for(int i = 0; i < expectedArray.length; i++){
			expectedArray[i] = boardArray[i];
		}
		List<Move> possible = board.getAllPossibleMoves(Players.P3); 
		
		msg.debugLegalMoves(this, possible);
		
		assertEquals(6, possible.size());
		
		//Test if move 1 is made correct
		expectedArray[32] = 0;
		expectedArray[39] = 3;
		Move m = possible.get(3); //had to change this from 0 to 3 because of the order change of getmovablepieces.
		board.makeMove(m, Players.P3);
		assertArrayEquals(expectedArray, boardArray);
		assertTrue(m.getwasProtected()[0]);
		
		//Test if undoing move 1 works correctly
		expectedArray[32] = 5;
		expectedArray[39] = 0;
		board.undoMove(Players.P3);
		assertArrayEquals(expectedArray, boardArray);
		
		//Test if move 2 is made correct
		expectedArray[32] = 0;
		expectedArray[38] = 3;
		expectedArray[74] = 0;
		expectedArray[75] = 5;
		m = possible.get(1);
		board.makeMove(m, Players.P3);
		assertArrayEquals(expectedArray, boardArray);
		assertTrue(m.getwasProtected()[0]);
		assertTrue(m.getwasProtected()[1]);
		
		//Test if undoing move 2 works correctly
		expectedArray[32] = 5;
		expectedArray[38] = 0;
		expectedArray[74] = 5;
		expectedArray[75] = 0;
		board.undoMove(Players.P3);
		assertArrayEquals(expectedArray, boardArray);
		
		//Test if move 3 is made correct
		expectedArray[32] = 0;
		expectedArray[37] = 3;
		expectedArray[75] = 5;
		expectedArray[73] = 0;
		m = possible.get(2);
		board.makeMove(m, Players.P3);
		assertArrayEquals(expectedArray, boardArray);
		assertTrue(m.getwasProtected()[0]);
		assertTrue(m.getwasProtected()[1]);
		assertTrue(m.getwasProtected()[2]);
		
		//Test if undoing move 3 works correctly
		expectedArray[32] = 5;
		expectedArray[37] = 0;
		expectedArray[75] = 0;
		expectedArray[73] = 5;
		board.undoMove(Players.P3);
		assertArrayEquals(expectedArray, boardArray);
		

		  /*for(int i = 0; i < possible.size(); i++)
	        {
			  msg.debug(this, "before " +  board.toString()); 
	            board.makeMove(possible.get(i), Players.P3);
	            board.undoMove(Players.P3);
	            msg.debug(this, "after  " +  board.toString()); 
	        }
	        assertArrayEquals(expectedArray, boardArray);*/
	}
	
	@Test
	public void testMakeAndUndoMoveSeven2()
	{
	                boardArray[0] = 5; 
	                boardArray[6] = 1; 

	                InformationGatherer ig = new InformationGatherer(Players.P1);
	                ig.setCardsForPlayer(new int[] { 7, -1, -1, -1, -1, -1 }, Players.P1);
	                BotBoard bb = new BotBoard(boardArray, ig);          
	                msg.debug(this, "initial state"); 
	                msg.debug(this, bb.toString()); 

	                byte[] expectedArray = new byte[80];
	                for(int i = 0; i < expectedArray.length; i++)
	                        expectedArray[i] = boardArray[i];

	                List<Move> possible = bb.getAllPossibleMoves(Players.P1);
	                msg.debugLegalMoves(this, possible); 

	                for (int i = 0; i < possible.size(); i++)
	                {
	                        bb.makeMove(possible.get(i), Players.P1);
	                        bb.undoMove(Players.P1);
	                        msg.debug(this, "Nr: " + i); 
	                        msg.debug(this, bb.toString()); 

	                        assertArrayEquals(expectedArray, bb.getBoard());
	                }
	                assertArrayEquals(expectedArray, bb.getBoard());
	}


	
	@Test
	public void testMoveSeven()
	{
		boardArray[32] = 5; 
		boardArray[73] = 5;
		boardArray[74] = 5; 
		ig = new InformationGatherer(Players.P3);
		ig.setCardsForPlayer(new int[] { 33, -1, -1, -1, -1, -1 }, Players.P3);
		board = new BotBoard(boardArray, ig);
		
		byte[] expectedArray = new byte[80];
		for(int i = 0; i < expectedArray.length; i++){
			expectedArray[i] = boardArray[i];
		}
		List<Move> possible = board.getAllPossibleMoves(Players.P3); 
		
		msg.debugLegalMoves(this, possible);

		  for(int i = 0; i < possible.size(); i++)
	        {
			  msg.debug(this, "before " +  board.toString()); 
	            board.makeMove(possible.get(i), Players.P3);
	            board.undoMove(Players.P3);
	            msg.debug(this, "after  " +  board.toString()); 
	        }
	        assertArrayEquals(expectedArray, boardArray);
	}
	
	
	@Test
	public void testUndoMoveSevenSplit() {

		
		byte[] boardArray = new byte[80];
		
		boardArray[14] = 2;
		boardArray[16] = Players.ANY_SAVE;
		boardArray[33] = 1;
		boardArray[70] = Players.ANY_SAVE;
		boardArray[16] = Players.ANY_SAVE;
		boardArray[71] = Players.ANY_SAVE;
		boardArray[75] = Players.ANY_SAVE;
		boardArray[79] = Players.ANY_SAVE;
		
		
		byte[] expectedArray = new byte[80];
		for(int i = 0; i < expectedArray.length; i++){
			expectedArray[i] = boardArray[i];
		}
		
		ig = new InformationGatherer(Players.P2);
		//ig.setCardsForPlayer(new int[] { 34, 100, 51, -1, -1, -1 }, Players.P2);
		ig.setCardsForPlayer(new int[] { 7, -1, -1, -1, -1, -1 }, Players.P2);
		board = new BotBoard(boardArray, ig);
		
		msg.debug(this, board.toString()); 
		
		List<Move> possible = board.getAllPossibleMoves(Players.P2);
		msg.debugLegalMoves(this, possible); 
	
		//Test if move 7 is made correct
		expectedArray[16] = 0;
		expectedArray[14] = 0;
		expectedArray[19] = 2;
		Move m = possible.get(7);
		board.makeMove(m, Players.P2);
		assertArrayEquals(expectedArray, boardArray);
		assertTrue(m.getwasProtected()[0]);
		assertTrue(m.getHits().length == 0); // FIXME Tschui: regression introduced by fixing 7-bug where own piece was hit.
		
		//Test if undoing move 7 works correctly
		expectedArray[16] = 5;
		expectedArray[14] = 2;
		expectedArray[19] = 0;
		board.undoMove(Players.P2);
		assertArrayEquals(expectedArray, boardArray);
		
		
		for (int i = 0; i < possible.size(); i++)
		{
			board.makeMove(possible.get(i), Players.P2);
			board.undoMove(Players.P2);
			msg.debug(this, "Nr: " + i); 
			msg.debug(this, this.board.toString()); 
			
			assertArrayEquals(expectedArray, this.board.getBoard());
		}
		assertArrayEquals(expectedArray, this.board.getBoard());
	}
	
	/*@Test
	public void testOptimizedGetPossibleMoves() {
		try{
			Method method = BotBoard.class.getDeclaredMethod("getPossibleMovesForJokerOptimized", new Class[]{int[].class, byte.class, boolean[].class});
			method.setAccessible(true);
			ig = new InformationGatherer(Players.P1);
			ig.setCardsForPlayer(new int[]{}, Players.P1);
			board = new BotBoard(boardArray, ig);
			boolean[] calculated = new boolean[13];
			for(int i = 0; i < calculated.length; i++){
				calculated[i] = true;
			}
			assertEquals(0, ((List<Move>)method.invoke(board, new Object[]{new int[]{}, Players.P1, calculated })).size());
			//assertTrue((Boolean)method.invoke(Rules.getInstance(), new Object[]{63, 3, (byte)1 }));
		}
        catch (NoSuchMethodException e) {
            // Should happen only rarely, because most times the
            // specified method should exist. If it does happen, just let
            // the test fail so the programmer can fix the problem.
            fail(e.getMessage());
        }
        catch (SecurityException e) {
            // Should happen only rarely, because the setAccessible(true)
            // should be allowed in when running unit tests. If it does
            // happen, just let the test fail so the programmer can fix
            // the problem.
        	fail(e.getMessage());
        }
        catch (IllegalAccessException e) {
            // Should never happen, because setting accessible flag to
            // true. If setting accessible fails, should throw a security
            // exception at that point and never get to the invoke. But
            // just in case, wrap it in a TestFailedException and let a
            // human figure it out.
        	fail(e.getMessage());
        }
        catch (IllegalArgumentException e) {
            // Should happen only rarely, because usually the right
            // number and types of arguments will be passed. If it does
            // happen, just let the test fail so the programmer can fix
            // the problem.
        	fail(e.getMessage());
        } catch (InvocationTargetException e) {
			fail(e.getMessage());
		}
	}*/
	@Test
	public void testTerminalState()
	{
		byte[] boardArray = new byte[80];
		
		// heaven for Player 1
		boardArray[64] = Players.ANY_SAVE;
		boardArray[65] = Players.ANY_SAVE;
		boardArray[66] = Players.ANY_SAVE;
		boardArray[67] = Players.ANY_SAVE;
		
		// heaven for Player 3
		boardArray[72] = Players.ANY_SAVE;
		boardArray[73] = Players.ANY_SAVE;
		boardArray[74] = Players.ANY_SAVE;
		boardArray[75] = Players.ANY_SAVE;
		
		ig = new InformationGatherer(Players.P1);
		ig.setCardsForPlayer(new int[] { 7, -1, -1, -1, -1, -1 }, Players.P1);
		board = new BotBoard(boardArray, ig);
		
		assertTrue(board.terminalState(Players.P1)); 
		assertTrue(board.terminalState(Players.P3)); 
		assertEquals(false, board.terminalState(Players.P2));
		assertEquals(false, board.terminalState(Players.P4));  
	}
	
	@Test
	public void testTerminalState2()
	{
		byte[] boardArray = new byte[80];
		
		// heaven for Player 1
		boardArray[64] = Players.ANY_SAVE;
		boardArray[65] = Players.ANY_SAVE;
		boardArray[66] = Players.ANY_SAVE;
		
		// heaven for Player 2
		boardArray[68] = Players.ANY_SAVE;
		boardArray[69] = Players.ANY_SAVE;
		boardArray[70] = Players.ANY_SAVE;
		boardArray[71] = Players.ANY_SAVE;
		
		// heaven for Player 3
		boardArray[72] = Players.ANY_SAVE;
		boardArray[73] = Players.ANY_SAVE;
		boardArray[74] = Players.ANY_SAVE;
		boardArray[75] = Players.ANY_SAVE;
		
		// heaven for Player 4
		boardArray[76] = Players.ANY_SAVE;
		boardArray[77] = Players.ANY_SAVE;
		boardArray[78] = Players.ANY_SAVE;
		boardArray[79] = Players.ANY_SAVE;
		
		ig = new InformationGatherer(Players.P2);
		ig.setCardsForPlayer(new int[] { 7, -1, -1, -1, -1, -1 }, Players.P2);
		board = new BotBoard(boardArray, ig);
		
		assertTrue(board.terminalState(Players.P2)); 
		assertTrue(board.terminalState(Players.P4)); 
		assertEquals(false, board.terminalState(Players.P1));
		assertEquals(false, board.terminalState(Players.P3));  
	}
	
	/**
	 * Test for splitting the card 7.
	 * ok
	 */
	@Test
	public void testGetAllPossibleMoves4_Seven() {
		
		boardArray[0] = 5; // homefield
		boardArray[60]= 1; // 4 field back 
		
		ig = new InformationGatherer(Players.P1);
		ig.setCardsForPlayer(new int[] { 7, -1, -1, -1, -1, -1 }, Players.P1);
		board = new BotBoard(boardArray, ig);
		List<Move> possible = board.getAllPossibleMoves(Players.P1);
		msg.debug(this, "-------------------------testGetAllPossibleMoves4_Seven-------------------------");
		msg.debugLegalMoves(this, possible);
		msg.debug(this, "-----------------------End testGetAllPossibleMoves4_Seven-----------------------");
		
		assertEquals(12, possible.size());
	}
	
	/**
	 * Test for splitting the card 7.
	 * ok
	 */
	@Test
	public void testGetAllPossibleMoves4_Seven2() {
		
		boardArray[0] = 1; // homefield not occupied
		boardArray[60]= 1; // 4 field back 
		
		ig = new InformationGatherer(Players.P1);
		ig.setCardsForPlayer(new int[] { 7, -1, -1, -1, -1, -1 }, Players.P1);
		board = new BotBoard(boardArray, ig);
		List<Move> possible = board.getAllPossibleMoves(Players.P1);
		msg.debug(this, "-------------------------testGetAllPossibleMoves4_Seven 2-------------------------");
		msg.debugLegalMoves(this, possible);
		msg.debug(this, "-----------------------End testGetAllPossibleMoves4_Seven 2-----------------------");
		
		assertEquals(16, possible.size());
	}
	
	/**
	 * Test for splitting the card 7.
	 * player has to make another round because 7 cannot be used to go to heaven
	 */
	@Test
	public void testGetAllPossibleMoves4_Seven3() {
		
		boardArray[63] = 1;
		
		
		ig = new InformationGatherer(Players.P1);
		ig.setCardsForPlayer(new int[] { 7, -1, -1, -1, -1, -1 }, Players.P1);
		board = new BotBoard(boardArray, ig);
		List<Move> possible = board.getAllPossibleMoves(Players.P1);
		msg.debug(this, "-------------------------testGetAllPossibleMoves4_Seven 3-------------------------");
		msg.debugLegalMoves(this, possible);
		msg.debug(this, "-----------------------End testGetAllPossibleMoves4_Seven 3-----------------------");
		
		assertEquals(1, possible.size());
	}
	
	/**
	 * Test for splitting the card 7.
	 * player can't use 7 because another player is blocking 
	 */
	@Test
	public void testGetAllPossibleMoves4_Seven4() {
		
		boardArray[61] = 2; // wants to move with seven
		boardArray[0] = 5; // blocks
		
		ig = new InformationGatherer(Players.P2);
		ig.setCardsForPlayer(new int[] { 7, -1, -1, -1, -1, -1 }, Players.P2);
		board = new BotBoard(boardArray, ig);
		List<Move> possible = board.getAllPossibleMoves(Players.P2);
		msg.debug(this, "-------------------------testGetAllPossibleMoves4_Seven 4-------------------------");
		msg.debugLegalMoves(this, possible);
		msg.debug(this, "-----------------------End testGetAllPossibleMoves4_Seven 4-----------------------");
		
		assertEquals(0, possible.size());
	}
	
	/**
	 * Test for splitting the card 7.
	 * player can't use 7 because another player is blocking 
	 */
	@Test
	public void testGetAllPossibleMoves4_Seven5() {
		
		boardArray[57] = 2; // wants to move with seven
		boardArray[70] = 5; // player 2 has a token in heaven
		boardArray[0] = 5; // player 1 blocks
		
		ig = new InformationGatherer(Players.P2);
		ig.setCardsForPlayer(new int[] { 7, -1, -1, -1, -1, -1 }, Players.P2);
		board = new BotBoard(boardArray, ig);
		List<Move> possible = board.getAllPossibleMoves(Players.P2);
		msg.debug(this, "-------------------------testGetAllPossibleMoves4_Seven 5-------------------------");
		msg.debugLegalMoves(this, possible);
		msg.debug(this, "-----------------------End testGetAllPossibleMoves4_Seven 5-----------------------");
		
		assertEquals(2, possible.size());
	}
	
	/**
	 * Test for splitting the card 7.
	 *  
	 */
	@Test
	public void testGetAllPossibleMoves4_Seven6() {
		
		boardArray[5] = 1; 
		boardArray[66] = 5; // player 1 has a token in heaven
		boardArray[52] = 1; 
		
		ig = new InformationGatherer(Players.P1);
		ig.setCardsForPlayer(new int[] { 7, -1, -1, -1, -1, -1 }, Players.P1);
		board = new BotBoard(boardArray, ig);
		List<Move> possible = board.getAllPossibleMoves(Players.P1);
		msg.debug(this, "-------------------------testGetAllPossibleMoves4_Seven 6-------------------------");
		msg.debugLegalMoves(this, possible);
		msg.debug(this, "-----------------------End testGetAllPossibleMoves4_Seven 6-----------------------");
		
		assertEquals(48, possible.size());
	}
	
	/**
	 * Test for splitting the card 7.
	 *  
	 */
	@Test
	public void testGetAllPossibleMoves4_Seven7() {
		
		boardArray[5] = 1; 
		boardArray[66] = 5; // player 1 has a token in heaven
		boardArray[52] = 1; 
		
		ig = new InformationGatherer(Players.P1);
		ig.setCardsForPlayer(new int[] { 7, -1, -1, -1, -1, -1 }, Players.P1);
		board = new BotBoard(boardArray, ig);
		List<Move> possible = board.getAllPossibleMoves(Players.P1);
		msg.debug(this, "-------------------------testGetAllPossibleMoves4_Seven 7-------------------------");
		msg.debugLegalMoves(this, possible);
		msg.debug(this, "-----------------------End testGetAllPossibleMoves4_Seven 7-----------------------");
		
		assertEquals(48, possible.size());
	}
	
	
	/**
	 * Test for Jack: homefield is blocked and therefore not considered
	 *
	 */
	@Test
	public void testGetAllPossibleMoves_Jack1() {

		boardArray[0] = 5;  // homefield 
		boardArray[6] = 1; 
		boardArray[11] = 2; 

		ig = new InformationGatherer(Players.P1);
		ig.setCardsForPlayer(new int[] { 11, -1, -1, -1, -1, -1 }, Players.P1);
		board = new BotBoard(boardArray, ig);
		List<Move> possible = board.getAllPossibleMoves(Players.P1);
		msg.debug(this, "-------------------------testGetAllPossibleMoves_Jack 1-------------------------");
		msg.debugLegalMoves(this, possible);
		msg.debug(this, "-----------------------End testGetAllPossibleMoves_Jack 1 -----------------------");

		assertEquals(1, possible.size());
	}
	
	/**
	 * Test for Jack: homefield is not blocked and therefore the token on that 
	 * field is taken into consideration.
	 */
	@Test
	public void testGetAllPossibleMoves_Jack2() {

		boardArray[0] = 1;  // homefield (not blocked)
		boardArray[6] = 1; 
		boardArray[11] = 2; 

		ig = new InformationGatherer(Players.P1);
		ig.setCardsForPlayer(new int[] { 11, -1, -1, -1, -1, -1 }, Players.P1);
		board = new BotBoard(boardArray, ig);
		List<Move> possible = board.getAllPossibleMoves(Players.P1);
		msg.debug(this, "-------------------------testGetAllPossibleMoves_Jack 2-------------------------");
		msg.debugLegalMoves(this, possible);
		msg.debug(this, "-----------------------End testGetAllPossibleMoves_Jack 2 -----------------------");

		assertEquals(2, possible.size());
	}
	
	/**
	 * Test for Jack: don't consider pieces of other players that are in heaven
	 *  
	 */
	@Test
	public void testGetAllPossibleMoves_Jack3() {

		boardArray[0] = 1;  // homefield (not blocked)
		boardArray[20] = 1;
		
		boardArray[68] = 2; // heaven of P2
		boardArray[69] = 2; 
		boardArray[70] = 2; 
		
		boardArray[32] = 5; // homefield of P3 
		boardArray[48] = 4; // homefield of P4
		 
		
		ig = new InformationGatherer(Players.P1);
		ig.setCardsForPlayer(new int[] { 11, -1, -1, -1, -1, -1 }, Players.P1);
		board = new BotBoard(boardArray, ig);
		List<Move> possible = board.getAllPossibleMoves(Players.P1);
		msg.debug(this, "-------------------------testGetAllPossibleMoves_Jack 3-------------------------");
		msg.debugLegalMoves(this, possible);
		msg.debug(this, "-----------------------End testGetAllPossibleMoves_Jack 3 -----------------------");

		assertEquals(2, possible.size());
	}
	
	/**
	 * Test for Jack: don't consider pieces of other players that are in heaven
	 * or block the homefield
	 *  
	 */
	@Test
	public void testGetAllPossibleMoves_Jack4() {

		boardArray[0] = 4;  // P4 on my homefield
		boardArray[16] = 1; // P1 on P2's homefield
		boardArray[32] = 4; // P4 on P3's homefield 
		boardArray[48] = 4; // homefield of P4
		
		boardArray[71] = 2; // heaven of P2
		boardArray[63] = 1; // P1 
		boardArray[79] = 4; // heaven P4 
		
		
		ig = new InformationGatherer(Players.P1);
		ig.setCardsForPlayer(new int[] { 11, -1, -1, -1, -1, -1 }, Players.P1);
		board = new BotBoard(boardArray, ig);
		List<Move> possible = board.getAllPossibleMoves(Players.P1);
		msg.debug(this, "-------------------------testGetAllPossibleMoves_Jack 4-------------------------");
		msg.debugLegalMoves(this, possible);
		msg.debug(this, "-----------------------End testGetAllPossibleMoves_Jack 4 -----------------------");

		assertEquals(6, possible.size());
	}
	
	/**
	 * Test for Jack: all token on the board
	 *  
	 */
	@Test
	public void testGetAllPossibleMoves_Jack5() {

		boardArray[3] = 1;  
		boardArray[30] = 1; 
		boardArray[60] = 1; 
		boardArray[52] = 1; 
		
		boardArray[15] = 2; 
		boardArray[21] = 2;  
		boardArray[59] = 2; 
		boardArray[7] = 2;
		
		boardArray[49] = 3; 
		boardArray[1] = 3;  
		boardArray[44] = 3; 
		boardArray[38] = 3;
		
		boardArray[27] = 4; 
		boardArray[5] = 4;  
		boardArray[16] = 4; 
		boardArray[50] = 4;
		
		ig = new InformationGatherer(Players.P1);
		ig.setCardsForPlayer(new int[] { 11, -1, -1, -1, -1, -1 }, Players.P1);
		board = new BotBoard(boardArray, ig);
		List<Move> possible = board.getAllPossibleMoves(Players.P1);
		msg.debug(this, "-------------------------testGetAllPossibleMoves_Jack 5 -------------------------");
		msg.debugLegalMoves(this, possible);
		msg.debug(this, "-----------------------End testGetAllPossibleMoves_Jack 5 -----------------------");

		assertEquals(48, possible.size()); // 4 * 12 possibility
	}
	
	/**
	 * Test with Cards.HEARTS_JACK, Cards.CLUBS_JACK, Cards.JOKER_AS_JACK
	 *  
	 */
	@Test
	public void testGetAllPossibleMoves_Jack6() {

		boardArray[0] = 4;  // P4 on my homefield
		boardArray[16] = 1; // P1 on P2's homefield
		boardArray[32] = 4; // P4 on P3's homefield 
		boardArray[48] = 4; // homefield of P4
		
		boardArray[71] = 2; // heaven of P2
		boardArray[63] = 1; // P1 
		boardArray[79] = 4; // heaven P4 
		
		
		ig = new InformationGatherer(Players.P1);
		ig.setCardsForPlayer(new int[] { Cards.HEARTS_JACK, Cards.CLUBS_JACK, Cards.JOKER_AS_JACK, -1, -1, -1 }, Players.P1);
		board = new BotBoard(boardArray, ig);
		List<Move> possible = board.getAllPossibleMoves(Players.P1);
		msg.debug(this, "-------------------------testGetAllPossibleMoves_Jack 6 -------------------------");
		msg.debugLegalMoves(this, possible);
		msg.debug(this, "-----------------------End testGetAllPossibleMoves_Jack 6  -----------------------");

		assertEquals(18, possible.size()); // 3*6
	}
	
	
	@Test
	public void testMakeAndUndoMove_Jack()
	{
		byte player = 4;
		//player four
		boardArray[77] = 5;
		boardArray[78] = 5;
		boardArray[79] = 5;
		boardArray[76] = 5;
		//player two
		boardArray[68] = 5;
		boardArray[71] = 5;
		boardArray[16] = 5;
		boardArray[5] = 2;
		//player one
		boardArray[67] = 5;
		boardArray[66] = 5;
		//player three
		boardArray[74] = 5;
		boardArray[75] = 5;
		boardArray[13] = 3; 
		int[] mycards = new int[]{Cards.HEARTS_JACK,-1,-1,-1,-1,-1};
		ig = new InformationGatherer(player);
		ig.setCardsForPlayer(mycards, player);
		board = new BotBoard(boardArray, ig);
		
		byte[] expectedArray = new byte[80];
		for(int i = 0; i < expectedArray.length; i++){
			expectedArray[i] = boardArray[i];
		}
		
		List<Move> possible = board.getAllPossibleMoves(player);
		
		msg.debug(this, "--------------------testMakeAndUndoMove_Jack --------------------");
		msg.debugLegalMoves(this, (ArrayList)possible);
		msg.debug(this, "------------------End testMakeAndUndoMove_Jack ------------------");
		
		for(int i=0; i<possible.size(); i++)
		{
			board.makeMove(possible.get(i), player); 
			board.undoMove(player); 
			assertArrayEquals(expectedArray, board.getBoard()); 
		}
	}
	
	@Test
	public void testMakeAndUndoMove_Jack2()
	{
		byte player = 1;
		
		//player one
		boardArray[0] = 5;
		boardArray[16] = 1; // on P2's homefield
		
		//player two
		boardArray[68] = 5; // heaven
		boardArray[32] = 2; // on P3's homefield
		boardArray[1] = 2;
		boardArray[2] = 2; 
		
		//player three
		boardArray[63] = 3;
		boardArray[75] = 3;
		boardArray[13] = 3; 
		boardArray[33] = 3; 
		
		//player four
		boardArray[48] = 5;
		boardArray[49] = 4;
		boardArray[50] = 4;
		boardArray[51] = 4;
		
		int[] mycards = new int[]{Cards.HEARTS_JACK,-1,-1,-1,-1,-1};
		ig = new InformationGatherer(player);
		ig.setCardsForPlayer(mycards, player);
		board = new BotBoard(boardArray, ig);
		
		byte[] expectedArray = new byte[80];
		for(int i = 0; i < expectedArray.length; i++){
			expectedArray[i] = boardArray[i];
		}
		
		List<Move> possible = board.getAllPossibleMoves(player);
		
		msg.debug(this, "--------------------testMakeAndUndoMove_Jack2 --------------------");
		msg.debugLegalMoves(this, (ArrayList)possible);
		msg.debug(this, "------------------End testMakeAndUndoMove_Jack2 ------------------");
		
		for(int i=0; i<possible.size(); i++)
		{
			board.makeMove(possible.get(i), player); 
			board.undoMove(player); 
			assertArrayEquals(expectedArray, board.getBoard()); 
		}
	}
	
	/**
	 * the move 28->33 / 41->43 didn't mark field 43 as "hit"
	 */
	@Test
	public void testBug_SevenSplit_MakeMove()
	{	
		byte player = 3; 
		boardArray[28] = 3;
		boardArray[41] = 3; 
		boardArray[43] = 1; 
		boardArray[69] = 2; // heaven field
		int[] mycards = new int[]{Cards.JOKER_AS_SEVEN,-1,-1,-1,-1,-1};
		
		ig = new InformationGatherer(player);
		ig.setCardsForPlayer(mycards, player);
		board = new BotBoard(boardArray, ig);
		
		byte[] expectedArray = new byte[80];
		for(int i = 0; i < expectedArray.length; i++){
			expectedArray[i] = boardArray[i];
		}
		
		List<Move> possible = board.getAllPossibleMoves(player);
		
		msg.debug(this, "--------------------testBug_SevenSplit_MakeMove --------------------");
		msg.debugLegalMoves(this, (ArrayList)possible);
		msg.debug(this, "------------------End testBug_SevenSplit_MakeMove ------------------");
		
		for(int i=0; i<possible.size(); i++)
		{
			board.makeMove(possible.get(i), player); 
			board.undoMove(player); 
			assertArrayEquals(expectedArray, board.getBoard()); 
		}
	}
	
	
	
	@Test
	public void testNestedMoves()
	{	
		byte player = 3;
		// P1
		boardArray[0] = 5;
		boardArray[3] = 1;
		boardArray[66] = 5; 
		// P2
		boardArray[16] = 5;
		boardArray[61] = 2; 
		boardArray[71] = 5; 
		// P3
		boardArray[60] = 3; 
		boardArray[75] = 5; 
		// P4
		boardArray[28] = 4; 
		boardArray[78] = 5; 
		boardArray[79] = 5; 
	
		int[] cardsP1 = new int[]{Cards.SPADES_EIGHT,Cards.SPADES_QUEEN,Cards.CLUBS_SIX,Cards.HEARTS_NINE,-1,-1};
		int[] cardsP2 = new int[]{Cards.CLUBS_SEVEN, Cards.HEARTS_SIX, Cards.HEARTS_TEN, Cards.HEARTS_EIGHT, -1, -1}; 
		int[] cardsP3 = new int[]{Cards.JOKER, Cards.SPADES_ACE, Cards.CLUBS_THREE, Cards.HEARTS_NINE, Cards.DIAMONDS_EIGHT, -1};
		int[] cardsP4 = new int[]{Cards.SPADES_FIVE, Cards.DIAMONDS_TWO, Cards.HEARTS_ACE, Cards.HEARTS_KING, Cards.SPADES_NINE, -1};

		ig = new InformationGatherer(player);
		
		ig.setCardsForPlayer(cardsP1, (byte)1);
		ig.setCardsForPlayer(cardsP2, (byte)2);
		ig.setCardsForPlayer(cardsP3, player);
		ig.setCardsForPlayer(cardsP4, (byte)4);
		
		board = new BotBoard(boardArray, ig);
		
		byte[] expectedArray = new byte[80];
		for(int i = 0; i < expectedArray.length; i++){
			expectedArray[i] = boardArray[i];
		}
		
		List<Move> possible = board.getAllPossibleMoves(player);
		
		msg.debug(this, "-------------------- testNestedMoves --------------------");
		msg.debugLegalMoves(this, (ArrayList)possible);
		msg.debug(this, "------------------  End testNestedMoves ------------------");
	
		for(int i=0; i<possible.size(); i++)
		{
			board.makeMove(possible.get(i), player); 
			
			byte[] expectedArray2 = new byte[80];
			for(int i2 = 0; i2 < expectedArray2.length; i2++){
				expectedArray2[i2] = boardArray[i2];
			}
			
			List<Move> possibleP1 = board.getAllPossibleMoves((byte)1);
			
			msg.debug(this, "-------------------- P1 --------------------");
			msg.debugLegalMoves(this, (ArrayList)possibleP1);
			msg.debug(this, "------------------  End P1 ------------------");
			
			for(int j=0; j<possibleP1.size(); j++)
			{
				board.makeMove(possibleP1.get(j), (byte)1); 
				board.undoMove((byte)1); 
				assertArrayEquals(expectedArray2, board.getBoard()); 
			}
		
			board.undoMove(player); 
			assertArrayEquals(expectedArray, board.getBoard());
		} // outer loop
	}
	
	
	@Test
	public void testBug_HitCount()
	{	
		byte player = 3;
		
		boardArray[4] = 1;
		boardArray[61] = 3; 

		int[] cardsP3 = new int[]{Cards.SPADES_FIVE, Cards.DIAMONDS_SEVEN, -1, -1, -1, -1};

		ig = new InformationGatherer(player);
		ig.setCardsForPlayer(cardsP3, player);
		board = new BotBoard(boardArray, ig);
		
		byte[] expectedArray = new byte[80];
		for(int i = 0; i < expectedArray.length; i++){
			expectedArray[i] = boardArray[i];
		}
		
		List<Move> possible = board.getAllPossibleMoves(player);
		
		msg.debug(this, "-------------------- P3 --------------------");
		msg.debugLegalMoves(this, (ArrayList)possible);
		msg.debug(this, "------------------  End P3 ------------------");
	
		for(int i=0; i<possible.size(); i++)
		{
			board.makeMove(possible.get(i), player); 
			board.undoMove(player); 
			assertArrayEquals(expectedArray, board.getBoard());
		}
	}
	
	
	// FIXME: add nested makeMove/undoMove
	@Test
	public void testBug_HitCount2()
	{	
		byte player = 1;
		
		boardArray[9] = 1;
		boardArray[11] = 3;
		boardArray[13] = 3; 
		boardArray[26] = 2;
		boardArray[38] = 2;
		boardArray[67] = 5; 
		boardArray[70] = 5; 
		boardArray[71] = 5; 
		boardArray[78] = 5; 
		boardArray[79] = 5; 
		
		int[] cardsP1 = new int[]{Cards.SPADES_THREE, Cards.CLUBS_KING, Cards.CLUBS_JACK, -1, -1, -1};
		int[] cardsP2 = new int[]{Cards.SPADES_JACK, Cards.HEARTS_QUEEN, Cards.SPADES_JACK, -1, -1, -1};
		int[] cardsP3 = new int[]{Cards.JOKER, Cards.DIAMONDS_SEVEN, Cards.CLUBS_NINE, -1, -1, -1};
		int[] cardsP4 = new int[]{Cards.CLUBS_FIVE, Cards.HEARTS_FIVE, Cards.HEARTS_JACK, -1, -1, -1};

		ig = new InformationGatherer(player);
		ig.setCardsForPlayer(cardsP1, player);
		ig.setCardsForPlayer(cardsP2, (byte)2);
		ig.setCardsForPlayer(cardsP3, (byte)3); 
		ig.setCardsForPlayer(cardsP4, (byte)4); 
		board = new BotBoard(boardArray, ig);
		
		byte[] expectedArray = new byte[80];
		for(int i = 0; i < expectedArray.length; i++){
			expectedArray[i] = boardArray[i];
		}
		
		List<Move> possible = board.getAllPossibleMoves(player);
		
		msg.debug(this, "-------------------- P1 --------------------");
		msg.debugLegalMoves(this, (ArrayList)possible);
		msg.debug(this, "------------------  End P1 ------------------");
	
		for(int i=0; i<possible.size(); i++)
		{
			board.makeMove(possible.get(i), player); 
			board.undoMove(player); 
			assertArrayEquals(expectedArray, board.getBoard());
		}
	}
	
	/**
	 * Bug with Seven Split
	 * card -> 7, positions -> s: 16, t: 19 / s: 43, t: 47
	 */
	@Test
	public void testBug_HitCount3()
	{	
		byte player = 2;
		
		boardArray[0] = 5;
		boardArray[16] = 2;
		boardArray[43] = 2; 
		boardArray[44] = 4;
		boardArray[46] = 3;
		boardArray[47] = 2; 
		boardArray[48] = 5; 
		boardArray[67] = 5; 
		
		int[] cardsP2 = new int[]{Cards.HEARTS_SEVEN, -1, -1, -1, -1, -1};
		//msg.addItemForWhiteList(this); 
		ig = new InformationGatherer(player);
		
		ig.setCardsForPlayer(cardsP2, player);
		
		board = new BotBoard(boardArray, ig);
		
		byte[] expectedArray = new byte[80];
		for(int i = 0; i < expectedArray.length; i++){
			expectedArray[i] = boardArray[i];
		}
		
		List<Move> possible = board.getAllPossibleMoves(player);
		msg.debug(this, "-------------------- P2 --------------------");
		msg.debugLegalMoves(this, (ArrayList)possible);
		msg.debug(this, "------------------  End P2 ------------------");
	
		for(int i=0; i<possible.size(); i++)
		{
			msg.debugMove(this, possible.get(i)); 
			board.makeMove(possible.get(i), player); 
			board.undoMove(player); 
			msg.debug(this, board.toString());
			assertArrayEquals(expectedArray, board.getBoard());
		}
	}
	
	
	/**
	 * Bug with Seven Split into three moves: 
	 * card -> 46, positions -> s: 74, t: 75 / s: 35, t: 36 / s: 31, t: 36
	 */
	@Test
	public void testBug_HitCount4()
	{	
		byte player = 3;
		
		boardArray[0] = 5;
		boardArray[16] = 5;
		boardArray[29] = 4; 
		boardArray[31] = 3;
		boardArray[35] = 3;
		boardArray[55] = 2; 
		boardArray[61] = 2; 
		boardArray[66] = 5; 
		boardArray[71] = 5; 
		boardArray[74] = 5; 
		boardArray[77] = 5; 
		boardArray[78] = 5; 
		boardArray[79] = 5; 
		
		int[] cardsP3 = new int[]{Cards.CLUBS_SEVEN, -1, -1, -1, -1, -1};
		//msg.addItemForWhiteList(this); 
		ig = new InformationGatherer(player);
		
		ig.setCardsForPlayer(cardsP3, player);
		
		board = new BotBoard(boardArray, ig);
		
		byte[] expectedArray = new byte[80];
		for(int i = 0; i < expectedArray.length; i++){
			expectedArray[i] = boardArray[i];
		}
		
		List<Move> possible = board.getAllPossibleMoves(player);
		
		msg.debug(this, "-------------------- P3 --------------------");
		msg.debugLegalMoves(this, (ArrayList)possible);
		msg.debug(this, "------------------  End P3 ------------------");
	
		for(int i=0; i<possible.size(); i++)
		{
			msg.debugMove(this, possible.get(i)); 
			board.makeMove(possible.get(i), player); 
			board.undoMove(player); 
			msg.debug(this, board.toString());
			assertArrayEquals(expectedArray, board.getBoard());
		}
	}
	

	/**
	 * s: 48, t: 52 / s: 45, t: 48
	 */
	@Test
	public void testBug_HitCount5()
	{	
		byte player = 4;
		
		// P2 has all pieces in heaven
		boardArray[68] = 5;
		boardArray[69] = 5;
		boardArray[70] = 5; 
		boardArray[71] = 5;
		
		// home of P4
		boardArray[48] = 5;
		
		boardArray[45] = 4;
		boardArray[47] = 3;
		
		boardArray[78] = 5; 
		boardArray[79] = 5; 
		
		int[] cardsP4 = new int[]{Cards.CLUBS_SEVEN, -1, -1, -1, -1, -1};
		//msg.addItemForWhiteList(this); 
		ig = new InformationGatherer(player);
		
		ig.setCardsForPlayer(cardsP4, player);
		
		board = new BotBoard(boardArray, ig);
		
		byte[] expectedArray = new byte[80];
		for(int i = 0; i < expectedArray.length; i++){
			expectedArray[i] = boardArray[i];
		}
		
		List<Move> possible = board.getAllPossibleMoves(player);
		
		msg.debug(this, "-------------------- P4 --------------------");
		msg.debugLegalMoves(this, (ArrayList)possible);
		msg.debug(this, "------------------  End P4 ------------------");
	
		for(int i=0; i<possible.size(); i++)
		{
			msg.debugMove(this, possible.get(i)); 
			board.makeMove(possible.get(i), player); 
			board.undoMove(player); 
			msg.debug(this, board.toString());
			assertArrayEquals(expectedArray, board.getBoard());
		}
	}
	
	
	@Test
	public void testBug_HitCount6()
	{	
		// Player 2 will move with P4
		byte player = 2;
		
		// P2 has all pieces in heaven
		boardArray[68] = 5;
		boardArray[69] = 5;
		boardArray[70] = 5; 
		boardArray[71] = 5;
		
		// home of P4
		boardArray[45] = 4;
		boardArray[48] = 5;
	
		boardArray[78] = 5; // heaven of P4
		boardArray[79] = 5; 
		
		int[] cardsP2 = new int[]{Cards.CLUBS_SEVEN, -1, -1, -1, -1, -1};
		//msg.addItemForWhiteList(this); 
		ig = new InformationGatherer(player);
		
		ig.setCardsForPlayer(cardsP2, player);
		
		board = new BotBoard(boardArray, ig);
		
		byte[] expectedArray = new byte[80];
		for(int i = 0; i < expectedArray.length; i++){
			expectedArray[i] = boardArray[i];
		}
		
		List<Move> possible = board.getAllPossibleMoves(player);
		
		msg.debug(this, "-------------------- P2 --------------------");
		msg.debugLegalMoves(this, (ArrayList)possible);
		msg.debug(this, "------------------  End P2 ------------------");
	
		for(int i=0; i<possible.size(); i++)
		{
			msg.debugMove(this, possible.get(i)); 
			board.makeMove(possible.get(i), player); 
			board.undoMove(player); 
			msg.debug(this, board.toString());
			assertArrayEquals(expectedArray, board.getBoard());
		}
	}
	

	// FIXME: failing testcase
	@Test
	public void testBug_HitCount7()
	{	
		byte player = 1;
		
		// P2 has all pieces in heaven
		boardArray[66] = 5;
		boardArray[67] = 5;
	
		boardArray[46] = 1;
		boardArray[47] = 2;
		boardArray[50] = 1;
		boardArray[51] = 3;
		boardArray[55] = 4; 
		
		
		int[] cardsP1 = new int[]{Cards.CLUBS_SEVEN, -1, -1, -1, -1, -1};
		//msg.addItemForWhiteList(this); 
		ig = new InformationGatherer(player);
		
		ig.setCardsForPlayer(cardsP1, player);
		
		board = new BotBoard(boardArray, ig);
		
		byte[] expectedArray = new byte[80];
		for(int i = 0; i < expectedArray.length; i++){
			expectedArray[i] = boardArray[i];
		}
		
		List<Move> possible = board.getAllPossibleMoves(player);
		
		msg.debug(this, "-------------------- P1 --------------------");
		msg.debugLegalMoves(this, (ArrayList)possible);
		msg.debug(this, "------------------  End P1 ------------------");
	
		for(int i=0; i<possible.size(); i++)
		{
			msg.debugMove(this, possible.get(i)); 
			board.makeMove(possible.get(i), player); 
			board.undoMove(player); 
			msg.debug(this, board.toString());
			assertArrayEquals(expectedArray, board.getBoard());
		}
	}
	
	
	// FIXME: failing testcase
	// hits[1,60 , 3,60]
	// order should be considerd: the field 60 contains 3 instead of player 1
	@Test
	public void testBug_HitCount8()
	{	
		byte player = 3;
		
		
		boardArray[57] = 3;
		boardArray[58] = 3;
	
		boardArray[60] = 1;
		
		
		int[] cardsP3 = new int[]{Cards.CLUBS_SEVEN, -1, -1, -1, -1, -1};
		//msg.addItemForWhiteList(this); 
		ig = new InformationGatherer(player);
		
		ig.setCardsForPlayer(cardsP3, player);
		
		board = new BotBoard(boardArray, ig);
		
		byte[] expectedArray = new byte[80];
		for(int i = 0; i < expectedArray.length; i++){
			expectedArray[i] = boardArray[i];
		}
		
		List<Move> possible = board.getAllPossibleMoves(player);
		
		msg.debug(this, "-------------------- P3 --------------------");
		msg.debugLegalMoves(this, (ArrayList)possible);
		msg.debug(this, "------------------  End P3 ------------------");
	
		for(int i=0; i<possible.size(); i++)
		{
			msg.debugMove(this, possible.get(i)); 
			board.makeMove(possible.get(i), player); 
			board.undoMove(player); 
			msg.debug(this, board.toString());
			assertArrayEquals(expectedArray, board.getBoard());
		}
	}
	
	@Test
	public void testBug_HitCount9()
	{	
		byte player = 2;
		
		
		boardArray[9] = 2;
		boardArray[12] = 2;
		boardArray[14] = 3;
		boardArray[15] = 3;
		
		int[] cardsP2 = new int[]{Cards.CLUBS_SEVEN, -1, -1, -1, -1, -1};
		//msg.addItemForWhiteList(this); 
		ig = new InformationGatherer(player);
		
		ig.setCardsForPlayer(cardsP2, player);
		
		board = new BotBoard(boardArray, ig);
		
		byte[] expectedArray = new byte[80];
		for(int i = 0; i < expectedArray.length; i++){
			expectedArray[i] = boardArray[i];
		}
		
		List<Move> possible = board.getAllPossibleMoves(player);
		
		msg.debug(this, "-------------------- P2 --------------------");
		msg.debugLegalMoves(this, (ArrayList)possible);
		msg.debug(this, "------------------  End P2 ------------------");
	
		for(int i=0; i<possible.size(); i++)
		{
			msg.debugMove(this, possible.get(i)); 
			board.makeMove(possible.get(i), player); 
			board.undoMove(player); 
			msg.debug(this, board.toString());
			assertArrayEquals(expectedArray, board.getBoard());
		}
	}
	
	@Test
	public void testBug_HitCount10()
	{	
		byte player = 3;
		
		
		boardArray[72] = 5;
		boardArray[73] = 5;
		boardArray[74] = 5;
		boardArray[43] = 3;
		
		int[] cardsP2 = new int[]{Cards.CLUBS_SEVEN, -1, -1, -1, -1, -1};
		//msg.addItemForWhiteList(this); 
		ig = new InformationGatherer(player);
		
		ig.setCardsForPlayer(cardsP2, player);
		
		board = new BotBoard(boardArray, ig);
		
		byte[] expectedArray = new byte[80];
		for(int i = 0; i < expectedArray.length; i++){
			expectedArray[i] = boardArray[i];
		}
		
		List<Move> possible = board.getAllPossibleMoves(player);
		
		msg.debug(this, "-------------------- P3 --------------------");
		msg.debugLegalMoves(this, (ArrayList)possible);
		msg.debug(this, "------------------  End P3 ------------------");
	
		for(int i=0; i<possible.size(); i++)
		{
			msg.debugMove(this, possible.get(i)); 
			board.makeMove(possible.get(i), player); 
			board.undoMove(player); 
			msg.debug(this, board.toString());
			assertArrayEquals(expectedArray, board.getBoard());
		}
	}
	
	/**
	 * s: 27, t: 73
	 * moves into heaven and hits two own pieces and one of player 1
	 */
	@Test
	public void testBug_HitCount11()
	{	
		byte player = 3;
		
		
		boardArray[27] = 3;
		boardArray[28] = 3;
		boardArray[30] = 1;
		boardArray[31] = 3;
	
		
		int[] cardsP3 = new int[]{Cards.CLUBS_SEVEN, -1, -1, -1, -1, -1};
		//msg.addItemForWhiteList(this); 
		ig = new InformationGatherer(player);
		
		ig.setCardsForPlayer(cardsP3, player);
		
		board = new BotBoard(boardArray, ig);
		
		byte[] expectedArray = new byte[80];
		for(int i = 0; i < expectedArray.length; i++){
			expectedArray[i] = boardArray[i];
		}
		
		List<Move> possible = board.getAllPossibleMoves(player);
		
		msg.debug(this, "-------------------- P3 --------------------");
		msg.debugLegalMoves(this, (ArrayList)possible);
		msg.debug(this, "------------------  End P3 ------------------");
	
		for(int i=0; i<possible.size(); i++)
		{
			msg.debugMove(this, possible.get(i)); 
			board.makeMove(possible.get(i), player); 
			board.undoMove(player); 
			msg.debug(this, board.toString());
			assertArrayEquals(expectedArray, board.getBoard());
		}
	}
	
	
	@Test
	public void testBug17()
	{	
		byte player = 1;
		
		boardArray[1] = 1;
		boardArray[2] = 3;
		boardArray[3] = 1;
		boardArray[4] = 2;
		boardArray[5] = 3;
		boardArray[6] = 4;
		boardArray[7] = 1;
		boardArray[8] = 2;
		
		
		int[] cardsP1 = new int[]{Cards.CLUBS_SEVEN, -1, -1, -1, -1, -1};
		ig = new InformationGatherer(player);
		ig.setCardsForPlayer(cardsP1, player);
		board = new BotBoard(boardArray, ig);
		
		byte[] expectedArray = new byte[80];
		for(int i = 0; i < expectedArray.length; i++){
			expectedArray[i] = boardArray[i];
		}
		
		List<Move> possible = board.getAllPossibleMoves(player);
		
		for(int i=0; i<possible.size(); i++)
		{
			board.makeMove(possible.get(i), player); 
			board.undoMove(player); 
			assertArrayEquals(expectedArray, board.getBoard());
		}
	}
	
	@Test
	public void testBug17_b()
	{	
		byte player = 3;
		
		boardArray[26] = 3;
		boardArray[27] = 1;
		boardArray[28] = 1;
		boardArray[29] = 2;
		boardArray[30] = 4;
		boardArray[31] = 4;
		boardArray[32] = 1;
		boardArray[33] = 3;
		
		
		int[] cardsP1 = new int[]{Cards.CLUBS_SEVEN, -1, -1, -1, -1, -1};
		ig = new InformationGatherer(player);
		ig.setCardsForPlayer(cardsP1, player);
		board = new BotBoard(boardArray, ig);
		
		byte[] expectedArray = new byte[80];
		for(int i = 0; i < expectedArray.length; i++){
			expectedArray[i] = boardArray[i];
		}
		
		List<Move> possible = board.getAllPossibleMoves(player);
		
		for(int i=0; i<possible.size(); i++)
		{
			board.makeMove(possible.get(i), player); 
			board.undoMove(player); 
			assertArrayEquals(expectedArray, board.getBoard());
		}
	}
	
	@Test
	public void testBug17_c()
	{	
		byte player = 3;
		
		boardArray[26] = 3;
		boardArray[27] = 1;
		boardArray[28] = 1;
		boardArray[29] = 3;
		boardArray[30] = 4;
		boardArray[31] = 4;
		boardArray[32] = 1;
		boardArray[33] = 1;
		boardArray[34] = 2;
		
		boardArray[73] = 5;
		boardArray[74] = 5;
		
		int[] cardsP1 = new int[]{Cards.CLUBS_SEVEN, -1, -1, -1, -1, -1};
		ig = new InformationGatherer(player);
		ig.setCardsForPlayer(cardsP1, player);
		board = new BotBoard(boardArray, ig);
		
		byte[] expectedArray = new byte[80];
		for(int i = 0; i < expectedArray.length; i++){
			expectedArray[i] = boardArray[i];
		}
		 
		List<Move> possible = board.getAllPossibleMoves(player);
		
		for(int i=0; i<possible.size(); i++)
		{
			board.makeMove(possible.get(i), player); 
			board.undoMove(player); 
			assertArrayEquals(expectedArray, board.getBoard());
		}
	}
}
