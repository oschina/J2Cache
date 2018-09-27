/**
 * Copyright (c) 2015-2017.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.oschina.j2cache.hibernate5.log;

import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.cache.CacheException;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolver;
import org.hibernate.engine.jndi.JndiException;
import org.hibernate.engine.jndi.JndiNameException;
import org.hibernate.engine.loading.internal.CollectionLoadContext;
import org.hibernate.engine.loading.internal.EntityLoadContext;
import org.hibernate.engine.spi.CollectionKey;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.id.IntegralDataTypeHolder;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.type.BasicType;
import org.hibernate.type.SerializationException;
import org.hibernate.type.Type;
import org.jboss.logging.BasicLogger;
import org.jboss.logging.DelegatingBasicLogger;
import org.jboss.logging.Logger;

import javax.annotation.Generated;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.net.URL;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.*;

@Generated(value = "org.jboss.logging.processor.generator.model.MessageLoggerImplementor", date = "2015-01-06T12:13:01-0800")
public class J2CacheMessageLogger_$logger extends DelegatingBasicLogger implements Serializable, J2CacheMessageLogger, CoreMessageLogger, BasicLogger {

    private static final long serialVersionUID = 1L;
    private static final String FQCN = J2CacheMessageLogger_$logger.class.getName();
    private static final Locale LOCALE;
    private static final String attemptToRestartAlreadyStartedJ2CacheProvider = "HHH020001: Attempt to restart an already started J2CacheRegionFactory.  Use sessionFactory.close() between repeated calls to buildSessionFactory. Using previously created J2CacheRegionFactory. If this behaviour is required, consider using org.hibernate.cache.ehcache.SingletonJ2CacheRegionFactory.";
    private static final String unableToFindConfiguration = "HHH020002: Could not find configuration [%s]; using defaults.";
    private static final String unableToFindJ2CacheConfiguration = "HHH020003: Could not find a specific ehcache configuration for cache named [%s]; using defaults.";
    private static final String unableToLoadConfiguration = "HHH020004: A configurationResourceName was set to %s but the resource could not be loaded from the classpath. Ehcache will configure itself using defaults.";
    private static final String incompatibleCacheValueMode = "HHH020005: The default cache value mode for this Ehcache configuration is \"identity\". This is incompatible with clustered Hibernate caching - the value mode has therefore been switched to \"serialization\"";
    private static final String incompatibleCacheValueModePerCache = "HHH020006: The value mode for the cache[%s] is \"identity\". This is incompatible with clustered Hibernate caching - the value mode has therefore been switched to \"serialization\"";
    private static final String readOnlyCacheConfiguredForMutableEntity = "HHH020007: read-only cache configured for mutable entity [%s]";
    private static final String softLockedCacheExpired = "HHH020008: Cache[%s] Key[%s] Lockable[%s]\nA soft-locked cache entry was expired by the underlying Ehcache. If this happens regularly you should consider increasing the cache timeouts and/or capacity limits";
    private static final String alreadySessionBound = "HHH000002: Already session bound on call to bind(); make sure you clean up your sessions!";
    private static final String autoCommitMode = "HHH000006: Autocommit mode: %s";
    private static final String autoFlushWillNotWork = "HHH000008: JTASessionContext being used with JDBC transactions; auto-flush will not operate correctly with getCurrentSession()";
    private static final String batchContainedStatementsOnRelease = "HHH000010: On release of batch it still contained JDBC statements";
    private static final String bytecodeProvider = "HHH000021: Bytecode provider name : %s";
    private static final String c3p0ProviderClassNotFound = "HHH000022: c3p0 properties were encountered, but the %s provider class was not found on the classpath; these properties are going to be ignored.";
    private static final String cachedFileNotFound = "HHH000023: I/O reported cached file could not be found : %s : %s";
    private static final String cacheProvider = "HHH000024: Cache provider: %s";
    private static final String callingJoinTransactionOnNonJtaEntityManager = "HHH000027: Calling joinTransaction() on a non JTA EntityManager";
    private static final String closing = "HHH000031: Closing";
    private static final String collectionsFetched = "HHH000032: Collections fetched (minimize this): %s";
    private static final String collectionsLoaded = "HHH000033: Collections loaded: %s";
    private static final String collectionsRecreated = "HHH000034: Collections recreated: %s";
    private static final String collectionsRemoved = "HHH000035: Collections removed: %s";
    private static final String collectionsUpdated = "HHH000036: Collections updated: %s";
    private static final String columns = "HHH000037: Columns: %s";
    private static final String compositeIdClassDoesNotOverrideEquals = "HHH000038: Composite-id class does not override equals(): %s";
    private static final String compositeIdClassDoesNotOverrideHashCode = "HHH000039: Composite-id class does not override hashCode(): %s";
    private static final String configurationResource = "HHH000040: Configuration resource: %s";
    private static final String configuredSessionFactory = "HHH000041: Configured SessionFactory: %s";
    private static final String configuringFromFile = "HHH000042: Configuring from file: %s";
    private static final String configuringFromResource = "HHH000043: Configuring from resource: %s";
    private static final String configuringFromUrl = "HHH000044: Configuring from URL: %s";
    private static final String configuringFromXmlDocument = "HHH000045: Configuring from XML document";
    private static final String connectionsObtained = "HHH000048: Connections obtained: %s";
    private static final String containerProvidingNullPersistenceUnitRootUrl = "HHH000050: Container is providing a null PersistenceUnitRootUrl: discovery impossible";
    private static final String containsJoinFetchedCollection = "HHH000051: Ignoring bag join fetch [%s] due to prior collection join fetch";
    private static final String creatingSubcontextInfo = "HHH000053: Creating subcontext: %s";
    private static final String definingFlushBeforeCompletionIgnoredInHem = "HHH000059: Defining %s=true ignored in HEM";
    private static final String deprecatedForceDescriminatorAnnotation = "HHH000062: @ForceDiscriminator is deprecated use @DiscriminatorOptions instead.";
    private static final String deprecatedOracle9Dialect = "HHH000063: The Oracle9Dialect dialect has been deprecated; use either Oracle9iDialect or Oracle10gDialect instead";
    private static final String deprecatedOracleDialect = "HHH000064: The OracleDialect dialect has been deprecated; use Oracle8iDialect instead";
    private static final String deprecatedUuidGenerator = "HHH000065: DEPRECATED : use [%s] instead with custom [%s] implementation";
    private static final String disallowingInsertStatementComment = "HHH000067: Disallowing insert statement comment for select-identity due to Oracle driver bug";
    private static final String duplicateGeneratorName = "HHH000069: Duplicate generator name %s";
    private static final String duplicateGeneratorTable = "HHH000070: Duplicate generator table: %s";
    private static final String duplicateImport = "HHH000071: Duplicate import: %s -> %s";
    private static final String duplicateJoins = "HHH000072: Duplicate joins for class: %s";
    private static final String duplicateListener = "HHH000073: entity-listener duplication, first event definition will be used: %s";
    private static final String duplicateMetadata = "HHH000074: Found more than one <persistence-unit-metadata>, subsequent ignored";
    private static final String entitiesDeleted = "HHH000076: Entities deleted: %s";
    private static final String entitiesFetched = "HHH000077: Entities fetched (minimize this): %s";
    private static final String entitiesInserted = "HHH000078: Entities inserted: %s";
    private static final String entitiesLoaded = "HHH000079: Entities loaded: %s";
    private static final String entitiesUpdated = "HHH000080: Entities updated: %s";
    private static final String entityAnnotationOnNonRoot = "HHH000081: @org.hibernate.annotations.Entity used on a non root entity: ignored for %s";
    private static final String entityManagerClosedBySomeoneElse = "HHH000082: Entity Manager closed by someone else (%s must not be used)";
    private static final String entityMappedAsNonAbstract = "HHH000084: Entity [%s] is abstract-class/interface explicitly mapped as non-abstract; be sure to supply entity-names";
    private static final String exceptionHeaderFound = "HHH000085: %s %s found";
    private static final String exceptionHeaderNotFound = "HHH000086: %s No %s found";
    private static final String exceptionInAfterTransactionCompletionInterceptor = "HHH000087: Exception in interceptor afterTransactionCompletion()";
    private static final String exceptionInBeforeTransactionCompletionInterceptor = "HHH000088: Exception in interceptor beforeTransactionCompletion()";
    private static final String exceptionInSubResolver = "HHH000089: Sub-resolver threw unexpected exception, continuing to next : %s";
    private static final String expectedType = "HHH000091: Expected type: %s, actual value: %s";
    private static final String expired = "HHH000092: An item was expired by the cache while it was locked (increase your cache timeout): %s";
    private static final String factoryBoundToJndiName = "HHH000094: Bound factory to JNDI name: %s";
    private static final String factoryJndiRename = "HHH000096: A factory was renamed from [%s] to [%s] in JNDI";
    private static final String factoryUnboundFromJndiName = "HHH000097: Unbound factory from JNDI name: %s";
    private static final String factoryUnboundFromName = "HHH000098: A factory was unbound from name: %s";
    private static final String failed = "HHH000099: an assertion failure occurred (this may indicate a bug in Hibernate, but is more likely due to unsafe use of the session): %s";
    private static final String failSafeCollectionsCleanup = "HHH000100: Fail-safe cleanup (collections) : %s";
    private static final String failSafeEntitiesCleanup = "HHH000101: Fail-safe cleanup (entities) : %s";
    private static final String fetchingDatabaseMetadata = "HHH000102: Fetching database metadata";
    private static final String firstOrMaxResultsSpecifiedWithCollectionFetch = "HHH000104: firstResult/maxResults specified with collection fetch; applying in memory!";
    private static final String flushes = "HHH000105: Flushes: %s";
    private static final String forcingContainerResourceCleanup = "HHH000106: Forcing container resource cleanup on transaction completion";
    private static final String forcingTableUse = "HHH000107: Forcing table use for sequence-style generator due to pooled optimizer selection where db does not support pooled sequences";
    private static final String foreignKeys = "HHH000108: Foreign keys: %s";
    private static final String foundMappingDocument = "HHH000109: Found mapping document in jar: %s";
    private static final String gettersOfLazyClassesCannotBeFinal = "HHH000112: Getters of lazy classes cannot be final: %s.%s";
    private static final String guidGenerated = "HHH000113: GUID identifier generated: %s";
    private static final String handlingTransientEntity = "HHH000114: Handling transient entity in delete processing";
    private static final String hibernateConnectionPoolSize = "HHH000115: Hibernate connection pool size: %s (min=%s)";
    private static final String honoringOptimizerSetting = "HHH000116: Config specified explicit optimizer of [%s], but [%s=%s]; using optimizer [%s] increment default of [%s].";
    private static final String hql = "HHH000117: HQL: %s, time: %sms, rows: %s";
    private static final String hsqldbSupportsOnlyReadCommittedIsolation = "HHH000118: HSQLDB supports only READ_UNCOMMITTED isolation";
    private static final String hydratingEntitiesCount = "HHH000119: On EntityLoadContext#clear, hydratingEntities contained [%s] entries";
    private static final String ignoringTableGeneratorConstraints = "HHH000120: Ignoring unique constraints specified on table generator [%s]";
    private static final String ignoringUnrecognizedQueryHint = "HHH000121: Ignoring unrecognized query hint [%s]";
    private static final String illegalPropertyGetterArgument = "HHH000122: IllegalArgumentException in class: %s, getter method of property: %s";
    private static final String illegalPropertySetterArgument = "HHH000123: IllegalArgumentException in class: %s, setter method of property: %s";
    private static final String immutableAnnotationOnNonRoot = "HHH000124: @Immutable used on a non root entity: ignored for %s";
    private static final String incompleteMappingMetadataCacheProcessing = "HHH000125: Mapping metadata cache was not completely processed";
    private static final String indexes = "HHH000126: Indexes: %s";
    private static final String couldNotBindJndiListener = "HHH000127: Could not bind JNDI listener";
    private static final String instantiatingExplicitConnectionProvider = "HHH000130: Instantiating explicit connection provider: %s";
    private static final String invalidArrayElementType = "HHH000132: Array element type error\n%s";
    private static final String invalidDiscriminatorAnnotation = "HHH000133: Discriminator column has to be defined in the root entity, it will be ignored in subclass: %s";
    private static final String invalidEditOfReadOnlyItem = "HHH000134: Application attempted to edit read only item: %s";
    private static final String invalidJndiName = "HHH000135: Invalid JNDI name: %s";
    private static final String invalidOnDeleteAnnotation = "HHH000136: Inapropriate use of @OnDelete on entity, annotation ignored: %s";
    private static final String invalidPrimaryKeyJoinColumnAnnotation = "HHH000137: Root entity should not hold a PrimaryKeyJoinColum(s), will be ignored: %s";
    private static final String invalidSubStrategy = "HHH000138: Mixing inheritance strategy in a entity hierarchy is not allowed, ignoring sub strategy in: %s";
    private static final String invalidTableAnnotation = "HHH000139: Illegal use of @Table in a subclass of a SINGLE_TABLE hierarchy: %s";
    private static final String jaccContextId = "HHH000140: JACC contextID: %s";
    private static final String JavaSqlTypesMappedSameCodeMultipleTimes = "HHH000141: java.sql.Types mapped the same code [%s] multiple times; was [%s]; now [%s]";
    private static final String bytecodeEnhancementFailed = "HHH000142: Bytecode enhancement failed: %s";
    private static final String jdbcAutoCommitFalseBreaksEjb3Spec = "HHH000144: %s = false breaks the EJB3 specification";
    private static final String jdbcRollbackFailed = "HHH000151: JDBC rollback failed";
    private static final String jndiInitialContextProperties = "HHH000154: JNDI InitialContext properties:%s";
    private static final String jndiNameDoesNotHandleSessionFactoryReference = "HHH000155: JNDI name %s does not handle a session factory reference";
    private static final String lazyPropertyFetchingAvailable = "HHH000157: Lazy property fetching available for: %s";
    private static final String loadingCollectionKeyNotFound = "HHH000159: In CollectionLoadContext#endLoadingCollections, localLoadingCollectionKeys contained [%s], but no LoadingCollectionEntry was found in loadContexts";
    private static final String localLoadingCollectionKeysCount = "HHH000160: On CollectionLoadContext#cleanup, localLoadingCollectionKeys contained [%s] entries";
    private static final String loggingStatistics = "HHH000161: Logging statistics....";
    private static final String logicalConnectionClosed = "HHH000162: *** Logical connection closed ***";
    private static final String logicalConnectionReleasingPhysicalConnection = "HHH000163: Logical connection releasing its physical connection";
    private static final String maxQueryTime = "HHH000173: Max query time: %sms";
    private static final String missingArguments = "HHH000174: Function template anticipated %s arguments, but %s arguments encountered";
    private static final String missingEntityAnnotation = "HHH000175: Class annotated @org.hibernate.annotations.Entity but not javax.persistence.Entity (most likely a user error): %s";
    private static final String namedQueryError = "HHH000177: Error in named query: %s";
    private static final String namingExceptionAccessingFactory = "HHH000178: Naming exception occurred accessing factory: %s";
    private static final String narrowingProxy = "HHH000179: Narrowing proxy to %s - this operation breaks ==";
    private static final String needsLimit = "HHH000180: FirstResult/maxResults specified on polymorphic query; applying in memory!";
    private static final String noAppropriateConnectionProvider = "HHH000181: No appropriate connection provider encountered, assuming application will be supplying connections";
    private static final String noDefaultConstructor = "HHH000182: No default (no-argument) constructor for class: %s (class must be instantiated by Interceptor)";
    private static final String noPersistentClassesFound = "HHH000183: no persistent classes found for query class: %s";
    private static final String noSessionFactoryWithJndiName = "HHH000184: No session factory with JNDI name %s";
    private static final String optimisticLockFailures = "HHH000187: Optimistic lock failures: %s";
    private static final String orderByAnnotationIndexedCollection = "HHH000189: @OrderBy not allowed for an indexed collection, annotation ignored.";
    private static final String overridingTransactionStrategyDangerous = "HHH000193: Overriding %s is dangerous, this might break the EJB3 specification implementation";
    private static final String packageNotFound = "HHH000194: Package not found or wo package-info.java: %s";
    private static final String parsingXmlError = "HHH000196: Error parsing XML (%s) : %s";
    private static final String parsingXmlErrorForFile = "HHH000197: Error parsing XML: %s(%s) %s";
    private static final String parsingXmlWarning = "HHH000198: Warning parsing XML (%s) : %s";
    private static final String parsingXmlWarningForFile = "HHH000199: Warning parsing XML: %s(%s) %s";
    private static final String persistenceProviderCallerDoesNotImplementEjb3SpecCorrectly = "HHH000200: Persistence provider caller does not implement the EJB3 spec correctly.PersistenceUnitInfo.getNewTempClassLoader() is null.";
    private static final String pooledOptimizerReportedInitialValue = "HHH000201: Pooled optimizer source reported [%s] as the initial value; use of 1 or greater highly recommended";
    private static final String preparedStatementAlreadyInBatch = "HHH000202: PreparedStatement was already in the batch, [%s].";
    private static final String processEqualityExpression = "HHH000203: processEqualityExpression() : No expression to process!";
    private static final String processingPersistenceUnitInfoName = "HHH000204: Processing PersistenceUnitInfo [\n\tname: %s\n\t...]";
    private static final String propertiesLoaded = "HHH000205: Loaded properties from resource hibernate.properties: %s";
    private static final String propertiesNotFound = "HHH000206: hibernate.properties not found";
    private static final String propertyNotFound = "HHH000207: Property %s not found in class but described in <mapping-file/> (possible typo error)";
    private static final String proxoolProviderClassNotFound = "HHH000209: proxool properties were encountered, but the %s provider class was not found on the classpath; these properties are going to be ignored.";
    private static final String queriesExecuted = "HHH000210: Queries executed to database: %s";
    private static final String queryCacheHits = "HHH000213: Query cache hits: %s";
    private static final String queryCacheMisses = "HHH000214: Query cache misses: %s";
    private static final String queryCachePuts = "HHH000215: Query cache puts: %s";
    private static final String rdmsOs2200Dialect = "HHH000218: RDMSOS2200Dialect version: 1.0";
    private static final String readingCachedMappings = "HHH000219: Reading mappings from cache file: %s";
    private static final String readingMappingsFromFile = "HHH000220: Reading mappings from file: %s";
    private static final String readingMappingsFromResource = "HHH000221: Reading mappings from resource: %s";
    private static final String readOnlyCacheConfiguredForMutableCollection = "HHH000222: read-only cache configured for mutable collection [%s]";
    private static final String recognizedObsoleteHibernateNamespace = "HHH000223: Recognized obsolete hibernate namespace %s. Use namespace %s instead. Refer to Hibernate 3.6 Migration Guide!";
    private static final String renamedProperty = "HHH000225: Property [%s] has been renamed to [%s]; update your properties appropriately";
    private static final String requiredDifferentProvider = "HHH000226: Required a different provider: %s";
    private static final String runningHbm2ddlSchemaExport = "HHH000227: Running hbm2ddl schema export";
    private static final String runningHbm2ddlSchemaUpdate = "HHH000228: Running hbm2ddl schema update";
    private static final String runningSchemaValidator = "HHH000229: Running schema validator";
    private static final String schemaExportComplete = "HHH000230: Schema export complete";
    private static final String schemaExportUnsuccessful = "HHH000231: Schema export unsuccessful";
    private static final String schemaUpdateComplete = "HHH000232: Schema update complete";
    private static final String scopingTypesToSessionFactoryAfterAlreadyScoped = "HHH000233: Scoping types to session factory %s after already scoped %s";
    private static final String searchingForMappingDocuments = "HHH000235: Searching for mapping documents in jar: %s";
    private static final String secondLevelCacheHits = "HHH000237: Second level cache hits: %s";
    private static final String secondLevelCacheMisses = "HHH000238: Second level cache misses: %s";
    private static final String secondLevelCachePuts = "HHH000239: Second level cache puts: %s";
    private static final String serviceProperties = "HHH000240: Service properties: %s";
    private static final String sessionsClosed = "HHH000241: Sessions closed: %s";
    private static final String sessionsOpened = "HHH000242: Sessions opened: %s";
    private static final String settersOfLazyClassesCannotBeFinal = "HHH000243: Setters of lazy classes cannot be final: %s.%s";
    private static final String sortAnnotationIndexedCollection = "HHH000244: @Sort not allowed for an indexed collection, annotation ignored.";
    private static final String splitQueries = "HHH000245: Manipulation query [%s] resulted in [%s] split queries";
    private static final String sqlWarning = "HHH000247: SQL Error: %s, SQLState: %s";
    private static final String startingQueryCache = "HHH000248: Starting query cache at region: %s";
    private static final String startingServiceAtJndiName = "HHH000249: Starting service at JNDI name: %s";
    private static final String startingUpdateTimestampsCache = "HHH000250: Starting update timestamps cache at region: %s";
    private static final String startTime = "HHH000251: Start time: %s";
    private static final String statementsClosed = "HHH000252: Statements closed: %s";
    private static final String statementsPrepared = "HHH000253: Statements prepared: %s";
    private static final String stoppingService = "HHH000255: Stopping service";
    private static final String subResolverException = "HHH000257: sub-resolver threw unexpected exception, continuing to next : %s";
    private static final String successfulTransactions = "HHH000258: Successful transactions: %s";
    private static final String synchronizationAlreadyRegistered = "HHH000259: Synchronization [%s] was already registered";
    private static final String synchronizationFailed = "HHH000260: Exception calling user Synchronization [%s] : %s";
    private static final String tableFound = "HHH000261: Table found: %s";
    private static final String tableNotFound = "HHH000262: Table not found: %s";
    private static final String multipleTablesFound = "HHH000263: More than one table found: %s";
    private static final String transactions = "HHH000266: Transactions: %s";
    private static final String transactionStartedOnNonRootSession = "HHH000267: Transaction started on non-root session";
    private static final String transactionStrategy = "HHH000268: Transaction strategy: %s";
    private static final String typeDefinedNoRegistrationKeys = "HHH000269: Type [%s] defined no registration keys; ignoring";
    private static final String typeRegistrationOverridesPrevious = "HHH000270: Type registration [%s] overrides previous : %s";
    private static final String unableToAccessEjb3Configuration = "HHH000271: Naming exception occurred accessing Ejb3Configuration";
    private static final String unableToAccessSessionFactory = "HHH000272: Error while accessing session factory with JNDI name %s";
    private static final String unableToAccessTypeInfoResultSet = "HHH000273: Error accessing type info result set : %s";
    private static final String unableToApplyConstraints = "HHH000274: Unable to apply constraints on DDL for %s";
    private static final String unableToBindEjb3ConfigurationToJndi = "HHH000276: Could not bind Ejb3Configuration to JNDI";
    private static final String unableToBindFactoryToJndi = "HHH000277: Could not bind factory to JNDI";
    private static final String unableToBindValueToParameter = "HHH000278: Could not bind value '%s' to parameter: %s; %s";
    private static final String unableToBuildEnhancementMetamodel = "HHH000279: Unable to build enhancement metamodel for %s";
    private static final String unableToBuildSessionFactoryUsingMBeanClasspath = "HHH000280: Could not build SessionFactory using the MBean classpath - will try again using client classpath: %s";
    private static final String unableToCleanUpCallableStatement = "HHH000281: Unable to clean up callable statement";
    private static final String unableToCleanUpPreparedStatement = "HHH000282: Unable to clean up prepared statement";
    private static final String unableToCleanupTemporaryIdTable = "HHH000283: Unable to cleanup temporary id table after use [%s]";
    private static final String unableToCloseConnection = "HHH000284: Error closing connection";
    private static final String unableToCloseInitialContext = "HHH000285: Error closing InitialContext [%s]";
    private static final String unableToCloseInputFiles = "HHH000286: Error closing input files: %s";
    private static final String unableToCloseInputStream = "HHH000287: Could not close input stream";
    private static final String unableToCloseInputStreamForResource = "HHH000288: Could not close input stream for %s";
    private static final String unableToCloseIterator = "HHH000289: Unable to close iterator";
    private static final String unableToCloseJar = "HHH000290: Could not close jar: %s";
    private static final String unableToCloseOutputFile = "HHH000291: Error closing output file: %s";
    private static final String unableToCloseOutputStream = "HHH000292: IOException occurred closing output stream";
    private static final String unableToCloseSession = "HHH000294: Could not close session";
    private static final String unableToCloseSessionDuringRollback = "HHH000295: Could not close session during rollback";
    private static final String unableToCloseStream = "HHH000296: IOException occurred closing stream";
    private static final String unableToCloseStreamError = "HHH000297: Could not close stream on hibernate.properties: %s";
    private static final String unableToCommitJta = "HHH000298: JTA commit failed";
    private static final String unableToCompleteSchemaUpdate = "HHH000299: Could not complete schema update";
    private static final String unableToCompleteSchemaValidation = "HHH000300: Could not complete schema validation";
    private static final String unableToConfigureSqlExceptionConverter = "HHH000301: Unable to configure SQLExceptionConverter : %s";
    private static final String unableToConstructCurrentSessionContext = "HHH000302: Unable to construct current session context [%s]";
    private static final String unableToConstructSqlExceptionConverter = "HHH000303: Unable to construct instance of specified SQLExceptionConverter : %s";
    private static final String unableToCopySystemProperties = "HHH000304: Could not copy system properties, system properties will be ignored";
    private static final String unableToCreateProxyFactory = "HHH000305: Could not create proxy factory for:%s";
    private static final String unableToCreateSchema = "HHH000306: Error creating schema ";
    private static final String unableToDeserializeCache = "HHH000307: Could not deserialize cache file: %s : %s";
    private static final String unableToDestroyCache = "HHH000308: Unable to destroy cache: %s";
    private static final String unableToDestroyQueryCache = "HHH000309: Unable to destroy query cache: %s: %s";
    private static final String unableToDestroyUpdateTimestampsCache = "HHH000310: Unable to destroy update timestamps cache: %s: %s";
    private static final String unableToDetermineLockModeValue = "HHH000311: Unable to determine lock mode value : %s -> %s";
    private static final String unableToDetermineTransactionStatus = "HHH000312: Could not determine transaction status";
    private static final String unableToDetermineTransactionStatusAfterCommit = "HHH000313: Could not determine transaction status after commit";
    private static final String unableToDropTemporaryIdTable = "HHH000314: Unable to drop temporary id table after use [%s]";
    private static final String unableToExecuteBatch = "HHH000315: Exception executing batch [%s], SQL: %s";
    private static final String unableToExecuteResolver = "HHH000316: Error executing resolver [%s] : %s";
    private static final String unableToFindPersistenceXmlInClasspath = "HHH000318: Could not find any META-INF/persistence.xml file in the classpath";
    private static final String unableToGetDatabaseMetadata = "HHH000319: Could not get database metadata";
    private static final String unableToInstantiateConfiguredSchemaNameResolver = "HHH000320: Unable to instantiate configured schema name resolver [%s] %s";
    private static final String unableToLocateCustomOptimizerClass = "HHH000321: Unable to interpret specified optimizer [%s], falling back to noop";
    private static final String unableToInstantiateOptimizer = "HHH000322: Unable to instantiate specified optimizer [%s], falling back to noop";
    private static final String unableToInstantiateUuidGenerationStrategy = "HHH000325: Unable to instantiate UUID generation strategy class : %s";
    private static final String unableToJoinTransaction = "HHH000326: Cannot join transaction: do not override %s";
    private static final String unableToLoadCommand = "HHH000327: Error performing load command : %s";
    private static final String unableToLoadDerbyDriver = "HHH000328: Unable to load/access derby driver class sysinfo to check versions : %s";
    private static final String unableToLoadProperties = "HHH000329: Problem loading properties from hibernate.properties";
    private static final String unableToLocateConfigFile = "HHH000330: Unable to locate config file: %s";
    private static final String unableToLocateConfiguredSchemaNameResolver = "HHH000331: Unable to locate configured schema name resolver class [%s] %s";
    private static final String unableToLocateMBeanServer = "HHH000332: Unable to locate MBeanServer on JMX service shutdown";
    private static final String unableToLocateUuidGenerationStrategy = "HHH000334: Unable to locate requested UUID generation strategy class : %s";
    private static final String unableToLogSqlWarnings = "HHH000335: Unable to log SQLWarnings : %s";
    private static final String unableToLogWarnings = "HHH000336: Could not log warnings";
    private static final String unableToMarkForRollbackOnPersistenceException = "HHH000337: Unable to mark for rollback on PersistenceException: ";
    private static final String unableToMarkForRollbackOnTransientObjectException = "HHH000338: Unable to mark for rollback on TransientObjectException: ";
    private static final String unableToObjectConnectionMetadata = "HHH000339: Could not obtain connection metadata: %s";
    private static final String unableToObjectConnectionToQueryMetadata = "HHH000340: Could not obtain connection to query metadata: %s";
    private static final String unableToObtainConnectionMetadata = "HHH000341: Could not obtain connection metadata : %s";
    private static final String unableToObtainConnectionToQueryMetadata = "HHH000342: Could not obtain connection to query metadata : %s";
    private static final String unableToObtainInitialContext = "HHH000343: Could not obtain initial context";
    private static final String unableToParseMetadata = "HHH000344: Could not parse the package-level metadata [%s]";
    private static final String unableToPerformJdbcCommit = "HHH000345: JDBC commit failed";
    private static final String unableToPerformManagedFlush = "HHH000346: Error during managed flush [%s]";
    private static final String unableToQueryDatabaseMetadata = "HHH000347: Unable to query java.sql.DatabaseMetaData";
    private static final String unableToReadClass = "HHH000348: Unable to read class: %s";
    private static final String unableToReadColumnValueFromResultSet = "HHH000349: Could not read column value from result set: %s; %s";
    private static final String unableToReadHiValue = "HHH000350: Could not read a hi value - you need to populate the table: %s";
    private static final String unableToReadOrInitHiValue = "HHH000351: Could not read or init a hi value";
    private static final String unableToReleaseBatchStatement = "HHH000352: Unable to release batch statement...";
    private static final String unableToReleaseCacheLock = "HHH000353: Could not release a cache lock : %s";
    private static final String unableToReleaseContext = "HHH000354: Unable to release initial context: %s";
    private static final String unableToReleaseCreatedMBeanServer = "HHH000355: Unable to release created MBeanServer : %s";
    private static final String unableToReleaseIsolatedConnection = "HHH000356: Unable to release isolated connection [%s]";
    private static final String unableToReleaseTypeInfoResultSet = "HHH000357: Unable to release type info result set";
    private static final String unableToRemoveBagJoinFetch = "HHH000358: Unable to erase previously added bag join fetch";
    private static final String unableToResolveAggregateFunction = "HHH000359: Could not resolve aggregate function [%s]; using standard definition";
    private static final String unableToResolveMappingFile = "HHH000360: Unable to resolve mapping file [%s]";
    private static final String unableToRetrieveCache = "HHH000361: Unable to retrieve cache from JNDI [%s]: %s";
    private static final String unableToRetrieveTypeInfoResultSet = "HHH000362: Unable to retrieve type info result set : %s";
    private static final String unableToRollbackConnection = "HHH000363: Unable to rollback connection on exception [%s]";
    private static final String unableToRollbackIsolatedTransaction = "HHH000364: Unable to rollback isolated transaction on error [%s] : [%s]";
    private static final String unableToRollbackJta = "HHH000365: JTA rollback failed";
    private static final String unableToRunSchemaUpdate = "HHH000366: Error running schema update";
    private static final String unableToSetTransactionToRollbackOnly = "HHH000367: Could not set transaction to rollback only";
    private static final String unableToStopHibernateService = "HHH000368: Exception while stopping service";
    private static final String unableToStopService = "HHH000369: Error stopping service [%s] : %s";
    private static final String unableToSwitchToMethodUsingColumnIndex = "HHH000370: Exception switching from method: [%s] to a method using the column index. Reverting to using: [%<s]";
    private static final String unableToSynchronizeDatabaseStateWithSession = "HHH000371: Could not synchronize database state with session: %s";
    private static final String unableToToggleAutoCommit = "HHH000372: Could not toggle autocommit";
    private static final String unableToTransformClass = "HHH000373: Unable to transform class: %s";
    private static final String unableToUnbindFactoryFromJndi = "HHH000374: Could not unbind factory from JNDI";
    private static final String unableToUpdateHiValue = "HHH000375: Could not update hi value in: %s";
    private static final String unableToUpdateQueryHiValue = "HHH000376: Could not updateQuery hi value in: %s";
    private static final String unableToWrapResultSet = "HHH000377: Error wrapping result set";
    private static final String unableToWriteCachedFile = "HHH000378: I/O reported error writing cached file : %s: %s";
    private static final String unexpectedLiteralTokenType = "HHH000380: Unexpected literal token type [%s] passed for numeric processing";
    private static final String unexpectedRowCounts = "HHH000381: JDBC driver did not return the expected number of row counts";
    private static final String unknownBytecodeProvider = "HHH000382: unrecognized bytecode provider [%s], using [%s] by default";
    private static final String unknownIngresVersion = "HHH000383: Unknown Ingres major version [%s]; using Ingres 9.2 dialect";
    private static final String unknownOracleVersion = "HHH000384: Unknown Oracle major version [%s]";
    private static final String unknownSqlServerVersion = "HHH000385: Unknown Microsoft SQL Server major version [%s] using [%s] dialect";
    private static final String unregisteredResultSetWithoutStatement = "HHH000386: ResultSet had no statement associated with it, but was not yet registered";
    private static final String unregisteredStatement = "HHH000387: ResultSet's statement was not registered";
    private static final String unsuccessful = "HHH000388: Unsuccessful: %s";
    private static final String unsuccessfulCreate = "HHH000389: Unsuccessful: %s";
    private static final String unsupportedAfterStatement = "HHH000390: Overriding release mode as connection provider does not support 'after_statement'";
    private static final String unsupportedIngresVersion = "HHH000391: Ingres 10 is not yet fully supported; using Ingres 9.3 dialect";
    private static final String unsupportedInitialValue = "HHH000392: Hibernate does not support SequenceGenerator.initialValue() unless '%s' set";
    private static final String unsupportedMultiTableBulkHqlJpaql = "HHH000393: The %s.%s.%s version of H2 implements temporary table creation such that it commits current transaction; multi-table, bulk hql/jpaql will not work properly";
    private static final String unsupportedOracleVersion = "HHH000394: Oracle 11g is not yet fully supported; using Oracle 10g dialect";
    private static final String unsupportedProperty = "HHH000395: Usage of obsolete property: %s no longer supported, use: %s";
    private static final String updatingSchema = "HHH000396: Updating schema";
    private static final String usingAstQueryTranslatorFactory = "HHH000397: Using ASTQueryTranslatorFactory";
    private static final String usingDefaultIdGeneratorSegmentValue = "HHH000398: Explicit segment value for id generator [%s.%s] suggested; using default [%s]";
    private static final String usingDefaultTransactionStrategy = "HHH000399: Using default transaction strategy (direct JDBC transactions)";
    private static final String usingDialect = "HHH000400: Using dialect: %s";
    private static final String usingOldDtd = "HHH000404: Don't use old DTDs, read the Hibernate 3.x Migration Guide!";
    private static final String usingReflectionOptimizer = "HHH000406: Using bytecode reflection optimizer";
    private static final String usingStreams = "HHH000407: Using java.io streams to persist binary types";
    private static final String usingTimestampWorkaround = "HHH000408: Using workaround for JVM bug in java.sql.Timestamp";
    private static final String usingUuidHexGenerator = "HHH000409: Using %s which does not generate IETF RFC 4122 compliant UUID values; consider using %s instead";
    private static final String validatorNotFound = "HHH000410: Hibernate Validator not found: ignoring";
    private static final String version = "HHH000412: Hibernate Core {%s}";
    private static final String warningsCreatingTempTable = "HHH000413: Warnings creating temp table : %s";
    private static final String willNotRegisterListeners = "HHH000414: Property hibernate.search.autoregister_listeners is set to false. No attempt will be made to register Hibernate Search event listeners.";
    private static final String writeLocksNotSupported = "HHH000416: Write locks via update not supported for non-versioned entities [%s]";
    private static final String writingGeneratedSchemaToFile = "HHH000417: Writing generated schema to file: %s";
    private static final String addingOverrideFor = "HHH000418: Adding override for %s: %s";
    private static final String resolvedSqlTypeDescriptorForDifferentSqlCode = "HHH000419: Resolved SqlTypeDescriptor is for a different SQL code. %s has sqlCode=%s; type override %s has sqlCode=%s";
    private static final String closingUnreleasedBatch = "HHH000420: Closing un-released batch";
    private static final String disablingContextualLOBCreation = "HHH000421: Disabling contextual LOB creation as %s is true";
    private static final String disablingContextualLOBCreationSinceConnectionNull = "HHH000422: Disabling contextual LOB creation as connection was null";
    private static final String disablingContextualLOBCreationSinceOldJdbcVersion = "HHH000423: Disabling contextual LOB creation as JDBC driver reported JDBC version [%s] less than 4";
    private static final String disablingContextualLOBCreationSinceCreateClobFailed = "HHH000424: Disabling contextual LOB creation as createClob() method threw error : %s";
    private static final String unableToCloseSessionButSwallowingError = "HHH000425: Could not close session; swallowing exception[%s] as transaction completed";
    private static final String setManagerLookupClass = "HHH000426: You should set hibernate.transaction.jta.platform if cache is enabled";
    private static final String legacyTransactionManagerStrategy = "HHH000428: Encountered legacy TransactionManagerLookup specified; convert to newer %s contract specified via %s setting";
    private static final String entityIdentifierValueBindingExists = "HHH000429: Setting entity-identifier value binding where one already existed : %s.";
    private static final String deprecatedDerbyDialect = "HHH000430: The DerbyDialect dialect has been deprecated; use one of the version-specific dialects instead";
    private static final String undeterminedH2Version = "HHH000431: Unable to determine H2 database version, certain features may not work";
    private static final String noColumnsSpecifiedForIndex = "HHH000432: There were not column names specified for index %s on table %s";
    private static final String timestampCachePuts = "HHH000433: update timestamps cache puts: %s";
    private static final String timestampCacheHits = "HHH000434: update timestamps cache hits: %s";
    private static final String timestampCacheMisses = "HHH000435: update timestamps cache misses: %s";
    private static final String entityManagerFactoryAlreadyRegistered = "HHH000436: Entity manager factory name (%s) is already registered.  If entity manager will be clustered or passivated, specify a unique value for property '%s'";
    private static final String cannotResolveNonNullableTransientDependencies = "HHH000437: Attempting to save one or more entities that have a non-nullable association with an unsaved transient entity. The unsaved transient entity must be saved in an operation prior to saving these dependent entities.\n\tUnsaved transient entity: (%s)\n\tDependent entities: (%s)\n\tNon-nullable association(s): (%s)";
    private static final String naturalIdCachePuts = "HHH000438: NaturalId cache puts: %s";
    private static final String naturalIdCacheHits = "HHH000439: NaturalId cache hits: %s";
    private static final String naturalIdCacheMisses = "HHH000440: NaturalId cache misses: %s";
    private static final String naturalIdMaxQueryTime = "HHH000441: Max NaturalId query time: %sms";
    private static final String naturalIdQueriesExecuted = "HHH000442: NaturalId queries executed to database: %s";
    private static final String tooManyInExpressions = "HHH000443: Dialect [%s] limits the number of elements in an IN predicate to %s entries.  However, the given parameter list [%s] contained %s entries, which will likely cause failures to execute the query in the database";
    private static final String usingFollowOnLocking = "HHH000444: Encountered request for locking however dialect reports that database prefers locking be done in a separate select (follow-on locking); results will be locked after initial query executes";
    private static final String aliasSpecificLockingWithFollowOnLocking = "HHH000445: Alias-specific lock modes requested, which is not currently supported with follow-on locking; all acquired locks will be [%s]";
    private static final String embedXmlAttributesNoLongerSupported = "HHH000446: embed-xml attributes were intended to be used for DOM4J entity mode. Since that entity mode has been removed, embed-xml attributes are no longer supported and should be removed from mappings.";
    private static final String explicitSkipLockedLockCombo = "HHH000447: Explicit use of UPGRADE_SKIPLOCKED in lock() calls is not recommended; use normal UPGRADE locking instead";
    private static final String multipleValidationModes = "HHH000448: 'javax.persistence.validation.mode' named multiple values : %s";
    private static final String nonCompliantMapConversion = "HHH000449: @Convert annotation applied to Map attribute [%s] did not explicitly specify attributeName using 'key'/'value' as required by spec; attempting to DoTheRightThing";
    private static final String alternateServiceRole = "HHH000450: Encountered request for Service by non-primary service role [%s -> %s]; please update usage";
    private static final String rollbackFromBackgroundThread = "HHH000451: Transaction afterCompletion called by a background thread; delaying afterCompletion processing until the original thread can handle it. [status=%s]";
    private static final String unableToLoadScannedClassOrResource = "HHH000452: Exception while loading a class or resource found during scanning";
    private static final String unableToDiscoverOsgiService = "HHH000453: Exception while discovering OSGi service implementations : %s";
    private static final String deprecatedManyToManyOuterJoin = "HHH000454: The outer-join attribute on <many-to-many> has been deprecated. Instead of outer-join=\"false\", use lazy=\"extra\" with <map>, <set>, <bag>, <idbag>, or <list>, which will only initialize entities (not as a proxy) as needed.";
    private static final String deprecatedManyToManyFetch = "HHH000455: The fetch attribute on <many-to-many> has been deprecated. Instead of fetch=\"select\", use lazy=\"extra\" with <map>, <set>, <bag>, <idbag>, or <list>, which will only initialize entities (not as a proxy) as needed.";
    private static final String unsupportedNamedParameters = "HHH000456: Named parameters are used for a callable statement, but database metadata indicates named parameters are not supported.";
    private static final String applyingExplicitDiscriminatorColumnForJoined = "HHH000457: Joined inheritance hierarchy [%1$s] defined explicit @DiscriminatorColumn.  Legacy Hibernate behavior was to ignore the @DiscriminatorColumn.  However, as part of issue HHH-6911 we now apply the explicit @DiscriminatorColumn.  If you would prefer the legacy behavior, enable the `%2$s` setting (%2$s=true)";
    private static final String creatingPooledLoOptimizer = "HHH000467: Creating pooled optimizer (lo) with [incrementSize=%s; returnClass=%s]";
    private static final String logBadHbmAttributeConverterType = "HHH000468: Unable to interpret type [%s] as an AttributeConverter due to an exception : %s";
    private static final String usingStoppedClassLoaderService = "HHH000469: The ClassLoaderService can not be reused. This instance was stopped already.";
    private static final String logUnexpectedSessionInCollectionNotConnected = "HHH000470: An unexpected session is defined for a collection, but the collection is not connected to that session. A persistent collection may only be associated with one session at a time. Overwriting session. %s";
    private static final String logCannotUnsetUnexpectedSessionInCollection = "HHH000471: Cannot unset session in a collection because an unexpected session is defined. A persistent collection may only be associated with one session at a time. %s";
    private static final String hikariProviderClassNotFound = "HHH000472: Hikari properties were encountered, but the Hikari ConnectionProvider was not found on the classpath; these properties are going to be ignored.";
    private static final String cachedFileObsolete = "HHH000473: Omitting cached file [%s] as the mapping file is newer";
    private static final String ambiguousPropertyMethods = "HHH000474: Ambiguous persistent property methods detected on %s; mark one as @Transient : [%s] and [%s]";
    private static final String logCannotLocateIndexColumnInformation = "HHH000475: Cannot locate column information using identifier [%s]; ignoring index [%s]";
    private static final String executingImportScript = "HHH000476: Executing import script '%s'";
    private static final String startingDelayedSchemaDrop = "HHH000477: Starting delayed drop of schema as part of SessionFactory shut-down'";
    private static final String unsuccessfulSchemaManagementCommand = "HHH000478: Unsuccessful: %s";
    private static final String collectionNotProcessedByFlush = "HHH000479: Collection [%s] was not processed by flush(). This is likely due to unsafe use of the session (e.g. used in multiple threads concurrently, updates during entity lifecycle hooks).";
    private static final String stalePersistenceContextInEntityEntry = "HHH000480: A ManagedEntity was associated with a stale PersistenceContext. A ManagedEntity may only be associated with one PersistenceContext at a time; %s";
    private static final String unknownJavaTypeNoEqualsHashCode = "HHH000481: Encountered Java type [%s] for which we could not locate a JavaTypeDescriptor and which does not appear to implement equals and/or hashCode.  This can lead to significant performance problems when performing equality/dirty checking involving this Java type.  Consider registering a custom JavaTypeDescriptor or at least implementing equals/hashCode.";
    private static final String cacheOrCacheableAnnotationOnNonRoot = "HHH000482: @javax.persistence.Cacheable or @org.hibernate.annotations.Cache used on a non-root entity: ignored for %s";
    private static final String emptyCompositesEnabled = "HHH000483: An experimental feature has been enabled (hibernate.create_empty_composites.enabled=true) that instantiates empty composite/embedded objects when all of its attribute values are null. This feature has known issues and should not be used in production until it is stabilized. See Hibernate Jira issue HHH-11936 for details.";
    private static final String immutableEntityUpdateQuery = "HHH000487: The query: [%s] attempts to update an immutable entity: %s";

    public J2CacheMessageLogger_$logger(Logger log) {
        super(log);
    }

    protected Locale getLoggingLocale() {
        return LOCALE;
    }

    public final void attemptToRestartAlreadyStartedJ2CacheProvider() {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.attemptToRestartAlreadyStartedJ2CacheProvider$str(), new Object[0]);
    }

    protected String attemptToRestartAlreadyStartedJ2CacheProvider$str() {
        return "HHH020001: Attempt to restart an already started J2CacheRegionFactory.  Use sessionFactory.close() between repeated calls to buildSessionFactory. Using previously created J2CacheRegionFactory. If this behaviour is required, consider using org.hibernate.cache.ehcache.SingletonJ2CacheRegionFactory.";
    }

    public final void unableToFindConfiguration(String name) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.unableToFindConfiguration$str(), name);
    }

    protected String unableToFindConfiguration$str() {
        return "HHH020002: Could not find configuration [%s]; using defaults.";
    }

    public final void unableToFindJ2CacheConfiguration(String name) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.unableToFindJ2CacheConfiguration$str(), name);
    }

    protected String unableToFindJ2CacheConfiguration$str() {
        return "HHH020003: Could not find a specific ehcache configuration for cache named [%s]; using defaults.";
    }

    public final void unableToLoadConfiguration(String configurationResourceName) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.unableToLoadConfiguration$str(), configurationResourceName);
    }

    protected String unableToLoadConfiguration$str() {
        return "HHH020004: A configurationResourceName was set to %s but the resource could not be loaded from the classpath. Ehcache will configure itself using defaults.";
    }

    public final void incompatibleCacheValueMode() {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.incompatibleCacheValueMode$str(), new Object[0]);
    }

    protected String incompatibleCacheValueMode$str() {
        return "HHH020005: The default cache value mode for this Ehcache configuration is \"identity\". This is incompatible with clustered Hibernate caching - the value mode has therefore been switched to \"serialization\"";
    }

    public final void incompatibleCacheValueModePerCache(String cacheName) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.incompatibleCacheValueModePerCache$str(), cacheName);
    }

    protected String incompatibleCacheValueModePerCache$str() {
        return "HHH020006: The value mode for the cache[%s] is \"identity\". This is incompatible with clustered Hibernate caching - the value mode has therefore been switched to \"serialization\"";
    }

    public final void readOnlyCacheConfiguredForMutableEntity(String entityName) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.readOnlyCacheConfiguredForMutableEntity$str(), entityName);
    }

    protected String readOnlyCacheConfiguredForMutableEntity$str() {
        return "HHH020007: read-only cache configured for mutable entity [%s]";
    }

    public final void softLockedCacheExpired(String regionName, Object key, String lock) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.softLockedCacheExpired$str(), regionName, key, lock);
    }

    protected String softLockedCacheExpired$str() {
        return "HHH020008: Cache[%s] Key[%s] Lockable[%s]\nA soft-locked cache entry was expired by the underlying Ehcache. If this happens regularly you should consider increasing the cache timeouts and/or capacity limits";
    }

    public final void alreadySessionBound() {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.alreadySessionBound$str(), new Object[0]);
    }

    protected String alreadySessionBound$str() {
        return "HHH000002: Already session bound on call to bind(); make sure you clean up your sessions!";
    }

    public final void autoCommitMode(boolean arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.autoCommitMode$str(), arg0);
    }

    protected String autoCommitMode$str() {
        return "HHH000006: Autocommit mode: %s";
    }

    public final void autoFlushWillNotWork() {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.autoFlushWillNotWork$str(), new Object[0]);
    }

    protected String autoFlushWillNotWork$str() {
        return "HHH000008: JTASessionContext being used with JDBC transactions; auto-flush will not operate correctly with getCurrentSession()";
    }

    public final void batchContainedStatementsOnRelease() {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.batchContainedStatementsOnRelease$str(), new Object[0]);
    }

    protected String batchContainedStatementsOnRelease$str() {
        return "HHH000010: On release of batch it still contained JDBC statements";
    }

    public final void bytecodeProvider(String arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.bytecodeProvider$str(), arg0);
    }

    protected String bytecodeProvider$str() {
        return "HHH000021: Bytecode provider name : %s";
    }

    public final void c3p0ProviderClassNotFound(String arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.c3p0ProviderClassNotFound$str(), arg0);
    }

    protected String c3p0ProviderClassNotFound$str() {
        return "HHH000022: c3p0 properties were encountered, but the %s provider class was not found on the classpath; these properties are going to be ignored.";
    }

    public final void cachedFileNotFound(String arg0, FileNotFoundException arg1) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.cachedFileNotFound$str(), arg0, arg1);
    }

    protected String cachedFileNotFound$str() {
        return "HHH000023: I/O reported cached file could not be found : %s : %s";
    }

    public final void cacheProvider(String arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.cacheProvider$str(), arg0);
    }

    protected String cacheProvider$str() {
        return "HHH000024: Cache provider: %s";
    }

    public final void callingJoinTransactionOnNonJtaEntityManager() {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.callingJoinTransactionOnNonJtaEntityManager$str(), new Object[0]);
    }

    protected String callingJoinTransactionOnNonJtaEntityManager$str() {
        return "HHH000027: Calling joinTransaction() on a non JTA EntityManager";
    }

    public final void closing() {
        super.log.logf(FQCN, Logger.Level.DEBUG, (Throwable)null, this.closing$str(), new Object[0]);
    }

    protected String closing$str() {
        return "HHH000031: Closing";
    }

    public final void collectionsFetched(long arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.collectionsFetched$str(), arg0);
    }

    protected String collectionsFetched$str() {
        return "HHH000032: Collections fetched (minimize this): %s";
    }

    public final void collectionsLoaded(long arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.collectionsLoaded$str(), arg0);
    }

    protected String collectionsLoaded$str() {
        return "HHH000033: Collections loaded: %s";
    }

    public final void collectionsRecreated(long arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.collectionsRecreated$str(), arg0);
    }

    protected String collectionsRecreated$str() {
        return "HHH000034: Collections recreated: %s";
    }

    public final void collectionsRemoved(long arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.collectionsRemoved$str(), arg0);
    }

    protected String collectionsRemoved$str() {
        return "HHH000035: Collections removed: %s";
    }

    public final void collectionsUpdated(long arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.collectionsUpdated$str(), arg0);
    }

    protected String collectionsUpdated$str() {
        return "HHH000036: Collections updated: %s";
    }

    public final void columns(Set arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.columns$str(), arg0);
    }

    protected String columns$str() {
        return "HHH000037: Columns: %s";
    }

    public final void compositeIdClassDoesNotOverrideEquals(String arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.compositeIdClassDoesNotOverrideEquals$str(), arg0);
    }

    protected String compositeIdClassDoesNotOverrideEquals$str() {
        return "HHH000038: Composite-id class does not override equals(): %s";
    }

    public final void compositeIdClassDoesNotOverrideHashCode(String arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.compositeIdClassDoesNotOverrideHashCode$str(), arg0);
    }

    protected String compositeIdClassDoesNotOverrideHashCode$str() {
        return "HHH000039: Composite-id class does not override hashCode(): %s";
    }

    public final void configurationResource(String arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.configurationResource$str(), arg0);
    }

    protected String configurationResource$str() {
        return "HHH000040: Configuration resource: %s";
    }

    public final void configuredSessionFactory(String arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.configuredSessionFactory$str(), arg0);
    }

    protected String configuredSessionFactory$str() {
        return "HHH000041: Configured SessionFactory: %s";
    }

    public final void configuringFromFile(String arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.configuringFromFile$str(), arg0);
    }

    protected String configuringFromFile$str() {
        return "HHH000042: Configuring from file: %s";
    }

    public final void configuringFromResource(String arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.configuringFromResource$str(), arg0);
    }

    protected String configuringFromResource$str() {
        return "HHH000043: Configuring from resource: %s";
    }

    public final void configuringFromUrl(URL arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.configuringFromUrl$str(), arg0);
    }

    protected String configuringFromUrl$str() {
        return "HHH000044: Configuring from URL: %s";
    }

    public final void configuringFromXmlDocument() {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.configuringFromXmlDocument$str(), new Object[0]);
    }

    protected String configuringFromXmlDocument$str() {
        return "HHH000045: Configuring from XML document";
    }

    public final void connectionsObtained(long arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.connectionsObtained$str(), arg0);
    }

    protected String connectionsObtained$str() {
        return "HHH000048: Connections obtained: %s";
    }

    public final void containerProvidingNullPersistenceUnitRootUrl() {
        super.log.logf(FQCN, Logger.Level.ERROR, (Throwable)null, this.containerProvidingNullPersistenceUnitRootUrl$str(), new Object[0]);
    }

    protected String containerProvidingNullPersistenceUnitRootUrl$str() {
        return "HHH000050: Container is providing a null PersistenceUnitRootUrl: discovery impossible";
    }

    public final void containsJoinFetchedCollection(String arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.containsJoinFetchedCollection$str(), arg0);
    }

    protected String containsJoinFetchedCollection$str() {
        return "HHH000051: Ignoring bag join fetch [%s] due to prior collection join fetch";
    }

    public final void creatingSubcontextInfo(String arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.creatingSubcontextInfo$str(), arg0);
    }

    protected String creatingSubcontextInfo$str() {
        return "HHH000053: Creating subcontext: %s";
    }

    public final void definingFlushBeforeCompletionIgnoredInHem(String arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.definingFlushBeforeCompletionIgnoredInHem$str(), arg0);
    }

    protected String definingFlushBeforeCompletionIgnoredInHem$str() {
        return "HHH000059: Defining %s=true ignored in HEM";
    }

    public final void deprecatedForceDescriminatorAnnotation() {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.deprecatedForceDescriminatorAnnotation$str(), new Object[0]);
    }

    protected String deprecatedForceDescriminatorAnnotation$str() {
        return "HHH000062: @ForceDiscriminator is deprecated use @DiscriminatorOptions instead.";
    }

    public final void deprecatedOracle9Dialect() {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.deprecatedOracle9Dialect$str(), new Object[0]);
    }

    protected String deprecatedOracle9Dialect$str() {
        return "HHH000063: The Oracle9Dialect dialect has been deprecated; use either Oracle9iDialect or Oracle10gDialect instead";
    }

    public final void deprecatedOracleDialect() {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.deprecatedOracleDialect$str(), new Object[0]);
    }

    protected String deprecatedOracleDialect$str() {
        return "HHH000064: The OracleDialect dialect has been deprecated; use Oracle8iDialect instead";
    }

    public final void deprecatedUuidGenerator(String arg0, String arg1) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.deprecatedUuidGenerator$str(), arg0, arg1);
    }

    protected String deprecatedUuidGenerator$str() {
        return "HHH000065: DEPRECATED : use [%s] instead with custom [%s] implementation";
    }

    public final void disallowingInsertStatementComment() {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.disallowingInsertStatementComment$str(), new Object[0]);
    }

    protected String disallowingInsertStatementComment$str() {
        return "HHH000067: Disallowing insert statement comment for select-identity due to Oracle driver bug";
    }

    public final void duplicateGeneratorName(String arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.duplicateGeneratorName$str(), arg0);
    }

    protected String duplicateGeneratorName$str() {
        return "HHH000069: Duplicate generator name %s";
    }

    public final void duplicateGeneratorTable(String arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.duplicateGeneratorTable$str(), arg0);
    }

    protected String duplicateGeneratorTable$str() {
        return "HHH000070: Duplicate generator table: %s";
    }

    public final void duplicateImport(String arg0, String arg1) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.duplicateImport$str(), arg0, arg1);
    }

    protected String duplicateImport$str() {
        return "HHH000071: Duplicate import: %s -> %s";
    }

    public final void duplicateJoins(String arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.duplicateJoins$str(), arg0);
    }

    protected String duplicateJoins$str() {
        return "HHH000072: Duplicate joins for class: %s";
    }

    public final void duplicateListener(String arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.duplicateListener$str(), arg0);
    }

    protected String duplicateListener$str() {
        return "HHH000073: entity-listener duplication, first event definition will be used: %s";
    }

    public final void duplicateMetadata() {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.duplicateMetadata$str(), new Object[0]);
    }

    protected String duplicateMetadata$str() {
        return "HHH000074: Found more than one <persistence-unit-metadata>, subsequent ignored";
    }

    public final void entitiesDeleted(long arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.entitiesDeleted$str(), arg0);
    }

    protected String entitiesDeleted$str() {
        return "HHH000076: Entities deleted: %s";
    }

    public final void entitiesFetched(long arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.entitiesFetched$str(), arg0);
    }

    protected String entitiesFetched$str() {
        return "HHH000077: Entities fetched (minimize this): %s";
    }

    public final void entitiesInserted(long arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.entitiesInserted$str(), arg0);
    }

    protected String entitiesInserted$str() {
        return "HHH000078: Entities inserted: %s";
    }

    public final void entitiesLoaded(long arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.entitiesLoaded$str(), arg0);
    }

    protected String entitiesLoaded$str() {
        return "HHH000079: Entities loaded: %s";
    }

    public final void entitiesUpdated(long arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.entitiesUpdated$str(), arg0);
    }

    protected String entitiesUpdated$str() {
        return "HHH000080: Entities updated: %s";
    }

    public final void entityAnnotationOnNonRoot(String arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.entityAnnotationOnNonRoot$str(), arg0);
    }

    protected String entityAnnotationOnNonRoot$str() {
        return "HHH000081: @org.hibernate.annotations.Entity used on a non root entity: ignored for %s";
    }

    public final void entityManagerClosedBySomeoneElse(String arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.entityManagerClosedBySomeoneElse$str(), arg0);
    }

    protected String entityManagerClosedBySomeoneElse$str() {
        return "HHH000082: Entity Manager closed by someone else (%s must not be used)";
    }

    public final void entityMappedAsNonAbstract(String arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.entityMappedAsNonAbstract$str(), arg0);
    }

    protected String entityMappedAsNonAbstract$str() {
        return "HHH000084: Entity [%s] is abstract-class/interface explicitly mapped as non-abstract; be sure to supply entity-names";
    }

    public final void exceptionHeaderFound(String arg0, String arg1) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.exceptionHeaderFound$str(), arg0, arg1);
    }

    protected String exceptionHeaderFound$str() {
        return "HHH000085: %s %s found";
    }

    public final void exceptionHeaderNotFound(String arg0, String arg1) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.exceptionHeaderNotFound$str(), arg0, arg1);
    }

    protected String exceptionHeaderNotFound$str() {
        return "HHH000086: %s No %s found";
    }

    public final void exceptionInAfterTransactionCompletionInterceptor(Throwable arg0) {
        super.log.logf(FQCN, Logger.Level.ERROR, arg0, this.exceptionInAfterTransactionCompletionInterceptor$str(), new Object[0]);
    }

    protected String exceptionInAfterTransactionCompletionInterceptor$str() {
        return "HHH000087: Exception in interceptor afterTransactionCompletion()";
    }

    public final void exceptionInBeforeTransactionCompletionInterceptor(Throwable arg0) {
        super.log.logf(FQCN, Logger.Level.ERROR, arg0, this.exceptionInBeforeTransactionCompletionInterceptor$str(), new Object[0]);
    }

    protected String exceptionInBeforeTransactionCompletionInterceptor$str() {
        return "HHH000088: Exception in interceptor beforeTransactionCompletion()";
    }

    public final void exceptionInSubResolver(String arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.exceptionInSubResolver$str(), arg0);
    }

    protected String exceptionInSubResolver$str() {
        return "HHH000089: Sub-resolver threw unexpected exception, continuing to next : %s";
    }

    public final void expectedType(String arg0, String arg1) {
        super.log.logf(FQCN, Logger.Level.ERROR, (Throwable)null, this.expectedType$str(), arg0, arg1);
    }

    protected String expectedType$str() {
        return "HHH000091: Expected type: %s, actual value: %s";
    }

    public final void expired(Object arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.expired$str(), arg0);
    }

    protected String expired$str() {
        return "HHH000092: An item was expired by the cache while it was locked (increase your cache timeout): %s";
    }

    public final void factoryBoundToJndiName(String arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.factoryBoundToJndiName$str(), arg0);
    }

    protected String factoryBoundToJndiName$str() {
        return "HHH000094: Bound factory to JNDI name: %s";
    }

    public final void factoryJndiRename(String arg0, String arg1) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.factoryJndiRename$str(), arg0, arg1);
    }

    protected String factoryJndiRename$str() {
        return "HHH000096: A factory was renamed from [%s] to [%s] in JNDI";
    }

    public final void factoryUnboundFromJndiName(String arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.factoryUnboundFromJndiName$str(), arg0);
    }

    protected String factoryUnboundFromJndiName$str() {
        return "HHH000097: Unbound factory from JNDI name: %s";
    }

    public final void factoryUnboundFromName(String arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.factoryUnboundFromName$str(), arg0);
    }

    protected String factoryUnboundFromName$str() {
        return "HHH000098: A factory was unbound from name: %s";
    }

    public final void failed(Throwable arg0) {
        super.log.logf(FQCN, Logger.Level.ERROR, (Throwable)null, this.failed$str(), arg0);
    }

    protected String failed$str() {
        return "HHH000099: an assertion failure occurred (this may indicate a bug in Hibernate, but is more likely due to unsafe use of the session): %s";
    }

    public final void failSafeCollectionsCleanup(CollectionLoadContext arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.failSafeCollectionsCleanup$str(), arg0);
    }

    protected String failSafeCollectionsCleanup$str() {
        return "HHH000100: Fail-safe cleanup (collections) : %s";
    }

    public final void failSafeEntitiesCleanup(EntityLoadContext arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.failSafeEntitiesCleanup$str(), arg0);
    }

    protected String failSafeEntitiesCleanup$str() {
        return "HHH000101: Fail-safe cleanup (entities) : %s";
    }

    public final void fetchingDatabaseMetadata() {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.fetchingDatabaseMetadata$str(), new Object[0]);
    }

    protected String fetchingDatabaseMetadata$str() {
        return "HHH000102: Fetching database metadata";
    }

    public final void firstOrMaxResultsSpecifiedWithCollectionFetch() {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.firstOrMaxResultsSpecifiedWithCollectionFetch$str(), new Object[0]);
    }

    protected String firstOrMaxResultsSpecifiedWithCollectionFetch$str() {
        return "HHH000104: firstResult/maxResults specified with collection fetch; applying in memory!";
    }

    public final void flushes(long arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.flushes$str(), arg0);
    }

    protected String flushes$str() {
        return "HHH000105: Flushes: %s";
    }

    public final void forcingContainerResourceCleanup() {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.forcingContainerResourceCleanup$str(), new Object[0]);
    }

    protected String forcingContainerResourceCleanup$str() {
        return "HHH000106: Forcing container resource cleanup on transaction completion";
    }

    public final void forcingTableUse() {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.forcingTableUse$str(), new Object[0]);
    }

    protected String forcingTableUse$str() {
        return "HHH000107: Forcing table use for sequence-style generator due to pooled optimizer selection where db does not support pooled sequences";
    }

    public final void foreignKeys(Set arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.foreignKeys$str(), arg0);
    }

    protected String foreignKeys$str() {
        return "HHH000108: Foreign keys: %s";
    }

    public final void foundMappingDocument(String arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.foundMappingDocument$str(), arg0);
    }

    protected String foundMappingDocument$str() {
        return "HHH000109: Found mapping document in jar: %s";
    }

    public final void gettersOfLazyClassesCannotBeFinal(String arg0, String arg1) {
        super.log.logf(FQCN, Logger.Level.ERROR, (Throwable)null, this.gettersOfLazyClassesCannotBeFinal$str(), arg0, arg1);
    }

    protected String gettersOfLazyClassesCannotBeFinal$str() {
        return "HHH000112: Getters of lazy classes cannot be final: %s.%s";
    }

    public final void guidGenerated(String arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.guidGenerated$str(), arg0);
    }

    protected String guidGenerated$str() {
        return "HHH000113: GUID identifier generated: %s";
    }

    public final void handlingTransientEntity() {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.handlingTransientEntity$str(), new Object[0]);
    }

    protected String handlingTransientEntity$str() {
        return "HHH000114: Handling transient entity in delete processing";
    }

    public final void hibernateConnectionPoolSize(int arg0, int arg1) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.hibernateConnectionPoolSize$str(), arg0, arg1);
    }

    protected String hibernateConnectionPoolSize$str() {
        return "HHH000115: Hibernate connection pool size: %s (min=%s)";
    }

    public final void honoringOptimizerSetting(String arg0, String arg1, int arg2, String arg3, int arg4) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.honoringOptimizerSetting$str(), new Object[]{arg0, arg1, arg2, arg3, arg4});
    }

    protected String honoringOptimizerSetting$str() {
        return "HHH000116: Config specified explicit optimizer of [%s], but [%s=%s]; using optimizer [%s] increment default of [%s].";
    }

    public final void hql(String arg0, Long arg1, Long arg2) {
        super.log.logf(FQCN, Logger.Level.DEBUG, (Throwable)null, this.hql$str(), arg0, arg1, arg2);
    }

    protected String hql$str() {
        return "HHH000117: HQL: %s, time: %sms, rows: %s";
    }

    public final void hsqldbSupportsOnlyReadCommittedIsolation() {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.hsqldbSupportsOnlyReadCommittedIsolation$str(), new Object[0]);
    }

    protected String hsqldbSupportsOnlyReadCommittedIsolation$str() {
        return "HHH000118: HSQLDB supports only READ_UNCOMMITTED isolation";
    }

    public final void hydratingEntitiesCount(int arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.hydratingEntitiesCount$str(), arg0);
    }

    protected String hydratingEntitiesCount$str() {
        return "HHH000119: On EntityLoadContext#clear, hydratingEntities contained [%s] entries";
    }

    public final void ignoringTableGeneratorConstraints(String arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.ignoringTableGeneratorConstraints$str(), arg0);
    }

    protected String ignoringTableGeneratorConstraints$str() {
        return "HHH000120: Ignoring unique constraints specified on table generator [%s]";
    }

    public final void ignoringUnrecognizedQueryHint(String arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.ignoringUnrecognizedQueryHint$str(), arg0);
    }

    protected String ignoringUnrecognizedQueryHint$str() {
        return "HHH000121: Ignoring unrecognized query hint [%s]";
    }

    public final void illegalPropertyGetterArgument(String arg0, String arg1) {
        super.log.logf(FQCN, Logger.Level.ERROR, (Throwable)null, this.illegalPropertyGetterArgument$str(), arg0, arg1);
    }

    protected String illegalPropertyGetterArgument$str() {
        return "HHH000122: IllegalArgumentException in class: %s, getter method of property: %s";
    }

    public final void illegalPropertySetterArgument(String arg0, String arg1) {
        super.log.logf(FQCN, Logger.Level.ERROR, (Throwable)null, this.illegalPropertySetterArgument$str(), arg0, arg1);
    }

    protected String illegalPropertySetterArgument$str() {
        return "HHH000123: IllegalArgumentException in class: %s, setter method of property: %s";
    }

    public final void immutableAnnotationOnNonRoot(String arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.immutableAnnotationOnNonRoot$str(), arg0);
    }

    protected String immutableAnnotationOnNonRoot$str() {
        return "HHH000124: @Immutable used on a non root entity: ignored for %s";
    }

    public final void incompleteMappingMetadataCacheProcessing() {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.incompleteMappingMetadataCacheProcessing$str(), new Object[0]);
    }

    protected String incompleteMappingMetadataCacheProcessing$str() {
        return "HHH000125: Mapping metadata cache was not completely processed";
    }

    public final void indexes(Set arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.indexes$str(), arg0);
    }

    protected String indexes$str() {
        return "HHH000126: Indexes: %s";
    }

    public final void couldNotBindJndiListener() {
        super.log.logf(FQCN, Logger.Level.DEBUG, (Throwable)null, this.couldNotBindJndiListener$str(), new Object[0]);
    }

    protected String couldNotBindJndiListener$str() {
        return "HHH000127: Could not bind JNDI listener";
    }

    public final void instantiatingExplicitConnectionProvider(String arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.instantiatingExplicitConnectionProvider$str(), arg0);
    }

    protected String instantiatingExplicitConnectionProvider$str() {
        return "HHH000130: Instantiating explicit connection provider: %s";
    }

    public final void invalidArrayElementType(String arg0) {
        super.log.logf(FQCN, Logger.Level.ERROR, (Throwable)null, this.invalidArrayElementType$str(), arg0);
    }

    protected String invalidArrayElementType$str() {
        return "HHH000132: Array element type error\n%s";
    }

    public final void invalidDiscriminatorAnnotation(String arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.invalidDiscriminatorAnnotation$str(), arg0);
    }

    protected String invalidDiscriminatorAnnotation$str() {
        return "HHH000133: Discriminator column has to be defined in the root entity, it will be ignored in subclass: %s";
    }

    public final void invalidEditOfReadOnlyItem(Object arg0) {
        super.log.logf(FQCN, Logger.Level.ERROR, (Throwable)null, this.invalidEditOfReadOnlyItem$str(), arg0);
    }

    protected String invalidEditOfReadOnlyItem$str() {
        return "HHH000134: Application attempted to edit read only item: %s";
    }

    public final void invalidJndiName(String arg0, JndiNameException arg1) {
        super.log.logf(FQCN, Logger.Level.ERROR, arg1, this.invalidJndiName$str(), arg0);
    }

    protected String invalidJndiName$str() {
        return "HHH000135: Invalid JNDI name: %s";
    }

    public final void invalidOnDeleteAnnotation(String arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.invalidOnDeleteAnnotation$str(), arg0);
    }

    protected String invalidOnDeleteAnnotation$str() {
        return "HHH000136: Inapropriate use of @OnDelete on entity, annotation ignored: %s";
    }

    public final void invalidPrimaryKeyJoinColumnAnnotation(String arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.invalidPrimaryKeyJoinColumnAnnotation$str(), arg0);
    }

    protected String invalidPrimaryKeyJoinColumnAnnotation$str() {
        return "HHH000137: Root entity should not hold a PrimaryKeyJoinColum(s), will be ignored: %s";
    }

    public final void invalidSubStrategy(String arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.invalidSubStrategy$str(), arg0);
    }

    protected String invalidSubStrategy$str() {
        return "HHH000138: Mixing inheritance strategy in a entity hierarchy is not allowed, ignoring sub strategy in: %s";
    }

    public final void invalidTableAnnotation(String arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.invalidTableAnnotation$str(), arg0);
    }

    protected String invalidTableAnnotation$str() {
        return "HHH000139: Illegal use of @Table in a subclass of a SINGLE_TABLE hierarchy: %s";
    }

    public final void jaccContextId(String arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.jaccContextId$str(), arg0);
    }

    protected String jaccContextId$str() {
        return "HHH000140: JACC contextID: %s";
    }

    public final void JavaSqlTypesMappedSameCodeMultipleTimes(int arg0, String arg1, String arg2) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.JavaSqlTypesMappedSameCodeMultipleTimes$str(), arg0, arg1, arg2);
    }

    protected String JavaSqlTypesMappedSameCodeMultipleTimes$str() {
        return "HHH000141: java.sql.Types mapped the same code [%s] multiple times; was [%s]; now [%s]";
    }

    protected String bytecodeEnhancementFailed$str() {
        return "HHH000142: Bytecode enhancement failed: %s";
    }

    public final String bytecodeEnhancementFailed(String arg0) {
        return String.format(this.getLoggingLocale(), this.bytecodeEnhancementFailed$str(), arg0);
    }

    public final void jdbcAutoCommitFalseBreaksEjb3Spec(String arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.jdbcAutoCommitFalseBreaksEjb3Spec$str(), arg0);
    }

    protected String jdbcAutoCommitFalseBreaksEjb3Spec$str() {
        return "HHH000144: %s = false breaks the EJB3 specification";
    }

    protected String jdbcRollbackFailed$str() {
        return "HHH000151: JDBC rollback failed";
    }

    public final String jdbcRollbackFailed() {
        return String.format(this.getLoggingLocale(), this.jdbcRollbackFailed$str());
    }

    public final void jndiInitialContextProperties(Hashtable arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.jndiInitialContextProperties$str(), arg0);
    }

    protected String jndiInitialContextProperties$str() {
        return "HHH000154: JNDI InitialContext properties:%s";
    }

    public final void jndiNameDoesNotHandleSessionFactoryReference(String arg0, ClassCastException arg1) {
        super.log.logf(FQCN, Logger.Level.ERROR, arg1, this.jndiNameDoesNotHandleSessionFactoryReference$str(), arg0);
    }

    protected String jndiNameDoesNotHandleSessionFactoryReference$str() {
        return "HHH000155: JNDI name %s does not handle a session factory reference";
    }

    public final void lazyPropertyFetchingAvailable(String arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.lazyPropertyFetchingAvailable$str(), arg0);
    }

    protected String lazyPropertyFetchingAvailable$str() {
        return "HHH000157: Lazy property fetching available for: %s";
    }

    public final void loadingCollectionKeyNotFound(CollectionKey arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.loadingCollectionKeyNotFound$str(), arg0);
    }

    protected String loadingCollectionKeyNotFound$str() {
        return "HHH000159: In CollectionLoadContext#endLoadingCollections, localLoadingCollectionKeys contained [%s], but no LoadingCollectionEntry was found in loadContexts";
    }

    public final void localLoadingCollectionKeysCount(int arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.localLoadingCollectionKeysCount$str(), arg0);
    }

    protected String localLoadingCollectionKeysCount$str() {
        return "HHH000160: On CollectionLoadContext#cleanup, localLoadingCollectionKeys contained [%s] entries";
    }

    public final void loggingStatistics() {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.loggingStatistics$str(), new Object[0]);
    }

    protected String loggingStatistics$str() {
        return "HHH000161: Logging statistics....";
    }

    public final void logicalConnectionClosed() {
        super.log.logf(FQCN, Logger.Level.DEBUG, (Throwable)null, this.logicalConnectionClosed$str(), new Object[0]);
    }

    protected String logicalConnectionClosed$str() {
        return "HHH000162: *** Logical connection closed ***";
    }

    public final void logicalConnectionReleasingPhysicalConnection() {
        super.log.logf(FQCN, Logger.Level.DEBUG, (Throwable)null, this.logicalConnectionReleasingPhysicalConnection$str(), new Object[0]);
    }

    protected String logicalConnectionReleasingPhysicalConnection$str() {
        return "HHH000163: Logical connection releasing its physical connection";
    }

    public final void maxQueryTime(long arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.maxQueryTime$str(), arg0);
    }

    protected String maxQueryTime$str() {
        return "HHH000173: Max query time: %sms";
    }

    public final void missingArguments(int arg0, int arg1) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.missingArguments$str(), arg0, arg1);
    }

    protected String missingArguments$str() {
        return "HHH000174: Function template anticipated %s arguments, but %s arguments encountered";
    }

    public final void missingEntityAnnotation(String arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.missingEntityAnnotation$str(), arg0);
    }

    protected String missingEntityAnnotation$str() {
        return "HHH000175: Class annotated @org.hibernate.annotations.Entity but not javax.persistence.Entity (most likely a user error): %s";
    }

    public final void namedQueryError(String arg0, HibernateException arg1) {
        super.log.logf(FQCN, Logger.Level.ERROR, arg1, this.namedQueryError$str(), arg0);
    }

    protected String namedQueryError$str() {
        return "HHH000177: Error in named query: %s";
    }

    public final void namingExceptionAccessingFactory(NamingException arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.namingExceptionAccessingFactory$str(), arg0);
    }

    protected String namingExceptionAccessingFactory$str() {
        return "HHH000178: Naming exception occurred accessing factory: %s";
    }

    public final void narrowingProxy(Class arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.narrowingProxy$str(), arg0);
    }

    protected String narrowingProxy$str() {
        return "HHH000179: Narrowing proxy to %s - this operation breaks ==";
    }

    public final void needsLimit() {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.needsLimit$str(), new Object[0]);
    }

    protected String needsLimit$str() {
        return "HHH000180: FirstResult/maxResults specified on polymorphic query; applying in memory!";
    }

    public final void noAppropriateConnectionProvider() {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.noAppropriateConnectionProvider$str(), new Object[0]);
    }

    protected String noAppropriateConnectionProvider$str() {
        return "HHH000181: No appropriate connection provider encountered, assuming application will be supplying connections";
    }

    public final void noDefaultConstructor(String arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.noDefaultConstructor$str(), arg0);
    }

    protected String noDefaultConstructor$str() {
        return "HHH000182: No default (no-argument) constructor for class: %s (class must be instantiated by Interceptor)";
    }

    public final void noPersistentClassesFound(String arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.noPersistentClassesFound$str(), arg0);
    }

    protected String noPersistentClassesFound$str() {
        return "HHH000183: no persistent classes found for query class: %s";
    }

    public final void noSessionFactoryWithJndiName(String arg0, NameNotFoundException arg1) {
        super.log.logf(FQCN, Logger.Level.ERROR, arg1, this.noSessionFactoryWithJndiName$str(), arg0);
    }

    protected String noSessionFactoryWithJndiName$str() {
        return "HHH000184: No session factory with JNDI name %s";
    }

    public final void optimisticLockFailures(long arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.optimisticLockFailures$str(), arg0);
    }

    protected String optimisticLockFailures$str() {
        return "HHH000187: Optimistic lock failures: %s";
    }

    public final void orderByAnnotationIndexedCollection() {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.orderByAnnotationIndexedCollection$str(), new Object[0]);
    }

    protected String orderByAnnotationIndexedCollection$str() {
        return "HHH000189: @OrderBy not allowed for an indexed collection, annotation ignored.";
    }

    public final void overridingTransactionStrategyDangerous(String arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.overridingTransactionStrategyDangerous$str(), arg0);
    }

    protected String overridingTransactionStrategyDangerous$str() {
        return "HHH000193: Overriding %s is dangerous, this might break the EJB3 specification implementation";
    }

    public final void packageNotFound(String arg0) {
        super.log.logf(FQCN, Logger.Level.DEBUG, (Throwable)null, this.packageNotFound$str(), arg0);
    }

    protected String packageNotFound$str() {
        return "HHH000194: Package not found or wo package-info.java: %s";
    }

    public final void parsingXmlError(int arg0, String arg1) {
        super.log.logf(FQCN, Logger.Level.ERROR, (Throwable)null, this.parsingXmlError$str(), arg0, arg1);
    }

    protected String parsingXmlError$str() {
        return "HHH000196: Error parsing XML (%s) : %s";
    }

    public final void parsingXmlErrorForFile(String arg0, int arg1, String arg2) {
        super.log.logf(FQCN, Logger.Level.ERROR, (Throwable)null, this.parsingXmlErrorForFile$str(), arg0, arg1, arg2);
    }

    protected String parsingXmlErrorForFile$str() {
        return "HHH000197: Error parsing XML: %s(%s) %s";
    }

    public final void parsingXmlWarning(int arg0, String arg1) {
        super.log.logf(FQCN, Logger.Level.ERROR, (Throwable)null, this.parsingXmlWarning$str(), arg0, arg1);
    }

    protected String parsingXmlWarning$str() {
        return "HHH000198: Warning parsing XML (%s) : %s";
    }

    public final void parsingXmlWarningForFile(String arg0, int arg1, String arg2) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.parsingXmlWarningForFile$str(), arg0, arg1, arg2);
    }

    protected String parsingXmlWarningForFile$str() {
        return "HHH000199: Warning parsing XML: %s(%s) %s";
    }

    public final void persistenceProviderCallerDoesNotImplementEjb3SpecCorrectly() {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.persistenceProviderCallerDoesNotImplementEjb3SpecCorrectly$str(), new Object[0]);
    }

    protected String persistenceProviderCallerDoesNotImplementEjb3SpecCorrectly$str() {
        return "HHH000200: Persistence provider caller does not implement the EJB3 spec correctly.PersistenceUnitInfo.getNewTempClassLoader() is null.";
    }

    public final void pooledOptimizerReportedInitialValue(IntegralDataTypeHolder arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.pooledOptimizerReportedInitialValue$str(), arg0);
    }

    protected String pooledOptimizerReportedInitialValue$str() {
        return "HHH000201: Pooled optimizer source reported [%s] as the initial value; use of 1 or greater highly recommended";
    }

    public final void preparedStatementAlreadyInBatch(String arg0) {
        super.log.logf(FQCN, Logger.Level.ERROR, (Throwable)null, this.preparedStatementAlreadyInBatch$str(), arg0);
    }

    protected String preparedStatementAlreadyInBatch$str() {
        return "HHH000202: PreparedStatement was already in the batch, [%s].";
    }

    public final void processEqualityExpression() {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.processEqualityExpression$str(), new Object[0]);
    }

    protected String processEqualityExpression$str() {
        return "HHH000203: processEqualityExpression() : No expression to process!";
    }

    public final void processingPersistenceUnitInfoName(String arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.processingPersistenceUnitInfoName$str(), arg0);
    }

    protected String processingPersistenceUnitInfoName$str() {
        return "HHH000204: Processing PersistenceUnitInfo [\n\tname: %s\n\t...]";
    }

    public final void propertiesLoaded(Properties arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.propertiesLoaded$str(), arg0);
    }

    protected String propertiesLoaded$str() {
        return "HHH000205: Loaded properties from resource hibernate.properties: %s";
    }

    public final void propertiesNotFound() {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.propertiesNotFound$str(), new Object[0]);
    }

    protected String propertiesNotFound$str() {
        return "HHH000206: hibernate.properties not found";
    }

    public final void propertyNotFound(String arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.propertyNotFound$str(), arg0);
    }

    protected String propertyNotFound$str() {
        return "HHH000207: Property %s not found in class but described in <mapping-file/> (possible typo error)";
    }

    public final void proxoolProviderClassNotFound(String arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.proxoolProviderClassNotFound$str(), arg0);
    }

    protected String proxoolProviderClassNotFound$str() {
        return "HHH000209: proxool properties were encountered, but the %s provider class was not found on the classpath; these properties are going to be ignored.";
    }

    public final void queriesExecuted(long arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.queriesExecuted$str(), arg0);
    }

    protected String queriesExecuted$str() {
        return "HHH000210: Queries executed to database: %s";
    }

    public final void queryCacheHits(long arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.queryCacheHits$str(), arg0);
    }

    protected String queryCacheHits$str() {
        return "HHH000213: Query cache hits: %s";
    }

    public final void queryCacheMisses(long arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.queryCacheMisses$str(), arg0);
    }

    protected String queryCacheMisses$str() {
        return "HHH000214: Query cache misses: %s";
    }

    public final void queryCachePuts(long arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.queryCachePuts$str(), arg0);
    }

    protected String queryCachePuts$str() {
        return "HHH000215: Query cache puts: %s";
    }

    public final void rdmsOs2200Dialect() {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.rdmsOs2200Dialect$str(), new Object[0]);
    }

    protected String rdmsOs2200Dialect$str() {
        return "HHH000218: RDMSOS2200Dialect version: 1.0";
    }

    public final void readingCachedMappings(File arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.readingCachedMappings$str(), arg0);
    }

    protected String readingCachedMappings$str() {
        return "HHH000219: Reading mappings from cache file: %s";
    }

    public final void readingMappingsFromFile(String arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.readingMappingsFromFile$str(), arg0);
    }

    protected String readingMappingsFromFile$str() {
        return "HHH000220: Reading mappings from file: %s";
    }

    public final void readingMappingsFromResource(String arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.readingMappingsFromResource$str(), arg0);
    }

    protected String readingMappingsFromResource$str() {
        return "HHH000221: Reading mappings from resource: %s";
    }

    public final void readOnlyCacheConfiguredForMutableCollection(String arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.readOnlyCacheConfiguredForMutableCollection$str(), arg0);
    }

    protected String readOnlyCacheConfiguredForMutableCollection$str() {
        return "HHH000222: read-only cache configured for mutable collection [%s]";
    }

    public final void recognizedObsoleteHibernateNamespace(String arg0, String arg1) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.recognizedObsoleteHibernateNamespace$str(), arg0, arg1);
    }

    protected String recognizedObsoleteHibernateNamespace$str() {
        return "HHH000223: Recognized obsolete hibernate namespace %s. Use namespace %s instead. Refer to Hibernate 3.6 Migration Guide!";
    }

    public final void renamedProperty(Object arg0, Object arg1) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.renamedProperty$str(), arg0, arg1);
    }

    protected String renamedProperty$str() {
        return "HHH000225: Property [%s] has been renamed to [%s]; update your properties appropriately";
    }

    public final void requiredDifferentProvider(String arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.requiredDifferentProvider$str(), arg0);
    }

    protected String requiredDifferentProvider$str() {
        return "HHH000226: Required a different provider: %s";
    }

    public final void runningHbm2ddlSchemaExport() {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.runningHbm2ddlSchemaExport$str(), new Object[0]);
    }

    protected String runningHbm2ddlSchemaExport$str() {
        return "HHH000227: Running hbm2ddl schema export";
    }

    public final void runningHbm2ddlSchemaUpdate() {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.runningHbm2ddlSchemaUpdate$str(), new Object[0]);
    }

    protected String runningHbm2ddlSchemaUpdate$str() {
        return "HHH000228: Running hbm2ddl schema update";
    }

    public final void runningSchemaValidator() {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.runningSchemaValidator$str(), new Object[0]);
    }

    protected String runningSchemaValidator$str() {
        return "HHH000229: Running schema validator";
    }

    public final void schemaExportComplete() {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.schemaExportComplete$str(), new Object[0]);
    }

    protected String schemaExportComplete$str() {
        return "HHH000230: Schema export complete";
    }

    public final void schemaExportUnsuccessful(Exception arg0) {
        super.log.logf(FQCN, Logger.Level.ERROR, arg0, this.schemaExportUnsuccessful$str(), new Object[0]);
    }

    protected String schemaExportUnsuccessful$str() {
        return "HHH000231: Schema export unsuccessful";
    }

    public final void schemaUpdateComplete() {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.schemaUpdateComplete$str(), new Object[0]);
    }

    protected String schemaUpdateComplete$str() {
        return "HHH000232: Schema update complete";
    }

    public final void scopingTypesToSessionFactoryAfterAlreadyScoped(SessionFactoryImplementor arg0, SessionFactoryImplementor arg1) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.scopingTypesToSessionFactoryAfterAlreadyScoped$str(), arg0, arg1);
    }

    protected String scopingTypesToSessionFactoryAfterAlreadyScoped$str() {
        return "HHH000233: Scoping types to session factory %s after already scoped %s";
    }

    public final void searchingForMappingDocuments(String arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.searchingForMappingDocuments$str(), arg0);
    }

    protected String searchingForMappingDocuments$str() {
        return "HHH000235: Searching for mapping documents in jar: %s";
    }

    public final void secondLevelCacheHits(long arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.secondLevelCacheHits$str(), arg0);
    }

    protected String secondLevelCacheHits$str() {
        return "HHH000237: Second level cache hits: %s";
    }

    public final void secondLevelCacheMisses(long arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.secondLevelCacheMisses$str(), arg0);
    }

    protected String secondLevelCacheMisses$str() {
        return "HHH000238: Second level cache misses: %s";
    }

    public final void secondLevelCachePuts(long arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.secondLevelCachePuts$str(), arg0);
    }

    protected String secondLevelCachePuts$str() {
        return "HHH000239: Second level cache puts: %s";
    }

    public final void serviceProperties(Properties arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.serviceProperties$str(), arg0);
    }

    protected String serviceProperties$str() {
        return "HHH000240: Service properties: %s";
    }

    public final void sessionsClosed(long arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.sessionsClosed$str(), arg0);
    }

    protected String sessionsClosed$str() {
        return "HHH000241: Sessions closed: %s";
    }

    public final void sessionsOpened(long arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.sessionsOpened$str(), arg0);
    }

    protected String sessionsOpened$str() {
        return "HHH000242: Sessions opened: %s";
    }

    public final void settersOfLazyClassesCannotBeFinal(String arg0, String arg1) {
        super.log.logf(FQCN, Logger.Level.ERROR, (Throwable)null, this.settersOfLazyClassesCannotBeFinal$str(), arg0, arg1);
    }

    protected String settersOfLazyClassesCannotBeFinal$str() {
        return "HHH000243: Setters of lazy classes cannot be final: %s.%s";
    }

    public final void sortAnnotationIndexedCollection() {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.sortAnnotationIndexedCollection$str(), new Object[0]);
    }

    protected String sortAnnotationIndexedCollection$str() {
        return "HHH000244: @Sort not allowed for an indexed collection, annotation ignored.";
    }

    public final void splitQueries(String arg0, int arg1) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.splitQueries$str(), arg0, arg1);
    }

    protected String splitQueries$str() {
        return "HHH000245: Manipulation query [%s] resulted in [%s] split queries";
    }

    public final void sqlWarning(int arg0, String arg1) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.sqlWarning$str(), arg0, arg1);
    }

    protected String sqlWarning$str() {
        return "HHH000247: SQL Error: %s, SQLState: %s";
    }

    public final void startingQueryCache(String arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.startingQueryCache$str(), arg0);
    }

    protected String startingQueryCache$str() {
        return "HHH000248: Starting query cache at region: %s";
    }

    public final void startingServiceAtJndiName(String arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.startingServiceAtJndiName$str(), arg0);
    }

    protected String startingServiceAtJndiName$str() {
        return "HHH000249: Starting service at JNDI name: %s";
    }

    public final void startingUpdateTimestampsCache(String arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.startingUpdateTimestampsCache$str(), arg0);
    }

    protected String startingUpdateTimestampsCache$str() {
        return "HHH000250: Starting update timestamps cache at region: %s";
    }

    public final void startTime(long arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.startTime$str(), arg0);
    }

    protected String startTime$str() {
        return "HHH000251: Start time: %s";
    }

    public final void statementsClosed(long arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.statementsClosed$str(), arg0);
    }

    protected String statementsClosed$str() {
        return "HHH000252: Statements closed: %s";
    }

    public final void statementsPrepared(long arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.statementsPrepared$str(), arg0);
    }

    protected String statementsPrepared$str() {
        return "HHH000253: Statements prepared: %s";
    }

    public final void stoppingService() {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.stoppingService$str(), new Object[0]);
    }

    protected String stoppingService$str() {
        return "HHH000255: Stopping service";
    }

    public final void subResolverException(String arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.subResolverException$str(), arg0);
    }

    protected String subResolverException$str() {
        return "HHH000257: sub-resolver threw unexpected exception, continuing to next : %s";
    }

    public final void successfulTransactions(long arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.successfulTransactions$str(), arg0);
    }

    protected String successfulTransactions$str() {
        return "HHH000258: Successful transactions: %s";
    }

    public final void synchronizationAlreadyRegistered(Synchronization arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.synchronizationAlreadyRegistered$str(), arg0);
    }

    protected String synchronizationAlreadyRegistered$str() {
        return "HHH000259: Synchronization [%s] was already registered";
    }

    public final void synchronizationFailed(Synchronization arg0, Throwable arg1) {
        super.log.logf(FQCN, Logger.Level.ERROR, (Throwable)null, this.synchronizationFailed$str(), arg0, arg1);
    }

    protected String synchronizationFailed$str() {
        return "HHH000260: Exception calling user Synchronization [%s] : %s";
    }

    public final void tableFound(String arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.tableFound$str(), arg0);
    }

    protected String tableFound$str() {
        return "HHH000261: Table found: %s";
    }

    public final void tableNotFound(String arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.tableNotFound$str(), arg0);
    }

    protected String tableNotFound$str() {
        return "HHH000262: Table not found: %s";
    }

    public final void multipleTablesFound(String arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.multipleTablesFound$str(), arg0);
    }

    protected String multipleTablesFound$str() {
        return "HHH000263: More than one table found: %s";
    }

    public final void transactions(long arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.transactions$str(), arg0);
    }

    protected String transactions$str() {
        return "HHH000266: Transactions: %s";
    }

    public final void transactionStartedOnNonRootSession() {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.transactionStartedOnNonRootSession$str(), new Object[0]);
    }

    protected String transactionStartedOnNonRootSession$str() {
        return "HHH000267: Transaction started on non-root session";
    }

    public final void transactionStrategy(String arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.transactionStrategy$str(), arg0);
    }

    protected String transactionStrategy$str() {
        return "HHH000268: Transaction strategy: %s";
    }

    public final void typeDefinedNoRegistrationKeys(BasicType arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.typeDefinedNoRegistrationKeys$str(), arg0);
    }

    protected String typeDefinedNoRegistrationKeys$str() {
        return "HHH000269: Type [%s] defined no registration keys; ignoring";
    }

    public final void typeRegistrationOverridesPrevious(String arg0, Type arg1) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.typeRegistrationOverridesPrevious$str(), arg0, arg1);
    }

    protected String typeRegistrationOverridesPrevious$str() {
        return "HHH000270: Type registration [%s] overrides previous : %s";
    }

    public final void unableToAccessEjb3Configuration(NamingException arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, arg0, this.unableToAccessEjb3Configuration$str(), new Object[0]);
    }

    protected String unableToAccessEjb3Configuration$str() {
        return "HHH000271: Naming exception occurred accessing Ejb3Configuration";
    }

    public final void unableToAccessSessionFactory(String arg0, NamingException arg1) {
        super.log.logf(FQCN, Logger.Level.ERROR, arg1, this.unableToAccessSessionFactory$str(), arg0);
    }

    protected String unableToAccessSessionFactory$str() {
        return "HHH000272: Error while accessing session factory with JNDI name %s";
    }

    public final void unableToAccessTypeInfoResultSet(String arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.unableToAccessTypeInfoResultSet$str(), arg0);
    }

    protected String unableToAccessTypeInfoResultSet$str() {
        return "HHH000273: Error accessing type info result set : %s";
    }

    public final void unableToApplyConstraints(String arg0, Exception arg1) {
        super.log.logf(FQCN, Logger.Level.WARN, arg1, this.unableToApplyConstraints$str(), arg0);
    }

    protected String unableToApplyConstraints$str() {
        return "HHH000274: Unable to apply constraints on DDL for %s";
    }

    public final void unableToBindEjb3ConfigurationToJndi(JndiException arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, arg0, this.unableToBindEjb3ConfigurationToJndi$str(), new Object[0]);
    }

    protected String unableToBindEjb3ConfigurationToJndi$str() {
        return "HHH000276: Could not bind Ejb3Configuration to JNDI";
    }

    public final void unableToBindFactoryToJndi(JndiException arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, arg0, this.unableToBindFactoryToJndi$str(), new Object[0]);
    }

    protected String unableToBindFactoryToJndi$str() {
        return "HHH000277: Could not bind factory to JNDI";
    }

    public final void unableToBindValueToParameter(String arg0, int arg1, String arg2) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.unableToBindValueToParameter$str(), arg0, arg1, arg2);
    }

    protected String unableToBindValueToParameter$str() {
        return "HHH000278: Could not bind value '%s' to parameter: %s; %s";
    }

    public final void unableToBuildEnhancementMetamodel(String arg0) {
        super.log.logf(FQCN, Logger.Level.ERROR, (Throwable)null, this.unableToBuildEnhancementMetamodel$str(), arg0);
    }

    protected String unableToBuildEnhancementMetamodel$str() {
        return "HHH000279: Unable to build enhancement metamodel for %s";
    }

    public final void unableToBuildSessionFactoryUsingMBeanClasspath(String arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.unableToBuildSessionFactoryUsingMBeanClasspath$str(), arg0);
    }

    protected String unableToBuildSessionFactoryUsingMBeanClasspath$str() {
        return "HHH000280: Could not build SessionFactory using the MBean classpath - will try again using client classpath: %s";
    }

    public final void unableToCleanUpCallableStatement(SQLException arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, arg0, this.unableToCleanUpCallableStatement$str(), new Object[0]);
    }

    protected String unableToCleanUpCallableStatement$str() {
        return "HHH000281: Unable to clean up callable statement";
    }

    public final void unableToCleanUpPreparedStatement(SQLException arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, arg0, this.unableToCleanUpPreparedStatement$str(), new Object[0]);
    }

    protected String unableToCleanUpPreparedStatement$str() {
        return "HHH000282: Unable to clean up prepared statement";
    }

    public final void unableToCleanupTemporaryIdTable(Throwable arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.unableToCleanupTemporaryIdTable$str(), arg0);
    }

    protected String unableToCleanupTemporaryIdTable$str() {
        return "HHH000283: Unable to cleanup temporary id table after use [%s]";
    }

    public final void unableToCloseConnection(Exception arg0) {
        super.log.logf(FQCN, Logger.Level.ERROR, arg0, this.unableToCloseConnection$str(), new Object[0]);
    }

    protected String unableToCloseConnection$str() {
        return "HHH000284: Error closing connection";
    }

    public final void unableToCloseInitialContext(String arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.unableToCloseInitialContext$str(), arg0);
    }

    protected String unableToCloseInitialContext$str() {
        return "HHH000285: Error closing InitialContext [%s]";
    }

    public final void unableToCloseInputFiles(String arg0, IOException arg1) {
        super.log.logf(FQCN, Logger.Level.ERROR, arg1, this.unableToCloseInputFiles$str(), arg0);
    }

    protected String unableToCloseInputFiles$str() {
        return "HHH000286: Error closing input files: %s";
    }

    public final void unableToCloseInputStream(IOException arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, arg0, this.unableToCloseInputStream$str(), new Object[0]);
    }

    protected String unableToCloseInputStream$str() {
        return "HHH000287: Could not close input stream";
    }

    public final void unableToCloseInputStreamForResource(String arg0, IOException arg1) {
        super.log.logf(FQCN, Logger.Level.WARN, arg1, this.unableToCloseInputStreamForResource$str(), arg0);
    }

    protected String unableToCloseInputStreamForResource$str() {
        return "HHH000288: Could not close input stream for %s";
    }

    public final void unableToCloseIterator(SQLException arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, arg0, this.unableToCloseIterator$str(), new Object[0]);
    }

    protected String unableToCloseIterator$str() {
        return "HHH000289: Unable to close iterator";
    }

    public final void unableToCloseJar(String arg0) {
        super.log.logf(FQCN, Logger.Level.ERROR, (Throwable)null, this.unableToCloseJar$str(), arg0);
    }

    protected String unableToCloseJar$str() {
        return "HHH000290: Could not close jar: %s";
    }

    public final void unableToCloseOutputFile(String arg0, IOException arg1) {
        super.log.logf(FQCN, Logger.Level.ERROR, arg1, this.unableToCloseOutputFile$str(), arg0);
    }

    protected String unableToCloseOutputFile$str() {
        return "HHH000291: Error closing output file: %s";
    }

    public final void unableToCloseOutputStream(IOException arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, arg0, this.unableToCloseOutputStream$str(), new Object[0]);
    }

    protected String unableToCloseOutputStream$str() {
        return "HHH000292: IOException occurred closing output stream";
    }

    public final void unableToCloseSession(HibernateException arg0) {
        super.log.logf(FQCN, Logger.Level.ERROR, arg0, this.unableToCloseSession$str(), new Object[0]);
    }

    protected String unableToCloseSession$str() {
        return "HHH000294: Could not close session";
    }

    public final void unableToCloseSessionDuringRollback(Exception arg0) {
        super.log.logf(FQCN, Logger.Level.ERROR, arg0, this.unableToCloseSessionDuringRollback$str(), new Object[0]);
    }

    protected String unableToCloseSessionDuringRollback$str() {
        return "HHH000295: Could not close session during rollback";
    }

    public final void unableToCloseStream(IOException arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, arg0, this.unableToCloseStream$str(), new Object[0]);
    }

    protected String unableToCloseStream$str() {
        return "HHH000296: IOException occurred closing stream";
    }

    public final void unableToCloseStreamError(IOException arg0) {
        super.log.logf(FQCN, Logger.Level.ERROR, (Throwable)null, this.unableToCloseStreamError$str(), arg0);
    }

    protected String unableToCloseStreamError$str() {
        return "HHH000297: Could not close stream on hibernate.properties: %s";
    }

    protected String unableToCommitJta$str() {
        return "HHH000298: JTA commit failed";
    }

    public final String unableToCommitJta() {
        return String.format(this.getLoggingLocale(), this.unableToCommitJta$str());
    }

    public final void unableToCompleteSchemaUpdate(Exception arg0) {
        super.log.logf(FQCN, Logger.Level.ERROR, arg0, this.unableToCompleteSchemaUpdate$str(), new Object[0]);
    }

    protected String unableToCompleteSchemaUpdate$str() {
        return "HHH000299: Could not complete schema update";
    }

    public final void unableToCompleteSchemaValidation(SQLException arg0) {
        super.log.logf(FQCN, Logger.Level.ERROR, arg0, this.unableToCompleteSchemaValidation$str(), new Object[0]);
    }

    protected String unableToCompleteSchemaValidation$str() {
        return "HHH000300: Could not complete schema validation";
    }

    public final void unableToConfigureSqlExceptionConverter(HibernateException arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.unableToConfigureSqlExceptionConverter$str(), arg0);
    }

    protected String unableToConfigureSqlExceptionConverter$str() {
        return "HHH000301: Unable to configure SQLExceptionConverter : %s";
    }

    public final void unableToConstructCurrentSessionContext(String arg0, Throwable arg1) {
        super.log.logf(FQCN, Logger.Level.ERROR, arg1, this.unableToConstructCurrentSessionContext$str(), arg0);
    }

    protected String unableToConstructCurrentSessionContext$str() {
        return "HHH000302: Unable to construct current session context [%s]";
    }

    public final void unableToConstructSqlExceptionConverter(Throwable arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.unableToConstructSqlExceptionConverter$str(), arg0);
    }

    protected String unableToConstructSqlExceptionConverter$str() {
        return "HHH000303: Unable to construct instance of specified SQLExceptionConverter : %s";
    }

    public final void unableToCopySystemProperties() {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.unableToCopySystemProperties$str(), new Object[0]);
    }

    protected String unableToCopySystemProperties$str() {
        return "HHH000304: Could not copy system properties, system properties will be ignored";
    }

    public final void unableToCreateProxyFactory(String arg0, HibernateException arg1) {
        super.log.logf(FQCN, Logger.Level.WARN, arg1, this.unableToCreateProxyFactory$str(), arg0);
    }

    protected String unableToCreateProxyFactory$str() {
        return "HHH000305: Could not create proxy factory for:%s";
    }

    public final void unableToCreateSchema(Exception arg0) {
        super.log.logf(FQCN, Logger.Level.ERROR, arg0, this.unableToCreateSchema$str(), new Object[0]);
    }

    protected String unableToCreateSchema$str() {
        return "HHH000306: Error creating schema ";
    }

    public final void unableToDeserializeCache(String arg0, SerializationException arg1) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.unableToDeserializeCache$str(), arg0, arg1);
    }

    protected String unableToDeserializeCache$str() {
        return "HHH000307: Could not deserialize cache file: %s : %s";
    }

    public final void unableToDestroyCache(String arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.unableToDestroyCache$str(), arg0);
    }

    protected String unableToDestroyCache$str() {
        return "HHH000308: Unable to destroy cache: %s";
    }

    public final void unableToDestroyQueryCache(String arg0, String arg1) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.unableToDestroyQueryCache$str(), arg0, arg1);
    }

    protected String unableToDestroyQueryCache$str() {
        return "HHH000309: Unable to destroy query cache: %s: %s";
    }

    public final void unableToDestroyUpdateTimestampsCache(String arg0, String arg1) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.unableToDestroyUpdateTimestampsCache$str(), arg0, arg1);
    }

    protected String unableToDestroyUpdateTimestampsCache$str() {
        return "HHH000310: Unable to destroy update timestamps cache: %s: %s";
    }

    public final void unableToDetermineLockModeValue(String arg0, Object arg1) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.unableToDetermineLockModeValue$str(), arg0, arg1);
    }

    protected String unableToDetermineLockModeValue$str() {
        return "HHH000311: Unable to determine lock mode value : %s -> %s";
    }

    protected String unableToDetermineTransactionStatus$str() {
        return "HHH000312: Could not determine transaction status";
    }

    public final String unableToDetermineTransactionStatus() {
        return String.format(this.getLoggingLocale(), this.unableToDetermineTransactionStatus$str());
    }

    protected String unableToDetermineTransactionStatusAfterCommit$str() {
        return "HHH000313: Could not determine transaction status after commit";
    }

    public final String unableToDetermineTransactionStatusAfterCommit() {
        return String.format(this.getLoggingLocale(), this.unableToDetermineTransactionStatusAfterCommit$str());
    }

    public final void unableToDropTemporaryIdTable(String arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.unableToDropTemporaryIdTable$str(), arg0);
    }

    protected String unableToDropTemporaryIdTable$str() {
        return "HHH000314: Unable to drop temporary id table after use [%s]";
    }

    public final void unableToExecuteBatch(Exception arg0, String arg1) {
        super.log.logf(FQCN, Logger.Level.ERROR, (Throwable)null, this.unableToExecuteBatch$str(), arg0, arg1);
    }

    protected String unableToExecuteBatch$str() {
        return "HHH000315: Exception executing batch [%s], SQL: %s";
    }

    public final void unableToExecuteResolver(DialectResolver arg0, String arg1) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.unableToExecuteResolver$str(), arg0, arg1);
    }

    protected String unableToExecuteResolver$str() {
        return "HHH000316: Error executing resolver [%s] : %s";
    }

    public final void unableToFindPersistenceXmlInClasspath() {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.unableToFindPersistenceXmlInClasspath$str(), new Object[0]);
    }

    protected String unableToFindPersistenceXmlInClasspath$str() {
        return "HHH000318: Could not find any META-INF/persistence.xml file in the classpath";
    }

    public final void unableToGetDatabaseMetadata(SQLException arg0) {
        super.log.logf(FQCN, Logger.Level.ERROR, arg0, this.unableToGetDatabaseMetadata$str(), new Object[0]);
    }

    protected String unableToGetDatabaseMetadata$str() {
        return "HHH000319: Could not get database metadata";
    }

    public final void unableToInstantiateConfiguredSchemaNameResolver(String arg0, String arg1) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.unableToInstantiateConfiguredSchemaNameResolver$str(), arg0, arg1);
    }

    protected String unableToInstantiateConfiguredSchemaNameResolver$str() {
        return "HHH000320: Unable to instantiate configured schema name resolver [%s] %s";
    }

    public final void unableToLocateCustomOptimizerClass(String arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.unableToLocateCustomOptimizerClass$str(), arg0);
    }

    protected String unableToLocateCustomOptimizerClass$str() {
        return "HHH000321: Unable to interpret specified optimizer [%s], falling back to noop";
    }

    public final void unableToInstantiateOptimizer(String arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.unableToInstantiateOptimizer$str(), arg0);
    }

    protected String unableToInstantiateOptimizer$str() {
        return "HHH000322: Unable to instantiate specified optimizer [%s], falling back to noop";
    }

    public final void unableToInstantiateUuidGenerationStrategy(Exception arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.unableToInstantiateUuidGenerationStrategy$str(), arg0);
    }

    protected String unableToInstantiateUuidGenerationStrategy$str() {
        return "HHH000325: Unable to instantiate UUID generation strategy class : %s";
    }

    public final void unableToJoinTransaction(String arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.unableToJoinTransaction$str(), arg0);
    }

    protected String unableToJoinTransaction$str() {
        return "HHH000326: Cannot join transaction: do not override %s";
    }

    public final void unableToLoadCommand(HibernateException arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.unableToLoadCommand$str(), arg0);
    }

    protected String unableToLoadCommand$str() {
        return "HHH000327: Error performing load command : %s";
    }

    public final void unableToLoadDerbyDriver(String arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.unableToLoadDerbyDriver$str(), arg0);
    }

    protected String unableToLoadDerbyDriver$str() {
        return "HHH000328: Unable to load/access derby driver class sysinfo to check versions : %s";
    }

    public final void unableToLoadProperties() {
        super.log.logf(FQCN, Logger.Level.ERROR, (Throwable)null, this.unableToLoadProperties$str(), new Object[0]);
    }

    protected String unableToLoadProperties$str() {
        return "HHH000329: Problem loading properties from hibernate.properties";
    }

    protected String unableToLocateConfigFile$str() {
        return "HHH000330: Unable to locate config file: %s";
    }

    public final String unableToLocateConfigFile(String arg0) {
        return String.format(this.getLoggingLocale(), this.unableToLocateConfigFile$str(), arg0);
    }

    public final void unableToLocateConfiguredSchemaNameResolver(String arg0, String arg1) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.unableToLocateConfiguredSchemaNameResolver$str(), arg0, arg1);
    }

    protected String unableToLocateConfiguredSchemaNameResolver$str() {
        return "HHH000331: Unable to locate configured schema name resolver class [%s] %s";
    }

    public final void unableToLocateMBeanServer() {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.unableToLocateMBeanServer$str(), new Object[0]);
    }

    protected String unableToLocateMBeanServer$str() {
        return "HHH000332: Unable to locate MBeanServer on JMX service shutdown";
    }

    public final void unableToLocateUuidGenerationStrategy(String arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.unableToLocateUuidGenerationStrategy$str(), arg0);
    }

    protected String unableToLocateUuidGenerationStrategy$str() {
        return "HHH000334: Unable to locate requested UUID generation strategy class : %s";
    }

    public final void unableToLogSqlWarnings(SQLException arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.unableToLogSqlWarnings$str(), arg0);
    }

    protected String unableToLogSqlWarnings$str() {
        return "HHH000335: Unable to log SQLWarnings : %s";
    }

    public final void unableToLogWarnings(SQLException arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, arg0, this.unableToLogWarnings$str(), new Object[0]);
    }

    protected String unableToLogWarnings$str() {
        return "HHH000336: Could not log warnings";
    }

    public final void unableToMarkForRollbackOnPersistenceException(Exception arg0) {
        super.log.logf(FQCN, Logger.Level.ERROR, arg0, this.unableToMarkForRollbackOnPersistenceException$str(), new Object[0]);
    }

    protected String unableToMarkForRollbackOnPersistenceException$str() {
        return "HHH000337: Unable to mark for rollback on PersistenceException: ";
    }

    public final void unableToMarkForRollbackOnTransientObjectException(Exception arg0) {
        super.log.logf(FQCN, Logger.Level.ERROR, arg0, this.unableToMarkForRollbackOnTransientObjectException$str(), new Object[0]);
    }

    protected String unableToMarkForRollbackOnTransientObjectException$str() {
        return "HHH000338: Unable to mark for rollback on TransientObjectException: ";
    }

    public final void unableToObjectConnectionMetadata(SQLException arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.unableToObjectConnectionMetadata$str(), arg0);
    }

    protected String unableToObjectConnectionMetadata$str() {
        return "HHH000339: Could not obtain connection metadata: %s";
    }

    public final void unableToObjectConnectionToQueryMetadata(SQLException arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.unableToObjectConnectionToQueryMetadata$str(), arg0);
    }

    protected String unableToObjectConnectionToQueryMetadata$str() {
        return "HHH000340: Could not obtain connection to query metadata: %s";
    }

    public final void unableToObtainConnectionMetadata(String arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.unableToObtainConnectionMetadata$str(), arg0);
    }

    protected String unableToObtainConnectionMetadata$str() {
        return "HHH000341: Could not obtain connection metadata : %s";
    }

    public final void unableToObtainConnectionToQueryMetadata(String arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.unableToObtainConnectionToQueryMetadata$str(), arg0);
    }

    protected String unableToObtainConnectionToQueryMetadata$str() {
        return "HHH000342: Could not obtain connection to query metadata : %s";
    }

    public final void unableToObtainInitialContext(NamingException arg0) {
        super.log.logf(FQCN, Logger.Level.ERROR, arg0, this.unableToObtainInitialContext$str(), new Object[0]);
    }

    protected String unableToObtainInitialContext$str() {
        return "HHH000343: Could not obtain initial context";
    }

    public final void unableToParseMetadata(String arg0) {
        super.log.logf(FQCN, Logger.Level.ERROR, (Throwable)null, this.unableToParseMetadata$str(), arg0);
    }

    protected String unableToParseMetadata$str() {
        return "HHH000344: Could not parse the package-level metadata [%s]";
    }

    protected String unableToPerformJdbcCommit$str() {
        return "HHH000345: JDBC commit failed";
    }

    public final String unableToPerformJdbcCommit() {
        return String.format(this.getLoggingLocale(), this.unableToPerformJdbcCommit$str());
    }

    public final void unableToPerformManagedFlush(String arg0) {
        super.log.logf(FQCN, Logger.Level.ERROR, (Throwable)null, this.unableToPerformManagedFlush$str(), arg0);
    }

    protected String unableToPerformManagedFlush$str() {
        return "HHH000346: Error during managed flush [%s]";
    }

    protected String unableToQueryDatabaseMetadata$str() {
        return "HHH000347: Unable to query java.sql.DatabaseMetaData";
    }

    public final String unableToQueryDatabaseMetadata() {
        return String.format(this.getLoggingLocale(), this.unableToQueryDatabaseMetadata$str());
    }

    public final void unableToReadClass(String arg0) {
        super.log.logf(FQCN, Logger.Level.ERROR, (Throwable)null, this.unableToReadClass$str(), arg0);
    }

    protected String unableToReadClass$str() {
        return "HHH000348: Unable to read class: %s";
    }

    public final void unableToReadColumnValueFromResultSet(String arg0, String arg1) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.unableToReadColumnValueFromResultSet$str(), arg0, arg1);
    }

    protected String unableToReadColumnValueFromResultSet$str() {
        return "HHH000349: Could not read column value from result set: %s; %s";
    }

    protected String unableToReadHiValue$str() {
        return "HHH000350: Could not read a hi value - you need to populate the table: %s";
    }

    public final String unableToReadHiValue(String arg0) {
        return String.format(this.getLoggingLocale(), this.unableToReadHiValue$str(), arg0);
    }

    public final void unableToReadOrInitHiValue(SQLException arg0) {
        super.log.logf(FQCN, Logger.Level.ERROR, arg0, this.unableToReadOrInitHiValue$str(), new Object[0]);
    }

    protected String unableToReadOrInitHiValue$str() {
        return "HHH000351: Could not read or init a hi value";
    }

    public final void unableToReleaseBatchStatement() {
        super.log.logf(FQCN, Logger.Level.ERROR, (Throwable)null, this.unableToReleaseBatchStatement$str(), new Object[0]);
    }

    protected String unableToReleaseBatchStatement$str() {
        return "HHH000352: Unable to release batch statement...";
    }

    public final void unableToReleaseCacheLock(CacheException arg0) {
        super.log.logf(FQCN, Logger.Level.ERROR, (Throwable)null, this.unableToReleaseCacheLock$str(), arg0);
    }

    protected String unableToReleaseCacheLock$str() {
        return "HHH000353: Could not release a cache lock : %s";
    }

    public final void unableToReleaseContext(String arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.unableToReleaseContext$str(), arg0);
    }

    protected String unableToReleaseContext$str() {
        return "HHH000354: Unable to release initial context: %s";
    }

    public final void unableToReleaseCreatedMBeanServer(String arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.unableToReleaseCreatedMBeanServer$str(), arg0);
    }

    protected String unableToReleaseCreatedMBeanServer$str() {
        return "HHH000355: Unable to release created MBeanServer : %s";
    }

    public final void unableToReleaseIsolatedConnection(Throwable arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.unableToReleaseIsolatedConnection$str(), arg0);
    }

    protected String unableToReleaseIsolatedConnection$str() {
        return "HHH000356: Unable to release isolated connection [%s]";
    }

    public final void unableToReleaseTypeInfoResultSet() {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.unableToReleaseTypeInfoResultSet$str(), new Object[0]);
    }

    protected String unableToReleaseTypeInfoResultSet$str() {
        return "HHH000357: Unable to release type info result set";
    }

    public final void unableToRemoveBagJoinFetch() {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.unableToRemoveBagJoinFetch$str(), new Object[0]);
    }

    protected String unableToRemoveBagJoinFetch$str() {
        return "HHH000358: Unable to erase previously added bag join fetch";
    }

    public final void unableToResolveAggregateFunction(String arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.unableToResolveAggregateFunction$str(), arg0);
    }

    protected String unableToResolveAggregateFunction$str() {
        return "HHH000359: Could not resolve aggregate function [%s]; using standard definition";
    }

    public final void unableToResolveMappingFile(String arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.unableToResolveMappingFile$str(), arg0);
    }

    protected String unableToResolveMappingFile$str() {
        return "HHH000360: Unable to resolve mapping file [%s]";
    }

    public final void unableToRetrieveCache(String arg0, String arg1) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.unableToRetrieveCache$str(), arg0, arg1);
    }

    protected String unableToRetrieveCache$str() {
        return "HHH000361: Unable to retrieve cache from JNDI [%s]: %s";
    }

    public final void unableToRetrieveTypeInfoResultSet(String arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.unableToRetrieveTypeInfoResultSet$str(), arg0);
    }

    protected String unableToRetrieveTypeInfoResultSet$str() {
        return "HHH000362: Unable to retrieve type info result set : %s";
    }

    public final void unableToRollbackConnection(Exception arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.unableToRollbackConnection$str(), arg0);
    }

    protected String unableToRollbackConnection$str() {
        return "HHH000363: Unable to rollback connection on exception [%s]";
    }

    public final void unableToRollbackIsolatedTransaction(Exception arg0, Exception arg1) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.unableToRollbackIsolatedTransaction$str(), arg0, arg1);
    }

    protected String unableToRollbackIsolatedTransaction$str() {
        return "HHH000364: Unable to rollback isolated transaction on error [%s] : [%s]";
    }

    protected String unableToRollbackJta$str() {
        return "HHH000365: JTA rollback failed";
    }

    public final String unableToRollbackJta() {
        return String.format(this.getLoggingLocale(), this.unableToRollbackJta$str());
    }

    public final void unableToRunSchemaUpdate(Exception arg0) {
        super.log.logf(FQCN, Logger.Level.ERROR, arg0, this.unableToRunSchemaUpdate$str(), new Object[0]);
    }

    protected String unableToRunSchemaUpdate$str() {
        return "HHH000366: Error running schema update";
    }

    public final void unableToSetTransactionToRollbackOnly(SystemException arg0) {
        super.log.logf(FQCN, Logger.Level.ERROR, arg0, this.unableToSetTransactionToRollbackOnly$str(), new Object[0]);
    }

    protected String unableToSetTransactionToRollbackOnly$str() {
        return "HHH000367: Could not set transaction to rollback only";
    }

    public final void unableToStopHibernateService(Exception arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, arg0, this.unableToStopHibernateService$str(), new Object[0]);
    }

    protected String unableToStopHibernateService$str() {
        return "HHH000368: Exception while stopping service";
    }

    public final void unableToStopService(Class arg0, String arg1) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.unableToStopService$str(), arg0, arg1);
    }

    protected String unableToStopService$str() {
        return "HHH000369: Error stopping service [%s] : %s";
    }

    public final void unableToSwitchToMethodUsingColumnIndex(Method arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.unableToSwitchToMethodUsingColumnIndex$str(), arg0);
    }

    protected String unableToSwitchToMethodUsingColumnIndex$str() {
        return "HHH000370: Exception switching from method: [%s] to a method using the column index. Reverting to using: [%<s]";
    }

    public final void unableToSynchronizeDatabaseStateWithSession(HibernateException arg0) {
        super.log.logf(FQCN, Logger.Level.ERROR, (Throwable)null, this.unableToSynchronizeDatabaseStateWithSession$str(), arg0);
    }

    protected String unableToSynchronizeDatabaseStateWithSession$str() {
        return "HHH000371: Could not synchronize database state with session: %s";
    }

    public final void unableToToggleAutoCommit(Exception arg0) {
        super.log.logf(FQCN, Logger.Level.ERROR, arg0, this.unableToToggleAutoCommit$str(), new Object[0]);
    }

    protected String unableToToggleAutoCommit$str() {
        return "HHH000372: Could not toggle autocommit";
    }

    public final void unableToTransformClass(String arg0) {
        super.log.logf(FQCN, Logger.Level.ERROR, (Throwable)null, this.unableToTransformClass$str(), arg0);
    }

    protected String unableToTransformClass$str() {
        return "HHH000373: Unable to transform class: %s";
    }

    public final void unableToUnbindFactoryFromJndi(JndiException arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, arg0, this.unableToUnbindFactoryFromJndi$str(), new Object[0]);
    }

    protected String unableToUnbindFactoryFromJndi$str() {
        return "HHH000374: Could not unbind factory from JNDI";
    }

    protected String unableToUpdateHiValue$str() {
        return "HHH000375: Could not update hi value in: %s";
    }

    public final String unableToUpdateHiValue(String arg0) {
        return String.format(this.getLoggingLocale(), this.unableToUpdateHiValue$str(), arg0);
    }

    public final void unableToUpdateQueryHiValue(String arg0, SQLException arg1) {
        super.log.logf(FQCN, Logger.Level.ERROR, arg1, this.unableToUpdateQueryHiValue$str(), arg0);
    }

    protected String unableToUpdateQueryHiValue$str() {
        return "HHH000376: Could not updateQuery hi value in: %s";
    }

    public final void unableToWrapResultSet(SQLException arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, arg0, this.unableToWrapResultSet$str(), new Object[0]);
    }

    protected String unableToWrapResultSet$str() {
        return "HHH000377: Error wrapping result set";
    }

    public final void unableToWriteCachedFile(String arg0, String arg1) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.unableToWriteCachedFile$str(), arg0, arg1);
    }

    protected String unableToWriteCachedFile$str() {
        return "HHH000378: I/O reported error writing cached file : %s: %s";
    }

    public final void unexpectedLiteralTokenType(int arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.unexpectedLiteralTokenType$str(), arg0);
    }

    protected String unexpectedLiteralTokenType$str() {
        return "HHH000380: Unexpected literal token type [%s] passed for numeric processing";
    }

    public final void unexpectedRowCounts() {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.unexpectedRowCounts$str(), new Object[0]);
    }

    protected String unexpectedRowCounts$str() {
        return "HHH000381: JDBC driver did not return the expected number of row counts";
    }

    public final void unknownBytecodeProvider(String arg0, String arg1) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.unknownBytecodeProvider$str(), arg0, arg1);
    }

    protected String unknownBytecodeProvider$str() {
        return "HHH000382: unrecognized bytecode provider [%s], using [%s] by default";
    }

    public final void unknownIngresVersion(int arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.unknownIngresVersion$str(), arg0);
    }

    protected String unknownIngresVersion$str() {
        return "HHH000383: Unknown Ingres major version [%s]; using Ingres 9.2 dialect";
    }

    public final void unknownOracleVersion(int arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.unknownOracleVersion$str(), arg0);
    }

    protected String unknownOracleVersion$str() {
        return "HHH000384: Unknown Oracle major version [%s]";
    }

    public final void unknownSqlServerVersion(int arg0, Class<? extends Dialect> arg1) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.unknownSqlServerVersion$str(), arg0, arg1);
    }

    protected String unknownSqlServerVersion$str() {
        return "HHH000385: Unknown Microsoft SQL Server major version [%s] using [%s] dialect";
    }

    public final void unregisteredResultSetWithoutStatement() {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.unregisteredResultSetWithoutStatement$str(), new Object[0]);
    }

    protected String unregisteredResultSetWithoutStatement$str() {
        return "HHH000386: ResultSet had no statement associated with it, but was not yet registered";
    }

    public final void unregisteredStatement() {
        super.log.logf(FQCN, Logger.Level.DEBUG, (Throwable)null, this.unregisteredStatement$str(), new Object[0]);
    }

    protected String unregisteredStatement$str() {
        return "HHH000387: ResultSet's statement was not registered";
    }

    public final void unsuccessful(String arg0) {
        super.log.logf(FQCN, Logger.Level.ERROR, (Throwable)null, this.unsuccessful$str(), arg0);
    }

    protected String unsuccessful$str() {
        return "HHH000388: Unsuccessful: %s";
    }

    public final void unsuccessfulCreate(String arg0) {
        super.log.logf(FQCN, Logger.Level.ERROR, (Throwable)null, this.unsuccessfulCreate$str(), arg0);
    }

    protected String unsuccessfulCreate$str() {
        return "HHH000389: Unsuccessful: %s";
    }

    public final void unsupportedAfterStatement() {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.unsupportedAfterStatement$str(), new Object[0]);
    }

    protected String unsupportedAfterStatement$str() {
        return "HHH000390: Overriding release mode as connection provider does not support 'after_statement'";
    }

    public final void unsupportedIngresVersion() {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.unsupportedIngresVersion$str(), new Object[0]);
    }

    protected String unsupportedIngresVersion$str() {
        return "HHH000391: Ingres 10 is not yet fully supported; using Ingres 9.3 dialect";
    }

    public final void unsupportedInitialValue(String arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.unsupportedInitialValue$str(), arg0);
    }

    protected String unsupportedInitialValue$str() {
        return "HHH000392: Hibernate does not support SequenceGenerator.initialValue() unless '%s' set";
    }

    public final void unsupportedMultiTableBulkHqlJpaql(int arg0, int arg1, int arg2) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.unsupportedMultiTableBulkHqlJpaql$str(), arg0, arg1, arg2);
    }

    protected String unsupportedMultiTableBulkHqlJpaql$str() {
        return "HHH000393: The %s.%s.%s version of H2 implements temporary table creation such that it commits current transaction; multi-table, bulk hql/jpaql will not work properly";
    }

    public final void unsupportedOracleVersion() {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.unsupportedOracleVersion$str(), new Object[0]);
    }

    protected String unsupportedOracleVersion$str() {
        return "HHH000394: Oracle 11g is not yet fully supported; using Oracle 10g dialect";
    }

    public final void unsupportedProperty(Object arg0, Object arg1) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.unsupportedProperty$str(), arg0, arg1);
    }

    protected String unsupportedProperty$str() {
        return "HHH000395: Usage of obsolete property: %s no longer supported, use: %s";
    }

    public final void updatingSchema() {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.updatingSchema$str(), new Object[0]);
    }

    protected String updatingSchema$str() {
        return "HHH000396: Updating schema";
    }

    public final void usingAstQueryTranslatorFactory() {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.usingAstQueryTranslatorFactory$str(), new Object[0]);
    }

    protected String usingAstQueryTranslatorFactory$str() {
        return "HHH000397: Using ASTQueryTranslatorFactory";
    }

    public final void usingDefaultIdGeneratorSegmentValue(String arg0, String arg1, String arg2) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.usingDefaultIdGeneratorSegmentValue$str(), arg0, arg1, arg2);
    }

    protected String usingDefaultIdGeneratorSegmentValue$str() {
        return "HHH000398: Explicit segment value for id generator [%s.%s] suggested; using default [%s]";
    }

    public final void usingDefaultTransactionStrategy() {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.usingDefaultTransactionStrategy$str(), new Object[0]);
    }

    protected String usingDefaultTransactionStrategy$str() {
        return "HHH000399: Using default transaction strategy (direct JDBC transactions)";
    }

    public final void usingDialect(Dialect arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.usingDialect$str(), arg0);
    }

    protected String usingDialect$str() {
        return "HHH000400: Using dialect: %s";
    }

    public final void usingOldDtd() {
        super.log.logf(FQCN, Logger.Level.ERROR, (Throwable)null, this.usingOldDtd$str(), new Object[0]);
    }

    protected String usingOldDtd$str() {
        return "HHH000404: Don't use old DTDs, read the Hibernate 3.x Migration Guide!";
    }

    public final void usingReflectionOptimizer() {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.usingReflectionOptimizer$str(), new Object[0]);
    }

    protected String usingReflectionOptimizer$str() {
        return "HHH000406: Using bytecode reflection optimizer";
    }

    public final void usingStreams() {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.usingStreams$str(), new Object[0]);
    }

    protected String usingStreams$str() {
        return "HHH000407: Using java.io streams to persist binary types";
    }

    public final void usingTimestampWorkaround() {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.usingTimestampWorkaround$str(), new Object[0]);
    }

    protected String usingTimestampWorkaround$str() {
        return "HHH000408: Using workaround for JVM bug in java.sql.Timestamp";
    }

    public final void usingUuidHexGenerator(String arg0, String arg1) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.usingUuidHexGenerator$str(), arg0, arg1);
    }

    protected String usingUuidHexGenerator$str() {
        return "HHH000409: Using %s which does not generate IETF RFC 4122 compliant UUID values; consider using %s instead";
    }

    public final void validatorNotFound() {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.validatorNotFound$str(), new Object[0]);
    }

    protected String validatorNotFound$str() {
        return "HHH000410: Hibernate Validator not found: ignoring";
    }

    public final void version(String arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.version$str(), arg0);
    }

    protected String version$str() {
        return "HHH000412: Hibernate Core {%s}";
    }

    public final void warningsCreatingTempTable(SQLWarning arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.warningsCreatingTempTable$str(), arg0);
    }

    protected String warningsCreatingTempTable$str() {
        return "HHH000413: Warnings creating temp table : %s";
    }

    public final void willNotRegisterListeners() {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.willNotRegisterListeners$str(), new Object[0]);
    }

    protected String willNotRegisterListeners$str() {
        return "HHH000414: Property hibernate.search.autoregister_listeners is set to false. No attempt will be made to register Hibernate Search event listeners.";
    }

    public final void writeLocksNotSupported(String arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.writeLocksNotSupported$str(), arg0);
    }

    protected String writeLocksNotSupported$str() {
        return "HHH000416: Write locks via update not supported for non-versioned entities [%s]";
    }

    public final void writingGeneratedSchemaToFile(String arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.writingGeneratedSchemaToFile$str(), arg0);
    }

    protected String writingGeneratedSchemaToFile$str() {
        return "HHH000417: Writing generated schema to file: %s";
    }

    public final void addingOverrideFor(String arg0, String arg1) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.addingOverrideFor$str(), arg0, arg1);
    }

    protected String addingOverrideFor$str() {
        return "HHH000418: Adding override for %s: %s";
    }

    public final void resolvedSqlTypeDescriptorForDifferentSqlCode(String arg0, String arg1, String arg2, String arg3) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.resolvedSqlTypeDescriptorForDifferentSqlCode$str(), new Object[]{arg0, arg1, arg2, arg3});
    }

    protected String resolvedSqlTypeDescriptorForDifferentSqlCode$str() {
        return "HHH000419: Resolved SqlTypeDescriptor is for a different SQL code. %s has sqlCode=%s; type override %s has sqlCode=%s";
    }

    public final void closingUnreleasedBatch() {
        super.log.logf(FQCN, Logger.Level.DEBUG, (Throwable)null, this.closingUnreleasedBatch$str(), new Object[0]);
    }

    protected String closingUnreleasedBatch$str() {
        return "HHH000420: Closing un-released batch";
    }

    public final void disablingContextualLOBCreation(String arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.disablingContextualLOBCreation$str(), arg0);
    }

    protected String disablingContextualLOBCreation$str() {
        return "HHH000421: Disabling contextual LOB creation as %s is true";
    }

    public final void disablingContextualLOBCreationSinceConnectionNull() {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.disablingContextualLOBCreationSinceConnectionNull$str(), new Object[0]);
    }

    protected String disablingContextualLOBCreationSinceConnectionNull$str() {
        return "HHH000422: Disabling contextual LOB creation as connection was null";
    }

    public final void disablingContextualLOBCreationSinceOldJdbcVersion(int arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.disablingContextualLOBCreationSinceOldJdbcVersion$str(), arg0);
    }

    protected String disablingContextualLOBCreationSinceOldJdbcVersion$str() {
        return "HHH000423: Disabling contextual LOB creation as JDBC driver reported JDBC version [%s] less than 4";
    }

    public final void disablingContextualLOBCreationSinceCreateClobFailed(Throwable arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.disablingContextualLOBCreationSinceCreateClobFailed$str(), arg0);
    }

    protected String disablingContextualLOBCreationSinceCreateClobFailed$str() {
        return "HHH000424: Disabling contextual LOB creation as createClob() method threw error : %s";
    }

    public final void unableToCloseSessionButSwallowingError(HibernateException arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.unableToCloseSessionButSwallowingError$str(), arg0);
    }

    protected String unableToCloseSessionButSwallowingError$str() {
        return "HHH000425: Could not close session; swallowing exception[%s] as transaction completed";
    }

    public final void setManagerLookupClass() {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.setManagerLookupClass$str(), new Object[0]);
    }

    protected String setManagerLookupClass$str() {
        return "HHH000426: You should set hibernate.transaction.jta.platform if cache is enabled";
    }

    public final void legacyTransactionManagerStrategy(String arg0, String arg1) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.legacyTransactionManagerStrategy$str(), arg0, arg1);
    }

    protected String legacyTransactionManagerStrategy$str() {
        return "HHH000428: Encountered legacy TransactionManagerLookup specified; convert to newer %s contract specified via %s setting";
    }

    public final void entityIdentifierValueBindingExists(String arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.entityIdentifierValueBindingExists$str(), arg0);
    }

    protected String entityIdentifierValueBindingExists$str() {
        return "HHH000429: Setting entity-identifier value binding where one already existed : %s.";
    }

    public final void deprecatedDerbyDialect() {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.deprecatedDerbyDialect$str(), new Object[0]);
    }

    protected String deprecatedDerbyDialect$str() {
        return "HHH000430: The DerbyDialect dialect has been deprecated; use one of the version-specific dialects instead";
    }

    public final void undeterminedH2Version() {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.undeterminedH2Version$str(), new Object[0]);
    }

    protected String undeterminedH2Version$str() {
        return "HHH000431: Unable to determine H2 database version, certain features may not work";
    }

    public final void noColumnsSpecifiedForIndex(String arg0, String arg1) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.noColumnsSpecifiedForIndex$str(), arg0, arg1);
    }

    protected String noColumnsSpecifiedForIndex$str() {
        return "HHH000432: There were not column names specified for index %s on table %s";
    }

    public final void timestampCachePuts(long arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.timestampCachePuts$str(), arg0);
    }

    protected String timestampCachePuts$str() {
        return "HHH000433: update timestamps cache puts: %s";
    }

    public final void timestampCacheHits(long arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.timestampCacheHits$str(), arg0);
    }

    protected String timestampCacheHits$str() {
        return "HHH000434: update timestamps cache hits: %s";
    }

    public final void timestampCacheMisses(long arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.timestampCacheMisses$str(), arg0);
    }

    protected String timestampCacheMisses$str() {
        return "HHH000435: update timestamps cache misses: %s";
    }

    public final void entityManagerFactoryAlreadyRegistered(String arg0, String arg1) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.entityManagerFactoryAlreadyRegistered$str(), arg0, arg1);
    }

    protected String entityManagerFactoryAlreadyRegistered$str() {
        return "HHH000436: Entity manager factory name (%s) is already registered.  If entity manager will be clustered or passivated, specify a unique value for property '%s'";
    }

    public final void cannotResolveNonNullableTransientDependencies(String arg0, Set<String> arg1, Set<String> arg2) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.cannotResolveNonNullableTransientDependencies$str(), arg0, arg1, arg2);
    }

    protected String cannotResolveNonNullableTransientDependencies$str() {
        return "HHH000437: Attempting to save one or more entities that have a non-nullable association with an unsaved transient entity. The unsaved transient entity must be saved in an operation prior to saving these dependent entities.\n\tUnsaved transient entity: (%s)\n\tDependent entities: (%s)\n\tNon-nullable association(s): (%s)";
    }

    public final void naturalIdCachePuts(long arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.naturalIdCachePuts$str(), arg0);
    }

    protected String naturalIdCachePuts$str() {
        return "HHH000438: NaturalId cache puts: %s";
    }

    public final void naturalIdCacheHits(long arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.naturalIdCacheHits$str(), arg0);
    }

    protected String naturalIdCacheHits$str() {
        return "HHH000439: NaturalId cache hits: %s";
    }

    public final void naturalIdCacheMisses(long arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.naturalIdCacheMisses$str(), arg0);
    }

    protected String naturalIdCacheMisses$str() {
        return "HHH000440: NaturalId cache misses: %s";
    }

    public final void naturalIdMaxQueryTime(long arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.naturalIdMaxQueryTime$str(), arg0);
    }

    protected String naturalIdMaxQueryTime$str() {
        return "HHH000441: Max NaturalId query time: %sms";
    }

    public final void naturalIdQueriesExecuted(long arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.naturalIdQueriesExecuted$str(), arg0);
    }

    protected String naturalIdQueriesExecuted$str() {
        return "HHH000442: NaturalId queries executed to database: %s";
    }

    public final void tooManyInExpressions(String arg0, int arg1, String arg2, int arg3) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.tooManyInExpressions$str(), new Object[]{arg0, arg1, arg2, arg3});
    }

    protected String tooManyInExpressions$str() {
        return "HHH000443: Dialect [%s] limits the number of elements in an IN predicate to %s entries.  However, the given parameter list [%s] contained %s entries, which will likely cause failures to execute the query in the database";
    }

    public final void usingFollowOnLocking() {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.usingFollowOnLocking$str(), new Object[0]);
    }

    protected String usingFollowOnLocking$str() {
        return "HHH000444: Encountered request for locking however dialect reports that database prefers locking be done in a separate select (follow-on locking); results will be locked after initial query executes";
    }

    public final void aliasSpecificLockingWithFollowOnLocking(LockMode arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.aliasSpecificLockingWithFollowOnLocking$str(), arg0);
    }

    protected String aliasSpecificLockingWithFollowOnLocking$str() {
        return "HHH000445: Alias-specific lock modes requested, which is not currently supported with follow-on locking; all acquired locks will be [%s]";
    }

    public final void embedXmlAttributesNoLongerSupported() {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.embedXmlAttributesNoLongerSupported$str(), new Object[0]);
    }

    protected String embedXmlAttributesNoLongerSupported$str() {
        return "HHH000446: embed-xml attributes were intended to be used for DOM4J entity mode. Since that entity mode has been removed, embed-xml attributes are no longer supported and should be removed from mappings.";
    }

    public final void explicitSkipLockedLockCombo() {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.explicitSkipLockedLockCombo$str(), new Object[0]);
    }

    protected String explicitSkipLockedLockCombo$str() {
        return "HHH000447: Explicit use of UPGRADE_SKIPLOCKED in lock() calls is not recommended; use normal UPGRADE locking instead";
    }

    public final void multipleValidationModes(String arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.multipleValidationModes$str(), arg0);
    }

    protected String multipleValidationModes$str() {
        return "HHH000448: 'javax.persistence.validation.mode' named multiple values : %s";
    }

    public final void nonCompliantMapConversion(String arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.nonCompliantMapConversion$str(), arg0);
    }

    protected String nonCompliantMapConversion$str() {
        return "HHH000449: @Convert annotation applied to Map attribute [%s] did not explicitly specify attributeName using 'key'/'value' as required by spec; attempting to DoTheRightThing";
    }

    public final void alternateServiceRole(String arg0, String arg1) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.alternateServiceRole$str(), arg0, arg1);
    }

    protected String alternateServiceRole$str() {
        return "HHH000450: Encountered request for Service by non-primary service role [%s -> %s]; please update usage";
    }

    public final void rollbackFromBackgroundThread(int arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.rollbackFromBackgroundThread$str(), arg0);
    }

    protected String rollbackFromBackgroundThread$str() {
        return "HHH000451: Transaction afterCompletion called by a background thread; delaying afterCompletion processing until the original thread can handle it. [status=%s]";
    }

    public final void unableToLoadScannedClassOrResource(Exception arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, arg0, this.unableToLoadScannedClassOrResource$str(), new Object[0]);
    }

    protected String unableToLoadScannedClassOrResource$str() {
        return "HHH000452: Exception while loading a class or resource found during scanning";
    }

    public final void unableToDiscoverOsgiService(String arg0, Exception arg1) {
        super.log.logf(FQCN, Logger.Level.WARN, arg1, this.unableToDiscoverOsgiService$str(), arg0);
    }

    protected String unableToDiscoverOsgiService$str() {
        return "HHH000453: Exception while discovering OSGi service implementations : %s";
    }

    public final void deprecatedManyToManyOuterJoin() {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.deprecatedManyToManyOuterJoin$str(), new Object[0]);
    }

    protected String deprecatedManyToManyOuterJoin$str() {
        return "HHH000454: The outer-join attribute on <many-to-many> has been deprecated. Instead of outer-join=\"false\", use lazy=\"extra\" with <map>, <set>, <bag>, <idbag>, or <list>, which will only initialize entities (not as a proxy) as needed.";
    }

    public final void deprecatedManyToManyFetch() {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.deprecatedManyToManyFetch$str(), new Object[0]);
    }

    protected String deprecatedManyToManyFetch$str() {
        return "HHH000455: The fetch attribute on <many-to-many> has been deprecated. Instead of fetch=\"select\", use lazy=\"extra\" with <map>, <set>, <bag>, <idbag>, or <list>, which will only initialize entities (not as a proxy) as needed.";
    }

    public final void unsupportedNamedParameters() {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.unsupportedNamedParameters$str(), new Object[0]);
    }

    protected String unsupportedNamedParameters$str() {
        return "HHH000456: Named parameters are used for a callable statement, but database metadata indicates named parameters are not supported.";
    }

    public final void applyingExplicitDiscriminatorColumnForJoined(String arg0, String arg1) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.applyingExplicitDiscriminatorColumnForJoined$str(), arg0, arg1);
    }

    protected String applyingExplicitDiscriminatorColumnForJoined$str() {
        return "HHH000457: Joined inheritance hierarchy [%1$s] defined explicit @DiscriminatorColumn.  Legacy Hibernate behavior was to ignore the @DiscriminatorColumn.  However, as part of issue HHH-6911 we now apply the explicit @DiscriminatorColumn.  If you would prefer the legacy behavior, enable the `%2$s` setting (%2$s=true)";
    }

    public final void creatingPooledLoOptimizer(int arg0, String arg1) {
        super.log.logf(FQCN, Logger.Level.DEBUG, (Throwable)null, this.creatingPooledLoOptimizer$str(), arg0, arg1);
    }

    protected String creatingPooledLoOptimizer$str() {
        return "HHH000467: Creating pooled optimizer (lo) with [incrementSize=%s; returnClass=%s]";
    }

    public final void logBadHbmAttributeConverterType(String arg0, String arg1) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.logBadHbmAttributeConverterType$str(), arg0, arg1);
    }

    protected String logBadHbmAttributeConverterType$str() {
        return "HHH000468: Unable to interpret type [%s] as an AttributeConverter due to an exception : %s";
    }

    protected String usingStoppedClassLoaderService$str() {
        return "HHH000469: The ClassLoaderService can not be reused. This instance was stopped already.";
    }

    public final HibernateException usingStoppedClassLoaderService() {
        HibernateException result = new HibernateException(String.format(this.getLoggingLocale(), this.usingStoppedClassLoaderService$str()));
        StackTraceElement[] st = result.getStackTrace();
        result.setStackTrace((StackTraceElement[])Arrays.copyOfRange(st, 1, st.length));
        return result;
    }

    public final void logUnexpectedSessionInCollectionNotConnected(String arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.logUnexpectedSessionInCollectionNotConnected$str(), arg0);
    }

    protected String logUnexpectedSessionInCollectionNotConnected$str() {
        return "HHH000470: An unexpected session is defined for a collection, but the collection is not connected to that session. A persistent collection may only be associated with one session at a time. Overwriting session. %s";
    }

    public final void logCannotUnsetUnexpectedSessionInCollection(String arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.logCannotUnsetUnexpectedSessionInCollection$str(), arg0);
    }

    protected String logCannotUnsetUnexpectedSessionInCollection$str() {
        return "HHH000471: Cannot unset session in a collection because an unexpected session is defined. A persistent collection may only be associated with one session at a time. %s";
    }

    public final void hikariProviderClassNotFound() {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.hikariProviderClassNotFound$str(), new Object[0]);
    }

    protected String hikariProviderClassNotFound$str() {
        return "HHH000472: Hikari properties were encountered, but the Hikari ConnectionProvider was not found on the classpath; these properties are going to be ignored.";
    }

    public final void cachedFileObsolete(File arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.cachedFileObsolete$str(), arg0);
    }

    protected String cachedFileObsolete$str() {
        return "HHH000473: Omitting cached file [%s] as the mapping file is newer";
    }

    protected String ambiguousPropertyMethods$str() {
        return "HHH000474: Ambiguous persistent property methods detected on %s; mark one as @Transient : [%s] and [%s]";
    }

    public final String ambiguousPropertyMethods(String arg0, String arg1, String arg2) {
        return String.format(this.getLoggingLocale(), this.ambiguousPropertyMethods$str(), arg0, arg1, arg2);
    }

    public final void logCannotLocateIndexColumnInformation(String arg0, String arg1) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.logCannotLocateIndexColumnInformation$str(), arg0, arg1);
    }

    protected String logCannotLocateIndexColumnInformation$str() {
        return "HHH000475: Cannot locate column information using identifier [%s]; ignoring index [%s]";
    }

    public final void executingImportScript(String arg0) {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.executingImportScript$str(), arg0);
    }

    protected String executingImportScript$str() {
        return "HHH000476: Executing import script '%s'";
    }

    public final void startingDelayedSchemaDrop() {
        super.log.logf(FQCN, Logger.Level.INFO, (Throwable)null, this.startingDelayedSchemaDrop$str(), new Object[0]);
    }

    protected String startingDelayedSchemaDrop$str() {
        return "HHH000477: Starting delayed drop of schema as part of SessionFactory shut-down'";
    }

    public final void unsuccessfulSchemaManagementCommand(String arg0) {
        super.log.logf(FQCN, Logger.Level.ERROR, (Throwable)null, this.unsuccessfulSchemaManagementCommand$str(), arg0);
    }

    protected String unsuccessfulSchemaManagementCommand$str() {
        return "HHH000478: Unsuccessful: %s";
    }

    protected String collectionNotProcessedByFlush$str() {
        return "HHH000479: Collection [%s] was not processed by flush(). This is likely due to unsafe use of the session (e.g. used in multiple threads concurrently, updates during entity lifecycle hooks).";
    }

    public final String collectionNotProcessedByFlush(String arg0) {
        return String.format(this.getLoggingLocale(), this.collectionNotProcessedByFlush$str(), arg0);
    }

    public final void stalePersistenceContextInEntityEntry(String arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.stalePersistenceContextInEntityEntry$str(), arg0);
    }

    protected String stalePersistenceContextInEntityEntry$str() {
        return "HHH000480: A ManagedEntity was associated with a stale PersistenceContext. A ManagedEntity may only be associated with one PersistenceContext at a time; %s";
    }

    public final void unknownJavaTypeNoEqualsHashCode(Class arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.unknownJavaTypeNoEqualsHashCode$str(), arg0);
    }

    protected String unknownJavaTypeNoEqualsHashCode$str() {
        return "HHH000481: Encountered Java type [%s] for which we could not locate a JavaTypeDescriptor and which does not appear to implement equals and/or hashCode.  This can lead to significant performance problems when performing equality/dirty checking involving this Java type.  Consider registering a custom JavaTypeDescriptor or at least implementing equals/hashCode.";
    }

    public final void cacheOrCacheableAnnotationOnNonRoot(String arg0) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.cacheOrCacheableAnnotationOnNonRoot$str(), arg0);
    }

    protected String cacheOrCacheableAnnotationOnNonRoot$str() {
        return "HHH000482: @javax.persistence.Cacheable or @org.hibernate.annotations.Cache used on a non-root entity: ignored for %s";
    }

    public final void emptyCompositesEnabled() {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.emptyCompositesEnabled$str(), new Object[0]);
    }

    protected String emptyCompositesEnabled$str() {
        return "HHH000483: An experimental feature has been enabled (hibernate.create_empty_composites.enabled=true) that instantiates empty composite/embedded objects when all of its attribute values are null. This feature has known issues and should not be used in production until it is stabilized. See Hibernate Jira issue HHH-11936 for details.";
    }

    public final void immutableEntityUpdateQuery(String arg0, String arg1) {
        super.log.logf(FQCN, Logger.Level.WARN, (Throwable)null, this.immutableEntityUpdateQuery$str(), arg0, arg1);
    }

    protected String immutableEntityUpdateQuery$str() {
        return "HHH000487: The query: [%s] attempts to update an immutable entity: %s";
    }

    static {
        LOCALE = Locale.ROOT;
    }

}
