package model;

import gui.MusteriPaneliGUI;
import patterns.singleton.Magaza;
import util.DosyaIslemleri;

// Mağaza müşterisi: bakiye taşır, alışveriş yapar.
public class Musteri extends Kullanici {
    private Magaza magaza;
    private double bakiye;

    public Musteri(String kullaniciAdi, String sifre, Magaza magaza, double bakiye) {
        super(kullaniciAdi, sifre);
        this.magaza = magaza;
        this.bakiye = bakiye;
        DosyaIslemleri.kullaniciEkle(this.kullaniciSatiri());
    }

    public double getBakiye() { return bakiye; }

    public Magaza getMagaza() { return magaza; }

    public boolean paraCek(double miktar) {
        if (bakiye >= miktar) {
            bakiye -= miktar;
            DosyaIslemleri.kullaniciGuncelle(this);
            return true;
        }
        return false;
    }

    @Override
    public String kullaniciSatiri() {
        return getKullaniciAdi() + "," + getSifre() + "," + this.getClass().getSimpleName() + "," + bakiye;
    }

    @Override
    public void menu() {
        // GUI tabanlı uygulamada konsol menüsüne gerek yok; MusteriPaneliGUI bu rolü üstleniyor.
    }
}
