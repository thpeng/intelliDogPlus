package intelliDOG.ai.utils; 

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 * This Frame shows the probability of cards of all players. It's from the point of view of one player.
 *
 */
public class BarChartProb extends JFrame {

	private static final long serialVersionUID = 1L;
	
	JLabel txtcardsLeft = new JLabel("Cards left: "); 
	JLabel txtentropyP1 = new JLabel("E1: "); 
	JLabel txtentropyP2 = new JLabel("E2: "); 
	JLabel txtentropyP3 = new JLabel("E3: "); 
	JLabel txtentropyP4 = new JLabel("E4: "); 
		
	JLabel cardsLeft = new JLabel(); 
	JLabel entropyP1 = new JLabel(); 
	JLabel entropyP2 = new JLabel(); 
	JLabel entropyP3 = new JLabel(); 
	JLabel entropyP4 = new JLabel(); 

	DefaultCategoryDataset datasets[] = new DefaultCategoryDataset[]{null, null, null, null};
	JFreeChart[] charts = new JFreeChart[]{null, null, null, null};
	
	/**
	 * player index
	 */
	private byte player;
	private byte playerLabel; 
    
	final String series1 = "First";
	final String series2 = "Second"; 
	
	final String Ace = "1";
	final String Two = "2";
	final String Three = "3";
	final String Four = "4";
	final String Five = "5";
	final String Six = "6";
	final String Seven = "7";
	final String Eight = "8";
	final String Nine = "9";
	final String Ten = "10";
	final String Eleven = "11";
	final String Twelve = "12";
	final String Thirteen = "13";
	final String Fourteen = "14";

     
    /**
     * Creates a new instance.
     *
     * @param title the frame title.
     */
    public BarChartProb(final String title, byte pl) {

    	super(title);
    	this.player = pl; 
    	this.playerLabel = pl; 
    	datasets[0] = (DefaultCategoryDataset) createDataset();
    	datasets[1] = (DefaultCategoryDataset) createDataset();
    	datasets[2] = (DefaultCategoryDataset) createDataset();
    	datasets[3] = (DefaultCategoryDataset) createDataset();

    	final JPanel mainPanel = new JPanel(); 
    	mainPanel.setLayout(new BorderLayout()); 

        final JPanel gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayout(2,2)); 
       
        charts[0] = createChart(datasets[0]);
        final ChartPanel chartPanel1 = new ChartPanel(charts[0]);
        gridPanel.add(chartPanel1); 
        
        charts[1] = createChart(datasets[1]);
        final ChartPanel chartPanel2 = new ChartPanel(charts[1]);
        gridPanel.add(chartPanel2); 
        
        charts[2] = createChart(datasets[2]);
        final ChartPanel chartPanel3 = new ChartPanel(charts[2]);
        gridPanel.add(chartPanel3); 
        
        charts[3] = createChart(datasets[3]);
        final ChartPanel chartPanel4 = new ChartPanel(charts[3]);
        gridPanel.add(chartPanel4); 
                
        JPanel labelPanel = new JPanel(); 
        labelPanel.add(txtcardsLeft); 
        labelPanel.add(cardsLeft); 
        labelPanel.add(txtentropyP1); 
        labelPanel.add(entropyP1); 
       
        labelPanel.add(txtentropyP2); 
        labelPanel.add(entropyP2); 
       
        labelPanel.add(txtentropyP3); 
        labelPanel.add(entropyP3);
        
        labelPanel.add(txtentropyP4); 
        labelPanel.add(entropyP4);
        
        
        mainPanel.add(labelPanel, BorderLayout.NORTH); 
        mainPanel.add(gridPanel, BorderLayout.CENTER);       
        mainPanel.setPreferredSize(new Dimension(850, 450));
        setContentPane(mainPanel);
    }

  
    /**
     * Update chart with new probability values
     * @param playerIndex the player on turn
     * @param p objective probabilities 
     * @param totalProb subjective probabilities
     * @param cardsLeft number of cards left
     * @param entropy 
     */
    public void updateChart(int playerIndex, double[][] p, double[][] totalProb, int cardsLeft, double[] entropy) {
    	
    	if( (playerIndex+1) == this.player)
    	{
    		for(int count = 0, pl = this.player; count < 4; pl = getNextPlayer((byte)pl), count++)
    		{
    			CategoryPlot plot = (CategoryPlot) charts[count].getPlot();

    			for(int i=0; i < datasets[count].getRowCount(); i++)
    			{
    				for(int j=0; j < datasets[count].getColumnCount(); j++)
    				{
    					// for subjective probability
//    					if(i==1 && p[pl-1][j] != totalProb[pl-1][j])
//    						datasets[count].setValue(totalProb[pl-1][j], datasets[count].getRowKey(i), datasets[count].getColumnKey(j));
//    					else if(i==0)
    						datasets[count].setValue(/*p[pl-1][j]*/(i==0?p[pl-1][j]:totalProb[pl-1][j]), datasets[count].getRowKey(i), datasets[count].getColumnKey(j));
    				}
    			}
    			plot.setDataset(datasets[count]);
    		}
    		// FIXME
    		this.cardsLeft.setText(Integer.toString(cardsLeft)); 
        	this.entropyP1.setText(Double.toString( (double) ((double)(Math.round(entropy[0]*100))/100)) + " ");
        	this.entropyP2.setText(Double.toString((double) ((double)(Math.round(entropy[1]*100))/100))+ " "); 
        	this.entropyP3.setText(Double.toString((double) ((double)(Math.round(entropy[2]*100))/100))+ " ");
        	this.entropyP4.setText(Double.toString((double) ((double)(Math.round(entropy[3]*100))/100))+ " "); 
    	}
    }
      
    	
    /**
     * Returns the initial data set.
     * 
     * @return The dataset.
     */
    private CategoryDataset createDataset() {
   
    	DefaultCategoryDataset dataset = new DefaultCategoryDataset();

    	dataset.addValue(0.0, series1, Ace);
    	dataset.addValue(0.0, series1, Two);
    	dataset.addValue(0.0, series1, Three);
    	dataset.addValue(0.0, series1, Four);
    	dataset.addValue(0.0, series1, Five);
    	dataset.addValue(0.0, series1, Six);
    	dataset.addValue(0.0, series1, Seven);
    	dataset.addValue(0.0, series1, Eight);
    	dataset.addValue(0.0, series1, Nine);
    	dataset.addValue(0.0, series1, Ten);
    	dataset.addValue(0.0, series1, Eleven);
    	dataset.addValue(0.0, series1, Twelve);
    	dataset.addValue(0.0, series1, Thirteen);
    	dataset.addValue(0.0, series1, Fourteen);    

    	dataset.addValue(0.0, series2, Ace);
    	dataset.addValue(0.0, series2, Two);
    	dataset.addValue(0.0, series2, Three);
    	dataset.addValue(0.0, series2, Four);
    	dataset.addValue(0.0, series2, Five);
    	dataset.addValue(0.0, series2, Six);
    	dataset.addValue(0.0, series2, Seven);
    	dataset.addValue(0.0, series2, Eight);
    	dataset.addValue(0.0, series2, Nine);
    	dataset.addValue(0.0, series2, Ten);
    	dataset.addValue(0.0, series2, Eleven);
    	dataset.addValue(0.0, series2, Twelve);
    	dataset.addValue(0.0, series2, Thirteen);
    	dataset.addValue(0.0, series2, Fourteen); 
        return dataset;
    }
    
    /**
     * Creates the chart.
     * 
     * @param dataset  the dataset.
     * @return The chart.
     */
    
    private JFreeChart createChart(final CategoryDataset dataset) {

    	final JFreeChart chart = ChartFactory.createBarChart3D(
    			"Player " + playerLabel,         // chart title
    			"Cards",               	  // domain axis label
    			"Probability",            // range axis label
    			dataset,                  // data
    			PlotOrientation.VERTICAL, // orientation
    			false,                    // include legend
    			true,                    
    			false                     
    	);

    	playerLabel = getNextPlayer(playerLabel); 
    	
        // background color
        chart.setBackgroundPaint(Color.white);

        final CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        
      
        // set the range axis to display integers only
        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits()); 
        rangeAxis.setUpperBound(1.0);
        rangeAxis.setLowerBound(0.0);
        
        final CategoryItemRenderer renderer = plot.getRenderer();
        renderer.setSeriesItemLabelsVisible(0, Boolean.TRUE);
       
        final CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        domainAxis.setCategoryMargin(0.1); // ten percent
     
        BarRenderer renderer2 = (BarRenderer) plot.getRenderer();
        renderer2.setItemMargin(0.0); // fifteen percent
        renderer.setSeriesPaint(0, Color.BLUE); 
        renderer.setSeriesPaint(1, Color.RED); 
        
        return chart;
    }
    
    private byte getNextPlayer(byte player)
    {
    	byte nextPlayer = (byte)((player + 1) % 5);
    	return (nextPlayer == 0 ? 1 : nextPlayer);
    }
    
    public byte getPlayer()
    {
    	return this.player; 
    }
}