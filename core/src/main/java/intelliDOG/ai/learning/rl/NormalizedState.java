package intelliDOG.ai.learning.rl;

public class NormalizedState implements State {

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
	public NormalizedState(int[] cards, byte[] board, byte player){
		this.cards = cards;
		this.board = board;
		this.player = player;
		this.inpVec = new int[87];
	}
	
	@Override
	public int[] getInputVector() {
		//reset inpVec
		for(int i = 0; i < inpVec.length; i++){
			inpVec[i] = 0;
		}
		//TODO: normalize the data to values between 0 and 1 when it is finally possible to input double values into the neural net
		
		//make the data ready for the input into the neural network!
		//add data from board
		for(int i = 0; i < 64; i++){
			inpVec[i] = this.board[i];
		}
		for(int i = 64; i < 68; i++){
			if(this.board[i] != 0){
				inpVec[i] = 1;
			}
		}
		for(int i = 68; i < 72; i++){
			if(this.board[i] != 0){
				inpVec[i] = 2;
			}
		}
		for(int i = 72; i < 76; i++){
			if(this.board[i] != 0){
				inpVec[i] = 3;
			}
		}
		for(int i = 76; i < 80; i++){
			if(this.board[i] != 0){
				inpVec[i] = 4;
			}
		}
		//add cards
		//calculate cards % 13 (with joker[100] =^= 14)
		for(int i = this.board.length; i < this.board.length + this.cards.length; i++){
			if(this.cards[i % 10] != -1){
				if(this.cards[i % 10] != 100){
					inpVec[i] = (this.cards[i % 10] % 13); 
				}else{
					inpVec[i] = 14;
				}
			}
		}
		//add player
		inpVec[this.board.length + this.cards.length] = this.player;
		return inpVec;
	}

}
