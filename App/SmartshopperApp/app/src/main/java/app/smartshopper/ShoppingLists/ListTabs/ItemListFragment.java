package app.smartshopper.ShoppingLists.ListTabs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;

import app.smartshopper.Database.Entries.ItemEntry;
import app.smartshopper.Database.Entries.Product;
import app.smartshopper.R;

/**
 * Created by hauke on 28.04.16.
 * <p/>
 * An ItemListFragment contains a list of items that belong to a certain list (single or group list).
 * When started the fragment reads all items from the database and displays them.
 * The "add"- and "change amount"-dialog is also located here.
 */
//TODO Move dialogs to extra class(es)
public class ItemListFragment extends Fragment implements AdapterView.OnItemClickListener, ProductPresenter {

    ArrayAdapter<ItemListEntry> _listAdapter;
    ProductHolder _productHolder;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_item_list, group, false);

        ListView list = (ListView) view.findViewById(R.id.itemlist_list);

        // Create ArrayAdapter using an empty list
        _listAdapter = new ArrayAdapter<ItemListEntry>(getContext(), R.layout.simple_row, new ArrayList<ItemListEntry>());

        productsChanged();

        // add adapter with items to list (necessary to display items)
        list.setAdapter(_listAdapter);

        // to get notified about clicks on items
        list.setOnItemClickListener(this);

        FloatingActionButton addItem = (FloatingActionButton) view.findViewById(R.id.fabAddItem);
        addItem.setOnClickListener(new View.OnClickListener() {
            public void onClick(View vw)
            {
                openAddItemDialog();
            }
        });

        return view;
    }

    private void openAddItemDialog()
    {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_add_item);
        dialog.setTitle("Add an item to your list");
        final EditText itemNameExitField = (EditText) dialog.findViewById(R.id.dialog_txtItemName);
        final EditText itemAmountEditField = (EditText) dialog.findViewById(R.id.dialog_AddItemAmountEditText);
        Button cancelButton = (Button) dialog.findViewById(R.id.dialog_AddItemCancelButton);
        final ListView productList = (ListView) dialog.findViewById(R.id.dialog_AddItemListView);
        final ArrayAdapter<Product> productListAdapter = new ArrayAdapter<Product>(getContext(), R.layout.simple_row, new ArrayList<Product>());

        // get all products and add them to the list
        final List<Product> listOfProducts = _productHolder.getAllAvailableProducts();
        productListAdapter.addAll(listOfProducts);

        productList.setAdapter(productListAdapter);

        productList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                int amountOfItems = 1;

                // when the text field is not empty
                if (!itemAmountEditField.getText().toString().isEmpty())
                {
                    amountOfItems = Integer.parseInt(itemAmountEditField.getText().toString());
                }

                if (amountOfItems != 0)
                {
                    if (_productHolder.addEntry(productListAdapter.getItem(position).toString(), amountOfItems))
                    {
                        Toast.makeText(getContext(), "item added", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else
                    {
                        Toast.makeText(getContext(), "could not find your item", Toast.LENGTH_SHORT).show();
                    }
                } else
                {
                    Toast.makeText(getContext(), "Please enter a number greater then 0!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
            }
        });

        itemNameExitField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                String prefix = s.toString().toLowerCase();
                List<Product> newListOfProducts = _productHolder.getAllAvailableProducts();
                int lengthOfList = newListOfProducts.size();

                for (int i = 0; i < lengthOfList; i++)
                {
                    String entry = newListOfProducts.get(i).toString().toLowerCase();
                    if (!entry.startsWith(prefix))
                    {
                        newListOfProducts.remove(i);
                        lengthOfList--;
                        i--;
                    }
                }

                productListAdapter.clear();
                productListAdapter.addAll(newListOfProducts);
            }

            @Override
            public void afterTextChanged(Editable s)
            {

            }
        });

        dialog.show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        final ItemListEntry itemEntry = _listAdapter.getItem(position);
        _productHolder.openConfigureItemDialog(itemEntry);
    }


    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof ProductHolder)
        {
            _productHolder = (ProductHolder) context;
        } else
        {
            throw new ClassCastException(context.toString() + " has to implement ProductHolder!");
        }
    }

    @Override
    public void productsChanged()
    {
        _listAdapter.clear();
        List<ItemEntry> rawItemList = _productHolder.getItemEntries();
        listEmpty();

        for (ItemEntry itemEntry : rawItemList)
        {
            ItemListEntry listEntry = new ItemListEntry(itemEntry);
            _listAdapter.add(listEntry);
        }
    }

    private void listEmpty()
    {
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fabAddItem);
        RelativeLayout.LayoutParams params;

        TextView tv = (TextView) view.findViewById(R.id.noItemsText);
        if (_productHolder.getItemEntries().isEmpty())
        {

            params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);

            tv.setVisibility(View.VISIBLE);

        } else
        {
            tv.setVisibility(View.GONE);

            params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            params.addRule(RelativeLayout.ALIGN_PARENT_END);
            params.setMarginEnd(15);
            params.bottomMargin = 15;
        }
        fab.setLayoutParams(params);
    }


}
