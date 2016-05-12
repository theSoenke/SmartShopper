package com.cyberland.felix.myibeaconapplication.Trilateration;

/**
 * Created by Studium on 28.04.2016.
 */
public class Vector
{
    public final double x;
    public final double y;
    public final double laenge;

    public Vector(double x, double y)
    {
        this.x=x;
        this.y=y;
        this.laenge =  Math.sqrt(Math.pow(x, 2)+Math.pow(y, 2));
    }

    public Vector mal(double faktor)
    {
        return new Vector(x*faktor,y*faktor);
    }
    public Vector plus(Vector vector)
    {
        return new Vector(x+vector.x,y+vector.y);
    }
    public Vector minus(Vector vector)
    {
        return new Vector(x-vector.x,y-vector.y);
    }
    public Vector geteilt(double teiler)
    {
        return new Vector(x/teiler,y/teiler);
    }


    @Override
    public boolean equals(Object o)
    {
        System.out.println("Compare"+toString()+" to "+o.toString());
        if(o instanceof Vector)
        {
            Vector v = (Vector) o;
            return v.x>=x-0.001&&v.x<=x+0.001&&v.y>=y-0.001&&v.y<=y+0.001;
        }
        else
        {
            return false;
        }
    }

    @Override
    public String toString()
    {
        return "Vector("+x+","+y+")";
    }
}