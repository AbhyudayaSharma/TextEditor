import javax.swing.text.AttributeSet;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;

/**
 * A clipboard used by the {@link StylizedTextPane}.
 * Sets the system clipboard to plain text on cut or copy.
 * If retrieving data from clipboard inside a {@link StylizedTextPane},
 * the data is returned as characters with their respective {@link javax.swing.text.AttributeSet}
 */
class StylizedClipboard {
    private final static StylizedClipboard clipboard = new StylizedClipboard();
    private final ArrayList<StylizedCharacter> contents;

    private StylizedClipboard() {
        contents = new ArrayList<>();
    }

    static StylizedClipboard getClipboard() {
        return clipboard;
    }

    ArrayList<StylizedCharacter> getContents() {
        return contents;
    }

    private void setContents(ArrayList<StylizedCharacter> newContents) {

        contents.addAll(newContents);
        setSystemClipboard(getContentsAsString());
    }

    private void clear() {
        contents.clear();
        setSystemClipboard("");
    }

    void setContents(ArrayList<StylizedCharacter> newContents, boolean deleteSelection) {
        if (deleteSelection) {
            clear();
        }
        setContents(newContents);
    }

    String getContentsAsString() {
        var sb = new StringBuilder();
        for (StylizedCharacter c :
                contents) {
            sb.append(c.character);
        }
        return sb.toString();
    }

    private void setSystemClipboard(String s) {
        var clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        var selection = new StringSelection(s);
        clipboard.setContents(selection, selection);
    }

    /**
     * A container for the attributes of a styled character
     */
    static class StylizedCharacter {
        final Character character;
        final AttributeSet attributes;

        StylizedCharacter(Character character, AttributeSet attributes) {
            this.character = character;
            this.attributes = attributes;
        }
    }
}