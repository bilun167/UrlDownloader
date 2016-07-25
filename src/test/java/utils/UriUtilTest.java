package utils;

import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class UriUtilTest {

	@Test
	public void testParseUserInfo() throws Exception {
		String[] parsedInfo = {"tai", "pass"};
		assertThat(UriUtil.parseUserInfo("tai:pass"), equalTo(parsedInfo));
	}
	
	@Test
	public void testAnotherParseUserInfo() throws Exception {
		String[] parsedInfo = {"anonymous", ""};
		assertThat(UriUtil.parseUserInfo("sth_without_password"), equalTo(parsedInfo));
	}

}
