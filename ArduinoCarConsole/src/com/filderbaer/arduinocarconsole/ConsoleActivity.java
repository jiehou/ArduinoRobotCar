package com.filderbaer.arduinocarconsole;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ConsoleActivity extends Activity implements Handler.Callback {

	private ArduinoCarConsoleApp appState;
	private Button btnForward, btnBackward, btnLeft, btnRight;
	private Handler mHandler;
	private Runnable mAction;
	private TextView tvCommand;

	private static final String TAG = "ConsoleActivity";
	protected static final String SYSTEM_DIALOG_REASON_KEY = "reason";
	protected static final String SYSTEM_DIALOG_HOME_KEY = "homekey";
	private static final String FORWARD = "f";
	private static final String BACKWARD = "b";
	private static final String LEFT = "l";
	private static final String RIGHT = "r";
	private static int DELAY = 50;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.console);
		// getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		appState = (ArduinoCarConsoleApp) getApplicationContext();

		try {
			// Buttons
			btnForward = (Button) findViewById(R.id.btnForward);
			btnBackward = (Button) findViewById(R.id.btnBackward);
			btnLeft = (Button) findViewById(R.id.btnLeft);
			btnRight = (Button) findViewById(R.id.btnRight);

			// TextView
			tvCommand = (TextView) findViewById(R.id.tvCommand);

			// Forward
			btnForward.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						if (mHandler != null)
							return true;
						mHandler = new Handler();
						mAction = new CommandThread(FORWARD, mHandler,
								tvCommand);
						mHandler.postDelayed(mAction, DELAY);
						break;
					case MotionEvent.ACTION_UP:
						if (mHandler == null)
							return true;
						mHandler.removeCallbacks(mAction);
						mHandler = null;
						tvCommand.setVisibility(View.INVISIBLE);
						break;
					default:
						break;
					}
					return false;
				}
			});

			// Backward
			btnBackward.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						if (mHandler != null) {
							return true;
						}
						mHandler = new Handler();
						mAction = new CommandThread(BACKWARD, mHandler,
								tvCommand);
						mHandler.postDelayed(mAction, DELAY);
						break;
					case MotionEvent.ACTION_UP:
						if (mHandler == null) {
							return true;
						}
						mHandler.removeCallbacks(mAction);
						mHandler = null;
						tvCommand.setVisibility(View.INVISIBLE);
					default:
						break;
					}
					return false;
				}
			});

			// Left
			btnLeft.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						if (mHandler != null)
							return true;
						mHandler = new Handler();
						mAction = new CommandThread(LEFT, mHandler, tvCommand);
						mHandler.postDelayed(mAction, DELAY);
						break;
					case MotionEvent.ACTION_UP:
						if (mHandler == null)
							return true;
						mHandler.removeCallbacks(mAction);
						mHandler = null;
						tvCommand.setVisibility(View.INVISIBLE);
						break;
					default:
						break;
					}
					return false;
				}
			});

			// Right
			btnRight.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						if (mHandler != null)
							return true;
						mHandler = new Handler();
						mAction = new CommandThread(RIGHT, mHandler, tvCommand);
						mHandler.postDelayed(mAction, DELAY);
						break;
					case MotionEvent.ACTION_UP:
						if (mHandler == null)
							return true;
						mHandler.removeCallbacks(mAction);
						mHandler = null;
						tvCommand.setVisibility(View.INVISIBLE);
						break;
					default:
						break;
					}
					return false;
				}
			});

			// Home
			registerReceiver(mReceiver, new IntentFilter(
					Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
		} catch (Exception e) {
			showToast(e.getMessage());
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		appState.setActivityHandler(new Handler(this));
		if (ArduinoCarConsoleApp.D) {
			showToast(TAG + " onResume()");
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		appState.disconnect();
		finish();
	}

	@Override
	public void finish() {
		appState.setActivityHandler(null);
		super.finish();
	}

	@Override
	public boolean handleMessage(Message msg) {
		return false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (ArduinoCarConsoleApp.D) {
			showToast(TAG + " onDestroy()");
		}
		appState.disconnect();
		unregisterReceiver(mReceiver);
	}

	private class CommandThread implements Runnable {
		private String mCommand;
		private Handler mHandler;
		private TextView mTextView;

		public CommandThread(String command, Handler handler, TextView textView) {
			mCommand = command;
			mHandler = handler;
			mTextView = textView;
		}

		@Override
		public void run() {
			appState.write(mCommand);
			mHandler.postDelayed(this, DELAY);
			mTextView.setText(mCommand);
			mTextView.setVisibility(View.VISIBLE);
		}
	}

	protected void showToast(String message) {
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT)
				.show();
	}

	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS == action) {
				String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
				if (reason != null) {
					showToast(TAG + ": home");
					// Home Key
					if (reason.equals(SYSTEM_DIALOG_HOME_KEY)) {
						onBackPressed();
					}
				}
			}
		}
	};

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// When the user clicks the application icon on the top left
		if (item.getItemId() == android.R.id.home) {
			// Behave as if the back button was clicked
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
