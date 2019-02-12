package pl.decerto.mule.internal.source;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.util.concurrent.Promise;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.connection.ConnectionProvider;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.param.Config;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.runtime.extension.api.runtime.source.PollContext;
import org.mule.runtime.extension.api.runtime.source.PollingSource;
import org.mule.runtime.extension.api.runtime.source.SourceCallbackContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.decerto.mule.internal.config.BasicConfiguration;
import pl.decerto.mule.internal.connection.JiraConnection;
import pl.decerto.mule.internal.source.results.JiraChangeAttributes;
import pl.decerto.mule.internal.source.results.JiraChangePayload;

@DisplayName("On create issue")
@Alias(value = "jira-listener")
public class JiraDirectoryListener extends PollingSource<JiraChangePayload, JiraChangeAttributes> {

	private static final Logger LOGGER = LoggerFactory.getLogger(JiraChangePayload.class);

	@Connection
	private ConnectionProvider<JiraConnection> connection;

	@Config
	private BasicConfiguration config;

	private JiraRestClient client;

	private String lastDate;

	private SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");


	@Override
	protected void doStart() throws ConnectionException {
		client = connection.connect().getClient();
		lastDate = DATE_TIME_FORMAT.format(new Date());
	}

	@Override
	protected void doStop() {

	}

	@Override
	public void poll(PollContext<JiraChangePayload, JiraChangeAttributes> pollContext) {
		if (pollContext.isSourceStopping()) {
			return;
		}
		String currentDate = DATE_TIME_FORMAT.format(new Date());

		if (currentDate.equals(lastDate)) {
			return;
		}
		String query = createSearchBetweenDatesJqlQuery(currentDate);
		LOGGER.debug("JQL " + query);
		Promise<SearchResult> searchResultPromise = client.getSearchClient().searchJql(query);
		try {
			acceptChanges(pollContext, searchResultPromise.get().getIssues());
			lastDate = currentDate;
		} catch (Exception e) {
			LOGGER.error("Error while executing JQL query ", e);
		}
	}

	@Override
	public void onRejectedItem(Result<JiraChangePayload, JiraChangeAttributes> result, SourceCallbackContext sourceCallbackContext) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Change rejected for processing: " + result.getOutput());
		}
	}

	private void acceptChanges(PollContext<JiraChangePayload, JiraChangeAttributes> pollContext, Iterable<Issue> issues) {
		issues.forEach(issue -> acceptItem(pollContext, issue));
	}

	private void acceptItem(PollContext<JiraChangePayload, JiraChangeAttributes> pollContext, Issue issue) {
		pollContext.accept(item -> {
			item.setResult(createResult(issue));
			item.setId(issue.getSummary());
		});
	}

	private String createSearchBetweenDatesJqlQuery(String currentDate) {
		return "created >= \"" + lastDate + "\" AND created < \"" + currentDate + "\"";
	}

	private Result<JiraChangePayload, JiraChangeAttributes> createResult(Issue issue) {
		JiraChangePayload change = createJiraChange(issue);
		return buildResult(change);
	}

	private JiraChangePayload createJiraChange(Issue issue) {
		JiraChangePayload change = new JiraChangePayload();
		change.setSummary(issue.getSummary());
		change.setDescription(issue.getDescription());
		change.setIssieType(issue.getIssueType().getName());
		change.setPriority(Objects.requireNonNull(issue.getPriority()).getName());
		change.setProject(issue.getProject().getName());
		change.setDueDate(issue.getDueDate() == null ? "" : issue.getDueDate().toString());
		return change;
	}

	Result<JiraChangePayload, JiraChangeAttributes> buildResult(JiraChangePayload change) {
		return Result.<JiraChangePayload, JiraChangeAttributes>builder()
				.output(change)
				.build();
	}
}