package gui;

import model.Admin;
import model.Ceket;
import model.Kiyafet;
import model.Pantolon;
import model.Urun;
import patterns.abstractfactory.KislikKoleksiyonFabrikasi;
import patterns.abstractfactory.KoleksiyonFabrikasi;
import patterns.abstractfactory.YazlikKoleksiyonFabrikasi;
import patterns.factorymethod.UrunFactory;
import patterns.observer.Observer;
import patterns.singleton.Magaza;
import util.DosyaIslemleri;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.List;

// Yöneticinin ürün ekleme/silme/güncelleme, stok yönetimi ve bakiye taleplerini onaylama
// işlemlerini yaptığı GUI. Magaza'nın OBSERVER'ıdır: değişiklikler GUI'ye otomatik yansır.
public class AdminPaneliGUI extends JFrame implements Observer {

    private final Magaza magaza;

    private final JComboBox<String> comboUrunler = new JComboBox<>();
    private final JComboBox<String> comboKategori = new JComboBox<>(new String[]{"Tümü", "Pantolon", "Gömlek", "Ceket"});
    private final JComboBox<String> comboTalepler = new JComboBox<>();
    private final JTextArea urunAlani = new JTextArea();
    private final JPanel panelUrunler = new JPanel(new GridLayout(0, 3, 10, 10));

    public AdminPaneliGUI(Magaza magaza) {
        this.magaza = magaza;
        this.magaza.gozlemciEkle(this);

        setTitle("Admin Paneli — TANSWEAR");
        setSize(1050, 870);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(UIThemes.BG_DARK);

        add(kuzeyPanel(), BorderLayout.NORTH);
        add(merkezPanel(), BorderLayout.CENTER);
        add(guneyPanel(), BorderLayout.SOUTH);

        paneliGuncelle();
        setVisible(true);
    }

    // ---------- LAYOUT ----------

    private JPanel kuzeyPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
        p.setBackground(UIThemes.BG_DARK);
        JLabel lbl = new JLabel("TANSWEAR — Admin Paneli");
        lbl.setForeground(UIThemes.FG_TEXT);
        lbl.setFont(UIThemes.TITLE_FONT);
        p.add(lbl);
        return p;
    }

    private JPanel merkezPanel() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBackground(UIThemes.BG_DARK);
        p.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        panelUrunler.setBackground(UIThemes.BG_DARK);

        JScrollPane scroll = new JScrollPane(panelUrunler);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.setPreferredSize(new Dimension(990, 320));
        p.add(scroll, BorderLayout.CENTER);

        urunAlani.setEditable(false);
        urunAlani.setBackground(UIThemes.BG_INPUT);
        urunAlani.setForeground(UIThemes.FG_TEXT);
        urunAlani.setFont(UIThemes.MONO_FONT);
        JScrollPane textScroll = new JScrollPane(urunAlani);
        textScroll.setPreferredSize(new Dimension(990, 140));
        p.add(textScroll, BorderLayout.SOUTH);

        return p;
    }

    private JPanel guneyPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(UIThemes.BG_DARK);
        p.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        // Satır 1: Ürün seçimi + temel CRUD
        JPanel satir1 = yeniKontrolSatiri();
        satir1.add(yeniEtiket("Kategori:"));
        satir1.add(comboKategori);
        comboKategori.addActionListener(e -> paneliGuncelle());

        comboUrunler.setPreferredSize(new Dimension(220, 30));
        satir1.add(yeniEtiket("Ürün:"));
        satir1.add(comboUrunler);

        JButton btnEkle = btn("Yeni Ürün", UIThemes.BG_BUTTON, UIThemes.FG_TEXT);
        JButton btnUrunGuncelle = btn("Güncelle", UIThemes.BG_BUTTON, UIThemes.FG_TEXT);
        JButton btnSil = btn("Sil", UIThemes.BG_BUTTON, UIThemes.FG_TEXT);
        JButton btnStokGuncelle = btn("Stok", UIThemes.BG_BUTTON, UIThemes.FG_TEXT);

        satir1.add(btnEkle);
        satir1.add(btnUrunGuncelle);
        satir1.add(btnSil);
        satir1.add(btnStokGuncelle);

        // Satır 2: Koleksiyon (Abstract Factory) + raporlar + bakiye
        JPanel satir2 = yeniKontrolSatiri();
        JButton btnKoleksiyon = btn("Hızlı Koleksiyon Ekle", UIThemes.BG_PRIMARY, UIThemes.FG_TEXT);
        JButton btnKritik = btn("Stoğu Azalan Ürünler", UIThemes.BG_BUTTON, UIThemes.FG_DANGER);
        JButton btnBakiyeYukle = btn("Bakiye Yükle", UIThemes.BG_BUTTON, UIThemes.FG_SUCCESS);
        JButton btnListele = btn("Listeyi Yenile", UIThemes.BG_BUTTON, UIThemes.FG_TEXT);

        satir2.add(btnKoleksiyon);
        satir2.add(btnKritik);
        satir2.add(btnBakiyeYukle);
        satir2.add(btnListele);

        // Satır 3: Bakiye talepleri + çıkış
        JPanel satir3 = yeniKontrolSatiri();
        comboTalepler.setPreferredSize(new Dimension(250, 30));
        talepleriYenile();
        JButton btnTalepOnayla = btn("Talebi Onayla", UIThemes.BG_BUTTON, UIThemes.FG_SUCCESS);
        JButton btnCikis = btn("Çıkış", UIThemes.BG_BUTTON, UIThemes.FG_DANGER);

        satir3.add(yeniEtiket("Bakiye Talepleri:"));
        satir3.add(comboTalepler);
        satir3.add(btnTalepOnayla);
        satir3.add(Box.createHorizontalStrut(50));
        satir3.add(btnCikis);

        p.add(satir1);
        p.add(satir2);
        p.add(satir3);

        // ---------- AKSİYONLAR ----------

        btnListele.addActionListener(e -> paneliGuncelle());
        btnEkle.addActionListener(e -> yeniUrunDialog());
        btnSil.addActionListener(e -> urunSil());
        btnUrunGuncelle.addActionListener(e -> urunGuncelleDialog());
        btnStokGuncelle.addActionListener(e -> stokGuncelleDialog());
        btnKritik.addActionListener(e -> kritikStoklariGoster());
        btnBakiyeYukle.addActionListener(e -> bakiyeYukleDialog());
        btnKoleksiyon.addActionListener(e -> koleksiyonEkleDialog());
        btnTalepOnayla.addActionListener(e -> talepOnayla());
        btnCikis.addActionListener(e -> {
            magaza.gozlemciCikar(this);
            new GirisEkrani(magaza);
            dispose();
        });

        return p;
    }

    private JPanel yeniKontrolSatiri() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        p.setBackground(UIThemes.BG_DARK);
        return p;
    }

    private JLabel yeniEtiket(String s) {
        JLabel l = new JLabel(s);
        l.setForeground(UIThemes.FG_TEXT);
        return l;
    }

    private JButton btn(String text, Color bg, Color fg) {
        return new ModernButton(text, bg, fg);
    }

    // ---------- AKSİYON METOTLARI ----------

    private void yeniUrunDialog() {
        JPanel panel = new JPanel(new GridLayout(7, 2, 5, 5));
        JComboBox<String> cbTur = new JComboBox<>(new String[]{"Pantolon", "Gömlek", "Ceket"});
        JTextField tfAd = new JTextField();
        JTextField tfFiyat = new JTextField();
        JTextField tfBeden = new JTextField();
        JTextField tfRenk = new JTextField();
        JTextField tfStok = new JTextField();
        JTextField tfResimYolu = new JTextField();

        panel.add(new JLabel("Tür:")); panel.add(cbTur);
        panel.add(new JLabel("Ad:")); panel.add(tfAd);
        panel.add(new JLabel("Fiyat:")); panel.add(tfFiyat);
        panel.add(new JLabel("Beden:")); panel.add(tfBeden);
        panel.add(new JLabel("Renk:")); panel.add(tfRenk);
        panel.add(new JLabel("Stok:")); panel.add(tfStok);
        panel.add(new JLabel("Resim Yolu:")); panel.add(tfResimYolu);

        int sonuc = JOptionPane.showConfirmDialog(this, panel, "Yeni Ürün Ekle", JOptionPane.OK_CANCEL_OPTION);
        if (sonuc != JOptionPane.OK_OPTION) return;

        try {
            String tur = (String) cbTur.getSelectedItem();
            String ad = tfAd.getText();
            double fiyat = Double.parseDouble(tfFiyat.getText());
            int stok = Integer.parseInt(tfStok.getText());
            String beden = tfBeden.getText();
            String renk = tfRenk.getText();
            String resimYolu = tfResimYolu.getText();

            // FACTORY METHOD ile ürün oluşturma
            Urun yeniUrun = UrunFactory.createUrun(tur, ad, fiyat, stok, beden, renk, resimYolu);
            magaza.urunEkle(yeniUrun);
            mesaj("Ürün eklendi: " + ad);
        } catch (NumberFormatException ex) {
            hata("Fiyat ve stok sayısal olmalı.");
        } catch (IllegalArgumentException ex) {
            hata(ex.getMessage());
        }
    }

    private void urunSil() {
        String secilen = (String) comboUrunler.getSelectedItem();
        if (secilen == null) { hata("Lütfen bir ürün seçin."); return; }
        magaza.urunSil(secilen);
        DosyaIslemleri.urunSil(secilen);
        mesaj("Ürün silindi: " + secilen);
    }

    private void urunGuncelleDialog() {
        String secilen = (String) comboUrunler.getSelectedItem();
        if (secilen == null) { hata("Lütfen bir ürün seçin."); return; }

        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        JTextField tfAd = new JTextField();
        JTextField tfFiyat = new JTextField();
        JTextField tfBeden = new JTextField();
        JTextField tfStok = new JTextField();
        panel.add(new JLabel("Yeni Ad:")); panel.add(tfAd);
        panel.add(new JLabel("Yeni Fiyat:")); panel.add(tfFiyat);
        panel.add(new JLabel("Yeni Beden:")); panel.add(tfBeden);
        panel.add(new JLabel("Yeni Stok:")); panel.add(tfStok);

        int sonuc = JOptionPane.showConfirmDialog(this, panel, "Ürünü Güncelle", JOptionPane.OK_CANCEL_OPTION);
        if (sonuc != JOptionPane.OK_OPTION) return;

        try {
            String yeniAd = tfAd.getText();
            double yeniFiyat = Double.parseDouble(tfFiyat.getText());
            String yeniBeden = tfBeden.getText();
            int yeniStok = Integer.parseInt(tfStok.getText());
            magaza.urunGuncelle(secilen, yeniAd, yeniFiyat, yeniBeden);
            DosyaIslemleri.urunGuncelle(secilen, yeniAd, yeniFiyat, yeniBeden);
            magaza.stokGuncelle(yeniAd, yeniStok);
            mesaj("Ürün güncellendi.");
        } catch (NumberFormatException ex) {
            hata("Fiyat ve stok sayısal olmalı.");
        }
    }

    private void stokGuncelleDialog() {
        String secilen = (String) comboUrunler.getSelectedItem();
        if (secilen == null) { hata("Lütfen bir ürün seçin."); return; }
        String input = JOptionPane.showInputDialog(this, "Yeni stok miktarını girin:");
        if (input == null) return;
        try {
            magaza.stokGuncelle(secilen, Integer.parseInt(input));
            mesaj("Stok güncellendi.");
        } catch (NumberFormatException ex) {
            hata("Geçerli bir sayı girin.");
        }
    }

    private void kritikStoklariGoster() {
        StringBuilder kritik = new StringBuilder("Kritik stok seviyesindeki ürünler:\n\n");
        boolean var = false;
        for (Urun u : magaza.getUrunListesi()) {
            if (u.getStok() < 3) {
                kritik.append("• ").append(u.getAd()).append(" — Stok: ").append(u.getStok()).append("\n");
                var = true;
            }
        }
        mesaj(var ? kritik.toString() : "Tüm ürünlerin stoğu yeterli.");
    }

    private void bakiyeYukleDialog() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        JTextField tfKullanici = new JTextField();
        JTextField tfMiktar = new JTextField();
        panel.add(new JLabel("Kullanıcı Adı:")); panel.add(tfKullanici);
        panel.add(new JLabel("Miktar:")); panel.add(tfMiktar);
        int sonuc = JOptionPane.showConfirmDialog(this, panel, "Manuel Bakiye Yükle", JOptionPane.OK_CANCEL_OPTION);
        if (sonuc != JOptionPane.OK_OPTION) return;
        try {
            DosyaIslemleri.bakiyeEkle(tfKullanici.getText(), Double.parseDouble(tfMiktar.getText()));
            mesaj("Bakiye yüklendi.");
        } catch (NumberFormatException ex) {
            hata("Miktar sayısal olmalı.");
        }
    }

    // ABSTRACT FACTORY kullanım noktası: Yazlık ya da Kışlık koleksiyonu seçilir,
    // seçilen fabrika tek seferde uyumlu üst+alt giyim üretir.
    private void koleksiyonEkleDialog() {
        String[] secenekler = {"Yazlık Koleksiyon", "Kışlık Koleksiyon"};
        int secim = JOptionPane.showOptionDialog(this,
                "Hangi sezon koleksiyonunu eklemek istersiniz?\n(Üst giyim + alt giyim ürünleri otomatik üretilir)",
                "Hızlı Koleksiyon Ekle",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, secenekler, secenekler[0]);
        if (secim < 0) return;

        KoleksiyonFabrikasi fabrika = (secim == 0)
                ? new YazlikKoleksiyonFabrikasi()
                : new KislikKoleksiyonFabrikasi();

        JPanel panel = new JPanel(new GridLayout(8, 2, 5, 5));
        JTextField tfUstAd = new JTextField();
        JTextField tfUstFiyat = new JTextField();
        JTextField tfUstStok = new JTextField();
        JTextField tfUstResim = new JTextField();
        JTextField tfAltAd = new JTextField();
        JTextField tfAltFiyat = new JTextField();
        JTextField tfAltStok = new JTextField();
        JTextField tfAltResim = new JTextField();
        panel.add(new JLabel("Üst Giyim Adı:"));   panel.add(tfUstAd);
        panel.add(new JLabel("Üst Fiyat:"));        panel.add(tfUstFiyat);
        panel.add(new JLabel("Üst Stok:"));         panel.add(tfUstStok);
        panel.add(new JLabel("Üst Resim:"));        panel.add(tfUstResim);
        panel.add(new JLabel("Alt Giyim Adı:"));   panel.add(tfAltAd);
        panel.add(new JLabel("Alt Fiyat:"));        panel.add(tfAltFiyat);
        panel.add(new JLabel("Alt Stok:"));         panel.add(tfAltStok);
        panel.add(new JLabel("Alt Resim:"));        panel.add(tfAltResim);

        int onay = JOptionPane.showConfirmDialog(this, panel,
                fabrika.koleksiyonAdi() + " — Bilgileri Girin", JOptionPane.OK_CANCEL_OPTION);
        if (onay != JOptionPane.OK_OPTION) return;

        try {
            String varsayilanBeden = (secim == 0) ? "M" : "L";
            String varsayilanRenk = (secim == 0) ? "Beyaz" : "Lacivert";

            Urun ust = fabrika.ustGiyimUret(tfUstAd.getText(),
                    Double.parseDouble(tfUstFiyat.getText()),
                    Integer.parseInt(tfUstStok.getText()),
                    varsayilanBeden, varsayilanRenk, tfUstResim.getText());
            Urun alt = fabrika.altGiyimUret(tfAltAd.getText(),
                    Double.parseDouble(tfAltFiyat.getText()),
                    Integer.parseInt(tfAltStok.getText()),
                    varsayilanBeden, varsayilanRenk, tfAltResim.getText());

            magaza.urunEkle(ust);
            magaza.urunEkle(alt);
            mesaj(fabrika.koleksiyonAdi() + " eklendi: " + ust.getAd() + " + " + alt.getAd());
        } catch (NumberFormatException ex) {
            hata("Fiyat ve stok sayısal olmalı.");
        }
    }

    private void talepleriYenile() {
        comboTalepler.removeAllItems();
        List<String> talepler = DosyaIslemleri.bakiyeTalepleriniGetir();
        for (String t : talepler) comboTalepler.addItem(t);
    }

    private void talepOnayla() {
        String secilen = (String) comboTalepler.getSelectedItem();
        if (secilen == null) { hata("Onaylanacak talep yok."); return; }
        try {
            String[] parcalar = secilen.split(" - ");
            DosyaIslemleri.bakiyeTalebiniOnayla(parcalar[0], Double.parseDouble(parcalar[1]));
            mesaj("Bakiye onaylandı.");
            comboTalepler.removeItem(secilen);
        } catch (Exception ex) {
            hata("Talep işlenemedi.");
        }
    }

    // ---------- OBSERVER ----------

    @Override
    public void guncelle() {
        paneliGuncelle();
    }

    private void paneliGuncelle() {
        panelUrunler.removeAll();
        urunAlani.setText("");
        comboUrunler.removeAllItems();

        String filtre = (String) comboKategori.getSelectedItem();

        for (Urun urun : magaza.getUrunListesi()) {
            if (filtre != null && !filtre.equals("Tümü") && !urun.getTur().equals(filtre)) {
                continue;
            }
            if (urun instanceof Kiyafet k) {
                panelUrunler.add(urunKarti(k));
                urunAlani.append(String.format("%-20s | %7.2f TL | Stok: %d%n",
                        urun.getAd(), urun.getFiyat(), urun.getStok()));
                comboUrunler.addItem(urun.getAd());
            }
        }
        panelUrunler.revalidate();
        panelUrunler.repaint();
    }

    private JPanel urunKarti(Kiyafet k) {
        JPanel kart = new JPanel();
        kart.setLayout(new BoxLayout(kart, BoxLayout.Y_AXIS));
        kart.setBackground(UIThemes.BG_PANEL);
        kart.setBorder(BorderFactory.createLineBorder(UIThemes.FG_MUTED));

        ImageIcon icon = resimYukle(k.getResimYolu(), 120, 120);
        if (icon != null) {
            JLabel lblResim = new JLabel(icon);
            lblResim.setAlignmentX(Component.CENTER_ALIGNMENT);
            kart.add(lblResim);
        } else {
            JLabel lblResim = new JLabel("📷 Resim Yok");
            lblResim.setForeground(UIThemes.FG_MUTED);
            lblResim.setAlignmentX(Component.CENTER_ALIGNMENT);
            kart.add(lblResim);
        }

        for (String bilgi : new String[]{
                "Ad: " + k.getAd(),
                "Tür: " + k.getTur(),
                "Fiyat: " + k.getFiyat() + " TL",
                "Stok: " + k.getStok(),
                "Beden: " + k.getBeden(),
                "Renk: " + k.getRenk()
        }) {
            JLabel lbl = new JLabel(bilgi);
            lbl.setForeground(UIThemes.FG_TEXT);
            lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            kart.add(lbl);
        }
        return kart;
    }

    /**
     * Resmi classpath üzerinden yükler.
     * Eclipse, src/ altındaki tüm dosyaları (resimler dahil) bin/ a kopyalar.
     * resimYolu: "resimler/pantolon1.jpg" → getResource("/resimler/pantolon1.jpg")
     *
     * HATA: onceden "/resimler/" + resimYolu idi → cift klasör hatı yapiyordu.
     */
    private ImageIcon resimYukle(String resimYolu, int genislik, int yukseklik) {
        if (resimYolu == null || resimYolu.isBlank() || resimYolu.equalsIgnoreCase("yok")) return null;
        // "/" + "resimler/pantolon1.jpg" = "/resimler/pantolon1.jpg"  (tekli, dogru)
        URL url = getClass().getResource("/" + resimYolu);
        if (url != null) {
            Image img = new ImageIcon(url).getImage()
                    .getScaledInstance(genislik, yukseklik, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        }
        return null;
    }

    // ---------- DIALOG YARDIMCILARI ----------

    private void mesaj(String s) { JOptionPane.showMessageDialog(this, s); }
    private void hata(String s)  { JOptionPane.showMessageDialog(this, s, "Hata", JOptionPane.ERROR_MESSAGE); }
}
