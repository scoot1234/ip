package bloop.gui;

import bloop.Duke;
import bloop.task.Task;
import bloop.ui.Ui;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/** Provides the JavaFX graphical interface for the Bloop chatbot. */
public class BloopApp extends Application {
    private final TextArea conversation = new TextArea();
    private final TextField commandField = new TextField();
    private final ListView<String> taskListView = new ListView<>();
    private Duke duke;

    /** Starts the Bloop graphical application. */
    @Override
    public void start(Stage stage) {
        duke = new Duke("data/duke.txt", new Ui(this::showBloopMessage));

        Label title = new Label("Bloop");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        Label instructions = new Label("Try: todo read book, list, mark 1, or bye");
        VBox header = new VBox(4, title, instructions);
        header.setPadding(new Insets(16));

        conversation.setEditable(false);
        conversation.setWrapText(true);
        conversation.setPromptText("Bloop's responses will appear here.");
        ScrollPane conversationPane = new ScrollPane(conversation);
        conversationPane.setFitToWidth(true);
        conversationPane.setFitToHeight(true);

        taskListView.setPlaceholder(new Label("No tasks yet"));
        VBox taskPanel = new VBox(8, new Label("Tasks"), taskListView);
        taskPanel.setPadding(new Insets(12));
        taskPanel.setPrefWidth(260);
        VBox.setVgrow(taskListView, Priority.ALWAYS);

        Button sendButton = new Button("Send");
        sendButton.setDefaultButton(true);
        sendButton.setOnAction(event -> submitCommand());
        commandField.setPromptText("Enter a command");
        commandField.setOnAction(event -> submitCommand());
        HBox inputBar = new HBox(8, commandField, sendButton);
        inputBar.setAlignment(Pos.CENTER);
        inputBar.setPadding(new Insets(12));
        HBox.setHgrow(commandField, Priority.ALWAYS);

        BorderPane root = new BorderPane();
        root.setTop(header);
        root.setCenter(conversationPane);
        root.setRight(taskPanel);
        root.setBottom(inputBar);
        BorderPane.setMargin(conversationPane, new Insets(0, 0, 0, 12));

        stage.setTitle("Bloop");
        stage.setScene(new Scene(root, 780, 520));
        stage.show();
        refreshTaskList();
        if (duke.startConversation()) {
            commandField.requestFocus();
        } else {
            commandField.setDisable(true);
            sendButton.setDisable(true);
        }
    }

    private void submitCommand() {
        String command = commandField.getText().trim();
        if (command.isEmpty()) {
            return;
        }

        conversation.appendText("You: " + command + "\n\n");
        commandField.clear();
        boolean shouldExit = duke.processCommand(command);
        refreshTaskList();
        if (shouldExit) {
            commandField.setDisable(true);
        }
    }

    private void showBloopMessage(String message) {
        conversation.appendText("Bloop: " + message + "\n\n");
    }

    private void refreshTaskList() {
        taskListView.getItems().clear();
        int taskNumber = 1;
        for (Task task : duke.getTasks().asList()) {
            taskListView.getItems().add(taskNumber + ". " + task);
            taskNumber++;
        }
    }
}
