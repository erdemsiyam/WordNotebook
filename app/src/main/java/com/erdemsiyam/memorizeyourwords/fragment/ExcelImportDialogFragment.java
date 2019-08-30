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

public class ExcelImportDialogFragment extends AppCompatDialogFragment {

    /* A Pop-up show up for "Excel Words Import" when click the "ExcelImport" option at "WordActivity".
       This Pop-up is "DialogFragment". */

    /* Using this library for excel import utils. : https://github.com/andruhon/android5xlsx */

    /* Constants. */
    public static final String TAG = "df_excel_import";
    private static final int PERMISSION_REQUEST_CODE = 1001;
    private static final String PERMISSION_READ = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final String PERMISSION_WRITE = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    /* Veriables.*/
    private WordActivity wordActivity;
    private long categoryId;
    private ArrayList<String> pathHistory;

    /* UI components. */
    private Button btnPathBack;
    private Button btnExcelImportCancel;
    private ListView lvFilesPaths;

    /* Constructor. */
    public ExcelImportDialogFragment(WordActivity wordActivity, long categoryId) {
        this.wordActivity = wordActivity;
        this.categoryId = categoryId;
    }

    /* Override method of AppCompatDialogFragment.
     * Creating Our CustomizeDialog. */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        /* "AlertDialog" Design will be used to our "CustomizeDialog". */
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        /* Including layout of DialogFragment's. */
        View view = inflater.inflate(R.layout.dialog_excel_import,null);

        /* UI components are installed. */
        btnPathBack = view.findViewById(R.id.btnPathBack);
        btnExcelImportCancel = view.findViewById(R.id.btnExcelImportCancel);
        lvFilesPaths = view.findViewById(R.id.lvFilesPaths);

        /* "AlertDialog" building. */

        builder.setView(view);
        builder.setTitle(R.string.excelimport_alert_title);

        /* Giving listeners to UI components. */
        lvFilesPaths.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String clickedDirectory = (String) adapterView.getItemAtPosition(i);
                if(clickedDirectory.endsWith(".xlsx")){
                    toastMessage(R.string.excelimport_message_loading);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //Execute method for reading the excel data.
                            readExcelData(clickedDirectory);
                        }
                    }).start();
                }else
                {
                    pathHistory.add(clickedDirectory);
                    checkInternalStorage();
                }
            }
        });
        btnPathBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pathHistory.size() <= 1){
                }else{
                    pathHistory.remove(pathHistory.get(pathHistory.size()-1));
                    checkInternalStorage();
                }
            }
        });
        btnExcelImportCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        /* Making first action : get storage to list paths. */
        openSdCard();

        return builder.create();  // Prepared Customize "AlertDialog" return.
    }

    /* Util Methods. */
    private void openSdCard(){
        checkFilePermissions();
        pathHistory = new ArrayList<String>();
        pathHistory.add(System.getenv("EXTERNAL_STORAGE"));
        checkInternalStorage();
    }
    private void readExcelData(String filePath) {
        // Decarle input file.
        File inputFile = new File(filePath);

        try {
            InputStream inputStream = new FileInputStream(inputFile);
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheetAt(0);
            int rowsCount = sheet.getPhysicalNumberOfRows();
            FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
            StringBuilder sb = new StringBuilder();

            // Outter loop, loops through rows.
            for (int r = 0; r < rowsCount; r++) {
                Row row = sheet.getRow(r);
                int cellsCount = row.getPhysicalNumberOfCells();
                // Inner loop, loops through columns.
                for (int c = 0; c < cellsCount; c++) {
                    // Handles if there are to many columns on the excel sheet.
                    if(c>2){
                        toastMessage(R.string.excelimport_message_incorrect_file);
                        break;
                    }else{
                        String value = getCellAsString(row, c, formulaEvaluator);
                        sb.append(value + "]:]");
                    }
                }
                sb.append("];]");
            }
            parseStringBuilder(sb);
        }catch (FileNotFoundException e) {}
        catch (IOException e) {}
    }
    private void parseStringBuilder(StringBuilder mStringBuilder) {

        // Splits the sb into rows.
        String[] rows = mStringBuilder.toString().split("];]");

        // Add to the ArrayList<XYValue> row by row.
        for(int i=0; i<rows.length; i++) {
            //Split the columns of the rows
            String[] columns = rows[i].split("]:]");

            // Use try catch to make sure there are no "" that try to parse into doubles.
            try{
                String strange = columns[0].trim();
                String explain = columns[1].trim();

                if(strange.length()>30) strange = strange.substring(0,29);
                if(explain.length()>30) explain = explain.substring(0,29);

                WordService.addWord(wordActivity,categoryId,strange,explain);

            }catch (NumberFormatException e){
            }
        }
        dismiss();
        toastMessage(R.string.excelimport_message_loaded);
        wordActivity.refreshRecyclerView();
    }
    private String getCellAsString(Row row, int c, FormulaEvaluator formulaEvaluator) {
        String value = "";
        try {
            Cell cell = row.getCell(c);
            CellValue cellValue = formulaEvaluator.evaluate(cell);
            switch (cellValue.getCellType()) {
                case Cell.CELL_TYPE_BOOLEAN:
                    value = ""+cellValue.getBooleanValue();
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    double numericValue = cellValue.getNumberValue();
                    if(HSSFDateUtil.isCellDateFormatted(cell)) {
                        double date = cellValue.getNumberValue();
                        SimpleDateFormat formatter =
                                new SimpleDateFormat("MM/dd/yy");
                        value = formatter.format(HSSFDateUtil.getJavaDate(date));
                    } else {
                        value = ""+numericValue;
                    }
                    break;
                case Cell.CELL_TYPE_STRING:
                    value = ""+cellValue.getStringValue();
                    break;
                default:
            }
        } catch (NullPointerException e) {}
        return value;
    }
    private void checkInternalStorage(){
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

        }catch(NullPointerException e){
        }
    }
    private void toastMessage(@StringRes int resId) {
        wordActivity.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(wordActivity, wordActivity.getResources().getString(resId), Toast.LENGTH_LONG).show();
            }
        });
    }
    private void checkFilePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean readGranted = wordActivity.checkSelfPermission(PERMISSION_READ) == PackageManager.PERMISSION_GRANTED;
            boolean writeGranted = wordActivity.checkSelfPermission(PERMISSION_WRITE) == PackageManager.PERMISSION_GRANTED;

            if (!readGranted || !writeGranted ) {
                wordActivity.requestPermissions(new String[]{ PERMISSION_READ, PERMISSION_WRITE}, PERMISSION_REQUEST_CODE);
            }
        }
    }
}
