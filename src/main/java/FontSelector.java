import javax.swing.*;
import java.awt.*;

class FontSelector extends JPanel {
    private JList<String> fontNameList;
    private JList<Integer> fontSizeList;
    private JList<String> fontAttributeList;
    private JTextArea previewArea = new JTextArea("AaBbYyZz");

    FontSelector() {
        super();
        setLayout(new GridBagLayout());

        var fontNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        fontNameList = new JList<>(fontNames);
        fontSizeList = new JList<>(new Integer[]{
                8, 9, 10, 11, 12, 14, 16, 18, 20, 22, 24, 26, 28, 36, 48, 72
        });
        fontAttributeList = new JList<>(new String[]{
                "Regular",
                "Italic",
                "Bold",
                "Bold Italic"
        });

        fontNameList.addListSelectionListener(e -> updatePreviewText());
        fontSizeList.addListSelectionListener(e -> updatePreviewText());

        var scrollableFontNames = new JScrollPane(fontNameList);
        var scrollableFontSizes = new JScrollPane(fontSizeList);
        var scrollableFontAttributes = new JScrollPane(fontAttributeList);
        scrollableFontSizes.setPreferredSize(new Dimension(50, 150));
        scrollableFontNames.setPreferredSize(new Dimension(160, 150));
        scrollableFontAttributes.setPreferredSize(new Dimension(80, 150));

        scrollableFontAttributes.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        fontNameList.setSelectedValue(Editor.DEFAULT_FONT.getFamily(), true);
        fontSizeList.setSelectedValue(Editor.DEFAULT_FONT.getSize(), true);
        previewArea.setFont(Editor.DEFAULT_FONT);
        previewArea.setPreferredSize(new Dimension(350, 100));
        previewArea.setEditable(false);
        previewArea.setBackground(getBackground());
        previewArea.setHighlighter(null);

        var gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        add(new JLabel("Font:"), gbc);

        gbc.gridx = 1;
        add(new JLabel("Font style:"));

        gbc.gridx = 2;
        add(new JLabel("Size:"), gbc);

        gbc.gridy = 1;
        gbc.gridx = 0;
        add(scrollableFontNames, gbc);

        gbc.gridx = 1;
        add(scrollableFontAttributes, gbc);

        gbc.gridx = 2;
        add(scrollableFontSizes, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Preview:"), gbc);

        gbc.gridy = 3;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        add(previewArea, gbc);
    }

    private void updatePreviewText() {
        previewArea.setFont(getSelectedFont());
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(375, 320);
    }

    Font getSelectedFont() {
        var fontName = fontNameList.getSelectedValue();
        var fontSize = fontSizeList.getSelectedValue();
        if (fontName != null && fontSize != null) {
            return new Font(fontNameList.getSelectedValue(), Font.PLAIN, fontSizeList.getSelectedValue());
        }
        return null;
    }
}
