package tacheA;
import java.time.LocalDate;

public class Paiement {
    private double montant;
    private LocalDate date;

    // Constructeur par défaut
    public Paiement() {}

    // Constructeur avec paramètres
    public Paiement(double montant, LocalDate date) {
        this.montant = montant;
        this.date = date;
    }

    // Getters
    public double getMontant() {
        return montant;
    }

    public LocalDate getDate() {
        return date;
    }

    // Setters
    public void setMontant(double montant) {
        this.montant = montant;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
