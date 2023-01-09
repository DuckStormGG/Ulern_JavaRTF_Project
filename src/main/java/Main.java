import java.io.File;
import java.sql.SQLException;


public class Main {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        DBHandler.Connect();
        DBHandler.CreateDB();
        DataHandler.ReadCSV(new File("C:\\Users\\Pavel\\IdeaProjects\\UleranProject\\.idea\\Country .csv").toPath());
        DBHandler.Normalize();
        ChartHandler.showChart();
        DBHandler.printMaxInSelectRegions();
        DBHandler.printMostAVGInSelectRegions();
        DBHandler.CloseDB();
    }
}

