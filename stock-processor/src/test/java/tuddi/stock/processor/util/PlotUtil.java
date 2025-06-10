package tuddi.stock.processor.util;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.util.Map;

public class PlotUtil {

    public static void plot(String title, Map<String, double[][]> datasetMap, String xLabel, String yLabel) {
        XYSeriesCollection dataset = new XYSeriesCollection();

        for (String seriesName: datasetMap.keySet()) {
            XYSeries xySeries = new XYSeries(seriesName);
            double[] x = datasetMap.get(seriesName)[0];
            double[] y = datasetMap.get(seriesName)[1];

            int upTo = Math.min(x.length, y.length);
            for (int i = 0; i < upTo; ++i) {
                xySeries.add(x[i], y[i]);
            }
            dataset.addSeries(xySeries);
        }

        var chart = ChartFactory.createXYLineChart(title, xLabel, yLabel, dataset, PlotOrientation.VERTICAL, true, true, false);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(600, 400));

        JFrame jFrame = new JFrame(title);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setContentPane(chartPanel);
        jFrame.pack();
        jFrame.setLocationRelativeTo(null);
        jFrame.setVisible(true);
    }

}
