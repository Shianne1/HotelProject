import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.BeforeClass;
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
    //private final String[] hotelChains = {"Holiday Inn", "Hyatt Regency", "Hilton", "Comfort Suites", "Hampton Inn"};

    private final String[] cities = {"Sacramento", "Atlanta", "Orlando", "Miami", "Austin"};

    @BeforeClass
    public static void setUp() throws SQLException {
        connection = DriverManager.getConnection(DB_URL);
        driver = new FirefoxDriver();

        String deleteSQL = "DELETE FROM test";
        PreparedStatement ds = connection.prepareStatement(deleteSQL);
        ds.executeUpdate();

        LocalDate startDate = LocalDate.of(2024, 5, 5);
        LocalDate endDate = LocalDate.of(2024, 5, 31);
        while (startDate.isBefore(endDate)) {
            hotelDates.add(startDate);
            startDate = startDate.plusDays(1);
        }
    }

    @Test
    @Parameters({"Hilton", "Hyatt Regency", "Hampton Inn", "Holiday Inn", "Comfort Suites" })
    public void testGetHotelPrice(String hotels){
        for(int k = 0; k < cities.length; k ++) {
            for (int i = 0; i < hotelDates.size(); i++) {
                String url = buildUrl(cities[k].toString(), hotels , hotelDates.get(i).toString(), 25, true);
                driver.get(url);
                driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
                if(!driver.findElement(By.id("results_list_lowest_price")).isEnabled() || driver.getTitle().contains("gateway")){
                    hotelDates.get(i).plusDays(1);
                } else {
                    WebElement price = driver.findElement(By.className("amount"));
                    //System.out.println(cities[k].toString() + " - " + hotels + " - " + hotelDates.get(i).toString() + " - $" + price.getText());
                    //System.out.println();

                    String timeStamp = (new Date()).toString();
                    insertHotelPricesToDatabase(hotels, cities[k].toString(), hotelDates.get(i).toString(), price.getText(), timeStamp);
                }
            }
        }
    }

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
            //findCheapestHotel(hotels, city);

        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    private void findCheapestHotel(String hotels, String city){
        String query = "SELECT * FROM test WHERE city = ? AND hotel = ? ORDER BY price ASC LIMIT 10";
        try{
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1,hotels);
            ps.setString(2,city);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                String cheapHotel = rs.getString("");
                String cheapCity = rs.getString("");
                String cheapDate = rs.getString("");
                String cheapPrice = rs.getString("");
                System.out.println("Cheapest hotels in " + cheapCity + " is " + cheapHotel + " - " + cheapPrice + " on " + cheapDate);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }

    }



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
