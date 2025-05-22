package Config;

import javax.swing.*;
import java.awt.*;

public class PaymentForm extends JFrame {

    public PaymentForm() {
        setTitle("Formulaire de Paiement");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Layout principal
        setLayout(new GridLayout(1, 2));

        // Panel Gauche : Infos Transaction
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel transactionLabel = new JLabel("Numéro de transaction : 938079");
        JLabel merchantLabel = new JLabel("Identifiant du marchand : 32996540");
        JLabel amountLabel = new JLabel("Montant :");
        JTextField amountField = new JTextField("20,00 EUR");
        amountField.setEditable(false);

       
        leftPanel.add(Box.createVerticalStrut(20));
        leftPanel.add(transactionLabel);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(merchantLabel);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(amountLabel);
        leftPanel.add(amountField);

        // Panel Droit : Choix de Paiement
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel chooseLabel = new JLabel("Choisissez votre moyen de paiement :");
        rightPanel.add(chooseLabel);
        rightPanel.add(Box.createVerticalStrut(20));

        // Boutons pour moyens de paiement
        String[] payments = {"Visa", "Mastercard", "CB"};
        for (String payment : payments) {
            JButton button = new JButton(payment);
            rightPanel.add(button);
            rightPanel.add(Box.createVerticalStrut(10));
        }

        // Ajouter les deux panels
        add(leftPanel);
        add(rightPanel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new PaymentForm().setVisible(true);
        });
    }
}

