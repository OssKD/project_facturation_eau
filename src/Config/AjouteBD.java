package Config;
import java.sql.*;
public class AjouteBD {
	
	
	public static void Ajouteruser(int id, String nom, String prenom, int compteur, String email,
            String password, int tel, String type, String address_h) {
			try {
			Connection conn = ConnecterBD.connectBD();
			String query = "INSERT INTO user (id_user, nom, prenom, numero_compteur, email, password, tel, type, adress_h) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement stmt = conn.prepareStatement(query);
			
			stmt.setInt(1, id);
			stmt.setString(2, nom);
			stmt.setString(3, prenom);
			stmt.setInt(4, compteur);       // ✅ compteur vient avant email
			stmt.setString(5, email);
			stmt.setString(6, password); 
			stmt.setInt(7, tel);
			stmt.setString(8, type);
			stmt.setString(9, address_h);
			
			stmt.executeUpdate();
			System.out.println("Insertion établie avec succès !");
			} catch (Exception e) {
			System.out.println("Erreur : " + e.getMessage());
			}
			}
			
	}

