package intelliDOG.ai.learning.ann.net;

import intelliDOG.ai.learning.ann.net.Constants;

/**
 * Das InputNeuron wird speziell und nur im InputLayer verwendet. Es besitzt keine Synapsen
 * und berechnet den Outputwert auch nicht anhand anderer Neuronen. Dagegen werden in den
 * InputNeuronen direkt die Netzeingaben gespeichert, welche dem NN übergeben wurden.
 * 
 * Den Output berechnet das InputNeuron wie das normale Neuron anhand der Aktivierungsfunktion,
 * welche direkt auf den Inputwert angewendet wird. Standardmässig ist es die Lineare Funktion.
 * 
 * Die Konstanten für den Typ der Aktivierungsfunktion sind in der Klasse muehle.model.Constants
 * zu finden.
 * 
 * @author Iseli Andreas, Mauerhofer Ralf
 * @date 23.04.2008
 */
public class InputNeuron implements INeuron, INNInput {

	private double inputValue;
	private double outputValue;
	private IActivationFunction activationFunction;
	
	/**
	 * Standardkonstruktor.
	 * 
	 * Übernimmt einen Integer, welcher die Aktivierungsfunktion dieses InputNeurons
	 * festlegt. Defaultmässig wird die Lineare Funktion verwendet.
	 * 
	 * Der InputWert wird zu Beginn auf 0 gesetzt.
	 * 
	 * @param activationType Typ der Aktivierungsfunktion
	 */
	public InputNeuron(int activationType) {
		this.inputValue = 0.0;
		this.outputValue = 0.0;
		//Aktivierungsfunktion festlegen
		switch(activationType) {
			case Constants.LINEARFUNCTION:		this.activationFunction = new LinearFunction();
												break;
			case Constants.SIGMOIDFUNCTION:		this.activationFunction = new SigmoidFunction();
												break;
			default:							this.activationFunction = new LinearFunction();
												break;
		}
	}

	/**
	 * Gibt den aktuellen Eingabewert dieses InputNeurons zurück.
	 * 
	 * @return double InputValue
	 */
	public double getInputValue() {
		return this.inputValue;
	}

	/**
	 * Setzt einen neuen Eingabewert.
	 * 
	 * @param inputValue Neuer Eingabewert
	 */
	public void setInputValue(double inputValue) {
		this.inputValue = inputValue;
	}

	/**
	 * Berechnet den neuen Outputwert anhand des Eingabewertes sowie der gewählten
	 * Aktivierungsfunktion.
	 */
	public void calculateOutput() {
		this.outputValue = this.activationFunction.calculate(this.inputValue);
	}

	/**
	 * Gibt den zuletzt berechneten Outputwert zurück.
	 * 
	 * @return double OutputValue
	 */
	public double getOutput() {
		return this.outputValue;
	}

}