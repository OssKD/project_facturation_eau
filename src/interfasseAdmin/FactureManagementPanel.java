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
    private JTextField compteurField, nouvelIndexField;
    private JTextField filtreCompteurField; // Champ pour le filtre
    private JButton btnCreer, btnModifier, btnSupprimer, btnPDF, btnFiltrer; // Bouton Filtrer
    private JTable factureTable;
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
        mainContentPanel.add(createFormPanel(), BorderLayout.WEST);
        mainContentPanel.add(createTablePanel(), BorderLayout.CENTER);
        add(mainContentPanel, BorderLayout.CENTER);

        chargerFactures(); // Charger toutes les factures à l'initialisation
    }

    // Panneau de formulaire
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

        addLabelAndField(fieldsPanel, "N° Compteur:", compteurField, gbc, 0);
        addLabelAndField(fieldsPanel, "Nouvel Index:", nouvelIndexField, gbc, 1);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        btnCreer = createStyledButton("Créer Facture", new Color(23, 76, 60));
        btnModifier = createStyledButton("Modifier", new Color(23, 76, 60));
        btnSupprimer = createStyledButton("Supprimer", new Color(23, 76, 60));
        btnPDF = createStyledButton("PDF", new Color(23, 76, 60));

        buttonPanel.add(btnCreer);
        buttonPanel.add(btnModifier);
        buttonPanel.add(btnSupprimer);
        buttonPanel.add(btnPDF);

        formPanel.add(fieldsPanel, BorderLayout.CENTER);
        formPanel.add(buttonPanel, BorderLayout.SOUTH);

        btnCreer.addActionListener(e -> ajouterFacture());
        btnSupprimer.addActionListener(e -> supprimerFacture());
        btnModifier.addActionListener(e -> modifierFacture());
        btnPDF.addActionListener(e -> genererPDF());

        return formPanel;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(150, 28)); // Ajusté la taille
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

    // Panneau de table - MODIFIÉ pour afficher moins de colonnes
    private JScrollPane createTablePanel() {
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Les cellules ne sont pas éditables
            }
        };
        // --- Colonnes affichées CHANGÉ ---
        String[] colonnes = {"ID Facture", "Mois", "Montant", "État de Paiement", "Compteur", "Consommation"};
        tableModel.setColumnIdentifiers(colonnes);
        // ---------------------------------

        factureTable = new JTable(tableModel);
        factureTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        factureTable.setRowHeight(13);
        factureTable.getTableHeader().setBackground(HEADER_COLOR);
        factureTable.getTableHeader().setForeground(Color.BLUE);
        factureTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));

        // Définit les rendus de cellule
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.FRANCE);

        // Rendu pour les montants
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

        // --- Appliquer les rendus de cellule (indices ajustés) ---
        factureTable.getColumnModel().getColumn(2).setCellRenderer(currencyRenderer); // Montant
        factureTable.getColumnModel().getColumn(3).setCellRenderer(rightRenderer); // État de Paiement
        factureTable.getColumnModel().getColumn(4).setCellRenderer(rightRenderer); // Compteur
        factureTable.getColumnModel().getColumn(5).setCellRenderer(rightRenderer); // Consommation
        // ----------------------------------------------------------


        factureTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && factureTable.getSelectedRow() != -1) {
                // Note : Afficher les détails pour modification va toujours essayer de remplir
                // les champs Compteur et Nouvel Index. Si vous sélectionnez une ligne
                // qui n'affiche pas ces informations, il faudra adapter cette méthode
                // ou l'interface de modification.
                 // Pour l'instant, on laisse tel quel car les champs Compteur et NouvelIndex
                 // sont essentiels pour l'ajout et la modification même si AncienIndex n'est pas affiché.
                afficherDetailsFacturePourModification(factureTable.getSelectedRow());
            }
        });

        // --- Panneau de Filtre ---
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
        // --- Fin Panneau de Filtre ---

        JScrollPane scrollPane = new JScrollPane(factureTable);

        // Combiner le panneau de filtre et le tableau dans un panneau de contenu
        JPanel contentPanel = new JPanel(new BorderLayout(0, 10));
        contentPanel.add(filterPanel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        // Envelopper le panneau de contenu dans un JScrollPane principal avec la bordure
        JScrollPane mainScrollPane = new JScrollPane(contentPanel);
        mainScrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(HEADER_COLOR),
            "Liste des Factures", TitledBorder.LEFT, TitledBorder.TOP, TITLE_FONT, HEADER_COLOR),
            new EmptyBorder(10, 10, 10, 10)));

        return mainScrollPane;
    }

    // Méthode adaptée pour afficher les détails pour la modification
     // Cette méthode charge toujours Compteur et Nouvel Index même s'Ancien Index n'est pas affiché
    private void afficherDetailsFacturePourModification(int row) {
        if (row < 0 || row >= tableModel.getRowCount()) {
            return; // Ligne invalide
        }
        // Puisque "Ancien Index" et "Nouvel Index" ne sont plus affichés dans le tableau,
        // vous ne pouvez pas les récupérer directement depuis tableModel.getValueAt(row, index).
        // Si vous voulez que ces champs soient remplis lors de la sélection, vous devez
        // soit les garder cachés dans le modèle de table (plus complexe), soit
        // les charger à nouveau depuis la base de données quand une ligne est sélectionnée.
        // La version actuelle remplit les champs Compteur et NouvelIndex des champs de SAISIE
        // en utilisant les valeurs récupérées du modèle de table (qui ne contient plus Ancien Index par exemple)
        // Pour une modification complète, il faudrait charger TOUTES les infos de la ligne sélectionnée
        // depuis la DB au moment de la sélection si les colonnes Ancien Index et Nouvel Index sont retirées du modèle.
        // Pour l'instant, on va se baser sur les colonnes restantes affichées.
        // Les indices des colonnes "Compteur" et "Nouvel Index" doivent être ajustés
        // dans le tableModel selon les colonnes AUSSI PRÉSENTES dans le modèle.
        // Avec les nouvelles colonnes: {"ID Facture", "Mois", "Montant", "État de Paiement", "Compteur", "Consommation"}
        // Compteur est à l'indice 4
        // Consommation est à l'indice 5
        // Nouvel Index n'est PLUS dans les colonnes affichées.


        // === MODIFICATION nécessaire ici ===
        // Puisque Nouvel_Index n'est plus affiché, on ne peut pas le récupérer directement
        // de tableModel. La modification nécessite le Nouvel Index EXISTANT pour validation.
        // Il est plus simple de NE PAS modifier la méthode afficherDetailsFacturePourModification
        // si son but est de remplir les champs de saisie Compteur et Nouvel Index
        // pour une modification future (qui nécessite AncienIndex et NouvelIndex de la DB).
        // L'état actuel du code basé sur les indices 5 (Compteur) et 7 (Nouvel Index)
        // des versions PRÉCÉDENTES n'est plus correct avec les nouvelles colonnes.
        // L'option la plus robuste si Ancien Index et Nouvel Index ne sont pas affichés
        // est de recharger les détails complets (y compris Ancien/Nouvel Index)
        // depuis la base de données basée sur l'ID Facture sélectionné.

        // Récupérer l'ID Facture de la ligne sélectionnée (indice 0)
        Object factIdObj = tableModel.getValueAt(row, 0);
        if (factIdObj == null) {
            return; // ID non trouvé dans le modèle? Ne devrait pas arriver.
        }
        int factureId = (Integer) factIdObj;

        // Charger les détails complets de la facture depuis la base de données
        String getFactureDetailsSQL = "SELECT numero_compteur, Nouvel_Index FROM facture WHERE id_facture = ?";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/javaswing_app", "root", "");
             PreparedStatement ps = conn.prepareStatement(getFactureDetailsSQL)) {

            ps.setInt(1, factureId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // Remplir les champs de saisie avec les valeurs chargées
                compteurField.setText(rs.getString("numero_compteur"));
                nouvelIndexField.setText(String.valueOf(rs.getDouble("Nouvel_Index"))); // Convertir double en String
            }
            rs.close();

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement des détails de facture pour modification: " + e.getMessage());
            // Afficher un message d'erreur à l'utilisateur si c'est critique
             JOptionPane.showMessageDialog(this, "Erreur lors du chargement complet des détails de facture.", "Erreur DB", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
             e.printStackTrace();
             System.err.println("Erreur inattendue lors de l'affichage des détails: " + e.getMessage());
        }
        // === Fin de la MODIFICATION nécessaire ===


    }

    // Méthode chargerFactures MODIFIÉE pour la requête SELECT et l'extraction des données
    private void chargerFactures() {
        String filtreCompteur = filtreCompteurField.getText().trim();

        // --- Requête SELECT modifiée pour ne prendre que les colonnes nécessaires ---
        String sql = "SELECT id_facture, DATE_FORMAT(mois, '%Y-%m') AS mois, montant, etat_payment, numero_compteur, Consommation FROM facture ";
        // On sélectionne id_facture, mois, montant, etat_payment, compteur, Consommation

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
                // --- Extraction des données ajustée aux colonnes sélectionnées ---
                Object[] row = {
                    rs.getInt("id_facture"),
                    rs.getString("mois"),
                    rs.getDouble("montant"),
                    rs.getString("etat_payment"),
                    rs.getString("numero_compteur"),
                    rs.getDouble("Consommation")
                    // Moitie, Ancien_Index, Nouvel_Index ne sont plus extraits ici
                };
                tableModel.addRow(row);
                // ---------------------------------------------------------------
            }
             rs.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des factures: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Méthode ajouterFacture (inchangée dans sa LOGIQUE de calcul, mais on n'insère plus 'prix_unite' ou 'moitie' ou 'Ancien_Index' directement si ces colonnes ont été retirées de la DB)
    // NOTE : L'insertion a été ajustée dans le code précédent pour ne pas insérer 'prix_unite'.
    // Il faut que la requête INSERT corresponde aux colonnes réelles de votre table DB.
    // La requête INSERT ici est "INSERT INTO facture(id_user, mois, montant, moitie, etat_payment, compteur, Ancien_Index, Nouvel_Index, Consommation)"
    // Si 'moitie', 'Ancien_Index', 'Nouvel_Index' doivent être retirés de l'INSERT, ajustez-la ici.
    // MAIS attention, 'Ancien_Index' et 'Nouvel_Index' sont ESSENTIELS pour le calcul des factures futures et la modification.
    // Il est recommandé de CONSERVER Ancien_Index et Nouvel_Index dans la base de données même
    // s'ils ne sont pas affichés dans le tableau principal.
    // L'INSERT est donc correct si votre table DB contient bien ces colonnes.
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

            // Rechercher la dernière facture pour ce compteur (nécessite toujours Nouvel_Index de la DB)
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

            // Générer la date du mois actuel
            LocalDate dateActuelle = LocalDate.now();
            String moisFacture = dateActuelle.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            // Insérer la nouvelle facture - La requête INSERT reste la même si vous conservez les colonnes en DB
             String insertFactureSQL = "INSERT INTO facture(id_user, mois, montant, moitie, etat_payment, numero_compteur , Ancien_Index, Nouvel_Index, Consommation) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/javaswing_app", "root", "");
                 PreparedStatement psInsert = conn.prepareStatement(insertFactureSQL)) {

                int idUser = 1; // Exemple simple
                psInsert.setInt(1, idUser);
                psInsert.setString(2, moisFacture);
                psInsert.setDouble(3, montant);
                psInsert.setInt(4, 1); // 'moitie' - à définir selon votre logique métier
                psInsert.setString(5, "N"); // 'etat_payment' - 'N' pour Non payé par défaut
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

     // Méthode modifierFacture (la LOGIQUE de calcul par tranches reste la même.
     // La récupération de l'Ancien Index avant modification est toujours nécessaire.)
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
                String nouvelIndexStr = nouvelIndexField.getText().trim();
                if (nouvelIndexStr.isEmpty()) {
                     JOptionPane.showMessageDialog(this, "Veuillez entrer le nouveau Nouvel Index pour la modification.", "Champ manquant", JOptionPane.WARNING_MESSAGE);
                     return;
                }
                double nouveauNouvelIndexSaisi = Double.parseDouble(nouvelIndexStr);

                // Récupérer l'Ancien Index actuel de la facture sélectionnée depuis la DB (nécessaire pour validation et calcul)
                String getFactureSQL = "SELECT Ancien_Index FROM facture WHERE id_facture = ?";
                double ancienIndexActuel = 0.0;
                boolean factureExists = false;

                 try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/javaswing_app", "root", "");
                     PreparedStatement psGet = conn.prepareStatement(getFactureSQL)) {
                     psGet.setInt(1, factureId);
                     ResultSet rsGet = psGet.executeQuery();
                     if (rsGet.next()) {
                         factureExists = true;
                         ancienIndexActuel = rsGet.getDouble("Ancien_Index");
                     }
                     rsGet.close();
                 }

                 if (!factureExists) {
                     JOptionPane.showMessageDialog(this, "Facture sélectionnée non trouvée dans la base de données.", "Erreur", JOptionPane.ERROR_MESSAGE);
                     return;
                 }

                // Validation du nouvel index modifié
                if (nouveauNouvelIndexSaisi < ancienIndexActuel) {
                    JOptionPane.showMessageDialog(this, "Le nouveau Nouvel Index saisi (" + nouveauNouvelIndexSaisi + ") doit être supérieur ou égal à l'Ancien Index actuel (" + ancienIndexActuel + ").", "Données Invalides", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Recalculer la consommation avec le nouveau Nouvel Index et l'Ancien Index actuel
                double nouvelleConsommation = nouveauNouvelIndexSaisi - ancienIndexActuel;

                // CALCUL DU MONTANT PAR TRANCHES POUR LA MODIFICATION (inchangé)
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

                // Mettre à jour la facture dans la base de données
                // On met à jour Nouvel_Index, Consommation et Montant. Ancien_Index ne change pas.
                String updateFactureSQL = "UPDATE facture SET Nouvel_Index=?, Consommation=?, montant=? WHERE id_facture=?";
                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/javaswing_app", "root", "");
                     PreparedStatement psUpdate = conn.prepareStatement(updateFactureSQL)) {

                    psUpdate.setDouble(1, nouveauNouvelIndexSaisi);
                    psUpdate.setDouble(2, nouvelleConsommation);
                    psUpdate.setDouble(3, nouveauMontant);
                    psUpdate.setInt(4, factureId);


                    int rowsAffected = psUpdate.executeUpdate();

                    if (rowsAffected > 0) {
                         JOptionPane.showMessageDialog(this, "Facture ID " + factureId + " modifiée avec succès !", "Succès", JOptionPane.INFORMATION_MESSAGE);
                        chargerFactures(); // Rafraîchir la table
                        viderChamps();
                    } else {
                        JOptionPane.showMessageDialog(this, "Aucune facture trouvée avec l'ID " + factureId + " pour modification.", "Erreur", JOptionPane.ERROR_MESSAGE);
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


    private void viderChamps() {
        compteurField.setText("");
        nouvelIndexField.setText("");
        factureTable.clearSelection();
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
                        JOptionPane.showMessageDialog(this, "Aucune facture trouvée avec l'ID " + factureId + " pour suppression.", "Erreur", JOptionPane.ERROR_MESSAGE);
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

    // Méthode convertirMoisDeDate (laissée au cas où, mais probablement non utilisée par la table maintenant)
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


    // Méthode pour générer le PDF (à implémenter)
    private void genererPDF() {
         JOptionPane.showMessageDialog(this, "La génération de PDF n'est pas encore implémentée.", "Fonctionnalité future", JOptionPane.INFORMATION_MESSAGE);
    }
}