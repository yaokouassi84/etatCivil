package etatCivil.accueil.fichierSql;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import etatCivil.accueil.conn.MyDb;


public class ExecuterScriptSQL extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
   
    public ExecuterScriptSQL() {
        super();
        
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // 📍 Chemin relatif dans le projet web
        String path = getServletContext().getRealPath("/WEB-INF/donneesSQL/tables.sql");

        StringBuilder sqlBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sqlBuilder.append(line).append("\n");
            }
        } catch (IOException e) {
            out.println("<h2>❌ Erreur de lecture du fichier SQL : " + e.getMessage() + "</h2>");
            return;
        }

        // ✅ Exécution du SQL
        try (Connection conn = MyDb.getConnection(); 
        		Statement stmt = conn.createStatement()) {
            stmt.execute(sqlBuilder.toString());
            out.println("<h2>✅ Script exécuté avec succès !</h2>");
        } catch (SQLException e) {
            out.println("<h2>❌ Erreur SQL : " + e.getMessage() + "</h2>");
            e.printStackTrace();
        }
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
