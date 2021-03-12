import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class serviceClient implements Runnable {
	 static Socket s;
	 static ServerSocket ss;
	 static BufferedReader buff = null;
	 static PrintStream printStream=null;

	 public serviceClient(Socket s, ServerSocket ss) {
		 this.s = s;
		 this.ss=ss;
	 }

	@Override
	public void run() {
		try {
            buff = new BufferedReader(new InputStreamReader(s.getInputStream()));
            String choice;
            while ((choice = buff.readLine()) != null) {
            	printStream = new PrintStream(s.getOutputStream());
                switch (choice) {
                    case "1":
                    	System.out.println("Client ["+s.getInetAddress().toString().split("/")[1]+"] : Client is attemping to send File");
                    	getFile();
                    	//String inGoingFileName;
                        //while((inGoingFileName = buff.readLine()) != null) {
                          //  getFile(inGoingFileName);
                        //}
                        continue;
                    case "2":
                    	System.out.println("Client ["+s.getInetAddress().toString().split("/")[1]+"] : Client is attemping to receive File");
                    	String outGoingFileName;
                        while((outGoingFileName = buff.readLine()) != null) {
                            putFile(outGoingFileName);
                        }
                        continue;
                    case "3":
                    	System.out.println("Client ["+s.getInetAddress().toString().split("/")[1]+"] : Client Application Closed\n");
                    	break;
                    default:
                        System.out.println("Input does not Match");
                        break;
                }
            }
        } catch (IOException e) {
        	System.out.println("Client request terminated, Waiting for Client's Next Request!!!\n"); 
        }
	}
	
	public void getFile() {
		
		try {
			
			//File file = new File(fileName);
			int b;
			DataInputStream dataInStream = new DataInputStream(s.getInputStream());
        	String fileName = dataInStream.readUTF();
        	System.out.println(fileName);
        	OutputStream opStream = new FileOutputStream(fileName);
        	long size = dataInStream.readLong();
        	try {
        		if(dataInStream.available() > 0 )
        		{
        			System.out.println("File Found");
        		}
        	}catch (Exception e)
        	{
        		System.out.println("File Not Found");
        	}
            byte[] buffer = new byte[2048];
            while (size > 0 && (b = dataInStream.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
                opStream.write(buffer, 0, b);
                size -= b;
            }
            opStream.close();
            System.out.println("File Downloaded: \""+fileName+"\" : from Client ["+s.getInetAddress().toString().split("/")[1]+"]\n");	
        
		}catch (IOException e) {
        	System.out.println("File Not FOund On Client Machine\nClient request terminated, Waiting for Client's Next Request!!!\n");
        }
    }

    public void putFile(String fileName) throws IOException {
    	try {
            String fname,ext,newFileName, formattedDate;
            DateTimeFormatter myFormatObj= null;
            LocalDateTime myDateObj=null;
            if(!(new File(fileName).exists())) {
        		System.out.println("File does not exist..");
        		try
        		{
        		PrintWriter out = new PrintWriter(s.getOutputStream(),true);
        		BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        		}
        		catch (Exception e)
        		{
        		System.out.println(e);
        		}
        		s.close();
        		return;
        		}
            else {
            File file = new File(fileName);  //handle file reading
        	
        		byte[] byteArray = new byte[(int) file.length()];
	            FileInputStream fileInStream = new FileInputStream(file);
	            BufferedInputStream buffInStream = new BufferedInputStream(fileInStream);
	            DataInputStream dataInStream = new DataInputStream(buffInStream);
	            dataInStream.readFully(byteArray, 0, byteArray.length);
	            OutputStream opStream = s.getOutputStream();  //handle file send over socket
	            DataOutputStream dataOpStream = new DataOutputStream(opStream); //Sending file name and file size to the server
	            fname = fileName.split("\\.")[0];
                ext = fileName.split("\\.")[1];
                myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                myDateObj = LocalDateTime.now();
                formattedDate = myDateObj.format(myFormatObj);
	            newFileName = ""+fname+" ["+ss.getInetAddress().toString().split("/")[1]+"]"+formattedDate+"."+ext;
                dataOpStream.writeUTF(newFileName);
	            dataOpStream.writeLong(byteArray.length);
	            dataOpStream.write(byteArray, 0, byteArray.length);
	            dataOpStream.flush();
	            dataInStream.close();
	            File file1 = new File("/home/kamanip/comp8567/ASP_Final_Project/Client/"+newFileName); 
                if(file1.renameTo(new File("/home/kamanip/comp8567/ASP_Final_Project/Client/Received_Files/"+newFileName))) 
                { 
                    file1.delete();  
                }
	            System.out.println("File Uploaded: \""+newFileName+"\" : to Client ["+s.getInetAddress().toString().split("/")[1]+"]\n");
            }
        } catch (Exception e) {
            System.err.println("Client request terminated, Waiting for Client's Next Request!!!\n");
        } 
    }
}
