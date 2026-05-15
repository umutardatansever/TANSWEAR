// 4. STRATEGY PATTERN (Strateji): Sepet, hangi indirim algoritmasını kullanacağını
// kendi içinde sabit bir if/else ile değil, bu arayüze atanmış bir strateji nesnesiyle belirler.
// Yeni bir indirim çeşidi eklemek için sadece yeni bir IndirimStratejisi sınıfı yazılır;
// Sepet sınıfını değiştirmek gerekmez.
public interface IndirimStratejisi {
    double indirimHesapla(double tutar);
}
