package TradingDemo;

import java.awt.BasicStroke; 
import java.awt.Color; 

import javax.swing.JPanel; 

import org.jfree.chart.ChartFactory; 
import org.jfree.chart.ChartPanel; 
import org.jfree.chart.JFreeChart; 
import org.jfree.chart.axis.NumberAxis; 
import org.jfree.chart.plot.PlotOrientation; 
import org.jfree.chart.plot.XYPlot; 
import org.jfree.chart.renderer.xy.DeviationRenderer; 
import org.jfree.data.xy.XYDataset; 
import org.jfree.data.xy.YIntervalSeries; 
import org.jfree.data.xy.YIntervalSeriesCollection; 
import org.jfree.ui.ApplicationFrame; 
import org.jfree.ui.RectangleInsets; 

/** 
 * This class is used to implement the jfree chart graph of Market Efficiency
 */ 
public class DeviationRendererMarketEfficiency extends ApplicationFrame { 

    
    public DeviationRendererMarketEfficiency(String title, YIntervalSeries AvgMarketEfficiencySeries) { 
        super(title); 
        JPanel chartPanel = createDemoPanel(AvgMarketEfficiencySeries); 
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270)); 
        setContentPane(chartPanel); 
    } 
    

    private static XYDataset createDataset(YIntervalSeries AvgMarketEfficiencySeries) { 
        YIntervalSeriesCollection dataset = new YIntervalSeriesCollection(); 
        dataset.addSeries(AvgMarketEfficiencySeries); 
        
        return dataset; 
    } 

    private static JFreeChart createChart(XYDataset dataset) { 
        
        // create the chart... 
        JFreeChart chart = ChartFactory.createXYLineChart( 
            "Daily Average Market Efficiency",      // chart title 
            "Day",                      // x axis label 
            "Percentage %",                      // y axis label 
            dataset,                  // data 
            PlotOrientation.VERTICAL, 
            true,                     // include legend 
            true,                     // tooltips 
            false                     // urls 
        ); 


        chart.setBackgroundPaint(Color.white); 
        // get a reference to the plot for further customisation... 
        XYPlot plot = (XYPlot) chart.getPlot(); 
        plot.setBackgroundPaint(Color.lightGray); 
        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0)); 
        plot.setDomainGridlinePaint(Color.white); 
        plot.setRangeGridlinePaint(Color.white); 
        
        DeviationRenderer renderer = new DeviationRenderer(true, false); 
        renderer.setSeriesStroke(0, new BasicStroke(3.0f, BasicStroke.CAP_ROUND, 
                BasicStroke.JOIN_ROUND)); 
        renderer.setSeriesStroke(0, new BasicStroke(3.0f)); 
        renderer.setSeriesStroke(1, new BasicStroke(3.0f)); 
        renderer.setSeriesFillPaint(0, new Color(200, 200, 255)); 
        renderer.setSeriesFillPaint(1, new Color(255, 200, 200)); 
        plot.setRenderer(renderer); 

        // change the auto tick unit selection to integer units only... 
        NumberAxis yAxis = (NumberAxis) plot.getRangeAxis(); 
        yAxis.setAutoRangeIncludesZero(false); 
        yAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits()); 
                
        return chart; 
        
    } 


    public static JPanel createDemoPanel(YIntervalSeries AvgMarketEfficiencySeries) { 
       
        JFreeChart chart = createChart(createDataset(AvgMarketEfficiencySeries));
        return new ChartPanel(chart); 
    } 

}