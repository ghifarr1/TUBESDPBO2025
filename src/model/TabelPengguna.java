package model;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TabelPengguna extends DB {

    public TabelPengguna() throws Exception, SQLException {
        //konstruktor
        super();
    }

    public void getPengguna() {
        try {

            String query = "SELECT * FROM thasil ORDER BY skor DESC";

            createQuery(query);
        } catch (Exception e) {
            // tampilkan kesalahan jika terjadi kesalahan
            System.out.println(e.toString());
        }
    }

    public void insertPengguna(String username, int score, int count) {
        try {
            String query = "INSERT INTO thasil VALUES ('" + username + "', " + score + ", " + count + ")";
            createUpdate(query);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public void updatePengguna(String username, int score, int count) {
        try {
            String query = "UPDATE thasil SET skor=" + score + ", count=" + count + " WHERE username='" + username + "'";
            createUpdate(query);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public boolean isUsernameExists(String username) {
        boolean exists = false;
        try {
            String query = "SELECT COUNT(*) FROM thasil WHERE username='" + username + "'";
            createQuery(query);
            ResultSet rs = getResult();
            if (rs.next()) {
                if (rs.getInt(1) > 0) {
                    exists = true;
                }
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return exists;
    }

    public int getSkorByUsername(String username) {
        int skor = 0;
        try {
            String query = "SELECT skor FROM thasil WHERE username='" + username + "'";
            createQuery(query);
            ResultSet rs = getResult();
            if (rs.next()) {
                skor = rs.getInt("skor");
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return skor;
    }
}