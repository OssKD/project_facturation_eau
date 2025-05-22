package interfasseAdmin;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.text.NumberFormat;
import java.util.Locale;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FactureManagementPanel extends JPanel {
    // Composants de l'interface
    private JTextField compteurField, nouvelIndexField, moisField; // Ajout de moisField
    private JComboBox<String> etatPaiementField; // Ajout de etatPaiementField
    private JTextField filtreCompteurField;
    private JButton btnCreer, btnModifier, btnSupprimer, btnFiltrer;
    private JTable factureTable; // Déclaration de la JTable
    private DefaultTableModel tableModel;

    private static final Color HEADER_COLOR = new Color(66, 135, 247);
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 18);

    public FactureManagementPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Titre
        JLabel titleLabel = new JLabel("Gestion des Factures d'Eau Automatisée");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(new Color(44, 62, 80));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Contenu principal
        JPanel mainContentPanel = new JPanel(new BorderLayout(15, 15));

        // === CORRECTION ICI : createTablePanel doit être appelé AVANT createFormPanel ===
        // Car createFormPanel() ajoute un Listener à factureTable
        mainContentPanel.add(createTablePanel(), BorderLayout.CENTER); // Initialise factureTable ici
        mainContentPanel.add(createFormPanel(), BorderLayout.WEST);  // Ce panneau utilise maintenant factureTable

        add(mainContentPanel, BorderLayout.CENTER);

        chargerFactures(); // Charger toutes les factures à l'initialisation
    }

    // Panneau de formulaire - MODIFIÉ (Listener déplacé pour plus de logique)
    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new BorderLayout(0, 10));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(HEADER_COLOR),
            "Créer/Modifier Facture", TitledBorder.LEFT, TitledBorder.TOP, TITLE_FONT, HEADER_COLOR),
            new EmptyBorder(10, 10, 10, 10)));

        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        compteurField = createStyledTextField();
        nouvelIndexField = createStyledTextField();
        moisField = createStyledTextField(); // Initialisation du nouveau champ
        etatPaiementField = new JComboBox<>(new String[]{"N", "P"}); // Initialisation du nouveau champ

        addLabelAndField(fieldsPanel, "N° Compteur:", compteurField, gbc, 0);
        addLabelAndField(fieldsPanel, "Nouvel Index:", nouvelIndexField, gbc, 1);
        addLabelAndField(fieldsPanel, "Mois (YYYY-MM-DD):", moisField, gbc, 2); // Ajout label+champ
        addLabelAndField(fieldsPanel, "État Paiement (N/P):", etatPaiementField, gbc, 3); // Ajout label+champ

        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        btnCreer = createStyledButton("Créer Facture", new Color(23, 76, 60));
        btnModifier = createStyledButton("Modifier", new Color(23, 76, 60));
        btnSupprimer = createStyledButton("Supprimer", new Color(23, 76, 60));

        buttonPanel.add(btnCreer);
        buttonPanel.add(btnModifier);
        buttonPanel.add(btnSupprimer);
      

        formPanel.add(fieldsPanel, BorderLayout.CENTER);
        formPanel.add(buttonPanel, BorderLayout.SOUTH);

        btnCreer.addActionListener(e -> ajouterFacture());
        btnSupprimer.addActionListener(e -> supprimerFacture());
        btnModifier.addActionListener(e -> modifierFacture());
       

        // Le Listener de sélection de ligne EST MAINTENANT AJOUTÉ DANS createTablePanel
        // C'est plus logique car il dépend directement de la JTable.
        // Si vous l'aviez laissé ici, la NPE surviendrait.

        return formPanel;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(150, 28));
        return field;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFont(new Font("Arial", Font.BOLD, 10));
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

    // Panneau de table - MODIFIÉ pour ajouter le Listener
    private JScrollPane createTablePanel() {
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Les cellules ne sont pas éditables
            }
        };
        String[] colonnes = {"ID Facture", "Mois", "Montant", "État de Paiement", "Compteur", "Consommation"};
        tableModel.setColumnIdentifiers(colonnes);


        factureTable = new JTable(tableModel); // factureTable est initialisé ici
        factureTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        factureTable.setRowHeight(13);
        factureTable.getTableHeader().setBackground(HEADER_COLOR);
        factureTable.getTableHeader().setForeground(Color.BLUE);
        factureTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);

        TableCellRenderer currencyRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                if (value instanceof Double || value instanceof Float) {
                     setText(String.format(Locale.FRANCE, "%.2f", value) + " dh");
                } else {
                    setText((value == null) ? "" : value.toString());
                }
                setHorizontalAlignment(JLabel.RIGHT);
                return this;
            }
        };

        factureTable.getColumnModel().getColumn(2).setCellRenderer(currencyRenderer); // Montant
        factureTable.getColumnModel().getColumn(3).setCellRenderer(rightRenderer); // État de Paiement
        factureTable.getColumnModel().getColumn(4).setCellRenderer(rightRenderer); // Compteur
        factureTable.getColumnModel().getColumn(5).setCellRenderer(rightRenderer); // Consommation


        // Le ListSelectionListener est ajouté ici, APRÈS l'initialisation de factureTable
        factureTable.getSelectionModel().addListSelectionListener(e -> {
            // S'assurer que la sélection est stable et qu'une ligne est bien sélectionnée
            if (!e.getValueIsAdjusting() && factureTable.getSelectedRow() != -1) {
                afficherDetailsFacturePourModification(factureTable.getSelectedRow());
            }
        });


        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        JLabel filterLabel = new JLabel("Filtrer par N° Compteur:");
        filterLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        filtreCompteurField = createStyledTextField();
        filtreCompteurField.setPreferredSize(new Dimension(120, 28));
        btnFiltrer = createStyledButton("Filtrer", new Color(41, 128, 185));
        btnFiltrer.addActionListener(e -> chargerFactures());
        filterPanel.add(filterLabel);
        filterPanel.add(filtreCompteurField);
        filterPanel.add(btnFiltrer);
        filtreCompteurField.addActionListener(e -> chargerFactures());

        JScrollPane scrollPane = new JScrollPane(factureTable);

        JPanel contentPanel = new JPanel(new BorderLayout(0, 10));
        contentPanel.add(filterPanel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        JScrollPane mainScrollPane = new JScrollPane(contentPanel);
        mainScrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(HEADER_COLOR),
            "Liste des Factures", TitledBorder.LEFT, TitledBorder.TOP, TITLE_FONT, HEADER_COLOR),
            new EmptyBorder(10, 10, 10, 10)));

        return mainScrollPane;
    }

    // Méthode adaptée pour afficher les détails pour la modification - MODIFIÉ
    private void afficherDetailsFacturePourModification(int row) {
        if (row < 0 || row >= tableModel.getRowCount()) {
            return; // Ligne invalide
        }

        Object factIdObj = tableModel.getValueAt(row, 0);
        if (factIdObj == null) {
             // Ne devrait pas arriver si l'ID est toujours la première colonne et int
             // Mais on garde la sécurité
             JOptionPane.showMessageDialog(this, "Impossible de trouver l'ID de la facture sélectionnée.", "Erreur", JOptionPane.ERROR_MESSAGE);
             viderChamps(); // Vide les champs si erreur
             return;
        }
        int factureId = (Integer) factIdObj;

        // Charger les détails complets de la facture depuis la base de données - REQUÊTE MODIFIÉE
        String getFactureDetailsSQL = "SELECT numero_compteur, Nouvel_Index, mois, etat_payment FROM facture WHERE id_facture = ?";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/javaswing_app", "root", "");
             PreparedStatement ps = conn.prepareStatement(getFactureDetailsSQL)) {

            ps.setInt(1, factureId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // Remplir les champs de saisie avec les valeurs chargées - CHAMPS MOIS ET ÉTAT AJOUTÉS
                // Attention : le mois est une DATE en DB, on l'affiche simplement comme String ici
                compteurField.setText(rs.getString("numero_compteur"));
                nouvelIndexField.setText(String.valueOf(rs.getDouble("Nouvel_Index")));
                moisField.setText(rs.getString("mois")); // Afficher le mois (format YYYY-MM-DD)
                etatPaiementField.setSelectedItem(rs.getString("etat_payment")); // Afficher l'état

            } else {
                // Si la facture n'est pas trouvée dans la DB malgré sa présence dans le modèle
                // (peu probable mais par sécurité)
                viderChamps();
                JOptionPane.showMessageDialog(this, "Détails complets de la facture ID " + factureId + " non trouvés en base.", "Données manquantes", JOptionPane.WARNING_MESSAGE);
            }
            rs.close();

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement des détails de facture pour modification: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement complet des détails de facture.", "Erreur DB", JOptionPane.ERROR_MESSAGE);
            viderChamps(); // Vide les champs en cas d'erreur DB
        } catch (Exception e) {
             e.printStackTrace();
             System.err.println("Erreur inattendue lors de l'affichage des détails: " + e.getMessage());
             viderChamps(); // Vide les champs en cas d'erreur inattendue
        }
    }

    // Méthode chargerFactures INCHANGÉE (adapte seulement l'affichage, pas la logique de sélection)
    private void chargerFactures() {
        String filtreCompteur = filtreCompteurField.getText().trim();


        String sql = "SELECT id_facture, DATE_FORMAT(mois, '%Y-%m-%d') AS mois, montant, etat_payment, numero_compteur, Consommation FROM facture ";


        if (!filtreCompteur.isEmpty()) {
            sql += " WHERE numero_compteur LIKE ?";
        }
        sql += " ORDER BY mois DESC";

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/javaswing_app", "root", "");
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (!filtreCompteur.isEmpty()) {
                ps.setString(1, "%" + filtreCompteur + "%");
            }

            ResultSet rs = ps.executeQuery();

            tableModel.setRowCount(0); // Vider le modèle

            while (rs.next()) {

                Object[] row = {
                    rs.getInt("id_facture"),
                    rs.getString("mois"), // Mois est maintenant lu comme une String formatted
                    rs.getDouble("montant"),
                    rs.getString("etat_payment"),
                    rs.getString("numero_compteur"),
                    rs.getDouble("Consommation")

                };
                tableModel.addRow(row);

            }
             rs.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des factures: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Méthode ajouterFacture (LOGIQUE INCHANGÉE, vérifie seulement le compteur)
    private void ajouterFacture() {
        try {
            String compteur = compteurField.getText().trim();
            if (compteur.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Veuillez entrer le numéro de compteur.", "Champ manquant", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String nouvelIndexStr = nouvelIndexField.getText().trim();
            if (nouvelIndexStr.isEmpty()) {
                 JOptionPane.showMessageDialog(this, "Veuillez entrer le nouvel index.", "Champ manquant", JOptionPane.WARNING_MESSAGE);
                 return;
            }

            //test d'esxistance de compteur
            String checkCompteurSQL = "SELECT COUNT(*) FROM user WHERE numero_compteur = ?";

            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/javaswing_app", "root", "");
                 PreparedStatement psCheck = conn.prepareStatement(checkCompteurSQL)) {

                psCheck.setString(1, compteur);
                ResultSet rsCheck = psCheck.executeQuery();

                if (rsCheck.next()) {
                    int count = rsCheck.getInt(1);
                    if (count == 0) {
                        JOptionPane.showMessageDialog(this, "Le numéro de compteur n'existe pas dans la base utilisateur.", "Compteur Inconnu", JOptionPane.WARNING_MESSAGE);
                        return; // Sort de la méthode, on ne continue pas l'ajout
                    }
                }
                rsCheck.close();
            }

            double nouvelIndex = Double.parseDouble(nouvelIndexStr);
            double ancienIndex = 0.0; // Valeur par défaut si aucune facture trouvée

            // Rechercher la dernière facture pour ce compteur
            String getLastFactureSQL = "SELECT Nouvel_Index FROM facture WHERE numero_compteur = ? ORDER BY mois DESC, id_facture DESC LIMIT 1";
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/javaswing_app", "root", "");
                 PreparedStatement psLast = conn.prepareStatement(getLastFactureSQL)) {

                psLast.setString(1, compteur);
                ResultSet rsLast = psLast.executeQuery();

                if (rsLast.next()) {
                    ancienIndex = rsLast.getDouble("Nouvel_Index");
                } else {
                    ancienIndex = 0.0;
                    System.out.println("Première facture pour compteur: " + compteur + ". Ancien Index = 0. ");
                }
                 rsLast.close();
            }

            // Valider le nouvel index par rapport à l'ancien
            if (nouvelIndex < ancienIndex) {
                JOptionPane.showMessageDialog(this, "Le nouvel index (" + nouvelIndex + ") doit être supérieur ou égal à l'ancien index (" + ancienIndex + ").", "Données Invalides", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Calculer consommation
            double consommation = nouvelIndex - ancienIndex;

            // CALCUL DU MONTANT PAR TRANCHES (inchangé)
            double montant = 0.0;
            if (consommation <= 150) {
                montant = consommation * 1.5;
            } else if (consommation > 150 && consommation <= 300) {
                montant = consommation * 2.0;
            } else if (consommation > 300 && consommation <= 450) {
                montant = consommation * 2.5;
            } else if (consommation > 450) {
                montant = consommation * 3.0;
            }

            // Générer la date du mois actuel (par défaut pour la création)
            LocalDate dateActuelle = LocalDate.now();
            String moisFacture = dateActuelle.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

             // Insérer la nouvelle facture - Utilise les champs existants + Ancien/Nouvel Index & Consommation
             String insertFactureSQL = "INSERT INTO facture(id_user, mois, montant, moitie, etat_payment, numero_compteur , Ancien_Index, Nouvel_Index, Consommation) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/javaswing_app", "root", "");
                 PreparedStatement psInsert = conn.prepareStatement(insertFactureSQL)) {

                int idUser = 1; // Exemple simple - Pense à récupérer dynamiquement l'id user lié au compteur
                                // Il faudrait potentiellement faire une requête supplémentaire pour obtenir l'id_user à partir du numero_compteur
                                // `SELECT id_user FROM user WHERE numero_compteur = ?`

                psInsert.setInt(1, idUser); // Utilise l'id user (pour l'instant 1)
                psInsert.setString(2, moisFacture); // Utilise la date actuelle pour la création
                psInsert.setDouble(3, montant);
                psInsert.setInt(4, 1); // 'moitie' - à définir selon votre logique métier
                psInsert.setString(5, "N"); // 'etat_payment' - 'N' pour Non payé par défaut pour la création
                psInsert.setString(6, compteur);
                psInsert.setDouble(7, ancienIndex); // Insérer l'ancien index trouvé/calculé
                psInsert.setDouble(8, nouvelIndex); // Insérer le nouvel index saisi
                psInsert.setDouble(9, consommation);

                int rowsAffected = psInsert.executeUpdate();

                if (rowsAffected > 0) {
                     JOptionPane.showMessageDialog(this, "Facture ajoutée avec succès pour le compteur " + compteur + " !", "Succès", JOptionPane.INFORMATION_MESSAGE);
                    chargerFactures(); // Rafraîchir la table
                    viderChamps();
                } else {
                    JOptionPane.showMessageDialog(this, "L'ajout de la facture a échoué.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Erreur de format: Veuillez entrer un nombre valide pour le Nouvel Index.", "Erreur de saisie", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur de base de données: " + ex.getMessage(), "Erreur SQL", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Une erreur inattendue est survenue lors de l'ajout: " + ex.getMessage(), "Erreur Générale", JOptionPane.ERROR_MESSAGE);
        }
    }

     // Méthode modifierFacture - MODIFIÉ
     private void modifierFacture() {
        int row = factureTable.getSelectedRow();
        if (row != -1) {
            Object factIdObj = tableModel.getValueAt(row, 0);
            if (factIdObj == null) {
                 JOptionPane.showMessageDialog(this, "Impossible de trouver l'ID de la facture sélectionnée.", "Erreur", JOptionPane.ERROR_MESSAGE);
                 return;
            }
            int factureId = (Integer) factIdObj;

            try {
                String nouveauCompteur = compteurField.getText().trim(); // Récupérer le nouveau compteur
                String nouvelIndexStr = nouvelIndexField.getText().trim(); // Récupérer le nouvel index saisi
                String moisModifie = moisField.getText().trim(); // Récupérer le mois modifié
                String etatModifie = (String) etatPaiementField.getSelectedItem(); // Récupérer l'état modifié

                // Validations de base des champs modifiés
                if (nouveauCompteur.isEmpty() || nouvelIndexStr.isEmpty() || moisModifie.isEmpty() || etatModifie == null || etatModifie.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs (Compteur, Nouvel Index, Mois, État Paiement) pour modifier la facture.", "Champ manquant", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                double nouveauNouvelIndexSaisi = Double.parseDouble(nouvelIndexStr);

                // --- Récupérer l'Ancien Index ACTUEL de la facture depuis la DB ---
                // C'est l'ancien index enregistré sur la ligne de FACTURE sélectionnée.
                // Il est nécessaire pour recalculer CONSOMMATION et MONTANT.
                String getAncienIndexSQL = "SELECT Ancien_Index FROM facture WHERE id_facture = ?";
                double ancienIndexActuel = 0.0;
                boolean factureExists = false;

                 try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/javaswing_app", "root", "");
                     PreparedStatement psGet = conn.prepareStatement(getAncienIndexSQL)) {
                     psGet.setInt(1, factureId);
                     ResultSet rsGet = psGet.executeQuery();
                     if (rsGet.next()) {
                         factureExists = true;
                         ancienIndexActuel = rsGet.getDouble("Ancien_Index");
                     }
                     rsGet.close();
                 }

                 if (!factureExists) {
                     JOptionPane.showMessageDialog(this, "Facture sélectionnée non trouvée dans la base de données (ID " + factureId + "). Impossible de modifier.", "Erreur", JOptionPane.ERROR_MESSAGE);
                     chargerFactures(); // Rafraîchir au cas où elle n'existe plus
                     viderChamps();
                     return;
                 }

                // --- Validation du nouvel index modifié par rapport à cet Ancien Index Actuel ---
                if (nouveauNouvelIndexSaisi < ancienIndexActuel) {
                    JOptionPane.showMessageDialog(this, "Le nouveau Nouvel Index saisi (" + nouveauNouvelIndexSaisi + ") doit être supérieur ou égal à l'Ancien Index actuel (" + ancienIndexActuel + " pour cette facture).", "Données Invalides", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Recalculer la consommation avec le nouveau Nouvel Index et l'Ancien Index actuel
                double nouvelleConsommation = nouveauNouvelIndexSaisi - ancienIndexActuel;

                // CALCULE LE MONTANT SI LE NOUVEL INDEX A CHANGÉ (LOGIQUE inchangée)
                 double nouveauMontant = 0.0;
                 if (nouvelleConsommation <= 150) {
                     nouveauMontant = nouvelleConsommation * 1.5;
                 } else if (nouvelleConsommation > 150 && nouvelleConsommation <= 300) {
                     nouveauMontant = nouvelleConsommation * 2.0;
                 } else if (nouvelleConsommation > 300 && nouvelleConsommation <= 450) {
                     nouveauMontant = nouvelleConsommation * 2.5;
                 } else if (nouvelleConsommation > 450) {
                     nouveauMontant = nouvelleConsommation * 3.0;
                 }


                // Mettre à jour la facture dans la base de données - REQUÊTE MODIFIÉE
                // On met à jour Compteur, Mois, Etat Paiement, Nouvel_Index, Consommation et Montant.
                // L'Ancien_Index de cette ligne de facture NE CHANGE PAS lors de la modification.
                String updateFactureSQL = "UPDATE facture SET numero_compteur=?, mois=?, etat_payment=?, Nouvel_Index=?, Consommation=?, montant=? WHERE id_facture=?";
                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/javaswing_app", "root", "");
                     PreparedStatement psUpdate = conn.prepareStatement(updateFactureSQL)) {

                    psUpdate.setString(1, nouveauCompteur); // Mettre à jour le compteur
                    psUpdate.setString(2, moisModifie); // Mettre à jour le mois (Assurez-vous que le format YYYY-MM-DD est correct pour la colonne DATE)
                    psUpdate.setString(3, etatModifie); // Mettre à jour l'état
                    psUpdate.setDouble(4, nouveauNouvelIndexSaisi); // Mettre à jour le nouvel index
                    psUpdate.setDouble(5, nouvelleConsommation); // Mettre à jour la consommation
                    psUpdate.setDouble(6, nouveauMontant); // Mettre à jour le montant recalculé
                    psUpdate.setInt(7, factureId); // Utilise l'ID pour cibler la bonne ligne


                    int rowsAffected = psUpdate.executeUpdate();

                    if (rowsAffected > 0) {
                         JOptionPane.showMessageDialog(this, "Facture ID " + factureId + " modifiée avec succès !", "Succès", JOptionPane.INFORMATION_MESSAGE);
                        chargerFactures(); // Rafraîchir la table pour montrer les changements
                        viderChamps(); // Vider les champs après modification réussie
                    } else {
                        // Cela peut arriver si la facture a été supprimée par un autre utilisateur
                        // ou si la sélection JTable n'était pas synchronisée avec la DB
                        JOptionPane.showMessageDialog(this, "Aucune facture trouvée avec l'ID " + factureId + " pour modification.", "Erreur", JOptionPane.ERROR_MESSAGE);
                         chargerFactures(); // التحديث للتأكد
                         viderChamps();
                    }
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Erreur de format: Veuillez entrer un nombre valide pour le Nouvel Index.", "Erreur de saisie", JOptionPane.WARNING_MESSAGE);
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erreur de base de données lors de la modification: " + ex.getMessage(), "Erreur SQL", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Une erreur inattendue est survenue lors de la modification: " + ex.getMessage(), "Erreur Générale", JOptionPane.ERROR_MESSAGE);
            }

        } else {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une facture à modifier.", "Aucune sélection", JOptionPane.INFORMATION_MESSAGE);
        }
    }


    // Méthode viderChamps - MODIFIÉE
    private void viderChamps() {
        compteurField.setText("");
        nouvelIndexField.setText("");
        moisField.setText(""); // Vide aussi le champ mois
        etatPaiementField.setSelectedIndex(0); // Remet la JComboBox au premier élément ("N")
        if (factureTable != null) { // Vérification
             factureTable.clearSelection(); // Désélectionne la ligne du tableau
        }
    }


    // Méthode supprimerFacture (inchangée)
    private void supprimerFacture() {
        int row = factureTable.getSelectedRow();
        if (row != -1) {
            Object factIdObj = tableModel.getValueAt(row, 0);
            if (factIdObj == null) {
                 JOptionPane.showMessageDialog(this, "Impossible de trouver l'ID de la facture sélectionnée.", "Erreur", JOptionPane.ERROR_MESSAGE);
                 return;
            }
            int factureId = (Integer) factIdObj;

            int confirmation = JOptionPane.showConfirmDialog(this, "Êtes-vous sûr de vouloir supprimer la facture ID " + factureId + " ?", "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (confirmation == JOptionPane.YES_OPTION) {
                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/javaswing_app", "root", "");
                     PreparedStatement ps = conn.prepareStatement("DELETE FROM facture WHERE id_facture = ?")) {

                    ps.setInt(1, factureId);

                    int rowsAffected = ps.executeUpdate();

                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(this, "Facture ID " + factureId + " supprimée avec succès !", "Succès", JOptionPane.INFORMATION_MESSAGE);
                        chargerFactures(); // Rafraîchir la table
                        viderChamps();
                    } else {
                        // Cela peut arriver si la facture a été supprimée par un autre utilisateur
                        // ou si la sélection JTable n'était pas synchronisée avec la DB
                        JOptionPane.showMessageDialog(this, "Aucune facture trouvée avec l'ID " + factureId + " pour suppression.", "Erreur", JOptionPane.ERROR_MESSAGE);
                         chargerFactures(); // التحديث للتأكد
                         viderChamps();
                    }

                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Erreur lors de la suppression de la facture: " + ex.getMessage(), "Erreur SQL", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une facture à supprimer.", "Aucune sélection", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Méthode convertirMoisDeDate (inchangée, mais potentiellement non utilisée directement)
    private String convertirMoisDeDate(String date) {
         if (date != null && (date.matches("\\d{4}-\\d{2}") || date.matches("\\d{4}-\\d{2}-\\d{2}"))) {
            try {
                String[] parts = date.split("-");
                int moisNumerique = Integer.parseInt(parts[1]);
                java.time.Month month = java.time.Month.of(moisNumerique);
                return month.getDisplayName(java.time.format.TextStyle.FULL, Locale.FRANCE);
            } catch (Exception e) {
                System.err.println("Erreur lors de la conversion du mois depuis la date: " + date);
                 return date; // Retourne la date brute si erreur
            }
        } else {
            return (date == null || date.isEmpty()) ? "" : date; // Retourne la date brute ou vide
        }
    }


   
}

