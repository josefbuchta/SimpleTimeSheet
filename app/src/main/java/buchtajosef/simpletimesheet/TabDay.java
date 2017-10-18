package buchtajosef.simpletimesheet;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Locale;


public class TabDay extends mFragment {

    private LinearLayout aTimeHeader;
    private ImageButton saveButton, addButton, deleteButton, nextButton, prevButton, homeButton;
    private NumberPicker.Formatter NPF;
    private NumberPicker hourIn, minuteIn, hourOut, minuteOut, hourCount, minuteCount;
    private ListView additionalListView;
    private TextView dayInfo;
    private EditText description;

    private final String[] _displayValuesH = getDisplayValues(0, 23);
    private final String[] _displayValuesM = getDisplayValues(0, 59);

    public static boolean savedState = false;
    int savedHourIn, savedMinuteIn, savedHourOut, savedMinuteOut, savedVisibilitySaveButton;
    String savedDescription;


    public TabDay() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            int year = savedInstanceState.getInt("year");
            int month = savedInstanceState.getInt("month");
            int day = savedInstanceState.getInt("day");
            DaysData dd = new DaysData(getContext(), null);
            dd.set(year, month, day);
            MainActivity.mSelectedDate.setDayAsSelected(dd);
            //fillView();
            savedState = true;
            savedHourIn = savedInstanceState.getInt("hourIn");
            savedMinuteIn = savedInstanceState.getInt("minuteIn");
            savedHourOut = savedInstanceState.getInt("hourOut");
            savedMinuteOut = savedInstanceState.getInt("minuteOut");
            savedDescription = savedInstanceState.getString("description");
            savedVisibilitySaveButton = savedInstanceState.getInt("save");
        } else {
            savedState = false;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        int year = MainActivity.mSelectedDate.getDayData().get(DaysData.YEAR);
        int month = MainActivity.mSelectedDate.getDayData().get(DaysData.MONTH);
        int day = MainActivity.mSelectedDate.getDayData().get(DaysData.DAY);
        outState.putInt("year",year);
        outState.putInt("month",month);
        outState.putInt("day",day);
        outState.putInt("hourIn",hourIn.getValue());
        outState.putInt("minuteIn",minuteIn.getValue());
        outState.putInt("hourOut",hourOut.getValue());
        outState.putInt("minuteOut",minuteOut.getValue());
        outState.putString("description",description.getText().toString());
        outState.putInt("save", saveButton.getVisibility());
        //Save the fragment's state here
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tab_day,container,false);
        setBackground(v);
        myTimeSheetDB =  new MySQLiteHelper(getActivity());

        ViewPagerAdapter.fragments.put(1,this);

        NPF = new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                return String.format(Locale.getDefault(), "%02d", value);
            }
        };
        aTimeHeader = (LinearLayout) v.findViewById(R.id.day_overview_addAdditionalTimeHeader);
        saveButton = (ImageButton) v.findViewById(R.id.day_overview_saveButton);
        addButton = (ImageButton) v.findViewById(R.id.day_overview_addAdditionalTime);
        deleteButton = (ImageButton) v.findViewById(R.id.day_overview_deleteButton);
        nextButton = (ImageButton) v.findViewById(R.id.day_overview_nextButton);
        prevButton = (ImageButton) v.findViewById(R.id.day_overview_prevButton);
        homeButton = (ImageButton) v.findViewById(R.id.day_overview_homeButton);
        hourIn = (NumberPicker) v.findViewById(R.id.day_overview_hourIn);
        hourOut = (NumberPicker) v.findViewById(R.id.day_overview_hourOut);
        minuteIn = (NumberPicker) v.findViewById(R.id.day_overview_minuteIn);
        minuteOut = (NumberPicker) v.findViewById(R.id.day_overview_minuteOut);
        hourCount = (NumberPicker) v.findViewById(R.id.day_overview_hourCount);
        minuteCount = (NumberPicker) v.findViewById(R.id.day_overview_minuteCount);
        additionalListView = (ListView) v.findViewById(R.id.day_overview_additionalRows);
        dayInfo = (TextView) v.findViewById(R.id.day_overview_day_info);
        description = (EditText) v.findViewById(R.id.day_overview_description_editText);
        description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                saveButton.setVisibility(View.VISIBLE);
            }
        });
        setupButtons();
        fillView();
        return v;
    }

    private void setupButtons () {
        assert saveButton != null;
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickSave();
            }
        });
        assert saveButton != null;
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickDelete();
            }
        });
        assert addButton != null;
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickAddAdditionalTime();
            }
        });
        assert nextButton != null;
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickNextDay();
            }
        });
        assert prevButton != null;
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickPrevDay();
            }
        });
        assert homeButton != null;
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickHomeButton();
            }
        });
    }

    private void addAdditionalTime () {
        DaysData selectedDay = MainActivity.mSelectedDate.getDayData();
        if (selectedDay.get(DaysData.ROWID) != 0 && selectedDay.get(DaysData.HOUR_OUT) != 0) {
            LayoutInflater li = LayoutInflater.from(this.getContext());
            View additionalTimeView = li.inflate(R.layout.additional_time,  new LinearLayout(this.getContext()), false);

            AlertDialog.Builder additionalTime = new AlertDialog.Builder(this.getContext());
            additionalTime.setView(additionalTimeView);

            final NumberPicker hourIn = (NumberPicker) additionalTimeView.findViewById(R.id.additionalTime_hourIn);
            hourIn.setDisplayedValues(_displayValuesH);
            hourIn.setMaxValue(_displayValuesH.length - 1);
            hourIn.setValue(Integer.parseInt(_displayValuesH[0]));

            final NumberPicker hourOut = (NumberPicker) additionalTimeView.findViewById(R.id.additionalTime_hourOut);
            hourOut.setDisplayedValues(_displayValuesH);
            hourOut.setMaxValue(_displayValuesH.length - 1);
            hourOut.setValue(Integer.parseInt(_displayValuesH[0]));

            final NumberPicker minuteIn = (NumberPicker) additionalTimeView.findViewById(R.id.additionalTime_minuteIn);
            minuteIn.setDisplayedValues(_displayValuesM);
            minuteIn.setMaxValue(_displayValuesM.length - 1);
            minuteIn.setValue(Integer.parseInt(_displayValuesM[0]));

            final NumberPicker minuteOut = (NumberPicker) additionalTimeView.findViewById(R.id.additionalTime_minuteOut);
            minuteOut.setDisplayedValues(_displayValuesM);
            minuteOut.setMaxValue(_displayValuesM.length - 1);
            minuteOut.setValue(Integer.parseInt(_displayValuesM[0]));

            final EditText description = (EditText) additionalTimeView.findViewById(R.id.additionalTime_description);

            additionalTime.setTitle(getString(R.string.dialog_title_addtional));

            additionalTime.setNegativeButton(
                    getString(R.string.dialog_button_cancel),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

            additionalTime.setPositiveButton(
                    getString(R.string.dialog_button_save),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int hIn = Integer.parseInt(_displayValuesH[hourIn.getValue()]);
                            int mIn = Integer.parseInt(_displayValuesM[minuteIn.getValue()]);
                            int hOut = Integer.parseInt(_displayValuesH[hourOut.getValue()]);
                            int mOut = Integer.parseInt(_displayValuesM[minuteOut.getValue()]);
                            myTimeSheetDB.insertAdditionalTime(MainActivity.mSelectedDate.getDayData().get(DaysData.ROWID), hIn, mIn,hOut,mOut, description.getText().toString());
                            MainActivity.mSelectedDate.reloadData();
                            fillView();
                        }
                    });

            additionalTime.show();
        }
    }

    public void onClickAddAdditionalTime () {
        addAdditionalTime();
    }

    public void onClickDelete () {
        deleteConfirmation();
    }

    public void onClickSave () {
        AlertDialog.Builder saveConfirm = new AlertDialog.Builder(this.getContext());
        saveConfirm.setMessage(getString(R.string.dialog_message_save));
        saveConfirm.setTitle(getString(R.string.dialog_title_save));
        saveConfirm.setNegativeButton(getString(R.string.dialog_button_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        saveConfirm.setPositiveButton(getString(R.string.dialog_button_save), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int hIn = Integer.parseInt(_displayValuesH[hourIn.getValue()]);
                int mIn = Integer.parseInt(_displayValuesM[minuteIn.getValue()]);
                int hOut = Integer.parseInt(_displayValuesH[hourOut.getValue()]);
                int mOut = Integer.parseInt(_displayValuesM[minuteOut.getValue()]);
                String desc = description.getText().toString();
                if (MainActivity.mSelectedDate.getDayData().get(DaysData.ROWID) != 0) {
                    myTimeSheetDB.updateTime(MainActivity.mSelectedDate.getDayData().get(DaysData.ROWID), hIn, mIn, hOut, mOut, desc);
                } else {
                    myTimeSheetDB.insertCompleteTime(
                            MainActivity.mSelectedDate.getDayData().get(DaysData.YEAR),
                            MainActivity.mSelectedDate.getDayData().get(DaysData.MONTH),
                            MainActivity.mSelectedDate.getDayData().get(DaysData.DAY),
                            hIn, mIn, hOut, mOut, desc);
                }
                MainActivity.mSelectedDate.reloadData();
                fillView();
            }
        });
        saveConfirm.show();
    }

    public void onClickNextDay () {
        MainActivity.mSelectedDate.setNextDay();
        fillView();
    }

    public void onClickPrevDay () {
        MainActivity.mSelectedDate.setPrevDay();
        fillView();
    }

    public void onClickHomeButton () {
        MainActivity.mSelectedDate.setToday();
        fillView();
    }

    public void editAdditionalTime (final int position) {
        ArrayList<DaysData> dd = MainActivity.mSelectedDate.getDayData().getAdditionalTimes();
        LayoutInflater li = LayoutInflater.from(this.getContext());
        View additionalTimeView = li.inflate(R.layout.additional_time, new LinearLayout(this.getContext()), false);

        AlertDialog.Builder additionalTime = new AlertDialog.Builder(this.getContext());
        additionalTime.setView(additionalTimeView);

        final NumberPicker hourIn = (NumberPicker) additionalTimeView.findViewById(R.id.additionalTime_hourIn);
        hourIn.setDisplayedValues(_displayValuesH);
        hourIn.setMaxValue(_displayValuesH.length - 1);
        hourIn.setValue(Integer.parseInt(_displayValuesH[dd.get(position).get(DaysData.HOUR_IN)]));

        final NumberPicker hourOut = (NumberPicker) additionalTimeView.findViewById(R.id.additionalTime_hourOut);
        hourOut.setDisplayedValues(_displayValuesH);
        hourOut.setMaxValue(_displayValuesH.length - 1);
        hourOut.setValue(Integer.parseInt(_displayValuesH[dd.get(position).get(DaysData.HOUR_OUT)]));


        final NumberPicker minuteIn = (NumberPicker) additionalTimeView.findViewById(R.id.additionalTime_minuteIn);
        minuteIn.setDisplayedValues(_displayValuesM);
        minuteIn.setMaxValue(_displayValuesM.length - 1);
        minuteIn.setValue(Integer.parseInt(_displayValuesM[dd.get(position).get(DaysData.MINUTE_IN)]));

        final NumberPicker minuteOut = (NumberPicker) additionalTimeView.findViewById(R.id.additionalTime_minuteOut);
        minuteOut.setDisplayedValues(_displayValuesM);
        minuteOut.setMaxValue(_displayValuesM.length - 1);
        minuteOut.setValue(Integer.parseInt(_displayValuesM[dd.get(position).get(DaysData.MINUTE_OUT)]));

        final EditText description = (EditText) additionalTimeView.findViewById(R.id.additionalTime_description);
        description.setText(dd.get(position).getString(DaysData.DESCRIPTION));

        additionalTime.setTitle(getString(R.string.dialog_title_addtional));

        additionalTime.setNeutralButton(
                getString(R.string.dialog_button_delete),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        deleteConfirmation(position);
                    }
                });

        additionalTime.setNegativeButton(
                getString(R.string.dialog_button_cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        additionalTime.setPositiveButton(
                getString(R.string.dialog_button_update),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        myTimeSheetDB.updateAdditionalTime(
                                MainActivity.mSelectedDate.getDayData().getAdditionalTimes().get(position).get(DaysData.ROWID),
                                Integer.parseInt(_displayValuesH[hourIn.getValue()]),
                                Integer.parseInt(_displayValuesM[minuteIn.getValue()]),
                                Integer.parseInt(_displayValuesH[hourOut.getValue()]),
                                Integer.parseInt(_displayValuesM[minuteOut.getValue()]),
                                description.getText().toString()
                        );
                        MainActivity.mSelectedDate.reloadData();
                        fillView();
                    }
                });

        additionalTime.show();
    }

    private class MyBaseAdapter extends BaseAdapter {
        private final Context mContext;
        private final ArrayList<DaysData> data;

        private MyBaseAdapter (Context context, ArrayList<DaysData> d){
            mContext = context;
            data = d;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView;

            if (convertView == null) {
                rowView = inflater.inflate(R.layout.additional_time_row,  new LinearLayout(getContext()), false);
                NumberPicker hourIn = (NumberPicker) rowView.findViewById(R.id.additionalTimeRow_hourIn);
                hourIn.setMinValue(0);
                hourIn.setMaxValue(23);
                hourIn.setFormatter(NPF);
                hourIn.setEnabled(false);
                setNumberPickerTextColor(hourIn, ContextCompat.getColor(getContext(), R.color.colorPrimary));
                NumberPicker minuteIn = (NumberPicker) rowView.findViewById(R.id.additionalTimeRow_minuteIn);
                minuteIn.setMinValue(0);
                minuteIn.setMaxValue(59);
                minuteIn.setFormatter(NPF);
                minuteIn.setEnabled(false);
                setNumberPickerTextColor(minuteIn, ContextCompat.getColor(getContext(), R.color.colorPrimary));
                NumberPicker hourOut = (NumberPicker) rowView.findViewById(R.id.additionalTimeRow_hourOut);
                hourOut.setMinValue(0);
                hourOut.setMaxValue(23);
                hourOut.setFormatter(NPF);
                hourOut.setEnabled(false);
                setNumberPickerTextColor(hourOut, ContextCompat.getColor(getContext(), R.color.colorPrimary));
                NumberPicker minuteOut = (NumberPicker) rowView.findViewById(R.id.additionalTimeRow_minuteOut);
                minuteOut.setMinValue(0);
                minuteOut.setMaxValue(59);
                minuteOut.setFormatter(NPF);
                minuteOut.setEnabled(false);
                setNumberPickerTextColor(minuteOut, ContextCompat.getColor(getContext(), R.color.colorPrimary));

                hourIn.setValue(data.get(position).get(DaysData.HOUR_IN));
                minuteIn.setValue(data.get(position).get(DaysData.MINUTE_IN));
                hourOut.setValue(data.get(position).get(DaysData.HOUR_OUT));
                minuteOut.setValue(data.get(position).get(DaysData.MINUTE_OUT));

                TextView description = (TextView) rowView.findViewById(R.id.additionalTimeRow_description);
                description.setText(data.get(position).getString(DaysData.DESCRIPTION));

            } else {
                rowView = convertView;
            }

            return rowView;
        }
    }

    public void fillView () {
        DaysData selectedDay = MainActivity.mSelectedDate.getDayData();
        String info = MainActivity.mSelectedDate.getDaysName() + ", " +
                MainActivity.mSelectedDate.getDayData().getString(DaysData.DAY) + ". " +
                MainActivity.mSelectedDate.getMonthName() + " " +
                MainActivity.mSelectedDate.getDayData().getString(DaysData.YEAR);
        dayInfo.setText(info);

        hourIn.setDisplayedValues(_displayValuesH);
        hourIn.setMaxValue(_displayValuesH.length - 1);
        hourIn.setValue(Integer.parseInt(_displayValuesH[selectedDay.get(DaysData.HOUR_IN)]));
        hourIn.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                saveButton.setVisibility(View.VISIBLE);
            }
        });

        minuteIn.setDisplayedValues(_displayValuesM);
        minuteIn.setMaxValue(_displayValuesM.length - 1);
        minuteIn.setValue(Integer.parseInt(_displayValuesM[selectedDay.get(DaysData.MINUTE_IN)]));
        minuteIn.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                saveButton.setVisibility(View.VISIBLE);
            }
        });

        hourOut.setDisplayedValues(_displayValuesH);
        hourOut.setMaxValue(_displayValuesH.length - 1);
        hourOut.setValue(Integer.parseInt(_displayValuesH[selectedDay.get(DaysData.HOUR_OUT)]));
        hourOut.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                saveButton.setVisibility(View.VISIBLE);
            }
        });

        minuteOut.setDisplayedValues(_displayValuesM);
        minuteOut.setMaxValue(_displayValuesM.length - 1);
        minuteOut.setValue(Integer.parseInt(_displayValuesM[selectedDay.get(DaysData.MINUTE_OUT)]));
        minuteOut.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                saveButton.setVisibility(View.VISIBLE);
            }
        });

        hourCount.setMinValue(0);
        hourCount.setMaxValue(59);
        hourCount.setFormatter(NPF);
        hourCount.setEnabled(false);
        setNumberPickerTextColor(hourCount, ContextCompat.getColor(getContext(), R.color.colorPrimary));
        minuteCount.setMinValue(0);
        minuteCount.setMaxValue(59);
        minuteCount.setFormatter(NPF);
        minuteCount.setEnabled(false);
        setNumberPickerTextColor(minuteCount, ContextCompat.getColor(getContext(), R.color.colorPrimary));
        if (selectedDay.get(DaysData.HOUR_OUT) != 0) {
            int h = selectedDay.get(DaysData.HOUR_OUT) - selectedDay.get(DaysData.HOUR_IN);
            int m = selectedDay.get(DaysData.MINUTE_OUT) - selectedDay.get(DaysData.MINUTE_IN);
            if (selectedDay.hasMoreTimes()) {
                ArrayList<DaysData> ddList = selectedDay.getAdditionalTimes();
                for (int i = 0; i < ddList.size(); i++) {
                    h += ddList.get(i).get(DaysData.HOUR_OUT) - ddList.get(i).get(DaysData.HOUR_IN);
                    m += ddList.get(i).get(DaysData.MINUTE_OUT) - ddList.get(i).get(DaysData.MINUTE_IN);
                }
            }
            int count = ((h * 60) + m);
            hourCount.setValue(count / 60);
            minuteCount.setValue(count % 60);
        } else {
            hourCount.setValue(0);
            minuteCount.setValue(0);
        }

        description.setText(selectedDay.getString(DaysData.DESCRIPTION));

        if (selectedDay.hasMoreTimes()) {
            aTimeHeader.setVisibility(View.VISIBLE);
            ArrayList<DaysData> aTimes = selectedDay.getAdditionalTimes();
            MyBaseAdapter adapter = new MyBaseAdapter(this.getContext(), aTimes);
            additionalListView.setAdapter(adapter);
            additionalListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //Toast.makeText(DayOverview.this, "Clicked On "+ String.valueOf(position), Toast.LENGTH_SHORT).show();
                    editAdditionalTime(position);
                }
            });
        } else {
            aTimeHeader.setVisibility(View.INVISIBLE);
            ArrayList<DaysData> aTimes = new ArrayList<>();
            MyBaseAdapter adapter = new MyBaseAdapter(this.getContext(), aTimes);
            additionalListView.setAdapter(adapter);
        }
        addButton.setVisibility(View.INVISIBLE);
        deleteButton.setVisibility(View.INVISIBLE);
        if (selectedDay.get(DaysData.HOUR_IN) != 0) {
            if (selectedDay.get(DaysData.HOUR_OUT) != 0) {
                addButton.setVisibility(View.VISIBLE);
            }
            deleteButton.setVisibility(View.VISIBLE);
        }
        saveButton.setVisibility(View.INVISIBLE);

        if (savedState) {
            savedState = false;
            hourIn.setValue(savedHourIn);
            minuteIn.setValue(savedMinuteIn);
            hourOut.setValue(savedHourOut);
            minuteOut.setValue(savedMinuteOut);
            description.setText(savedDescription);
            switch (savedVisibilitySaveButton) {
                case View.VISIBLE:
                    saveButton.setVisibility(View.VISIBLE);
                    break;
                case View.INVISIBLE:
                    saveButton.setVisibility(View.INVISIBLE);
                    break;
                case View.GONE:
                    saveButton.setVisibility(View.GONE);
                    break;
            }
        }
    }

    private void deleteConfirmation (final int position) {
        AlertDialog.Builder deleteConfirm = new AlertDialog.Builder(this.getContext());
        deleteConfirm.setMessage(getString(R.string.dialog_message_delete_additional));
        deleteConfirm.setTitle(getString(R.string.dialog_title_delete));
        deleteConfirm.setNegativeButton(getString(R.string.dialog_button_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                editAdditionalTime(position);
            }
        });
        deleteConfirm.setPositiveButton(getString(R.string.dialog_button_delete), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                myTimeSheetDB.deleteAdditionalTime(MainActivity.mSelectedDate.getDayData().getAdditionalTimes().get(position).get(DaysData.ROWID));
                MainActivity.mSelectedDate.reloadData();
                fillView();
            }
        });
        deleteConfirm.show();
    }

    private void deleteConfirmation () {
        AlertDialog.Builder deleteConfirm = new AlertDialog.Builder(this.getContext());
        deleteConfirm.setMessage(getString(R.string.dialog_message_delete_day));
        deleteConfirm.setTitle(getString(R.string.dialog_title_delete));
        deleteConfirm.setNegativeButton(getString(R.string.dialog_button_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        deleteConfirm.setPositiveButton(getString(R.string.dialog_button_delete), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                myTimeSheetDB.deleteDay(MainActivity.mSelectedDate.getDayData().get(DaysData.ROWID));
                MainActivity.mSelectedDate.reloadData();
                fillView();
            }
        });
        deleteConfirm.show();
    }

    public String[] getDisplayValues(int minimumInclusive, int maximumInclusive) {
        ArrayList<String> result = new ArrayList<>();

        //result.add("0");
        for(int i = maximumInclusive; i >= minimumInclusive; i--) {
            if (i < 10) {
                result.add("0" + Integer.toString(i));
            } else {
                result.add(Integer.toString(i));
            }
        }

        return result.toArray(new String[0]);
    }

    private boolean setNumberPickerTextColor(NumberPicker numberPicker, int color) {
        final int count = numberPicker.getChildCount();
        for(int i = 0; i < count; i++){
            View child = numberPicker.getChildAt(i);
            if(child instanceof EditText){
                try{
                    Field selectorWheelPaintField = numberPicker.getClass()
                            .getDeclaredField("mSelectorWheelPaint");
                    selectorWheelPaintField.setAccessible(true);
                    ((Paint)selectorWheelPaintField.get(numberPicker)).setColor(color);
                    ((EditText)child).setTextColor(color);
                    numberPicker.invalidate();
                    return true;
                }
                catch(Exception e){
                    Log.w("NumberPickerTextColor", e);
                }
            }
        }
        return false;
    }
}
