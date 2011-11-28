package intelliDOG.ai.learning.ann;

import org.joone.engine.extenders.DeltaRuleExtender;

public class ErrorDeltaRuleExtender extends DeltaRuleExtender {

	@Override
	public double getDelta(double[] currentGradientOuts, int j, double aPreviousDelta) {
		return getLearner().getMonitor().getMomentum();
	}

	@Override
	public double getDelta(double[] currentInps, int j, double[] currentPattern, int k, double aPreviousDelta) {
		//TODO: find a better way for doing this
		//for now we use the momentum (as we don't need it) for getting the actual error!
		return getLearner().getMonitor().getMomentum();
	}

	@Override
	public void postBiasUpdate(double[] arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void postWeightUpdate(double[] arg0, double[] arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void preBiasUpdate(double[] arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void preWeightUpdate(double[] arg0, double[] arg1) {
		// TODO Auto-generated method stub

	}

}