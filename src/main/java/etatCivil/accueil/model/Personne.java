package etatCivil.accueil.model;

import java.time.LocalDate;
import java.time.Period;
import java.util.Arrays;
import java.util.Date;

public class Personne {
	
	private int id;
	private String nom;
	private String [] prenom;
	private Date dn;
	private char s;
	
	public Personne() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Personne(int id, String nom, String[] prenom, Date dn, char s) {
		super();
		this.id = id;
		this.nom = nom;
		this.prenom = prenom;
		this.dn = dn;
		this.s = s;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String[] getPrenom() {
		return prenom;
	}

	public void setPrenom(String[] prenom) {
		this.prenom = prenom;
	}

	public Date getDn() {
		return dn;
	}

	public void setDn(Date dn) {
		this.dn = dn;
	}

	public char getS() {
		return s;
	}

	public void setS(char s) {
		this.s = s;
	}
	// Ajouter la méthode getAge()
    public int getAge() {
        if (this.dn == null) {
            return 0;  // Si la date de naissance est null, on renvoie 0 comme âge
        }
        LocalDate naissance = new java.sql.Date(dn.getTime()).toLocalDate();
        LocalDate aujourdHui = LocalDate.now();
        return Period.between(naissance, aujourdHui).getYears();
    }
	@Override
	public String toString() {
		return "Personne [id=" + id + ", nom=" + nom + ", prenom=" + Arrays.toString(prenom) + ", dn=" + dn + ", s=" + s
				+ "]";
	}

}
