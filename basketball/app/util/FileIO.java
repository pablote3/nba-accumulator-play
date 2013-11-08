package util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class FileIO {
	static public Properties loadProperties(String file) throws FileNotFoundException, IOException {
		final FileInputStream in;
		Properties myProps = new Properties();
		in = new FileInputStream(file);
		myProps.load( in );
		in.close();
		return myProps;
	}
	
	static public String getPropertyPath(String key) {
		return System.getProperty(key);
	}
}
