package konie.zaj3ui3;

import java.util.Comparator;

public class Horse implements Printable, Comparable<Horse> {
    private String name;
    private String breed;
    private HorseType type;
    private HorseStatus status;
    private HorseCondition condition;
    private int age;
    private double price;

    private double weight;
    private HorseGender gender;


    public Horse(String name, String breed, HorseType type, HorseStatus status, HorseCondition condition, int age, double price, double weight, HorseGender gender) {

        if (!isAgeCorrect(age)){
            throw new IllegalArgumentException("Age is incorrect.");
        }
        if (!isPriceCorrect(price)){
            throw new IllegalArgumentException("Price is incorrect.");
        }
        if (!isWeightCorrect(weight)){
            throw new IllegalArgumentException("Weight is incorrect.");
        }

        this.name = name;
        this.breed = breed;
        this.type = type;
        this.status = status;
        this.age = age;
        this.price = price;
        this.weight = weight;
        this.gender = gender;
        this.condition = condition;
    }
    public Horse(String name) {
        this.name = name;
        this.breed = "";
        this.type = HorseType.COLD_BLOODED;
        this.status = HorseStatus.GOOD;
        this.condition = HorseCondition.HEALTHY;
        this.age = 0;
        this.price = 0.0;
        this.weight = 0.0;
        this.gender = HorseGender.GELDING;
    }

    private boolean isPriceCorrect(double price) {
        return !(price < 0);
    }

    private boolean isAgeCorrect(int age) {
        return age >= 0;
    }

    private boolean isWeightCorrect(double weight) {
        return !(weight < 0);
    }

    public String getName(){
        return name;
    }

    public String getBreed(){
        return breed;
    }

    public int getAge(){
        return age;
    }

    public double getPrice(){
        return price;
    }

    public void changeCondition(HorseCondition condition){
        this.condition = condition;
    }

    public HorseStatus getStatus(){
        return this.status;
    }

    public void changeWeight(double weight){
        if (!isWeightCorrect(weight)){
            throw new IllegalArgumentException("Weight is incorrect.");
        }
        this.weight = weight;
    }



    @Override
    public void print() {
        System.out.println("--- Horse Details ---");
        System.out.println("Name: " + name);
        System.out.println("Gender: " + gender);
        System.out.println("Age: " + age + " years");
        System.out.println("Breed: " + breed);
        System.out.println("Type: " + type);
        System.out.println("Status: " + status);
        System.out.println("Weight: " + weight + " kg");
        System.out.println("Price: " + price);
        System.out.println("---------------------");
    }

    @Override
    public int compareTo(Horse o) {
        return this.name.compareTo(o.getName());
    }

    public static Comparator<Horse> BreedComparator = new Comparator<Horse>() {
        @Override
        public int compare(Horse o1, Horse o2) {
            return o1.getBreed().compareTo(o2.getBreed());
        }
    };

    public static Comparator<Horse> AgeComparator = new Comparator<Horse>() {
        @Override
        public int compare(Horse o1, Horse o2) {
            return Integer.compare(o1.getAge(), o2.getAge());
        }
    };

    public static Comparator<Horse> PriceComparator = new Comparator<Horse>() {
        @Override
        public int compare(Horse o1, Horse o2) {
            return Double.compare(o1.getPrice(), o2.getPrice());
        }
    };

    public static Comparator<Horse> NameComparator = new  Comparator<Horse>() {
        @Override
        public int compare(Horse o1, Horse o2) {
            return o1.getName().compareTo(o2.getName());
        }
    };
}
