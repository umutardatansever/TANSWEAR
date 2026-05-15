// 7. ABSTRACT FACTORY PATTERN (Soyut Fabrika):
// Tek tek ürün üreten Factory Method'un (UrunFactory) bir adım üstü.
// Burada birbiriyle uyumlu ÜRÜN AİLELERİ üretiyoruz: bir koleksiyonun "üst giyim" ve
// "alt giyim" parçaları aynı sezonun ürünleri olacak şekilde birlikte oluşturuluyor.
// Yazlık fabrikası ince Gömlek + ince Pantolon, Kışlık fabrikası kalın Ceket + kalın Pantolon üretir.
public interface KoleksiyonFabrikasi {
    Urun ustGiyimUret(String ad, double fiyat, int stok, String beden, String renk, String resimYolu);
    Urun altGiyimUret(String ad, double fiyat, int stok, String beden, String renk, String resimYolu);
    String koleksiyonAdi();
}
