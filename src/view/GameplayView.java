package view;

import viewmodel.GameplayViewModel;
import javax.swing.*;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class GameplayView extends JFrame implements PropertyChangeListener {

    private GameplayViewModel viewModel;
    private GamePanel gamePanel;

    public GameplayView(String username) {
        // Dapatkan ukuran layar default dari sistem
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = (int) screenSize.getWidth();
        int screenHeight = (int) screenSize.getHeight();

        // Berikan ukuran layar ke ViewModel saat dibuat
        this.viewModel = new GameplayViewModel(username, screenWidth, screenHeight);
        // =================================================================

        this.viewModel.addPropertyChangeListener(this);
        this.gamePanel = new GamePanel(this.viewModel);

        this.setTitle("Gameplay - " + username);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setUndecorated(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.add(this.gamePanel);
    }

    public void startGame() {
        this.setVisible(true);
        this.gamePanel.requestFocusInWindow();
        viewModel.startGame();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("gamestate".equals(evt.getPropertyName())) {
            gamePanel.repaint();
        } else if ("gameover".equals(evt.getPropertyName())) {
            this.dispose();
            MenuPengguna menu = new MenuPengguna();
            menu.tampilMenu();
        }
    }
}