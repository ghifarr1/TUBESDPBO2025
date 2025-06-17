package viewmodel;

import java.util.ArrayList;
import model.TabelPengguna;
import model.Pengguna;

public class ProsesPengguna {

    private String error; // error yang terjadi
    private TabelPengguna tabelpengguna; // kelas untuk mengakses query tabel pengguna
    private ArrayList<Pengguna> data; // tempat menyimpan hasil query

    public ProsesPengguna(){
        // konstruktor
        try {
            tabelpengguna = new TabelPengguna(); // instansiasi TabelPengguna
            data = new ArrayList<Pengguna>(); // instansiasi list untuk data Pengguna
        } catch (Exception e) {
            error = e.toString();
        }
    }

    public void prosesDataPengguna(){
        try {
            // mengambil data di tabel pengguna
            tabelpengguna.getPengguna();
            while (tabelpengguna.getResult().next()){
                // ambil hasil query
                Pengguna pengguna = new Pengguna(); // instansiasi objek pengguna untuk setiap data pengguna

                pengguna.setUsername(tabelpengguna.getResult().getString(1)); // mengisi username
                pengguna.setSkor(tabelpengguna.getResult().getInt(2)); // mengisi skor
                pengguna.setCount(tabelpengguna.getResult().getInt(3)); // mengisi count

                data.add(pengguna);// tambahkan data pengguna ke dalam list
            }

            // tutup koneksi
            tabelpengguna.closeResult();
            tabelpengguna.closeConnection();
        } catch (Exception e) {
            // memproses error
            error = e.toString();
        }
    }

    public String getUsername(int i){
        // mengembalikan username pengguna dengan indeks ke i
        return data.get(i).getUsername();
    }

    public int getSkor(int i){
        // mengembalikan skor pengguna dengan indeks ke i
        return data.get(i).getSkor();
    }

    public int getCount(int i){
        // mengembalikan count pengguna dengan indeks ke i
        return data.get(i).getCount();
    }

    public int getSize(){
        // mengembalikan banyaknya data pengguna yang masuk ke dalam list
        return data.size();
    }

    public String getError(){
        // mengembalikan error
        return this.error;
    }
}
