package patterns.adapter;

// ADAPTER PATTERN — Adaptee (uyumsuz olan taraf).
// Bu sınıfı doğrudan değiştiremiyoruz (örnek: 3. parti banka kütüphanesi olduğunu varsayalım).
// Kendi imzasıyla çalışır: kart sahibi adı + DOLAR cinsinden tutar bekler.
public class DisBankaSistemi {

    public boolean disBankaOdemeAl(String musteriIsmi, double miktarDolar) {
        System.out.println("Dış banka: " + musteriIsmi + " adına "
                + String.format("%.2f", miktarDolar) + " USD tutarında ödeme alındı.");
        return true; // Simülasyon — gerçek bağlantı olmadığı için her zaman başarılı.
    }
}
