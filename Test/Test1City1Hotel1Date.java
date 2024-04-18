import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Test1City1Hotel1Date {
    private static WebDriver driver;
    private static final String GET_A_ROOM = "https://www.getaroom.com/";
    private static Connection connection;
    private static String DB_URL = "jdbc:sqlite:hotelcheckin.sqlite";

    private static List<LocalDate> hotelDates = new ArrayList<>();

    @BeforeClass
    public static void setUp() throws SQLException {
        connection = DriverManager.getConnection(DB_URL);
        driver = new FirefoxDriver();
        LocalDate startDate = LocalDate.of(2024,5,1);
        LocalDate endDate = LocalDate.of(2024,5,2);
        while(startDate.isBefore(endDate)){
            hotelDates.add(startDate);
            startDate = startDate.plusDays(1);
        }
    }

    @Test
    public void testAtlantaAndHolidayInn(){
        driver.get(GET_A_ROOM);
        WebElement destination = driver.findElement(By.id("destination"));
        destination.clear();
        destination.sendKeys("Atlanta", Keys.ENTER);


    }

    private void helperMethodLoadURL(){
        driver.getCurrentUrl();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.until(ExpectedConditions.urlMatches(driver.getCurrentUrl()));

    }
}