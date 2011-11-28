package intelliDOG.ai.learning.ann.net;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;
import java.util.Random;

import intelliDOG.ai.learning.ann.net.exceptions.*;

/**
 * Neuronale Netz Klasse. Besitzt verschiedene Layer mit Neuronen verschiedener Aktivierungsfunktionen.
 * 
 * Die möglichen Aktivierungsfunktionen und Layer sind den globalen Konstanten zu entnehmen (muehle.model.Constants).
 * 
 * Grundsätzlich besitzt ein Neuronales Netz mindestens den Input- sowie OutputLayer. HiddenLayers sind optional,
 * es sind jedoch auch mehrere möglich. Beim Hinzufügen von Layers ist zu beachten, dass zuerst ein InputLayer,
 * danach soviele wie gewünscht HiddenLayers und erst zuletzt der OutputLayer über die Methode "addLayer(...)"
 * hinzugefügt werden.
 * 
 * Wird durch die Methode setTeacher(...) ein Trainier festgelegt, so kann ein frisch aufgebautes Netz trainiert,
 * oder auch ein bereits trainiertes Netz weiter trainiert werden.
 * 
 * @author Iseli Andreas, Mauerhofer Ralf
 * @date 23.04.08
 */
public class NeuralNet {
	
	private Layer inputLayer;
	private Layer outputLayer;
	private LinkedList<Layer> hiddenLayers;
	private ATeacher teacher;
	private int[] inputTypes;
	private double learningRate;
	private double momentum;
	private double absoluteError;
	private String pathToXML = "";	//only set if created from XML or save in XML File
	
	/**
	 * Standarndkonstruktor. Initialisiert alle Variabeln.
	 */
	public NeuralNet() {
		this.inputLayer = null;
		this.outputLayer = null;
		this.hiddenLayers = new LinkedList<Layer>();
		this.teacher = null;
		this.inputTypes = null;
		this.learningRate = 0.2; //Standardmaessig 0.2, wird vom Teacher notfalls ueberschrieben
	}
	
	/**
	 * Konstruktor zum erstellen eines NN aus einem File. Initialisiert alle Variabeln und fuehrt die Methode zum
	 * erstellen des NN gem�ss den Angaben im File aus.
	 * 
	 * @param file Das File von welchem das NN erstellt werden soll.
	 * @throws FileNotValidException 
	 */
	public NeuralNet(File file) throws NeuralNetFileNotValidException {
		this.inputLayer = null;
		this.outputLayer = null;
		this.hiddenLayers = new LinkedList<Layer>();
		this.teacher = null;
		this.inputTypes = null;
		learningRate = 0.2;
		this.createNNFromFile(file);
	}
	
	/**
	 * Methode, welche dem neuronalen Netz einen neuen Layer hinzufügt. Zuerst muss ein InputLayer hinzugefügt werden,
	 * danach die gewünschte Anzahl HiddenLayer und erst am Schluss der OutputLayer. Danach ist das NN komplett und kann
	 * nicht mehr verändert werden! Dafür werden alle nötigen Synapsen (FullSynapse) automatisch erstellt.
	 * 
	 * Geworfene Exception: WrongLayerException
	 * 
	 * @param layer Gewünschter hinzuzufügender Layer
	 * @throws NeuralNetWrongLayerException Exception, falls ein falscher Layer eingegeben wurde / oder falsche Reihenfolge 
	 */
	public void addLayer(Layer layer) throws NeuralNetWrongLayerException {
		//Test ob null
		if(layer == null)
			throw new NullPointerException("Layer reference was null.");
		//Abfrage welcher Layer; Tests ob richtige Reihenfolge beim hinzufuegen
		/* INPUTLAYER */
		if(layer.getType() == Constants.INPUTLAYER) {
			//Testen ob schon inputlayer vorhanden
			if(this.inputLayer != null) {
				throw new NeuralNetWrongLayerException("An inputlayer already exists.");
			} else {
				this.inputLayer = layer;
			}
		/* HIDDENLAYER */
		} else if(layer.getType() == Constants.HIDDENLAYER) {
			//Bias Neuron erstellen für HiddenLayer mit einem konstanten Output von 1
			InputNeuron biasNeuron = new InputNeuron(Constants.LINEARFUNCTION);
			biasNeuron.setInputValue(1.0);
			biasNeuron.calculateOutput();
			
			//Test ob inputlayer schon vorhanden
			if(this.inputLayer == null) {
				throw new NeuralNetWrongLayerException("There is now inputlayer specified. Please first add an inputlayer.");
			//Test ob outputlayer vorhanden
			} else if(this.outputLayer != null) {
				throw new NeuralNetWrongLayerException("Outputlayer is already specified, you can't add anymore hiddenlayers.");
			} else {
				this.hiddenLayers.add(layer);
			}
			//Synapsen erstellen
			List<INeuron> outputNeurons = null;
			List<INeuron> inputNeurons = null;
			//Falls erster Hiddenlayer, Synapsen direkt mit Inputlayer erstellen
			if(this.hiddenLayers.size() == 1) {
				outputNeurons = this.hiddenLayers.getFirst().getNeurons();
				inputNeurons = this.inputLayer.getNeurons();
			//Ansonsten die Synapsen mit dem vorhergehenden Hiddenlayer erstellen
			} else {
				Iterator<Layer> hiddenDescIt = this.hiddenLayers.descendingIterator();
				outputNeurons = ((Layer)hiddenDescIt.next()).getNeurons();
				inputNeurons = ((Layer)hiddenDescIt.next()).getNeurons();
			}
			//Iterators für Synapsen
			Iterator<INeuron> outputIt = outputNeurons.iterator();
			Iterator<INeuron> inputIt = null;
			//Für jedes Neuron des hinteren Layers...
			while(outputIt.hasNext()) {
				Neuron tempOutputNeuron = (Neuron)outputIt.next(); //sind immer Neuronen und keine Inputneuronen
				inputIt = inputNeurons.iterator();
				//...eine Synapse zu jedem Neuron des vorderen Layers erstellen
				while(inputIt.hasNext()) {
					INeuron tempInputNeuron = (INeuron)inputIt.next();
					//Synapse erstellen
					tempOutputNeuron.addInputSynapse(new Synapse(tempInputNeuron, tempOutputNeuron, this.weightGenerator()));
				}
				
				/* Bias Neuron hinzufügen */
				tempOutputNeuron.addInputSynapse(new Synapse(biasNeuron, tempOutputNeuron, this.weightGenerator()));
				
			}
		/* OUTPUTLAYER */
		} else if(layer.getType() == Constants.OUTPUTLAYER) {
			//Bias Neuron erstellen für OutputLayer mit einem konstanten Output von 1
			InputNeuron biasNeuron = new InputNeuron(Constants.LINEARFUNCTION);
			biasNeuron.setInputValue(1.0);
			biasNeuron.calculateOutput();
			
			//testen ob inputlayer vorhanden
			if(this.inputLayer == null) {
				throw new NeuralNetWrongLayerException("There is now inputlayer specified. Please first add an inputlayer.");
			} else if(this.outputLayer != null) {
				throw new NeuralNetWrongLayerException("An outputlayer already exists. NN can't be modified anymore.");
			} else {
				this.outputLayer = layer;
			}
			//Synapsen erstellen
			List<INeuron> outputNeurons = null;
			List<INeuron> inputNeurons = null;
			//Falls nur inputlayer, direkt mit diesem die Synapsen erstellen
			if(this.hiddenLayers.isEmpty() == true) {
				outputNeurons = this.outputLayer.getNeurons();
				inputNeurons = this.inputLayer.getNeurons();
			//Ansonsten die Synapsen mit dem letzten hiddenlayer erstellen
			} else {
				outputNeurons = this.outputLayer.getNeurons();
				inputNeurons = this.hiddenLayers.getLast().getNeurons();
			}
			//Iterators für Synapsen
			Iterator<INeuron> outputIt = outputNeurons.iterator();
			Iterator<INeuron> inputIt = null;
			//Für jedes Neuron des hinteren Layers...
			while(outputIt.hasNext()) {
				Neuron tempOutputNeuron = (Neuron)outputIt.next();	 //sind immer Neuronen und keine Inputneuronen
				inputIt = inputNeurons.iterator();
				//...eine Synapse zu jedem Neuron des vorderen Layers erstellen
				while(inputIt.hasNext()) {
					INeuron tempInputNeuron = (INeuron)inputIt.next();
					//Synapse erstellen
					tempOutputNeuron.addInputSynapse(new Synapse(tempInputNeuron, tempOutputNeuron, this.weightGenerator()));
				}
				
				/* Bias Neuron hinzufügen */
				tempOutputNeuron.addInputSynapse(new Synapse(biasNeuron, tempOutputNeuron, this.weightGenerator()));
				
			}
		//Falls angegebener Layertyp nicht existiert
		} else {
			throw new NeuralNetWrongLayerException("Specified layer type does not exist.");
		}
	}
	
	/**
	 * Gibt einen zufällig generierten Startwert für die Gewichtung einer Synapse zwischen -0.1 und 0.1 zurück.
	 * 
	 * @return double
	 */
	private double weightGenerator() {
		Random random = new Random();
		return ((random.nextDouble() / 5.0) - 0.1);
	}
	
	/**
	 * Sagt dem NN, welche der zur Verfügung gestellten Inputwerte verwendet werden sollen. Die Grösse des
	 * Arrays inputTypes muss mit der Anzahl InputNeuronen (Neuronen des InputLayers) übereinstimmen.
	 * 
	 * Die möglichen Inputtypen sind in der Klasse muehle.model.Constants gespeichert.
	 * 
	 * @param inputTypes Array mit Angaben der gewünschten Inputwerten
	 */
	public void setInputValueTypes(int[] inputTypes) throws NeuralNetNotValidException {
		//Test ob InputLayer gesetzt
		if(this.inputLayer == null)
			throw new NeuralNetNotValidException("The Input Layer has to be set first.");
		//Test ob die Anzahl InputTypes mit den Anzahl InputNeurons übereinstimmt
		if(inputTypes.length != this.inputLayer.getSize()) {
			throw new NeuralNetNotValidException("The number of inputTypes is not the same as the number of InputNeurons.");
		//Wenn ja, diese Abspeichern
		} else {
			//neuen storage für test
			NNInputValueStorage store = new NNInputValueStorage();
			//array aufbauen
			this.inputTypes = new int[this.inputLayer.getSize()];
			//werte speichern
			for(int i=0; i<inputTypes.length; i++) {
				//Sicherheitscheck ob value existiert
				if(store.isValidKey(inputTypes[i])) {
					this.inputTypes[i] = inputTypes[i];
				} else {
					this.inputTypes = null;
					throw new NeuralNetNotValidException("There was an invalid InputType within the array.");
				}
			}
		}
	}
	
	/**
	 * Gibt ein Array der gesetzten InputValueTypes zurück.
	 * 
	 * Achtung: Sind keine gesetzt, so ist das Array leer...
	 * 
	 * @return int[] InputValueTypes
	 */
	public int[] getInputValueTypes() {
		return this.inputTypes;
	}
	
	/**
	 * Berechnet den Netoutput anhand der gesetzte Eingabewerte und gibt diesen zurück.
	 * 
	 * Besitzt der OutputLayer nur ein Neuron, so befindet sich der wert im Rückgabewert [0]
	 * 
	 * @return double[] Array mit den Outputwerten des OutputLayers
	 */
	public double[] calculateOutput(int[] inputs) throws NeuralNetException {
		if(!this.isValid())
			throw new NeuralNetNotValidException("NN not valid");
		
		//testen ob inputlänge stimmt
		if(inputs.length != this.inputLayer.getSize())
			throw new NeuralNetException("Size of Input not the same as number of Inputneurons.");
		
		//Vor dem Start die zu verwendenden Inputwerte setzen
		Iterator<INeuron> inputNeuronIt = this.inputLayer.getNeurons().iterator();
		int counter = 0;
		while(inputNeuronIt.hasNext()) {
			InputNeuron tempNeuron = (InputNeuron)inputNeuronIt.next();
			tempNeuron.setInputValue(inputs[counter]);
			counter++;
		}
		
		//Zuerst Outputs der Neuronen des InputLayers
		this.inputLayer.calculateNeuronOutputs();
		
		//Dann die Outputs aller HiddenLayers, in der richtigen Reihenfolge
		Iterator<Layer> hiddenIt = this.hiddenLayers.iterator();
		while(hiddenIt.hasNext()) {
			Layer tempHiddenLayer = (Layer)hiddenIt.next();
			tempHiddenLayer.calculateNeuronOutputs();
		}
		
		//Und zuguter letzt der Output des letzten Layers
		this.outputLayer.calculateNeuronOutputs();
		
		//Noch die Outputwerte abspeichern und zurückgeben
		List<INeuron> outputNeurons = this.outputLayer.getNeurons();
		double[] netOutput = new double[outputNeurons.size()];
		Iterator<INeuron> outIt = outputNeurons.iterator();
		counter = 0;
		while(outIt.hasNext()) {
			INeuron tempNeuron = (INeuron)outIt.next();
			netOutput[counter] = tempNeuron.getOutput();
			counter++;
		}
		return netOutput;
	}
	
	/**
	 * Setzt die Trainingsklasse. Überschreibt eine vorherige gesetzte Trainingsklasse.
	 * 
	 * @param teacher Neuer Teacher fürs NN Training
	 */
	public void setTeacher(ATeacher teacher) throws NeuralNetTeacherNotValidException {
		//Testen ob null
		if(teacher == null) 
			throw new NeuralNetTeacherNotValidException("Teacher to set was null");
		//Testen dass anzahl inputwerte = inputvaluetypes und outputwerte = anz outputneuronen
		if(!(this.inputTypes.length == teacher.getNumberOfInputValues())) {
			throw new NeuralNetTeacherNotValidException("Expected number of InputValues within teacher is not the same within the NN");
		} else if(!(this.getNumberOfOutputNeurons() == teacher.getNumberOfOutputValues())) {
			throw new NeuralNetTeacherNotValidException("Expected number of OutputValues within teacher is not the same within the NN");
		} else {
			this.teacher = teacher;
		}
	}
	
	/**
	 * Klont das vorhandene NN (ohne Teacher!) und gibt eine Kopie dieses NNs zurück.
	 * 
	 * Bedingungen dass es geht: Input- & OutputLayer sowie InputTypes müssen gesetzt sein!
	 * 
	 * @throws NeuralNetNotValidException Exception falls das NN nicht korrekt aufgebaut ist
	 */
	public NeuralNet cloneNet() throws NeuralNetNotValidException {
		//Test, ob das NN korrekt erstellt worden ist
		if(this.isValid()) {
			//Ein neues NN kreieren
			NeuralNet newNN = new NeuralNet();
			
			/* CLONE INPUT LAYER */		
			//Neuen InputLayer erstellen
			Layer newInputLayer = new Layer(this.inputLayer.getSize(), this.inputLayer.getType(), this.inputLayer.getActivationType());
			//InputLayer dem neuen NN hinzufügen
			try {
				newNN.addLayer(newInputLayer);
			} catch (NeuralNetWrongLayerException e) {}
			
			/* CLONE HIDDEN LAYERS */
			//Falls im NN Hidden Layers vorhanden sind, neue Hidden Layers erstellen
			if(!this.hiddenLayers.isEmpty()) {				
				//Iterator für alle original Hidden Layers
				Iterator<Layer> orgHiddenIt = this.hiddenLayers.iterator();
				//Für jeden Hidden Layer einen neuen erstellen...
				while(orgHiddenIt.hasNext()) {
					//Original sowie neuen Hidden Layer festlegen
					Layer orgHiddenLayer = (Layer)orgHiddenIt.next();
					Layer newHiddenLayer = new Layer(orgHiddenLayer.getSize(), orgHiddenLayer.getType(), orgHiddenLayer.getActivationType());
					//Hidden Layer dem neuen NN hinzufügen
					try {
						newNN.addLayer(newHiddenLayer);
					} catch (NeuralNetWrongLayerException e) {}
					//Die originalen sowie neuen Output Neuronenlisten festlegen
					List<INeuron> orgOutputNeurons = orgHiddenLayer.getNeurons();
					List<INeuron> newOutputNeurons = newHiddenLayer.getNeurons();
					//Iteratoren der Neuronenlisten festlegen
					Iterator<INeuron> orgOutputIt = orgOutputNeurons.iterator();
					Iterator<INeuron> newOutputIt = newOutputNeurons.iterator();
					//Für jedes Neuron des hinteren Layers (alt sowie neu) ...
					while(orgOutputIt.hasNext() && newOutputIt.hasNext()) {
						//Die alten sowie neuen Output Neuronen festlegen
						Neuron tempOrgOutputNeuron = (Neuron)orgOutputIt.next();	 //sind immer Neuronen und keine Inputneuronen
						Neuron tempNewOutputNeuron = (Neuron)newOutputIt.next();	 //sind immer Neuronen und keine Inputneuronen
						//Iterator durch die alten Synapsen
						Iterator<Synapse> oldSynapseIt = tempOrgOutputNeuron.getInputSynapses().iterator();
						//Iterator durch die neuen Synapsen
						Iterator<Synapse> newSynapseIt = tempNewOutputNeuron.getInputSynapses().iterator();
						//Für jede alte Synapse den Wert in der neuen Synapse überschreiben
						while(oldSynapseIt.hasNext() && newSynapseIt.hasNext()) {
							Synapse tempOldSynapse = (Synapse)oldSynapseIt.next();
							Synapse tempNewSynapse = (Synapse)newSynapseIt.next();
							tempNewSynapse.setWeight(tempOldSynapse.getWeight());
						}
					}
				}
			}
			
			/* CLONE OUTPUT LAYER */
			//neuen OutputLayer erstellen
			Layer newOutputLayer = new Layer(this.outputLayer.getSize(), this.outputLayer.getType(), this.outputLayer.getActivationType());
			//Output Layer dem neuen NN hinzufügen
			try {
				newNN.addLayer(newOutputLayer);
			} catch (NeuralNetWrongLayerException e) {}
			//Die alten sowie neuen Neuronenlisten für die Synapsenlisten holen
			List<INeuron> orgOutputNeurons = this.outputLayer.getNeurons();
			List<INeuron> newOutputNeurons = newOutputLayer.getNeurons();
			//Iteratoren der Neuronenlisten festlegen
			Iterator<INeuron> orgOutputIt = orgOutputNeurons.iterator();
			Iterator<INeuron> newOutputIt = newOutputNeurons.iterator();
			//Für jedes Neuron des hinteren Layers (alt sowie neu) ...
			while(orgOutputIt.hasNext() && newOutputIt.hasNext()) {
				//Die alten sowie neuen Output Neuronen festlegen
				Neuron tempOrgOutputNeuron = (Neuron)orgOutputIt.next();	 //sind immer Neuronen und keine Inputneuronen
				Neuron tempNewOutputNeuron = (Neuron)newOutputIt.next();	 //sind immer Neuronen und keine Inputneuronen
				//Iterator durch die alten Synapsen
				Iterator<Synapse> oldSynapseIt = tempOrgOutputNeuron.getInputSynapses().iterator();
				//Iterator durch die neuen Synapse
				Iterator<Synapse> newSynapseIt = tempNewOutputNeuron.getInputSynapses().iterator();
				//Für jede alte Synapse den Wert in der neuen Synapse überschreiben
				while(oldSynapseIt.hasNext() && newSynapseIt.hasNext()) {
					Synapse tempOldSynapse = (Synapse)oldSynapseIt.next();
					Synapse tempNewSynapse = (Synapse)newSynapseIt.next();
					tempNewSynapse.setWeight(tempOldSynapse.getWeight());
				}
			}
			//Zuguter letzt noch die InputValueTypes setzen
			newNN.setInputValueTypes(this.inputTypes);
			//Das kopierte/geklonte NN zurückgeben
			return newNN;
		//Falls das NN nicht korrekt erstellt wurde, Exception werfen
		} else {
			throw new NeuralNetNotValidException("The neural network is corrupt and cannot be cloned.");
		}
	}
	
	/**
	 * Startet das Trainieren dieses neuronalen Netzes anhand des gesetzten Teachers.
	 *
	 * Wirft eine Exception, falls keine Teacherklasse gesetzt ist!
	 */
	public void learn() throws NeuralNetException {
		learn(0.0, 0.0, false);
	}
	
	public void learn(double td, double outpErr, boolean forTD) throws NeuralNetException {
		//Test, ob Teacher Objekt nicht null
		if(this.teacher == null)
			throw new NeuralNetTeacherNotValidException("There exists no teacher for this NN to train.");
		//Test, ob das NN korrekt erstellt worden ist
		if(this.isValid()) {
			//Test, ob der Teacher korrekt konfiguriert wurde
			if(this.teacher.isValid()) {
				//LearningRate/Momentum setzen
				this.learningRate = this.teacher.getLearningRate();
				this.momentum = this.teacher.getMomentum();
				//netError zurücksetzen
				this.absoluteError = 0.0;
				
				//Training starten
				if(forTD){
					((TDTeacher)this.teacher).startTraining(this, td, outpErr);
				}else{
					this.teacher.startTraining(this);
				}
				
				//NetError auslesen
				this.absoluteError = this.teacher.getNetError();
			//Falls der Teacher nicht korrekt konfiguriert wurde, Exception werfen
			} else {
				throw new NeuralNetException("The teacher of this NN has not been configured correct. NN cannot be trained.");
			}
		//Falls das NN nicht korrekt erstellt wurde, Exception werfen
		} else {
			throw new NeuralNetNotValidException("The neural network is corrupt and cannot be trained.");
		}
	}
	
	/**
	 * Unterbricht den Lernprozess, sofern gerade gelernt wird.
	 */
	public void interruptLearn() {
		if(this.teacher != null)
			this.teacher.interruptTraining();
	}
	
	/**
	 * Testet das neuronale Netz auf seine Richtigkeit. Nur wenn diese Methode true zurückgibt,
	 * kann mit diesem NN gearbeitet werden.
	 * 
	 * @return boolean Validity
	 */
	public boolean isValid() {
		if(this.inputLayer != null && this.outputLayer != null && this.inputTypes != null)
			return true;
		else
			return false;
	}
	
	/**
	 * Hilfsmethode zum Speichern eines Neuronalen Netzwerks in eine XML Datei.
	 * Anmerkung: Weil die Synapsen eines rueckwaertsgerichtet sind, muss beim speichern der Gewichte
	 * der zugehoerigen Synapsen die vorangehende Anzahl Neuronen bekannt sein, damit alle
	 * Gewichte korrekt abgespeichert werden. Deshlab muss fuer den nicht existenten Layer
	 * vor dem InputLayer ein Wert 0 (bzw. "") gesetzt werden.
	 * 
	 * @param file Das File in welches das Neuronale Netzwerk gespeichert wird.
	 * @throws NeuralNetNotValidException 
	 * @throws NeuralNetFileNotValidException 
	 */
	public void saveNNToFile(String file) throws NeuralNetNotValidException, NeuralNetFileNotValidException {
		if (this.isValid()) {
		 	//Sammeln der gebrauchten Daten
		 	int numberOfLayers = this.hiddenLayers.size() + 2;
		 	
		 	//Initialisierung der gebrauchten Daten. 
		 	// Die vorangestellte 0 ist noetig weil die Schleife unten auf den vorangehenden 
		 	// Wert (Layer) zugreifen muss.
		 	ArrayList<Integer> typeOfLayers = new ArrayList<Integer>();
		 	typeOfLayers.add(0);
			ArrayList<Integer> activationTypeOfLayers = new ArrayList<Integer>();
			activationTypeOfLayers.add(0);
			ArrayList<Integer> numberOfNeurons = new ArrayList<Integer>();
			numberOfNeurons.add(0);
			
			//Abfragen der zu Speichernden der Layertypen, des ActivationsType und Anzahl der Neuronen pro Layer
			/* PREPARE INPUT LAYER */		
			typeOfLayers.add(this.inputLayer.getType());
			activationTypeOfLayers.add(this.inputLayer.getActivationType());
			numberOfNeurons.add(this.inputLayer.getNeurons().size());
			
			/* PREPARE HIDDEN LAYERS */
			for (int i=0; i<this.hiddenLayers.size(); i++) {
				typeOfLayers.add(this.hiddenLayers.get(i).getType());
				activationTypeOfLayers.add(this.hiddenLayers.get(i).getActivationType());
				numberOfNeurons.add(this.hiddenLayers.get(i).getNeurons().size());
			}

			/* PREPARE OUTPUT LAYER */
			typeOfLayers.add(this.outputLayer.getType());
			activationTypeOfLayers.add(this.outputLayer.getActivationType());
			numberOfNeurons.add(this.outputLayer.getNeurons().size());
			
			//Die zu speichernden Gewichte der Reihe nach in eine Liste abfüllen
			//Bias Gewichte werden in separate liste geschrieben.
			ArrayList<Double> weights = new ArrayList<Double>();
			ArrayList<Double> biasWeights = new ArrayList<Double>();
			for (int i=0; i<this.hiddenLayers.size(); i++) {
				List<INeuron> neurons = this.hiddenLayers.get(i).getNeurons();
				for (int j=0; j<neurons.size(); j++) {
					List<Synapse> synapses = ((Neuron)neurons.get(j)).getInputSynapses();
					for (int k=0; k<synapses.size(); k++) {
						if (k == synapses.size()-1) {
							biasWeights.add(synapses.get(k).getWeight());
						} else {
							weights.add(synapses.get(k).getWeight());
						}
					}
				}
			}
			List<INeuron> neurons = this.outputLayer.getNeurons();
			for (int i=0; i<neurons.size(); i++){
				List<Synapse> synapses = ((Neuron)neurons.get(i)).getInputSynapses();
				for (int j=0; j<synapses.size(); j++) {
					if (j == synapses.size()-1) {
						biasWeights.add(synapses.get(j).getWeight());
					} else {
						weights.add(synapses.get(j).getWeight());
					}
				}
			}
			
		 	//Speichern des Neuralen Netzwerks in eine Datei
		 	try {  
		    	//Datei um das Neuronale Netzwerk zu speichern erstellen.
		    	FileWriter fstream = new FileWriter(file);
		    	BufferedWriter out = new BufferedWriter(fstream);
		    	  
		    	//Output wird geschrieben.  	  
		    	out.write("<?xml version=\"1.0\"?>\n");
		    	out.write("<neuralNet>\n");
		    	
		    	//Anzahl Layer des Netzwerks.
		    	out.write("<numberOfLayers>" + numberOfLayers + "</numberOfLayers>\n");
		    	//die verwendeten InputTypen
		    	out.write("<inputTypes>\n");
		    	for (int i=1; i <= this.inputTypes.length; i++) {
		    		out.write("<inputType number=\"" + i + "\">" + this.inputTypes[i-1] + "</inputType>\n");
		    	}
		    	out.write("</inputTypes>\n");
		    	    	 
		    	//Schleife für alle Layer
		    	int h = 0;
		    	int g = 0;
		    	for (int i=1; i <= numberOfLayers; i++) {
		    		out.write("<layer number=\"" + i + "\">\n");
		    		out.write("<properties>\n");
		    		out.write("<layerType>" + typeOfLayers.get(i) + "</layerType>\n");
		    		out.write("<activationType>" + activationTypeOfLayers.get(i) + "</activationType>\n");
		    		out.write("<numberOfNeurons>" + numberOfNeurons.get(i) + "</numberOfNeurons>\n");
		    		out.write("</properties>\n");
		    		//Schleife fuer alle Neuronen pro Layer
		    		for (int j=1; j <= numberOfNeurons.get(i); j++) {
			    		out.write("<neuron number=\"" + j + "\">\n");
			    		//Schleife fuer alle Synapsen dieses Layers zum vorangehenden Layer
			    		int k;
			    		for (k=1; k <= numberOfNeurons.get(i-1); k++) {
				    		out.write("<synapse number=\"" + k + "\">\n");
		    				out.write("<weight>" + weights.get(h) + "</weight>\n");
		    				h++;
		    				out.write("</synapse>\n");
				    	}
			    		//Falls es sich nicht um einen Inputlayer handelt, Bias Gewichte hinzufügen.
			    		if (i > 1) {
			    			out.write("<synapse number=\"" + k + "\" type=\"bias\">\n");
			    			out.write("<weight>" + biasWeights.get(g) + "</weight>\n");
			    			out.write("</synapse>\n");
			    			g++;
			    		}
			    		out.write("</neuron>\n");
		    		}
		    		out.write("</layer>\n");
		    	} 	  
		    	out.write("</neuralNet>\n");
	    	  
		    	//Beenden des Outputstreams
		    	out.close();
		    	//Pfad speichern
		    	this.pathToXML = file;
		    //Abfangen einer allfälligen Fehlermeldung.
		 	} catch (Exception e) {
		 		throw new NeuralNetFileNotValidException("File can not be saved.");
		 	}
		} else {
			throw new NeuralNetNotValidException("The neural network is corrupt and cannot be trained.");
		}
	}
	
	/**
	 * Methode zum erstellen eines neuronalen Netzwerks aus einem File. Zuerst wird der Parser initialisiert
	 * und dan die Werte gehohlt. Das neue Netz wird dann Layer f�r Layer aufgebaut.
	 * 
	 * @param file Das einzulesende File.
	 * @return newNN, das neue vom File eingelesene neuronale Natzwerk
	 * @throws NeuralNetFileNotValidException
	 */
	private NeuralNet createNNFromFile(File file) throws NeuralNetFileNotValidException {
		//Test ob file gesetzt
		if(file == null || !file.isFile())
			throw new NeuralNetFileNotValidException("No or not correct XML file selected.");

		//Versuch das XML File zu parsen
		try {
			NNXMLParser parser = new NNXMLParser(file);

			//Ben�tigte Werte vom parser hohlen und noetigenfalls aufbereiten
			int newNumberOfLayers = parser.getNumberOfLayers();
			ArrayList<Integer> parserInputTypes = parser.getInputTypes();
			int[] newInputTypes = new int[parserInputTypes.size()];
			for (int i = 0; i < parserInputTypes.size(); i++) {
				newInputTypes[i] = parserInputTypes.get(i);
			}
			ArrayList<Integer> newTypeOfLayers = parser.getTypeOfLayers();
			ArrayList<Integer> newActivationTypeOfLayers = parser.getActivationTypeOfLayers();
			ArrayList<Integer> newNumberOfNeurons = parser.getNumberOfNeurons();
			ArrayList<Double> newWeights = parser.getWeights();
							
			//Schleife über alle Layer
			//h dient zum zaehlen der Gewichtungen, damit sei in der richtigen Reihenfolge gespeichert werden.
			int h = 0;
			for (int i = 0; i < newNumberOfLayers; i++) {
				Layer newLayer = new Layer(newNumberOfNeurons.get(i), newTypeOfLayers.get(i), newActivationTypeOfLayers.get(i));
				//Layer dem neuen NN hinzufuegen
				try {
					this.addLayer(newLayer);
				} catch (NeuralNetWrongLayerException e) {}
				//folgende Schritte werden f�r den InputLayers (i=0) nicht ausgef�hrt
				if (i != 0) {
					List<INeuron> neurons = newLayer.getNeurons();
					for (int j = 0; j < neurons.size(); j++) {
						List<Synapse> synapses = ((Neuron)neurons.get(j)).getInputSynapses();
						for (int k = 0; k < synapses.size(); k++) {
							synapses.get(k).setWeight(newWeights.get(h));
							h++;
						}
					}
				}
			}

			//Zuguter letzt noch die InputValueTypes setzen
			this.setInputValueTypes(newInputTypes);
			
			//Pfad speichern
			this.pathToXML = file.getAbsolutePath();
			
			//Das neu eingelesene NN wird zurückgegeben
			return this;
			
		} catch (Exception e) {
			throw new NeuralNetFileNotValidException("File could not be parsed. " + e.getMessage());
		}
	}
	
	/**
	 * Methode propagiert einen entsprechenden Error im Netzwerk zurück und passt die Gewichtungen der Synapsen
	 * entsprechend dem Backpropagation Algorithmus an.
	 * 
	 * Dabei wird auch ein Momentum verwendet, welches aber nur greift, wenn es > 0 gesetzt ist. Je grösser
	 * das Momentum ist, umso stärker wird die zuletzt gemachte Gewichtsverändertung mit einberechnet.
	 * 
	 * @param errorRate zu zurückpropagierende Fehlerrate
	 */
	protected void backpropagate(double errorRate) {
		backpropagate(errorRate, 0.0, 0.0, 0.0, Constants.BACKPROP_NORMAL);
	}
	
	
	protected void backpropagate(double errorRate, double outputError, double gamma, double lambda, int mode) {
		//backpropagate the error rate layer by layer, neuron by neuron
		
		//Gewichtungen outputlayer neu berechnen
		Iterator<INeuron> outputNeuronsIt = this.outputLayer.getNeurons().iterator();
		while(outputNeuronsIt.hasNext()) {
			Neuron tempNeuron = (Neuron)outputNeuronsIt.next();
			Iterator<Synapse> synapsesIt = tempNeuron.getInputSynapses().iterator();
			//Für alle Synapsen / Gewichtungen pro Neuron
			while(synapsesIt.hasNext()) {
				Synapse tempSynapse = (Synapse)synapsesIt.next();
				//ALT: delta value berechnen
				//double delta = this.learningRate * errorRate * tempSynapse.getInputNeuron().getOutput() * tempNeuron.getOutput() * (1-tempNeuron.getOutput());
				double delta = 0.0;
				if(mode == Constants.BACKPROP_NORMAL){
					//NEU: Momentum benutzen um den Lernprozess zu verbessern
					delta = (1-this.momentum) * this.learningRate * errorRate * tempSynapse.getInputNeuron().getOutput() * tempNeuron.getOutput() * (1-tempNeuron.getOutput())
									+ this.momentum * (tempSynapse.getWeight() - tempSynapse.getPreviousWeight());
				}else if(mode == Constants.BACKPROP_TD_LAMBDA){
					
					
					//TODO: make sure that the eligibility update is also correct for bias neurons!
					//eligibility = gamma * lambda * eligibility + gradient(V(s))[with respect to Theta]
					tempSynapse.setEtrace(gamma * lambda * tempSynapse.getEtrace() + (this.learningRate * tempNeuron.getOutput() * (1-tempNeuron.getOutput()) * outputError * tempSynapse.getInputNeuron().getOutput()));
					
				
					//backpropagation with -> alpha * delta * eligibility
					delta = (1 - this.momentum) * this.learningRate * errorRate * tempSynapse.getEtrace() 
								+ this.momentum * (tempSynapse.getWeight() - tempSynapse.getPreviousWeight());
				}
				//neue Gewichtung setzen
				tempSynapse.adjustWeight(delta);
			}
		}
		
		//Gewichtungen der hiddenlayers neu berechnen
		if(!this.hiddenLayers.isEmpty()) {
			//Iterator verwenden, der von hinten nach vorne durchiteriert
			Iterator<Layer> hiddenLayersIt = this.hiddenLayers.descendingIterator();
			//Für jeden Hiddenlayer
			while(hiddenLayersIt.hasNext()) {
				Layer tempHiddenLayer = (Layer)hiddenLayersIt.next();
				Iterator<INeuron> hiddenNeuronsIt = tempHiddenLayer.getNeurons().iterator();
				//Für jedes Neuron des hiddenLayers
				while(hiddenNeuronsIt.hasNext()) {
					Neuron tempNeuron = (Neuron)hiddenNeuronsIt.next();
					Iterator<Synapse> synapsesIt = tempNeuron.getInputSynapses().iterator();
					//Für alle Synapsen / Gewichtungen pro Neuron
					while(synapsesIt.hasNext()) {
						Synapse tempSynapse = (Synapse)synapsesIt.next();
						//delta value berechnen
						//double delta = this.learningRate * errorRate * tempSynapse.getInputNeuron().getOutput() * tempNeuron.getOutput() * (1-tempNeuron.getOutput());
						//Momentum benutzen um den Lernprozess zu verbessern
						double delta = (1-this.momentum) * this.learningRate * errorRate * tempSynapse.getInputNeuron().getOutput() * tempNeuron.getOutput() * (1-tempNeuron.getOutput())
										+ this.momentum * (tempSynapse.getWeight() - tempSynapse.getPreviousWeight());
						//neue Gewichtung setzen
						tempSynapse.adjustWeight(delta);
					}
				}
			}
		}
		
	}
	
	/**
	 * Gibt die aktuelle Netz-Error-Rate zurueck. Ist erst dann aussagenkräftig, sobald das NN
	 * mindestens einmal trainiert worden ist.
	 * 
	 * @return double net error rate
	 */
	public final double getNetError() {
		return this.absoluteError;
	}
	
	public int getNumberOfInputNeurons() {
		return this.inputLayer.getSize();
	}
	
	public int[] getNumberOfHiddenNeurons() {
		if(this.hiddenLayers.size() > 0) {
			int[] hiddens = new int[this.hiddenLayers.size()];
			for(int i=0; i<hiddens.length; i++) {
				hiddens[i] = this.hiddenLayers.get(i).getSize();
			}
			return hiddens;
		} else {
			return new int[0];
		}
	}
	
	public int getNumberOfOutputNeurons() {
		return this.outputLayer.getSize();
	}
	
	/**
	 * Gibt den Pfad zur korrespondierenden XML Datei zurück, sofern dieses NN
	 * aus einer XML Datei erstellt oder in einer gespeichert wurde.
	 * 
	 * Der String ist leer, sofern keine der genannten Tatsachen zutrifft.
	 * 
	 * @return String Pfad zur XML Datei
	 */
	public String getPathToXML() {
		return this.pathToXML;
	}
	
}