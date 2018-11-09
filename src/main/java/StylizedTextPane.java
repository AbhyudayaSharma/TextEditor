import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;

class StylizedTextPane extends JTextPane {
    private final StylizedClipboard clipboard = StylizedClipboard.getClipboard();

    StylizedTextPane() {
        super();
        setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.BLACK, Color.GRAY));
    }

    @Override
    public void cut() {
        clipboard.setContents(getSelectionAsList(), true);
    }

    @Override
    public void copy() {
        clipboard.setContents(getSelectionAsList(), false);
    }

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

    private ArrayList<StylizedClipboard.StylizedCharacter> getSelectionAsList() {
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
            document.remove(selectionStart, selectionEnd - selectionStart);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        return list;
    }
}
