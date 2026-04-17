package konie.zaj3ui3;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Fasada oddzielająca interfejs użytkownika od modelu systemu.
 * Zapewnia uproszczony dostęp do operacji na stadninach i koniach.
 */
public class StableFacade {
    private StableManager stableManager;

    public StableFacade() {
        this.stableManager = new StableManager();
        initializeTestData();
    }

    // ============= OPERACJE NA STADNINACH =============

    /**
     * Dodaje nową stadninę
     * @throws StableException gdy nazwa jest pusta lub stadnina już istnieje
     */
    public void addStable(String name, int capacity) throws StableException {
        if (name == null || name.trim().isEmpty()) {
            throw new StableException("Nazwa stadniny nie może być pusta");
        }
        if (capacity <= 0) {
            throw new StableException("Pojemność musi być większa od 0");
        }
        if (stableManager.getStable(name) != null) {
            throw new StableException("Stadnina o nazwie '" + name + "' już istnieje");
        }

        stableManager.addStable(name, capacity);
    }

    /**
     * Usuwa stadninę
     * @throws StableException gdy stadnina nie istnieje
     */
    public void removeStable(String name) throws StableException {
        if (stableManager.getStable(name) == null) {
            throw new StableException("Stadnina '" + name + "' nie istnieje");
        }
        stableManager.removeStable(name);
    }

    /**
     * Zwraca wszystkie stadniny
     */
    public List<StableInfo> getAllStables() {
        List<StableInfo> stables = new ArrayList<>();
        for (String name : getStableNames()) {
            Stable stable = stableManager.getStable(name);
            stables.add(new StableInfo(
                    name,
                    stable.getFillPercentage(),
                    getHorsesInStable(name).size()
            ));
        }
        return stables;
    }

    /**
     * Zwraca stadniny posortowane według obciążenia (malejąco)
     */
    public List<StableInfo> getStablesSortedByLoad() {
        List<StableInfo> stables = getAllStables();
        stables.sort((s1, s2) -> Double.compare(s2.getFillPercentage(), s1.getFillPercentage()));
        return stables;
    }

    // ============= OPERACJE NA KONIACH =============

    /**
     * Dodaje konia do stadniny
     * @throws StableException gdy stadnina nie istnieje lub parametry są nieprawidłowe
     */
    public void addHorse(String stableName, String name, String breed,
                         HorseType type, HorseStatus status, HorseCondition condition,
                         int age, double price, double weight, HorseGender gender)
            throws StableException {

        Stable stable = stableManager.getStable(stableName);
        if (stable == null) {
            throw new StableException("Stadnina '" + stableName + "' nie istnieje");
        }

        try {
            Horse horse = new Horse(name, breed, type, status, condition, age, price, weight, gender);
            stable.addHorse(horse);
        } catch (IllegalArgumentException e) {
            throw new StableException("Nieprawidłowe dane konia: " + e.getMessage());
        }
    }

    /**
     * Usuwa konia ze stadniny
     * @throws StableException gdy stadnina lub koń nie istnieje
     */
    public void removeHorse(String stableName, String horseName) throws StableException {
        Stable stable = stableManager.getStable(stableName);
        if (stable == null) {
            throw new StableException("Stadnina '" + stableName + "' nie istnieje");
        }

        Horse horse = stable.search(horseName);
        if (horse == null) {
            throw new StableException("Koń '" + horseName + "' nie został znaleziony");
        }

        stable.removeHorse(horse);
    }

    /**
     * Zwraca wszystkie konie w danej stadninie
     */
    public List<HorseInfo> getHorsesInStable(String stableName) {
        Stable stable = stableManager.getStable(stableName);
        if (stable == null) {
            return new ArrayList<>();
        }

        return stable.sortByName().stream()
                .map(horse -> new HorseInfo(
                        horse.getName(),
                        horse.getBreed(),
                        horse.getAge(),
                        horse.getPrice(),
                        horse.getStatus().toString()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Filtruje konie według fragmentu nazwy
     */
    public List<HorseInfo> filterHorses(String stableName, String nameFragment) {
        Stable stable = stableManager.getStable(stableName);
        if (stable == null) {
            return new ArrayList<>();
        }

        return stable.searchPartial(nameFragment).stream()
                .map(horse -> new HorseInfo(
                        horse.getName(),
                        horse.getBreed(),
                        horse.getAge(),
                        horse.getPrice(),
                        horse.getStatus().toString()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Filtruje konie według statusu
     */
    public List<HorseInfo> filterHorsesByStatus(String stableName, HorseStatus status) {
        Stable stable = stableManager.getStable(stableName);
        if (stable == null) {
            return new ArrayList<>();
        }

        return stable.sortByName().stream()
                .filter(horse -> horse.getStatus() == status)
                .map(horse -> new HorseInfo(
                        horse.getName(),
                        horse.getBreed(),
                        horse.getAge(),
                        horse.getPrice(),
                        horse.getStatus().toString()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Sortuje konie według ceny
     */
    public List<HorseInfo> getHorsesSortedByPrice(String stableName) {
        Stable stable = stableManager.getStable(stableName);
        if (stable == null) {
            return new ArrayList<>();
        }

        return stable.sortByPrice().stream()
                .map(horse -> new HorseInfo(
                        horse.getName(),
                        horse.getBreed(),
                        horse.getAge(),
                        horse.getPrice(),
                        horse.getStatus().toString()
                ))
                .collect(Collectors.toList());
    }

    // ============= METODY POMOCNICZE =============

    private List<String> getStableNames() {
        return new ArrayList<>(stableManager.getStableNames());
    }

    private Map<String, Stable> getAllStablesMap() {
        return stableManager.getAllStables();
    }

    /**
     * Inicjalizuje testowe dane
     */
    private void initializeTestData() {
        try {
            addStable("Stadnina Słoneczna", 10);
            addStable("Stadnina Zielona Dolina", 15);
            addStable("Stadnina Górska", 8);

            addHorse("Stadnina Słoneczna", "Thunder", "Arabian",
                    HorseType.HOT_BLOODED, HorseStatus.GOOD, HorseCondition.HEALTHY,
                    5, 15000.0, 450.0, HorseGender.MARE);

            addHorse("Stadnina Słoneczna", "Bella", "Thoroughbred",
                    HorseType.HOT_BLOODED, HorseStatus.GOOD, HorseCondition.HEALTHY,
                    3, 25000.0, 480.0, HorseGender.MARE);

            addHorse("Stadnina Zielona Dolina", "Rocky", "Quarter Horse",
                    HorseType.HOT_BLOODED, HorseStatus.GOOD, HorseCondition.HEALTHY,
                    7, 12000.0, 520.0, HorseGender.GELDING);

        } catch (StableException e) {
            System.err.println("Błąd inicjalizacji danych testowych: " + e.getMessage());
        }
    }

    // ============= KLASY POMOCNICZE (DTO) =============

    /**
     * Data Transfer Object dla informacji o stadninie
     */
    public static class StableInfo {
        private final String name;
        private final double fillPercentage;
        private final int horseCount;

        public StableInfo(String name, double fillPercentage, int horseCount) {
            this.name = name;
            this.fillPercentage = fillPercentage;
            this.horseCount = horseCount;
        }

        public String getName() { return name; }
        public double getFillPercentage() { return fillPercentage; }
        public int getHorseCount() { return horseCount; }
    }

    /**
     * Data Transfer Object dla informacji o koniu
     */
    public static class HorseInfo {
        private final String name;
        private final String breed;
        private final int age;
        private final double price;
        private final String status;

        public HorseInfo(String name, String breed, int age, double price, String status) {
            this.name = name;
            this.breed = breed;
            this.age = age;
            this.price = price;
            this.status = status;
        }

        public String getName() { return name; }
        public String getBreed() { return breed; }
        public int getAge() { return age; }
        public double getPrice() { return price; }
        public String getStatus() { return status; }
    }
}

/**
 * Wyjątek dla operacji na stadninach
 */
class StableException extends Exception {
    public StableException(String message) {
        super(message);
    }
}