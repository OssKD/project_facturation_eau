package interfasseClient;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.swing.*;

public class PaymentPanel extends JPanel {

    private JTextField idField;
    private JTextField montantField;
    private JComboBox<String> methodePaiementCombo;
    private JButton payerButton;

    public PaymentPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // ID de la facture
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("ID Facture :"), gbc);

        idField = new JTextField(10);
        gbc.gridx = 1;
        add(idField, gbc);

        // Montant à payer
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Montant à payer :"), gbc);

        montantField = new JTextField(10);
        gbc.gridx = 1;
        add(montantField, gbc);

        // Méthode de paiement
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Méthode de paiement :"), gbc);

        methodePaiementCombo = new JComboBox<>(new String[]{"Carte", "Chèque"});
        gbc.gridx = 1;
        add(methodePaiementCombo, gbc);

        // Bouton Payer
        payerButton = new JButton("Payer maintenant");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        add(payerButton, gbc);

        payerButton.addActionListener(e -> effectuerPaiement());
    }

    private void effectuerPaiement() {
        String idStr = idField.getText().trim();
        String montantStr = montantField.getText().trim();
        String methode = (String) methodePaiementCombo.getSelectedItem();

        if (idStr.isEmpty() || montantStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs.");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            double montant = Double.parseDouble(montantStr);

            if (montant <= 0) {
                JOptionPane.showMessageDialog(this, "Le montant doit être supérieur à 0.");
                return;
            }

            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/ja", "root", "");
            PreparedStatement stmt = conn.prepareStatement(
                "UPDATE factures SET statut = 'Payée', methode_paiement = ? WHERE id = ? AND montant = ? AND statut = 'Non payée'"
            );
            stmt.setString(1, methode);
            stmt.setInt(2, id);
            stmt.setDouble(3, montant);

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Paiement effectué avec succès via " + methode + " !");
            } else {
                JOptionPane.showMessageDialog(this, "Aucune facture correspondante trouvée ou déjà payée.");
            }

            stmt.close();
            conn.close();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID ou montant invalide.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du paiement.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Paiement Facture");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 300);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new PaymentPanel());
            frame.setVisible(true);
        });
    }
}

