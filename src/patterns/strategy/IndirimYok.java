package patterns.strategy;

// STRATEGY - Concrete Strategy: indirim uygulanmadığı durum (varsayılan).
public class IndirimYok implements IndirimStratejisi {
    @Override
    public double indirimHesapla(double tutar) {
        return tutar;
    }
}
