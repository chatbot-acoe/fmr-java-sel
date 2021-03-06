package com.mindtree.testscripts;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

public class GoogleDockerTest {

	static ThreadLocal<RemoteWebDriver> driver = new ThreadLocal<>();
	static ExtentReports extent;
	static ThreadLocal<ExtentTest> logger = new ThreadLocal<>();

	final static String APP_URL = "https://www.google.co.in/";

	final static String SELENIUM_HUB_URL = "http://linux-azure.eastus2.cloudapp.azure.com:4444/wd/hub";

	@BeforeClass
	public void beforeClass() {
		extent = new ExtentReports(System.getProperty("user.dir") + "/test-output/ExtentReport.html", true);
		extent.addSystemInfo("Reports Generated By: ", "Akshata Vernekar");
		// extent.loadConfig(new File(System.getProperty("user.dir") +
		// File.separator + "extent-config.xml"));
		extent.loadConfig(new File("extent-config.xml"));

	}

	@Test
	public void googleTest() throws InterruptedException, IOException {
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--start-maximized");
		DesiredCapabilities dc = DesiredCapabilities.chrome();
		dc.setCapability(ChromeOptions.CAPABILITY, options);
		dc.setCapability("name", "Google Test");

		driver.set(new RemoteWebDriver(new URL(SELENIUM_HUB_URL), dc));

		logger.set(extent.startTest("Google Test", "This Test is test Google application"));
		logger.get().log(LogStatus.INFO, "Selenium HUB URL :  " + SELENIUM_HUB_URL);

		driver.get().get(APP_URL);
		logger.get().log(LogStatus.PASS, "Successfully launched Google");
		captureScreenshot(driver.get(), logger.get());

	}

	@AfterMethod
	public void afterMethod(ITestResult result) throws IOException, InterruptedException {
		if (result.getStatus() == ITestResult.FAILURE) {
			Cookie cookie = new Cookie("zaleniumTestPassed", "false");
			driver.get().manage().addCookie(cookie);

			captureScreenshot(driver.get(), logger.get());
			logger.get().log(LogStatus.FAIL, "Test Case Failed is " + result.getName());
			logger.get().log(LogStatus.FAIL, "Test Case Failed is " + result.getThrowable());

		} else if (result.getStatus() == ITestResult.SKIP) {
			logger.get().log(LogStatus.SKIP, "Test Case Skipped is " + result.getName());
		} else {
			Cookie cookie = new Cookie("zaleniumTestPassed", "true");
			driver.get().manage().addCookie(cookie);
		}
		extent.endTest(logger.get());

		driver.get().quit();

	}

	@AfterClass
	public void afterclass() {
		extent.flush();
	}

	public synchronized static void captureScreenshot(RemoteWebDriver driver, ExtentTest logger)
			throws IOException, InterruptedException {
		String base64 = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
		logger.log(LogStatus.INFO, "Snapshot below: " + logger.addBase64ScreenShot("data:image/png;base64," + base64));
	}

}
