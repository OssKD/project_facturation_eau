 package interfasseClient;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import Config.PaymentForm;

/**
 * Panneau de gestion des factures
 * Permet de visualiser, filtrer et payer les factures
 */
public class FacturesPanel extends JPanel {
    // Constantes
    private static final String DB_URL = "jdbc:mysql://localhost:3306/javaswing_app";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    
    // Composants UI
    private JTable facturesTable;
    private JTextField filterMonthField;
    private JButton filterButton;
    private JButton payerButton;
    private JButton resetFilterButton;
    private DefaultTableModel tableModel;
    
    // Données
    private final int userId;
    
    /**
     * Constructeur du panneau de factures
     * @param userId Identifiant de l'utilisateur connecté
     */
    public FacturesPanel(int userId) {
        this.userId = userId;
        initializeUI();
        loadFacturesData();
        setupEventListeners();
    }
    
    /**
     * Initialisation de l'interface utilisateur
     */
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Titre du panneau
        JLabel titleLabel = new JLabel("Gestion des Factures");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titleLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        add(titleLabel, BorderLayout.NORTH);
        
        // Panneau principal (centre)
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        
        // Création du panneau de filtrage
        JPanel filterPanel = createFilterPanel();
        centerPanel.add(filterPanel, BorderLayout.NORTH);
        
        // Création de la table des factures
        JScrollPane tableScrollPane = createTablePanel();
        centerPanel.add(tableScrollPane, BorderLayout.CENTER);
        
        add(centerPanel, BorderLayout.CENTER);
    }
    
    /**
     * Création du panneau de filtrage
     * @return Le panneau de filtrage configuré
     */
    private JPanel createFilterPanel() {
        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.Y_AXIS));
        filterPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), 
                "Options de filtrage", 
                TitledBorder.LEFT, 
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12)));
        
        // Sous-panneau pour les contrôles de filtrage
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        controlsPanel.add(new JLabel("Filtrer par Mois:"));
        filterMonthField = new JTextField(10);
        controlsPanel.add(filterMonthField);
        
        
        filterButton = new JButton("Filtrer");
        filterButton.setBackground(new Color(66, 139, 202));
        filterButton.setForeground(Color.WHITE);
        controlsPanel.add(filterButton);
        
        
        resetFilterButton = new JButton("Réinitialiser");
        resetFilterButton.setBackground(new Color(192, 192, 192));
        controlsPanel.add(resetFilterButton);
        
        
        // Sous-panneau pour les actions
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        
        payerButton = new JButton("Payer les factures sélectionnées");
        payerButton.setBackground(new Color(92, 184, 92));
        payerButton.setForeground(Color.WHITE);
        payerButton.setIcon(UIManager.getIcon("FileView.floppyDriveIcon"));
        actionsPanel.add(payerButton);
        
        // Ajout des sous-panneaux au panneau principal
        filterPanel.add(controlsPanel);
        filterPanel.add(actionsPanel);
        
        return filterPanel;
    }
    
    /**
     * Création du panneau de table des factures
     * @return Le panneau de table configuré
     */
    private JScrollPane createTablePanel() {
        // Modèle de table avec colonne de sélection
        tableModel = new DefaultTableModel(
            new Object[]{"Sélectionner", "ID Facture", "Mois", "Montant (€)", "Statut", "Action"},0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Boolean.class; // Première colonne : checkbox
                return super.getColumnClass(columnIndex);
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0 || column == 5; // Seulement la case à cocher et le bouton sont éditables
            }
        };

        facturesTable = new JTable(tableModel);
        facturesTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        facturesTable.setRowHeight(35);
        facturesTable.setGridColor(new Color(230, 230, 230));
        facturesTable.setFont(new Font("Arial", Font.PLAIN, 12));
        
        // En-têtes personnalisés
        JTableHeader header = facturesTable.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 12));
        header.setBackground(new Color(240, 240, 240));
        
        // Configuration des renderers et des éditeurs personnalisés
        facturesTable.getColumn("Action").setCellRenderer(new ButtonRenderer());
        facturesTable.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox()));
        
        // Configuration des largeurs de colonnes
        facturesTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        facturesTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        facturesTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        facturesTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        facturesTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        facturesTable.getColumnModel().getColumn(5).setPreferredWidth(120);
        
        // Formatter les montants avec deux décimales
        TableColumn montantColumn = facturesTable.getColumnModel().getColumn(3);
        montantColumn.setCellRenderer(new DefaultTableCellRenderer() {
            {
                setHorizontalAlignment(JLabel.RIGHT);
            }
            
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                if (value instanceof Double) {
                    value = String.format("%.2f €", (Double) value);
                }
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(facturesTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createEtchedBorder(), 
                        "Liste des factures", 
                        TitledBorder.LEFT, 
                        TitledBorder.TOP,
                        new Font("Arial", Font.BOLD, 12)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        
        return scrollPane;
    }
    
    /**
     * Configuration des écouteurs d'événements
     */
    private void setupEventListeners() {
        filterButton.addActionListener(e -> filterFactures());
        resetFilterButton.addActionListener(e -> loadFacturesData());
        payerButton.addActionListener(e -> payerFactures());
        
        // Ajout d'un écouteur sur la touche ENTRÉE dans le champ de filtre
        filterMonthField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    filterFactures();
                }
            }
        });
    }

    /**
     * Chargement des factures depuis la base de données
     */
    private void loadFacturesData() {
        String sql = "SELECT * FROM facture WHERE id_user = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            updateTableWithResults(rs);
            
        } catch (SQLException ex) {
            showErrorDialog("Erreur lors du chargement des factures: " + ex.getMessage());
        }
    }

    /**
     * Filtrage des factures par mois
     */
    private void filterFactures() {
        String filterMonth = filterMonthField.getText().trim();
        String query = "SELECT * FROM facture WHERE id_user = ? AND mois LIKE ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            stmt.setString(2, "%" + filterMonth + "%");
            ResultSet rs = stmt.executeQuery();
            
            updateTableWithResults(rs);
            
        } catch (SQLException ex) {
            showErrorDialog("Erreur lors du filtrage des factures: " + ex.getMessage());
        }
    }
    
    /**
     * Mise à jour de la table avec les résultats d'une requête
     * @param rs Résultats de la requête
     * @throws SQLException En cas d'erreur SQL
     */
    private void updateTableWithResults(ResultSet rs) throws SQLException {
        tableModel.setRowCount(0); // Vider la table
        
        while (rs.next()) {
            int idFacture = rs.getInt("id_facture");
            String mois = rs.getString("mois");
            double montant = rs.getDouble("montant");
            String statut = rs.getString("moitie");
            
            // Personnalisation du statut pour une meilleure lisibilité
            String statusDisplay = "Non payée";
            if ("oui".equalsIgnoreCase(statut)) {
                statusDisplay = "Payée";
            } else if ("moitié".equalsIgnoreCase(statut)) {
                statusDisplay = "Payée partiellement";
            }
            
            tableModel.addRow(new Object[]{false, idFacture, mois, montant, statusDisplay, "Télécharger"});
        }
        
        // Message si aucun résultat
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, 
                    "Aucune facture trouvée avec ces critères.", 
                    "Information", 
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Paiement des factures sélectionnées
     */
    private void payerFactures() {
        List<Integer> selectedIds = new ArrayList<>();

        for (int i = 0; i < facturesTable.getRowCount(); i++) {
            Boolean isSelected = (Boolean) facturesTable.getValueAt(i, 0);
            if (isSelected != null && isSelected) {
                int idFacture = (int) facturesTable.getValueAt(i, 1);
                selectedIds.add(idFacture);
            }
        }

        if (selectedIds.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                    "Veuillez cocher au moins une facture à payer.", 
                    "Attention", 
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Ouvrir le formulaire de paiement avec les factures sélectionnées
        PaymentPanel paymentForm = new PaymentPanel();
    //    paymentForm.setSelectedFactures(selectedIds); // Cette méthode doit être ajoutée à PaymentForm
        paymentForm.setVisible(true);
        
        // Recharger les données après paiement
        // Note: idéalement, il faudrait un callback depuis PaymentForm quand le paiement est terminé
      //  paymentForm.addWindowListener(new WindowAdapter() {
//            @Override
//            public void windowClosed(WindowEvent e) {
//                loadFacturesData();
//            }
//        });
    }
    
    /**
     * Affiche une boîte de dialogue d'erreur
     * @param message Message d'erreur à afficher
     */
    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, 
                message, 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Renderer personnalisé pour les boutons dans la table
     */
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setBackground(new Color(66, 139, 202));
            setForeground(Color.WHITE);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText("Télécharger");
            return this;
        }
    }

    /**
     * Éditeur personnalisé pour les boutons dans la table
     */
    class ButtonEditor extends DefaultCellEditor {
        private final JButton button;
        private String label;
        private boolean isPushed;
        private int selectedRow;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.setBackground(new Color(66, 139, 202));
            button.setForeground(Color.WHITE);

            button.addActionListener(e -> {
                fireEditingStopped();
                handleDownloadButtonClick();
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, 
                boolean isSelected, int row, int column) {
            label = (value == null) ? "Télécharger" : value.toString();
            button.setText(label);
            isPushed = true;
            selectedRow = row;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            isPushed = false;
            return label;
        }
        
        private void handleDownloadButtonClick() {
            try {
                int idFacture = (int) facturesTable.getValueAt(selectedRow, 1);
                downloadPDF(idFacture);
                JOptionPane.showMessageDialog(null, 
                        "Facture " + idFacture + " téléchargée avec succès !",
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                showErrorDialog("Erreur lors du téléchargement de la facture: " + ex.getMessage());
            }
        }
    }

    /**
     * Téléchargement d'une facture au format PDF
     * @param idFacture Identifiant de la facture à télécharger
     * @throws SQLException En cas d'erreur SQL
     */
    private void downloadPDF(int idFacture) throws SQLException {
        // Récupération des informations de la facture pour le PDF
        String sql = "SELECT * FROM facture WHERE id_facture = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idFacture);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                // Récupérer les informations nécessaires pour le PDF
                String mois = rs.getString("mois");
                double montant = rs.getDouble("montant");
                
                // Génération du PDF
                PdfGenerator pdfGenerator = new PdfGenerator();
                //pdfGenerator.generatePDF(idFacture, mois, montant, userId);
            } else {
                throw new SQLException("Facture introuvable: ID " + idFacture);
            }
            
        } catch (SQLException ex) {
            throw new SQLException("Erreur lors de la récupération des données de facture: " + ex.getMessage());
        }
    }
}