package etatCivil.accueil.fichierSql;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;

import etatCivil.accueil.conn.MyDb;
import etatCivil.accueil.model.Personne;


public class ListePersonnes extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    
    public ListePersonnes() {
        super();
        
    }
    private int calculerAge(java.util.Date dateNaissance) {
        if (dateNaissance == null) return 0;
        LocalDate naissance = new java.sql.Date(dateNaissance.getTime()).toLocalDate();
        LocalDate aujourdHui = LocalDate.now();
        return Period.between(naissance, aujourdHui).getYears();
    }
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		 response.setContentType("text/html;charset=UTF-8");
		 PrintWriter out = response.getWriter();

	        ArrayList<Personne> personnes = new ArrayList<>();

	        try (Connection conn = MyDb.getConnection();
	             Statement stmt = conn.createStatement();
	             ResultSet rs = stmt.executeQuery("SELECT * FROM personne")) {

	            while (rs.next()) {
	                Personne p = new Personne();
	                p.setId(rs.getInt("id"));
	                p.setNom(rs.getString("nom"));
	                p.setPrenom(rs.getString("prenom").split("\\s*,\\s*"));
	                p.setDn(rs.getDate("dn"));
	                p.setS(rs.getString("s").charAt(0));
	                personnes.add(p);
	            }

	        } catch (Exception e) {
	            e.printStackTrace();
	            out.println("<p class='text-danger'>Erreur lors de la récupération des données.</p>");
	            return;
	        }

	        // HTML + Bootstrap + DataTables
	        out.println("<!DOCTYPE html>");
	        out.println("<html lang='fr'>");
	        out.println("<head>");
	        out.println("<meta charset='UTF-8'>");
	        out.println("<meta name='viewport' content='width=device-width, initial-scale=1'>");
	        out.println("<title>Liste des personnes</title>");
	        out.println("<link href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css' rel='stylesheet'>");
	        out.println("<link href='https://cdn.datatables.net/1.13.6/css/dataTables.bootstrap5.min.css' rel='stylesheet'>");
	        out.println("<link href='https://cdn.datatables.net/buttons/2.4.1/css/buttons.bootstrap5.min.css' rel='stylesheet'>");
	        out.println("<style>.femme { color: red; }</style>");
	        out.println("</head>");

	        out.println("<body class='bg-light'>");
	        out.println("<div class='container mt-5'>");
	        out.println("<h1 class='display-4 text-center mb-4'>Liste des personnes</h1>");
	        out.println("<div class='table-responsive'>");
	        out.println("<table id='personnesTable' class='table table-bordered table-striped table-hover'>");
	        out.println("<thead class='table-dark'><tr><th>ID</th><th>Nom</th><th>Prénoms</th><th>Date de naissance</th><th>Sexe</th><th>Âge</th></tr></thead>");
	        out.println("<tbody>");

	        for (Personne p : personnes) {
	            String cssClass = (p.getS() == 'F') ? "femme" : "";
	            int age = calculerAge(p.getDn());

	            out.println("<tr class='" + cssClass + "'>");
	            out.println("<td>" + p.getId() + "</td>");
	            out.println("<td>" + p.getNom() + "</td>");
	            out.println("<td>" + String.join(", ", p.getPrenom()) + "</td>");
	            out.println("<td>" + p.getDn() + "</td>");
	            out.println("<td>" + p.getS() + "</td>");
	            out.println("<td>" + age + " ans</td>");
	            out.println("</tr>");
	        }

	        out.println("</tbody>");
	        out.println("</table>");
	        out.println("</div></div>");

	        // Scripts DataTables + Export
	        out.println("<script src='https://code.jquery.com/jquery-3.7.0.min.js'></script>");
	        out.println("<script src='https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js'></script>");
	        out.println("<script src='https://cdn.datatables.net/1.13.6/js/jquery.dataTables.min.js'></script>");
	        out.println("<script src='https://cdn.datatables.net/1.13.6/js/dataTables.bootstrap5.min.js'></script>");
	        out.println("<script src='https://cdn.datatables.net/buttons/2.4.1/js/dataTables.buttons.min.js'></script>");
	        out.println("<script src='https://cdn.datatables.net/buttons/2.4.1/js/buttons.bootstrap5.min.js'></script>");
	        out.println("<script src='https://cdnjs.cloudflare.com/ajax/libs/jszip/3.10.1/jszip.min.js'></script>");
	        out.println("<script src='https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.2.7/pdfmake.min.js'></script>");
	        out.println("<script src='https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.2.7/vfs_fonts.js'></script>");
	        out.println("<script src='https://cdn.datatables.net/buttons/2.4.1/js/buttons.html5.min.js'></script>");
	        out.println("<script src='https://cdn.datatables.net/buttons/2.4.1/js/buttons.print.min.js'></script>");
	        out.println("<script>");
	        out.println("$(document).ready(function() {");
	        out.println("  $('#personnesTable').DataTable({");
	        out.println("    dom: 'Bfrtip',");
	        out.println("    buttons: ['copy', 'csv', 'excel', 'pdf', 'print'],");
	        out.println("    language: { url: '//cdn.datatables.net/plug-ins/1.13.6/i18n/fr-FR.json' }");
	        out.println("  });");
	        out.println("});");
	        out.println("</script>");

	        out.println("</body></html>");
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
