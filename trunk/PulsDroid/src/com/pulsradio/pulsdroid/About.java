package com.pulsradio.pulsdroid;

import java.util.regex.Pattern;

import android.app.Activity;
import android.os.Bundle;
import android.text.util.Linkify;
import android.widget.TextView;

public class About extends Activity {
	
	private TextView link1;
	private TextView link2;
	private Pattern pattern1;
	private Pattern pattern2;
	private String text1;
	private String text2;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        
        link1 = (TextView) findViewById(R.id.link);
        text1 = "http://code.google.com/p/pulsdroid/";
        link1.setText(text1);
        pattern1 = Pattern.compile("http://code.google.com/p/pulsdroid/");
        Linkify.addLinks(link1, pattern1, "http://");
        
        link2 = (TextView) findViewById(R.id.copyright);
        text2 = getString(R.string.about_copyright);
        link2.setText(text2);
        pattern2 = Pattern.compile("www.pulsradio.com");
        Linkify.addLinks(link2, pattern2, "http://");
	}
}
