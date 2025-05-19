package module;  

import javax.swing.*;
import java.awt.*;

public class MainAppLogin {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Créer la fenêtre de connexion
            JFrame frame = new JFrame("Connexion");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
            // Créer un panel de connexion
            frame.setContentPane(new LoginPanel(frame)); // Panel avec l'UI de connexion
            
            // Pour rendre la fenêtre pleine écran
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);  // Maximiser la fenêtre
            
            
            frame.setVisible(true); // Afficher la fenêtre
          
        });
    }
}
	 