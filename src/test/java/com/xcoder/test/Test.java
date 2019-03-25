package com.xcoder.test;

import com.xcoder.utilities.http.HttpClient;

public class Test {
    public static void main(String[] args) throws Exception {
        String result = new HttpClient("http://www.github.com").postRest("");
        System.out.println(result);
    }
}
