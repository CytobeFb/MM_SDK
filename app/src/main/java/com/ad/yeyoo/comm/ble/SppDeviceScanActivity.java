package com.ad.yeyoo.comm.ble;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ad.yeyoo.R;

import java.util.Set;

/**
 * This Activity appears as a dialog. It lists any paired devices and
 * devices detected in the area after discovery. When a device is chosen
 * by the user, the MAC address of the device is sent back to the parent
 * Activity in the result Intent.
 */
public class SppDeviceScanActivity extends Activity {
    // Debugging
    private static final String TAG = "BtDeviceScanActivity";

    public static String EXTRAS_DEVICE_NAME = "device_name";
    // Return Intent extra
    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    // Member fields
    private BluetoothAdapter mBtAdapter;
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;
    private ArrayAdapter<String> mNewDevicesArrayAdapter;
    private int mScreenWidth = -1, mScreenHeight = -1;

    private ListView pairedListView = null;
    private ListView newDevicesListView = null;

    private boolean isDiscoveryFinished = false;
    // The BroadcastReceiver that listens for discovered devices and
    // changes the title when discovery is finished
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action) ||
                    BluetoothDevice.ACTION_NAME_CHANGED.equals(action) ||
                    BluetoothDevice.ACTION_CLASS_CHANGED.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device != null && device.getBondState() == BluetoothDevice.BOND_NONE) {
                    String name = device.getName();
                    String address = device.getAddress();
                    if (name != null && mNewDevicesArrayAdapter.getPosition(name + "\n" + address) < 0) {
                        String deviceName = device.getName();
                        if (deviceName != null && (deviceName.contains("AD-RF") || deviceName.contains("AD-SPP"))) {
                            mNewDevicesArrayAdapter.add(name + "\n" + address);
                        }
                    }
                }
                // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                setProgressBarIndeterminateVisibility(false);
                setTitle(R.string.ad_comm_spp_select_device);
                if (mNewDevicesArrayAdapter.getCount() == 0) {
                    String noDevices = getResources().getText(R.string.ad_comm_spp_none_found).toString();
                    mNewDevicesArrayAdapter.add(noDevices);
                }
                unregisterReceiver(this);
                isDiscoveryFinished = true;
            }

            //setListViewHeight(newDevicesListView, 0.8f);
        }
    };
    // The on-click listener for all devices in the ListViews
    private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            // Cancel discovery because it's costly and we're about to connect
            mBtAdapter.cancelDiscovery();

            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String name = null;
            String address = null;
            if (info.length() > 17) {
                name = info.substring(0, info.length() - 18);
                address = info.substring(info.length() - 17);
            } else {
                finish();
            }
            // Create the result Intent and include the MAC address
            Intent intent = new Intent();
            intent.putExtra(EXTRAS_DEVICE_NAME, name);
            intent.putExtra(EXTRA_DEVICE_ADDRESS, address);

            // Set result and finish this Activity
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup the window
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.comm_spp_device_list);

        setTitle(R.string.ad_comm_spp_select_device);

        // Set result CANCELED incase the user backs out
        setResult(Activity.RESULT_CANCELED);

        // Initialize the button to perform device discovery
        Button scanButton = (Button) findViewById(R.id.button_scan);

        scanButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                doDiscovery();
                v.setVisibility(View.GONE);
            }
        });

        // Initialize array adapters. One for already paired devices and
        // one for newly discovered devices
        mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.comm_spp_device_item);
        mNewDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.comm_spp_device_item);

        // Find and set up the ListView for paired devices
        pairedListView = (ListView) findViewById(R.id.paired_devices);
        pairedListView.setAdapter(mPairedDevicesArrayAdapter);
        pairedListView.setOnItemClickListener(mDeviceClickListener);

        // Find and set up the ListView for newly discovered devices
        newDevicesListView = (ListView) findViewById(R.id.new_devices);
        newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
        newDevicesListView.setOnItemClickListener(mDeviceClickListener);

        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_NAME_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_CLASS_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        this.registerReceiver(mReceiver, filter);
        // Get the local Bluetooth adapter
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBtAdapter == null) {
            Toast.makeText(
                    getApplicationContext(),
                    getResources().getString(R.string.ad_comm_spp_no_bluetooth),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        //打开蓝牙
        if (!mBtAdapter.isEnabled())
            mBtAdapter.enable();

        // Get a set of currently paired devices
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices.size() > 0) {
            findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
            for (BluetoothDevice device : pairedDevices) {
                if (BluetoothAdapter.checkBluetoothAddress(device.getAddress())) {
                    String deviceName = device.getName();
                    if (deviceName != null && (deviceName.contains("AD-RF") || deviceName.contains("AD-SPP"))) {
                        mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                    }
                }
            }
        } else {
            String noDevices = getResources().getText(R.string.ad_comm_spp_none_paired).toString();
            mPairedDevicesArrayAdapter.add(noDevices);
        }

        //setListViewHeight(pairedListView, 0.3f);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Make sure we're not doing discovery anymore
        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }

        if (!isDiscoveryFinished) {
            try {
                // Unregister broadcast listeners
                this.unregisterReceiver(mReceiver);
            } catch (Exception e) {
                ;
            }
        }
    }

    /**
     * OutStart device discover with the BluetoothAdapter
     */
    private void doDiscovery() {

        // Indicate scanning in the title
        setProgressBarIndeterminateVisibility(true);
        setTitle(R.string.ad_comm_spp_scanning);

        // Turn on sub-title for new devices
        findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);

        // If we're already discovering, stop it
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }

        // Request discover from BluetoothAdapter
        mBtAdapter.startDiscovery();
    }

    public void setListViewHeight(ListView listView, float scale) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
/*
        if (totalHeight > 0) {
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));

            if (params.height + listView.getTop() > mScreenHeight * scale) {
                params.height = (int) (mScreenHeight * scale) - listView.getTop();
            }
            if (params.height > 0) listView.setLayoutParams(params);
        }*/
        ViewGroup.LayoutParams params = listView.getLayoutParams();

        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));

        ((ViewGroup.MarginLayoutParams) params).setMargins(10, 10, 10, 10); // 可删除

        listView.setLayoutParams(params);

    }

    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                if (event.getX() < 0 || event.getY() < 0 ||
                        2 * event.getRawX() - event.getX() > mScreenWidth ||
                        2 * event.getRawY() - event.getY() > mScreenHeight) {
                    this.finish();
                }
                break;
        }
        return true;
    }
}

