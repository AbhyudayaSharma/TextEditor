import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.InputEvent;
import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            final var frame = new JFrame("TextEditor");
            final var editor = new Editor();
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setLayout(new GridBagLayout())
            ;
            JMenuBar menuBar = new JMenuBar();
            JMenu fileMenu = new JMenu("File");
            JMenu editMenu = new JMenu("Edit");

            JMenuItem saveButton = new JMenuItem("Save...");
            saveButton.setAccelerator(KeyStroke.getKeyStroke('S', InputEvent.CTRL_DOWN_MASK));
            saveButton.addActionListener(e -> {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileFilter(new FileNameExtensionFilter("Styled documents",
                        Editor.FILE_EXTENSION.substring(1))); // FileNameExtensionFilter doesn't want a '.'
                fileChooser.showDialog(frame, "Save");
                File file = fileChooser.getSelectedFile();
                if (file != null) {
                    try {
                        editor.save(file);
                        JOptionPane.showMessageDialog(frame, "File written successfully!", "Information",
                                JOptionPane.INFORMATION_MESSAGE);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            JMenuItem openButton = new JMenuItem("Open...");
            openButton.setAccelerator(KeyStroke.getKeyStroke('O', InputEvent.CTRL_DOWN_MASK));
            openButton.addActionListener(e -> {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileFilter(new FileNameExtensionFilter("Styled documents",
                        Editor.FILE_EXTENSION.substring(1))); // FileNameExtensionFilter doesn't want a '.'
                fileChooser.showDialog(frame, "Open");
                File file = fileChooser.getSelectedFile();
                if (file != null) {
                    try {
                        editor.open(file);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            JMenuItem cutButton = new JMenuItem("Cut");
            JMenuItem copyButton = new JMenuItem("Copy");
            JMenuItem pasteButton = new JMenuItem("Paste");

            fileMenu.add(openButton);
            fileMenu.add(new JSeparator());
            fileMenu.add(saveButton);

            editMenu.add(cutButton);
            editMenu.add(copyButton);
            editMenu.add(pasteButton);

            menuBar.add(fileMenu);
            menuBar.add(editMenu);
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

            frame.setPreferredSize(new Dimension(500, 500));
            frame.pack();
            frame.setVisible(true);
        });
    }
}
