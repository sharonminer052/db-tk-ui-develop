package com.databasepreservation.common.client.tools;

import java.util.ArrayList;
import java.util.List;

import com.databasepreservation.common.client.common.breadcrumb.BreadcrumbItem;
import com.databasepreservation.common.client.common.breadcrumb.BreadcrumbPanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

import config.i18n.client.ClientMessages;

/**
 * @author Bruno Ferreira <bferreira@keep.pt>
 */
public class BreadcrumbManager {
  private static final ClientMessages messages = GWT.create(ClientMessages.class);

  public static void updateBreadcrumb(BreadcrumbPanel breadcrumb, List<BreadcrumbItem> items) {
    breadcrumb.updatePath(items);
    breadcrumb.setVisible(true);
  }

  public static List<BreadcrumbItem> empty() {
    return new ArrayList<>();
  }

  public static List<BreadcrumbItem> forHome() {
    List<BreadcrumbItem> items = new ArrayList<>();
    items
      .add(new BreadcrumbItem(SafeHtmlUtils.fromSafeConstant(FontAwesomeIconManager.getTag(FontAwesomeIconManager.HOME)
        + SafeHtmlUtils.htmlEscape(messages.menusidebar_home())), HistoryManager::gotoHome));
    return items;
  }

  public static List<BreadcrumbItem> forLogin() {
    List<BreadcrumbItem> items = new ArrayList<>();
    items.add(new BreadcrumbItem(
      SafeHtmlUtils.fromSafeConstant(
        FontAwesomeIconManager.getTag(FontAwesomeIconManager.LOGIN) + SafeHtmlUtils.htmlEscape(messages.loginLogin())),
      HistoryManager::gotoLogin));
    return items;
  }

  public static List<BreadcrumbItem> forNewUpload() {
    List<BreadcrumbItem> items = forHome();
    items.add(
      new BreadcrumbItem(SafeHtmlUtils.fromSafeConstant(FontAwesomeIconManager.getTag(FontAwesomeIconManager.NEW_UPLOAD)
        + SafeHtmlUtils.htmlEscape(messages.newUpload())), HistoryManager::gotoNewUpload));
    return items;
  }

  public static List<BreadcrumbItem> forDatabases() {
    List<BreadcrumbItem> items = new ArrayList<>();
    items.add(
      new BreadcrumbItem(SafeHtmlUtils.fromSafeConstant(FontAwesomeIconManager.getTag(FontAwesomeIconManager.DATABASES)
        + SafeHtmlUtils.htmlEscape(messages.menusidebar_databases())), HistoryManager::gotoDatabaseList));
    return items;
  }

  public static List<BreadcrumbItem> forDatabaseInformation(final String databaseUUID, final String databaseName) {
    List<BreadcrumbItem> items;
    items = forSIARDMainPage(databaseUUID, databaseName);

    items.add(new BreadcrumbItem(
      SafeHtmlUtils.fromSafeConstant(FontAwesomeIconManager.getTag(FontAwesomeIconManager.DATABASE_INFORMATION)
        + SafeHtmlUtils.htmlEscape(messages.databaseInformationTextForTitle())),
      () -> HistoryManager.gotoDatabase(databaseUUID)));

    return items;
  }

  public static List<BreadcrumbItem> forDatabaseSearchPanel(final String databaseUUID, final String databaseName) {
    List<BreadcrumbItem> items;
    items = forSIARDMainPage(databaseUUID, databaseName);

    items.add(new BreadcrumbItem(
      SafeHtmlUtils.fromSafeConstant(FontAwesomeIconManager.getTag(FontAwesomeIconManager.DATABASE_SEARCH)
        + SafeHtmlUtils.htmlEscape(messages.search())),
      () -> HistoryManager.gotoDatabaseSearch(databaseUUID)));

    return items;
  }

  public static List<BreadcrumbItem> forDatabaseReport(final String databaseUUID, final String databaseName) {
    List<BreadcrumbItem> items;
    items = forSIARDMainPage(databaseUUID, databaseName);

    items.add(new BreadcrumbItem(
      SafeHtmlUtils.fromSafeConstant(FontAwesomeIconManager.getTag(FontAwesomeIconManager.DATABASE_REPORT)
        + SafeHtmlUtils.htmlEscape(messages.titleReport())),
      () -> HistoryManager.gotoDatabaseReport(databaseUUID)));
    return items;
  }

  public static List<BreadcrumbItem> forDatabaseUsers(final String databaseName, final String databaseUUID) {
    List<BreadcrumbItem> items;
    items = forSIARDMainPage(databaseUUID, databaseName);

    items.add(new BreadcrumbItem(
      SafeHtmlUtils.fromSafeConstant(FontAwesomeIconManager.getTag(FontAwesomeIconManager.DATABASE_USERS)
        + SafeHtmlUtils.htmlEscape(messages.titleUsers())),
      () -> HistoryManager.gotoDatabaseUsers(databaseUUID)));
    return items;
  }

  public static List<BreadcrumbItem> forDatabaseSavedSearches(final String databaseName, final String databaseUUID) {
    List<BreadcrumbItem> items;
    items = forSIARDMainPage(databaseUUID, databaseName);

    items.add(new BreadcrumbItem(
      SafeHtmlUtils.fromSafeConstant(FontAwesomeIconManager.getTag(FontAwesomeIconManager.SAVED_SEARCH)
        + SafeHtmlUtils.htmlEscape(messages.menusidebar_savedSearches())),
      () -> HistoryManager.gotoSavedSearches(databaseUUID)));
    return items;
  }

  public static List<BreadcrumbItem> forDatabaseSavedSearch(final String databaseName, final String databaseUUID,
    final String savedSearchUUID) {
    List<BreadcrumbItem> items = forDatabaseSavedSearches(databaseName, databaseUUID);
    items.add(new BreadcrumbItem(
      SafeHtmlUtils.fromSafeConstant(FontAwesomeIconManager.getTag(FontAwesomeIconManager.SAVED_SEARCH)
        + SafeHtmlUtils.htmlEscape(messages.menusidebar_savedSearch())),
      () -> HistoryManager.gotoSavedSearch(databaseUUID, savedSearchUUID)));
    return items;
  }

  public static List<BreadcrumbItem> forSchemaRoutines(final String databaseUUID, final String databaseName) {
    List<BreadcrumbItem> items;
    items = forSIARDMainPage(databaseUUID, databaseName);

    items.add(new BreadcrumbItem(
      SafeHtmlUtils.fromSafeConstant(FontAwesomeIconManager.getTag(FontAwesomeIconManager.SCHEMA_ROUTINES)
        + SafeHtmlUtils.htmlEscape(messages.menusidebar_routines())),
      () -> HistoryManager.gotoSchemaRoutines(databaseUUID)));
    return items;
  }

  public static List<BreadcrumbItem> forView(final String databaseName, final String databaseUUID,
    final String viewName, final String viewUUID) {
    List<BreadcrumbItem> items;
    items = forSIARDMainPage(databaseUUID, databaseName);

    items.add(new BreadcrumbItem(
      SafeHtmlUtils.fromSafeConstant(
        FontAwesomeIconManager.getTag(FontAwesomeIconManager.SCHEMA_VIEWS) + SafeHtmlUtils.htmlEscape(viewName)),
      () -> HistoryManager.gotoTable(databaseUUID, viewUUID)));
    return items;
  }

  public static List<BreadcrumbItem> forTable(final String databaseName, final String databaseUUID,
    final String tableName, final String tableUUID) {
    List<BreadcrumbItem> items;
    items = forSIARDMainPage(databaseUUID, databaseName);

    items.add(new BreadcrumbItem(
      SafeHtmlUtils.fromSafeConstant(
        FontAwesomeIconManager.getTag(FontAwesomeIconManager.TABLE) + SafeHtmlUtils.htmlEscape(tableName)),
      () -> HistoryManager.gotoTable(databaseUUID, tableUUID)));
    return items;
  }

  public static List<BreadcrumbItem> forRecord(final String databaseName, final String databaseUUID,
    final String tableName, final String tableUUID, final String recordUUID) {
    List<BreadcrumbItem> items = forTable(databaseName, databaseUUID, tableName, tableUUID);
    items.add(new BreadcrumbItem(
      SafeHtmlUtils.fromSafeConstant(FontAwesomeIconManager.getTag(FontAwesomeIconManager.RECORD)
        + SafeHtmlUtils.htmlEscape(messages.menusidebar_record())),
      () -> HistoryManager.gotoRecord(databaseUUID, tableUUID, recordUUID)));
    return items;
  }

  public static List<BreadcrumbItem> forReferences(final String databaseName, final String databaseUUID,
    final String tableName, final String tableUUID, final String recordUUID, final String columnNameInTable,
    final String columnIndexInTable) {
    List<BreadcrumbItem> items = forRecord(databaseName, databaseUUID, tableName, tableUUID, recordUUID);
    items.add(new BreadcrumbItem(
      SafeHtmlUtils.fromSafeConstant(FontAwesomeIconManager.getTag(FontAwesomeIconManager.REFERENCE)
        + SafeHtmlUtils.htmlEscape(messages.menusidebar_referencesForColumn(columnNameInTable))),
      () -> HistoryManager.gotoReferences(databaseUUID, tableUUID, recordUUID, columnIndexInTable)));
    return items;
  }

  public static List<BreadcrumbItem> loadingDatabase(final String databaseUUID) {
    List<BreadcrumbItem> items = forDatabases();
    String loadingDatabase = "Database (loading)";
    items.add(new BreadcrumbItem(
      SafeHtmlUtils.fromSafeConstant(
        FontAwesomeIconManager.getTag(FontAwesomeIconManager.DATABASE) + SafeHtmlUtils.htmlEscape(loadingDatabase)),
      () -> HistoryManager.gotoDatabase(databaseUUID)));
    return items;
  }

  public static List<BreadcrumbItem> forSIARDMainPage(final String databaseUUID, final String databaseName) {
    List<BreadcrumbItem> items = forManageDatabase();
    items.add(new BreadcrumbItem(
      SafeHtmlUtils.fromSafeConstant(
        FontAwesomeIconManager.getTag(FontAwesomeIconManager.DATABASE) + SafeHtmlUtils.htmlEscape(databaseName)),
      () -> HistoryManager.gotoSIARDInfo(databaseUUID)));
    return items;
  }

  public static List<BreadcrumbItem> forSIARDIngesting(final String databaseUUID, final String databaseName) {
    List<BreadcrumbItem> items = forSIARDMainPage(databaseUUID, databaseName);
    items.add(new BreadcrumbItem(
      SafeHtmlUtils.fromSafeConstant(FontAwesomeIconManager.getTag(FontAwesomeIconManager.WRITE)
        + SafeHtmlUtils.htmlEscape(messages.breadcrumbTextForSIARDIngesting())),
      () -> HistoryManager.gotoIngestSIARDData(databaseUUID, databaseName)));
    return items;
  }

  public static List<BreadcrumbItem> forSIARDValidatorPage(final String databaseUUID, final String databaseName) {
    List<BreadcrumbItem> items = forSIARDMainPage(databaseUUID, databaseName);
    items.add(new BreadcrumbItem(
      SafeHtmlUtils.fromSafeConstant(FontAwesomeIconManager.getTag(FontAwesomeIconManager.SIARD_VALIDATIONS)
        + SafeHtmlUtils.htmlEscape(messages.breadcrumbTextForSIARDValidator()))));
    return items;
  }

  public static List<BreadcrumbItem> forManageDatabase() {
    List<BreadcrumbItem> items = new ArrayList<>();
    items.add(
      new BreadcrumbItem(SafeHtmlUtils.fromSafeConstant(FontAwesomeIconManager.getTag(FontAwesomeIconManager.SERVER)
        + SafeHtmlUtils.htmlEscape(messages.breadcrumbTextForManageDatabase())), HistoryManager::gotoDatabase));
    return items;
  }

  public static List<BreadcrumbItem> forCreateConnection() {
    List<BreadcrumbItem> items = forHome();
    items.add(
      new BreadcrumbItem(SafeHtmlUtils.fromSafeConstant(FontAwesomeIconManager.getTag(FontAwesomeIconManager.WIZARD)
        + SafeHtmlUtils.htmlEscape(messages.breadcrumbTextForWizardCreateSIARDConnection()))));
    return items;
  }

  public static List<BreadcrumbItem> forTableAndColumns() {
    List<BreadcrumbItem> items = forHome();
    items.add(
      new BreadcrumbItem(SafeHtmlUtils.fromSafeConstant(FontAwesomeIconManager.getTag(FontAwesomeIconManager.WIZARD)
        + SafeHtmlUtils.htmlEscape(messages.breadcrumbTextForWizardCreateSIARDTableAndColumns()))));
    return items;
  }

  public static List<BreadcrumbItem> forSIARDExportOptions() {
    List<BreadcrumbItem> items = forHome();
    items.add(
      new BreadcrumbItem(SafeHtmlUtils.fromSafeConstant(FontAwesomeIconManager.getTag(FontAwesomeIconManager.WIZARD)
        + SafeHtmlUtils.htmlEscape(messages.breadcrumbTextForWizardCreateSIARDExportOptions()))));
    return items;
  }

  public static List<BreadcrumbItem> forMetadataExportOptions() {
    List<BreadcrumbItem> items = forHome();
    items.add(
      new BreadcrumbItem(SafeHtmlUtils.fromSafeConstant(FontAwesomeIconManager.getTag(FontAwesomeIconManager.WIZARD)
        + SafeHtmlUtils.htmlEscape(messages.breadcrumbTextForWizardCreateSIARDMetadataOptions()))));
    return items;
  }

  public static List<BreadcrumbItem> forCustomViews() {
    List<BreadcrumbItem> items = forHome();
    items.add(
      new BreadcrumbItem(SafeHtmlUtils.fromSafeConstant(FontAwesomeIconManager.getTag(FontAwesomeIconManager.WIZARD)
        + SafeHtmlUtils.htmlEscape(messages.breadcrumbTextForWizardCreateSIARDCustomViews()))));
    return items;
  }

  public static List<BreadcrumbItem> forSIARDEditMetadataPage(final String databaseUUID, final String databaseName) {
    List<BreadcrumbItem> items = forSIARDMainPage(databaseUUID, databaseName);
    items.add(
      new BreadcrumbItem(SafeHtmlUtils.fromSafeConstant(FontAwesomeIconManager.getTag(FontAwesomeIconManager.DATABASE)
        + SafeHtmlUtils.htmlEscape(messages.breadcrumbTextForSIARDEditMetadata()))));
    return items;
  }

  public static List<BreadcrumbItem> forCreateSIARD() {
    List<BreadcrumbItem> items = forHome();
    items.add(new BreadcrumbItem(
      SafeHtmlUtils.fromSafeConstant(SafeHtmlUtils.htmlEscape(messages.breadcrumbTextForCreateSIARD()))));
    return items;
  }

  public static List<BreadcrumbItem> forTableAndColumnsSendToWM(final String databaseUUID, final String databaseName) {
    List<BreadcrumbItem> items = forSIARDMainPage(databaseUUID, databaseName);
    items.add(
      new BreadcrumbItem(SafeHtmlUtils.fromSafeConstant(FontAwesomeIconManager.getTag(FontAwesomeIconManager.WIZARD)
        + SafeHtmlUtils.htmlEscape(messages.breadcrumbTextForWizardSendToTableAndColumns()))));
    return items;
  }

  public static List<BreadcrumbItem> forDBMSConnectionSendToWM(final String databaseUUID, final String databaseName) {
    List<BreadcrumbItem> items = forSIARDMainPage(databaseUUID, databaseName);
    items.add(
      new BreadcrumbItem(SafeHtmlUtils.fromSafeConstant(FontAwesomeIconManager.getTag(FontAwesomeIconManager.WIZARD)
        + SafeHtmlUtils.htmlEscape(messages.breadcrumbTextForWizardSendToDBMSConnection()))));
    return items;
  }

  public static List<BreadcrumbItem> forSIARDExportOptionsSenToWM(final String databaseUUID,
    final String databaseName) {
    List<BreadcrumbItem> items = forSIARDMainPage(databaseUUID, databaseName);
    items.add(
      new BreadcrumbItem(SafeHtmlUtils.fromSafeConstant(FontAwesomeIconManager.getTag(FontAwesomeIconManager.WIZARD)
        + SafeHtmlUtils.htmlEscape(messages.breadcrumbTextForWizardSendToSIARDExportOptions()))));
    return items;
  }

  public static List<BreadcrumbItem> forMetadataExportOptionsSendToWM(final String databaseUUID,
    final String databaseName) {
    List<BreadcrumbItem> items = forSIARDMainPage(databaseUUID, databaseName);
    items.add(
      new BreadcrumbItem(SafeHtmlUtils.fromSafeConstant(FontAwesomeIconManager.getTag(FontAwesomeIconManager.WIZARD)
        + SafeHtmlUtils.htmlEscape(messages.breadcrumbTextForWizardSendToMetadataExportOptions()))));
    return items;
  }

  public static List<BreadcrumbItem> forProgressBarPanelSendToWM(final String databaseUUID, final String databaseName) {
    List<BreadcrumbItem> items = forSIARDMainPage(databaseUUID, databaseName);
    items.add(
      new BreadcrumbItem(SafeHtmlUtils.fromSafeConstant(FontAwesomeIconManager.getTag(FontAwesomeIconManager.WIZARD)
        + SafeHtmlUtils.htmlEscape(messages.breadcrumbTextForWizardSendToProgressPanel()))));
    return items;
  }

  public static List<BreadcrumbItem> forActivityLog() {
    List<BreadcrumbItem> items = forManageDatabase();
    items
      .add(new BreadcrumbItem(SafeHtmlUtils.fromSafeConstant(FontAwesomeIconManager.getTag(FontAwesomeIconManager.ACTIVITY_LOG)
        + SafeHtmlUtils.htmlEscape(messages.breadcrumbTextForActivityLog())), HistoryManager::gotoActivityLog));
    return items;
  }

  public static List<BreadcrumbItem> forActivityLogDetailed() {
    List<BreadcrumbItem> items = forActivityLog();
    items
        .add(new BreadcrumbItem(SafeHtmlUtils.fromSafeConstant(FontAwesomeIconManager.getTag(FontAwesomeIconManager.ACTIVITY_LOG)
            + SafeHtmlUtils.htmlEscape(messages.breadcrumbTextForActivityLogDetail()))));
    return items;
  }

  public static List<BreadcrumbItem> forAdvancedConfiguration(final String databaseUUID, final String databaseName) {
    List<BreadcrumbItem> items = forSIARDMainPage(databaseUUID, databaseName);
    items.add(new BreadcrumbItem(
        SafeHtmlUtils.fromSafeConstant(FontAwesomeIconManager.getTag(FontAwesomeIconManager.COG)
            + SafeHtmlUtils.htmlEscape(messages.breadcrumbTextForAdvancedConfiguration()))));
    return items;
  }
}
