package module;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class MainAppLogin {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Connexion");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(new LoginPanel(frame)); // panel li fiha l'UI
            frame.pack();
            frame.setLocationRelativeTo(null); // centrer la fenêtre
            frame.setVisible(true); 
            
        });
    }
}
