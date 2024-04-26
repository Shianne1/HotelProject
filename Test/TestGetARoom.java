import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TestGetARoom {
    private static WebDriver driver;

    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;
    private static final String DATE_PATTERN = "yyyy-MM-dd";
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN);

    private final String[] cities = {"Washington DC", "Tokyo", "Chicago", "Paris", "New York City"};
    private final String[] hotelChains = {"Park Hyatt", "Holiday Inn", "Ritz", "Best Western", "Four Seasons"};

    @BeforeClass
    public static void setUp()
    {
        driver = new ChromeDriver();
        driver.manage().window().setSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
    }

    @Test
    public void testGetPrice()
    {
        String url = buildUrl("Paris", "Holiday Inn", "2024-05-01", 25, true);
        driver.get(url);
        WebElement price = driver.findElement(By.className("amount"));
        System.out.println("$" + price.getText());
    }

    private String buildUrl(String city, String hotel, String strCheckIn)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN);
        LocalDate checkIn = LocalDate.parse(strCheckIn);
        LocalDate checkOut = checkIn.plusDays(1);

        String baseUrl = "https://www.getaroom.com/search?";
        String cityFixed = city.replace(" ", "+");
        String hotelFixed = hotel.replace(" ", "+");

        return baseUrl + "destination=" + cityFixed +
                "&hide_unavailable=true" +
                "&check_in=" + checkIn.format(formatter).replace(" ", "") +
                "&check_out=" + checkOut.format(formatter).replace(" ", "") +
                "&property_name=" + hotelFixed;
    }

    private String buildUrl(String city, String hotel, String strCheckIn, int per_page, boolean sortByPrice)
    {
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
