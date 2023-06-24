package com.geoderp.geoplugin.Utility;

import java.io.File;
import java.sql.*;
import java.util.logging.Level;
import java.util.ArrayList;

import org.bukkit.plugin.java.JavaPlugin;

public class Database {
    private Connection db;
    private String dbPath;
    private JavaPlugin Plugin;

    public Database(JavaPlugin plugin, String databaseName) {
        // Todo : pull database filepath from config
        this.Plugin = plugin;
        dbPath = Plugin.getDataFolder() + File.separator + databaseName;
        connect();
        createNoteTable();
    }

    public void connect() {
        db = null;
        try {
            Class.forName("org.sqlite.JDBC");
            db = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        }
        catch (Exception e) {
            Plugin.getLogger().log(Level.INFO, "Connection and Tables Error: " + e);
        }
    }

    public void createNoteTable() {
        try {
            Statement stmt = db.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS notes(id integer PRIMARY KEY, creator text, date text, target text, content text);";
            stmt.executeUpdate(sql);
            stmt.close();
        }
        catch (Exception e) {
            Plugin.getLogger().log(Level.INFO, "Error creating table in " + dbPath + " database: " + e);
        }
    }

    public void insert(String table, String creator, String date, String target, String content) {
        String sql = "";
        if (table.equals("notes")) {
            sql = "INSERT INTO notes(creator, date, target, content) VALUES(?,?,?,?)";
        }

        try {
            PreparedStatement stmt = db.prepareStatement(sql);
            stmt.setString(1, creator);
            stmt.setString(2, date);
            stmt.setString(3, target);
            stmt.setString(4, content);
            stmt.executeUpdate();
            stmt.close();
        }
        catch (Exception e) {
            Plugin.getLogger().log(Level.INFO, "Error inserting into " + dbPath + " database: " + e);
        }
    }

    public ArrayList<String[]> selectAll(String table, String criteria, String value) {
        ArrayList<String[]> results = new ArrayList<String[]>();
        String sql = "";
        criteria = criteria.trim();
        criteria = criteria.strip();
        value = value.trim();
        value = value.strip();

        if (table.equals("notes")) {
            sql = "SELECT id, creator, date, target, content FROM notes WHERE "+ criteria +" = ?";
        }

        try {
            PreparedStatement stmt = db.prepareStatement(sql);
            stmt.setString(1, value);
            ResultSet rs = stmt.executeQuery();

            while(rs.next()) {
                String[] item = new String[5];
                item[0] = String.valueOf(rs.getInt("id"));
                item[1] = rs.getString("creator");
                item[2] = rs.getString("date");
                item[3] = rs.getString("target");
                item[4] = rs.getString("content");
                results.add(item);
            }
            return results;
        }
        catch (Exception e) {
            Plugin.getLogger().log(Level.INFO, "Error selecting from " + dbPath + " database: " + e);
        }
        return results;
    }

    public int selectID(String table, String criteria, String value) {
        String sql = "";
        int foundID = -1;
        criteria = criteria.trim();
        criteria = criteria.strip();
        value = value.trim();
        value = value.strip();

        if (table.equals("notes")) {
            sql = "SELECT id FROM notes WHERE "+ criteria +" = ?";
        }

        try {
            PreparedStatement stmt = db.prepareStatement(sql);
            stmt.setString(1, value);
            ResultSet rs = stmt.executeQuery();

            while(rs.next()) {
                foundID = rs.getInt("id");
            }
            return foundID;
        }
        catch (Exception e) {
            Plugin.getLogger().log(Level.INFO, "Error selecting ID from " + dbPath + " database: " + e);
        }

        return foundID;
    }

    public ArrayList<String[]> selectRecent(String table) {
        ArrayList<String[]> results = new ArrayList<String[]>();
        String sql = "";
        if (table.equals("notes")) {
            sql = "SELECT id, creator, date, target, content FROM notes ORDER BY id DESC LIMIT 5";
        }

        try {
            PreparedStatement stmt = db.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while(rs.next()) {
                String[] item = new String[5];
                item[0] = String.valueOf(rs.getInt("id"));
                item[1] = rs.getString("creator");
                item[2] = rs.getString("date");
                item[3] = rs.getString("target");
                item[4] = rs.getString("content");
                results.add(item);
            }
            return results;
        }
        catch (Exception e) {
            Plugin.getLogger().log(Level.INFO, "Error selecting recent from " + dbPath + " database: " + e);
        }

        return results;
    }

    public String[] selectNewest(String table, String criteria, String value) {
        String[] result = {};
        String sql = "";
        criteria = criteria.trim();
        criteria = criteria.strip();
        value = value.trim();
        value = value.strip();

        if (table.equals("notes")) {
            sql = "SELECT id, creator, date, target, content FROM notes WHERE "+ criteria +" = ? ORDER BY id DESC LIMIT 1";
        }

        try {
            PreparedStatement stmt = db.prepareStatement(sql);
            stmt.setString(1, value);
            ResultSet rs = stmt.executeQuery();

            while(rs.next()) {
                result = new String[5];
                result[0] = String.valueOf(rs.getInt("id"));
                result[1] = rs.getString("creator");
                result[2] = rs.getString("date");
                result[3] = rs.getString("target");
                result[4] = rs.getString("content");
            }
            return result;
        }
        catch (Exception e) {
            Plugin.getLogger().log(Level.INFO, "Error selecting from " + dbPath + " database: " + e);
        }
        return result;
    }

    public void remove(String table, int id) {
        String sql = "";
        if(table.equals("notes")) {
            sql = "DELETE FROM notes WHERE id = ?";
        }

        try {
            PreparedStatement stmt = db.prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
        catch (Exception e) {
            Plugin.getLogger().log(Level.INFO, "Error removing from " + dbPath + " database: " + e);
        }
    }

    public void closeDatabase() {
        try {
            db.close();
        }
        catch (Exception e) {
            Plugin.getLogger().log(Level.INFO, "Error closing " + dbPath + " database: " + e);
        }
    }
}
