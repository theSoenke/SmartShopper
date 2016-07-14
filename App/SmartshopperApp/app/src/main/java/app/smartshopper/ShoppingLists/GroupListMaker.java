package app.smartshopper.ShoppingLists;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import app.smartshopper.Database.Entries.ItemEntry;
import app.smartshopper.Database.Entries.MarketEntry;
import app.smartshopper.Database.Entries.ShoppingList;
import app.smartshopper.Database.Entries.User;
import app.smartshopper.Database.Tables.ItemEntryDataSource;
import app.smartshopper.Database.Tables.MarketEntryDataSource;
import app.smartshopper.Database.Tables.ParticipantDataSource;
import app.smartshopper.Database.Tables.ProductDataSource;
import app.smartshopper.Database.Tables.UserDataSource;

/**
 * Created by Marvin on 14.07.2016.
 */
public class GroupListMaker {

    private ItemEntryDataSource _itemSource;
    private MarketEntryDataSource _marketEntries;
    private ProductDataSource _productSource;
    private ParticipantDataSource _participantDataSource;
    private UserDataSource _userDataSource;
    private User _localowner;

    public GroupListMaker(Context context){

        _itemSource = new ItemEntryDataSource(context);
        _marketEntries = new MarketEntryDataSource(context);
        _productSource = new ProductDataSource(context);
        _participantDataSource = new ParticipantDataSource(context);
        _userDataSource = new UserDataSource(context);
        if(_userDataSource.getUserByName("me") == null){
            _localowner = new User();
            _localowner.setEntryName("me");
            _localowner.setId(_userDataSource.generateUniqueID());
            _userDataSource.addLocally(_localowner);
        }else{
            _localowner = _userDataSource.getUserByName("me");
        }

    }


    public List<ItemEntry> getListForOwner(ShoppingList shoppingList){
        List<List<ItemEntry>> start = groupListSetup(shoppingList);
        if(shoppingList.getOwner() == null){
            return start.get(getPositionInList(_localowner,shoppingList));
        }else{
            return start.get(getPositionInList(shoppingList.getOwner(),shoppingList));
        }
    }

    public List<List<ItemEntry>> groupListSetup(ShoppingList shoppingList){
        List<List<ItemEntry>> out = new ArrayList<>();
        List<MarketEntry> interm = new ArrayList<>();
        List<ItemEntry> in = _itemSource.getEntriesForList(shoppingList.getId());
        for(int i = 0;i<in.size();i++){
            interm.add(_marketEntries.getCheapestMarketEntryForProduct(in.get(i).getProduct().getId()));
            Log.i("ID TO TEST", "x " +  in.get(i).getProduct().getId());
        }
        List<List<MarketEntry>> list = splitGroupList(interm, shoppingList);
        for(int j = 0;j<list.size();j++){

            List<ItemEntry> midout= new ArrayList<>();
            for(int k = 0;k<list.get(j).size();k++){
                if(list.get(j).get(k) == null){
                    Log.i("Market after cast ", "null");
                }else{
                    Log.i("Market after cast", "not null");
                    Log.i("Market after cast", "x " + list.get(j).get(k).getProductID());
                }
                midout.add(_itemSource.getItemEntry(shoppingList,_productSource.get(list.get(j).get(k).getProductID())));


            }
            out.add(midout);
        }
        return out;
    }

    public List<List<MarketEntry>> splitGroupList(List<MarketEntry> input, ShoppingList shoppingList){
        List<List<MarketEntry>> output = new ArrayList<>();
        Collections.sort(input);
        Collections.reverse(input);
        for(int i=0;i<getUserList(shoppingList).size();i++){
            List<MarketEntry> buf = new ArrayList<>();
            output.add(buf);
        }
        while (!input.isEmpty()){
            output.get(0).add(input.get(0));
            Collections.sort(output, new Comparator<List<MarketEntry>>() {
                @Override
                public int compare(List<MarketEntry> lhs, List<MarketEntry> rhs) {
                    int sum_lhs=0;
                    int sum_rhs=0;
                    for(int i = 0; i<lhs.size();i++){
                        sum_lhs += lhs.get(i).getPrice();
                    }
                    for(int j = 0; j<rhs.size();j++){
                        sum_rhs += rhs.get(j).getPrice();
                    }
                    if(sum_lhs > sum_rhs){
                        return 1;
                    }
                    if(sum_rhs > sum_lhs){
                        return -1;
                    }
                    return 0;
                }
            });
            input.remove(0);
        }
        return output;
    }


    public int getPositionInList(User user, ShoppingList shoppingList){
        List<User> userList = getUserList(shoppingList);
        for(int i = 0;i<userList.size();i++){
            if(userList.get(i).getId().equalsIgnoreCase(user.getId())){
                return i;
            }
        }
        return 0;
    }

    public HashMap<String,List<String>> formatGroupEntries(List<List<ItemEntry>> in, ShoppingList shoppingList){
        HashMap<String,List<String>> returnmap = new HashMap<>();
        List<User> getuser = getUserList(shoppingList);
        for(int i= 0;i<getuser.size();i++){
            List<ItemEntry> entryList = in.get(getPositionInList(getuser.get(i), shoppingList));
            Log.i("Position of user", getuser.get(i).getEntryName() + ": " + getPositionInList(getuser.get(i), shoppingList));
            Log.i("Size of List", "for user" + getuser.get(i).getEntryName() + " : " + entryList.size());
            List<String> formatList = new ArrayList<>();
            for(int j= 0; j < entryList.size(); j++){
                if(entryList.get(j)!= null){
                    formatList.add(entryList.get(j).toString());
                }else{
                    Log.i("EntryList", "ENTRY IS NULL WTF");
                }
            }
            returnmap.put(getuser.get(i).getEntryName(),formatList);
        }
        return returnmap;
    }

    public List<User> getUserList(ShoppingList shoppingList){
        Log.i("getUserList", shoppingList.getId());
        List<User> returnl = _participantDataSource.getUserOfList(shoppingList.getId());
        //returnl.add(_shoppingList.getOwner());
        if(shoppingList.getOwner() == null){
            Log.i("owner is null", " we are doomed");

            returnl.add(_localowner);
        }else {
            returnl.add(shoppingList.getOwner());
        }
        Log.i("getUserList size", "" + returnl.size());
        return returnl;
    }
}
