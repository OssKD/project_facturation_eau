package module;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.Timer;

import java.awt.RenderingHints;

public class PasswordResetPanel extends JPanel {
    private JTextField emailField;
    private JPasswordField ancienPasswordField, newPasswordField, confirmPasswordField;
    private JButton resetButton, backButton;
    private ArrayList<WaterDrop> waterDrops;
    private Timer animationTimer;
    private final int MAX_DROPS = 30;
    private final Color BUTTON_COLOR = new Color(0, 119, 182);
    private final Color BUTTON_HOVER = new Color(3, 82, 123);
    
    // Instance de SendMailExample
    private SendMailExample mailSender = new SendMailExample();

    public PasswordResetPanel(JFrame frame) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        initWaterAnimation();

        JPanel formPanel = createFormPanel(frame);
        add(new WaterAnimationPanel(), BorderLayout.CENTER);
        add(formPanel, BorderLayout.EAST);
    }

    private void initWaterAnimation() {
        waterDrops = new ArrayList<>();
        for (int i = 0; i < MAX_DROPS; i++) {
            createNewDrop();
        }
        animationTimer = new Timer(50, e -> {
            for (int i = 0; i < waterDrops.size(); i++) {
                WaterDrop drop = waterDrops.get(i);
                drop.move();
                if (drop.y > getHeight()) {
                    waterDrops.remove(i);
                    createNewDrop();
                }
            }
            repaint();
        });
        animationTimer.start();
    }

    private void createNewDrop() {
        int x = (int) (Math.random() * getWidth());
        int y = - (int) (Math.random() * 200);
        int size = (int) (Math.random() * 20) + 10;
        float speed = (float) (Math.random() * 2 + 1);
        float alpha = (float) (Math.random() * 0.5 + 0.3);
        waterDrops.add(new WaterDrop(x, y, size, speed, alpha));
    }

    private JPanel createFormPanel(JFrame frame) {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(255, 255, 255, 220));
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Réinitialisation du mot de passe");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 10, 30, 10);
        formPanel.add(titleLabel, gbc);

        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 10, 5, 10);
        gbc.gridy++;
        formPanel.add(new JLabel("Email:"), gbc);
        emailField = createStyledTextField();
        gbc.gridy++;
        gbc.insets = new Insets(0, 10, 15, 10);
        formPanel.add(emailField, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(10, 10, 5, 10);
        formPanel.add(new JLabel("Ancien mot de passe:"), gbc);
        ancienPasswordField = createStyledPasswordField();
        gbc.gridy++;
        gbc.insets = new Insets(0, 10, 15, 10);
        formPanel.add(ancienPasswordField, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(10, 10, 5, 10);
        formPanel.add(new JLabel("Nouveau mot de passe:"), gbc);
        newPasswordField = createStyledPasswordField();
        gbc.gridy++;
        gbc.insets = new Insets(0, 10, 15, 10);
        formPanel.add(newPasswordField, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(10, 10, 5, 10);
        formPanel.add(new JLabel("Confirmer mot de passe:"), gbc);
        confirmPasswordField = createStyledPasswordField();
        gbc.gridy++;
        gbc.insets = new Insets(0, 10, 20, 10);
        formPanel.add(confirmPasswordField, gbc);

        resetButton = createStyledButton("Changer le mot de passe");
        gbc.gridy++;
        formPanel.add(resetButton, gbc);

        backButton = createStyledButton("Retour à la connexion");
        gbc.gridy++;
        formPanel.add(backButton, gbc);

        resetButton.addActionListener(e -> handleReset());
        backButton.addActionListener(e -> {
            frame.setContentPane(new LoginPanel(frame));
            frame.revalidate();
            frame.repaint();
        });

        return formPanel;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField(20);
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setMargin(new Insets(10, 10, 10, 10));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        return field;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField(20);
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setMargin(new Insets(10, 10, 10, 10));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        return field;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(BUTTON_COLOR);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMargin(new Insets(10, 10, 10, 10));
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(BUTTON_HOVER);
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(BUTTON_COLOR);
            }
        });
        return button;
    }

    private void handleReset() {
        String email = emailField.getText();
        String ancienPassword = new String(ancienPasswordField.getPassword());
        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        // Validation des champs
        if (email.isEmpty() || ancienPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showErrorMessage("Veuillez remplir tous les champs.");
            return;
        }

        if (!email.contains("@")) {
            showErrorMessage("Email invalide.");
            return;
        }

        if (newPassword.length() < 6) {
            showErrorMessage("Le mot de passe doit contenir au moins 6 caractères.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showErrorMessage("La confirmation du mot de passe est incorrecte.");
            return;
        }

        // Logique pour la mise à jour dans la base de données
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/javaswing_app", "root", "");
             PreparedStatement checkStmt = conn.prepareStatement("SELECT * FROM user WHERE email = ? AND password = ?")) {
            checkStmt.setString(1, email);
            checkStmt.setString(2, encryptPassword(ancienPassword));
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                int option = JOptionPane.showConfirmDialog(this,
                        "Voulez-vous changer votre mot de passe ?",
                        "Confirmation",
                        JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    try (PreparedStatement updateStmt = conn.prepareStatement("UPDATE user SET password = ? WHERE email = ?")) {
                        updateStmt.setString(1, encryptPassword(newPassword));
                        updateStmt.setString(2, email);
                        updateStmt.executeUpdate();
                        //envoyer message a email
                        String title="Réinitialisation de mot de passe";
                        String message="Votre mot de passe a été mis à jour avec succès.";
                        JOptionPane.showMessageDialog(this, "Mot de passe mis à jour avec succès !");
                        mailSender.sendRecoveryEmail(email,title,message); // Appeller la méthode pour envoyer l'email
                    }
                }
            } else {
                showErrorMessage("Aucun compte trouvé avec cet email ou mot de passe incorrect.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            showErrorMessage("Erreur lors de la connexion à la base de données.");
        }
    }

    private String encryptPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    // Classe de goutte d'eau pour l'animation
    private class WaterDrop {
        private int x, y, size;
        private float speed, alpha;

        public WaterDrop(int x, int y, int size, float speed, float alpha) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.speed = speed;
            this.alpha = alpha;
        }

        public void move() {
            y += speed;
            x += Math.sin(y * 0.05) * 0.5;
        }

        public void draw(Graphics2D g2d) {
            Composite originalComposite = g2d.getComposite();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2d.setColor(BUTTON_COLOR);
            g2d.fillOval(x, y, size, size);
            g2d.setComposite(originalComposite);
        }
    }

    private class WaterAnimationPanel extends JPanel {
        private final GradientPaint BACKGROUND_GRADIENT = new GradientPaint(0, 0, new Color(240, 248, 255), 0, 600, new Color(179, 229, 252));

        public WaterAnimationPanel() {
            setOpaque(false);
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setPaint(BACKGROUND_GRADIENT);
            g2d.fillRect(0, 0, getWidth(), getHeight());
            drawWaterWave(g2d);
            for (WaterDrop drop : waterDrops) {
                drop.draw(g2d);
            }
            g2d.dispose();
        }

        private void drawWaterWave(Graphics2D g2d) {
            int width = getWidth();
            int height = getHeight();
            double time = System.currentTimeMillis() / 1000.0;
            int waveHeight = height / 4;
            int baseY = height - waveHeight;
            Path2D path = new Path2D.Double();
            path.moveTo(0, baseY);
            for (int x = 0; x < width; x += 5) {
                double y = baseY + Math.sin(x * 0.02 + time) * 15;
                path.lineTo(x, y);
            }
            path.lineTo(width, height);
            path.lineTo(0, height);
            path.closePath();
            GradientPaint waterGradient = new GradientPaint(0, baseY, new Color(100, 181, 246, 180), 0, height, new Color(33, 150, 243, 220));
            g2d.setPaint(waterGradient);
            g2d.fill(path);
        }
    }
}