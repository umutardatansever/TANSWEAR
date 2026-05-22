package model;

import gui.AdminPaneliGUI;
import patterns.singleton.Magaza;
import util.DosyaIslemleri;

// Yönetici (admin) kullanıcısı. Kayıt edildiğinde dosyaya da yazılır.
public class Admin extends Kullanici {
    private Magaza magaza;

    public Admin(String kullaniciAdi, String sifre, Magaza magaza) {
        super(kullaniciAdi, sifre);
        this.magaza = magaza;
        DosyaIslemleri.kullaniciEkle(this.kullaniciSatiri());
    }

    public Magaza getMagaza() {
        return magaza;
    }

    @Override
    public void menu() {
        // GUI tabanlı uygulamada konsol menüsüne gerek yok; AdminPaneliGUI bu rolü üstleniyor.
    }
}
