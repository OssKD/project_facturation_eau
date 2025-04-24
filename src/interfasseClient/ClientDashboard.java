package interfasseClient;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class ClientDashboard extends JFrame {
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private String userEmail;
    public ClientDashboard(String email) {
    	this.userEmail=email;
        setTitle("Dashboard Client");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ===== Barre de navigation à gauche =====
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new GridLayout(6, 1, 10, 10));
        navPanel.setBackground(new Color(220, 230, 240));
        navPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        JButton profileButton = new JButton("Mon Profil");
        JButton facturesButton = new JButton("Mes Factures");
        JButton paiementButton = new JButton("Payer");
        JButton notificationsButton = new JButton("Notifications");
        JButton logoutButton = new JButton("Se déconnecter");

        // Ajout des boutons
        navPanel.add(profileButton);
        navPanel.add(facturesButton);
        navPanel.add(paiementButton);
        navPanel.add(notificationsButton);
        navPanel.add(Box.createVerticalStrut(10));
        navPanel.add(logoutButton);

        add(navPanel, BorderLayout.WEST);

        // ===== Panel principal avec CardLayout =====
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        // Ajout des panels
        contentPanel.add(new ProfilePanel(email), "profil");
        contentPanel.add(new FacturesPanel(), "factures");
        contentPanel.add(new PaymentPanel(), "paiement");
        contentPanel.add(new NotificationsPanel(), "notifications");

        add(contentPanel, BorderLayout.CENTER);

        // ===== Actions des boutons de navigation =====
        profileButton.addActionListener(e -> cardLayout.show(contentPanel, "profil"));
        facturesButton.addActionListener(e -> cardLayout.show(contentPanel, "factures"));
        paiementButton.addActionListener(e -> cardLayout.show(contentPanel, "paiement"));
        notificationsButton.addActionListener(e -> cardLayout.show(contentPanel, "notifications"));

        logoutButton.addActionListener(e -> {
            int confirmed = JOptionPane.showConfirmDialog(this,
                    "Voulez-vous vous déconnecter ?", "Déconnexion", JOptionPane.YES_NO_OPTION);
            if (confirmed == JOptionPane.YES_OPTION) {
                dispose(); // Fermer la fenêtre
                // Option : rediriger vers LoginForm ici
            }
        });

        // ===== Afficher la fenêtre =====
        setVisible(true);
    }
//
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(ClientDashboard::new ) ;
//    }
}
