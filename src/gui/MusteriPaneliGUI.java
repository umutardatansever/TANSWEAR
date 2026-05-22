package gui;

import model.Admin;
import model.Kiyafet;
import model.Musteri;
import model.Sepet;
import model.Urun;
import patterns.adapter.BankaAdapter;
import patterns.adapter.OdemeSistemi;
import patterns.decorator.Fiyatlanabilir;
import patterns.decorator.HediyePaketiDecorator;
import patterns.decorator.HizliKargoDecorator;
import patterns.observer.Observer;
import patterns.singleton.Magaza;
import patterns.strategy.IndirimStratejisi;
import patterns.strategy.IndirimYok;
import patterns.strategy.YuzdeOnIndirim;
import patterns.strategy.YuzdeYirmiIndirim;
import util.DosyaIslemleri;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.net.URL;

// Müşterinin ürünleri listelediği, sepete ekleyip ödeme yaptığı ana ekran.
// Magaza'nın OBSERVER'ıdır; admin değişiklik yapınca liste otomatik güncellenir.
//
// Kullandığı tasarım kalıpları:
//  • OBSERVER (gözlemci olarak)
//  • STRATEGY (kupon koduna göre indirim)
//  • DECORATOR (hediye paketi / hızlı kargo)
//  • ADAPTER (kredi kartı ödemesi -> dış banka)
public class MusteriPaneliGUI extends JFrame implements Observer {

    private final Magaza magaza;
    private final Musteri musteri;
    private final Sepet sepet = new Sepet();

    private final JTextArea sepetAlani = new JTextArea();
    private final JPanel panelUrunler = new JPanel(new GridLayout(0, 3, 10, 10));
    private final JTextField txtArama = new JTextField(18);
    private final JLabel lblBakiye = new JLabel();

    public MusteriPaneliGUI(Magaza magaza, Musteri musteri) {
        this.magaza = magaza;
        this.musteri = musteri;
        this.magaza.gozlemciEkle(this);

        setTitle("TANSWEAR — Müşteri Paneli");
        setSize(1050, 850);
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
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBackground(UIThemes.BG_DARK);
        p.setBorder(BorderFactory.createEmptyBorder(15, 20, 5, 20));

        JLabel lblBaslik = new JLabel("TANSWEAR");
        lblBaslik.setForeground(UIThemes.FG_TEXT);
        lblBaslik.setFont(UIThemes.TITLE_FONT);
        p.add(lblBaslik, BorderLayout.WEST);

        JPanel sag = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        sag.setBackground(UIThemes.BG_DARK);

        JLabel lblHosgeldin = new JLabel("Hoş geldin, " + musteri.getKullaniciAdi() + " 👋");
        lblHosgeldin.setForeground(UIThemes.FG_MUTED);
        lblHosgeldin.setFont(UIThemes.BODY_FONT);
        sag.add(lblHosgeldin);

        bakiyeEtiketiniGuncelle();
        sag.add(lblBakiye);

        p.add(sag, BorderLayout.EAST);

        // Arama kutusu
        JPanel aramaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        aramaPanel.setBackground(UIThemes.BG_DARK);
        JLabel lblArama = new JLabel("Ürün Ara: ");
        lblArama.setForeground(UIThemes.FG_MUTED);
        aramaPanel.add(lblArama);
        txtArama.setBackground(UIThemes.BG_INPUT);
        txtArama.setForeground(UIThemes.FG_TEXT);
        txtArama.setCaretColor(UIThemes.FG_TEXT);
        txtArama.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
        txtArama.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { paneliGuncelle(); }
            public void removeUpdate(DocumentEvent e) { paneliGuncelle(); }
            public void changedUpdate(DocumentEvent e) { paneliGuncelle(); }
        });
        aramaPanel.add(txtArama);
        p.add(aramaPanel, BorderLayout.SOUTH);

        return p;
    }

    private JPanel merkezPanel() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBackground(UIThemes.BG_DARK);
        p.setBorder(BorderFactory.createEmptyBorder(5, 20, 10, 20));

        panelUrunler.setBackground(UIThemes.BG_DARK);
        JScrollPane scroll = new JScrollPane(panelUrunler);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.setPreferredSize(new Dimension(820, 320));
        p.add(scroll, BorderLayout.CENTER);

        sepetAlani.setEditable(false);
        sepetAlani.setBackground(UIThemes.BG_INPUT);
        sepetAlani.setForeground(UIThemes.FG_SUCCESS);
        sepetAlani.setFont(UIThemes.MONO_FONT);
        JScrollPane sepetPane = new JScrollPane(sepetAlani);
        sepetPane.setPreferredSize(new Dimension(820, 160));
        p.add(sepetPane, BorderLayout.SOUTH);

        return p;
    }

    private JPanel guneyPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(UIThemes.BG_DARK);
        p.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        JPanel satir1 = yeniSatir();
        JButton btnSepetiGor = btn("Sepeti Görüntüle", UIThemes.BG_BUTTON, UIThemes.FG_TEXT);
        JButton btnSatinAl = btn("Sepeti Satın Al", UIThemes.BG_PRIMARY, UIThemes.FG_TEXT);
        JButton btnCikar = btn("Sepetten Çıkar", UIThemes.BG_BUTTON, UIThemes.FG_TEXT);
        satir1.add(btnSepetiGor); satir1.add(btnSatinAl); satir1.add(btnCikar);

        JPanel satir2 = yeniSatir();
        JButton btnBakiye = btn("Bakiye Göster", UIThemes.BG_BUTTON, UIThemes.FG_TEXT);
        JButton btnPara = btn("Para Yatır", UIThemes.BG_BUTTON, UIThemes.FG_SUCCESS);
        JButton btnCikis = btn("Çıkış", UIThemes.BG_BUTTON, UIThemes.FG_DANGER);
        satir2.add(btnBakiye); satir2.add(btnPara); satir2.add(btnCikis);

        p.add(satir1); p.add(satir2);

        btnSepetiGor.addActionListener(e -> sepetAlani.setText(sepet.sepetGoruntule()));
        btnSatinAl.addActionListener(e -> satinAlmaAkisi());
        btnCikar.addActionListener(e -> sepetenCikarDialog());
        btnBakiye.addActionListener(e -> mesaj("Bakiyeniz: " + String.format("%.2f", musteri.getBakiye()) + " TL"));
        btnPara.addActionListener(e -> paraYatirDialog());
        btnCikis.addActionListener(e -> {
            magaza.gozlemciCikar(this);
            new GirisEkrani(magaza);
            dispose();
        });

        return p;
    }

    private JPanel yeniSatir() {
        JPanel s = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 8));
        s.setBackground(UIThemes.BG_DARK);
        return s;
    }

    private JButton btn(String text, Color bg, Color fg) {
        return new ModernButton(text, bg, fg);
    }

    private void bakiyeEtiketiniGuncelle() {
        lblBakiye.setText("Bakiye: " + String.format("%.2f", musteri.getBakiye()) + " TL");
        lblBakiye.setForeground(UIThemes.FG_SUCCESS);
        lblBakiye.setFont(UIThemes.HEADING_FONT);
    }

    // ---------- AKSİYONLAR ----------

    private void satinAlmaAkisi() {
        if (sepet.toplamUrunSayisi() == 0) { mesaj("Sepetiniz boş."); return; }

        // STRATEGY: Kupon kodunu indirim stratejisine çevir
        String kupon = JOptionPane.showInputDialog(this,
                "Varsa indirim kuponunuzu giriniz (TANSWEAR10 / WELCOME20):",
                "Kupon Kodu", JOptionPane.QUESTION_MESSAGE);
        IndirimStratejisi strateji = stratejiSec(kupon);
        sepet.setIndirimStratejisi(strateji);
        if (!(strateji instanceof IndirimYok)) {
            mesaj("İndirim uygulandı.");
        } else if (kupon != null && !kupon.isBlank()) {
            mesaj("Geçersiz kupon kodu, indirim uygulanmadı.");
        }

        // DECORATOR: Ekstra servisleri sepetin üzerine sar
        Fiyatlanabilir sonSepet = ekstraServisleriUygula(sepet);
        if (sonSepet == null) return; // iptal

        double toplamTutar = sonSepet.getTutar();

        // Ödeme yöntemi: bakiye veya ADAPTER ile dış banka
        String[] yontemler = {"Cüzdan Bakiyesi", "Kredi Kartı"};
        int yontem = JOptionPane.showOptionDialog(this,
                sonSepet.aciklamaGetir() + "\nÖdenecek Tutar: "
                        + String.format("%.2f", toplamTutar) + " TL\nÖdeme Yöntemi Seçin:",
                "Ödeme İşlemi",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                null, yontemler, yontemler[0]);
        if (yontem < 0) return;

        boolean basarili = false;
        if (yontem == 0) {
            if (musteri.getBakiye() >= toplamTutar) {
                musteri.paraCek(toplamTutar);
                basarili = true;
            } else {
                hata("Bakiyeniz yetersiz.");
            }
        } else {
            OdemeSistemi adapter = new BankaAdapter();
            basarili = adapter.odemeYap(toplamTutar, musteri.getKullaniciAdi());
            if (basarili) mesaj("Kredi kartı ile dış bankadan ödeme alındı.");
        }

        if (basarili && sepet.satinAl()) {
            mesaj("Satın alma tamamlandı!\n"
                    + sonSepet.aciklamaGetir() + "\n"
                    + "Ödenen: " + String.format("%.2f", toplamTutar) + " TL\n"
                    + "Kalan Bakiye: " + String.format("%.2f", musteri.getBakiye()) + " TL");
            sepetAlani.setText("");
            bakiyeEtiketiniGuncelle();
        } else if (basarili) {
            hata("Stokta sorun çıktı, satın alma başarısız.");
        }
    }

    private IndirimStratejisi stratejiSec(String kupon) {
        if (kupon == null) return new IndirimYok();
        return switch (kupon.trim().toUpperCase()) {
            case "TANSWEAR10" -> new YuzdeOnIndirim();
            case "WELCOME20"  -> new YuzdeYirmiIndirim();
            default           -> new IndirimYok();
        };
    }

    private Fiyatlanabilir ekstraServisleriUygula(Fiyatlanabilir mevcut) {
        JCheckBox cbHediye = new JCheckBox("Hediye Paketi (+20 TL)");
        JCheckBox cbKargo = new JCheckBox("Hızlı Kargo (+50 TL)");
        Object[] ekstralar = {"Siparişinize eklemek istediğiniz servisleri seçin:", cbHediye, cbKargo};
        int onay = JOptionPane.showConfirmDialog(this, ekstralar, "Ekstra Servisler",
                JOptionPane.OK_CANCEL_OPTION);
        if (onay != JOptionPane.OK_OPTION) return null;

        Fiyatlanabilir sonuc = mevcut;
        if (cbHediye.isSelected()) sonuc = new HediyePaketiDecorator(sonuc);
        if (cbKargo.isSelected())  sonuc = new HizliKargoDecorator(sonuc);
        return sonuc;
    }

    private void sepetenCikarDialog() {
        String urunAdi = JOptionPane.showInputDialog(this, "Sepetten çıkarmak istediğiniz ürünün adı:");
        if (urunAdi == null || urunAdi.isBlank()) return;
        sepet.urunCikar(urunAdi);
        sepetAlani.setText(sepet.sepetGoruntule());
    }

    private void paraYatirDialog() {
        String giris = JOptionPane.showInputDialog(this, "Yatırmak istediğiniz tutarı girin:");
        if (giris == null) return;
        try {
            double miktar = Double.parseDouble(giris);
            if (miktar <= 0) { hata("Pozitif bir tutar girin."); return; }
            DosyaIslemleri.bakiyeTalepEkle(musteri.getKullaniciAdi(), miktar);
            mesaj("Para yatırma talebi oluşturuldu. Admin onayından sonra bakiyenize geçecek.");
        } catch (NumberFormatException ex) {
            hata("Geçerli bir sayı girin.");
        }
    }

    // ---------- OBSERVER ----------

    @Override
    public void guncelle() {
        paneliGuncelle();
    }

    private void paneliGuncelle() {
        panelUrunler.removeAll();
        String aramaText = txtArama != null ? txtArama.getText().toLowerCase().trim() : "";

        for (Urun urun : magaza.getUrunListesi()) {
            if (!aramaText.isEmpty() && !urun.getAd().toLowerCase().contains(aramaText)) {
                continue;
            }
            if (urun instanceof Kiyafet k) {
                panelUrunler.add(urunKarti(k));
            }
        }
        panelUrunler.revalidate();
        panelUrunler.repaint();
    }

    private JPanel urunKarti(Kiyafet kiyafet) {
        JPanel kart = new JPanel();
        kart.setLayout(new BoxLayout(kart, BoxLayout.Y_AXIS));
        kart.setBackground(UIThemes.BG_PANEL);
        kart.setBorder(BorderFactory.createLineBorder(UIThemes.FG_MUTED));

        ImageIcon icon = resimYukle(kiyafet.getResimYolu(), 120, 120);
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
                "Ad: " + kiyafet.getAd(),
                "Fiyat: " + kiyafet.getFiyat() + " TL",
                "Stok: " + kiyafet.getStok(),
                "Beden: " + kiyafet.getBeden(),
                "Renk: " + kiyafet.getRenk()
        }) {
            JLabel lbl = new JLabel(bilgi);
            lbl.setForeground(UIThemes.FG_TEXT);
            lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            kart.add(lbl);
        }

        JButton btnEkle = new ModernButton("Sepete Ekle", UIThemes.BG_PRIMARY, UIThemes.FG_TEXT);
        btnEkle.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnEkle.addActionListener(ev -> {
            if (kiyafet.getStok() <= 0) {
                hata(kiyafet.getAd() + " stokta kalmadı.");
                return;
            }
            sepet.urunEkle(kiyafet);
            mesaj(kiyafet.getAd() + " sepete eklendi!");
        });

        kart.add(Box.createRigidArea(new Dimension(0, 5)));
        kart.add(btnEkle);
        kart.add(Box.createRigidArea(new Dimension(0, 5)));
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

    private void mesaj(String s) { JOptionPane.showMessageDialog(this, s); }
    private void hata(String s)  { JOptionPane.showMessageDialog(this, s, "Hata", JOptionPane.ERROR_MESSAGE); }
}
