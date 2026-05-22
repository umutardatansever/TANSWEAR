package patterns.observer;

import patterns.singleton.Magaza;

// 3. OBSERVER PATTERN — Subject (Konu) tarafı.
// Magaza bu arayüzü uygular: gözlemci ekler/çıkarır ve değişiklikte hepsine haber verir.
public interface Subject {
    void gozlemciEkle(Observer o);
    void gozlemciCikar(Observer o);
    void gozlemcilereHaberVer();
}
