package viewmodel;

import model.Karakter;
import model.Uang;
import model.Jaring;
import model.TabelPengguna;
import model.KantongUang;
import model.HantuApi;
import helpers.AudioPlayer;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JOptionPane;

public class GameplayViewModel implements Runnable {

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private AudioPlayer gameMusic;
    private volatile boolean running = false;
    private volatile boolean paused = false;
    private Thread gameThread;
    private Karakter karakter;
    private Jaring jaring;
    private ArrayList<Uang> listUang;
    private ArrayList<HantuApi> listHantu;
    private KantongUang kantongUang;
    private int karakterDx = 0;
    private int karakterDy = 0;
    private boolean jaringIsMoving = false;
    private boolean jaringIsRetracting = false;
    private double jaringDx = 0;
    private double jaringDy = 0;
    private double maxLassoRange;
    private String username;
    private int score = 0;
    private int count = 0;

    private Uang caughtUang = null;
    private int heldMoneyValue = 0;

    private int screenWidth;
    private int screenHeight;
    private final int GROUND_TOP_Y;
    private final int GROUND_BOTTOM_Y;
    private final int SKY_Y_MAX;
    private final int RIVER_Y_MIN;
    private static final int KARAKTER_SIZE = 180;
    private static final int UANG_SIZE = 80;
    private static final int NET_WIDTH = 40;
    private static final int NET_HEIGHT = 60;
    private static final int MAX_UANG_ON_SCREEN = 15;

    private static final int MAX_HANTU_ON_SCREEN = 5;

    public GameplayViewModel(String username, int screenWidth, int screenHeight) {
        this.username = username;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.GROUND_TOP_Y = (int) (screenHeight * 0.25);
        this.GROUND_BOTTOM_Y = (int) (screenHeight * 0.7);
        this.SKY_Y_MAX = (int) (screenHeight * 0.11);
        this.RIVER_Y_MIN = (int) (screenHeight * 0.75);
        this.maxLassoRange = screenHeight * 0.4;
        initObjects();
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    private void initObjects() {
        int startX = (this.screenWidth / 2) - (KARAKTER_SIZE / 2);
        int movementAreaHeight = (GROUND_BOTTOM_Y - KARAKTER_SIZE) - GROUND_TOP_Y;
        int startY = GROUND_TOP_Y + (movementAreaHeight / 2);
        karakter = new Karakter(startX, startY, KARAKTER_SIZE, KARAKTER_SIZE);
        jaring = new Jaring(0, 0, 0, 0);
        listUang = new ArrayList<>();
        listHantu = new ArrayList<>();
        int kantongWidth = 100;
        int kantongHeight = 100;
        int kantongX = (int) (this.screenWidth * 0.85);
        int kantongY = GROUND_BOTTOM_Y - kantongHeight;
        kantongUang = new KantongUang(kantongX, kantongY, kantongWidth, kantongHeight);
        spawnUang(MAX_UANG_ON_SCREEN);
        spawnHantu(MAX_HANTU_ON_SCREEN);
    }

    private void spawnUang(int amount) {
        Random rand = new Random();
        for (int i = 0; i < amount; i++) {
            int x, y;
            boolean overlaps;
            do {
                overlaps = false;
                x = rand.nextInt(this.screenWidth - UANG_SIZE);
                if (rand.nextBoolean()) {
                    y = rand.nextInt(SKY_Y_MAX);
                } else {
                    y = RIVER_Y_MIN + rand.nextInt(screenHeight - RIVER_Y_MIN - UANG_SIZE);
                }
                Rectangle newUangBounds = new Rectangle(x, y, UANG_SIZE, UANG_SIZE);
                for (Uang existingUang : listUang) {
                    Rectangle existingBounds = new Rectangle(existingUang.getX(), existingUang.getY(), existingUang.getWidth(), existingUang.getHeight());
                    if (newUangBounds.intersects(existingBounds)) {
                        overlaps = true;
                        break;
                    }
                }
            } while (overlaps);
            int value = generateRandomValue();
            listUang.add(new Uang(x, y, UANG_SIZE, UANG_SIZE, value));
        }
    }

    private void spawnHantu(int amount) {
        Random rand = new Random();
        int hantuSize = 50;
        for (int i = 0; i < amount; i++) {
            int x, y;
            boolean overlaps;
            do {
                overlaps = false;
                x = rand.nextInt(this.screenWidth - hantuSize);
                if (rand.nextBoolean()) {
                    y = rand.nextInt(SKY_Y_MAX);
                } else {
                    y = RIVER_Y_MIN + rand.nextInt(screenHeight - RIVER_Y_MIN - hantuSize);
                }

                // Area pengecekan diperbesar untuk memberi jarak aman
                Rectangle checkArea = new Rectangle(x - 20, y - 20, hantuSize + 40, hantuSize + 40);

                for (Uang existingUang : listUang) {
                    if (checkArea.intersects(new Rectangle(existingUang.getX(), existingUang.getY(), existingUang.getWidth(), existingUang.getHeight()))) {
                        overlaps = true;
                        break;
                    }
                }
                if (overlaps) continue;
                for (HantuApi existingHantu : listHantu) {
                    if (checkArea.intersects(new Rectangle(existingHantu.getX(), existingHantu.getY(), existingHantu.getWidth(), existingHantu.getHeight()))) {
                        overlaps = true;
                        break;
                    }
                }
            } while (overlaps);
            listHantu.add(new HantuApi(x, y, hantuSize, hantuSize));
        }
    }

    private int generateRandomValue() {
        Random rand = new Random();
        int chance = rand.nextInt(100);
        if (chance < 50) return 10 + rand.nextInt(21);
        else if (chance < 80) return 40 + rand.nextInt(21);
        else if (chance < 95) return 70 + rand.nextInt(11);
        else return 90 + rand.nextInt(11);
    }

    public void startGame() {
        if (gameThread == null) {
            running = true;
            gameThread = new Thread(this);
            gameThread.start();

            try {
                gameMusic = new AudioPlayer("/assets/music.wav");
                gameMusic.loop(); // Mainkan musik secara berulang
            } catch (Exception e) {
                System.err.println("Gagal memulai musik gameplay.");
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        while (running) {
            try {
                // Logika untuk menjeda thread
                synchronized (this) {
                    while (paused) {
                        wait(); // Thread akan berhenti di sini sampai dibangunkan
                    }
                }
                // Setelah tidak di-pause, baru jalankan update
                update();
                support.firePropertyChange("gamestate", null, null);
                Thread.sleep(17);
            } catch (InterruptedException e) {
                e.printStackTrace();
                running = false; // Hentikan jika ada interupsi
            }
        }
    }

    public synchronized void pause() {
        paused = true;
    }

    public synchronized void resume() {
        paused = false;
        notifyAll(); // Membangunkan thread yang sedang 'wait()'
    }

    private void update() {
        karakter.setX(karakter.getX() + karakterDx);
        karakter.setY(karakter.getY() + karakterDy);
        if (karakter.getX() < 0) karakter.setX(0);
        if (karakter.getX() > this.screenWidth - karakter.getWidth()) karakter.setX(this.screenWidth - karakter.getWidth());
        if (karakter.getY() < GROUND_TOP_Y) {
            karakter.setY(GROUND_TOP_Y);
        }
        if (karakter.getY() + karakter.getHeight() > GROUND_BOTTOM_Y) {
            karakter.setY(GROUND_BOTTOM_Y - karakter.getHeight());
        }
        // Gerakkan uang
        for (Uang uang : listUang) {
            if (!uang.isCaught()) {
                if (uang.getY() < GROUND_TOP_Y) {
                    uang.setX(uang.getX() - 2);
                    if (uang.getX() < -uang.getWidth()) {
                        uang.setX(this.screenWidth);
                    }
                } else {
                    uang.setX(uang.getX() + 2);
                    if (uang.getX() > this.screenWidth) {
                        uang.setX(-uang.getWidth());
                    }
                }
            }
        }
        // Gerakkan hantu
        for (HantuApi hantu : listHantu) {
            if (hantu.getY() < GROUND_TOP_Y) {
                hantu.setX(hantu.getX() - 2);
                if (hantu.getX() < -hantu.getWidth()) {
                    hantu.setX(this.screenWidth);
                }
            } else {
                hantu.setX(hantu.getX() + 2);
                if (hantu.getX() > this.screenWidth) {
                    hantu.setX(-hantu.getWidth());
                }
            }
        }
        if (jaringIsMoving) {
            if (jaringIsRetracting) {
                if (caughtUang != null) {
                    caughtUang.setX(jaring.getX() + (jaring.getWidth()/2) - (caughtUang.getWidth()/2));
                    caughtUang.setY(jaring.getY() + (jaring.getHeight()/2) - (caughtUang.getHeight()/2));
                }
                int krabCenterX = karakter.getX() + 110;
                int krabCenterY = karakter.getY() + 110;
                double angle = Math.atan2(krabCenterY - jaring.getY(), krabCenterX - jaring.getX());
                jaringDx = 15.0 * Math.cos(angle);
                jaringDy = 15.0 * Math.sin(angle);
                if (Math.hypot(jaring.getX() - krabCenterX, jaring.getY() - krabCenterY) < 20) {
                    jaringIsMoving = false;
                    if (caughtUang != null) {
                        karakter.setHoldingMoney(true);
                        this.heldMoneyValue = caughtUang.getValue();
                        listUang.remove(caughtUang);
                        caughtUang = null;
                        System.out.println("Uang ditangan Krab! Bawa ke kantong!");
                    }
                }
            } else {
                int startX = karakter.getX() + 90;
                int startY = karakter.getY() + 50;
                double distance = Math.hypot(jaring.getX() - startX, jaring.getY() - startY);
                if (distance >= maxLassoRange) {
                    jaringIsRetracting = true;
                }
            }
            jaring.setX((int) (jaring.getX() + jaringDx));
            jaring.setY((int) (jaring.getY() + jaringDy));
            if (jaring.getX() < -NET_WIDTH || jaring.getX() > screenWidth || jaring.getY() < -NET_HEIGHT || jaring.getY() > screenHeight) {
                jaringIsMoving = false;
            }
            if (!jaringIsRetracting) {
                checkCollisions();
            }
        }

        // Cek collision pemain dengan hantu
        checkPlayerCollision();

        // Cek kondisi game over karena nyawa habis
        if (karakter.getHealth() <= 0) {
            System.out.println("GAME OVER - Nyawa Habis");
            // Menampilkan pop-up sebelum keluar ke menu
            JOptionPane.showMessageDialog(null, "GAME OVER!", "Permainan Berakhir", JOptionPane.INFORMATION_MESSAGE);
            exitToMenu(); // Panggil metode exit yang sudah ada
            return; // Hentikan proses update lebih lanjut di frame ini
        }

        checkDeposit();
        if (listUang.size() < MAX_UANG_ON_SCREEN) {
            spawnUang(1);
        }

        // Pastikan jumlah hantu tetap maksimal
        if (listHantu.size() < MAX_HANTU_ON_SCREEN) {
            spawnHantu(MAX_HANTU_ON_SCREEN - listHantu.size());
        }
    }

    // method untuk mengecek collision jaring dengan hantu
    private void checkJaringHantuCollision() {
        if (!jaringIsMoving || jaringIsRetracting) return;

        Rectangle jaringBounds = new Rectangle(jaring.getX(), jaring.getY(), jaring.getWidth(), jaring.getHeight());

        // Gunakan iterator untuk menghindari ConcurrentModificationException
        java.util.Iterator<HantuApi> iterator = listHantu.iterator();
        while (iterator.hasNext()) {
            HantuApi hantu = iterator.next();

            // Buat hitbox hantu yang sedikit diperkecil untuk fairness
            int hitboxSize = hantu.getWidth() / 2;
            int hitboxX = hantu.getX() + (hantu.getWidth() - hitboxSize) / 2;
            int hitboxY = hantu.getY() + (hantu.getHeight() - hitboxSize) / 2;
            Rectangle hantuBounds = new Rectangle(hitboxX, hitboxY, hitboxSize, hitboxSize);

            if (jaringBounds.intersects(hantuBounds)) {
                // Kurangi health karakter
                karakter.setHealth(karakter.getHealth() - 20);
                System.out.println("Jaring kena hantu! Damage -20. Sisa nyawa: " + karakter.getHealth());

                // Hapus hantu yang terkena jaring
                iterator.remove();

                // Spawn hantu baru di lokasi random
                spawnHantu(1);

                // Paksa jaring untuk kembali (retracting)
                jaringIsRetracting = true;
                caughtUang = null; // Pastikan tidak ada uang yang tertangkap

                break; // Keluar dari loop karena sudah ada collision
            }
        }
    }


    private void checkPlayerCollision() {
        if (!running) return;

        Rectangle karakterBounds = new Rectangle(karakter.getX(), karakter.getY(), karakter.getWidth(), karakter.getHeight());

        // Gunakan iterator agar aman saat menghapus hantu dari daftar
        java.util.Iterator<HantuApi> iterator = listHantu.iterator();
        while (iterator.hasNext()) {
            HantuApi hantu = iterator.next();

            // Menggunakan hitbox hantu yang diperkecil (lebih adil)
            int hitboxSize = hantu.getWidth() / 2;
            int hitboxX = hantu.getX() + (hantu.getWidth() - hitboxSize) / 2;
            int hitboxY = hantu.getY() + (hantu.getHeight() - hitboxSize) / 2;
            Rectangle hantuBounds = new Rectangle(hitboxX, hitboxY, hitboxSize, hitboxSize);

            // Jika karakter menabrak hantu
            if (karakterBounds.intersects(hantuBounds)) {
                karakter.setHealth(karakter.getHealth() - 20); // Kurangi nyawa
                System.out.println("Kena Hantu! Sisa Nyawa: " + karakter.getHealth());
                iterator.remove(); // Hapus hantu yang menabrak
                spawnHantu(1);     // Langsung munculkan hantu baru di tempat lain
                break; // Hanya proses satu tabrakan per frame untuk menghindari damage beruntun
            }
        }
    }

    private void checkCollisions() {
        if (!jaringIsMoving || caughtUang != null) return;
        Rectangle jaringBounds = new Rectangle(jaring.getX(), jaring.getY(), jaring.getWidth(), jaring.getHeight());
        for (Uang uang : listUang) {
            int hitboxWidth = uang.getWidth() / 2;
            int hitboxHeight = uang.getHeight() / 2;
            int hitboxX = uang.getX() + (uang.getWidth() - hitboxWidth) / 2;
            int hitboxY = uang.getY() + (uang.getHeight() - hitboxHeight) / 2;
            Rectangle uangBounds = new Rectangle(hitboxX, hitboxY, hitboxWidth, hitboxHeight);
            if (jaringBounds.intersects(uangBounds)) {
                jaringIsRetracting = true;
                uang.setCaught(true);
                this.caughtUang = uang;
                System.out.println("Uang Kena!");
                break;
            }
        }

        // Cek collision dengan hantu
        checkJaringHantuCollision();
    }

    private void checkDeposit() {
        if (!karakter.isHoldingMoney()) {
            return;
        }
        Rectangle karakterBounds = new Rectangle(karakter.getX(), karakter.getY(), karakter.getWidth(), karakter.getHeight());

        // Buat hitbox kantong yang 2x lebih besar dari gambarnya
        int enlargedWidth = kantongUang.getWidth() * 2;
        int enlargedHeight = kantongUang.getHeight() * 2;
        // Hitung posisi X dan Y agar hitbox besar ini tetap berpusat di posisi kantong asli
        int enlargedX = kantongUang.getX() - (kantongUang.getWidth() / 2);
        int enlargedY = kantongUang.getY() - (kantongUang.getHeight() / 2);

        Rectangle kantongBounds = new Rectangle(enlargedX, enlargedY, enlargedWidth, enlargedHeight);

        if (karakterBounds.intersects(kantongBounds)) {
            this.score += this.heldMoneyValue;
            this.count++;
            karakter.setHoldingMoney(false);
            this.heldMoneyValue = 0;
            System.out.println("Deposit berhasil! Skor bertambah!");
        }
    }

    public void moveKarakter(int dx, int dy) {
        this.karakterDx = dx;
        this.karakterDy = dy;
    }

    public void stopKarakterHorizontal() {
        this.karakterDx = 0;
    }

    public void stopKarakterVertical() {
        this.karakterDy = 0;
    }

    public void fireJaring(int targetX, int targetY) {
        if (!jaringIsMoving && !karakter.isHoldingMoney()) {
            jaringIsMoving = true;
            jaringIsRetracting = false;
            jaring.setWidth(NET_WIDTH);
            jaring.setHeight(NET_HEIGHT);
            int startX = karakter.getX() + 90;
            int startY = karakter.getY() + 50;
            jaring.setX(startX);
            jaring.setY(startY);
            double angle = Math.atan2(targetY - startY, targetX - startX);
            double speed = 15.0;
            jaringDx = speed * Math.cos(angle);
            jaringDy = speed * Math.sin(angle);
        }
    }

    public void exitToMenu() {
        if (gameMusic != null) {
            gameMusic.stop(); // Hentikan musik sebelum keluar
        }
        running = false;
        resume(); // Bangunkan thread (jika sedang pause) agar bisa berhenti
        try {
            TabelPengguna dao = new TabelPengguna();
            if (dao.isUsernameExists(this.username)) {
                int oldSkor = dao.getSkorByUsername(this.username);
                if (this.score > oldSkor) {
                    dao.updatePengguna(this.username, this.score, this.count);
                    System.out.println("New High Score! Data " + this.username + " diupdate.");
                } else {
                    System.out.println("Score tidak lebih tinggi dari high score sebelumnya.");
                }
            } else {
                dao.insertPengguna(this.username, this.score, this.count);
                System.out.println("Data " + this.username + " berhasil disimpan!");
            }
            dao.closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        support.firePropertyChange("gameover", null, null);
    }

    public Karakter getKarakter() { return karakter; }
    public Jaring getJaring() { return jaring; }
    public KantongUang getKantongUang() { return kantongUang; }
    public boolean isJaringMoving() { return jaringIsMoving; }
    public ArrayList<Uang> getListUang() { return listUang; }
    public int getScore() { return score; }
    public int getCount() { return count; }
    public ArrayList<HantuApi> getListHantu() { return listHantu; }
}