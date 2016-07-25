package generator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.net.URI;

import org.junit.Test;

public class FileNameGeneratorTest {
	private FileNameGenerator fng = new FileNameGenerator();
	
	@Test
	public void testGenerate() throws Exception {
		assertThat(fng.generate(new URI(
				"http://spatialkeydocs.s3.amazonaws.com/FL_insurance_sample.csv.zip")),
			equalTo("FL_insurance_sample.csv.zip"));
		assertThat(fng.generate(new URI("ftp://speedtest.tele2.net/1MB.zip")),
            equalTo("1MB.zip"));
		assertThat(fng.generate(new URI(
				"sftp://demo:password@test.rebex.net/readme.txt")),
			equalTo("readme.txt"));
	}

}
