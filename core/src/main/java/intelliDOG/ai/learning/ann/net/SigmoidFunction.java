package intelliDOG.ai.learning.ann.net;

/**
 * Sigmoidal Aktivierungsfunktion. Verwendet die sigmoid Funktion um den Rückgabewert
 * zur berechnen.
 * 
 * @author Iseli Andreas, Mauerhofer Ralf
 * @date 25.04.2008
 */
public class SigmoidFunction implements IActivationFunction {

	/**
	 * Berechnet die Aktivierungsfunktion anhand des Netinputs eines Neurons.
	 * 
	 * @param netInput Netzeingabe eines Neurons
	 * @return double Wert der Aktivierungsfunktion
	 */
	public double calculate(double netInput) {
		double divident = 1;	// => divident : divisor = quotient
		double divisor = 1 + (Math.pow(Math.E, -netInput));
		double f = divident / divisor;
		//return
		return f;
	}

}
