package patterns.observer;

import patterns.singleton.Magaza;

// 3. OBSERVER PATTERN — Observer (Gözlemci) tarafı.
// Subject'in (Magaza) yaydığı değişiklikleri dinleyen tüm bileşenler bu arayüzü uygular.
public interface Observer {
    void guncelle();
}
