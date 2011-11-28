package intelliDOG.ai.learning.ann.net.exceptions;

/**
 * Spezifische Exceptionklasse für das Storage-Management.
 * 
 * @author Iseli Andreas
 * @date 25.04.08
 */
public class NeuralNetStorageValueNotExistException extends NeuralNetException {
	private static final long serialVersionUID = 1L;
	public NeuralNetStorageValueNotExistException() {
		super();
	}
	public NeuralNetStorageValueNotExistException(String msg) {
		super(msg);
	}
}
