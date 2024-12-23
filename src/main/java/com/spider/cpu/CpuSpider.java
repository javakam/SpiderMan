package com.spider.cpu;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class CpuSpider {
    public static final String CPU_LIST="https://cpu.bmcx.com/web_system/bmcx_com_www/system/file/cpu/get_data/?lx=cpu&s=1&e=999999999&ajaxtimestamp="+System.currentTimeMillis();

    public static void main(String[] args) {
        getAllCpu();
    }

    private static void getAllCpu() {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                //.url("https://jsonplaceholder.typicode.com/posts/1")
                .url(CPU_LIST)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                // 输出响应状态码和响应体
                System.out.println("Response Code: " + response.code());
                System.out.println("Response Body: " + response.body().string());
            } else {
                System.out.println("Request not successful");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
