package interfasseClient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;

public class PaymentPanel extends JPanel {

    private JTextField idField, montantField;
    private JComboBox<String> methodePaiementCombo;
    private JButton checkButton, payerButton;
    private JLabel statutLabel;

    // Palette de couleurs moderne
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 250); // Gris très pâle / lavande claire
    private static final Color MAIN_COLOR = new Color(52, 152, 219); // Bleu principal (style flat design)
    private static final Color SECONDARY_COLOR = new Color(41, 128, 185); // Bleu secondaire / hover ou focus
    private static final Color SUCCESS_COLOR = new Color(46, 204, 113); // Vert succès / validation OK
    private static final Color ERROR_COLOR = new Color(231, 76, 60); // Rouge erreur / invalide
    private static final Color TEXT_COLOR = new Color(44, 62, 80); // Couleur du texte général (titres, labels)

    // Polices modernisées
    private static final Font LABEL_FONT = new Font("Arial", Font.BOLD, 13);
    private static final Font FIELD_FONT = new Font("Arial", Font.PLAIN, 14);
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 22);
    private static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 14);

    public PaymentPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(25, 30, 25, 30));
        
        // Panneau principal
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // En-tête avec logo et titre
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(BACKGROUND_COLOR);
        
        // Icône simulée (pourrait être remplacée par une vraie image)
        JLabel iconLabel = new JLabel("💧");
        iconLabel.setFont(new Font("Dialog", Font.PLAIN, 40));
        
        // Titre
        JLabel titleLabel = new JLabel("Paiement de Facture");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(MAIN_COLOR);
        
        headerPanel.add(iconLabel);
        headerPanel.add(Box.createHorizontalStrut(15));
        headerPanel.add(titleLabel);
        
        // Formulaire
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        // ID Facture
        gbc.gridwidth = 1;
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel idLabel = new JLabel("Numéro de facture");
        idLabel.setFont(LABEL_FONT);
        idLabel.setForeground(TEXT_COLOR);
        formPanel.add(idLabel, gbc);

        idField = createStyledTextField();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.insets = new Insets(5, 0, 15, 0);
        formPanel.add(idField, gbc);

        // Montant
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.insets = new Insets(0, 0, 5, 0);
        JLabel montantLabel = new JLabel("Montant à payer (€)");
        montantLabel.setFont(LABEL_FONT);
        montantLabel.setForeground(TEXT_COLOR);
        formPanel.add(montantLabel, gbc);

        montantField = createStyledTextField();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.insets = new Insets(5, 0, 15, 0);
        formPanel.add(montantField, gbc);

        // Méthode de paiement
        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.insets = new Insets(0, 0, 5, 0);
        JLabel methodeLabel = new JLabel("Méthode de paiement");
        methodeLabel.setFont(LABEL_FONT);
        methodeLabel.setForeground(TEXT_COLOR);
        formPanel.add(methodeLabel, gbc);

        methodePaiementCombo = new JComboBox<>(new String[]{"Carte bancaire", "Chèque", "Virement bancaire"});
        methodePaiementCombo.setFont(FIELD_FONT);
        methodePaiementCombo.setBackground(Color.WHITE);
        methodePaiementCombo.setForeground(TEXT_COLOR);
        methodePaiementCombo.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.insets = new Insets(5, 0, 15, 0);
        formPanel.add(methodePaiementCombo, gbc);

        // Statut
        gbc.gridy = 6;
        gbc.gridx = 0;
        statutLabel = new JLabel("Veuillez saisir les informations de votre facture");
        statutLabel.setFont(FIELD_FONT);
        statutLabel.setForeground(Color.GRAY);
        statutLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.insets = new Insets(10, 0, 15, 0);
        formPanel.add(statutLabel, gbc);

        // Panneau des boutons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        buttonPanel.setBackground(Color.WHITE);
        
        checkButton = createStyledButton("Vérifier", MAIN_COLOR);
        payerButton = createStyledButton("Payer maintenant", SUCCESS_COLOR);
        payerButton.setEnabled(false);
        
        buttonPanel.add(checkButton);
        buttonPanel.add(payerButton);
        
        gbc.gridy = 7;
        gbc.gridx = 0;
        gbc.insets = new Insets(5, 0, 5, 0);
        formPanel.add(buttonPanel, gbc);

        // Actions
        checkButton.addActionListener(e -> verifierFacture());
        payerButton.addActionListener(e -> effectuerPaiement());

        // Assemblage final
        add(headerPanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
    }
    
    private JTextField createStyledTextField() {
        JTextField field = new JTextField(15);
        field.setFont(FIELD_FONT);
        field.setForeground(TEXT_COLOR);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        return field;
    }
    
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // Effet de survol
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }

    private void verifierFacture() {
        String idStr = idField.getText().trim();
        String montantStr = montantField.getText().trim();

        if (idStr.isEmpty() || montantStr.isEmpty()) {
            statutLabel.setText("Veuillez remplir tous les champs requis");
            statutLabel.setForeground(ERROR_COLOR);
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            double montant = Double.parseDouble(montantStr);

            // Animation de chargement
            statutLabel.setText("Vérification en cours...");
            statutLabel.setForeground(MAIN_COLOR);

            // Simuler un délai de connexion (peut être supprimé en production)
            Timer timer = new Timer(800, e -> {
                try {
                    Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/gestion_eau", "root", "");
                    PreparedStatement stmt = conn.prepareStatement(
                            "SELECT * FROM factures WHERE id = ? AND montant = ? AND statut = 'Non payée'");
                    stmt.setInt(1, id);
                    stmt.setDouble(2, montant);

                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        statutLabel.setText("✓ Facture trouvée et validée");
                        statutLabel.setForeground(SUCCESS_COLOR);
                        payerButton.setEnabled(true);
                    } else {
                        statutLabel.setText("✗ Facture non trouvée ou déjà payée");
                        statutLabel.setForeground(ERROR_COLOR);
                        payerButton.setEnabled(false);
                    }

                    rs.close();
                    stmt.close();
                    conn.close();

                } catch (SQLException ex) {
                    statutLabel.setText("Erreur de connexion à la base de données");
                    statutLabel.setForeground(ERROR_COLOR);
                    ex.printStackTrace();
                }
            });
            timer.setRepeats(false);
            timer.start();

        } catch (NumberFormatException ex) {
            statutLabel.setText("Format de numéro ou de montant invalide");
            statutLabel.setForeground(ERROR_COLOR);
        }
    }

    private void effectuerPaiement() {
        String idStr = idField.getText().trim();
        String montantStr = montantField.getText().trim();
        String methode = (String) methodePaiementCombo.getSelectedItem();

        // Dialogue de confirmation amélioré
        JPanel confirmPanel = new JPanel(new BorderLayout(10, 10));
        confirmPanel.add(new JLabel("<html><b>Détails du paiement :</b><br>" +
                "Facture n° : " + idStr + "<br>" +
                "Montant : " + montantStr + " €<br>" +
                "Méthode : " + methode + "</html>"), BorderLayout.CENTER);
                
        int confirm = JOptionPane.showConfirmDialog(this,
                confirmPanel,
                "Confirmation de paiement",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            int id = Integer.parseInt(idStr);
            double montant = Double.parseDouble(montantStr);

            // Animation de chargement
            statutLabel.setText("Traitement du paiement...");
            statutLabel.setForeground(MAIN_COLOR);
            payerButton.setEnabled(false);

            // Simuler un délai de traitement (peut être supprimé en production)
            Timer timer = new Timer(1000, e -> {
                try {
                    Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/gestion_eau", "root", "");
                    PreparedStatement stmt = conn.prepareStatement(
                            "UPDATE factures SET statut = 'Payée', methode_paiement = ? WHERE id = ? AND montant = ? AND statut = 'Non payée'");
                    stmt.setString(1, methode);
                    stmt.setInt(2, id);
                    stmt.setDouble(3, montant);

                    int rows = stmt.executeUpdate();

                    stmt.close();
                    conn.close();

                    if (rows > 0) {
                        // Message de succès avec style
                        JLabel successLabel = new JLabel("<html><div style='text-align: center;'>" +
                                "<span style='font-size: 32pt; color: " + String.format("#%02x%02x%02x", SUCCESS_COLOR.getRed(), 
                                SUCCESS_COLOR.getGreen(), SUCCESS_COLOR.getBlue()) + ";'>✓</span><br>" +
                                "<b>Paiement effectué avec succès</b><br><br>" +
                                "Un reçu a été envoyé à votre adresse email.</div></html>");
                        successLabel.setHorizontalAlignment(SwingConstants.CENTER);
                        
                        JOptionPane.showMessageDialog(this, successLabel, "Paiement réussi", 
                                JOptionPane.PLAIN_MESSAGE);
                                
                        // Réinitialiser les champs
                        idField.setText("");
                        montantField.setText("");
                        
                        statutLabel.setText("Prêt pour un nouveau paiement");
                        statutLabel.setForeground(Color.GRAY);
                    } else {
                        statutLabel.setText("Erreur : facture non trouvée ou déjà payée");
                        statutLabel.setForeground(ERROR_COLOR);
                        payerButton.setEnabled(false);
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                    statutLabel.setText("Erreur lors du traitement du paiement");
                    statutLabel.setForeground(ERROR_COLOR);
                }
            });
            timer.setRepeats(false);
            timer.start();

        } catch (Exception ex) {
            ex.printStackTrace();
            statutLabel.setText("Erreur lors du paiement");
            statutLabel.setForeground(ERROR_COLOR);
        }
    }

    // Test indépendant
    public static void main(String[] args) {
        try {
            // Appliquer le look and feel système
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Gestion des Paiements");
            frame.setContentPane(new PaymentPanel());
            frame.setSize(500, 600);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }
}