package net.anzix.mvnprof;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ConsoleOutput implements Output {

	PrintStream out;

	public ConsoleOutput() {
		super();
		out = System.out;
	}

	@Override
	public void output(Build b) {
		out.println("Stats by projects:");
		List<Project> projects = new ArrayList();
		projects.addAll(b.getSubprojects());
		Collections.sort(projects, new Comparator<Project>() {

			@Override
			public int compare(Project o1, Project o2) {
				return Long.valueOf(o1.getRunTime()).compareTo(
						Long.valueOf(o2.getRunTime()))
						* -1;
			}
		});
		for (Project p : projects) {
			List<Event> es = new ArrayList(p.getEvents());
			Collections.sort(es, new Comparator<Event>() {

				@Override
				public int compare(Event o1, Event o2) {
					return Long.valueOf(o2.getRunTime()).compareTo(Long.valueOf(o1.getRunTime()));
				}
			});
			out.format("%-5s %s %s\n",p.getRunTime(),p.getName(),p.getVersion());
			for (Event e : es){
				out.format("   %-5s %-30s %-40s %s\n",e.getRunTime(),e.getExecutionId(),e.getPlugin(),e.getVersion());
			}								
		}
	}

}
