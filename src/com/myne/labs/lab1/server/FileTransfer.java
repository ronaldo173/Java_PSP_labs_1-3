package com.myne.labs.lab1.server;

import java.io.*;
import java.util.*;

class FileTransfer {
     File workDir;


     public FileTransfer(File wd) {
        workDir = wd;
     }
     
     public String[] list() {
        String[] ls = workDir.list();
	return ls;
     }
     
     public Vector getFile(String name) {
        Vector fileContent = new Vector();
        File workFile = new File(workDir, name);
        if(workFile.exists() && workFile.isFile()) {
	   try {
	       FileReader fr = new FileReader(workFile);
	       BufferedReader br = new BufferedReader(fr);
	       boolean eof = false;
	       while(!eof) {
	   	  String str = br.readLine();
	   	  if(str == null) {
	   	     eof = true;
	   	  } else {
	   	     fileContent.addElement(str);
	   	  }
	       }
	       return fileContent;
	   } catch(FileNotFoundException fnf) {
	       System.out.println("File not found: " + name);
	       return null;
	   } catch(IOException io) {
	       System.out.println("Error during reading from " + name);
	       return null;
	   }	      
	} else {
	   return null;
	}
     }

}
