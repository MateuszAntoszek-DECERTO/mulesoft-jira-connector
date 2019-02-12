package pl.decerto.mule.internal.config;

import org.mule.runtime.extension.api.annotation.Operations;
import org.mule.runtime.extension.api.annotation.Sources;
import org.mule.runtime.extension.api.annotation.connectivity.ConnectionProviders;
import pl.decerto.mule.internal.connection.JiraConnectionProvider;
import pl.decerto.mule.internal.operation.JiraOperations;
import pl.decerto.mule.internal.source.JiraCreateListener;
import pl.decerto.mule.internal.source.JiraJqlListener;
import pl.decerto.mule.internal.source.JiraUpdateListener;

@Operations(JiraOperations.class)
@ConnectionProviders(JiraConnectionProvider.class)
@Sources({JiraCreateListener.class, JiraJqlListener.class, JiraUpdateListener.class})
public class BasicConfiguration {

}
