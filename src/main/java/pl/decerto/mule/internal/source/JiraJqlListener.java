package pl.decerto.mule.internal.source;

import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;

@DisplayName("On JQL Query")
@Alias(value = "jira-jql-listener")
public class JiraJqlListener extends JiraListener {

	@Parameter
	private String jqlQuery;

	@Override
	String getJqlQuery() {
		return jqlQuery;
	}
}