package ru.ok.timer;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView timerText;
    private Button setTimerButton;
    private Button startStopButton;
    private Button resetButton;

    private long timerInitial;
    private long timeRemain;
    private long startTime;

    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {

            timeRemain = timerInitial - (System.currentTimeMillis() - startTime);

            if (timeRemain <= 0) {
                resetTimer();
                return;
            }

            int millis = (int) (timeRemain % 1000);
            int seconds = (int) (timeRemain / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;

            setTimerText(minutes, seconds, millis);

            timerHandler.postDelayed(this, 16);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timerText = findViewById(R.id.timer);
        setTimerButton = findViewById(R.id.btn_set_timer);
        startStopButton = findViewById(R.id.btn_start_stop);
        startStopButton.setText(getString(R.string.start));
        resetButton = findViewById(R.id.btn_reset);

        setTimerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSetTimerDialog();
            }
        });

        startStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (startStopButton.getText().equals(getString(R.string.pause))) {
                    pauseTimer();
                } else {
                    startTime = System.currentTimeMillis();
                    startTimer();
                }
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });
    }

    private void pauseTimer() {
        timerHandler.removeCallbacks(timerRunnable);
        startStopButton.setText(getString(R.string.cont));
        setTimerButton.setEnabled(true);
        timerInitial = timeRemain;
    }

    private void resetTimer() {
        timerHandler.removeCallbacks(timerRunnable);
        startStopButton.setText(getString(R.string.start));
        setTimerButton.setEnabled(true);
        timerInitial = 0;
        setTimerText(0, 0, 0);
    }

    private void startTimer() {
        if (timerInitial > 0) {
            timerHandler.postDelayed(timerRunnable, 0);
            startStopButton.setText(getString(R.string.pause));
            setTimerButton.setEnabled(false);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        pauseTimer();
    }

    private void setTimerText(int minutes, int seconds, int millis) {
        timerText.setText(String.format(Locale.ENGLISH, "%02d:%02d:%03d", minutes, seconds, millis));
    }

    public void showSetTimerDialog() {

        final Dialog d = new Dialog(MainActivity.this);
        d.setContentView(R.layout.dialog);
        Button setButton = d.findViewById(R.id.set_button);
        Button cancelButton = d.findViewById(R.id.cancel_button);

        final NumberPicker minutesPicker = d.findViewById(R.id.minutes_picker);
        minutesPicker.setMaxValue(100);
        minutesPicker.setMinValue(0);
        minutesPicker.setWrapSelectorWheel(false);

        final NumberPicker secondsPicker = d.findViewById(R.id.seconds_picker);
        secondsPicker.setMaxValue(59);
        secondsPicker.setMinValue(0);
        secondsPicker.setWrapSelectorWheel(false);

        final NumberPicker millisPicker = d.findViewById(R.id.millis_picker);
        millisPicker.setMaxValue(999);
        millisPicker.setMinValue(0);
        millisPicker.setWrapSelectorWheel(false);

        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int startMinutes = minutesPicker.getValue();
                int startSeconds = secondsPicker.getValue();
                int startMillis = millisPicker.getValue();
                timerInitial = convertToMillis(startMinutes, startSeconds, startMillis);
                setTimerText(startMinutes, startSeconds, startMillis);
                d.dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });

        d.show();
    }

    private long convertToMillis(int minutes, int seconds, int millis) {
        return millis + seconds * 1000 + minutes * 60 * 1000;
    }
}
