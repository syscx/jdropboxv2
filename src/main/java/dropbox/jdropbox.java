package dropbox;
import java.io.*;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class jdropbox {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
    	TimerTask task = new schetimer();
   	 
    	Timer timer = new Timer();
    	timer.schedule(task, 1000,60000);		
		//This one, you should make directory search and find 2015- start
	}

}
