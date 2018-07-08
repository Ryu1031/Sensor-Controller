package com.example.myapplication.fragment;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.SystemApplication;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.java_websocket.WebSocketClient;

import java.util.ArrayList;

import utils.AppUtil;
import utils.Log;

/**
 * Created by Ryusuke on 2016/08/11.
 */
public class ControlFragment extends Fragment
        implements SeekBar.OnSeekBarChangeListener, AdapterView.OnItemSelectedListener,
        View.OnClickListener {
    public static final String FRAGMENT_TAG = ControlFragment.class.getSimpleName();

    private static final int ANIMATION_TIME = 2000;

    private ViewHolder mViewHolder;

    private Context mContext;

    private WebSocketClient mWebSocketClient;

    private WebClientSupport mSupport;

    public static ControlFragment newInstance() {
        Bundle args = new Bundle();
        ControlFragment fragment = new ControlFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.control_layout, container, false);
        SystemApplication application = (SystemApplication) getActivity().getApplication();
        mWebSocketClient = application.getWebSocketClient();
        setup(rootView);
        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mWebSocketClient.close();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        int viewId = seekBar.getId();
        switch (viewId) {
            case R.id.scroll_bar_one:
                int seekbarOneValue = mViewHolder.seekBarOne.getProgress();
                mViewHolder.textViewOne.setText(String.valueOf(seekbarOneValue));
                mSupport.sendSpeed(seekbarOneValue, 1);
                break;
            case R.id.scroll_bar_two:
                int seekbarTwoValue = mViewHolder.seekBarTwo.getProgress();
                mViewHolder.textViewTwo.setText(String.valueOf(seekbarTwoValue));
                mSupport.sendSpeed(seekbarTwoValue, 2);
                break;
            default:
                Log.w("Nothing id :" + viewId);
                break;
        }
        //FIXME:
        Log.w("onProgressChanged:" + viewId + " progress:" + progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        //NOP
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        //NOP
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        int viewId = adapterView.getId();
        String item = "";
        switch (viewId) {
            case R.id.send_line_spinner_one:
                item = adapterView.getItemAtPosition(position).toString();
                mSupport.sendTopic(item);
                break;
            case R.id.send_line_spinner_two:
                item = adapterView.getItemAtPosition(position).toString();
                mSupport.sendTopic(item);
                break;
            case R.id.spinner_record:
                item = adapterView.getItemAtPosition(position).toString();
                break;
            default:
                Log.w("Nothing id :" + viewId);
                break;
        }
        //FIXME:
        AppUtil.showToast(mContext, item);
        Log.w("onItemSelected:" + viewId + " position:" + position + " item:" + item);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        //NOP
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        Log.w("onItemSelected:" + viewId);
        String text = null;
        switch (viewId) {
            case R.id.button_send_text_one:
                text = mViewHolder.editTextOne.getText().toString();
                mSupport.sendText(text, 1);
                break;
            case R.id.button_send_text_two:
                text = mViewHolder.editTextTwo.getText().toString();
                mSupport.sendText(text, 2);
                break;
            case R.id.record_button_back:
                //FIXME:
                break;
            case R.id.record_button_next:
                //FIXME:
                break;
            default:
                Log.w("Nothing id :" + viewId);
                break;
        }
        if (!TextUtils.isEmpty(text)) {
            AppUtil.showToast(mContext, text);
            //FIXME:
        }
    }

    private void showFragment() {
        Fragment fragment = ConnectionFragment.newInstance();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_container, fragment, ControlFragment.FRAGMENT_TAG);
        transaction.commit();
    }

    private void setup(View rootView) {
        mContext = getActivity().getApplicationContext();
        mSupport = new WebClientSupport();
        mViewHolder = new ViewHolder(rootView);

        // spinner
        mViewHolder.spinnerOne.setSelection(0, false);
        mViewHolder.spinnerTwo.setSelection(0, false);
        mViewHolder.spinnerRecord.setSelection(0, false);

        // setListener
        mViewHolder.seekBarOne.setOnSeekBarChangeListener(this);
        mViewHolder.seekBarTwo.setOnSeekBarChangeListener(this);
        mViewHolder.spinnerOne.setOnItemSelectedListener(this);
        mViewHolder.spinnerTwo.setOnItemSelectedListener(this);
        mViewHolder.spinnerRecord.setOnItemSelectedListener(this);
        mViewHolder.sendButtonOne.setOnClickListener(this);
        mViewHolder.sendButtonTwo.setOnClickListener(this);
        mViewHolder.recordButtonBack.setOnClickListener(this);
        mViewHolder.recordButtonNext.setOnClickListener(this);

        // Title
        mViewHolder.connections.setText(createReadyStateString());

        // SeekBar
        String progressValueOne = String.valueOf(mViewHolder.seekBarOne.getProgress());
        String progressValueTwo = String.valueOf(mViewHolder.seekBarTwo.getProgress());
        mViewHolder.textViewOne.setText(progressValueOne);
        mViewHolder.textViewTwo.setText(progressValueTwo);

        // barchart
        createBarChart();
    }

    private String createReadyStateString() {
        int stringId = 0;
        switch (mWebSocketClient.getReadyState()) {
            case org.java_websocket.WebSocket.READY_STATE_CONNECTING: //isConnecting
                stringId = R.string.connection_label_connected;
                break;
            case org.java_websocket.WebSocket.READY_STATE_OPEN: //isOpen
                stringId = R.string.connection_label_connected;
                break;
            case org.java_websocket.WebSocket.READY_STATE_CLOSING: //isClosing
                stringId = R.string.connection_label_disconnected;
                break;
            case org.java_websocket.WebSocket.READY_STATE_CLOSED: //isClosed
                stringId = R.string.connection_label_disconnected;
                break;
            default:
                stringId = R.string.connection_label_disconnected;
                break;
        }
        return getString(stringId);
    }

    private void createBarChart() {
        mViewHolder.barChartView.setDescription("Score");
        mViewHolder.barChartView.getAxisRight().setEnabled(false);
        mViewHolder.barChartView.getAxisLeft().setEnabled(true);
        mViewHolder.barChartView.setDrawGridBackground(true);
        mViewHolder.barChartView.setDrawBarShadow(false);
        mViewHolder.barChartView.setEnabled(true);

        mViewHolder.barChartView.setTouchEnabled(true);
        mViewHolder.barChartView.setPinchZoom(true);
        mViewHolder.barChartView.setDoubleTapToZoomEnabled(true);

        mViewHolder.barChartView.setHighlightEnabled(true);
        mViewHolder.barChartView.setDrawHighlightArrow(true);
        mViewHolder.barChartView.setHighlightEnabled(true);

        mViewHolder.barChartView.setScaleEnabled(true);

        mViewHolder.barChartView.getLegend().setEnabled(true);

        XAxis xAxis = mViewHolder.barChartView.getXAxis();
        xAxis.setDrawLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(true);
        xAxis.setSpaceBetweenLabels(0);

        mViewHolder.barChartView.setData(createBarChartData());

        mViewHolder.barChartView.invalidate();
        mViewHolder.barChartView.animateY(ANIMATION_TIME, Easing.EasingOption.EaseInBack);
    }

    // BarChartの設定
    private BarData createBarChartData() {
        ArrayList<BarDataSet> barDataSets = new ArrayList<>();

        // X軸
        ArrayList<String> xValues = new ArrayList<>();
        for (int i = 0;
             i < 24;
             i++) {
            xValues.add(String.valueOf(i));
        }

        // valueA
        ArrayList<BarEntry> valuesA = new ArrayList<>();
        for (int i = 0;
             i < 24;
             i++) {
            valuesA.add(new BarEntry(100 + i * 100, i));
        }

        BarDataSet valuesADataSet = new BarDataSet(valuesA, "Total");
        valuesADataSet.setColor(ColorTemplate.COLORFUL_COLORS[3]);

        barDataSets.add(valuesADataSet);

        // valueB
        ArrayList<BarEntry> valuesB = new ArrayList<>();

        for (int i = 0;
             i < 24;
             i++) {
            valuesB.add(new BarEntry(100 + i * 50, i));
        }

        BarDataSet valuesBDataSet = new BarDataSet(valuesB, "Average Time");
        valuesBDataSet.setColor(ColorTemplate.COLORFUL_COLORS[4]);

        barDataSets.add(valuesBDataSet);

        return new BarData(xValues, barDataSets);
    }

    public class WebClientSupport {
        private static final String TOPIC_NEWS = "100|N";
        private static final String TOPIC_WEATHER = "100|N";
        private static final String TOPIC_HUMIDITY = "100|C";

        public void sendTopic(String topic) {
            Resources res = getResources();
            String result = null;
            if (res.getString(R.string.topic_news).equals(topic)) {
                result = TOPIC_NEWS;
            } else if (res.getString(R.string.topic_weather).equals(topic)) {
                result = TOPIC_WEATHER;
            } else if (res.getString(R.string.topic_humidity).equals(topic)) {
                result = TOPIC_HUMIDITY;
            } else {
                Log.e("Nothing");
                return;
            }
            Log.e(result);
            try {
                if (reConectionCheckAuto()) {
                    mWebSocketClient.send(result);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void sendSpeed(int speed, int row) {
            String value = null;
            switch (row) {
                case 1:
                    value = "S";
                    break;
                case 2:
                    value = "s";
                    break;
            }
            speed += 100;
            String result = String.format("%1$03d", speed) + "|" + value;
            Log.e(result);
            try {
                if (reConectionCheckAuto()) {
                    mWebSocketClient.send(result);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void sendText(String message, int row) {
            String value = null;
            switch (row) {
                case 1:
                    value = "txt1";
                    break;
                case 2:
                    value = "Txt2";
                    break;
            }
            String result = value + "|" + message + ";";
            Log.e(result);
            try {
                if (reConectionCheckAuto()) {
                    mWebSocketClient.send(result);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public boolean reConectionCheckAuto() {
            boolean result = false;
            switch (mWebSocketClient.getReadyState()) {
                case org.java_websocket.WebSocket.READY_STATE_CONNECTING: //isConnecting
                case org.java_websocket.WebSocket.READY_STATE_OPEN: //isOpen
                    if (!mWebSocketClient.getConnection().isConnecting()) {
                        result = true;
                    }
                    break;
                case org.java_websocket.WebSocket.READY_STATE_CLOSING: //isClosing
                case org.java_websocket.WebSocket.READY_STATE_CLOSED: //isClosed
                    break;
            }
            return result;
        }
    }

    private static class ViewHolder {
        public final TextView connections;
        public final TextView textViewOne;
        public final TextView textViewTwo;
        public final EditText editTextOne;
        public final EditText editTextTwo;
        public final SeekBar seekBarOne;
        public final SeekBar seekBarTwo;
        public final Spinner spinnerOne;
        public final Spinner spinnerTwo;
        public final Spinner spinnerRecord;
        public final ImageButton sendButtonOne;
        public final ImageButton sendButtonTwo;
        public final ImageButton recordButtonBack;
        public final ImageButton recordButtonNext;
        public final BarChart barChartView;

        public ViewHolder(View rootView) {
            connections = (TextView) rootView.findViewById(R.id.connections);
            textViewOne = (TextView) rootView.findViewById(R.id.scroll_score_one);
            textViewTwo = (TextView) rootView.findViewById(R.id.scroll_score_two);
            seekBarOne = (SeekBar) rootView.findViewById(R.id.scroll_bar_one);
            seekBarTwo = (SeekBar) rootView.findViewById(R.id.scroll_bar_two);
            editTextOne = (EditText) rootView.findViewById(R.id.editText_one);
            editTextTwo = (EditText) rootView.findViewById(R.id.editText_two);
            spinnerOne = (Spinner) rootView.findViewById(R.id.send_line_spinner_one);
            spinnerTwo = (Spinner) rootView.findViewById(R.id.send_line_spinner_two);
            spinnerRecord = (Spinner) rootView.findViewById(R.id.spinner_record);
            sendButtonOne = (ImageButton) rootView.findViewById(R.id.button_send_text_one);
            sendButtonTwo = (ImageButton) rootView.findViewById(R.id.button_send_text_two);
            recordButtonBack = (ImageButton) rootView.findViewById(R.id.record_button_back);
            recordButtonNext = (ImageButton) rootView.findViewById(R.id.record_button_next);
            barChartView = (BarChart) rootView.findViewById(R.id.bar_chart);
        }
    }
}
