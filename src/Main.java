import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;
import java.awt.Font;
import java.util.Enumeration;

// Uygulamanın giriş noktası: tema/font ayarlarını yapar, Singleton mağazayı yükler ve giriş ekranını açar.
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                setUIFont(new FontUIResource(new Font("Segoe UI", Font.PLAIN, 14)));
            } catch (Exception e) {
                e.printStackTrace();
            }

            // SINGLETON: Tek mağaza örneği.
            Magaza magaza = Magaza.getInstance();
            magaza.dosyadanUrunEkle();

            new GirisEkrani(magaza);
        });
    }

    // Tüm Swing bileşenleri için varsayılan fontu ayarlar.
    private static void setUIFont(FontUIResource f) {
        Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            if (UIManager.get(key) instanceof FontUIResource) {
                UIManager.put(key, f);
            }
        }
    }
}
