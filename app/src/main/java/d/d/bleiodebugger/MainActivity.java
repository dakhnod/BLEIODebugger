package d.d.bleiodebugger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {
    private ListView resultsListView;
    private BluetoothLeScanner scanner;

    private ArrayList<BLEIODevice> scanResults = new ArrayList<>();
    private ArrayAdapter<BLEIODevice> resultAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initBluetooth();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.startScan();
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.stopScan();
    }

    private void initViews(){
        resultsListView = findViewById(R.id.list_results);

        resultAdapter = new ArrayAdapter<BLEIODevice>(this, R.layout.layout_scan_result, scanResults){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if(convertView == null){
                    convertView = getLayoutInflater().inflate(R.layout.layout_scan_result, null);
                }

                BLEIODevice item = getItem(position);

                ((TextView) convertView.findViewById(R.id.scan_result_address)).setText(item.getAddress());
                ((TextView) convertView.findViewById(R.id.scan_result_address)).setTextColor(item.getContact() ? Color.GREEN : Color.RED);
                ((TextView) convertView.findViewById(R.id.scan_result_count)).setText("count: " + item.getCount());
                ((TextView) convertView.findViewById(R.id.scan_result_battery)).setText("battery: " + item.getBatteryPercent());

                return convertView;
            }
        };

        resultsListView.setAdapter(resultAdapter);
    }

    private void startScan(){
        ScanSettings settings = new ScanSettings.Builder()
                .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();
        this.scanner.startScan(
                Collections.singletonList(
                        new ScanFilter.Builder()
                                .build()
                ),
                settings,
                callback
        );
    }

    private void stopScan(){
        this.scanner.stopScan(callback);
    }

    private void initBluetooth(){
        scanner = ((BluetoothManager) getSystemService(BLUETOOTH_SERVICE))
                .getAdapter()
                .getBluetoothLeScanner();

    }

    ScanCallback callback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);

            Log.d("Main", "scan result");

            String address = result.getDevice().getAddress();
            if(address.equals("F0:7C:FA:62:4E:57")){
                Log.d("Main", "chair");
            }
            byte[] manufacturerData = result.getScanRecord().getManufacturerSpecificData(0xFFFF);
            if(manufacturerData == null) return;
            if(manufacturerData.length != 8) return;

            if(manufacturerData[0] != 0x01) return;
            if(manufacturerData[1] != 0x01) return;

            ByteBuffer buffer = ByteBuffer.wrap(manufacturerData);
            buffer.order(ByteOrder.LITTLE_ENDIAN);

            boolean contact = buffer.get(2) == 0x01;

            int count = buffer.getInt(3);
            int batteryPercent = buffer.get(7);

            BLEIODevice newDevice = new BLEIODevice(
                    address,
                    contact,
                    count,
                    batteryPercent
            );

            for (BLEIODevice scannedDevice : scanResults){
                if(scannedDevice.getAddress().equals(address)){
                    if(scannedDevice.equals(newDevice)){
                        return;
                    }
                    scannedDevice.setBatteryPercent(batteryPercent);
                    scannedDevice.setContact(contact);
                    scannedDevice.setCount(count);

                    resultAdapter.notifyDataSetChanged();
                    return;
                }
            }

            scanResults.add(newDevice);
            resultAdapter.notifyDataSetChanged();
        }
    };

}