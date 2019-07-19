package com.erdemsiyam.memorizeyourwords.util.adapter;

import android.content.Context;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.widget.RecyclerView;

import com.erdemsiyam.memorizeyourwords.R;
import com.erdemsiyam.memorizeyourwords.entity.Category;
import com.erdemsiyam.memorizeyourwords.util.listener.CategorySelectActionModeCallBack;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

import co.dift.ui.SwipeToAction;

public class CategoryRecyclerViewAdapter extends RecyclerView.Adapter<CategoryRecyclerViewAdapter.CategoryViewHolder> implements Filterable {
    public static CategoryRecyclerViewAdapter instance; // this instance created for reaching to filter methods from listener class (CategorySearchListener)
    private AppCompatActivity context;
    private List<Category> categories;

    public CategoryRecyclerViewAdapter(Context context, List<Category> categories) {
        instance = this;
        this.categories = categories;
        filteredCategories = new ArrayList<>(categories);
        this.context = (AppCompatActivity) context;
        selectedCategories = new SparseBooleanArray();
    }
    public class CategoryViewHolder extends SwipeToAction.ViewHolder<Category> {
        public TextView categoryName;
        public Chip wordCount;
        public ImageButton alarmButton;
        public RelativeLayout background; // if this object selected, then background color changing to PrimaryColor
        public CategoryViewHolder(View layout) {
            super(layout);
            categoryName = layout.findViewById(R.id.categoryName);
            wordCount = layout.findViewById(R.id.categoryWordCount);
            alarmButton = layout.findViewById(R.id.alarmButton);
            background = layout.findViewById(R.id.elementCategoryContainer);
        }
    }
    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_category, parent, false);
        return new CategoryViewHolder(layout);
    }
    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position) {
        Category category = filteredCategories.get(position);
        holder.categoryName.setText(category.getName());
        holder.wordCount.setText("123");
        holder.data = category;

        toggleBackgroundColor(holder,position); // if this item selected Then set this background color gray.
    }
    @Override
    public int getItemCount() { return filteredCategories.size(); }
    @Override
    public long getItemId(int position) { return filteredCategories.get(position).getId(); } //changed

    /*################# SELECT SECTION ###############*/
    private SparseBooleanArray selectedCategories;
    private int currentSelectedPosition= -1;
    public ActionMode selectingCategoriesActionMode;
    public ActionMode.Callback selectingCategoriesActionModeCallBack = new CategorySelectActionModeCallBack(this,context);

    public void displaySelectingActionModeIfNotExist(){
        if(selectingCategoriesActionMode == null) {
            selectingCategoriesActionMode = context.startSupportActionMode(selectingCategoriesActionModeCallBack);
        }
    }
    public void toggleBackgroundColor(CategoryViewHolder holder,int position){ // switch background color.
        if(selectedCategories.get(position,false)){
            //holder.itemView.setBackgroundColor(Color.rgb(102,199,241)); // if this selected, so coloring the background gray.0x9934B5E4
            holder.background.setBackgroundColor(Color.rgb(161,217,241));
        }else{
            //holder.itemView.setBackgroundColor(Color.TRANSPARENT); // if this is not selected, coloring the background to default color.
            holder.background.setBackgroundColor(Color.WHITE);
        }
        if(currentSelectedPosition == position) currentSelectedPosition = -1; // we have done with this "current position"
    }
    public void toggleSelection(int position){ // switch select or non-select.
        currentSelectedPosition = position; // we will work with this position. get it.
        if(selectedCategories.get(position,false))
            selectedCategories.delete(position); // if this is already selected, and user again click this, we will remove this from selectedList
        else
            selectedCategories.put(position,true); // if this is not selected yet, so user click to this then set this is selected.
        notifyItemChanged(position); // we saying here ; at this position item have changed, update this. After recyclerview will refresh this item.
    }
    public void toggleSelection(Category category){ // switch select or non-select.
        int position = categories.indexOf(category);
        currentSelectedPosition = position; // we will work with this position. get it.
        if(selectedCategories.get(position,false))
            selectedCategories.delete(position); // if this is already selected, and user again click this, we will remove this from selectedList
        else
            selectedCategories.put(position,true); // if this is not selected yet, so user click to this then set this is selected.
        notifyItemChanged(position); // we saying here ; at this position item have changed, update this. After recyclerview will refresh this item.
    }
    public List<Integer> getSelectedCategoryPositions(){ // get which categories are selected.
        List<Integer> selectedPositions = new ArrayList<>(selectedCategories.size());
        for(int i=0; i< selectedCategories.size();i++){
            selectedPositions.add(selectedCategories.keyAt(i));
        }
        return selectedPositions;
    }
    public void removeData(int position){
        categories.remove(position);
        currentSelectedPosition = -1;
    }
    public int getSelectedCategoryCount(){ return selectedCategories.size();} // get count of how many category selected.
    public void clearSelections(){ // selected categories are freedom.
        selectedCategories.clear();
        notifyDataSetChanged(); // all categories will be refreshed by recyclerview
    }

    /*################# FİLTER SECTION ###############*/
    private List<Category> filteredCategories;
    @Override
    public Filter getFilter() { return filter; } // This object just below there.
    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Category> filteredList = new ArrayList<>();
            if(constraint == null || constraint.length() == 0){
                filteredList.addAll(categories);
            }else{
                String filterPattern = constraint.toString().toLowerCase().trim();
                for(Category c : categories){
                    if(c.getName().toLowerCase().contains(filterPattern)){
                        filteredList.add(c);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredCategories.clear();
            filteredCategories.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    /*################# Others #######################*/
    public void addCategory(Category newCategory){
        categories.add(newCategory);
        filteredCategories.add(newCategory);
        notifyDataSetChanged();
        //notifyItemInserted(categories.size() - 1);
    }
}