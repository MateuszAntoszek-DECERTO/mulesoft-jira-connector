package pl.decerto.mule.internal.operation.group;

import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;

public class IssueGroup {

	@Parameter
	private String summary;

	@Parameter
	private String description;

	@Parameter
	@Optional
	private String issueType;

	@Parameter
	private String project;

	@Parameter
	private String priority;

	public String getSummary() {
		return summary;
	}

	public String getDescription() {
		return description;
	}

	public String getIssueType() {
		return issueType;
	}

	public String getPriority() {
		return priority;
	}

	public String getProject() {
		return project;
	}
}
