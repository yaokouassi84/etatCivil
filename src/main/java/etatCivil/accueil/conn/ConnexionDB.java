package etatCivil.accueil.conn;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

public class ConnexionDB extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
    public ConnexionDB() {
        super();
        // TODO Auto-generated constructor stub
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        Connection conn = null;
        try {
            conn = MyDb.getConnection(); // 👈 Appel de ta classe MyDb
            if (conn != null && !conn.isClosed()) {
                out.println("<h2>✅ Connexion à la base de données réussie !</h2>");
            } else {
                out.println("<h2>❌ Connexion échouée !</h2>");
            }
        } catch (SQLException e) {
            out.println("<h2>❌ Erreur SQL : " + e.getMessage() + "</h2>");
            e.printStackTrace();
        } finally {
            MyDb.closeConnection(conn);
        }
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
