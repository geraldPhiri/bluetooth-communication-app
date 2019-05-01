package b.com;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
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

public class client extends Activity implements Runnable{
   boolean shouldRead=true;
   Thread thread;
   Handler h;
   Scanner sc;
   PrintWriter pw;
   ListView msgslist;
   EditText msg;
   ArrayList<String>msgs=new ArrayList();
   ArrayList<Integer>msgsNum=new ArrayList();
   BluetoothSocket socket=null;
   protected void onCreate(Bundle s){
      super.onCreate(s);
      setContentView(R.layout.comm);     msgslist=(ListView)findViewById(R.id.msgs);
      msg=(EditText)findViewById(R.id.msg);  
      Intent intent=getIntent();
      BluetoothDevice device=(BluetoothDevice) intent.getParcelableExtra("device");
      h=new Handler(){ 
       @Override
       public void handleMessage(Message m){
         
         msgslist.setAdapter(new adapter());
                   }
        };
       try{
socket=device.createRfcommSocketToServiceRecord(UUID.fromString("a0f810e2-7ccc-49af-86f5-76776bfd7705"));
         thread=new Thread(this);
         thread.start();
       }     
       catch(Exception e){}        
       }
  @Override
  public void run(){
    try{ 
       socket.connect();    
       pw=new PrintWriter(socket.getOutputStream(),true);       
sc=new Scanner(socket.getInputStream());
msg.setOnEditorActionListener(new TextView.OnEditorActionListener(){
        @Override
        public boolean onEditorAction(TextView t,int i,KeyEvent k){         
         msgs.add(0,t.getText().toString());
         msgsNum.add(0,1);
         msgslist.setAdapter(new adapter());
         pw.println(t.getText().toString());
         msg.setText("");
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
    catch(Exception z){
    //h.sendEmptyMessage(1);
    }
    }
  
  class adapter extends ArrayAdapter{
  adapter(){
super(client.this,android.R.layout.simple_list_item_1,msgs);
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