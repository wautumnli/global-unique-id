package com.ql.uniqueId;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Main {
    public static void main(String[] args) {
        Map<String, String> map  = new HashMap<>();
        String test = map.get("test");
        System.out.println(test);
    }
}