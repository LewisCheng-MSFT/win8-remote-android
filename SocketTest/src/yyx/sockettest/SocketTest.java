package yyx.sockettest;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.speech.RecognizerIntent;
import android.support.v4.app.NavUtils;

public class SocketTest extends Activity implements OnTouchListener,OnGestureListener,OnDoubleTapListener{
    TextView textview1;
    Button button1,change;
    GestureDetector detector=new GestureDetector(this);
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
    int dX,dY,sY;
    int control,dkey;
    int down=0;
    int count=1;
    int width;
    Long LastClickTime;
    private final byte move=1;
    private final byte click=2;
    private final byte cmd=3;
    private final byte scroll=4;
    private final byte metroMove=5;
    private final byte metroClick=6;
    private final byte thunder=30;
    private final byte qq=31;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        width = metric.widthPixels;
        Log.d("MyDebug",String.valueOf(width));
        button1=(Button) findViewById(R.id.button1);
        change=(Button) findViewById(R.id.change);
        textview1=(TextView) findViewById(R.id.textview1);
        change.setOnClickListener(new ChangeListener());
        textview1.setOnTouchListener(this);
        textview1.setFocusable(true);
        textview1.setClickable(true);
        textview1.setLongClickable(true);
        detector.setIsLongpressEnabled(true);
        PackageManager pm=getPackageManager();
        List activities=pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size()!=0){
        	button1.setOnClickListener(new MyClickListener());
        }
        else{
        	button1.setText(R.string.HaveNotVoiceApp);
        }
    }	
    
    
    //--------------------------ModeChange---------------------------
    
    public class ChangeListener implements OnClickListener{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent ChangeIntent =new Intent();
			ChangeIntent.setClass(SocketTest.this,GestureRecognize.class);
			SocketTest.this.startActivity(ChangeIntent);
		}

    }
    
    
    //-------------------------VoiceRecognize------------------------------------
    public class MyClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (v.getId()==R.id.button1){
				startVoiceRecognitionActivity();
				//Intent intent=new Intent();
			    //intent.setClass(SocketTest.this, VoiceRecognize.class);
			    //SocketTest.this.startActivity(intent);
			}			
			
		}
    }
    
    private void startVoiceRecognitionActivity() {
    	Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
    	intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
    	intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "开始语音识别");
    	startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }
    

    
  @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (requestCode==VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK){
		    ArrayList results=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
		    String resultsString="";
		    if (results.size()!=0){
		    	for(int i=0;i<=4;i++){
		    	    resultsString=""+results.get(i);  
		    	    Recognize(resultsString);
		    	}
		    }

		}

	  super.onActivityResult(requestCode, resultCode, data);
	}

  
    public void Recognize(String voice){
    	if (voice.equals("启动迅雷")||voice.equals("启动讯雷")){
    		control=thunder;
            Toast.makeText(this, "启动迅雷", Toast.LENGTH_LONG).show();
    		new ServerThread().start();
    	}
    	
    	if (voice.equals("启动qq")||voice.equals("启动 qq")){
    		control=qq;
            Toast.makeText(this, "启动qq", Toast.LENGTH_LONG).show();
    		new ServerThread().start();
    	}
    	
    }
    


	//----------------------------TouchEvent---------------------------------------	
    @Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
    	return false;
	}




	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		return false;
	}





	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		Log.d("MyDebug", "LongPress");
		down=4;
		new ServerThread().start();
	}





	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub     
		Log.d("MyDebug",String.valueOf(e1.getX()));
		if (e1.getX()<((4.0/5.0)*width)){    
		    if (Math.abs(distanceX)>=4||Math.abs(distanceY)>=4){
		        down=0;
		        sY=0;
		    	dX=(int)(e1.getX()-e2.getX());
		        dY=(int)(e1.getY()-e2.getY());
                new ServerThread().start();
		    }    
		}
		else{
			sY=(int)(e1.getY()-e2.getY());
			new ServerThread().start();
		}
		return false;
	}





	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}





	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}


    


	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		int pointcount=event.getPointerCount();
        if (pointcount>=2){
        	Log.d("MyDebug","2points");
        	down=3;
        	new ServerThread().start();
        }
        /*if(pointcount==1&&event.getAction()==event.ACTION_DOWN){
        	if(count==1){
        	    LastClickTime=System.currentTimeMillis();
        	    count++;
        	}    
        	if(count==2&&System.currentTimeMillis()-LastClickTime<=500){
        		Log.d("MyDebug","Debug...");
        		X1=(int) event.getX();
        		Y1=(int) event.getY();
        		down=4;
        		count=1;
        	}
        }
        if(down==4&&event.getAction()==event.ACTION_MOVE){
        	X2=(int) event.getX();
    		Y2=(int) event.getY();
    		dX=X2-X1;
    		dY=Y2-Y1;
    		new ServerThread().start();
        }*/
		return detector.onTouchEvent(event); 
	}


	

	@Override
	public boolean onDoubleTap(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {
		// TODO Auto-generated method stub
		if(e.getAction()==e.ACTION_UP){
			Log.d("MyDebug","DoubleClick");
			down=2;
			new ServerThread().start();
		}
		return false;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		// TODO Auto-generated method stub
		Log.d("MyDebug", "SingleClick");
		down=1;
		new ServerThread().start();
		return false;
	}




    //---------------------------------SendData------------------------------------


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
			String dd=String.valueOf(down);
			Log.d("MyDebug",dd);
			String ip="192.168.1.2";
			InetAddress pcaddress=InetAddress.getByName(ip);
			
			
			
			if(dX!=0||dY!=0){
				byte[] i1=int2Byte(dX);
				byte[] i2=int2Byte(dY);
				byte[] data=new byte[9];
				data[0]=metroMove;
				System.arraycopy(i1, 0, data, 1, i1.length);
		        System.arraycopy(i2, 0, data, i1.length+1, i2.length);
		        DatagramSocket socket=new DatagramSocket(4567);
		        DatagramPacket packet=new DatagramPacket(data,data.length,pcaddress,4567);
		        socket.send(packet);
		        Log.d("MyDebug","SendMetro!!!");
	            down=0;
	            control=0;
	            dkey=0;
	            dX=0;
	            dY=0;
	            sY=0;
	            socket.close();
			}
			
			
			if(down!=0){
				byte[] i1=int2Byte(down);
				byte[] data=new byte[5];
				data[0]=metroClick;
				System.arraycopy(i1, 0, data, 1, i1.length);
				DatagramSocket socket=new DatagramSocket(4567);
				DatagramPacket packet=new DatagramPacket(data,data.length,pcaddress,4567);
			    socket.send(packet);
			    Log.d("MyDebug","SendMetroDown!!!");
		        down=0;
		        control=0;
		        dkey=0;
		        dX=0;
	            dY=0;
	            sY=0;
		        socket.close();
			}
			
			
			if(control!=0||dkey!=0){
				byte[] i1=int2Byte(control);
				byte[] i2=int2Byte(dkey);
				byte[] data=new byte[9];
				data[0]=cmd;
				System.arraycopy(i1, 0, data, 1, i1.length);
		        System.arraycopy(i2, 0, data, i1.length+1, i2.length);
		        DatagramSocket socket=new DatagramSocket(4567);
		        DatagramPacket packet=new DatagramPacket(data,data.length,pcaddress,4567);
		        socket.send(packet);
		        Log.d("MyDebug","SendControl!!!");
	            down=0;
	            control=0;
	            dkey=0;
	            dX=0;
	            dY=0;
	            sY=0;
	            socket.close();
			}
			
			
			if(sY!=0){
				byte[] i1=int2Byte(sY);
				byte[] data=new byte[5];
				data[0]=scroll;
				System.arraycopy(i1, 0, data, 1, i1.length);
		        DatagramSocket socket=new DatagramSocket(4567);
		        DatagramPacket packet=new DatagramPacket(data,data.length,pcaddress,4567);
		        socket.send(packet);
		        Log.d("MyDebug","SendScroll!!!");
	            down=0;
	            control=0;
	            dkey=0;
	            dX=0;
	            dY=0;
	            sY=0;
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

	 
	 

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    
}

