package app.smartshopper.Database;

/**
 * Created by Felix on 02.05.2016.
 */
public class Product extends DatabaseEntry {
    private int posX;
    private int posY;

    public int getPosX() {
        return posX;
    }

    public void setPosX(int posx) {
        this.posX = posx;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosY(int posy) {
        this.posY = posy;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Object && obj != null) {
            Product p = (Product) obj;
            return p.getEntryName().equals(getEntryName()) &&
                    p.getPosX() == getPosX() &&
                    p.getPosY() == getPosY();
        }
        return false;
    }

    @Override
    public String toString(){
        return getEntryName();
    }
}
