package module;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Panneau de réinitialisation de mot de passe avec animation d'eau et design moderne
 */
public class PasswordResetPanel extends JPanel {
    // Composants de l'interface
    private JTextField emailField;
    private JPasswordField newPasswordField;
    private JButton resetButton, backButton;
    
    // Composants pour l'animation d'eau
    private ArrayList<WaterDrop> waterDrops;
    private Timer animationTimer;
    private final int MAX_DROPS = 30;
    private final Random random = new Random();
    
    // Couleurs thématiques (reprises du LoginPanel)
    private final Color WATER_BLUE = new Color(33, 150, 243);
    private final Color DEEP_BLUE = new Color(21, 101, 192);
    private final Color LIGHT_BLUE = new Color(179, 229, 252);
    private final Color BUTTON_COLOR = new Color(0, 119, 182);
    private final Color BUTTON_HOVER = new Color(3, 82, 123);
    
    /**
     * Constructeur du panneau de réinitialisation de mot de passe
     */
    public PasswordResetPanel(JFrame frame) {
        // Configuration du panneau
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        // Initialisation de l'animation d'eau
        initWaterAnimation();
        
        // Création du panneau de formulaire
        JPanel formPanel = createFormPanel(frame);
        
        // Ajout des panneaux au conteneur principal
        add(new WaterAnimationPanel(), BorderLayout.CENTER);
        add(formPanel, BorderLayout.EAST);
    }
    
    /**
     * Initialisation de l'animation d'eau
     */
    private void initWaterAnimation() {
        waterDrops = new ArrayList<>();
        
        // Création des gouttes d'eau initiales
        for (int i = 0; i < MAX_DROPS; i++) {
            createNewDrop();
        }
        
        // Configuration du timer d'animation
        animationTimer = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < waterDrops.size(); i++) {
                    WaterDrop drop = waterDrops.get(i);
                    drop.move();
                    
                    // Remplacement des gouttes qui sont sorties de l'écran
                    if (drop.y > getHeight()) {
                        waterDrops.remove(i);
                        createNewDrop();
                    }
                }
                repaint();
            }
        });
        animationTimer.start();
    }
    
    /**
     * Création d'une nouvelle goutte d'eau pour l'animation
     */
    private void createNewDrop() {
        int x = random.nextInt(getWidth() > 0 ? getWidth() : 800);
        int y = -random.nextInt(200);
        int size = random.nextInt(20) + 10;
        float speed = random.nextFloat() * 2 + 1;
        float alpha = random.nextFloat() * 0.5f + 0.3f;
        
        waterDrops.add(new WaterDrop(x, y, size, speed, alpha));
    }
    
    /**
     * Création du panneau de formulaire
     */
    private JPanel createFormPanel(JFrame frame) {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(new Color(255, 255, 255, 220));
        formPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Titre de réinitialisation
        JLabel titleLabel = new JLabel("Réinitialisation du mot de passe");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(DEEP_BLUE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 10, 30, 10);
        formPanel.add(titleLabel, gbc);
        
        // Champ Email
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        emailLabel.setForeground(DEEP_BLUE);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 10, 5, 10);
        formPanel.add(emailLabel, gbc);
        
        emailField = createStyledTextField();
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 10, 15, 10);
        formPanel.add(emailField, gbc);
        
        // Champ Nouveau mot de passe
        JLabel passwordLabel = new JLabel("Nouveau mot de passe:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordLabel.setForeground(DEEP_BLUE);
        gbc.gridy = 3;
        gbc.insets = new Insets(10, 10, 5, 10);
        formPanel.add(passwordLabel, gbc);
        
        newPasswordField = createStyledPasswordField();
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 10, 20, 10);
        formPanel.add(newPasswordField, gbc);
        
        // Bouton de réinitialisation
        resetButton = createStyledButton("Changer le mot de passe");
        gbc.gridy = 5;
        gbc.insets = new Insets(15, 10, 10, 10);
        formPanel.add(resetButton, gbc);
        
        // Bouton retour
        backButton = createLinkButton("Retour à la connexion");
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(backButton, gbc);
        
        // Configuration des actions
        resetButton.addActionListener(e -> handleReset());
        backButton.addActionListener(e -> {
            LoginPanel loginPanel = new LoginPanel(frame);
            frame.setContentPane(loginPanel);
            frame.revalidate();
            frame.repaint();
        });
        
        return formPanel;
    }
    
    /**
     * Création d'un champ texte stylisé
     */
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
    
    /**
     * Création d'un champ mot de passe stylisé
     */
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
    
    /**
     * Création d'un bouton stylisé
     */
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(BUTTON_COLOR);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMargin(new Insets(10, 10, 10, 10));
        
        // Ajout des effets de survol
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(BUTTON_HOVER);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(BUTTON_COLOR);
            }
        });
        
        return button;
    }
    
    /**
     * Création d'un bouton de type lien
     */
    private JButton createLinkButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 12));
        button.setForeground(WATER_BLUE);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Ajout des effets de survol
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setForeground(DEEP_BLUE);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setForeground(WATER_BLUE);
            }
        });
        
        return button;
    }
    
    /**
     * Gestion de la réinitialisation de mot de passe
     */
    private void handleReset() {
        String email = emailField.getText();
        String newPassword = new String(newPasswordField.getPassword());

        if (email.isEmpty() || newPassword.isEmpty()) {
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

        // Vérifie si l'utilisateur existe dans la base
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/javaswing_app", "root", "");
             PreparedStatement checkStmt = conn.prepareStatement("SELECT * FROM user WHERE email = ?")) {

            checkStmt.setString(1, email);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                // Si l'email existe, permet à l'utilisateur de changer le mot de passe
                int option = JOptionPane.showConfirmDialog(this,
                        "Un compte a été trouvé avec cet email. Voulez-vous changer votre mot de passe ?",
                        "Changer mot de passe", JOptionPane.YES_NO_OPTION);

                if (option == JOptionPane.YES_OPTION) {
                    // Mise à jour du mot de passe
                    try (PreparedStatement updateStmt = conn.prepareStatement("UPDATE user SET password = ? WHERE email = ?")) {
                        updateStmt.setString(1, newPassword); // À hasher en production
                        updateStmt.setString(2, email);
                        updateStmt.executeUpdate();

                        JOptionPane.showMessageDialog(this, 
                            "Mot de passe mis à jour avec succès !",
                            "Succès",
                            JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Opération annulée.");
                }
            } else {
                // Si l'email n'existe pas dans la base
                showErrorMessage("Aucun compte trouvé avec cet email.");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            showErrorMessage("Erreur de connexion à la base de données.");
        }
    }
    
    /**
     * Affichage d'un message d'erreur stylisé
     */
    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(
            this,
            message,
            "Erreur",
            JOptionPane.ERROR_MESSAGE
        );
    }
    
    /**
     * Panneau pour l'animation d'eau
     */
    private class WaterAnimationPanel extends JPanel {
        private final GradientPaint BACKGROUND_GRADIENT = new GradientPaint(
            0, 0, new Color(240, 248, 255),
            0, 600, new Color(179, 229, 252)
        );
        
        public WaterAnimationPanel() {
            setOpaque(false);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            
            // Configuration du rendu pour des animations fluides
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            
            // Dessin du fond dégradé
            g2d.setPaint(BACKGROUND_GRADIENT);
            g2d.fillRect(0, 0, getWidth(), getHeight());
            
            // Vague de fond
            drawWaterWave(g2d);
            
            // Dessin des gouttes d'eau
            for (WaterDrop drop : waterDrops) {
                drop.draw(g2d);
            }
            
            g2d.dispose();
        }
        
        /**
         * Dessine une vague d'eau en bas de l'écran
         */
        private void drawWaterWave(Graphics2D g2d) {
            int height = getHeight();
            int width = getWidth();
            
            // Calcul de la position de la vague basée sur le temps
            double time = System.currentTimeMillis() / 1000.0;
            int waveHeight = height / 4;
            int baseY = height - waveHeight;
            
            // Création du chemin pour la vague
            Path2D path = new Path2D.Double();
            path.moveTo(0, baseY);
            
            // Génération des points de la vague
            for (int x = 0; x < width; x += 5) {
                double y = baseY + Math.sin(x * 0.02 + time) * 15;
                path.lineTo(x, y);
            }
            
            // Fermeture du chemin
            path.lineTo(width, height);
            path.lineTo(0, height);
            path.closePath();
            
            // Dégradé pour la vague
            GradientPaint waterGradient = new GradientPaint(
                0, baseY, new Color(100, 181, 246, 180),
                0, height, new Color(33, 150, 243, 220)
            );
            
            g2d.setPaint(waterGradient);
            g2d.fill(path);
        }
    }
    
    /**
     * Classe représentant une goutte d'eau pour l'animation
     */
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
        
        /**
         * Déplace la goutte d'eau vers le bas
         */
        public void move() {
            y += speed;
            // Légère oscillation horizontale
            x += Math.sin(y * 0.05) * 0.5;
        }
        
        /**
         * Dessine la goutte d'eau
         */
        public void draw(Graphics2D g2d) {
            // Sauvegarde de la transparence d'origine
            Composite originalComposite = g2d.getComposite();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            
            // Dessin de la goutte d'eau
            g2d.setColor(WATER_BLUE);
            
            // Forme de la goutte
            Path2D dropPath = new Path2D.Double();
            dropPath.moveTo(x, y - size/2);
            dropPath.curveTo(
                x - size/2, y - size/3,
                x - size/2, y + size/3,
                x, y + size/2
            );
            dropPath.curveTo(
                x + size/2, y + size/3,
                x + size/2, y - size/3,
                x, y - size/2
            );
            
            // Remplissage avec un dégradé radial
            RadialGradientPaint paint = new RadialGradientPaint(
                new Point2D.Float(x - size/6, y - size/6),
                size,
                new float[] { 0.0f, 0.85f },
                new Color[] { 
                    new Color(LIGHT_BLUE.getRed(), LIGHT_BLUE.getGreen(), LIGHT_BLUE.getBlue(), (int)(255 * alpha)),
                    new Color(WATER_BLUE.getRed(), WATER_BLUE.getGreen(), WATER_BLUE.getBlue(), (int)(200 * alpha))
                }
            );
            
            g2d.setPaint(paint);
            g2d.fill(dropPath);
            
            // Reflet sur la goutte
            g2d.setColor(new Color(255, 255, 255, (int)(120 * alpha)));
            g2d.fillOval(x - size/4, y - size/3, size/3, size/4);
            
            // Restauration de la transparence d'origine
            g2d.setComposite(originalComposite);
        }
    }
}