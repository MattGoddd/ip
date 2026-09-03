package charlie.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * Displays one wrapped message aligned to identify its sender.
 */
public class DialogBox extends HBox {
    /** Maximum width of one message bubble. */
    private static final double MESSAGE_MAX_WIDTH = 360;

    /**
     * Creates a message bubble with the requested alignment and color.
     *
     * @param text Message to display.
     * @param alignment Side on which to place the message.
     * @param backgroundColor CSS background color for the message bubble.
     */
    private DialogBox(String text, Pos alignment, String backgroundColor) {
        Label message = new Label(text);
        message.setWrapText(true);
        message.setMaxWidth(MESSAGE_MAX_WIDTH);
        message.setPadding(new Insets(10));
        message.setStyle("-fx-background-color: " + backgroundColor
                + "; -fx-background-radius: 10;");

        setAlignment(alignment);
        getChildren().add(message);
    }

    /**
     * Returns a right-aligned dialog for text entered by the user.
     *
     * @param text User's message.
     * @return User dialog box.
     */
    public static DialogBox getUserDialog(String text) {
        return new DialogBox(text, Pos.TOP_RIGHT, "#d8ecff");
    }

    /**
     * Returns a left-aligned dialog for Charlie's response.
     *
     * @param text Charlie's message.
     * @return Charlie dialog box.
     */
    public static DialogBox getCharlieDialog(String text) {
        return new DialogBox(text, Pos.TOP_LEFT, "#eeeeee");
    }
}
