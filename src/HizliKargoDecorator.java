// DECORATOR - Concrete Decorator: sepete 50 TL'lik hızlı kargo ücreti ekler.
public class HizliKargoDecorator extends SepetDecorator {

    private static final double UCRET = 50.0;

    public HizliKargoDecorator(Fiyatlanabilir sepet) {
        super(sepet);
    }

    @Override
    public double getTutar() {
        return super.getTutar() + UCRET;
    }

    @Override
    public String aciklamaGetir() {
        return super.aciklamaGetir() + " + Hızlı Kargo (" + UCRET + " TL)";
    }
}
