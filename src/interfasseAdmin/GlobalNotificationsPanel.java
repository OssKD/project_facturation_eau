package interfasseAdmin;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

public class GlobalNotificationsPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField filterField;
    private ServerSocket serverSocket; // Socket pour l'écoute
    private int lastUserId = -1;
    public GlobalNotificationsPanel() {
        setLayout(new BorderLayout());
        JLabel title = new JLabel("Notifications Globales", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        // Tableau pour afficher les messages
        tableModel = new DefaultTableModel(new Object[]{"ID", "Nom", "Message"}, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Charger les messages dès le début
        
        chargerTout();
        
        // Boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnEnvoyerRappel = new JButton("Envoyer Rappel");
      
        JButton btnActualiser = new JButton("Actualiser"); // Nouveau bouton
        buttonPanel.add(btnEnvoyerRappel);
        
        buttonPanel.add(btnActualiser);
        add(buttonPanel, BorderLayout.SOUTH);
 

        // Actions
        btnEnvoyerRappel.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                // Récupérer l'ID de la ligne sélectionnée
                String idStr = table.getValueAt(selectedRow, 0).toString(); // "Utilisateur X"
                String message=table.getValueAt(selectedRow, 2).toString();
            // Extraire l'ID numérique à partir du texte "Utilisateur X"
                int id = -1;
                try {
                    id = Integer.parseInt(idStr.replace("Utilisateur ", "").trim());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "ID utilisateur invalide : " + idStr);
                    return;
                }

                // Appeler la méthode d’envoi de rappel ou afficher un message
                EnvoyermessageRappel(message ,Integer.parseInt(idStr));
                
            } else {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner une ligne pour envoyer un rappel.");
            }
        });

      
        btnActualiser.addActionListener(e -> {
            // Relancer les messages de notification
            if (serverSocket == null || serverSocket.isClosed()) {
                new Thread(this::recevoirMessages).start(); // Démarrer l'écoute si ce n'est pas déjà fait
            } else {
                JOptionPane.showMessageDialog(this, "Le serveur est déjà en cours d'écoute.");
            }
        });
    }
     public void recevoirMessages() {
        try {
            serverSocket = new ServerSocket(5000);
            System.out.println("✅ Serveur Banque en attente de connexions...");

            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())) {
                     
                    System.out.println("Connexion acceptée de " + clientSocket.getInetAddress());

                    // Lecture d'un message string
                    String messageComplet = (String) in.readObject();
                    // Lecture de l'objet Map
                    Map<String, Object> paiement = (Map<String, Object>) in.readObject();

                    System.out.println("📥 Message reçu : " + messageComplet);
                    System.out.println("📥 Données de paiement reçues : ");
                    paiement.forEach((cle, valeur) -> System.out.println("  " + cle + " = " + valeur));

                    // Récupérer les valeurs spécifiques
                    Integer id = (Integer) paiement.get("idUser");
                    Integer id_facture = (Integer) paiement.get("idFacture");
                    String description = (String) paiement.get("description");
                    String mes=testMessage(description);
                    
                    // Vous pouvez traiter les données ici
                     storeMessageInDatabase(mes, id); // Exemple de traitement
                } catch (IOException | ClassNotFoundException e) {
                    System.err.println("Erreur lors de la réception des messages : " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Erreur de création du serveur : " + e.getMessage());
        }
    }
 
     private String testMessage(String message) {
         switch (message) {
         case "A":
             return "payment bian effectuer .";
         case "B":
             return "Erreur : montant insufisant";
         case "C":
             return "Erreur :il n'ys pas un compte";
         default:
             return "Type de message non reconnu.";
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
     
     private void EnvoyermessageRappel(String message , int id ) {
         String sql = "INSERT INTO rappel (id_user,message) VALUES (?,?)"; // Assurez-vous que la table "notification" existe
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
    // Ne pas effacer le tableau ici !
    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/javaswing_app", "root", "");
         PreparedStatement stmt = conn.prepareStatement("SELECT u.nom, n.id_user, n.message  FROM notification n JOIN user u ON n.id_user = u.id_user;")) {
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            int id = rs.getInt("id_user");
            String nom = rs.getString("nom");
            String message = rs.getString("message");
            tableModel.addRow(new Object[]{ id, nom, message });
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Erreur lors du chargement des messages.");
    }
    }
	
	private void chargerRappels() {
	     // Ne pas effacer le tableau ici non plus
	String sql = "SELECT u.nom, f.id_user, f.mois FROM facture f JOIN user u ON f.id_user = u.id_user WHERE etat_payment='N'";
	try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/javaswing_app", "root", "");
	     PreparedStatement stmt = conn.prepareStatement(sql);
	     ResultSet rs = stmt.executeQuery()) {
	
	    LocalDate currentDate = LocalDate.now();
	    while (rs.next()) {
	        int idUser = rs.getInt("id_user");
	        String nom = rs.getString("nom");
	        String moisStr = rs.getString("mois");
	        try {
	            LocalDate moisFacture;
	            if (moisStr.length() == 7) {
	                moisFacture = LocalDate.parse(moisStr + "-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	            } else {
	                moisFacture = LocalDate.parse(moisStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	            }
	            long diffDays = ChronoUnit.DAYS.between(moisFacture, currentDate);
	            if (diffDays > 0 && diffDays < 15) {
	                String message = "⚠️ Facture impayée depuis " + diffDays + " jours.";
	                tableModel.addRow(new Object[]{idUser, nom, message});
	            }
	        } catch (DateTimeParseException e) {
	            System.err.println("Format de date invalide pour id_user=" + idUser + " : " + moisStr);
	        }
	    }
	
	} catch (SQLException ex) {
	    ex.printStackTrace();
	    JOptionPane.showMessageDialog(this, "❌ Erreur lors du chargement des rappels.");
	    }
	}

	private void chargerTout() {
	    tableModel.setRowCount(0); // Effacer une seule fois
	    chargerMessages();
	    chargerRappels();
	}
	
}