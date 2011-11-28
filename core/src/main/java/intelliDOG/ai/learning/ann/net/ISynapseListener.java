package intelliDOG.ai.learning.ann.net;

import java.util.List;

/**
 * Schnittstelle für Standardneuronen, welche Synapsen besitzen müssen für die Berechnung
 * des neuen Outputwertes.
 * 
 * Die Neuronen besitzen nur rückwirkend Synapsen; so besitzen zB. die Neuronen des OutputLayers Synapsen
 * zu den Neuronen des HiddenLayers, die des HiddenLayers jedoch nicht zu denen des OutputLayers. Dies
 * spart Zeit, Platz und wird sowieso nicht benötigt.
 * 
 * @author Iseli Andreas, Mauerhofer Ralf
 * @date 02.04.2008
 */
public interface ISynapseListener {

	/** Hinzufügen einer neuen Synapse */
	public void addInputSynapse(Synapse newSynapse);
	/** Zurückgeben aller Synapsen */
	public List<Synapse> getInputSynapses();
	
}
