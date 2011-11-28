package intelliDOG.ai.learning.ann;

import java.util.Properties;

import org.joone.engine.FullSynapse;
import org.joone.engine.LinearLayer;
import org.joone.engine.Monitor;
import org.joone.engine.NeuralNetEvent;
import org.joone.engine.NeuralNetListener;
import org.joone.engine.SigmoidLayer;
import org.joone.engine.learning.TeachingSynapse;
import org.joone.net.NeuralNet;

import intelliDOG.ai.learning.rl.State;

public class JooneAnn implements Ann, NeuralNetListener {

	Monitor monitor;
	NeuralNet nnet;
	
	
	private void initNeuralNet() {
		LinearLayer input = new LinearLayer();
		SigmoidLayer hidden = new SigmoidLayer();
		SigmoidLayer output = new SigmoidLayer();
		
		input.setRows(87);
		hidden.setRows(44);
		output.setRows(1);
		
		FullSynapse synapse_IH = new FullSynapse(); /* Input  -> Hidden conn. */
		FullSynapse synapse_HO = new FullSynapse(); /* Hidden -> Output conn. */
		
		input.addOutputSynapse(synapse_IH);
		hidden.addInputSynapse(synapse_IH);
		
		hidden.addOutputSynapse(synapse_HO);
		output.addInputSynapse(synapse_HO);
		
		nnet = new NeuralNet();
		nnet.addLayer(input, NeuralNet.INPUT_LAYER);
		nnet.addLayer(hidden, NeuralNet.HIDDEN_LAYER);
		nnet.addLayer(output, NeuralNet.OUTPUT_LAYER);

		monitor = nnet.getMonitor();
		monitor.setLearningRate(1); //The learning rate is included in our calculations!
		monitor.setMomentum(0); //We don't use a momentum
		
		monitor.addNeuralNetListener(this);
		
		monitor.addLearner(0, "intelliDOG.ai.learning.ann.ErrorLearner");
		monitor.setLearningMode(0); //TODO: test if this works!
		
		TeachingSynapse trainer = new TeachingSynapse();
		//create an input synapse for the trainer!
		//trainer.setDesired(samples);
		
		nnet.setTeacher(trainer);
		output.addOutputSynapse(trainer);
		
		monitor.setTrainingPatterns(1); /* # of rows contained in the input data */
		monitor.setTotCicles(1); /* How many times the net must be trained on the input patterns */
		monitor.setLearning(true); /* The net must be trained */
		
	}
	
	@Override
	public double getValue(State s) {
		//TODO implement!
		monitor.setLearning(false);
		//set input
		//nnet.go()
		
		//get output!
		
		return 0;
	}

	@Override
	public void updateWeights(double td, double outpErr) {
		// TODO Auto-generated method stub
		//add teacher
		
		//learn

	}
	
	@Override
	public Properties getProperties() {
		throw new UnsupportedOperationException("not yet implemented!");
	}

	@Override
	public void cicleTerminated(NeuralNetEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void errorChanged(NeuralNetEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void netStarted(NeuralNetEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void netStopped(NeuralNetEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void netStoppedError(NeuralNetEvent arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void load(String fileName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void save(String fileName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setProperties(Properties p) {
		// TODO Auto-generated method stub
		
	}


}
