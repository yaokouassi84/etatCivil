package etatCivil.accueil.webSocket;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import com.google.gson.Gson;

import etatCivil.accueil.dao.PersonneDao;
import etatCivil.accueil.model.Personne;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

@ServerEndpoint("/ws/chatAddPersonne")
public class ChatWebSocketAddPersonne {

	private static final Set<Session> sessions = new CopyOnWriteArraySet<>();
    private static final Gson gson = new Gson();  // Instance Gson pour la sérialisation
    private static final PersonneDao personneDao = new PersonneDao();  // DAO pour la gestion de Personne

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
            String messagePersonne = "Nouvelle personne ajoutée : " + personne.toString();

            // Ajout de la personne à la base de données (assure-toi que le DAO est bien configuré)
            personneDao.addPersonne(personne);  // Exemple d'ajout à la base de données

            // Envoyer l'objet Personne à tous les autres clients
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
