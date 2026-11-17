# Fishing Assistant — itel RS4 / Android 13

**Project sumber Android Studio (Java), prototipe v0.2.**  
Konfigurasi: `minSdk 26`, `compileSdk 35`, `targetSdk 33` (Android 13).

## Penyesuaian itel RS4
- Overlay mengambil ukuran layar aktual saat layanan dimulai, sehingga posisi awal bulatan mengikuti orientasi layar saat itu.
- Disarankan buka game dan ubah ke **landscape terlebih dahulu**, baru tampilkan overlay.
- Bulatan MANCING dan TARIK ditempatkan dekat sisi bawah layar dan bisa digeser ke koordinat tombol game.
- Area deteksi warna hijau memakai persentase ukuran layar, bukan koordinat piksel tetap.
- RAM 8 GB cukup untuk menjalankan prototipe ringan ini; kinerja deteksi tetap bergantung pada game, resolusi, dan beban perangkat.

## Cara membangun APK
1. Ekstrak ZIP.
2. Buka folder ini di Android Studio (JDK 17+, Android SDK 35).
3. Tunggu Gradle Sync.
4. Pilih **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
5. APK debug biasanya tersedia di `app/build/outputs/apk/debug/app-debug.apk`.

## Penggunaan pada HP
1. Instal APK.
2. Buka game dan aktifkan orientasi landscape.
3. Buka Fishing Assistant, izinkan **Display over other apps**.
4. Aktifkan Fishing Assistant pada **Settings > Accessibility**.
5. Kembali ke aplikasi, pilih **Tampilkan tombol melayang**.
6. Geser bulatan biru tepat ke tombol Mancing/Angkat dan bulatan hijau ke tombol Tarik.
7. Pilih **Aktifkan deteksi layar**, lalu setujui dialog berbagi layar Android.
8. Mulai dengan pengawasan. Gunakan STOP jika sentuhan tidak sesuai.

## Batasan penting — belum siap untuk auto-farming tanpa pengujian
- Deteksi saat ini hanya heuristik jumlah piksel hijau dalam area (ROI). Ia belum melacak posisi ikan, batas indikator hijau, kecepatan ikan, atau kondisi ikan berhasil ditangkap secara andal.
- Status Strike saat ini diperkirakan dari kemunculan warna hijau. Efek UI hijau lain dapat memicu false positive.
- Siklus tekan Mancing/Tarik adalah kerangka awal; timing dan koordinat perlu dikalibrasi langsung pada game di perangkat.
- Proyek belum dikompilasi atau diuji pada itel RS4. ZIP ini **bukan APK siap instal**.
- Android dapat menghentikan screen capture/overlay ketika izin dicabut, layar terkunci, atau sistem menghemat baterai.
- Aplikasi tidak membaca memori game atau memodifikasi game.

## Langkah kalibrasi berikutnya
Diperlukan satu screenshot game saat menunggu Strike dan satu screenshot saat mini-game Tarik aktif dari itel RS4 (landscape). Dari situ ROI dan logika pelacakan indikator dapat disetel berdasarkan UI yang benar-benar tampil.
