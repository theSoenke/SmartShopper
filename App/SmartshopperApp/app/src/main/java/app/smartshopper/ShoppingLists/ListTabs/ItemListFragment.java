package app.smartshopper.ShoppingLists.ListTabs;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import app.smartshopper.Database.ItemEntry;
import app.smartshopper.Database.ItemEntryDataSource;
import app.smartshopper.Database.MySQLiteHelper;
import app.smartshopper.Database.Product;
import app.smartshopper.Database.ProductDataSource;
import app.smartshopper.Database.ShoppingList;
import app.smartshopper.Database.ShoppingListDataSource;
import app.smartshopper.R;
import app.smartshopper.ShoppingLists.AbstractDetailedListActivity;
import app.smartshopper.ShoppingLists.SingleList.SingleListFragment;

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


    ArrayAdapter<String> _listAdapter;
    long _shoppingList;
    ItemEntryDataSource _itemSource;
    ProductDataSource _productSource;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, group, false);

        ListView list = (ListView) view.findViewById(R.id.itemlist_list);

        String listName = "";
        if (group != null) {
            listName = group.getTag().toString();
        }

        // Create ArrayAdapter using an empty list
        _listAdapter = new ArrayAdapter<String>(getContext(), R.layout.simple_row, new ArrayList<String>());

        // get all lists with this name
        ShoppingListDataSource shoppingListSource = new ShoppingListDataSource(getContext());
        List<ShoppingList> listOfEntries = shoppingListSource.getEntry(MySQLiteHelper.SHOPPINGLIST_COLUMN_NAME + "='" + listName + "'");

        if (listOfEntries.size() > 0) {
            if (listOfEntries.size() > 1) {
                Toast.makeText(getContext(), "There's more than one list with the name " + listName + "! Taking the first occurrence.", Toast.LENGTH_SHORT).show();
            }
            _shoppingList = listOfEntries.get(0).getId();

            _itemSource = new ItemEntryDataSource(getContext());
            List<ItemEntry> items = _itemSource.getEntry(MySQLiteHelper.ITEMENTRY_LIST_ID + "=" + _shoppingList);

            _productSource = new ProductDataSource(getContext());


            for (ItemEntry item : items) {
                List<Product> product = _productSource.getEntry(MySQLiteHelper.PRODUCT_COLUMN_ID + "=" + item.getProductID());

                if (product.size() > 0) {
                    String entryString = product.get(0).getEntryName();
                    if (item.getAmount() > 1) {
                        entryString += " (" + item.getAmount() + "x)";
                    }
                    _listAdapter.add(entryString);
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
            list.setAdapter(_listAdapter);

            // to get notified about clicks on items
            list.setOnItemClickListener(this);
        } else {
            Toast.makeText(getContext(), "There's no list calles '" + list + "'!", Toast.LENGTH_SHORT).show();
        }
        FloatingActionButton addItem = (FloatingActionButton) view.findViewById(R.id.fabAddItem);
        addItem.setOnClickListener(new View.OnClickListener() {
            public void onClick(View vw) {
                openAddItemDialog();
            }
        });

        return view;
    }

    private boolean addItemtoList(String item) {
        List<Product> productList =  _productSource.getEntry(MySQLiteHelper.PRODUCT_COLUMN_NAME + " = " + item);
        if (productList.isEmpty()) {
           return false;
        } else {
            Product prod = _productSource.getEntry(MySQLiteHelper.PRODUCT_COLUMN_NAME + " = " + item).get(0);
            String entryString = prod.getEntryName();
            _itemSource.add(prod.getId(), _shoppingList, 1);
            _listAdapter.add(entryString);
            return true;
        }
    }


    private void openAddItemDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_add_item);
        dialog.setTitle("Add an item to your list");
        final EditText itemName = (EditText) dialog.findViewById(R.id.dialog_txtItemName);
        Button btadd = (Button) dialog.findViewById(R.id.dialog_btAddItem);
        btadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addItemtoList("'" + itemName.getText().toString() + "'")) {
                    Toast.makeText(getContext(), "item added", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    Toast.makeText(getContext(), "could not find your item", Toast.LENGTH_SHORT).show();
                }
                ;
            }
        });
        Button btabort = (Button) dialog.findViewById(R.id.btAbortAddItem);
        btabort.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
