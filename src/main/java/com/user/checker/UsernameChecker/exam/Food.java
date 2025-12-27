package com.user.checker.UsernameChecker.exam;


public class Food {

/*
"id": 1,
    "user_id": "18",
    "age": "33",
    "user_weight": "91.88",
    "name": "Pasta Carbonara",
    "price": 10.39,
    "weight": 630,
    "calories": 383,
    "fat": 10.3,
    "carbs": 5.95,
    "protein": 12.67,
    "time_consumed": "11:58",
    "date_consumed": "2022-09-25",
    "type": "lunch",
    "favorite": "false",
    "procedence": "purchased"



    Task Description
You are provided with a list of inputs from an daily diet platform. The data is available in JSON format. Your goal is to implement a series of requirements to extract certain data for reporting purposes.   You can use any reasonable output method (developer console, terminal, HTML, file).  Please write your code so that requirements can be tested both one by one and all at once.  JSON File: https://topt.al/r6cvQM

REQUIREMENTS

1) Read the file If you're not able to accomplish this, you can skip this requirement by copying and pasting JSON content as string
2) Parse JSON data You can use www.quicktype.io to parse the data to a structured object
3) Calculate total expenditure between 2022-12-01 and 2022-12-04 inclusive  Output: The calculated total with 2 decimal places
4) Find and sum 3 dishes having maximum total amount of protein, fat and carbs.  Output: 3 lines in format ""<Dishname> - total <carbs/fats/proteins>: <amount rounded to 2 decimal places>g""
5) List 3 most common dishes that have been consumed between 9:00 and 14:00, along with how many times they were consumed. Output: Lines in format ""<Food name> - <times consumed on time range>"". Sort the list descending

RESULTS

1) No Result
2) No Result
3) 6668.51
4) French Fries with Sausages - total carbs: 1719.05g  Linguine with Clams - total fats: 1649.49g  Cheeseburger - total proteins: 1719.76g
5) Peking Duck - 35 Chicken Parm - 34 Mushroom Risotto - 32

 */
    private Long id;
    private Long user_id;
    private Long age;
    private Double user_weight;
    private String name;
    private Double price;
    private Long weight;
    private Long calories;
    private Double fat;
    private Double carbs;
    private Double protein;
    private String time_consumed;
    private String date_consumed;
    private String type;
    private boolean favorite;
    private boolean procedence;


    public Food(Long id, Long user_id, Long age, Double user_weight, String name, Double price, Long weight, Long calories, Double fat, Double carbs, Double protein, String time_consumed, String date_consumed, String type, boolean favorite, boolean procedence) {
        this.id = id;
        this.user_id = user_id;
        this.age = age;
        this.user_weight = user_weight;
        this.name = name;
        this.price = price;
        this.weight = weight;
        this.calories = calories;
        this.fat = fat;
        this.carbs = carbs;
        this.protein = protein;
        this.time_consumed = time_consumed;
        this.date_consumed = date_consumed;
        this.type = type;
        this.favorite = favorite;
        this.procedence = procedence;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public Long getAge() {
        return age;
    }

    public void setAge(Long age) {
        this.age = age;
    }

    public Double getUser_weight() {
        return user_weight;
    }

    public void setUser_weight(Double user_weight) {
        this.user_weight = user_weight;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Long getWeight() {
        return weight;
    }

    public void setWeight(Long weight) {
        this.weight = weight;
    }

    public Long getCalories() {
        return calories;
    }

    public void setCalories(Long calories) {
        this.calories = calories;
    }

    public Double getFat() {
        return fat;
    }

    public void setFat(Double fat) {
        this.fat = fat;
    }

    public Double getCarbs() {
        return carbs;
    }

    public void setCarbs(Double carbs) {
        this.carbs = carbs;
    }

    public Double getProtein() {
        return protein;
    }

    public void setProtein(Double protein) {
        this.protein = protein;
    }

    public String getTime_consumed() {
        return time_consumed;
    }

    public void setTime_consumed(String time_consumed) {
        this.time_consumed = time_consumed;
    }

    public String getDate_consumed() {
        return date_consumed;
    }

    public void setDate_consumed(String date_consumed) {
        this.date_consumed = date_consumed;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public boolean isProcedence() {
        return procedence;
    }

    public void setProcedence(boolean procedence) {
        this.procedence = procedence;
    }
}
