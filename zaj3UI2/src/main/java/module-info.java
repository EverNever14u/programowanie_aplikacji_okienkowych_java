module org.example.zaj3ui2 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens org.example.zaj3ui2 to javafx.fxml;
    exports org.example.zaj3ui2;
}