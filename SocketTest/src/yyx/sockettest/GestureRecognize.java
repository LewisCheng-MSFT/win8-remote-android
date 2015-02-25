package yyx.sockettest;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

import yyx.sockettest.SocketTest.ServerThread;

import android.os.Bundle;
import android.app.Activity;
import android.gesture.*;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class GestureRecognize extends Activity implements OnGesturePerformedListener{
    GestureLibrary mLibrary;
    int control;
    private final byte cmd=3;
    private final byte cross=10;
    private final byte desk=11;
    private final byte panel=12;
    private final byte c=13;
    private final byte v=14;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gestureview);
        GestureOverlayView gesture=(GestureOverlayView) findViewById(R.id.gestures);
        gesture.addOnGesturePerformedListener(this);
        mLibrary=GestureLibraries.fromRawResource(this,R.raw.gestures);
        if(!mLibrary.load()){
        	finish();
        }
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

	@Override
	public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
		// TODO Auto-generated method stub
		ArrayList predictions=mLibrary.recognize(gesture);
		if(predictions.size()>0){
			Prediction prediction=(Prediction) predictions.get(0);
			if(prediction.score>1.0){
				Toast.makeText(this,prediction.name,Toast.LENGTH_SHORT).show();
				if(prediction.name.equals("cross")){
					control=cross;
					new ServerThread().start();
				}
				if(prediction.name.equals("desk")){
					control=desk;
					new ServerThread().start();
				}
				if(prediction.name.equals("panel")){
					control=panel;
					new ServerThread().start();
				}
				if(prediction.name.equals("c")){
					control=c;
					new ServerThread().start();
				}
				if(prediction.name.equals("v")){
					control=v;
					new ServerThread().start();
				}
				
				
			}
		}
	}

    
	class ServerThread extends Thread{

		@Override
		public synchronized void start() {
			// TODO Auto-generated method stub
			super.start();

		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try{
				String ip="192.168.1.2";
				InetAddress pcaddress=InetAddress.getByName(ip);

				if(control!=0){
					byte[] i1=int2Byte(control);
					byte[] data=new byte[9];
					data[0]=cmd;
					System.arraycopy(i1, 0, data, 1, i1.length);
			        DatagramSocket socket=new DatagramSocket(4567);
			        DatagramPacket packet=new DatagramPacket(data,data.length,pcaddress,4567);
			        socket.send(packet);
			        Log.d("MyDebug","SendControl!!!");
		            control=0;
		            socket.close();
				}
				
				
			}
			catch(Exception e){
				e.printStackTrace();
			}
			//super.run();
		}

		
		}


		//-----------------------FunctionHelpsToSendData-----------------------------	

		 public static byte[] int2Byte(int i) {   
			  byte[] result = new byte[4];   
			  result[3] = (byte)((i >> 24) & 0xFF);
			  result[2] = (byte)((i >> 16) & 0xFF);
			  result[1] = (byte)((i >> 8) & 0xFF); 
			  result[0] = (byte)(i & 0xFF);
			  return result;
		 }
    
    
}

