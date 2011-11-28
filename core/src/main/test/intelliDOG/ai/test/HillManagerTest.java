package intelliDOG.ai.test;

import java.util.Properties;

import org.junit.*;

import intelliDOG.ai.optimization.EvaluatorWrapper;
import intelliDOG.ai.optimization.hillclimbing.HillManager;
import static org.junit.Assert.*;

public class HillManagerTest 
{
	private HillManager instance = null; 
	
	@Before
	public void setup()
	{
		instance = new HillManager(); 
	}
	
	@Test
	public void testGetNextValue()
	{
		assertEquals("0.11", instance.getNextValue("0.1", 0.1f));
		assertEquals("6", instance.getNextValue("5", 0.1f));
		assertEquals("0.01", instance.getNextValue("0.0", 0.1f));
		assertEquals("110", instance.getNextValue("100", 0.1f));
		assertEquals("1", instance.getNextValue("0", 0.1f));
	}
	
	@Test
	public void testGenerateNextClimber()
	{
		String[] keys = instance.getKeys();
		Properties p = new Properties();
		for(String key : keys)
		{
			p.setProperty(key, "100");
		}
		EvaluatorWrapper ew = new EvaluatorWrapper();
		ew.load(p);
		ew = instance.generateNextClimber(ew, 0.1f);
		p = ew.getProperties();
		assertEquals("110", p.getProperty(keys[0]));
		for(int i = 1; i<keys.length; i++)
		{
			assertEquals("100", p.getProperty(keys[i]));
		}
		
	}
}
