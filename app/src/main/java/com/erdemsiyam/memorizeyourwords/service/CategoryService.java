package com.erdemsiyam.memorizeyourwords.service;

import android.content.Context;
import com.erdemsiyam.memorizeyourwords.database.MyDatabase;
import com.erdemsiyam.memorizeyourwords.entity.Category;

import java.util.List;

public final class CategoryService {

    public static List<Category> getCategories(Context context){
        return MyDatabase.getMyDatabase(context).getCategoryDAO().getAllCategory();
    }
    public static Category getCategoryById(Context context, Long id){
        return MyDatabase.getMyDatabase(context).getCategoryDAO().getCategoryById(id);
    }
    public static Category addCategory(Context context,String name,String color){
        Category category = new Category();
        category.setName(name);
        category.setColor(color);
        category.setId(MyDatabase.getMyDatabase(context).getCategoryDAO().insertCategory(category));
        return category;
    }
    public static void updateCategory(Context context, Category updatedCategory){
        MyDatabase.getMyDatabase(context).getCategoryDAO().updateCategory(updatedCategory);
    }
    public static void deleteCategory(Context context, Category willRemoveCategory){
        MyDatabase.getMyDatabase(context).getCategoryDAO().deleteCategory(willRemoveCategory);
    }

}
