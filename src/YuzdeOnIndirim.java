// STRATEGY - Concrete Strategy: TANSWEAR10 kuponu için %10 indirim.
public class YuzdeOnIndirim implements IndirimStratejisi {
    @Override
    public double indirimHesapla(double tutar) {
        return tutar * 0.90;
    }
}
