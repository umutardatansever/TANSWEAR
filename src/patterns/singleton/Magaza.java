package patterns.singleton;

import gui.AdminPaneliGUI;
import gui.MusteriPaneliGUI;
import model.Kiyafet;
import model.Urun;
import patterns.factorymethod.UrunFactory;
import patterns.observer.Observer;
import patterns.observer.Subject;
import util.DosyaIslemleri;

import java.util.ArrayList;
import java.util.List;

// Mağazadaki ürünlerin ve gözlemcilerin (GUI panellerinin) tek noktadan yönetildiği sınıf.
//
// İçinde iki tasarım kalıbı var:
//  • SINGLETON: Sistemde tek bir Magaza nesnesi olur (getInstance ile erişilir).
//  • OBSERVER (Subject rolü): Mağazadaki ürün/stok değişimi olduğunda kayıtlı olan tüm
//    gözlemcilere (AdminPaneliGUI, MusteriPaneliGUI) otomatik haber verir.
public class Magaza implements Subject {

    private static Magaza instance;

    private final ArrayList<Urun> urunListesi = new ArrayList<>();
    private final ArrayList<Observer> gozlemciler = new ArrayList<>();

    // SINGLETON: Constructor private; dışarıdan 'new Magaza()' yapılamaz.
    private Magaza() {}

    // SINGLETON: Tek erişim noktası.
    public static Magaza getInstance() {
        if (instance == null) {
            instance = new Magaza();
        }
        return instance;
    }

    public void urunEkle(Urun urun) {
        urunListesi.add(urun);
        if (urun instanceof Kiyafet k) {
            String satir = urun.getTur() + "," + urun.getAd() + "," + urun.getFiyat() + "," + urun.getStok()
                    + "," + k.getBeden() + "," + k.getRenk() + "," + k.getResimYolu();
            DosyaIslemleri.urunEkle(satir);
        }
        gozlemcilereHaberVer();
    }

    public void urunSil(String ad) {
        Urun silinecek = urunBul(ad);
        if (silinecek != null) {
            urunListesi.remove(silinecek);
            gozlemcilereHaberVer();
        }
    }

    public void urunGuncelle(String eskiAd, String yeniAd, double yeniFiyat, String yeniBeden) {
        Urun urun = urunBul(eskiAd);
        if (urun != null) {
            urun.setAd(yeniAd);
            urun.setFiyat(yeniFiyat);
            if (urun instanceof Kiyafet k) {
                k.setBeden(yeniBeden);
            }
            gozlemcilereHaberVer();
        }
    }

    public void stokGuncelle(String ad, int yeniStok) {
        Urun urun = urunBul(ad);
        if (urun != null) {
            urun.setStok(yeniStok);
            DosyaIslemleri.stokGuncelle(ad, yeniStok);
            gozlemcilereHaberVer();
        }
    }

    public void urunSatinAl(String ad) {
        Urun urun = urunBul(ad);
        if (urun != null && urun.getStok() > 0) {
            urun.stokAzalt(1);
            DosyaIslemleri.stokGuncelle(ad, urun.getStok());
            gozlemcilereHaberVer();
        }
    }

    public Urun urunBul(String ad) {
        for (Urun u : urunListesi) {
            if (u.getAd().equalsIgnoreCase(ad)) {
                return u;
            }
        }
        return null;
    }

    public ArrayList<Urun> getUrunListesi() {
        return urunListesi;
    }

    // Programın açılışında dosyadaki ürünleri belleğe yükler. Ürün oluşturma sorumluluğunu
    // FACTORY METHOD'a (UrunFactory) devreder.
    public void dosyadanUrunEkle() {
        List<String> urunSatirlari = DosyaIslemleri.urunleriGetir();
        for (String satir : urunSatirlari) {
            String[] parca = satir.split(",", 7);
            if (parca.length < 7) continue;

            try {
                String tur = parca[0];
                String ad = parca[1];
                double fiyat = Double.parseDouble(parca[2]);
                int stok = Integer.parseInt(parca[3]);
                String beden = parca[4];
                String renk = parca[5];
                String resimYolu = parca[6];

                Urun urun = UrunFactory.createUrun(tur, ad, fiyat, stok, beden, renk, resimYolu);
                if (urun != null) {
                    urunListesi.add(urun);
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Ürün okuma hatası: " + e.getMessage());
            }
        }
    }

    // ---------- OBSERVER (Subject) ----------

    @Override
    public void gozlemciEkle(Observer o) {
        gozlemciler.add(o);
    }

    @Override
    public void gozlemciCikar(Observer o) {
        gozlemciler.remove(o);
    }

    @Override
    public void gozlemcilereHaberVer() {
        for (Observer o : gozlemciler) {
            o.guncelle();
        }
    }
}
