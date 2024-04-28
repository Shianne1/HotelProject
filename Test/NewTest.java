import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RunWith(JUnitParamsRunner.class)

public class NewTest {
    private static WebDriver driver;
    private static Connection connection;
    private static final String DATE_PATTERN = "yyyy-MM-dd";
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN);
    private static String DB_URL = "jdbc:sqlite:hotelcheckin.sqlite";
    private static List<LocalDate> hotelDates = new ArrayList<>();
    private final String[] cities = {"Washington DC", "Tokyo", "Chicago", "Paris", "New York City"};
    private final String[] hotelChains = {"Park Hyatt", "Holiday Inn", "Ritz", "Best Western", "Four Seasons"};

    @BeforeClass
    public static void setUp() throws SQLException {
        connection = DriverManager.getConnection(DB_URL);
        //driver = new FirefoxDriver();
        driver = new ChromeDriver();
        LocalDate startDate = LocalDate.of(2024, 5, 1);
        LocalDate endDate = LocalDate.of(2024, 5, 31);
        while (startDate.isBefore(endDate)) {
            hotelDates.add(startDate);
            startDate = startDate.plusDays(1);
        }
    }

    @Test
    @Parameters({"Atlanta", "Orlando", "Sacramento", "Miami", "Austin"})
    public void testGetPrice(String location) {
        for (int i = 0; i < hotelDates.size(); i++) {
                //String url = buildUrl("Atlanta","Holiday Inn", hotelDates.get(i).toString(), 25, true);
                String url = buildUrl(location, "Holiday Inn", hotelDates.get(i).toString(), 25, true);
                driver.get(url);
                WebElement price = driver.findElement(By.className("amount"));
                System.out.println(location + " - " + "Holiday Inn" + " - " + hotelDates.get(i).toString() + " - $" + price.getText());
                System.out.println();
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
