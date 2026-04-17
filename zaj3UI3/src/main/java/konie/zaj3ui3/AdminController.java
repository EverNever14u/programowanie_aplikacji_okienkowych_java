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
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class AdminController implements Initializable {

    // ============= FXML COMPONENTS - STABLES =============
    @FXML private TableView<StableTableRow> stablesTable;
    @FXML private TableColumn<StableTableRow, String> stableNameColumn;
    @FXML private TableColumn<StableTableRow, Double> stableFillColumn;
    @FXML private TableColumn<StableTableRow, Integer> stableCountColumn;
    @FXML private Button addStableBtn;
    @FXML private Button removeStableBtn;
    @FXML private Button sortStablesBtn;

    // ============= FXML COMPONENTS - HORSES =============
    @FXML private TableView<HorseTableRow> horsesTable;
    @FXML private TableColumn<HorseTableRow, String> horseNameColumn;
    @FXML private TableColumn<HorseTableRow, String> horseBreedColumn;
    @FXML private TableColumn<HorseTableRow, Integer> horseAgeColumn;
    @FXML private TableColumn<HorseTableRow, Double> horsePriceColumn;
    @FXML private TableColumn<HorseTableRow, String> horseStatusColumn;

    @FXML private TextField filterTextField;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private Button addHorseBtn;
    @FXML private Button removeHorseBtn;
    @FXML private Button sortHorsesByPriceBtn;
    @FXML private Button clearFiltersBtn;

    @FXML private Label selectedStableLabel;
    @FXML private Label statusLabel;

    // ============= DATA =============
    private StableFacade facade;
    private ObservableList<StableTableRow> stablesData;
    private ObservableList<HorseTableRow> horsesData;
    private String currentSelectedStable = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        facade = new StableFacade();

        // Inicjalizacja tabel
        initializeStablesTable();
        initializeHorsesTable();
        initializeStatusComboBox();

        // Załaduj dane
        loadStablesData();

        // Dodaj listener do wyboru stadniny
        stablesTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        handleStableSelection(newSelection.getName());
                    }
                }
        );

        updateStatus("System gotowy do pracy");
    }

    private void initializeStablesTable() {
        stableNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        stableFillColumn.setCellValueFactory(new PropertyValueFactory<>("fillPercentage"));
        stableCountColumn.setCellValueFactory(new PropertyValueFactory<>("horseCount"));

        // Formatowanie kolumny procentowej
        stableFillColumn.setCellFactory(col -> new TableCell<StableTableRow, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.1f%%", item));
                }
            }
        });

        stablesData = FXCollections.observableArrayList();
        stablesTable.setItems(stablesData);
    }

    private void initializeHorsesTable() {
        horseNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        horseBreedColumn.setCellValueFactory(new PropertyValueFactory<>("breed"));
        horseAgeColumn.setCellValueFactory(new PropertyValueFactory<>("age"));
        horsePriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        horseStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Formatowanie kolumny ceny
        horsePriceColumn.setCellFactory(col -> new TableCell<HorseTableRow, Double>() {
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
        horsesTable.setItems(horsesData);
    }

    private void initializeStatusComboBox() {
        statusComboBox.getItems().addAll("Wszystkie", "GOOD", "EXCELLENT", "POOR");
        statusComboBox.setValue("Wszystkie");
    }

    // ============= STABLE OPERATIONS =============

    private void loadStablesData() {
        stablesData.clear();
        for (StableFacade.StableInfo info : facade.getAllStables()) {
            stablesData.add(new StableTableRow(
                    info.getName(),
                    info.getFillPercentage(),
                    info.getHorseCount()
            ));
        }
    }

    @FXML
    private void handleAddStable(ActionEvent event) {
        Dialog<StableDialogResult> dialog = createAddStableDialog();
        Optional<StableDialogResult> result = dialog.showAndWait();

        result.ifPresent(stableData -> {
            try {
                facade.addStable(stableData.name, stableData.capacity);
                loadStablesData();
                updateStatus("Dodano stadninę: " + stableData.name);
            } catch (StableException e) {
                showError("Błąd dodawania stadniny", e.getMessage());
            }
        });
    }

    @FXML
    private void handleRemoveStable(ActionEvent event) {
        StableTableRow selected = stablesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Brak wyboru", "Wybierz stadninę do usunięcia");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Potwierdzenie");
        confirm.setHeaderText("Usuwanie stadniny");
        confirm.setContentText("Czy na pewno chcesz usunąć stadninę: " + selected.getName() + "?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                facade.removeStable(selected.getName());
                loadStablesData();
                clearHorsesTable();
                updateStatus("Usunięto stadninę: " + selected.getName());
            } catch (StableException e) {
                showError("Błąd usuwania", e.getMessage());
            }
        }
    }

    @FXML
    private void handleSortStables(ActionEvent event) {
        stablesData.clear();
        for (StableFacade.StableInfo info : facade.getStablesSortedByLoad()) {
            stablesData.add(new StableTableRow(
                    info.getName(),
                    info.getFillPercentage(),
                    info.getHorseCount()
            ));
        }
        updateStatus("Posortowano stadniny według obciążenia");
    }

    // ============= HORSE OPERATIONS =============

    private void handleStableSelection(String stableName) {
        currentSelectedStable = stableName;
        selectedStableLabel.setText(stableName);

        // Włącz przyciski dla koni
        addHorseBtn.setDisable(false);
        removeHorseBtn.setDisable(false);
        sortHorsesByPriceBtn.setDisable(false);
        clearFiltersBtn.setDisable(false);

        loadHorsesData();
        updateStatus("Wybrano stadninę: " + stableName);
    }

    private void loadHorsesData() {
        if (currentSelectedStable == null) return;

        horsesData.clear();
        for (StableFacade.HorseInfo info : facade.getHorsesInStable(currentSelectedStable)) {
            horsesData.add(new HorseTableRow(
                    info.getName(),
                    info.getBreed(),
                    info.getAge(),
                    info.getPrice(),
                    info.getStatus()
            ));
        }
    }

    @FXML
    private void handleAddHorse(ActionEvent event) {
        if (currentSelectedStable == null) return;

        Dialog<HorseDialogResult> dialog = createAddHorseDialog();
        Optional<HorseDialogResult> result = dialog.showAndWait();

        result.ifPresent(horseData -> {
            try {
                facade.addHorse(
                        currentSelectedStable,
                        horseData.name,
                        horseData.breed,
                        horseData.type,
                        horseData.status,
                        horseData.condition,
                        horseData.age,
                        horseData.price,
                        horseData.weight,
                        horseData.gender
                );
                loadHorsesData();
                loadStablesData(); // Odśwież licznik koni
                updateStatus("Dodano konia: " + horseData.name);
            } catch (StableException e) {
                showError("Błąd dodawania konia", e.getMessage());
            }
        });
    }

    @FXML
    private void handleRemoveHorse(ActionEvent event) {
        HorseTableRow selected = horsesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Brak wyboru", "Wybierz konia do usunięcia");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Potwierdzenie");
        confirm.setHeaderText("Usuwanie konia");
        confirm.setContentText("Czy na pewno chcesz usunąć konia: " + selected.getName() + "?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                facade.removeHorse(currentSelectedStable, selected.getName());
                loadHorsesData();
                loadStablesData();
                updateStatus("Usunięto konia: " + selected.getName());
            } catch (StableException e) {
                showError("Błąd usuwania", e.getMessage());
            }
        }
    }

    @FXML
    private void handleSortHorsesByPrice(ActionEvent event) {
        if (currentSelectedStable == null) return;

        horsesData.clear();
        for (StableFacade.HorseInfo info : facade.getHorsesSortedByPrice(currentSelectedStable)) {
            horsesData.add(new HorseTableRow(
                    info.getName(),
                    info.getBreed(),
                    info.getAge(),
                    info.getPrice(),
                    info.getStatus()
            ));
        }
        updateStatus("Posortowano konie według ceny");
    }

    @FXML
    private void handleFilterByName(ActionEvent event) {
        if (currentSelectedStable == null) return;

        String fragment = filterTextField.getText();
        horsesData.clear();

        for (StableFacade.HorseInfo info : facade.filterHorses(currentSelectedStable, fragment)) {
            horsesData.add(new HorseTableRow(
                    info.getName(),
                    info.getBreed(),
                    info.getAge(),
                    info.getPrice(),
                    info.getStatus()
            ));
        }
        updateStatus("Przefiltrowano konie według nazwy: " + fragment);
    }

    @FXML
    private void handleFilterByStatus(ActionEvent event) {
        if (currentSelectedStable == null) return;

        String statusStr = statusComboBox.getValue();
        if ("Wszystkie".equals(statusStr)) {
            loadHorsesData();
            return;
        }

        try {
            HorseStatus status = HorseStatus.valueOf(statusStr);
            horsesData.clear();

            for (StableFacade.HorseInfo info : facade.filterHorsesByStatus(currentSelectedStable, status)) {
                horsesData.add(new HorseTableRow(
                        info.getName(),
                        info.getBreed(),
                        info.getAge(),
                        info.getPrice(),
                        info.getStatus()
                ));
            }
            updateStatus("Przefiltrowano konie według statusu: " + statusStr);
        } catch (IllegalArgumentException e) {
            showError("Błąd filtrowania", "Nieprawidłowy status");
        }
    }

    @FXML
    private void handleClearFilters(ActionEvent event) {
        filterTextField.clear();
        statusComboBox.setValue("Wszystkie");
        loadHorsesData();
        updateStatus("Wyczyszczono filtry");
    }

    // ============= DIALOGS =============

    private Dialog<StableDialogResult> createAddStableDialog() {
        Dialog<StableDialogResult> dialog = new Dialog<>();
        dialog.setTitle("Dodaj stadninę");
        dialog.setHeaderText("Wprowadź dane nowej stadniny");

        ButtonType addButtonType = new ButtonType("Dodaj", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField nameField = new TextField();
        nameField.setPromptText("Nazwa stadniny");
        TextField capacityField = new TextField();
        capacityField.setPromptText("Pojemność");

        grid.add(new Label("Nazwa:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Pojemność:"), 0, 1);
        grid.add(capacityField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    return new StableDialogResult(
                            nameField.getText(),
                            Integer.parseInt(capacityField.getText())
                    );
                } catch (NumberFormatException e) {
                    showError("Błąd", "Pojemność musi być liczbą całkowitą");
                    return null;
                }
            }
            return null;
        });

        return dialog;
    }

    private Dialog<HorseDialogResult> createAddHorseDialog() {
        Dialog<HorseDialogResult> dialog = new Dialog<>();
        dialog.setTitle("Dodaj konia");
        dialog.setHeaderText("Wprowadź dane nowego konia");

        ButtonType addButtonType = new ButtonType("Dodaj", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField nameField = new TextField();
        TextField breedField = new TextField();
        TextField ageField = new TextField();
        TextField priceField = new TextField();
        TextField weightField = new TextField();

        ComboBox<HorseType> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll(HorseType.values());
        typeCombo.setValue(HorseType.HOT_BLOODED);

        ComboBox<HorseStatus> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll(HorseStatus.values());
        statusCombo.setValue(HorseStatus.GOOD);

        ComboBox<HorseCondition> conditionCombo = new ComboBox<>();
        conditionCombo.getItems().addAll(HorseCondition.values());
        conditionCombo.setValue(HorseCondition.HEALTHY);

        ComboBox<HorseGender> genderCombo = new ComboBox<>();
        genderCombo.getItems().addAll(HorseGender.values());
        genderCombo.setValue(HorseGender.GELDING);

        int row = 0;
        grid.add(new Label("Nazwa:"), 0, row);
        grid.add(nameField, 1, row++);
        grid.add(new Label("Rasa:"), 0, row);
        grid.add(breedField, 1, row++);
        grid.add(new Label("Wiek:"), 0, row);
        grid.add(ageField, 1, row++);
        grid.add(new Label("Cena:"), 0, row);
        grid.add(priceField, 1, row++);
        grid.add(new Label("Waga (kg):"), 0, row);
        grid.add(weightField, 1, row++);
        grid.add(new Label("Typ:"), 0, row);
        grid.add(typeCombo, 1, row++);
        grid.add(new Label("Status:"), 0, row);
        grid.add(statusCombo, 1, row++);
        grid.add(new Label("Kondycja:"), 0, row);
        grid.add(conditionCombo, 1, row++);
        grid.add(new Label("Płeć:"), 0, row);
        grid.add(genderCombo, 1, row++);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    return new HorseDialogResult(
                            nameField.getText(),
                            breedField.getText(),
                            typeCombo.getValue(),
                            statusCombo.getValue(),
                            conditionCombo.getValue(),
                            Integer.parseInt(ageField.getText()),
                            Double.parseDouble(priceField.getText()),
                            Double.parseDouble(weightField.getText()),
                            genderCombo.getValue()
                    );
                } catch (NumberFormatException e) {
                    showError("Błąd", "Nieprawidłowe wartości liczbowe");
                    return null;
                }
            }
            return null;
        });

        return dialog;
    }

    // ============= HELPERS =============

    private void clearHorsesTable() {
        currentSelectedStable = null;
        selectedStableLabel.setText("(brak wyboru)");
        horsesData.clear();
        addHorseBtn.setDisable(true);
        removeHorseBtn.setDisable(true);
        sortHorsesByPriceBtn.setDisable(true);
        clearFiltersBtn.setDisable(true);
    }

    private void updateStatus(String message) {
        statusLabel.setText(message);
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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

    // ============= INNER CLASSES =============

    public static class StableTableRow {
        private final SimpleStringProperty name;
        private final SimpleDoubleProperty fillPercentage;
        private final SimpleIntegerProperty horseCount;

        public StableTableRow(String name, double fillPercentage, int horseCount) {
            this.name = new SimpleStringProperty(name);
            this.fillPercentage = new SimpleDoubleProperty(fillPercentage);
            this.horseCount = new SimpleIntegerProperty(horseCount);
        }

        public String getName() { return name.get(); }
        public double getFillPercentage() { return fillPercentage.get(); }
        public int getHorseCount() { return horseCount.get(); }
    }

    public static class HorseTableRow {
        private final SimpleStringProperty name;
        private final SimpleStringProperty breed;
        private final SimpleIntegerProperty age;
        private final SimpleDoubleProperty price;
        private final SimpleStringProperty status;

        public HorseTableRow(String name, String breed, int age, double price, String status) {
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

    private static class StableDialogResult {
        String name;
        int capacity;

        StableDialogResult(String name, int capacity) {
            this.name = name;
            this.capacity = capacity;
        }
    }

    private static class HorseDialogResult {
        String name, breed;
        HorseType type;
        HorseStatus status;
        HorseCondition condition;
        int age;
        double price, weight;
        HorseGender gender;

        HorseDialogResult(String name, String breed, HorseType type, HorseStatus status,
                          HorseCondition condition, int age, double price, double weight,
                          HorseGender gender) {
            this.name = name;
            this.breed = breed;
            this.type = type;
            this.status = status;
            this.condition = condition;
            this.age = age;
            this.price = price;
            this.weight = weight;
            this.gender = gender;
        }
    }
}