package app;

import java.util.ArrayList;

import io.javalin.http.Context;
import io.javalin.http.Handler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


/**
 * Example Index HTML class using Javalin
 * <p>
 * Generate a static HTML page using Javalin
 * by writing the raw HTML into a Java String object
 *
 * @author Timothy Wiley, 2023. email: timothy.wiley@rmit.edu.au
 * @author Santha Sumanasekara, 2021. email: santha.sumanasekara@rmit.edu.au
 */
public class PageMoviesList implements Handler {

    // URL of this page relative to http://localhost:7001/
    public static final String URL = "/movies";

    @Override
    public void handle(Context context) throws Exception {
        // Create a simple HTML webpage in a String
        String html = "<html>";

        // Add some Head information
        html = html + "<head>" + 
               "<title>List All Movies</title>";

        // Add some CSS (external file)
        html = html + "<link rel='stylesheet' type='text/css' href='common.css' />";
        html = html + "</head>";

        // Add the body
        html = html + "<body>";

        // Add the topnav
        // This uses a Java v15+ Text Block
        html = html + """
            <div class='topnav'>
                <a href='/'>Prog. 2 Sample</a>
                <a href='2X'>Prog. 2X</a>
                <a href='2Y'>Prog. 2Y</a>
                <a href='movies'>Prog. 3 Sample</a>
                <a href='moviestype'>Prog. 4 Sample</a>
            </div>
        """;

        // Add header content block
        html = html + """
            <div class='header'>
                <h1>
                    List all Movies
                </h1>
            </div>
        """;

        // Add Div for page Content
        html = html + "<div class='content'>";

        // Next we will ask this *class* for the movies
        ArrayList<String> movieTitles = getMovieTitles();

        // Add HTML for the movies list
        html = html + "<h1>All Movies</h1>";
        
        // Start the HTML list
        html = html + "<ul>";

        // Finally we can print out all of the movies
        for (String title : movieTitles) {
            html = html + "<li>" + title + "</li>";
        }

        // Finish the List HTML
        html = html + "</ul>";
        
        // 🚨 This block of code is for Pre-Req students.
        // Altneratively we can use JDBCConnection to add HTML for the movies list
        // Uncomment the code to use the JDBCConnection Objects example(s)
        // JDBCConnection jdbc = new JDBCConnection();
        // ArrayList<Movie> movies = jdbc.getMovies();
        // html = html + "<h1>All Movies with Years (from JDBCConnection)</h1>" +
        //               "<ul>";
        // for (Movie movie : movies) {
        //     html = html + "<li>" + movie.name + " was made in " + movie.year + "</li>";
        // }
        // html = html + "</ul>";
        
        // TODO: Add your code here for the other queries


        // Close Content div
        html = html + "</div>";

        // Footer
        html = html + """
            <div class='footer'>
                <p>COSC2803 - Java Programming Studio</p>
            </div>
        """;

        // Finish the HTML webpage
        html = html + "</body>" + "</html>";
        

        // DO NOT MODIFY THIS
        // Makes Javalin render the webpage
        context.html(html);
    }


    /**
     * Get all of the Movie Titles in the database
     * @return
     *    Returns an ArrayList of String with ONLY the movie titles
     */
    public ArrayList<String> getMovieTitles() {
        // Create the ArrayList to return - of Strings for the movie titles
        ArrayList<String> movies = new ArrayList<String>();

        // Setup the variable for the JDBC connection
        Connection connection = null;

        try {
            // Connect to JDBC data base
            connection = DriverManager.getConnection(JDBCConnection.DATABASE);

            // Prepare a new SQL Query & Set a timeout
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            // The Query
            String query = "SELECT * FROM movie";
            
            // Get Result
            ResultSet results = statement.executeQuery(query);

            // Process all of the results
            // The "results" variable is similar to an array
            // We can iterate through all of the database query results
            while (results.next()) {
                // We can lookup a column of the a single record in the
                // result using the column name
                // BUT, we must be careful of the column type!
                String movieName     = results.getString("mvtitle");

                // Store the movieName in the ArrayList to return
                movies.add(movieName);
            }

            // Close the statement because we are done with it
            statement.close();
        } catch (SQLException e) {
            // If there is an error, lets just pring the error
            System.err.println(e.getMessage());
        } finally {
            // Safety code to cleanup
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }

        // Finally we return all of the movie titles
        return movies;
    }

}
