package interfasseAdmin;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GlobalNotificationsPanel extends JPanel {
    private JTable table;
    private DefaultListModel<String> notificationsModel;
    private ServerSocket serverSocket; // Socket pour l'écoute
    private int lastUserId = -1;
    public GlobalNotificationsPanel() {
        setLayout(new BorderLayout());
        JLabel title = new JLabel("Notifications Globales", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        notificationsModel = new DefaultListModel<>();
        JList<String> notificationsList = new JList<>(notificationsModel);
        add(new JScrollPane(notificationsList), BorderLayout.CENTER);

        // Boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnEnvoyerRappel = new JButton("Envoyer Rappel");
        JButton btnAutomatiser = new JButton("Automatiser Rappels");
        JButton btnActualiser = new JButton("Actualiser"); // Nouveau bouton
        buttonPanel.add(btnEnvoyerRappel);
        buttonPanel.add(btnAutomatiser);
        buttonPanel.add(btnActualiser);
        add(buttonPanel, BorderLayout.SOUTH);

        // Charger les messages et démarrer le serveur
        chargerMessages();
//        new Thread(this::recevoirMessages).start(); // Démarrer l'écouteur

        // Actions
        btnEnvoyerRappel.addActionListener(e -> envoyerRappelManuel());
        btnAutomatiser.addActionListener(e -> automatiserRappels());
        
        btnActualiser.addActionListener(e -> {
            // Relancer les messages de notification
            if (serverSocket == null || serverSocket.isClosed()) {
                new Thread(this::recevoirMessages).start(); // Démarrer l'écoute si ce n'est pas déjà fait
            } else {
                JOptionPane.showMessageDialog(this, "Le serveur est déjà en cours d'écoute.");
            }
        });
    }

    private void recevoirMessages() {
        try {
            serverSocket = new ServerSocket(5000);
            System.out.println("✅ Serveur Banque en attente de connexions...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String messageComplet = in.readLine();

                if (messageComplet != null && messageComplet.contains("#")) {
                    String[] parts = messageComplet.split("#");
                    String message = parts[0];
                    int id = Integer.parseInt(parts[1]);
                    lastUserId = id; // On garde l'id pour l'utiliser dans rappel manuel

                    System.out.println("📥 Message reçu: " + message + " | id: " + id);
                    storeMessageInDatabase(message, id);
                    notificationsModel.addElement(message);
                }

                in.close();
                clientSocket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    

    private void storeMessageInDatabase(String message , int id) {
        String sql = "INSERT INTO notification (id_user,message) VALUES (?,?)"; // Assurez-vous que la table "notification" existe
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/javaswing_app", "root", "");
             PreparedStatement stmt = conn.prepareStatement(sql)) {
        	stmt.setInt(1, id);
            stmt.setString(2, message);
            stmt.executeUpdate();
            System.out.println("✅ Message stocké dans la base de données.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du stockage du message en base de données.");
        }
    }

    private void chargerMessages() {
        notificationsModel.clear();
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/javaswing_app", "root", "");
             PreparedStatement stmt = conn.prepareStatement("SELECT message FROM notification")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String message = rs.getString("message");
                notificationsModel.addElement(message); // Ajouter à la liste
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des messages.");
        }
    }

    private void envoyerRappelManuel() {
        notificationsModel.clear();
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/javaswing_app", "root", "");
             PreparedStatement stmt = conn.prepareStatement("insert INto notification where id_user=?")) {
        	   stmt.setInt(1, lastUserId); 
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String message = rs.getString("message");
                notificationsModel.addElement(message); // Ajouter à la liste
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des messages.");
        }
       
    }

    private void automatiserRappels() {
        JOptionPane.showMessageDialog(this, "🤖 Système d'envoi automatique des rappels activé.");
    }

    // Main pour test
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Notifications Globales");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(500, 400);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new GlobalNotificationsPanel());
            frame.setVisible(true);
        });
    }
}