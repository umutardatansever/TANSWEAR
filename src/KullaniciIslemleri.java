import java.io.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Kullanıcı kaydı / giriş kaydı gibi yardımcı dosya işlemleri.
public class KullaniciIslemleri {

    private static final String KULLANICI_DOSYASI = "kullanicilar.txt";
    private static final String GIRIS_KAYDI_DOSYASI = "giris_kaydi.txt";

    private KullaniciIslemleri() {}

    // Kullanıcı varsa bilgilerini günceller, yoksa yeni kayıt ekler.
    public static void kullaniciEkleTekil(String kullaniciAdi, String sifre, String tip, double bakiye) {
        try {
            File orjinal = new File(KULLANICI_DOSYASI);
            List<String> yeniSatirlar = new ArrayList<>();
            boolean bulundu = false;

            if (orjinal.exists()) {
                try (Scanner sc = new Scanner(orjinal)) {
                    while (sc.hasNextLine()) {
                        String satir = sc.nextLine();
                        if (satir.startsWith(kullaniciAdi + ",")) {
                            bulundu = true;
                            yeniSatirlar.add(kullaniciAdi + "," + sifre + "," + tip + "," + bakiye);
                        } else {
                            yeniSatirlar.add(satir);
                        }
                    }
                }
            }

            if (!bulundu) {
                yeniSatirlar.add(kullaniciAdi + "," + sifre + "," + tip + "," + bakiye);
            }

            try (FileWriter fw = new FileWriter(orjinal, false)) {
                for (String s : yeniSatirlar) {
                    fw.write(s + "\n");
                }
            }
        } catch (IOException e) {
            System.out.println("Kullanıcı yazılamadı: " + e.getMessage());
        }
    }

    public static void girisKaydet(String kullaniciAdi, String sifre) {
        try (FileWriter fw = new FileWriter(GIRIS_KAYDI_DOSYASI, true)) {
            String zaman = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            fw.write(kullaniciAdi + "," + sifre + "," + zaman + "\n");
        } catch (IOException e) {
            System.out.println("Giriş kaydı yapılamadı: " + e.getMessage());
        }
    }
}
