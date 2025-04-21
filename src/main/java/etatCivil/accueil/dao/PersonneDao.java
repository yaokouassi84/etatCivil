package etatCivil.accueil.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import etatCivil.accueil.conn.MyDb;
import etatCivil.accueil.model.Personne;

public class PersonneDao {

    // Méthode pour ajouter une Personne dans la base de données
	public void addPersonne(Personne personne) {
	    String query = "INSERT INTO personne (id, nom, prenom, dn, s) VALUES (?, ?, ?, ?, ?)";
	    try (Connection conn = MyDb.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(query)) {

	        stmt.setInt(1, personne.getId());
	        stmt.setString(2, personne.getNom());
	        stmt.setString(3, String.join(",", personne.getPrenom()));  // Convertir les prénoms en chaîne
	        stmt.setDate(4, new java.sql.Date(personne.getDn().getTime()));  // Convertir Date en SQL Date
	        stmt.setString(5, String.valueOf(personne.getS()));

	        stmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	        System.out.println("Erreur lors de l'ajout de la personne : " + e.getMessage());
	    }
	}

}

