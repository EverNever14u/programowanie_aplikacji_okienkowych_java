package konie.zaj3ui3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Stable {
    private String stableName;
    private List<Horse> horseList;
    private int maxCapacity;


    public Stable(String stableName, int maxCapacity) {
        this.stableName = stableName;
        this.horseList = new ArrayList<Horse>();
        this.maxCapacity = maxCapacity;

    }

    public double getFillPercentage(){
        return (double) (horseList.size() * 100) / maxCapacity;
    }

    public void addHorse(Horse horse) {
        if (horseList.contains(horse) ) {
            System.out.println("Horse is already in stable");
            return;
        }

        if (horseList.size() >= maxCapacity) {
            System.err.println("Too many horses");
        }

        horseList.add(horse);
    }

    public String getStableName() {
        return stableName;
    }

    public void removeHorse(Horse horse) {
        horseList.remove(horse);
    }

    public void sickHorse(Horse horse) {
        if (horseList.contains(horse)) {
            horse.changeCondition(HorseCondition.ILL);
        }
    }

    public boolean isEmpty() {
        return horseList.isEmpty();
    }

    public void changeCondition(Horse horse, HorseCondition condition) {
        if (horseList.contains(horse)) {
            horse.changeCondition(condition);
        }
    }

    public void changeWeight(Horse horse, double kg) {
        if (horseList.contains(horse)) {
            horse.changeWeight(kg);
        };
    }

    public int countByStatus(HorseStatus status) {
        int horsesOfGivenStatus = 0;
        for (Horse horse : horseList) {
            if (horse.getStatus() == status) {
                horsesOfGivenStatus += 1;
            }
        }

        return horsesOfGivenStatus;
    }

    public List<Horse> sortByName() {
        List<Horse> sortedHorses = new ArrayList<Horse>(horseList);

        Collections.sort(sortedHorses);

        return sortedHorses;
    }

    public List<Horse> sortByPrice() {
        List<Horse> sortedHorses = new ArrayList<Horse>(horseList);
        sortedHorses.sort(Horse.PriceComparator);

        return sortedHorses;
    }

    public Horse search(String name){

        Horse searchHorse = new Horse(name);

        for (Horse horse : horseList) {
            if (Horse.NameComparator.compare(horse,  searchHorse) == 0) {
                return horse;
            }
        }

        return null;

    }

    public List<Horse> searchPartial(String fragment){
        if (fragment == null || fragment.isEmpty()){
            return horseList;
        }

        List<Horse> foundHorses = new ArrayList<Horse>();

        String lowerCaseFragment = fragment.toLowerCase();

        for (Horse horse : horseList) {
            if (horse.getName().toLowerCase().contains(lowerCaseFragment)){
                foundHorses.add(horse);
            }
            if (horse.getBreed().toLowerCase().contains(lowerCaseFragment)){
                foundHorses.add(horse);
            }
        }

        return foundHorses;

    }

    public void summary(){
        for  (Horse horse : horseList){
            horse.print();
        }
    }

    public Horse max() {
        return Collections.max(horseList, Horse.PriceComparator);
    }


}
