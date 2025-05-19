package module;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class mot_passeOublier extends JPanel {
    private JTextField emailField;
    private JTextField codeField;
    private JPasswordField newPasswordField;
    private JButton sendCodeButton, confirmButton, backButton;
    private ArrayList<WaterDrop> waterDrops;
    private Timer animationTimer;
    private String confirmationCode;
    private final JFrame parentFrame;
    private final SendMailExample mailSender = new SendMailExample();

    private final int MAX_DROPS = 30;
    private final Color BUTTON_COLOR = new Color(0, 119, 182);
    private final Color BUTTON_HOVER = new Color(3, 82, 123);

    public mot_passeOublier(JFrame frame) {
        this.parentFrame = frame;
        setLayout(new BorderLayout());
        initWaterAnimation();
        add(new WaterAnimationPanel(), BorderLayout.CENTER);
        add(createFormPanel(), BorderLayout.EAST);
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
        int y = -(int) (Math.random() * 200);
        int size = (int) (Math.random() * 20) + 10;
        float speed = (float) (Math.random() * 2 + 1);
        float alpha = (float) (Math.random() * 0.5 + 0.3);
        waterDrops.add(new WaterDrop(x, y, size, speed, alpha));
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(255, 255, 255, 220));
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Mot de passe oublié");
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

        sendCodeButton = createStyledButton("Envoyer le code");
        gbc.gridy++;
        gbc.insets = new Insets(5, 10, 20, 10);
        formPanel.add(sendCodeButton, gbc);

        gbc.insets = new Insets(10, 10, 5, 10);
        gbc.gridy++;
        formPanel.add(new JLabel("Code de confirmation:"), gbc);
        codeField = createStyledTextField();
        gbc.gridy++;
        gbc.insets = new Insets(0, 10, 15, 10);
        formPanel.add(codeField, gbc);

        gbc.insets = new Insets(10, 10, 5, 10);
        gbc.gridy++;
        formPanel.add(new JLabel("Nouveau mot de passe:"), gbc);
        newPasswordField = createStyledPasswordField();
        gbc.gridy++;
        gbc.insets = new Insets(0, 10, 20, 10);
        formPanel.add(newPasswordField, gbc);

        confirmButton = createStyledButton("Confirmer");
        gbc.gridy++;
        gbc.insets = new Insets(5, 10, 10, 10);
        formPanel.add(confirmButton, gbc);

        backButton = createStyledButton("Retour à la connexion");
        gbc.gridy++;
        gbc.insets = new Insets(10, 10, 10, 10);
        formPanel.add(backButton, gbc);

        sendCodeButton.addActionListener(e -> sendConfirmationCode());
        confirmButton.addActionListener(e -> confirmNewPassword());

        backButton.addActionListener(e -> {
            if (parentFrame != null) {
                parentFrame.setContentPane(new LoginPanel(parentFrame));
                parentFrame.revalidate();
                parentFrame.repaint();
            }
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

    private void sendConfirmationCode() {
        String email = emailField.getText();
        if (email.isEmpty()) {
            showErrorMessage("Veuillez entrer une adresse email.");
            return;
        }

        if (!email.contains("@")) {
            showErrorMessage("Email invalide.");
            return;
        }

        confirmationCode = String.valueOf(new Random().nextInt(900000) + 100000);
        String title = "Code de Réinitialisation du Mot de Passe";
        String message = "Votre code de confirmation est: " + confirmationCode;
        mailSender.sendRecoveryEmail(email, title, message);
    }

    private void confirmNewPassword() {
        String enteredCode = codeField.getText();
        String newPassword = new String(newPasswordField.getPassword());

        if (enteredCode.isEmpty() || newPassword.isEmpty()) {
            showErrorMessage("Veuillez remplir tous les champs.");
            return;
        }

        if (newPassword.length() < 6) {
            showErrorMessage("Le mot de passe doit contenir au moins 6 caractères.");
            return;
        }

        if (confirmationCode != null && confirmationCode.equals(enteredCode)) {
            int option = JOptionPane.showConfirmDialog(this,
                    "Voulez-vous changer votre mot de passe ?",
                    "Confirmation",
                    JOptionPane.YES_NO_OPTION);

            if (option == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(this, "Mot de passe réinitialisé avec succès !", "Succès", JOptionPane.INFORMATION_MESSAGE);
                if (parentFrame != null) {
                    parentFrame.setContentPane(new LoginPanel(parentFrame));
                    parentFrame.revalidate();
                    parentFrame.repaint();
                }
            }
        } else {
            showErrorMessage("Code de confirmation incorrect.");
        }
    }

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

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
