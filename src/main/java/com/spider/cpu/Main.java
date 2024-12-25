package com.spider.cpu;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) {
        String html = "<html><head></head><body><table><tr><td bgcolor=\"#FFFFFF\">AMD Ryzen Threadripper PRO 7995WX　　<label><input type=\"checkbox\" id=\"a5493\" value=\"AMD Ryzen Threadripper PRO 7995WX\" onclick=\"dblist(5493,event)\">添加对比</label></td></tr></table></body></html>";
        Document doc = Jsoup.parse(html);
        System.out.println(doc.outerHtml() + "\n");

        // 使用CSS选择器来选择元素
        Elements tds = doc.select("td");
        Element element = tds.get(0);
        System.out.println(element.outerHtml() + "\n");
        //todo 2024年12月24日16:48:42 提取出td字段
        System.out.println(element.select("input").attr("value") + "\n");

    }

    public static void main2(String[] args) {
        // 假设这是你的URL
        String url = "https://cpu.bmcx.com/5493_5994_5510_5551__cpu/";

        // 使用正则表达式来提取数字
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(url);
        while (matcher.find()) {
            String number = matcher.group();
            // 输出提取的数字
            System.out.println(number);
        }
    }

    public static void main1(String[] args) {
        // 创建一个包含10个元素的List
        List<Integer> list = new ArrayList<>();
        for (int i = 1; i <= 13; i++) {
            list.add(i);
        }

        // 创建一个线程池
        ExecutorService executor = Executors.newFixedThreadPool(4);

        // 异步执行网络请求
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (int i = 0; i < list.size(); i += 4) {
            List<Integer> sublist = list.subList(i, Math.min(i + 4, list.size()));
            CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> {
                // 执行网络请求
                makeNetworkRequest(sublist);
                return null;
            }, executor);
            futures.add(future);
        }

        // 等待所有网络请求完成
        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        try {
            allOf.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            // 关闭线程池
            executor.shutdown();
        }
    }

    private static void makeNetworkRequest(List<Integer> sublist) {
        // 模拟网络请求
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Network request completed for items: " + sublist);
    }
}
