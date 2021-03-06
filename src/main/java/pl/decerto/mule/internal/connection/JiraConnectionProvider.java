package pl.decerto.mule.internal.connection;

import com.atlassian.jira.rest.client.api.RestClientException;

import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.connection.ConnectionValidationResult;
import org.mule.runtime.api.connection.PoolingConnectionProvider;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Password;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JiraConnectionProvider implements PoolingConnectionProvider<JiraConnection> {

	private final Logger LOGGER = LoggerFactory.getLogger(JiraConnectionProvider.class);

	@Parameter
	@DisplayName("Jira URL")
	private String jiraUrl;

	@Parameter
	@DisplayName("Username")
	private String username;

	@Parameter
	@DisplayName("Password")
	@Password
	private String password;

	@Override
	public JiraConnection connect() {
		return new JiraConnection(jiraUrl, username, password);
	}

	@Override
	public void disconnect(JiraConnection connection) {

	}

	@Override
	public ConnectionValidationResult validate(JiraConnection connection) {
		try {
			connection.getClient().getMetadataClient()
					.getStatuses()
					.fail(throwable -> new ConnectionException("Connection not valid", throwable))
					.claim();
		} catch (RestClientException e) {
			return ConnectionValidationResult.failure("There is an issue with REST API", e);
		}
		return ConnectionValidationResult.success();
	}
}
