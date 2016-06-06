package app.smartshopper.ShoppingLists.ListTabs;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import app.smartshopper.Database.ItemEntry;
import app.smartshopper.Database.ItemEntryDataSource;
import app.smartshopper.Database.MySQLiteHelper;
import app.smartshopper.Database.Product;
import app.smartshopper.Database.ProductDataSource;
import app.smartshopper.Database.ShoppingList;
import app.smartshopper.Database.ShoppingListDataSource;
import app.smartshopper.R;

/**
 * Created by hauke on 28.04.16.
 */
public class ItemListFragment extends Fragment implements AdapterView.OnItemClickListener, ProductPresenter {

    ArrayAdapter<String> _listAdapter;
    ProductHolder _productHolder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, group, false);

        ListView list = (ListView) view.findViewById(R.id.itemlist_list);

        // Create ArrayAdapter using an empty list
        _listAdapter = new ArrayAdapter<String>(getContext(), R.layout.simple_row, new ArrayList<String>());

        // get all lists with this name
        ShoppingListDataSource shoppingListSource = new ShoppingListDataSource(getContext());

        productsChanged();

        // add adapter with items to list (necessary to display items)
        list.setAdapter(_listAdapter);

        // to get notified about clicks on items
        list.setOnItemClickListener(this);

        FloatingActionButton addItem = (FloatingActionButton) view.findViewById(R.id.fabAddItem);
        addItem.setOnClickListener(new View.OnClickListener() {
            public void onClick(View vw) {
                openAddItemDialog();
            }
        });

        return view;
    }

    private void openAddItemDialog() {
        //TODO show all items in the list "dialog_AddItemListView"
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_add_item);
        dialog.setTitle("Add an item to your list");
        final EditText itemNameExitField = (EditText) dialog.findViewById(R.id.dialog_txtItemName);
        final ListView productList = (ListView) dialog.findViewById(R.id.dialog_AddItemListView);
        final ArrayAdapter<Product> productListAdapter = new ArrayAdapter<Product>(getContext(), R.layout.simple_row, new ArrayList<Product>());

        // get all products and add them to the list
        //FIXME get all products an not only the ones that already exist in the list
        final List<Product> listOfProducts = _productHolder.getAllAvailableProducts();
        productListAdapter.addAll(listOfProducts);

        productList.setAdapter(productListAdapter);

        productList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (_productHolder.addEntry(productListAdapter.getItem(position).toString(), 1)) {
                    Toast.makeText(getContext(), "item added", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    Toast.makeText(getContext(), "could not find your item", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //TODO add listener to the input field to filter the list of items
        itemNameExitField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String prefix = s.toString();
                List<Product> newListOfProducts = (List<Product>) ((ArrayList<Product>) listOfProducts).clone();
                int lengthOfList = newListOfProducts.size();

                for (int i = 0; i < lengthOfList; i++) {
                    String entry = newListOfProducts.get(i).toString();
                    if (!entry.startsWith(prefix)) {
                        newListOfProducts.remove(i);
                        lengthOfList--;
                        i--;
                    }
                }

                productListAdapter.clear();
                productListAdapter.addAll(newListOfProducts);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        dialog.show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final Dialog dialog = new Dialog(getContext());
        final String itemName = _listAdapter.getItem(position);
        String[] split = itemName.split("\\s+");
        dialog.setContentView(R.layout.dialog_configure_item);
        dialog.setTitle("Configure " + "'" + split[1] + "'");
        TextView tw = (TextView) dialog.findViewById(R.id.dialog_ConfigItemTextView);
        Button btAbort = (Button) dialog.findViewById(R.id.dialog_btAbortConfigItem);
        Button btDelete = (Button) dialog.findViewById(R.id.dialog_btDeleteItem);
        Button btBought = (Button) dialog.findViewById(R.id.dialog_btMarkItem);
        Button btAmount = (Button) dialog.findViewById(R.id.dialog_btChangeItemAmount);
        btAbort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _productHolder.removeEntry(itemName);
                productsChanged();
                dialog.dismiss();
            }
        });
        btBought.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _productHolder.markItemAsBought(itemName);
                productsChanged();
                dialog.dismiss();
            }
        });
        btAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openChangeAmountDialog(itemName);
                dialog.dismiss();
            }
        });


        dialog.show();
    }

    private void openChangeAmountDialog(String itemname) {

        final String iname = itemname;
        String[] split = iname.split("\\s+");
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_enter_item_amount);
        dialog.setTitle(split[1]);
        final EditText AmountEditText = (EditText) dialog.findViewById(R.id.dialog_txtNewItemAmount);
         AmountEditText.setText(split[0]);
        Button btAbort = (Button) dialog.findViewById(R.id.dialog_btAbortItemAmountChange);
        Button btConfirm = (Button) dialog.findViewById(R.id.dialog_btConfirmItemAmountChange);
        btAbort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _productHolder.changeItemAmount(iname, Integer.parseInt(AmountEditText.getText().toString()));
                productsChanged();
                dialog.dismiss();
            }
        });
        dialog.show();

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ProductHolder) {
            _productHolder = (ProductHolder) context;
        } else {
            throw new ClassCastException(context.toString() + " has to implement ProductHolder!");
        }
    }

    @Override
    public void productsChanged() {
        _listAdapter.clear();
        List<ItemEntry> entryList = _productHolder.getItemEntries();
        if (entryList != null) {
            for (ItemEntry item : entryList) {
                String entryString = item.getAmount() + " " + _productHolder.getProductFromID(item.getProductID()).
                        getEntryName();
                if(item.isBought() != 0){
                    entryString += " (gekauft)";
                }
                _listAdapter.add(entryString);
            }
        }
    }

}
