package net.anzix.mvnprof;

import java.io.File;
import java.text.ParseException;
import java.util.regex.Matcher;

import org.junit.Assert;
import org.junit.Test;

public class LogFileReaderTest {
	@Test
	public void test() throws Exception {
		LogFileReader lf = new LogFileReader(null);
		Assert.assertTrue("2011.10.09 20:56:35.562"
				.matches(lf.timeStampPattern));

		String ps = "2011.10.09 20:56:39.259 [INFO] Building util 1.2.7-dev";
		Matcher m = lf.projectStart.matcher(ps);
		Assert.assertTrue(m.matches());
		Assert.assertEquals("util", m.group(2));

		ps = "2011.10.09 20:56:35.583 [INFO] Building smali-pom 1.0-SNAPSHOT";
		m = lf.projectStart.matcher(ps);
		Assert.assertTrue(m.matches());
		Assert.assertEquals("smali-pom", m.group(2));

		ps = "2011.10.09 20:56:44.698 [INFO] Building maven-smali-plugin Maven Mojo 1.2.7-dev";
		m = lf.projectStart.matcher(ps);
		Assert.assertTrue(m.matches());
		Assert.assertEquals("maven-smali-plugin Maven Mojo", m.group(2));
		Assert.assertEquals("1.2.7-dev", m.group(3));

		ps = "2011.10.09 20:56:46.377 [INFO] Reactor Summary:";
		m = lf.reactorSummary.matcher(ps);
		Assert.assertTrue(m.matches());

		String preNo = "2011.10.09 20:56:44.473 [INFO] --- maven-jar-plugin:2.3.1:jar (default-jar) @ baksmali ---";
		String pre = "2011.10.09 20:56:39.259 [INFO] ------------------------------------------------------------------------";
		m = lf.projectStartPre.matcher(pre);
		Assert.assertTrue(m.matches());
		m = lf.projectStartPre.matcher(preNo);
		Assert.assertFalse(m.matches());

		String goal = "2011.10.09 20:56:39.283 [INFO] --- maven-resources-plugin:2.4.3:resources (default-resources) @ util ---";
		m = lf.goal.matcher(goal);
		Assert.assertTrue(m.matches());
		Assert.assertEquals("maven-resources-plugin", m.group(2).toString());

		String date = "2011.10.09 20:56:35.583";
		lf.sdf.parse(date);
	}

	@Test
	public void read() {
		LogFileReader lf = new LogFileReader(new File(
				"res/test/smali-build.log"));
		Build b = lf.read();

		Assert.assertEquals(6, b.getSubprojects().size());

		Project p = b.getSubprojects().get(0);
		Assert.assertEquals("smali-pom", p.getName());
		Assert.assertEquals("1.0-SNAPSHOT", p.getVersion());

		Assert.assertEquals(1, p.getEvents().size());

		Event e = p.getEvents().get(0);
		Assert.assertEquals("maven-install-plugin", e.getPlugin());
		Assert.assertEquals(470, e.getRunTime());

		p = b.getSubprojects().get(1);
		Assert.assertEquals(7, p.getEvents().size());

		p = b.getSubprojects().get(b.getSubprojects().size() - 1);
		Assert.assertEquals("maven-smali-plugin Maven Mojo", p.getName());
		Assert.assertEquals(9, p.getEvents().size());
		e = p.getEvents().get(p.getEvents().size() - 1);

		Assert.assertNotNull(e.getEndDate());
	}
}
