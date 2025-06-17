package model;

public class Karakter extends GameObject {

    private boolean isHoldingMoney = false;
    private int health = 100;

    public Karakter(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    public boolean isHoldingMoney() {
        return isHoldingMoney;
    }

    public void setHoldingMoney(boolean holdingMoney) {
        isHoldingMoney = holdingMoney;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }
}