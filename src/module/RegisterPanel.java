package module;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class RegisterPanel extends JPanel {
	    private JTextField nomField, prenomField, emailField, compteurField, adresseField, telephoneField;
	    private JPasswordField passwordField;
	    private JButton registerButton, backButton;

	    public RegisterPanel(JFrame frame) {
	        setLayout(new GridBagLayout());
	        GridBagConstraints gbc = new GridBagConstraints();

	        nomField = new JTextField(20);
	        prenomField = new JTextField(20);
	        emailField = new JTextField(20);
	        compteurField = new JTextField(20);
	        adresseField = new JTextField(20);
	        telephoneField = new JTextField(20);
	        passwordField = new JPasswordField(20);

	        registerButton = new JButton("S'inscrire");
	        backButton = new JButton("Retour");

	        // Ajout des champs
	        addField("Nom", nomField, gbc, 0);
	        addField("Prénom", prenomField, gbc, 1);
	        addField("Email", emailField, gbc, 2);
	        addField("Numéro compteur", compteurField, gbc, 3);
	        addField("Adresse", adresseField, gbc, 4);
	        addField("Téléphone", telephoneField, gbc, 5);
	        addField("Mot de passe", passwordField, gbc, 6);

	        // Boutons
	        gbc.gridx = 0;
	        gbc.gridy = 7;
	        gbc.gridwidth = 2;
	        gbc.insets = new Insets(10, 0, 10, 0);
	        add(registerButton, gbc);

	        gbc.gridy = 8;
	        add(backButton, gbc);

	        // Action
	        registerButton.addActionListener(e -> handleRegister());
	        backButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Retour à la connexion..."));
	    }

	    private void addField(String label, JComponent field, GridBagConstraints gbc, int row) {
	        gbc.insets = new Insets(5, 10, 5, 10);
	        gbc.gridx = 0;
	        gbc.gridy = row;
	        add(new JLabel(label + " : "), gbc);
	        gbc.gridx = 1;
	        add(field, gbc);
	    }

	    private void handleRegister() {
	        String nom = nomField.getText();
	        String prenom = prenomField.getText();
	        String email = emailField.getText();
	        String compteur = compteurField.getText();
	        String adresse = adresseField.getText();
	        String telephone = telephoneField.getText();
	        String password = new String(passwordField.getPassword());

	        // Validation simple
	        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || compteur.isEmpty()
	            || adresse.isEmpty() || telephone.isEmpty() || password.isEmpty()) {
	            JOptionPane.showMessageDialog(this, "Tous les champs sont obligatoires.");
	            return;
	        }

	        if (!email.contains("@")) {
	            JOptionPane.showMessageDialog(this, "Email invalide.");
	            return;
	        }

	        if (!telephone.matches("\\d+")) {
	            JOptionPane.showMessageDialog(this, "Téléphone invalide.");
	            return;
	        }

	        if (password.length() < 6) {
	            JOptionPane.showMessageDialog(this, "Le mot de passe doit contenir au moins 6 caractères.");
	            return;
	        }

	        
	        /*
	         Exemple de table MySQL à créer (users)
				sql
				r
				CREATE TABLE users (
				    id INT AUTO_INCREMENT PRIMARY KEY,
				    nom VARCHAR(50),
				    prenom VARCHAR(50),
				    email VARCHAR(100) UNIQUE,
				    compteur VARCHAR(50),
				    adresse TEXT,
				    telephone VARCHAR(20),
				    password VARCHAR(100)
				);
					          
	       */
	        
	        
	        // Enregistrement dans MySQL
	        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/gestion_eau", "root", "");
	             PreparedStatement stmt = conn.prepareStatement(
	                     "INSERT INTO users (nom, prenom, email, compteur, adresse, telephone, password) VALUES (?, ?, ?, ?, ?, ?, ?)")) {

	            stmt.setString(1, nom);
	            stmt.setString(2, prenom);
	            stmt.setString(3, email);
	            stmt.setString(4, compteur);
	            stmt.setString(5, adresse);
	            stmt.setString(6, telephone);
	            stmt.setString(7, password); // à hasher en prod

	            stmt.executeUpdate();
	            JOptionPane.showMessageDialog(this, "Inscription réussie !");
	        } catch (SQLException ex) {
	            ex.printStackTrace();
	            JOptionPane.showMessageDialog(this, "Erreur lors de l'inscription.");
	        }
	    }
	}


