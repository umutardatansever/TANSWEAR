// Beden, renk ve resim yolu gibi giyim ürünlerine özgü alanları tanımlayan ara soyut sınıf.
public abstract class Kiyafet extends Urun {
    protected String beden;
    protected String renk;
    protected String resimYolu;

    public Kiyafet(String ad, double fiyat, int stok, String beden, String renk, String resimYolu) {
        super(ad, fiyat, stok);
        this.beden = beden;
        this.renk = renk;
        this.resimYolu = resimYolu;
    }

    public String getBeden() { return beden; }
    public void setBeden(String beden) { this.beden = beden; }

    public String getRenk() { return renk; }
    public void setRenk(String renk) { this.renk = renk; }

    public String getResimYolu() { return resimYolu; }
    public void setResimYolu(String resimYolu) { this.resimYolu = resimYolu; }
}
