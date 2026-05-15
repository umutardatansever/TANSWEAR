# TANSWEAR Mağaza Otomasyonu — Tasarım Kalıpları Raporu

Bu dokümanda projede uygulanan **7 tasarım kalıbı** açıklanmıştır
(hocanın istediği 6'nın üstüne 1 ekstra: Abstract Factory de eklenmiştir).

| # | Kalıp                  | Tip          | Konum                                                        |
|---|------------------------|--------------|--------------------------------------------------------------|
| 1 | **Singleton**          | Yaratıcı     | `Magaza`                                                     |
| 2 | **Factory Method**     | Yaratıcı     | `UrunFactory`                                                |
| 3 | **Abstract Factory**   | Yaratıcı     | `KoleksiyonFabrikasi`, `YazlikKoleksiyonFabrikasi`, `KislikKoleksiyonFabrikasi` |
| 4 | **Adapter**            | Yapısal      | `BankaAdapter` ↔ `DisBankaSistemi`                          |
| 5 | **Decorator**          | Yapısal      | `SepetDecorator`, `HediyePaketiDecorator`, `HizliKargoDecorator` |
| 6 | **Observer**           | Davranışsal  | `Subject` (Magaza), `Observer` (AdminPaneliGUI, MusteriPaneliGUI) |
| 7 | **Strategy**           | Davranışsal  | `IndirimStratejisi`, `IndirimYok`, `YuzdeOnIndirim`, `YuzdeYirmiIndirim` |

---

## 1) Singleton — `Magaza`

**Amaç:** Sistemde tek bir mağaza nesnesi olduğundan emin olmak.

**Uygulama:**
- Constructor `private`
- `static instance` alanı
- `getInstance()` üzerinden lazy initialization

```java
public class Magaza implements Subject {
    private static Magaza instance;
    private Magaza() {}
    public static Magaza getInstance() {
        if (instance == null) instance = new Magaza();
        return instance;
    }
}
```

`Main`, `Sepet.satinAl()`, GUI'lerin tamamı `Magaza.getInstance()` üzerinden aynı tek nesneye erişir.

---

## 2) Factory Method — `UrunFactory`

**Amaç:** "Hangi `new ___()` çağrılacak?" sorusunu istemciden saklamak; ürün üretim mantığını tek yere toplamak.

```java
public static Urun createUrun(String tur, ...) {
    switch (tur) {
        case "Pantolon": return new Pantolon(...);
        case "Gömlek":   return new Gomlek(...);
        case "Ceket":    return new Ceket(...);
    }
}
```

`AdminPaneliGUI` ürün eklerken ve `Magaza.dosyadanUrunEkle()` ürün okurken bu fabrikayı kullanır.

---

## 3) Abstract Factory — `KoleksiyonFabrikasi`

**Amaç:** Birbirine *uyumlu* ürün ailelerini birlikte üretmek (sezona göre üst+alt giyim).

```
KoleksiyonFabrikasi  (interface)
        ▲
   ┌────┴────┐
Yazlık     Kışlık
(Gömlek+    (Ceket+
 Pantolon)   Pantolon)
```

`AdminPaneliGUI`'deki **"Hızlı Koleksiyon Ekle"** butonu, kullanıcının seçimine göre `YazlikKoleksiyonFabrikasi` veya `KislikKoleksiyonFabrikasi` örneği oluşturur ve aynı fabrikadan hem `ustGiyimUret()` hem `altGiyimUret()` çağrısı yapar — böylece bir koleksiyonun parçaları her zaman birbiriyle uyumlu sezondan üretilir.

---

## 4) Adapter — `BankaAdapter`

**Amaç:** Farklı imzaya sahip iki sistemi birbiriyle konuşturmak.

- **Target:** `OdemeSistemi.odemeYap(miktarTL, kartSahibi)`
- **Adaptee:** `DisBankaSistemi.disBankaOdemeAl(musteriIsmi, miktarDolar)`
- **Adapter:** `BankaAdapter` — TL'yi Dolar'a çevirip dış bankayı çağırır.

```java
public boolean odemeYap(double miktarTL, String kartSahibi) {
    double miktarDolar = miktarTL / USD_KURU;
    return disBanka.disBankaOdemeAl(kartSahibi, miktarDolar);
}
```

`MusteriPaneliGUI` "Kredi Kartı" ödemesinde sadece `OdemeSistemi`'ni gördüğü için dış bankanın gerçek imzasını/biçimini bilmek zorunda değildir.

---

## 5) Decorator — `SepetDecorator` ve alt sınıfları

**Amaç:** Bir nesneye, alt sınıf yaratmadan, çalışma zamanında dinamik olarak yeni davranış (ekstra ücret + açıklama) eklemek.

```
Fiyatlanabilir
   ▲           ▲
Sepet     SepetDecorator
            ▲       ▲
  HediyePaketi   HızlıKargo
```

Müşteri hediye paketi ve hızlı kargo seçerse:
```java
Fiyatlanabilir son = sepet;
if (hediye) son = new HediyePaketiDecorator(son);
if (kargo)  son = new HizliKargoDecorator(son);
double tutar = son.getTutar();   // 100 + 20 + 50 = 170
String aciklama = son.aciklamaGetir();
```

Sarmalayıcılar zincirleme uygulanabildiği için kombinasyonlar sınırsızdır.

---

## 6) Observer — `Subject` / `Observer`

**Amaç:** Bir nesnedeki değişiklikleri dinleyen abonelere otomatik haber vermek.

- **Subject:** `Magaza` — gözlemci listesi tutar, değişiklikte `gozlemcilereHaberVer()` çağırır.
- **Observer:** `AdminPaneliGUI`, `MusteriPaneliGUI` — `guncelle()` metodu çağrıldığında ekranı yeniden çizer.

Admin yeni ürün eklediğinde / stok güncellediğinde aynı anda açık olan müşteri paneli de anında güncellenir; manuel "yenile" düğmesine basmaya gerek kalmaz.

---

## 7) Strategy — `IndirimStratejisi`

**Amaç:** Bir algoritmayı (indirim hesabı) çalışma zamanında değiştirilebilir hale getirmek.

```
IndirimStratejisi
       ▲
┌──────┼──────┐
İndirim   %10        %20
 Yok    İndirim   İndirim
```

Müşteri kupon koduna göre `Sepet`'e farklı strateji yerleşir:

```java
sepet.setIndirimStratejisi(switch (kupon) {
    case "TANSWEAR10" -> new YuzdeOnIndirim();
    case "WELCOME20"  -> new YuzdeYirmiIndirim();
    default           -> new IndirimYok();
});
double tutar = sepet.toplamTutar(); // strateji hesaplar
```

Yeni bir indirim çeşidi eklemek için `Sepet` sınıfı değiştirilmez; sadece yeni bir `IndirimStratejisi` implementasyonu yazılır (Open/Closed Principle).

---

## Klasör/Dosya Genel Yapısı

```
MagazaProjesi/
├── src/
│   ├── Main.java                  ← Uygulama girişi (Singleton'ı başlatır, GUI açar)
│   ├── GirisEkrani.java           ← Login/Kayıt ekranı
│   ├── AdminPaneliGUI.java        ← Yönetici paneli
│   ├── MusteriPaneliGUI.java      ← Müşteri paneli
│   ├── ModernButton.java          ← Özel buton bileşeni
│   ├── UIThemes.java              ← Tema sabitleri (renk/font)
│   │
│   ├── Magaza.java                ← Singleton + Subject
│   ├── Subject.java / Observer.java
│   │
│   ├── Urun.java                  ← Soyut ürün sınıfı
│   ├── Kiyafet.java               ← Ara soyut sınıf (beden/renk/resim)
│   ├── Gomlek.java / Pantolon.java / Ceket.java
│   ├── SatinAlabilir.java
│   │
│   ├── UrunFactory.java                       ← Factory Method
│   ├── KoleksiyonFabrikasi.java               ← Abstract Factory (interface)
│   ├── YazlikKoleksiyonFabrikasi.java         ← Abstract Factory (concrete)
│   ├── KislikKoleksiyonFabrikasi.java         ← Abstract Factory (concrete)
│   │
│   ├── Sepet.java
│   ├── Fiyatlanabilir.java                    ← Decorator (Component)
│   ├── SepetDecorator.java                    ← Decorator (abstract)
│   ├── HediyePaketiDecorator.java             ← Decorator (concrete)
│   ├── HizliKargoDecorator.java               ← Decorator (concrete)
│   │
│   ├── IndirimStratejisi.java                 ← Strategy (interface)
│   ├── IndirimYok.java                        ← Strategy (concrete)
│   ├── YuzdeOnIndirim.java                    ← Strategy (concrete)
│   ├── YuzdeYirmiIndirim.java                 ← Strategy (concrete)
│   │
│   ├── OdemeSistemi.java                      ← Adapter (Target)
│   ├── DisBankaSistemi.java                   ← Adapter (Adaptee)
│   ├── BankaAdapter.java                      ← Adapter (Adapter)
│   │
│   ├── Kullanici.java
│   ├── Admin.java
│   ├── Musteri.java
│   ├── DosyaIslemleri.java
│   ├── KullaniciIslemleri.java
│   └── resimler/                              ← Ürün görselleri
│
├── UML.puml                       ← UML sınıf diyagramı (PlantUML)
├── TASARIM_KALIPLARI.md           ← Bu doküman
├── README.md                      ← Çalıştırma talimatları
├── urunler.txt                    ← Ürün veritabanı
└── kullanicilar.txt               ← Kullanıcı veritabanı
```
