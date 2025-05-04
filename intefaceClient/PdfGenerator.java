package interfasseClient;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class PdfGenerator {
    public void generatePDF(int id_facture) throws SQLException {
        Document document = new Document();
        
        // Déclarer les variables ici
        String nom = "";
        String email = "";
        String adress_h = "";
        double montant = 0;
        Date dateFacture = null;

        try {
            // Vérifier si le dossier existe
            File dossierFactures = new File("factures");
            if (!dossierFactures.exists()) {
                dossierFactures.mkdirs();
            }

            // Connexion à la base de données
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/javaswing_app", "root", "");
                 PreparedStatement ps = conn.prepareStatement(
                     "SELECT u.*, f.* FROM user u INNER JOIN facture f ON u.id_user = f.id_user WHERE f.id_facture = ?"
                 )) {

                ps.setInt(1, id_facture);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        // Récupérer les données de la facture et utilisateur
                        nom = rs.getString("nom");
                        email = rs.getString("email");
                        montant = rs.getDouble("montant");
                        adress_h = rs.getString("adress_h");
                        dateFacture = rs.getDate("mois");
                    } else {
                        System.out.println("Aucune facture trouvée avec l'ID : " + id_facture);
                        return; // arrêter la fonction
                    }
                }
            }

            // Chemin du fichier
            String cheminFichier = "factures/facture_" + dateFacture + " "+ nom + ".pdf";
            PdfWriter.getInstance(document, new FileOutputStream(cheminFichier));
            document.open();

            // Titre
            Paragraph titre = new Paragraph("FACTURE", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, BaseColor.BLACK));
            titre.setAlignment(Element.ALIGN_CENTER);
            document.add(titre);

            // Espace
            document.add(new Paragraph(" "));

            // Infos Client
            document.add(new Paragraph("Client : " +nom ));
            document.add(new Paragraph("email : " + email)); 
            document.add(new Paragraph("Adresse : " + adress_h));

            // Espace
            document.add(new Paragraph(" "));

            // Table des articles
            PdfPTable table = new PdfPTable(3); // 3 colonnes
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            // En-têtes
            table.addCell("id_facture ");
            table.addCell("mois");
            table.addCell("montant");
           

            // Contenu (exemple)
            table.addCell(String.valueOf(id_facture));

            table.addCell(String.valueOf(dateFacture) );
            table.addCell(String.valueOf(montant));
           ;

        
            // Ajouter la table
            document.add(table);

      

            // Footer
            document.add(new Paragraph(" "));
            Paragraph merci = new Paragraph("Merci pour votre confiance !");
            merci.setAlignment(Element.ALIGN_CENTER);
            document.add(merci);

        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        } finally {
            document.close();
        }
    }
}
