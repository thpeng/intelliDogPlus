package intelliDOG.ai.learning.ann;

import org.joone.engine.ExtendableLearner;
import org.joone.engine.extenders.OnlineModeExtender;

public class ErrorLearner extends ExtendableLearner{
	
	private static final long serialVersionUID = -639585781736875252L;

	public ErrorLearner(){
		setUpdateWeightExtender(new OnlineModeExtender());
		//TODO add the 'normal' gradient delta rule extender!
		addDeltaRuleExtender(new ErrorDeltaRuleExtender());
	}
	
}
