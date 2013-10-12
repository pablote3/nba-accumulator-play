package util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class FileIO {
	static public Properties loadProperties(String key) throws FileNotFoundException, IOException {
		final FileInputStream in;
		String propPath = getPropertyPath(key) + "\\properties\\service.properties";
		Properties myProps = new Properties();
		in = new FileInputStream(propPath);
		myProps.load( in );
		in.close();
		return myProps;
	}
	
	static public String getPropertyPath(String key) {
		return System.getProperty(key);
	}
}
