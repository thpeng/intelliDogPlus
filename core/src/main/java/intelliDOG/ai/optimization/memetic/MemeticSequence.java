package intelliDOG.ai.optimization.memetic;

import intelliDOG.ai.optimization.OptimizationManager;
import intelliDOG.ai.optimization.OptimizationSequence;
import intelliDOG.ai.optimization.Statistic;
import intelliDOG.ai.optimization.genetic.GeneticManager;
import intelliDOG.ai.utils.DebugMsg;

import java.io.File;
/**
 * sequences for memetic simulations
 * @author tpeng
 *
 */
public class MemeticSequence extends OptimizationSequence {

	/**
	 * main method
	 * @param args
	 */
	public static void main(String[] args)
	{
		MemeticSequence gs = new MemeticSequence(); 
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
//		gs.sequenceOne();
		sequenceTwo();
//		gs.sequenceOne();
//		gs.longRunner(); 
//		gs.test2entities_both_prop();
//		gs.test2entities();
	}
	
	/**
	 * long running memetic test with statistic
	 */
	public  void sequenceOne()
	{
//		DebugMsg.getInstance().addItemForWhiteList();
//		 DebugMsg.getInstance().addItemForWhiteList(SimpleBot.class.getCanonicalName());
		MemeticManager gm = new MemeticManager(); 
		DebugMsg.getInstance().addItemForWhiteList(gm);
		gm.setCPUPolicy(OptimizationManager.CPU_MULTIPLE);
		Statistic s = new Statistic("output_seedone.txt", gm); 
		gm.statisticOn(s);
		gm.setGamePolicy(OptimizationManager.GP_BEST_OF_NINE);
		gm.begin(1,100, 0.4f );
		gm.setDeepMutation(2);
		for(int i = 0; i<100;i++)
		{
			gm.begin(1, 0.5f );
			File f = new File("output_memetic"+i); 
			if(!f.exists())
			{
				f.mkdir();	
			}
			gm.dumpAll("output_memetic"+i);
		}
		
		
	}
	/**
	 * compares some aspects from the Genetic to the Memetic
	 */
	public void sequenceTwo()
	{
		
		GeneticManager gm = new GeneticManager(); 
		gm.setCPUPolicy(OptimizationManager.CPU_MULTIPLE);
		Statistic gs1 = new Statistic("output/gmstat1.txt",gm);
		gs1.setInterval(1);
		Statistic gs10 = new Statistic("output/gmstat10.txt",gm);
		gs10.setInterval(10);
		Statistic gs25 = new Statistic("output/gmstat25.txt",gm);
		gs25.setInterval(25);
		gm.statisticOn(gs1);
		gm.statisticOn(gs10);
		gm.statisticOn(gs25);
		gm.setDeepMutation(2);
		gm.begin(1000,100, 0.4f);
		
		MemeticManager mm = new MemeticManager();
		mm.setCPUPolicy(OptimizationManager.CPU_MULTIPLE);
		Statistic ms1 = new Statistic("output/mmstat1.txt",mm);
		ms1.setInterval(1);
		mm.statisticOn(ms1);
		mm.begin(1000,60, 0.4f);

	}
}
