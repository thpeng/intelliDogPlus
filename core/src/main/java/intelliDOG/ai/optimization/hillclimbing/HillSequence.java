package intelliDOG.ai.optimization.hillclimbing;

import intelliDOG.ai.optimization.OptimizationManager;
import intelliDOG.ai.optimization.OptimizationSequence;
import intelliDOG.ai.utils.DebugMsg;
/**
 * squences for random restart Hill Climbing simulations
 * @author tpeng
 *
 */
public class HillSequence extends OptimizationSequence {

	private HillManager hm = new HillManager(); 
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		HillSequence gs = new HillSequence(); 
		if(args.length == 0)
		{
			gs.runStoredProcedure(); 
		}
		else
		{
			gs.callMethodByName(gs, args);
		}
	}
	@Override
	public void runStoredProcedure() {
		// TODO Auto-generated method stub
		sequenceOne(); 
	}
	/**
	 * standard random restart test
	 */
	public void sequenceOne()
	{
		DebugMsg.getInstance().addItemForWhiteList(hm);
		hm.setGamePolicy(OptimizationManager.GP_BEST_OF_ELEVEN);
		hm.begin(1000, 0.2f);
	}

}
