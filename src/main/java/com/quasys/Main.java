package com.quasys;

import org.json.JSONObject;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");


        JSONObject secrets = BeyondInsight.getSecrets();
        System.out.println(secrets);



        while (true) {
            try {
                // Her 10 saniyede bir döngü
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                System.out.println("Thread interrupted");
            }
        }

//        System.out.println(secrets.getJSONObject("abdulmelik").getJSONObject("Dev").getJSONObject("hola").getString("Password"));
//
//
//        String keyPath = "abdulmelik/Dev/hola/Password";
//        Object value = getValueByKeyPath(secrets, keyPath);
//        System.out.println("Value for key path '" + keyPath + "': " + value);


    }
//    public static Object getValueByKeyPath(JSONObject jsonObject, String keyPath) {
//        String[] keys = keyPath.split("/");
//        Object current = jsonObject;
//        for (String key : keys) {
//            if (current instanceof JSONObject) {
//                current = ((JSONObject) current).get(key);
//            } else {
//                throw new IllegalArgumentException("Invalid key path: " + keyPath);
//            }
//        }
//        return current;
//    }
}