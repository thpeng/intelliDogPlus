package intelliDOG.ai.learning.ann.net;

/**
 * Schnittstelle für eine Aktivierungsfunktion eines Neurons.
 * 
 * @author Iseli Andreas, Ralf Mauerhofer
 * @date 02.04.2008
 */
public interface IActivationFunction {
	
	/** Berechnet die Aktivierungsfunktion anhand des Netinputs eines Neurons */
	public double calculate(double netInput);
	
}
