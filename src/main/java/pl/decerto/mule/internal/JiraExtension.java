package pl.decerto.mule.internal;

import org.mule.runtime.extension.api.annotation.Configurations;
import org.mule.runtime.extension.api.annotation.Extension;
import org.mule.runtime.extension.api.annotation.dsl.xml.Xml;
import pl.decerto.mule.internal.config.BasicConfiguration;


@Xml(prefix = "jira")
@Extension(name = "Jira", vendor = "decerto")
@Configurations(BasicConfiguration.class)
public class JiraExtension {

}
