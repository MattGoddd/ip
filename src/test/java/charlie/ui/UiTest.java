package charlie.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class UiTest {
    @Test
    public void graphicalUi_showMessageAndMessages_emitsUnindentedLines() {
        List<String> output = new ArrayList<>();
        Ui ui = new Ui(output::add);

        ui.showMessage("first");
        ui.showMessages("second", "third");
        ui.showHorizontalLine();

        assertEquals(List.of("first", "second", "third"), output);
    }

    @Test
    public void graphicalUi_showIntro_emitsBannerAndGreetingWithoutSeparators() {
        List<String> output = new ArrayList<>();

        new Ui(output::add).showIntro();

        assertEquals(7, output.size());
        assertTrue(output.get(0).contains("____"));
        assertEquals("Hello! I'm Charlie!", output.get(5));
        assertEquals("What do you want to do today?", output.get(6));
    }

    @Test
    public void graphicalUi_showOutroAndLoadingError_emitsExpectedMessages() {
        List<String> output = new ArrayList<>();
        Ui ui = new Ui(output::add);

        ui.showLoadingError("broken file");
        ui.showOutro();
        ui.close();

        assertEquals(List.of(
                "Error loading saved tasks: broken file",
                "Goodbye! See you next time."), output);
    }
}
