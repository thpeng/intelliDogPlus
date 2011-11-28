package intelliDOG.ai.learning.ann.net;

/**
 * Definiert die Schnittstelle für ein InputNeuron, welches in einem InputLayer die erweiterte
 * Fähigkeit besitzen muss, einen Initialisierungswert gesetzt zu bekommen.
 * 
 * @author Iseli Andreas, Mauerhofer Ralf
 * @date 23.04.2008
 */
public interface INNInput {

	/** neuen Inputwert setzen */
	public void setInputValue(double inputValue);
	/** letzten Inputwert zurückgeben */
	public double getInputValue();
	
}
