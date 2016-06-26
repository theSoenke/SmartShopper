package app.smartshopper.Database.Sync;

import android.content.Context;
import android.util.Log;

import app.smartshopper.Database.Entries.ItemEntry;
import app.smartshopper.Database.MySQLiteHelper;
import app.smartshopper.Database.Tables.ItemEntryDataSource;
import app.smartshopper.Database.Tables.ParticipantDataSource;
import app.smartshopper.Database.Tables.ProductDataSource;
import app.smartshopper.Database.Tables.ShoppingListDataSource;
import app.smartshopper.Database.Tables.UserDataSource;

/**
 * Created by hauke on 11.05.16.
 */
public class Synchronizer {

    public void sync(Context context) {
        Log.i("SYNCHRONIZER", "Start synchronizing local database ...");
        // TODO connect to remote database and sync local database

        MySQLiteHelper helper = new MySQLiteHelper(context, MySQLiteHelper.DATABASE_NAME, MySQLiteHelper.DATABASE_VERSION);
        helper.onCreate(helper.getWritableDatabase());

        Log.i("SYNCHRONIZER", "Sync products ...");
        ProductDataSource p = syncProducts(context);
        Log.i("SYNCHRONIZER", "Sync shopping lists ...");
        ShoppingListDataSource s = syncShoppingLists(context);
        Log.i("SYNCHRONIZER", "Sync item entries ...");
        syncItemEntries(context, p, s);

        Log.i("SYNCHRONIZER", "Sync user data ...");
        UserDataSource u = syncUsers(context);
        syncParticipants(context, s, u);
        Log.i("SYNCHRONIZER", "Finished synchronizing");
    }

    private ProductDataSource syncProducts(Context context) {
        ProductDataSource s = new ProductDataSource(context);
        s.add("Hammer", 200, 100);
        s.add("Bohrmaschine", 100, 100);
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
        s.add("Baumarkt");
        s.add("Wocheneinkauf");
        s.add("Getränkemarkt");

        // group lists
        s.add("Geburtstag von Max Mustermann");
        s.add("Vereinstreffen");
        s.add("OE-Liste");

        return s;
    }

    private void syncItemEntries(Context context, ProductDataSource p, ShoppingListDataSource s) {
        ItemEntryDataSource i = new ItemEntryDataSource(context);

        String Baumarkt = s.getEntry(MySQLiteHelper.SHOPPINGLIST_COLUMN_NAME + " = 'Baumarkt'").get(0).getId();

        String Wocheneinkauf = s.getEntry(MySQLiteHelper.SHOPPINGLIST_COLUMN_NAME + " = 'Wocheneinkauf'").get(0).getId();

        String Greänkemarkt = s.getEntry(MySQLiteHelper.SHOPPINGLIST_COLUMN_NAME + " = 'Getränkemarkt'").get(0).getId();

        String Geburtstag = s.getEntry(MySQLiteHelper.SHOPPINGLIST_COLUMN_NAME + " = 'Geburtstag von Max Mustermann'").get(0).getId();

        String Vereinstreffen = s.getEntry(MySQLiteHelper.SHOPPINGLIST_COLUMN_NAME + " = 'Vereinstreffen'").get(0).getId();

        String OE = s.getEntry(MySQLiteHelper.SHOPPINGLIST_COLUMN_NAME + " = 'OE-Liste'").get(0).getId();

        i.add(p.getEntry(MySQLiteHelper.PRODUCT_COLUMN_NAME + " = 'Hammer'").get(0).getId(), Baumarkt, 2);
        i.add(p.getEntry(MySQLiteHelper.PRODUCT_COLUMN_NAME + " = 'Bohrmaschine'").get(0).getId(), Baumarkt, 4);
        i.add(p.getEntry(MySQLiteHelper.PRODUCT_COLUMN_NAME + " = 'Farbe'").get(0).getId(), Baumarkt, 1);

        i.add(p.getEntry(MySQLiteHelper.PRODUCT_COLUMN_NAME + " = 'Wurst'").get(0).getId(), Wocheneinkauf, 1);
        i.add(p.getEntry(MySQLiteHelper.PRODUCT_COLUMN_NAME + " = 'Käse'").get(0).getId(), Wocheneinkauf, 5);

        // just to have a already bought item that's in the middle of the list
        ItemEntry entry = new ItemEntry();
        entry.setEntryName("Tiefkühlpizza");
        entry.setAmount(1);
        entry.setBought(1);
        entry.setListID(Wocheneinkauf);
        entry.setProductID(p.getEntry(MySQLiteHelper.PRODUCT_COLUMN_NAME + " = 'Tiefkühlpizza'").get(0).getId());
        i.add(entry);

        i.add(p.getEntry(MySQLiteHelper.PRODUCT_COLUMN_NAME + " = 'Toast'").get(0).getId(), Wocheneinkauf, 1);
        i.add(p.getEntry(MySQLiteHelper.PRODUCT_COLUMN_NAME + " = 'Bratwurst'").get(0).getId(), Wocheneinkauf, 7);
        i.add(p.getEntry(MySQLiteHelper.PRODUCT_COLUMN_NAME + " = 'Curry-Ketchup'").get(0).getId(), Wocheneinkauf, 1);
        i.add(p.getEntry(MySQLiteHelper.PRODUCT_COLUMN_NAME + " = 'Tomate'").get(0).getId(), Wocheneinkauf, 1);
        i.add(p.getEntry(MySQLiteHelper.PRODUCT_COLUMN_NAME + " = 'Zwiebeln'").get(0).getId(), Wocheneinkauf, 3);

        i.add(p.getEntry(MySQLiteHelper.PRODUCT_COLUMN_NAME + " = 'Bier'").get(0).getId(), Greänkemarkt, 1);

        i.add(p.getEntry(MySQLiteHelper.PRODUCT_COLUMN_NAME + " = 'Geschenke'").get(0).getId(), Geburtstag, 1);

        i.add(p.getEntry(MySQLiteHelper.PRODUCT_COLUMN_NAME + " = 'Kööm'").get(0).getId(), Vereinstreffen, 1);
        i.add(p.getEntry(MySQLiteHelper.PRODUCT_COLUMN_NAME + " = 'Klootkugel'").get(0).getId(), Vereinstreffen, 1);
        i.add(p.getEntry(MySQLiteHelper.PRODUCT_COLUMN_NAME + " = 'Notizblock'").get(0).getId(), Vereinstreffen, 1);

        i.add(p.getEntry(MySQLiteHelper.PRODUCT_COLUMN_NAME + " = 'Bier'").get(0).getId(), OE, 1);
        i.add(p.getEntry(MySQLiteHelper.PRODUCT_COLUMN_NAME + " = 'Mate'").get(0).getId(), OE, 1);
    }

    private UserDataSource syncUsers(Context context) {
        UserDataSource u = new UserDataSource(context);

        u.add("Dieter");
        u.add("Batman");
        u.add("SpiderMan");
        u.add("Ronny Schäfer");
        u.add("Ash Ketchup");
        u.add("Professor Eich");
        u.add("Rocko");
        u.add("Misty");

        return u;
    }

    private void syncParticipants(Context context, ShoppingListDataSource s, UserDataSource u) {
        ParticipantDataSource p = new ParticipantDataSource(context);

        String Geburtstag = s.getEntry(MySQLiteHelper.SHOPPINGLIST_COLUMN_NAME + " = 'Geburtstag von Max Mustermann'").get(0).getId();
        String Vereinstreffen = s.getEntry(MySQLiteHelper.SHOPPINGLIST_COLUMN_NAME + " = 'Vereinstreffen'").get(0).getId();
        String OE = s.getEntry(MySQLiteHelper.SHOPPINGLIST_COLUMN_NAME + " = 'OE-Liste'").get(0).getId();

        String Dieter = u.getEntry(MySQLiteHelper.USER_COLUMN_NAME + " = 'Dieter'").get(0).getId();
        String Batman = u.getEntry(MySQLiteHelper.USER_COLUMN_NAME + " = 'Batman'").get(0).getId();
        String SpiderMan = u.getEntry(MySQLiteHelper.USER_COLUMN_NAME + " = 'SpiderMan'").get(0).getId();
        String Ronny = u.getEntry(MySQLiteHelper.USER_COLUMN_NAME + " = 'Ronny Schäfer'").get(0).getId();
        String AshKetchup = u.getEntry(MySQLiteHelper.USER_COLUMN_NAME + " = 'Ash Ketchup'").get(0).getId();
        String ProfEich = u.getEntry(MySQLiteHelper.USER_COLUMN_NAME + " = 'Professor Eich'").get(0).getId();
        String Rocko = u.getEntry(MySQLiteHelper.USER_COLUMN_NAME + " = 'Rocko'").get(0).getId();
        String Misty = u.getEntry(MySQLiteHelper.USER_COLUMN_NAME + " = 'Misty'").get(0).getId();

        p.add(Geburtstag, Dieter);
        p.add(Geburtstag, Batman);

        p.add(Vereinstreffen, SpiderMan);
        p.add(Vereinstreffen, Ronny);

        p.add(OE, AshKetchup);
        p.add(OE, ProfEich);
        p.add(OE, Rocko);
        p.add(OE, Misty);
    }
}
