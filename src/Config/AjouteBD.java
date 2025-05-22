package Config;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
			stmt.setString(6, encryptPassword(password)); 
			stmt.setInt(7, tel);
			stmt.setString(8, type);
			stmt.setString(9, address_h);
			
			stmt.executeUpdate();
			System.out.println("Insertion établie avec succès !");
			} catch (Exception e) {
			System.out.println("Erreur : " + e.getMessage());
			}
			}
    private static String encryptPassword(String password) {
        try {
            // 1. Obtention d'une instance de MessageDigest pour l'algorithme SHA-256
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            
            // 2. Hachage du mot de passe passé en paramètre
            byte[] hash = md.digest(password.getBytes());
            
            // 3. Conversion du tableau d'octets en une chaîne hexadécimale
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0'); // Ajout d'un zéro si besoin pour avoir 2 chiffres
                hexString.append(hex);
            }
            
            // 4. Retourne le mot de passe chiffré sous forme de chaîne hexadécimale
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
	}

