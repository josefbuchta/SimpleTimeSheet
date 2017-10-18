package buchtajosef.simpletimesheet;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;


public class TabClock extends mFragment {

    private TextView timeIn, timeOut, dayInfo;
    private Button punchButton;

    public TabClock() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.fragment_tab_clock,container,false);
        setBackground(v);
        myTimeSheetDB =  new MySQLiteHelper(getActivity());

        ViewPagerAdapter.fragments.put(0,this);

        punchButton = (Button) v.findViewById(R.id.tab_clock_button_punch);
        assert punchButton != null;
        punchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickPunchButton(v);
            }
        });

        timeIn = (TextView) v.findViewById(R.id.timeSheet_timeIn);
        timeOut = (TextView) v.findViewById(R.id.timeSheet_timeOut);
        dayInfo = (TextView) v.findViewById(R.id.timeSheet_day_info);
        fillView();
        return v;
    }

    public void onClickPunchButton (View v) {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        //myTimeSheetDB.deleteMonth(2017,1);
        DaysData todaysData = MainActivity.mSelectedDate.getTodaysData();
        if (todaysData == null || todaysData.get(DaysData.ROWID) == 0) {
            myTimeSheetDB.insertTime(year, month, day, hour, minute);
        } else {
            myTimeSheetDB.insertOutTime(todaysData.get(DaysData.ROWID), hour, minute);
            v.setEnabled(false);
        }
        MainActivity.mSelectedDate.reloadData();
        fillView();
    }

    public void fillView () {
        DaysData todaysData = MainActivity.mSelectedDate.getTodaysData();

        String info = MainActivity.mSelectedDate.getTodaysDayName() + ", " +
                MainActivity.mSelectedDate.getTodaysData().getString(DaysData.DAY) + ". " +
                MainActivity.mSelectedDate.getTodaysMonthName() + " " +
                MainActivity.mSelectedDate.getTodaysData().getString(DaysData.YEAR);
        dayInfo.setText(info);

        String sTimeIn = "";
        String sTimeOut = "";
        if (todaysData != null && todaysData.get(DaysData.ROWID) != 0) {
            sTimeIn = todaysData.getString(DaysData.HOUR_IN) + ":" + todaysData.getString(DaysData.MINUTE_IN);
            timeIn.setTextColor(ContextCompat.getColor(getContext(), R.color.colorTextTimeSet));

            punchButton.setText(getString(R.string.layout_button_punch_out));
            sTimeOut = todaysData.getString(DaysData.HOUR_OUT) + ":" + todaysData.getString(DaysData.MINUTE_OUT);
            if (todaysData.get(DaysData.HOUR_OUT) == 0 && todaysData.get(DaysData.MINUTE_OUT) == 0) {
                punchButton.setEnabled(true);
                timeOut.setTextColor(ContextCompat.getColor(getContext(), R.color.colorTextTimeNotSet));
            } else {
                punchButton.setEnabled(false);
                timeOut.setTextColor(ContextCompat.getColor(getContext(), R.color.colorTextTimeSet));
            }
        } else {
            punchButton.setEnabled(true);
            punchButton.setText(getString(R.string.layout_button_punch_in));
        }
        timeIn.setText(sTimeIn);
        timeOut.setText(sTimeOut);
    }
}
