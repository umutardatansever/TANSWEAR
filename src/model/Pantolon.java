package model;

public class Pantolon extends Kiyafet {

    public Pantolon(String ad, double fiyat, int stok, String beden, String renk, String resimYolu) {
        super(ad, fiyat, stok, beden, renk, resimYolu);
    }

    @Override
    public void bilgiGoster() {
        System.out.println("Pantolon: " + getAd() + " | Fiyat: " + getFiyat()
                + " TL | Stok: " + getStok() + " | Beden: " + beden + " | Renk: " + renk);
    }
}
