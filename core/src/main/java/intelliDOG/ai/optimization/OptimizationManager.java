package intelliDOG.ai.optimization;


import intelliDOG.ai.optimization.genetic.GameRunner;
import intelliDOG.ai.utils.DebugMsg;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Properties;
import java.util.Random;
/**
 * The OptimizationManager contains several utility methods
 * and some core algorithms for a optimization sequence. The
 * concrete implementation must extend it.
 * @author tpeng
 *
 */
public abstract class OptimizationManager {

	protected static final String DATE_FORMAT_NOW = "yyyy.MM.dd-HH.mm";
	
	//game policies
	public static final int GP_GROUP_PLAY = 1;
	public static final int GP_BEST_OF_FIVE = 5;
	public static final int GP_BEST_OF_SEVEN = 7;
	public static final int GP_BEST_OF_NINE = 9;
	public static final int GP_BEST_OF_ELEVEN = 11;
	public static final int GP_ALL_VS_ALL = Integer.MAX_VALUE;
	protected int gamePolicy = GP_BEST_OF_NINE;
	
	//cpu policies
	public static final int CPU_ONE = 101;
	public static final int CPU_MULTIPLE = 102;
	protected int cpu_policy = CPU_ONE;
	
	//selection policies
	public static final int SELECTION_TRUNCATE = 201; 
	public static final int SELECTION_ROULETTE = 202;
	public static final int SELECTION_DISCREPANCY = 203;
	public static final int SELECTION_SIMILARITY = 204;
	protected int selection = SELECTION_TRUNCATE; 
	
	//instance variables
	protected List<EvaluatorWrapper> wrappers = new LinkedList<EvaluatorWrapper>();
	protected int generation = 0;
	protected DebugMsg msg = DebugMsg.getInstance();
	protected String[] keys = new EvaluatorWrapper().getAttributes();
	protected String sim_id = "";
	private int bot_id = 0;
	protected Random r = new Random();
	
	//modifiable by user
	protected List<Statistic> stats = new LinkedList<Statistic>();
	protected Properties randomSeed = new Properties();
	protected Class<?> currentBot = null;
	protected boolean pairMode = false; 
	protected int groupSize = 10; 
	protected int deepMutation = 1; 

	/**
	 * mainloop must be implemented by every simulation
	 * @param generations how many generations
	 * @param mutate mutation factor
	 */
	public abstract void mainLoop(int generations, float mutate);
	/**
	 * only begin methods should be called by clients
	 * @param generations how many generations
	 * @param population_count population size
	 * @param mutate mutation factor
	 */
	public abstract void begin(int generations, int population_count, float mutate);
	/**
	 * only begin methods should be called by clients
	 * @param generations how many generations
	 * @param path population folder with properties
	 * @param mutate mutation factor
	 */
	public abstract void begin(int generations, String path, float mutate);
	
	/**
	 * only begin methods should be called by clients
	 * @param generations how many generations
	 * @param mutate mutation factor
	 */
	public abstract void begin(int generations, float mutate);
	
	/** 
	 * dump the current generation
	 * @param path the output folder
	 */
	public void dumpAll(String path) 
	{	
		File f = new File(path);
		if(!f.exists())
			f.mkdirs();
		for(EvaluatorWrapper wrapper : wrappers)
		{
			wrapper.dump(path, generation);
		}
	}
	/**
	 * generate a new random wrapper
	 * @param p the properties to load
	 * @return a new wrapper
	 */
	public EvaluatorWrapper makeRandomWrapper(Properties p) {
		EvaluatorWrapper ew = new EvaluatorWrapper();
		Properties result = new Properties();
		for(String key : keys)
		{
			String value = randomSeed.getProperty(key);
			if(value.contains("."))
			{
				float max = Float.valueOf(value);
				int i = (int) max; 
				int res = r.nextInt(i);
				result.setProperty(key,Float.toString(r.nextFloat()+ res));
			}
			else
			{
				int max = Integer.valueOf(value);
				result.setProperty(key, Integer.toString(r.nextInt(max)));
				
			}
		}
		result.setProperty("name", generateBotName());
		result.setProperty("generation_born",  Integer.toString(getGeneration()));
		result.setProperty("parents", "null_null");
		ew.load(result); 
		return ew;
	}

	/**
	 * loads all properties from a folder
	 * @param path the folder to load
	 * @return a new generation
	 * @throws IOException in case the io did not succeed
	 */
	public List<EvaluatorWrapper> loadPropertiesFromDirectory(String path) throws IOException {
		File dir = new File(path);
		List<EvaluatorWrapper> ews = new LinkedList<EvaluatorWrapper>(); 
	
		String[] children = dir.list();
		if (children == null) {
			// Either dir does not exist or is not a directory
		} 
		else 
		{
			for (int i=0; i<children.length; i++) 
			{
				// Get filename of file or directory
				String filename = children[i];
				if(filename.contains(".properties"))
				{
					Properties p = new Properties();
					java.io.FileInputStream fis = new java.io.FileInputStream(new java.io.File(path + "/" + filename));
					p.load(fis);
					p.setProperty("generation_born", Integer.toString(0));
					EvaluatorWrapper ew = new EvaluatorWrapper();
					ew.load(p);
					ews.add(ew);
					fis.close(); 
				}
			}		
			msg.debug(this,children.length + " properties found and loaded");
		}
		return ews; 
	}

	/**
	 * sorts the current generation with the comparable 
	 * interface
	 * @param ew a list of wrappers
	 */
	public void sortGeneration(List<EvaluatorWrapper> ew) {
		java.util.Collections.sort(ew);
	}

	/**
	 * randomizes the list to prevent early convergence
	 * @param ew the generation
	 * @return same generation, but shaked
	 */
	public List<EvaluatorWrapper> shakeList(List<EvaluatorWrapper> ew) {
		List<EvaluatorWrapper> result = new LinkedList<EvaluatorWrapper>();
		
		while(!ew.isEmpty())
		{
			int index = 0; 
			if(ew.size() !=1)
			{
				index = r.nextInt(ew.size()-1);
			}
			result.add(ew.get(index));
			ew.remove(index);	
		}
		return result;
	}

	/**
	 * chooses randomly between two properties. needed for mutation.
	 * @param one properties one
	 * @param two properties two
	 * @return the chosen properties
	 */
	protected Properties choose(Properties one, Properties two) {
		if(r.nextBoolean())
		{
			return one;
		}
		else
		{
			return two; 
		}
	}

	/**
	 * generates a identifier for a bot
	 * @return the identifier
	 */
	protected String generateBotName() {
		String result = sim_id + "-" + bot_id;
		bot_id++;
		return result;
	}

	/**
	 * generates a 2d array with the game policy group play
	 * @param groupsize the group play size
	 * @param list the population
	 * @return gamepairs
	 */
	public EvaluatorWrapper[][] doGroupPlay(int groupsize, List<EvaluatorWrapper> list) {
		if(list.size()%groupsize != 0) 
			throw new IllegalArgumentException("the populationsize is not a factor of the groupsize!");
		EvaluatorWrapper[][] result = new EvaluatorWrapper[list.size()*(groupsize-1)][2];
		
		for(int j = 0; j< list.size(); j = j +groupsize)//mache gruppen für die gegebene grösse groupsize
		{
			List<EvaluatorWrapper> temp = list.subList(j, j+groupsize);
			for(int i = 0; i< groupsize; i++)//für jede instanz in der liste
			{
				for(int x = 1; x<temp.size(); x++)//offset++
				{
					int index = (i+x)%(groupsize);
					result[(j*(groupsize-1))+(i*(groupsize-1))+x-1][0] = temp.get(i);
					result[(j*(groupsize-1))+(i*(groupsize-1))+x-1][1] = temp.get(index);
				}
			}
		}
		msg.debug(this,"generated " + result.length + " gamepairs with groupPlay and size: " +groupsize);
		return result; 
	}

	/**
	 * generates a 2d array for all best of game policies
	 * @param bestof which best of policy
	 * @param list the population
	 * @return gamepairs
	 */
	public EvaluatorWrapper[][] doBestOf(int bestof, List<EvaluatorWrapper> list) {
		assert list.size()%2 == 0;
		EvaluatorWrapper[][] result = new EvaluatorWrapper[(list.size()*bestof)/2][2];
		ListIterator<EvaluatorWrapper> it = list.listIterator();
		int indexResult = 0;
		while(it.hasNext())
		{
			EvaluatorWrapper p1 = it.next();
			EvaluatorWrapper p2 = it.next();
			for(int i = 0; i< bestof; i++ )
			{
				result[indexResult][0] = p1;
				result[indexResult][1] = p2; 
				indexResult++;
			}
		}
		msg.debug(this,"generated " + result.length + " gamepairs and bestOf size: "+ bestof);
		it = null; 
		return result; 
	}
	/**
	 * does a all vs all on the current population and names
	 * them accordingly to their strength
	 */
	public void getBest() 
	{
		for(EvaluatorWrapper ew : wrappers)
		{
			ew.reset(); 
		}
		fitness(GP_ALL_VS_ALL, wrappers);
		sortGeneration(wrappers);
		for(int i = 0; i< wrappers.size(); i++)
		{
			EvaluatorWrapper ew = wrappers.get(wrappers.size()-1-i);
			ew.getProperties().setProperty("old_name", ew.getName());
			ew.setName("rank"+ (i<999 ? "0" : "")+(i<99 ? "0" : "")+(i<9 ? "0" : "")+(i+1));
		}
	}

	/**
	 * get the current generation
	 * @return the generation
	 */
	public List<EvaluatorWrapper> getWrappers() {
		return this.wrappers; 
	}
	/**
	 * evaluates the fitness of the given population
	 * @param gamePolicy applied
	 * @param population current generation
	 */
	public void fitness(int gamePolicy, List<EvaluatorWrapper> population) {
		EvaluatorWrapper[][] gamePairs = null;
		int winsize = 0; 
		switch(gamePolicy)
		{
		case GP_GROUP_PLAY:
			gamePairs = doGroupPlay(groupSize, population); 
			winsize = population.size(); 
			break;
		case GP_BEST_OF_FIVE:
			gamePairs = doBestOf(GP_BEST_OF_FIVE, population);
			winsize = 3; 
			break;
		case GP_BEST_OF_SEVEN:
			gamePairs = doBestOf(GP_BEST_OF_SEVEN, population);
			winsize = 4; 
			break;
		case GP_BEST_OF_NINE:
			gamePairs = doBestOf(GP_BEST_OF_NINE, population);
			winsize = 5; 
			break;
		case GP_BEST_OF_ELEVEN:
			gamePairs = doBestOf(GP_BEST_OF_ELEVEN, population);
			winsize = 6; 
			break;
		case GP_ALL_VS_ALL:
			gamePairs = doGroupPlay(population.size(), population);
			winsize = population.size()*population.size()-1; 
			break; 
		default:
			throw new UnsupportedOperationException("GamePolicy not defined!");
		}
		int cpu = 0;
		switch(cpu_policy)
		{
		 
		case CPU_ONE:
			msg.debug(this, "ONE_CPU_POLICY chosen");
			cpu = 1; 
			GameRunner gr = new GameRunner(); 
			gr.setDetails(gamePairs, winsize,gamePolicy, 0, gamePairs.length, currentBot, pairMode);
			gr.run(); 
			break; 
		case CPU_MULTIPLE:
			cpu = Runtime.getRuntime().availableProcessors();
			msg.debug(this, cpu + "CPU detected and Multiple CPU option chosen");
			GameRunner[] grs =new GameRunner[cpu];
			Thread[] t =new Thread[cpu];
			int from = 0;
			int to = 0; 
			for(int i = 0; i< cpu; i++)
			{
				
				to = (i+1)*(gamePairs.length/cpu); 
				if(gamePolicy != GP_ALL_VS_ALL && gamePolicy != GP_GROUP_PLAY)
				{
					int temp = to/gamePolicy;
					to = temp * gamePolicy;
					if(i == cpu-1)
					{
						to = gamePairs.length; 
					}
				}
				msg.debug(this,"gamepairs from"+ from + " to: "+ to);
				grs[i] = new GameRunner(); 
				grs[i].setDetails(gamePairs, winsize,gamePolicy, from, to, currentBot, pairMode);
				t[i] = new Thread(grs[i]);
				t[i].start();
				from = to; 
			}
			for(int i = 0; i< cpu; i++)
			{
				try {
					t[i].join();
					msg.debug(this, i + "'th thread joined");
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			break; 
		default: 
			throw new IllegalArgumentException("CPU Policy "+ cpu+ " is not defined!");
		}
	}
	/**
	 * reset the manager
	 */
	public void reset()
	{
		generation = 0; 
		Calendar cal = Calendar.getInstance();
	    SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
	    sim_id = sdf.format(cal.getTime());
	}

	/**
	 * get all keys of the wrappers evaluator
	 * @return
	 */
	public String[] getKeys() 
	{
		return keys;
	}
	/**
	 * set the deepmutation. 1 = no deepmutation
	 * @param i how many times mutation should be applied
	 */
	public void setDeepMutation(int i)
	{
		if(i> keys.length || i<0)
		{
			throw new IllegalArgumentException("Do not set a mutation rate higher than" + keys.length + " and lower than 0");
		}
		deepMutation = i; 
	}
	/**
	 * sets the game policy
	 * @param x game policy which should be applied
	 */
	public void setGamePolicy(int x)
	{
		gamePolicy = x; 
	}
	/**
	 * truncate algorithm for selection
	 * @param ews the population
	 * @return population which can breed
	 */
	public List<EvaluatorWrapper> truncate(List<EvaluatorWrapper>ews)
	{
		msg.debug(this, "truncate breeding policy applied");
		ews.subList(0, wrappers.size()/2).clear();
		return ews; 
	}
	
	/**
	 * roulette algorithm for selection
	 * @param ews the population
	 * @return population which can breed
	 */
	public List<EvaluatorWrapper> roulette(List<EvaluatorWrapper>ews)
	{
		msg.debug(this,"roulette breeding policy applied");
		List<EvaluatorWrapper> res = new LinkedList<EvaluatorWrapper>();
		int targetLength = ews.size()/2;
		int index = ews.size()-1; 
		while(targetLength > res.size())
		{
			if(index<0)
			{
				index = ews.size()-1; 
			}
			int random = r.nextInt(targetLength*2);
			if(index >= random)
			{
				res.add(ews.get(index));
				ews.remove(index);
			}
			index--; 
		}
		return res; 
	}
	/**
	 * discrepancy algorithm for selection
	 * @param ews the population
	 * @return population which can breed
	 */
	public List<EvaluatorWrapper> discrepancy(List<EvaluatorWrapper>ews)
	{
		return simdisc(ews, 0);
	}
	
	/**
	 * similarity algorithm for selection
	 * @param ews the population
	 * @return population which can breed
	 */
	public List<EvaluatorWrapper> similarity(List<EvaluatorWrapper>ews)
	{
		return simdisc(ews, Float.MAX_VALUE);
	}
	/**
	 * support algorithm for selection with discrepancy
	 * and similarity
	 * @param ews the population
	 * @param revValue reference value
	 * @return population which can breed
	 */
	public List<EvaluatorWrapper> simdisc(List<EvaluatorWrapper>ews, float revValue)
	{
		float highestVal = revValue;
		int highestValpos = 0; 
		List<EvaluatorWrapper>res = new LinkedList<EvaluatorWrapper>();
		EvaluatorWrapper highest = null; 
		int limit = ews.size()/4; 
		for(int i = 0; i< limit; i++)
		{
			EvaluatorWrapper ew = ews.get(ews.size()-1);
			ews.remove(ews.size()-1);
			for(int j = ews.size()-1; j>=0; j--)
			{
				float temp = deviance(ew.getProperties(), ews.get(j).getProperties());
				msg.debug(this,"targetpos " + j +" bewertung: "+ temp);
				if(temp>highestVal && revValue == 0)
				{
					highestVal = temp;
					highestValpos = j; 
					highest = ews.get(j);
				}
				if(temp<highestVal && revValue == Float.MAX_VALUE)
				{
					highestVal = temp;
					highestValpos = j; 
					highest = ews.get(j);
				}
			}
			res.add(ew);
			res.add(highest);
			ews.remove(highestValpos);
			highest = null; 
			highestVal = revValue; 
		}
		return res; 
	}
	
	/**
	 * measures the deviance between two candidates
	 * @param a first properties
	 * @param b second properties
	 * @return a float value representing the deviance.
	 */
	protected float deviance(Properties a, Properties b)
	{
		assert a!=null; assert b!=null; 
		float result = 0; 
		for(String key : keys)
		{
			float f1 = Float.parseFloat(a.getProperty(key));
			float f2 = Float.parseFloat(b.getProperty(key));
			float reverence = f1+f2; 
			float difference = Math.abs(f1-f2); 
			if(reverence != 0)
				result += difference/reverence;
		}
		assert result>0; 
		return result; 
	}
	/**
	 * gives a boolean back if the candidate should be 
	 * mutated
	 * @param percentage how often mutation should be applied
	 * @return true or false
	 */
	protected boolean isMutate(float percentage)
	{
		if(percentage == 0.0f)
		{
			return false; 
		}
		int maxMutate = (int)(100 * percentage);
		if(r.nextInt(100)<=  maxMutate)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	/**
	 * load the seed properties from a given folder
	 * @param path the folder
	 * @return the properties
	 */
	public Properties loadSeed(String path)
	{
		Properties p = new Properties(); 
		try {
			InputStream in = getClass().getResourceAsStream(path);
			p.load(in);
			in.close(); 
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException("RandomSeed not found at: " +path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return p; 
	}
	/**
	 * merge randomly two properties. used for breeding
	 * @param one father
	 * @param two mother
	 * @return child
	 */
	protected Properties copy(Properties one, Properties two) {
		
		Properties result = new Properties();
		String[] attributes = new EvaluatorWrapper().getAttributes(); 
		for(String key : attributes)
		{
			result.setProperty(key, choose(one, two).getProperty(key));
		}
		return result;
	}
	/**
	 * @return the generation
	 */
	public int getGeneration() {
		return generation;
	}
	/**
	 * @return the selection policy
	 */
	public void setSelectionPolicy(int x)
	{
		selection = x; 
	}
	/**
	 * @param x set the group size in group play game policy
	 */
	public void setGroupSize(int x)
	{
		if(x < 2)
			throw new IllegalArgumentException("groupsize below 2 not allowed!");
		groupSize = x; 
	}
	/**
	 * @param x set the cpu policy
	 */
	public void setCPUPolicy(int x) {
		this.cpu_policy = x; 
	}
	/**
	 * @param ew set the population
	 */
	public void setWrappers(List<EvaluatorWrapper> ew) {
		this.wrappers = ew; 
	}
	/**
	 * enable the statistic
	 * @param s the statistic
	 */
	public void statisticOn(Statistic s) {
		stats.add(s);
	}
	/**
	 * get the simulation identifier
	 * @return the identifier
	 */
	public String getSim_ID() {
		return sim_id;
	}
}
