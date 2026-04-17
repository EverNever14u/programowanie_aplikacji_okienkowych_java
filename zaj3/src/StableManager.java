import java.util.*;

public class StableManager {
    private Map<String, Stable> stables;

    public StableManager() {
        this.stables = new HashMap<>();
    }

    public void addStable(String name, int capacity){
        Stable stable = new Stable(name, capacity);
        stables.put(name, stable);
    }

    public void removeStable(String name){
        stables.remove(name);
    }

    public List<Stable> findEmpty(){
        List<Stable> emptyStables = new ArrayList<Stable>();

        for(Stable stable : stables.values()){
            if (stable.isEmpty()){
                emptyStables.add(stable);
            }
        }

        return emptyStables;
    }

    public void summary(){
        System.out.println("Stables:");
        for(String stableName : stables.keySet()){
            System.out.println(stableName + " - fill: " + stables.get(stableName).getFillPercentage() + "%");
        }
    }

    public Stable getStable(String name){
        return stables.get(name);
    }
}
