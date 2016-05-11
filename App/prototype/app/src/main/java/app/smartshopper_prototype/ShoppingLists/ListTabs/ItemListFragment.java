package app.smartshopper_prototype.ShoppingLists.ListTabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import app.smartshopper_prototype.Database.DatabaseEntry;
import app.smartshopper_prototype.Database.ItemEntry;
import app.smartshopper_prototype.Database.ItemEntryDataSource;
import app.smartshopper_prototype.Database.MySQLiteHelper;
import app.smartshopper_prototype.Database.Product;
import app.smartshopper_prototype.Database.ProductDataSource;
import app.smartshopper_prototype.Database.ShoppingList;
import app.smartshopper_prototype.Database.ShoppingListDataSource;
import app.smartshopper_prototype.R;

/**
 * Created by hauke on 28.04.16.
 */
/* TODO Change this class into ItemListTabFragment
Make this into a kind of general "ItemListTabFragment". This shows the list of item and the two tabs.

The group-list version (a class that inherits from the ItemListTabFragment) has some controls to add
items to the whole group (and an algorithm decides weather you or a participant has to buy the item).

The single-list version (also a class that inherits from the ItemListTabFragment) shows the current
kind of information.
*/
public class ItemListFragment extends Fragment implements AdapterView.OnItemClickListener {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sinlge_list, group, false);

        ListView list = (ListView) view.findViewById(R.id.singlelist_list);

        String listName = "";
        if (group != null) {
            listName = group.getTag().toString();
        }

        // Create ArrayAdapter using an empty list
        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(getContext(), R.layout.simple_row, new ArrayList<String>());

        // get all lists with this name
        ShoppingListDataSource shoppingListSource = new ShoppingListDataSource(getContext());
        List<ShoppingList> listOfEntries = shoppingListSource.getEntry(MySQLiteHelper.SHOPPINGLIST_COLUMN_NAME + "='" + listName+"'");

        if(listOfEntries.size() > 0) {
            if(listOfEntries.size() > 1){
                Toast.makeText(getContext(), "There's more than one list with the name "+listName+"! Taking the first occurrence.", Toast.LENGTH_SHORT).show();
            }
            long shoppingListID = listOfEntries.get(0).getId();

            ItemEntryDataSource itemSource = new ItemEntryDataSource(getContext());
            List<ItemEntry> items = itemSource.getEntry(MySQLiteHelper.ITEMENTRY_LIST_ID+"="+shoppingListID);

            ProductDataSource productSource = new ProductDataSource(getContext());

            for(ItemEntry item : items){
                List<Product> product = productSource.getEntry(MySQLiteHelper.PRODUCT_COLUMN_ID + "=" + item.getProductID());

                if(product.size() > 0){
                    String entryString = product.get(0).getEntryName();
                    if(item.getAmount() > 1){
                        entryString += " ("+item.getAmount()+"x)";
                    }
                    listAdapter.add(entryString);
                }
            }

            // connect to database to get list
//        if (liste.equalsIgnoreCase("Baumarkt")) {
//            listAdapter.add("Hammer");
//            listAdapter.add("Bohrmaschine");
//            listAdapter.add("Farbe");
//        } else if (liste.equalsIgnoreCase("Wocheneinkauf")) {
//            listAdapter.add("Wurst");
//            listAdapter.add("Käse");
//            listAdapter.add("Tiefkühlpizza");
//            listAdapter.add("Toast");
//            listAdapter.add("Bratwurst");
//            listAdapter.add("Curry-Ketchup");
//            listAdapter.add("Tomate");
//            listAdapter.add("Zwiebeln");
//        } else if (liste.equalsIgnoreCase("Getränkemarkt")) {
//            listAdapter.add("Bier");
//
//        }
//
//        if (liste.equalsIgnoreCase("Geburtstag von Max Mustermann")) {
//            listAdapter.add("Geschenke");
//        } else if (liste.equalsIgnoreCase("Vereinstreffen")) {
//            listAdapter.add("Kööm");
//            listAdapter.add("Neue Klootkugel");
//            listAdapter.add("Notizblock");
//        } else if (liste.equalsIgnoreCase("OE-Liste")) {
//            listAdapter.add("Bier");
//            listAdapter.add("Mate");
//        }

            // add adapter with items to list (necessary to display items)
            list.setAdapter(listAdapter);

            // to get notified about clicks on items
            list.setOnItemClickListener(this);
        }else{
            Toast.makeText(getContext(), "There's no list calles '"+list+"'!", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
