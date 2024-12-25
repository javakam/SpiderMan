package com.spider.cpu;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CpuSpider {
    public static final String CPU_LIST = "https://cpu.bmcx.com/web_system/bmcx_com_www/system/file/cpu/get_data/?lx=cpu&s=1&e=999999999&ajaxtimestamp=" + System.currentTimeMillis();
    //eg: "https://cpu.bmcx.com/5421_5895_5913_5140__cpu/"
    private static final String CPU_INFO_BASE_URL = "https://cpu.bmcx.com/";
    private static final String CPU_INFO_SUFFIX_URL = "_cpu/";
    private static final Pattern CPU_ID_PATTERN = Pattern.compile("\\d+");

    private static final Gson mGson = new Gson();
    private static final ExecutorService executor = Executors.newFixedThreadPool(4);

    public static void main(String[] args) {
        getAllCpuCounts();
    }

    private static void getAllCpuCounts() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                //.url("https://jsonplaceholder.typicode.com/posts/1")
                .url(CPU_LIST)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                // 输出响应状态码和响应体
                System.out.println("getAllCpuCounts Response Code: " + response.code());
//                System.out.println("Response Body: " + response.body().string());

                /*
               [{"id":"5493","pai_ming":1,"ming_cheng":"AMD Ryzen Threadripper PRO 7995WX","shu_zhi":"158518","bai_fen_bi":100},
                {"id":"5994","pai_ming":2,"ming_cheng":"AMD EPYC 9655P 96-Core","shu_zhi":"155878","bai_fen_bi":98.334573991597}]
                 */
                if (response.code() == 200) {
                    Type listType = new TypeToken<List<CpuBean>>() {
                    }.getType();
                    List<CpuBean> cpuList = mGson.fromJson(response.body().string(), listType);
                    System.out.println(cpuList.size());
                    getCpuInfo(cpuList);
                } else {
                    System.out.println("Request not successful");
                }
            } else {
                System.out.println("Request failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void getCpuInfo(List<CpuBean> cpuList) {
        //2024年12月24日14:55:37  取出前n条做测试
        int searchCount = 5;
        int startIndex = 0;
        int endIndex = Math.min(startIndex + searchCount, cpuList.size());
        List<CpuBean> testList = cpuList.subList(startIndex, endIndex);
        System.out.println(testList.size());

        // 异步执行网络请求
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < testList.size(); i += 4) {
            List<CpuBean> sublist = testList.subList(i, Math.min(i + 4, testList.size()));
            CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> {
                // 执行网络请求
                //"https://cpu.bmcx.com/5421_5895_5913_5140__cpu/"
                sb.setLength(0);
                sb.append(CPU_INFO_BASE_URL);
                for (CpuBean cpu : sublist) {
                    sb.append(cpu.getId()).append('_');
                }
                sb.append(CPU_INFO_SUFFIX_URL);
                System.out.println(sb.toString());
                parseCpuInfoHtml(sb.toString());
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
        }

        // 每次取出四个元素进行处理
        /*
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < testList.size(); i += 4) {
            List<CpuBean> sublist = testList.subList(i, Math.min(i + 4, testList.size()));
            //"https://cpu.bmcx.com/5421_5895_5913_5140__cpu/"
            sb.setLength(0);
            sb.append(CPU_INFO_BASE_URL);
            for (CpuBean cpu : sublist) {
                sb.append(cpu.getId()).append('_');
            }
            sb.append(CPU_INFO_SUFFIX_URL);
            System.out.println(sb.toString());
        }
        */
    }


    /*
   所有CPU
   String CPU_LIST="https://cpu.bmcx.com/web_system/bmcx_com_www/system/file/cpu/get_data/?lx=cpu&s=1&e=999999999&ajaxtimestamp="+System.currentTimeMillis();

   查询 4 个CPU数据
   https://cpu.bmcx.com/5421_5895_5913_5140__cpu/

   jsoup
   https://blog.csdn.net/justLym/article/details/105715516
    */
    private static void parseCpuInfoHtml(String url) {
        //String url = "https://cpu.bmcx.com/5421_5895_5913_5140__cpu/";
        try {
            Document doc = Jsoup.connect(url).get();
            //Document parse = Jsoup.parse(new URL(url), 1000 * 10);

            Elements tbodys = doc.select("tbody");
            //System.out.println(tbodys.size());
            if (tbodys.isEmpty()) {
                System.out.println("没有数据");
                return;
            }

            Element tbody = tbodys.get(1);
            //html原始数据
            System.out.println(tbody.html());
            Elements trs = tbody.select("tr");
            LinkedHashMap<Integer, CpuInfoBean> map = new LinkedHashMap<>();
            for (Element tr : trs) {
                Elements tds = tr.select("td");
                final String tdText = Objects.requireNonNull(tds.first()).text();
                //System.out.println(tdText);
                if (tdText.equalsIgnoreCase("名称")) {
                    tds.remove(0);
                    // <td bgcolor="#FFFFFF"><a href="/5421__cpu/" target="_blank">AMD Ryzen 5 7500F</a></td>
                    for (int i = 0; i < tds.size(); i++) {
                        Element td = tds.get(i);
                        CpuInfoBean cpu = new CpuInfoBean();
                        final String href = td.select("a[href]").attr("href");
                        if (href.length() >= 5) {//多个查询
                            int cpuId = Integer.parseInt(href.substring(1, 5));
                            cpu.setCpuId(cpuId);
                            cpu.setName(td.text());
                            map.put(i, cpu);
                        } else {//单个查询
                            System.out.println("Invalid href: " + href.length());
                            //单个查询“名称”部分的html代码中没有id数据需要从传入的url进行解析
                            Matcher matcher = CPU_ID_PATTERN.matcher(url);
                            if (matcher.find()) {
                                //System.out.println(td.outerHtml());

                                String name = td.select("input").attr("value");
                                cpu.setCpuId(Integer.parseInt(matcher.group()));
                                cpu.setName(name);
                                map.put(i, cpu);
                                //System.out.println(cpu.getCpuId() + " ; " + cpu.getName());
                            } else {
                                System.out.println("Invalid url: " + url);
                            }
                        }
                    }
                } else if (tdText.equalsIgnoreCase("性能排名")) {
                    for (int i = 1; i < tds.size(); i++) {
                        map.get(i - 1).setPerformanceRank(tds.get(i).text());
                    }
                } else if (tdText.equalsIgnoreCase("得分")) {
                    for (int i = 1; i < tds.size(); i++) {
                        map.get(i - 1).setScore(tds.get(i).text());
                    }
                } else if (tdText.equalsIgnoreCase("TDP")) {
                    for (int i = 1; i < tds.size(); i++) {
                        map.get(i - 1).setTdp(tds.get(i).text());
                    }
                } else if (tdText.equalsIgnoreCase("TDP Down")) {
                    for (int i = 1; i < tds.size(); i++) {
                        map.get(i - 1).setTdpDown(tds.get(i).text());
                    }
                } else if (tdText.equalsIgnoreCase("插槽类型")) {
                    for (int i = 1; i < tds.size(); i++) {
                        map.get(i - 1).setSlotType(tds.get(i).text());
                    }
                } else if (tdText.equalsIgnoreCase("核心数")) {
                    for (int i = 1; i < tds.size(); i++) {
                        map.get(i - 1).setCoreNumber(tds.get(i).text());
                    }
                } else if (tdText.equalsIgnoreCase("线程数")) {
                    for (int i = 1; i < tds.size(); i++) {
                        map.get(i - 1).setThreadNumber(tds.get(i).text());
                    }
                } else if (tdText.equalsIgnoreCase("主频")) {
                    for (int i = 1; i < tds.size(); i++) {
                        map.get(i - 1).setFrequency(tds.get(i).text());
                    }
                } else if (tdText.equalsIgnoreCase("睿频")) {
                    for (int i = 1; i < tds.size(); i++) {
                        map.get(i - 1).setTurboFrequency(tds.get(i).text());
                    }
                } else if (tdText.equalsIgnoreCase("发布时间")) {
                    for (int i = 1; i < tds.size(); i++) {
                        map.get(i - 1).setReleased(tds.get(i).text());
                    }
                }
                //
            }

            //录入数据库
            System.out.println(url);
            for (CpuInfoBean cpu : map.values()) {
                System.out.println(cpu.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
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
