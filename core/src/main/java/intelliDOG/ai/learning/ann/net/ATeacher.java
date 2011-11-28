package intelliDOG.ai.learning.ann.net;

import intelliDOG.ai.learning.ann.net.exceptions.NeuralNetException;

/**
 * Abstrakte Trainerklasse. Diese definiert die Rahmenmethoden, welche für das Training benötigt werden.
 * 
 * Ein Trainer funktioniert nicht selbständig, sondern wird einem Neuronalen Netz gesetzt, damit dieses
 * innerhalb seiner learn() Methode den Trainer für den spezfischen Teil aufrufen kann.
 * 
 * Eine erbende Klasse muss zwingend die Methode doEpochTraining(..) überschreiben, da diese für jede
 * Epoch das Trainieren des Neuronalen Netzes übernehmen muss. Da es verschiedene Trainingsmöglichkeiten
 * gibt, wird diese Methode einem spezifischen Trainer überlasssen.
 * 
 * @author Iseli Andreas, Mauerhofer Ralf
 * @date 01.05.2008
 */
public abstract class ATeacher {
	
	private double learningRate;
	private double momentum;
	private int learningEpochs;
	private double absoluteError;
	private boolean isTraining;
	private ITeacherListener tListener;
	
	/**
	 * Standardkonstruktor, übernimmt die nötigen globalen Trainingsparameter.
	 * 
	 * @param learningRate Lernrate
	 * @param momentum Momentum
	 * @param learningEpochs Lernepochen
	 */
	public ATeacher(double learningRate, double momentum,  int learningEpochs) {
		this.learningRate = learningRate;
		this.momentum = momentum;
		this.learningEpochs = learningEpochs;
		this.absoluteError = -1.0;
		this.isTraining = false;
	}
	
	/** 
	 * Startet das Trainieren des Neuronalen Netzes. Ruft für jede epoche die abstrakte Methode
	 * doEpochTraining auf, welche von einem erbenden Teacher überschrieben werden muss. Als
	 * Rückgabewert wird dann der absolute Error erwartet.
	 * 
	 * @param net zu trainierendes Neuronales Netz
	 */
	public void startTraining(NeuralNet net) throws NeuralNetException {
		this.isTraining = true;
		
		//Fuer jede Lerneopche die individuelle Lernmethode des erbenden Trainiers aufrufen
		for(int i=0; i<this.learningEpochs; i++) {
			
			//Sofern noch gelernt werden soll
			if(this.isTraining) {
				this.absoluteError = this.doEpochTraining(net);
			} else {
				break;
			}
	
		}
		
		this.isTraining = false;
	}
	
	/**
	 * Abstrakte Methode, welche von einem erbenden Trainer überschrieben werden muss. In dieser
	 * Methode findet das eigentliche Training statt:
	 * 
	 * 	- Eingabe in das NN
	 * 	- Ausgabe des NN berechnen
	 *  - Error sowie netError berechnen
	 *  - Error backpropagieren
	 *  
	 *  Diese Methode wird pro Epoche einmal aufgerufen und MUSS alle Trainigsdaten in einem
	 *  Durchlauf trainieren.
	 * 
	 * @param net zu trainierendes Neuronales Netz
	 * @return absoluter Netzerror nach dem Trainingsdurchlauf
	 */
	protected abstract double doEpochTraining(NeuralNet net) throws NeuralNetException;
	
	/**
	 * Gibt die Anzahl Inputwerte zurück, welche dieser Teacher dem Neuronalen Netz
	 * "füttern" würde.
	 * 
	 * Diese Methode muss von jedem erbenden Trainer entsprechend überschrieben werden.
	 * 
	 * @return int Anzahl Inputwerte
	 */
	public abstract int getNumberOfInputValues();

	/**
	 * Gibt die Anzahl Outputwerte zurück, welche dieser Teacher vom Neuronalen netz 
	 * erwarten würde.
	 * 
	 * Diese Methode muss von jedem erbenden Trainer entsprechend überschrieben werden.
	 * 
	 * @return Anzahl Intputwerte
	 */
	public abstract int getNumberOfOutputValues();
	
	/**
	 * Methode gibt true zurück, wenn alle Trainingswerte im korrekten Bereich gesetzt sind.
	 * 
	 * @return boolean true/false
	 */
	public boolean isValid() {
		if(this.learningRate > 0.0 && this.learningEpochs > 0 && this.momentum >= 0 && this.momentum < 1)
			return true;
		else
			return false;
	}
	
	/**
	 * Gibt zurück, ob dieser Trainer gerade läuft
	 * 
	 * @return boolean isTraining
	 */
	protected boolean isTraining() {
		return this.isTraining;
	}
	
	/**
	 * Unterbricht das Training sofern der Trainer gerade am laufen ist.
	 */
	public void interruptTraining() {
		this.isTraining = false;
	}
	
	/**
	 * Gibt die Lernrate für das Training des NN zurück.
	 * 
	 * @return double Lernrate des NN
	 */
	public double getLearningRate() {
		return this.learningRate;
	}
	
	/**
	 * Setzt die Lernrate für das Trainig des NN.
	 * 
	 * @param learningRate Lernrate des NN
	 */
	public void setLearningRate(double learningRate) {
		this.learningRate = learningRate;
	}

	/**
	 * Gibt die Anzahl Lernepochen zurück.
	 * 
	 * @return Anzahl Lernepochen
	 */
	public int getLearningEpochs() {
		return this.learningEpochs;
	}

	/**
	 * Setzt die Anzahl Lernepochen.
	 * 
	 * @param learningEpochs Anzahl Lernepochen
	 */
	public void setLearningEpochs(int learningEpochs) {
		this.learningEpochs = learningEpochs;
	}
	
	/**
	 * Gibt das Momentum zurück
	 * 
	 * @return double Momentum
	 */
	public double getMomentum() {
		return this.momentum;
	}
	
	/**
	 * Setzt das Momentum.
	 * 
	 * @param momentum Momentum
	 */
	public void setMomentum(double momentum) {
		this.momentum = momentum;
	}
	
	/**
	 * Gibt den absoluten Netzerror zurück. Ist das Netz noch nicht trainiert worden,
	 * so ist dieser -1.0
	 * 
	 * @return double netError
	 */
	public double getNetError() {
		return this.absoluteError;
	}
	
	/**
	 * Fügt dem Trainer einen neuen Listener hinzu, welcher die Trainigsnachrichten
	 * empfangen möchte. (Einfaches Listener Modell ohne Observer Interface)
	 */
	public void addListener(ITeacherListener teacherListener) {
		this.tListener = teacherListener;
	}
	
	protected void sendMessageToListener(String msg) {
		if(this.tListener != null)
			this.tListener.notifyMessage(msg);
	}
	
}
