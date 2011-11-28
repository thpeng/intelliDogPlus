package intelliDOG.ai.optimization;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
/**
 * The Statistic class measures some operating figures
 * like min, max, median and median deviation on each 
 * attribute
 * @author tpeng
 *
 */
public class Statistic 
{
	private OptimizationManager gm = null;
	private static final String VALUE_DELIMITER = ":";
	private static final String ATTRIBUTE_DELIMITER = "|";
	private FileOutputStream fos = null; 
	private String[] attributes = null; 
	private int interval = 1;
	private int current = 0;
	
	/**
	 * statistic with an output path an instance
	 * @param path the output file
	 * @param gm optimizationmanager
	 */
	public Statistic(String path, OptimizationManager gm)
	{
		File f = new File(path);
		try {
			fos = new FileOutputStream(f);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.gm = gm; 
		attributes = new EvaluatorWrapper().getAttributes();
		WriteHeader(attributes);
	}
	/**
	 * set an interval of generations
	 * @param i interval
	 */
	public void setInterval(int i )
	{
		this.interval = i; 
	}
	/**
	 * set the optimizationManager
	 * @param gm
	 */
	public void setOptimizationManager(OptimizationManager gm)
	{
		this.gm = gm; 
	}
	/**
	 * record a population
	 * @param ews the current population
	 * @return float values
	 */
	public float[][] record(List<EvaluatorWrapper> ews)
	{
		float[][] record = new float[attributes.length][4];
		if(current % interval == 0)
		{
			
			for(EvaluatorWrapper ew : ews)
			{
				Properties p = ew.getProperties();
				for(int i =0; i< attributes.length; i++)
				{
					String s = attributes[i];
					if(ews.get(0)== ew)
					{
						record[i][0] = Float.parseFloat(p.getProperty(s));
						record[i][1] = Float.parseFloat(p.getProperty(s));
						record[i][2] = Float.parseFloat(p.getProperty(s));
					}
					else
					{
						
						float f = Float.parseFloat(p.getProperty(s));
						if(f<record[i][0])
						{
							record[i][0] = Float.parseFloat(p.getProperty(s));
						}
						if(f>record[i][1])
						{
							record[i][1] = Float.parseFloat(p.getProperty(s));
						}
						record[i][2] += Float.parseFloat(p.getProperty(s));
					}
				}
			}
			for(int i = 0; i< record.length; i++)
			{
				 float temp =  (int) ((record[i][2]/ews.size())*1000); 
				 temp =(temp /1000); 
				 record[i][2] = temp;
				 record[i][3] = deviance(ews,attributes[i],temp);
			}
			write(record);
			
		}
		current++; 
		return record;
	}
	/**
	 * calculate the median deviance for each attribute
	 * @param ews the population
	 * @param key the current attribute
	 * @param median the current median for this key
	 * @return the median deviance in percent
	 */
	private float deviance(List<EvaluatorWrapper> ews, String key, float median) {
		float result = 0; 
		for(EvaluatorWrapper ew : ews)
		{
			float temp = Float.parseFloat(ew.getProperties().getProperty(key));
			temp = Math.abs(median - temp);
			temp = 100/median*temp;
			result += temp; 
		}
		result = result/ews.size();
		result =  (int) (result*1000);
		result =(result /1000); 
		return result;
	}
	/**
	 * reset the statistic and open a new outputfile
	 * @param path the file
	 */
	public void reset(String path)
	{
		current = 0; 
	}
	/**
	 * write the record into a file
	 * @param values float values
	 */
	private void write(float[][] values)
	{
		StringBuffer result = new StringBuffer();
		for(int i = 0; i < values.length; i++)
		{
			for(int j = 0; j< 4; j++)
			{
				if(j<3)
					result.append(Float.toString(values[i][j])+VALUE_DELIMITER);
				else
					result.append(Float.toString(values[i][j])+ATTRIBUTE_DELIMITER);
			}
		}
		result.append("\n");
		try {
			fos.write(result.toString().getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * write the header in a file
	 * @param attributes the keys
	 */
	private void WriteHeader(String[] attributes)
	{
		StringBuffer sb = new StringBuffer(); 
		sb.append("Statistic written on the: " + gm.getSim_ID()+'\n'+ " interval: "+interval); 
		for(String s : attributes)
		{
			sb.append(s+":::"+ATTRIBUTE_DELIMITER);
		}
		sb.append('\n');
		for(int i =0; i< attributes.length; i++)
		{
			sb.append("low:high:average:deviance"+ ATTRIBUTE_DELIMITER); 
		}
		sb.append('\n');
		try {
			fos.write(sb.toString().getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * clean up, close all outputstreams
	 */
	public void cleanUp()
	{
		try {
			fos.close();
			fos = null; 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	

}
