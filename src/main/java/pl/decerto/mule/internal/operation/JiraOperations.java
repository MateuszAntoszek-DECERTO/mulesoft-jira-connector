package pl.decerto.mule.internal.operation;

import static org.mule.runtime.extension.api.annotation.param.MediaType.ANY;
import com.atlassian.jira.rest.client.api.domain.Issue;
import java.util.concurrent.ExecutionException;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.decerto.mule.internal.connection.JiraConnection;

public class JiraOperations {

	private final Logger LOGGER = LoggerFactory.getLogger(JiraOperations.class);

	@MediaType(ANY)
	public Issue getIssue(String issueName, @Connection JiraConnection connection) throws ExecutionException, InterruptedException {
		LOGGER.debug("get issue " + issueName);
		return connection.getClient()
				.getIssueClient()
				.getIssue(issueName)
				.get();
	}
}
