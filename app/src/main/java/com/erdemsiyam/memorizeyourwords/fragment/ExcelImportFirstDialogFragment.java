package com.erdemsiyam.memorizeyourwords.fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import com.erdemsiyam.memorizeyourwords.R;
import com.erdemsiyam.memorizeyourwords.activity.WordActivity;
import com.erdemsiyam.memorizeyourwords.service.WordService;
import com.erdemsiyam.memorizeyourwords.util.ExcelImportModel;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ExcelImportFirstDialogFragment extends AppCompatDialogFragment {

    /* A Pop-up show up for "Excel Words Import" when click the "ExcelImport" option at "WordActivity".
       This Pop-up is "DialogFragment".
       This is the first "DialogFragment" for ExcelImport.
       In here User selects ".xlsx" file path.
       Then after opens second dialog, it's "ExcelImportSecondDialogFragment". */

    /* Using this library for excel import utils. : https://github.com/andruhon/android5xlsx */

    /* Constants. */
    public  static final String TAG = "df_excel_import_first";
    public static ExcelImportFirstDialogFragment instance; // "WordActivity" uses this to allow the process to continue in response to the permission request.
    public  static final int    PERMISSION_REQUEST_CODE = 1001;
    private static final String PERMISSION_READ = Manifest.permission.READ_EXTERNAL_STORAGE;

    /* Variables.*/
    private WordActivity wordActivity;
    private ExcelImportModel excelImportModel;
    private ArrayList<String> pathHistory;

    /* UI components. */
    private Button btnPathBack;
    private Button btnExcelImportCancel;
    private ListView lvFilesPaths;

    /* Constructor. */
    public ExcelImportFirstDialogFragment(WordActivity wordActivity, long categoryId) {
        instance = this;
        this.wordActivity = wordActivity;
        this.excelImportModel = new ExcelImportModel();
        excelImportModel.setCategoryId(categoryId);
    }

    /*  Override method of AppCompatDialogFragment.
        Creating Our CustomizeDialog. */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        /* "AlertDialog" Design will be used to our "CustomizeDialog". */
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        /* Including layout of DialogFragment's. */
        View view = inflater.inflate(R.layout.dialog_excel_import_first,null);

        /* UI components are installed. */
        btnPathBack = view.findViewById(R.id.btnPathBack);
        btnExcelImportCancel = view.findViewById(R.id.btnExcelImportCancel);
        lvFilesPaths = view.findViewById(R.id.lvFilesPaths);

        /* "AlertDialog" building. */
        builder.setView(view);
        builder.setTitle(R.string.excelimport_first_alert_title);

        /* Giving listeners to UI components. */
        lvFilesPaths.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String clickedDirectory = (String) adapterView.getItemAtPosition(i);
                if(clickedDirectory.endsWith(".xlsx")){ // If clicked a ".xlsx" file then get this, and show second dialog "ExcelImportSecondDialogFragment" to select columns and sheet.
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //Execute method for reading the excel data.
                            excelImportModel.setPathExcel(clickedDirectory);

                            /* Opens Second Dialog : "ExcelImportSecondDialogFragment". */
                            ExcelImportSecondDialogFragment dialog = new ExcelImportSecondDialogFragment(wordActivity,excelImportModel);
                            dialog.show(wordActivity.getSupportFragmentManager(), ExcelImportSecondDialogFragment.TAG);
                            dismiss(); // tHis dialog removed from show.
                        }
                    }).start();
                }else
                {
                    pathHistory.add(clickedDirectory);
                    checkInternalStorage();
                }
            }
        });
        btnPathBack.setOnClickListener(v -> {
            if(pathHistory.size() > 1){
                pathHistory.remove(pathHistory.get(pathHistory.size()-1));
                checkInternalStorage();
            }
        });
        btnExcelImportCancel.setOnClickListener(v -> {
            dismiss();
        });

        /* Making first action : get storage to list paths. */
        openSdCard();

        return builder.create();  // Prepared Customize "AlertDialog" return.
    }

    /* Util Methods. */
    public  void    openSdCard(){
        /* Check permission to read storage. */
        if(!checkFileReadPermissions()) return; // If not allow, then do nothing.

        /* Opening SdCard stuffs. */
        pathHistory = new ArrayList<>();
        pathHistory.add(System.getenv("EXTERNAL_STORAGE"));

        /* Storage load to ListView "lvFilesPaths". */
        checkInternalStorage();
    }
    private void    checkInternalStorage(){
        /* Storage load to ListView "lvFilesPaths". */
        try{
            File file;
            if (!Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                toastMessage(R.string.excelimport_message_no_sd_card);
                return;
            }
            else{
                // Locate the image folder in your SD Card.
                file = new File(pathHistory.get(pathHistory.size()-1));
            }
            File[] listFile = file.listFiles();
            // Create a String array for filePathStrings.
            String[] filePathStrings = new String[listFile.length];
            // Create a String array for fileNameStrings.
            String[] fileNameStrings = new String[listFile.length];
            for (int i = 0; i < listFile.length; i++) {
                // Get the path of the image file.
                filePathStrings[i] = listFile[i].getAbsolutePath();
                // Get the name image file.
                fileNameStrings[i] = listFile[i].getName();
            }
            for (int i = 0; i < listFile.length; i++)
            {
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(wordActivity, android.R.layout.simple_list_item_1, filePathStrings);
            lvFilesPaths.setAdapter(adapter);
        }
        catch(NullPointerException e){
        }
    }
    private boolean checkFileReadPermissions() {
        /* Check "ReadFile" permission. */

        /* Permission not need to API 22 and below.*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // If device API 23 or above.
            boolean readGranted = wordActivity.checkSelfPermission(PERMISSION_READ) == PackageManager.PERMISSION_GRANTED;

            /* If it is not allow, "Request ALLOW "from User.*/
            if (!readGranted) {
                wordActivity.requestPermissions(new String[]{ PERMISSION_READ }, PERMISSION_REQUEST_CODE);
                /*  The Answer comes to "WordActivity"'s override method "onRequestPermissionsResult"
                    Got there and wait to answer after call "openSdCard()" method again to Load Files. */
                return false;
            }
        }
        return true;
    }
    private void    toastMessage(@StringRes int resId) {
        wordActivity.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(wordActivity, wordActivity.getResources().getString(resId), Toast.LENGTH_LONG).show();
            }
        });
    }

}
