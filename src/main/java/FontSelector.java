import javax.swing.*;
import java.awt.*;

/**
 * A Panel from which a user can select a font from all the Fonts installed in his/her computer.
 */
class FontSelector extends JPanel {
    private final JList<String> fontNameList;
    private final JList<Integer> fontSizeList;
    private final JList<String> fontAttributeList;
    private final JTextArea previewArea = new JTextArea("AaBbYyZz");

    FontSelector() {
        super();
        setLayout(new GridBagLayout());

        // get all usable font names
        var fontNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        fontNameList = new JList<>(fontNames);
        fontSizeList = new JList<>(new Integer[]{
                8, 9, 10, 11, 12, 14, 16, 18, 20, 22, 24, 26, 28, 36, 48, 72
        });
        fontAttributeList = new JList<>(new String[]{
                "Regular",
                "<html><i>Italic</i></html>", // swing supports basic HTML
                "<html><b>Bold</b></html>",
                "<html><b><i>Bold Italic</i></b></html>"
        });

        // Can only select one font at a time
        fontNameList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fontAttributeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fontSizeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        fontNameList.addListSelectionListener(e -> updatePreviewText());
        fontSizeList.addListSelectionListener(e -> updatePreviewText());
        fontAttributeList.addListSelectionListener(e -> updatePreviewText());

        // Make everything scrollable
        var scrollableFontNames = new JScrollPane(fontNameList);
        var scrollableFontSizes = new JScrollPane(fontSizeList);
        var scrollableFontAttributes = new JScrollPane(fontAttributeList);
        scrollableFontSizes.setPreferredSize(new Dimension(100, 150));
        scrollableFontNames.setPreferredSize(new Dimension(200, 150));
        scrollableFontAttributes.setPreferredSize(new Dimension(175, 150));

        scrollableFontAttributes.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); // consistency

        fontNameList.setSelectedValue(Editor.DEFAULT_FONT.getFamily(), true);
        fontSizeList.setSelectedValue(Editor.DEFAULT_FONT.getSize(), true);
        fontAttributeList.setSelectedIndex(0);

        previewArea.setFont(Editor.DEFAULT_FONT);
        previewArea.setEditable(false);
        previewArea.setBackground(getBackground());
        previewArea.setHighlighter(null); // make the text un-selectable

        var previewPanel = new JPanel();
        previewPanel.setBorder(BorderFactory.createEtchedBorder());
        previewPanel.setLayout(new GridBagLayout());
        previewPanel.add(previewArea, new GridBagConstraints(0, 0, 1, 1, 0,
                0, GridBagConstraints.CENTER, 0, new Insets(0, 0, 0, 0), 0, 0));
        previewPanel.setPreferredSize(new Dimension(500, 175));

        var gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.WEST;
        add(new JLabel("Font:"), gbc);

        gbc.gridx = 1;
        add(new JLabel("Font style:"), gbc);

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
        add(previewPanel, gbc);
    }

    /**
     * Updates the preview text and the font of the font attribute selector
     */
    private void updatePreviewText() {
        var selectedFont = getSelectedFont();
        if (selectedFont != null) {
            previewArea.setFont(selectedFont);
            fontAttributeList.setFont(selectedFont.deriveFont(Font.PLAIN, 12f));
        }
    }

    /**
     * Returns the font selected by the font selector.
     *
     * @return the selected font
     */
    Font getSelectedFont() {
        var fontName = fontNameList.getSelectedValue();
        var fontSize = fontSizeList.getSelectedValue();
        var fontStyle = fontAttributeList.getSelectedIndex();
        int style;

        // Index from the array given to fontAttributeList
        switch (fontStyle) {
            case 1:
                style = Font.ITALIC;
                break;
            case 2:
                style = Font.BOLD;
                break;
            case 3:
                style = Font.BOLD | Font.ITALIC;
                break;
            default:
                style = Font.PLAIN;
                break;
        }

        if (fontName != null && fontSize != null) {
            return new Font(fontName, style, fontSize);
        }

        return null;
    }
}
