import drawing.DrawingPanel;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Native look and feel
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException |
                    UnsupportedLookAndFeelException ignore) {
            }

            final var frame = new JFrame("TextEditor");
            final var editor = new Editor();
            frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            frame.setLayout(new GridBagLayout());

            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    if (editor.confirmClose()) {
                        System.exit(0);
                    }
                }
            });

            JMenuBar menuBar = new JMenuBar();
            JMenu fileMenu = new JMenu("File");
            JMenu editMenu = new JMenu("Edit");
            JMenu formatMenu = new JMenu("Format");

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
            JMenuItem findButton = new JMenuItem("Find...");
            JMenuItem replaceButton = new JMenuItem("Replace...");

            JCheckBoxMenuItem boldButton = new JCheckBoxMenuItem("Bold");
            JCheckBoxMenuItem italicButton = new JCheckBoxMenuItem("Italic");
            JCheckBoxMenuItem underlineButton = new JCheckBoxMenuItem("Underline");
            JMenuItem upperCaseButton = new JMenuItem("Upper Case");
            JMenuItem lowerCaseButton = new JMenuItem("Lower Case");
            JMenuItem fontButton = new JMenuItem("Font...");

            cutButton.setAccelerator(KeyStroke.getKeyStroke('X', InputEvent.CTRL_DOWN_MASK));
            copyButton.setAccelerator(KeyStroke.getKeyStroke('C', InputEvent.CTRL_DOWN_MASK));
            pasteButton.setAccelerator(KeyStroke.getKeyStroke('V', InputEvent.CTRL_DOWN_MASK));
            findButton.setAccelerator(KeyStroke.getKeyStroke('F', InputEvent.CTRL_DOWN_MASK));
            replaceButton.setAccelerator(KeyStroke.getKeyStroke('H', InputEvent.CTRL_DOWN_MASK));

            cutButton.addActionListener(e -> editor.cut());
            copyButton.addActionListener(e -> editor.copy());
            pasteButton.addActionListener(e -> editor.paste());
            findButton.addActionListener(e -> editor.findText());
            replaceButton.addActionListener(e -> editor.replaceText());

            fileMenu.add(openButton);
            fileMenu.add(new JSeparator());
            fileMenu.add(saveButton);

            editMenu.add(cutButton);
            editMenu.add(copyButton);
            editMenu.add(pasteButton);
            editMenu.add(new JSeparator());
            editMenu.add(findButton);
            editMenu.add(replaceButton);

            formatMenu.add(boldButton);
            formatMenu.add(italicButton);
            formatMenu.add(underlineButton);
            formatMenu.add(new JSeparator());
            formatMenu.add(upperCaseButton);
            formatMenu.add(lowerCaseButton);
            formatMenu.add(new JSeparator());
            formatMenu.add(fontButton);

            formatMenu.addMenuListener(new MenuListener() {
                @Override
                public void menuSelected(MenuEvent e) {
                    boldButton.setState(editor.isSelectionBold());
                    italicButton.setState(editor.isSelectionItalic());
                    underlineButton.setState(editor.isSelectionUnderline());
                }

                @Override
                public void menuDeselected(MenuEvent e) {
                }

                @Override
                public void menuCanceled(MenuEvent e) {
                }
            });

            boldButton.addActionListener(e -> editor.toggleBoldOnSelection());
            italicButton.addActionListener(e -> editor.toggleItalicsOnSelection());
            underlineButton.addActionListener(e -> editor.toggleUnderlineOnSelection());
            upperCaseButton.addActionListener(e -> editor.selectionToUpperCase());
            lowerCaseButton.addActionListener(e -> editor.selectionToLowerCase());
            fontButton.addActionListener(e -> {
                var fontSelector = new FontSelector();
                var response = JOptionPane.showConfirmDialog(frame, fontSelector, "Font",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (response == JOptionPane.OK_OPTION) {
                    editor.setSelectionFont(fontSelector.getSelectedFont());
                }
            });

            boldButton.setAccelerator(KeyStroke.getKeyStroke('B', InputEvent.CTRL_DOWN_MASK));
            italicButton.setAccelerator(KeyStroke.getKeyStroke('I', InputEvent.CTRL_DOWN_MASK));
            underlineButton.setAccelerator(KeyStroke.getKeyStroke('U', InputEvent.CTRL_DOWN_MASK));

            menuBar.add(fileMenu);
            menuBar.add(editMenu);
            menuBar.add(formatMenu);
            frame.setJMenuBar(menuBar);

            JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JLabel wordCountLabel = new JLabel("0 words");
            JLabel charCountLabel = new JLabel("0 characters");
            ButtonGroup modeGroup = new ButtonGroup();
            JRadioButton editorRadio = new JRadioButton("Editor", true);
            JRadioButton shapesRadio = new JRadioButton("Shapes");
            modeGroup.add(editorRadio);
            modeGroup.add(shapesRadio);

            shapesRadio.addActionListener(e -> {
                JOptionPane.showMessageDialog(frame, new DrawingPanel(), "Drawing Mode",
                        JOptionPane.PLAIN_MESSAGE);
                editorRadio.setSelected(true);
            });

            statusBar.add(new JSeparator(SwingConstants.VERTICAL));
            statusBar.add(new JLabel("Mode: "));
            statusBar.add(editorRadio);
            statusBar.add(shapesRadio);
            statusBar.add(new JSeparator(SwingConstants.VERTICAL));
            statusBar.add(wordCountLabel);
            statusBar.add(new JSeparator(SwingConstants.VERTICAL));
            statusBar.add(charCountLabel);
            statusBar.add(new JSeparator(SwingConstants.VERTICAL));
            statusBar.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED, Color.BLACK, Color.GRAY));

            // update the word-count and char-count every 200ms
            new Timer(200, e -> {
                // wordCountLabel
                var wordCount = editor.wordCount();
                var selectedWordCount = editor.selectedWordCount();
                if (selectedWordCount == 0) {
                    wordCountLabel.setText(wordCount + (wordCount == 1 ? " word" : " words"));
                } else {
                    wordCountLabel.setText(selectedWordCount + " of " + wordCount +
                            (wordCount == 1 ? " word" : " words"));
                }

                // charCountLabel
                var charCount = editor.charCount();
                var selectedCharCount = editor.selectedCharCount();
                if (selectedCharCount == 0) {
                    charCountLabel.setText(charCount + (charCount == 1 ? " character" : " characters"));
                } else {
                    charCountLabel.setText(selectedCharCount + " of " + charCount +
                            (charCount == 1 ? " character" : " characters"));
                }
            }).start();

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

            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.weightx = 1;
            gbc.weighty = 0;
            gbc.fill = 1;
            gbc.insets = new Insets(1, 1, 1, 1);
            frame.add(statusBar, gbc);

            frame.setMinimumSize(new Dimension(300, 200));
            frame.setPreferredSize(new Dimension(1024, 768));
            frame.pack();
            frame.setVisible(true);
        });
    }
}
