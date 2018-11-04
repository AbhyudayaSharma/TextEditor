import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.text.DefaultStyledDocument;
import java.awt.*;
import java.io.*;
import java.util.StringTokenizer;

class Editor extends JPanel {
    private JTextPane textPane;

    final static String FILE_EXTENSION = ".std";

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

        System.out.println(textPane.getStyledDocument().getClass().getCanonicalName());
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
            if (response == JOptionPane.YES_OPTION) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                    var document = (DefaultStyledDocument) ois.readObject();
                    textPane.setStyledDocument(document);
                } catch (ClassNotFoundException | IOException e) {
                    throw new IOException("Unsupported file format!");
                }
            }
        }
    }
}
