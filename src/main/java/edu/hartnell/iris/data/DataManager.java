package edu.hartnell.iris.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import edu.hartnell.iris.Iris;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataManager {

    private HikariDataSource hikari;
    private String HOSTNAME, PORT, USER, PASS;

    public DataManager(String host, String port, String user, String pass){
        HOSTNAME = host; PORT = port; USER = user; this.PASS = pass;
        if (!databaseExists("Iris")) {
            createDatabase();
        }

        setupHikari("Iris");
        //createOrganisationTable("CompSciClub");

        // One Time code //

        try {
            Connection CSCconnection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:8889/CSC","root","root");
            List<Contact> contacts = new ArrayList<>();
            PreparedStatement statement1 = CSCconnection.prepareStatement("" +
                    "SELECT * FROM CSC.Members"
            );
            ResultSet set = statement1.executeQuery();
            while (set.next()){
                contacts.add(new Contact(set.getString("Name"),set.getString("Nick"),
                        "member", set.getString("Email"), null));
                Iris.warn(set.getString("Name") + " contact has been added!");
            }
            CSCconnection.close();
            statement1.close();
            set.close();

            Connection connection = getConnection();
            for (Contact contact : contacts) {
                PreparedStatement statement2 = connection.prepareStatement("" +
                        "INSERT INTO Iris.CompSciClub VALUES (?, ?, ?, ?, NULL);"
                );
                statement2.setString(1, contact.getName());
                statement2.setString(2, contact.getNick());
                statement2.setString(3, contact.getPosition());
                statement2.setString(4, contact.getEmail());
                statement2.execute();
                statement2.close();
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void setupHikari(String name) {
        HikariConfig config = new HikariConfig();
        config.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        config.addDataSourceProperty("serverName", HOSTNAME);
        config.addDataSourceProperty("port", PORT);
        config.addDataSourceProperty("databaseName", name);
        config.addDataSourceProperty("user", USER);
        config.addDataSourceProperty("password", PASS);
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "1024");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("cacheServerConfiguration", "true");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        hikari = new HikariDataSource(config);
    }

    private Boolean databaseExists(String name) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection =
                    DriverManager.getConnection("jdbc:mysql://" + HOSTNAME + ":"
                    + PORT + "/mysql?zeroDateTimeBehavior=convertToNull", USER, PASS);

            ResultSet resultSet = connection.getMetaData().getCatalogs();

            while (resultSet.next()) {
                String databaseName = resultSet.getString(1);
                if (databaseName.equalsIgnoreCase(name)) {
                    return true;
                }
            }

            resultSet.close();
            connection.close();
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    private void createDatabase() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection =
                    DriverManager.getConnection("jdbc:mysql://" + HOSTNAME + ":" +
                    PORT + "/mysql?zeroDateTimeBehavior=convertToNull", USER, PASS);

            PreparedStatement statement = connection.prepareStatement("" +
                    "CREATE DATABASE Iris;"
            );
            statement.execute();

            statement.close();
            connection.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void createOrganisationTable(String TableName){
        try {
            Connection con = getConnection();
            PreparedStatement statement = con.prepareStatement("" +
                    "CREATE TABLE " + TableName + " (name VARCHAR(64), nick VARCHAR(64)," +
                    " position VARCHAR(64), email VARCHAR(64), phone VARCHAR(64));"
            );
            statement.execute();
            con.close();
            statement.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void dropDatabase(String name) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection =
                    DriverManager.getConnection("jdbc:mysql://" + HOSTNAME + ":" +
                    PORT + "/mysql?zeroDateTimeBehavior=convertToNull", USER, PASS);

            PreparedStatement dropDB = connection.prepareStatement
                    ("DROP DATABASE " + name + ";");

            dropDB.executeUpdate();

            dropDB.close();
            connection.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection(){
        try {
            return hikari.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void getOrginizations(String org){
        try {
            Connection connection = hikari.getConnection();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void close(){
        hikari.close();
    }

}
