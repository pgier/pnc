package org.jboss.pnc.core.builder.operationHandlers;

import org.jboss.pnc.core.BuildDriverFactory;
import org.jboss.pnc.core.builder.BuildTask;
import org.jboss.pnc.core.exception.CoreException;
import org.jboss.pnc.model.TaskStatus;
import org.jboss.pnc.model.builder.BuildDetails;
import org.jboss.pnc.spi.builddriver.BuildDriver;
import org.jboss.pnc.spi.builddriver.BuildDriverResult;

import javax.inject.Inject;
import java.util.function.Consumer;

/**
 * Created by <a href="mailto:matejonnet@gmail.com">Matej Lazar</a> on 2014-12-03.
 */
public class CollectResultsHandler extends OperationHandlerBase implements OperationHandler {

    private final BuildDriverFactory buildDriverFactory;

    @Inject
    public CollectResultsHandler(BuildDriverFactory buildDriverFactory) {
        this.buildDriverFactory = buildDriverFactory;
    }

    @Override
    protected TaskStatus.Operation executeAfter() {
        return TaskStatus.Operation.WAITING_BUILD_TO_COMPLETE;
    }

    @Override
    protected void doHandle(BuildTask buildTask) {
        buildTask.onStatusUpdate(new TaskStatus(TaskStatus.Operation.COLLECT_RESULTS, TaskStatus.State.STARTED));
        try {
            Consumer<BuildDriverResult> onBuildResultComplete = (buildDriverResult) -> {
                BuildDetails buildDetails = buildTask.getBuildDetails();
                buildDetails.setBuildStatus(buildDriverResult.getBuildStatus());
                buildDetails.setBuildLog(buildDriverResult.getConsoleOutput());
                buildTask.onStatusUpdate(new TaskStatus(TaskStatus.Operation.COLLECT_RESULTS, TaskStatus.State.COMPLETED));
            };

            Consumer<Exception> onBuildResultError = (e) -> {
                buildTask.onError(e);
            };

            //TODO better validation
            assert (buildTask.getBuildDetails() != null);

            BuildDriver buildDriver = buildDriverFactory.getBuildDriver(buildTask.getProjectBuildConfiguration().getEnvironment().getBuildType());
            buildDriver.retrieveBuildResults(buildTask.getBuildDetails(), onBuildResultComplete, onBuildResultError);

        } catch (CoreException e) {
            buildTask.onError(e);
        }
    }
}