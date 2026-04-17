module konie.zaj3ui3 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;

    opens konie.zaj3ui3 to javafx.fxml;
    exports konie.zaj3ui3;
}