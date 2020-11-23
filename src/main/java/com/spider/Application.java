package com.spider;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.Keys;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 修改“小视频首页”和“存放目录”之后运行main函数即可
 */
@SuppressWarnings("all")
public class Application {

    private static final String BASE_URL = "https://www.ixigua.com/";
    /**
     * 小视频首页，按需修改
     */
    private static final String MAIN_PAGE_URL = BASE_URL + "home/3276166340814919/hotsoon/";

    /**
     * 存放目录，按需修改
     */
    private static final String FILE_SAVE_DIR = "D:\\TempFiles";

    /**
     * 线程池，按需修改并行数量。实际开发请自定义避免OOM
     */
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    /**
     * 谷歌浏览器参数
     */
    private static final ChromeOptions CHROME_OPTIONS = new ChromeOptions();

    private final static String CHROME_DRIVER_PATH = "src\\main\\resources\\chromedriver.exe";


    static {
        // 驱动位置
        System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_PATH);
        // 避免被浏览器检测识别
        CHROME_OPTIONS.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
    }

    /**
     * main函数
     * <p>
     * https://www.open-open.com/jsoup/selector-syntax.htm
     *
     * @param args 运行参数
     * @throws InterruptedException 睡眠中断异常
     */
    public static void main(String[] args) throws InterruptedException {
        // 获取小视频列表的div元素，批量处理
        String result = getMainPageSource();
        System.out.println(result);
        Document mainDoc = Jsoup.parse(result);
        Elements divItems = mainDoc.select("div[class=\"VerticalFeedCard\"]");
        // 这里使用CountDownLatch关闭线程池，只是避免执行完一直没退出
        CountDownLatch countDownLatch = new CountDownLatch(divItems.size());
        System.out.println("divItems size : " + divItems.size());
        divItems.forEach(item ->
                EXECUTOR.execute(() -> {
                    try {
                        Application.handleElement(item);
                        //Application.handleItem(item);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    countDownLatch.countDown();
                })
        );
        countDownLatch.await();
        EXECUTOR.shutdown();
        System.exit(0);
    }

    /**
     * 获取首页内容
     *
     * @return 首页内容
     * @throws InterruptedException 睡眠中断异常
     */
    private static String getMainPageSource() throws InterruptedException {
        ChromeDriver driver = new ChromeDriver(CHROME_OPTIONS);
//        ChromeDriver driver = new ChromeDriver(createChromeDriverOptions());
        try {
            driver.get(MAIN_PAGE_URL);
            long waitTime = Double.valueOf(Math.max(3, Math.random() * 5) * 1000).longValue();
            TimeUnit.MILLISECONDS.sleep(waitTime);
            long timeout = 5_000;//30_000
            // 循环下拉，直到全部加载完成或者超时
            do {
                new Actions(driver).sendKeys(Keys.END).perform();
                TimeUnit.MILLISECONDS.sleep(waitTime);
                timeout -= waitTime;
            } while (!driver.getPageSource().contains("已经到底部，没有新的内容啦") && timeout > 0);
            return driver.getPageSource();
        } finally {
            driver.close();
        }
    }

    private static void handleElement(Element div) throws Exception {
        String href = div.getElementsByTag("a").first().attr("href");
        System.out.println("video -> " + BASE_URL + href);
    }

    /**
     * 处理每个小视频
     *
     * @param div 小视频div标签元素
     * @throws Exception 各种异常
     */
    private static void handleItem(Element div) throws Exception {
        String href = div.getElementsByTag("a").first().attr("href");
        String src = getVideoUrl(BASE_URL + href);
        System.out.println("video -> " + src);

        // 有些blob开头的（可能还有其它）暂不处理
        if (src.startsWith("//")) {
            Connection.Response response = Jsoup.connect("https:" + src)
                    // 解决org.jsoup.UnsupportedMimeTypeException: Unhandled content type. Must be text/*, application/xml, or application/xhtml+xml. Mimetype=video/mp4, URL=
                    .ignoreContentType(true)
                    // The default maximum is 1MB.
                    .maxBodySize(100 * 1024 * 1024)
                    .execute();

            Files.write(Paths.get(FILE_SAVE_DIR, href.substring(1) + ".mp4"), response.bodyAsBytes());
        } else {
            System.out.println("无法解析的src：[" + src + "]");
        }
    }

    /**
     * 获取小视频实际链接
     *
     * @param itemUrl 小视频详情页
     * @return 小视频实际链接
     * @throws InterruptedException 睡眠中断异常
     */
    private static String getVideoUrl(String itemUrl) throws InterruptedException {
        ChromeDriver driver = new ChromeDriver(CHROME_OPTIONS);
        try {
            driver.get(itemUrl);
            long waitTime = Double.valueOf(Math.max(5, Math.random() * 10) * 1000).longValue();
            long timeout = 50_000;
            Element v;
            /**
             * 循环等待，直到链接出来
             * ※这里可以考虑浏览器驱动自带的显式等待()和隐士等待
             */
            do {
                TimeUnit.MILLISECONDS.sleep(waitTime);
                timeout -= waitTime;
            } while ((Objects.isNull(v = Jsoup.parse(driver.getPageSource()).getElementById("vs"))
                    || Objects.isNull(v = v.getElementsByTag("video").first()))
                    && timeout > 0);

            return v.attr("src");
        } finally {
            driver.close();
        }
    }

    private static ChromeOptions createChromeDriverOptions() {
        System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_PATH);
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("test-type");
        //	chromeOptions.addArguments("start-maximized");
        chromeOptions.addArguments("--js-flags=--expose-gc");
        chromeOptions.addArguments("--enable-precise-memory-info");
        chromeOptions.addArguments("--disable-popup-blocking");
        chromeOptions.addArguments("--disable-default-apps");
        chromeOptions.addArguments("test-type=browser");
        chromeOptions.addArguments("disable-infobars");
        //chromeOptions.addArguments("--headless");
        //chromeOptions.addArguments("window-size=1200x600");
        return chromeOptions;
    }

}