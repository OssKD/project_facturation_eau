package module;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import Config.AjouteBD;

/**
 * Panneau d'inscription avec animation d'eau et design moderne
 */
public class RegisterPanel extends JPanel {
    // Composants de l'interface
    private JTextField nomField, prenomField, emailField, compteurField, adresseField, telephoneField;
    private JPasswordField passwordField;
    private JButton registerButton, backButton;
    
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
    private final Color REGISTER_BUTTON_COLOR = new Color(76, 175, 80);
    private final Color REGISTER_BUTTON_HOVER = new Color(56, 142, 60);
    
    /**
     * Constructeur du panneau d'inscription
     */
    public RegisterPanel(JFrame frame) {
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
        formPanel.setPreferredSize(new Dimension(400, 0));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Titre d'inscription
        JLabel titleLabel = new JLabel("Inscription");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(DEEP_BLUE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 10, 20, 10);
        formPanel.add(titleLabel, gbc);
        
        // Champ Nom
        addFormField(formPanel, "Nom:", gbc, 1, nomField = createStyledTextField());
        
        // Champ Prénom
        addFormField(formPanel, "Prénom:", gbc, 3, prenomField = createStyledTextField());
        
        // Champ Email
        addFormField(formPanel, "Email:", gbc, 5, emailField = createStyledTextField());
        
        // Champ Numéro compteur
        addFormField(formPanel, "Numéro compteur:", gbc, 7, compteurField = createStyledTextField());
        
        // Champ Adresse
        addFormField(formPanel, "Adresse:", gbc, 9, adresseField = createStyledTextField());
        
        // Champ Téléphone
        addFormField(formPanel, "Téléphone:", gbc, 11, telephoneField = createStyledTextField());
        
        // Champ Mot de passe
        addFormField(formPanel, "Mot de passe:", gbc, 13, passwordField = createStyledPasswordField());
        
        // Boutons
        // Bouton d'inscription
        registerButton = createStyledButton("S'inscrire");
        registerButton.setBackground(REGISTER_BUTTON_COLOR);
        registerButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                registerButton.setBackground(REGISTER_BUTTON_HOVER);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                registerButton.setBackground(REGISTER_BUTTON_COLOR);
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 15;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 10, 10, 10);
        formPanel.add(registerButton, gbc);
        
        // Bouton retour
        backButton = createStyledButton("Retour");
        backButton.setBackground(BUTTON_COLOR);
        backButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                backButton.setBackground(BUTTON_HOVER);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                backButton.setBackground(BUTTON_COLOR);
            }
        });
        gbc.gridy = 16;
        gbc.insets = new Insets(10, 10, 10, 10);
        formPanel.add(backButton, gbc);
        
        // Configuration des actions
        registerButton.addActionListener(e -> handleRegister());
        backButton.addActionListener(e -> {
            LoginPanel loginPanel = new LoginPanel(frame);
            frame.setContentPane(loginPanel);
            frame.revalidate();
            frame.repaint();
        });
        
        return formPanel;
    }
    
    /**
     * Ajoute un champ au formulaire avec son label
     */
    private void addFormField(JPanel panel, String labelText, GridBagConstraints gbc, int row, JComponent field) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        label.setForeground(DEEP_BLUE);
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(8, 10, 3, 10);
        panel.add(label, gbc);
        
        gbc.gridy = row + 1;
        gbc.insets = new Insets(0, 10, 8, 10);
        panel.add(field, gbc);
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
     * Gestion de l'inscription
     */
    private void handleRegister() {
        String nom = nomField.getText();
        String prenom = prenomField.getText();
        String email = emailField.getText();
        String compteur = compteurField.getText(); // utilisé comme mot de passe ici ?
        String adresse = adresseField.getText();
        String telephone = telephoneField.getText();
        String password = new String(passwordField.getPassword());

        // Validation simple
        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || compteur.isEmpty()
            || adresse.isEmpty() || telephone.isEmpty() || password.isEmpty()) {
            showErrorMessage("Tous les champs sont obligatoires.");
            return;
        }

        if (!email.contains("@")) {
            showErrorMessage("Email invalide.");
            return;
        }

        if (!telephone.matches("\\d+")) {
            showErrorMessage("Téléphone invalide.");
            return;
        }

        if (password.length() < 6) {
            showErrorMessage("Le mot de passe doit contenir au moins 6 caractères.");
            return;
        }

        // Générer un ID aléatoire (ou mieux : auto_increment depuis la base)
        int id = (int)(Math.random() * 100000); // à éviter en prod si non unique

        try {
            // Appel à la méthode de la classe AjouteBD
            AjouteBD.Ajouteruser(id, nom, prenom,Integer.parseInt(compteur), email, password,Integer.parseInt(telephone), "U", adresse); // type = "U" pour user
            JOptionPane.showMessageDialog(this, "Inscription réussie !", "Succès", JOptionPane.INFORMATION_MESSAGE);
           
        } catch (Exception e) {
            e.printStackTrace();
            showErrorMessage("Erreur lors de l'inscription : " + e.getMessage());
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