package gui;

import javax.swing.*;
import java.awt.*;

// Hover/press animasyonlu, yuvarlatılmış kenarlı modern stilde özel buton bileşeni.
public class ModernButton extends JButton {

    private final Color hoverBackgroundColor;
    private final Color pressedBackgroundColor;

    public ModernButton(String text, Color bg, Color fg) {
        super(text);
        super.setContentAreaFilled(false);
        setBackground(bg);
        setForeground(fg);
        setFocusPainted(false);
        setBorderPainted(false);
        setFont(new Font("SansSerif", Font.BOLD, 14));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        this.hoverBackgroundColor = bg.brighter();
        this.pressedBackgroundColor = bg.darker();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color renk = getBackground();
        if (getModel().isPressed()) {
            renk = pressedBackgroundColor;
        } else if (getModel().isRollover()) {
            renk = hoverBackgroundColor;
        }
        g2.setColor(renk);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
        g2.dispose();

        super.paintComponent(g);
    }
}
