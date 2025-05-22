package Config;
import java.sql.Connection;
import java.sql.DriverManager;

public class ConnecterBD {
    public  static void main(String[] args) {
		Connection cnx = connectBD();
	}
	public static Connection  connectBD() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			System.out.println("connection OKi");
			String url="jdbc:mysql://localhost:3306/javaswing_app";
			String user ="root";
			String pdw ="";
			Connection cnx = DriverManager.getConnection(url, user, pdw);	
			System.out.print("la connection est bien etablie");
			return cnx ;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
}
