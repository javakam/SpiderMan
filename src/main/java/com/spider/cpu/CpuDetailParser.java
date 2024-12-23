package com.spider.cpu;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Objects;

public class CpuDetailParser {

    public static void main(String[] args) {
        parseCpuDetailHtml();
    }

    /*
    所有CPU
    public static final String CPU_LIST="https://cpu.bmcx.com/web_system/bmcx_com_www/system/file/cpu/get_data/?lx=cpu&s=1&e=999999999&ajaxtimestamp="+System.currentTimeMillis();

    查询 4 个CPU数据
    https://cpu.bmcx.com/5421_5895_5913_5140__cpu/

    jsoup
    https://blog.csdn.net/justLym/article/details/105715516
     */
    private static void parseCpuDetailHtml() {
        String url = "https://cpu.bmcx.com/5421_5895_5913_5140__cpu/";
        try {
            Document doc = Jsoup.connect(url).get();
            //Document parse = Jsoup.parse(new URL(url), 1000 * 10);

            Elements tbodys = doc.select("tbody");
            //System.out.println(tbodys.size());
            if (tbodys.isEmpty()) {
                return;
            }

            Element tbody = tbodys.get(1);
            System.out.println(tbody.html());
            Elements trs = tbody.select("tr");
            LinkedHashMap<Integer, CpuBean> map = new LinkedHashMap<>();
            for (Element tr : trs) {
                Elements tds = tr.select("td");
                final String tdText = Objects.requireNonNull(tds.first()).text();
                //System.out.println(tdText);
                if (tdText.equalsIgnoreCase("名称")) {
                    tds.remove(0);
                    // <td bgcolor="#FFFFFF"><a href="/5421__cpu/" target="_blank">AMD Ryzen 5 7500F</a></td>
                    for (int i = 0; i < tds.size(); i++) {
                        final Element td = tds.get(i);
                        CpuBean cpu = new CpuBean();
                        final String href = td.select("a[href]").attr("href");
                        int cpuId = Integer.parseInt(href.substring(1, 5));
                        cpu.setCpuId(cpuId);
                        cpu.setName(td.text());
                        //System.out.println(cpu.toString());
                        map.put(i, cpu);
                    }
                } else if (tdText.equalsIgnoreCase("性能排名")) {
                    tds.remove(0);
                    for (int i = 0; i < tds.size(); i++) {
                        map.get(i).setPerformanceRank(tds.get(i).text());
                    }
                } else if (tdText.equalsIgnoreCase("得分")) {
                    tds.remove(0);
                    for (int i = 0; i < tds.size(); i++) {
                        map.get(i).setScore(tds.get(i).text());
                    }
                } else if (tdText.equalsIgnoreCase("TDP")) {
                    tds.remove(0);
                    for (int i = 0; i < tds.size(); i++) {
                        map.get(i).setTdp(tds.get(i).text());
                    }
                } else if (tdText.equalsIgnoreCase("TDP Down")) {
                    tds.remove(0);
                    for (int i = 0; i < tds.size(); i++) {
                        map.get(i).setTdpDown(tds.get(i).text());
                    }
                } else if (tdText.equalsIgnoreCase("插槽类型")) {
                    tds.remove(0);
                    for (int i = 0; i < tds.size(); i++) {
                        map.get(i).setSlotType(tds.get(i).text());
                    }
                } else if (tdText.equalsIgnoreCase("核心数")) {
                    tds.remove(0);
                    for (int i = 0; i < tds.size(); i++) {
                        map.get(i).setCoreNumber(tds.get(i).text());
                    }
                } else if (tdText.equalsIgnoreCase("线程数")) {
                    tds.remove(0);
                    for (int i = 0; i < tds.size(); i++) {
                        map.get(i).setThreadNumber(tds.get(i).text());
                    }
                } else if (tdText.equalsIgnoreCase("主频")) {
                    tds.remove(0);
                    for (int i = 0; i < tds.size(); i++) {
                        map.get(i).setFrequency(tds.get(i).text());
                    }
                } else if (tdText.equalsIgnoreCase("睿频")) {
                    tds.remove(0);
                    for (int i = 0; i < tds.size(); i++) {
                        map.get(i).setTurboFrequency(tds.get(i).text());
                    }
                } else if (tdText.equalsIgnoreCase("发布时间")) {
                    tds.remove(0);
                    for (int i = 0; i < tds.size(); i++) {
                        map.get(i).setReleased(tds.get(i).text());
                    }
                }
                //
            }

            //Log
            System.out.println(url);
            for (CpuBean cpu : map.values()) {
                System.out.println(cpu.toString());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //Okhttp 方案
    private static void parse() {
        OkHttpClient client = new OkHttpClient();
        String url = "https://cpu.bmcx.com/5421_5895_5913_5140__cpu/";

        // 构建Request对象
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                // 获取响应体中的HTML字符串
                String html = response.body().string();
                System.out.println(html);

                // 使用Jsoup解析HTML
                Document doc = Jsoup.parse(html);

                // 根据需要选择元素。这里仅作为例子，假设我们想要获取所有标题为h2的文本。
                Elements h2Elements = doc.select("h2");
                for (Element element : h2Elements) {
                    System.out.println(element.text());
                }

                // 如果您知道具体的标签或者CSS选择器，可以直接替换上面的选择器来获取您需要的数据。
                // 例如，如果您想要获取表格内的某些数据，可以使用类似于下面的选择器：
                // Elements tableRows = doc.select("table tr");
                // 然后遍历这些行，进一步选择td元素等。

            } else {
                System.out.println("Request not successful");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}