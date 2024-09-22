module com.example.chat_assignment {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.chat_assignment to javafx.fxml;
    exports com.example.chat_assignment;
}