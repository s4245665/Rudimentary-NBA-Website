
package app;

import java.util.ArrayList;
import java.util.Comparator;

import io.javalin.http.Context;
import io.javalin.http.Handler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PageMoviesType implements Handler {

    public static final String URL = "/moviestype";

    @Override
    public void handle(Context context) throws Exception {

        String html = "<html>";

        html += "<head>";
        html += "<title>Movies by Type</title>";
        html += "<link rel='stylesheet' type='text/css' href='common.css' />";
        html += "</head>";

        html += "<body>";

        html += """
            <div class='topnav'>
                <a href='/'>Prog. 2 Sample</a>
                <a href='2X'>Prog. 2X</a>
                <a href='2Y'>Prog. 2Y</a>
                <a href='movies'>Prog. 3 Sample</a>
                <a href='moviestype'>Prog. 4 Sample</a>
            </div>
        """;

        html += """
            <div class='header'>
                <h1>List Movies by Type</h1>
            </div>
        """;

        html += "<div class='content'>";

        html += "<form action='/moviestype' method='post'>";

        html += "<div class='form-group'>";
        html += "<label for='movietype_drop'>Select Movie Type (Dropdown):</label>";
        html += "<select id='movietype_drop' name='movietype_drop'>";
        html += "<option>HORROR</option>";
        html += "<option>COMEDY</option>";
        html += "<option>DRAMA</option>";
        html += "</select>";
        html += "</div>";

        html += "<div class='form-group'>";
        html += "<label for='movietype_textbox'>Movie Type (Textbox)</label>";
        html += "<input class='form-control' id='movietype_textbox' name='movietype_textbox'>";
        html += "</div>";

        html += "<button type='submit' class='btn btn-primary'>Get all of the movies!</button>";

        html += "</form>";

        String nameSort = context.formParam("nameSort");
        String yearSort = context.formParam("yearSort");
        String awardSort = context.formParam("awardSort");
        String genreSort = context.formParam("genreSort");

        String movietype_drop = context.formParam("movietype_drop");

        if (movietype_drop == null) {
            html += "<h2><i>No Results to show for dropdown</i></h2>";
        } else {
            html += outputMovies(movietype_drop, nameSort, yearSort, awardSort, genreSort);
        }

        String movietype_textbox = context.formParam("movietype_textbox");

        if (movietype_textbox == null || movietype_textbox.equals("")) {
            html += "<h2><i>No Results to show for textbox</i></h2>";
        } else {
            html += outputMovies(movietype_textbox, nameSort, yearSort, awardSort, genreSort);
        }

        html += "</div>";

        html += """
            <div class='footer'>
                <p>COSC2803 - Java Programming Studio</p>
            </div>
        """;

        html += "</body>";
        html += "</html>";

        context.html(html);
    }

    public String outputMovies(String type, String nameSort, String yearSort, String awardSort, String genreSort) {

        String html = "";

        html += "<h2>" + type + " Movies</h2>";

        ArrayList<String> movieTitles = getMoviesByType(type);

        html += "<ul>";
        for (String title : movieTitles) {
            html += "<li>" + title + "</li>";
        }
        html += "</ul>";

        JDBCConnection jdbc = new JDBCConnection();
        ArrayList<Movie> movies = jdbc.getMoviesByType(type);
        //TODO use this code to have it look if the box = descending or not and if so then use the sort thingy
        //TODO what to figure out live update
        if ("asc".equals(nameSort)) {
            movies.sort(Comparator.comparing(movie -> movie.name));
        }
        else if ("desc".equals(nameSort)) {
            movies.sort(Comparator.comparing((Movie movie) -> movie.name).reversed());
        }

        if ("asc".equals(yearSort)) {
            movies.sort(Comparator.comparingInt(movie -> movie.year));
        }
        else if ("desc".equals(yearSort)) {
            movies.sort(Comparator.comparingInt((Movie movie) -> movie.year).reversed());
        }

        if ("asc".equals(awardSort)) {
            movies.sort(Comparator.comparingInt(movie -> movie.award));
        }
        else if ("desc".equals(awardSort)) {
            movies.sort(Comparator.comparingInt((Movie movie) -> movie.award).reversed());
        }

        if ("asc".equals(genreSort)) {
            movies.sort(Comparator.comparing(movie -> movie.genre));
        }
        else if ("desc".equals(genreSort)) {
            movies.sort(Comparator.comparing((Movie movie) -> movie.genre).reversed());
        }

        html += "<h2>" + type + " Movies (from JDBCConnection)</h2>";

        html += """
    <div class='table-container'>
    <div class='table-wrapper'>
    <table class='movie-table'>
        <tr>
            <th>
                <select class='header-select' name = 'idSort'>
                    <option value=''>ID</option>
                    <option> value='asc'>Ascending</option>
                    <option value='desc'>Descending</option>
                </select>
            </th>
            <th>
                <select class='header-select' name = 'nameSort'>
                    <option value=''>Name</option>
                    <option value='asc'>Ascending</option>
                    <option value='desc'>Descending</option>
                </select>
            </th>
            <th>
                <select class='header-select' id='year_drop' name='yearSort'>
                    <option value=''>Year</option>
                    <option value='asc'>Ascending</option>
                    <option value='desc'>Descending</option>
                </select>
            </th>
            <th>
                <select class='header-select'  name = 'awardSort'>
                    <option value=''>Award</option>
                    <option value='asc'>Ascending</option>
                    <option value='desc'>Descending</option>
                </select>
            </th>
            <th>
                <select class='header-select'  name = 'genreSort'>
                    <option value=''>Genre</option>
                    <option value='asc'>Ascending</option>
                    <option> value='desc'Descending</option>
                </select>
            </th>
        </tr>
    """;

        for (Movie movie : movies) {
            html += "<tr>"
                  + "<td>" + movie.id + "</td>"
                  + "<td>" + movie.name + "</td>"
                  + "<td>" + movie.year + "</td>"
                  + "<td>" + movie.award + "</td>"
                  + "<td>" + movie.genre + "</td>"
                  + "</tr>";
        }

        html += "</table>";
        html += "</div>";

        return html;
    }

    public ArrayList<String> getMoviesByType(String movieType) {

        ArrayList<String> movies = new ArrayList<String>();

        Connection connection = null;

        try {
            connection = DriverManager.getConnection(JDBCConnection.DATABASE);

            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            String query = "SELECT * FROM movie WHERE mvtype = '" + movieType + "'";
            System.out.println(query);

            ResultSet results = statement.executeQuery(query);

            while (results.next()) {
                String title = results.getString("mvtitle");
                movies.add(title);
            }

            statement.close();

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }

        return movies;
    }
}
