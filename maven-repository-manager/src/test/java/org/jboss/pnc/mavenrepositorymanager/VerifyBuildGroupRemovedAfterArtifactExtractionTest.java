/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2014 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.pnc.mavenrepositorymanager;

import org.commonjava.aprox.folo.client.AproxFoloContentClientModule;
import org.commonjava.aprox.model.core.StoreType;
import org.jboss.pnc.mavenrepositorymanager.fixture.TestBuildExecution;
import org.jboss.pnc.model.Artifact;
import org.jboss.pnc.spi.repositorymanager.BuildExecution;
import org.jboss.pnc.spi.repositorymanager.RepositoryManagerResult;
import org.jboss.pnc.spi.repositorymanager.model.RepositorySession;
import org.jboss.pnc.test.category.ContainerTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@Category(ContainerTest.class)
public class VerifyBuildGroupRemovedAfterArtifactExtractionTest extends AbstractRepositoryManagerDriverTest {

    @Test
    public void extractBuildArtifactsTriggersBuildRepoPromotionToChainGroup() throws Exception {
        String path = "/org/myproj/myproj/1.0/myproj-1.0.pom";
        String content = "This is a test " + System.currentTimeMillis();

        String buildId = "build";

        // create a dummy composed (chained) build execution, and a repo session based on it
        BuildExecution execution = new TestBuildExecution(buildId);
        RepositorySession session = driver.createBuildRepository(execution);

        // simulate a build deploying a file.
        driver.getAprox().module(AproxFoloContentClientModule.class)
                .store(buildId, StoreType.hosted, buildId, path, new ByteArrayInputStream(content.getBytes()));

        // now, extract the build artifacts. This will trigger promotion of the build hosted repo to the chain group.
        RepositoryManagerResult result = session.extractBuildArtifacts();

        // do some sanity checks while we're here
        List<Artifact> deps = result.getBuiltArtifacts();
        assertThat(deps.size(), equalTo(1));

        Artifact a = deps.get(0);
        assertThat(a.getFilename(), equalTo(new File(path).getName()));

        // end result: the build aggregation group should have been garbage collected
        boolean buildGroupExists = driver.getAprox().stores().exists(StoreType.group, buildId);
        assertThat(buildGroupExists, equalTo(false));
    }

}
