package etatCivil.accueil.webSocket;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;


public class ChatServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    
    public ChatServlet() {
        super();
       
    }

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String message = request.getParameter("msg");
        if (message == null || message.isBlank()) {
            message = "Message par défaut depuis la servlet";
        }

        // Appel de la méthode static du WebSocket
        ChatWebSocket.envoyerMessageATous(message);

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("Message envoyé à tous les clients WebSocket : " + message);
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
