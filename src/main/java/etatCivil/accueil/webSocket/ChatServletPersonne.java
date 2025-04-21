package etatCivil.accueil.webSocket;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import com.google.gson.Gson;

import etatCivil.accueil.model.Personne;


public class ChatServletPersonne extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    
    public ChatServletPersonne() {
        super();
        // TODO Auto-generated constructor stub
    }

    private static final Gson gson = new Gson();  // Gson pour manipuler les objets Personne en JSON

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Créer un objet Personne pour tester l'envoi
        Personne personne = new Personne(1, "Dupont", new String[]{"Jean", "Paul"}, new java.util.Date(), 'M');

        // Convertir l'objet Personne en JSON
        String message = gson.toJson(personne);

        // Appeler la méthode de ChatWebSocket pour envoyer le message à tous les clients connectés
        ChatWebSocketPersonne.envoyerMessageATous(message);

        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("Message envoyé à tous les clients WebSocket : " + message);
    }
    
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
