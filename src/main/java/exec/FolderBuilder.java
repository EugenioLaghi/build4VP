package exec;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class FolderBuilder {

	/**
	 * Esempio di chiamata : java -classpath /c/DevRootMediolanum/Projects/build4VP/src/exec exec.FolderBuilder /c/Repository_Bmed/med-git/ib-fe-jbus-paypal /c/Repository_Bmed/VP/Paypal
	 * 
	 * @param args[0] = nome cartella di origine (es. /c/Repository_Bmed/med-git/ib-fe-jbus-paypal)
	 * @param args[1] = nome cartella di destinazione (es. /c/Repository_Bmed/VP/Paypal)
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException{
		System.out.println(args[0] + " " +args[1]);
		List<File> fileList = listFilesForFolder(new File(args[0]));
		
		for(File file : fileList){
			copyFile(file,args[1]);
		}
	}
	
	private static List<File> listFilesForFolder(final File folder) {
		System.out.println(folder.exists());
		System.out.println(folder.getAbsolutePath());
		List<File> out = new ArrayList<File>();
	    for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory() && !fileEntry.getName().startsWith(".")) {
	            out.addAll(listFilesForFolder(fileEntry));
	        } else {
	        	if(fileEntry.getAbsolutePath().matches(".*java.+")){
	        		out.add(fileEntry);
	        	}
	        }
	    }
	    return out;
	}
	
	public static void copyFile(File file, String path) throws IOException{
		
		if(file.exists()){
			System.out.println(file.getAbsolutePath());
		
			BufferedReader reader = new BufferedReader(new FileReader(file));

			String line = "";
			while(line.isEmpty()){
				line = reader.readLine().trim();
			}
			reader.close();
		    line = line.replaceFirst("package ", "");
		    line = line.replaceFirst(";", "");
		    
		    //Creo la cartella
		    line = line.replaceAll("\\.", "\\\\");
		    
		    line = path+"\\"+line;
		    
		    File folder = new File(line);
		    
		    if(!folder.exists()){
		    	if(folder.mkdirs()){
		    		System.out.println("Directory created");
		    	} else {
		    		System.out.println("ERROR in directory creation");
		    	}
		    }
		    
		    //Creo il file
		    String newFilePath = line+"\\"+file.getAbsolutePath().replaceAll(".*\\\\", "");
		    
		    File newFile = new File(newFilePath);
		    
		    if(newFile.createNewFile()){
		    	System.out.println("File created");
		    } else {
		    	System.out.println("ERROR - " + newFile.getAbsolutePath() + " already exists");
		    }
		    
		    //Copio il file
		    FileChannel source = new FileInputStream(file).getChannel();
		    FileChannel destination = new FileOutputStream(newFile).getChannel();

		    try{
		        long count = 0;
		        long size = source.size();              
		        while((count += destination.transferFrom(source, count, size-count))<size);
		        System.out.println("File copied");
		    } catch(Exception e){
		    	System.out.println("ERROR - "+  file.getAbsolutePath() + " NOT COPIED");
		    } finally {
		        source.close();
		        destination.close();
		    }
	        
		    System.out.println("");
	    } else {
	    	System.err.println("ERROR - " + file.getAbsolutePath() + " NOT FOUND");
	    	System.out.println("");
	    }
	}
}
