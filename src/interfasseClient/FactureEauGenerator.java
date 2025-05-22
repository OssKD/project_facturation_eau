package interfasseClient;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;


import java.io.FileOutputStream;
import java.sql.*;

public class FactureEauGenerator {

    public static void genererFacturePDF(int idFacture, String fichierDestination) {
        String url = "jdbc:mysql://localhost:3306/javaswing_app";
        String user = "root";
        String password = "";

        try (Connection con = DriverManager.getConnection(url, user, password)) {

            String sql = "SELECT f.*, u.nom, u.prenom, u.adress_h, u.numero_compteur " +
                         "FROM facture f JOIN user u ON f.id_user = u.id_user WHERE f.id_facture = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, idFacture);
            ResultSet rs = pst.executeQuery();

            if (!rs.next()) {
                System.out.println("Facture non trouvée !");
                return;
            }

            // Données récupérées
            String nomClient = rs.getString("nom") + " " + rs.getString("prenom");
            String adresseClient = rs.getString("adress_h");
            String numeroClient = rs.getString("numero_compteur") + "";
            String numeroFacture = rs.getString("id_facture");
            String periode = rs.getString("mois");
            String dateFacture = rs.getString("mois");
            String dateEcheance = "15 jours après émission";

            int indexAncien = rs.getInt("Ancien_Index");
            int indexNouveau = rs.getInt("Nouvel_Index");
            int consommation = rs.getInt("Consommation");
            double montant = rs.getDouble("montant");

            double prixUnitaire = 2.0;
            double abonnement = 20.0;
            double taxes = 0.15 * montant;

            Document document = new Document(PageSize.A4, 50, 50, 60, 50);
            PdfWriter.getInstance(document, new FileOutputStream(fichierDestination));
            document.open();

            // --- LOGO + TITRE ---
            PdfPTable topTable = new PdfPTable(2);
            topTable.setWidthPercentage(100);
            topTable.setWidths(new float[]{1, 4});

            // Logo
            Image logo = Image.getInstance("images/logo.JPG");
            logo.scaleAbsolute(60, 60);
            PdfPCell logoCell = new PdfPCell(logo);
            logoCell.setBorder(Rectangle.NO_BORDER);
            logoCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            topTable.addCell(logoCell);

            // Titre société
            PdfPCell societeCell = new PdfPCell(new Phrase("SOCIETE DES EAUX", new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, BaseColor.BLUE)));
            societeCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            societeCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            societeCell.setPadding(10);
            societeCell.setBorder(Rectangle.NO_BORDER);
            topTable.addCell(societeCell);

            document.add(topTable);
            document.add(new LineSeparator());
            document.add(Chunk.NEWLINE);

            // --- INFOS FACTURE ---
            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.setSpacingAfter(10f);
            infoTable.addCell(cell("Facture N° :", true));
            infoTable.addCell(cell(numeroFacture, false));
            infoTable.addCell(cell("Date d'émission :", true));
            infoTable.addCell(cell(dateFacture, false));
            infoTable.addCell(cell("Période :", true));
            infoTable.addCell(cell(periode, false));
            infoTable.addCell(cell("Échéance :", true));
            infoTable.addCell(cell(dateEcheance, false));
            document.add(infoTable);

            // --- INFOS CLIENT ---
            Paragraph clientTitle = new Paragraph("Informations du client", new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.BLACK));
            clientTitle.setSpacingBefore(10f);
            clientTitle.setSpacingAfter(5f);
            document.add(clientTitle);

            document.add(new Paragraph("Nom : " + nomClient));
            document.add(new Paragraph("Adresse : " + adresseClient));
            document.add(new Paragraph("N° Compteur : " + numeroClient));
            document.add(Chunk.NEWLINE);

            // --- DÉTAILS COMPTEUR ---
            Paragraph compteurTitle = new Paragraph("Détails du compteur", new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD));
            compteurTitle.setSpacingBefore(10f);
            compteurTitle.setSpacingAfter(5f);
            document.add(compteurTitle);

            PdfPTable compteurTable = new PdfPTable(2);
            compteurTable.setWidthPercentage(60);
            compteurTable.setSpacingAfter(10f);
            compteurTable.addCell(cell("Ancien index", true));
            compteurTable.addCell(cell(indexAncien + " m³", false));
            compteurTable.addCell(cell("Nouvel index", true));
            compteurTable.addCell(cell(indexNouveau + " m³", false));
            compteurTable.addCell(cell("Consommation", true));
            compteurTable.addCell(cell(consommation + " m³", false));
            document.add(compteurTable);

            // --- DÉTAIL FACTURATION ---
            Paragraph factureTitle = new Paragraph("Détail de la facturation", new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD));
            factureTitle.setSpacingAfter(5f);
            document.add(factureTitle);

            PdfPTable factureTable = new PdfPTable(4);
            factureTable.setWidthPercentage(100);
            factureTable.setSpacingBefore(10f);
            factureTable.setWidths(new float[]{3, 2, 2, 2});

            factureTable.addCell(headerCell("Description"));
            factureTable.addCell(headerCell("Quantité"));
            factureTable.addCell(headerCell("Prix unitaire"));
            factureTable.addCell(headerCell("Total"));

            factureTable.addCell(cell("Eau consommée", false));
            factureTable.addCell(cell(consommation + " m³", false));
            factureTable.addCell(cell(String.format("%.2f €", prixUnitaire), false));
            factureTable.addCell(cell(String.format("%.2f €", consommation * prixUnitaire), false));

            factureTable.addCell(cell("Abonnement mensuel", false));
            factureTable.addCell(cell("-", false));
            factureTable.addCell(cell("-", false));
            factureTable.addCell(cell(String.format("%.2f €", abonnement), false));

            factureTable.addCell(cell("Taxes", false));
            factureTable.addCell(cell("-", false));
            factureTable.addCell(cell("-", false));
            factureTable.addCell(cell(String.format("%.2f €", taxes), false));

            PdfPCell totalCell = new PdfPCell(new Phrase("TOTAL TTC", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
            totalCell.setColspan(3);
            totalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalCell.setBackgroundColor(new BaseColor(230, 230, 250));
            factureTable.addCell(totalCell);
            factureTable.addCell(new PdfPCell(new Phrase(String.format("%.2f €", montant), new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD))));

            document.add(factureTable);

            // --- Message final ---
            document.add(Chunk.NEWLINE);
            document.add(new Paragraph("Merci pour votre confiance !", new Font(Font.FontFamily.HELVETICA, 11, Font.ITALIC, BaseColor.DARK_GRAY)));

            document.close();
            System.out.println("Facture PDF générée à : " + fichierDestination);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Cellules de contenu
    private static PdfPCell cell(String text, boolean bold) {
        Font font = new Font(Font.FontFamily.HELVETICA, 11, bold ? Font.BOLD : Font.NORMAL);
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(5);
        return cell;
    }

    // En-têtes de tableau
    private static PdfPCell headerCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE)));
        cell.setBackgroundColor(new BaseColor(0, 102, 204));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(7);
        return cell;
    }
}