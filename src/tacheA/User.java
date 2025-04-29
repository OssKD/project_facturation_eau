package tacheA;
public class User {
    private int id;
    private String nom;
    private String prenom;
    private String email;
    private String password;
    private String type;
    private String adresse;

    // Constructeur par défaut
    public User() {}

    // Constructeur avec paramètres
    public User(int id, String nom, String prenom, String email, String password, String type,String adresse) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.password = password;
        this.type = type;
        this.adresse = adresse;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getType() {
        return type;
    }
    
    public String getAdresse() {
    	return adresse;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setType(String type) {
        this.type = type;
    }
    public void setAdresse(String type) {
        this.adresse = adresse;
    }
}
