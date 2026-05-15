// 5. DECORATOR PATTERN (Süsleyici): Component arayüzü.
// Hem süslenecek olan asıl bileşenin (Sepet) hem de süsleyicilerin (HediyePaketi, HızlıKargo)
// aynı tipte görünmesini sağlar; süsleyiciler iç içe sarmalanabilir.
public interface Fiyatlanabilir {
    double getTutar();
    String aciklamaGetir();
}
