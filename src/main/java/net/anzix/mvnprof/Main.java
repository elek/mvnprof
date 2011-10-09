package net.anzix.mvnprof;

import java.io.File;

public class Main {
	private File file;

	public Main(File file) {
		super();
		this.file = file;
	}

	public static void main(String[] args) {
		new Main(new File(args[0])).start();
	}

	private void start() {
		BuildReader reader = new LogFileReader(file);
		Build b = reader.read();
		Output out = new ConsoleOutput();
		out.output(b);

	}
}
