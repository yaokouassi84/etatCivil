package etatCivil.accueil.service;
import java.util.ArrayList;
import java.util.List;

import etatCivil.accueil.model.Personne;

public class PersonneService {

    // Liste en mémoire pour stocker les Personnes
    private static List<Personne> personnes = new ArrayList<>();

    // Méthode pour ajouter une personne
    public static void addPersonne(Personne personne) {
        personnes.add(personne);
    }

    // Méthode pour obtenir toutes les personnes
    public static List<Personne> getPersonnes() {
        return personnes;
    }
}
