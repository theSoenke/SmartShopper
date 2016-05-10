package app.smartshopper_prototype.ShoppingLists.GroupList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import app.smartshopper_prototype.R;

/**
 * Created by marvin on 30.04.16.
 */
public class GroupExpListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<String>> _listDataChild;

    public GroupExpListAdapter(Context context, List<String> listDataHeader,
                               HashMap<String, List<String>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.group_row, null);
        }

        TextView childItem = (TextView) convertView.findViewById(R.id.rowChildTextView);
        childItem.setText(childText);
        return convertView;

    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    /**
     * Will be called when the indicator of the parent group item is clicked.
     *
     * @param isExpanded True when the group item is already extended, false if not.
     * @param position   The position of the group item in the view.
     */
    public void OnIndicatorClick(boolean isExpanded, int position) {
    }

    public void OnItemClick(String entry) {
    }

    @Override
    public View getGroupView(final int groupPosition, final boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.group_row, null);
        }

        createGroupItem(convertView, headerTitle);
        createIndicator(isExpanded, convertView, groupPosition);

        return convertView;
    }

    /**
     * Creates the group item (TextView) with the given header title.
     *
     * @param convertView The view this item is on.
     * @param headerTitle The title of the header.
     */
    private void createGroupItem(View convertView, final String headerTitle) {
        TextView groupItem = (TextView) convertView
                .findViewById(R.id.rowParentTextView);
        groupItem.setText(headerTitle);

        groupItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnItemClick(headerTitle);
            }
        });
    }

    /**
     * Creates the indicator for the group item and sets a click listener to the image of the indicator.
     *
     * @param isExpanded    The status of the group item if it's expanded (true) or not (false).
     * @param convertView   The view object this is in.
     * @param groupPosition The position of the group list item.
     */
    private void createIndicator(final boolean isExpanded, View convertView, final int groupPosition) {
        ImageView image = (ImageView) convertView.findViewById(R.id.rowGroupIndicator);
        int resource = isExpanded ? R.drawable.ic_arrow_up : R.drawable.ic_arrow_down;
        image.setImageResource(resource);
        image.setVisibility(convertView.VISIBLE);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnIndicatorClick(isExpanded, groupPosition);
            }
        });
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}

