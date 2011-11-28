package intelliDOG.ai.learning.ann.net;

import intelliDOG.ai.learning.ann.net.exceptions.NeuralNetException;

public class TDTeacher extends ATeacher {

	private int numberOfInputValues;
	private double td;
	private double outputErr;
	private double gamma;
	private double lambda;
	private double alphaDecrease;
	
	
	public TDTeacher(double learningRate, double momentum, int learningEpochs, int numberOfInputValues, double gamma, double lambda, double alphaDecrease) {
		super(learningRate, momentum, learningEpochs);
		this.numberOfInputValues = numberOfInputValues;
		this.gamma = gamma;
		this.lambda = lambda;
		this.alphaDecrease = alphaDecrease;
	}

	public void startTraining(NeuralNet net, double td, double outputErr) throws NeuralNetException {
		this.td = td;
		this.outputErr = outputErr;
		super.startTraining(net);
	}
	
	public double decreaseAlpha(){
		super.setLearningRate(super.getLearningRate() * (1 - this.alphaDecrease));
		return super.getLearningRate();
	}
	
	@Override
	protected double doEpochTraining(NeuralNet net) throws NeuralNetException {
		//if learning is on
		if(this.isTraining()) {
			//backpropagate this error
			net.backpropagate(this.td, outputErr, gamma, lambda, Constants.BACKPROP_TD_LAMBDA);
		}
		//return absolute error
		return Math.abs(this.outputErr);
	}

	@Override
	public int getNumberOfInputValues() {
		return this.numberOfInputValues;
	}

	@Override
	public int getNumberOfOutputValues() {
		return 1;
	}

}
