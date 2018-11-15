import javax.swing.*;
import java.awt.*;

class FontSelector extends JPanel {
    FontSelector() {
        super();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(300, 300);
    }

    Font getSelectedFont() {
        // TODO: 11/13/2018 Start Here
        System.out.println("font selected");
        return super.getFont();
    }
}
