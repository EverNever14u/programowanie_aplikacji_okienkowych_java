//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
void main() {


            // === Tworzenie StableManager ===
            StableManager manager = new StableManager();
            manager.addStable("Sunny Stable", 3);
            manager.addStable("Empty Stable", 2);

            // === Tworzenie koni ===
            Horse h1 = new Horse("Thunder", "Arabian", HorseType.HOT_BLOODED, HorseStatus.GOOD, HorseCondition.HEALTHY, 5, 12000, 450, HorseGender.MARE);
            Horse h2 = new Horse("Storm", "Friesian", HorseType.COLD_BLOODED, HorseStatus.GOOD, HorseCondition.HEALTHY, 7, 15000, 600, HorseGender.GELDING);
            Horse h3 = new Horse("Bella", "Mustang", HorseType.HOT_BLOODED, HorseStatus.GOOD, HorseCondition.ILL, 3, 8000, 400, HorseGender.MARE);

            Stable stable1 = manager.getStable("Sunny Stable");

            // === Dodawanie koni do stajni ===
            stable1.addHorse(h1);
            stable1.addHorse(h2);
            stable1.addHorse(h3);

            // Próba dodania duplikatu
            stable1.addHorse(h1);


            // === Wypisanie wszystkich koni ===
            System.out.println("\n--- SUMMARY ---");
            stable1.summary();

            // === Choroba konia ===
            stable1.sickHorse(h1);
            stable1.summary();

            // === Zmiana wagi ===
            stable1.changeWeight(h2, 620);

            // === Liczenie koni o danym statusie ===
            System.out.println("\nHorses with status GOOD: " + stable1.countByStatus(HorseStatus.GOOD));

            // === Sortowanie ===
            System.out.println("\nSorted by Name:");
            for (Horse h : stable1.sortByName()) {
                System.out.println(h.getName());
            }

            System.out.println("\nSorted by Price:");
            for (Horse h : stable1.sortByPrice()) {
                System.out.println(h.getName() + " - " + h.getPrice());
            }

            // === Szukanie ===
            System.out.println("\nSearch exact name 'Storm':");
            Horse found = stable1.search("Storm");
            if (found != null) found.print();

            System.out.println("\nSearch partial 'bel':");
            for (Horse h : stable1.searchPartial("bel")) {
                System.out.println("Found: " + h.getName());
            }

            // === Najdroższy koń ===
            System.out.println("\nMost expensive horse:");
            Horse maxHorse = stable1.max();
            maxHorse.print();

            // === Usuwanie konia ===
            stable1.removeHorse(h3);
            System.out.println("\nAfter removing Bella:");
            stable1.summary();



            // === Pokazanie pustych stajni ===
            System.out.println("\nEmpty stables:");
            for (Stable s : manager.findEmpty()) {
                System.out.println(s.getStableName());
            }

            // === Podsumowanie wszystkich stajni ===
            manager.summary();

            // === Usunięcie stajni ===
            manager.removeStable("Empty Stable");
            System.out.println("\nAfter removing Empty Stable:");
            manager.summary();

}
