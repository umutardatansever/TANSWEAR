package util;

import model.Musteri;

import java.io.*;
import java.util.*;

// Tüm dosya tabanlı (urunler.txt, kullanicilar.txt) okuma/yazma işlemlerini tek noktada toplayan
// yardımcı sınıf. Diğer sınıflar dosya formatını bilmek zorunda kalmaz.
public class DosyaIslemleri {
    private static final String URUN_DOSYASI = "urunler.txt";
    private static final String KULLANICI_DOSYASI = "kullanicilar.txt";
    private static final String GIRIS_KAYDI_DOSYASI = "giris_kaydi.txt";

    private DosyaIslemleri() {}

    // ---------- KULLANICI ----------

    public static void kullaniciEkle(String kullaniciSatiri) {
        try {
            File dosya = new File(KULLANICI_DOSYASI);
            boolean zatenVar = false;
            if (dosya.exists()) {
                try (Scanner scanner = new Scanner(dosya)) {
                    while (scanner.hasNextLine()) {
                        if (scanner.nextLine().equals(kullaniciSatiri)) {
                            zatenVar = true;
                            break;
                        }
                    }
                }
            }
            if (!zatenVar) {
                try (FileWriter writer = new FileWriter(KULLANICI_DOSYASI, true)) {
                    writer.write(kullaniciSatiri + "\n");
                }
            }
        } catch (IOException e) {
            System.out.println("Kullanıcı yazma hatası: " + e.getMessage());
        }
    }

    public static void kullaniciGuncelle(Musteri musteri) {
        File orjinal = new File(KULLANICI_DOSYASI);
        File gecici = new File("gecici_kullanici.txt");
        try (Scanner okuyucu = new Scanner(orjinal);
             PrintWriter yazici = new PrintWriter(new FileWriter(gecici))) {
            while (okuyucu.hasNextLine()) {
                String satir = okuyucu.nextLine();
                if (satir.startsWith(musteri.getKullaniciAdi() + ",")) {
                    yazici.println(musteri.kullaniciSatiri());
                } else {
                    yazici.println(satir);
                }
            }
        } catch (IOException e) {
            System.out.println("Müşteri güncelleme hatası: " + e.getMessage());
        }
        orjinal.delete();
        gecici.renameTo(orjinal);
    }

    public static double kullaniciBakiyesiGetir(String kullaniciAdi) {
        try (Scanner scanner = new Scanner(new File(KULLANICI_DOSYASI))) {
            while (scanner.hasNextLine()) {
                String[] parca = scanner.nextLine().split(",");
                if (parca.length >= 4 && parca[0].equals(kullaniciAdi)) {
                    return Double.parseDouble(parca[3]);
                }
            }
        } catch (Exception e) {
            System.out.println("Bakiye okunamadı: " + e.getMessage());
        }
        return 0.0;
    }

    public static void bakiyeEkle(String kullaniciAdi, double miktar) {
        File orjinal = new File(KULLANICI_DOSYASI);
        File gecici = new File("gecici_kullanicilar.txt");

        try (Scanner okuyucu = new Scanner(orjinal);
             PrintWriter yazici = new PrintWriter(new FileWriter(gecici))) {
            while (okuyucu.hasNextLine()) {
                String satir = okuyucu.nextLine();
                String[] parcalar = satir.split(",");
                if (parcalar.length >= 4 && parcalar[0].equals(kullaniciAdi)) {
                    double yeniBakiye = Double.parseDouble(parcalar[3]) + miktar;
                    parcalar[3] = String.valueOf(yeniBakiye);
                    satir = String.join(",", parcalar);
                }
                yazici.println(satir);
            }
        } catch (IOException e) {
            System.out.println("Bakiye ekleme hatası: " + e.getMessage());
        }

        orjinal.delete();
        gecici.renameTo(orjinal);
    }

    public static boolean kullaniciVarMi(String kullaniciAdi, String sifre) {
        try (Scanner okuyucu = new Scanner(new File(KULLANICI_DOSYASI))) {
            while (okuyucu.hasNextLine()) {
                String[] parcalar = okuyucu.nextLine().split(",");
                if (parcalar.length >= 2 && parcalar[0].equals(kullaniciAdi) && parcalar[1].equals(sifre)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("Kullanıcı kontrol hatası: " + e.getMessage());
        }
        return false;
    }

    public static boolean kullaniciZatenVar(String kullaniciAdi) {
        try (Scanner sc = new Scanner(new File(KULLANICI_DOSYASI))) {
            while (sc.hasNextLine()) {
                String[] parcalar = sc.nextLine().split(",");
                if (parcalar.length >= 1 && parcalar[0].equals(kullaniciAdi)) {
                    return true;
                }
            }
        } catch (IOException e) {
            // Dosya yoksa kullanıcı da yok demektir.
        }
        return false;
    }

    // ---------- ÜRÜN ----------

    public static void urunEkle(String urunSatiri) {
        try (FileWriter fw = new FileWriter(URUN_DOSYASI, true)) {
            fw.write(urunSatiri + "\n");
        } catch (IOException e) {
            System.out.println("Ürün yazma hatası: " + e.getMessage());
        }
    }

    public static void urunSil(String urunAdi) {
        File orjinal = new File(URUN_DOSYASI);
        File gecici = new File("gecici.txt");
        try (Scanner okuyucu = new Scanner(orjinal);
             PrintWriter yazici = new PrintWriter(new FileWriter(gecici))) {
            while (okuyucu.hasNextLine()) {
                String satir = okuyucu.nextLine();
                if (!satir.contains("," + urunAdi + ",")) {
                    yazici.println(satir);
                }
            }
        } catch (IOException e) {
            System.out.println("Silme hatası: " + e.getMessage());
        }
        orjinal.delete();
        gecici.renameTo(orjinal);
    }

    public static void stokGuncelle(String urunAdi, int yeniStok) {
        try {
            File dosya = new File(URUN_DOSYASI);
            List<String> satirlar = new ArrayList<>();
            try (Scanner scanner = new Scanner(dosya)) {
                while (scanner.hasNextLine()) {
                    String satir = scanner.nextLine();
                    String[] parcalar = satir.split(",");
                    // Format: tur,ad,fiyat,stok,beden,renk,resim
                    if (parcalar.length >= 7 && parcalar[1].equalsIgnoreCase(urunAdi)) {
                        parcalar[3] = String.valueOf(yeniStok);
                        satirlar.add(String.join(",", parcalar));
                    } else {
                        satirlar.add(satir);
                    }
                }
            }
            try (FileWriter writer = new FileWriter(dosya)) {
                for (String s : satirlar) {
                    writer.write(s + "\n");
                }
            }
        } catch (IOException e) {
            System.out.println("Stok güncelleme hatası: " + e.getMessage());
        }
    }

    public static void urunGuncelle(String eskiAd, String yeniAd, double yeniFiyat, String yeniBeden) {
        try {
            File dosya = new File(URUN_DOSYASI);
            List<String> satirlar = new ArrayList<>();
            try (Scanner scanner = new Scanner(dosya)) {
                while (scanner.hasNextLine()) {
                    String satir = scanner.nextLine();
                    String[] parcalar = satir.split(",");
                    // Format: tur,ad,fiyat,stok,beden,renk,resim
                    if (parcalar.length >= 7 && parcalar[1].equalsIgnoreCase(eskiAd)) {
                        parcalar[1] = yeniAd;
                        parcalar[2] = String.valueOf(yeniFiyat);
                        parcalar[4] = yeniBeden;
                        satirlar.add(String.join(",", parcalar));
                    } else {
                        satirlar.add(satir);
                    }
                }
            }
            try (FileWriter writer = new FileWriter(dosya)) {
                for (String s : satirlar) {
                    writer.write(s + "\n");
                }
            }
        } catch (IOException e) {
            System.out.println("Ürün güncelleme hatası: " + e.getMessage());
        }
    }

    public static List<String> urunleriGetir() {
        List<String> urunler = new ArrayList<>();
        try (Scanner okuyucu = new Scanner(new File(URUN_DOSYASI))) {
            while (okuyucu.hasNextLine()) {
                urunler.add(okuyucu.nextLine());
            }
        } catch (IOException e) {
            System.out.println("Ürün listeleme hatası: " + e.getMessage());
        }
        return urunler;
    }

    // ---------- BAKİYE TALEPLERİ ----------

    public static void bakiyeTalepEkle(String kullaniciAdi, double miktar) {
        try {
            File dosya = new File(KULLANICI_DOSYASI);
            List<String> yeniSatirlar = new ArrayList<>();
            try (Scanner scanner = new Scanner(dosya)) {
                while (scanner.hasNextLine()) {
                    String satir = scanner.nextLine();
                    String[] parcalar = satir.split(",");
                    if (parcalar.length >= 1 && parcalar[0].equals(kullaniciAdi) && !satir.contains("TALEP:")) {
                        satir += ",TALEP:" + miktar;
                    }
                    yeniSatirlar.add(satir);
                }
            }
            try (FileWriter writer = new FileWriter(dosya, false)) {
                for (String yeni : yeniSatirlar) {
                    writer.write(yeni + "\n");
                }
            }
        } catch (IOException e) {
            System.out.println("Bakiye talep hatası: " + e.getMessage());
        }
    }

    public static List<String> bakiyeTalepleriniGetir() {
        List<String> talepler = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File(KULLANICI_DOSYASI))) {
            while (scanner.hasNextLine()) {
                String satir = scanner.nextLine();
                if (satir.contains("TALEP:")) {
                    String[] parcalar = satir.split(",");
                    String kullanici = parcalar[0];
                    String talep = parcalar[parcalar.length - 1].replace("TALEP:", "");
                    talepler.add(kullanici + " - " + talep);
                }
            }
        } catch (IOException e) {
            System.out.println("Talep listeleme hatası: " + e.getMessage());
        }
        return talepler;
    }

    public static void bakiyeTalebiniOnayla(String kullaniciAdi, double miktar) {
        try {
            File dosya = new File(KULLANICI_DOSYASI);
            List<String> yeniSatirlar = new ArrayList<>();
            boolean talepIslendi = false;

            try (Scanner scanner = new Scanner(dosya)) {
                while (scanner.hasNextLine()) {
                    String satir = scanner.nextLine();
                    String[] parcalar = satir.split(",");

                    if (parcalar.length >= 4 && parcalar[0].equals(kullaniciAdi)
                            && satir.contains("TALEP:") && !talepIslendi) {
                        double yeniBakiye = Double.parseDouble(parcalar[3]) + miktar;
                        parcalar[3] = String.valueOf(yeniBakiye);

                        List<String> parcaList = new ArrayList<>(Arrays.asList(parcalar));
                        parcaList.removeIf(p -> p.startsWith("TALEP:"));
                        satir = String.join(",", parcaList);
                        talepIslendi = true;
                    }
                    yeniSatirlar.add(satir);
                }
            }

            try (FileWriter writer = new FileWriter(dosya, false)) {
                for (String s : yeniSatirlar) {
                    writer.write(s + "\n");
                }
            }
        } catch (IOException e) {
            System.out.println("Talep onaylama hatası: " + e.getMessage());
        }
    }

    // ---------- GİRİŞ KAYDI ----------

    public static String girisKaydiDosyasi() {
        return GIRIS_KAYDI_DOSYASI;
    }
}
