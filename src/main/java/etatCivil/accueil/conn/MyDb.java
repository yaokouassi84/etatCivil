package etatCivil.accueil.conn;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDb {
    private static final String URL = "jdbc:mysql://localhost:3306/etatcivil"; 
    private static final String USER = "root";
    private static final String PASSWORD = ""; 

    // Méthode pour ouvrir une connexion
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); 
        } catch (ClassNotFoundException e) {
            System.out.println("Erreur de chargement du driver JDBC");
            e.printStackTrace();
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Méthode pour fermer une connexion
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.out.println("Erreur lors de la fermeture de la connexion");
                e.printStackTrace();
            }
        }
    }
}
