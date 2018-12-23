package com.nathanaelg.jasmine;

import com.nathanaelg.jasmine.date.TimeStamp;
import com.nathanaelg.jasmine.messages.Message;
import com.nathanaelg.jasmine.messages.SummarizedMessage;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private static final String HOST = "nathanaelg.com"; //temp development host
    private static final String DATABASE = "jasmine"; //Name of database
    private static final String PORT = "3306"; //Default MySQL port
    private static final String OPTIONS = "?verifyServerCertificate=false&useSSL=true"; //Specify options here
    private static final String CONNECTIONURL = ("jdbc:mysql://" + HOST + "/"
            + DATABASE + OPTIONS); //Connection URL used with DriverManager.
    //Specifies the Java Database Connector being used, which is the MySQL connector
    private static final String USERNAME = "jasmine_user";
    private static final String PASSWORD = "Jasmine6462038941";

    public enum Tables {
        MESSAGE ("message"),
        TOKENS ("tokens"),
        USERS ("users");

        private String field;

        Tables(String field) {
            this.field = field;
        }

        public String getField() {
            return field;
        }

        @Override
        public String toString() {
            return getField();
        }
    }

    static void setMessage(Message messageBean) throws Exception {
        Connection dbConnection =
                DriverManager.getConnection(CONNECTIONURL, USERNAME, PASSWORD);
        try (PreparedStatement statement = dbConnection.prepareStatement("INSERT INTO " + Tables.MESSAGE + " SET recipient = ?, sender = ?, title = ?, message = ?, ts = ?, msgRead = 0, priority = ?;")) {
            statement.setString(1, messageBean.getRecipient());
            statement.setString(2, messageBean.getSender());
            statement.setString(3, messageBean.getTitle());
            statement.setString(4, messageBean.getMessage());
            statement.setString(5, messageBean.getTimestamp());
            statement.setInt(6, messageBean.getPriority());
            statement.executeUpdate();
        }
        dbConnection.close();
    }

    static Message getMessage(String recipient) throws Exception {
        Message message;
        Integer id;

        Connection dbConnection = DriverManager.getConnection(CONNECTIONURL, USERNAME, PASSWORD);

        PreparedStatement statement = dbConnection.prepareStatement("SELECT * FROM " + Tables.MESSAGE + " WHERE recipient = ? AND msgRead = 0 ORDER BY priority DESC, id ASC LIMIT 1;");
        statement.setString(1, recipient);
        ResultSet rs = statement.executeQuery();
        if (rs.first()) {
            id = rs.getInt("id");
            message = new Message(recipient,
                    rs.getString("sender"),
                    rs.getString("title"),
                    rs.getString("message"),
                    rs.getString("ts"));
        } else {
            statement.close();
            return new Message(null, null,"No messages", "There are no more messages at this time.", TimeStamp.getCurrentTimeStamp());
        }
        rs.close();
        statement.close();

        try (PreparedStatement statement2 = dbConnection.prepareStatement("UPDATE " + Tables.MESSAGE + " SET msgRead = 1, msgReadTs = ? WHERE id = ?;")) {
            statement2.setString(1, TimeStamp.getCurrentTimeStamp());
            statement2.setInt(2, id);
            statement2.executeUpdate();
        }

        dbConnection.close();
        return message;
    }

    static String getMessageSender(int messageID) throws Exception {
        try (ResultSet rs = executeQuery("SELECT sender FROM " + Tables.MESSAGE + " WHERE id = ?;", messageID)) {
            if (rs.next()) {
                return rs.getString(1);
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The message with the given message-id was not found.");
            }
        }
    }

    static List<SummarizedMessage> getAllSentMessages(String sender) throws Exception {
        ArrayList<SummarizedMessage> messages = new ArrayList<>();
        CachedRowSet resultSet = executeQuery("SELECT id, title, msgRead, priority FROM " + Tables.MESSAGE + " WHERE sender = ?;", sender);
        while (resultSet.next()) {
            messages.add(new SummarizedMessage(resultSet.getInt(1), resultSet.getString(2), resultSet.getBoolean(3), resultSet.getInt(4)));
        }
        return messages;
    }

    static Message getMessage(int messageID) throws Exception {
        CachedRowSet resultSet = executeQuery("SELECT * FROM " + Tables.MESSAGE + " WHERE id = ?;", messageID);
        if (resultSet.next()) {
            return new Message(resultSet.getString("recipient"),
                    resultSet.getString("sender"),
                    resultSet.getString("title"),
                    resultSet.getString("message"),
                    resultSet.getString("ts"));
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unable to find message with given message id");
        }
    }

    /**
     * Executes SQL data-manipulation statements, such as INSERT, UPDATE, DELETE, etc.
     * Besides the appropriate SQL command needed, it takes an array of arguments
     * in order to execute a sanitized PreparedStatement to avoid unsanitized inputs
     * into the database.
     * @param command SQL data manipulation statement with queries replaced with '?' to be replaced with values in @args
     * @param args Query values used for the @command, in order of appearance from left to right
     * @throws Exception SQLException or general Exceptions
     */
    public static void executeUpdate(String command, Object... args) throws Exception {
        Connection dbConnection = DriverManager.getConnection(CONNECTIONURL, USERNAME, PASSWORD);
        try (PreparedStatement statement = dbConnection.prepareStatement(command)) {
            for (int i = 1; i <= args.length; i++) {
                statement.setObject(i, args[i-1]);
            }
            statement.executeUpdate();
        }
        dbConnection.close();
    }

    public static CachedRowSet executeQuery(String query, Object... args) throws SQLException {
        Connection dbConnection = DriverManager.getConnection(CONNECTIONURL, USERNAME, PASSWORD);
        try (PreparedStatement statement = dbConnection.prepareStatement(query)) {
            for (int i = 1; i <= args.length; i++) {
                statement.setObject(i, args[i-1]);
            }
            ResultSet result = statement.executeQuery();
            CachedRowSet crs = RowSetProvider.newFactory().createCachedRowSet();
            crs.populate(result);
            statement.close();
            dbConnection.close();
            return crs;
        }
    }

    public static boolean checkExists(Database.Tables table, String whereClause, Object... args) throws SQLException {
        ResultSet result;
        int exists;
        if (whereClause != null) {
            result = executeQuery("SELECT EXISTS(SELECT 1 FROM " + table + " WHERE " + whereClause + ");", args);
        } else {
            result = executeQuery("SELECT EXISTS(SELECT 1 FROM " + table + ");");
        }
        result.next();
        exists = result.getInt(1);
        result.close();
        return exists == 1;
    }


}
