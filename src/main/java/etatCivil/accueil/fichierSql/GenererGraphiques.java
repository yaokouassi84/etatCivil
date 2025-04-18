package etatCivil.accueil.fichierSql;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.statistics.HistogramDataset;

import etatCivil.accueil.conn.MyDb;
import etatCivil.accueil.model.Personne;


public class GenererGraphiques extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    
    public GenererGraphiques() {
        super();
        
    }

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("image/png");
	    OutputStream out = response.getOutputStream();

	    ArrayList<Personne> personnes = new ArrayList<>();

	    // --- Récupération des données ---
	    try (Connection conn = MyDb.getConnection();
	         Statement stmt = conn.createStatement();
	         ResultSet rs = stmt.executeQuery("SELECT * FROM personne")) {

	        while (rs.next()) {
	            Personne p = new Personne();
	            p.setId(rs.getInt("id"));
	            p.setNom(rs.getString("nom"));
	            p.setPrenom(rs.getString("prenom").split("\\s*,\\s*"));
	            p.setDn(rs.getDate("dn"));
	            p.setS(rs.getString("s").charAt(0));
	            personnes.add(p);
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur lors de la récupération des données.");
	        return;
	    }

	    try {
	        // --- 1. Pie Chart : Répartition des sexes ---
	        int hommeCount = 0, femmeCount = 0;
	        for (Personne p : personnes) {
	            if (p.getS() == 'H') hommeCount++;
	            else femmeCount++;
	        }

	        DefaultPieDataset pieDataset = new DefaultPieDataset();
	        pieDataset.setValue("Hommes", hommeCount);
	        pieDataset.setValue("Femmes", femmeCount);

	        JFreeChart pieChart = ChartFactory.createPieChart(
	            "Répartition des sexes", pieDataset, true, true, false
	        );

	        // Personnalisation des couleurs du camembert
	        org.jfree.chart.plot.PiePlot plot = (org.jfree.chart.plot.PiePlot) pieChart.getPlot();
	        plot.setSectionPaint("Hommes", new java.awt.Color(66, 135, 245));  // Bleu
	        plot.setSectionPaint("Femmes", new java.awt.Color(255, 105, 180)); // Rose

	        // --- Autres graphiques si besoin (non envoyés ici, juste en exemple) ---

	        // 2. Bar chart : Âge par personne
	        DefaultCategoryDataset ageDataset = new DefaultCategoryDataset();
	        for (Personne p : personnes) {
	            ageDataset.addValue(p.getAge(), "Âge", p.getNom());
	        }
	        JFreeChart ageBarChart = ChartFactory.createBarChart(
	            "Répartition des âges", "Nom", "Âge", ageDataset,
	            PlotOrientation.VERTICAL, false, true, false
	        );

	        // 3. Histogramme des âges
	        org.jfree.data.statistics.HistogramDataset histDataset = new org.jfree.data.statistics.HistogramDataset();
	        double[] ages = personnes.stream().mapToDouble(Personne::getAge).toArray();
	        histDataset.addSeries("Âges", ages, 10);
	        JFreeChart histChart = ChartFactory.createHistogram(
	            "Distribution des âges", "Âge", "Fréquence",
	            histDataset, PlotOrientation.VERTICAL, false, true, false
	        );

	        // 4. Moyenne d'âge par sexe
	        DefaultCategoryDataset moyenneDataset = new DefaultCategoryDataset();
	        double avgH = personnes.stream().filter(p -> p.getS() == 'H').mapToInt(Personne::getAge).average().orElse(0);
	        double avgF = personnes.stream().filter(p -> p.getS() == 'F').mapToInt(Personne::getAge).average().orElse(0);
	        moyenneDataset.addValue(avgH, "Homme", "Moyenne");
	        moyenneDataset.addValue(avgF, "Femme", "Moyenne");
	        JFreeChart moyenneBarChart = ChartFactory.createBarChart(
	            "Moyenne d'âge par sexe", "Sexe", "Âge moyen", moyenneDataset,
	            PlotOrientation.VERTICAL, false, true, false
	        );

	        // --- Exporter UN graphique en PNG ---
	        JFreeChart chartToDisplay = pieChart; // Change ici si tu veux afficher un autre graphique

	        java.awt.image.BufferedImage image = chartToDisplay.createBufferedImage(800, 600);
	        javax.imageio.ImageIO.write(image, "PNG", out);

	    } catch (Exception e) {
	        e.printStackTrace();
	        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur lors de la génération du graphique.");
	    }
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
