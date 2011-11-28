package intelliDOG.ai.learning.rl;

public class StandardState implements State{
	private int[] cards;
	private byte[] board;
	private byte player;
	int[] inpVec;
	
	/**
	 * Constructor for a State
	 * @param cards
	 * @param board
	 * @param player
	 */
	public StandardState(int[] cards, byte[] board, byte player){
		this.cards = cards;
		this.board = board;
		this.player = player;
		this.inpVec = new int[87];
	}
	
	@Override
	public int[] getInputVector(){
		
		//make the data ready for the input into the neural network!
		//add data from board
		for(int i = 0; i < this.board.length; i++){
			inpVec[i] = this.board[i];
		}
		//add cards
		for(int i = this.board.length; i < this.board.length + this.cards.length; i++){
			inpVec[i] = this.cards[i % 10];
		}
		//add player
		inpVec[this.board.length + this.cards.length] = this.player;
		
		return inpVec;
	}
	
}
