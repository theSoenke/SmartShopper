package app.smartshopper_prototype.Database;

/**
 * Created by Felix on 02.05.2016.
 */
public class Product {
    private long id;
    private String product_name;
    private int posX;
    private int posY;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getProductName() {
        return product_name;
    }

    public void setProductName(String product) {
        this.product_name = product;
    }

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
            return p.getProductName().equals(getProductName()) &&
                    p.getPosX() == getPosX() &&
                    p.getPosY() == getPosY();
        }
        return false;
    }
}
