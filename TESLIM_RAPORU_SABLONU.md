# Teslim Raporu — Eşzamanlı Kripto Fiyat Simülatörü

> Reponun kökünde `TESLIM_RAPORU.md` olarak bulunmalıdır. `<...>` yerlerini doldurun.
> Amaç: değerlendirenin projeyi hızlıca gözden geçirebilmesi.

## 1. Grup Bilgileri

| Alan | Bilgi |
|---|---|
| Grup adı | Phome |
| Grup üyeleri (5) | <Yusuf GÜNEŞ, GPT> |
| GitHub repo linki | https://github.com/yusufgunes58/pricesim |
| Pull Request linkleri |https://github.com/yusufgunes58/pricesim/pull/1 , https://github.com/yusufgunes58/pricesim/pull/2  |
| Conflict çözülen dosya | XXX |
| Conflict çözüm commit / PR | XXX |
| Yapılan bonus (varsa) | <virtual threads / CompletableFuture / deadlock / yok> | YOK. .XX 

## 2. Kısa Açıklama

<Projenin ne yaptığını 2-3 cümleyle özetleyin.>

## 3. Çalıştırma (özet)

```
git clone <repo-linki>
cd <proje>
mvn spring-boot:run
# Swagger: http://localhost:8080/swagger-ui/index.html
# Deneyin: POST /simulate?updates=10000&workers=4&seed=42  ->  GET /stats
```

## 4. Tasarım Kararları (özet)

<README'deki 9 satırlık tabloyu 4-5 satırla özetleyin: kuyruk, worker havuzu, coin kilidi,
lock kapsamı, tamamlanma/shutdown.>

## 5. Race Condition Kanıtı

```
BTC beklenen 61240 | güvenli 61240 | güvensiz 60890
Sayaç beklenen 10000 | güvenli 10000 | güvensiz 9784
safeInvariantPassed: true
```

## 6. Metrik Özeti

| Updates | Workers | Süre | Throughput |
|---:|---:|---:|---:|
| 50.000 | 1 | <...> | <...> |
| 50.000 | 4 | <...> | <...> |
| 50.000 | 8 | <...> | <...> |

## 7. Thread Dump Özeti

<Simülasyon sırasında alınan thread dump'tan kısa gözlem: kaç worker, hangi state'ler,
çekişme/deadlock durumu. Ayrıntı README'de.>

## 8. Zorunlu Özellikler — Öz Değerlendirme

- [ ] /simulate, /coins, /stats çalışıyor
- [ ] Geçersiz parametre → HTTP 400, ikinci eşzamanlı istek → HTTP 409
- [ ] Aynı görev listesi (immutable, tek üretim) safe ve unsafe'de kullanılıyor
- [ ] BlockingQueue + sabit thread pool (her görev için yeni thread yok)
- [ ] Güvensiz sürüm hatayı gösteriyor; güvenli sürüm invariant'ı sağlıyor
- [ ] En az bir yerde ReentrantLock kullanıldı
- [ ] Graceful shutdown; işlerin bitmesi bekleniyor
- [ ] Seed ile tekrarlanabilir görev üretimi
- [ ] throughput/süre + 1/2/4/8 worker tablosu
- [ ] Thread dump alındı ve README'de yorumlandı
- [ ] Swagger çalışıyor, adres README'de
- [ ] Unit + en az 1 integration test
- [ ] En az 3 branch, 2 PR, 2 review, 1 çözülmüş conflict

## 9. Bireysel Katkı Tablosu

| Üye | Rol / Ne yaptı? | Branch | PR | Review |
|---|---|---|---|---|
| <Ad> | Coin & State | <...> | <...> | <...> |
| <Ad> | Worker Pool | <...> | <...> | <...> |
| <Ad> | Invariant | <...> | <...> | <...> |
| <Ad> | API & Swagger | <...> | <...> | <...> |
| <Ad> | Metrik & Test | <...> | <...> | <...> |

## 10. Notlar (opsiyonel)

<Zorlandığınız yerler, karar tartışmaları, gelecekte ne eklerdiniz.>
