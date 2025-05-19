package interfasseClient;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ProfilePanel extends JPanel {
    private JTextField phoneField, addressField, nomField, prenomField, compteField,emailField;

    private JButton editButton, saveButton;
    private int id;

    // Design constants
    private static final Color BACKGROUND_COLOR = new Color(245, 250, 255);
    private static final Color PRIMARY_COLOR = new Color(52, 152, 219);
    private static final Color LABEL_COLOR = new Color(50, 50, 50);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font FIELD_FONT = new Font("Segoe UI", Font.PLAIN, 16);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 15);

    public ProfilePanel(int id_user) {
        this.id = id_user;
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        // Title
        JLabel title = new JLabel("👤   Mon Profil", JLabel.CENTER);
        title.setFont(TITLE_FONT);
        title.setForeground(PRIMARY_COLOR);
        title.setBorder(BorderFactory.createEmptyBorder(25, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 20, 12, 20);
        gbc.anchor = GridBagConstraints.WEST;

        // Components
        emailField = createTextField();
        phoneField = createTextField();
        addressField = createTextField();
        nomField = createTextField();
        prenomField = createTextField();
        compteField = createTextField();

        // Fields are not editable
        emailField.setEditable(false);
        phoneField.setEditable(false);
        addressField.setEditable(false);
        nomField.setEditable(false);
        prenomField.setEditable(false);
        compteField.setEditable(false);

        // Email
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(createFormLabel("Email :"), gbc);
        gbc.gridx = 1;
        formPanel.add(emailField, gbc);

        // Téléphone
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(createFormLabel("Téléphone :"), gbc);
        gbc.gridx = 1;
        formPanel.add(phoneField, gbc);

        // Adresse
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(createFormLabel("Adresse :"), gbc);
        gbc.gridx = 1;
        formPanel.add(addressField, gbc);

        // Nom
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(createFormLabel("Nom :"), gbc);
        gbc.gridx = 1;
        formPanel.add(nomField, gbc);

        // Prénom
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(createFormLabel("Prénom :"), gbc);
        gbc.gridx = 1;
        formPanel.add(prenomField, gbc);

        // Numéro de Compte
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(createFormLabel("Numéro de Compte :"), gbc);
        gbc.gridx = 1;
        formPanel.add(compteField, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        editButton = createStyledButton("Modifier");
        saveButton = createStyledButton("Enregistrer");
        saveButton.setEnabled(false);

        buttonPanel.add(editButton);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(saveButton);

        // Add components to main panel
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load data and add actions
        loadUserData();
        editButton.addActionListener(e -> enableEditing());
        saveButton.addActionListener(e -> saveChanges());
    }

    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(LABEL_FONT);
        label.setForeground(LABEL_COLOR);
        return label;
    }

    private JTextField createTextField() {
        JTextField tf = new JTextField(20);
        tf.setFont(FIELD_FONT);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        return tf;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR.darker(), 1),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        button.setOpaque(true);
        return button;
    }

    private void loadUserData() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/javaswing_app", "root", "");
             PreparedStatement stmt = conn.prepareStatement("SELECT email, tel, adress_h, nom, prenom, numero_compteur FROM user WHERE id_user = ?")) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                emailField.setText(rs.getString("email"));
                phoneField.setText(rs.getString("tel"));
                addressField.setText(rs.getString("adress_h"));
                nomField.setText(rs.getString("nom"));
                prenomField.setText(rs.getString("prenom"));
                compteField.setText(rs.getString("numero_compteur"));
            } else {
                JOptionPane.showMessageDialog(this, "Utilisateur non trouvé.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des informations.");
        }
    }

    private void enableEditing() {
        emailField.setEditable(true);
        phoneField.setEditable(true);
        addressField.setEditable(true);
        nomField.setEditable(true);
        prenomField.setEditable(true);
        compteField.setEditable(true);
        saveButton.setEnabled(true);
        editButton.setEnabled(false);
    }

    private void saveChanges() {
        String newEmail = emailField.getText().trim(); // Ajoutez cette ligne
        String newPhone = phoneField.getText().trim();
        String newAddress = addressField.getText().trim();
        String newNom = nomField.getText().trim();
        String newPrenom = prenomField.getText().trim();
        String newCompte = compteField.getText().trim();
        
        // Vérifiez que tous les champs ne sont pas vides
        if (newEmail.isEmpty() || newPhone.isEmpty() || newAddress.isEmpty() || newNom.isEmpty() || newPrenom.isEmpty() || newCompte.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tous les champs doivent être remplis.");
            return;
        }
        
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/javaswing_app", "root", "");
             PreparedStatement stmt = conn.prepareStatement("UPDATE user SET email = ?, tel = ?, adress_h = ?, nom = ?, prenom = ?, numero_compteur = ? WHERE id_user = ?")) { 
        	// Ajoutez email dans la requête
            stmt.setString(1, newEmail); // Ajoutez cette ligne
            stmt.setString(2, newPhone);
            stmt.setString(3, newAddress);
            stmt.setString(4, newNom);
            stmt.setString(5, newPrenom);
            stmt.setString(6, newCompte);
            stmt.setInt(7, id); // Notez le décalage de l'index
            
            int updated = stmt.executeUpdate();
            if (updated > 0) {
                JOptionPane.showMessageDialog(this, "Informations mises à jour avec succès.");
                // Rendre tous les champs non éditables
                emailField.setEditable(false); // Ajoutez cette ligne
                phoneField.setEditable(false);
                addressField.setEditable(false);
                nomField.setEditable(false);
                prenomField.setEditable(false);
                compteField.setEditable(false);
                saveButton.setEnabled(false);
                editButton.setEnabled(true);
            } else {
                JOptionPane.showMessageDialog(this, "Erreur lors de la mise à jour.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur de connexion à la base de données.");
        }
    }
}