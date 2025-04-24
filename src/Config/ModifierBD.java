package Config;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ModifierBD {

	public static void main(String[] args) {
	    updateuser(2, "abdsamad", "chohaidi", "abdsamadmail.com", "4321", "U", "asfi 04");
	}
    public static void updateuser(int id,String nom ,String prenom,String email,String password ,String type,String address_h) {
    	Connection cnx =ConnecterBD.connectBD();
    	String query="UPDATE user "
    			+ " SET "
    			+ "    nom   = ?,"
    			+ "    prenom      = ?,"
    			+ "    email   = ?,"
    			+ "    password = ?,"
    			+ "    type  = ?,"
    			+ "    adress_h        = ?"
    			+ "WHERE id_user = ?";
    	try {
    	
			PreparedStatement stmt =cnx.prepareStatement(query);
    		stmt.setString(1, nom);
    		stmt.setString(2, prenom);
    		stmt.setString(3, email);
    		stmt.setString(4, password);
    		stmt.setString(5, type);
    		stmt.setString(6, address_h);
    		stmt.setInt(7, id);
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
