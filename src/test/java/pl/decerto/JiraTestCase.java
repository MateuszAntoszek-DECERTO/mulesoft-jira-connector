package pl.decerto;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import com.atlassian.jira.rest.client.api.domain.Issue;
import org.junit.Test;
import org.mule.functional.junit4.MuleArtifactFunctionalTestCase;

public class JiraTestCase extends MuleArtifactFunctionalTestCase {

	@Override
	protected String getConfigFile() {
		return "test-mule-config.xml";
	}

	@Test
	public void executeCreateSpreadsheets() throws Exception {
		Issue issue = ((Issue) flowRunner("getIssue").run()
				.getMessage()
				.getPayload()
				.getValue());
		assertThat(issue.getSummary(), is(org.hamcrest.core.IsNull.notNullValue(String.class)));
	}
}
