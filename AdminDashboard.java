package interfaceAdmin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AdminDashboard extends JFrame {
    private JPanel contentPanel;
    private JPanel menuPanel;
    private int activeButtonIndex = -1;
    private final Color MAIN_COLOR = new Color(41, 128, 185);
    private final Color DARK_MAIN_COLOR = new Color(33, 97, 140);
    private final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private final Color MENU_COLOR = new Color(52, 73, 94);
    private final Color MENU_HOVER_COLOR = new Color(44, 62, 80);
    private final Font MENU_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    public AdminDashboard() {
        setTitle("Gestion de Facturation d'Eau - ((Administration))");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_COLOR);

        // Panel d'en-tête
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Menu latéral gauche
        menuPanel = createMenuPanel();
        add(menuPanel, BorderLayout.WEST);

        // Panneau principal pour les vues
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        add(contentPanel, BorderLayout.CENTER);
        
        // Affichage du panel d'accueil par défaut
        showPanel(createWelcomePanel());

        setVisible(true);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(MAIN_COLOR);
        headerPanel.setPreferredSize(new Dimension(getWidth(), 60));
        headerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel titleLabel = new JLabel("Administration - Système de Facturation d'Eau");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setOpaque(false);
        
        JLabel userLabel = new JLabel("Admin");
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userLabel.setForeground(Color.WHITE);
        userPanel.add(userLabel);
        
        JLabel userIcon = new JLabel("\uD83D\uDC64"); // Emoji utilisateur
        userIcon.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        userIcon.setForeground(Color.WHITE);
        userPanel.add(userIcon);
        
        headerPanel.add(userPanel, BorderLayout.EAST);
        
        return headerPanel;
    }

    private JPanel createMenuPanel() {
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(MENU_COLOR);
        menuPanel.setPreferredSize(new Dimension(220, getHeight()));
        menuPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, DARK_MAIN_COLOR));

        // Logo ou titre en haut du menu
        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.setBackground(DARK_MAIN_COLOR);
        logoPanel.setMaximumSize(new Dimension(220, 80));
        logoPanel.setPreferredSize(new Dimension(220, 80));
        
        JLabel logoLabel = new JLabel("\uD83D\uDCA7 Water Billing", SwingConstants.CENTER);
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        logoLabel.setForeground(Color.WHITE);
        logoPanel.add(logoLabel, BorderLayout.CENTER);
        
        menuPanel.add(logoPanel);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Boutons de menu avec icônes
        String[][] menuItems = {
            {"\uD83D\uDC64", "Gestion Clients"},
            {"\uD83D\uDCB8", "Gestion Factures"},
            {"\uD83D\uDD14", "Notifications"},
            {"\uD83D\uDCCA", "Statistiques"},
        };

        for (int i = 0; i < menuItems.length; i++) {
            JPanel menuItemPanel = createMenuButton(menuItems[i][0], menuItems[i][1], i);
            menuPanel.add(menuItemPanel);
        }

        menuPanel.add(Box.createVerticalGlue());
        
        // Bouton déconnexion en bas
        JPanel logoutPanel = createMenuButton("\uD83D\uDEAA", "Déconnexion", menuItems.length);
        menuPanel.add(logoutPanel);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        return menuPanel;
    }

    private JPanel createMenuButton(String icon, String text, int index) {
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBackground(MENU_COLOR);
        buttonPanel.setMaximumSize(new Dimension(220, 50));
        buttonPanel.setPreferredSize(new Dimension(220, 50));
        buttonPanel.setBorder(new EmptyBorder(8, 15, 8, 5));
        buttonPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        iconLabel.setForeground(Color.WHITE);
        iconLabel.setBorder(new EmptyBorder(0, 0, 0, 10));
        
        JLabel textLabel = new JLabel(text);
        textLabel.setFont(MENU_FONT);
        textLabel.setForeground(Color.WHITE);

        JPanel contentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        contentPanel.setOpaque(false);
        contentPanel.add(iconLabel);
        contentPanel.add(textLabel);
        
        buttonPanel.add(contentPanel, BorderLayout.CENTER);

        // Indicateur de sélection
        JPanel indicatorPanel = new JPanel();
        indicatorPanel.setPreferredSize(new Dimension(4, 50));
        indicatorPanel.setBackground(MENU_COLOR);
        buttonPanel.add(indicatorPanel, BorderLayout.WEST);

        // Écouteurs d'événements
        buttonPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (activeButtonIndex != index) {
                    buttonPanel.setBackground(MENU_HOVER_COLOR);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (activeButtonIndex != index) {
                    buttonPanel.setBackground(MENU_COLOR);
                    indicatorPanel.setBackground(MENU_COLOR);
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                activeButtonIndex = index;
                updateMenuSelection();
                
                // Actions selon le bouton cliqué
                switch(text) {
                    case "Gestion Clients":
                        showPanel(new ClientManagementPanel());
                        break;
                    case "Gestion Factures":
                        showPanel(new FactureManagementPanel());
                        break;
                    case "Notifications":
                        showPanel(new GlobalNotificationsPanel());
                        break;
                    case "Déconnexion":
                        handleLogout();
                        break;
                }
            }
        });

        return buttonPanel;
    }

    private void updateMenuSelection() {
        // Mise à jour visuelle des boutons du menu
        for (int i = 0; i < menuPanel.getComponentCount(); i++) {
            Component component = menuPanel.getComponent(i);
            if (component instanceof JPanel && component.getPreferredSize().height == 50) {
                JPanel menuItem = (JPanel) component;
                JPanel indicator = (JPanel) menuItem.getComponent(1);
                
                int buttonIndex = i - 2; // Ajuster pour les composants au-dessus des boutons
                if (buttonIndex == activeButtonIndex) {
                    menuItem.setBackground(MENU_HOVER_COLOR);
                    indicator.setBackground(MAIN_COLOR);
                } else {
                    menuItem.setBackground(MENU_COLOR);
                    indicator.setBackground(MENU_COLOR);
                }
            }
        }
    }

    private JPanel createWelcomePanel() {
        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setBackground(BACKGROUND_COLOR);
        
        JLabel welcomeLabel = new JLabel("Bienvenue dans le système de facturation d'eau", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeLabel.setForeground(DARK_MAIN_COLOR);
        welcomePanel.add(welcomeLabel, BorderLayout.NORTH);
        
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        statsPanel.setBackground(BACKGROUND_COLOR);
        statsPanel.setBorder(new EmptyBorder(50, 100, 50, 100));
        
        statsPanel.add(createStatCard("Clients", "124", new Color(52, 152, 219)));
        statsPanel.add(createStatCard("Factures", "56", new Color(231, 76, 60)));
        statsPanel.add(createStatCard("Factures impayées", "12", new Color(230, 126, 34)));
        statsPanel.add(createStatCard("Revenus mensuels", "8,450 €", new Color(46, 204, 113)));
        
        welcomePanel.add(statsPanel, BorderLayout.CENTER);
        
        return welcomePanel;
    }
    
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel cardPanel = new JPanel(new BorderLayout());
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(new Color(100, 100, 100));
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
        valueLabel.setForeground(color);
        
        cardPanel.add(titleLabel, BorderLayout.NORTH);
        cardPanel.add(valueLabel, BorderLayout.CENTER);
        
        return cardPanel;
    }

    private void handleLogout() {
        int response = JOptionPane.showConfirmDialog(
            this,
            "Voulez-vous vraiment vous déconnecter?",
            "Confirmation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (response == JOptionPane.YES_OPTION) {
            dispose(); // Fermer la fenêtre actuelle
            // Ajouter ici le code pour réouvrir la fenêtre de connexion
            // new LoginFrame();
        }
    }

    private void showPanel(JPanel panel) {
        contentPanel.removeAll();
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    public static void main(String[] args) {
        try {
            // Appliquer un look and feel moderne
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Personnaliser certains éléments d'UI
            UIManager.put("Button.arc", 8);
            UIManager.put("Component.arc", 8);
            UIManager.put("ProgressBar.arc", 8);
            UIManager.put("TextComponent.arc", 8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> new AdminDashboard());
    }
}
