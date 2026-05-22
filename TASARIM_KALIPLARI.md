# TANSWEAR Mağaza Otomasyonu — Tasarım Kalıpları Raporu

Bu dokümanda projede uygulanan **7 tasarım kalıbı** açıklanmıştır
(hocanın istediği 6'nın üstüne 1 ekstra: Abstract Factory de eklenmiştir).

| # | Kalıp                  | Tip          | Konum (Paket / Sınıflar)                                                        |
|---|------------------------|--------------|---------------------------------------------------------------------------------|
| 1 | **Singleton**          | Yaratıcı     | `patterns.singleton.Magaza`                                                     |
| 2 | **Factory Method**     | Yaratıcı     | `patterns.factorymethod.UrunFactory`                                            |
| 3 | **Abstract Factory**   | Yaratıcı     | `patterns.abstractfactory` (`KoleksiyonFabrikasi`, `Yazlik...`, `Kislik...`)     |
| 4 | **Adapter**            | Yapısal      | `patterns.adapter` (`BankaAdapter` ↔ `DisBankaSistemi` / `OdemeSistemi`)        |
| 5 | **Decorator**          | Yapısal      | `patterns.decorator` (`SepetDecorator`, `HediyePaketi...`, `HizliKargo...`)     |
| 6 | **Observer**           | Davranışsal  | `patterns.observer` (`Subject` ↔ `Observer`), `gui` (`AdminPaneliGUI`, etc.)    |
| 7 | **Strategy**           | Davranışsal  | `patterns.strategy` (`IndirimStratejisi`, `IndirimYok`, `YuzdeOn...`, etc.)     |

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
│   ├── Main.java                  ← Uygulama girişi (default package)
│   │
│   ├── patterns/                  ← Tasarım kalıplarının paketleri
│   │   ├── singleton/
│   │   │   └── Magaza.java        ← Singleton + Subject
│   │   ├── factorymethod/
│   │   │   └── UrunFactory.java   ← Factory Method
│   │   ├── abstractfactory/
│   │   │   ├── KoleksiyonFabrikasi.java
│   │   │   ├── YazlikKoleksiyonFabrikasi.java
│   │   │   └── KislikKoleksiyonFabrikasi.java
│   │   ├── adapter/
│   │   │   ├── OdemeSistemi.java
│   │   │   ├── DisBankaSistemi.java
│   │   │   └── BankaAdapter.java
│   │   ├── decorator/
│   │   │   ├── Fiyatlanabilir.java
│   │   │   ├── SepetDecorator.java
│   │   │   ├── HediyePaketiDecorator.java
│   │   │   └── HizliKargoDecorator.java
│   │   ├── observer/
│   │   │   ├── Subject.java
│   │   │   └── Observer.java
│   │   └── strategy/
│   │       ├── IndirimStratejisi.java
│   │       ├── IndirimYok.java
│   │       ├── YuzdeOnIndirim.java
│   │       └── YuzdeYirmiIndirim.java
│   │
│   ├── model/                     ← Veri modelleri ve sepet
│   │   ├── Urun.java
│   │   ├── Kiyafet.java
│   │   ├── Gomlek.java
│   │   ├── Pantolon.java
│   │   ├── Ceket.java
│   │   ├── SatinAlabilir.java
│   │   ├── Sepet.java
│   │   ├── Kullanici.java
│   │   ├── Admin.java
│   │   └── Musteri.java
│   │
│   ├── gui/                       ← Grafiksel kullanıcı arayüzleri
│   │   ├── GirisEkrani.java
│   │   ├── AdminPaneliGUI.java
│   │   ├── MusteriPaneliGUI.java
│   │   ├── ModernButton.java
│   │   └── UIThemes.java
│   │
│   ├── util/                      ← Dosya ve kullanıcı yardımcı sınıfları
│   │   ├── DosyaIslemleri.java
│   │   └── KullaniciIslemleri.java
│   │
│   └── resimler/                  ← Ürün görselleri
│
├── UML.puml                       ← Güncellenmiş UML sınıf diyagramı
├── TASARIM_KALIPLARI.md           ← Bu doküman
├── README.md                      ← Çalıştırma talimatları
├── urunler.txt                    ← Ürün veritabanı
└── kullanicilar.txt               ← Kullanıcı veritabanı
```
