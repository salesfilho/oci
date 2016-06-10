/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.prof.salesfilho.oci.view.graph;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.math3.util.Precision;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;

/**
 *
 * @author salesfilho
 */
@Getter
@Setter
public class Graph extends ApplicationFrame {

    private String title;
    private String xLabel;
    private String yLabel;
    private XYDataset dataSet;
    private XYSeriesCollection collectionDataset;
    private JFreeChart chart;
    private ChartPanel chartPanel;

    public Graph(final String title) {
        super(title);
        this.title = title;
        dataSet = collectionDataset = new XYSeriesCollection();
        chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);
    }

//    private XYDataset createDataset() {
//        collectionDataset = new XYSeriesCollection();
//        return collectionDataset;
//    }

    public void addSeire(double[] values, String serieName) {
        final XYSeries serie = new XYSeries(serieName);
        for (int i = 0; i < values.length; i++) {
            serie.add( i, Precision.round(values[i], 2));
        }
        collectionDataset.addSeries(serie);
        updateChart();
    }

    private void updateChart(){
        this.dataSet = this.collectionDataset;
        this.chart = createChart();
        chartPanel.setChart(chart);
    }
    
    public JFreeChart createChart() {
        chart = ChartFactory.createXYLineChart(
                this.title,
                this.xLabel,
                this.yLabel,
                getDataSet(),
                PlotOrientation.VERTICAL,
                true, true, false);

        chart.setBackgroundPaint(Color.white);

        final XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.lightGray);
        //    plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5.0, 5.0, 5.0, 5.0));
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);

//        final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
//        renderer.setSeriesLinesVisible(1, false);
//        renderer.setSeriesShapesVisible(1, false);
//        plot.setRenderer(renderer);

        // change the auto tick unit selection to integer units only...
        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        // OPTIONAL CUSTOMISATION COMPLETED.

        return chart;
    }

//    public void saveGraphAsPNG(String fileName) {
//        OutputStream fos;
//        try {
//            fos = new FileOutputStream(fileName);
//            ChartUtilities.writeChartAsPNG(fos, createChart(), 1024, 512);
//            fos.close();
//        } catch (FileNotFoundException ex) {
//            Logger.getLogger(Graph.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            Logger.getLogger(Graph.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//
//    public byte[] getGraphAsImage() {
//        BufferedImage bufferedImage = createChart().createBufferedImage(512, 512);
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        byte[] imageInByte = null;
//        try {
//            ImageIO.write(bufferedImage, "jpg", baos);
//            baos.flush();
//            imageInByte = baos.toByteArray();
//            baos.close();
//        } catch (IOException ex) {
//            Logger.getLogger(Graph.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return imageInByte;
//    }
//
//    public BufferedImage getGraphAsBufferedImage() {
//        return createChart().createBufferedImage(1024, 512);
//    }
}
