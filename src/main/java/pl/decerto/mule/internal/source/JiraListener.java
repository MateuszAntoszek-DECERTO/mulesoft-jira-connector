package pl.decerto.mule.internal.source;

import static java.util.Comparator.comparing;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.RestClientException;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.util.concurrent.Promise;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.StreamSupport;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.base.AbstractInstant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.connection.ConnectionProvider;
import org.mule.runtime.extension.api.annotation.param.Config;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Example;
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

public abstract class JiraListener extends PollingSource<JiraChangePayload, JiraChangeAttributes> {

	private static final Logger LOGGER = LoggerFactory.getLogger(JiraChangePayload.class);

	@Connection
	private ConnectionProvider<JiraConnection> connection;

	@Config
	private BasicConfiguration config;

	private DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");

	JiraRestClient client;

	@Parameter
	@DisplayName("Date from")
	@Example("yyyy-MM-dd HH:mm")
	private String dateFrom;

	@Parameter
	@Optional
	@DisplayName("Jira Timezone")
	@Example("Code (like GMT) or offset(like +00:00)")
	private String timezone;

	String lastDate;

	String currentDate;

	@Override
	protected void doStart() throws ConnectionException {
		client = connection.connect().getClient();
		lastDate = dateFrom;
		setTimeZone();
	}

	@Override
	protected void doStop() {

	}

	@Override
	public void poll(PollContext<JiraChangePayload, JiraChangeAttributes> pollContext) {
		if (pollContext.isSourceStopping()) {
			return;
		}
		currentDate = DATE_TIME_FORMAT.print(new LocalDateTime(DATE_TIME_FORMAT.getZone()));
		String query = getJqlQuery();
		LOGGER.debug("JQL " + query);
		Promise<SearchResult> searchResultPromise = client.getSearchClient().searchJql(query);
		try {
			SearchResult result = searchResultPromise.claim();
			acceptChanges(pollContext, result.getIssues());
			updateLastDate(result);
		} catch (RestClientException e) {
			LOGGER.error("There is an issue with REST API.", e);
		} catch (Exception e) {
			LOGGER.error("Error while executing JQL query ", e);
		}
	}

	private void updateLastDate(SearchResult issues) {
		if (issues.getTotal() > 0) {
			DateTime dateTime = StreamSupport.stream(issues.getIssues().spliterator(), false)
					.map(Issue::getCreationDate)
					.max(comparing(AbstractInstant::toDate))
					.get();
			lastDate = DATE_TIME_FORMAT.print(dateTime.plusMinutes(1));
			LOGGER.info("DATTAAA "+ lastDate);
		}
	}

	@Override
	public void onRejectedItem(Result<JiraChangePayload, JiraChangeAttributes> result, SourceCallbackContext sourceCallbackContext) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Change rejected for processing: " + result.getOutput());
		}
	}

	void acceptChanges(PollContext<JiraChangePayload, JiraChangeAttributes> pollContext, Iterable<Issue> issues) {
		issues.forEach(issue -> acceptItem(pollContext, issue));
	}

	private void acceptItem(PollContext<JiraChangePayload, JiraChangeAttributes> pollContext, Issue issue) {
		pollContext.accept(item -> {
			item.setResult(createResult(issue));
			item.setId(issue.getSummary());
		});
	}

	private Result<JiraChangePayload, JiraChangeAttributes> createResult(Issue issue) {
		JiraChangePayload change = createJiraChange(issue);
		return buildResult(change);
	}

	private JiraChangePayload createJiraChange(Issue issue) {
		JiraChangePayload change = new JiraChangePayload();
		change.setSummary(issue.getSummary());
		change.setDescription(issue.getDescription());
		change.setIssueType(issue.getIssueType().getName());
		change.setPriority(Objects.requireNonNull(issue.getPriority()).getName());
		change.setProject(issue.getProject().getName());
		change.setDueDate(issue.getDueDate() == null ? "" : issue.getDueDate().toString());
		change.setSelf(issue.getSelf().toString());
		change.setStatus(issue.getStatus() == null ? "" : issue.getStatus().getName());
		change.setKey(issue.getKey());
		return change;
	}

	Result<JiraChangePayload, JiraChangeAttributes> buildResult(JiraChangePayload change) {
		return Result.<JiraChangePayload, JiraChangeAttributes>builder()
				.output(change)
				.build();
	}

	Function<Issue, DateTime> getComparingDate() {
		return Issue::getCreationDate;
	}

	private void setTimeZone() {
		if (StringUtils.isNoneBlank(timezone)) {
			DATE_TIME_FORMAT = DATE_TIME_FORMAT.withZone(DateTimeZone.forID(timezone));
		} else {
			DATE_TIME_FORMAT = DATE_TIME_FORMAT.withZone(DateTimeZone.getDefault());
		}
		LOGGER.debug("set time zone " + DATE_TIME_FORMAT.getZone().getID());
	}

	abstract String getJqlQuery();
}