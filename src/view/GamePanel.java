package view;

import model.Karakter;
import model.KantongUang;
import model.Uang;
import model.HantuApi;
import viewmodel.GameplayViewModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GamePanel extends JPanel {

    private GameplayViewModel viewModel;
    private Image krabActionImage;
    private Image krabRelaxImage;
    private Image uangImage;
    private Image gagangJaringImage;
    private Image kepalaJaringImage;
    private Image backgroundImage;
    private Image kantongUangImage;
    private Image hantuImage;
    private Font pointFont;

    public GamePanel(GameplayViewModel viewModel) {
        this.viewModel = viewModel;
        this.pointFont = new Font("Arial", Font.BOLD, 16);
        loadAssets();
        setFocusable(true);
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                viewModel.fireJaring(e.getX(), e.getY());
            }
        });
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // Jika tombol yang ditekan adalah Spasi
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    viewModel.pause();

                    // Siapkan tombol dan pesan untuk pop-up
                    String[] options = {"Lanjut", "Kembali ke Menu"};
                    JLabel title = new JLabel("Game Paused", SwingConstants.CENTER);
                    title.setFont(new Font("Arial", Font.BOLD, 24));

                    // Tampilkan pop-up
                    int choice = JOptionPane.showOptionDialog(
                            GamePanel.this,
                            title,
                            "Pause Menu",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.PLAIN_MESSAGE,
                            null,
                            options,
                            options[0]
                    );

                    // Lakukan aksi berdasarkan pilihan user
                    if (choice == 0 || choice == JOptionPane.CLOSED_OPTION) { // "Lanjut" atau menutup dialog
                        viewModel.resume();
                    } else if (choice == 1) { // "Kembali ke Menu"
                        viewModel.exitToMenu();
                    }
                } else {
                    // Logika pergerakan
                    int speed = 5;
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_LEFT: viewModel.moveKarakter(-speed, 0); break;
                        case KeyEvent.VK_RIGHT: viewModel.moveKarakter(speed, 0); break;
                        case KeyEvent.VK_UP: viewModel.moveKarakter(0, -speed); break;
                        case KeyEvent.VK_DOWN: viewModel.moveKarakter(0, speed); break;
                    }
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_RIGHT) viewModel.stopKarakterHorizontal();
                if (key == KeyEvent.VK_UP || key == KeyEvent.VK_DOWN) viewModel.stopKarakterVertical();
            }
        });
    }

    private void loadAssets() {
        try {
            krabActionImage = new ImageIcon(getClass().getResource("/assets/CrabAction.png")).getImage();
            krabRelaxImage = new ImageIcon(getClass().getResource("/assets/CrabRelax.png")).getImage();
            uangImage = new ImageIcon(getClass().getResource("/assets/UangTerbang.png")).getImage();
            backgroundImage = new ImageIcon(getClass().getResource("/assets/MainBackground.png")).getImage();
            gagangJaringImage = new ImageIcon(getClass().getResource("/assets/gagang_jaring.png")).getImage();
            kepalaJaringImage = new ImageIcon(getClass().getResource("/assets/kepala_jaring.png")).getImage();
            kantongUangImage = new ImageIcon(getClass().getResource("/assets/KantongUang.png")).getImage();
            hantuImage = new ImageIcon(getClass().getResource("/assets/hantu_api.png")).getImage();
        } catch (Exception e) {
            System.err.println("Error loading assets: Pastikan semua file aset sudah ada, termasuk 'CrabRelax.png'.");
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (viewModel == null || viewModel.getKarakter() == null || viewModel.getListUang() == null || viewModel.getKantongUang() == null || viewModel.getListHantu() == null) return;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        Karakter karakter = viewModel.getKarakter();

        g2d.setFont(pointFont);
        for (Uang uang : viewModel.getListUang()) {
            g2d.drawImage(uangImage, uang.getX(), uang.getY(), uang.getWidth(), uang.getHeight(), this);
            String points = String.valueOf(uang.getValue());
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(points);
            int x = uang.getX() + (uang.getWidth() / 2) - (textWidth / 2);
            int y = uang.getY() - 5;
            g2d.setColor(Color.BLACK); g2d.drawString(points, x + 1, y + 1);
            g2d.setColor(Color.YELLOW); g2d.drawString(points, x, y);
        }

        // Gambar Hantu
        for (HantuApi hantu : viewModel.getListHantu()) {
            g2d.drawImage(hantuImage, hantu.getX(), hantu.getY(), hantu.getWidth(), hantu.getHeight(), this);
        }

        KantongUang kantong = viewModel.getKantongUang();
        // Menggambar kantong uang
        g2d.drawImage(kantongUangImage, kantong.getX(), kantong.getY(), kantong.getWidth(), kantong.getHeight(), this);

        // Pilih gambar Tuan Krab yang sesuai
        Image currentKrabImage;
        if (karakter.isHoldingMoney()) {
            currentKrabImage = krabRelaxImage;
        } else {
            currentKrabImage = krabActionImage;
        }

        // Ambil posisi Tuan Krab
        int karakterX = karakter.getX();
        int karakterY = karakter.getY();

        // Menggunakan posisi anchor dan ukuran jaring
        int GAGANG_WIDTH = 40;
        int GAGANG_HEIGHT = 60;
        int KEPALA_WIDTH = 40;
        int KEPALA_HEIGHT = 60;
        int anchorX = karakterX + 45;
        int anchorY = karakterY + 6;

        if (viewModel.isJaringMoving()) {
            int jaringX = viewModel.getJaring().getX();
            int jaringY = viewModel.getJaring().getY();
            int jaringCenterX = jaringX + KEPALA_WIDTH / 2;
            int jaringCenterY = jaringY + KEPALA_HEIGHT / 2;
            int gagangTipX = anchorX + 40;
            int gagangTipY = anchorY;

            // Menggambar Tuan Krab dan jaring dengan urutan layering
            g2d.drawImage(gagangJaringImage, anchorX, anchorY, GAGANG_WIDTH, GAGANG_HEIGHT, this);
            g2d.setColor(new Color(101, 67, 33, 200));
            g2d.setStroke(new BasicStroke(5));
            g2d.drawLine(gagangTipX, gagangTipY, jaringCenterX, jaringCenterY);
            g2d.drawImage(currentKrabImage, karakterX, karakterY, karakter.getWidth(), karakter.getHeight(), this);
            g2d.drawImage(kepalaJaringImage, jaringX, jaringY, KEPALA_WIDTH, KEPALA_HEIGHT, this);
        } else {
            // Menggambar Tuan Krab dan jaringnya saat diam
            if (!karakter.isHoldingMoney()) {
                g2d.drawImage(gagangJaringImage, anchorX, anchorY, GAGANG_WIDTH, GAGANG_HEIGHT, this);
                int gagangTipX = anchorX + 40;
                int kepalaIdleX = gagangTipX - KEPALA_WIDTH / 2 - 3;
                int kepalaIdleY = anchorY - GAGANG_HEIGHT + 20;
                g2d.drawImage(kepalaJaringImage, kepalaIdleX, kepalaIdleY, KEPALA_WIDTH, KEPALA_HEIGHT, this);
            }
            g2d.drawImage(currentKrabImage, karakterX, karakterY, karakter.getWidth(), karakter.getHeight(), this);
        }

        // Menggunakan posisi dan warna teks skor
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 17));
        g2d.drawString("Score: " + viewModel.getScore(), kantong.getX() - 135, kantong.getY() - 160);
        g2d.drawString("Count: " + viewModel.getCount(), kantong.getX() - 135, kantong.getY() - 130);

        // Gambar Health Bar
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 17));
        g2d.drawString("Health:", kantong.getX() + 38, kantong.getY() - 170);

        // Latar belakang bar
        g2d.setColor(Color.RED);
        g2d.fillRect(kantong.getX() + 40, kantong.getY() - 165, 104, 24);

        // Bar nyawa
        g2d.setColor(Color.GREEN);
        int healthBarWidth = (int) (100 * (karakter.getHealth() / 100.0));
        if (healthBarWidth < 0) healthBarWidth = 0; // Pastikan tidak negatif
        g2d.fillRect(kantong.getX() + 42, kantong.getY() - 163, healthBarWidth, 20);
    }
}