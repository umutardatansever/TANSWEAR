// ABSTRACT FACTORY - Concrete Factory:
// Kış koleksiyonu için uyumlu üst-alt giyim üretir (üstte Ceket, altta kalın Pantolon).
public class KislikKoleksiyonFabrikasi implements KoleksiyonFabrikasi {

    @Override
    public Urun ustGiyimUret(String ad, double fiyat, int stok, String beden, String renk, String resimYolu) {
        return new Ceket(ad, fiyat, stok, beden, renk, resimYolu);
    }

    @Override
    public Urun altGiyimUret(String ad, double fiyat, int stok, String beden, String renk, String resimYolu) {
        return new Pantolon(ad, fiyat, stok, beden, renk, resimYolu);
    }

    @Override
    public String koleksiyonAdi() {
        return "Kışlık Koleksiyon";
    }
}
