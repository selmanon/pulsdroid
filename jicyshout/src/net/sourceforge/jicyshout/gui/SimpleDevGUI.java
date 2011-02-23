package net.sourceforge.jicyshout.gui;

import java.io.BufferedInputStream;
import java.net.URL;
import java.net.URLConnection;

import net.sourceforge.jicyshout.jicylib1.IcyInputStream;

public class SimpleDevGUI {

	public static void main (String args[]) {
        byte[] chow = new byte[200];
        
        try {
            URL url = new URL ("http://stream.pulsradio.com:5000/");
            URLConnection conn = url.openConnection();
            conn.setRequestProperty ("Icy-Metadata", "1");

            IcyInputStream icy = new IcyInputStream(new BufferedInputStream(conn.getInputStream()));
            icy.read();
            
            System.out.println("icy-notice1:" + icy.IcyTagged("icy-notice1"));
            System.out.println("icy-notice2:" + icy.IcyTagged("icy-notice2"));
            System.out.println("icy-name:" + icy.IcyTagged("icy-name"));
            System.out.println("icy-genre:" + icy.IcyTagged("icy-genre"));
            System.out.println("icy-url:" + icy.IcyTagged("icy-url"));
            System.out.println("content-type:" + icy.IcyTagged("content-type"));
            System.out.println("icy-pub:" + icy.IcyTagged("icy-pub"));            
            System.out.println("icy-metaint:" + icy.IcyTagged("icy-metaint"));
            System.out.println("icy-br:" + icy.IcyTagged("icy-br"));
            
         

            while (icy.available() > -1) {
            	icy.read(chow, 0, chow.length);
            }
            
            
            //System.out.println("Test");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
