package charlie;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import charlie.ui.DialogBox;

/**
 * Displays Charlie as a minimal graphical chatbot.
 */
public class Main extends Application {
    /** Default location used to persist tasks. */
    private static final String DEFAULT_FILE_PATH = "data/charlie.txt";

    /** Chatbot that processes commands entered in the window. */
    private final Charlie charlie = new Charlie(DEFAULT_FILE_PATH);

    /** Contains the user and Charlie messages in display order. */
    private final VBox dialogContainer = new VBox(10);

    /** Scrollable area containing the conversation. */
    private final ScrollPane scrollPane = new ScrollPane(dialogContainer);

    /** Accepts the next command from the user. */
    private final TextField userInput = new TextField();

    /** Submits the command currently in the text field. */
    private final Button sendButton = new Button("Send");

    /** Main application window, closed after the {@code bye} command. */
    private Stage stage;

    /**
     * Builds and displays the graphical interface.
     *
     * @param stage Primary JavaFX window supplied by the framework.
     */
    @Override
    public void start(Stage stage) {
        this.stage = stage;

        dialogContainer.setPadding(new Insets(12));
        dialogContainer.setFillWidth(true);
        dialogContainer.heightProperty().addListener(observable -> scrollPane.setVvalue(1.0));

        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        userInput.setPromptText("Enter a command, e.g. list");
        HBox.setHgrow(userInput, Priority.ALWAYS);
        HBox inputArea = new HBox(8, userInput, sendButton);
        inputArea.setPadding(new Insets(10));

        BorderPane mainLayout = new BorderPane(scrollPane);
        mainLayout.setBottom(inputArea);

        userInput.setOnAction(event -> handleUserInput());
        sendButton.setOnAction(event -> handleUserInput());

        dialogContainer.getChildren().add(DialogBox.getCharlieDialog(charlie.getGreeting()));

        stage.setTitle("Charlie");
        stage.setMinWidth(420);
        stage.setMinHeight(520);
        stage.setScene(new Scene(mainLayout, 520, 640));
        stage.show();
        userInput.requestFocus();
    }

    /**
     * Displays one user command and Charlie's response, then clears the input field.
     */
    private void handleUserInput() {
        String input = userInput.getText();
        String response = charlie.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                DialogBox.getCharlieDialog(response));
        userInput.clear();

        if (charlie.isExitRequested()) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
            PauseTransition closeDelay = new PauseTransition(Duration.seconds(1));
            closeDelay.setOnFinished(event -> stage.close());
            closeDelay.play();
        }
    }
}
