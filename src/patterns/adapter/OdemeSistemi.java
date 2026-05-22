package patterns.adapter;

// 6. ADAPTER PATTERN — Target arayüzü.
// Bizim sistemimizin beklediği "ortak" ödeme arayüzü. TL cinsinden çalışır.
public interface OdemeSistemi {
    boolean odemeYap(double miktarTL, String kartSahibi);
}
