package com.hornet.horizontalscrolldatepicker;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Gallery;

import java.util.Calendar;

/**
 * Created by Ahmed on 06.01.14.
 */
public class HorizontalDateFilter extends Gallery {

// #MARK - Constants

    // default initialize params
    public static final int COLOR_ORANGE = Color.parseColor("#FF6600");
    public static final int COLOR_RED = Color.parseColor("#FF4444");
    public static final int COLOR_WHITE = Color.parseColor("#FFFFFF");
    public static final int TEXT_SIZE_NORMAL = 17;
    public static final int TEXT_SIZE_YEAR = 10;
    private static final int YEAR = 3;
    private static final int MONTH = 2;
    private static final int WEEK = 1;
    private static final int DAY = 0;
    private static final int YEAR_START = 2010;
    private static final float ALPHA_UNSELECTED = 0.5f;
    private static final int SHAPE_WIDTH = 100;
    private static final int SHAPE_HEIGHT = 100;

    // view properties
    private int backgroundColor;
    private int shapeColor;
    private int shapeColorPressed;
    private int textColor;
    private int textSize;
    private int dateToPickIndex;
    private Typeface typeface = null;
    private HorizontalDateFilterClickListener listener = null;
    private Calendar currentDate = null;
    private Calendar weekDateStart = null;
    private Calendar weekDateEnd = null;

    private Context context;
    private HorizontalDateFilter horizontalDateFilter;

// #MARK - Constructors

    public HorizontalDateFilter(Context context) {
        super(context);
        this.context = context;
        int backgroundColor = COLOR_WHITE;
        int shapeColor = COLOR_ORANGE;
        int shapeColorPressed = COLOR_RED;
        int dateToPickIndex = WEEK;
        int textColor = COLOR_WHITE;
        init(backgroundColor, shapeColor, shapeColorPressed, dateToPickIndex, textColor);
    }

    public HorizontalDateFilter(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.HorizontalDateFilter, 0, 0);
        try{
            int backgroundColor = typedArray.getColor(R.styleable.HorizontalDateFilter_background, COLOR_WHITE);
            int shapeColor = typedArray.getColor(R.styleable.HorizontalDateFilter_itemColor, COLOR_ORANGE);
            int shapeColorPressed = typedArray.getColor(R.styleable.HorizontalDateFilter_itemColorPressed, COLOR_RED);
            int dateToPickIndex = typedArray.getInteger(R.styleable.HorizontalDateFilter_dateToPick, WEEK);
            int textColor = typedArray.getColor(R.styleable.HorizontalDateFilter_textColor, COLOR_WHITE);
            init(backgroundColor, shapeColor, shapeColorPressed, dateToPickIndex, textColor);
        } finally{
            typedArray.recycle();
        }
    }

// #MARK - Custom methods

    private void init(int backgroundColor, int shapeColor, int shapeColorPressed, int dateToPickIndex, int textColor){
        horizontalDateFilter = this;

        this.setUnselectedAlpha(ALPHA_UNSELECTED);
        this.setSpacing(20);
        this.setBackgroundColor(backgroundColor);
        setHorizontalDatePickerAdapter.execute(dateToPickIndex);
        this.setOnItemClickListener(itemClickListener);

        this.backgroundColor = backgroundColor;
        this.shapeColor = shapeColor;
        this.shapeColorPressed = shapeColorPressed;
        this.textColor = textColor;
        this.dateToPickIndex = dateToPickIndex;
        this.textSize = TEXT_SIZE_NORMAL;
        if(dateToPickIndex == YEAR){
           this.textSize = TEXT_SIZE_YEAR;
        }

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return true;
    }

    public void setHorizontalDateFilterClickListener(HorizontalDateFilterClickListener listener){
        this.listener = listener;
    }

    private ShapeDrawable getShape(){
        ShapeDrawable shapeDrawable = new ShapeDrawable(new OvalShape());
        shapeDrawable.getPaint().setColor(shapeColor);
        shapeDrawable.setIntrinsicHeight(SHAPE_HEIGHT);
        shapeDrawable.setIntrinsicWidth(SHAPE_WIDTH);
        return shapeDrawable;
    }

    private ShapeDrawable getShapePressed(){
        ShapeDrawable shapeDrawablePressed = new ShapeDrawable(new OvalShape());
        shapeDrawablePressed.getPaint().setColor(shapeColorPressed);
        shapeDrawablePressed.setIntrinsicHeight(SHAPE_HEIGHT);
        shapeDrawablePressed.setIntrinsicWidth(SHAPE_WIDTH);
        return shapeDrawablePressed;
    }

    private Button getItemView(String title){
        Button button = new Button(context);
        Gallery.LayoutParams params = new Gallery.LayoutParams(SHAPE_WIDTH, SHAPE_HEIGHT);

        StateListDrawable buttonBackground = new StateListDrawable();
        buttonBackground.addState(new int[] {android.R.attr.state_pressed}, getShapePressed());
        buttonBackground.addState(new int[] { }, getShape());

        if(this.typeface != null){
            Typeface typeface = this.typeface;
            button.setTypeface(typeface);
        }
        button.setLayoutParams(params);
        button.setGravity(Gravity.CENTER);
        button.setBackgroundDrawable(buttonBackground);
        button.setTextSize(textSize);
        button.setTextColor(textColor);
        button.setText(title);
        return button;
    }

    public void setTypeface(Typeface typeface){
        this.typeface = typeface;
    }

    private String[] generateViewTitleSequence(int from, int arraySize){
        String[] data = new String[arraySize];
        for(int i = 0; i < arraySize; ++i){
            data[i] = from + "";
            from++;
        }
        return data;
    }

    private int getDaysNumberInCurrentYear(){
        Calendar calendar = Calendar.getInstance();
        return calendar.getActualMaximum(Calendar.DAY_OF_YEAR);
    }

    private int getWeeksNumberInCurrentYear(){
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        calendar.set(Calendar.YEAR, currentYear);
        calendar.set(Calendar.MONTH, Calendar.DECEMBER);
        calendar.set(Calendar.DAY_OF_MONTH, 31);
        int ordinalDay = calendar.get(Calendar.DAY_OF_YEAR);
        int weekDay = calendar.get(Calendar.DAY_OF_WEEK) - 1; // Sunday = 0
        return (ordinalDay - weekDay + 10) / 7;
    }

    private int getValueFromView(View view){
        return  view.getId();
    }

    private Calendar getWeekDateStart(int currentWeek){
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        calendar.clear();
        calendar.set(Calendar.WEEK_OF_YEAR, currentWeek);
        calendar.set(Calendar.YEAR, currentYear);
        return calendar;
    }

    private Calendar getWeekDateEnd(int currentWeek){
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        calendar.clear();
        calendar.set(Calendar.YEAR, currentYear);
        calendar.set(Calendar.WEEK_OF_YEAR, currentWeek);
        for (int i = 0; i < 7; i++) {
            calendar.add(Calendar.DAY_OF_WEEK, 1);
        }
        return calendar;
    }

    private Integer getCurrentSelectionItemPosition(){
        Integer position = null;
        Calendar calendar = Calendar.getInstance();
        int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        switch(dateToPickIndex){
            case YEAR:
                if(year >= YEAR_START && year <= YEAR_START + 10){
                    position = year - YEAR_START;
                }
                break;

            case MONTH:
                position = month;
                break;

            case WEEK:
                position = calendar.get(Calendar.WEEK_OF_YEAR) - 1;
                break;

            case DAY:
                position = dayOfYear;
                break;
        }

        return position;
    }

// #MARK - AsyncTask

    private AsyncTask<Integer, Void, String[]> setHorizontalDatePickerAdapter = new AsyncTask<Integer, Void, String[]>() {

        @Override
        protected String[] doInBackground(Integer... dateToPickIndex) {
            String[] viewTitleSequence = null;
            switch(dateToPickIndex[0]){
                case YEAR:
                    viewTitleSequence = generateViewTitleSequence(YEAR_START, 10);
                    break;

                case MONTH:
                    viewTitleSequence = generateViewTitleSequence(1, 12);
                    break;

                case WEEK:
                    viewTitleSequence = generateViewTitleSequence(1, getWeeksNumberInCurrentYear());
                    break;

                case DAY:
                    viewTitleSequence = generateViewTitleSequence(1, getDaysNumberInCurrentYear());
                    break;
            }
            return viewTitleSequence;
        }

        @Override
        protected void onPostExecute(String[] data) {
            CustomArrayAdapter adapter = new CustomArrayAdapter(context, 0, data);
            horizontalDateFilter.setAdapter(adapter);
            horizontalDateFilter.setSelection(getCurrentSelectionItemPosition());
        }

    };

// #MARK - Listeners

    private OnItemClickListener itemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            currentDate = Calendar.getInstance();
            weekDateStart = Calendar.getInstance();
            weekDateEnd = Calendar.getInstance();
            int currentItemValue = getValueFromView(view);
            switch(dateToPickIndex){
                case YEAR:
                    currentDate.set(Calendar.YEAR, currentItemValue);
                    break;

                case MONTH:
                    currentDate.set(Calendar.MONTH, currentItemValue);
                    break;

                case WEEK:
                    weekDateStart = getWeekDateStart(currentItemValue);
                    weekDateEnd = getWeekDateEnd(currentItemValue);
                    break;

                case DAY:
                    currentDate.set(Calendar.DAY_OF_YEAR, currentItemValue);
                    break;
            }

            if(listener != null){
                if(dateToPickIndex == WEEK){
                    listener.onItemClickWeek(parent, view, position, id, weekDateStart, weekDateEnd);
                } else {
                    listener.onItemClick(parent, view, position, id, currentDate);
                }

            }
        }
    };

// #MARK - Adapter

    class CustomArrayAdapter extends ArrayAdapter<String> {

    // #MARK - Constants

        private final String[] data;

    // #MARK - Constructor

        public CustomArrayAdapter(Context context, int textViewResourceId, String[] date) {
            super(context, textViewResourceId, date);
            this.data = date;

        }

    // #MARK - Override methods

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            ViewHolder viewHolder;
            View rowView = convertView;
            if(rowView == null){
                viewHolder = new ViewHolder();
                viewHolder.button = getItemView("");
                rowView = viewHolder.button;
                rowView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) rowView.getTag();
            }

            viewHolder.button.setText(data[position]);
            viewHolder.button.setId(Integer.parseInt(data[position]));

            return rowView;
        }

    // #MARK - ViewHolder

        class ViewHolder{
            public Button button;
        }
    }

// #MARK - Interfaces (custom click event)

    interface HorizontalDateFilterClickListener {

        public void onItemClick(AdapterView<?> parent, View view, int position, long id, Calendar currentDate);

        public void onItemClickWeek(AdapterView<?> parent, View view, int position, long id, Calendar weekDateStart, Calendar weekDateEnd);

    }

}
