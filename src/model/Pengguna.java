package model;

public class Pengguna {

    private String username; // id pengguna
    private int skor; // nama pengguna
    private int count; // username pengguna

    public Pengguna(){
        // konstruktor
    }

    public void setUsername(String username){
        // mengeset username pengguna
        this.username = username;
    }

    public String getUsername(){
        // mengembalikan username pengguna
        return this.username;
    }

    public void setSkor(int skor){
        // mengeset skor pengguna
        this.skor = skor;
    }

    public int getSkor(){
        // mengembalikan skor pengguna
        return this.skor;
    }

    public void setCount(int count){
        // mengeset count pengguna
        this.count = count;
    }

    public int getCount(){
        // mengembalikan count pengguna
        return this.count;
    }
}
