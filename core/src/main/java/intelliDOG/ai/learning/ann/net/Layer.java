package intelliDOG.ai.learning.ann.net;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Die Klasse Layer repräsentiert einen gewünschten Layer im neuronalen Netz. Dies kann
 * ein Input-, Hidden- oder Output-Layer sein. Anhand des Aktivierungstyps legt dieser
 * Layer auch fest, welche Aktivierungsfunktion die eigenen Neuronen verwenden.
 * 
 * @author Andreas Iseli, Ralf Mauerhofer
 * @date 07.04.2008
 */
public class Layer {

	private LinkedList<INeuron> neurons;
	private int type;
	private int activationType;
	
	/**
	 * Standardkonstruktor. Erstellt den neuen Layer mit der gewünschten Anzahl Neuronen sowie
	 * der gewünschten Aktivierungsfunktion für die Neuronen.
	 * 
	 * @param numberOfNeurons Anzahl Neuronen
	 * @param layerType Layertyp (Input, Hidden oder Output)
	 * @param activationType Aktivierungsfunktion der Neuronen dieses Layers
	 */
	public Layer(int numberOfNeurons, int layerType, int activationType) {
		this.type = layerType;
		this.activationType = activationType;
		this.neurons = new LinkedList<INeuron>();
		//Unterscheiden ob InputNeuronen oder normale Neuronen verwendet werden müssen
		if(layerType == Constants.INPUTLAYER) {
			//Anhand des Aktivierungstyps die gewünschte Anzahl Neuronen erstellen 
			for(int i=0; i<numberOfNeurons; i++) {
				this.neurons.add(new InputNeuron(activationType));
			}
		} else {
			//Anhand des Aktivierungstyps die gewünschte Anzahl Neuronen erstellen 
			for(int i=0; i<numberOfNeurons; i++) {
				this.neurons.add(new Neuron(activationType));
			}
		}
	}
	
	/**
	 * Gibt den Layertyp zurück.
	 * 
	 * @return int Layertyp
	 */
	public int getType() {
		return this.type;
	}
	
	/**
	 * Gibt den Typ der Aktivierungsfunktion zurück, welche die Neuronen dieses
	 * Layers verwenden.
	 * 
	 * @return in Aktivierungstyp
	 */
	public int getActivationType() {
		return this.activationType;
	}
	
	/**
	 * Gibt die Anzahl Neuronen dieses Layers zurück.
	 * 
	 * @return int Anzahl Neuronen
	 */
	public int getSize() {
		return this.neurons.size();
	}
	
	/**
	 * Gibt eine Liste mit den Neuronen dieses Layers zurück.
	 * 
	 * @return List Neuronen
	 */
	public List<INeuron> getNeurons() {
		return this.neurons;
	}
	
	/**
	 * Berechnet die neuen Outputwerte der Neuronen dieses Layers.
	 */
	public void calculateNeuronOutputs() {
		Iterator<INeuron> neuronIt = this.neurons.iterator();
		while(neuronIt.hasNext()) {
			INeuron tempNeuron = (INeuron)neuronIt.next();
			tempNeuron.calculateOutput();
		}
	}
	
}
