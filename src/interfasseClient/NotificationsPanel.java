package interfasseClient;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.sql.*;
import java.util.HashMap;

public class NotificationsPanel extends JPanel {
    private DefaultListModel<String> notificationListModel;
    private JList<String> notificationList;
    private JButton markAsReadButton;
    private int id_user;
    private HashMap<Integer, Integer> indexToIdMap = new HashMap<>();

    public NotificationsPanel(int id) {
        this.id_user = id;
        setLayout(new BorderLayout());
        setBackground(new Color(245, 250, 255));

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
        notificationList.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        notificationList.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(notificationList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        add(scrollPane, BorderLayout.CENTER);

        // Bouton "Marquer comme lu"
        markAsReadButton = new JButton("✔️ Marquer comme lu");
        markAsReadButton.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
        markAsReadButton.setBackground(new Color(33, 102, 132));
        markAsReadButton.setForeground(Color.WHITE);
        markAsReadButton.setFocusPainted(false);
        markAsReadButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(245, 250, 255));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        buttonPanel.add(markAsReadButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Action du bouton : supprimer la notification sélectionnée
        markAsReadButton.addActionListener(e -> {
            int selectedIndex = notificationList.getSelectedIndex();
            if (selectedIndex != -1) {
                int idRappel = indexToIdMap.getOrDefault(selectedIndex, -1);
                if (idRappel != -1) {
                    deleteNotification(selectedIndex, idRappel);
                }
            } else {
                JOptionPane.showMessageDialog(this, "⚠️ Veuillez sélectionner une notification à marquer comme lue.");
            }
        });

        loadNotifications(id_user);
    }

    // Charger toutes les notifications de l'utilisateur
    private void loadNotifications(int id) {
        notificationListModel.clear();
        indexToIdMap.clear();

        String sql = "SELECT id_rappel, message FROM rappel WHERE id_user = ?";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/javaswing_app", "root", "");
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            int index = 0;
            while (rs.next()) {
                int idRappel = rs.getInt("id_rappel");
                String message = rs.getString("message");
                notificationListModel.addElement("🔔 " + message);
                indexToIdMap.put(index, idRappel);
                index++;
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "❌ Erreur lors du chargement des notifications.");
        }
    }

    // Supprimer une seule notification
    private void deleteNotification(int index, int idRappel) {
        String sql = "DELETE FROM rappel WHERE id_rappel = ?";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/javaswing_app", "root", "");
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idRappel);
            ps.executeUpdate();

            notificationListModel.remove(index);
            indexToIdMap.remove(index);
            reindexMap();

            JOptionPane.showMessageDialog(this, "✅ Notification marquée comme lue.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "❌ Erreur lors de la suppression de la notification.");
        }
    }

    // Recalcule les index de la map après suppression
    private void reindexMap() {
        HashMap<Integer, Integer> newMap = new HashMap<>();
        int i = 0;
        for (int key : indexToIdMap.values()) {
            newMap.put(i++, key);
        }
        indexToIdMap = newMap;
    }
}
