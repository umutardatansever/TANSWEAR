package gui;

import java.awt.Color;
import java.awt.Font;

// GUI genelinde kullanılan tema renkleri ve fontları için tek nokta.
// Sayısal renk değerlerinin her sınıfta tekrarlanmasını engeller.
public final class UIThemes {

    private UIThemes() {}

    public static final Color BG_DARK      = new Color(30, 30, 30);
    public static final Color BG_PANEL     = new Color(45, 45, 45);
    public static final Color BG_INPUT     = new Color(50, 50, 50);
    public static final Color BG_BUTTON    = new Color(60, 60, 60);
    public static final Color BG_PRIMARY   = new Color(70, 130, 180);
    public static final Color FG_TEXT      = Color.WHITE;
    public static final Color FG_MUTED     = Color.LIGHT_GRAY;
    public static final Color FG_DANGER    = new Color(232, 90, 90);
    public static final Color FG_SUCCESS   = new Color(120, 200, 120);

    public static final Font  TITLE_FONT   = new Font("SansSerif", Font.BOLD, 26);
    public static final Font  HEADING_FONT = new Font("SansSerif", Font.BOLD, 18);
    public static final Font  BODY_FONT    = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font  MONO_FONT    = new Font("Monospaced", Font.PLAIN, 12);
}
