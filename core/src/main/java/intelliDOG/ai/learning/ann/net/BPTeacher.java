package intelliDOG.ai.learning.ann.net;

import intelliDOG.ai.learning.ann.net.exceptions.*;

/**
 * Spezifischer Backpropagation Trainer. Dieser trainiert ein Neuronales Netz anhand des bekannten Backpropagation-
 * Algorithmus und benötigt entsprechend die Input und Outputwerte, da es ein überwachtes Lernen ist.
 * 
 * @author Iseli Andreas, Mauerhofer Ralf
 * @date 01.05.2008
 */
public class BPTeacher extends ATeacher {
	
	private int[][] inputs;
	private int[] outputs;
	
	/**
	 * Standardkonstruktor, übernimmt die nötigen globalen Trainigsparameter sowie ein zweidimensionales Array
	 * von mehreren Inputwerten und dem korrespondierenden Array von Outputwerten, welche
	 * durch die Inputwerte erreicht werden sollten.
	 * 
	 * Die erste Dimension des InputArrays ist die Anzahl der Trainingswerte, in der zweiten Dimension sind alle
	 * benötigten Eingabewerte für das Netz.
	 * 
	 * Das OutputArray hat nur eine Dimension mit den korrespondierenden Outputwerten. Das Trainieren ist insofern
	 * eingeschränkt, dass nur Neuronale Netze mit nur einem Neuron im OutputLayer trainiert werden können.
	 * 
	 * @param learningRate Lernrate
	 * @param momentum Momentum
	 * @param learningEpochs Lerneopchen
	 * @param inputs Zweidimensionales Array von Netzeingabewerten
	 * @param outputs Array vom gewünschten Netzausgabewerte 
	 */
	public BPTeacher(double learningRate, double momentum, int learningEpochs, int[][] inputs, int[] outputs) {
		super(learningRate, momentum, learningEpochs);
		this.inputs = inputs;
		this.outputs = outputs;
	}


	/**
	 * Methode übernimmt das Training während einer Epoche spezfisch für ein normales
	 * Backpropagation Lernen.
	 * 
	 * @param net zu trainierendes Neuronales Netz
	 * @return double Absoluter Netzerror
	 */
	protected double doEpochTraining(NeuralNet net) {
		
		//Zum Berechnen des absoluten NetErrors
		double absError = 0.0;
		
		//Für jeden Trainingssatz
		for(int j=0; j<this.inputs.length; j++) {
			//Sofern noch gelernt werden soll
			if(this.isTraining()) {
				try {
					
					/* Netzausgabe berechnen */
					double netOutput[] = net.calculateOutput(this.inputs[j]);
					/* Berechnen des Fehlers */
					double error = (this.outputs[j] - netOutput[0]);
					/* net error berechnen */
					absError += Math.abs(error);
					/* backpropagieren */
					net.backpropagate(error);
					
				} catch (NeuralNetException e) {}
			//Ansonsten abbrechen
			} else {
				break;
			}
		}
		
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
		return this.inputs[0].length;
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