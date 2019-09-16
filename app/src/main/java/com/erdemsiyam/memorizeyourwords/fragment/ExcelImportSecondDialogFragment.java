package com.erdemsiyam.memorizeyourwords.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.appcompat.widget.AppCompatEditText;
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
import java.io.InputStream;
import java.text.SimpleDateFormat;

public class ExcelImportSecondDialogFragment extends AppCompatDialogFragment {

    /* A Pop-up show up for "Excel Words Import" when select ".xlsx" file path at "ExcelImportFirstDialog".
       This Pop-up is "DialogFragment".
       This is the second and last "DialogFragment" for ExcelImport.
       User selects "StrangeColumnIndex","ExplainColumnIndex","SheetIndex".
       Then after opens "SecondDialogFragment" is "ExcelImportSecondDialogFragment". */

    /* Using this library for excel import utils. : https://github.com/andruhon/android5xlsx */

    /* Constants. */
    public static final String TAG = "df_excel_import_second";

    /* Variables.*/
    private WordActivity     wordActivity;
    private ExcelImportModel excelImportModel;

    /* UI components. */
    private AppCompatEditText txtExcelImpStrangeColumnIndex;
    private AppCompatEditText txtExcelImpExplainColumnIndex;
    private AppCompatEditText txtExcelImpSheetIndex;
    private Button            btnExcelImportWords;

    /* Constructor. */
    public ExcelImportSecondDialogFragment(WordActivity wordActivity, ExcelImportModel excelImportModel) {
        this.wordActivity = wordActivity;
        this.excelImportModel = excelImportModel;
    }

    /*  Override method of AppCompatDialogFragment.
        Creating Our CustomizeDialog. */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        /* "AlertDialog" Design will be used to our "CustomizeDialog". */
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        /* Including layout of DialogFragment's. */
        View view = inflater.inflate(R.layout.dialog_excel_import_second,null);

        /* UI components are installed. */
        txtExcelImpStrangeColumnIndex = view.findViewById(R.id.txtExcelImpStrangeColumnIndex);
        txtExcelImpExplainColumnIndex = view.findViewById(R.id.txtExcelImpExplainColumnIndex);
        txtExcelImpSheetIndex = view.findViewById(R.id.txtExcelImpSheetIndex);
        btnExcelImportWords = view.findViewById(R.id.btnExcelImportWords);

        /* "AlertDialog" building. */
        builder.setView(view);
        builder.setTitle(R.string.excelimport_second_alert_title);

        /* Giving listener to UI component Button.. */
        btnExcelImportWords.setOnClickListener(v -> {

            /* Send message words loading. */
            toastMessage(R.string.excelimport_message_loading);

            /* Get selected values from UI. */
            excelImportModel.setStrangeCellIndex(Integer.valueOf(txtExcelImpStrangeColumnIndex.getText().toString()));
            excelImportModel.setExplainCellIndex(Integer.valueOf(txtExcelImpExplainColumnIndex.getText().toString()));
            excelImportModel.setSelectedSheetIndex(Integer.valueOf(txtExcelImpSheetIndex.getText().toString()));

            /* Get read words from excel. */
            readExcelData();
        });

        return builder.create();  // Prepared Customize "AlertDialog" return.
    }

    private void readExcelData() {
        /* Words to DB from Excel. */

        try {
            File inputFile = new File(excelImportModel.getPathExcel()); // Read excel path.
            InputStream inputStream = new FileInputStream(inputFile);
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);

            /* Get sheet index from model. */
            if( excelImportModel.getSelectedSheetIndex() > workbook.getNumberOfSheets() ) { // Selected sheet index much more total sheet count, then send message and stop.
                toastMessage(R.string.excelimport_message_no_sheet_index);
                return;
            }
            XSSFSheet sheet = workbook.getSheetAt(excelImportModel.getSelectedSheetIndex()-1);

            /* Counter of added words.*/
            int addedWordCount = 0;

            /* Loop the rows.*/
            int rowsCount = sheet.getLastRowNum();
            FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
            for (int r = 0; r <= rowsCount; r++) { // Loop each row.
                Row row = sheet.getRow(r);

                /* Get words. */
                String strange = getCellAsString(row, excelImportModel.getStrangeCellIndex()-1, formulaEvaluator).trim();
                String explain = getCellAsString(row, excelImportModel.getExplainCellIndex()-1, formulaEvaluator).trim();

                /* Control the, Is words right? */
                if(strange.equals("") || explain.equals("")) { // Pass this row if empty.
                    continue;
                }
                if(strange.length()>30) strange = strange.substring(0,29); // Clip word if it is long.
                if(explain.length()>30) explain = explain.substring(0,29); // Clip word if it is long.

                /* Add words to DB. */
                WordService.addWord(wordActivity,excelImportModel.getCategoryId(),strange,explain);
                ++addedWordCount; // Increase one word added.
            }

            /* Warning user if added word count is 0. */
            if(addedWordCount == 0){
                toastMessage(R.string.excelimport_message_no_word);
                return;
            }
            /* Send message its done. */
            toastMessage(R.string.excelimport_message_loaded,addedWordCount);

            /* Refresh word list at "WordActivity". */
            wordActivity.refreshRecyclerView();

            /* Close this Dialog. */
            dismiss();
        }
        catch (Exception e) { }
    }
    private String getCellAsString(Row row, int c, FormulaEvaluator formulaEvaluator) {
        /* Cell conversion to String. */
        try {
            Cell cell = row.getCell(c);
            CellValue cellValue = formulaEvaluator.evaluate(cell);
            switch (cellValue.getCellType()) {
                case Cell.CELL_TYPE_BOOLEAN:
                    return ""+cellValue.getBooleanValue();
                case Cell.CELL_TYPE_NUMERIC:
                    double numericValue = cellValue.getNumberValue();
                    if(HSSFDateUtil.isCellDateFormatted(cell)) {
                        double date = cellValue.getNumberValue();
                        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy");
                        return formatter.format(HSSFDateUtil.getJavaDate(date));
                    }
                    else
                        return ""+numericValue;
                case Cell.CELL_TYPE_STRING:
                    return ""+cellValue.getStringValue();
            }
        }
        catch (Exception e) { }
        return "";
    }

    private void toastMessage(@StringRes int resId) {
        wordActivity.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(wordActivity, wordActivity.getResources().getString(resId), Toast.LENGTH_LONG).show();
            }
        });
    }
    private void toastMessage(@StringRes int resId, int count) {
        wordActivity.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(wordActivity, wordActivity.getResources().getString(resId)+" "+ count, Toast.LENGTH_LONG).show();
            }
        });
    }
}
