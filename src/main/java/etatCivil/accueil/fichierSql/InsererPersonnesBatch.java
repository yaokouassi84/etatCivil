package etatCivil.accueil.fichierSql;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import etatCivil.accueil.conn.MyDb;


public class InsererPersonnesBatch extends HttpServlet {
	private static final long serialVersionUID = 1L;
   
    public InsererPersonnesBatch() {
        super();
        
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		 response.setContentType("text/html");
		 response.setContentType("text/html");
	        PrintWriter out = response.getWriter();

	        // Chemin vers le fichier SQL
	        String fichierSQLPath = getServletContext().getRealPath("/WEB-INF/donneesSQL/insertBatch.sql");
	        File sqlFile = new File(fichierSQLPath);
	        
	        // Vérification si le fichier existe
	        if (!sqlFile.exists()) {
	            out.println("<h2>❌ Le fichier SQL n'a pas été trouvé : " + fichierSQLPath + "</h2>");
	            return;
	        }

	        // Lire le fichier SQL
	        StringBuilder sqlBuilder = new StringBuilder();
	        try (BufferedReader reader = new BufferedReader(new FileReader(fichierSQLPath))) {
	            String ligne;
	            while ((ligne = reader.readLine()) != null) {
	                // Ignore les lignes vides et les commentaires
	                if (!ligne.trim().isEmpty() && !ligne.trim().startsWith("--")) {
	                    sqlBuilder.append(ligne).append("\n");
	                }
	            }
	        } catch (IOException e) {
	            out.println("<h2>❌ Erreur de lecture du fichier SQL : " + e.getMessage() + "</h2>");
	            return;
	        }

	        // Exécution des requêtes SQL
	        try (Connection conn = MyDb.getConnection(); 
	        		Statement stmt = conn.createStatement()) {
	            // Séparation des requêtes par ;
	            String[] queries = sqlBuilder.toString().split(";");
	            int requetesExecutees = 0;

	            // Exécution des requêtes
	            for (String query : queries) {
	                if (!query.trim().isEmpty()) {
	                    stmt.executeUpdate(query.trim());  // Exécution de chaque requête
	                    requetesExecutees++;
	                }
	            }

	            out.println("<h2>✅ " + requetesExecutees + " requêtes SQL exécutées avec succès !</h2>");
	        } catch (SQLException e) {
	            out.println("<h2>❌ Erreur SQL : " + e.getMessage() + "</h2>");
	            e.printStackTrace(out);
	        }
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
