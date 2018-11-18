import javax.swing.text.AttributeSet;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;

/**
 * A clipboard used by the {@link StylizedTextPane}.
 * Sets the system clipboard to plain text on cut or copy.
 * If retrieving data from clipboard inside a {@link StylizedTextPane},
 * the data is returned as characters with their respective {@link javax.swing.text.AttributeSet}
 * A singleton class representing that there exists just one Clipboard
 */
class StylizedClipboard {
    private final static StylizedClipboard clipboard = new StylizedClipboard();
    private final ArrayList<StylizedCharacter> contents;

    /**
     * Singleton initializer
     */
    private StylizedClipboard() {
        contents = new ArrayList<>();
    }

    /**
     * Returns the singleton instance of the clipboard
     *
     * @return the clipboard
     */
    static StylizedClipboard getClipboard() {
        return clipboard;
    }

    /**
     * Returns the contents of the clipboard
     *
     * @return the contents of the clipboard
     */
    ArrayList<StylizedCharacter> getContents() {
        return contents;
    }

    /**
     * Sets the contents of the clipboard from the characters and their attributes provided
     * as an {@link ArrayList} argument.
     *
     * @param newContents an {@link ArrayList} containing the characters and their respective attributes
     */
    void setContents(ArrayList<StylizedCharacter> newContents) {
        clear();
        contents.addAll(newContents);
        setSystemClipboard(getContentsAsString());
    }

    /**
     * Clear the clipboard. Also clear the system clipboard.
     */
    private void clear() {
        contents.clear();
        setSystemClipboard("");
    }

    /**
     * Returns the contents of the clipboard as a {@link String}
     *
     * @return String containing the contents of the clipboard.
     */
    String getContentsAsString() {
        var sb = new StringBuilder();
        for (StylizedCharacter c :
                contents) {
            sb.append(c.character);
        }
        return sb.toString();
    }

    /**
     * Set the system clipboard to the {@link String} provided as the argument
     *
     * @param s the string to which to set the clipboard
     */
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

        /**
         * A creates the container for the characters
         *
         * @param character  the character
         * @param attributes the attributes of the character
         */
        StylizedCharacter(Character character, AttributeSet attributes) {
            this.character = character;
            this.attributes = attributes;
        }
    }
}
