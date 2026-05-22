# TANSWEAR -- Java Tabanli Magaza Otomasyonu

Java Swing ile gelistirilmis, dosya tabanli veri saklama kullanan bir kiyafet magazasi uygulamasi. Proje, nesne yonelimli programlamanin temel tasarim kaliplarini gercek bir senaryo uzerinde uygulamak amaciyla olusturulmustur.

---

## Icindekiler

- [Proje Ozeti](#proje-ozeti)
- [Ozellikler](#ozellikler)
- [Mimari ve Tasarim Kaliplari](#mimari-ve-tasarim-kaliplari)
- [Klasor Yapisi](#klasor-yapisi)
- [Kurulum ve Calistirma](#kurulum-ve-calistirma)
- [Kullanim Kilavuzu](#kullanim-kilavuzu)
- [Teknik Detaylar](#teknik-detaylar)

---

## Proje Ozeti

TANSWEAR, bir kiyafet magazasi icin gelistirilmis otomasyon sistemidir. Uygulama iki farkli kullanici rolu destekler: yonetici (admin) ve musteri. Admin urunleri yonetirken, musteriler urunleri goruntuleleyip satin alma islemi gerceklestirebilir. Proje genelinde toplam **7 tasarim kalibi** uygulanmistir.

---

## Ozellikler

### Musteri Islevleri

- Urunleri gorsel kartlarla listeleme
- Urun arama (anlil filtreleme)
- Sepete urun ekleme/cikarma
- Kupon koduyla indirim uygulama
- Hediye paketi ve hizli kargo secenekleri
- Cuzdan bakiyesi veya kredi karti ile odeme
- Para yatirma talebi olusturma

### Admin Islevleri

- Urun ekleme, silme, guncelleme
- Stok yonetimi
- Kategoriye gore filtreleme
- Hizli koleksiyon ekleme (yazlik / kislik)
- Kritik stok uyarisi goruntuleme
- Musteri bakiye taleplerini onaylama
- Manuel bakiye yukleme

---

## Mimari ve Tasarim Kaliplari

Projede 7 tasarim kalibi, 3 farkli kategoride uygulanmistir. Asagida her kalibin ne oldugu, neden secildigi ve projede nasil hayata gecirildigi aciklanmistir.

### Yonetim Tablosu

| No | Kalip             | Kategori    | Ilgili Siniflar                                                            |
|----|-------------------|-------------|----------------------------------------------------------------------------|
| 1  | Singleton         | Yaratici    | `Magaza`                                                                   |
| 2  | Factory Method    | Yaratici    | `UrunFactory`                                                              |
| 3  | Abstract Factory  | Yaratici    | `KoleksiyonFabrikasi`, `YazlikKoleksiyonFabrikasi`, `KislikKoleksiyonFabrikasi` |
| 4  | Adapter           | Yapisal     | `OdemeSistemi`, `BankaAdapter`, `DisBankaSistemi`                          |
| 5  | Decorator         | Yapisal     | `Fiyatlanabilir`, `SepetDecorator`, `HediyePaketiDecorator`, `HizliKargoDecorator` |
| 6  | Observer          | Davranissal | `Subject`, `Observer`, `Magaza`, `AdminPaneliGUI`, `MusteriPaneliGUI`      |
| 7  | Strategy          | Davranissal | `IndirimStratejisi`, `IndirimYok`, `YuzdeOnIndirim`, `YuzdeYirmiIndirim`   |

---

### 1. Singleton -- `Magaza`

**Nedir:** Bir sinifin sistemde yalnizca tek bir orneginin (instance) bulunmasini garanti eden yaratici kaliptir.

**Neden kullanildi:** Magaza uygulamasinda urun listesi, gozlemciler ve stok bilgileri tek bir merkezden yonetilmelidir. Birden fazla `Magaza` nesnesi olusturulursa, admin panelinde yapilan degisiklik musteri panelindeki listeye yansimazdı. Singleton, tum bilesenlerin (GUI, Sepet, Main) ayni veri kaynagina eristigi garanti eder.

**Nasil uygulanir:**

- Constructor `private` yapilarak disaridan `new Magaza()` engellenir.
- `static instance` alani ve `getInstance()` metodu ile tek nesneye erisim saglanir (lazy initialization).

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

**Kullanim noktalari:** `Main.main()`, `Sepet.satinAl()`, `AdminPaneliGUI`, `MusteriPaneliGUI`.

---

### 2. Factory Method -- `UrunFactory`

**Nedir:** Nesne olusturma isini alt siniflara veya bir fabrika sinifina devreden yaratici kaliptir. Istemci kodu hangi somut sinifin uretildigini bilmek zorunda kalmaz.

**Neden kullanildi:** Uygulamada uc farkli urun tipi vardir: Pantolon, Gomlek, Ceket. Urun olusturulacak her yerde (admin paneli, dosya okuma) `switch-case` ile `new Pantolon(...)`, `new Gomlek(...)` yazmak yerine tek bir fabrika metodu kullanmak, kod tekrarini onler ve yeni urun tipleri eklendiginde degisikligi tek bir noktayla sinirlar.

**Nasil uygulanir:**

```java
public class UrunFactory {
    public static Urun createUrun(String tur, String ad, double fiyat,
                                  int stok, String beden, String renk, String resimYolu) {
        switch (tur) {
            case "Pantolon": return new Pantolon(ad, fiyat, stok, beden, renk, resimYolu);
            case "Gomlek":   return new Gomlek(ad, fiyat, stok, beden, renk, resimYolu);
            case "Ceket":    return new Ceket(ad, fiyat, stok, beden, renk, resimYolu);
            default: throw new IllegalArgumentException("Bilinmeyen urun turu: " + tur);
        }
    }
}
```

**Kullanim noktalari:** `AdminPaneliGUI.yeniUrunDialog()`, `Magaza.dosyadanUrunEkle()`.

---

### 3. Abstract Factory -- `KoleksiyonFabrikasi`

**Nedir:** Birbiriyle iliskili veya uyumlu nesneler ailesini, somut siniflarini belirtmeden ureten yaratici kaliptir. Factory Method'un bir ust seviyesidir.

**Neden kullanildi:** Bir giyim koleksiyonunun "ust giyim" ve "alt giyim" parcalari ayni sezona ait olmalidir. Factory Method tek tek urun uretirken, Abstract Factory iki urunu bir arada uretir ve sezon tutarliligi saglar: yazlik fabrika ince Gomlek + ince Pantolon, kislik fabrika kalin Ceket + kalin Pantolon uretir. Boylece "yazlik ceket + kislik pantolon" gibi uyumsuz bir bilesim olusturulamaz.

**Nasil uygulanir:**

```
KoleksiyonFabrikasi (interface)
        ^
   +----+----+
Yazlik       Kislik
Fabrikasi    Fabrikasi
```

- `KoleksiyonFabrikasi` arayuzu: `ustGiyimUret()`, `altGiyimUret()`, `koleksiyonAdi()` metotlarini tanimlar.
- `YazlikKoleksiyonFabrikasi`: Gomlek + Pantolon uretir.
- `KislikKoleksiyonFabrikasi`: Ceket + Pantolon uretir.

**Kullanim noktasi:** `AdminPaneliGUI.koleksiyonEkleDialog()`.

---

### 4. Adapter -- `BankaAdapter`

**Nedir:** Uyumsuz arayuzlere sahip iki sistemi birbirine baglayan yapisal kaliptir. Mevcut kodu degistirmeden, farkli bir arayuze uyum saglar.

**Neden kullanildi:** Proje icindeki odeme sistemi TL cinsinden calisirken, dis banka sistemi USD cinsinden odeme kabul eder. Musteri panelinin dis bankanin ic yapisini (doviz kurunu, parametre sirasini) bilmesine gerek yoktur. Adapter, iki sistem arasindaki donusumu tek bir yerde yapar; is mantigi degistiginde yalnizca Adapter guncellenir.

**Roller:**

| Rol     | Sinif              | Aciklama                                          |
|---------|--------------------|----------------------------------------------------|
| Target  | `OdemeSistemi`     | Bizim sistemimizin bekledigi arayuz (TL cinsinden) |
| Adaptee | `DisBankaSistemi`  | Uyumsuz olan dis sistem (USD cinsinden)            |
| Adapter | `BankaAdapter`     | TL'yi USD'ye cevirip dis bankayi cagirir           |

```java
public boolean odemeYap(double miktarTL, String kartSahibi) {
    double miktarDolar = miktarTL / USD_KURU;
    return disBanka.disBankaOdemeAl(kartSahibi, miktarDolar);
}
```

**Kullanim noktasi:** `MusteriPaneliGUI.satinAlmaAkisi()` icindeki "Kredi Karti" secenegi.

---

### 5. Decorator -- `SepetDecorator`

**Nedir:** Bir nesneye alt sinif yaratmadan, calisma zamaninda dinamik olarak yeni davranislar (islevler) ekleyen yapisal kaliptir.

**Neden kullanildi:** Musteri, siparisine hediye paketi ve/veya hizli kargo ekleyebilir. Bu opsiyonlarin her kombinasyonu icin ayri bir sinif olusturmak (`SepetHediyeli`, `SepetKargolu`, `SepetHediyeliVeKargolu`...) sinif patlamasina yol acar. Decorator kalibinda her ekstra servis bagimsiz bir sarmalayici siniftir ve zincir seklinde bir birine eklenebilir. Yeni bir servis eklemek icin sadece yeni bir Decorator sinifi yazmak yeterlidir.

**Sinif hiyerarsisi:**

```
Fiyatlanabilir (interface)
    ^              ^
  Sepet      SepetDecorator (abstract)
                ^            ^
      HediyePaketi    HizliKargo
      Decorator       Decorator
```

**Calisma zamani ornegi:**

```java
Fiyatlanabilir sonuc = sepet;                          // Temel: 100 TL
if (hediye) sonuc = new HediyePaketiDecorator(sonuc);  // +20 TL
if (kargo)  sonuc = new HizliKargoDecorator(sonuc);    // +50 TL
double tutar = sonuc.getTutar();                       // = 170 TL
```

**Kullanim noktasi:** `MusteriPaneliGUI.ekstraServisleriUygula()`.

---

### 6. Observer -- `Subject` / `Observer`

**Nedir:** Bir nesnedeki durum degisikliklerini, ona abone olan diger nesnelere otomatik olarak bildiren davranissal kaliptir. "Yayin-abone" (publish-subscribe) modeli olarak da bilinir.

**Neden kullanildi:** Admin yeni urun ekler veya stok guncellerse, o sirada acik olan musteri panelinin de aninda guncellenmesi gerekir. Observer olmadan her GUI ekraninin periyodik olarak veriyi sorgulamasi (polling) ya da kullanicinin "Yenile" butonuna basmasi gerekirdi. Observer kalibinda magaza degistiginde tum kayitli panellere otomatik haber gider; gereksiz sorgu yapilmaz ve kullanici deneyimi daha akici olur.

**Roller:**

| Rol       | Sinif/Arayuz                              | Gorev                                   |
|-----------|-------------------------------------------|-----------------------------------------|
| Subject   | `Subject` (arayuz), `Magaza` (uygulayan) | Gozlemcileri tutar, degisiklikte haber verir |
| Observer  | `Observer` (arayuz)                       | `guncelle()` metodu ile bildirim alir    |
| Concrete  | `AdminPaneliGUI`, `MusteriPaneliGUI`     | Haber alinca ekrani yeniden cizer        |

**Akis:** Admin urun ekler --> `Magaza.urunEkle()` --> `gozlemcilereHaberVer()` --> Tum GUI'ler `guncelle()` ile tabloyu yeniler.

---

### 7. Strategy -- `IndirimStratejisi`

**Nedir:** Bir algoritmalar ailesini tanimlayin, her birini ayri bir sinifa koyun ve bunlari birbiriyle degistirilebilir yapin. Istemci kodu, kullanilan algoritmadan bagimsiz olarak calisir.

**Neden kullanildi:** Farkli kupon kodlari farkli indirim oranlarini tetikler. Bu mantik `Sepet` sinifinin icinde `if/else` zincirleriyle yazilsaydi, her yeni indirim turunde `Sepet` sinifi degistirilmek zorunda kalirdi. Bu durum Acik/Kapali Ilkesini (Open/Closed Principle) ihlal eder. Strategy kalibinda yeni bir indirim eklemek icin yalnizca yeni bir `IndirimStratejisi` sinifi olusturulur; `Sepet` sinifina dokunulmaz.

**Sinif hiyerarsisi:**

```
IndirimStratejisi (interface)
    ^         ^           ^
IndirimYok  YuzdeOn     YuzdeYirmi
            Indirim     Indirim
```

**Mevcut kupon kodlari:**

| Kod          | Strateji Sinifi      | Indirim Orani |
|--------------|----------------------|---------------|
| TANSWEAR10   | `YuzdeOnIndirim`     | %10           |
| WELCOME20    | `YuzdeYirmiIndirim`  | %20           |
| (gecersiz)   | `IndirimYok`         | %0            |

**Kullanim noktasi:** `MusteriPaneliGUI.stratejiSec()`, `Sepet.toplamTutar()`.

---

## Tasarim Kaliplarinin Birlikte Calismasi

Asagida bir musteri satin alma akisinda kaliplarin nasil ic ice calistiginin ozeti verilmistir:

```
1. Musteri giris yapar
       |
2. MusteriPaneliGUI olusturulur
       |-- SINGLETON: Magaza.getInstance() ile tekil magazaya erisir
       |-- OBSERVER: Magaza'ya gozlemci olarak kaydolur
       |
3. Urunler listelenir
       |-- FACTORY METHOD: Dosyadan okunan veriler UrunFactory ile nesnelere donusturulur
       |
4. Musteri urun secer, "Satin Al" der
       |-- STRATEGY: Kupon koduna gore IndirimStratejisi belirlenir
       |-- DECORATOR: Hediye paketi / hizli kargo servisleri sepete sarmalanir
       |-- ADAPTER: Kredi karti secilirse BankaAdapter uzerinden dis bankaya odeme yapilir
       |
5. Satin alma tamamlanir
       |-- SINGLETON: Magaza.getInstance() uzerinden stok dusurulur
       |-- OBSERVER: Stok degisikligi otomatik olarak acik GUI'lere yansir
```

---

## Klasor Yapisi

```
MagazaProjesi/
|-- src/
|   |-- Main.java                         Uygulama giris noktasi (default package)
|   |
|   |-- patterns/                         TASARIM KALIPLARI PAKETI
|   |   |-- singleton/
|   |   |   +-- Magaza.java               Singleton + Subject
|   |   |-- factorymethod/
|   |   |   +-- UrunFactory.java          Factory Method
|   |   |-- abstractfactory/
|   |   |   +-- KoleksiyonFabrikasi.java  Abstract Factory (arayuz)
|   |   |   +-- YazlikKoleksiyonFabrikasi.java
|   |   |   +-- KislikKoleksiyonFabrikasi.java
|   |   |-- adapter/
|   |   |   +-- OdemeSistemi.java         Adapter (Target)
|   |   |   +-- DisBankaSistemi.java      Adapter (Adaptee)
|   |   |   +-- BankaAdapter.java         Adapter (Adapter)
|   |   |-- decorator/
|   |   |   +-- Fiyatlanabilir.java       Decorator (Component)
|   |   |   +-- SepetDecorator.java       Decorator (Abstract Decorator)
|   |   |   +-- HediyePaketiDecorator.java
|   |   |   +-- HizliKargoDecorator.java
|   |   |-- observer/
|   |   |   +-- Subject.java              Observer (Subject arayuzu)
|   |   |   +-- Observer.java             Observer (Observer arayuzu)
|   |   |-- strategy/
|   |   |   +-- IndirimStratejisi.java    Strategy (arayuz)
|   |   |   +-- IndirimYok.java
|   |   |   +-- YuzdeOnIndirim.java
|   |   |   +-- YuzdeYirmiIndirim.java
|   |
|   |-- model/                            TEMEL VERI MODELLERI
|   |   |-- Urun.java
|   |   |-- Kiyafet.java
|   |   |-- Gomlek.java
|   |   |-- Pantolon.java
|   |   |-- Ceket.java
|   |   |-- SatinAlabilir.java
|   |   |-- Sepet.java
|   |   |-- Kullanici.java
|   |   |-- Admin.java
|   |   +-- Musteri.java
|   |
|   |-- gui/                              GRAFIKSEL KULLANICI ARAYUZLERI
|   |   |-- GirisEkrani.java
|   |   |-- AdminPaneliGUI.java
|   |   |-- MusteriPaneliGUI.java
|   |   |-- ModernButton.java
|   |   +-- UIThemes.java
|   |
|   |-- util/                             YARDIMCI SINIFLAR
|   |   |-- DosyaIslemleri.java
|   |   +-- KullaniciIslemleri.java
|   |
|   +-- resimler/                         Urun gorselleri
|       |-- pantolon1.jpg
|       |-- pantolon2.jpg
|       |-- pantolon3.jpg
|       |-- gomlek1.jpg
|       |-- gomlek2.jpg
|       |-- ceket1.jpg
|       +-- ceket2.jpg
|
|-- bin/                                  Derlenen siniflar (.class)
|-- urunler.txt                           Urun veritabani
|-- kullanicilar.txt                      Kullanici veritabani
|-- giris_kaydi.txt                       Giris gecmisi
|-- UML.puml                              UML sinif diyagrami (PlantUML)
|-- TASARIM_KALIPLARI.md                  Detayli tasarim kaliplari dokumani
+-- README.md                             Bu dosya
```

---

## Kurulum ve Calistirma

### On Kosullar

- **Java JDK 17** veya ustu kurulu olmalidir.
- Komut satirinda `java` ve `javac` komutlari erisebilir olmalidir.

### Komut Satirindan Derleme ve Calistirma

Proje paket yapisina (subdirectories) sahip oldugu icin recursive derlenmelidir:

#### 1. Windows PowerShell Uzerinde:
```powershell
cd MagazaProjesi
javac -d bin -encoding UTF-8 (Get-ChildItem -Recurse src/*.java)
xcopy /Y /E /I src\resimler bin\resimler
java -cp bin Main
```

#### 2. Windows Klasik Komut Satirinda (CMD):
```cmd
cd MagazaProjesi
dir /s /b src\*.java > sources.txt
javac -d bin -encoding UTF-8 @sources.txt
del sources.txt
xcopy /Y /E /I src\resimler bin\resimler
java -cp bin Main
```

#### 3. Linux / macOS Terminalinde:
```bash
cd MagazaProjesi
find src -name "*.java" | xargs javac -d bin -encoding UTF-8
cp -r src/resimler bin/
java -cp bin Main
```

Not: `xcopy` adimi, urun gorsellerinin classpath uzerinde bulunmasini saglar. Eclipse veya IntelliJ kullaniyorsaniz IDE bu kopyalamayi otomatik yapar.

### Bat Dosyasi ile (Windows)

Masaustundeki `MagazaProjesi_Baslat.bat` dosyasini cift tiklayarak uygulamayi dogrudan baslatabilirsiniz. Bu dosya, onceden derlenmis `bin/` klasorunundeki sinif dosyalarini kullanir.

### IDE Uzerinden (Eclipse / IntelliJ)

1. Projeyi aciniz.
2. `src` klasorunu kaynak (source) olarak isaretleyiniz.
3. `Main.java` uzerinde sag tiklayip **Run As > Java Application** deyiniz.

---

## Kullanim Kilavuzu

### Varsayilan Hesaplar

| Kullanici Tipi | Kullanici Adi | Sifre   |
|----------------|---------------|---------|
| Admin          | TANSWEAR      | admin   |
| Musteri        | Kaydol butonu ile yeni hesap olusturulur | -- |

### Kupon Kodlari

| Kod          | Indirim Orani |
|--------------|---------------|
| TANSWEAR10   | %10           |
| WELCOME20    | %20           |

### Ekstra Servisler

| Servis         | Ek Ucret |
|----------------|----------|
| Hediye Paketi  | +20 TL   |
| Hizli Kargo    | +50 TL   |

Her iki servis ayni anda secilebilir. Dekoratorler zincirleme sarmalanir: Sepet Tutari + Hediye Paketi + Hizli Kargo.

### Dosya Tabanli Veri Formati

| Dosya              | Format                                          |
|--------------------|-------------------------------------------------|
| `urunler.txt`      | `tur,ad,fiyat,stok,beden,renk,resimYolu`       |
| `kullanicilar.txt` | `kullaniciAdi,sifre,tip,bakiye[,TALEP:miktar]`  |
| `giris_kaydi.txt`  | `kullaniciAdi,sifre,tarih`                      |

---

## Teknik Detaylar

### Kullanilan Teknolojiler

| Bilesne         | Teknoloji         |
|-----------------|-------------------|
| Dil             | Java 17+          |
| GUI             | Java Swing        |
| Veri Saklama    | Duz metin dosyasi |
| Gorsel Yukleme  | Classpath (getResource) |

### SOLID Ilkeleriyle Uyum

- **Single Responsibility:** Her sinif tek bir sorumluluga sahiptir. Ornegin `DosyaIslemleri` yalnizca dosya I/O, `UrunFactory` yalnizca urun olusturma ile ilgilenir.
- **Open/Closed:** Strategy ve Decorator kaliplari sayesinde yeni indirim turleri ve ekstra servisler mevcut siniflar degistirilmeden eklenebilir.
- **Liskov Substitution:** Tum `Kiyafet` alt siniflari (`Gomlek`, `Pantolon`, `Ceket`) ust sinifin yerine guvenlice kullanilabilir.
- **Interface Segregation:** `Fiyatlanabilir`, `SatinAlabilir`, `OdemeSistemi` gibi kucuk, odakli arayuzler kullanilmistir.
- **Dependency Inversion:** GUI siniflarim somut siniflar yerine arayuzlere (`IndirimStratejisi`, `OdemeSistemi`, `Fiyatlanabilir`) bagimlidir.

---

## Lisans

Bu proje egitim amacli gelistirilmistir.
