package intelliDOG.ai.optimization.genetic;


import intelliDOG.ai.bots.SimpleBot;
import intelliDOG.ai.optimization.EvaluatorWrapper;
import intelliDOG.ai.optimization.OptimizationManager;
import intelliDOG.ai.optimization.Statistic;
import intelliDOG.ai.optimization.memetic.MemeticManager;

import java.io.IOException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * Genetic Algorithm implementation. 
 * @author tpeng
 *
 */
public class GeneticManager extends OptimizationManager
{
	@Override
	public void begin(int generations, float mutate)
	{
		reset(); 
		if(generations < 0)
			throw new IllegalArgumentException("no negative number of allowed!");
		if(mutate<0 || mutate>1)
			throw new IllegalArgumentException("no mutation above 0 (0%) or above 1 (100%) allowed!");
		if(wrappers.size()%4 != 0 || wrappers.size() == 0)
			throw new IllegalArgumentException("the size of the population must be a multiple of 4, detected: "+ wrappers.size());
		mainLoop(generations, mutate);
	}
	
	@Override
	public void begin(int generations, int population_count, float mutate)
	{
		reset();
		wrappers = new LinkedList<EvaluatorWrapper>();
		for(int i = 0; i<population_count; i++)
		{
			wrappers.add(makeRandomWrapper(randomSeed));
		}
		begin(generations, mutate);
	}
	
	@Override
	public void begin(int generations, String path, float mutate)
	{
		wrappers = new LinkedList<EvaluatorWrapper>();
		try
		{
			wrappers = loadPropertiesFromDirectory(path);
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
		begin(generations, mutate);
	}
	@Override
	public  void mainLoop(int generations, float mutate)
	{
		
		for(;generation<generations; generation++)
		{
			List<EvaluatorWrapper> nextGen = new LinkedList<EvaluatorWrapper>();
			for(EvaluatorWrapper ew : wrappers)
			{
				ew.reset(); 
			}
			if(generation%5 ==0)
			{
				msg.debug(this,"The population will be shaked");
				wrappers = shakeList(wrappers);
			}

			fitness(gamePolicy, wrappers); 

			sortGeneration(wrappers);
			switch (selection)
			{
			case SELECTION_TRUNCATE:
				wrappers = truncate(wrappers);
				break;
			case SELECTION_ROULETTE:
				wrappers = roulette(wrappers);
				break; 
			case SELECTION_DISCREPANCY:
				wrappers = discrepancy(wrappers); 
				break; 
			case SELECTION_SIMILARITY:
				wrappers = similarity(wrappers); 
				break; 
			default:
				throw new IllegalArgumentException("the breeding policy " + selection + " is not defined!");
			}
			if(!stats.isEmpty() && !(this instanceof MemeticManager))
			{
				for(Statistic s : stats)
				{
					s.record(wrappers);
				}
			}
			if(generation%40==0 &&(this instanceof MemeticManager))
			{
				for(Statistic s : stats)
				{
					s.record(wrappers);
				}
			}
			for(int i = 0; i< wrappers.size(); i = i+2)
			{
				breed(wrappers.get(i), wrappers.get(i+1), nextGen, mutate);
			}
			wrappers = nextGen;
			nextGen = null; 
		}
	}
	/**
	 * breeding mechanism for the genetic algorithm
	 * @param one father
	 * @param two mother
	 * @param nextGen next population
	 * @param percentage mutation factor
	 */
	protected void breed(EvaluatorWrapper one, EvaluatorWrapper two, List<EvaluatorWrapper> nextGen, float percentage)
	{
		EvaluatorWrapper[] children = new EvaluatorWrapper[]{new EvaluatorWrapper(),new EvaluatorWrapper()};
		for(EvaluatorWrapper child : children)
		{
			Properties pchild = copy(one.getProperties(), two.getProperties());
			if(isMutate(percentage))
			{
				int length = pchild.size();
				

				for(int j = 0; j< deepMutation; j++)
				{
					int index = r.nextInt(length);
					Enumeration<?> keys = pchild.keys();
					String key = "";
					for(int i = 0; i<=index; i++)
					{
						key = (String) keys.nextElement();
					}
					EvaluatorWrapper ew = makeRandomWrapper(randomSeed);
					String result = ew.getProperties().getProperty(key);
					msg.debug(this,"mutated: " +key + " before: "+pchild.getProperty(key) + " after: "+ result );
					pchild.setProperty(key, result);
					keys = null; 
				}
			}
			
			pchild.setProperty("name", generateBotName());
			pchild.setProperty("generation_born",  Integer.toString(getGeneration()));
			pchild.setProperty("parents", one.getName()+ "_"+ two.getName());
			child.load(pchild);
			nextGen.add(child);
		}
		nextGen.add(one);
		nextGen.add(two);
	}
	
	@Override
	public void reset()
	{
		super.reset(); 
		currentBot = SimpleBot.class; 
		randomSeed = loadSeed("/ressources/genetic/RandomSeedGenetic.properties");
	}
	
	
	
	
}
