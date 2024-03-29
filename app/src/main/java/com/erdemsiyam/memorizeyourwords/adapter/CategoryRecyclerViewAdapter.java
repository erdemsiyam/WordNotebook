package com.erdemsiyam.memorizeyourwords.adapter;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;
import com.erdemsiyam.memorizeyourwords.activity.CategoryActivity;
import com.erdemsiyam.memorizeyourwords.R;
import com.erdemsiyam.memorizeyourwords.activity.SettingActivity;
import com.erdemsiyam.memorizeyourwords.activity.WordActivity;
import com.erdemsiyam.memorizeyourwords.androidservice.CategoryNotificationService;
import com.erdemsiyam.memorizeyourwords.androidservice.WordNotificationService;
import com.erdemsiyam.memorizeyourwords.entity.Category;
import com.erdemsiyam.memorizeyourwords.entity.NotificationCategory;
import com.erdemsiyam.memorizeyourwords.entity.NotificationWord;
import com.erdemsiyam.memorizeyourwords.service.CategoryService;
import com.erdemsiyam.memorizeyourwords.fragment.CategoryEditModalBottomSheetDialog;
import com.erdemsiyam.memorizeyourwords.listener.CategorySelectActionModeCallBack;
import com.erdemsiyam.memorizeyourwords.service.NotificationCategoryService;
import com.erdemsiyam.memorizeyourwords.service.NotificationWordService;
import com.erdemsiyam.memorizeyourwords.util.TimePrintHelper;
import com.erdemsiyam.memorizeyourwords.util.WordGroupType;
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
        /* UI data loading. */
        Category category = filteredCategories.get(position); // Get the next category to UI data loading.
        holder.categoryName.setText(category.getName()); // Get name.
        holder.wordCount.setText(CategoryService.getCategoryWordCount(categoryActivity,category)+""); // Get the category's word count from DB.
        holder.wordCount.setClickable(false); // Not need.
        holder.data = category;

        /* UI Font Sizes loading. */
        holder.categoryName.setTextSize(SettingActivity.getFont(categoryActivity,0)); // FontSize loading from Setting.
        holder.wordCount.setTextSize(SettingActivity.getFont(categoryActivity,0)); // Not need.

        /* WordNotification Setups. */
        NotificationWord notificationWord = NotificationWordService.getByCategory(categoryActivity,category.getId()); // Is this category saved in WordNotification?
        holder.btnCategoryWordsNotification.setImageResource((notificationWord != null)?R.drawable.ic_notification_word_on:R.drawable.ic_notification_word_off);// Yes : put green icon. No : put fade icon.
        holder.btnCategoryWordsNotification.setOnClickListener(new View.OnClickListener(){ // Listener for click to notification icon : changes notification statu.
            @Override
            public void onClick(View v) {
                if(notificationWord != null) // If there is setup notification. So remove this.
                    createAlertDialogForRemoveWordNotification(category,notificationWord,position).show();
                else // If not exists any notification for this category. So create.
                    createAlertDialogForSelectingWordTypesToWordNotification(category,position).show();
            }
        });
        holder.btnCategoryWordsNotification.setOnLongClickListener(new View.OnLongClickListener() { // Listener for long click to notification icon.
            @Override
            public boolean onLongClick(View v) {
                if(notificationWord != null){
                    /* Shows which word group the saved notification consists of. */
                    Toast.makeText(categoryActivity,categoryActivity.getResources().getString(WordGroupType.getTypeByKey(notificationWord.getWordType()).value),Toast.LENGTH_SHORT).show();
                }else{
                    /* Gives information about it when long click if not allow. */
                    Toast.makeText(categoryActivity,R.string.words_notification_about,Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        /* Change the background color if this item selected.*/
        toggleBackgroundColor(holder,position);

        /* CategoryNotification Setups. */
        NotificationCategory notificationCategory = NotificationCategoryService.getByCategory(categoryActivity,category.getId()); // Is this category saved in CategoryNotification?
        holder.btnCategoryNotification.setImageResource((notificationCategory != null)?R.drawable.ic_notification_category_on:R.drawable.ic_notification_category_off);// Yes : put green icon. No : put fade icon.
        holder.btnCategoryNotification.setOnClickListener(v -> {
            if(notificationCategory == null){
                createAlertDialogsForCreateCategoryNotification(category,position);
            } else {
                createAlertDialogToRemoveCategoryNotification(category,notificationCategory,position);
            }
        });
        holder.btnCategoryNotification.setOnLongClickListener(new View.OnLongClickListener() { // Listener for long click to notification icon.
            @Override
            public boolean onLongClick(View v) {
                if(notificationCategory != null){
                    /* Shows which word group the saved notification consists of. */
                    String message = categoryActivity.getResources().getString(WordGroupType.getTypeByKey(notificationCategory.getWordType()).value)+" " + TimePrintHelper.getTime(categoryActivity.getApplicationContext(),notificationCategory.getHour(),notificationCategory.getMinute());
                    Toast.makeText(categoryActivity,message,Toast.LENGTH_SHORT).show();
                }else{
                    /* Gives information about it when long click if not allow. */
                    Toast.makeText(categoryActivity,R.string.category_notification_about,Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
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
        /* When SwipeRight, Selecting this item to exam. */
        selectingCategory(category);
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
        for(int i = 0; i<ids.length;i++){
            ids[i] = filteredCategories.get(positions.get(i)).getId();
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


    /*################# WORD NOTIFICATION SECTION #################*/

    /* Indexing Variable for AlertDialog. */
    private int wordTypeToWordNotificationSelectIndex =-1;

    /* These methods at below, are belong to "WordNotification" Icon's click listeners. */
    private AlertDialog createAlertDialogForSelectingWordTypesToWordNotification(Category category, int position){
        /* AlertDialog is prepared which "WordGroup" we want to choose for "WordNotification". */
        AlertDialog.Builder builder = new AlertDialog.Builder(categoryActivity);
        builder.setTitle(R.string.words_notification_select_alert_title);
        String[] options = WordGroupType.getValuesAsStringArray(categoryActivity); // Enum options are taken as Array of String type.
        builder.setSingleChoiceItems(options, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                wordTypeToWordNotificationSelectIndex = i; // The index is keeping at each click to options at AlertDialog.
            }
        });
        builder.setPositiveButton(R.string.words_notification_select_alert_button_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(wordTypeToWordNotificationSelectIndex <0){ // If not selected any word group then throw message.
                    Toast.makeText(categoryActivity, categoryActivity.getResources().getString(R.string.words_notification_error_message_word_group), Toast.LENGTH_SHORT).show();
                    return;
                }
                NotificationWordService.addNotificationWord(categoryActivity,category.getId(), wordTypeToWordNotificationSelectIndex); // Add this category as new notification to NotificationWord on DB.
                notifyItemChanged(position); // Refreshed this category.
                restartWordNotificationReceiver(); // Restarting "WordNotification BroadcastReceiver".
                Toast.makeText(categoryActivity, categoryActivity.getResources().getString(R.string.words_notification_success_message1)
                        +" "+getStartEndTimeOfWordNotification()
                        +" "+categoryActivity.getResources().getString(R.string.words_notification_success_message2)
                        +" "+getLoopTimeOfWordNotification()
                        +" "+categoryActivity.getResources().getString(R.string.minute), Toast.LENGTH_LONG).show(); // Say it's done.
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        return builder.create(); // AlertDialog is ready.
    }
    private AlertDialog createAlertDialogForRemoveWordNotification(Category category, NotificationWord notificationWord, int position){
        /* Shows AlertDialog if user want to delete "WordNotification". */
        AlertDialog.Builder builder = new AlertDialog.Builder(categoryActivity);
        builder.setTitle(R.string.words_notification_remove_alert_title);
        builder.setMessage(category.getName()+ " : "+categoryActivity.getResources().getString(WordGroupType.getTypeByKey(notificationWord.getWordType()).value) );
        builder.setPositiveButton(R.string.yes,new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                NotificationWordService.delete(categoryActivity,notificationWord); // Delete "WordNotification" from DB if user click "Yes".
                /* Notice : We don't stop Service because, maybe other categories have notification. */
                notifyItemChanged(position); // Refreshed this category.
            }
        });
        builder.setNegativeButton(R.string.cancel,new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        return builder.create(); // AlertDialog is ready.
    }

    /* Util Method for WordNotifications. */
    public  String getStartEndTimeOfWordNotification(){
        SharedPreferences sp = categoryActivity.getSharedPreferences(SettingActivity.PREFERENCE_NAME,SettingActivity.PREFERENCE_MODE);
        int startHour = sp.getInt(SettingActivity.WORD_NOTIFICATION_START_TIME_HOUR,9);
        int startMinute = sp.getInt(SettingActivity.WORD_NOTIFICATION_START_TIME_MINUTE,0);
        int endHour = sp.getInt(SettingActivity.WORD_NOTIFICATION_END_TIME_HOUR,23);
        int endMinute = sp.getInt(SettingActivity.WORD_NOTIFICATION_END_TIME_MINUTE,59);
        return " "+TimePrintHelper.getTime(categoryActivity,startHour,startMinute)+" - "+ TimePrintHelper.getTime(categoryActivity,endHour,endMinute)+" ";
    }
    public  String getLoopTimeOfWordNotification(){
        int startHour = categoryActivity.getSharedPreferences(SettingActivity.PREFERENCE_NAME,SettingActivity.PREFERENCE_MODE).getInt(SettingActivity.WORD_NOTIFICATION_PERIOD,30);
        return ((startHour<10)?"0"+startHour:""+startHour);
    }
    private void   restartWordNotificationReceiver(){
        /* "WordNotificationService" restarting. */

        /* Stop. */
        WordNotificationService.stop(categoryActivity);

        /* Start. */
        WordNotificationService.start(categoryActivity);
    }

    /*################# CATEGORY NOTIFICATION SECTION #################*/

    /* Indexing Variable for AlertDialog. */
    private int wordTypeToCategoryNotificationSelectIndex =-1;

    /* These methods at below, are belong to "CategoryNotification" Icon's click listeners. */
    private void createAlertDialogsForCreateCategoryNotification(Category category, int position){
        /* AlertDialog to selecting "WordGroup" */
        AlertDialog.Builder builder = new AlertDialog.Builder(categoryActivity);
        builder.setTitle(R.string.category_notification_select_alert_title);
        String[] options = WordGroupType.getValuesAsStringArray(categoryActivity); // Enum options are taken as Array of String type.
        builder.setSingleChoiceItems(options, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                wordTypeToCategoryNotificationSelectIndex = i; // The index is keeping at each click to options at AlertDialog.
            }
        });
        builder.setPositiveButton(R.string.category_notification_select_alert_button_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                /* If not selected any word group then throw message. */
                if(wordTypeToCategoryNotificationSelectIndex <0){
                    Toast.makeText(categoryActivity, categoryActivity.getResources().getString(R.string.words_notification_error_message_word_group), Toast.LENGTH_SHORT).show();
                    return;
                }

                /* At below create "TimePicker" to selecting "AlertTime". After selecting "WordGroup"*/

                /* Created TimePickerDialog to catch the "NotificationTime". */
                class TimePicker implements TimePickerDialog.OnTimeSetListener {
                    @Override
                    public void onTimeSet(android.widget.TimePicker view, int hourOfDay, int minute) {
                        /* Create "NotificationCategory" and set "Alarm" after catch "NotificationTime". */

                        /* Created "NotificationCategory". */
                        NotificationCategory notificationCategory = NotificationCategoryService.addNotificationCategory(categoryActivity,category.getId(),wordTypeToCategoryNotificationSelectIndex,hourOfDay,minute); // Add this category as new notification to NotificationWord on DB.
                        notifyItemChanged(position); // Refresh this category on "ListView" to show alarm activated.

                        if(notificationCategory == null) return; // At "Android 4.1" have a bug about running this method TWICE. At twice this object comes null, if this null return because that's mean we in 2nd round.

                        /* Creating alarm. This is helper method. */
                        CategoryNotificationService.setAlarm(categoryActivity,notificationCategory);

                        /* The message shows up, alarm set done. */
                        String message = categoryActivity.getResources().getString(R.string.category_notification_success_message)+" "+TimePrintHelper.getTime(categoryActivity,hourOfDay,minute);
                        Toast.makeText(categoryActivity,message, Toast.LENGTH_SHORT).show();
                    }
                }

                /* Call the "TimePicker" class to catch "NotificationTime" and set it on "Alarm". */
                TimePickerDialog timePickerDialog = new TimePickerDialog(categoryActivity,new TimePicker(),0,0, DateFormat.is24HourFormat(categoryActivity));
                timePickerDialog.setTitle(R.string.category_notification_select_time_title);
                timePickerDialog.show(); // Shows "TimePicker" to Alarm.
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.create().show(); // Show "AlertDialog" which are "SelectingWordGroup" and including "TimePicker" to Alarm.
    }
    private void createAlertDialogToRemoveCategoryNotification(Category category, NotificationCategory notificationCategory, int position){
        /* Shows AlertDialog if user want to delete "CategoryNotification". */
        AlertDialog.Builder builder = new AlertDialog.Builder(categoryActivity);
        builder.setTitle(R.string.category_notification_remove_alert_title);
        builder.setMessage(category.getName()+ " : "+categoryActivity.getResources().getString(WordGroupType.getTypeByKey(notificationCategory.getWordType()).value));
        builder.setPositiveButton(R.string.yes,new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AlarmManager alarmManager = (AlarmManager) categoryActivity.getSystemService(Context.ALARM_SERVICE);

                /* An intent created for cancel alarm. */
                Intent intent = new Intent(categoryActivity, CategoryNotificationService.class);
                PendingIntent pendingIntent = PendingIntent.getService(categoryActivity, notificationCategory.getNotificationId(), intent, 0);

                /* Alarm cancelled.*/
                alarmManager.cancel(pendingIntent);

                /* Delete "CategoryNotification" from DB. */
                NotificationCategoryService.delete(categoryActivity,notificationCategory);

                /* Refresh this category to show alarm ended. */
                notifyItemChanged(position);
            }
        });
        builder.setNegativeButton(R.string.cancel,new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.create().show(); // AlertDialog shows.
    }

}