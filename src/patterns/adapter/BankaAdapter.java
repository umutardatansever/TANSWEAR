package patterns.adapter;

// 6. ADAPTER PATTERN (Adaptör) — Concrete Adapter.
// Bizim sistemimiz 'OdemeSistemi' (TL) bekler, dış banka 'DisBankaSistemi' (USD) ile çalışır.
// Bu sınıf çağrıyı uygun formata çevirip dış sisteme iletir; istemci kodu hangi tarafın
// kullanıldığını bilmek zorunda kalmaz.
public class BankaAdapter implements OdemeSistemi {

    private static final double USD_KURU = 30.0; // 1 USD = 30 TL varsayımı (simülasyon)

    private final DisBankaSistemi disBanka;

    public BankaAdapter() {
        this.disBanka = new DisBankaSistemi();
    }

    public BankaAdapter(DisBankaSistemi disBanka) {
        this.disBanka = disBanka;
    }

    @Override
    public boolean odemeYap(double miktarTL, String kartSahibi) {
        double miktarDolar = miktarTL / USD_KURU;
        return disBanka.disBankaOdemeAl(kartSahibi, miktarDolar);
    }
}
