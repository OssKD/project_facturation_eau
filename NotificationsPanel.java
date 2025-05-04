package interfasseClient;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class NotificationsPanel extends JPanel {
    private DefaultListModel<String> notificationListModel;
    private JList<String> notificationList;
    private JButton markAllReadButton;

    public NotificationsPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 250, 255)); // fond clair

        // Titre
        JLabel titleLabel = new JLabel("📢 Notifications Client", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(33, 102, 132));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Liste des notifications
        notificationListModel = new DefaultListModel<>();
        notificationList = new JList<>(notificationListModel);
        notificationList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        notificationList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        notificationList.setBackground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(notificationList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        add(scrollPane, BorderLayout.CENTER);

        // Bouton pour marquer toutes les notifications comme lues
        markAllReadButton = new JButton("✔️ Marquer tout comme lu");
        markAllReadButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        markAllReadButton.setBackground(new Color(33, 102, 132));
        markAllReadButton.setForeground(Color.WHITE);
        markAllReadButton.setFocusPainted(false);
        markAllReadButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(245, 250, 255));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        buttonPanel.add(markAllReadButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Charger les notifications
        loadNotifications();

        // Action bouton
        markAllReadButton.addActionListener(e -> clearNotifications());
    }

    // Charger depuis la BDD
    private void loadNotifications() {
        notificationListModel.clear();

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/gestion_eau", "root", "");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT message FROM notifications")) {

            while (rs.next()) {
                notificationListModel.addElement("🔔 " + rs.getString("message"));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "❌ Erreur lors du chargement des notifications.");
        }
    }

    // Supprimer toutes les notifications
    private void clearNotifications() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/gestion_eau", "root", "");
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("DELETE FROM notifications");
            notificationListModel.clear();
            JOptionPane.showMessageDialog(this, "✅ Toutes les notifications ont été marquées comme lues.");

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "❌ Erreur lors de la suppression des notifications.");
        }
    }

    // Testeur indépendant
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Notifications");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(500, 350);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new NotificationsPanel());
            frame.setVisible(true);
        });
    }
}
