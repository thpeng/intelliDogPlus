package intelliDOG.ai.learning.ann.net;

/**
 * Eine Synapse bildet die Verbindung zwischen zwei Neuronen, welche sich jeweils in zwei verschiedenen,
 * sich aufeinander folgenden Layern befinden. zB Input => Hidden.
 * 
 * Entsprechend gibt es ein input sowie ein output Neuron. Die zugehörige Gewichtung wird jeweils beim
 * output Neuron für die Berechnung des Netzinputs verwendet.
 * 
 * @author Iseli Andreas, Mauerhofer Ralf
 * @date 02.04.2008
 */
public class Synapse {

	private INeuron inputNeuron;
	private INeuron outputNeuron;
	private double weight;
	private double previousWeight;
	private double etrace;
	
	/**
	 * Standardkonstruktor.
	 * 
	 * @param input InputNeuron
	 * @param output OutputNeuron
	 * @param weight Gewichtung der Synapse
	 */
	public Synapse(INeuron input, INeuron output, double weight) {
		this.inputNeuron = input;
		this.outputNeuron = output;
		this.weight = weight;
		this.previousWeight = 0;
	}
	
	/**
	 * Gibt das InputNeuron zurück, von welchem der Output benötigt wird.
	 * 
	 * @return INeuron INputNeuron
	 */
	public final INeuron getInputNeuron() {
		return this.inputNeuron;
	}
	
	/**
	 * Gibt das OutputNeuron zurück.
	 * 
	 * @return INeuron OutputNeuron
	 */
	public final INeuron getOutputNeuron() {
		return this.outputNeuron;
	}
	
	/**
	 * Gibt die aktuelle Gewichtung dieser Synapse zurück.
	 * 
	 * @return double Gewichtung der Synapse
	 */
	public final double getWeight() {
		return weight;
	}
	
	/**
	 * Setzt die Gewichtung dieser Synapse neu.
	 * 
	 * @param weight neue Gewichtung
	 */
	public void setWeight(double weight) {
		this.weight = weight;
	}
	
	/**
	 * 
	 * @return the actual eligibility trace for this synapse
	 */
	public double getEtrace(){
		return this.etrace;
	}
	
	/**
	 * 
	 * @param etrace the eligibility trace to set for this synapse
	 */
	public void setEtrace(double etrace){
		this.etrace = etrace;
	}
	
	/**
	 * Korrigiert die Gewichtung dieser Synapse anhand des übergebenen delta Wertes.
	 * 
	 * Neue Gewichtung = alte Gewichtung + delta
	 * 
	 * delta kann auch negativ sein.
	 * 
	 * @param delta Wert für die Gewichtungsänderung
	 */
	public void adjustWeight(double delta) {
		this.previousWeight = this.weight;
		this.weight = this.weight + delta;
	}
	
	/**
	 * Gibt die jeweils vorletze Gewichtung zurück.
	 * 
	 * @return double previousWeight
	 */
	public double getPreviousWeight() {
		return this.previousWeight;
	}
}
