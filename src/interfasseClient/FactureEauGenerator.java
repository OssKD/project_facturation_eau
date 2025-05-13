package interfasseClient;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

public class FactureEauGenerator {

    public void generatePDF(int id_facture) throws SQLException {
        Document document = new Document();

        // Données de la facture
        String nom = "", email = "", adresse = "";
        double montant = 0;
        Date dateFacture = null;

        try {
            File dossier = new File("factures");
            if (!dossier.exists()) dossier.mkdirs();

            // Connexion et récupération des données
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/javaswing_app", "root", "");
                 PreparedStatement ps = conn.prepareStatement("SELECT u.nom, u.email, u.adress_h, f.montant, f.mois FROM user u INNER JOIN facture f ON u.id_user = f.id_user WHERE f.id_facture = ?")) {

                ps.setInt(1, id_facture);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    nom = rs.getString("nom");
                    email = rs.getString("email");
                    adresse = rs.getString("adress_h");
                    montant = rs.getDouble("montant");
                    dateFacture = rs.getDate("mois");
                } else {
                    System.out.println("Facture introuvable");
                    return;
                }
            }

            // Vérifier si dateFacture est null
            if (dateFacture == null) {
                System.out.println("Date de facture introuvable.");
                return; 
            }

            // Génération du fichier
            String path = "factures/Facture_" + nom + "_" + dateFacture + ".pdf";
            PdfWriter.getInstance(document, new FileOutputStream(path));
            document.open();

            // Styles
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, BaseColor.DARK_GRAY);
            Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

            // Titre
            Paragraph titre = new Paragraph("FACTURE D'EAU", titleFont);
            titre.setAlignment(Element.ALIGN_CENTER);
            titre.setSpacingAfter(20);
            document.add(titre);

            // Infos client
            PdfPTable clientTable = new PdfPTable(2);
            clientTable.setWidthPercentage(100);
            clientTable.setSpacingAfter(15);

            clientTable.addCell(getCell("Nom du client :", labelFont));
            clientTable.addCell(getCell(nom, normalFont));
            clientTable.addCell(getCell("Email :", labelFont));
            clientTable.addCell(getCell(email, normalFont));
            clientTable.addCell(getCell("Adresse :", labelFont));
            clientTable.addCell(getCell(adresse, normalFont));
            clientTable.addCell(getCell("Date de facturation :", labelFont));
            clientTable.addCell(getCell(String.valueOf(dateFacture), normalFont));

            document.add(clientTable);

            // Détails de la facture
            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10);
            table.setWidths(new float[]{2f, 4f, 2f});

            table.addCell(getHeaderCell("ID Facture"));
            table.addCell(getHeaderCell("Mois"));
            table.addCell(getHeaderCell("Montant (€)"));

            table.addCell(getCell(String.valueOf(id_facture), normalFont));
            table.addCell(getCell(String.valueOf(dateFacture), normalFont));
            table.addCell(getCell(String.format("%.2f", montant), normalFont));

            document.add(table);

            // Footer
            Paragraph footer = new Paragraph("Merci pour votre confiance !", labelFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingBefore(30);
            document.add(footer);

        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        } finally {
            document.close();
        }
    }

    private PdfPCell getCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(8);
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }

    private PdfPCell getHeaderCell(String text) {
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, BaseColor.WHITE);
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(new BaseColor(0, 121, 182));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(8);
        return cell;
    }
}