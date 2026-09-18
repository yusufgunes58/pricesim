# pricesim
Concurrent Crypto Price Simulator

# Eşzamanlı Kripto Fiyat Simülatörü

> Bu bir **şablondur**. `<...>` yerlerini doldurun, açıklama satırlarını (bu blok dahil) silin.
> Yönergedeki başlıkların tümü bulunmalıdır. En kritik bölümler:
> **Tasarım Kararları**, **Race Condition Gözlemi**, **Invariant**, **Thread Dump İncelemesi**.

## Proje Hakkında
Coin bilgileri üzerinde değişiklik yapılan bir "concurrency" simülasyonu. Coin states değişikliği için kullanıcıdan görev sayısı alınır ve belirtilen sayıda worker bir pool içinde ArrayBlockedQueue ile sırasıyla görevleri işler. Son 5 simülasyon bilgileri tutulması için bir "Deque" oluşturulur fakat bu bilgiler henüz kullanıcıya verilmemiştir.

## Kullanılan Teknolojiler

Java 21, Spring Boot, Maven, Git/GitHub, Swagger/OpenAPI, JUnit 5(not ready).

## Uygulamayı Çalıştırma

# IntelliJ IDEA / Maven ile
1. git clone https://github.com/yusufgunes58/pricesim.git
2. Projeyi IntelliJ IDEA ile açın (Open → pom.xml).
3. PricesimApplication sınıfını çalıştırın.

# Docker Compose ile
Docker kuruluysa, Java veya Maven kurulumu gerekmeden:
1. git clone https://github.com/yusufgunes58/pricesim.git
2. cd pricesim
3. docker compose up --build
4. 
## Swagger Adresi

- http://localhost:8080/swagger-ui/index.html
- Endpoint'ler Swagger üzerinden test edilebilir.

## Endpoint'ler

| Endpoint | Ne yapar? |
|---|---|
| `POST /simulate?updates=10000&workers=4&seed=42` | Görevleri üretir, kuyruğa koyar, havuzla işler, biter. 409: aynı anda ikinci istek. 400: geçersiz parametre. |
| `GET /coins` | Son simülasyondaki güvenli coin durumları. |
| `GET /stats` | Son simülasyon sonucu (beklenen/güvensiz/güvenli, süre, throughput, invariant). 404: henüz simülasyon yok. |

## Mimari Akış

```
Task Producer → BlockingQueue<PriceUpdateTask> → Sabit Worker Pool (N) → Coin State + Sayaçlar
```
api->
    controller->
        SimulationController: Simülasyonu başlatmak için kullanılır. Thread ve Task sayısı alınır. Seed isteğe bağlıdır.
        CoinController: Coin ekleme ve listeleme işlemleri içindir. 
        StatsController: Son simülasyon sonuçlarını döndürür.
    dto->
        request->
            SimulateRequest->Simülasyon isteği için gerekenleri kaydetmektedir./record
        response->
            SimulationResponse->Simülasyon bilgilerini tutmaktadır./record
    exception->
        ErrorResponse->Hata cevaplarını tutan "record".
        GlobalExceptionHandler-> Hata mesajlarını döndürmek için yaratılmış handler.
        SimulationConflictException-> 409 Conflict için özel yazdırılmak istenen sınıf.
        SimulationNotFoundException-> Gerekli bilgiler bulunmazsa dönülecek.
    counter->
        ICounter->Ortak fonksiyonlar yaratılmış bir interface.
        SafeCounter-> Güvenilir sayaç kontrolü için yaratılan sınıf.
        UnsafeCounter->Race Condition durumunda güncelleme sayısını gösteriyor.
    ...
    PriceWorker-> Kuyruktaki görevleri işlemektedir. Consumer kısmını yansıtır. /Runnable

    SimulationLcck-> Simülasyon conflict'i önlemek için.

    TaskGenerator-> Seçilen seed ve random coin'e göre PriceUpdateTask yaratır.

    TaskQueue-> Updates bilgisini aldığımız için ArrayBlockedQueue kullanıldı ki sureklı yenı node olusturmayız.

    TaskRandomizer-> Random coin ve delta seçmek için. Eğer seed girilmediyse random seed verir.

   SimCoordinator->Yapılan işlemleri burada methodları döndürecek sağlarız. Kullanıcıya dönecek mesajlar vs. burada oluşturulur.

   SimulationResultStore: Son 5 simülasyon bilgisi "deque" oluşturarak tutarız.
   
   CoinService: Default olarak 3 coin oluşturmak için. Yeni coin eklense bile SimCoordinator de simülasyon işleminde coinleri tutan map default olarak gömülen 3 coin haricinde sıfırlanacaktır.

   ...


## ⭐ Tasarım Kararları

> Yönergedeki 9 karar noktasının her biri için aracınızı ve **kısa "neden"ini** yazın.

| Karar noktası | Kararımız | Neden? (+alternatif karşılaştırması) |
|---|---|---|
| Görev kuyruğu | ArrayBlockingQueue(updates) | Görev sayısı net bir şekilde belli olduğu için seçtik. Linked ile ek node maliyeti oluyor ve gelecekte yüklü updates ler için memory maliyetini önemsedim. |
| Worker havuzu | FixedThreadPool(workers)| Workers kontrol etmek için ve memory maliyeti açısından.. |
| Güvenli sayaç |  AtomicLong | tek bir işlem olacağı AtomicLong güvenilir bir sonuç verecek. CAS ile çalıştığı için biraz daha maliyetli..   |
| Coin kilidi | ReentrantLock | synchronized ile threadslar sonsuz beklemeye gırebılır fakat Reentrant ile tryLock kullanımıyla lock durumu ayarlanabılır. |
| Lock kapsamı | coin başına | Farklı coinler eşzamanlı olarak işlemek için  |
| İşlerin tamamlanması | shutDown + awaitTermination | Threadler işlemlerini bitirdiğinde kapanır eğer uzun sürerse(+1 min) kapatması için interrupt veriyor. |
| Graceful shutdown | <shutdown + awaitTermination> | interrupt yollamaktadır.  |
| Sonucun paylaşılması | DTO | Snapshot sınıfı oluşturmuştum en son bakacaktım fakat DTO ile basitçe oluşturdum. Snapshot da immutable bir şekilde güncelleyeceğim. |
| İkinci simülasyon isteği | <AtomicBoolean> | <finally ile serbest bırakma> Diğer workerlara engel olmamak için setleniyor. |

## Race Condition Gözlemi

<İki race noktasını açıklayın: (1) sayaç `count++` oku-artır-yaz; (2) coin state çok alanlı
tutarsızlık. Neden güncelleme kayboluyor?>

```
BTC   beklenen: 61.240 | güvenli: 61.240 ✓ | güvensiz: 60.890 ✗
Sayaç beklenen: 10.000 | güvenli: 10.000 ✓ | güvensiz: 9.784  ✗
```

> Gözlem: <kaç çalıştırmanın kaçında güvensiz bozuldu? worker/görev sayısını artırınca ne oldu?>
Worker sayısı artınca unsafe işlemlerde verimlilik ciddi derece de düştü ve unsafe coin güncellemerinin yoksayılması arttı. 

## Güvenli Çözüm

<Coin state'i neyle koruduğunuzu ve neden tek başına AtomicLong'un yetmediğini açıklayın.>
Reentrant ile esnek lock kullandım bu sayede birden fazla işlem yapacağım alanı kilitleyip diğer workerlerın da sırasıyla erişebilmesini sağlamak istedim. Eğer tek bir işlem gerçekleşiyorsa AtomicLong biraz daha hızlıdır.

```java
lock.lock();
try {
    currentPrice += delta;
    updateCount++;
    lastDelta = delta;
    lastUpdatedBy = Thread.currentThread().getName();
} finally {
    lock.unlock();
}
```

## Invariant ve Doğruluk Kanıtı

```
safePrice       == initialPrice + sum(all deltas)   ->  <geçti/geçmedi>
safeUpdateCount == o coin için üretilen görev sayısı ->  <geçti/geçmedi>
InvariantChecker safe coin state ile karşılaştırır. ExpectedCoinResult ile hesaplanır. 
```

## Performans Sonuçları
// seed = 42
Updates  Workers  Time(unsafe/safe ms)   Throughput(Safe/Unsafe per sec)  Invariant 
100000      1	  	5 / 6       	        20.0M / 16.6M 	     	       true
100000      2	    7 / 8    		        14.2M / 12.5M	      	       true
100000	    4   	8 / 10	    	        12.4M / 10.0M	               true
100000      8		8 / 10		            12.5M / 10.0M	               true

<Yorum: worker artınca ne oldu? Bir noktadan sonra neden hızlanmadı (lock contention / context switch)?>
Thread ları lock ile sırada beklettiğimiz için time artmaktadır ve bu durum verimliliği düşürmektedir. 
Tabii daha fazla worker daha fazla işlemin kaydedilip beklenmesi demek olduğu için context switch de ciddi derece de CPU yu harcamaktadır.

## ReentrantLock ve synchronized Karşılaştırması

<Nerede hangisini kullandınız? ReentrantLock'un sağladığı ekstralar (tryLock, adalet, kesintiye
uğrayabilir kilitleme) sizin için gerekli miydi? Global lock vs coin başına lock farkı.>

## Thread Dump İncelemesi

<Simülasyon çalışırken bir thread dump alın (IntelliJ "Capture Thread Dump" / jstack / jcmd).
Kısa bir kesit yapıştırın ve yorumlayın.>
Simülasyon sırasında dump alabilmek için sleep ekledim fakat bu seferde açılan her worker TIMED_WAITING oluyor. Worker isimlendirmem : CoinSim Worker-1. İstenilen sayıda worker oluşturuluyor! 

```
"worker-1" ... RUNNABLE ...
"worker-2" ... WAITING (parking) ... at ...BlockingQueue.take(...)
...
```

- **Kaç worker var?** parametreyle uyumlu sayıda worker oluşturuluyor.
- **Hangi state'teler?** <boş worker'lar take() üzerinde WAITING mi> XXXXXXXXXXXXXXXXX /look
- **Lock çekişmesi/deadlock var mı?** <BLOCKED thread'ler / "Found one Java-level deadlock" var mı> - Yok.

## Merge Conflict Deneyimi

- **Branch isimleri:** <...>
- **Conflict çıkan dosya / bölüm:** <...>
- **İki branch'in farklı değişikliği:** <...>
- **Hangi içerik korundu:** <...>
- **IntelliJ mi terminal mi:** <...>
- **Çözüm commit / PR linki:** <...>
- **Ne öğrendik:** <...>

XXXXXXXXXXXXXXXX


## Testler

<Hangi testler var? Nasıl çalıştırılır (`mvn test`)? Kısa liste.>

- Seed tekrarlanabilirlik testi
- Beklenen fiyat hesabı testi
- Güvenli sayaç testi
- Coin invariant testi
- Parametre validation (HTTP 400) testi
- Controller integration testi

## Grup Üyeleri ve Katkıları

| Üye | Sorumluluk | Branch | Pull Request | Review |
|---|---|---|---|---|
| Yusuf GÜNEŞ | ? | main, feature/code-refactor, feature/simulation-core, feature/simulation-metrics | https://github.com/yusufgunes58/pricesim/pull/1 , https://github.com/yusufgunes58/pricesim/pull/2 | İdkXXXXXXXXXX

## Bonus Çalışmalar

<Yaptıysanız: Java 21 Virtual Threads karşılaştırması / CompletableFuture / Deadlock + lock
ordering. Neyi nasıl yaptığınızı ve sonucu kısaca anlatın. Yapmadıysanız "Yok" yazın.>
Yok.


# about branch & commit
Commit ve branchlar isimlendirmelere ve basit grup çalışmasına dikkat etmek amacıyla yaratılmıştır. Kurulan branchlerde oluşacak değişikler sadece main branchi üzerinde çalışılmamasını göstermektedir. Merge Conflict de deneyimlenecektir. Özel sebeplerden gece saatlerinde hatta kütüpte sabahlayıp hazırladım. XXX read again XX

