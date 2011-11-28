package intelliDOG.ai.optimization.memetic;

import java.util.LinkedList;
import java.util.List;

import intelliDOG.ai.optimization.EvaluatorWrapper;
import intelliDOG.ai.optimization.Statistic;
import intelliDOG.ai.optimization.genetic.GeneticManager;
import intelliDOG.ai.optimization.hillclimbing.HillManager;
/**
 * The implementation of the Memetic Manager
 * @author tpeng
 */
public class MemeticManager extends GeneticManager
{
	private int geneticRounds = 200; 
	@Override
	public void mainLoop(int generations, float mutate)
	{
		if(currentBot.getCanonicalName().contains("TwoStep"))
			throw new UnsupportedOperationException("TwoStepBot not allowed for Memetic Algorithm!");
		if(pairMode == true)
			throw new UnsupportedOperationException("PairMode not allowed for Memetic Algorithm!");
		HillManager hm = new HillManager(); 
		hm.setGamePolicy(gamePolicy);
		HillManager.setInverse(true);
		if(cpu_policy == CPU_MULTIPLE)
		{
			hm.setCPUPolicy(CPU_MULTIPLE);
		}

		for(int i = 0; i<generations; i++)
		{
			msg.debug(this,"entering genetic part");
			super.mainLoop(geneticRounds, mutate);
			List<EvaluatorWrapper> newWrappers = new LinkedList<EvaluatorWrapper>(); 
			msg.debug(this,"entering hillclimbing");

			newWrappers = hm.begin(0.1f, wrappers); 
			msg.debug(this,"clean up, make fitness, sort");
			for(EvaluatorWrapper ew : wrappers)
			{
				ew.reset(); 
			}
			fitness(GP_ALL_VS_ALL, newWrappers); 
			sortGeneration(newWrappers);
			msg.debug(this,"length of wrappers before cleanout: "+ newWrappers.size());
			newWrappers.subList(0, (newWrappers.size()/2)).clear();
			msg.debug(this,"length of wrappers before cleanout: "+ newWrappers.size());
			wrappers = newWrappers;
			if(!stats.isEmpty())
			{
				for(Statistic s : stats)
				{
					s.record(wrappers);
				}
			}
		}
	}
	/**
	 * sets the rounds of genetic simulations
	 * @param i rounds
	 */
	public void setGeneticRounds(int i)
	{
		geneticRounds = i; 
	}
}
