package intelliDOG.ai.optimization.genetic;

import intelliDOG.ai.bots.IBot;
import intelliDOG.ai.bots.SimpleBot;
import intelliDOG.ai.evaluators.SimpleEvaluatorV5;
import intelliDOG.ai.framework.BotBoard;
import intelliDOG.ai.framework.Game;
import intelliDOG.ai.framework.InformationGatherer;
import intelliDOG.ai.framework.Players;
import intelliDOG.ai.optimization.EvaluatorWrapper;
import intelliDOG.ai.optimization.OptimizationManager;
import intelliDOG.ai.optimization.OptimizationSequence;
import intelliDOG.ai.optimization.Statistic;
import intelliDOG.ai.utils.DebugMsg;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class GeneticSequence extends OptimizationSequence 
{
	/** 
	 * main methods
	 * @param args commandline argument
	 */
	public static void main(String[] args)
	{
		GeneticSequence gs = new GeneticSequence(); 
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
	public void runStoredProcedure()
	{
			sequenceOne();
//		gs.sequenceFive();
//		gs.sequenceOne();
//		gs.longRunner(); 
//		test2entities_both_prop();
//		test2entities_both_prop();
		//testMutation(); 
//		
//		runPairGenetic(); 
//		runTwoStepGenetic();
//		sequencePairsTwo(); 
	}
	
	/**
	 * calls a standard simulation with 300 new random candidates
	 */
	public  void sequenceOne()
	{
//		DebugMsg.getInstance().addItemForWhiteList(new BotBoard(null, null));
//		 DebugMsg.getInstance().addItemForWhiteList(SimpleBot.class.getCanonicalName());
		OptimizationManager gm = new GeneticManager(); 
		Statistic s = new Statistic("output_seedone.txt", gm); 
		gm.statisticOn(s);
		gm.setGamePolicy(OptimizationManager.GP_BEST_OF_SEVEN);
		gm.begin(5, 300, 0.1f);
		
		File f = new File("seed_two"); 
		if(!f.exists())
		{
			f.mkdir();	
		}
		gm.dumpAll("seed_two");
		
	}
	/**
	 * load from folder, store in folder
	 */
	public  void sequenceTwo()
	{
		
		File f = new File("seed_two"); 
		if(!f.exists())
		{
			f.mkdir();
		}
//		DebugMsg.getInstance().addItemForWhiteList(new BotBoard(null, null));
//		 DebugMsg.getInstance().addItemForWhiteList(SimpleBot.class.getCanonicalName());
		OptimizationManager gm = new GeneticManager(); 
		gm.setGamePolicy(OptimizationManager.GP_BEST_OF_FIVE);
		gm.begin(1, "seed_one", 0.1f);
		gm.dumpAll("seed_two");
		
	}
	/**
	 * load from folder, store in folder
	 */
	public  void sequenceThree()
	{
		
		File f = new File("seed_three"); 
		if(!f.exists())
		{
			f.mkdir();
		}
//		DebugMsg.getInstance().addItemForWhiteList(new BotBoard(null, null));
//		 DebugMsg.getInstance().addItemForWhiteList(SimpleBot.class.getCanonicalName());
		OptimizationManager gm = new GeneticManager(); 
		gm.setGamePolicy(OptimizationManager.GP_BEST_OF_SEVEN);
		gm.begin(5, "seed_two", 0.1f);
		gm.dumpAll("seed_three");
		
	}
	/**
	 * simulation with multi processor support
	 */
	public  void sequenceFour()
	{
			
			File f = new File("output"); 
			if(!f.exists())
			{
				f.mkdir();
			}
//			DebugMsg.getInstance().addItemForWhiteList(new BotBoard(null, null));
			 DebugMsg.getInstance().addItemForWhiteList(GameRunner.class.getCanonicalName());
			OptimizationManager gm = new GeneticManager(); 
			DebugMsg.getInstance().addItemForWhiteList(gm);
			gm.setCPUPolicy(OptimizationManager.CPU_MULTIPLE);
			gm.setGamePolicy(OptimizationManager.GP_BEST_OF_SEVEN);
			gm.begin(5, 200, 0.1f);
			gm.dumpAll("output");
	}
	/**
	 * simulation with getBest() call
	 */
	public  void sequenceFive()
	{
		
		File f = new File("results1210"); 
		if(!f.exists())
		{
			f.mkdir();
		}
//		DebugMsg.getInstance().addItemForWhiteList(new BotBoard(null, null));
//		 DebugMsg.getInstance().addItemForWhiteList(GameRunner.class.getCanonicalName());
		OptimizationManager gm = new GeneticManager(); 
//		DebugMsg.getInstance().addItemForWhiteList(gm);
		//gm.setCPUPolicy(GeneticManager.MULTIPLE_CPU_POLICY);
		gm.setGamePolicy(OptimizationManager.GP_BEST_OF_NINE);
		gm.begin(0, "longrunner/round41", 0.1f);

		gm.getBest();
		gm.dumpAll("results1210");
		
	}
	/**
	 * runs a few thousands simulations
	 */
	public void longRunner()
	{
		OptimizationManager gm = new GeneticManager(); 
		gm.setGamePolicy(OptimizationManager.GP_BEST_OF_SEVEN);
		gm.begin(1, 400, 0.2f);
		gm.statisticOn(new Statistic("longrunner/longrunner.txt", gm));
		for(int i=0; i<1000; i++)
		{
			File f = new File("longrunner/round" + i);
			f.mkdir();
			gm.dumpAll("longrunner/round"+i);
			gm.setGamePolicy(OptimizationManager.GP_BEST_OF_SEVEN);
			gm.begin(50, 0.5f);
			
		}
	}
	/**
	 * compares a evaluator vs the SEV5
	 * @param file
	 */
	public void test2entities(String file) 
	{
		try{
			File f = new File(file);
			FileInputStream is = new FileInputStream(f); 
			Properties p = new Properties();
			p.load(is);
			InformationGatherer ig1= new InformationGatherer(Players.P1);
			InformationGatherer ig2= new InformationGatherer(Players.P2);
			InformationGatherer ig3= new InformationGatherer(Players.P3);
			InformationGatherer ig4= new InformationGatherer(Players.P4);
			IBot[] bots = {new SimpleBot(new BotBoard(new byte[80], ig1), ig1),
					new SimpleBot(new BotBoard(new byte[80], ig2), ig2),
					new SimpleBot(new BotBoard(new byte[80], ig3), ig3),
					new SimpleBot(new BotBoard(new byte[80], ig4), ig4)};
			int firstPlayerwon = 0; 
			int secondPlayerwon = 0; 
			EvaluatorWrapper ew = new EvaluatorWrapper();
			EvaluatorWrapper ew2 = new EvaluatorWrapper();
			ew.load(p);
			ew2.load(p);
			Game g = new Game(bots, null, null, 0l);
			g.setMaxTurns(2000);
			for(int i =0; i<1000; i++)
			{
				bots[0].setEvaluator(ew);
				bots[1].setEvaluator(new SimpleEvaluatorV5());
				bots[2].setEvaluator(ew2);
				bots[3].setEvaluator(new SimpleEvaluatorV5());

				g.run(); 
				int won = g.hasWon();
				if(won == 1)
				{
					firstPlayerwon++; 
				}
				if(won==2)
				{
					secondPlayerwon++; 
				}
				g.reset(); 
			}
			System.out.println(f.getPath() +" won: "+ firstPlayerwon+" old sev5 won: "+ secondPlayerwon);
		}catch(Exception e)
		{
			e.printStackTrace(); 
		}
	}
	/**
	 * sequence for some tests
	 */
	public void test2entities_both_prop()
	{
		testHelper("/home/tpeng/temp_bachelor/peng/results0311m1/rank0001.properties",
				"/home/tpeng/temp_bachelor/peng/results0311m2/rank0001.properties");
		testHelper("/home/tpeng/temp_bachelor/peng/results0311m2/rank0001.properties",
		"/home/tpeng/temp_bachelor/peng/results0311m3/rank0001.properties");
		testHelper("/home/tpeng/temp_bachelor/peng/results0311m3/rank0001.properties",
		"/home/tpeng/temp_bachelor/peng/results0311m1/rank0001.properties");
		test2entities("/home/tpeng/temp_bachelor/peng/results0311m1/rank0001.properties");
		test2entities("/home/tpeng/temp_bachelor/peng/results0311m2/rank0001.properties");
		test2entities("/home/tpeng/temp_bachelor/peng/results0311m3/rank0001.properties");
	}
	/**
	 * tests two entities vs each other
	 * @param s1 first
	 * @param s2 second
	 */
	private void testHelper(String s1, String s2) 
	{
		try{
			File f = new File(s1);
			FileInputStream is = new FileInputStream(f); 
			Properties p = new Properties();
			p.load(is);
			
			File f2 = new File(s2);
			FileInputStream is2 = new FileInputStream(f2); 
			Properties p2 = new Properties();
			p2.load(is2);

			InformationGatherer ig1= new InformationGatherer(Players.P1);
			InformationGatherer ig2= new InformationGatherer(Players.P2);
			InformationGatherer ig3= new InformationGatherer(Players.P3);
			InformationGatherer ig4= new InformationGatherer(Players.P4);
			IBot[] bots = {new SimpleBot(new BotBoard(new byte[80], ig1), ig1),
					new SimpleBot(new BotBoard(new byte[80], ig2), ig2),
					new SimpleBot(new BotBoard(new byte[80], ig3), ig3),
					new SimpleBot(new BotBoard(new byte[80], ig4), ig4)};
			int firstPlayerwon = 0; 
			int secondPlayerwon = 0; 
			EvaluatorWrapper ew = new EvaluatorWrapper();
			EvaluatorWrapper ew2 = new EvaluatorWrapper();
			EvaluatorWrapper ew3 = new EvaluatorWrapper();
			EvaluatorWrapper ew4 = new EvaluatorWrapper();

			ew.load(p);
			ew2.load(p);
			ew3.load(p2);
			ew4.load(p2);
			Game g = new Game(bots, null, null, 0l);
			g.setMaxTurns(2000);
			for(int i =0; i<1000; i++)
			{
				if(i<500)
				{
					bots[0].setEvaluator(ew);
					bots[1].setEvaluator(ew3);
					bots[2].setEvaluator(ew2);
					bots[3].setEvaluator(ew4);
				}
				else
				{
					bots[0].setEvaluator(ew3);
					bots[1].setEvaluator(ew);
					bots[2].setEvaluator(ew4);
					bots[3].setEvaluator(ew2);
				}
				g.run(); 
				int won = g.hasWon();
				if(i<500)
				{
					if(won == 1)
					{
						firstPlayerwon++; 
					}
					if(won==2)
					{
						secondPlayerwon++; 
					}
				}
				else
				{
					if(won == 1)
					{
						secondPlayerwon++; 
					}
					if(won==2)
					{
						firstPlayerwon++; 
					}
				}
				g.reset(); 
			}
			System.out.println(f.getPath() + " evaluator won: "+ firstPlayerwon + " " + f2.getPath() + " evaluator won: "+ secondPlayerwon);
		}catch(Exception e)
		{
			e.printStackTrace(); 
		}
	}
	/**
	 * makes a new seed generation
	 */
	public void makeSeed()
	{
		OptimizationManager gm = new GeneticManager();
		gm.setGamePolicy(OptimizationManager.GP_BEST_OF_FIVE);
		gm.begin(0,300, 0.0f);
		gm.dumpAll("/ressources/genetic/ReferenceSeed");
		
	}
	/**
	 * tests all kinds of mutation settings
	 */
	public void testMutation()
	{
		OptimizationManager gm = new GeneticManager(); 
		float mutation = 0.0f;
		String[] paths = new String[11];
		for(int i = 0; i<11; i++)
		{
			String temp = "test/mutation0"+i+"f";
			File f = new File(temp);
			f.mkdir(); 
			gm = new GeneticManager(); 
			gm.setCPUPolicy(OptimizationManager.CPU_MULTIPLE);
			gm.setGamePolicy(OptimizationManager.GP_BEST_OF_NINE);
			gm.begin(500, "/ressources/genetic/ReferenceSeed", mutation);
			gm.getBest(); 
			gm.dumpAll(temp);
			mutation = mutation +0.1f;
			paths[i] = temp + "/"+ "rank0001.properties";
		}
		for(int i = 0;i<11; i++)
		{
			String output = "test/mutation0"+i+"f";
			paths[i] = output + "/"+ "rank0001.properties";
		}
		for(int i = 0; i<11; i++)
		{
			for(int j = i+1; j<11; j++)
			{
				testHelper(paths[i], paths[j]);
			}
		}
		
		
	}
	/** 
	 * tests all kind of game policies
	 */
	public void testPolicy()
	{
		OptimizationManager gm = new GeneticManager(); 
		String[] paths = new String[11];
		int[] gamePolicies = new int[]{1,5,7,9,11,Integer.MAX_VALUE};
		for(int i = 0; i<6; i++)
		{
			String output = "test/Policy"+gamePolicies[i];
			File f = new File(output);
			f.mkdir(); 
			gm = new GeneticManager(); 
			gm.setCPUPolicy(OptimizationManager.CPU_MULTIPLE);
			gm.setGamePolicy(gamePolicies[i]);
			gm.begin(500, "/ressources/genetic/ReferenceSeed", 0.3f);
			gm.getBest(); 
			gm.dumpAll(output);
			paths[i] = output + "/"+ "rank0001.properties";
		}
		for(int i = 0; i<6; i++)
		{
			for(int j = i+1; j<6; j++)
			{
				testHelper(paths[i], paths[j]);
			}
		}
	}
	/**
	 * tests the influence of the generation increment
	 */
	public void testGenerations()
	{
		OptimizationManager gm = new GeneticManager(); 
		String[] paths = new String[11];
		int population = 100; 
		for(int i = 0; i<10; i++)
		{
			String output = "test/Population"+population;
			File f = new File(output);
			f.mkdir(); 
			gm = new GeneticManager(); 
			gm.setCPUPolicy(OptimizationManager.CPU_MULTIPLE);
			gm.begin(population, "/ressources/genetic/ReferenceSeed", 0.3f);
			gm.getBest(); 
			gm.dumpAll(output);
			paths[i] = output + "/"+ "rank0001.properties";
			population+=100; 
		}
		for(int i = 0; i<10; i++)
		{
			for(int j = i+1; j<10; j++)
			{
				testHelper(paths[i], paths[j]);
			}
		}
	}
	
	/**
	 * tests the impact of the population size compared 
	 * to the SEV5
	 */
	public void testPopulation()
	{
		OptimizationManager gm = new GeneticManager();
		gm.begin(0,300, 0.3f);
		gm.setCPUPolicy(OptimizationManager.CPU_MULTIPLE);
		String formerBest = null; 
		for(int i = 0; i<200; i++)
		{
			String path = "test/population"+i;
			File f = new File(path);
			f.mkdir();
			gm.begin(10, 0.3f);
			gm.getBest(); 
			gm.dumpAll(path);
			String temp = path+"/rank0001.properties";
			if(formerBest != null)
			{
				testHelper(temp, formerBest);
			}
			formerBest = temp; 
			
		}
	}
	/**
	 * test the impact of the population size vs each other
	 */
	public void testPopulation2()
	{
		OptimizationManager gm = new GeneticManager();
		gm.begin(0,300, 0.3f);
		gm.setCPUPolicy(OptimizationManager.CPU_MULTIPLE);
		for(int i = 0; i<200; i++)
		{
			String path = "test/population"+i;
			File f = new File(path);
			f.mkdir();
			gm.begin(10, 0.3f);
			gm.getBest(); 
			gm.dumpAll(path);
			String temp = path+"/rank0001.properties";
			test2entities(temp);
		}
	}
	
	/** 
	 * tests the deep mutation settings
	 */
	public void testDeepMutation()
	{
		OptimizationManager gm = new GeneticManager(); 
		int deepmutation = 2;
		String[] paths = new String[5];
		for(int i = 0; i<3; i++)
		{
			String temp = "test/deepmutation"+deepmutation;
			File f = new File(temp);
			f.mkdir(); 
			gm = new GeneticManager(); 
			gm.setCPUPolicy(OptimizationManager.CPU_MULTIPLE);
			gm.setDeepMutation(deepmutation);
			gm.setGamePolicy(OptimizationManager.GP_BEST_OF_NINE);
			gm.begin(500, "/ressources/genetic/ReferenceSeed", 0.7f);
			gm.getBest(); 
			gm.dumpAll(temp);
			paths[i] = temp + "/"+ "rank0001.properties";
			deepmutation++;
		}
//		for(int i = 1; i<5; i++)
//		{
//			String temp = "test/deepmutation"+deepmutation;
//			paths[i] = temp + "/"+ "rank0001.properties";
//			deepmutation+=5;
//		}
		paths[3] = "test/mutation07f/rank0001.properties";
		paths[4] = "test/deepmutation5/rank0001.properties";
		for(int i = 0; i<5; i++)
		{
			for(int j = i+1; j<5; j++)
			{
				testHelper(paths[j], paths[i]);			}
		}	
		
	}
	
	/**
	 * Tests all kinds of selection policies
	 */
	public void testSelectionPolicies()
	{
		OptimizationManager gm = new GeneticManager(); 
		String[] paths = new String[4];
		int[] gamePolicies = new int[]{OptimizationManager.SELECTION_ROULETTE,
				OptimizationManager.SELECTION_DISCREPANCY,
				OptimizationManager.SELECTION_SIMILARITY};
		for(int i = 1; i<3; i++)
		{
			String output = "test/SELECTION"+gamePolicies[i];
			File f = new File(output);
			f.mkdir(); 
			gm = new GeneticManager(); 
			gm.setCPUPolicy(GeneticManager.CPU_MULTIPLE);
			gm.setSelectionPolicy(gamePolicies[1]);
			gm.begin(500, "/ressources/genetic/ReferenceSeed", 0.5f);
			gm.getBest(); 
			gm.dumpAll(output);
			paths[i] = output + "/"+ "rank0001.properties";
		}
		
		for(int i = 0; i<4; i++)
		{
			for(int j = i+1; j<4; j++)
			{
				testHelper(paths[i], paths[j]);
			}
		}
		
		
	}
	/** 
	 * twostep genetic test
	 */
	public void runTwoStepGenetic()
	{
		TwoStepGeneticManager tsgm = new TwoStepGeneticManager();
		tsgm.setSelectionPolicy(OptimizationManager.SELECTION_ROULETTE);
		tsgm.setCPUPolicy(OptimizationManager.CPU_MULTIPLE);
		tsgm.setDeepMutation(2);
		tsgm.begin(500,100, 0.4f);
		tsgm.getBest(); 
		tsgm.dumpAll("output_twostep");
	}
	/**
	 * pair genetic test
	 */
	public void runPairGenetic()
	{
		GeneticPairManager tsgm = new GeneticPairManager();
//		DebugMsg.getInstance().addItemForWhiteList(tsgm);
//		DebugMsg.getInstance().addItemForWhiteList(new GameRunner());
		tsgm.setSelectionPolicy(OptimizationManager.SELECTION_ROULETTE);
		tsgm.setCPUPolicy(OptimizationManager.CPU_MULTIPLE);
		tsgm.setDeepMutation(2);
		tsgm.begin(500,300, 0.3f);
		tsgm.getBest(); 
		tsgm.dumpAll("output_pair");
	}

}
