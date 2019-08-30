package com.erdemsiyam.memorizeyourwords.adapter;

import android.content.Intent;
import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;
import com.erdemsiyam.memorizeyourwords.activity.CategoryActivity;
import com.erdemsiyam.memorizeyourwords.R;
import com.erdemsiyam.memorizeyourwords.activity.WordActivity;
import com.erdemsiyam.memorizeyourwords.entity.Category;
import com.erdemsiyam.memorizeyourwords.service.CategoryService;
import com.erdemsiyam.memorizeyourwords.fragment.CategoryEditModalBottomSheetDialog;
import com.erdemsiyam.memorizeyourwords.listener.CategoryNotificationButtonOnClickListener;
import com.erdemsiyam.memorizeyourwords.listener.CategorySelectActionModeCallBack;
import com.google.android.material.chip.Chip;
import java.util.ArrayList;
import java.util.List;
import co.dift.ui.SwipeToAction;

public class CategoryRecyclerViewAdapter extends RecyclerView.Adapter<CategoryRecyclerViewAdapter.CategoryViewHolder> implements Filterable, SearchView.OnQueryTextListener,SwipeToAction.SwipeListener<Category> {

    /* Variables of Top. */
    private CategoryActivity    categoryActivity; // Instance of dependent Activity.
    private List<Category>      categories; // All categories.

    /* Constructor (gets instances, creating some objects). */
    public CategoryRecyclerViewAdapter(CategoryActivity categoryActivity, List<Category> categories) {
        this.categoryActivity = categoryActivity;
        this.categories = categories;
        filteredCategories = new ArrayList<>(categories); // All categories are copied for filtered display.
        selectedCategories = new SparseBooleanArray(); // This is for to see which categories are selected.
    }

    /* The "ViewHolder" inner class. */
    public class CategoryViewHolder extends SwipeToAction.ViewHolder<Category> {
        /* Holder UI components. */
        public TextView categoryName;
        public Chip wordCount;
        public ImageButton btnCategoryNotification;
        public ImageButton btnCategoryWordsNotification;
        public RelativeLayout background;

        /* Constructor. */
        public CategoryViewHolder(View layout) {
            super(layout);
            categoryName = layout.findViewById(R.id.categoryName);
            wordCount = layout.findViewById(R.id.categoryWordCount);
            btnCategoryNotification = layout.findViewById(R.id.btnCategoryNotification);
            btnCategoryWordsNotification = layout.findViewById(R.id.btnCategoryWordsNotification);
            background = layout.findViewById(R.id.elementCategoryContainer);
        }
    }

    /* Override methods of RecyclerView. */
    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_category, parent, false);
        return new CategoryViewHolder(layout);
    }
    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position) {
        /* UI datas loading. */
        Category category = filteredCategories.get(position); // Get the next category to UI data loading.
        holder.categoryName.setText(category.getName()); // Get name.
        holder.wordCount.setText(CategoryService.getCategoryWordCount(categoryActivity,category)+""); // Get the category's word count from DB.
        holder.wordCount.setClickable(false); // Not need.
        holder.btnCategoryNotification.setOnClickListener(new CategoryNotificationButtonOnClickListener(categoryActivity,this,category)); // Set notification task. // Will be done later.
        holder.data = category;

        holder.btnCategoryNotification.setClickable(false);// Soon.
        holder.btnCategoryWordsNotification.setClickable(false);// Soon.

        /* Notification and Alarm will be done later. */
        /*
        if(category.getAlarm() > 0)
            holder.btnCategoryNotification.setColorFilter(Color.argb(255, 0, 255, 0));
        else
            holder.btnCategoryNotification.setColorFilter(Color.argb(255, 150, 150, 150));
        */

        /* Change the background color if this item selected.*/
        toggleBackgroundColor(holder,position);
    }
    @Override
    public int getItemCount() { return filteredCategories.size(); }
    @Override
    public long getItemId(int position) {
        /* As you can see, not Categories list, the FilteredCategoriesList is shown in ListView.
            Categories showing according to filtering by searching */
        return filteredCategories.get(position).getId();
    }

    /* Override methods of SwipeToAction.SwipeListener<Category> : Category click actions.*/
    @Override
    public boolean swipeLeft(Category category) {
        /* When SwipeLeft, pop up Category Edit "BottomSheetDialog". */
        CategoryEditModalBottomSheetDialog bottomSheetDialog = new CategoryEditModalBottomSheetDialog(categoryActivity,category);
        bottomSheetDialog.show(categoryActivity.getSupportFragmentManager(),CategoryEditModalBottomSheetDialog.TAG);
        return true;
    }
    @Override
    public boolean swipeRight(Category category) {
        /* When SwipeRight, "WordActivity" opens to show the words of the selected category. */
        openTheSelectedCategory(category);
        return true;
    }
    @Override
    public void onClick(Category category) {
        /* Select or open a category. */
        if(getSelectedCategoryCount() > 0){ // If there is a selected category, then current selected is also added.
            selectingCategory(category);
        }
        else{ // If no category is selected, then currently selected category opens.
            openTheSelectedCategory(category);
        }
    }
    @Override
    public void onLongClick(Category category) {
        /* Selecting category by long click. */
        selectingCategory(category);
    }

    /* Util methods : Opening and Selecting categories. */
    private void openTheSelectedCategory(Category category){
        /* When click to category, "WordActivity" opens to show the words of the selected category. */
        Intent intent = new Intent(categoryActivity, WordActivity.class);
        intent.putExtra(CategoryActivity.INTENT_CATEGORY_ID,category.getId());
        intent.putExtra(CategoryActivity.INTENT_CATEGORY_NAME,category.getName());
        categoryActivity.startActivity(intent);
    }
    private void selectingCategory(Category category){
        displaySelectingActionModeIfNotExist(); // if action mod null : then fill it and start.
        toggleSelection(category); // switch select or non-select.

        int selectedCount = getSelectedCategoryCount();
        if( selectedCount == 0){ // if all selects are removed, then Action Mode Will Closed.
            selectingCategoriesActionMode.finish();
        }else{ // if there is selecting items exists, write these count to the title.
            selectingCategoriesActionMode.setTitle(selectedCount + " " + categoryActivity.getResources().getString(R.string.category_selecting_title));
            selectingCategoriesActionMode.invalidate();
        }
    }


    /*################# SELECTING SECTION #################*/

    /* Variables of "Selecting Section". */
    private SparseBooleanArray  selectedCategories; // Selected categories are stored.
    private int                 currentSelectedPosition= -1;
    private ActionMode          selectingCategoriesActionMode;

    /* Methods of "Selecting Section". */
    public void displaySelectingActionModeIfNotExist(){
        /* Selecting "Action Mode" starts. */
        if(selectingCategoriesActionMode == null) {
            selectingCategoriesActionMode = categoryActivity.startSupportActionMode(new CategorySelectActionModeCallBack(this, categoryActivity));
        }
    }
    public void terminateActionMode(){
        /* Selecting "Action Mode" ending. */
        selectingCategoriesActionMode = null;
        clearSelections(); // selected categories are freedom.
    }
    public void toggleBackgroundColor(CategoryViewHolder holder,int position){
        /* According to selected status, toggle background color between "White" ,"Blue". */
        if(selectedCategories.get(position,false)){ // If this category selected then set background color "Blue".
            holder.background.setBackgroundColor(categoryActivity.getResources().getColor(R.color.main_blue_1));
        }else{
            holder.background.setBackgroundColor(Color.WHITE);
        }
        if(currentSelectedPosition == position) currentSelectedPosition = -1; // If no items left, veriable is emptied.
    }
    public void toggleSelection(int position){
        /* Switch selecting of category by position */
        currentSelectedPosition = position;
        if(selectedCategories.get(position,false))
            selectedCategories.delete(position); // If this is already selected, and user again click this, we will remove this from selectedList.
        else
            selectedCategories.put(position,true); // If this is not selected yet, so user click to this then set this is selected.
        notifyItemChanged(position); // At this position item have changed, update this. After recyclerview will refresh this item.
    }
    public void toggleSelection(Category category){
        /* Switch selecting of category by object */
        int position = categories.indexOf(category); // The index of the object is obtained.
        toggleSelection(position);
    }
    public List<Integer>    getSelectedCategoryPositions(){
        /* Get which categories are selected. */
        List<Integer> selectedPositions = new ArrayList<>(selectedCategories.size());
        for(int i=0; i< selectedCategories.size();i++){
            selectedPositions.add(selectedCategories.keyAt(i));
        }
        return selectedPositions;
    }
    public long[]           getSelectedCategoryIds(){
        /* Get categories ids which categories are selected. (for starting exam.) */
        List<Integer> positions = getSelectedCategoryPositions();
        long[] ids = new long[positions.size()];
        for (Integer i : positions) {
            ids[i] = filteredCategories.get(i).getId();
        }
        return ids;
    }
    public int  getSelectedCategoryCount(){
        /* Get count of how many category selected. */
        return selectedCategories.size();
    }
    public void clearSelections(){
        /* All categories are deselected. */
        selectedCategories.clear();
        notifyDataSetChanged(); // All categories refreshed at RecyclerView.
    }


    /*################# FILTERING SECTION #################*/

    /* Variables of "Filtering Section". */
    private List<Category> filteredCategories; // Search-filtered categories. These categories are shown on the ListView.
    private Filter filter = new Filter(){
        @Override
        protected Filter.FilterResults performFiltering(CharSequence constraint) {
            List<Category> filteredList = new ArrayList<>();
            if(constraint == null || constraint.length() == 0){ // If there is no any search data then put all categories to ListView.
                filteredList.addAll(categories);
            }else{ // Search by word entered.
                String filterPattern = constraint.toString().toLowerCase().trim();
                for(Category c : categories){
                    if(c.getName().toLowerCase().contains(filterPattern)){
                        filteredList.add(c);
                    }
                }
            }
            Filter.FilterResults results = new Filter.FilterResults();
            results.values = filteredList; // We got the result categories, At below OverrideMethod we will put this to ListView.
            return results;
        }
        @Override
        protected void publishResults(CharSequence constraint, Filter.FilterResults results) {
            filteredCategories.clear();
            filteredCategories.addAll((List) results.values); // There is putting to ListView.
            notifyDataSetChanged();
        }
    }; // Custom Filter anonim class.

    /* Override method of Filterable. */
    @Override
    public Filter getFilter() { return filter; }

    /* Override methods of SearchView.OnQueryTextListener : Filtering categories with query words */
    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }
    @Override
    public boolean onQueryTextChange(String newText) {
        getFilter().filter(newText); // Will be filtered according to the changing search word.
        return true;
    }


    /*################# Others #################*/
    public void addCategory(Category newCategory){
        /* Add category to List. */
        categories.add(newCategory);
        filteredCategories.add(newCategory);
        notifyItemInserted(categories.size() - 1);
    }
    public void deleteCategory(Category category){
        /* Delete category from List. */
        int index = categories.indexOf(category);
        categories.remove(category);
        filteredCategories.remove(category);
        notifyItemRemoved(index);
    }
    public void updateCategory(Category category){
        /* Update category from List. */
        int index = categories.indexOf(category);
        notifyItemChanged(index);
    }
}