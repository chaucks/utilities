package com.xcoder.test;

import com.xcoder.utilities.common.DateUtensil;

import java.util.Date;

public class Test {
    public static void main(String[] args) {
        Date date = DateUtensil.lastDayOfMonth();
        System.out.println(DateUtensil.format(date, "yyyy-MM-dd HH:mm:ss"));
    }
}
