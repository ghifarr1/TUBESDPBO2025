package view;

import viewmodel.ProsesPengguna;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class MenuPengguna extends JFrame {

    private ProsesPengguna prosespengguna;
    private JTable table;
    private DefaultTableModel tableModel;
    private BufferedImage backgroundImage;
    private BufferedImage crabImage;

    public MenuPengguna() {
        prosespengguna = new ProsesPengguna();
        loadAssets();
        initComponents();
    }

    private void loadAssets() {
        try {
            // Path untuk assets di dalam src
            backgroundImage = ImageIO.read(new File("src/assets/MainBackground.png"));
            crabImage = ImageIO.read(new File("src/assets/CrabRelax.png"));
            System.out.println("Assets loaded successfully!");
        } catch (IOException e) {
            System.err.println("Error loading assets: " + e.getMessage());
            try {
                backgroundImage = ImageIO.read(new File("assets/MainBackground.png"));
                crabImage = ImageIO.read(new File("assets/CrabRelax.png"));
                System.out.println("Assets loaded from alternative path!");
            } catch (IOException e2) {
                System.err.println("Could not load assets from any path: " + e2.getMessage());
            }
        }
    }

    private void initComponents() {
        setTitle("Crab the Kikir");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main Panel dengan background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
                } else {
                    // Fallback gradient background
                    Graphics2D g2d = (Graphics2D) g;
                    GradientPaint gradient = new GradientPaint(0, 0, new Color(135, 206, 250),
                            0, getHeight(), new Color(25, 25, 112));
                    g2d.setPaint(gradient);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        mainPanel.setLayout(null);
        setContentPane(mainPanel);

        // Title
        JLabel titleLabel = new JLabel("CRAB THE KIKIR", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(new Color(255, 215, 0));
        titleLabel.setBounds(200, 30, 600, 50);
        mainPanel.add(titleLabel);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Welcome to the Greatest Money Adventure!", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 16));
        subtitleLabel.setForeground(Color.WHITE);
        subtitleLabel.setBounds(200, 80, 600, 30);
        mainPanel.add(subtitleLabel);

        // Crab Image
        JLabel crabLabel = new JLabel();
        if (crabImage != null) {
            ImageIcon crabIcon = new ImageIcon(crabImage.getScaledInstance(120, 120, Image.SCALE_SMOOTH));
            crabLabel.setIcon(crabIcon);
        } else {
            crabLabel.setText("🦀");
            crabLabel.setFont(new Font("Arial", Font.BOLD, 72));
            crabLabel.setHorizontalAlignment(SwingConstants.CENTER);
        }
        crabLabel.setBounds(50, 120, 120, 120);
        mainPanel.add(crabLabel);

        // Input Panel
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());
        inputPanel.setBounds(200, 150, 600, 60);
        inputPanel.setBackground(new Color(255, 255, 255, 180));
        inputPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        mainPanel.add(inputPanel);

        // Username components
        JLabel userLabel = new JLabel("Captain's Name:");
        userLabel.setFont(new Font("Arial", Font.BOLD, 14));
        inputPanel.add(userLabel);

        JTextField usernameField = new JTextField(15);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        inputPanel.add(usernameField);

        JButton playButton = new JButton("PLAY");
        playButton.setFont(new Font("Arial", Font.BOLD, 14));
        playButton.setBackground(new Color(34, 139, 34));
        playButton.setForeground(Color.WHITE);
        playButton.setFocusPainted(false);
        inputPanel.add(playButton);

        JButton quitButton = new JButton("QUIT");
        quitButton.setFont(new Font("Arial", Font.BOLD, 14));
        quitButton.setBackground(new Color(220, 20, 60));
        quitButton.setForeground(Color.WHITE);
        quitButton.setFocusPainted(false);
        inputPanel.add(quitButton);

        // Table Panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBounds(50, 250, 900, 350);
        tablePanel.setBackground(new Color(255, 255, 255, 200));
        tablePanel.setBorder(BorderFactory.createTitledBorder("HALL OF FAME - TOP TREASURE HUNTERS"));
        mainPanel.add(tablePanel);

        // Table
        String[] columnNames = {"Username", "Score", "Count"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setRowHeight(22);
        table.getTableHeader().setBackground(new Color(255, 215, 0));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Button Actions
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText().trim();
                if (username.isEmpty()) {
                    JOptionPane.showMessageDialog(MenuPengguna.this,
                            "Ahoy Captain! Please enter your name!",
                            "Name Required", JOptionPane.WARNING_MESSAGE);
                } else {
                    int option = JOptionPane.showConfirmDialog(MenuPengguna.this,
                            "Ready to start the adventure, Captain " + username + "?",
                            "Start Game", JOptionPane.YES_NO_OPTION);
                    if (option == JOptionPane.YES_OPTION) {
                        dispose();
                        GameplayView gameplayView = new GameplayView(username);
                        gameplayView.startGame();
                    }
                }
            }
        });

        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int option = JOptionPane.showConfirmDialog(MenuPengguna.this,
                        "Are you sure you want to quit?",
                        "Quit Game", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });
    }

    public void tampilMenu() {
        try {
            prosespengguna.prosesDataPengguna();
            tableModel.setRowCount(0);
            for (int i = 0; i < prosespengguna.getSize(); i++) {
                Object[] rowData = {
                        prosespengguna.getUsername(i),
                        prosespengguna.getSkor(i),
                        prosespengguna.getCount(i)
                };
                tableModel.addRow(rowData);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading data: " + prosespengguna.getError(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        this.setVisible(true);
    }
}