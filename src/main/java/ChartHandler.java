import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.Map;


public class ChartHandler {
    public static void showChart() throws SQLException {
        JFreeChart barChart = ChartFactory.createBarChart(
                "Показатель экономики по странам",
                "Страны",
                "Процент людей с доступом в интернет",
                createDataset(),
                PlotOrientation.VERTICAL,
                true, true, false);
        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new java.awt.Dimension(560, 367));
        CategoryPlot plot = (CategoryPlot) barChart.getPlot();
        ChartPanel panel = new ChartPanel(barChart);
        panel.setFillZoomRectangle(true);
        panel.setMouseWheelEnabled(true);
        panel.setPreferredSize(new Dimension(600, 300));
        JFrame a = new JFrame("Показатели стран");
        a.add(panel);
        a.setSize(600, 480);
        a.setVisible(true);
    }

    private static CategoryDataset createDataset() throws SQLException {
        Map<String, Double> Data = DBHandler.GetChartData();
        final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map.Entry<String, Double> entry : Data.entrySet()) {
            dataset.addValue(entry.getValue(), entry.getKey(), "Country");
        }
        return dataset;
    }

}
