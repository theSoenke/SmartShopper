package com.cyberland.felix.sqliteapp;

/**
 * Created by Felix on 02.05.2016.
 */
public class Product
{
    private int id;
    private String product_name;
    private int posX;
    private int posY;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getProductName()
    {
        return product_name;
    }

    public void setProductName(String product)
    {
        this.product_name = product;
    }

    public int getPosX()
    {
        return posX;
    }

    public void setPosX(int posx)
    {
        this.posX = posx;
    }

    public int getPosY()
    {
        return posY;
    }

    public void setPosY(int posy)
    {
        this.posY = posy;
    }
}
