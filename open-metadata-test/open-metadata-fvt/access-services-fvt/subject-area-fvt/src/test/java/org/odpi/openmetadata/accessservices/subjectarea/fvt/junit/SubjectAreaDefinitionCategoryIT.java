/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */

package org.odpi.openmetadata.accessservices.subjectarea.fvt.junit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.odpi.openmetadata.accessservices.subjectarea.fvt.SubjectAreaDefinitionCategoryFVT;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class SubjectAreaDefinitionCategoryIT {
    @ParameterizedTest
    @ValueSource(strings = {"serverinmem","servergraph"})
    public void testSubjectAreaDefinitionCategory(String server) {
        assertDoesNotThrow(() -> SubjectAreaDefinitionCategoryFVT.runIt("https://localhost:10443", server, "garygeeke"));
    }
}
