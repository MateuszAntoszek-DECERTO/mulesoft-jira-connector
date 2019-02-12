package pl.decerto.mule.internal.config;

import org.mule.runtime.extension.api.annotation.Operations;
import org.mule.runtime.extension.api.annotation.Sources;
import org.mule.runtime.extension.api.annotation.connectivity.ConnectionProviders;
import pl.decerto.mule.internal.connection.JiraConnectionProvider;
import pl.decerto.mule.internal.operation.JiraOperations;
import pl.decerto.mule.internal.source.JiraDirectoryListener;

@Operations(JiraOperations.class)
@ConnectionProviders(JiraConnectionProvider.class)
@Sources({JiraDirectoryListener.class})
public class BasicConfiguration {

}
