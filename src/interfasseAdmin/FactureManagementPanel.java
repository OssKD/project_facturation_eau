package interfasseAdmin;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.text.NumberFormat;
import java.util.Locale;

public class FactureManagementPanel extends JPanel {
    // Composants de l'interface
    private JTextField compteurField, ancienIndexField, nouvelIndexField, prixUniteField;
    private JComboBox<String> moisComboBox;
    private JButton btnCreer, btnModifier, btnSupprimer, btnPDF;
    private JTable factureTable;
    private DefaultTableModel tableModel;
    private static final Color HEADER_COLOR = new Color(66, 135, 247);
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 18);

    public FactureManagementPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Titre
        JLabel titleLabel = new JLabel("Gestion des Factures d'Eau");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(new Color(44, 62, 80));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        add(titleLabel, BorderLayout.NORTH);
        
        // Contenu principal
        JPanel mainContentPanel = new JPanel(new BorderLayout(15, 15));
        mainContentPanel.add(createFormPanel(), BorderLayout.WEST);
        mainContentPanel.add(createTablePanel(), BorderLayout.CENTER);
        add(mainContentPanel, BorderLayout.CENTER);
        chargerFactures(); // Charger les factures à l'initialisation
    }

    // Panneau de formulaire
    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new BorderLayout(0, 10));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(HEADER_COLOR),
            "Informations de la Facture", TitledBorder.LEFT, TitledBorder.TOP, TITLE_FONT, HEADER_COLOR),
            new EmptyBorder(10, 10, 10, 10)));
        
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);
        
        // Création des champs de texte
        compteurField = createStyledTextField();
        ancienIndexField = createStyledTextField();
        nouvelIndexField = createStyledTextField();
        prixUniteField = createStyledTextField();
        
        // ComboBox pour les mois
        String[] mois = {"Janvier", "Février", "Mars", "Avril", "Mai", "Juin", "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"};
        moisComboBox = new JComboBox<>(mois);
        
        addLabelAndField(fieldsPanel, "N° Compteur:", compteurField, gbc, 0);
        addLabelAndField(fieldsPanel, "Mois:", moisComboBox, gbc, 1);
        addLabelAndField(fieldsPanel, "Ancien Index:", ancienIndexField, gbc, 2);
        addLabelAndField(fieldsPanel, "Nouvel Index:", nouvelIndexField, gbc, 3);
        addLabelAndField(fieldsPanel, "Prix Unité:", prixUniteField, gbc, 4);
        
        formPanel.add(fieldsPanel, BorderLayout.CENTER);
        
        // Panneau pour les boutons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        btnCreer = createStyledButton("Créer", new Color(23, 76, 60));
        btnModifier = createStyledButton("Modifier", new Color(23, 76, 60));
        btnSupprimer = createStyledButton("Supprimer", new Color(23, 76, 60));
        btnPDF = createStyledButton("PDF", new Color(23, 76, 60));
        
        buttonPanel.add(btnCreer);
        buttonPanel.add(btnModifier);
        buttonPanel.add(btnSupprimer);
        buttonPanel.add(btnPDF);
        formPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Actions des boutons
        btnCreer.addActionListener(e -> ajouterFacture());
        btnSupprimer.addActionListener(e -> supprimerFacture());
        btnModifier.addActionListener(e -> modifierFacture());
        
        return formPanel;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(15, 28));
        return field;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.black);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFont(new Font("Arial", Font.CENTER_BASELINE, 10));
        return button;
    }

    private void addLabelAndField(JPanel panel, String labelText, JComponent field, GridBagConstraints gbc, int row) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        panel.add(label, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(field, gbc);
    }

    // Panneau de table
    private JScrollPane createTablePanel() {
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Les cellules ne sont pas éditables
            }
        };
        
        String[] colonnes = {"ID Facture", "Mois", "Montant", "Moitie", "État de Paiement", "Compteur", "Ancien Index", "Nouvel Index", "Consommation"};
        tableModel.setColumnIdentifiers(colonnes);
        factureTable = new JTable(tableModel);
        factureTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        factureTable.setRowHeight(13);
        factureTable.getTableHeader().setBackground(HEADER_COLOR);
        factureTable.getTableHeader().setForeground(Color.BLUE);
        factureTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        
        // Définit les rendus de cellule pour le montant
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.FRANCE);
        
        // Rendu pour les montants
        TableCellRenderer currencyRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                if (value instanceof Double) {
                    setText(currencyFormat.format(value));
                } else {
                    setText((value == null) ? "" : value.toString());
                }
                setHorizontalAlignment(JLabel.RIGHT);
                return this;
            }
        };

        // Appliquer les rendus de cellule
        factureTable.getColumnModel().getColumn(2).setCellRenderer(currencyRenderer);
        factureTable.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);
        factureTable.getColumnModel().getColumn(5).setCellRenderer(rightRenderer);
        factureTable.getColumnModel().getColumn(6).setCellRenderer(rightRenderer);
        factureTable.getColumnModel().getColumn(7).setCellRenderer(rightRenderer);
        
        // Écouteur de sélection pour mettre à jour les champs lorsque la ligne est sélectionnée
        factureTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && factureTable.getSelectedRow() != -1) {
                afficherDetailsFacture(factureTable.getSelectedRow());
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(factureTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(HEADER_COLOR),
            "Liste des Factures", TitledBorder.LEFT, TitledBorder.TOP, TITLE_FONT, HEADER_COLOR),
            new EmptyBorder(10, 10, 10, 10)));
        return scrollPane;
    }

    private void afficherDetailsFacture(int row) {
        System.out.println("Ligne sélectionnée: " + row); // Diagnostic
        
        // Récupérer et afficher les valeurs
        compteurField.setText(tableModel.getValueAt(row, 5).toString());
        String dd = tableModel.getValueAt(row, 1).toString();
        String mois = convertirMoisDeDate(dd); 
        System.out.println("Mois récupéré: " + mois); // Diagnostic
        moisComboBox.setSelectedItem(mois);
        ancienIndexField.setText(tableModel.getValueAt(row, 6).toString());
        nouvelIndexField.setText(tableModel.getValueAt(row, 7).toString());
        
        // Calculer le prix unitaire
        double consommation = (Double) tableModel.getValueAt(row, 8);
        double montant = (Double) tableModel.getValueAt(row, 2);
        double prixUnite = consommation > 0 ? montant / consommation : 0;
        prixUniteField.setText(String.format("%.2f", prixUnite));
    }

    private void chargerFactures() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/javaswing_app", "root", "");
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT id_facture, DATE_FORMAT(mois, '%Y-%m') AS mois, montant, moitie, etat_payment, compteur, Ancien_Index, Nouvel_Index, Consommation FROM facture ORDER BY id_facture")) {
         
            tableModel.setRowCount(0); // Vider le modèle de table avant le remplissage
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("id_facture"),
                    rs.getString("mois"),
                    rs.getDouble("montant"),
                    rs.getInt("moitie"),
                    rs.getString("etat_payment"),
                    rs.getString("compteur"),
                    rs.getDouble("Ancien_Index"),
                    rs.getDouble("Nouvel_Index"),
                    rs.getDouble("Consommation")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des factures: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ajouterFacture() {
        try {
            String compteur = compteurField.getText();
            String mois = getNumeriqueMois(moisComboBox.getSelectedIndex() + 1); // Ajustez l'index du mois
            double ancienIndex = Double.parseDouble(ancienIndexField.getText());
            double nouvelIndex = Double.parseDouble(nouvelIndexField.getText());
            if (compteur.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Veuillez entrer un numéro de compteur valide.");
                return;
            }
            if (nouvelIndex < ancienIndex) {
                JOptionPane.showMessageDialog(this, "Le nouvel index doit être supérieur à l'ancien index.", "Données invalides", JOptionPane.WARNING_MESSAGE);
                return;
            }
            double consommation = nouvelIndex - ancienIndex;
            double montant = consommation * Double.parseDouble(prixUniteField.getText());
            
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/javaswing_app", "root", "");
                 PreparedStatement ps = conn.prepareStatement("INSERT INTO facture(id_user, mois, montant, moitie, etat_payment, compteur, Ancien_Index, Nouvel_Index, Consommation) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                 
                ps.setInt(1, 1);  // Ajuster l'id_user et d'autres paramètres selon vos besoins
                ps.setString(2, mois);
                ps.setDouble(3, montant);
                ps.setInt(4, 1);
                ps.setString(5, "N");
                ps.setString(6, compteur);
                ps.setDouble(7, ancienIndex);
                ps.setDouble(8, nouvelIndex);
                ps.setDouble(9, consommation);
                ps.executeUpdate();
            }
            JOptionPane.showMessageDialog(this, "Facture ajoutée avec succès !");
            chargerFactures();
            viderChamps();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer des valeurs numériques valides pour les index et le prix unitaire.", "Format invalide", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout de la facture: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    

    private void viderChamps() {
        compteurField.setText("");
        moisComboBox.setSelectedIndex(0);
        ancienIndexField.setText("");
        nouvelIndexField.setText("");
        prixUniteField.setText("");
        factureTable.clearSelection();
    }

    private void supprimerFacture() {
        int row = factureTable.getSelectedRow();
        if (row != -1) {
            int factureId = (Integer) factureTable.getValueAt(row, 0);
            int confirmation = JOptionPane.showConfirmDialog(this, "Êtes-vous sûr de vouloir supprimer cette facture ?", "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (confirmation == JOptionPane.YES_OPTION) {
                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/javaswing_app", "root", "");
                     PreparedStatement ps = conn.prepareStatement("DELETE FROM facture WHERE id_facture = ?")) {
                    ps.setInt(1, factureId);
                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Facture supprimée avec succès !");
                    chargerFactures();
                    viderChamps();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Erreur lors de la suppression de la facture: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une facture à supprimer.", "Aucune sélection", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void modifierFacture() {
        int row = factureTable.getSelectedRow();
        if (row != -1) {
            int factureId = (Integer) factureTable.getValueAt(row, 0);
            try {
                String compteur = compteurField.getText();
                double ancienIndex = Double.parseDouble(ancienIndexField.getText());
                double nouvelIndex = Double.parseDouble(nouvelIndexField.getText());
                double prixUnite = Double.parseDouble(prixUniteField.getText());
                
                if (compteur.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Veuillez entrer un numéro de compteur valide.");
                    return;
                }
                
                if (nouvelIndex < ancienIndex) {
                    JOptionPane.showMessageDialog(this, "Le nouvel index doit être supérieur à l'ancien index.", "Données invalides", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                double consommation = nouvelIndex - ancienIndex;
                double montant = consommation * prixUnite;
                String moisNom = moisComboBox.getSelectedItem().toString();
                int moisNumerique = getMoisNumerique(moisNom);
                String moisAA = String.format("2024-%02d-01", moisNumerique); // Format correct avec un jour
                
                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/javaswing_app", "root", "");
                     PreparedStatement ps = conn.prepareStatement("UPDATE facture SET mois=?, Ancien_Index=?, Nouvel_Index=?, Consommation=?, montant=? WHERE id_facture=?")) {
                    ps.setString(1, moisAA); // Mois formaté
                    ps.setDouble(2, ancienIndex);
                    ps.setDouble(3, nouvelIndex);
                    ps.setDouble(4, consommation);
                    ps.setDouble(5, montant);
                    ps.setInt(6, factureId);
                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Facture modifiée avec succès !");
                    chargerFactures();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Erreur lors de la modification de la facture: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Veuillez entrer des valeurs numériques valides pour les index et le prix unitaire.", "Format invalide", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une facture à modifier.", "Aucune sélection", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private int getMoisNumerique(String moisNom) {
        switch (moisNom) {
            case "Janvier": return 1;
            case "Février": return 2;
            case "Mars": return 3;
            case "Avril": return 4;
            case "Mai": return 5;
            case "Juin": return 6;
            case "Juillet": return 7;
            case "Août": return 8;
            case "Septembre": return 9;
            case "Octobre": return 10;
            case "Novembre": return 11;
            case "Décembre": return 12;
            default: return 0;
        }
    }
    private String getNumeriqueMois(int N) {
        switch (N) {
            case 1: return "Janvier";
            case 2: return "Février";
            case 3: return "Mars";
            case 4: return "Avril";
            case 5: return "Mai";
            case 6: return "Juin";
            case 7: return "Juillet";
            case 8: return "Août";
            case 9: return "Septembre";
            case 10: return "Octobre";
            case 11: return "Novembre";
            case 12: return "Décembre";
            default: return "Erreur";
        }
    }
    private String convertirMoisDeDate(String date) {
        // Vérifiez si le format est correct
        if (date != null && date.matches("\\d{4}-\\d{2}")) {
            // Séparez l'année et le mois
            String[] parts = date.split("-");
            int moisNumerique = Integer.parseInt(parts[1]);

            // Appel à la méthode existante pour obtenir le nom du mois
            return getNumeriqueMois(moisNumerique);
        } else {
            return "Erreur"; // Gérez les formats invalides
        }
    }
 
}