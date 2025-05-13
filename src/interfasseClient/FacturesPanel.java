package interfasseClient;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.*;
import java.util.*;

import Config.PaymentForm;

public class FacturesPanel extends JPanel {
    private JTable facturesTable;
    private JTextField filterMonthField;
    private JButton filterButton, payerButton;
    private DefaultTableModel tableModel;
    private int id_user;

    public FacturesPanel(int id) {
        this.id_user = id;
        setLayout(new BorderLayout());

        // Panel de filtrage
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Filtrer par Mois :"));
        filterMonthField = new JTextField(10);
        filterPanel.add(filterMonthField);

        filterButton = new JButton("Filtrer");
        filterPanel.add(filterButton);

        payerButton = new JButton("Payer");
        filterPanel.add(payerButton);

        add(filterPanel, BorderLayout.NORTH);

        // Table sans checkbox
        tableModel = new DefaultTableModel(
            new Object[]{"id_facture", "Mois", "Montant", "Statut", "Télécharger"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // Seulement le bouton Télécharger est éditable
            }
        };

        facturesTable = new JTable(tableModel);
        facturesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // sélection simple par ligne

        // Ajouter les boutons "Télécharger"
        facturesTable.getColumn("Télécharger").setCellRenderer(new ButtonRenderer());
        facturesTable.getColumn("Télécharger").setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane tableScrollPane = new JScrollPane(facturesTable);
        add(tableScrollPane, BorderLayout.CENTER);

        // Charger les données
        loadFacturesData(id_user);

        // Actions des boutons
        filterButton.addActionListener(e -> filterFactures());
        payerButton.addActionListener(e -> payerFactures());
    }

    private void loadFacturesData(int id) {
        String sql = "SELECT * FROM facture WHERE id_user = ?";

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/javaswing_app", "root", "");
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            tableModel.setRowCount(0);

            while (rs.next()) {
                int id_facture = rs.getInt("id_facture");
                String mois = rs.getString("mois");
                double montant = rs.getDouble("montant");
                String statut = rs.getString("moitie");
                tableModel.addRow(new Object[]{id_facture, mois, montant, statut, "Télécharger"});
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des factures.");
        }
    }

    private void filterFactures() {
        String filterMonth = filterMonthField.getText().trim();
        String query = "SELECT * FROM facture WHERE mois LIKE ? AND id_user = ?";

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/javaswing_app", "root", "");
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, "%" + filterMonth + "%");
            stmt.setInt(2, id_user);
            ResultSet rs = stmt.executeQuery();

            tableModel.setRowCount(0);
            while (rs.next()) {
                int id_facture = rs.getInt("id_facture");
                String mois = rs.getString("mois");
                double montant = rs.getDouble("montant");
                String statut = rs.getString("moitie");
                tableModel.addRow(new Object[]{id_facture, mois, montant, statut, "Télécharger"});
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du filtrage des factures.");
        }
    }

    private void payerFactures() {
        int selectedRow = facturesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une facture à payer.");
            return;
        }

        int id_facture = (int) facturesTable.getValueAt(selectedRow, 0); // colonne 0 = id_facture

        // Ouvrir le formulaire de paiement
        PaymentForm payer = new PaymentForm();
        payer.setVisible(true);
    }

    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText("Télécharger");
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        private int selectedRow;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);

            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                    try {
                        int id_facture = (int) facturesTable.getValueAt(selectedRow, 0);
                        downloadPDF(id_facture);
                        JOptionPane.showMessageDialog(null, "Facture " + id_facture + " téléchargée avec succès !");
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Erreur lors du téléchargement de la facture.");
                    }
                }
            });
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            label = (value == null) ? "Télécharger" : value.toString();
            button.setText(label);
            isPushed = true;
            selectedRow = row;
            return button;
        }

        public Object getCellEditorValue() {
            isPushed = false;
            return label;
        }
    }

    private void downloadPDF(int id_facture) throws SQLException {
        FactureEauGenerator pdfGenerator = new FactureEauGenerator();
        pdfGenerator.generatePDF(id_facture);
    }
}
