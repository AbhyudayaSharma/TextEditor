import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.util.StringTokenizer;

class Editor extends JPanel {
    private JTextPane textPane;

    Editor() {
        super();
        textPane = new JTextPane();
        textPane.setContentType("text/html");
        textPane.setText("<html><body>This is some random text.</body></html>");

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
        System.out.println(textPane.getText());
        return new StringTokenizer(textPane.getText(), " ,.-").countTokens();
    }
}
