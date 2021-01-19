package com.example.crawlerjddemo.controller;

import com.example.crawlerjddemo.task.ItemTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
    private ItemTask itemTask;

    @GetMapping("/test")
    public void test() {
        try {
            itemTask.itemTask();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
