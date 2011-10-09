package net.anzix.mvnprof;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogFileReader implements BuildReader {

	private File buildLog;

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd hh:mm:ss.SSS");

	String timeStampPattern = "(\\d{4}\\.\\d{2}\\.\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3})";

	Pattern projectStartPre;
	Pattern projectStart;
	Pattern goal;
	Pattern reactorSummary;

	public LogFileReader(File file) {
		this.buildLog = file;
		projectStart = Pattern.compile(timeStampPattern
				+ " \\[INFO\\] Building (.+) ([^\\s]+)");
		projectStartPre = Pattern.compile(timeStampPattern
				+ " \\[INFO\\] \\-{50,}");
		goal = Pattern
				.compile(timeStampPattern
						+ " \\[INFO\\] \\-{3} ([^\\s\\:]+)\\:([^\\s\\:]+)\\:([^\\s\\:]+) (\\(.+\\)) @ (.+) \\-{3}");
		reactorSummary = Pattern.compile(timeStampPattern
				+ " \\[INFO\\] Reactor Summary\\:");
	}

	@Override
	public Build read() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(buildLog));
			Build b = new Build();
			String line = "";
			String prevLine = "";
			Project current = null;
			Event lastEvent = null;
			while ((line = reader.readLine()) != null) {
				Matcher l = projectStart.matcher(line);
				Matcher pl = projectStartPre.matcher(prevLine);
				if (l.matches() && pl.matches()) {
					Date time = sdf.parse(l.group(1));
					if (current != null) {
						b.addProject(current);
						current.setEndDate(time);
					}
					if (lastEvent != null) {
						lastEvent.setEndDate(time);
						lastEvent = null;
					}
					current = new Project();
					current.setName(l.group(2));
					current.setVersion(l.group(3));
					current.setStartDate(time);
				} else {
					Matcher mg = goal.matcher(line);
					if (mg.matches()) {
						Event e = new Event();
						e.setPlugin(mg.group(2));
						e.setVersion(mg.group(3));
						e.setGoal(mg.group(4));
						e.setExecutionId(mg.group(5));
						Date time = sdf.parse(mg.group(1));
						e.setStartDate(time);
						current.addEvent(e);
						if (lastEvent != null) {
							lastEvent.setEndDate(time);
						}
						lastEvent = e;
					} else {
						Matcher mrs = reactorSummary.matcher(line);
						if (mrs.matches()){
							Date time = sdf.parse(mrs.group(1));
							current.setEndDate(time);
							lastEvent.setEndDate(time);
							b.addProject(current);
							current = null;
							lastEvent = null;
						}
					}
				}
				prevLine = line;
			}
			reader.close();
			return b;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ProfException(e);
		}
	}
}
