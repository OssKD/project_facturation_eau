package module;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import interfasseAdmin.AdminDashboard;
import interfasseClient.ClientDashboard;


/**
 * Panneau de connexion avec animation d'eau et design moderne
 */
public class LoginPanel extends JPanel {
    // Composants de l'interface
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton, forgotPasswordButton, registerButton;
    private JButton togglePasswordButton;
    private boolean passwordVisible = false;
    
    // Icônes pour le bouton de visibilité du mot de passe
    private ImageIcon eyeIcon;
    private ImageIcon lockIcon;
    
    // Composants pour l'animation d'eau
    private ArrayList<WaterDrop> waterDrops;
    private Timer animationTimer;
    private final int MAX_DROPS = 30; 
    private final Random random = new Random();
    
    // Couleurs thématiques
    private final Color WATER_BLUE = new Color(33, 150, 243);
    private final Color DEEP_BLUE = new Color(21, 101, 192);
    private final Color LIGHT_BLUE = new Color(179, 229, 252);
    private final Color BUTTON_COLOR = new Color(0, 119, 182);
    private final Color BUTTON_HOVER = new Color(3, 82, 123);
    
    /**
     * Constructeur du panneau de connexion
     */
    public LoginPanel(JFrame frame) {
    	
        // Configuration du panneau
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        // Chargement des icônes
        loadIcons();
      
        // Initialisation de l'animation d'eau
        initWaterAnimation();
        
        // Création du panneau de formulaire
        JPanel formPanel = createFormPanel(frame);
        
        // Ajout des panneaux au conteneur principal
        add(new WaterAnimationPanel(), BorderLayout.CENTER);
        add(formPanel, BorderLayout.EAST);
    }
    
    /**
     * Chargement des icônes pour l'interface
     */
    private void loadIcons() {
        try {
            // Chargement et redimensionnement des icônes
            ImageIcon originalEyeIcon = new ImageIcon(getClass().getResource("/images/eye.png"));
            ImageIcon originalLockIcon = new ImageIcon(getClass().getResource("/images/lock.png"));
            
            // Redimensionner les icônes (ajuster la taille selon vos besoins)
            Image eyeImage = originalEyeIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            Image lockImage = originalLockIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            
            eyeIcon = new ImageIcon(eyeImage);
            lockIcon = new ImageIcon(lockImage);
        } catch (Exception e) {
            // En cas d'erreur, utiliser des caractères Unicode comme fallback
            System.err.println("Erreur lors du chargement des icônes: " + e.getMessage());
            eyeIcon = null;
            lockIcon = null;
        }
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
//        formPanel.setPreferredSize(new Dimension(400, 0));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Titre de connexion
        JLabel titleLabel = new JLabel("Connexion");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
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
        
        // Champ Mot de passe
        JLabel passwordLabel = new JLabel("Mot de passe:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordLabel.setForeground(DEEP_BLUE);
        gbc.gridy = 3;
        gbc.insets = new Insets(10, 10, 5, 10);
        formPanel.add(passwordLabel, gbc);
        
        // Panneau pour contenir le champ mot de passe et le bouton de visibilité
        JPanel passwordPanel = new JPanel(new BorderLayout(5, 0));
        passwordPanel.setBackground(new Color(255, 255, 255, 0)); // Transparent
        
        passwordField = createStyledPasswordField();
        passwordPanel.add(passwordField, BorderLayout.CENTER);
        
        // Bouton de visibilité du mot de passe
        togglePasswordButton = new JButton();
        togglePasswordButton.setFocusPainted(false);
        togglePasswordButton.setBorderPainted(false);
        togglePasswordButton.setContentAreaFilled(false);
        togglePasswordButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        togglePasswordButton.setToolTipText("Afficher/masquer le mot de passe");
        
        // Définir l'icône initiale (ou texte de secours si l'icône n'est pas disponible)
        if (eyeIcon != null) {
            togglePasswordButton.setIcon(eyeIcon);
        } else {
            togglePasswordButton.setText("👁");
            togglePasswordButton.setFont(new Font("Arial", Font.PLAIN, 18));
            togglePasswordButton.setForeground(DEEP_BLUE);
        }
        
        togglePasswordButton.addActionListener(e -> togglePasswordVisibility());
        
        passwordPanel.add(togglePasswordButton, BorderLayout.EAST);
        
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 10, 20, 10);
        formPanel.add(passwordPanel, gbc);
        
        // Bouton de connexion
        loginButton = createStyledButton("Connexion");
        gbc.gridy = 5;
        gbc.insets = new Insets(15, 10, 10, 10);
        formPanel.add(loginButton, gbc);
        
        // changer mot de passe oublié
        JButton changemot_passe = createLinkButton("changer mot de passe ?");
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(changemot_passe, gbc);
        
        // Bouton mot de passe oublié
        forgotPasswordButton = createLinkButton("Mot de passe oublié ?");
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(forgotPasswordButton, gbc);
        
        // Séparateur
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(200, 200, 200));
        gbc.gridy = 8;
        gbc.insets = new Insets(20, 10, 20, 10);
        formPanel.add(separator, gbc);
        
        // Bouton d'inscription
        registerButton = createStyledButton("S'inscrire");
        registerButton.setBackground(new Color(76, 175, 80));
        registerButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                registerButton.setBackground(new Color(56, 142, 60));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                registerButton.setBackground(new Color(76, 175, 80));
            }
        });
        registerButton.addActionListener(e -> {
            RegisterPanel registerPanel = new RegisterPanel(frame);
            frame.setContentPane(registerPanel);
            frame.revalidate(); // Pour mettre à jour l'affichage
            frame.repaint();    // Pour redessiner la fenêtre
        });

        gbc.gridy = 8;
        gbc.insets = new Insets(10, 10, 10, 10);
        formPanel.add(registerButton, gbc);
        
        // Configuration des actions
        loginButton.addActionListener(e -> handleLogin(frame));
      
        changemot_passe.addActionListener(e -> {
            PasswordResetPanel change = new PasswordResetPanel(frame);
            frame.setContentPane(change);
            frame.revalidate(); // Pour mettre à jour l'affichage
            frame.repaint();    // Pour redessiner la fenêtre
        });
        
        forgotPasswordButton.addActionListener(e -> {
            mot_passeOublier mot_passeObl = new mot_passeOublier(frame);
            frame.setContentPane(mot_passeObl);
            frame.revalidate(); // Pour mettre à jour l'affichage
            frame.repaint();    // Pour redessiner la fenêtre
        }
        );
        // Le code pour le bouton d'inscription est commenté dans le code original
        
        return formPanel;
    }
    
    /**
     * Méthode pour basculer la visibilité du mot de passe
     */
    private void togglePasswordVisibility() {
        passwordVisible = !passwordVisible;
        
        if (passwordVisible) {
            // Afficher le mot de passe
            passwordField.setEchoChar((char) 0); // Désactive les caractères masqués
            
            // Changer l'icône ou le texte
            if (lockIcon != null) {
                togglePasswordButton.setIcon(lockIcon);
            } else {
                togglePasswordButton.setText("🔒");
            }
        } else {
            // Masquer le mot de passe
            passwordField.setEchoChar('•'); // Réactive les caractères masqués
            
            // Changer l'icône ou le texte
            if (eyeIcon != null) {
                togglePasswordButton.setIcon(eyeIcon);
            } else {
                togglePasswordButton.setText("👁");
            }
        }
        
        passwordField.repaint();
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
        field.setEchoChar('•'); // Définir le caractère d'affichage masqué
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
     * Gestion de la connexion
     */

    private void handleLogin(JFrame frame) {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (email.isEmpty() || password.isEmpty()) {
            showErrorMessage("Veuillez remplir tous les champs.");
            return;
        }

        // Vérification admin
        if (email.equals("admin") && password.equals("123")) {
            AdminDashboard dashboard = new AdminDashboard();
            dashboard.setVisible(true);
            frame.dispose(); // Ne pas oublier de fermer la fenêtre même pour admin
            return;
        }
        else {

        // Connexion à la base de données
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/javaswing_app", "root", "");
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM user WHERE email = ? AND password = ?")) {

            stmt.setString(1, email);
            stmt.setString(2, password); // Attention : doit correspondre au mot de passe stocké

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id_user");
                ClientDashboard dashboard = new ClientDashboard(id);
                dashboard.setVisible(true);
                frame.dispose(); // Fermer la fenêtre de connexion
            } else {
                showErrorMessage("Email ou mot de passe incorrect.");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            showErrorMessage("Erreur de connexion à la base de données.");
        }
        }
    }


    private String encryptPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();

            // Convertir le tableau d'octets en chaîne hexadécimale
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
     * Effet de transition lors de la connexion réussie
     */
    private void fadeOutTransition(JFrame frame, Runnable onComplete) {
        final float[] opacity = new float[] { 1.0f };
        final Timer timer = new Timer(20, null);
        
        timer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                opacity[0] -= 0.05f;
                if (opacity[0] <= 0) {
                    timer.stop();
                    onComplete.run();
                } else {
                    WindowUtils.setWindowOpacity(frame, opacity[0]);
                }
            }
        });
        
        timer.start();
    }
    
    /**
     * Classe utilitaire pour gérer l'opacité de la fenêtre
     */
    private static class WindowUtils {
        public static void setWindowOpacity(Window window, float opacity) {
            try {
                Class<?> awtUtilsClass = Class.forName("com.sun.awt.AWTUtilities");
                awtUtilsClass.getMethod("setWindowOpacity", Window.class, float.class)
                             .invoke(null, window, opacity);
            } catch (Exception e) {
                // Fallback si l'API n'est pas disponible
                window.setOpacity(opacity);
            }
        }
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