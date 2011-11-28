package intelliDOG.ai.learning.ann.net;

/**
 * Lineare Aktivierungsfunktion. Dies gibt direkt den Netinput wieder zurück.
 * 
 * @author Iseli Andreas, Mauerhofer Ralf
 * @date 02.04.2008
 */
public class LinearFunction implements IActivationFunction {

	/**
	 * Berechnet die Aktivierungsfunktion anhand des Netinputs eines Neurons
	 * 
	 * @param netInput Netzeingabe eines Neurons
	 * @return double Wert der Aktivierungsfunktion
	 */
	public double calculate(double netInput) {
		return netInput;
	}

}
