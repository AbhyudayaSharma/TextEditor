import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Function;

/**
 * A text editor that uses {@link StyledDocument} for setting attributes to each character.
 *
 * @author Abhyudaya Sharma
 */
class Editor extends JPanel {
    private static final String FILE_EXTENSION = ".std";
    private static final String WORD_DELIMITERS = " ,.!?/\\()[]{};:\t\r\n";
    private final StylizedTextPane textPane;

    private String savedFilePath = null;

    /**
     * Creates a new {@link Editor} with a scrollable {@link StylizedTextPane}
     * which has no soft-wraps.
     */
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
        return textPane.getSelectionStart() == textPane.getSelectionEnd() ? 0 :
                new StringTokenizer(textPane.getSelectedText(), WORD_DELIMITERS).countTokens();
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
     * Opens up a dialog to select where to store the file.
     */
    void saveAs() {
        var fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Styled documents",
                Editor.FILE_EXTENSION.substring(1))); // FileNameExtensionFilter doesn't want a '.'
        fileChooser.showDialog(this, "Save");
        var file = fileChooser.getSelectedFile();
        if (file != null) {
            // The file should have the extension applied if it doesn't already
            var path = file.getAbsolutePath();
            if (!path.endsWith(FILE_EXTENSION)) path += FILE_EXTENSION;
            saveAs(path, true);
        }
    }

    /**
     * Internal method which provides the logic for writing the textPane's {@link StyledDocument}
     * to the given filepath.
     *
     * @param filePath    the path of the file.
     * @param checkExists check for replacing the file if already exists
     */
    private void saveAs(String filePath, boolean checkExists) {
        File file = new File(filePath);
        if (checkExists && file.exists()) {
            var selection = JOptionPane.showConfirmDialog(getTopLevelAncestor(), "File already exists. " +
                    "Do you want to replace it?", "Replace?", JOptionPane.YES_NO_OPTION);
            if (selection == JOptionPane.NO_OPTION) return;
        }

        try (var oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(textPane.getStyledDocument());
            JOptionPane.showMessageDialog(getTopLevelAncestor(), "File written successfully!",
                    "Information", JOptionPane.INFORMATION_MESSAGE);
            savedFilePath = filePath;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(getTopLevelAncestor(), ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Save the current text stored in the textPane as a {@link StyledDocument}.
     */
    void save() {
        if (savedFilePath != null) {
            saveAs(savedFilePath, false);
        } else {
            saveAs();
        }
    }

    /**
     * Opens up a dialog to select the {@link StyledDocument} file to be opened
     */
    void open() {
        var fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Styled documents",
                Editor.FILE_EXTENSION.substring(1))); // FileNameExtensionFilter doesn't want a '.'
        fileChooser.showDialog(getTopLevelAncestor(), "Open");
        var file = fileChooser.getSelectedFile();
        if (file != null) {
            try {
                open(file);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(getTopLevelAncestor(), ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Opens a supported Styled Document Format file
     *
     * @param file the Styled Document File to be opened.
     * @throws IOException when unable to open the file
     */
    private void open(File file) throws IOException {
        if (textPane.getText().length() != 0) {
            int response = JOptionPane.showOptionDialog(getTopLevelAncestor(), "You have data in you editor. " +
                            "You will lose it when you load a new file. Do you want to continue?", "Warning",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, null, JOptionPane.NO_OPTION);
            if (response == JOptionPane.NO_OPTION) {
                return;
            }
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            var document = (DefaultStyledDocument) ois.readObject();
            textPane.setStyledDocument(document);
            savedFilePath = file.getAbsolutePath();
        } catch (ClassNotFoundException | IOException e) {
            throw new IOException("Unsupported file format!");
        }
    }

    /**
     * Toggles the BOLD attribute on the selected text
     */
    void toggleBoldOnSelection() {
        toggleAttributeOnSelection("bold", StyleConstants::setBold, this::isSelectionBold);
    }

    /**
     * Toggles ITALIC on selected text
     */
    void toggleItalicsOnSelection() {
        toggleAttributeOnSelection("italic", StyleConstants::setItalic, this::isSelectionItalic);
    }

    /**
     * Returns the filePath to the saved if file (if any)
     *
     * @return null if no file is saved, the path of the file otherwise.
     */
    String getSavedFilePath() {
        return savedFilePath;
    }

    /**
     * Toggles the UNDERLINE attribute on the selected text.
     */
    void toggleUnderlineOnSelection() {
        toggleAttributeOnSelection("underline", StyleConstants::setUnderline, this::isSelectionUnderline);
    }

    /**
     * Checks whether each element of the selection has the property applied.
     *
     * @param propertyChecker a {@link Function} from {@link StyleConstants} like {@code StyleConstants::isBold}
     * @return true if all chars in the selection return true for the {@code propertyChecker}, false otherwise.
     */
    private boolean selectionHasAttribute(Function<AttributeSet, Boolean> propertyChecker) {
        var selectionStart = textPane.getSelectionStart();
        var selectionEnd = textPane.getSelectionEnd();
        var document = textPane.getStyledDocument();

        boolean hasProperty = selectionStart != selectionEnd;
        for (int i = selectionStart; i < selectionEnd; i++) {
            var element = document.getCharacterElement(i);

            if (!propertyChecker.apply(element.getAttributes())) {
                hasProperty = false;
                break;
            }
        }
        return hasProperty;
    }

    /**
     * Checks if the selected text is bold
     *
     * @return true if every character in the selection is bold. Otherwise false.
     */
    boolean isSelectionBold() {
        return selectionHasAttribute(StyleConstants::isBold);
    }

    /**
     * Checks if the selected text is italic
     *
     * @return true if every character in the selection is italic. Otherwise false.
     */
    boolean isSelectionItalic() {
        return selectionHasAttribute(StyleConstants::isItalic);
    }

    /**
     * Checks if the selected text is underlined
     *
     * @return true if every character in the selection is underline. Otherwise false.
     */
    boolean isSelectionUnderline() {
        return selectionHasAttribute(StyleConstants::isUnderline);
    }

    /**
     * Toggles an attribute on the selection. Toggle is based on the value of selectionAttributeChecker.
     *
     * @param styleName                 the name for the attribute style
     * @param attributeSetter           {@link BiConsumer} used to set the attribute on the selection,
     *                                  for example, StyleConstants::setBold.
     * @param selectionAttributeChecker checks whether the attribute is currently applied. The value returned
     *                                  by this is used to toggle the attribute.
     */
    private void toggleAttributeOnSelection(String styleName, BiConsumer<Style, Boolean> attributeSetter,
                                            BooleanSupplier selectionAttributeChecker) {
        Style style = textPane.addStyle(styleName, null);
        attributeSetter.accept(style, !selectionAttributeChecker.getAsBoolean());
        addAttribute(style);
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
            if (!findText(textField.getText(), checkBox.isSelected(), false)) {
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
     * @param lastIndex     true => last index; false => first index
     * @return true if the text was found, false otherwise.
     */
    private boolean findText(String str, boolean caseSensitive, boolean lastIndex) {
        var data = textPane.getText();

        // convert both to lowercase if not case-sensitive
        if (!caseSensitive) {
            data = data.toLowerCase(Locale.US);
            str = str.toLowerCase(Locale.US);
        }

        var index = lastIndex ? data.lastIndexOf(str) : data.indexOf(str);
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

        if (replaceAll) {
            // If all occurrences have been replaced, highlight the last occurrence.
            findText(to, true, true);
        }
        return replaced;
    }

    /**
     * Converts the selected text to Uppercase.
     */
    void selectionToUpperCase() {
        var selectionStart = textPane.getSelectionStart();
        var selectionEnd = textPane.getSelectionEnd();
        var substring = textPane.getText().substring(selectionStart, selectionEnd);
        var upperCaseSubstring = substring.toUpperCase(Locale.US);
        replaceText(substring, upperCaseSubstring, true, selectionStart);
    }

    /**
     * Converts the selected text to Lowercase.
     */
    void selectionToLowerCase() {
        var selectionStart = textPane.getSelectionStart();
        var selectionEnd = textPane.getSelectionEnd();
        var substring = textPane.getText().substring(selectionStart, selectionEnd);
        var upperCaseSubstring = substring.toLowerCase(Locale.US);
        replaceText(substring, upperCaseSubstring, true, selectionStart);
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
        return replaceText(from, to, caseSensitive, 0);
    }

    /**
     * Replaces the first occurrence of the text 'from' to the text 'to', starting the search from fromIndex
     *
     * @param from          the text to be replaced
     * @param to            the replaced text
     * @param caseSensitive replace text ignoring case if false
     * @param fromIndex     the index from which to start the search
     * @return true if able to replace, false otherwise.
     */
    private boolean replaceText(String from, String to, boolean caseSensitive, int fromIndex) {
        var data = textPane.getText();

        if (!caseSensitive) {
            data = data.toLowerCase(Locale.US);
            from = from.toLowerCase(Locale.US);
        }

        var index = data.indexOf(from, fromIndex);
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
            findText(to, true, false);
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

    /**
     * Utility for closing the editor while warning about
     * unsaved changes.
     *
     * @return true if ready to quit, false otherwise.
     */
    boolean confirmClose() {
        var output = JOptionPane.showConfirmDialog(getTopLevelAncestor(),
                "Do you want to exit? You may have unsaved changes.",
                "Confirm exit", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return output == JOptionPane.YES_OPTION;
    }

    /**
     * Sets the font on the selected text in the editor.
     *
     * @param font the font to be set to the selected text.
     */
    void setSelectionFont(final Font font) {
        var style = textPane.addStyle("font", null);
        StyleConstants.setFontFamily(style, font.getFamily());
        StyleConstants.setFontSize(style, font.getSize());
        addAttribute(style);
    }
}
