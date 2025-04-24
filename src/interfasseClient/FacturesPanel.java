package interfasseClient;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;



	public class FacturesPanel extends JPanel {
	    private JTable facturesTable;
	    private JTextField filterMonthField, filterStatusField;
	    private JButton filterButton;
	    private DefaultTableModel tableModel;

	    public FacturesPanel() {
	        setLayout(new BorderLayout());

	        // Panel de filtrage
	        JPanel filterPanel = new JPanel();
	        filterPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

	        filterPanel.add(new JLabel("Filtrer par Mois :"));
	        filterMonthField = new JTextField(10);
	        filterPanel.add(filterMonthField);

	        filterPanel.add(new JLabel("Filtrer par Statut :"));
	        filterStatusField = new JTextField(10);
	        filterPanel.add(filterStatusField);

	        filterButton = new JButton("Filtrer");
	        filterPanel.add(filterButton);

	        add(filterPanel, BorderLayout.NORTH);

	        // Table des factures
	        tableModel = new DefaultTableModel(new String[]{"Mois", "Montant", "Statut", "Télécharger"}, 0);
	        facturesTable = new JTable(tableModel);

	        // Ajout d'un bouton dans la table pour chaque ligne (télécharger PDF)
	        facturesTable.getColumn("Télécharger").setCellRenderer(new ButtonRenderer());
	        facturesTable.getColumn("Télécharger").setCellEditor(new ButtonEditor(new JCheckBox()));

	        JScrollPane tableScrollPane = new JScrollPane(facturesTable);
	        add(tableScrollPane, BorderLayout.CENTER);

	        // Charger les factures au démarrage
	        loadFacturesData();

	        // Action du bouton Filtrer
	        filterButton.addActionListener(e -> filterFactures());
	    }

	    // Charger les factures depuis la base de données
	    private void loadFacturesData() {
	        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/javaswing_app", "root", "");
	             Statement stmt = conn.createStatement();
	             ResultSet rs = stmt.executeQuery("SELECT * FROM facture")) {

	            while (rs.next()) {
	                String mois = rs.getString("mois");
	                double montant = rs.getDouble("montant");
	                String statut = rs.getString("moitie");
	                tableModel.addRow(new Object[]{mois, montant, statut, "Télécharger"});
	            }

	        } catch (SQLException ex) {
	            ex.printStackTrace();
	            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des factures.");
	        }
	    }

	    // Filtrer les factures selon les critères
	    private void filterFactures() {
	        String filterMonth = filterMonthField.getText().trim();
	        String filterStatus = filterStatusField.getText().trim();

	        // Requête pour filtrer selon le mois et le statut
	        String query = "SELECT * FROM factures WHERE mois LIKE ? AND statut LIKE ?";
	        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/gestion_eau", "root", "");
	             PreparedStatement stmt = conn.prepareStatement(query)) {

	            stmt.setString(1, "%" + filterMonth + "%");
	            stmt.setString(2, "%" + filterStatus + "%");
	            ResultSet rs = stmt.executeQuery();

	            // Vider la table actuelle
	            tableModel.setRowCount(0);

	            while (rs.next()) {
	                String mois = rs.getString("mois");
	                double montant = rs.getDouble("montant");
	                String statut = rs.getString("statut");
	                tableModel.addRow(new Object[]{mois, montant, statut, "Télécharger"});
	            }

	        } catch (SQLException ex) {
	            ex.printStackTrace();
	            JOptionPane.showMessageDialog(this, "Erreur lors du filtrage des factures.");
	        }
	    }

	    // Classe pour afficher un bouton dans la colonne "Télécharger"
	    class ButtonRenderer extends JButton implements TableCellRenderer {
	        public ButtonRenderer() {
	            setOpaque(true);
	        }

	        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
	            setText("Télécharger");
	            return this;
	        }
	    }

	    // Classe pour gérer l'action du bouton "Télécharger"
	    class ButtonEditor extends DefaultCellEditor {
	        protected JButton button;
	        private String label;
	        private boolean isPushed;

	        public ButtonEditor(JCheckBox checkBox) {
	            super(checkBox);
	            button = new JButton();
	            button.setOpaque(true);
	            button.addActionListener(new ActionListener() {
	                public void actionPerformed(ActionEvent e) {
	                    int row = facturesTable.getSelectedRow();
	                    String mois = (String) facturesTable.getValueAt(row, 0);
	                    // Ici, on appelle la fonction pour générer le PDF ou télécharger la facture
	                    downloadPDF(mois);
	                }
	            });
	        }

	        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
	            label = (value == null) ? "Télécharger" : value.toString();
	            button.setText(label);
	            isPushed = true;
	            return button;
	        }

	        public Object getCellEditorValue() {
	            if (isPushed) {
	                // Action pour télécharger le PDF de la facture
	                isPushed = false;
	            }
	            return label;
	        }
	    }

	    // Fonction pour simuler le téléchargement du PDF
	    private void downloadPDF(String mois) {
	        // Code pour générer ou télécharger le PDF (simulé ici)
	        JOptionPane.showMessageDialog(this, "Téléchargement du PDF pour le mois : " + mois);
	    }
	}


