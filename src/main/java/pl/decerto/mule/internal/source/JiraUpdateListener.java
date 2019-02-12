package pl.decerto.mule.internal.source;

import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;

@DisplayName("On Update issue")
@Alias(value = "jira-update-listener")
public class JiraUpdateListener extends JiraListener {

	@Override
	String getJqlQuery() {
		return "updated >= \"" + lastDate + "\" AND updated < \"" + currentDate + "\" ";
	}
}