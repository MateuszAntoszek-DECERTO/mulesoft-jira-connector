package pl.decerto.mule.internal.source;

import com.atlassian.jira.rest.client.api.RestClientException;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.util.concurrent.Promise;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.runtime.source.PollContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.decerto.mule.internal.source.results.JiraChangeAttributes;
import pl.decerto.mule.internal.source.results.JiraChangePayload;

@DisplayName("On JQL Query")
@Alias(value = "jira-jql-listener")
public class JiraJqlListener extends JiraListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(JiraJqlListener.class);

	@Parameter
	@DisplayName("JQL Query")
	private String jqlQuery;

	@Override
	public void poll(PollContext<JiraChangePayload, JiraChangeAttributes> pollContext) {
		if (pollContext.isSourceStopping()) {
			return;
		}
		String query = getJqlQuery();
		LOGGER.debug("JQL " + query);
		Promise<SearchResult> searchResultPromise = client.getSearchClient().searchJql(query);
		try {
			SearchResult result = searchResultPromise.claim();
			acceptChanges(pollContext, result.getIssues());
		} catch (RestClientException e) {
			LOGGER.error("There is an issue with REST API.", e);
		} catch (Exception e) {
			LOGGER.error("Error while executing JQL query ", e);
		}
	}

	@Override
	String getJqlQuery() {
		return jqlQuery;
	}
}