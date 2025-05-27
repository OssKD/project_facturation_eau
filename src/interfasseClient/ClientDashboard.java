package interfasseClient;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import module.LoginPanel;
import Config.Icons;
public class ClientDashboard extends JFrame {
    private JPanel contentPanel;
    private JPanel menuPanel;
    private int activeButtonIndex = -1; 
    private int clientId;

    // Couleurs et polices
    private final Color MAIN_COLOR = new Color(41, 128, 185);
    private final Color DARK_MAIN_COLOR = new Color(33, 97, 140);
    private final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private final Color MENU_COLOR = new Color(52, 73, 94);
    private final Color MENU_HOVER_COLOR = new Color(44, 62, 80);
    private final Font MENU_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    public ClientDashboard(int clientId) {
        this.clientId = clientId;

        setTitle("Espace Client  " + clientId);
        setSize(1000, 700);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_COLOR);

        add(createHeaderPanel(), BorderLayout.NORTH);
        menuPanel = createMenuPanel();
        add(menuPanel, BorderLayout.WEST);

        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        add(contentPanel, BorderLayout.CENTER);

        showPanel(createWelcomePanel());
        setVisible(true);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(MAIN_COLOR);
        panel.setPreferredSize(new Dimension(getWidth(), 60));
        panel.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel title = new JLabel("Tableau de Bord - Espace Client");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        panel.add(title, BorderLayout.WEST);

          
        return panel;
    }

    private JPanel createMenuPanel() {
    	
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(MENU_COLOR);
        panel.setPreferredSize(new Dimension(220, getHeight()));
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, DARK_MAIN_COLOR));
        
        panel.add(createLogoPanel());

        String[][] menuItems = { 
        	    {Icons.PROFILE, "Mon Profil"},
        	    {Icons.PAYMENT, "Paiement"},
        	    {Icons.BILLS, "Mes Factures"},
        	    {Icons.NOTIFICATIONS, "Notifications"},
        	}; 
        
        for (int i = 0; i < menuItems.length; i++) {
            JPanel item = createMenuButton(menuItems[i][0], menuItems[i][1], i);
            panel.add(item);
        }

        panel.add(Box.createVerticalGlue());
        panel.add(createMenuButton("\uD83D\uDEAA", "Déconnexion", menuItems.length)); // Déconnexion

        return panel;
    }

    private JPanel createLogoPanel() {
        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.setBackground(DARK_MAIN_COLOR);
        logoPanel.setMaximumSize(new Dimension(220, 80));
        logoPanel.setPreferredSize(new Dimension(220, 80));
        JLabel logoLabel = new JLabel("\uD83D\uDCA7 Water Company", SwingConstants.CENTER);
        logoLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 16));
        logoLabel.setForeground(Color.WHITE);
        logoPanel.add(logoLabel, BorderLayout.CENTER);
        return logoPanel;
    }
    
    private JPanel createMenuButton(String icon, String text, int index) {
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBackground(MENU_COLOR);
        buttonPanel.setMaximumSize(new Dimension(220, 50));
        buttonPanel.setPreferredSize(new Dimension(220, 50));
        buttonPanel.setBorder(new EmptyBorder(8, 15, 8, 5));
        buttonPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel iconLabel = new JLabel(icon);
        // Police spéciale pour emoji (important)
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        iconLabel.setForeground(Color.WHITE);
        iconLabel.setBorder(new EmptyBorder(0, 0, 0, 10));

        JLabel textLabel = new JLabel(text);
        textLabel.setFont(MENU_FONT); // ta police perso pour le texte
        textLabel.setForeground(Color.WHITE); 

        JPanel content = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        content.setOpaque(false);
        content.add(iconLabel);
        content.add(textLabel);

        buttonPanel.add(content, BorderLayout.CENTER);

        JPanel indicatorPanel = new JPanel();
        indicatorPanel.setPreferredSize(new Dimension(4, 50));
        indicatorPanel.setBackground(MENU_COLOR);
        buttonPanel.add(indicatorPanel, BorderLayout.WEST);

        buttonPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (activeButtonIndex != index) buttonPanel.setBackground(MENU_HOVER_COLOR);
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

                switch (text) {
                    case "Mon Profil":
                        showPanel(new ProfilePanel(clientId));
                        break;
                    case "Paiement":
                        showPanel(new PaymentPanel());
                        break;
                    case "Mes Factures":
                        showPanel(new FacturesPanel(clientId));
                        break;
                    case "Notifications":
                        showPanel(new NotificationsPanel(clientId));
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
        for (int i = 0; i < menuPanel.getComponentCount(); i++) {
            Component c = menuPanel.getComponent(i);
            if (c instanceof JPanel && c.getPreferredSize().height == 50) {
                JPanel item = (JPanel) c;
                JPanel indicator = (JPanel) item.getComponent(1);

                int buttonIndex = i - 1;
                if (buttonIndex == activeButtonIndex) {
                    item.setBackground(MENU_HOVER_COLOR);
                    indicator.setBackground(MAIN_COLOR);
                } else {
                    item.setBackground(MENU_COLOR);
                    indicator.setBackground(MENU_COLOR);
                }
            }
        }
    }

    private void showPanel(JPanel panel) {
        System.out.println("Affichage du panneau : " + panel.getClass().getSimpleName());
        contentPanel.removeAll();
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);

        JLabel label = new JLabel("Bienvenue sur votre tableau de bord", SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 35)); // Augmentation de la taille
        label.setForeground(DARK_MAIN_COLOR);

        panel.add(label, BorderLayout.CENTER);
        return panel;
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

            // Créer une nouvelle fenêtre avec le LoginPanel
            JFrame loginFrame = new JFrame("Connexion");
            loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            loginFrame.setSize(800, 600);
            loginFrame.setLocationRelativeTo(null);
            loginFrame.setContentPane(new LoginPanel(loginFrame)); // Passer le frame au panel
            loginFrame.setVisible(true);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClientDashboard(1));
    }
}
