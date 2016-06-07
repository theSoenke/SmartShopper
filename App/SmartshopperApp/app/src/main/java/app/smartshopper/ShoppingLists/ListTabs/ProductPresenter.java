package app.smartshopper.ShoppingLists.ListTabs;

/**
 * Created by Rasmus on 02.06.16.
 *
 * A class that implements the ProductPresenter (usually a fragment or activity) is able to react
 * on changed products which enables the use of the observer pattern.
 */
public interface ProductPresenter
{
    void productsChanged();
}
