package pl.decerto.mule.internal.operation;

import static org.mule.runtime.extension.api.annotation.param.MediaType.ANY;
import static org.mule.runtime.extension.api.annotation.param.MediaType.TEXT_PLAIN;
import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.atlassian.jira.rest.client.api.domain.Issue;
import java.util.concurrent.ExecutionException;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.ParameterGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.decerto.mule.api.service.JiraApiService;
import pl.decerto.mule.internal.connection.JiraConnection;
import pl.decerto.mule.internal.operation.group.IssueGroup;

public class JiraOperations {

	private final Logger LOGGER = LoggerFactory.getLogger(JiraOperations.class);

	private JiraApiService jiraApiService = new JiraApiService();

	@MediaType(ANY)
	public Issue getIssue(String issueName, @Connection JiraConnection connection) throws ExecutionException, InterruptedException {
		return jiraApiService.getIssue(issueName, connection);
	}

	@MediaType(ANY)
	public BasicIssue createIssue(@ParameterGroup(name = "Issue") IssueGroup issue, @Connection JiraConnection connection) {
		return jiraApiService.createIssue(issue, connection);
	}

	@MediaType(TEXT_PLAIN)
	public Issue updateIssue(String issueName, @ParameterGroup(name = "Issue") IssueGroup issue,
			@Connection JiraConnection connection) throws ExecutionException, InterruptedException {
		jiraApiService.updateIssue(issueName, issue, connection);
		return jiraApiService.getIssue(issueName, connection);
	}

	@MediaType(TEXT_PLAIN)
	public Issue assignIssue(String issueName, String userName, @Connection JiraConnection connection) throws ExecutionException,
			InterruptedException {
		jiraApiService.assignIssue(issueName, userName, connection);
		return jiraApiService.getIssue(issueName, connection);
	}
}
