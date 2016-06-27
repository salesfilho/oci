/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.prof.salesfilho.oci.view.gui;

import br.prof.salesfilho.oci.dao.DatabaseDescriptor;
import br.prof.salesfilho.oci.dao.DescriptorDAO;
import br.prof.salesfilho.oci.domain.ImageDescriptor;
import br.prof.salesfilho.oci.image.GraphicBuilder;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jfree.ui.RefineryUtilities;

/**
 *
 * @author salesfilho
 */
public class DatabaseChartGenerator {

    private DescriptorDAO dao;
    private DatabaseDescriptor databaseDescriptor;
    private ImageDescriptor imageDescriptor;

    public static void main(String[] args) {
        DatabaseChartGenerator databaseChartGenerator = new DatabaseChartGenerator();
        databaseChartGenerator.dao = new DescriptorDAO();
        databaseChartGenerator.dao.open(new File("/Users/salesfilho/Downloads/database/descritors.xml"));

        List<ImageDescriptor> nudeBustList = databaseChartGenerator.dao.findAllWomanNudeBust();

        Map<String, double[]> dataMap = new HashMap<>();
        for (ImageDescriptor descritor : nudeBustList) {
            dataMap.put("Avarage RGB channel", descritor.getAvgChannel());
            dataMap.put("RED channel", descritor.getRedChannel());
            if (true) {
                break;
            }
        }
        databaseChartGenerator.plot(dataMap, "Nude Bust Woman Descritor for RGB Channels ", "Descriptor");

        List<ImageDescriptor> nonNuudeBustList = databaseChartGenerator.dao.findAllWomanNonNudeBust();

        dataMap = new HashMap<>();
        for (ImageDescriptor descritor : nonNuudeBustList) {
            dataMap.put("Avarage RGB channel", descritor.getAvgChannel());
            dataMap.put("RED channel", descritor.getRedChannel());
            if (true) {
                break;
            }
        }
        databaseChartGenerator.plot(dataMap, "Non Nude Bust Woman Descritor for RGB Channels ", "Descriptor");
    }

    public void plot(double[] data, String title, String serieName) {

        final GraphicBuilder g1 = new GraphicBuilder(title);
        g1.addSeire(data, serieName);
        g1.createChart();
        g1.pack();
        RefineryUtilities.centerFrameOnScreen(g1);
        g1.setVisible(true);
    }

    public void plot(Map<String, double[]> dataMap, String title, String legend) {
        final GraphicBuilder g1 = new GraphicBuilder(title);
        g1.createCombinedChart(dataMap, legend);
        g1.pack();
        RefineryUtilities.centerFrameOnScreen(g1);
        g1.setVisible(true);

    }

}
