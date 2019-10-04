package com.erdemsiyam.memorizeyourwords.fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
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
import com.erdemsiyam.memorizeyourwords.entity.Category;
import com.erdemsiyam.memorizeyourwords.entity.Word;
import com.erdemsiyam.memorizeyourwords.service.CategoryService;
import com.erdemsiyam.memorizeyourwords.service.WordService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExcelExportDialogFragment extends AppCompatDialogFragment {

    /* A Pop-up show up for "Excel Words Export" when click the "ExcelExport" option at "WordActivity".
       This Pop-up is "DialogFragment".
       In here User selects directory path to create and save ".xlsx" file. */

    /* Using this library for excel import utils. : https://github.com/andruhon/android5xlsx */

    /* Constants. */
    public  static final String TAG = "df_excel_export";
    public  static ExcelExportDialogFragment instance; // "WordActivity" uses this to allow the process to continue in response to the permission request.
    public  static final int PERMISSION_REQUEST_CODE = 1002;
    private static final String PERMISSION_WRITE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final String PERMISSION_READ = Manifest.permission.READ_EXTERNAL_STORAGE;

    /* Variables.*/
    private WordActivity wordActivity;
    private ArrayList<String> pathHistory;
    private long categoryId;

    /* UI components. */
    private Button btnExcelExportPathBack;
    private Button btnExcelExportCancel;
    private Button btnExcelExportPathSubmit;
    private ListView lvDeviceFilesPaths;

    /* Constructor. */
    public ExcelExportDialogFragment(WordActivity wordActivity, long categoryId) {
        instance = this;
        this.wordActivity = wordActivity;
        this.categoryId = categoryId;
    }

    /*  Override method of AppCompatDialogFragment.
        Creating Our CustomizeDialog. */

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        /* "AlertDialog" Design will be used to our "CustomizeDialog". */
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        /* Including layout of DialogFragment's. */
        View view = inflater.inflate(R.layout.dialog_excel_export,null);

        /* UI components are installed. */
        btnExcelExportPathBack = view.findViewById(R.id.btnExcelExportPathBack);
        btnExcelExportCancel = view.findViewById(R.id.btnExcelExportCancel);
        btnExcelExportPathSubmit = view.findViewById(R.id.btnExcelExportPathSubmit);
        lvDeviceFilesPaths = view.findViewById(R.id.lvDeviceFilesPaths);

        /* "AlertDialog" building. */
        builder.setView(view);
        builder.setTitle(R.string.excelexport_alert_title);

        /* Giving listeners to UI components. */
        lvDeviceFilesPaths.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String clickedDirectory = (String) adapterView.getItemAtPosition(position);
                String lastClickedDirectory = pathHistory.get(pathHistory.size()-1);

                /* Return it if click more times same directory. */
                if(clickedDirectory.equalsIgnoreCase(lastClickedDirectory))
                    return;

                /* Add last directory which user clicked. */
                pathHistory.add(clickedDirectory);
                checkInternalStorage();
            }
        });
        btnExcelExportPathSubmit.setOnClickListener(v -> {
            /* Export Words. */

            /* Get category from DB. */
            Category category = CategoryService.getCategoryById(wordActivity,categoryId);

            /* Get all words of category from DB. */
            List<Word> words = WordService.getWordsByCategoryId(wordActivity,categoryId);

            /* Control category and words. */
            if(category == null || words.size() <= 0){
                toastMessage(R.string.excelexport_message_no_content);
                return;
            }

            /* Create excel file. */
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet(WorkbookUtil.createSafeSheetName(category.getName()));

            /* Add words to excel file. */
            for (int i=0;i<words.size();i++) {
                Word nextWord = words.get(i);
                Row row = sheet.createRow(i);
                Cell cell1 = row.createCell(0);
                Cell cell2 = row.createCell(1);
                cell1.setCellValue(nextWord.getStrange());
                cell2.setCellValue(nextWord.getExplain());
            }

            /* Create excel file name. */
            String dateTime = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault()).format(new Date());
            String outFileName = category.getName()+"-"+dateTime+".xlsx";

            /* Save the excel file to device. */
            try {
                /* Control : Is the path chosen. */
                int lastPathIndex = pathHistory.size();
                if(lastPathIndex < 2) {
                    toastMessage(R.string.excelexport_message_no_path);
                    return;
                }

                /* Creating file. */
                String lastPath = pathHistory.get(lastPathIndex-1);
                File outFile = new File(lastPath, outFileName);
                OutputStream outputStream = new FileOutputStream(outFile.getAbsolutePath());
                workbook.write(outputStream);
                outputStream.flush();
                outputStream.close();

                /* Show message export done. */
                toastMessage(R.string.excelexport_message_success);

                /* Share with other application if user wants. */
                shareFile(lastPath+"/"+outFileName);

                dismiss(); // Close DialogFragment.
            }
            catch (Exception e) { }
        });
        btnExcelExportPathBack.setOnClickListener(v -> {
            /* Get move back on directory path if its not last. */
            if(pathHistory.size() > 1){
                pathHistory.remove(pathHistory.get(pathHistory.size()-1));
                checkInternalStorage();
            }
        });
        btnExcelExportCancel.setOnClickListener(v -> {
            dismiss();
        });

        /* Making first action : get storage to list paths. */
        openSdCard();

        return builder.create();  // Prepared Customize "AlertDialog" return.
    }

    /* Util Methods. */
    public  void    openSdCard(){
        /* Check permission to read storage. */
        if(!checkFileWriteReadPermissions()) return; // If not allow, then do nothing.

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
            lvDeviceFilesPaths.setAdapter(adapter);
        }
        catch(NullPointerException e){
        }
    }
    private boolean checkFileWriteReadPermissions() {
        /* Check "Write And Read File" permission. */

        /* Permission not need to API 22 and below.*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // If device API 23 or above.
            boolean readGranted = wordActivity.checkSelfPermission(PERMISSION_READ) == PackageManager.PERMISSION_GRANTED;
            boolean writeGranted = wordActivity.checkSelfPermission(PERMISSION_WRITE) == PackageManager.PERMISSION_GRANTED;

            /* If it is not allow, "Request ALLOW "from User.*/
            if (!writeGranted || !readGranted) {
                wordActivity.requestPermissions(new String[]{ PERMISSION_WRITE, PERMISSION_READ}, PERMISSION_REQUEST_CODE);
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
    public  void    shareFile(String filePath) {
        Uri fileUri = Uri.parse("content://"+filePath);
        Intent shareIntent = new Intent();
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
        shareIntent.setType("application/octet-stream");
        startActivity(Intent.createChooser(shareIntent,getResources().getString(R.string.excelexport_share_title)));
    }
}
