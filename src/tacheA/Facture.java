package tacheA;

public class Facture {
    private String mois;
    private int idFacture;
    private double montant;
    private int idUser;
    private String etat_payee ;

    // Constructeur par défaut
    public Facture() {}

    // Constructeur complet
    public Facture(String mois, int idFacture, double montant, int idUser,String etat_payee) {
        this.mois = mois;
        this.idFacture = idFacture;
        this.montant = montant;
        this.idUser = idUser; 
        this.etat_payee = etat_payee;
    }

    // Getters
    public String getMois() {
        return mois;
    }

    public int getIdFacture() {
        return idFacture;
    }

    public double getMontant() {
        return montant;
    }

    public int getIdUser() {
        return idUser;
    }
    public String getetat_payee() {
        return etat_payee;
    }
    

    // Setters
    public void setMois(String mois) {
        this.mois = mois;
    }

    public void setIdFacture(int idFacture) {
        this.idFacture = idFacture;
    }

    public void setMontant(double montant) {
        this.montant = montant;
    }

    public void setIdUser(int id) {
        this.idUser = id;
    }
    public void setetat_payee(String etat_payee) {
        this.etat_payee = etat_payee;
    }
    
}
