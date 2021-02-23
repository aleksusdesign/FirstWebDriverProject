package avic;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import java.util.List;
import static org.openqa.selenium.By.xpath;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class AvicTests {

    private WebDriver driver;

    @BeforeTest
    public void profileSetUp() {
        System.setProperty("webdriver.chrome.driver", "src\\main\\resources\\chromedriver.exe");
    }

    @BeforeMethod
    public void testsSetUp() {
        driver = new ChromeDriver();//создаем экзаемпляр хром драйвера
        driver.manage().window().maximize();//открыли браузер на весь экран
        driver.get("https://avic.ua/");//открыли сайт
    }
    /*
        Тест проверяет вход на сайт
         */
    @Test(priority = 1)
    public void checkLogin() {
        driver.findElement(xpath("//div[contains(@class,'bottom__login')]//i[@class='icon icon-user-big']")).click();//переходим на страницу логина
        new WebDriverWait(driver, 30).until(
                webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
        driver.findElement(xpath("//input[@name='login']")).sendKeys("aleksus20103@gmail.com");//вводим login
        driver.findElement(xpath("//input[@name='password']")).sendKeys("adminadmin");//вводим password
        driver.findElement(xpath("//button[contains(@class,'submit main')]")).click();//нажимаем на кнопку Войти
        assertEquals(driver.findElement(xpath("//div[@class='ttl js_title']")).getText(),""); //проверяем что появился попам с сообщением об успешном входе
    }
    /*
        Тест проверяет добавление товара в избранное(желаемое)
         */
    @Test(priority = 2)
    public void checkAddFavourite() {
        checkLogin();
        driver.findElement(xpath("//div[@id='modalAlert']//button[@title='Close']")).click();
        driver.findElement(xpath("//span[@class='sidebar-item']")).click();//каталог товаров
        driver.findElement(xpath("//ul[contains(@class,'sidebar-list')]//a[contains(@href, 'elektronika')]")).click();//Раздел Компьютеры
        driver.findElement(xpath("//div[@class='brand-box__title']/a[contains(@href,'gotovyie-pk')]")).click();//Системные блоки
        new WebDriverWait(driver, 30).until(
                webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));//wait for page loading
        driver.findElement(xpath("//button[@data-wishlist-add='211814']")).click();//добавляем товар 211814 в избранное
        WebDriverWait wait = new WebDriverWait(driver, 30);//ждем пока не отобразится попап с уведомлением об успехе
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("modalAlert")));
        driver.findElement(xpath("//div[@id='modalAlert']//button[@title='Close']")).click();//закрываем попап
        driver.findElement(xpath("//div[contains(@class,'bottom__login')]//i[@class='icon icon-user-big']")).click(); //переходим в профиль
        driver.findElement(xpath("//a[contains(@href,'favorite')][contains(@class,'ca')]")).click(); //переходим в желаемые
        List<WebElement> elementList = driver.findElements(xpath("//div[@class='prod-cart__descr']")); //собираем желаемые товары
        assertEquals(elementList.size(), 1);//проверяем что товар добавился
        driver.findElement(xpath("//button[@data-wishlist-remove='211814']")).click();//удаляем товар из избранных для корректных последующих тестов
    }
    /*
    Тест проверяет выдачу товаров на заданный в фильтре диапазон цены
     */
    @Test(priority = 3)
    public void checkRangePrice() {
        driver.findElement(xpath("//span[@class='sidebar-item']")).click();//открываем каталог товаров
        driver.findElement(xpath("//ul[contains(@class,'sidebar-list')]//a[contains(@href, 'apple-store')]")).click();//Apple Store
        driver.findElement(xpath("//div[@class='brand-box__title']/a[contains(@href,'iphone')]")).click();//iphone
        driver.findElement(xpath("//input[contains(@class,'min')]")).clear();//очищаем min price
        driver.findElement(xpath("//input[contains(@class,'min')]")).sendKeys("40000");//вставляем min price
        driver.findElement(xpath("//input[contains(@class,'max')]")).clear();//очищаем max price
        driver.findElement(xpath("//input[contains(@class,'max')]")).sendKeys("43000");//вставляем max price
        WebDriverWait wait = new WebDriverWait(driver, 100);
        wait.until(ExpectedConditions.visibilityOfElementLocated(xpath("//div[contains(@class, 'open-f')]//a")));//ждем пока не появится кнопка показать
        driver.findElement(xpath("//div[contains(@class, 'open-f')]//a")).click();//жмем на кнопку показать
        new WebDriverWait(driver, 30).until(
                webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete")); //ждем пока не загрузится страница
        List<WebElement> elementList = driver.findElements(xpath("//div[@class='prod-cart__prise-new']"));//собрали цены в лист
        for (WebElement webElement : elementList) { //прошлись циклом и проверили что кажджая цена дейстивтельно входит в диапазон значений
            int price = Integer.parseInt(webElement.getText().split(" ")[0]);
            assertTrue(price>=40000&&price<=43000);
        }
    }

    @AfterMethod
    public void tearDown() {
        driver.close();//закрытие драйвера
    }
}
