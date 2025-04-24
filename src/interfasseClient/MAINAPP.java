package interfasseClient;  // ola package li bghiti

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class MAINAPP {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Création du JFrame
            JFrame frame = new JFrame("Gestion des factures");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // On met ton panel à l’intérieur
            frame.setContentPane(new FacturesPanel());

            frame.pack();                 // ajuste taille selon préférences
            frame.setLocationRelativeTo(null);  // centre l’écran
            frame.setVisible(true);
        });
    }
}
