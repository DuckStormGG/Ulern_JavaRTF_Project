import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class DBHandler {
    public static Connection connection;
    public static Statement statement;
    public static ResultSet resultSet;

    public static void Connect() throws ClassNotFoundException, SQLException {
        connection = null;
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:CountriesDB.s3db");
    }

    public static void CreateDB() throws ClassNotFoundException, SQLException {
        statement = connection.createStatement();
        statement.execute("CREATE TABLE if not exists 'temp_table' " +
                "('countryID' INTEGER PRIMARY KEY AUTOINCREMENT, 'country' TEXT,'subregion' TEXT, 'region'" +
                " text,'internetUsers' INT, 'population' INT, 'economicIndicator' DECIMAL(5,2) );");
        statement.execute("CREATE TABLE if not exists 'subregions' ('subRegionID' INTEGER PRIMARY KEY AUTOINCREMENT," +
                " 'subregion' TEXT, 'region' TEXT);");
        statement.execute("CREATE TABLE if not exists 'countriesName' ('countryID' INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "'countryName' TEXT);");
        statement.execute("CREATE TABLE if not exists 'countriesPopulation' ('countryID' INTEGER PRIMARY KEY AUTOINCREMENT," +
                " 'population' INTEGER);");
        statement.execute("CREATE TABLE if not exists 'countriesInternetUsers' ('countryID' INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  'internetUsers' INTEGER);");
    }

    public static void WriteDB(String Country, String SubRegion, String Region, String Population, String InternetUsers,
                               String EconomicIndicator) throws SQLException {
        PreparedStatement temp = connection.prepareStatement("INSERT or IGNORE INTO 'temp_table' ('country','subregion'," +
                "'region','internetUsers','population',economicIndicator) VALUES (?,?,?,?,?,?); ");
        temp.setObject(1, Country);
        temp.setObject(2, SubRegion);
        temp.setObject(3, Region);
        temp.setObject(4, Population);
        temp.setObject(5, InternetUsers);
        temp.setObject(6, EconomicIndicator);
        temp.execute();
    }

    public static void Normalize() throws SQLException {
        statement.execute("INSERT or IGNORE INTO subregions(subregion, region) SELECT DISTINCT subregion, region FROM temp_table");
        statement.execute("INSERT or IGNORE INTO countriesName SELECT countryid,country FROM temp_table");
        statement.execute("INSERT or IGNORE INTO countriesPopulation SELECT countryid,population FROM temp_table");
        statement.execute("INSERT or IGNORE INTO countriesInternetUsers SELECT countryid,internetusers FROM temp_table");
        statement.execute("CREATE TABLE IF NOT EXISTS countrySubregion AS SELECT countryid, subregionid " +
                "FROM temp_table INNER JOIN subregions ON temp_table.subregion == subregions.subregion");
        statement.execute("CREATE TABLE IF NOT EXISTS countryEconomics AS " +
                "SELECT temp_table.countryid AS countryid, " +
                "countrySubregion.subregionid as subregionid, " +
                "temp_table.economicindicator as economicindicator " +
                "FROM temp_table INNER JOIN countrySubregion ON temp_table.countryid == countrySubregion.countryid");
        statement.execute("DROP TABLE temp_table");

    }

    public static Map<String, Double> GetChartData() throws SQLException {
        Map<String, Double> map = new HashMap<>();
        resultSet = statement.executeQuery("SELECT countriesName.countryname as countryname," +
                "countryEconomics.economicindicator as economicindicator " +
                "FROM countryEconomics INNER JOIN countriesName ON countryEconomics.countryid == countriesName.countryid");
        while (resultSet.next()) {
            map.put(resultSet.getString("countryName"), resultSet.getDouble("economicindicator"));
        }
        return map;
    }

    public static void printMaxInSelectRegions() throws SQLException {
        resultSet = statement.executeQuery("SELECT countriesName.countryname, " +
                "MAX(countryEconomics.economicindicator) FROM countryEconomics INNER JOIN countriesName " +
                "ON countryEconomics.countryid == countriesName.countryid AND " +
                "(subregionid == 1 or subregionid ==5 OR subregionid ==18 OR subregionid ==19)");
        System.out.println("Страна с самым высоким показателем экономики среди \"Latin America and Caribbean\" " +
                "и \"Eastern Asia\" :" + resultSet.getString("countryName"));
    }

    public static void printMostAVGInSelectRegions() throws SQLException {
        resultSet = statement.executeQuery("SELECT MIN(ABS(economicindicator - (SELECT AVG(economicindicator) " +
                "FROM countryEconomics WHERE (subregionid == 3 OR subregionid == 9))))" +
                ",countriesName.countryname FROM countryEconomics INNER JOIN countriesName " +
                "ON countryEconomics.countryid == countriesName.countryid AND (subregionid == 3 OR subregionid == 9)");
        System.out.println("Страна с \"самым средними показателем\" среди \"Western Europe\" и \"North America\":"
                + resultSet.getString("countryName"));
    }

    public static void CloseDB() throws ClassNotFoundException, SQLException {
        connection.close();
        statement.close();
        resultSet.close();
    }
}
