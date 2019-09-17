package config.i18n.client;

import com.google.gwt.i18n.client.Messages;

/**
 * @author Bruno Ferreira <bferreira@keep.pt>
 */
public interface ClientMessages extends Messages {
  String browserOfflineError();

  String cannotReachServerError();

  String alertErrorTitle();

  String databaseDoesNotContainUsers();

  String databaseDoesNotContainRoles();

  String databaseDoesNotContainPrivileges();

  String titleUsers();

  String titleReport();

  String titleDescription();

  String titleAdmin();

  String titleRoles();

  String titleType();

  String titlePrivileges();

  String titleGrantor();

  String titleGrantee();

  String titleObject();

  String titleOption();

  String searchFieldDatePlaceHolder();

  String searchFieldDateFromPlaceHolder();

  String searchFieldDateToPlaceHolder();

  String searchFieldNumericPlaceHolder();

  String searchFieldNumericFromPlaceHolder();

  String searchFieldNumericToPlaceHolder();

  String searchFieldTimeFromPlaceHolder();

  String searchFieldTimeToPlaceHolder();

  String fillUsernameAndPasswordMessage();

  String couldNotLoginWithTheProvidedCredentials();

  String loginProfile();

  String loginLogout();

  String loginLogin();

  String dialogNotFoundGoToHome();

  String dialogResourceNotFound();

  String dialogPermissionDenied();

  String dialogMustLogin();

  String dialogLogin();

  String menusidebar_database();

  String menusidebar_information();

  String menusidebar_usersRoles();

  String menusidebar_savedSearches();

  String menusidebar_searchAllRecords();

  String menusidebar_structure();

  String menusidebar_routines();

  String menusidebar_triggers();

  String menusidebar_checkConstraints();

  String menusidebar_views();

  String menusidebar_data();

  String menusidebar_filterSidebar();

  String siardMetadata_databaseName();

  String siardMetadata_archivalDate();

  String siardMetadata_archivist();

  String siardMetadata_archivistContact();

  String siardMetadata_clientMachine();

  String siardMetadata_databaseProduct();

  String siardMetadata_databaseUser();

  String siardMetadata_dataOriginTimeSpan();

  String siardMetadata_dataOwner();

  String description();

  String siardMetadata_producerApplication();

  String siardMetadata_DescriptionUnavailable();

  String menusidebar_manageDatabases();

  String searchPlaceholder();

  String diagram();

  String diagram_usingTheDiagram();

  String diagram_Explanation();

  String diagram_rows(@PluralCount int itemCount);

  String diagram_columns(@PluralCount int itemCount);

  String diagram_relations(@PluralCount int itemCount);

  String references_relation();

  String references_relatedTable();

  String references_foreignKeyName();

  String references_foreignKeyDescription();

  String references_thisRecordIsRelatedTo();

  String references_thisRecordIsReferencedBy();

  String row_downloadLOB();

  String references_isRelatedTo();

  String references_isReferencedBy();

  String constraints_thisSchemaDoesNotHaveAnyCheckConstraints();

  String constraints_constraintsInTable(String tableName);

  String references_referencesForValue(String value);

  String name();

  String constraints_condition();

  String schema_tableName();

  String schema_numberOfRows();

  String schema_numberOfColumns();

  String schema_relationsOut();

  String schema_relationsIn();

  String routines_thisSchemaDoesNotHaveAnyRoutines();

  String routine_sourceCode();

  String routine_body();

  String routine_characteristic();

  String routine_returnType();

  String routineParameter_mode();

  String foreignKeys();

  String foreignKeys_referencedSchema();

  String foreignKeys_referencedTable();

  String mappingSourceToReferenced(String arrowHTML);

  String foreignKeys_matchType();

  String foreignKeys_updateAction();

  String foreignKeys_deleteAction();

  String primaryKey();

  String foreignKeys_usedByAForeignKeyRelation();

  String columnName();

  String typeName();

  String originalTypeName();

  String nullable();

  String thisSchemaDoesNotHaveAnyTriggers();

  String triggersInTable(String tableName);

  String triggeredAction();

  String actionTime();

  String triggerEvent();

  String aliasList();

  String originalQuery();

  String query();

  String loginVisitorMessage();

  String loginDialogTitle();

  String loginDialogUsername();

  String loginDialogPassword();

  String loginDialogLogin();

  String loginDialogCancel();

  String editingSavedSearch();

  String noRecordsMatchTheSearchTerms();

  String homePageDescriptionTop();

  String homePageDescriptionBottom();

  String homePageDescriptionHere();

  String loading();

  String inSchema();

  String inTable();

  String inColumn();

  String loadingTableInfo();

  String searchAllRecords();

  String references();

  String savedSearch_Save();

  String savedSearch_Cancel();

  String exportVisible();

  String exportAll();

  String goHome();

  String goToDatabaseInformation();

  String aboutDBVTK();

  String whatIsDBVTK();

  String license();

  String download();

  String binary();

  String source();

  String development();

  String bugReporting();

  String contactUs();

  String infoAndSupport();

  String uniqueID();

  String edit();

  String delete();

  String table();

  String created();

  String thereAreNoSavedSearches();

  String largeBinaryObject();

  String searchingRange_to();

  String searchingTime_at();

  String saveSearch();

  String saving();

  String searchOnTable(String tableName);

  String addSearchField();

  String search();

  String menusidebar_home();

  String menusidebar_databases();

  String menusidebar_savedSearch();

  String menusidebar_record();

  String menusidebar_referencesForColumn(String columnNameInTable);

  String databaseListing();

  String of();

  String ofOver();

  String showMore();

  String showLess();

  String includingStoredProceduresAndFunctions();

  String schemaName();

  String schemaDescription();

  String newUpload();

  String uploadedSIARD();

  String uploads();

  String newUploadLabel();

  String usersAndPermissions();

  String SIARD();

  String dialogReimportSIARDTitle();

  String dialogReimportSIARD();

  String update();

  String databaseInformation();

  String databaseInformationDescription();

  String metadataButtonSave();

  String metadataHasUpdates();

  String metadataMissingFields(String fieldList);

  String metadataSuccessUpdated();

  String metadataFailureUpdated();

  String metadataButtonRevert();

  String metadataDoesNotContainDescription();

  String tableDoesNotContainColumns();

  String tableDoesNotContainPrimaryKey();

  String tableDoesNotContainForeignKeys();

  String tableDoesNotContainCandidateKeys();

  String tableDoesNotContainConstraints();

  String tableDoesNotContainTriggers();

  String candidateKeys();

  String columns();

  String viewDoesNotContainDescription();

  String viewDoesNotContainQuery();

  String viewDoesNotContainQueryOriginal();

  String viewDoesNotContainColumns();

  String routineDoesNotContainDescription();

  String routines_thisRoutineFieldDoesNotHaveContent();

  String routines_parametersList();

  String dialogUpdateMetadata();

  String dialogConfirmUpdateMetadata();

  String dialogLargeFileConfirmUpdateMetadata();

  String newText();

  String SIARDError();

  /********************************************
   * Information Strings
   ********************************************/
  String noItemsToDisplay();

  /********************************************
   * Humanized Strings
   ********************************************/
  String humanizedTextForSIARDValidationSuccess();

  String humanizedTextForSIARDValidationFailed();

  String humanizedTextForSIARDNotValidated();

  String humanizedTextForSolrIngesting();

  String humanizedTextForSolrAvailable();

  String humanizedTextForSolrMetadataOnly();

  String humanizedTextForSolrRemoving();

  String humanizedTextForSolrError();

  /********************************************
   * Basic Actions
   ********************************************/
  String basicActionClose();

  String basicActionCancel();

  String basicActionDiscard();

  String basicActionConfirm();

  String basicActionClear();

  String basicActionNext();

  String basicActionBack();

  String basicActionSkip();

  String basicActionSelectAll();

  String basicActionSelectNone();

  String basicActionOpen();

  String basicActionAdd();

  String basicActionMigrate();

  String basicActionBrowse();

  String basicActionUpdate();

  String basicActionNew();

  String basicActionSave();

  String basicActionTest();

  /*********************************************
   * Sidebar Menus
   ********************************************/
  String sidebarMenuTextForDatabases();

  String sidebarMenuTextForTables();

  String sidebarMenuTextForViews();

  /*********************************************
   * Breadcrumbs Text
   ********************************************/
  String breadcrumbTextForManageSIARD();

  String breadcrumbTextForWizardCreateSIARDConnection();

  String breadcrumbTextForWizardCreateSIARDTableAndColumns();

  String breadcrumbTextForWizardCreateSIARDExportOptions();

  String breadcrumbTextForWizardCreateSIARDMetadataOptions();

  String breadcrumbTextForWizardCreateSIARDCustomViews();

  String breadcrumbTextForSIARDEditMetadata();

  String breadcrumbTextForCreateSIARD();

  String breadcrumbTextForWizardSendToTableAndColumns();

  String breadcrumbTextForWizardSendToDBMSConnection();

  String breadcrumbTextForWizardSendToSIARDExportOptions();

  String breadcrumbTextForWizardSendToMetadataExportOptions();

  String breadcrumbTextForWizardSendToProgressPanel();

  String breadcrumbTextForSIARDValidator();

  /*********************************************
   * Home Page
   ********************************************/
  String homePageButtonTextForCreateSIARD();

  String homePageButtonTextForOpenSIARD();

  String homePageButtonTextForManageSIARD();

  String homePageHeaderTextForCreateSIARD();

  String homePageDescriptionTextForCreateSIARD();

  String homePageHeaderTextForOpenSIARD();

  String homePageDescriptionTextForOpenSIARD();

  String homePageHeaderTextForManageSIARD();

  String homePageDescriptionTextForManageSIARD();

  String homePageTextForFinancedBy();

  String homePageTextForDevelopedBy();

  /********************************************
   * Manage SIARD
   ********************************************/
  String managePageButtonTextForCreateSIARD();

  String managePageButtonTextForOpenSIARD();

  String managePageTableHeaderTextForDatabaseName();

  String managePageTableHeaderTextForProductName();

  String managePageTableHeaderTextForArchivalDate();

  String managePageTableHeaderTextForSIARDLocation();

  String managePageTableHeaderTextForSIARDValidationStatus();

  String managePageTableHeaderTextForDatabaseStatus();

  String managePageTableHeaderTextForActions();

  /********************************************
   * SIARD Home Page
   ********************************************/
  String SIARDHomePageToastTitle(String method);

  String SIARDHomePageButtonTextEditMetadata();

  String SIARDHomePageButtonTextMigrateToSIARD();

  String SIARDHomePageButtonTextSendToLiveDBMS();

  String SIARDHomePageButtonTextShowFile();

  String SIARDHomePageButtonTextValidateNow();

  String SIARDHomePageButtonTextSeeReport();

  String SIARDHomePageButtonTextForBrowseNow();

  String SIARDHomePageButtonTextForDeleteIngested();

  String SIARDHomePageOptionsHeaderForSIARD();

  String SIARDHomePageOptionsHeaderForValidation();

  String SIARDHomePageOptionsHeaderForBrowsing();

  String SIARDHomePageLabelForViewerMetadataName();

  String SIARDHomePageLabelForViewerMetadataArchivalDate();

  String SIARDHomePageLabelForViewerMetadataArchiver();

  String SIARDHomePageLabelForViewerMetadataArchiverContact();

  String SIARDHomePageLabelForViewerMetadataClientMachine();

  String SIARDHomePageLabelForViewerMetadataDatabaseProduct();

  String SIARDHomePageLabelForViewerMetadataDataOriginTimespan();

  String SIARDHomePageLabelForViewerMetadataDataOwner();

  String SIARDHomePageLabelForViewerMetadataProducerApplication();

  String SIARDHomePageLabelForValidatedAt();

  String SIARDHomePageLabelForValidationVersion();

  String SIARDHomePageTextForMissingDescription();

  String SIARDHomePageLabelForValidationStatus();

  /********************************************
   * Edit Metadata
   ********************************************/
  String editMetadataNotificationTitle();

  String editMetadataInformationMessage();

  /********************************************
   * Create Wizard: Home Page
   ********************************************/
  String createSIARDWizardManagerErrorTitle();

  String createSIARDWizardManagerSelectDataSourceError();

  /********************************************
   * Create Wizard: Information Messages
   ********************************************/
  String createSIARDWizardManagerInformationMessagesTitle();

  String createSIARDWizardManagerInformationMessage();

  /********************************************
   * Connection Page
   ********************************************/
  String connectionPageTextForTabGeneral();

  String connectionPageTextForTabSSHTunnel();

  String connectionPageLabelForUseSSHTunnel();

  String connectionPageLabelForProxyHost();

  String connectionPageLabelForProxyPort();

  String connectionPageLabelForProxyUserLabel();

  String connectionPageLabelForProxyPasswordLabel();

  String connectionPageLabelsFor(@Select String fieldName);

  String connectionPageLabelForChooseDriverLocation();

  String errorMessagesConnectionTitle();

  String connectionPageErrorMessageFor(@Select int error);

  String connectionPageTitle();

  String connectionPageTextForWelcome();

  String connectionPageTextForConnectionHelper();

  String connectionPageTextForTableAndColumnsHelper();

  String connectionPageTextForCustomViewsHelper();

  String connectionPageTextForExportOptionsHelper();

  String connectionPageTextForMetadataExportOptionsHelper();

  String connectionPageTextForWelcomeDBMSHelper();

  String connectionPageTextForDBMSHelper();

  String connectionPageTextForSSHelper();

  /********************************************
   * Create Wizard: Table & Columns
   ********************************************/
  String tableAndColumnsPageTitle();

  String tableAndColumnsPageTextForExternalLOBConfigure();

  String tableAndColumnsPageLabelForReferenceType();

  String tableAndColumnsPageLabelForBasePath();

  String tableAndColumnsPageTableHeaderTextForColumnFilters();

  String tableAndColumnsPageTableHeaderTextForColumnName();

  String tableAndColumnsPageTableHeaderTextForOriginalTypeName();

  String tableAndColumnsPageTableHeaderTextForDescription();

  String tableAndColumnsPageTableHeaderTextForOptions();

  String tableAndColumnsPageTableHeaderTextForSelect();

  String tableAndColumnsPageTableHeaderTextForViewName();

  String tableAndColumnsPageTableHeaderTextForTableName();

  String tableAndColumnsPageTableHeaderTextForNumberOfRows();

  String tableAndColumnsPageDialogTitleForExternalLOBDialog();

  String tableAndColumnsPageErrorMessageFor(@Select int error);

  /********************************************
   * Create Wizard: Custom Views
   ********************************************/
  String customViewsPageTitle();

  String customViewsUpdateMessage();

  String customViewsPageLabelForSchemaName();

  String customViewsPageLabelForName();

  String customViewsPageLabelForDescription();

  String customViewsPageLabelForQuery();

  String customViewsPageTextForDescription();

  String customViewsPageTextForDialogTitle();

  String customViewsPageTextForDialogMessage();

  String customViewsPageTextForQueryResultsDialogTitle();

  String customViewsPageTextForDialogConfirmDelete();

  String customViewsPageErrorMessageForQueryError();

  String customViewsPageErrorMessagesFor(@Select int error);

  /********************************************
   * Wizard Export Options
   ********************************************/
  String wizardExportOptionsTitle();

  String wizardExportOptionsDescription();

  String wizardMetadataExportOptionsTitle();

  String wizardMetadataExportOptionsDescription();

  String wizardExportOptionsLabels(@Select String fieldName);

  String wizardExportOptionsHelperText(@Select String fieldName);

  String errorMessagesExportOptionsTitle();

  String errorMessagesExportOptions(@Select int error);

  /*********************************************
   * Send to: Table & Columns
   ********************************************/
  String wizardSendToDBMSExportButton();

  /*********************************************
   * Send to: Export Format
   ********************************************/
  String wizardSendToExportFormatTitle();

  String wizardSendToExportFormatSubTitle();

  /*********************************************
   * Send to: Information Messages
   ********************************************/
  String sendToWizardManagerInformationTitle();

  String sendToWizardManagerInformationMessageSIARD();

  String sendToWizardManagerInformationMessageDBMS(String name);


  /*********************************************
   * Open SIARD
   ********************************************/
  String dialogOpenSIARDMessage();

  String errorMessagesOpenFile();

  /*********************************************
   * SIARD Validator
   ********************************************/
  String SIARDValidatorSettings();

  String allowedTypes();

  String reporterDestinationFolder();

  String reporterTip();

  String allowedTypesTip();

  String clear();

  String reporterFile();

  String numberOfValidationError();

  String numberOfValidationsPassed();

  String numberOfValidationsSkipped();

  String runAgain();

  String validationRequirements(@Select String codeID);

  /*********************************************
   * Progress Bar
   ********************************************/
  String progressBarPanelTextForTables();

  String progressBarPanelTextForRows();

  String progressBarPanelTextForCurrentTable();

  String progressBarPanelTextForCurrentRows();

  String progressBarPanelTextForRetrievingTableStructure();

  String progressBarPanelTextForDBMSWizardTitle(String dbms);

  String progressBarPanelTextForDBMSWizardSubTitle();

  String progressBarPanelTextForCreateWizardProgressTitle();

  String progressBarPanelTextForCreateWizardProgressSubTitle();
}
