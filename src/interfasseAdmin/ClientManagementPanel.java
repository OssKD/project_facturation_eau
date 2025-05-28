package interfasseAdmin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ObjectInputFilter.Config;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import Config.AjouteBD;
import Config.Delete;
import Config.ModifierBD;
public class ClientManagementPanel extends JPanel {
    private JTextField searchField;
    private JButton searchButton, addButton, updateButton, deleteButton;
    private JTable clientTable;
    private DefaultTableModel tableModel;
    
    // Couleurs et styles pour le design
    private final Color MAIN_COLOR = new Color(41, 128, 185);
    private final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private final Color TABLE_HEADER_COLOR = new Color(52, 73, 94);
    private final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private final Font NORMAL_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    private final Font BUTTON_FONT = new Font("Segoe UI", Font.PLAIN, 12);

    public ClientManagementPanel() {
        setLayout(new BorderLayout(0, 15));
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Panel de titre
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Gestion des Clients");
        titleLabel.setFont(TITLE_FONT);
        titlePanel.add(titleLabel, BorderLayout.WEST);
        add(titlePanel, BorderLayout.NORTH);

        // Panel principal contenant recherche et table
        JPanel mainPanel = new JPanel(new BorderLayout(0, 15));
        mainPanel.setOpaque(false);

        // Panel de recherche avec un design amélioré
        JPanel searchPanel = createSearchPanel();
        mainPanel.add(searchPanel, BorderLayout.NORTH);

        // Table des clients avec un design amélioré
        JPanel tablePanel = createTablePanel();
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        // Boutons d'action en bas
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);

        // Charger tous les clients au démarrage
        loadClients("");

        // Configuration des actions des boutons
        setupButtonActions();
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(10, 15, 10, 15)
        ));

        JLabel searchLabel = new JLabel("Rechercher par compteur :");
        searchLabel.setFont(NORMAL_FONT);
        panel.add(searchLabel);

        searchField = new JTextField(20);
        searchField.setFont(TITLE_FONT);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        panel.add(searchField);

        searchButton = new JButton("Rechercher");
        
        searchButton.setBackground(new Color(23, 76, 60));
        searchButton.setForeground(Color.black);
        searchButton.setFont(BUTTON_FONT);
        searchButton.setFocusPainted(false);
        searchButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(23, 76, 60)),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)));
        
        
        panel.add(searchButton);

        JButton resetButton = new JButton("Réinitialiser");
        resetButton.setBackground(new Color(10, 76, 6));
        resetButton.setForeground(Color.black);
        resetButton.setFont(BUTTON_FONT);
        resetButton.setFocusPainted(false);
        resetButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(23, 76, 60)),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)));
        
        resetButton.addActionListener(e -> {
            searchField.setText("");
            loadClients("");
        });
        panel.add(resetButton);

        return panel;
    }
   
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));

        // Modèle de table avec colonnes
        tableModel = new DefaultTableModel(new String[]{"ID", "Nom", "Prenom", "Compteur", "Email", "Password", "Téléphone", "Adresse" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Rendre toutes les cellules non éditables
            }
        };

        clientTable = new JTable(tableModel);
        clientTable.setRowHeight(30);
        clientTable.setFont(NORMAL_FONT);
        clientTable.setSelectionBackground(new Color(21, 237, 247));
        clientTable.setSelectionForeground(Color.BLACK);
        clientTable.setShowGrid(false);
        clientTable.setIntercellSpacing(new Dimension(0, 0));
        clientTable.setFillsViewportHeight(true);

        // Style de l'en-tête du tableau
        JTableHeader header = clientTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(TABLE_HEADER_COLOR);
        header.setForeground(Color.DARK_GRAY);
        header.setPreferredSize(new Dimension(header.getWidth(), 35));
        header.setBorder(BorderFactory.createEmptyBorder());

        // Largeurs des colonnes (ajustées)
        clientTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        clientTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Nom
        clientTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Prenom
        clientTable.getColumnModel().getColumn(3).setPreferredWidth(80);  // Compteur
        clientTable.getColumnModel().getColumn(4).setPreferredWidth(150); // Email
        clientTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Password
        clientTable.getColumnModel().getColumn(6).setPreferredWidth(100); // Téléphone
        clientTable.getColumnModel().getColumn(7).setPreferredWidth(200); // Adresse

        JScrollPane scrollPane = new JScrollPane(clientTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE); // Remplacé noir par blanc

        panel.add(scrollPane, BorderLayout.CENTER);

        // Panneau d'informations
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.setBackground(new Color(240, 240, 240));
        infoPanel.setBorder(new EmptyBorder(5, 10, 10, 10));

        JLabel infoLabel = new JLabel("Sélectionnez un client pour le modifier ou le supprimer");
        infoLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        infoLabel.setForeground(new Color(1, 100, 100));
        infoPanel.add(infoLabel);

        panel.add(infoPanel, BorderLayout.SOUTH);

        return panel;
    }

    /////pour les bottone cherche et modiger et ajouuter
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panel.setOpaque(false);

        deleteButton = new JButton("Supprimer");
        deleteButton.setBackground(new Color(23, 76, 60));
        deleteButton.setForeground(Color.black);
        deleteButton.setFont(BUTTON_FONT);
        deleteButton.setFocusPainted(false);
        deleteButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(23, 76, 60)),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        
        updateButton = new JButton("Modifier");
        updateButton.setBackground(new Color(23, 76, 60));
        updateButton.setForeground(Color.black);
        updateButton.setFont(BUTTON_FONT);
        updateButton.setFocusPainted(false);
        updateButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(23, 76, 60)),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        
        addButton = new JButton("Ajouter un client");
        addButton.setBackground(new Color(23, 76, 60));
        addButton.setForeground(Color.black);
        addButton.setFont(BUTTON_FONT);
        addButton.setFocusPainted(false);
        addButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(23, 76, 60)),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        		));

        
        
        panel.add(deleteButton);
        panel.add(updateButton);
        panel.add(addButton);

        return panel;
    
    
 
        
       
    }

    private void setupButtonActions() {
        searchButton.addActionListener(e -> {
            String keyword = searchField.getText().trim();
            loadClients(keyword);
        });

        addButton.addActionListener(e -> ajouterClient());
        updateButton.addActionListener(e -> modifierClient());
        deleteButton.addActionListener(e -> supprimerClient());
    }

    // Charger les clients depuis la base de données
    private void loadClients(String keyword) {
        try {
            // Chargement du pilote JDBC (peut être nécessaire pour certaines configurations)
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/javaswing_app", "root", "");
                 PreparedStatement stmt = conn.prepareStatement(
                         "SELECT * FROM user WHERE nom like ?")) {

                stmt.setString(1, "%" + keyword + "%");
                ResultSet rs = stmt.executeQuery();

                tableModel.setRowCount(0); // Vider la table
                while (rs.next()) {
                    Vector<String> row = new Vector<>();
                    row.add(rs.getString("id_user"));     // ID
                    row.add(rs.getString("nom"));         // Nom
                    row.add(rs.getString("prenom"));      // Prénom
                    row.add(rs.getString("numero_compteur"));    // Compteur
                    row.add(rs.getString("email"));       // Email
                    row.add(rs.getString("password"));    // Password
                    row.add(rs.getString("tel"));   // Téléphone
                    row.add(rs.getString("adress_h")); 
                    tableModel.addRow(row);
                }

            } catch (SQLException e) {
                e.printStackTrace();
                showErrorDialog("Erreur de base de données", "Impossible de charger les clients: " + e.getMessage());
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            showErrorDialog("Erreur de pilote", "Pilote MySQL non trouvé");
        }
    }

	// Ajouter un client avec interface améliorée    clieeeeeeeeeeeeeeeeeeeeeeeeeeeeeent 
	private void ajouterClient() {
	    // Création des champs de formulaire avec style
	JTextField nomField = createStyledTextField();
	JTextField prenomField = createStyledTextField(); // champ Prénom
	JTextField emailField = createStyledTextField();
	JPasswordField password = createStyledPasswordField(); // champ Mot de passe
	JTextField compteurField = createStyledTextField();
	JTextField telField = createStyledTextField();
	JTextField adresseField = createStyledTextField();
	
	// Création du panel de formulaire
	JPanel formPanel = new JPanel(new GridBagLayout());
	GridBagConstraints gbc = new GridBagConstraints();
	gbc.insets = new Insets(2, 4, 4, 4);
	gbc.fill = GridBagConstraints.HORIZONTAL;
	gbc.weightx = 1.0;
	
	// Ligne 1: Nom
	gbc.gridx = 0;
	gbc.gridy = 0;
	formPanel.add(new JLabel("Nom:"), gbc);
	gbc.gridx = 1;
	formPanel.add(nomField, gbc);
	
	// Ligne 2: Prénom
	gbc.gridx = 0;
	gbc.gridy = 1;
	formPanel.add(new JLabel("Prénom:"), gbc);
	gbc.gridx = 1;
	formPanel.add(prenomField, gbc);
	
	// Ligne 3: Email
	gbc.gridx = 0;
	gbc.gridy = 2;
	formPanel.add(new JLabel("Email:"), gbc);
	gbc.gridx = 1;
	formPanel.add(emailField, gbc);
	
	// Ligne 4: Mot de passe
	gbc.gridx = 0;
	gbc.gridy = 3;
	formPanel.add(new JLabel("Mot de passe:"), gbc);
	gbc.gridx = 1;
	formPanel.add(password, gbc);
	
	// Ligne 5: Compteur
	gbc.gridx = 0;
	gbc.gridy = 4;
	formPanel.add(new JLabel("Compteur:"), gbc);
	gbc.gridx = 1;
	formPanel.add(compteurField, gbc);
	
	// Ligne 6: Téléphone
	gbc.gridx = 0;
	gbc.gridy = 5;
	formPanel.add(new JLabel("Téléphone:"), gbc);
	gbc.gridx = 1;
	formPanel.add(telField, gbc);
	
	// Ligne 7: Adresse
	gbc.gridx = 0;
	gbc.gridy = 6;
	formPanel.add(new JLabel("Adresse:"), gbc);
	gbc.gridx = 1;
	formPanel.add(adresseField, gbc);
	
	// Affichage de la boîte de dialogue
	JOptionPane optionPane = new JOptionPane(formPanel, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
	JDialog dialog = optionPane.createDialog(this, "Ajouter un nouveau client");
	dialog.setSize(400, 400);
	dialog.setLocationRelativeTo(this);
	dialog.setVisible(true);
	
	// Traitement de la réponse
	Object selectedValue = optionPane.getValue();
	if (selectedValue != null && (Integer) selectedValue == JOptionPane.OK_OPTION) {
	  
	        if (nomField.getText().trim().isEmpty() || compteurField.getText().trim().isEmpty()) {
	            showErrorDialog("Champs obligatoires", "Le nom et le numéro de compteur sont obligatoires.");
	            return;
	        }
	
	        String nom=nomField.getText();
	        String prenom=prenomField.getText();
	        String pas=password.getText();
	        String email =emailField.getText();
	        int numero_compteur=Integer.parseInt(compteurField.getText());
	        int tel=Integer.parseInt(telField.getText());
	        String addres=adresseField.getText();
	        AjouteBD.Ajouteruser(0, nom,prenom,numero_compteur,email,pas ,tel,"u",addres);
	    
	    }
	} 

	// Modifier un client avec interface améliorée
    private void modifierClient() {
    int selected = clientTable.getSelectedRow();
    if (selected == -1) {
        showWarningDialog("Aucune sélection", "Veuillez sélectionner un client à modifier.");
        return;
    }

    String id = tableModel.getValueAt(selected, 0).toString();
    String nom = tableModel.getValueAt(selected, 1).toString();
    String prenom =tableModel.getValueAt(selected, 2).toString();
    String compteur = tableModel.getValueAt(selected, 3).toString();
    String email = tableModel.getValueAt(selected, 4).toString();
    String password = tableModel.getValueAt(selected, 5).toString();
    String tel = tableModel.getValueAt(selected, 6).toString();
    String type = "u"; // À récupérer ou à gérer manuellement
    String adresse = tableModel.getValueAt(selected, 7).toString();

    JTextField nomField = createStyledTextField();
    nomField.setText(nom);

    JTextField prenomField = createStyledTextField();
    prenomField.setText(prenom);

    JTextField compteurField = createStyledTextField();
    compteurField.setText(compteur);

    JTextField emailField = createStyledTextField();
    emailField.setText(email);

    JTextField passwordField = createStyledTextField();
    passwordField.setText(password);

    JTextField telField = createStyledTextField();
    telField.setText(tel);

    JTextField typeField = createStyledTextField();
    typeField.setText(type);

    JTextField adresseField = createStyledTextField();
    adresseField.setText(adresse);

	// Création du panel de formulaire
	JPanel formPanel = new JPanel(new GridBagLayout());
	GridBagConstraints gbc = new GridBagConstraints();
	gbc.insets = new Insets(2, 4, 4, 4);
	gbc.fill = GridBagConstraints.HORIZONTAL;
	gbc.weightx = 1.0;
	
	// Ligne 1: Nom
	gbc.gridx = 0;
	gbc.gridy = 0;
	formPanel.add(new JLabel("Nom:"), gbc);
	gbc.gridx = 1;
	formPanel.add(nomField, gbc);
	
	// Ligne 2: Prénom
	gbc.gridx = 0;
	gbc.gridy = 1;
	formPanel.add(new JLabel("Prénom:"), gbc);
	gbc.gridx = 1;
	formPanel.add(prenomField, gbc);
	
	// Ligne 3: Email
	gbc.gridx = 0;
	gbc.gridy = 2;
	formPanel.add(new JLabel("Email:"), gbc);
	gbc.gridx = 1;
	formPanel.add(emailField, gbc);
	
	// Ligne 4: Mot de passe
	gbc.gridx = 0;
	gbc.gridy = 3;
	formPanel.add(new JLabel("Mot de passe:"), gbc);
	gbc.gridx = 1;
	formPanel.add(passwordField, gbc);
	
	// Ligne 5: Compteur
	gbc.gridx = 0;
	gbc.gridy = 4;
	formPanel.add(new JLabel("Compteur:"), gbc);
	gbc.gridx = 1;
	formPanel.add(compteurField, gbc);
	
	// Ligne 6: Téléphone
	gbc.gridx = 0;
	gbc.gridy = 5;
	formPanel.add(new JLabel("Téléphone:"), gbc);
	gbc.gridx = 1;
	formPanel.add(telField, gbc);
	
	// Ligne 7: Adresse
	gbc.gridx = 0;
	gbc.gridy = 6;
	formPanel.add(new JLabel("Adresse:"), gbc);
	gbc.gridx = 1;
	formPanel.add(adresseField, gbc);
	
	// Affichage de la boîte de dialogue
		JOptionPane optionPane = new JOptionPane(formPanel, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
		JDialog dialog = optionPane.createDialog(this, "Modifier le client");
		dialog.setSize(400, 400);
		dialog.setLocationRelativeTo(this);
		dialog.setVisible(true);
  

    Object selectedValue = optionPane.getValue();
    if (selectedValue != null && (Integer) selectedValue == JOptionPane.OK_OPTION) {
        try {
            if (nomField.getText().trim().isEmpty() || compteurField.getText().trim().isEmpty()) {
                showErrorDialog("Champs obligatoires", "Le nom et le numéro de compteur sont obligatoires.");
                return;
            }

            // Appel à la méthode de mise à jour dans ModifierBD
            ModifierBD.updateuser(
                    Integer.parseInt(id),
                    nomField.getText(),
                    prenomField.getText(),
                    Integer.parseInt(compteurField.getText()),
                    emailField.getText(),
                    passwordField.getText(),
                    Integer.parseInt(telField.getText()),
                    typeField.getText(),
                    adresseField.getText()
            );

            showSuccessDialog("Client modifié avec succès !");
            loadClients("");  // Recharge la liste

        } catch (NumberFormatException e) {
            showErrorDialog("Erreur de format", "Assurez-vous que le numéro de compteur et le téléphone sont des nombres valides.");
        }
    }
}

    // Supprimer un client avec confirmation améliorée
    private void supprimerClient() {
        int selected = clientTable.getSelectedRow();
        if (selected == -1) {
            showWarningDialog("Aucune sélection", "Veuillez sélectionner un client à supprimer.");
            return;
        }

        String id = tableModel.getValueAt(selected, 0).toString();
        String nom = tableModel.getValueAt(selected, 1).toString();

        // Panel de confirmation avec style
        JPanel confirmPanel = new JPanel(new BorderLayout(0, 10));
        confirmPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JLabel warningIcon = new JLabel("\u26A0"); // Symbole d'avertissement
        warningIcon.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        warningIcon.setForeground(new Color(231, 76, 60));
        
        JLabel confirmLabel = new JLabel("<html>Êtes-vous sûr de vouloir supprimer le client <b>" + nom + "</b> ?<br>Cette action est irréversible.</html>");
        confirmLabel.setFont(NORMAL_FONT);
        
        confirmPanel.add(warningIcon, BorderLayout.WEST);
        confirmPanel.add(confirmLabel, BorderLayout.CENTER);

        // Options pour la boîte de dialogue
        Object[] options = {"Supprimer", "Annuler"};
        
        int result = JOptionPane.showOptionDialog(
                this,
                confirmPanel,
                "Confirmation de suppression",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                options,
                options[1]  // Option par défaut (Annuler)
        );

        if (result == JOptionPane.YES_OPTION) {
            	Delete.Deleteuser(Integer.parseInt(id));  
        }
    }
    
    // Méthodes utilitaires pour l'interface utilisateur
    
    private JTextField createStyledTextField() {
        JTextField textField = new JTextField();
        textField.setFont(NORMAL_FONT);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(20, 20, 200)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        return textField;
    }
 
    private JPasswordField createStyledPasswordField() {
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(NORMAL_FONT);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(20, 20, 200)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        return passwordField;
    }

    private void showSuccessDialog(String message) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        
        JLabel iconLabel = new JLabel("\u2714"); // Symbole de coche
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 18));
        iconLabel.setForeground(new Color(46, 204, 113));
        panel.add(iconLabel, BorderLayout.WEST);
        
        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(NORMAL_FONT);
        panel.add(messageLabel, BorderLayout.CENTER);
        
        JOptionPane.showMessageDialog(
                this,
                panel,
                "Succès",
                JOptionPane.PLAIN_MESSAGE
        );
    }
    
    private void showErrorDialog(String title, String message) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        
        JLabel iconLabel = new JLabel("\u2716"); // Symbole X
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 18));
        iconLabel.setForeground(new Color(231, 76, 60));
        panel.add(iconLabel, BorderLayout.WEST);
        
        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(NORMAL_FONT);
        panel.add(messageLabel, BorderLayout.CENTER);
        
        JOptionPane.showMessageDialog(
                this,
                panel,
                title,
                JOptionPane.ERROR_MESSAGE
        );
    }
    
    private void showWarningDialog(String title, String message) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        
        JLabel iconLabel = new JLabel("\u26A0"); // Symbole d'avertissement
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 18));
        iconLabel.setForeground(new Color(243, 156, 18));
        panel.add(iconLabel, BorderLayout.WEST);
        
        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(NORMAL_FONT);
        panel.add(messageLabel, BorderLayout.CENTER);
        
        JOptionPane.showMessageDialog(
                this,
                panel,
                title,
                JOptionPane.WARNING_MESSAGE
        );
    }
}