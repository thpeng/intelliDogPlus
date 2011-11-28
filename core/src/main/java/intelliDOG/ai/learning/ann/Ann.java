package intelliDOG.ai.learning.ann;

import intelliDOG.ai.learning.rl.State;

import java.util.Properties;

public interface Ann {

	public void updateWeights(double td, double outpErr);
	
	public double getValue(State s);
	
	public Properties getProperties();
	
	public void setProperties(Properties p);
	
	public void save(String fileName);
	
	public void load(String fileName);
}
