import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.io.*;
import java.util.StringTokenizer;

class Editor extends JPanel {
    final static String FILE_EXTENSION = ".std";
    private JTextPane textPane;

    Editor() {
        super();
        textPane = new JTextPane();
        textPane.setText("");

        // no soft-wraps
        var noWrapPanel = new JPanel(new BorderLayout());
        noWrapPanel.add(textPane);
        // scrollable interface
        var scrollPane = new JScrollPane(noWrapPanel);
        textPane.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.BLACK, Color.GRAY));

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
        var selectionLength = selectionEnd - selectionStart;
        var document = textPane.getStyledDocument();
        try {
            var selection = document.getText(selectionStart, selectionLength);
            document.remove(selectionStart, selectionLength);
            // TODO: 11/4/2018 use SimpleAttributeSet to have multiple properties.
//            MutableAttributeSet mas = new SimpleAttributeSet(document.getCharacterElement(1).getAttributes());
            document.insertString(selectionStart, selection, style);
        } catch (BadLocationException e) {
            e.printStackTrace(); // should never happen
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
}
