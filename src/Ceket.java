public class Ceket extends Kiyafet {

    public Ceket(String ad, double fiyat, int stok, String beden, String renk, String resimYolu) {
        super(ad, fiyat, stok, beden, renk, resimYolu);
    }

    @Override
    public void bilgiGoster() {
        System.out.println("Ceket: " + getAd() + " | Fiyat: " + getFiyat()
                + " TL | Stok: " + getStok() + " | Beden: " + beden + " | Renk: " + renk);
    }
}
