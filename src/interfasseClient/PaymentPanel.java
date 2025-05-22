package interfasseClient;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

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
        JLabel montantLabel = new JLabel("Montant à payer (dh)");
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

     // Désactiver le bouton Payer par défaut
     payerButton.setEnabled(false);

     if (idStr.isEmpty() || montantStr.isEmpty()) {
         statutLabel.setText("Veuillez remplir tous les champs.");
         statutLabel.setForeground(ERROR_COLOR);
         return;
     }

     try {
         int id = Integer.parseInt(idStr);
         double montantPaye = Double.parseDouble(montantStr);

         // Animation de chargement
         statutLabel.setText("Vérification en cours...");
         statutLabel.setForeground(MAIN_COLOR);

         // Utilisation d'un swing Timer pour la simulation de délai (peut être retiré)
         Timer timer = new Timer(800, e -> {
             Connection conn = null;
             PreparedStatement stmt = null;
             ResultSet rs = null;

             try {
                 conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/javaswing_app", "root", "");

                 // Première requête : Chercher la facture non payée avec l'ID donné
                 String selectNonPayeeSql = "SELECT montant FROM facture WHERE id_facture = ? AND etat_payment = 'N'";
                 stmt = conn.prepareStatement(selectNonPayeeSql);
                 stmt.setInt(1, id);
                 rs = stmt.executeQuery();

                 if (rs.next()) {
                      // Facture non payée trouvée
                     double montantDu = rs.getDouble("montant");
                     rs.close(); // Fermer le premier Resultset

                     if (montantPaye >= montantDu) {
                         statutLabel.setText("✓ Facture trouvée et montant suffisant.");
                         statutLabel.setForeground(SUCCESS_COLOR);
                         payerButton.setEnabled(true);
                     } else {
                         statutLabel.setText("✗ Montant payé insuffisant.");
                         statutLabel.setForeground(ERROR_COLOR);
                         payerButton.setEnabled(false);
                     }

                 } else {
                     // Facture pas trouvée avec etat_payment = 'N'.
                     // Il faut maintenant vérifier si elle existe tout simplement pour savoir si elle est absente ou déjà payée.

                     // Fermer le premier PreparedStatement avant de créer le second
                     if (stmt != null) stmt.close();
                     stmt = null; // Important pour le bloc finally

                     // Seconde requête : Chercher la facture avec l'ID donné, quel que soit son état de paiement
                     String selectAnySql = "SELECT etat_payment FROM facture WHERE id_facture = ?";
                     stmt = conn.prepareStatement(selectAnySql);
                     stmt.setInt(1, id);
                     rs = stmt.executeQuery();

                     if (rs.next()) {
                         // La facture existe bien avec cet ID
                         String etat = rs.getString("etat_payment"); // Récupérer l'état actuel
                         rs.close(); // Fermer le second Resultset

                         if ("N".equals(etat)) {
                              statutLabel.setText("✗ Problème de vérification (état inattendu pour ID existant).");
                             statutLabel.setForeground(ERROR_COLOR);
                             payerButton.setEnabled(false);
                         } else {
                             // La facture existe mais son état n'est pas 'N', donc elle est considérée comme payée ou autre état.
                             statutLabel.setText("ⓘ Facture ID " + id + " est déjà payée."); // Message pour déjà payé
                             statutLabel.setForeground(ERROR_COLOR); // Utiliser une couleur d'information
                             payerButton.setEnabled(false); // Le bouton Payer reste désactivé
                         }
                     } else {
                         // La facture n'existe pas du tout avec cet ID
                         statutLabel.setText("✗ Facture ID " + id + " non trouvée."); // Message pour non trouvé
                         statutLabel.setForeground(ERROR_COLOR);
                         payerButton.setEnabled(false);
                     }
                 }

             } catch (SQLException ex) {
                 statutLabel.setText("Erreur de base de données lors de la vérification.");
                 statutLabel.setForeground(ERROR_COLOR);
                 ex.printStackTrace();
             } finally {
                 // Fermer les ressources dans un bloc finally
                 try { if (rs != null) rs.close(); } catch (SQLException ex) { ex.printStackTrace(); }
                 try { if (stmt != null) stmt.close(); } catch (SQLException ex) { ex.printStackTrace(); }
                 try { if (conn != null) conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
             }
         });
         timer.setRepeats(false);
         timer.start();

     } catch (NumberFormatException ex) {
         statutLabel.setText("Format d'identifiant ou de montant invalide.");
         statutLabel.setForeground(ERROR_COLOR);
         payerButton.setEnabled(false);
     }
 }
 
    private void effectuerPaiement() {
    String idStr = idField.getText().trim();
    String montantStr = montantField.getText().trim();
    String methode = (String) methodePaiementCombo.getSelectedItem();

    JPanel confirmPanel = new JPanel(new BorderLayout(10, 10));
    confirmPanel.add(new JLabel("<html><b>Détails du paiement :</b><br>" +
            "Facture n° : " + idStr + "<br>" +
            "Montant : " + montantStr + " dh <br>" +
            "Méthode : " + methode + "</html>"), BorderLayout.CENTER);

    int confirm = JOptionPane.showConfirmDialog(this,
            confirmPanel,
            "Confirmation de paiement",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);

    if (confirm != JOptionPane.YES_OPTION) return;

    String serveurIP = "192.168.110.3"; // IP du serveur Banque
    int port = 5000;

    try {
        int id = Integer.parseInt(idStr);
        double montant = Double.parseDouble(montantStr);

        statutLabel.setText("Traitement du paiement...");
        statutLabel.setForeground(MAIN_COLOR);
        payerButton.setEnabled(false);

        Timer timer = new Timer(1000, e -> {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/javaswing_app", "root", "");

                PreparedStatement stmt = conn.prepareStatement(
                        "SELECT id_facture, id_user  FROM facture WHERE id_facture = ?");
                stmt.setInt(1, id);

                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    Map<String, Object> paiement = new HashMap<>();
                    paiement.put("id_facture", rs.getInt("id_facture"));
                    paiement.put("montant",montantStr);
                    paiement.put("user", rs.getInt("id_user"));

                    // Envoyer le Map via socket
                    try (
                        Socket socket = new Socket(serveurIP, port);
                        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())
                    ) {
                        out.writeObject(paiement);
                        out.flush();
                        statutLabel.setText("Paiement envoyé !");
                        statutLabel.setForeground(new Color(0, 128, 0));
                    } catch (IOException ioEx) {
                        ioEx.printStackTrace();
                        statutLabel.setText("Erreur d'envoi au serveur");
                        statutLabel.setForeground(ERROR_COLOR);    
                    }
                } else {
                    statutLabel.setText("Facture introuvable !");
                    statutLabel.setForeground(ERROR_COLOR);
                }

                rs.close();
                stmt.close();
                conn.close();

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

   
}