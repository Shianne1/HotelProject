import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Test1City1Hotel1Date {
    private static WebDriver driver;
    private static final String GET_A_ROOM = "https://www.getaroom.com/search?";
    private static Connection connection;
    private static String DB_URL = "jdbc:sqlite:hotelcheckin.sqlite";

    private static List<LocalDate> hotelDates = new ArrayList<>();

    @BeforeClass
    public static void setUp() throws SQLException {
        connection = DriverManager.getConnection(DB_URL);
        //driver = new FirefoxDriver();
        driver = new ChromeDriver();
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
        helperMethodLoadURL();
    }

    private void helperMethodLoadURL(){
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.getCurrentUrl();
        //driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
        //driver.getCurrentUrl();
        //enterHotel();
        //grabHotelCards();
        enterDates();
        enterHotel();

        //driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        //driver.getCurrentUrl();
        //grabHotelCards();



        /*
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.getCurrentUrl();
        enterHotel();

         */

    }

    private void enterDates(){
        driver.getCurrentUrl();

        WebElement checkIn = driver.findElement(By.cssSelector("#check_in"));
        //checkIn.clear();
        checkIn.sendKeys("05/01/2024", Keys.ENTER);
        checkIn.submit();

        WebElement checkOut = driver.findElement(By.cssSelector("#check_out"));
        //checkOut.clear();
        checkOut.sendKeys("05/02/2024", Keys.ENTER);
        checkOut.submit();

        //WebElement searchHotels = driver.findElement(By.id("enter-travel-dates"));
        //searchHotels.click();
        //driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
        //driver.getCurrentUrl();
        //enterHotel();
    }

    private void enterHotel(){
        driver.getCurrentUrl();
        WebElement inputHotel = driver.findElement(By.cssSelector("#hotelName"));
        inputHotel.clear();
        inputHotel.sendKeys("Holiday Inn", Keys.ENTER);
    }

    private void grabHotelCards(){
        List<WebElement> cards = driver.findElements(By.className("hotel-card"));

        for(int i = 0; i < cards.size(); i++){
            // get  hotel name
            // hotel price
            // location
            WebElement current = cards.get(i);
            WebElement title = current.findElement(By.className("name"));
            WebElement city = current.findElement(By.className("city"));
            WebElement priceInfo = current.findElement(By.className("amount"));

            String timeStamp = (new Date()).toString();
            //insertHotelPricesToDatabase(title.getText(),location, checkIn.getText(), checkOut.getText(), priceInfo.getText(), timeStamp);

            System.out.println(title.getText());
            //System.out.println(location);
            System.out.println(city.getText());
            System.out.println(hotelDates.toString());
            System.out.println(hotelDates.toString());
            System.out.println(priceInfo.getText());
            System.out.println(timeStamp);
            System.out.println();
        }
        System.out.println(cards.size());
    }



    /*
    https://www.getaroom.com/search?amenities=&destination=Atlanta&page=1&per_page=25&rinfo=%5B%5B18%5D%5D&sort_order=position&hide_unavailable=true&check_in=null&check_out=null&property_name=

    https://www.getaroom.com/search?page=1&per_page=25&destination=Atlanta&rinfo=%5B%5B18%5D%5D&amenities=&sort_order=position&hide_unavailable=true&check_in=null&check_out=null&property_name=Hilton

    https://www.getaroom.com/search?page=1&per_page=25&destination=Atlanta&rinfo=%5B%5B18%5D%5D&amenities=&sort_order=position&hide_unavailable=true&check_in=null&check_out=null&property_name=Holiday%20Inn


     */
}
