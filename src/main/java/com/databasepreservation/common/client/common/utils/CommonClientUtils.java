package com.databasepreservation.common.client.common.utils;

import com.databasepreservation.common.client.ViewerConstants;
import com.databasepreservation.common.client.common.fields.GenericField;
import com.databasepreservation.common.client.models.status.collection.TableStatus;
import com.databasepreservation.common.client.models.structure.ViewerTable;
import com.databasepreservation.common.client.models.structure.ViewerView;
import com.databasepreservation.common.client.tools.FontAwesomeIconManager;
import com.databasepreservation.common.client.tools.HistoryManager;
import com.databasepreservation.common.client.tools.ViewerStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Widget;
import config.i18n.client.ClientMessages;
import dk.sa.xmlns.diark._1_0.fileindex.FileIndexType;

import java.util.List;

/**
 * @author Bruno Ferreira <bferreira@keep.pt>
 */
public class CommonClientUtils {
  private static final ClientMessages messages = GWT.create(ClientMessages.class);

  public static FlowPanel getPanelInformation(String label, String text, String classes) {
    FlowPanel panel = new FlowPanel();

    SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
    safeHtmlBuilder.append(SafeHtmlUtils.fromString(text));

    HTML html = new HTML(safeHtmlBuilder.toSafeHtml());
    html.addStyleName(classes);

    GenericField genericField = GenericField.createInstance(label, html);
    genericField.setCSSMetadata("metadata-field", "metadata-information-element-label");
    panel.add(genericField);

    return panel;
  }

  public static FlowPanel getCardTitle(String text) {
    FlowPanel panel = new FlowPanel();
    SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
    safeHtmlBuilder.append(SafeHtmlUtils.fromString(text));
    HTML html = new HTML(safeHtmlBuilder.toSafeHtml());
    panel.add(html);

    return panel;
  }

  public static FlowPanel getHeader(ViewerTable table, String hClass, boolean multiSchema) {
    if (multiSchema) {
      return getHeaderMultiSchema(table, hClass);
    } else {
      return getHeaderSingleSchema(table, hClass);
    }
  }

  public static FlowPanel getHeader(TableStatus tableStatus, ViewerTable table, String hClass, boolean multiSchema) {
    if (multiSchema) {
      return getHeaderMultiSchema(tableStatus, table, hClass);
    } else {
      return getHeaderSingleSchema(tableStatus, table, hClass);
    }
  }

  private static FlowPanel getHeaderMultiSchema(TableStatus status, ViewerTable table, String hClass) {
    String separatorIconTag = FontAwesomeIconManager.getTagWithStyleName(FontAwesomeIconManager.SCHEMA_TABLE_SEPARATOR,
        "schema-table-separator");

    if (table.isCustomView()) {
      final SafeHtml stackedIconSafeHtml = FontAwesomeIconManager.getStackedIconSafeHtml(
          FontAwesomeIconManager.SCHEMA_VIEWS, FontAwesomeIconManager.COG,
          table.getSchemaName() + " " + separatorIconTag + " " + status.getCustomName());
      return getHeader(stackedIconSafeHtml, hClass);
    } else if (table.isMaterializedView()) {
      final SafeHtml stackedIconSafeHtml = FontAwesomeIconManager.getStackedIconSafeHtml(
          FontAwesomeIconManager.SCHEMA_VIEWS, FontAwesomeIconManager.TABLE,
          table.getSchemaName() + " " + separatorIconTag + " " + status.getCustomName());
      return getHeader(stackedIconSafeHtml, hClass);
    } else {
      final SafeHtml tagSafeHtml = FontAwesomeIconManager.getTagSafeHtml(FontAwesomeIconManager.TABLE,
          table.getSchemaName() + " " + separatorIconTag + " " + status.getCustomName());
      return getHeader(tagSafeHtml, hClass);
    }
  }

  private static FlowPanel getHeaderMultiSchema(ViewerTable table, String hClass) {
    String separatorIconTag = FontAwesomeIconManager.getTagWithStyleName(FontAwesomeIconManager.SCHEMA_TABLE_SEPARATOR,
      "schema-table-separator");

    if (table.isCustomView()) {
      final SafeHtml stackedIconSafeHtml = FontAwesomeIconManager.getStackedIconSafeHtml(
        FontAwesomeIconManager.SCHEMA_VIEWS, FontAwesomeIconManager.COG,
        table.getSchemaName() + " " + separatorIconTag + " " + table.getNameWithoutPrefix());
      return getHeader(stackedIconSafeHtml, hClass);
    } else if (table.isMaterializedView()) {
      final SafeHtml stackedIconSafeHtml = FontAwesomeIconManager.getStackedIconSafeHtml(
        FontAwesomeIconManager.SCHEMA_VIEWS, FontAwesomeIconManager.TABLE,
        table.getSchemaName() + " " + separatorIconTag + " " + table.getNameWithoutPrefix());
      return getHeader(stackedIconSafeHtml, hClass);
    } else {
      final SafeHtml tagSafeHtml = FontAwesomeIconManager.getTagSafeHtml(FontAwesomeIconManager.TABLE,
        table.getSchemaName() + " " + separatorIconTag + " " + table.getNameWithoutPrefix());
      return getHeader(tagSafeHtml, hClass);
    }
  }

  private static FlowPanel getHeaderSingleSchema(ViewerTable table, String hClass) {
    if (table.isCustomView()) {
      final SafeHtml stackedIconSafeHtml = FontAwesomeIconManager.getStackedIconSafeHtml(
        FontAwesomeIconManager.SCHEMA_VIEWS, FontAwesomeIconManager.COG, table.getNameWithoutPrefix());
      return getHeader(stackedIconSafeHtml, hClass);
    } else if (table.isMaterializedView()) {
      final SafeHtml stackedIconSafeHtml = FontAwesomeIconManager.getStackedIconSafeHtml(
        FontAwesomeIconManager.SCHEMA_VIEWS, FontAwesomeIconManager.TABLE, table.getNameWithoutPrefix());
      return getHeader(stackedIconSafeHtml, hClass);
    } else {
      final String tag = FontAwesomeIconManager.getTag(FontAwesomeIconManager.TABLE);
      return getHeader(tag, table, hClass);
    }
  }

  private static FlowPanel getHeaderSingleSchema(TableStatus status, ViewerTable table, String hClass) {
    if (table.isCustomView()) {
      final SafeHtml stackedIconSafeHtml = FontAwesomeIconManager.getStackedIconSafeHtml(
          FontAwesomeIconManager.SCHEMA_VIEWS, FontAwesomeIconManager.COG, status.getCustomName());
      return getHeader(stackedIconSafeHtml, hClass);
    } else if (table.isMaterializedView()) {
      final SafeHtml stackedIconSafeHtml = FontAwesomeIconManager.getStackedIconSafeHtml(
          FontAwesomeIconManager.SCHEMA_VIEWS, FontAwesomeIconManager.TABLE, status.getCustomName());
      return getHeader(stackedIconSafeHtml, hClass);
    } else {
      final String tag = FontAwesomeIconManager.getTag(FontAwesomeIconManager.TABLE);
      return getHeader(tag, status.getCustomName(), hClass);
    }
  }

  public static FlowPanel getHeader(SafeHtml iconStack, String hClass) {
    FlowPanel panel = new FlowPanel();
    HTML html = new HTML(iconStack);
    html.addStyleName(hClass);
    panel.add(html);

    return panel;
  }

  private static FlowPanel getHeader(String iconTag, ViewerTable table, String hClass) {
    return getHeader(iconTag, table.getNameWithoutPrefix(), hClass);
  }

  public static HTML getHeaderHTML(String iconTag, String title, String styleName) {
    SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
    safeHtmlBuilder.append(SafeHtmlUtils.fromSafeConstant(iconTag)).appendEscaped(" ")
        .append(SafeHtmlUtils.fromString(title));

    HTML html = new HTML(safeHtmlBuilder.toSafeHtml());
    html.addStyleName(styleName);

    return html;
  }

  public static FlowPanel getHeader(String iconTag, String title, String hClass) {
    FlowPanel panel = new FlowPanel();

    SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
    safeHtmlBuilder.append(SafeHtmlUtils.fromSafeConstant(iconTag)).appendEscaped(" ")
        .append(SafeHtmlUtils.fromString(title));

    HTML html = new HTML(safeHtmlBuilder.toSafeHtml());
    html.addStyleName(hClass);
    panel.add(html);

    return panel;
  }

  public static FlowPanel getHeader(String iconTag, SafeHtml title, String hClass) {
    FlowPanel panel = new FlowPanel();

    SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
    safeHtmlBuilder.append(SafeHtmlUtils.fromSafeConstant(iconTag)).appendEscaped(" ")
        .append(title);

    HTML html = new HTML(safeHtmlBuilder.toSafeHtml());
    html.addStyleName(hClass);
    panel.add(html);

    return panel;
  }

  public static Anchor getAnchorForLOBDownload(final String databaseUUID, final String tableUUID, final String rowUUID,
    final int columnIndexInEnclosingTable, final String lobName) {
    String servlet = ViewerConstants.API_SERVLET;
    String resource = ViewerConstants.API_V1_LOBS_RESOURCE;
    String urlBuilder = servlet + resource + "/" + databaseUUID + "/" + tableUUID +
        "/" + rowUUID + "/" + columnIndexInEnclosingTable + "/" + lobName;
    return new Anchor(messages.row_downloadLOB(), urlBuilder);
  }

  public static SafeHtmlBuilder constructViewQuery(ViewerView view) {
    SafeHtmlBuilder infoBuilder = new SafeHtmlBuilder();

    if (ViewerStringUtils.isNotBlank(view.getQueryOriginal())) {
      infoBuilder.append(CommonClientUtils.getFieldHTML(messages.originalQuery(), SafeHtmlUtils.fromSafeConstant(
        "<pre><code class='sql'>" + SafeHtmlUtils.htmlEscape(view.getQueryOriginal()) + "</code></pre>")));
    }

    if (ViewerStringUtils.isNotBlank(view.getQuery())) {
      infoBuilder.append(CommonClientUtils.getFieldHTML(messages.query(), SafeHtmlUtils
        .fromSafeConstant("<pre><code class='sql'>" + SafeHtmlUtils.htmlEscape(view.getQuery()) + "</code></pre>")));
    }

    return infoBuilder;
  }

  public static SafeHtmlBuilder constructSpan(String value, String title, String css) {
    SafeHtmlBuilder span = new SafeHtmlBuilder();

    if (title == null) title = "";

    span.append(SafeHtmlUtils.fromSafeConstant("<span class='"))
            .append(SafeHtmlUtils.fromSafeConstant(css))
            .append(SafeHtmlUtils.fromSafeConstant("' title='"))
            .append(SafeHtmlUtils.fromSafeConstant(title))
            .append(SafeHtmlUtils.fromSafeConstant("'>"))
            .append(SafeHtmlUtils.fromSafeConstant(value))
            .append(SafeHtmlUtils.fromSafeConstant("</span>"));
    return span;
  }

  public static SafeHtml wrapOnDiv(List<SafeHtmlBuilder> builders) {
    SafeHtmlBuilder div = new SafeHtmlBuilder();

    div.append(SafeHtmlUtils.fromSafeConstant("<div>"));
    for (SafeHtmlBuilder builder : builders) {
      div.append(builder.toSafeHtml());
    }
    div.append(SafeHtmlUtils.fromSafeConstant("</div>"));

    return div.toSafeHtml();
  }

  public static FlowPanel wrapOnDiv(String divClassName, Widget... widgets) {
    FlowPanel panel = new FlowPanel();
    panel.addStyleName(divClassName);
    for (Widget widget : widgets) {
      panel.add(widget);
    }

    return panel;
  }

  public static FlowPanel getSchemaAndTableHeader(String databaseUUID, ViewerTable table, String hClass) {
    FlowPanel panel = new FlowPanel();
    panel.addStyleName("schema-table-header");

    String iconTag = FontAwesomeIconManager.getTag(FontAwesomeIconManager.SCHEMA_TABLE_SEPARATOR);
    final SafeHtml html = FontAwesomeIconManager.getTagSafeHtml(FontAwesomeIconManager.TABLE, table.getSchemaName() + iconTag + table.getName());
    Hyperlink tableLink = new Hyperlink(html, HistoryManager.linkToTable(databaseUUID, table.getUuid()));
    tableLink.addStyleName(hClass);
    panel.add(tableLink);

    return panel;
  }

  public static SafeHtml getFieldHTML(String label, String value) {
    boolean blankLabel = ViewerStringUtils.isBlank(label);
    boolean blankValue = ViewerStringUtils.isBlank(value);

    if (blankLabel || blankValue) {
      return SafeHtmlUtils.EMPTY_SAFE_HTML;

    } else {
      SafeHtmlBuilder b = new SafeHtmlBuilder();
      b.append(SafeHtmlUtils.fromSafeConstant("<div class=\"field\">"));
      b.append(SafeHtmlUtils.fromSafeConstant("<div class=\"label\">"));
      b.append(SafeHtmlUtils.fromString(label));
      b.append(SafeHtmlUtils.fromSafeConstant("</div>"));
      b.append(SafeHtmlUtils.fromSafeConstant("<div class=\"value\">"));
      b.append(SafeHtmlUtils.fromString(value));
      b.append(SafeHtmlUtils.fromSafeConstant("</div>"));
      b.append(SafeHtmlUtils.fromSafeConstant("</div>"));
      return b.toSafeHtml();
    }
  }

  public static SafeHtml getFieldHTML(String label, SafeHtml value) {
    boolean blankLabel = ViewerStringUtils.isBlank(label);
    boolean blankValue = value == null || value == SafeHtmlUtils.EMPTY_SAFE_HTML;

    if (blankLabel || blankValue) {
      return SafeHtmlUtils.EMPTY_SAFE_HTML;

    } else {
      SafeHtmlBuilder b = new SafeHtmlBuilder();
      b.append(SafeHtmlUtils.fromSafeConstant("<div class=\"field\">"));
      b.append(SafeHtmlUtils.fromSafeConstant("<div class=\"label\">"));
      b.append(SafeHtmlUtils.fromString(label));
      b.append(SafeHtmlUtils.fromSafeConstant("</div>"));
      b.append(SafeHtmlUtils.fromSafeConstant("<div class=\"value\">"));
      b.append(value);
      b.append(SafeHtmlUtils.fromSafeConstant("</div>"));
      b.append(SafeHtmlUtils.fromSafeConstant("</div>"));
      return b.toSafeHtml();
    }
  }
}
