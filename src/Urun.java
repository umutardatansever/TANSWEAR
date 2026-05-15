// Tüm ürünlerin ortak özelliklerini barındıran soyut sınıf.
// SatinAlabilir arayüzünü uygulayarak her ürünün satın alınabilir olmasını garantiler.
public abstract class Urun implements SatinAlabilir {
    private String ad;
    private double fiyat;
    private int stok;

    public Urun(String ad, double fiyat, int stok) {
        this.ad = ad;
        this.fiyat = fiyat;
        this.stok = stok;
    }

    public String getAd() { return ad; }
    public void setAd(String ad) { this.ad = ad; }

    public double getFiyat() { return fiyat; }
    public void setFiyat(double fiyat) { this.fiyat = fiyat; }

    public int getStok() { return stok; }
    public void setStok(int stok) { this.stok = stok; }

    public void stokAzalt(int miktar) {
        if (stok >= miktar) {
            stok -= miktar;
        }
    }

    public String getTur() {
        return this.getClass().getSimpleName();
    }

    @Override
    public boolean satinAl() {
        if (stok > 0) {
            stok--;
            return true;
        }
        return false;
    }

    public abstract void bilgiGoster();
}
