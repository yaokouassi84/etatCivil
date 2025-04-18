package etatCivil.accueil.fichierSql;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import etatCivil.accueil.conn.MyDb;
import etatCivil.accueil.model.Personne;

public class ImprimerPersonne extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    
    public ImprimerPersonne() {
        super();
        // TODO Auto-generated constructor stub
    }

    private int calculerAge(java.util.Date dateNaissance) {
        if (dateNaissance == null) return 0;
        java.time.LocalDate naissance = new java.sql.Date(dateNaissance.getTime()).toLocalDate();
        java.time.LocalDate aujourdHui = java.time.LocalDate.now();
        return java.time.Period.between(naissance, aujourdHui).getYears();
    }
    private Image genererQRCode(String texte, int width, int height) throws Exception {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix bitMatrix = writer.encode(texte, BarcodeFormat.QR_CODE, width, height);

        BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", baos);
        baos.flush();

        return Image.getInstance(baos.toByteArray());
    }

    private Image genererCode128(String texte, int width, int height) throws Exception {
        Code128Writer writer = new Code128Writer();
        BitMatrix bitMatrix = writer.encode(texte, BarcodeFormat.CODE_128, width, height);

        BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", baos);
        baos.flush();

        return Image.getInstance(baos.toByteArray());
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String idParam = request.getParameter("id");

	    if (idParam == null || idParam.isEmpty()) {
	        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de la personne manquant.");
	        return;
	    }

	    int id;
	    try {
	        id = Integer.parseInt(idParam);
	    } catch (NumberFormatException e) {
	        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID invalide.");
	        return;
	    }

	    Personne p = null;

	    try (Connection conn = MyDb.getConnection();
	         PreparedStatement stmt = conn.prepareStatement("SELECT * FROM personne WHERE id = ?")) {

	        stmt.setInt(1, id);
	        ResultSet rs = stmt.executeQuery();

	        if (rs.next()) {
	            p = new Personne();
	            p.setId(rs.getInt("id"));
	            p.setNom(rs.getString("nom"));
	            p.setPrenom(rs.getString("prenom").split("\\s*,\\s*"));
	            p.setDn(rs.getDate("dn"));
	            p.setS(rs.getString("s").charAt(0));
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur lors de la récupération de la personne.");
	        return;
	    }

	    if (p == null) {
	        response.sendError(HttpServletResponse.SC_NOT_FOUND, "Personne introuvable.");
	        return;
	    }

	    response.setContentType("application/pdf");
	    response.setHeader("Content-Disposition", "inline; filename=facture_personne_" + id + ".pdf");

	    try {
	        Document document = new Document();
	        PdfWriter.getInstance(document, response.getOutputStream());
	        document.open();

	        // Logo (optionnel)
	        try {
	            Image logo = Image.getInstance("https://upload.wikimedia.org/wikipedia/commons/thumb/3/3f/Logo_Java.svg/1200px-Logo_Java.svg.png");
	            logo.scaleToFit(70, 70);
	            logo.setAlignment(Image.ALIGN_LEFT);
	            document.add(logo);
	        } catch (Exception ex) {
	            System.out.println("Erreur chargement du logo : " + ex.getMessage());
	        }

	        // Titre centré
	        Paragraph titre = new Paragraph("FACTURE / FICHE D’IDENTITÉ", 
	                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK));
	        titre.setAlignment(Element.ALIGN_CENTER);
	        document.add(titre);
	        document.add(new Paragraph(" "));

	        // Ajouter le QR Code avec les informations
	        try {
	            String texteQR = "ID: " + p.getId() + "\nNom: " + p.getNom() + "\nPrénoms: " + String.join(" ", p.getPrenom());
	            Image qrCode = genererQRCode(texteQR, 150, 150);
	            qrCode.setAlignment(Image.ALIGN_CENTER);
	            document.add(new Paragraph("\nQR Code avec infos personnelles :", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
	            document.add(qrCode);
	        } catch (Exception e) {
	            e.printStackTrace();
	            document.add(new Paragraph("QR Code non généré."));
	        }

	        // Tableau de données
	        PdfPTable table = new PdfPTable(2);
	        table.setWidthPercentage(100);
	        table.setSpacingBefore(20f);
	        table.setWidths(new int[]{2, 4});

	        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);
	        Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
	        BaseColor headerBg = new BaseColor(44, 62, 80); // gris foncé
	        BaseColor borderColor = BaseColor.LIGHT_GRAY;

	        String[][] infos = {
	            {"Nom", p.getNom()},
	            {"Prénoms", String.join(" ", p.getPrenom())},
	            {"Date de naissance", p.getDn().toString()},
	            {"Sexe", p.getS() == 'F' ? "Féminin" : "Masculin"},
	            {"Âge", calculerAge(p.getDn()) + " ans"}
	        };

	        for (String[] row : infos) {
	            PdfPCell header = new PdfPCell(new Phrase(row[0], headerFont));
	            header.setBackgroundColor(headerBg);
	            header.setPadding(8);
	            header.setBorderColor(borderColor);
	            table.addCell(header);

	            PdfPCell value = new PdfPCell(new Phrase(row[1], cellFont));
	            value.setPadding(8);
	            value.setBorderColor(borderColor);
	            table.addCell(value);
	        }

	        document.add(table);

	        // Ajouter le Code128
	        try {
	            String code128Data = "ID:" + p.getId();
	            Image code128 = genererCode128(code128Data, 300, 100);
	            document.add(new Paragraph("\nCode-barres (Code128) :", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
	            document.add(code128);
	        } catch (Exception e) {
	            e.printStackTrace();
	            document.add(new Paragraph("Code-barres non généré."));
	        }

	        // Signature et pied de page
	        document.add(new Paragraph(" "));
	        Paragraph signatureLabel = new Paragraph("Signature :", FontFactory.getFont(FontFactory.HELVETICA, 12));
	        signatureLabel.setAlignment(Element.ALIGN_RIGHT);
	        document.add(signatureLabel);

	        Paragraph signatureLine = new Paragraph("_________________________", FontFactory.getFont(FontFactory.HELVETICA, 12));
	        signatureLine.setAlignment(Element.ALIGN_RIGHT);
	        document.add(signatureLine);

	        document.add(new Paragraph(" "));
	        Paragraph date = new Paragraph("Document généré le " + java.time.LocalDate.now(),
	                FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, BaseColor.GRAY));
	        date.setAlignment(Element.ALIGN_CENTER);
	        document.add(date);

	        document.close();

	    } catch (Exception e) {
	        e.printStackTrace();
	        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur lors de la génération du PDF.");
	    }
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
