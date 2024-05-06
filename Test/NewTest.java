import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.sql.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(JUnitParamsRunner.class)

public class NewTest {
    private static WebDriver driver;
    private static Connection connection;
    private static final String DATE_PATTERN = "yyyy-MM-dd";
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN);
    private static String DB_URL = "jdbc:sqlite:tester.sqlite";
    private static List<LocalDate> hotelDates = new ArrayList<>();

    private final String[] cities = {"Sacramento", "Orlando", "Miami", "Austin", "Atlanta"};


    /**
     * This will call onto the database and driver.
     * This will also make the list of dates that are needed for the search.
     * @throws SQLException
     */
    @BeforeClass
    public static void setUp() throws SQLException {
        connection = DriverManager.getConnection(DB_URL);
        driver = new FirefoxDriver();

        // This was made to delete all records because my internet was bad and the website was bad,
        // that I didn't want any records in there that was going to make it worse.
        /*
        String deleteSQL = "DELETE FROM test";
        PreparedStatement ds = connection.prepareStatement(deleteSQL);
        ds.executeUpdate();

         */

        LocalDate startDate = LocalDate.of(2024, 5, 7);
        LocalDate endDate = LocalDate.of(2024, 12, 31);
        while (startDate.isBefore(endDate)) {
            hotelDates.add(startDate);
            startDate = startDate.plusDays(1);
        }
    }


    /**
     * This will test to see if it can grab the price of the hotel and put it into a database.
     * Had to you a bypass to make the test work because the website will sometimes give out dead spots.
     * @param hotels
     */
    @Test
    @Parameters({"Hilton", "Hyatt Regency", "Comfort Suites", "Hampton Inn", "Holiday Inn"})
    public void testGetHotelPrice(String hotels){
        for(int k = 0; k < cities.length; k ++) {
            for (int i = 0; i < hotelDates.size(); i++) {
                String url = buildUrl(cities[k].toString(), hotels , hotelDates.get(i).toString(), 25, true);
                driver.get(url);
                driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
                if(!driver.findElement(By.id("results_list_lowest_price")).isEnabled()){
                    hotelDates.get(i).plusDays(1);
                } else {
                    WebElement price = driver.findElement(By.className("amount"));
                    String timeStamp = (new Date()).toString();
                    insertHotelPricesToDatabase(hotels, cities[k].toString(), hotelDates.get(i).toString(), price.getText(), timeStamp);
                }
            }
        }

    }


    /**
     * This will allow for the data that is being taken from the website, and put it into the database.
     * @param hotels
     * @param city
     * @param date
     * @param price
     * @param time
     */
    private void insertHotelPricesToDatabase (String hotels, String city, String date, String price, String time){
        String sql = "insert into test values (null,?,?,?,?,?)";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1,hotels);
            ps.setString(2,city);
            ps.setString(3,date);
            ps.setString(4,price);
            ps.setString(5,time);
            ps.executeUpdate();

        } catch (SQLException e){
            e.printStackTrace();
        }
    }


    /**
     * This will delete from the database that had the hotels Hyatt Regency and Hampton Inn.
     * This was made because I kept getting errors within these 2 hotels due to bad internet and poor website.
     * It is ignored because it doesn't need to be called unless there is an error with those two.
     */
    @Test
    @Ignore
    public void deleteHyattAndHampton(){
        String deleteSQL = "DELETE FROM test WHERE hotel = 'Hyatt Regency' OR hotel = 'Hampton Inn'";
        try {
            PreparedStatement ds = connection.prepareStatement(deleteSQL);
            int rowsAffected = ds.executeUpdate();
            System.out.println("Rows deleted: " + rowsAffected);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * This will filter out the 10 cheapest prices from each hotel and city from database.
     * @param hotel
     */
    @Test
    @Parameters({"Hilton", "Hyatt Regency", "Comfort Suites", "Hampton Inn", "Holiday Inn"})
    public void findCheapestHotel(String hotel){
        for(int k = 0; k < cities.length; k ++) {
            String query = "WITH RankedPrices AS (SELECT date, hotel, city, price, ROW_NUMBER() OVER (PARTITION BY hotel, city ORDER BY price ASC) AS price_rank FROM test WHERE hotel = ? AND city = ?)  SELECT date, hotel, city, price FROM RankedPrices WHERE price_rank <= 10 ";

            try {
                PreparedStatement ps = connection.prepareStatement(query);
                ps.setString(1, hotel);
                ps.setString(2, cities[k].toString());
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    String cheapHotel = rs.getString("hotel");
                    String cheapCity = rs.getString("city");
                    String cheapDate = rs.getString("date");
                    String cheapPrice = rs.getString("price");
                    System.out.println(cheapHotel + " : On " + cheapDate + " the price for " + cheapCity + " is $" + cheapPrice);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }


    /**
     * This will build the url that is needed when searching for the hotels and their prices.
     * @param city
     * @param hotel
     * @param strCheckIn
     * @param per_page
     * @param sortByPrice
     * @return
     */
    private String buildUrl(String city, String hotel, String strCheckIn, int per_page, boolean sortByPrice) {
        LocalDate checkIn = LocalDate.parse(strCheckIn);
        LocalDate checkOut = checkIn.plusDays(1);

        String baseUrl = "https://www.getaroom.com/search?";
        String cityFixed = city.replace(" ", "+");
        String hotelFixed = hotel.replace(" ", "+");

        baseUrl = baseUrl + "destination=" + cityFixed +
                "&per_page=" + per_page +
                "&hide_unavailable=true" +
                "&check_in=" + checkIn.format(formatter).replace(" ", "") +
                "&check_out=" + checkOut.format(formatter).replace(" ", "") +
                "&property_name=" + hotelFixed;

        if (sortByPrice)
            baseUrl = baseUrl + "&sort_order=price";

        return baseUrl;
    }
}
