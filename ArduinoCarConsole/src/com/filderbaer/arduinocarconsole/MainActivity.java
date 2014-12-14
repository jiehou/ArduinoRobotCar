package com.filderbaer.arduinocarconsole;

import java.util.ArrayList;
import java.util.Set;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.os.Handler;

public class MainActivity extends Activity implements Handler.Callback {

	private ArrayList<Device> devAvailableList, devPairedList;
	private DeviceListBaseAdapter devAvailableListAdapter,
			devPairedListAdapter;
	private ListView devAvailableListView, devPairedListView;
	private ProgressDialog connectionProgressDialog;
	private Button btnFindDevices;
	private BluetoothAdapter bluetoothAdapter;
	private ArduinoCarConsoleApp appState;

	private static final String TAG = "MainActivity";
	private static final int BLUETOOTH_ENABLE = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Request spinner in upper right corner
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_main);

		// Setup Bluetooth devices lists with custom rows
		devPairedListView = (ListView) findViewById(R.id.lvPairedDevices);
		devPairedList = new ArrayList<Device>();
		devPairedListAdapter = new DeviceListBaseAdapter(this, devPairedList);
		devPairedListView.setAdapter(devPairedListAdapter);
		devPairedListView.setOnItemClickListener(deviceClickListener);

		devAvailableListView = (ListView) findViewById(R.id.lvAvailableDevices);
		devAvailableList = new ArrayList<Device>();
		devAvailableListAdapter = new DeviceListBaseAdapter(this,
				devAvailableList);
		devAvailableListView.setAdapter(devAvailableListAdapter);
		devAvailableListView.setOnItemClickListener(deviceClickListener);

		appState = (ArduinoCarConsoleApp) getApplicationContext();
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		// Register a receiver to handle Bluetooth actions
		registerReceiver(mReceiver, new IntentFilter(
				BluetoothDevice.ACTION_FOUND));
		registerReceiver(mReceiver, new IntentFilter(
				BluetoothAdapter.ACTION_DISCOVERY_FINISHED));

		btnFindDevices = (Button) findViewById(R.id.btnFindDevices);
		btnFindDevices.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startDiscovery();
			}
		});
		startDiscovery();
	}

	final OnItemClickListener deviceClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// Cancel discovery because it is costly
			bluetoothAdapter.cancelDiscovery();
			// Get the selected device
			Device device = (Device) parent.getItemAtPosition(position);
			// Show connection dialog and make sure that the connection can be
			// canceled
			connectionProgressDialog = ProgressDialog.show(MainActivity.this,
					"Connection Status", "Establishing connection...", false,
					true);
			connectionProgressDialog
					.setOnCancelListener(new OnCancelListener() {
						@Override
						public void onCancel(DialogInterface dialog) {
							connectionProgressDialog.dismiss();
							appState.disconnect();
							if (ArduinoCarConsoleApp.D) {
								Log.i(TAG, "canceled connection progress");
							}
							return;
						}
					});
			appState.connect(bluetoothAdapter.getRemoteDevice(device
					.getAddress()));
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case BLUETOOTH_ENABLE:
			if (!bluetoothAdapter.isEnabled()) {
				showToast("Bluetooth must be enabled");
			} else {
				startDiscovery();
			}
			break;
		default:
			break;
		}
	}
	
	@Override
	protected void onResume() {
		if (ArduinoCarConsoleApp.D) {
			Log.i(TAG, "Set Handler");
		}
		appState.setActivityHandler(new Handler(this));
		super.onResume();
	}

	@Override
	protected void onPause() {
		bluetoothAdapter.cancelDiscovery();
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// Make sure we are not doing discovery anymore
		if (bluetoothAdapter != null) {
			bluetoothAdapter.cancelDiscovery();
		}
		// Unregister broadcast listeners
		unregisterReceiver(mReceiver);
	}

	@Override
	public boolean handleMessage(Message msg) {
		// In case the connection dialog has not disappeared yet
		if (connectionProgressDialog != null) {
			connectionProgressDialog.dismiss();
		}
		switch (msg.what) {
		case ArduinoCarConsoleApp.MSG_CANCEL:
			if (msg.obj != null) {
				if (ArduinoCarConsoleApp.D) {
					Log.i(TAG, "Message: " + msg.obj);
				}
				showToast((String) msg.obj);
			}
			break;
		case ArduinoCarConsoleApp.MSG_CONNECTED:
			// When connected to a device start the activity
			if (ArduinoCarConsoleApp.D) {
				Log.i(TAG, "Connection successful to " + msg.obj);
			}
			startActivity(new Intent(getApplicationContext(),
					ConsoleActivity.class));
			break;
		default:
			break;
		}
		return false;
	}

	/**
	 * Start discovering Bluetooth devices. It will check if Bluetooth is
	 * enabled and then disable the search button before searching for visible
	 * devices
	 */
	public void startDiscovery() {
		if (!checkBluetoothState()) {
			finish();
			return;
		}
		// Show search progress spinner
		setProgressBarIndeterminateVisibility(true);
		// Disable button
		btnFindDevices.setText(R.string.searching);
		btnFindDevices.setEnabled(false);
		// Remove title for available devices
		findViewById(R.id.tvAvailableDevices).setVisibility(View.GONE);
		devPairedList.clear();
		devPairedListAdapter.notifyDataSetChanged();
		// Show already paired devices in the upper list
		Set<BluetoothDevice> pairedDevices = bluetoothAdapter
				.getBondedDevices();
		if (pairedDevices.size() > 0) {
			findViewById(R.id.tvPairedDevices).setVisibility(View.VISIBLE);
			for (BluetoothDevice device : pairedDevices) {
				// Signal strength isn't available for paired devices
				devPairedList.add(new Device(device.getName(), device
						.getAddress(), (short) 0));
			}
		}
		// Tell the list adapter that its data has changed so it would update
		// itself
		devPairedListAdapter.notifyDataSetChanged();
		// Available Devices
		devAvailableList.clear();
		devAvailableListAdapter.notifyDataSetChanged();
		// Start Discovery
		bluetoothAdapter.startDiscovery();
	}

	/**
	 * This method tells the caller if the Bluetooth is enabled or it even
	 * exists on the phone
	 */
	private boolean checkBluetoothState() {
		if (bluetoothAdapter == null) {
			showToast("Bluetooth not available");
			return false;
		} else if (!bluetoothAdapter.isEnabled()) {
			startActivityForResult(new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE), BLUETOOTH_ENABLE);
		}
		return true;
	}

	// Add found device to the devices list
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				// Found device in range
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				Device foundDevice = new Device(device.getName(),
						device.getAddress(), intent.getShortExtra(
								BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE));
				// If it is not a paired device then add it to the list
				if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
					devAvailableList.add(foundDevice);
					// Signal list content change
					devAvailableListAdapter.notifyDataSetChanged();
					// Make the available devices title visible
					findViewById(R.id.tvAvailableDevices).setVisibility(
							View.VISIBLE);
				}
			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
					.equals(action)) {
				// When finished (timeout) remove the progress indicator and
				// enable search button
				setProgressBarIndeterminateVisibility(false);
				btnFindDevices.setText(R.string.searchDevices);
				btnFindDevices.setEnabled(true);
			}
		}
	};
	
	protected void showToast(String message) {
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT)
				.show();
	}
}
