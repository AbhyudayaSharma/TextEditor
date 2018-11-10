import javax.swing.*;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.io.*;
import java.util.StringTokenizer;

class Editor extends JPanel {
    final static String FILE_EXTENSION = ".std";
    private final StylizedTextPane textPane;

    Editor() {
        super();
        textPane = new StylizedTextPane();

        // no soft-wraps
        var noWrapPanel = new JPanel(new BorderLayout());
        noWrapPanel.add(textPane);
        // scrollable interface
        var scrollPane = new JScrollPane(noWrapPanel);

        // add the scrollPane
        setLayout(new GridLayout(1, 1));
        add(scrollPane);
    }

    /**
     * Counts the number of words in the panel
     *
     * @return the number of words
     */
    int wordCount() {
        return new StringTokenizer(textPane.getText(), " ,.!?/\\()[]{};:").countTokens();
    }

    /**
     * Saves the data in the editor to a file
     *
     * @param file data is written to this file
     * @throws IOException when unable to write the file
     */
    void save(File file) throws IOException {
        var path = file.getAbsolutePath();
        // The file should have the extension applied if it doesn't already
        if (!path.endsWith(FILE_EXTENSION)) {
            path += FILE_EXTENSION;
        }

        try (var oos = new ObjectOutputStream(new FileOutputStream(path))) {
            oos.writeObject(textPane.getStyledDocument());
        }
    }

    /**
     * Opens a supported Styled Document Format file
     *
     * @param file the Styled Document File to be opened.
     * @throws IOException when unable to open the file
     */
    void open(File file) throws IOException {
        if (textPane.getText().length() != 0) {
            int response = JOptionPane.showOptionDialog(getTopLevelAncestor(), "You have data in you editor. " +
                            "You will lose it when you load a new file. Do you want to continue?", "Warning",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, JOptionPane.NO_OPTION);
            if (response == JOptionPane.NO_OPTION) {
                return;
            }
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            var document = (DefaultStyledDocument) ois.readObject();
            textPane.setStyledDocument(document);
        } catch (ClassNotFoundException | IOException e) {
            throw new IOException("Unsupported file format!");
        }
    }

    /**
     * Toggles the BOLD attribute on the selected text
     */
    void toggleBoldOnSelection() {
        Style bold = textPane.addStyle("bold", null);
        StyleConstants.setBold(bold, !isSelectionBold());
        addAttribute(bold);
    }

    /**
     * Checks if the selected text is bold
     *
     * @return true if every character in the selection is bold. Otherwise false.
     */
    boolean isSelectionBold() {
        var selectionStart = textPane.getSelectionStart();
        var selectionEnd = textPane.getSelectionEnd();
        var document = textPane.getStyledDocument();

        boolean isBold = selectionStart != selectionEnd;
        for (int i = selectionStart; i < selectionEnd; i++) {
            var element = document.getCharacterElement(i);
            if (!StyleConstants.isBold(element.getAttributes())) {
                isBold = false;
                break;
            }
        }
        return isBold;
    }

    /**
     * Toggles ITALIC on selected text
     */
    void toggleItalicsOnSelection() {
        Style italic = textPane.addStyle("italic", null);
        StyleConstants.setItalic(italic, !isSelectionItalic());
        addAttribute(italic);
    }

    /**
     * Utility function to add a style to the selected text
     *
     * @param style the style to be added
     */
    private void addAttribute(Style style) {
        var selectionStart = textPane.getSelectionStart();
        var selectionEnd = textPane.getSelectionEnd();
        var document = textPane.getStyledDocument();
        for (var i = selectionStart; i < selectionEnd; i++) {
            var oldAttributes = document.getCharacterElement(i).getAttributes();
            var newAttributes = new SimpleAttributeSet(oldAttributes);
            newAttributes.removeAttribute(style.getName()); // remove if it already exists
            newAttributes.addAttributes(style);
            document.setCharacterAttributes(i, 1, newAttributes, true);
        }
    }

    /**
     * Checks if the selected text is italic
     *
     * @return true if every character in the selection is italic. Otherwise false.
     */
    boolean isSelectionItalic() {
        var selectionStart = textPane.getSelectionStart();
        var selectionEnd = textPane.getSelectionEnd();
        var document = textPane.getStyledDocument();

        boolean isItalics = selectionStart != selectionEnd;
        for (int i = selectionStart; i < selectionEnd; i++) {
            var element = document.getCharacterElement(i);
            if (!StyleConstants.isItalic(element.getAttributes())) {
                isItalics = false;
                break;
            }
        }
        return isItalics;
    }


    /**
     * Wrapper for cut
     */
    void cut() {
        textPane.cut();
    }

    /**
     * Wrapper for copy
     */
    void copy() {
        textPane.copy();
    }

    /**
     * Wrapper for paste
     */
    void paste() {
        textPane.paste();
    }

    /**
     * Finds text in the textPane with a dialog for input
     */
    void findText() {
        var input = (String) JOptionPane.showInputDialog(getTopLevelAncestor(), "Please enter the text to find:",
                "Find Text", JOptionPane.QUESTION_MESSAGE);
        if (input != null) {
            if (!findText(input)) {
                JOptionPane.showMessageDialog(getTopLevelAncestor(), "Given text was not found in the editor.",
                        "Text not found", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    /**
     * Finds text in the textPane
     *
     * @param str the string to be found
     */
    private boolean findText(String str) {
        var data = textPane.getText();
        var index = data.indexOf(str);
        if (index >= 0) {
            textPane.setSelectionStart(index);
            textPane.setSelectionEnd(index + str.length());
            return true;
        } else {
            return false;
        }
    }
}
