package Config;
import java.sql.*;
public class AjouteBD {
	
	public static void main(String[] args) {
		Ajouteruser(3, "karim", "zanda", "karim@gmail.com","54321", "U", "hayMatar 04");
	}
    public  static void Ajouteruser(int id,String nom ,String prenom,String email,String password ,String type,String address_h) {
    	try {
    		Connection conn =ConnecterBD.connectBD();
    		String query = "INSERT INTO user (id_user, nom, prenom, email,password, type, adress_h) VALUES (?, ?, ?, ?, ?, ?, ?)";
    		PreparedStatement stmt = conn.prepareStatement(query);

    		stmt.setInt(1, id);
    		stmt.setString(2, nom);
    		stmt.setString(3, prenom);
    		stmt.setString(4, email);
    		stmt.setString(5, password);
    		stmt.setString(6, type);
    		stmt.setString(7, address_h);

    		stmt.executeUpdate();
    		System.out.print("insertion etablie");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
    }
}
//hello evryone 
