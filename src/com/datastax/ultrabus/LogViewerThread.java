package com.datastax.ultrabus;

/*
 * Slurp the logfile all at once into a collection that can be displayed in
 * the log viewer.
 * 
 * I thought about setting up a TCP socket and configure JMX to log to it, but then
 * I came to my senses and realized that the Windows firewall will drive everybody insane
 * so let's not do that. This is crazy ugly to do, but for the purposes of the UltraBus
 * launcher, it's better to simply read the file every second if it increases in size.
 */

import java.util.List;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
 
public class LogViewerThread implements Runnable {
	public long previousLogSize = 0;
	private Path logfile = Paths.get("C:\\Users\\al\\eclipse\\artifacts.xml");
	private LogViewerPane logPane;

	public LogViewerThread(LogViewerPane lp) {
		logPane = lp;
	}
	
	@Override
	public void run() {
		while (true) {
			try {
				if (updateAvailable()) {
					update();
				}
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void setLogfilePath(Path newPath) {
		previousLogSize = 0;
		logfile = newPath;
	}
	
	public boolean updateAvailable() {
		long sz = logfile.toFile().length();
		if (sz > previousLogSize) {
			System.out.println("size changed\n");
			return true;
		}
		else {
			System.out.println("no update\n");
			return false;
		}
	}
	
	public void update() {
		previousLogSize = logfile.toFile().length();
		// potential race here, unimportant for now
		logPane.setText(slurp());
	}
	
	public String slurp() {
			try {
				List<String> lines = Files.readAllLines(logfile, Charset.defaultCharset());
				return lines.toString(); // TODO: probably wrong
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
	}

}