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

