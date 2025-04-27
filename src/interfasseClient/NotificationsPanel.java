package interfasseClient;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

public class NotificationsPanel extends JPanel {
	    private DefaultListModel<String> notificationListModel;
	    private JList<String> notificationList;
	    private JButton markAllReadButton;

	    public NotificationsPanel() {
	        setLayout(new BorderLayout()); 

	        JLabel titleLabel = new JLabel("Notifications Client");
	        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
	        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	        add(titleLabel, BorderLayout.NORTH);

	        // Liste des notifications
	        notificationListModel = new DefaultListModel<>();
	        notificationList = new JList<>(notificationListModel);
	        notificationList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	        JScrollPane scrollPane = new JScrollPane(notificationList);
	        add(scrollPane, BorderLayout.CENTER);

	        // Bouton pour marquer toutes les notifications comme lues
	        markAllReadButton = new JButton("Tout marquer comme lu");
	        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
	        buttonPanel.add(markAllReadButton);
	        add(buttonPanel, BorderLayout.SOUTH);

	        // Charger les notifications
	        loadNotifications();

	        // Action bouton "marquer comme lu"
	        markAllReadButton.addActionListener(e -> {
	            clearNotifications();
	        });
	    }

	    // Charger les notifications depuis la base de données
	    private void loadNotifications() {
	        notificationListModel.clear();

	        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/gestion_eau", "root", "");
	             Statement stmt = conn.createStatement();
	             ResultSet rs = stmt.executeQuery("SELECT message FROM notifications")) {

	            while (rs.next()) {
	                notificationListModel.addElement(rs.getString("message"));
	            }

	        } catch (SQLException ex) {
	            ex.printStackTrace();
	            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des notifications.");
	        }
	    }

	    // Vider la liste de notifications (simule la lecture)
	    private void clearNotifications() {
	        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/gestion_eau", "root", "");
	             Statement stmt = conn.createStatement()) {

	            stmt.executeUpdate("DELETE FROM notifications");
	            notificationListModel.clear();
	            JOptionPane.showMessageDialog(this, "Toutes les notifications ont été marquées comme lues.");

	        } catch (SQLException ex) {
	            ex.printStackTrace();
	            JOptionPane.showMessageDialog(this, "Erreur lors de la suppression des notifications.");
	        }
	    }

	    // Tester le panel
	    public static void main(String[] args) {
	        SwingUtilities.invokeLater(new Runnable() {
	            public void run() {
	                JFrame frame = new JFrame("Paiement Facture");
	                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	                frame.setSize(400, 300);
	                frame.setLocationRelativeTo(null);
	                frame.setContentPane(new PaymentPanel());
	                frame.setVisible(true);
	            }
	        });
	    }
}
