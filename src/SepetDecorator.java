// DECORATOR - Soyut süsleyici sınıfı.
// Hem Fiyatlanabilir'i uygular hem de içinde başka bir Fiyatlanabilir tutar.
// Alt sınıflar tutara/açıklamaya kendi katkılarını ekler.
public abstract class SepetDecorator implements Fiyatlanabilir {

    protected final Fiyatlanabilir sepet;

    protected SepetDecorator(Fiyatlanabilir sepet) {
        this.sepet = sepet;
    }

    @Override
    public double getTutar() {
        return sepet.getTutar();
    }

    @Override
    public String aciklamaGetir() {
        return sepet.aciklamaGetir();
    }
}
