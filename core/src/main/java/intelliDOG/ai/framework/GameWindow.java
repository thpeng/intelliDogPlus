package intelliDOG.ai.framework;

public interface GameWindow {

	/**
	 * This will update the GUI with the given values
	 * @param board the actual situation on the board
	 * @param cards the cards of all players
	 * @param playerOnTurn the player that is on turn
	 * @param lastBeforeCardsDistribution when true, no timeout is being used 
	 * because the view will be updated right after the card distribution (with a timeout)  
	 */
	public abstract void update(byte[] board, int[][] cards, byte playerOnTurn,
			boolean lastBeforeCardsDistribution);

	public abstract void updateOnNewRound(int[][] cards);

	/**
	 * Adds the specified text to the Message textarea
	 * @param text the text to add
	 */
	public abstract void addMessage(String text);

	/**
	 * Clears the Message textarea
	 */
	public abstract void clearMessages();

	/**
	 * set the timeout to sleep after each update
	 * @param timeout the timeout to set
	 */
	public abstract void setTimeout(long timeout);

	/**
	 * @return the successful
	 */
	public abstract boolean wasSuccessful();

	/**
	 * @return the playerNames
	 */
	public abstract String[] getPlayerNames();

}