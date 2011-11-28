package intelliDOG.ai.learning.ann.net;

/**
 * Schnittstelle definiert die Standardmethoden, welche jede Art von Neuron unterstützen muss.
 * 
 * @author Iseli Andreas, Mauerhofer Ralf
 * @date 23.04.2008
 */
public interface INeuron {

	/** Berechnen des Neuronoutputs */
	public void calculateOutput();
	/** Zurückgeben des zuletzt berechneten Neuronoutputs */
	public double getOutput();
	
}
