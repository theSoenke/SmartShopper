package app.smartshopper.ShoppingLists.ListTabs;

import android.app.Dialog;
import android.content.Context;
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
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import app.smartshopper.Database.Entries.ItemEntry;
import app.smartshopper.Database.Entries.Market;
import app.smartshopper.Database.Entries.Product;
import app.smartshopper.Database.Entries.User;
import app.smartshopper.Database.Tables.ItemEntryDataSource;
import app.smartshopper.R;
import app.smartshopper.ShoppingLists.GroupList.GroupExpListAdapter;
import app.smartshopper.ShoppingLists.GroupListMaker;

/**
 * Created by Marvin on 11.07.2016.
 */
public class GroupItemListFragment extends Fragment implements ProductPresenter {

    int _expandedParent = -1;
    ProductHolder _productHolder;
    ExpandableListAdapter _adapter;
    ExpandableListView _list;
    GroupListMaker _maker;
    View view;
    List<List<ItemEntry>> _source;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_group_list_item_list, group, false);

        _maker = new GroupListMaker(getContext());
        _list = (ExpandableListView) view.findViewById(R.id.grouplist_item_list);
        configAdapter();

        FloatingActionButton addItem = (FloatingActionButton) view.findViewById(R.id.fabAddGroupListItem);
        addItem.setOnClickListener(new View.OnClickListener() {
            public void onClick(View vw)
            {
                openAddGroupItemDialog();
            }
        });
        return view;
    }

    private void configAdapter() {
        List<User> userList = _maker.getUserList(_productHolder.getList());
        List<String> formattedUserList = new ArrayList<>();
        for(int i = 0;i< userList.size();i++){
            formattedUserList.add(userList.get(i).getEntryName());
        }
        _source = _maker.groupListSetup(_productHolder.getList());
        _adapter = new GroupExpListAdapter(getContext(),formattedUserList,_maker.formatGroupEntries(_source,_productHolder.getList())){
            @Override
            public void OnIndicatorClick(boolean isExpanded, int groupPosition) {
                if (isExpanded) {
                    _list.collapseGroup(groupPosition);
                } else {
                    _list.collapseGroup(_expandedParent);
                    _list.expandGroup(groupPosition);
                    _expandedParent = groupPosition;
                }
            }

            @Override
            public void OnItemClick(String entry) {
            }
        };
        _list.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                ItemEntryDataSource i = new ItemEntryDataSource(getContext());
                _productHolder.openConfigureItemDialog(_source.get(groupPosition).get(childPosition));
                return true;
            }
        });
        _list.setAdapter(_adapter);
        //listEmpty();
    }

    private void openAddGroupItemDialog()
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
        configAdapter();
    }

    /**
     * Shows the floating action button right in the middle of the screen when no list exists.
     * If there's a list (or more then one), the button is in the lower right corner.
     */
    private void listEmpty()
    {
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fabAddGroupListItem);
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
