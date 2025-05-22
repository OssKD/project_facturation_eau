package interfasseAdmin;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ClientManagementPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField idField, nomField, prenomField, adresseField;
    private JButton btnCreer, btnModifier, btnSupprimer, btnActualiser;

    public ClientManagementPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        add(createTablePanel(), BorderLayout.CENTER);
        add(createFormPanel(), BorderLayout.EAST);
        chargerClients();
    }

    private JScrollPane createTablePanel() {
        String[] columnNames = {"ID", "Nom", "Prénom", "Adresse"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> remplirChampsDepuisTable());

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Liste des clients"));
        return scrollPane;
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Gérer un client"));
        formPanel.setPreferredSize(new Dimension(400, getHeight()));
        formPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel idLabel = new JLabel("ID:");
        idField = new JTextField(15);
        idField.setEditable(false);

        JLabel nomLabel = new JLabel("Nom:");
        nomField = new JTextField(15);

        JLabel prenomLabel = new JLabel("Prénom:");
        prenomField = new JTextField(15);

        JLabel adresseLabel = new JLabel("Adresse:");
        adresseField = new JTextField(15);

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(idLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(idField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(nomLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(nomField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(prenomLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(prenomField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(adresseLabel, gbc);
        gbc.gridx = 1;
        formPanel.add(adresseField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        btnCreer = createStyledButton("Créer", new Color(46, 204, 113));
        btnModifier = createStyledButton("Modifier", new Color(241, 196, 15));
        btnSupprimer = createStyledButton("Supprimer", new Color(231, 76, 60));
        btnActualiser = createStyledButton("Actualiser", new Color(52, 152, 219));

        btnCreer.addActionListener(e -> ajouterClient());
        btnModifier.addActionListener(e -> modifierClient());
        btnSupprimer.addActionListener(e -> supprimerClient());
        btnActualiser.addActionListener(e -> {
            idField.setText("");
            nomField.setText("");
            prenomField.setText("");
            adresseField.setText("");
            chargerClients();
        });

        buttonPanel.add(btnCreer);
        buttonPanel.add(btnModifier);
        buttonPanel.add(btnSupprimer);
        buttonPanel.add(btnActualiser);

        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        return formPanel;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        return button;
    }

    private void chargerClients() {
        tableModel.setRowCount(0);
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/javaswing_app", "root", "");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id_user, nom, prenom, adress_h FROM user")) {
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id_user"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("adress_h")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur de chargement : " + ex.getMessage());
        }
    }

    private void ajouterClient() {
        String nom = nomField.getText();
        String prenom = prenomField.getText();
        String adresse = adresseField.getText();

        if (nom.isEmpty() || prenom.isEmpty() || adresse.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs.");
            return;
        }

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/javaswing_app", "root", "");
             PreparedStatement pst = conn.prepareStatement(
                     "INSERT INTO user(nom, prenom, numero_compteur, email, password, tel, type, adress_h) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
            pst.setString(1, nom);
            pst.setString(2, prenom);
            pst.setInt(3, (int) (Math.random() * 90000) + 10000); // numéro compteur aléatoire
            pst.setString(4, nom.toLowerCase() + "@email.com");
            pst.setString(5, "1234"); // à remplacer par un hash si sécurité requise
            pst.setInt(6, 600000000); // téléphone par défaut
            pst.setString(7, "u"); // type utilisateur
            pst.setString(8, adresse);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Client ajouté !");
            chargerClients();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur d'ajout : " + ex.getMessage());
        }
    }

    private void modifierClient() {
        if (idField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Sélectionnez un client à modifier.");
            return;
        }

        int id = Integer.parseInt(idField.getText());
        String nom = nomField.getText();
        String prenom = prenomField.getText();
        String adresse = adresseField.getText();

        if (nom.isEmpty() || prenom.isEmpty() || adresse.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs.");
            return;
        }

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/javaswing_app", "root", "");
             PreparedStatement pst = conn.prepareStatement(
                     "UPDATE user SET nom=?, prenom=?, adress_h=? WHERE id_user=?")) {
            pst.setString(1, nom);
            pst.setString(2, prenom);
            pst.setString(3, adresse);
            pst.setInt(4, id);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Client modifié !");
            chargerClients();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur de modification : " + ex.getMessage());
        }
    }

    private void supprimerClient() {
        if (idField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Sélectionnez un client à supprimer.");
            return;
        }

        int id = Integer.parseInt(idField.getText());
        int confirm = JOptionPane.showConfirmDialog(this, "Voulez-vous vraiment supprimer ce client ?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/javaswing_app", "root", "");
             PreparedStatement pst = conn.prepareStatement("DELETE FROM user WHERE id_user=?")) {
            pst.setInt(1, id);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Client supprimé !");
            chargerClients();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur de suppression : " + ex.getMessage());
        }
    }

    private void remplirChampsDepuisTable() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            idField.setText(tableModel.getValueAt(selectedRow, 0).toString());
            nomField.setText(tableModel.getValueAt(selectedRow, 1).toString());
            prenomField.setText(tableModel.getValueAt(selectedRow, 2).toString());
            adresseField.setText(tableModel.getValueAt(selectedRow, 3).toString());
        }
    }
}
