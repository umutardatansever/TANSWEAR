public class Gomlek extends Kiyafet {

    public Gomlek(String ad, double fiyat, int stok, String beden, String renk, String resimYolu) {
        super(ad, fiyat, stok, beden, renk, resimYolu);
    }

    @Override
    public void bilgiGoster() {
        System.out.println("Gömlek: " + getAd() + " | Fiyat: " + getFiyat()
                + " TL | Stok: " + getStok() + " | Beden: " + beden + " | Renk: " + renk);
    }
}
