package intelliDOG.ai.learning.ann.net.exceptions;

/**
 * Spezifische Exceptionklasse für das NN-Management.
 * 
 * @author Iseli Andreas
 * @date 23.04.08
 */
public class NeuralNetNotValidException extends NeuralNetException {
	private static final long serialVersionUID = 1L;
	public NeuralNetNotValidException() {
		super();
	}
	public NeuralNetNotValidException(String msg) {
		super(msg);
	}
}
