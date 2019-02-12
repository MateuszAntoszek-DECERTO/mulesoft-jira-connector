package pl.decerto.mule.internal.source;

import org.apache.commons.lang3.StringUtils;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Summary;

@DisplayName("On Update issue")
@Alias(value = "jira-update-listener")
public class JiraUpdateListener extends JiraListener {

	@Parameter
	@Optional
	@DisplayName("JQL Query")
	@Summary("Query to restrict result")
	private String jqlQuery;

	@Override
	String getJqlQuery() {
		return "updated >= \"" + lastDate + "\" AND updated < \"" + currentDate + "\" "
				+ (StringUtils.isNoneBlank(jqlQuery) ? ("AND " + jqlQuery) : "");
	}
}