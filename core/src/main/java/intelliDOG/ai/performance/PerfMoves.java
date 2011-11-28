package intelliDOG.ai.performance;

import java.util.List;

import intelliDOG.ai.framework.BotBoard;
import intelliDOG.ai.framework.InformationGatherer;
import intelliDOG.ai.framework.Move;
import intelliDOG.ai.framework.Players;
import intelliDOG.ai.utils.DebugMsg;

public class PerfMoves {

	
	private static BotBoard board;
	private static InformationGatherer ig;
	private static byte[] boardArray;
	private static DebugMsg msg = DebugMsg.getInstance();
	
	
	public static void main(String[] args) {
		boardArray = new byte[80];
		//msg.setDebugAll(true);
		test2();
	}
	
	private static void test1(){
		boardArray[63] = 2;
		ig = new InformationGatherer(Players.P2);
		ig.setCardsForPlayer(new int[] { 4, -1, -1, -1, -1, -1 }, Players.P2);
		board = new BotBoard(boardArray, ig);
		List<Move> moves = board.getAllPossibleMoves(Players.P2);
		System.out.println(moves.size());
	}
	
	private static void test2(){
		boardArray[46] = 4;
		boardArray[77] = 5;
		boardArray[79] = 5;
		ig = new InformationGatherer(Players.P4);
		ig.setCardsForPlayer(new int[] { 7, 5, 3, 11, 9, -1 }, Players.P4);
		board = new BotBoard(boardArray, ig);
		List<Move> possible = board.getAllPossibleMoves(Players.P4);
		
		System.out.println(possible.size());
	}

}
