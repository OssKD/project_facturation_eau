package Config;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class Delete {
	
	public static void Deleteuser(int id) {
		try {
			Connection cnx =ConnecterBD.connectBD();
			String query ="DELETE FROM user WHERE id_user=?";
			PreparedStatement stmt = cnx.prepareStatement(query);
			stmt.setInt(1, id);
			stmt.executeUpdate();
			System.out.print("user est bien suprimer");
			
		} catch (Exception e) {
			System.out.print(e.getMessage());
		}	
	}
}
