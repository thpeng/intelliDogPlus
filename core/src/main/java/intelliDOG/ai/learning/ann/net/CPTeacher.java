package intelliDOG.ai.learning.ann.net;

import intelliDOG.ai.learning.ann.net.exceptions.NeuralNetException;

/**
 * Diese Klasse repräsentiert einen Trainer, welcher das Paired-Comparism Paradigm zum Lernen eines NNs verwendet.
 * 
 * Entsprechend dem CP Lernalgorithmus werden zwei Inputs benötigt, wobei der eine die jeweils besseren Spielstellungen
 * beinhaltet wie der andere. Das Paired-Comparism Paradigm ist kein herkömmlicher Lernalgorithmus, denn es ist zwar
 * ein überwachtest Lernen, bewertet den Output des NN jedoch nur relativ und nicht absolut. Dies bedeutet, es wird
 * der Output1 der ersten Inputwerte berechnet, in einer Kopie des NN der Output2, und diese werden dann verglichen.
 * Der erste Output sollte höher sein als der zweite, ist dem so, muss nichts verändert werden. Ist jedoch der zweite
 * Output höher als der erste, so wird der Error berechnet und zurückpropagiert. 
 * 
 * @author Iseli Andreas, Mauerhofer Ralf
 * @date 08.05.2008
 */
public class CPTeacher extends ATeacher {

	private int[][] inputs1;	//Eingaben mit den jeweils besseren Spielstellungen
	private int[][] inputs2;	//Eingaben mit den schlechteren Spielstellungen
	
	/**
	 * Standardkonstruktor, übernimmt die nötigen globalen Trainigsparameter sowie zwei zweidimensionale Arrays von
	 * Inputwerten, welche beim CP verglichen werden. Im ersten Array sind jeweils die Spielstellungen, welche
	 * als besser bewertet werden müssten als diejenigen im zweiten Array im gleichen Index.
	 * 
	 * Es werden immer nur inputs1[x][..] mit inputs2[x][..] verglichen. Es existieren keine Quervergleiche, diese 
	 * werden vor dem Training erstellt und schon so in diese Inputarrays abgefüllt. So muss nur noch einfach trainiert werden.
	 * 
	 * @param learningRate Lernrate
	 * @param momentum Momentum
	 * @param learningEpochs Lerneopchen
	 * @param inputs1 InputArray mit den besseren Spielstellungen
	 * @param inputs2 OutputArray mit den schlechteren Spielstellungen
	 */
	public CPTeacher(double learningRate, double momentum, int learningEpochs, int[][] inputs1, int[][] inputs2) {
		super(learningRate, momentum, learningEpochs);
		this.inputs1 = inputs1;
		this.inputs2 = inputs2;
	}

	/**
	 * Methode übernimmt das Training während einer Epoche spezfisch für das Lernen mit dem
	 * Paired-Comparism Paradigm.
	 * 
	 * @param net zu trainierendes Neuronales Netz
	 * @return double Absoluter Netzerror
	 */
	protected double doEpochTraining(NeuralNet net) throws NeuralNetException {
		
		//Zum berechnen des absoluten Neterrors
		double absError = 0.0;
		
		//Platzhalter für geklontes Netz
		NeuralNet netClone = null;
		
		//Counter zählt die Anzahl richtigen Bewertungen des Netzwerks
		int rightCounter = 0;
		int j = 0;
		
		//für jeden Trainingssatz
		for(j=0; j<this.inputs1.length; j++) {
			//Sofern noch gelernt werden soll
			if(this.isTraining()) {
				try {
					
					//Netz klonen
					netClone = net.cloneNet();
					
					//Netzausgaben berechnen
					double[] netOutput1 = net.calculateOutput(inputs1[j]);
					double[] netOutput2 = netClone.calculateOutput(inputs2[j]);
					
					//Netz wir verändert, wenn der output, welcher besser sein sollte nicht besser ist
					//der sogenannte Fehlerwert wird initialisiert
					double error = 0.0;
					if(netOutput2[0] >= netOutput1[0]) {
						//Falls die Differenz der beiden Ausgaben grösser ist als der kleinere Wert wird das Netzwerk 
						//um eine grösseren Wert korrigiert
						if ((netOutput2[0]-netOutput1[0]) > netOutput1[0]) {
							error = -0.02;
						} else {
							error = -0.01;
						}
						//Die Backpropagation wird ausgelöst
						net.backpropagate(error);
					} else {
						//die Anzahl richtigen Bewertungen werden gezählt
						rightCounter++;
					}
					
				} catch (NeuralNetException e) {}
				
				//der absolute Error beträgt die Anzahl richtig bewerteten über die Totale Anzahl Trainings
				absError = 1 - (rightCounter/(j+1));
			//Ansonsten abbrechen
			} else {
				break;
			}
		}
		
		//Ausgabe der Anzahl richtigen des Netzwerks
		this.sendMessageToListener("(" + (100*rightCounter/(j+1)) + "%) " + rightCounter + " von " + (j+1) );
		
		//Absoluten Error zurückgeben
		return absError;
	}
	
	/**
	 * Gibt die Anzahl Inputwerte zurück, welche dieser Teacher dem Neuronalen Netz
	 * "füttern" würde.
	 * 
	 * @return int Anzahl Inputwerte
	 */
	public int getNumberOfInputValues() {
		return this.inputs1[0].length;
	}

	/**
	 * Gibt die Anzahl Outputwerte zurück, welche dieser Teacher vom Neuronalen netz 
	 * erwarten würde.
	 * 
	 * @return Anzahl Intputwerte
	 */
	public int getNumberOfOutputValues() {
		return 1;
	}

}
