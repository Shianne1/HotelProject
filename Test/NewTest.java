import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
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
        //driver = new ChromeDriver();

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

    @Test
    @Parameters({"Hilton", "Hyatt Regency", "Comfort Suites", "Hampton Inn", "Holiday Inn"})
    //@Parameters({"Comfort Suites" })
    public void testGetHotelPrice(String hotels){
        for(int k = 0; k < cities.length; k ++) {
            for (int i = 0; i < hotelDates.size(); i++) {
                String url = buildUrl(cities[k].toString(), hotels , hotelDates.get(i).toString(), 25, true);
                driver.get(url);
                driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
                if(!driver.findElement(By.id("results_list_lowest_price")).isEnabled() /*|| driver.getTitle().contains("gateway")*/){
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

    @Test
    @Parameters({"Hilton", "Comfort Suites", "Holiday Inn"})
    public void findCheapestHotel(String hotel){
        //String query = "SELECT * FROM test WHERE city = ? AND hotel = ? ORDER BY price ASC LIMIT 10";
        String query = "SELECT * FROM test WHERE hotel = ? ORDER BY price ASC LIMIT 10";
        try{
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1,hotel);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                String cheapHotel = rs.getString("hotel");
                String cheapCity = rs.getString("city");
                String cheapDate = rs.getString("date");
                String cheapPrice = rs.getString("price");
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
