package b.com;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import b.com.R;
import java.util.ArrayList;
import java.util.List;

/**
* @author Gerald Phiri
*/
public class MainActivity extends Activity {  
  final int ENABLE_BLUETOOTH=1;
  final int MAKE_DISCOVERABLE=2;
  
  /* toClear used know whether to clear devices and deviceNames Lists in onResume
   * if connects with a device it should be set to true
   * it should be set to false in onResume
   */
  boolean toClear=false;
  
  
  ImageView playButton;
  ListView list;
  private CustomAdapter adapter;
  private BluetoothAdapter bluetoothAdapter;
  private List<BluetoothDevice> devices=new ArrayList<BluetoothDevice>();
  private List<String> deviceNames=new ArrayList<String>();
  DeviceAdder addDevices;          //BroadcastReciever

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    try{
      requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
      }
      catch(Exception excep){
        
      }
    
    playButton=(ImageView)findViewById(R.id.playbutton);
    list=(ListView)findViewById(R.id.list);
    list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
  public void onItemClick(AdapterView av, View v, int i, long l){
    playButton.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.e));
    bluetoothAdapter.cancelDiscovery();
    toClear=true;
    startActivity(new Intent(getApplicationContext(),client.class).putExtra("device",devices.get(i)));
   
  }});//list.setOnItemClickListener
  
    adapter=new CustomAdapter(this);
    bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
    addDevices=new DeviceAdder();
    registerReceiver(addDevices, new IntentFilter(BluetoothDevice.ACTION_FOUND));
    
  } //onCreate
  
  
  /*
   *clear lisview when out of
   */
  @Override
  protected void onResume(){
     super.onResume();
    if(toClear){
       deviceNames.clear();
       devices.clear();
       list.setAdapter(adapter);
       toClear=false;
    }
    
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.options,menu);
    return true;
  } //onCreateOptionsMenu
  
  @Override
  public boolean onOptionsItemSelected(MenuItem menuItem) {
    if(menuItem.getItemId()==R.id.scan) {
      scanSelected();
    }    
    if(menuItem.getItemId()==R.id.discoverable) {
      discoverableSelected();
    }
    return true;
  } //onOptionsItemSelected
  
  
  private final void scanSelected() {
    if(bluetoothAdapter.isEnabled()){
      if(!bluetoothAdapter.isDiscovering()) {
        bluetoothAdapter.startDiscovery();
      playButton.startAnimation(AnimationUtils.loadAnimation(this,
      R.anim.d));
      }
      else{
        bluetoothAdapter.cancelDiscovery();
        bluetoothAdapter.startDiscovery();
        playButton.startAnimation(AnimationUtils.loadAnimation(this,
        R.anim.d));
      }
    }
    else {
      startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),ENABLE_BLUETOOTH);
    }
  } //scanSelected
  
  
  private final void discoverableSelected() {
    if(bluetoothAdapter.getScanMode()!=BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
      startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE),MAKE_DISCOVERABLE);
    }
    else{
      startActivity(new Intent(MainActivity.this,server.class));
    }
  } //discoverableSelected
  
  @Override
  public void onActivityResult(int req,int result, Intent intent) {
    if(req==ENABLE_BLUETOOTH){
      if(result==RESULT_OK) {
        bluetoothAdapter.startDiscovery();     playButton.startAnimation(AnimationUtils.loadAnimation(this,
        R.anim.d));
      } //if result
    } //if req
    
    else if(req==MAKE_DISCOVERABLE){
      if(result!=RESULT_CANCELED){
        startActivity(new Intent(MainActivity.this,server.class));
      }//if
    }//else if
    
  } //onActivityResult
  
  
  
  public class DeviceAdder extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
      String deviceName;
      if(!deviceNames.contains(deviceName=(intent.getStringExtra(BluetoothDevice.EXTRA_NAME)))) {
     
           deviceNames.add(deviceName); devices.add((BluetoothDevice)intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE));
      list.setAdapter(adapter);
      
      }
    }  
  } //class DeviceAdder
  
  public class CustomAdapter extends ArrayAdapter {
  
    CustomAdapter(Context context) {
      super(context,android.R.layout.simple_list_item_1,deviceNames);
    }
    
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
      view=getLayoutInflater().inflate(R.layout.devicename,null,true);
      ((TextView)view.findViewById(R.id.devicename)).setText(deviceNames.get(i));
      return view;
    }
  }
  
  
  public void playClicked(View view){
   scanSelected();
  }
  
}
