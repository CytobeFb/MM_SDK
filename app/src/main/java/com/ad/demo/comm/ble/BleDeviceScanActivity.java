package com.ad.demo.comm.ble;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ad.demo.R;

import java.util.ArrayList;

/**
 * Created by endyc on 2018-08-31.
 */

public class BleDeviceScanActivity extends Activity {
    // Debugging
    private static final String TAG = "BleDeviceScanActivity";
    private static final int REQUEST_ENABLE_BT = 1;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;
    //-------------------获取定位权限--------------------------------
    private static final int ACCESS_LOCATION = 1;
    public static String EXTRAS_DEVICE_NAME = "device_name";
    // Return Intent extra
    public static String EXTRA_DEVICE_ADDRESS = "device_address";
    // Device scan callback.
    protected ScanCallback mScanCallback;
    Button scanButton;
    private Context mContext;
    private BleDeviceListAdapter mLeDeviceListAdapter;
    // Device scan callback.
    protected LeScanCallback mLeScanCallback =
            new LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String deviceName = device.getName();
                            if (deviceName != null && (deviceName.contains("AD-RF") || deviceName.contains("AD-BLE"))) {
                                BleDeviceCustom mDev = new BleDeviceCustom(mContext, device, rssi);
                                mLeDeviceListAdapter.addDevice(mDev);
                                mLeDeviceListAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                }
            };
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;
    private ListView newDevicesListView = null;
    // The on-click listener for all devices in the ListViews
    private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final BluetoothDevice device = mLeDeviceListAdapter.getDevice(position).getDevice();
            if (device == null) return;

            // Create the result Intent and include the MAC address
            final Intent intent = new Intent();
            intent.putExtra(EXTRAS_DEVICE_NAME, device.getName());
            intent.putExtra(EXTRA_DEVICE_ADDRESS, device.getAddress());

            scanLeDevice(false);
            // Set result and finish this Activity
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    };
    //-------------------获取定位权限--------------------------------

    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mScanCallback = new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        String deviceName = result.getDevice().getName();
                        if (deviceName != null && (deviceName.contains("AD-RF") || deviceName.contains("AD-BLE"))) {
                            BleDeviceCustom mDev = new BleDeviceCustom(mContext, result.getDevice(), result.getRssi());
                            mLeDeviceListAdapter.addDevice(mDev);
                            mLeDeviceListAdapter.notifyDataSetChanged();
                        }
                    }
                }
            };
        }
    }

    /**
     * 获取定位权限
     */
    @SuppressLint("WrongConstant")
    private void getPermission() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            int permissionCheck = 0;
            permissionCheck = this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            permissionCheck += this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                //未获得权限
                this.requestPermissions( // 请求授权
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION},
                        ACCESS_LOCATION);// 自定义常量,任意整型

            }
        }
    }

    /**
     * 请求权限的结果回调。每次调用 requestpermissions（string[]，int）时都会调用此方法。
     *
     * @param requestCode  传入的请求代码
     * @param permissions  传入permissions的要求
     * @param grantResults 相应权限的授予结果:PERMISSION_GRANTED 或 PERMISSION_DENIED
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case ACCESS_LOCATION:
                if (hasAllPermissionGranted(grantResults)) {
                    Toast.makeText(this, "onRequestPermissionsResult: 用户允许权限", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "onRequestPermissionsResult: 拒绝搜索设备权限", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private boolean hasAllPermissionGranted(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查BLE是否起作用
     */
    private boolean checkBLEFeature() {
        //判断是否支持蓝牙4.0
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ad_comm_ble_not_supported, Toast.LENGTH_SHORT).show();
            return false;
        }
        //获取蓝牙适配器
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        //判断是否支持蓝牙
        if (mBluetoothAdapter == null) {
            //不支持
            Toast.makeText(this, R.string.ad_comm_ble_error_supported, Toast.LENGTH_SHORT).show();
            return false;
        } else
            //打开蓝牙
            if (!mBluetoothAdapter.isEnabled()) {//判断是否已经打开
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup the window
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.comm_ble_device_list);

        setTitle(R.string.ad_comm_ble_select_device);
        mContext = this;
        mHandler = new Handler();

        getPermission();

        // Checks if Bluetooth is supported on the device.
        if (!checkBLEFeature()) {
            finish();
            return;
        }

        // Initialize the button to perform device discovery
        scanButton = (Button) findViewById(R.id.button_scan);

        scanButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mScanning) {
                    scanLeDevice(false);
                } else {
                    scanLeDevice(true);
                }
            }
        });

        // Find and set up the ListView for newly discovered devices
        newDevicesListView = (ListView) findViewById(R.id.new_devices);
        // Initializes list view adapter.
        mLeDeviceListAdapter = new BleDeviceListAdapter(mContext);
        newDevicesListView.setAdapter(mLeDeviceListAdapter);
        newDevicesListView.setOnItemClickListener(mDeviceClickListener);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }

        // Initializes list view adapter.
        mLeDeviceListAdapter = new BleDeviceListAdapter(mContext);
        newDevicesListView.setAdapter(mLeDeviceListAdapter);
        scanLeDevice(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanLeDevice(false);
        mLeDeviceListAdapter.clear();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBluetoothAdapter.isEnabled()) {
            scanLeDevice(false);
        }
    }

    private void scanLeDevice(final boolean enable) {
        Log.i(TAG, "scanLeDevice: Build.VERSION.SDK_INT = " + Build.VERSION.SDK_INT);

        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        mBluetoothAdapter.getBluetoothLeScanner().stopScan(mScanCallback);
                    } else {
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    }

                }
            }, SCAN_PERIOD);

            mScanning = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mBluetoothAdapter.getBluetoothLeScanner().startScan(mScanCallback);
            } else {
                mBluetoothAdapter.startLeScan(mLeScanCallback);
            }
            scanButton.setText(R.string.ad_comm_ble_stop);
        } else {
            mScanning = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mBluetoothAdapter.getBluetoothLeScanner().stopScan(mScanCallback);
            } else {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }
            scanButton.setText(R.string.ad_comm_ble_scan);
        }
    }

    protected static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
        TextView deviceRssi;
    }

    protected class BleDeviceCustom {
        private BluetoothDevice device;
        private int rssi;
        private Context context;

        public BleDeviceCustom(Context context, BluetoothDevice dev, int rssi) {
            this.device = dev;
            this.rssi = rssi;
            this.context = context;
        }

        public String getName() {
            return device.getName();
        }

        public String getAddress() {
            return device.getAddress();
        }

        public BluetoothDevice getDevice() {
            return device;
        }

        public void setDevice(BluetoothDevice device) {
            this.device = device;
        }

        public int getRssi() {
            return rssi;
        }

        public void setRssi(int rssi) {
            this.rssi = rssi;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof BleDeviceCustom) {
                return device.equals(((BleDeviceCustom) o).getDevice());
            }
            return false;
        }
    }

    protected class BleDeviceListAdapter extends BaseAdapter {

        private Context context;

        private ArrayList<BleDeviceCustom> mLeDevices;
        private LayoutInflater mInflator;


        public BleDeviceListAdapter(Context context) {
            super();
            this.context = context;
            mLeDevices = new ArrayList<BleDeviceCustom>();
            mInflator = LayoutInflater.from(context);


        }

        public void addDevice(BleDeviceCustom device) {
            if (!mLeDevices.contains(device)) {
                mLeDevices.add(device);
            }
        }

        public BleDeviceCustom getDevice(int position) {
            return mLeDevices.get(position);
        }

        public void clear() {
            mLeDevices.clear();
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            // General ListView optimization code.
            if (view == null) {
                view = mInflator.inflate(R.layout.comm_ble_device_item, null);
                viewHolder = new ViewHolder();
                viewHolder.deviceRssi = (TextView) view.findViewById(R.id.device_rssi);
                viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
                viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            BleDeviceCustom mDev = mLeDevices.get(i);
            final String deviceName = mDev.getName();
            if (deviceName != null && deviceName.length() > 0)
                viewHolder.deviceName.setText(deviceName);
            else
                viewHolder.deviceName.setText(R.string.ad_comm_ble_unknown_device);
            viewHolder.deviceAddress.setText(mDev.getAddress());
            viewHolder.deviceRssi.setText("RSSI: " + String.valueOf(mDev.getRssi()) + "dBm");

            return view;
        }
    }
}
