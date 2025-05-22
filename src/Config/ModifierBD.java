package Config;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
    		stmt.setString(5, encryptPassword(password));
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
