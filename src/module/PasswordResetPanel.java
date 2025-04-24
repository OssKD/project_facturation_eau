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

public class PasswordResetPanel extends JPanel {
    private JTextField emailField;
    private JPasswordField newPasswordField;
    private JButton resetButton, backButton;

    public PasswordResetPanel(JFrame frame) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        emailField = new JTextField(20);
        newPasswordField = new JPasswordField(20);
        resetButton = new JButton("Changer mot de passe");
        backButton = new JButton("Retour");

        gbc.insets = new Insets(10, 10, 10, 10);

        // Email
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Email : "), gbc);
        gbc.gridx = 1;
        add(emailField, gbc);

        // Nouveau mot de passe
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Nouveau mot de passe : "), gbc);
        gbc.gridx = 1;
        add(newPasswordField, gbc);

        // Boutons
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        add(resetButton, gbc);

        gbc.gridy = 3;
        add(backButton, gbc);

        // Actions
        resetButton.addActionListener(e -> handleReset());
        backButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Retour à la connexion..."));
    }

    private void handleReset() {
        String email = emailField.getText();
        String newPassword = new String(newPasswordField.getPassword());

        if (email.isEmpty() || newPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tous les champs sont obligatoires.");
            return;
        }

        if (!email.contains("@")) {
            JOptionPane.showMessageDialog(this, "Email invalide.");
            return;
        }

        if (newPassword.length() < 6) {
            JOptionPane.showMessageDialog(this, "Le mot de passe doit contenir au moins 6 caractères.");
            return;
        }

        
        /*
Comportement attendu :

Email trouvé : Si l'email est trouvé en base, l'utilisateur peut choisir de changer son mot de passe ou non.

Si oui, on effectue la mise à jour.

Si non, on affiche un message disant que l'option a été annulée.

Email non trouvé : Si l'email n'existe pas en base, on avertit l'utilisateur qu'aucun compte n’a été trouvé avec cet email.*/
        
        
        // Vérifie si l'utilisateur existe dans la base
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/gestion_eau", "root", "");
             PreparedStatement checkStmt = conn.prepareStatement("SELECT * FROM users WHERE email = ?")) {

            checkStmt.setString(1, email);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                // Si l'email existe, permet à l'utilisateur de changer le mot de passe
                int option = JOptionPane.showConfirmDialog(this,
                        "Un compte a été trouvé avec cet email. Voulez-vous changer votre mot de passe ?",
                        "Changer mot de passe", JOptionPane.YES_NO_OPTION);

                if (option == JOptionPane.YES_OPTION) {
                    // Mise à jour du mot de passe
                    PreparedStatement updateStmt = conn.prepareStatement("UPDATE users SET password = ? WHERE email = ?");
                    updateStmt.setString(1, newPassword); // À hasher en production
                    updateStmt.setString(2, email);
                    updateStmt.executeUpdate();

                    JOptionPane.showMessageDialog(this, "Mot de passe mis à jour avec succès !");
                } else {
                    JOptionPane.showMessageDialog(this, "Mot de passe non changé.");
                }
            } else {
                // Si l'email n'existe pas dans la base
                JOptionPane.showMessageDialog(this, "Aucun compte trouvé avec cet email.");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur de connexion à la base de données.");
        }
    }
}

