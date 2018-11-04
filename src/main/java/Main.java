import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            final var frame = new JFrame("TextEditor");
            final var editor = new Editor();
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setLayout(new GridBagLayout());
            JMenuBar menuBar = new JMenuBar();
            menuBar.add(new JMenu("File"));
            frame.setJMenuBar(menuBar);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 1;
            gbc.gridheight = 1;
            gbc.weightx = 1;
            gbc.weighty = 1;
            gbc.fill = 1;
            gbc.insets = new Insets(1, 1, 1, 1);
            frame.add(editor, gbc);
            System.out.println(editor.wordCount());

            frame.pack();
            frame.setVisible(true);
        });
    }
}
