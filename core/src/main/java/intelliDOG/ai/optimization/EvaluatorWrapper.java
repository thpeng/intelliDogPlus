package intelliDOG.ai.optimization;

import intelliDOG.ai.evaluators.Evaluator;
import intelliDOG.ai.evaluators.SimpleEvaluatorV5;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
/**
 * wrapper class for the SEV5
 * @author tpeng
 *
 */
public class EvaluatorWrapper implements Comparable<EvaluatorWrapper>, Evaluator
{
	private String name = "";
	private String parents = "";
	private int generation_born = 0;
	private int generation_died = 0;
	private int games_played = 0;
	private int games_won = 0;
	private int games_won_in_this_generation;
	private EvaluatorWrapper mate; 
	private String mateName; 
	
	private SimpleEvaluatorV5 instance = null;
	private Properties props = null; 

	/**
	 * normal constructor
	 */
	public EvaluatorWrapper()
	{
		instance = new SimpleEvaluatorV5();
	}
	/**
	 * load additionally the given properties
	 * @param p the properties
	 */
	public EvaluatorWrapper(Properties p)
	{
		instance = new SimpleEvaluatorV5(); 
		load(p);
	}
	/**
	 * loads the evaluator weights form a given property
	 * @param p the properties 
	 */
	public void load(Properties p)
	{
		name =  p.getProperty("name");
		parents = p.getProperty("parents");
		if(parents == null)
		{
			parents = "null_null";
		}
		if(p.getProperty("generation_born") != null)
			generation_born = Integer.parseInt(p.getProperty("generation_born"));
		if(p.getProperty("generation_died") != null)
			generation_died = Integer.parseInt(p.getProperty("generation_died"));
		if(p.getProperty("games_played") != null)
			games_played = Integer.parseInt(p.getProperty("games_played"));
		if(p.getProperty("games_won") != null)
			games_won = Integer.parseInt(p.getProperty("games_won"));
		if(p.getProperty("mate") != null)
			mateName = p.getProperty("mate");
		instance.loadFromProperties(p);
		props = p;
		
	}
	
	/**
	 * dump the current values into a folder given by 
	 * the path
	 * @param path the folder
	 * @param generation the current generation.
	 */
	public void dump(String path, int generation)
	{
		props.setProperty("name", name);
		if(mate != null)
			props.setProperty("mate", mateName);
		props.setProperty("parents", parents);
		props.setProperty("generation_born", Integer.toString(generation_born));
		props.setProperty("generation_died", Integer.toString(generation));
		props.setProperty("games_played", Integer.toString(games_played));
		props.setProperty("games_won", Integer.toString(games_won));
		props.setProperty("games_won_in_this_generation", Integer.toString(games_won_in_this_generation));
		try {
			FileOutputStream fos = new FileOutputStream(path+"/" +name+".properties"); 
			props.store(fos, null);
			fos.close(); 
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/**
	 * @return the identifier
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the parents identifier as string
	 */
	public String getParents() {
		return parents;
	}

	/**
	 * @return in which generation the evaluator was born
	 */
	public int getGeneration_born() {
		return generation_born;
	}

	/**
	 * @return in which generation the evaluator died
	 */
	public int getGeneration_died() {
		return generation_died;
	}

	/**
	 * @return how many games were played
	 */
	public int getGames_played() {
		return games_played;
	}
	/**
	 * call when a game with this evaluator was played
	 */
	public synchronized void played()
	{
		games_played = games_played+1;
	}
	/**
	 * call this when a game was won with this evaluator
	 */
	public synchronized void won()
	{
		this.games_won = games_won +1; 
		this.games_won_in_this_generation = games_won_in_this_generation+1;
	}

	/**
	 * @return the amount of games won
	 */
	public int getGames_won() {
		return games_won;
	}
	/**
	 * reset this evaluator
	 */
	public void reset()
	{
		games_won_in_this_generation = 0; 
	}
	/**
	 * @return the amount of games won in this generation
	 */
	public int getGames_won_in_this_generation() {
		return games_won_in_this_generation;
	}
	/**
	 * method to test some functions
	 * @param x  amount of games won.
	 */
	public void setGames_won_in_this_generation(int x)
	{
		this.games_won_in_this_generation = x; 
	}

	@Override
	public int compareTo(EvaluatorWrapper o) {
		int temp = this.getGames_won_in_this_generation() - o.getGames_won_in_this_generation();
		if(temp<0)
			return -1;
		if(temp>0)
			return 1;
		//default if its the same
		return 0;
	}
	/**
	 * @return the current properties
	 */
	public Properties getProperties()
	{
		return props; 
	}
	/**
	 * @param name set the name
	 */
	public void setName(String name)
	{
		this.name = name; 
	}
	@Override
	public int evaluate(byte[] actualState, byte[] targetState, byte player) {
		return instance.evaluate(actualState, targetState, player);
	}
	@Override
	public int evaluate(byte[] actualState, byte[] targetState, byte player,
			int[] card) {
		return instance.evaluate(actualState, targetState, player, card);
	}

	@Override
	public int evaluate(byte[] targetState, byte player) {
		return instance.evaluate(targetState, player);
	}

	@Override
	public int evaluate(byte[] targetState, byte player, int[] card,
			float fading) {
		return instance.evaluate(targetState, player, card, fading);
	}
	/**
	 * get all attribute as keys
	 * @return the keys
	 */
	public String[] getAttributes()
	{
		return instance.getAttributes();
	}
	
	/**
	 * copy constructor for various purposes
	 * @return a similar wrapper
	 */
	public EvaluatorWrapper copy()
	{
		EvaluatorWrapper ew = new EvaluatorWrapper();
		Properties newprops = new Properties();
		for(String key : instance.getAttributes())
		{
			newprops.setProperty(key, props.getProperty(key));
		}
		newprops.setProperty("name", "descOf_"+getName());
		newprops.setProperty("parents", getName());
		ew.load(newprops); 
		return ew; 
			
		
	}
	/**
	 * method needed for twostep and pairs algorithm
	 * @return the mate
	 */
	public EvaluatorWrapper getMate() {
		return mate;
	}
	/**
	 * set the players running mate
	 * @param mate the mate
	 */
	public void setMate(EvaluatorWrapper mate) {
		this.mate = mate;
		this.mateName = mate.getName(); 
	}
	/**
	 * get the identifier of the mate
	 * @return a string
	 */
	public String getMateName()
	{
		return mateName; 
	}
}
