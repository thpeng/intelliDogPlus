package intelliDOG.ai.learning.ann.net.exceptions;

/**
 * Spezifische Exceptionklasse für das NN-File-Management.
 * 
 * @author Iseli Andreas
 * @date 23.04.08
 */
public class NeuralNetFileNotValidException extends NeuralNetException{
	private static final long serialVersionUID = 1L;
	public NeuralNetFileNotValidException() {
		super();
	}
	public NeuralNetFileNotValidException(String msg) {
		super(msg);
	}
}
