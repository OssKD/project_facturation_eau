package interfasseClient;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ProfilePanel extends JPanel {
	    private JTextField phoneField, addressField ,emailField;
	    private JLabel phoneLabel  , adressLabel ,emaillabel;
	    private JButton editButton, saveButton;
	    private String userEmail;

	    public ProfilePanel(String userEmail) {
	        this.userEmail = userEmail; // L'email de l'utilisateur passé à ce panel

	        setLayout(new GridBagLayout());
	        GridBagConstraints gbc = new GridBagConstraints();
	        
	        // Zones de texte
	        emailField = new JTextField(20);
	        phoneField = new JTextField(20);
	        addressField = new JTextField(20);
            
	        emaillabel = new JLabel(userEmail);
	        
	        
	        
	        
	        // Boutons
	        editButton = new JButton("Modifier");
	        saveButton = new JButton("Enregistrer");

	        saveButton.setEnabled(false); // Désactiver le bouton Enregistrer au début

	        gbc.insets = new Insets(10, 10, 10, 10);

	        // Email (non éditable)
	        gbc.gridx = 0; gbc.gridy = 0;
	        add(new JLabel("Email : "), gbc);
	        gbc.gridx = 1; gbc.gridy = 0;
	        add(emaillabel, gbc);
		    gbc.gridx = 1;         
	        add(emailField, gbc);

	        // Téléphone
	        gbc.gridx = 0; gbc.gridy = 1;
	        add(new JLabel("Téléphone : "), gbc);
	        gbc.gridx = 1;
	        add(phoneField, gbc);

	        // Adresse
	        gbc.gridx = 0; gbc.gridy = 2;
	        add(new JLabel("Adresse : "), gbc);
	        gbc.gridx = 1;
	        add(addressField, gbc);

	        // Boutons
	        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
	        add(editButton, gbc);

	        gbc.gridy = 4;
	        add(saveButton, gbc);

	        // Charger les données existantes
	        loadUserData();

	        // Actions des boutons
	        editButton.addActionListener(e -> enableEditing());
	        saveButton.addActionListener(e -> saveChanges());
	    }

	    // Charger les informations de l'utilisateur depuis la base de données
	    private void loadUserData() {
	        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/gestion_eau", "root", "");
	             PreparedStatement stmt = conn.prepareStatement("SELECT phone, address FROM users WHERE email = ?")) {
	            stmt.setString(1, userEmail);
	            ResultSet rs = stmt.executeQuery();
	            
	            if (rs.next()) {
	                phoneField.setText(rs.getString("phone"));
	                addressField.setText(rs.getString("address"));
	            } else {
	                JOptionPane.showMessageDialog(this, "Utilisateur non trouvé.");
	            }

	        } catch (SQLException ex) {
	            ex.printStackTrace();
	            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des informations.");
	        }
	    }

	    // Permet de modifier les informations
	    private void enableEditing() {
	        phoneField.setEditable(true);
	        addressField.setEditable(true);
	        saveButton.setEnabled(true); // Activer le bouton Enregistrer
	        editButton.setEnabled(false); // Désactiver le bouton Modifier après modification
	    }

	    // Enregistrer les modifications
	    private void saveChanges() {
	        String newPhone = phoneField.getText();
	        String newAddress = addressField.getText();

	        if (newPhone.isEmpty() || newAddress.isEmpty()) {
	            JOptionPane.showMessageDialog(this, "Tous les champs doivent être remplis.");
	            return;
	        }

	        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/javaswing_app", "root", "");
	             PreparedStatement stmt = conn.prepareStatement("UPDATE users SET phone = ?, address = ? WHERE email = ?")) {
	            
	            stmt.setString(1, newPhone);
	            stmt.setString(2, newAddress);
	            stmt.setString(3, userEmail);

	            int rowsUpdated = stmt.executeUpdate();

	            if (rowsUpdated > 0) {
	                JOptionPane.showMessageDialog(this, "Informations mises à jour avec succès.");
	                saveButton.setEnabled(false); // Désactiver le bouton Enregistrer
	                editButton.setEnabled(true); // Réactiver le bouton Modifier
	                phoneField.setEditable(false);
	                addressField.setEditable(false);
	            } else {
	                JOptionPane.showMessageDialog(this, "Erreur lors de l'enregistrement des modifications.");
	            }

	        } catch (SQLException ex) {
	            ex.printStackTrace();
	            JOptionPane.showMessageDialog(this, "Erreur de connexion à la base de données.");
	        }
	    }
	}



