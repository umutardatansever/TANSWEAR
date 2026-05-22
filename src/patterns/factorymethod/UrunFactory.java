package patterns.factorymethod;

import model.Ceket;
import model.Gomlek;
import model.Pantolon;
import model.Urun;

// 2. FACTORY METHOD PATTERN (Fabrika Metodu):
// Gömlek/Pantolon/Ceket nesnelerini oluşturma sorumluluğunu tek bir yerde toplar.
// Ürün türü stringini alır, doğru somut sınıfın örneğini üretir; çağıran taraf "new"
// ile hangi sınıfı kullandığını bilmek zorunda kalmaz.
public class UrunFactory {

    private UrunFactory() {}

    public static Urun createUrun(String tur, String ad, double fiyat, int stok,
                                  String beden, String renk, String resimYolu) {
        if (tur == null) {
            throw new IllegalArgumentException("Ürün türü boş olamaz.");
        }
        switch (tur) {
            case "Pantolon":
                return new Pantolon(ad, fiyat, stok, beden, renk, resimYolu);
            case "Gömlek":
            case "Gomlek":
                return new Gomlek(ad, fiyat, stok, beden, renk, resimYolu);
            case "Ceket":
                return new Ceket(ad, fiyat, stok, beden, renk, resimYolu);
            default:
                throw new IllegalArgumentException("Bilinmeyen ürün türü: " + tur);
        }
    }
}
