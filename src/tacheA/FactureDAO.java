package tacheA;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FactureDAO {
    private Connection connection;

    public FactureDAO(Connection connection) {
        this.connection = connection;
    }

    // CREATE
    public void create(Facture facture) throws SQLException {
        String sql = "INSERT INTO factures (idfacture, iduser, mois, montant) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, facture.getIdFacture());
            stmt.setInt(2, facture.getIdUser());
            stmt.setString(3, facture.getMois()); // ou LocalDate si tu stockes ça en date
            stmt.setDouble(4, facture.getMontant());
            stmt.executeUpdate();
        }
    }

    // UPDATE
    public void update(Facture facture) throws SQLException {
        String sql = "UPDATE factures SET iduser = ?, mois = ?, montant = ? WHERE idfacture = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, facture.getIdUser());
            stmt.setString(2, facture.getMois());
            stmt.setDouble(3, facture.getMontant());
            stmt.setInt(4, facture.getIdFacture());
            stmt.executeUpdate();
        }
    }

    // FIND by CLIENT (iduser)
    public List<Facture> findByUser(int idUser) throws SQLException {
        String sql = "SELECT * FROM factures WHERE iduser = ?";
        List<Facture> list = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idUser);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapFacture(rs));
            }
        }
        return list;
    }

    // FIND by MOIS (ou date)
    public List<Facture> findByMois(String mois) throws SQLException {
        String sql = "SELECT * FROM factures WHERE mois = ?";
        List<Facture> list = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, mois);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapFacture(rs));
            }
        }
        return list;
    }

    // Helper
    private Facture mapFacture(ResultSet rs) throws SQLException {
        Facture f = new Facture();
        f.setIdFacture(rs.getInt("idfacture"));
        f.setIdUser(rs.getInt("iduser"));
        f.setMois(rs.getString("mois"));
        f.setMontant(rs.getDouble("montant"));
        return f;
    }
}
