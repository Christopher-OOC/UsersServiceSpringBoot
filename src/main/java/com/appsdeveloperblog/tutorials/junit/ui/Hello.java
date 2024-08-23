package com.appsdeveloperblog.tutorials.junit.ui;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.stream.Stream;

public class Hello {

    public static void main(String[] args) {
        int x = 10;
        List<Integer> numbers = new ArrayList<>();
        numbers.add(1);
        numbers.add(2);
        numbers.add(3);

        changeX(10);
        changeNumbers(numbers);

        System.out.println(x);
        System.out.println(numbers);

        Stream<Integer> stream = Stream.of(1, 2, 3, 4, 5);
        List<Integer> li = stream.collect(Collectors.toList());
    }

    public static void changeX(int x) {
        x = x + 2;
    }

    public static void changeNumbers(List<Integer> numbers) {
        numbers.set(0, 3);
        numbers.set(1, 2);
        numbers.set(2, 1);
    }
}
