package pl.decerto.mule.internal.source;

import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;

@DisplayName("On Create issue")
@Alias(value = "jira-create-listener")
public class JiraCreateListener extends JiraListener {

	@Override
	String getJqlQuery() {
		return "created >= \"" + lastDate + "\" AND created < \"" + currentDate + "\" ";
	}
}