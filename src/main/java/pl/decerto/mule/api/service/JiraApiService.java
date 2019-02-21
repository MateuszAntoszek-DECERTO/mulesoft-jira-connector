package pl.decerto.mule.api.service;

import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.IssueFieldId;
import com.atlassian.jira.rest.client.api.domain.input.ComplexIssueInputFieldValue;
import com.atlassian.jira.rest.client.api.domain.input.FieldInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.decerto.mule.internal.connection.JiraConnection;
import pl.decerto.mule.internal.operation.group.IssueGroup;

public class JiraApiService {

	private final Logger LOGGER = LoggerFactory.getLogger(JiraApiService.class);

	public Issue getIssue(String issueName, JiraConnection connection) throws ExecutionException, InterruptedException {
		LOGGER.debug("get issue " + issueName);
		return connection.getClient()
				.getIssueClient()
				.getIssue(issueName)
				.get();
	}

	public BasicIssue createIssue(IssueGroup issue, JiraConnection connection) {
		IssueInput create = createIssueInput(issue);
		return connection.getClient()
				.getIssueClient()
				.createIssue(create)
				.claim();
	}

	public boolean updateIssue(String issueName, IssueGroup issue, JiraConnection connection) {
		IssueInput create = createIssueInput(issue);
		return connection.getClient()
				.getIssueClient()
				.updateIssue(issueName, create)
				.isDone();
	}

	public boolean assignIssue(String issueKey, String userName, JiraConnection connection) {
		FieldInput fieldInput = createField(IssueFieldId.ASSIGNEE_FIELD, userName);
		return connection.getClient()
				.getIssueClient()
				.updateIssue(issueKey, IssueInput.createWithFields(fieldInput))
				.isDone();
	}

	private IssueInput createIssueInput(IssueGroup issue) {
		HashMap<String, FieldInput> fields = new HashMap<>();
		putToMap(createField(IssueFieldId.SUMMARY_FIELD, issue.getSummary()), fields);
		putToMap(createField(IssueFieldId.DESCRIPTION_FIELD, issue.getDescription()), fields);
		putToMap(createComplexFieldWithName(IssueFieldId.PRIORITY_FIELD, issue.getPriority()), fields);
		putToMap(createComplexFieldWithName(IssueFieldId.ISSUE_TYPE_FIELD, issue.getIssueType()), fields);
		putToMap(createComplexFieldWithKey(IssueFieldId.PROJECT_FIELD, issue.getProject()), fields);
		return new IssueInput(fields);
	}

	private void putToMap(FieldInput field, HashMap<String, FieldInput> fields) {
		if (field != null) {
			fields.put(field.getId(), field);
		}
	}

	private FieldInput createField(IssueFieldId descriptionField, String value) {
		return new FieldInput(descriptionField, value);
	}

	private FieldInput createComplexFieldWithName(IssueFieldId descriptionField, String value) {
		if (value == null) {
			return null;
		}
		return new FieldInput(descriptionField, ComplexIssueInputFieldValue.with("name", value));
	}

	private FieldInput createComplexFieldWithKey(IssueFieldId descriptionField, String value) {
		if (value == null) {
			return null;
		}
		return new FieldInput(descriptionField, ComplexIssueInputFieldValue.with("key", value));
	}
}
