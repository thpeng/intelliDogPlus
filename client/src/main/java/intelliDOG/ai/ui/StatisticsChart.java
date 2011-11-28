package intelliDOG.ai.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class StatisticsChart extends JFrame {

	private static final long serialVersionUID = -3448501695435568823L;

	private final JFreeChart statsChart; 
	
	private final XYSeries pieceCount1Series;
	private final XYSeries pieceCount2Series;
	private final XYSeries timeSeries;
	private final XYSeries avgTimeSeries;
	private final XYSeries movesSeries;
	private final XYSeries avgMovesSeries;
	
	
	public StatisticsChart(){
		
		pieceCount1Series = new XYSeries("Pieces Team 1");
		pieceCount2Series = new XYSeries("Pieces Team 2");
		timeSeries = new XYSeries("Time (s)");
		avgTimeSeries = new XYSeries("Avg. Time (s)");
		movesSeries = new XYSeries("# of moves");
		avgMovesSeries = new XYSeries("Avg. # of moves");
		
		statsChart = createChart();
		this.setContentPane(new ChartPanel(statsChart));
		
		this.setLocation(new Point(450, 200));
		this.setSize(new Dimension(850, 450));
		this.setTitle("intelliDOG Statistics Chart");
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setVisible(true);
		
	}
	
	public void addData(int gameNr, int winningTeam, int pieceCount1, int pieceCount2, double time, int moves, double avgTime, int avgMoves){
		pieceCount1Series.add(gameNr, pieceCount1);
		pieceCount2Series.add(gameNr, pieceCount2);
		timeSeries.add(gameNr, time);
		movesSeries.add(gameNr, moves);
		avgTimeSeries.add(gameNr, avgTime);
		avgMovesSeries.add(gameNr, avgMoves);
	}
	
	public void resetData(){
		pieceCount1Series.clear();
		pieceCount2Series.clear();
		timeSeries.clear();
		movesSeries.clear();
		avgTimeSeries.clear();
		avgMovesSeries.clear();
	}
	
	private JFreeChart createChart(){
        //         Add the series to your data set
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(pieceCount1Series);
        dataset.addSeries(pieceCount2Series);
        //         Generate the graph
        JFreeChart chart = ChartFactory.createXYLineChart("Statistics", // Title
                "Game #", // x-axis Label
                "# of Pieces", // y-axis Label
                dataset, // Dataset
                PlotOrientation.VERTICAL, // Plot Orientation
                true, // Show Legend
                true, // Use tooltips
                false // Configure chart to generate URLs?
            );
        
        XYPlot plot = chart.getXYPlot();
        
        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);
        
        XYLineAndShapeRenderer rend = (XYLineAndShapeRenderer) plot.getRenderer();
        rend.setSeriesShapesVisible(0, true);
        rend.setSeriesShapesVisible(1, true);
        rend.setSeriesShapesVisible(2, true);
        
        
        NumberAxis rangeAxis = (NumberAxis)plot.getDomainAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        
        NumberAxis piecesAxis = (NumberAxis)plot.getRangeAxis();
        piecesAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        
        //second axis
        NumberAxis axis2 = new NumberAxis("# of Moves");
        plot.setRangeAxis(1, axis2);
        XYSeriesCollection dataset2 = new XYSeriesCollection();
        dataset2.addSeries(movesSeries);
        plot.setDataset(1, dataset2);
        plot.mapDatasetToRangeAxis(1, 1);
        plot.setRenderer(1, new XYLineAndShapeRenderer());
        plot.getRenderer(1).setSeriesPaint(0, Color.yellow);
        ((XYLineAndShapeRenderer)(plot.getRenderer(1))).setSeriesShapesVisible(0, true);
        //avg. # of moves
        dataset2.addSeries(avgMovesSeries);
        plot.getRenderer(1).setSeriesPaint(1, Color.orange);
        ((XYLineAndShapeRenderer)(plot.getRenderer(1))).setSeriesShapesVisible(1, true);
        
        //third axis
        NumberAxis axis3 = new NumberAxis("Time in sec");
        plot.setRangeAxis(2, axis3);
        XYSeriesCollection dataset3 = new XYSeriesCollection();
        dataset3.addSeries(timeSeries);
        plot.setDataset(2, dataset3);
        plot.mapDatasetToRangeAxis(2,2);
        plot.setRenderer(2, new XYLineAndShapeRenderer());
        plot.getRenderer(2).setSeriesPaint(0, Color.green);
        ((XYLineAndShapeRenderer)(plot.getRenderer(2))).setSeriesShapesVisible(0, true);
        //avg. time
        dataset3.addSeries(avgTimeSeries);
        plot.getRenderer(2).setSeriesPaint(1, Color.magenta);
        ((XYLineAndShapeRenderer)(plot.getRenderer(2))).setSeriesShapesVisible(1, true);
        
        return chart;
	}
	
	public static void main(String[] args){
		StatisticsChart sc = new StatisticsChart();
	}
	
}
