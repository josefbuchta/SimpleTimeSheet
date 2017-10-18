package buchtajosef.simpletimesheet;

import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ibm.icu.text.DateFormatSymbols;
import com.ibm.icu.text.SimpleDateFormat;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class ExportActivity extends AppCompatActivity {

    private ArrayList<TextView> years = new ArrayList<>();
    private ArrayList<TextView> yearsMonthsCount = new ArrayList<>();
    private ArrayList<ImageView> yearsBox = new ArrayList<>();
    private ArrayList<ImageView> yearsChecked = new ArrayList<>();
    private ArrayList<ImageView> yearsPartial = new ArrayList<>();
    private ArrayList<LinearLayout> yearsMonths = new ArrayList<>();

    private ArrayList<ArrayList<TextView>> monthsText = new ArrayList<>();
    private ArrayList<ArrayList<ImageView>> monthsChecked = new ArrayList<>();

    private Button exportButton;

    MySQLiteHelper myTimeSheetDB;
    ArrayList<String> globalMonthNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);

        exportButton = (Button) findViewById(R.id.activity_export_button);
        View v = findViewById(R.id.activity_export);
        setBackground(v);
        fillData(v);
        setOnClickListeners();
    }

    private void setBackground (View v) {
        ShapeDrawable.ShaderFactory shaderFactory = new ShapeDrawable.ShaderFactory() {
            @Override
            public Shader resize(int width, int height) {
                float x = 1.7f;
                return new RadialGradient(width/2,(height/x),900.0f,
                        new int[]{
                                ContextCompat.getColor(getApplicationContext(), R.color.colorAccent),// Color.parseColor("#06b1dd"),
                                ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary)},//Color.parseColor("#03417a")},
                        new float[]{
                                0, 1f},
                        Shader.TileMode.CLAMP);
            }
        };
        PaintDrawable paint = new PaintDrawable();
        paint.setShape(new RectShape());
        paint.setShaderFactory(shaderFactory);
        v.setBackground(paint);
    }

    private void fillData (View v) {
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] gmn = dfs.getMonths(DateFormatSymbols.STANDALONE, DateFormatSymbols.WIDE);
        for (String word : gmn) {
            globalMonthNames.add(word.substring(0,1).toUpperCase() + word.substring(1));
        }

        LayoutInflater inflater = LayoutInflater.from(this);//(LayoutInflater) getSystemService(getApplicationContext().LAYOUT_INFLATER_SERVICE);
        myTimeSheetDB =  new MySQLiteHelper(this);
        ArrayList<Integer> y = myTimeSheetDB.getYearsInDB();

        LinearLayout scroll = (LinearLayout) v.findViewById(R.id.export_scroll_view);

        for (Integer i : y) {
            TextView tvY;
            TextView tvC;
            ImageView ivY;
            ImageView ivY2;
            ImageView ivY3;
            LinearLayout llY;

            View yearView = inflater.inflate(R.layout.export_year, (ViewGroup) v, false);//scroll, true);//(ViewGroup) v, false);
            tvY =(TextView) yearView.findViewById(R.id.export_year);
            tvY.setText(String.valueOf(i));
            years.add (tvY);
            tvC =(TextView) yearView.findViewById(R.id.export_month_count);
            tvC.setText("0");
            yearsMonthsCount.add (tvC);
            ivY = (ImageView) yearView.findViewById(R.id.export_year_box_checked);
            yearsChecked.add(ivY);
            ivY2 = (ImageView) yearView.findViewById(R.id.export_year_box_partial);
            yearsPartial.add(ivY2);
            ivY3 = (ImageView) yearView.findViewById(R.id.export_year_box);
            yearsBox.add(ivY3);
            llY = (LinearLayout) yearView.findViewById(R.id.export_months);
            yearsMonths.add(llY);

            ArrayList<Integer> m = myTimeSheetDB.getMonthsInYear(i);
            ArrayList<TextView> mt = new ArrayList<>();
            ArrayList<ImageView> mc = new ArrayList<>();
            for(Integer x = 0; x < 12; x++) {
                String IDivMonth = "export_month_" + (x+1);

                int resID = getResources().getIdentifier(IDivMonth, "id", getPackageName());
                TextView tvM = (TextView) yearView.findViewById(resID);
                tvM.setText(globalMonthNames.get(x));

                resID = getResources().getIdentifier(IDivMonth+"_checked", "id", getPackageName());
                ImageView ivM = (ImageView) yearView.findViewById(resID);

                if (m.contains(x)) {
                    tvM.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
                    ((RelativeLayout) tvM.getParent()).setBackgroundColor(ContextCompat.getColor(this, R.color.colorCalendarDay));

                    resID = getResources().getIdentifier(IDivMonth + "_box", "id", getPackageName());
                    ((ImageView) yearView.findViewById(resID)).setColorFilter(ContextCompat.getColor(this, R.color.colorPrimaryDark));

                    mt.add(tvM);
                    mc.add(ivM);
                }
            }
            monthsText.add(mt);
            monthsChecked.add(mc);
            //LinearLayout mainLayout = (LinearLayout) v;
            //mainLayout.addView(yearView);
            scroll.addView(yearView);
            exportButton.setEnabled(true);
        }
    }

    private void setOnClickListeners () {
        //Listeners for click on years TextVies
        for (TextView tv : years) {
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = years.indexOf(view);
                    boolean visible = yearsMonths.get(position).getVisibility() == View.VISIBLE;

                    /*for (LinearLayout ll : yearsMonths) {
                        ll.setVisibility(View.GONE);
                    }*/
                    if (!visible) {
                        yearsMonths.get(position).setVisibility(View.VISIBLE);
                    } else {
                        yearsMonths.get(position).setVisibility(View.GONE);
                    }
                    setMonthCount(position);
                }
            });
        }

        //Listeners for checkboxes beside years
        for (ImageView box : yearsBox) {
            box.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = yearsBox.indexOf(view);
                    ArrayList<ImageView> monthCheckboxes = monthsChecked.get(position);
                    boolean conditionSet = false;
                    if (yearsChecked.get(position).getVisibility() == View.VISIBLE) {
                        yearsChecked.get(position).setVisibility(View.INVISIBLE);
                        for (ImageView iv : monthCheckboxes) {
                            iv.setVisibility(View.INVISIBLE);
                        }
                        conditionSet = true;
                    }
                    if (yearsPartial.get(position).getVisibility() == View.VISIBLE || !conditionSet) {
                        yearsPartial.get(position).setVisibility(View.INVISIBLE);
                        yearsChecked.get(position).setVisibility(View.VISIBLE);
                        for (ImageView iv : monthCheckboxes) {
                            iv.setVisibility(View.VISIBLE);
                        }
                    }
                    setMonthCount(position);
                }
            });
        }

        for (int i = 0; i < years.size(); i++) {
            ArrayList<TextView> months = monthsText.get(i);
            for (TextView tv : months) {
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int index = 0;
                        int index2 = 0;
                        for (int i = 0; i < years.size(); i++) {
                            index = i;
                            index2 = monthsText.get(i).indexOf(view);
                            if (index2 != -1) {
                                i = years.size();
                            }
                        }
                        if (monthsChecked.get(index).get(index2).getVisibility() == View.VISIBLE) {
                            monthsChecked.get(index).get(index2).setVisibility(View.INVISIBLE);
                        } else {
                            monthsChecked.get(index).get(index2).setVisibility(View.VISIBLE);
                        }
                        boolean allSelected = true;
                        boolean oneSelected = false;
                        for (ImageView iv : monthsChecked.get(index)) {
                            if (iv.getVisibility() == View.INVISIBLE) {
                                allSelected = false;
                            } else {
                                oneSelected = true;
                            }
                        }
                        if (allSelected) {
                            yearsChecked.get(index).setVisibility(View.VISIBLE);
                            yearsPartial.get(index).setVisibility(View.INVISIBLE);
                        } else if (oneSelected) {
                            yearsChecked.get(index).setVisibility(View.INVISIBLE);
                            yearsPartial.get(index).setVisibility(View.VISIBLE);
                        } else {
                            yearsChecked.get(index).setVisibility(View.INVISIBLE);
                            yearsPartial.get(index).setVisibility(View.INVISIBLE);
                        }
                        setMonthCount(index);
                    }
                });
            }
        }
    }

    void setMonthCount (int indexOfYear) {
        int count = 0;
        for (ImageView iv : monthsChecked.get(indexOfYear)) {
            if (iv.getVisibility() == View.VISIBLE) {
                count++;
            }
        }
        yearsMonthsCount.get(indexOfYear).setText(String.valueOf(count));
    }

    public void onClickExport (View v) {
        ArrayList<ArrayList<DaysData>> data = new ArrayList<>();
        for (int y = 0; y < years.size(); y++) {
            int year = Integer.valueOf(years.get(y).getText().toString());
            for (int i = 0; i < monthsChecked.get(y).size(); i ++) {
                if (monthsChecked.get(y).get(i).getVisibility() == View.VISIBLE) {
                    String monthName = monthsText.get(y).get(i).getText().toString();
                    int month = globalMonthNames.indexOf(monthName);
                    Cursor resultSet = myTimeSheetDB.getMonthData(year, month);
                    ArrayList<DaysData> monthData = new ArrayList<>();
                    if (resultSet.moveToFirst()) {
                        do {
                            DaysData dd = new DaysData(getApplicationContext(), resultSet);
                            monthData.add(dd);
                        } while (resultSet.moveToNext());

                        Calendar c = Calendar.getInstance();
                        c.set(monthData.get(0).get(DaysData.YEAR), monthData.get(0).get(DaysData.YEAR),monthData.get(0).get(DaysData.DAY));
                        int maxDay = c.getActualMaximum(Calendar.DAY_OF_MONTH);
                        for (int d = 1; d<= maxDay; d++) {
                            if ( d > monthData.size() || monthData.get(d-1).get(DaysData.DAY) != d ) {
                                DaysData dd = new DaysData(getApplicationContext(), null);
                                dd.set(monthData.get(0).get(Calendar.YEAR), monthData.get(0).get(Calendar.MONTH), d);
                                monthData.add(d-1,dd);
                            }
                        }
                    }
                    data.add(monthData);
                }
            }
        }

        if (data.size() > 0) {
            exportToXlsx(data);
        } else {
            AlertDialog.Builder noData = new AlertDialog.Builder(this);
            noData.setTitle(getString(R.string.dialog_title_error));
            noData.setMessage(getString(R.string.dialog_message_export_nodata));
            noData.setPositiveButton(
                    getString(R.string.dialog_button_ok),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            noData.show();
        }
    }

    private void exportToXlsx (ArrayList<ArrayList<DaysData>> data) {
        try {
            Calendar mCalendar = Calendar.getInstance();
            Workbook wb = new HSSFWorkbook();
            for (int x = 0; x < data.size(); x++) {
                ArrayList<DaysData> mDaysData = data.get(x);
                DaysData day = mDaysData.get(0);
                mCalendar.set(day.get(DaysData.YEAR), day.get(DaysData.MONTH), 1);
                DateFormatSymbols dfs = new DateFormatSymbols();
                String[] dayNames = dfs.getShortWeekdays();
                for (int i = 1; i < dayNames.length; i++) {
                    dayNames[i] = dayNames[i].substring(0, 1).toUpperCase() + dayNames[i].substring(1);
                }
                String[] monthNames = dfs.getMonths(DateFormatSymbols.STANDALONE, DateFormatSymbols.WIDE);
                for (int i = 0; i < monthNames.length; i++) {
                    monthNames[i] = monthNames[i].substring(0,1).toUpperCase() + monthNames[i].substring(1);
                }
                Sheet sheet = wb.createSheet(day.getString(DaysData.YEAR)+"-"+monthNames[day.get(DaysData.MONTH)]);
                setStyles(wb, sheet);

                Row dataRow;
                for (int i = 0; i < mDaysData.size(); i++) {
                    day = mDaysData.get(i);
                    mCalendar.set(Calendar.DAY_OF_MONTH, day.get(DaysData.DAY));
                    dataRow = sheet.createRow(i);
                    dataRow.createCell(0).setCellValue(dayNames[mCalendar.get(Calendar.DAY_OF_WEEK)]);
                    dataRow.createCell(1).setCellValue(day.get(DaysData.DAY));

                    if (day.get(DaysData.ROWID) != 0) {
                        dataRow.createCell(2).setCellValue(day.get(DaysData.HOUR_IN));
                        dataRow.createCell(3).setCellValue(":");
                        dataRow.createCell(4).setCellValue(day.get(DaysData.MINUTE_IN));
                        dataRow.createCell(5).setCellValue(day.get(DaysData.HOUR_OUT));
                        dataRow.createCell(6).setCellValue(":");
                        dataRow.createCell(7).setCellValue(day.get(DaysData.MINUTE_OUT));
                        dataRow.createCell(8).setCellValue(day.getString(DaysData.DESCRIPTION));
                        if (day.hasMoreTimes()) {
                            ArrayList<DaysData> additionalTimes = day.getAdditionalTimes();
                            int startColumn = 2;
                            int numberOfcolumns = 7;
                            for (int a = 1; a <= additionalTimes.size(); a++) {
                                int start = startColumn + (numberOfcolumns * a);
                                dataRow.createCell(start).setCellValue(additionalTimes.get(a-1).get(DaysData.HOUR_IN));
                                dataRow.createCell(start+1).setCellValue(":");
                                dataRow.createCell(start+2).setCellValue(additionalTimes.get(a-1).get(DaysData.MINUTE_IN));
                                dataRow.createCell(start+3).setCellValue(additionalTimes.get(a-1).get(DaysData.HOUR_OUT));
                                dataRow.createCell(start+4).setCellValue(":");
                                dataRow.createCell(start+5).setCellValue(additionalTimes.get(a-1).get(DaysData.MINUTE_OUT));
                                dataRow.createCell(start+6).setCellValue(additionalTimes.get(a-1).getString(DaysData.DESCRIPTION));
                            }
                        }

                    } else {
                        dataRow.createCell(2).setCellValue("");
                        dataRow.createCell(3).setCellValue("");
                        dataRow.createCell(4).setCellValue("");
                        dataRow.createCell(5).setCellValue("");
                        dataRow.createCell(6).setCellValue("");
                        dataRow.createCell(7).setCellValue("");
                    }

                }

                if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                    String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getPath();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                    String currentDateandTime = sdf.format(new Date());
                    path += "/Simple TimeSheet";//"/"+ currentDateandTime +".xlsx";
                    File file = new File(path+"/"+ currentDateandTime +".xlsx");
                    File dir = new File(path);
                    //File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "timesheet.xlsx");
                    boolean directoryOK = false;
                    if (dir.exists()) {
                        directoryOK = true;
                    } else {
                        if (dir.mkdir()) directoryOK = true;
                    }

                    if (directoryOK) {
                        boolean noFile = true;
                        if (file.exists()) noFile = file.delete();
                        if (noFile) {
                            FileOutputStream os = new FileOutputStream(file);
                            wb.write(os);
                            Toast.makeText(getApplicationContext(), getString(R.string.export_file_created), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), getString(R.string.export_cannot_delete), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.dir_failed), Toast.LENGTH_SHORT).show();
                    }
                }
            }

        } catch (Exception e) {
            e.getMessage();
        }
    }

    private void setStyles (Workbook wb, Sheet sheet) {
        Map<String, CellStyle> styles = createStyles(wb);
        int characterWidth = 256;

        sheet.setDefaultColumnWidth(3);
        sheet.setDefaultColumnStyle (0, styles.get("day"));
        sheet.setDefaultColumnStyle(1, styles.get("day"));
        sheet.setColumnWidth(0, characterWidth * 3);
        sheet.setColumnWidth(1, characterWidth * 3);

        int startColumn = 2;
        int numberOfcolumns = 7;
        for (int i = 0; i < 5; i++) {
            int start = startColumn + (numberOfcolumns * i);
            sheet.setDefaultColumnStyle(start + 1, styles.get("separator"));//3
            sheet.setDefaultColumnStyle(start + 2, styles.get("minute"));   //4
            sheet.setDefaultColumnStyle(start + 4, styles.get("separator"));//6
            sheet.setDefaultColumnStyle(start + 5, styles.get("minute"));   //7
            sheet.setDefaultColumnStyle(start + 6, styles.get("text"));     //8
            sheet.setColumnWidth(start + 1, characterWidth);                //3
            sheet.setColumnWidth(start + 2, characterWidth * 3);            //4
            sheet.setColumnWidth(start + 4, characterWidth);                //6
            sheet.setColumnWidth(start + 5, characterWidth * 3);            //7
            sheet.setColumnWidth(start + 6, characterWidth * 15);           //8
        }
    }

    private Map<String, CellStyle> createStyles (Workbook wb) {
        Map<String, CellStyle> styles = new HashMap<>();
        CellStyle style;

        style = wb.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_LEFT);
        styles.put("minute", style);

        style = wb.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_CENTER);
        styles.put("day", style);

        style = wb.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setWrapText(true);
        styles.put("separator", style);

        style = wb.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_LEFT);
        style.setWrapText(true);
        styles.put("text", style);

        return styles;
    }
}
