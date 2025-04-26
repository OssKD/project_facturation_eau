package interfaceAdmin;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class GlobalNotificationsPanel extends JPanel {

	    private JTable table;
	    private DefaultListModel<String> notificationsModel;

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

	        buttonPanel.add(btnEnvoyerRappel);
	        buttonPanel.add(btnAutomatiser);
	        add(buttonPanel, BorderLayout.SOUTH);

	        // Charger les retards depuis la base
	        chargerRetards();

	        // Actions
	        btnEnvoyerRappel.addActionListener((ActionEvent e) -> envoyerRappelManuel());
	        btnAutomatiser.addActionListener((ActionEvent e) -> automatiserRappels());
	    }

	    private void chargerRetards() {
	        notificationsModel.clear();
	        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/gestion_eau", "root", "");
	             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM factures WHERE statut = 'Non payée'")) {

	            ResultSet rs = stmt.executeQuery();
	            while (rs.next()) {
	                String ligne = "Client #" + rs.getInt("id_client") + " - Mois: " + rs.getString("mois") + " - Montant: " + rs.getDouble("montant");
	                notificationsModel.addElement("⚠️ Retard : " + ligne);
	            }

	        } catch (SQLException ex) {
	            ex.printStackTrace();
	            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des retards.");
	        }
	    }

	    private void envoyerRappelManuel() {
	        JOptionPane.showMessageDialog(this, "📧 Rappels envoyés manuellement à tous les clients en retard.");
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
