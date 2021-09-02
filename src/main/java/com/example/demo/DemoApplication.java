package com.example.demo;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}

@RestController
class HomeController {
    @GetMapping({"/", "/demo"})
    public Map index() throws IOException, InterruptedException {
        Map<String, Object> map = new HashMap<>();
        map.put("HOSTNAME", System.getenv("HOSTNAME"));
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        map.put("Initial memory:", (double)memoryMXBean.getHeapMemoryUsage().getInit() /1073741824);
        map.put("Used heap memory", (double)memoryMXBean.getHeapMemoryUsage().getUsed() /1073741824);
        map.put("Max heap memory", (double)memoryMXBean.getHeapMemoryUsage().getMax() /1073741824);
        map.put("Committed memory", (double)memoryMXBean.getHeapMemoryUsage().getCommitted() /1073741824);
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

        for(Long threadID : threadMXBean.getAllThreadIds()) {
            ThreadInfo info = threadMXBean.getThreadInfo(threadID);
            map.put("Thread name", info.getThreadName());
            map.put("Thread State", info.getThreadState());
            map.put("CPU time", threadMXBean.getThreadCpuTime(threadID));
        }
        Process exec = Runtime.getRuntime().exec("cat /sys/class/thermal/thermal_zone0/temp");
        InputStream inputStream = exec.getInputStream();
        String value = new BufferedReader(new InputStreamReader(inputStream)).readLine();
        map.put("Temperature", (StringUtils.isNumeric(value) ? Double.parseDouble(value) / 1000 : value));
        exec.waitFor();
        return map;
    }
}