package app.smartshopper.Database.Entries;

/**
 * Created by hauke on 10.05.16.
 */
public class MarketEntry extends DatabaseEntry {

    private String _marketID;
    private String _productID;
    private float _price;
    private int _posx;
    private int _posy;

    public MarketEntry(){
        _marketID = "";
        _productID = "";
    }

    public MarketEntry(String marketID, String productID, float price, int posx, int posy){
        _marketID = marketID;
        _productID = productID;
        _price = price;
        _posx = posx;
        _posy = posy;
    }

    public String getProductID() {
        return _productID;
    }

    public void setProductID(String _productID) {
        this._productID = _productID;
    }

    public String getMarketID() {
        return _marketID;
    }

    public void setMarketID(String _marketID) {
        this._marketID = _marketID;
    }

    public float getPrice() {
        return _price;
    }

    public void setPrice(float _price) {
        this._price = _price;
    }

    public int getPosX() {
        return _posx;
    }

    public void setPosX(int _posx) {
        this._posx = _posx;
    }

    public int getPosY() {
        return _posy;
    }

    public void setPosY(int _posy) {
        this._posy = _posy;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Object && obj != null) {
            MarketEntry e = (MarketEntry) obj;
            return e.getProductID().equals(getProductID()) &&
                    e.getMarketID().equals(getMarketID());
        }
        return false;
    }

    public SyncableLocation getLocation() {
        return new SyncableLocation(getPosX(), getPosY());
    }
}
