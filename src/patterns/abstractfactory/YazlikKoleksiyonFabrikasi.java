package patterns.abstractfactory;

import model.Gomlek;
import model.Pantolon;
import model.Urun;

// ABSTRACT FACTORY - Concrete Factory:
// Yaz koleksiyonu için uyumlu üst-alt giyim üretir (üstte Gömlek, altta ince Pantolon).
public class YazlikKoleksiyonFabrikasi implements KoleksiyonFabrikasi {

    @Override
    public Urun ustGiyimUret(String ad, double fiyat, int stok, String beden, String renk, String resimYolu) {
        return new Gomlek(ad, fiyat, stok, beden, renk, resimYolu);
    }

    @Override
    public Urun altGiyimUret(String ad, double fiyat, int stok, String beden, String renk, String resimYolu) {
        return new Pantolon(ad, fiyat, stok, beden, renk, resimYolu);
    }

    @Override
    public String koleksiyonAdi() {
        return "Yazlık Koleksiyon";
    }
}
