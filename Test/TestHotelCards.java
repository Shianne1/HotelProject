import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TestHotelCards {
    private static WebDriver driver;
    private static Connection connection;
    private static String DB_URL = "jdbc:sqlite:hotelcheckin.sqlite";

    private static List<LocalDate> hotelDates = new ArrayList<>();

    @BeforeClass
    public static void setUp() throws SQLException {
        connection = DriverManager.getConnection(DB_URL);
        //driver = new FirefoxDriver();
        driver = new ChromeDriver();
        LocalDate startDate = LocalDate.of(2024,5,1);
        LocalDate endDate = LocalDate.of(2024,10,1);
        while(startDate.isBefore(endDate)){
            hotelDates.add(startDate);
            startDate = startDate.plusDays(1);
        }
    }

    @Test
    public void testAtlanta(){
        driver.get("https://www.getaroom.com/search?amenities=&destination=Atlanta&page=1&per" +
                "_page=25&rinfo=%5B%5B18%5D%5D&sort_order=position&hide_unavailable=true&check_in=2024-05-01&check_out=2024-05-02&property_name=");

        driver.manage().window().maximize();

        for(int i = 0; i < hotelDates.size(); i++) {
            // date of stay

            // WebDriverWait waitHotel = new WebDriverWait(driver,Duration.ofSeconds(50));

            //WebElement inputHotel = waitHotel.until(ExpectedConditions.elementToBeClickable((By.xpath("/html/body/div[1]/div/section/div/div/div/div/div/div[1]/form/div/div[2]/div/div/input"))));
            //inputHotel.sendKeys("Holiday Inn", Keys.ENTER);

            WebElement checkIn = driver.findElement(By.id("check_in"));
            checkIn.clear();
            checkIn.sendKeys(hotelDates.get(i).toString());
            checkIn.submit();
            WebElement checkOut = driver.findElement(By.id("check_out"));
            checkOut.clear();
            checkOut.sendKeys(hotelDates.get(i).plusDays(1).toString());
            checkOut.submit();

            WebElement searchHotels = driver.findElement(By.id("enter-travel-dates"));
            searchHotels.submit();

            List<WebElement> cards = driver.findElements(By.className("hotel-card"));

            for(int k = 0; k < cards.size(); k++){
                try{
                    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
                    //driver.get(driver.getCurrentUrl());
                    WebElement city = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("name")));

                    String cityName = city.getText();

                    WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("city")));
                    //WebElement title = current.findElement(By.className("name"));
                    String hotelName = title.getText();

                    //WebElement priceInfo = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("amount")));
                    WebElement priceInfo = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("amount")));
                    //WebElement priceInfo = current.findElement(By.className("amount"));
                    String price = priceInfo.getText();

                    String timeStamp = (new Date()).toString();
                    //insertHotelPricesToDatabase(title.getText(),location, checkIn.getText(), checkOut.getText(), priceInfo.getText(), timeStamp);

                    System.out.println(hotelName);
                    System.out.println(cityName);
                    System.out.println(hotelDates.get(i).toString());
                    System.out.println(hotelDates.get(i).plusDays(1));
                    System.out.println(price);
                    System.out.println(timeStamp);
                    System.out.println();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            driver.switchTo().defaultContent();

        }
    }
}
