package net.anzix.mvnprof;

import java.util.ArrayList;
import java.util.List;

public class Build {
	private List<Project> subprojects = new ArrayList();

	public List<Project> getSubprojects() {
		return subprojects;
	}

	public void setSubprojects(List<Project> subprojects) {
		this.subprojects = subprojects;
	}

	public void addProject(Project current) {
		subprojects.add(current);

	}

}
