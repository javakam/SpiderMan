package com.spider;

import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Example program to list links from a URL.
 *
 * https://www.open-open.com/jsoup/example-list-links.htm
 */
public class ListLinks {
    private static final String testUrl="http://news.ycombinator.com";
//    private static final String testUrl="https://www.ixigua.com/home/742906704958820/";
//    private static final String testUrl="https://www.ixigua.com/home/742906704958820/?is_new_connect=0&is_new_user=0";

    public static void main(String[] args) throws IOException {
        String url = testUrl;
        print("Fetching %s...", url);

        Document doc = Jsoup.connect(url).get();
        Elements links = doc.select("a[href]");
        Elements media = doc.select("[src]");
        Elements imports = doc.select("link[href]");

        print("\nMedia: (%d)", media.size());
        for (Element src : media) {
            if (src.tagName().equals("img")) {
                print(" * %s: <%s> %sx%s (%s)",
                        src.tagName(), src.attr("abs:src"), src.attr("width"), src.attr("height"),
                        trim(src.attr("alt"), 20));
            } else {
                print(" * %s: <%s>", src.tagName(), src.attr("abs:src"));
            }
        }

        print("\nImports: (%d)", imports.size());
        for (Element link : imports) {
            print(" * %s <%s> (%s)", link.tagName(),link.attr("abs:href"), link.attr("rel"));
        }

        print("\nLinks: (%d)", links.size());
        for (Element link : links) {
            print(" * a: <%s>  (%s)", link.attr("abs:href"), trim(link.text(), 35));
        }
    }

    private static void print(String msg, Object... args) {
        System.out.println(String.format(msg, args));
    }

    private static String trim(String s, int width) {
        if (s.length() > width) {
            return s.substring(0, width-1) + ".";
        } else {
            return s;
        }
    }
}