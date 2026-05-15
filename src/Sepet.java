import java.util.ArrayList;

// Müşterinin sepetini temsil eder. Üç tasarım kalıbının kesişim noktasıdır:
//  • STRATEGY: Hangi indirim algoritmasının uygulanacağı IndirimStratejisi ile
//    çalışma zamanında değiştirilebilir (kupon koduna göre).
//  • DECORATOR: Sepet 'Fiyatlanabilir' arayüzünü uyguladığı için HediyePaketi/HızlıKargo
//    dekoratörleriyle sarmalanıp tutarı/açıklaması zenginleştirilebilir.
//  • SINGLETON kullanımı: satinAl() içinden Magaza.getInstance() ile mağaza tekiline erişir.
public class Sepet implements SatinAlabilir, Fiyatlanabilir {

    private final ArrayList<Urun> urunler = new ArrayList<>();
    private IndirimStratejisi indirimStratejisi = new IndirimYok();

    public void setIndirimStratejisi(IndirimStratejisi strateji) {
        this.indirimStratejisi = strateji;
    }

    public IndirimStratejisi getIndirimStratejisi() {
        return indirimStratejisi;
    }

    public void urunEkle(Urun urun) {
        urunler.add(urun);
    }

    public void urunCikar(String urunAdi) {
        for (int i = 0; i < urunler.size(); i++) {
            if (urunler.get(i).getAd().equalsIgnoreCase(urunAdi)) {
                urunler.remove(i);
                return;
            }
        }
    }

    public void sepetiBosalt() {
        urunler.clear();
    }

    public double toplamTutar() {
        double toplam = 0;
        for (Urun u : urunler) {
            toplam += u.getFiyat();
        }
        // STRATEGY: aktif stratejiye göre indirimli tutarı döndürür.
        return indirimStratejisi.indirimHesapla(toplam);
    }

    @Override
    public double getTutar() {
        return toplamTutar();
    }

    @Override
    public String aciklamaGetir() {
        return "Sepet Tutarı";
    }

    public int toplamUrunSayisi() {
        return urunler.size();
    }

    public String sepetGoruntule() {
        if (urunler.isEmpty()) {
            return "Sepetiniz boş.";
        }
        StringBuilder sb = new StringBuilder();
        for (Urun u : urunler) {
            sb.append("• ").append(u.getAd()).append(" — ").append(u.getFiyat()).append(" TL\n");
        }
        sb.append("\nToplam (indirim sonrası): ").append(String.format("%.2f", toplamTutar())).append(" TL");
        return sb.toString();
    }

    public ArrayList<Urun> getUrunler() {
        return urunler;
    }

    @Override
    public boolean satinAl() {
        if (urunler.isEmpty()) return false;

        for (Urun u : urunler) {
            int yeniStok = u.getStok() - 1;
            Magaza.getInstance().stokGuncelle(u.getAd(), yeniStok);
        }
        sepetiBosalt();
        return true;
    }
}
