package intelliDOG.ai.test;

import org.junit.*;
import static org.junit.Assert.*;

import intelliDOG.ai.optimization.EvaluatorWrapper;
import intelliDOG.ai.optimization.OptimizationManager;
import intelliDOG.ai.optimization.memetic.MemeticManager;

public class MemeticManagerTest
{
	private MemeticManager mm = new MemeticManager();
	
	@Before
	public void setup()
	{
		mm = new MemeticManager(); 
	}
	
	@Test
	public void testRunner()
	{
		mm.setGeneticRounds(0);
		mm.setGamePolicy(OptimizationManager.GP_BEST_OF_FIVE);
		mm.begin(1, 2, 0.1f);
		for(int i = 0; i<5; i++)
		{
			assertEquals(2, mm.getWrappers().size());
			for(EvaluatorWrapper ew : mm.getWrappers())
			{
				System.out.println(ew.getName());
			}
			mm.setGamePolicy(OptimizationManager.GP_BEST_OF_FIVE);
			mm.begin(1, 0.1f);
		}
	}
}
