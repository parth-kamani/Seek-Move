import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Client {
	static Socket s;
    static String fileName;
    static BufferedReader buff = new BufferedReader(new InputStreamReader(System.in));
    static PrintStream printStream;
    
	public static void main(String[] args) throws IOException {
		while(true){
		String choice;
		//System.out.print("Enter Server IP: ");
		//String ip = buff.readLine();
		String ip = "137.207.82.53";
		try {
            s = new Socket(ip, 2399);
        } catch (Exception e) {
            System.err.println("No Server Found!!!");
            System.exit(1);
        }
		printStream = new PrintStream(s.getOutputStream());
		System.out.println("Type \"1\" to Upload File.");
        System.out.println("Type \"2\" to Download File.");
        System.out.println("Type \"3\" Exit.");
        System.out.print("\nEnter: ");
        choice= buff.readLine();
        try {
            switch (Integer.parseInt(choice)) {
	            case 1:
	            	printStream.println("1");
	                System.out.println("\n\"To Upload\" SYNOPSIS: put fileName (Simply Enter File Name)");
	            	System.out.print("put ");
	                fileName = buff.readLine();
	                putFile(fileName);
	                continue;
	            case 2:
	            	printStream.println("2");
	                System.out.println("\"To Download\" Synopsis: get fileName (Simply Enter File Name)");
	                System.out.print("get ");
	                fileName = buff.readLine();
	                printStream.println(fileName);
	                getFile(fileName);
	                continue;
	            case 3:
	            	printStream.println("3");
	            	System.out.println("Client Application Terminated");
	            	s.close();
	            	System.exit(1);
	            default:
                    System.out.println("Input does not Match");
                    break;
	        	}
	        } catch (Exception e) {
	            System.err.println(e);
	        }
		}
	}
	
	public static void putFile(String fileName) {
        try {
        	String fname,ext,newFileName, formattedDate;
            DateTimeFormatter myFormatObj= null;
            LocalDateTime myDateObj=null;
        	File file = new File(fileName);
        	if(!file.exists()) {
        		System.out.println("File does not exist..");
        		try
        		{
        		PrintWriter out = new PrintWriter(s.getOutputStream(),
        		true);
        		BufferedReader in = new BufferedReader(new
        		InputStreamReader(s.getInputStream()));
        		}
        		catch (Exception e)
        		{
        			System.out.println(e);
        		}
        		s.close();
        		return;
        		}
        else {
        	byte[] byteArray = new byte[(int) file.length()];
            FileInputStream fileInStream = new FileInputStream(file);
            BufferedInputStream buffInStream = new BufferedInputStream(fileInStream);
            DataInputStream dataInStream = new DataInputStream(buffInStream);
            dataInStream.readFully(byteArray, 0, byteArray.length);
            OutputStream opStream = s.getOutputStream();
            //Sending file name and file size to the server
            DataOutputStream dataOpStream = new DataOutputStream(opStream);
            fname = fileName.split("\\.")[0];
            ext = fileName.split("\\.")[1];
            myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            myDateObj = LocalDateTime.now();
            formattedDate = myDateObj.format(myFormatObj);
            newFileName = ""+fname+" ["+s.getInetAddress().toString().split("/")[1]+"]"+formattedDate+"."+ext;
            dataOpStream.writeUTF(newFileName);
            dataOpStream.writeLong(byteArray.length);
            dataOpStream.write(byteArray, 0, byteArray.length);
            dataOpStream.flush();
            dataInStream.close();
            File file1 = new File("/home/kamanip/comp8567/ASP_Final_Project/Server/"+newFileName); 
            if(file1.renameTo(new File("/home/kamanip/comp8567/ASP_Final_Project/Server/Received_Files/"+newFileName))) 
            { 
                file1.delete();  
            }
            System.out.println("File Uploaded: \""+newFileName+"\" : to Server ["+s.getInetAddress().toString().split("/")[1]+"]\n");
        }
        } catch (Exception e) {
            System.err.println(e);
        }
        return;
    }

    public static void getFile(String fileName) {
        try {
            int b;
            InputStream inStream = s.getInputStream();
            DataInputStream dataInStream = new DataInputStream(inStream);
            fileName = dataInStream.readUTF();
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
            inStream.close();
            System.out.println("File Downloaded: \""+fileName+"\" : from Server ["+s.getInetAddress().toString().split("/")[1]+"]\n");            
        } catch (IOException e) {
        	System.out.println("File Not Found on Server Machine!!!\n");
         }
    
    }
}
