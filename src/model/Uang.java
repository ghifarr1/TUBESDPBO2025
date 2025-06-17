package model;

public class Uang extends GameObject {

    private int value;
    private boolean isCaught = false;

    public Uang(int x, int y, int width, int height, int value) {
        super(x, y, width, height);
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public boolean isCaught() {
        return isCaught;
    }

    public void setCaught(boolean caught) {
        isCaught = caught;
    }
}