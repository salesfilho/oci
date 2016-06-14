/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.prof.salesfilho.oci.image;

import java.awt.Color;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.math3.util.Precision;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLine3DRenderer;
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
public class GraphicBuilder extends ApplicationFrame {

    private String title;
    private String xLabel;
    private String yLabel;
    private XYDataset dataSet;
    private XYSeriesCollection collectionDataset;
    private JFreeChart chart;
    private ChartPanel chartPanel;

    public GraphicBuilder(final String title) {
        super(title);
        this.title = title;
        dataSet = collectionDataset = new XYSeriesCollection();
        chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);
    }

    public void addSeire(double[] values, String serieName) {
        final XYSeries serie = new XYSeries(serieName);
        for (int i = 0; i < values.length; i++) {
            serie.add(i, Precision.round(values[i], 2));
        }
        collectionDataset.addSeries(serie);
    }

    public void createCombinedChart(Map<String, double[]> mapSeries, String legendTitle) {
        XYSeriesCollection xds;
        final CombinedDomainXYPlot plot = new CombinedDomainXYPlot(new NumberAxis(legendTitle));
        plot.setGap(10.0);
        for (Map.Entry<String, double[]> entrySet : mapSeries.entrySet()) {
            String serieName = entrySet.getKey();
            double[] values = entrySet.getValue();

            final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

            final XYSeries series = new XYSeries(serieName);
            for (int i = 0; i < values.length; i++) {
                series.add(i, Precision.round(values[i], 2));
                renderer.setSeriesVisible(i, true, true);
                renderer.setSeriesShapesVisible(i, true);
            }
            xds = new XYSeriesCollection();
            xds.addSeries(series);

            final NumberAxis rangeAxis = new NumberAxis(serieName);

            final XYPlot subplot = new XYPlot(xds, null, rangeAxis, renderer);
            subplot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
            plot.add(subplot);

        }
        this.chart = new JFreeChart(this.title, JFreeChart.DEFAULT_TITLE_FONT, plot, true);
        chartPanel.setChart(chart);
    }

    public void createChart() {
        chart = ChartFactory.createXYLineChart(
                this.title,
                this.xLabel,
                this.yLabel,
                this.collectionDataset,
                PlotOrientation.VERTICAL,
                true, true, false);

        chart.setBackgroundPaint(Color.white);

        final XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.lightGray);
        //    plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5.0, 5.0, 5.0, 5.0));
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);

        final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        List<XYSeries> listSeries = collectionDataset.getSeries();
        for (int i = 0; i < listSeries.size(); i++) {
            renderer.setSeriesLinesVisible(i, true);
            renderer.setSeriesShapesVisible(i, true);
            renderer.setSeriesShapesFilled(i, false);
        }

        plot.setRenderer(renderer);
        plot.setRangePannable(true);
        // change the auto tick unit selection to integer units only...
        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        chartPanel.setChart(chart);

    }

//    public void saveGraphAsPNG(String fileName) {
//        OutputStream fos;
//        try {
//            fos = new FileOutputStream(fileName);
//            ChartUtilities.writeChartAsPNG(fos, createChart(), 1024, 512);
//            fos.close();
//        } catch (FileNotFoundException ex) {
//            Logger.getLogger(GraphicBuilder.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            Logger.getLogger(GraphicBuilder.class.getName()).log(Level.SEVERE, null, ex);
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
//            Logger.getLogger(GraphicBuilder.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return imageInByte;
//    }
//
//    public BufferedImage getGraphAsBufferedImage() {
//        return createChart().createBufferedImage(1024, 512);
//    }
}
