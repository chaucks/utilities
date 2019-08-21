package com.xcoder.utilities.common;

import java.util.*;
import java.util.stream.Collectors;

public class TestUtensil {

    public static void main(String[] args) {
        List<Map> sortedList = Arrays.stream(new HashMap[]{null, null}).filter(Objects::nonNull).filter(j -> !j.isEmpty())
                .sorted(Comparator.comparing(j0 -> (String) j0.get("ID"))).collect(Collectors.toList());

        String sortedString = Arrays.stream("3,1,2".split(",")).filter(Objects::nonNull)
                .sorted(String::compareTo).collect(Collectors.joining(","));

    }

}
