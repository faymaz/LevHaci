# Levhacı - Perfboard Tasarım Aracı

Levhacı, elektronik devre tasarımcıları için geliştirilmiş özel bir perfboard tasarım uygulamasıdır. Java ve JavaFX kullanılarak modern bir arayüz ile profesyonel devre düzenleri oluşturmanızı sağlar.

## Levhacı Nedir?

"Levhacı" kelimesi "levha yapan kişi" anlamına gelir. Bu uygulama ile çeşitli elektronik bileşenler kullanarak perfboard üzerinde hassas ve kolay şekilde elektronik devreler tasarlayabilirsiniz.

## Özellikler

### Board Türleri
- **Delikli Board** - Standart nokta matris perfboardlar
- **Şeritli Board** - Önceden bağlı şerit düzenleri
- **Karışık Board** - Nokta ve şerit kombinasyonu
- **Tek/Çift Taraf** seçenekleri

### Standart Boyutlar
- **160x100mm** - Büyük projeler için
- **100x100mm** - Orta ölçekli devreler
- **Özel boyutlar** - Esnek ölçüler
- **2.54mm ızgara** - Standart perfboard aralığı

### Bileşen Kütüphanesi

#### Pasif Bileşenler
- **Dirençler** - Ayarlanabilir 2-5 delik aralığı
- **Kondensatörler** - Elektrolitik ve seramik türler
- **Potansiyometreler** - 3 pinli ayar elemanları

#### Aktif Bileşenler  
- **LED'ler** - Çeşitli renkler
- **Diyotlar** - Doğrultucu ve Zener türleri
- **Transistörler** - NPN/PNP çeşitleri

#### Entegre Devreler
- **DIP4** - Optocoupler, Temel Mantık
- **DIP8** - 555 Timer, Op-amp, EEPROM  
- **DIP14** - TTL Mantık, Sayıcılar
- **DIP16** - Mikrocontroller, SRAM
- **DIP28** - ATmega328, EPROM
- **DIP40** - 8-bit CPU, Büyük MCU
- DIP4'ten DIP40'a kadar tam seri

#### Anahtarlar
- **Push Butonlar** - Anlık anahtarlar
- **Toggle Anahtarlar** - Açma/kapama kontrolleri

### Gelişmiş Yerleştirme Sistemi
- **Izgara Hizalama** - Otomatik ızgara hizalama
- **4 Yön Desteği** - Yatay, dikey, çapraz yerleştirme
- **Sürükle Bırak** - Kolay bileşen konumlandırma
- **Gerçek Ölçek** - 1:1 ölçek görüntüleme

## Kurulum

### Gereksinimler
- **Java 17** (OpenJDK önerilen)
- **Maven** 
- **JavaFX SDK** (Maven bağımlılıkları ile dahil)

### Kurulum Adımları
1. Depoyu klonlayın:
   ```bash
   git clone https://github.com/faymaz/Levhaci.git
   cd Levhaci/PerfBoardDesigner
   ```

2. Bağımlılıkları yükleyin:
   ```bash
   mvn install
   ```

3. Uygulamayı çalıştırın:
   ```bash
   mvn javafx:run
   ```

## Kullanım

### Temel İşlemler
1. **Board Ayarları**: Üst panelden board türü ve boyutunu seçin
2. **Bileşen Seçimi**: Sol panelden bileşenleri seçin
3. **Yerleştirme**: "Add to Board" ile bileşenleri yerleştirin
4. **Düzenleme**: Bileşenleri sürükleyerek yeniden konumlandırın
5. **Bağlantılar**: Tel araçları ile bağlantıları oluşturun

### Gelişmiş Özellikler
- **Izgara Birimleri**: Bileşen boyutlarını ızgara birimlerine göre ayarlayın
- **Yönlendirme**: Bileşenleri 4 farklı yönde yerleştirin
- **Ölçek**: Görünüm ölçeğini %50 ile %200 arasında ayarlayın
- **Seçim**: Bileşenleri seçin ve silin veya düzenleyin

## Geliştirme

Bu proje açık kaynak olarak geliştirilmektedir. Katkılar memnuniyetle karşılanır!

### Katkıda Bulunma
1. Depoyu fork edin
2. Özellik dalı oluşturun (`git checkout -b feature/yeni-ozellik`)
3. Değişikliklerinizi commit edin (`git commit -m 'Yeni özellik ekle'`)
4. Dalı push edin (`git push origin feature/yeni-ozellik`)
5. Pull Request oluşturun

## Lisans

Bu proje MIT Lisansı altında yayınlanmıştır. Detaylar için [LICENSE](LICENSE) dosyasını inceleyin.

## Bağlantılar

- **GitHub**: [https://github.com/faymaz/Levhaci](https://github.com/faymaz/Levhaci)
- **Issues**: Hata raporları ve özellik istekleri için
- **Wiki**: Detaylı dokümantasyon

## Geliştirici

**faymaz** - *Proje yaratıcısı ve baş geliştirici*

---

*Elektronik projelerinizi Levhacı ile tasarlayın*