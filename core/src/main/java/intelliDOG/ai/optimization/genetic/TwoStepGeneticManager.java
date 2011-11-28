package intelliDOG.ai.optimization.genetic;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import intelliDOG.ai.bots.TwoStepBot;
import intelliDOG.ai.optimization.EvaluatorWrapper;
/**
 * Implementation of the GeneticManager adapted to the
 * TwoStepBot
 * @author tpeng
 *
 */
public class TwoStepGeneticManager extends GeneticManager
{
	private Properties randomSeedt2; 
	@Override
	protected void breed(EvaluatorWrapper one, EvaluatorWrapper two, List<EvaluatorWrapper> nextGen, float percentage)
	{
		List<EvaluatorWrapper> tier1 = new LinkedList<EvaluatorWrapper>();
		List<EvaluatorWrapper> tier2 = new LinkedList<EvaluatorWrapper>();
		EvaluatorWrapper ew1 = one.getMate();
		EvaluatorWrapper ew2 = two.getMate(); 
		super.breed(one, two, tier1, percentage); 
		super.breed(ew1, ew2, tier2, percentage);
		if(tier1.get(0).getMate() != null || tier1.get(1).getMate() != null || tier2.get(0).getMate() != null 
				||tier2.get(1).getMate() != null)
			throw new IllegalArgumentException("Matename already set: " +tier1.get(0).getMate() +" "
					+ tier1.get(1).getMate()+" "+tier2.get(0).getMate()+" "+tier2.get(1).getMate());
		tier1.get(0).setMate(tier2.get(0));
		tier2.get(0).setMate(tier1.get(0));
		tier1.get(1).setMate(tier2.get(1));
		tier2.get(1).setMate(tier1.get(1));
		for(EvaluatorWrapper ew : tier1)
		{
			nextGen.add(ew);
		}
	}
	
	@Override
	public List<EvaluatorWrapper> loadPropertiesFromDirectory(String path) throws IOException
	{
		List<EvaluatorWrapper> tier1 = super.loadPropertiesFromDirectory(path);
		List<EvaluatorWrapper> tier2 = super.loadPropertiesFromDirectory(path+"/tier2");
		if(tier1.size() != tier2.size())
			throw new IllegalArgumentException("tier 2 must contain the same number of properties!");
		for(EvaluatorWrapper t1 : tier1)
		{
			String matename = t1.getMateName();
			for(EvaluatorWrapper t2 : tier2)
			{
				if(t2.getMateName().contains(matename));
				{
					if(t2.getMate() != null)
						throw new IllegalArgumentException("Mate already set! on: " + t2.getName() + " and mate: "+t2.getMate().getName());
					t1.setMate(t2);
					t2.setMate(t1);
				}
			}
			if(t1.getMate() ==null)
				throw new IllegalArgumentException("no mate given for this candidate: "+ t1.getName());
		}
		return tier1;
		
	}
	
	@Override
	public EvaluatorWrapper makeRandomWrapper(Properties p) 
	{
		EvaluatorWrapper t1 = super.makeRandomWrapper(p);
		EvaluatorWrapper t2 = super.makeRandomWrapper(randomSeedt2);
		t1.setMate(t2);
		t2.setMate(t1);
		return t1; 
	}
	
	@Override
	public void reset()
	{
		super.reset(); 
		currentBot = TwoStepBot.class; 
		randomSeedt2 = loadSeed("ressources/genetic/RandomSeedGenetic.properties");
	}
	
	@Override
	public void dumpAll(String path)
	{
		super.dumpAll(path);
		for(EvaluatorWrapper ew : wrappers)
		{
			File f1 = new File(path+"/tier2"); 
			if(!f1.exists())
			{
				f1.mkdirs(); 
			}
			ew.getMate().dump(path+"/tier2", getGeneration());
		}
	}
}
