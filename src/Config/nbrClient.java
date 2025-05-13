package Config;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;

public class nbrClient {

	
            public static int nbrclient() {
    
    		    int count = 0; // Déclaré en dehors du bloc try

    		    try {
    		        Connection conn = ConnecterBD.connectBD();
    		        String query = "SELECT COUNT(*) FROM user";
    		        PreparedStatement stmt = conn.prepareStatement(query);
    		        ResultSet rs = stmt.executeQuery();

    		        if (rs.next()) {
    		            count = rs.getInt(1); // Le nombre total d'utilisateurs
    		        }

    		    } catch (Exception e) {
    		        System.out.println("Erreur : " + e.getMessage());
    		    }

    		    return count;
    		}
            public static int nbrFacture() {
                
    		    int count = 0; // Déclaré en dehors du bloc try

    		    try {
    		        Connection conn = ConnecterBD.connectBD();
    		        String query = "SELECT COUNT(*) FROM facture";
    		        PreparedStatement stmt = conn.prepareStatement(query);
    		        ResultSet rs = stmt.executeQuery();

    		        if (rs.next()) {
    		            count = rs.getInt(1); // Le nombre total d'utilisateurs
    		        }

    		    } catch (Exception e) {
    		        System.out.println("Erreur : " + e.getMessage());
    		    }

    		    return count;
    		}
		    public static int nbrFactureImpayer() {
			                
			    		    int count = 0; // Déclaré en dehors du bloc try
			
					    try {
					        Connection conn = ConnecterBD.connectBD();
				        String query = "SELECT COUNT(*) FROM facture where etat_payment='N'";
					        PreparedStatement stmt = conn.prepareStatement(query);
					        ResultSet rs = stmt.executeQuery();
			
					        if (rs.next()) {
					            count = rs.getInt(1); // Le nombre total d'utilisateurs
					        }
			
					    } catch (Exception e) {
					        System.out.println("Erreur : " + e.getMessage());
					    }
			
					    return count;
					}
		  

		    public static String revenueMensuel() {
		        double totalRevenue = 0.0;

		        try {
		            Connection conn = ConnecterBD.connectBD();
		            String query = "SELECT SUM(montant) FROM facture WHERE etat_payment = 'P'";
		            PreparedStatement stmt = conn.prepareStatement(query);
		            ResultSet rs = stmt.executeQuery();

		            if (rs.next()) {
		                totalRevenue = rs.getDouble(1); // Récupère le total des montants
		            }

		        } catch (Exception e) {
		            System.out.println("Erreur : " + e.getMessage());
		        }

		        // Formater le résultat à 2 décimales
		        DecimalFormat df = new DecimalFormat("#.00");
		        return df.format(totalRevenue); // Retourne le résultat sous forme de String avec 2 décimales
		    }


}
