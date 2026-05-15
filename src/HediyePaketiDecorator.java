// DECORATOR - Concrete Decorator: sepetin tutarına 20 TL'lik hediye paketi ekler.
public class HediyePaketiDecorator extends SepetDecorator {

    private static final double UCRET = 20.0;

    public HediyePaketiDecorator(Fiyatlanabilir sepet) {
        super(sepet);
    }

    @Override
    public double getTutar() {
        return super.getTutar() + UCRET;
    }

    @Override
    public String aciklamaGetir() {
        return super.aciklamaGetir() + " + Hediye Paketi (" + UCRET + " TL)";
    }
}
