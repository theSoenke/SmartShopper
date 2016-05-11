package app.smartshopper_prototype.Database;

import android.content.Context;

import java.util.List;

/**
 * Created by hauke on 11.05.16.
 */
public class Synchronizer {

    public void sync(Context context) {
        // TODO connect to remote database and sync local database

        MySQLiteHelper helper = new MySQLiteHelper(context, MySQLiteHelper.DATABASE_NAME, MySQLiteHelper.DATABASE_VERSION);
        helper.onCreate(helper.getWritableDatabase());

        ProductDataSource p = syncProducts(context);
        ShoppingListDataSource s = syncShoppingLists(context);
        syncItemEntries(context, p, s);
    }

    private ProductDataSource syncProducts(Context context) {
        ProductDataSource s = new ProductDataSource(context);
        s.add("Hammer", 0, 0);
        s.add("Bohrmaschine", 0, 0);
        s.add("Farbe", 0, 0);

        s.add("Wurst", 0, 0);
        s.add("Käse", 0, 0);
        s.add("Tiefkühlpizza", 0, 0);
        s.add("Toast", 0, 0);
        s.add("Bratwurst", 0, 0);
        s.add("Curry-Ketchup", 0, 0);
        s.add("Tomate", 0, 0);
        s.add("Zwiebeln", 0, 0);

        s.add("Bier", 0, 0);

        s.add("Geschenke", 0, 0);

        s.add("Kööm", 0, 0);
        s.add("Klootkugel", 0, 0);
        s.add("Notizblock", 0, 0);

        s.add("Bier", 0, 0);
        s.add("Mate", 0, 0);

        return s;
    }

    private ShoppingListDataSource syncShoppingLists(Context context) {
        ShoppingListDataSource s = new ShoppingListDataSource(context);

        // single lists
        s.add("Baumarkt", true);
        s.add("Wocheneinkauf", true);
        s.add("Getränkemarkt", true);

        // group lists
        s.add("Geburtstag von Max Mustermann", false);
        s.add("Vereinstreffen", false);
        s.add("OE-Liste", false);

        return s;
    }

    private void syncItemEntries(Context context, ProductDataSource p, ShoppingListDataSource s) {
        ItemEntryDataSource i = new ItemEntryDataSource(context);

        long Baumarkt = s.getEntry(MySQLiteHelper.SHOPPINGLIST_COLUMN_NAME + " = 'Baumarkt'" +
                " AND " + MySQLiteHelper.SHOPPINGLIST_COLUMN_SINGLE + " = " + 1).get(0).getId();

        long Wocheneinkauf = s.getEntry(MySQLiteHelper.SHOPPINGLIST_COLUMN_NAME + " = 'Wocheneinkauf'" +
                " AND " + MySQLiteHelper.SHOPPINGLIST_COLUMN_SINGLE + " = " + 1).get(0).getId();

        long Greänkemarkt = s.getEntry(MySQLiteHelper.SHOPPINGLIST_COLUMN_NAME + " = 'Getränkemarkt'" +
                " AND " + MySQLiteHelper.SHOPPINGLIST_COLUMN_SINGLE + " = " + 1).get(0).getId();

        long Geburtstag = s.getEntry(MySQLiteHelper.SHOPPINGLIST_COLUMN_NAME + " = 'Geburtstag von Max Mustermann'" +
                " AND " + MySQLiteHelper.SHOPPINGLIST_COLUMN_SINGLE + " = " + 0).get(0).getId();

        long Vereinstreffen = s.getEntry(MySQLiteHelper.SHOPPINGLIST_COLUMN_NAME + " = 'Vereinstreffen'" +
                " AND " + MySQLiteHelper.SHOPPINGLIST_COLUMN_SINGLE + " = " + 0).get(0).getId();

        long OE = s.getEntry(MySQLiteHelper.SHOPPINGLIST_COLUMN_NAME + " = 'OE-Liste'" +
                " AND " + MySQLiteHelper.SHOPPINGLIST_COLUMN_SINGLE + " = " + 0).get(0).getId();

        i.add(p.getEntry(MySQLiteHelper.PRODUCT_COLUMN_NAME + " = 'Hammer'").get(0).getId(), Baumarkt, 1);
        i.add(p.getEntry(MySQLiteHelper.PRODUCT_COLUMN_NAME + " = 'Bohrmaschine'").get(0).getId(), Baumarkt, 1);
        i.add(p.getEntry(MySQLiteHelper.PRODUCT_COLUMN_NAME + " = 'Farbe'").get(0).getId(), Baumarkt, 1);

        i.add(p.getEntry(MySQLiteHelper.PRODUCT_COLUMN_NAME + " = 'Wurst'").get(0).getId(), Wocheneinkauf, 1);
        i.add(p.getEntry(MySQLiteHelper.PRODUCT_COLUMN_NAME + " = 'Käse'").get(0).getId(), Wocheneinkauf, 1);
        i.add(p.getEntry(MySQLiteHelper.PRODUCT_COLUMN_NAME + " = 'Tiefkühlpizza'").get(0).getId(), Wocheneinkauf, 1);
        i.add(p.getEntry(MySQLiteHelper.PRODUCT_COLUMN_NAME + " = 'Toast'").get(0).getId(), Wocheneinkauf, 1);
        i.add(p.getEntry(MySQLiteHelper.PRODUCT_COLUMN_NAME + " = 'Bratwurst'").get(0).getId(), Wocheneinkauf, 1);
        i.add(p.getEntry(MySQLiteHelper.PRODUCT_COLUMN_NAME + " = 'Curry-Ketchup'").get(0).getId(), Wocheneinkauf, 1);
        i.add(p.getEntry(MySQLiteHelper.PRODUCT_COLUMN_NAME + " = 'Tomate'").get(0).getId(), Wocheneinkauf, 1);
        i.add(p.getEntry(MySQLiteHelper.PRODUCT_COLUMN_NAME + " = 'Zwiebeln'").get(0).getId(), Wocheneinkauf, 1);

        i.add(p.getEntry(MySQLiteHelper.PRODUCT_COLUMN_NAME + " = 'Bier'").get(0).getId(), Greänkemarkt, 1);

        i.add(p.getEntry(MySQLiteHelper.PRODUCT_COLUMN_NAME + " = 'Geschenke'").get(0).getId(), Geburtstag, 1);

        i.add(p.getEntry(MySQLiteHelper.PRODUCT_COLUMN_NAME + " = 'Kööm'").get(0).getId(), Vereinstreffen, 1);
        i.add(p.getEntry(MySQLiteHelper.PRODUCT_COLUMN_NAME + " = 'Klootkugel'").get(0).getId(), Vereinstreffen, 1);
        i.add(p.getEntry(MySQLiteHelper.PRODUCT_COLUMN_NAME + " = 'Notizblock'").get(0).getId(), Vereinstreffen, 1);

        i.add(p.getEntry(MySQLiteHelper.PRODUCT_COLUMN_NAME + " = 'Bier'").get(0).getId(), OE, 1);
        i.add(p.getEntry(MySQLiteHelper.PRODUCT_COLUMN_NAME + " = 'Mate'").get(0).getId(), OE, 1);
    }
}
