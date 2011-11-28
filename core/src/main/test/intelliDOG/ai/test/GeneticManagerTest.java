package intelliDOG.ai.test;
import static org.junit.Assert.*;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Properties;
import java.util.Random;

import intelliDOG.ai.optimization.EvaluatorWrapper;
import intelliDOG.ai.optimization.OptimizationManager;
import intelliDOG.ai.optimization.Statistic;
import intelliDOG.ai.optimization.genetic.GameRunner;
import intelliDOG.ai.optimization.genetic.GeneticManager;
import intelliDOG.ai.utils.DebugMsg;

import org.junit.After;
import org.junit.Before;
import org.junit.Test; 


public class GeneticManagerTest
{
	private OptimizationManager gm = new GeneticManager();
	File f = new File("testseed");
	File f1 = new File("testoutput");
	
	@Before 
	public void setup() throws Exception
	{
		deleteDir(f);
		deleteDir(f1);
		gm = new GeneticManager();
		gm.reset(); 
		f = new File("testseed");
		f1 = new File("testoutput");
		f.mkdir();
		f1.mkdir(); 
	}
	
	@After
	public void tearDown() throws Exception
	{
		deleteDir(f);
		deleteDir(f1);
	}
	
	@Test
	public void testSortGeneration() throws Exception
	{
		List<EvaluatorWrapper> list = new LinkedList<EvaluatorWrapper>();
		Random r = new Random(); 
		for(int i = 0; i<10; i++)
			list.add(new EvaluatorWrapper());
		for(EvaluatorWrapper eval : list)
		{
			eval.setGames_won_in_this_generation(r.nextInt(100));
//			System.out.print(eval.getGames_won_in_this_generation() + ", ");
			
		}
//		System.out.println("\n=============================");
		gm.sortGeneration(list);
		for(EvaluatorWrapper eval : list)
		{
//			System.out.print(eval.getGames_won_in_this_generation() + ", ");
			
		}
//		System.out.println("\n=============================");
//		System.out.println(list.get(9).getGames_won_in_this_generation());
	}
	@Test
	public void testGroupPlay()
	{
		List<EvaluatorWrapper> ew = new LinkedList<EvaluatorWrapper>();
		for(int i = 0; i<12;i++)
		{
			ew.add(new EvaluatorWrapper());
		}
		EvaluatorWrapper[][] result = gm.doGroupPlay(4, ew);
		assertEquals(36, result.length);
		assertEquals(ew.get(0), result[0][0]);
		assertEquals(ew.get(0), result[1][0]);
		assertEquals(ew.get(0), result[2][0]);
		assertEquals(ew.get(0), result[5][1]);
		assertEquals(ew.get(0), result[7][1]);
		
		
	}
	@Test
	public void testBestOf()
	{
		List<EvaluatorWrapper> ew = new LinkedList<EvaluatorWrapper>();
		for(int i = 0; i<12;i++)
		{
			ew.add(new EvaluatorWrapper());
		}
		EvaluatorWrapper[][] result = gm.doBestOf(OptimizationManager.GP_BEST_OF_SEVEN, ew);
		assertEquals(42, result.length);
		assertEquals(ew.get(0), result[0][0]);
		assertEquals(ew.get(0), result[1][0]);
		assertEquals(ew.get(0), result[2][0]);
		assertEquals(ew.get(0), result[3][0]);
		assertEquals(ew.get(0), result[4][0]);
		assertEquals(ew.get(0), result[5][0]);
		assertEquals(ew.get(0), result[6][0]);
		assertEquals(ew.get(3), result[7][1]);
		assertEquals(ew.get(3), result[8][1]);
		assertEquals(ew.get(3), result[9][1]);
		assertEquals(ew.get(3), result[10][1]);
		assertEquals(ew.get(3), result[11][1]);
		assertEquals(ew.get(3), result[12][1]);
		assertEquals(ew.get(3), result[13][1]);
		
	}
	@Test
	public void testBehaviour()
	{
		OptimizationManager gm = new GeneticManager();
		try
		{
			gm.begin(2, 0.5f);
			fail();
		}
		catch(Exception e)
		{
			//expected
		}
		try
		{
			gm.begin(-1, 12,0.5f);
			fail();
		}
		catch(Exception e)
		{
			//expected
		}
		try
		{
			gm.begin(1,-12 ,0.5f);
			fail();
		}
		catch(Exception e)
		{
			//expected
		}
		try
		{
			gm.begin(1,77, 0.5f);
			fail();
		}
		catch(Exception e)
		{
			//expected
		}
		try
		{
			gm.begin(1, 12, -0.5f);
			fail();
		}
		catch(Exception e)
		{
			//expected
		}
		try
		{
			gm.begin(1, 3,0.5f);
			fail();
		}
		catch(Exception e)
		{
			//expected
		}
	}
	@Test 
	public void testPropertiesHandling()
	{
		List<EvaluatorWrapper> ew = new LinkedList<EvaluatorWrapper>();
		for(int i = 0; i<5; i++)
			ew.add(gm.makeRandomWrapper(gm.loadSeed("ressources/genetic/RandomSeedGenetic.properties")));
		gm.setWrappers(ew);
		gm.dumpAll("testseed");
		assertEquals(5, f.list().length);
		gm.setWrappers(new LinkedList<EvaluatorWrapper>());
		List<EvaluatorWrapper> wrappers = null; 
		try
		{
			wrappers = gm.loadPropertiesFromDirectory("testseed");
		}
		catch(Exception e)
		{
			e.printStackTrace(); 
			fail(); 
			
		}
		assertEquals(5, wrappers.size());
		deleteDir(f);
		deleteDir(f1);
	}
	@Test
	public void testCompleteFramework()
	{
		
		gm.setGamePolicy(OptimizationManager.GP_BEST_OF_FIVE);
		gm.begin(1, 20, 0.1f);
		gm.dumpAll("testseed");
		assertEquals(20, gm.getWrappers().size());
		assertEquals(1, gm.getGeneration() );
		gm.begin(2, "testseed", 0.2f);
		gm.dumpAll("testoutput");
		assertEquals(20, gm.getWrappers().size());
		assertEquals(2, gm.getGeneration() );
		
	}
	@Test
	public void testCompleteFrameworkWithMCPU()
	{
 
		gm.setCPUPolicy(OptimizationManager.CPU_MULTIPLE);
		gm.setGamePolicy(OptimizationManager.GP_BEST_OF_SEVEN);
		gm.begin(1, 20, 0.1f);
		gm.dumpAll("testseed");
		assertEquals(20, gm.getWrappers().size());
		assertEquals(1, gm.getGeneration() );
		gm.setGamePolicy(OptimizationManager.GP_GROUP_PLAY);
		gm.begin(2, "testseed", 0.2f);
		gm.dumpAll("testoutput");
		assertEquals(20, gm.getWrappers().size());
		assertEquals(2, gm.getGeneration() );
		
	}
	@Test
	public void testNumberOfGames()
	{
 
		gm.setCPUPolicy(OptimizationManager.CPU_MULTIPLE);
		gm.setGamePolicy(OptimizationManager.GP_BEST_OF_FIVE);
		gm.begin(0, 20, 0.1f);
		EvaluatorWrapper[] ews = new EvaluatorWrapper[20];
		gm.getWrappers().toArray(ews);
		gm.setGamePolicy(OptimizationManager.GP_GROUP_PLAY);
		gm.begin(1, 0.2f);
		int numberOfGames = 0; 
		int numbersWon = 0;
		for(EvaluatorWrapper ew : ews)
		{
			numberOfGames = numberOfGames +ew.getGames_played();
			numbersWon = numbersWon +ew.getGames_won_in_this_generation();
		}
//		numberOfGames = numberOfGames/2; 
		assertEquals(360, numberOfGames);
//		assertEquals(180, numbersWon); //not defined because of the "draw" option
		
		
	}
	@Test
	public void testGetBest()
	{
//		DebugMsg.getInstance().addItemForWhiteList(gm);
//		DebugMsg.getInstance().addItemForWhiteList(new GameRunner());
		gm.setCPUPolicy(OptimizationManager.CPU_MULTIPLE);
		List<EvaluatorWrapper>ews = new LinkedList<EvaluatorWrapper>(); 
		for(int i = 0; i<12; i++)
		{
			EvaluatorWrapper ew = gm.makeRandomWrapper(gm.loadSeed("ressources/genetic/RandomSeedGenetic.properties"));
			ews.add(ew);
			
		}
		gm.setWrappers(ews);
		gm.getBest();
		ListIterator<EvaluatorWrapper> i =  gm.getWrappers().listIterator();
		EvaluatorWrapper before = null; 
		while(i.hasNext())
		{
			EvaluatorWrapper now = i.next(); 
			if(before!= null)
			{
				assertTrue(now.getGames_won_in_this_generation()>=before.getGames_won_in_this_generation());
			}
			before = now; 
		}
		int games = 0; 
		for(EvaluatorWrapper ew : ews )
		{
			games += ew.getGames_played();
		}
		assertEquals(12*11*2, games);
		
//		testGetBest(); 
	}
	@Test
	public void testGenerations()
	{
		gm.setGamePolicy(OptimizationManager.GP_BEST_OF_FIVE);
		gm.begin(1, 20, 0.1f);
		EvaluatorWrapper[] ew = new EvaluatorWrapper[20];
		gm.getWrappers().toArray(ew);
		gm.setGamePolicy(OptimizationManager.GP_BEST_OF_FIVE);
		gm.begin(1, 0.1f);
		EvaluatorWrapper[] result = new EvaluatorWrapper[20];
		gm.getWrappers().toArray(result);
		int counter = 0; 
		for(int i = 0; i<20; i++)
		{
			for(int j = 0; j<20; j++)
			{
				if(ew[i].getName() == result[j].getName())
					counter++;
			}
		}
		assertEquals(10, counter);
		
	}
	@Test
	public void testGenerations2()
	{
		gm.setGamePolicy(OptimizationManager.GP_BEST_OF_FIVE);
		gm.begin(1, 20, 0.1f);
		EvaluatorWrapper[] ew = new EvaluatorWrapper[20];
		
		gm.getWrappers().toArray(ew);
		gm.setGamePolicy(OptimizationManager.GP_BEST_OF_FIVE);
		gm.begin(1, 0.1f);
		EvaluatorWrapper[] result = new EvaluatorWrapper[20];
		gm.sortGeneration(gm.getWrappers());
		gm.getWrappers().subList(10, 20).toArray(result);
		int counter = 0; 
		for(int i = 0; i<20; i++)
		{
			for(int j = 0; j<10; j++)
			{
				if(ew[i].getName() == result[j].getName())
					counter++;
			}
		}
		assertEquals(10, counter);
		
	}
	
	@Test
	public void testStatistic()
	{
		Statistic s = new Statistic("teststat.txt", gm);
		gm.statisticOn(s);
		Properties p = new Properties(); 
		Properties p2 = new Properties();
		for(String key : new EvaluatorWrapper().getAttributes())
		{
			p.setProperty(key, ""+ 10); 
			p2.setProperty(key,""+ 20);
		}
		List<EvaluatorWrapper> ews = new LinkedList<EvaluatorWrapper>();
		EvaluatorWrapper ew = new EvaluatorWrapper(); 
		EvaluatorWrapper ew2 = new EvaluatorWrapper(); 
		ew.load(p);
		ew2.load(p2);
		ews.add(ew);
		ews.add(ew2);
		float[][] record = s.record(ews);
		assertEquals(new EvaluatorWrapper().getAttributes().length, record.length);
		assertEquals(3, record[0].length);
		assertEquals(15, record[0][2]);
		assertEquals(10, record[0][0]);
		assertEquals(20, record[0][1]);
		s.cleanUp(); 
		f.delete();
	}
	
	/**
	 * helper klasse aus dem internet http://www.javafaq.nu/java-example-code-119.html
	 * @param dir
	 * @return
	 */
	private static boolean deleteDir(File dir) {

        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so now it can be smoked
        return dir.delete();
	}
	
}
