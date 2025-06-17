package model;

// Kelas dasar untuk semua objek yang ada di dalam game.
// Memiliki properti dasar seperti posisi (x, y) dan ukuran (width, height).
public class GameObject {
    protected int x;
    protected int y;
    protected int width;
    protected int height;

    public GameObject() {
        // Konstruktor default
    }

    public GameObject(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    // --- Getters and Setters ---
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}