package pl.decerto.mule.internal.source.results;

public class JiraChangePayload {

	private String summary;

	private String description;

	private String project;

	private String issieType;

	private String priority;

	private String dueDate;

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public String getIssieType() {
		return issieType;
	}

	public void setIssieType(String issieType) {
		this.issieType = issieType;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getDueDate() {
		return dueDate;
	}

	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}
}
