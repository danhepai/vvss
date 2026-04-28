module drinkshop {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;

    requires org.controlsfx.controls;
    requires java.desktop;

    // 1. Give JavaFX access to your UI and Domain
    opens drinkshop.ui to javafx.fxml;
    exports drinkshop.ui;

    opens drinkshop.domain to javafx.base, org.mockito, net.bytebuddy;
    exports drinkshop.domain;

    // 2. Add permissions for your Repositories and Services
    // Mockito needs to "open" these to create the mock objects
    opens drinkshop.repository to org.mockito, net.bytebuddy;
    exports drinkshop.repository;

    opens drinkshop.service to org.mockito, net.bytebuddy, org.junit.platform.commons;
    exports drinkshop.service;
}