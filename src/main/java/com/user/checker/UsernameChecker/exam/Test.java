package com.user.checker.UsernameChecker.exam;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test {

    public static void main() {

        Map<Long, Food> idToFood = new HashMap<>();
        Map<Long, Food> userIdToFood = new HashMap<>();


        try (JsonReader reader = new JsonReader(new FileReader("src/main/java/com/user/checker/UsernameChecker/exam/calories.json"))) {

            List<Food> foodList = new Gson().fromJson(
                    reader,
                    new TypeToken<List<Food>>() {
                    }.getType());

            Double totalExpense = (double) 0;
            for (Food f : foodList) {
                idToFood.put(f.getId(), f);
                userIdToFood.put(f.getUser_id(), f);
//                System.out.println("Food Id" + f.getId());


//                2022-12-01 and 2022-12-04 inclusive
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate fooddate = LocalDate.parse(f.getDate_consumed(), formatter);
                LocalDate afterDate =  LocalDate.parse("2022-12-01", formatter).minus(1, ChronoUnit.DAYS);
                LocalDate beforeDate = LocalDate.parse("2022-12-05", formatter);
                boolean isTrue = fooddate.isAfter(afterDate) && fooddate.isBefore(beforeDate);
                if(isTrue)
                {
//                    isTrue = date.parse(f.getDate_consumed()).before(date.parse("2022-12-04"));
//                    if (isTrue)

                        totalExpense+= f.getPrice();

                }

            }
            System.out.println(totalExpense);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
