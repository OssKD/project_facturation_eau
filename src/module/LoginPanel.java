package module;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import interfasseAdmin.AdminDashboard;
import interfasseClient.ClientDashboard;

	public class LoginPanel extends JPanel {
	    private JTextField emailField;
	    private JPasswordField passwordField;
	    private JButton loginButton, forgotPasswordButton, registerButton;

	    public LoginPanel(JFrame frame) {
	        setLayout(new GridBagLayout());
	        GridBagConstraints gbc = new GridBagConstraints();

	        JLabel emailLabel = new JLabel("Email:");
	        emailField = new JTextField(20);

	        JLabel passwordLabel = new JLabel("Mot de passe:");
	        passwordField = new JPasswordField(20);

	        loginButton = new JButton("Connexion");
	        forgotPasswordButton = new JButton("Mot de passe oublié ?");
	        registerButton = new JButton("S'inscrire");

	        // Placement des composants
	        gbc.insets = new Insets(10, 10, 10, 10);
	        gbc.gridx = 0; gbc.gridy = 0;
	        add(emailLabel, gbc);
	        gbc.gridx = 1;
	        add(emailField, gbc); 
 
	        gbc.gridx = 0; gbc.gridy = 1;
	        add(passwordLabel, gbc);
	        gbc.gridx = 1;
	        add(passwordField, gbc);

	        gbc.gridx = 0; gbc.gridy = 2;
	        gbc.gridwidth = 2;
	        add(loginButton, gbc);

	        gbc.gridy = 3;
	        add(forgotPasswordButton, gbc);

	        gbc.gridy = 4;
	        add(registerButton, gbc);

	        // Action de Connexion
	        loginButton.addActionListener(e -> handleLogin(frame));

	        // Navigation (simulation)
	        forgotPasswordButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Réinitialisation du mot de passe..."));
//	        registerButton.addActionListener(e -> {
//	            JFrame registerFrame = new JFrame("Créer un compte");
//	            registerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); //بوحدها
//	            registerFrame.setSize(800, 600);
//	            registerFrame.setLocationRelativeTo(null);
//	            registerFrame.setContentPane(new RegisterPanel(registerFrame));
//	            registerFrame.setVisible(true);
//	            
//	            ClientDashboard clientDashboard = new ClientDashboard(String email);
//				clientDashboard.setVisible(false);
//	        });

	    }

	    private void handleLogin(JFrame frame) {
	        String email = emailField.getText();
	        String password = new String(passwordField.getPassword());

	        if (email.isEmpty() || password.isEmpty()) {
	            JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs.");
	            return;
	        }
            //pour ;a connectio avec base de donner
	        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/javaswing_app", "root", "");
	             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM user WHERE email = ? AND password = ?")) {

	            stmt.setString(1, email);
	            stmt.setString(2, password); // En production : hasher le mot de passe !
	            ResultSet rs = stmt.executeQuery();

	            if (rs.next()) {
	                int id = rs.getInt("id_user"); // Récupérer l'id_user depuis la base

	                if (email.equals("admin") && password.equals("123")) {
	                    AdminDashboard dashboard = new AdminDashboard();
	                    dashboard.setVisible(true);
	                } else {
	                    ClientDashboard dashboard = new ClientDashboard(id); // Passer l'id_user 
	                    dashboard.setVisible(true);
	                }
	                // Ferme l'ancienne fenêtre
	                frame.dispose();
	            } else {
	                JOptionPane.showMessageDialog(this, "Email ou mot de passe incorrect.");
	            }

	        } catch (SQLException ex) {
	            ex.printStackTrace();
	            JOptionPane.showMessageDialog(this, "Erreur de connexion à la base de données.");
	        }
	    }
	}

	


