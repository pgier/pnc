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

package org.jboss.pnc.mock.repositorymanager;

import org.jboss.pnc.mock.model.builders.ArtifactBuilder;
import org.jboss.pnc.model.Artifact;
import org.jboss.pnc.spi.repositorymanager.RepositoryManagerResult;

import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="mailto:matejonnet@gmail.com">Matej Lazar</a>
 */
public class RepositoryManagerResultMock {
    public static RepositoryManagerResult mockResult() {
        return new RepositoryManagerResult() {
            @Override
            public List<Artifact> getBuiltArtifacts() {
                Artifact[] artifacts = {ArtifactBuilder.mockArtifact(11), ArtifactBuilder.mockArtifact(12)};
                return Arrays.asList(artifacts);
            }

            @Override
            public List<Artifact> getDependencies() {
                Artifact[] artifacts = {ArtifactBuilder.mockArtifact(21), ArtifactBuilder.mockArtifact(22)};
                return Arrays.asList(artifacts);
            }

            @Override
            public String getBuildContentId() {
                return "mock-content-id";
            }
        };
    }
}
