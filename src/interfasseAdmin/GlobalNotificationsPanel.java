package interfasseAdmin;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Map;

public class GlobalNotificationsPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private ServerSocket serverSocket;

    public GlobalNotificationsPanel() {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Notifications Globales", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        // Ajout d'une colonne id_facture
        tableModel = new DefaultTableModel(new Object[]{"ID", "Nom", "Message", "ID Facture"}, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        chargerTout();

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnEnvoyerRappel = new JButton("Envoyer Rappel");
        JButton btnActualiser = new JButton("Actualiser");

        buttonPanel.add(btnEnvoyerRappel);
        buttonPanel.add(btnActualiser);
        add(buttonPanel, BorderLayout.SOUTH);

        btnEnvoyerRappel.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                try {
                    int idUser = Integer.parseInt(table.getValueAt(selectedRow, 0).toString());
                    String message = table.getValueAt(selectedRow, 2).toString();
                    int idFacture = Integer.parseInt(table.getValueAt(selectedRow, 3).toString());

                    EnvoyermessageRappel(message, idUser, idFacture);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Erreur de récupération des données sélectionnées.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner une ligne.");
            }
        });

        btnActualiser.addActionListener(e -> {
            if (serverSocket == null || serverSocket.isClosed()) {
                new Thread(this::recevoirMessages).start();
            } else {
                JOptionPane.showMessageDialog(this, "Le serveur écoute déjà.");
            }
        });
    }

    private void recevoirMessages() {
        try {
            serverSocket = new ServerSocket(6000);
            System.out.println("✅ Serveur en attente de messages...");

            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())) {

                    String messageComplet = (String) in.readObject();
                    Map<String, Object> paiement = (Map<String, Object>) in.readObject();

                    Integer id = (Integer) paiement.get("idUser");
                    Integer id_facture = (Integer) paiement.get("idFacture");
                    String description = (String) paiement.get("description");
                    String message = testMessage(description);

                    storeMessageInDatabase(message, id);

                    if ("A".equals(description)) {
                        mettreAJourEtatPaiement(id_facture);
                        Integer montant = (Integer) paiement.get("montant");
                        insererPaiementDansBase(id, id_facture, montant);
                    }

                } catch (Exception e) {
                    System.err.println("Erreur de lecture socket : " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Erreur serveur socket : " + e.getMessage());
        }
    }

    private String testMessage(String code) {
        return switch (code) {
            case "A" -> "✅ Paiement bien effectué.";
            case "B" -> "❌ Erreur : montant insuffisant.";
            case "C" -> "❌ Erreur : compte inexistant.";
            default -> "⚠️ Message non reconnu.";
        };
    }

    private void insererPaiementDansBase(int idUser, int idFacture, int montant) {
        String sql = "INSERT INTO payement1 (id_user, id_facture, montant_paiement) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUser);
            stmt.setInt(2, idFacture);
            stmt.setInt(3, montant);
            stmt.executeUpdate();
            System.out.println("✅ Paiement inséré.");
        } catch (SQLException e) {
            System.err.println("❌ Erreur insertion paiement : " + e.getMessage());
        }
    }

    private void mettreAJourEtatPaiement(int idFacture) {
        String sql = "UPDATE facture SET etat_payment = 'P' WHERE id_facture = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idFacture);
            stmt.executeUpdate();
            System.out.println("✅ État paiement mis à jour.");
        } catch (SQLException e) {
            System.err.println("❌ Erreur update paiement : " + e.getMessage());
        }
    }

    private void storeMessageInDatabase(String message, int idUser) {
        String sql = "INSERT INTO notification (id_user, message) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUser);
            stmt.setString(2, message);
            stmt.executeUpdate();
            System.out.println("✅ Notification enregistrée.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void EnvoyermessageRappel(String message, int idUser, int idFacture) {
        String checkSQL = "SELECT COUNT(*) FROM rappel WHERE id_user = ? AND id_facture = ?";
        String insertSQL = "INSERT INTO rappel (id_user, id_facture, date_rappel) VALUES (?, ?, NOW())";
        String notificationSQL = "INSERT INTO notification (id_user, message) VALUES (?, ?)";

        try (Connection conn = getConnection()) {
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSQL)) {
                checkStmt.setInt(1, idUser);
                checkStmt.setInt(2, idFacture);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next() && rs.getInt(1) == 0) {
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSQL)) {
                        insertStmt.setInt(1, idUser);
                        insertStmt.setInt(2, idFacture);
                        insertStmt.executeUpdate();
                    }

                    try (PreparedStatement notifStmt = conn.prepareStatement(notificationSQL)) {
                        notifStmt.setInt(1, idUser);
                        notifStmt.setString(2, message);
                        notifStmt.executeUpdate();
                    }

                    System.out.println("✅ Rappel et notification envoyés.");
                } else {
                    System.out.println("ℹ️ Rappel déjà existant.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void chargerMessages() {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT u.nom, n.id_user, n.message FROM notification n JOIN user u ON n.id_user = u.id_user")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id_user");
                String nom = rs.getString("nom");
                String msg = rs.getString("message");
                tableModel.addRow(new Object[]{id, nom, msg, ""}); // Pas d'ID facture
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void chargerRappels() {
        String sql = "SELECT u.nom, f.id_user, f.id_facture, f.mois, f.etat_payment " +
                     "FROM facture f JOIN user u ON f.id_user = u.id_user";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            LocalDate now = LocalDate.now();

            while (rs.next()) {
                int idUser = rs.getInt("id_user");
                int idFacture = rs.getInt("id_facture");
                String nom = rs.getString("nom");
                String moisStr = rs.getString("mois");
                String etat = rs.getString("etat_payment");

                LocalDate mois = LocalDate.parse(moisStr.length() == 7 ? moisStr + "-01" : moisStr,
                        DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                long jours = ChronoUnit.DAYS.between(mois, now);

                if ("N".equals(etat) && jours > 20) {
                    String checkSQL = "SELECT COUNT(*) FROM rappel WHERE id_user = ? AND id_facture = ?";
                    try (PreparedStatement checkStmt = conn.prepareStatement(checkSQL)) {
                        checkStmt.setInt(1, idUser);
                        checkStmt.setInt(2, idFacture);
                        ResultSet rsCheck = checkStmt.executeQuery();
                        if (rsCheck.next() && rsCheck.getInt(1) == 0) {
                            String insertSQL = "INSERT INTO rappel (id_user, id_facture, date_rappel) VALUES (?, ?, NOW())";
                            try (PreparedStatement insStmt = conn.prepareStatement(insertSQL)) {
                                insStmt.setInt(1, idUser);
                                insStmt.setInt(2, idFacture);
                                insStmt.executeUpdate();
                            }
                        }
                    }
                    tableModel.addRow(new Object[]{idUser, nom, "⚠️ Facture impayée depuis " + jours + " jours", idFacture});
                } else if ("O".equals(etat)) {
                    String delSQL = "DELETE FROM rappel WHERE id_user = ? AND id_facture = ?";
                    try (PreparedStatement delStmt = conn.prepareStatement(delSQL)) {
                        delStmt.setInt(1, idUser);
                        delStmt.setInt(2, idFacture);
                        delStmt.executeUpdate();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void chargerTout() {
        tableModel.setRowCount(0);
        chargerMessages();
        chargerRappels();
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/javaswing_app", "root", "");
    }
}
