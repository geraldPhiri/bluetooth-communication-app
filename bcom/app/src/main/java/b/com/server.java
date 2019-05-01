package b.com;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.UUID;

public class server extends Activity implements Runnable{
   Thread thread;
   boolean shouldRead=true;
   BluetoothSocket socket;
   Scanner sc;
   PrintWriter pw;
   ListView msgslist;
   EditText msg1;
   ArrayList<String>msgs=new ArrayList();
   ArrayList<Integer>msgsNum=new ArrayList();
   BluetoothSocket mysocket;
   Handler h;
   protected void onCreate(Bundle s){
      super.onCreate(s);
      setContentView(R.layout.comm2);
 msgslist=(ListView)findViewById(R.id.msgs2); msg1=(EditText)findViewById(R.id.msg2);
     thread=new Thread(this);
     thread.start();
     h=new Handler(){ 
       @Override
       public void handleMessage(Message m){
       if(m.what==1)
        msgslist.setAdapter(new adapter());
                   }
       };
   }
   @Override
   public void run(){
    try{
BluetoothServerSocket bs=BluetoothAdapter.getDefaultAdapter().listenUsingRfcommWithServiceRecord("server",UUID.fromString("a0f810e2-7ccc-49af-86f5-76776bfd7705"));
socket=bs.accept();
pw=new PrintWriter(socket.getOutputStream(),true);
sc=new Scanner(socket.getInputStream());
msg1.setOnEditorActionListener(new TextView.OnEditorActionListener(){
  @Override
  public boolean onEditorAction(TextView t1,int i1,KeyEvent k1){         
         msgs.add(0,t1.getText().toString());
         msgsNum.add(0,1);
         msgslist.setAdapter(new adapter());
         pw.println(t1.getText().toString());
         msg1.setText("");
         return true;
         }
                 });
         while(shouldRead){
            String m=sc.nextLine();
            msgs.add(0,m);
            msgsNum.add(0,0);
            h.sendEmptyMessage(1);
         }
         
   }
   catch(Exception i){    
//h.sendEmptyMessage(1);   
   }    
      }
  
class adapter extends ArrayAdapter{
  adapter(){
super(server.this,android.R.layout.simple_list_item_1,msgs);
     }
  @Override
  public View getView(int i,View v,ViewGroup vg){
   if(msgsNum.get(i)==1) v=getLayoutInflater().inflate(R.layout.received,null,false);
   else v=getLayoutInflater().inflate(R.layout.received,null,false);
   TextView t=(TextView)v.findViewById(R.id.smsg);
 t.setText(msgs.get(i));
   return v;   
  }
}
protected void onDestroy(){
     super.onDestroy();
     shouldRead=false;
     if(socket!=null){
     if(pw!=null)
       pw.println("To communicate to the user, please establish connection again.");
     }
 }
  }