package top.hrfeat.demo.service.impl;

import top.hrfeat.demo.service.IDemoService;
import top.hrfeat.mvcframework.annotation.HRService;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: 81247
 * @Description: ${Description}
 */
@HRService("iDemoService")
public class DemoServiceImpl implements IDemoService     {
    public static final AtomicInteger atomicInteger = new AtomicInteger(0);
    public static int value = 0;


    public String get(String name) {
        ConcurrentHashMap<Object, Object> hashMap = new ConcurrentHashMap();
        CopyOnWriteArrayList arrayList = new CopyOnWriteArrayList();
        hashMap.put("test", "sdads");
        for (Object o : hashMap.keySet()) {
        }
        int i = 0;
        atomicInteger.getAndIncrement();
//        atomicIntegerTest();

        return name+":hello godfather"+ atomicInteger;
    }

    public static void main(String[] args) throws InterruptedException {
        /*ExecutorService executorService = Executors.newFixedThreadPool(10000);
        for (int i = 0; i < 10000; i++) {
            executorService.execute(() -> {
                for (int j = 0; j < 4; j++) {
                    System.out.println(value++);
                }
            });
        }
        executorService.shutdown();
        Thread.sleep(3000);
        System.out.println("最终结果是" + value);*/

        ExecutorService executorService = Executors.newFixedThreadPool(10000);
        for (int i = 0; i < 10000; i++) {
            executorService.execute(() -> {
                for (int j = 0; j < 4; j++) {
                    System.out.println(atomicInteger.getAndIncrement());
                }
            });
        }
        executorService.shutdown();
    }




    private  static void atomicIntegerTest() {
        ExecutorService executorService = Executors.newFixedThreadPool(10000);
        for (int i = 0; i < 10000; i++) {
            executorService.execute(() ->{
                for (int j = 0; j < 4; j++) {
                    System.out.println(atomicInteger.getAndIncrement());
                }
            });
        }
        executorService.shutdown();
    }
}
