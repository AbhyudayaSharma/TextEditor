import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Locale;
import java.util.StringTokenizer;

class Editor extends JPanel {
    final static String FILE_EXTENSION = ".std";
    private static final String WORD_DELIMITERS = " ,.!?/\\()[]{};:";
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
     * @return the total number of words
     */
    int wordCount() {
        return new StringTokenizer(textPane.getText(), WORD_DELIMITERS).countTokens();
    }

    /**
     * Counts the number of words in the selection.
     *
     * @return the number of words in the current selection
     */
    int selectedWordCount() {
        var selectionStart = textPane.getSelectionStart();
        var selectionEnd = textPane.getSelectionEnd();
        return selectionStart == selectionEnd ? 0 :
                new StringTokenizer(textPane.getText().substring(selectionStart, selectionEnd), WORD_DELIMITERS)
                        .countTokens();
    }

    /**
     * Counts the total number of characters in the editor
     *
     * @return the total number of characters
     */
    int charCount() {
        return textPane.getText().length();
    }

    /**
     * Counts the number of characters in the selection
     *
     * @return the number of characters in the selection
     */
    int selectedCharCount() {
        var selectionStart = textPane.getSelectionStart();
        var selectionEnd = textPane.getSelectionEnd();

        return selectionEnd == selectionStart ? 0 : textPane.getText().substring(selectionStart, selectionEnd).length();
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
        var panel = new JPanel(new GridLayout(2, 2));
        var textField = new JTextField(10);
        var checkBox = new JCheckBox("Match Case", true);
        panel.add(new JLabel("Find what:"));

        panel.add(textField);
        panel.add(checkBox);
        textField.requestFocus();

        var input = JOptionPane.showConfirmDialog(getTopLevelAncestor(), panel,
                "Find", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (input == JOptionPane.OK_OPTION) {
            if (!findText(textField.getText(), checkBox.isSelected())) {
                JOptionPane.showMessageDialog(getTopLevelAncestor(), "Given text was not found in the editor.",
                        "Text not found", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    /**
     * Finds text in the textPane
     *
     * @param str           the string to be found
     * @param caseSensitive find text ignoring case if false
     */
    private boolean findText(String str, boolean caseSensitive) {
        var data = textPane.getText();

        // convert both to lowercase if not case-sensitive
        if (!caseSensitive) {
            data = data.toLowerCase(Locale.US);
            str = str.toLowerCase(Locale.US);
        }

        var index = data.indexOf(str);
        if (index >= 0) {
            textPane.setSelectionStart(index);
            textPane.setSelectionEnd(index + str.length());
            return true;
        } else {
            return false;
        }
    }

    /**
     * Replaces the text in the panel while respecting the initial formatting
     *
     * @param from          the text to be replaced
     * @param to            the replaced text
     * @param caseSensitive replace text ignoring case if false
     * @param replaceAll    whether to replace all occurrences or just the first
     * @return true if replaced successfully, false if unable to replace
     */
    private boolean replaceText(String from, String to, boolean caseSensitive, boolean replaceAll) {
        boolean flag;
        boolean replaced = false;
        do {
            flag = replaceText(from, to, caseSensitive);
            replaced = flag || replaced;
        } while (flag && replaceAll);
        return replaced;
    }

    /**
     * Replaces the first occurrence of the text 'from' to the text 'to'
     *
     * @param from          the text to be replaced
     * @param to            the replaced text
     * @param caseSensitive replace text ignoring case if false
     * @return true if able to replace, false otherwise.
     */
    private boolean replaceText(String from, String to, boolean caseSensitive) {
        var data = textPane.getText();

        if (!caseSensitive) {
            data = data.toLowerCase(Locale.US);
            from = from.toLowerCase(Locale.US);
        }

        var index = data.indexOf(from);
        if (index >= 0) {
            var list = new ArrayList<AttributeSet>(from.length());
            var document = textPane.getStyledDocument();

            for (int i = 0; i < from.length(); i++) {
                list.add(document.getCharacterElement(i + index).getAttributes());
            }

            try {
                document.remove(index, from.length());
                document.insertString(index, to, null);

                var size = list.size();

                for (int i = 0; i < to.length(); i++) {
                    if (i < size) {
                        document.setCharacterAttributes(i + index, 1, list.get(i), true);
                    } else {
                        // Match the attributes of the last character.
                        document.setCharacterAttributes(i + index, 1, list.get(size - 1), true);
                    }
                }
            } catch (BadLocationException e) {
                e.printStackTrace();
                return false;
            }
            findText(to, true);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Replaces the text in the textPane with a dialog for input
     */
    void replaceText() {
        var fromField = new JTextField(15);
        var toField = new JTextField(15);
        var replaceAllCheck = new JCheckBox("Replace All", false);
        var caseSensitiveCheck = new JCheckBox("Match case", true);
        var panel = new JPanel();

        panel.setLayout(new GridLayout(3, 2));
        panel.add(new JLabel("Find what:"));
        panel.add(fromField);
        panel.add(new JLabel("Replace with:"));
        panel.add(toField);
        panel.add(replaceAllCheck);
        panel.add(caseSensitiveCheck);

        fromField.requestFocus();

        var input = JOptionPane.showConfirmDialog(getTopLevelAncestor(), panel, "Replace",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (input == JOptionPane.OK_OPTION) {
            if (!replaceText(fromField.getText(), toField.getText(), caseSensitiveCheck.isSelected(),
                    replaceAllCheck.isSelected())) {
                JOptionPane.showMessageDialog(getTopLevelAncestor(), "The text was not found.",
                        "TextEditor", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
}
