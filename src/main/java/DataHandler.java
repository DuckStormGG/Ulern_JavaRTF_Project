import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Objects;


public class DataHandler {

    public static void ReadCSV(Path file) {
        try (Reader reader = Files.newBufferedReader(file)) {
            try (CSVReader csvReader = new CSVReader(reader)) {
                String[] line = csvReader.readNext();
                while ((line = csvReader.readNext()) != null) {
                    DBHandler.WriteDB(line[0], line[1], line[2], line[3].replace(",", "")
                            , line[4].replace(",", ""), getIndicator(line[3], line[4]));
                }
            } catch (CsvValidationException | SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getIndicator(String users, String population) {
        if (users != null & !Objects.equals(population, "")) {
            Float usersInt = Float.parseFloat(users.replace(",", ""));
            Float populationInt = Float.parseFloat(population.replace(",", ""));
            var temp = usersInt / populationInt * 100;
            return Float.toString(temp);
        } else return null;
    }

}
