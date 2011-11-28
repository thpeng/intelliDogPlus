package intelliDOG.ai.learning.ann.net;

import java.util.LinkedList;
import java.util.Iterator;
import java.util.List;


/**
 * Dies ist das StandardNeuron und wird in den Hidden- sowie OutputLayers verwendet. Es besitzt
 * eine Liste von Synapsen welche mit den Neuronen des jeweils vorgehenden Layers verbunden sind
 * und so die Outputs sowie Gewichtungen für die Berechnungen des eigenen Outputwertes liefern.
 * 
 * Der Output wird somit durch eine Summierung der Funktion (Input * Gewichtung) aller Synapsen
 * berechnet und schliesslich der Aktivierungsfunktion übergeben. Standardmässig ist dies die
 * Sigmoidal Funktion.
 * 
 * Die Konstanten für den Typ der Aktivierungsfunktion sind in der Klasse muehle.model.Constants
 * zu finden.
 * 
 * @author Andreas Iseli, Ralf Mauerhofer
 * @date 07.04.2008
 */
public class Neuron implements INeuron, ISynapseListener {

	private LinkedList<Synapse> inputSynapses;
	private IActivationFunction activationFunction;
	private double outputValue;
	
	/**
	 * Standardkonstruktor, legt die Aktivierungsfunktion fest und initialisiert
	 * alle Variablen. Defaultmässig wird die Sigmoid Funktion verwendet.
	 * 
	 * @param activationType Typ der Aktivierungsfunktion
	 */
	public Neuron(int activationType) {
		//Aktivierungsfunktion festlegen
		switch(activationType) {
			case Constants.LINEARFUNCTION:		this.activationFunction = new LinearFunction();
												break;
			case Constants.SIGMOIDFUNCTION:		this.activationFunction = new SigmoidFunction();
												break;
			default:							this.activationFunction = new SigmoidFunction();
												break;
		}
		//Variableninitialisierung
		this.inputSynapses = new LinkedList<Synapse>();
		this.outputValue = 0.0;
	}
	
	/**
	 * Berechnet den jeweils neuen OutputValue dieses Neurons anhand der Inputsynapsen sowie 
	 * der Aktivierungsfunktion.
	 */
	public void calculateOutput() {
		//Anhand der Inputsynapsen mit der Uebertragungsfunktion die Netzeingabe berechnen
		double sum = 0.0;
		double threshold = 0.0;	//Ist null, ist nur vollständigkeitshalber integriert
		//Für jede Synapse den Output des Inputneurons holen und mit der Gewichten verrechnen sowie summieren
		Iterator<Synapse> synIt = this.inputSynapses.iterator();
		while(synIt.hasNext()) {
			Synapse tempSyn = (Synapse)synIt.next();
			sum += (tempSyn.getInputNeuron().getOutput() * tempSyn.getWeight());
		}
		//Netinput festlegen
		double netInput = sum - threshold;
		//Anhand der Netzeingabe mit der Aktivierungsfunktion die Ausgabe des Neurons berechnen
		this.outputValue = this.activationFunction.calculate(netInput);
	}
	
	/**
	 * Gibt den zuletzt berechneten Outputwert zurück.
	 * 
	 * @return double OutputValue
	 */
	public double getOutput() {
		return this.outputValue;
	}

	/**
	 * Methode fügt dem Neuron eine neue Inputsynapse hinzu, welche für die Berechnung des
	 * Outputvalues wichtig ist.
	 * 
	 * @param newSynapse Synapse
	 */
	public void addInputSynapse(Synapse newSynapse) {
		this.inputSynapses.add(newSynapse);
	}
	
	/**
	 * Gibt die Liste der Inputsynapsen zurück.
	 * 
	 * @return List Inputsynapsen
	 */
	public List<Synapse> getInputSynapses() {
		return this.inputSynapses;
	}
	
}