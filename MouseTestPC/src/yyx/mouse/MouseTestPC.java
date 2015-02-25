package yyx.mouse;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.*;
import com.ice.jni.registry.*;

public class MouseTestPC {
    private final static byte move=1;
    private final static byte click=2;
    private final static byte cmd=3;
    private final static byte scroll=4;
    private final static byte metroMove=5;
    private final static byte metroClick=6;
    
	public static void main(String[] args){
		int dX,dY,down,control,dkey,sY,vY;
		
		try{
			DatagramSocket socket=new DatagramSocket(4567);
			while (true){
			    byte[] data=new byte[1024];
		    	DatagramPacket packet=new DatagramPacket(data,data.length);
			    socket.receive(packet);
		    	
			    if(data[0]==move){
			    	byte[] a=byteCut(data,1,4);
				    byte[] b=byteCut(data,5,8);
				    dX=byte2Int(a);
				    dY=byte2Int(b);
				    new MouseMove().move(dX,dY);
			    }
			    
			    if(data[0]==click){
			    	byte[] a=byteCut(data,1,4);
			    	down=byte2Int(a);
			    	new MouseMove().down(down);
			    }
			    
			    if(data[0]==cmd){
			    	byte[] a=byteCut(data,1,4);
				    control=byte2Int(a);
				    new Control().event(control);
			    }
			    
			    if(data[0]==scroll){
			    	byte[] a=byteCut(data,1,4);
				    sY=byte2Int(a);
				    new Scroll().scroll(sY);
			    }
			    
			    if(data[0]==metroMove){
			    	byte[] a=byteCut(data,1,4);
				    byte[] b=byteCut(data,5,8);
				    dX=byte2Int(a);
				    dY=byte2Int(b);
				    new MetroMove().move(dX,dY);
			    }
			    
			    if(data[0]==metroClick){
			    	byte[] a=byteCut(data,1,4);
			    	down=byte2Int(a);
			    	new MetroClick().Click(down);
			    }
			    
			    
			    
	    	}
		}	
		catch(Exception e){
    		e.printStackTrace();
    	}
		

	}
	
	public static int byte2Int(byte[] bytes) {
	    int addr = bytes[0] & 0xff;
	    addr += ((bytes[1] << 8) & 0xff00);
	    addr += ((bytes[2] << 16) & 0xff0000);
	    addr += ((bytes[3] << 24) & 0xff000000);
	    return addr;
	}
	
	public static byte[] byteCut(byte[] bytes,int start,int end) {
	    byte[] result=new byte[4];
		int count=0;
	    for (int i=start;i<=end;i++){
	    	result[count]=bytes[i];
	    	count++;
	    }
		return result;
		
	}
	
}

class MetroMove{
	static int x=0;
	static int y=0;
	void move(int dX,int dY){
		if(Math.abs(dX)>=Math.abs(dY)){
		    if(dX<0){
		    	if(x<0){
		    	    x=0;
		    	}    
		    	x++;
		    	y=0;
		    }
		    else{
		    	if(x>0){
		    	    x=0;
		    	} 
		    	x--;
		    	y=0;
		    }
		} 
		else{
		    if(dY<0){
		    	if(y>0){
		    	    y=0;
		    	} 
		    	y--;
		    	x=0;
		    }
		    else{
		    	if(y<0){
		    	    y=0;
		    	} 
		    	y++;
		    	x=0;
		    }
		}
		
		if(x>=5){
			try{
				Robot robot=new Robot();
				robot.keyPress(KeyEvent.VK_RIGHT);
		        robot.keyRelease(KeyEvent.VK_RIGHT);
			  }
	 	      catch(AWTException e){
		    	e.printStackTrace();
		      }
			  finally{
				  x=0;
				  y=0;
			  }
		}
		
		if(x<=-5){
			try{
				Robot robot=new Robot();
				robot.keyPress(KeyEvent.VK_LEFT);
		        robot.keyRelease(KeyEvent.VK_LEFT);
			}
	 	      catch(AWTException e){
		    	e.printStackTrace();
		      }
			  finally{
				  x=0;
				  y=0;
			  }
		 }	
		
		 if(y>=8){
			 try{
				 Robot robot=new Robot();
				 robot.keyPress(KeyEvent.VK_UP);
			     robot.delay(10);
			     robot.keyRelease(KeyEvent.VK_UP);
			     robot.delay(10);
		     }
		 	 catch(AWTException e){
			    e.printStackTrace();
			 }
			 finally{
				x=0;
				y=0;
			 }
		}
		 
		 if(y<=-8){
			 try{
				 Robot robot=new Robot();
				 robot.keyPress(KeyEvent.VK_DOWN);
			     robot.keyRelease(KeyEvent.VK_DOWN);
		     }
		 	 catch(AWTException e){
			    e.printStackTrace();
			 }
			 finally{
				x=0;
				y=0;
			 }
		}	
			
		}
	}





class MetroClick{
	static boolean flag=true;
	void Click(int down){
		if(down==1){
		    try{
		    	Robot robot=new Robot();
		    	robot.keyPress(KeyEvent.VK_ENTER);
			    robot.delay(10);
			    robot.keyRelease(KeyEvent.VK_ENTER);
			    robot.delay(10);
		    }
 	        catch(AWTException e){
	    	    e.printStackTrace();
	        }
		}
		else if(down==2){
			 try{    
				 Robot robot=new Robot();
			     robot.keyPress(KeyEvent.VK_WINDOWS);
				 robot.keyPress(KeyEvent.VK_C);
				 robot.delay(10);
				 robot.keyRelease(KeyEvent.VK_C);
				 robot.keyRelease(KeyEvent.VK_WINDOWS);
				 robot.delay(10);

			 }
	 	        catch(AWTException e){
		    	    e.printStackTrace();
		        }
		}
		else if(down==3){
			try{
				Robot robot=new Robot();
		    	robot.keyPress(KeyEvent.VK_WINDOWS);
			    robot.keyPress(KeyEvent.VK_Z);
			    robot.delay(20);
			    robot.keyRelease(KeyEvent.VK_Z);
			    robot.keyRelease(KeyEvent.VK_WINDOWS);
		    }
 	        catch(AWTException e){
	    	    e.printStackTrace();
	        }
		}	
		else if(down==4){
			if(flag==true){
			try{
				Robot robot=new Robot();
			    robot.keyPress(KeyEvent.VK_SPACE);
				robot.delay(10);
				robot.keyRelease(KeyEvent.VK_SPACE);
				robot.delay(10);
				robot.keyPress(KeyEvent.VK_TAB);
				robot.delay(10);
				robot.keyRelease(KeyEvent.VK_TAB);
				robot.delay(10);
			}
	 	    catch(AWTException e){
		    	e.printStackTrace();
		    }
			finally{
				flag=!flag;
			}
			}
			else{
				try{
					Robot robot=new Robot();
				    robot.keyPress(KeyEvent.VK_ESCAPE);
					robot.delay(10);
					robot.keyRelease(KeyEvent.VK_ESCAPE);
					robot.delay(10);
				}
		 	    catch(AWTException e){
			    	e.printStackTrace();
			    }
				finally{
					flag=!flag;
				}
			}
		}
		
	}
		
	
}







class MouseMove{
	
	void move(int dX,int dY){
		try{
	    	Robot robot=new Robot();
		    Point point=MouseInfo.getPointerInfo().getLocation();
		    robot.mouseMove(point.x-dX/25,point.y-dY/25);
		}
 	    catch(AWTException e){
	    	e.printStackTrace();
	    }
	}
	
	
	void down(int down){
		if(down==1){
		    try{
		    	Robot robot=new Robot();
		        robot.mousePress(InputEvent.BUTTON1_MASK);
		        robot.delay(10);
		        robot.mouseRelease(InputEvent.BUTTON1_MASK);
		    }
 	        catch(AWTException e){
	    	    e.printStackTrace();
	        }
		}
		else if(down==2){
			 try{  
				 Robot robot=new Robot();
			        robot.mousePress(InputEvent.BUTTON1_MASK);
			        robot.delay(10);
			        robot.mouseRelease(InputEvent.BUTTON1_MASK);
			        robot.delay(10);
			        robot.mousePress(InputEvent.BUTTON1_MASK);
			        robot.delay(10);
			        robot.mouseRelease(InputEvent.BUTTON1_MASK);
			    }
	 	        catch(AWTException e){
		    	    e.printStackTrace();
		        }
		}
		else if(down==3){
			try{
				Robot robot=new Robot();
		        robot.mousePress(InputEvent.BUTTON3_MASK);
		        robot.delay(10);
		        robot.mouseRelease(InputEvent.BUTTON3_MASK);
		    }
 	        catch(AWTException e){
	    	    e.printStackTrace();
	        }

		}
		/*else if(down==4){
			try{
				Robot robot=new Robot();
		        robot.mousePress(InputEvent.BUTTON3_MASK);
		        robot.delay(10);
		        Point point=MouseInfo.getPointerInfo().getLocation();
			    robot.mouseMove(point.x-dX/25,point.y-dY/25);
		    }
 	        catch(AWTException e){
	    	    e.printStackTrace();
	        }
		}*/
		
		
		
	}

}





class Scroll{
	void scroll(int sY){
		try{    
			if(sY>0){
			    Robot robot=new Robot();
			    robot.mouseWheel((int)(((float)-sY)/150));
			}
			else{
				Robot robot=new Robot();
				robot.mouseWheel((int)(((float)-sY)/150));
			}
	        
		}
		catch(AWTException e){
	    	e.printStackTrace();
	    }
		
		
		
		
	}
	
	
}
  

class Control{	
    private final static byte thunder=30;
    private final static byte qq=31;
    private final static byte cross=10;
    private final static byte desk=11;
    private final static byte panel=12;
    private final static byte c=13;
    private final static byte v=14;
	void event(int control){
		if(control==thunder){
			try{  
			    RegistryKey subKey= Registry.HKEY_LOCAL_MACHINE.openSubKey("Software").openSubKey("Thunder Network")
					                .openSubKey("ThunderOem").openSubKey("thunder_backwnd");
			    String path = subKey.getStringValue("Path");
			    System.out.println(path);
			    Runtime runtime = Runtime.getRuntime(); 
			    Process process = runtime.exec(path);

			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
				
		if(control==qq){
			try{  
			    RegistryKey subKey= Registry.HKEY_LOCAL_MACHINE.openSubKey("Software").openSubKey("Tencent")
					                .openSubKey("PlatForm_Type_List").openSubKey("3");
			    String path = subKey.getStringValue("TypePath");
			    System.out.println(path);
			    Runtime runtime = Runtime.getRuntime(); 
			    Process process = runtime.exec(path);

			}
			catch(Exception e){ 
				e.printStackTrace();
			}
		}
		
		if(control==cross){
			 try{
			    	Robot robot=new Robot();
			    	robot.keyPress(KeyEvent.VK_ALT);
				    robot.delay(10);
				    robot.keyPress(KeyEvent.VK_F4);
				    robot.delay(10);
				    robot.keyRelease(KeyEvent.VK_F4);
				    robot.delay(10);
				    robot.keyRelease(KeyEvent.VK_ALT);
				    robot.delay(10);
			    }
	 	        catch(AWTException e){
		    	    e.printStackTrace();
		        }	
		}
		
		if(control==desk){
			 try{
			    	Robot robot=new Robot();
			    	robot.keyPress(KeyEvent.VK_WINDOWS);
				    robot.delay(10);
				    robot.keyPress(KeyEvent.VK_D);
				    robot.delay(10);
				    robot.keyRelease(KeyEvent.VK_D);
				    robot.delay(10);
				    robot.keyRelease(KeyEvent.VK_WINDOWS);
				    robot.delay(10);
			    }
	 	        catch(AWTException e){
		    	    e.printStackTrace();
		        }	
		}
		
		if(control==panel){
			 try{
			    	Robot robot=new Robot();
			    	robot.keyPress(KeyEvent.VK_WINDOWS);
				    robot.delay(10);
				    robot.keyPress(KeyEvent.VK_X);
				    robot.delay(10);
				    robot.keyRelease(KeyEvent.VK_X);
				    robot.delay(10);
				    robot.keyRelease(KeyEvent.VK_WINDOWS);
				    robot.delay(20);
				    robot.keyPress(KeyEvent.VK_P);
				    robot.delay(10);
				    robot.keyRelease(KeyEvent.VK_P);
				    robot.delay(10);
			    }
	 	        catch(AWTException e){
		    	    e.printStackTrace();
		        }	
		}
		
		if(control==c){
			 try{
			    	Robot robot=new Robot();
			    	robot.keyPress(KeyEvent.VK_CONTROL);
				    robot.delay(10);
				    robot.keyPress(KeyEvent.VK_C);
				    robot.delay(10);
				    robot.keyRelease(KeyEvent.VK_C);
				    robot.delay(10);
				    robot.keyRelease(KeyEvent.VK_CONTROL);
				    robot.delay(10);
			    }
	 	        catch(AWTException e){
		    	    e.printStackTrace();
		        }	
		}
		
		if(control==v){
			 try{
			    	Robot robot=new Robot();
			    	robot.keyPress(KeyEvent.VK_CONTROL);
				    robot.delay(10);
				    robot.keyPress(KeyEvent.VK_V);
				    robot.delay(10);
				    robot.keyRelease(KeyEvent.VK_V);
				    robot.delay(10);
				    robot.keyRelease(KeyEvent.VK_CONTROL);
				    robot.delay(10);
			    }
	 	        catch(AWTException e){
		    	    e.printStackTrace();
		        }	
		}
		
	}
	
}

	