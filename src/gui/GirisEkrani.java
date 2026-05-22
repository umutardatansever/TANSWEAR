package gui;

import model.Admin;
import model.Musteri;
import patterns.singleton.Magaza;
import util.DosyaIslemleri;
import util.KullaniciIslemleri;

import javax.swing.*;
import java.awt.*;

// Uygulamanın açılış ekranı: kullanıcı kayıt ve giriş işlemlerini yönetir.
public class GirisEkrani extends JFrame {

    private final JTextField txtKullaniciAdi = new JTextField(15);
    private final JPasswordField txtSifre = new JPasswordField(15);
    private final Magaza magaza;

    public GirisEkrani(Magaza magaza) {
        this.magaza = magaza;

        setTitle("TANSWEAR Giriş Paneli");
        setSize(500, 380);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(UIThemes.BG_DARK);

        add(formuOlustur());
        setVisible(true);
    }

    private JPanel formuOlustur() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UIThemes.BG_DARK);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblBaslik = new JLabel("TANSWEAR", SwingConstants.CENTER);
        lblBaslik.setFont(new Font("SansSerif", Font.BOLD, 32));
        lblBaslik.setForeground(new Color(220, 220, 220));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(lblBaslik, gbc);

        JLabel lblAltBaslik = new JLabel("Modaya Yön Veren Mağaza", SwingConstants.CENTER);
        lblAltBaslik.setFont(UIThemes.BODY_FONT);
        lblAltBaslik.setForeground(UIThemes.FG_MUTED);
        gbc.gridy = 1;
        panel.add(lblAltBaslik, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(etiket("Kullanıcı adı:"), gbc);
        styleInput(txtKullaniciAdi);
        gbc.gridx = 1; panel.add(txtKullaniciAdi, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(etiket("Şifre:"), gbc);
        styleInput(txtSifre);
        gbc.gridx = 1; panel.add(txtSifre, gbc);

        JButton btnGiris = new ModernButton("Giriş Yap", UIThemes.BG_PRIMARY, UIThemes.FG_TEXT);
        JButton btnKaydol = new ModernButton("Kaydol", UIThemes.BG_BUTTON, UIThemes.FG_TEXT);

        btnGiris.addActionListener(e -> girisYap());
        btnKaydol.addActionListener(e -> kaydol());

        JPanel butonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        butonPanel.setBackground(UIThemes.BG_DARK);
        butonPanel.add(btnGiris);
        butonPanel.add(btnKaydol);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        panel.add(butonPanel, gbc);

        return panel;
    }

    private JLabel etiket(String s) {
        JLabel l = new JLabel(s);
        l.setForeground(UIThemes.FG_MUTED);
        l.setFont(UIThemes.BODY_FONT);
        return l;
    }

    private void styleInput(JTextField f) {
        f.setFont(UIThemes.BODY_FONT);
        f.setBackground(UIThemes.BG_INPUT);
        f.setForeground(UIThemes.FG_TEXT);
        f.setCaretColor(UIThemes.FG_TEXT);
        f.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    private void kaydol() {
        String kullaniciAdi = txtKullaniciAdi.getText().trim();
        String sifre = new String(txtSifre.getPassword());

        if (kullaniciAdi.length() > 15 || sifre.length() > 15) {
            uyari("Kullanıcı adı ve şifre en fazla 15 karakter olmalıdır.");
            return;
        }
        if (kullaniciAdi.isBlank() || sifre.isBlank()) {
            uyari("Kullanıcı adı ve şifre boş olamaz.");
            return;
        }
        if (DosyaIslemleri.kullaniciZatenVar(kullaniciAdi)) {
            hata("Bu kullanıcı adı zaten kayıtlı!");
            return;
        }

        KullaniciIslemleri.kullaniciEkleTekil(kullaniciAdi, sifre, "Musteri", 0.0);
        bilgi("Kayıt başarılı! Artık giriş yapabilirsiniz.");
    }

    private void girisYap() {
        String kullaniciAdi = txtKullaniciAdi.getText().trim();
        String sifre = new String(txtSifre.getPassword());

        if (kullaniciAdi.length() > 15 || sifre.length() > 15) {
            uyari("Kullanıcı adı ve şifre en fazla 15 karakter olmalıdır.");
            return;
        }
        if (kullaniciAdi.isBlank() || sifre.isBlank()) {
            uyari("Lütfen kullanıcı adı ve şifre giriniz.");
            return;
        }

        if (kullaniciAdi.equals("TANSWEAR") && sifre.equals("admin")) {
            new Admin(kullaniciAdi, sifre, magaza);
            new AdminPaneliGUI(magaza);
            dispose();
        } else if (DosyaIslemleri.kullaniciVarMi(kullaniciAdi, sifre)) {
            KullaniciIslemleri.girisKaydet(kullaniciAdi, sifre);
            double bakiye = DosyaIslemleri.kullaniciBakiyesiGetir(kullaniciAdi);
            Musteri musteri = new Musteri(kullaniciAdi, sifre, magaza, bakiye);
            new MusteriPaneliGUI(magaza, musteri);
            dispose();
        } else {
            hata("Kullanıcı bulunamadı. Lütfen önce kaydolun.");
        }
    }

    private void uyari(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Uyarı", JOptionPane.WARNING_MESSAGE);
    }
    private void hata(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Hata", JOptionPane.ERROR_MESSAGE);
    }
    private void bilgi(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Başarılı", JOptionPane.INFORMATION_MESSAGE);
    }
}
