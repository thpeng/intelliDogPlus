package intelliDOG.ai.optimization.hillclimbing;

import intelliDOG.ai.bots.SimpleBot;
import intelliDOG.ai.optimization.EvaluatorWrapper;
import intelliDOG.ai.optimization.OptimizationManager;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
/**
 * Implementation of the Hill Climbing algorithm
 * @author tpeng
 */
public class HillManager extends OptimizationManager implements Runnable
{
	private EvaluatorWrapper now = null; 
	private EvaluatorWrapper changed = null; 
	private int keyPointer = 0; 
	private int numberOfFail = 0; 
	private int name  = 0; 
	private float stepwidth = 0;
	private HillManager father = null;
	private List<EvaluatorWrapper> resultWrappers = null; 
	private static boolean inverse = false; 
	
	/**
	 * constructor with some initialization
	 */
	public HillManager()
	{
		randomSeed = new Properties();
		super.reset(); 
		randomSeed = super.loadSeed("/ressources/hillclimbing/RandomRestartSeed.properties");
		this.currentBot = SimpleBot.class;
	}
	@Override
	public void begin(int times, float step)
	{
		if(times < 0)
			throw new IllegalArgumentException("no negative number of allowed!");
		if(step<0 || step>1)
			throw new IllegalArgumentException("no stepwidth above 0 (0%) or above 1 (100%) allowed!");
		if(currentBot.getCanonicalName().contains("TwoStep"))
			throw new UnsupportedOperationException("TwoStepBot not allowed for Memetic Algorithm!");
		if(pairMode == true)
			throw new UnsupportedOperationException("PairMode not allowed for Memetic Algorithm!");
		now = randomRestart(); 
		mainLoop(times, step);
	}
	
	@Override
	public void mainLoop(int times, float step)
	{
		wrappers = new LinkedList<EvaluatorWrapper>(); 
		for(int i = 0; i<times; )
		{
			if(now == null)
			{
				now = randomRestart();
			}
			now.reset(); 
			keyPointer =keyPointer%keys.length;
			float tempstep = step; 
			if(numberOfFail /keys.length > 0)
			{	
				if(numberOfFail/keys.length > 1)
				{
					tempstep = tempstep/2; 
					msg.debug(this, "second round: step factor reduced to: "+ tempstep);
				}
			}
			changed = generateNextClimber(now, tempstep);
			List<EvaluatorWrapper> ews = new ArrayList<EvaluatorWrapper>();
			ews.add(now);
			ews.add(changed);
			if(gamePolicy == GP_ALL_VS_ALL || gamePolicy == GP_GROUP_PLAY)
				throw new IllegalArgumentException("Only best of game policies allowed for Hill Climbing!");
			fitness(gamePolicy, ews);
			if(changed.getGames_won_in_this_generation() + now.getGames_won_in_this_generation() < (gamePolicy/2)+1)
			{
				i++; 
				msg.debug(this, "both are bad, can't play games, try next spot, total games valid: " +
						(changed.getGames_won_in_this_generation()+ now.getGames_won_in_this_generation()));
				now = null; 
			}
			else
			{
				int offset = 3; 
				if(changed.getGames_won_in_this_generation() > now.getGames_won_in_this_generation()+offset )
				{
					msg.debug(this, "we have a new winner!");
					now = changed; 
					numberOfFail = 0; 
				}
				else{
					msg.debug(this, "the old is better");
					numberOfFail++; 
					keyPointer++; 
				//else, do nothing, the old is better than the new
		
				}
			}
			//reset
			if(numberOfFail == (keys.length)*2) //search next spot, all attributes were modified.
			{
				msg.debug(this, "all attributes checked, we have a peak and start again");
				wrappers.add(now);
				i++;
			}
			name++;
		}
		numberOfFail = 0; 
		keyPointer = 0; 
	}
	/**
	 * spawns the next wrapper
	 * @param ew current node
	 * @param step the stepwidth
	 * @return a successor wrapper
	 */
	public EvaluatorWrapper generateNextClimber(EvaluatorWrapper ew, float step)
	{
		String key = keys[keyPointer];
		EvaluatorWrapper newEw = ew.copy(); 
		ew.setName(generateBotName());
		newEw.getProperties().setProperty(key, getNextValue(ew.getProperties().getProperty(key), step));
		msg.debug(this, "changing key: "+key+" from: " +ew.getProperties().getProperty(key) + " to: " +newEw.getProperties().getProperty(key));
		return newEw; 
	}
	/**
	 * calculate the next value based on a current and the
	 * stepwidth
	 * @param value the value
	 * @param step the stepwidth
	 * @return a float or integer in string representation
	 */
	public String getNextValue(String value, float step)
	{
		String result = null; ; 
		if(value.contains("."))
		{
			float f = Float.valueOf(value);
			float temp = f; 
			f = f + (f*step);
			if(f == temp)
			{
				if(step>0)
				{
					msg.debug(this, "min value float:  +0.01");
					f+=0.01f;
				}
				else
				{
					msg.debug(this, "min value float:  -0.01");
					f-=0.01f;
				}
			}
			if(temp <0)
			{
				msg.debug(this, "float value below 0, set 0");
				temp = 0; 
			}
			result = Float.toString(f);
		}
		else
		{
			int temp = Integer.valueOf(value);
			int temp2 = temp; 
			temp = temp + (int) (temp*step);
			
			if(temp == temp2)
			{
				if(step>0)
				{
					msg.debug(this, "min value int:  +1");
					temp+=1; 
				}
				else
				{
					msg.debug(this, "min value int:  -1");
					temp-=1;
				}
			}
			if(temp< 0)
			{
				msg.debug(this, "int value below 0, set 0");
				temp = 0;
			}
			result = Integer.toString(temp);
		}
		return result; 
	}
	/**
	 * random restart algorithm
	 * @return a new start wrapper
	 */
	public EvaluatorWrapper randomRestart()
	{
		keyPointer =0;
		numberOfFail = 0; 
		EvaluatorWrapper ew = new EvaluatorWrapper();
		Properties result = new Properties();
		for(String key : ew.getAttributes())
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
		ew.load(result);
		ew.setName(generateBotName());
		return ew;
	}
	
	/**
	 * backport for the memetic algorithm
	 * @param step the stepwidth
	 * @param ws a list of wrappers which should be improved
	 * @return the improved wrappers
	 */
	public List<EvaluatorWrapper> begin(float step, List<EvaluatorWrapper> ws) 
	{
		if(ws == null || ws.size() == 0)
			throw new IllegalArgumentException("The list is empty or null");
		if(step<0 || step>1)
			throw new IllegalArgumentException("no mutation above 0 (0%) or above 1 (100%) allowed!");
		setDetails(step, ws, gamePolicy, this);
		this.resultWrappers = new LinkedList<EvaluatorWrapper>();
		switch(cpu_policy)
		{
		case CPU_ONE:
			run();
			break;
		case CPU_MULTIPLE:
			int cpu = Runtime.getRuntime().availableProcessors();
			Thread[] threads = new Thread[cpu];
			
			int from = 0;
			for(int i = 0; i< threads.length; i++)
			{
				int to = (i+1)*(ws.size()/cpu);
				HillManager h = new HillManager();
				threads[i] = new Thread(h);
				h.setDetails(step, ws.subList(from, to), gamePolicy, this);
				msg.debug(this,from + " " + to );
				threads[i].start();
				msg.debug(this,threads[i] + " started");
				from = to; 
			}
			for(Thread t : threads)
			{
				try {
					t.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			}
			break; 
			default:
				throw new IllegalArgumentException("CPU Policy "+ cpu_policy+ " not defined");
		}
		return resultWrappers;
	}
	@Override
	public void run() 
	{
		if(inverse)
		{
			runner(); 
			stepwidth = stepwidth*-1;
		}
		runner();
	}
	/**
	 * backport for single thread
	 */
	private void runner()
	{
		for(EvaluatorWrapper ew : wrappers)
		{
			EvaluatorWrapper eval1 = ew.copy(); 
			eval1.setName(generateBotName());
			now = eval1; 
			if(stepwidth>0)
				msg.debug(this, "ascendant! detected");
			else
				msg.debug(this, "descendant! detected");
			mainLoop(1, stepwidth);
			if(now != null && eval1 != now)
			{
				msg.debug(this,"found a better one");
				father.addResultWrapper(now);
			}
			else
			{
				msg.debug(this,"the genetic result is better");
				father.addResultWrapper(eval1);
			}	
				
		}
		
	}
	
	@Override
	protected String generateBotName() {
		return "hillclimber_"+name+"_"+getSim_ID(); 
	}
	/**
	 * set the details on multiple threads
	 */
	public void setDetails(float step, List<EvaluatorWrapper> ws, int gp, HillManager father) 
	{
		this.wrappers = ws;
		this.stepwidth = step;
		this.gamePolicy = gp; 
		this.father = father; 
	}
	/**
	 * add a result on the central instance
	 * @param ew the wrapper
	 */
	public synchronized void addResultWrapper(EvaluatorWrapper ew)
	{
		this.resultWrappers.add(ew);
	}
	/**
	 * enable the inverse hill climbing
	 * @param b
	 */
	public static void setInverse(boolean b)
	{
		inverse = b; 
	}
	@Override
	public void begin(int generations, int population_count, float mutate) {
		throw new UnsupportedOperationException("not applicable");
		
	}
	@Override
	public void begin(int generations, String path, float mutate) {
		throw new UnsupportedOperationException("not applicable");
		
	}
}
