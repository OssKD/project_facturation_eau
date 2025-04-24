package Config;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class RechercherBD {

	public static void main(String[] args) {
	     rechercheruser(1);
	}
    public static void rechercheruser(int id) {
    	try {
			Connection cnx =ConnecterBD.connectBD();
			String query= "SELECT * FROM user WHERE id_user=?";
			PreparedStatement stmt =cnx.prepareStatement(query);
			stmt.setInt(1, id);
		    ResultSet rst =stmt.executeQuery();
		    rst.next();
		    int nbrRows = rst.getRow();
		    if(nbrRows != 0) {   
		        	System.out.println("ID: " + rst.getInt("id_user"));
                  System.out.println("Username: " + rst.getString("nom"));}
		    else {System.out.print("user n'est trouve");}
		} catch (Exception e) {
		System.out.print(e.getMessage());
		}
    }
}
//public static void getUserById(int id) {
//    String query = "SELECT * FROM user WHERE id_user = ?";
//    try (
//        Connection cnx = ConnecterBD.connectBD();
//        PreparedStatement stmt = cnx.prepareStatement(query)
//    ) {
//        stmt.setInt(1, id);                   // bddl '?'
//        try (ResultSet rs = stmt.executeQuery()) {
//            if (rs.next()) {
//          
//                // … zid li bghiti
//            } else {
//                System.out.println("Mafitch utilisateur b had ID.");
//            }
//        }
//    } catch (Exception e) {
//        System.out.println("Erreur: " + e.getMessage());
//    }
//}
