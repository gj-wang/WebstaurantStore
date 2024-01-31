package webStaurantStore;

import java.text.MessageFormat;
import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.bonigarcia.wdm.WebDriverManager;

public class Test {
	public static void main(String[] args) throws InterruptedException {
		// Need to validate description contains this specific string
		final String searchProduct = "stainless work table";
		final String containString = "Table";
		
		WebDriverManager.chromedriver().setup();
		WebDriver driver = new ChromeDriver();
		
		// go to the target web site
		driver.get("http://www.webstaurantstore.com");
		
		// search for "stainless work table"
		driver.findElement(By.id("searchval")).sendKeys(searchProduct + "\n");
		
		// find out how many pages available
		List<WebElement> pages = driver
									.findElement(By.id("paging"))
									.findElements(By.className("inline-block"));
		
		// loop through all the pages/items
		for (int pIndex = 0; pIndex < pages.size(); pIndex++) {		
			// get all the items on a page
			List<WebElement> items = driver
										.findElement(By.id("product_listing"))
										.findElements(By.cssSelector("[data-testid='productBoxContainer']"));
			
			for(int i = 0; i < items.size(); i++) {
				WebElement item = items.get(i);
				String description = item.findElement(By.cssSelector("[data-testid='itemDescription']")).getText();

				// Check if the product description contains the word "Table", if not, send warning to console
				if(!description.contains(containString)) {
					System.out.println("=========");
					System.out.println("\nItem " + description + " does not conatian word " + containString + "\n");
				}
			}

			// Add the last product of the page to the cart
			WebElement lastItem = items.get(items.size()-1);
			lastItem.findElement(By.name("addToCartButton")).click();

			// click "next page"
			driver.findElement(By.id("paging"))
				  .findElement(By.cssSelector(".rounded-r-md"))
				  .click();
			
			// make sure the page is turned
			int pNumber = pIndex + 2;  // page number is 2 ahead of index
			if (pNumber > pages.size()) {
				break;
			}
			String pageStr = MessageFormat.format("current page, page {0}", pNumber);
			String newPage = driver.findElement(By.cssSelector("a[aria-label^='current page']")).getAttribute("aria-label");
			String searchTxt = "a[aria-label='"+pageStr+"']";

			// wait for up to 5 seconds for the new page to show up
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(searchTxt)));
		}

		// Go to cart
		driver.findElement(By.cssSelector("[data-testid='cart-button']")).click();
		
		// empty cart
		driver.findElement(By.cssSelector("button.emptyCartButton")).click();
		
		// wait for up to 5 seconds for the modal to show up
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("footer[data-testid='modal-footer']")));
		
		// click empty button
		WebElement modal = driver.findElement(By.cssSelector("footer[data-testid='modal-footer']"));
		modal.findElement(By.xpath("button[contains(text(),'Empty')]")).click();
		
		driver.quit();
	}
}
