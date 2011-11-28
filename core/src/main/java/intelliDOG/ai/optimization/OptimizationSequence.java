package intelliDOG.ai.optimization;

import java.lang.reflect.*;

public abstract class OptimizationSequence
{
	/**
	 * method to call a commandline specified method
	 * @param o the optimizationmanager instance
	 * @param args the method to be called
	 */
	public void callMethodByName(Object o, String[] args)
	{
		try
		{
			if(args.length == 1)
			{
				if(args[0].contains("list"))
				{
					Method[] marr = o.getClass().getMethods();
					System.out.println("methods avalaible:");
					for(Method m : marr)
					{
						System.out.println(m.toString());
					}
				}
				else
				{
					Method m = o.getClass().getMethod(args[0]);
					m.invoke(o);
				}
			}
			else
			{
				throw new IllegalArgumentException("only one argument supported!");
			}
		}
		catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			System.out.println("the name of the method was not found, try the command \"list\"");
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * default work sequence
	 */
	public abstract void runStoredProcedure(); 

}
