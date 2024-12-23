package com.spider.zdm;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class JsoupExample {
    public static void main(String[] args) {
        String url = "https://search.smzdm.com/?c=home&s=%E7%BD%97%E6%8A%80%E9%BC%A0%E6%A0%87&order=score&cate_id=163&v=b&mx_v=b"; // 替换为实际的URL
        try {
            // 连接到URL并获取HTML文档
            Document doc = Jsoup.connect(url).get();
            
            // 使用选择器查找包含"Hero"文本的标题标签
            Elements titles = doc.select(":containsOwn(Hero)");
            
            // 遍历找到的元素
            for (Element title : titles) {
                // 检查元素是否是标题标签（例如 h1, h2, h3 等）
                if (title.tagName().matches("h[1-6]")) {
                    // 打印标题标签及其文本内容
                    System.out.println("Title: " + title.tagName() + " - Text: " + title.text());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
