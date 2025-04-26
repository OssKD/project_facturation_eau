package interfaceAdmin;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.text.NumberFormat;
import java.util.Locale;

public class FactureManagementPanel extends JPanel {
    // Composants d'interface
    private JTextField compteurField, ancienIndexField, nouvelIndexField, prixUniteField;
    private JComboBox<String> moisComboBox;
    private JButton btnCreer, btnModifier, btnSupprimer, btnPDF;
    private JTable factureTable;
    private DefaultTableModel tableModel;
    
    // Constantes pour les couleurs et policest                  title
    private static final Color HEADER_COLOR = new Color(66, 135, 247);
    private static final Color BUTTON_COLOR = new Color(59, 12, 22);
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 18);
    
    public FactureManagementPanel() {
        // Configuration du panel principal
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Titre principal
        JLabel titleLabel = new JLabel("Gestion des Factures d'Eau");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(44, 62, 80));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        add(titleLabel, BorderLayout.NORTH);
        
        // Création et ajout des sous-panels
        JPanel mainContentPanel = new JPanel(new BorderLayout(15, 15));
        mainContentPanel.add(createFormPanel(), BorderLayout.WEST);
        mainContentPanel.add(createTablePanel(), BorderLayout.CENTER);
        add(mainContentPanel, BorderLayout.CENTER);
        
        // Charger les données
        chargerFactures();
    }
    
    // Création du panel de formulaire
    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new BorderLayout(0, 10));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(HEADER_COLOR), 
            "Informations de la Facture", TitledBorder.LEFT, TitledBorder.TOP, TITLE_FONT, HEADER_COLOR),
            new EmptyBorder(10, 10, 10, 10)));
            
        // Panel principal pour les champs
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);
        
        // Création des composants
        compteurField = createStyledTextField();
        
        String[] mois = {"Janvier", "Février", "Mars", "Avril", "Mai", "Juin", 
                         "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"};
        moisComboBox = new JComboBox<>(mois);
        
        ancienIndexField = createStyledTextField();
        nouvelIndexField = createStyledTextField();
        prixUniteField = createStyledTextField();
        
        // Ajout des labels et champs avec GridBagLayout
        addLabelAndField(fieldsPanel, "N° Compteur:", compteurField, gbc, 0);
        addLabelAndField(fieldsPanel, "Mois:", moisComboBox, gbc, 1);
        addLabelAndField(fieldsPanel, "Ancien Index:", ancienIndexField, gbc, 2);
        addLabelAndField(fieldsPanel, "Nouvel Index:", nouvelIndexField, gbc, 3);
        addLabelAndField(fieldsPanel, "Prix Unité:", prixUniteField, gbc, 4);
        
        formPanel.add(fieldsPanel, BorderLayout.CENTER);
        
        // Panel pour les boutons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        buttonPanel.setBorder(new EmptyBorder(10, 8, 23, 0));
        
        btnCreer = createStyledButton("Créer", new Color(23, 76, 60));
        btnModifier = createStyledButton("Modifier", new Color(23, 76, 60));
        btnSupprimer = createStyledButton("Supprimer", new Color(23, 76, 60));
        btnPDF = createStyledButton("PDF", new Color(23, 76, 60));
        
        buttonPanel.add(btnCreer);
        buttonPanel.add(btnModifier);
        buttonPanel.add(btnSupprimer);
        buttonPanel.add(btnPDF);
        
        formPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Configuration des actions des boutons
        btnCreer.addActionListener(e -> ajouterFacture());
        btnSupprimer.addActionListener(e -> supprimerFacture());
        btnModifier.addActionListener(e -> modifierFacture());
        
        return formPanel;
    }
    
    // Méthode pour créer un JTextField stylisé
    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(15, 28));
        return field;
    }
    
    // Méthode pour créer un bouton stylisé
    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.black);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFont(new Font("Arial", Font.CENTER_BASELINE, 10));
        return button;
    }
    
    // Méthode pour ajouter un label et un champ au panel  les nom :id compteur //// nom //index///moins 
    private void addLabelAndField(JPanel panel, String labelText, JComponent field, 
                                 GridBagConstraints gbc, int row) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.PLAIN,14));
        
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        panel.add(label, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(field, gbc);
    }
    
    // Méthode pour créer le tableau des factures
    private JScrollPane createTablePanel() {
        // Modèle de tableau avec colonnes non éditables
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        /////Résumé des termes :
       /// Ancien index : Lecture du compteur à la fin de la période précédente.

       /// Nouveau index : Lecture du compteur à la fin de la période actuelle.

      ///  Consommation : Différence entre le nouvel index et l'ancien index, ce qui représente la quantité d'eau utilisée pendant la période.
        
        // Définition des colonnes   
        String[] colonnes = {"ID", "Compteur", "Mois", "Ancien Index", "Nouvel Index", "Consommation", "Montant"};
        tableModel.setColumnIdentifiers(colonnes);
        
        // Création et configuration du tableau
        factureTable = new JTable(tableModel);
        factureTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        factureTable.setRowHeight(13);
        factureTable.getTableHeader().setBackground(HEADER_COLOR);
        factureTable.getTableHeader().setForeground(Color.BLUE);
        factureTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        
        // Configurez le renderer pour formater les montants
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        
        // Format pour les valeurs numériques (colonnes 3, 4, 5, 6)
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.FRANCE);
        TableCellRenderer currencyRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                if (value instanceof Double) {
                    if (column == 6) { // Colonne Montant
                        setText(currencyFormat.format(value));
                    } else {
                        setText(String.format("%.2f", value));
                    }
                } else {
                    setText((value == null) ? "" : value.toString());
                }
                setHorizontalAlignment(JLabel.RIGHT);
                return this;
            }
        };
        
        factureTable.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);
        factureTable.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);
        factureTable.getColumnModel().getColumn(5).setCellRenderer(rightRenderer);
        factureTable.getColumnModel().getColumn(6).setCellRenderer(currencyRenderer);
        
        // Ajout d'un écouteur de sélection
        factureTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && factureTable.getSelectedRow() != -1) {
                afficherDetailsFacture(factureTable.getSelectedRow());
            }
        });
        
        // Création du scroll pane avec un titre
        JScrollPane scrollPane = new JScrollPane(factureTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(HEADER_COLOR), 
            "Liste des Factures", TitledBorder.LEFT, TitledBorder.TOP, TITLE_FONT, HEADER_COLOR),
            new EmptyBorder(10, 10, 10, 10)));
            
        return scrollPane;
    }
    
    // Méthode pour afficher les détails d'une facture sélectionnée
    private void afficherDetailsFacture(int row) {
        compteurField.setText(tableModel.getValueAt(row, 1).toString());
        moisComboBox.setSelectedItem(tableModel.getValueAt(row, 2).toString());
        ancienIndexField.setText(tableModel.getValueAt(row, 3).toString());
        nouvelIndexField.setText(tableModel.getValueAt(row, 4).toString());
        
        // Calcul du prix unitaire (montant / consommation)
        double consommation = (Double) tableModel.getValueAt(row, 5);
        double montant = (Double) tableModel.getValueAt(row, 6);
        double prixUnite = consommation > 0 ? montant / consommation : 0;
        prixUniteField.setText(String.format("%.2f", prixUnite));
    }
    
    // Les autres méthodes existantes pour les opérations CRUD
    
    // Méthode pour charger les factures existantes dans la table
    private void chargerFactures() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/gestion_eau", "root", "");
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM factures ORDER BY id DESC")) {

            // Vider le modèle de données avant de le remplir à nouveau
            tableModel.setRowCount(0);

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id"),
                    rs.getString("compteur"),
                    rs.getString("mois"),
                    rs.getDouble("ancien_index"),
                    rs.getDouble("nouvel_index"),
                    rs.getDouble("consommation"),
                    rs.getDouble("montant")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Erreur lors du chargement des factures: " + ex.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Méthode pour ajouter une nouvelle facture
    private void ajouterFacture() {
        try {
            String compteur = compteurField.getText();
            String mois = moisComboBox.getSelectedItem().toString();
            double ancienIndex = Double.parseDouble(ancienIndexField.getText());
            double nouvelIndex = Double.parseDouble(nouvelIndexField.getText());
            double prixUnite = Double.parseDouble(prixUniteField.getText());

            // Validation des données
            if (compteur.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Veuillez entrer un numéro de compteur valide.");
                return;
            }
            
            if (nouvelIndex < ancienIndex) {
                JOptionPane.showMessageDialog(this, 
                    "Le nouvel index doit être supérieur à l'ancien index.",
                    "Données invalides", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double consommation = nouvelIndex - ancienIndex;
            double montant = consommation * prixUnite;

            // Connexion à la base de données et insertion de la facture
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/gestion_eau", "root", "");
                 PreparedStatement ps = conn.prepareStatement("INSERT INTO factures (compteur, mois, ancien_index, nouvel_index, consommation, montant) VALUES (?, ?, ?, ?, ?, ?)")) {

                ps.setString(1, compteur);
                ps.setString(2, mois);
                ps.setDouble(3, ancienIndex);
                ps.setDouble(4, nouvelIndex);
                ps.setDouble(5, consommation);
                ps.setDouble(6, montant);

                ps.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "Facture ajoutée avec succès !");
            chargerFactures();  // Recharger les factures après l'ajout
            viderChamps();      // Vider les champs après ajout

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, 
                "Veuillez entrer des valeurs numériques valides pour les index et le prix unitaire.",
                "Format invalide", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Erreur lors de l'ajout de la facture: " + ex.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Méthode pour vider les champs du formulaire
    private void viderChamps() {
        compteurField.setText("");
        moisComboBox.setSelectedIndex(0);
        ancienIndexField.setText("");
        nouvelIndexField.setText("");
        prixUniteField.setText("");
        factureTable.clearSelection();
    }

    // Méthode pour supprimer une facture sélectionnée
    private void supprimerFacture() {
        int row = factureTable.getSelectedRow();
        if (row != -1) {
            int factureId = (int) factureTable.getValueAt(row, 0); // Récupérer l'ID de la facture sélectionnée

            // Confirmation de la suppression
            int confirmation = JOptionPane.showConfirmDialog(this, 
                "Êtes-vous sûr de vouloir supprimer cette facture ?", 
                "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                
            if (confirmation == JOptionPane.YES_OPTION) {
                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/gestion_eau", "root", "");
                     PreparedStatement ps = conn.prepareStatement("DELETE FROM factures WHERE id = ?")) {

                    ps.setInt(1, factureId);
                    ps.executeUpdate();

                    JOptionPane.showMessageDialog(this, "Facture supprimée avec succès !");
                    chargerFactures();  // Recharger les factures après suppression
                    viderChamps();      // Vider les champs après suppression
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, 
                        "Erreur lors de la suppression de la facture: " + ex.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, 
                "Veuillez sélectionner une facture à supprimer.",
                "Aucune sélection", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Méthode pour modifier une facture
    private void modifierFacture() {
        int row = factureTable.getSelectedRow();
        if (row != -1) {
            int factureId = (int) factureTable.getValueAt(row, 0); // Récupérer l'ID de la facture sélectionnée

            try {
                // Récupérer les nouvelles valeurs des champs
                String compteur = compteurField.getText();
                String mois = moisComboBox.getSelectedItem().toString();
                double ancienIndex = Double.parseDouble(ancienIndexField.getText());
                double nouvelIndex = Double.parseDouble(nouvelIndexField.getText());
                double prixUnite = Double.parseDouble(prixUniteField.getText());

                // Validation
                if (compteur.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Veuillez entrer un numéro de compteur valide.");
                    return;
                }
                
                if (nouvelIndex < ancienIndex) {
                    JOptionPane.showMessageDialog(this, 
                        "Le nouvel index doit être supérieur à l'ancien index.",
                        "Données invalides", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                double consommation = nouvelIndex - ancienIndex;
                double montant = consommation * prixUnite;

                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/gestion_eau", "root", "");
                     PreparedStatement ps = conn.prepareStatement("UPDATE factures SET compteur = ?, mois = ?, ancien_index = ?, nouvel_index = ?, consommation = ?, montant = ? WHERE id = ?")) {

                    ps.setString(1, compteur);
                    ps.setString(2, mois);
                    ps.setDouble(3, ancienIndex);
                    ps.setDouble(4, nouvelIndex);
                    ps.setDouble(5, consommation);
                    ps.setDouble(6, montant);
                    ps.setInt(7, factureId);

                    ps.executeUpdate();

                    JOptionPane.showMessageDialog(this, "Facture modifiée avec succès !");
                    chargerFactures();  // Recharger les factures après modification
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, 
                        "Erreur lors de la modification de la facture: " + ex.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Veuillez entrer des valeurs numériques valides pour les index et le prix unitaire.",
                    "Format invalide", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, 
                "Veuillez sélectionner une facture à modifier.",
                "Aucune sélection", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}