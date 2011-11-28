package intelliDOG.ai.framework;

import java.util.Arrays;

/**
 * This class represents a move that can be made on a <class>Board</class> or <class>BotBoard</class>
 * It contains all information that is used to get from one gamestate to the next
 * or vice versa.
 */
public class Move {

	private int card;
	private byte[] positions;
	private byte[] hits;
	private boolean[] wasProtected;
	private byte[][] hits_seven; 
	
	/**
	 * Constructor for a new move
	 * @param card card used for this move
	 * @param positions array of start and end position
	 * @param hits players that were hit while moving
	 * @param wasProtected array that indicates protected fields
	 */
	public Move(int card, byte[] positions, byte[] hits, boolean[] wasProtected){
		this.card = card;
		assert positions.length <= 14;
		assert hits.length <= 14;
		this.positions = positions;
		this.hits = hits;
		this.wasProtected = wasProtected;
		
		hits_seven = new byte[][]{};
	}
	
	public Move(int card, byte[] positions){
		this(card, positions, new byte[]{}, new boolean[]{});
	}

	/**
	 * @return the positions
	 */
	public byte[] getPositions() {
		return positions;
	}

	/**
	 * @return the hits
	 */
	public byte[] getHits() {
		return hits;
	}

	/**
	 * @return the hits
	 */
	public byte[][] get7Hits() {
		return hits_seven;
	}
	
	/**
	 * @param hits the hits to set
	 */
	public void setHits(byte[] hits) {
		assert hits.length <= 14;
		this.hits = hits;
	}

	/**
	 * @param set hits array used by the card 7
	 */
	public void set7Hits(byte[][] hits) {
		this.hits_seven = hits;
	}
	
	/**
	 * @return the card
	 */
	public int getCard() {
		return card;
	}

	/**
	 * @return the wasProtected
	 */
	public boolean[] getwasProtected() {
		return wasProtected;
	}

	/**
	 * @param wasProtected the wasProtected to set
	 */
	public void setWasProtected(boolean[] wasProtected) {
		assert wasProtected.length <= 7;
		this.wasProtected = wasProtected;
	}
	
	/**
	 * this method is used to get a copy of a move for a card
	 * with the same type but other suited (same suit works also but makes no sense)
	 * @param card the card that shall be used for the copy
	 * @return the copy of itself with the card given over
	 */
	public Move copy(int card){
		assert card != 100 && card % 13 == this.card % 13;
		
		return new Move(card, Arrays.copyOf(positions, positions.length), 
				Arrays.copyOf(hits, hits.length), 
				Arrays.copyOf(wasProtected, wasProtected.length));
	}
	
	
	public boolean sameMove(Move other)
	{
		if(this.positions.length != other.positions.length)
			return false; 

		for(int pos = 0; pos < other.positions.length; pos++)
		{
			if(this.positions[pos] != other.positions[pos])
				return false; 
		}
		return true; 
	}
}
