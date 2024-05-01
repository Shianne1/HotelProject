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
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Duration;
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
    private final String[] hotelChains = {"Holiday Inn", "Hyatt Regency", "Hilton", "Comfort Suites", "Hampton Inn"};

    private final String[] cities = {"Sacramento", "Atlanta", "Orlando", "Miami", "Austin"};

    @BeforeClass
    public static void setUp() throws SQLException {
        connection = DriverManager.getConnection(DB_URL);
        //driver = new ChromeDriver();
        driver = new FirefoxDriver();
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
        for(int k = 0; k < hotelChains.length; k ++) {
            for (int i = 0; i < hotelDates.size(); i++) {
                String url = buildUrl(location, hotelChains[k].toString() , hotelDates.get(i).toString(), 25, true);
                driver.get(url);
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
                WebElement price = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("amount")));
                //WebElement price = driver.findElement(By.cssSelector("#amount"));
                System.out.println(location + " - " + hotelChains[k].toString() + " - " + hotelDates.get(i).toString() + " - $" + price.getText());
                System.out.println();
            }
        }

    }

    @Test
    @Parameters({"Hyatt Regency", "Hilton", "Hampton Inn", "Holiday Inn", "Comfort Suites" })
    public void testGetHotelPrice(String hotels){
        for(int k = 0; k < cities.length; k ++) {
            for (int i = 0; i < hotelDates.size(); i++) {
                String url = buildUrl(cities[k].toString(), hotels , hotelDates.get(i).toString(), 25, true);
                //driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
                driver.get(url);
                driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
               // WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                if(!driver.findElement(By.id("results_list_lowest_price")).isEnabled()){
                    hotelDates.get(i + 1);
                } else {
                    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                    WebElement price = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("amount")));
                    System.out.println(cities[k].toString() + " - " + hotels + " - " + hotelDates.get(i).toString() + " - $" + price.getText());
                    System.out.println();
                }
                /*
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                WebElement price = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("amount")));

                //WebElement price = driver.findElement(By.cssSelector("#amount"));
                System.out.println(cities[k].toString() + " - " + hotels + " - " + hotelDates.get(i).toString() + " - $" + price.getText());
                System.out.println();

                 */
            }
        }
    }

    private void getHotel(String location){
        for(int k = 0; k < hotelChains.length; k ++) {
            for (int i = 0; i < hotelDates.size(); i++) {
                String url = buildUrl(location, hotelChains[k].toString() , hotelDates.get(i).toString(), 25, true);
                driver.get(url);
               // driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));
               // WebElement price = driver.findElement(By.className("amount"));

                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
                WebElement price = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("amount")));
                //WebElement price = driver.findElement(By.cssSelector("#amount"));
                System.out.println(location + " - " + hotelChains[k].toString() + " - " + hotelDates.get(i).toString() + " - $" + price.getText());
                System.out.println();
            }
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
