package intelliDOG.ai.learning.ann.net.exceptions;

/**
 * Spezifische Exceptionklasse für das Layermanagement.
 * 
 * @author Iseli Andreas
 * @date 23.04.08
 */
public class NeuralNetWrongLayerException extends NeuralNetException {
	private static final long serialVersionUID = 1L;
	public NeuralNetWrongLayerException() {
		super();
	}
	public NeuralNetWrongLayerException(String msg) {
		super(msg);
	}
}
