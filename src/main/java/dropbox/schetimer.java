package dropbox;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;
import java.util.TimerTask;
import java.util.Vector;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWebAuthNoRedirect;
import com.dropbox.core.DbxWriteMode;


public class schetimer extends TimerTask{
	@Override
	public void run() {
		Vector<String> homedirectory = readconf();
		
		System.out.println("do checker");
		File folder = new File(homedirectory.elementAt(0));
		File[] listOfFiles = folder.listFiles(); 
		String files;
		
		for (int i = 0; i < listOfFiles.length; i++) 
		{
		 
			if (listOfFiles[i].isDirectory()) 
			{
				files = listOfFiles[i].getName();
				if(files.startsWith("20"))
				{
					System.out.println("I think I should do the dir "+files);
					docheckdir(files,homedirectory.elementAt(1),homedirectory.elementAt(2),homedirectory.elementAt(3));
				}

		    }
		}
	}
	
	public Vector<String> readconf()
	{
		BufferedReader br = null;
		Vector<String> vs = new Vector<String>();
		 
		try {
 
			String sCurrentLine;
 
			br = new BufferedReader(new FileReader("jdropconf.txt"));
 
			while ((sCurrentLine = br.readLine()) != null) {
				vs.add(sCurrentLine);
				System.out.println("The conf file = "+sCurrentLine);
			}
 
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return vs;
		
	}
	

	public void docheckdir(String boxdir,String appkey,String APPSECRET,String tokenacc)
	{
		String files;
		File folder = new File(boxdir);
		File[] listOfFiles = folder.listFiles(); 
		 
		// Get your app key and secret from the Dropbox developers website.
		final String APP_KEY = appkey;
		final String APP_SECRET = APPSECRET;
		
		DbxAppInfo appInfo = new DbxAppInfo(APP_KEY, APP_SECRET);
		
		DbxRequestConfig config = new DbxRequestConfig("JavaTutorial/1.0",Locale.getDefault().toString());
		DbxWebAuthNoRedirect webAuth = new DbxWebAuthNoRedirect(config, appInfo);
		
		String fullpath="";
		
		// Have the user sign in and authorize your app.
		String authorizeUrl = webAuth.start();
//		System.out.println("1. Go to: " + authorizeUrl);
//		System.out.println("2. Click \"Allow\" (you might have to log in first)");
//		System.out.println("3. Copy the authorization code.");
		try
		{
//			String code = new BufferedReader(new InputStreamReader(System.in)).readLine().trim();
	
			// This will fail if the user enters an invalid authorization code.
			//  DbxAuthFinish authFinish = webAuth.finish(code);
			//String accessToken = authFinish.accessToken;
			String accessToken = tokenacc;
	
			DbxClient client = new DbxClient(config, accessToken);
	
			System.out.println("Linked account: " + client.getAccountInfo().displayName);
	
			DbxEntry.WithChildren listing = client.getMetadataWithChildren("/udoocamera/"+boxdir);
			
			for (int i = 0; i < listOfFiles.length; i++) 
			{
				files = listOfFiles[i].getName();
				fullpath="/home/ubuntu/"+boxdir+"/"+files;
				int doupload = 1;
				if( !(files.endsWith(".JPG")||files.endsWith(".jpg"))  )
				{
					doupload =0;
				}

				if(listing!=null && doupload==1)
				{
					for (DbxEntry child : listing.children) 
					{
						if (listOfFiles[i].isFile()) 
						{
							if (files.equalsIgnoreCase(child.name) )
							{
								doupload = 0;
							}
						}
					}
				}
				
				if(doupload ==1)
				{
					System.out.println("start to send data");
					File inputFile = new File(fullpath);
					FileInputStream inputStream = new FileInputStream(inputFile);
					try 
					{
						DbxEntry.File uploadedFile = client.uploadFile("/udoocamera/"+boxdir+"/"+files,
						DbxWriteMode.add(), inputFile.length(), inputStream);
						System.out.println("Uploaded: " + uploadedFile.toString());
					} 
					finally 
					{
						inputStream.close();
					}
				}
				else
					System.out.println(doupload+"file"+files);
			}
			
			System.out.println("End!");
	  }
	  catch(Exception ex)
	  {
	  	ex.printStackTrace();
	  }	
	}
	
}
