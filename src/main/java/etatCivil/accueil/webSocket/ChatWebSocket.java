package etatCivil.accueil.webSocket;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

@ServerEndpoint("/ws/chat")  // URL d’accès au WebSocket
public class ChatWebSocket {

	 private static final Set<Session> sessions = new CopyOnWriteArraySet<>();

	    @OnOpen
	    public void onOpen(Session session) {
	        sessions.add(session);
	        System.out.println("Nouvelle connexion : " + session.getId());
	    }

	    @OnMessage
	    public void onMessage(String message, Session session) {
	        System.out.println("Message reçu : " + message);
	        try {
	            session.getBasicRemote().sendText("Reçu : " + message);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }

	    @OnClose
	    public void onClose(Session session) {
	        sessions.remove(session);
	        System.out.println("Session fermée : " + session.getId());
	    }

	    // Méthode accessible depuis la servlet
	    public static void envoyerMessageATous(String message) {
	        for (Session s : sessions) {
	            if (s.isOpen()) {
	                try {
	                    s.getBasicRemote().sendText("[Serveur] " + message);
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	            }
	        }
	    }
}

