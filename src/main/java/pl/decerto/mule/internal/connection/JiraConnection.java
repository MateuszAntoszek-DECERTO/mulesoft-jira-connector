package pl.decerto.mule.internal.connection;


import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClientFactory;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class JiraConnection {

	private final Logger LOGGER = LoggerFactory.getLogger(JiraConnection.class);

	private JiraRestClient restClient;

	public JiraConnection(String url, String userName, String password) {
		try {
			JiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
			URI jiraServerUri = new URI(url);
			restClient = factory.createWithBasicHttpAuthentication(jiraServerUri, userName, password);
			LOGGER.debug("Connecting to jira: Success" );
		} catch (Exception e) {
			LOGGER.error("Connecting to jira: Error", e);
		}
	}

	public JiraRestClient getClient() {
		return restClient;
	}
}
