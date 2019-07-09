package com.erdemsiyam.memorizeyourwords.util.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.erdemsiyam.memorizeyourwords.R;
import com.erdemsiyam.memorizeyourwords.entity.Category;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

import co.dift.ui.SwipeToAction;

public class CategoryRecyclerViewAdapter extends RecyclerView.Adapter<CategoryRecyclerViewAdapter.CategoryViewHolder> implements Filterable {
    public static CategoryRecyclerViewAdapter instance; // this instance created for reaching to filter methods from listener class (CategorySearchListener)
    private List<Category> categories;

    public CategoryRecyclerViewAdapter(List<Category> categories) {
        instance = this;
        this.categories = categories;
        filteredCategories = new ArrayList<>(categories);
    }
    public class CategoryViewHolder extends SwipeToAction.ViewHolder<Category> {
        public TextView categoryName;
        public Chip wordCount;
        public ImageButton alarmButton;
        public CategoryViewHolder(View layout) {
            super(layout);
            categoryName = layout.findViewById(R.id.categoryName);
            wordCount = layout.findViewById(R.id.categoryWordCount);
            alarmButton = layout.findViewById(R.id.alarmButton);
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
    }
    @Override
    public int getItemCount() { return filteredCategories.size(); }
    @Override
    public long getItemId(int position) { return filteredCategories.get(position).getId(); } //changed

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

}