package Config;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ModifierBD {


    public static void updateuser(int id,String nom ,String prenom,int numero_compteur,String email,String password ,int tel,String type,String address_h) {
    	Connection cnx =ConnecterBD.connectBD();
    	String query="UPDATE user "
    			+ " SET "
    			+ "    nom   = ?,"
    			+ "    prenom      = ?,"
    			+ "    numero_compteur      = ?,"
    			+ "    email   = ?,"
    			+ "    password = ?,"
    			+ "    tel = ?,"
    			+ "    type  = ?,"
    			+ "    adress_h        = ?"
    			+ "WHERE id_user = ?";
    	try {
    	
			PreparedStatement stmt =cnx.prepareStatement(query);
    		stmt.setString(1, nom); 
    		stmt.setString(2, prenom);
    		stmt.setInt(3, numero_compteur);
    		stmt.setString(4, email);
    		stmt.setString(5, password);
    		stmt.setInt(6, tel);
    		stmt.setString(7, type);
    		stmt.setString(8, address_h);
    		stmt.setInt(9, id);
    		  int updated = stmt.executeUpdate();  // hna bṣaḥ
              if (updated > 0) {
                  System.out.println("La mise à jour est bien établie.");
              } else {
                  System.out.println("Mafitch utilisateur b had ID: " + id);
              }
		} catch (SQLException e) {
			System.out.print(e.getMessage());
		}
    	
    }
}
