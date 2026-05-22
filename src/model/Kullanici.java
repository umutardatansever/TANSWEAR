package model;

// Kullanıcı tipleri (Admin, Müşteri) için ortak temel sınıf.
public abstract class Kullanici {
    protected String kullaniciAdi;
    protected String sifre;

    public Kullanici(String kullaniciAdi, String sifre) {
        this.kullaniciAdi = kullaniciAdi;
        this.sifre = sifre;
    }

    public String getKullaniciAdi() { return kullaniciAdi; }
    public String getSifre() { return sifre; }

    public String kullaniciSatiri() {
        return kullaniciAdi + "," + sifre + "," + this.getClass().getSimpleName();
    }

    public abstract void menu();
}
