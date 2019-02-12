package pl.decerto.mule.internal.source;

import org.apache.commons.lang3.StringUtils;
import org.mule.runtime.extension.api.annotation.Alias;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.runtime.extension.api.annotation.param.display.Summary;

@DisplayName("On Create issue")
@Alias(value = "jira-create-listener")
public class JiraCreateListener extends JiraListener {

	@Parameter
	@Optional
	@DisplayName("JQL Query")
	@Summary("Query to restrict result")
	private String jqlQuery;

	@Override
	String getJqlQuery() {
		return "created >= \"" + lastDate + "\" AND created < \"" + currentDate + "\" "
				+ (StringUtils.isNoneBlank(jqlQuery) ? ("AND " + jqlQuery) : "");
	}
}