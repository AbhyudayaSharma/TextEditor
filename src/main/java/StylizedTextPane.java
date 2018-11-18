import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;

/**
 * A {@link JTextPane} that uses a {@link javax.swing.text.StyledDocument} to store formatted text.
 * Used in {@link Editor} as the base for writing text. Uses the StylizedClipboard for clipboard functions.
 */
class StylizedTextPane extends JTextPane {
    private final StylizedClipboard clipboard = StylizedClipboard.getClipboard();

    StylizedTextPane() {
        super();
        setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.BLACK, Color.GRAY));
        // override the default CTRL-H
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.CTRL_DOWN_MASK), "Nothing");
    }

    /**
     * Cut the contents into the clipboard
     */
    @Override
    public void cut() {
        clipboard.setContents(getSelectionAsList(true));
    }

    /**
     * Copy the contents into the clipboard
     */
    @Override
    public void copy() {
        clipboard.setContents(getSelectionAsList(false));
    }

    /**
     * Paste the contents of the clipboard into {@link StylizedTextPane}
     */
    @Override
    public void paste() {
        var document = getStyledDocument();
        var selectionStart = getSelectionStart();
        var selectionEnd = getSelectionEnd();
        var selectionLength = selectionEnd - selectionStart;
        var systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        var sysClipboardData = "";

        try {
            sysClipboardData = systemClipboard.getData(DataFlavor.stringFlavor).toString();
        } catch (UnsupportedFlavorException | IOException e) {
            e.printStackTrace();
        }

        var string = clipboard.getContentsAsString();

        try {
            document.remove(selectionStart, selectionLength);
            var newChars = clipboard.getContents();
            if (sysClipboardData.equals(string) && !string.isEmpty()) {
                document.insertString(selectionStart, string, null);
                for (int i = 0; i < string.length(); i++) {
                    document.setCharacterAttributes(i + selectionStart, 1,
                            newChars.get(i).attributes, true);
                }
            } else if (!sysClipboardData.isEmpty()) {
                document.insertString(selectionStart, sysClipboardData, null);
            }
        } catch (BadLocationException ignore) {
        }
    }

    /**
     * Get the contents of the current selection characters with their respective
     * attributes in an {@link ArrayList}.
     *
     * @param deleteSelection delete the current selection if true, do not change the selection otherwise.
     * @return array list containing the characters and their attributes, indexed relative to selection start.
     */
    private ArrayList<StylizedClipboard.StylizedCharacter> getSelectionAsList(boolean deleteSelection) {
        var selectionStart = getSelectionStart();
        var selectionEnd = getSelectionEnd();
        var document = getStyledDocument();
        ArrayList<StylizedClipboard.StylizedCharacter> list = new ArrayList<>();
        try {
            for (int i = selectionStart; i < selectionEnd; i++) {
                char c = document.getText(i, 1).charAt(0);
                AttributeSet set = document.getCharacterElement(i).getAttributes();
                list.add(new StylizedClipboard.StylizedCharacter(c, set));
            }
            if (deleteSelection) {
                document.remove(selectionStart, selectionEnd - selectionStart);
            }
        } catch (BadLocationException ignore) {
        }
        return list;
    }
}
