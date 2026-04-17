package konie.zaj3ui3;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class UserController implements Initializable {

    @FXML private ComboBox<String> stableComboBox;
    @FXML private Label stableInfoLabel;

    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilterCombo;
    @FXML private TextField minAgeField;
    @FXML private TextField maxAgeField;
    @FXML private Label resultsCountLabel;

    @FXML private TableView<HorseRow> horsesTable;
    @FXML private TableColumn<HorseRow, String> nameColumn;
    @FXML private TableColumn<HorseRow, String> breedColumn;
    @FXML private TableColumn<HorseRow, Integer> ageColumn;
    @FXML private TableColumn<HorseRow, Double> priceColumn;
    @FXML private TableColumn<HorseRow, String> statusColumn;

    @FXML private TextArea detailsArea;
    @FXML private Label statusLabel;

    private StableFacade facade;
    private ObservableList<HorseRow> horsesData;
    private ObservableList<HorseRow> allHorsesData; // Pełna lista do filtrowania
    private String currentStable = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        facade = new StableFacade();

        initializeComponents();
        loadStablesList();

        // Listener do wyboru konia z tabeli
        horsesTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        showHorseDetails(newSelection);
                    }
                }
        );

        updateStatus("Witaj! Wybierz stadninę, aby przeglądać konie.");
    }

    private void initializeComponents() {
        // Inicjalizacja tabeli
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        breedColumn.setCellValueFactory(new PropertyValueFactory<>("breed"));
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Formatowanie ceny
        priceColumn.setCellFactory(col -> new TableCell<HorseRow, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f zł", item));
                }
            }
        });

        horsesData = FXCollections.observableArrayList();
        allHorsesData = FXCollections.observableArrayList();
        horsesTable.setItems(horsesData);

        // Inicjalizacja ComboBox statusu
        statusFilterCombo.getItems().addAll("Wszystkie", "GOOD", "EXCELLENT", "POOR");
        statusFilterCombo.setValue("Wszystkie");
    }

    private void loadStablesList() {
        List<String> stableNames = facade.getAllStables().stream()
                .map(StableFacade.StableInfo::getName)
                .collect(Collectors.toList());

        stableComboBox.getItems().clear();
        stableComboBox.getItems().addAll(stableNames);

        if (!stableNames.isEmpty()) {
            updateStatus("Załadowano " + stableNames.size() + " stadnin");
        }
    }

    @FXML
    private void handleStableSelection(ActionEvent event) {
        String selectedStable = stableComboBox.getValue();
        if (selectedStable == null || selectedStable.isEmpty()) {
            return;
        }

        currentStable = selectedStable;
        loadHorsesForStable(selectedStable);

        // Pokaż informacje o stadninie
        StableFacade.StableInfo info = facade.getAllStables().stream()
                .filter(s -> s.getName().equals(selectedStable))
                .findFirst()
                .orElse(null);

        if (info != null) {
            stableInfoLabel.setText(String.format(
                    "Koni: %d | Obciążenie: %.1f%%",
                    info.getHorseCount(),
                    info.getFillPercentage()
            ));
        }

        updateStatus("Wybrano stadninę: " + selectedStable);
    }

    private void loadHorsesForStable(String stableName) {
        List<StableFacade.HorseInfo> horses = facade.getHorsesInStable(stableName);

        allHorsesData.clear();
        horsesData.clear();

        for (StableFacade.HorseInfo info : horses) {
            HorseRow row = new HorseRow(
                    info.getName(),
                    info.getBreed(),
                    info.getAge(),
                    info.getPrice(),
                    info.getStatus()
            );
            allHorsesData.add(row);
            horsesData.add(row);
        }

        updateResultsCount();
        detailsArea.clear();
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        applyFilters();
    }

    @FXML
    private void handleStatusFilter(ActionEvent event) {
        applyFilters();
    }

    private void applyFilters() {
        if (currentStable == null) {
            return;
        }

        String searchText = searchField.getText().toLowerCase().trim();
        String statusFilter = statusFilterCombo.getValue();
        Integer minAge = parseInteger(minAgeField.getText());
        Integer maxAge = parseInteger(maxAgeField.getText());

        horsesData.clear();

        for (HorseRow horse : allHorsesData) {
            boolean matches = true;

            // Filtr po nazwie/rasie
            if (!searchText.isEmpty()) {
                matches = horse.getName().toLowerCase().contains(searchText) ||
                        horse.getBreed().toLowerCase().contains(searchText);
            }

            // Filtr po statusie
            if (matches && !"Wszystkie".equals(statusFilter)) {
                matches = horse.getStatus().equals(statusFilter);
            }

            // Filtr po wieku minimalnym
            if (matches && minAge != null) {
                matches = horse.getAge() >= minAge;
            }

            // Filtr po wieku maksymalnym
            if (matches && maxAge != null) {
                matches = horse.getAge() <= maxAge;
            }

            if (matches) {
                horsesData.add(horse);
            }
        }

        updateResultsCount();
        updateStatus("Zastosowano filtry");
    }

    @FXML
    private void handleClearFilters(ActionEvent event) {
        searchField.clear();
        statusFilterCombo.setValue("Wszystkie");
        minAgeField.clear();
        maxAgeField.clear();

        horsesData.clear();
        horsesData.addAll(allHorsesData);

        updateResultsCount();
        detailsArea.clear();
        updateStatus("Wyczyszczono filtry");
    }

    private void showHorseDetails(HorseRow horse) {
        StringBuilder details = new StringBuilder();
        details.append("╔════════════════════════════════════════╗\n");
        details.append("║        SZCZEGÓŁY KONIA                 ║\n");
        details.append("╠════════════════════════════════════════╣\n");
        details.append(String.format("║ Nazwa:    %-28s ║\n", horse.getName()));
        details.append(String.format("║ Rasa:     %-28s ║\n", horse.getBreed()));
        details.append(String.format("║ Wiek:     %-28d ║\n", horse.getAge()));
        details.append(String.format("║ Cena:     %-28.2f ║\n", horse.getPrice()));
        details.append(String.format("║ Status:   %-28s ║\n", horse.getStatus()));
        details.append(String.format("║ Stadnina: %-28s ║\n", currentStable));
        details.append("╚════════════════════════════════════════╝\n");

        detailsArea.setText(details.toString());
        updateStatus("Wyświetlono szczegóły konia: " + horse.getName());
    }

    private void updateResultsCount() {
        resultsCountLabel.setText("Znaleziono: " + horsesData.size() + " koni");
    }

    private void updateStatus(String message) {
        statusLabel.setText(message);
    }

    private Integer parseInteger(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
            Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ============= INNER CLASS =============

    public static class HorseRow {
        private final SimpleStringProperty name;
        private final SimpleStringProperty breed;
        private final SimpleIntegerProperty age;
        private final SimpleDoubleProperty price;
        private final SimpleStringProperty status;

        public HorseRow(String name, String breed, int age, double price, String status) {
            this.name = new SimpleStringProperty(name);
            this.breed = new SimpleStringProperty(breed);
            this.age = new SimpleIntegerProperty(age);
            this.price = new SimpleDoubleProperty(price);
            this.status = new SimpleStringProperty(status);
        }

        public String getName() { return name.get(); }
        public String getBreed() { return breed.get(); }
        public int getAge() { return age.get(); }
        public double getPrice() { return price.get(); }
        public String getStatus() { return status.get(); }
    }
}