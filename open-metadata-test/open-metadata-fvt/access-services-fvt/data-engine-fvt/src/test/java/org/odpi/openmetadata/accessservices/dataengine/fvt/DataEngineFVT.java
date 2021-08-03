/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.accessservices.dataengine.fvt;


import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.odpi.openmetadata.accessservices.dataengine.DataStoreAndRelationalTableSetupService;
import org.odpi.openmetadata.accessservices.dataengine.ProcessSetupService;
import org.odpi.openmetadata.accessservices.dataengine.RepositoryService;
import org.odpi.openmetadata.accessservices.dataengine.client.DataEngineClient;
import org.odpi.openmetadata.accessservices.dataengine.model.DataFile;
import org.odpi.openmetadata.accessservices.dataengine.model.Database;
import org.odpi.openmetadata.accessservices.dataengine.model.RelationalTable;
import org.odpi.openmetadata.accessservices.dataengine.model.SoftwareServerCapability;
import org.odpi.openmetadata.frameworks.connectors.ffdc.ConnectorCheckedException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.InvalidParameterException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.PropertyServerException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.UserNotAuthorizedException;
import org.odpi.openmetadata.http.HttpHelper;
import org.odpi.openmetadata.repositoryservices.connectors.stores.metadatacollectionstore.properties.instances.EntityDetail;
import org.odpi.openmetadata.repositoryservices.connectors.stores.metadatacollectionstore.properties.instances.Relationship;
import org.odpi.openmetadata.repositoryservices.ffdc.exception.EntityNotKnownException;
import org.odpi.openmetadata.repositoryservices.ffdc.exception.FunctionNotSupportedException;
import org.odpi.openmetadata.repositoryservices.ffdc.exception.PagingErrorException;
import org.odpi.openmetadata.repositoryservices.ffdc.exception.PropertyErrorException;
import org.odpi.openmetadata.repositoryservices.ffdc.exception.RepositoryErrorException;
import org.odpi.openmetadata.repositoryservices.ffdc.exception.TypeErrorException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * This class holds functional verification tests written with the help of the Junit framework. There are parametrized tests
 * covering the creation of an external data engine source and a whole job process containing stages.
 * Depending on the number of the series of parameters of each test method, the tests will run or not multiple times.
 * The parameters are computed in the method indicated in the @MethodSource annotation.
 */
public class DataEngineFVT {

    private static final String SOFTWARE_SERVER_CAPABILITY_TYPE_GUID = "fe30a033-8f86-4d17-8986-e6166fa24177";
    private static final String DATABASE_TYPE_GUID = "0921c83f-b2db-4086-a52c-0d10e52ca078";
    private static final String DATAFILE_TYPE_GUID = "10752b4a-4b5d-4519-9eae-fdd6d162122f";
    private static final String TABULAR_COLUMN_TYPE_GUID = "d81a0425-4e9b-4f31-bc1c-e18c3566da10";
    private static final String RELATIONAL_TABLE_TYPE_GUID = "ce7e72b8-396a-4013-8688-f9d973067425";
    private static final String RELATIONAL_COLUMN_TYPE_GUID = "aa8d5470-6dbc-4648-9e2f-045e5df9d2f9";
    private static final String RELATIONAL_DB_SCHEMA_TYPE_TYPE_GUID = "f20f5f45-1afb-41c1-9a09-34d8812626a4";
    private static final String DEPLOYED_DATABASE_SCHEMA_TYPE_GUID = "eab811ec-556a-45f1-9091-bc7ac8face0f";
    private static final String TABULAR_SCHEMA_TYPE_TYPE_GUID = "248975ec-8019-4b8a-9caf-084c8b724233";

    private static final String DESCRIPTION = "description";
    private static final String NAME = "name";
    private static final String TYPE = "type";
    private static final String VERSION = "version";
    private static final String PATCH_LEVEL = "patchLevel";
    private static final String QUALIFIED_NAME = "qualifiedName";
    private static final String DISPLAY_NAME = "displayName";
    private static final String SOURCE = "source";
    private static final String FILE_TYPE = "fileType";
    private static final String DEPLOYED_IMPLEMENTATION_TYPE = "deployedImplementationType";
    private static final String DATABASE_VERSION = "databaseVersion";
    private static final String INSTANCE = "instance";
    private static final String IMPORTED_FROM = "importedFrom";

    public DataEngineFVT() {
        HttpHelper.noStrictSSL();
    }

    private final ProcessSetupService processSetupService = new ProcessSetupService();
    private final DataStoreAndRelationalTableSetupService dataStoreAndRelationalTableSetupService = new DataStoreAndRelationalTableSetupService();

    @ParameterizedTest
    @MethodSource("org.odpi.openmetadata.accessservices.dataengine.PlatformConnectionProvider#getConnectionDetails")
    public void registerExternalTool(String userId, DataEngineClient dataEngineClient, RepositoryService repositoryService)
            throws InvalidParameterException, UserNotAuthorizedException, PropertyServerException, ConnectorCheckedException,
            org.odpi.openmetadata.repositoryservices.ffdc.exception.UserNotAuthorizedException, FunctionNotSupportedException,
            org.odpi.openmetadata.repositoryservices.ffdc.exception.InvalidParameterException, RepositoryErrorException,
            PropertyErrorException, TypeErrorException, PagingErrorException {

        SoftwareServerCapability softwareServerCapability = processSetupService.createExternalDataEngine(userId, dataEngineClient);

        List<EntityDetail> entityDetails = repositoryService.findEntityByPropertyValue(SOFTWARE_SERVER_CAPABILITY_TYPE_GUID,
                softwareServerCapability.getQualifiedName());

        if (entityDetails == null || entityDetails.isEmpty()) {
            fail();
        }

        assertEquals(1, entityDetails.size());
        EntityDetail entity = entityDetails.get(0);
        assertEquals(softwareServerCapability.getDescription(), entity.getProperties().getPropertyValue(DESCRIPTION).valueAsString());
        assertEquals(softwareServerCapability.getName(), entity.getProperties().getPropertyValue(NAME).valueAsString());
        assertEquals(softwareServerCapability.getEngineType(), entity.getProperties().getPropertyValue(TYPE).valueAsString());
        assertEquals(softwareServerCapability.getEngineVersion(), entity.getProperties().getPropertyValue(VERSION).valueAsString());
        assertEquals(softwareServerCapability.getPatchLevel(), entity.getProperties().getPropertyValue(PATCH_LEVEL).valueAsString());
        assertEquals(softwareServerCapability.getQualifiedName(), entity.getProperties().getPropertyValue(QUALIFIED_NAME).valueAsString());
        assertEquals(softwareServerCapability.getSource(), entity.getProperties().getPropertyValue(SOURCE).valueAsString());

    }

    @ParameterizedTest
    @MethodSource("org.odpi.openmetadata.accessservices.dataengine.PlatformConnectionProvider#getConnectionDetails")
    public void verifyLineageMappingsForAJobProcess(String userId, DataEngineClient dataEngineClient, RepositoryService repositoryService)
            throws InvalidParameterException, UserNotAuthorizedException, PropertyServerException, ConnectorCheckedException,
            org.odpi.openmetadata.repositoryservices.ffdc.exception.UserNotAuthorizedException,
            FunctionNotSupportedException, org.odpi.openmetadata.repositoryservices.ffdc.exception.InvalidParameterException,
            RepositoryErrorException, PropertyErrorException, TypeErrorException, PagingErrorException, EntityNotKnownException {

        processSetupService.createExternalDataEngine(userId, dataEngineClient);
        processSetupService.createJobProcessWithContent(userId, dataEngineClient);

        Map<String, List<String>> columnLineages = processSetupService.getJobProcessLineageMappingsProxiesByCsvColumn();

        for (String columnName : columnLineages.keySet()) {
            List<String> attributes = columnLineages.get(columnName);
            String previousAttribute = null;
            for (int i = 0; i < attributes.size() - 1; i++) {
                String currentAttribute = attributes.get(i);
                String entityGUID = repositoryService.findEntityGUIDByQualifiedName(currentAttribute);
                List<Relationship> relationships = repositoryService.findRelationshipsByGUID(entityGUID);
                List<String> lineageMappingOtherProxyQualifiedName =
                        repositoryService.getLineageMappingsProxiesQualifiedNames(relationships, currentAttribute);
                List<String> expectedLineageMappings = new ArrayList<>();
                if (previousAttribute != null) {
                    expectedLineageMappings.add(previousAttribute);
                }
                expectedLineageMappings.add(attributes.get(i + 1));
                Collections.sort(expectedLineageMappings);
                Collections.sort(lineageMappingOtherProxyQualifiedName);
                assertEquals(expectedLineageMappings, lineageMappingOtherProxyQualifiedName);
                previousAttribute = currentAttribute;
            }
        }
    }

    @ParameterizedTest
    @MethodSource("org.odpi.openmetadata.accessservices.dataengine.PlatformConnectionProvider#getConnectionDetails")
    public void upsertDatabase(String userId, DataEngineClient dataEngineClient, RepositoryService repositoryService)
            throws UserNotAuthorizedException, ConnectorCheckedException, PropertyServerException, InvalidParameterException,
            org.odpi.openmetadata.repositoryservices.ffdc.exception.UserNotAuthorizedException, FunctionNotSupportedException,
            org.odpi.openmetadata.repositoryservices.ffdc.exception.InvalidParameterException, RepositoryErrorException,
            PropertyErrorException, TypeErrorException, PagingErrorException, EntityNotKnownException {

        Database database = dataStoreAndRelationalTableSetupService.upsertDatabase(userId, dataEngineClient);

        // assert Database
        List<EntityDetail> databases = repositoryService.findEntityByPropertyValue(DATABASE_TYPE_GUID, database.getQualifiedName());
        if (databases == null || databases.isEmpty()) {
            fail();
        }
        assertEquals(1, databases.size());
        EntityDetail databaseAsEntityDetail = databases.get(0);
        assertEquals(database.getDisplayName(), databaseAsEntityDetail.getProperties().getPropertyValue(NAME).valueAsString());
        assertEquals(database.getDescription(), databaseAsEntityDetail.getProperties().getPropertyValue(DESCRIPTION).valueAsString());
        assertEquals(database.getDatabaseType(), databaseAsEntityDetail.getProperties().getPropertyValue(DEPLOYED_IMPLEMENTATION_TYPE).valueAsString());
        assertEquals(database.getDatabaseVersion(), databaseAsEntityDetail.getProperties().getPropertyValue(DATABASE_VERSION).valueAsString());
        assertEquals(database.getDatabaseInstance(), databaseAsEntityDetail.getProperties().getPropertyValue(INSTANCE).valueAsString());
        assertEquals(database.getDatabaseImportedFrom(), databaseAsEntityDetail.getProperties().getPropertyValue(IMPORTED_FROM).valueAsString());

        // assert Deployed Database Schema
        List<EntityDetail> schemas = repositoryService
                .getRelatedEntities(databaseAsEntityDetail.getGUID(), DEPLOYED_DATABASE_SCHEMA_TYPE_GUID);
        if (schemas == null || schemas.isEmpty()) {
            fail();
        }
        assertEquals(1, schemas.size());
        EntityDetail deployedDatabaseSchemaAsEntityDetail = schemas.get(0);
        assertEquals(database.getQualifiedName() + ":schema",
                deployedDatabaseSchemaAsEntityDetail.getProperties().getPropertyValue(QUALIFIED_NAME).valueAsString());
    }

    @ParameterizedTest
    @MethodSource("org.odpi.openmetadata.accessservices.dataengine.PlatformConnectionProvider#getConnectionDetails")
    public void upsertRelationalTable(String userId, DataEngineClient dataEngineClient, RepositoryService repositoryService)
            throws UserNotAuthorizedException, ConnectorCheckedException, PropertyServerException, InvalidParameterException,
            org.odpi.openmetadata.repositoryservices.ffdc.exception.UserNotAuthorizedException, FunctionNotSupportedException,
            org.odpi.openmetadata.repositoryservices.ffdc.exception.InvalidParameterException, RepositoryErrorException,
            PropertyErrorException, TypeErrorException, PagingErrorException, EntityNotKnownException {

        RelationalTable relationalTable = dataStoreAndRelationalTableSetupService.upsertRelationalTable(userId, dataEngineClient);

        // assert Relational Table
        List<EntityDetail> relationalTables = repositoryService
                .findEntityByPropertyValue(RELATIONAL_TABLE_TYPE_GUID, relationalTable.getQualifiedName());
        if (relationalTables == null || relationalTables.isEmpty()) {
            fail();
        }
        assertEquals(1, relationalTables.size());
        EntityDetail relationalTableAsEntityDetail = relationalTables.get(0);
        assertEquals(relationalTable.getDisplayName(),
                relationalTableAsEntityDetail.getProperties().getPropertyValue(DISPLAY_NAME).valueAsString());
        assertEquals(relationalTable.getDescription(),
                relationalTableAsEntityDetail.getProperties().getPropertyValue(DESCRIPTION).valueAsString());

        // assert Relational DB Schema Type and Deployed Database Schema
        List<EntityDetail> relationalSchemas = repositoryService
                .getRelatedEntities(relationalTableAsEntityDetail.getGUID(), RELATIONAL_DB_SCHEMA_TYPE_TYPE_GUID);
        if (relationalSchemas == null || relationalSchemas.isEmpty()) {
            fail();
        }
        assertEquals(1, relationalSchemas.size());
        EntityDetail relationalDbSchemaAsEntityDetail = relationalSchemas.get(0);

        List<EntityDetail> deployedSchemas = repositoryService
                .getRelatedEntities(relationalTableAsEntityDetail.getGUID(), DEPLOYED_DATABASE_SCHEMA_TYPE_GUID);
        if (deployedSchemas == null || deployedSchemas.isEmpty()) {
            fail();
        }
        assertEquals(1, deployedSchemas.size());
        EntityDetail deployedDbSchemaTypeAsEntityDetail = deployedSchemas.get(0);

        assertEquals("SchemaOf:" + relationalDbSchemaAsEntityDetail.getProperties().getPropertyValue(QUALIFIED_NAME).valueAsString(),
                deployedDbSchemaTypeAsEntityDetail.getProperties().getPropertyValue(QUALIFIED_NAME).valueAsString());

        // assert Relational Columns
        List<EntityDetail> relationalColumns = repositoryService
                .findEntityByPropertyValue(RELATIONAL_COLUMN_TYPE_GUID, relationalTable.getColumns().get(0).getQualifiedName());
        if (relationalColumns == null || relationalColumns.isEmpty()) {
            fail();
        }
        assertEquals(1, relationalColumns.size());
        EntityDetail relationalColumnAsEntityDetail = relationalColumns.get(0);
        assertEquals(relationalTable.getColumns().get(0).getDisplayName(),
                relationalColumnAsEntityDetail.getProperties().getPropertyValue(DISPLAY_NAME).valueAsString());
        assertEquals(relationalTable.getColumns().get(0).getDescription(),
                relationalColumnAsEntityDetail.getProperties().getPropertyValue(DESCRIPTION).valueAsString());
    }

    @ParameterizedTest
    @MethodSource("org.odpi.openmetadata.accessservices.dataengine.PlatformConnectionProvider#getConnectionDetails")
    public void upsertDataFile(String userId, DataEngineClient dataEngineClient, RepositoryService repositoryService)
            throws UserNotAuthorizedException, ConnectorCheckedException, PropertyServerException, InvalidParameterException,
            org.odpi.openmetadata.repositoryservices.ffdc.exception.UserNotAuthorizedException, FunctionNotSupportedException,
            org.odpi.openmetadata.repositoryservices.ffdc.exception.InvalidParameterException, RepositoryErrorException,
            PropertyErrorException, TypeErrorException, PagingErrorException, EntityNotKnownException {

        DataFile dataFile = dataStoreAndRelationalTableSetupService.upsertDataFile(userId, dataEngineClient);

        // assert Data File
        List<EntityDetail> dataFiles = repositoryService
                .findEntityByPropertyValue(DATAFILE_TYPE_GUID, dataFile.getQualifiedName());
        if (dataFiles == null || dataFiles.isEmpty()) {
            fail();
        }
        assertEquals(1, dataFiles.size());
        EntityDetail dataFileAsEntityDetail = dataFiles.get(0);
        assertEquals(dataFile.getDisplayName(), dataFileAsEntityDetail.getProperties().getPropertyValue(NAME).valueAsString());
        assertEquals(dataFile.getQualifiedName(), dataFileAsEntityDetail.getProperties().getPropertyValue(QUALIFIED_NAME).valueAsString());
        assertEquals(dataFile.getDescription(), dataFileAsEntityDetail.getProperties().getPropertyValue(DESCRIPTION).valueAsString());
        assertEquals(dataFile.getFileType(), dataFileAsEntityDetail.getProperties().getPropertyValue(FILE_TYPE).valueAsString());

        // assert Tabular Schema Type
        List<EntityDetail> tabularSchemas = repositoryService
                .getRelatedEntities(dataFileAsEntityDetail.getGUID(), TABULAR_SCHEMA_TYPE_TYPE_GUID);
        if (tabularSchemas == null || tabularSchemas.isEmpty()) {
            fail();
        }
        assertEquals(1, tabularSchemas.size());
        EntityDetail tabularSchemaTypeAsEntityDetail = tabularSchemas.get(0);
        assertEquals("Schema", tabularSchemaTypeAsEntityDetail.getProperties().getPropertyValue(NAME).valueAsString());
        assertEquals(dataFileAsEntityDetail.getProperties().getPropertyValue(QUALIFIED_NAME).valueAsString() + "::schema",
                tabularSchemaTypeAsEntityDetail.getProperties().getPropertyValue(QUALIFIED_NAME).valueAsString());


        // assert Tabular Column
        List<EntityDetail> tabularColumns = repositoryService
                .findEntityByPropertyValue(TABULAR_COLUMN_TYPE_GUID, dataFile.getColumns().get(0).getQualifiedName());
        if (tabularColumns == null || tabularColumns.isEmpty()) {
            fail();
        }
        assertEquals(1, tabularColumns.size());
        EntityDetail tabularColumnAsEntityDetail = tabularColumns.get(0);
        assertEquals(dataFile.getColumns().get(0).getDisplayName(),
                tabularColumnAsEntityDetail.getProperties().getPropertyValue(DISPLAY_NAME).valueAsString());
        assertEquals(dataFile.getColumns().get(0).getDescription(),
                tabularColumnAsEntityDetail.getProperties().getPropertyValue(DESCRIPTION).valueAsString());
    }

}
