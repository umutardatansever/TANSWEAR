package patterns.strategy;

// STRATEGY - Concrete Strategy: WELCOME20 kuponu için %20 indirim.
public class YuzdeYirmiIndirim implements IndirimStratejisi {
    @Override
    public double indirimHesapla(double tutar) {
        return tutar * 0.80;
    }
}
