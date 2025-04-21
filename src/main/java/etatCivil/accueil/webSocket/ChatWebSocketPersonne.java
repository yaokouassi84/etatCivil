package etatCivil.accueil.webSocket;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import com.google.gson.Gson;

import etatCivil.accueil.model.Personne;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

@ServerEndpoint("/ws/chatPersonne")
public class ChatWebSocketPersonne {

    private static final Set<Session> sessions = new CopyOnWriteArraySet<>();
    private static final Gson gson = new Gson();  // Instance Gson pour la sérialisation

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
        System.out.println("Nouvelle connexion : " + session.getId());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("Message reçu : " + message);

        // Si le message est au format JSON et représente une Personne
        try {
            Personne personne = gson.fromJson(message, Personne.class);
            String messagePersonne = "Personne reçue : " + personne.toString();

            // Envoyer l'objet Personne en format texte à tous les autres clients
            envoyerMessageATous(messagePersonne);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                session.getBasicRemote().sendText("Erreur lors de la réception du message.");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
        System.out.println("Session fermée : " + session.getId());
    }

    // Méthode pour envoyer un message à tous les clients connectés
    public static void envoyerMessageATous(String message) {
        for (Session s : sessions) {
            if (s.isOpen()) {
                try {
                    s.getBasicRemote().sendText(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
