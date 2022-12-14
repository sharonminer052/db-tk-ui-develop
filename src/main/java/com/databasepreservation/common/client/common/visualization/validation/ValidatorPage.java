/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE file at the root of the source
 * tree and available online at
 *
 * https://github.com/keeps/dbptk-ui
 */
package com.databasepreservation.common.client.common.visualization.validation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.databasepreservation.common.client.ViewerConstants;
import com.databasepreservation.common.client.common.ContentPanel;
import com.databasepreservation.common.client.common.breadcrumb.BreadcrumbItem;
import com.databasepreservation.common.client.common.breadcrumb.BreadcrumbPanel;
import com.databasepreservation.common.client.common.dialogs.Dialogs;
import com.databasepreservation.common.client.common.fields.MetadataField;
import com.databasepreservation.common.client.common.utils.ApplicationType;
import com.databasepreservation.common.client.common.utils.CommonClientUtils;
import com.databasepreservation.common.client.common.utils.JavascriptUtils;
import com.databasepreservation.common.client.common.visualization.manager.SIARDPanel.SIARDManagerPage;
import com.databasepreservation.common.client.models.progress.ValidationProgressData;
import com.databasepreservation.common.client.models.structure.ViewerDatabase;
import com.databasepreservation.common.client.models.validation.ValidationRequirement;
import com.databasepreservation.common.client.services.SiardService;
import com.databasepreservation.common.client.tools.BreadcrumbManager;
import com.databasepreservation.common.client.tools.FontAwesomeIconManager;
import com.databasepreservation.common.client.tools.HistoryManager;
import com.databasepreservation.common.client.tools.RestUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import config.i18n.client.ClientMessages;

/**
 * @author Gabriel Barros <gbarros@keep.pt>
 */
public class ValidatorPage extends ContentPanel {
  private static Map<String, ValidatorPage> instances = new HashMap<>();
  private ViewerDatabase database;
  private int lastPosition = 0;
  private Integer countErrors = 0;
  private Integer countPassed = 0;
  private Integer countSkipped = 0;
  private Integer numberOfWarnings = 0;
  private Integer numberOfFailedRequirements = 0;
  private Boolean stickToBottom = true;
  private FlowPanel tailIndicator = new FlowPanel();

  @Override
  public void handleBreadcrumb(BreadcrumbPanel breadcrumb) {
    List<BreadcrumbItem> breadcrumbItems = BreadcrumbManager.forSIARDValidatorPage(database.getUuid(),
        database.getMetadata().getName());
    BreadcrumbManager.updateBreadcrumb(breadcrumb, breadcrumbItems);
  }

  private enum Status {
    OK, ERROR, SKIPPED, FINISH
  }

  private Timer autoUpdateTimer = new Timer() {
    @Override
    public void run() {
      update();
    }
  };

  private static final ClientMessages messages = GWT.create(ClientMessages.class);

  interface ValidatorUiBinder extends UiBinder<Widget, ValidatorPage> {
  }

  private static ValidatorUiBinder binder = GWT.create(ValidatorUiBinder.class);

  @UiField
  FlowPanel container;

  @UiField
  FlowPanel content;

  @UiField
  FlowPanel validationInformation;

  @UiField
  FlowPanel stickToBottomPanel;

  @UiField
  FlowPanel header;

  @UiField
  SimplePanel description;

  @UiField
  FocusPanel contentScroll;

  @UiField
  Button btnBack;

  public static ValidatorPage getInstance(ViewerDatabase database) {
    return instances.computeIfAbsent(database.getUuid(), k -> new ValidatorPage(database));
  }

  public void error() {
    stopUpdating();
    populateValidationInfo(false, true);
  }

  private ValidatorPage(ViewerDatabase database) {
    this.database = database;
    initWidget(binder.createAndBindUi(this));

    configureHeader();
    buildStickButton();

    populateValidationInfo(false, false);
    content.clear();
    initProgress();
  }

  private void configureHeader() {
    header.add(CommonClientUtils.getHeaderHTML(FontAwesomeIconManager.getTag(FontAwesomeIconManager.SIARD_VALIDATIONS),
      messages.validatorPageTextForTitle(), "h1"));

    MetadataField instance = MetadataField.createInstance(messages.validatorPageDescription());
    instance.setCSS("table-row-description", "font-size-description");

    description.setWidget(instance);
  }

  private void initProgress() {
    SiardService.Util.call((ValidationProgressData result) -> {
      resetInfos();
      populateValidationInfo(false, false);
      autoUpdateTimer.scheduleRepeating(1000);
      autoUpdateTimer.run();
      result.setFinished(false);
      // runValidator();
    }).getValidationProgressData(database.getUuid(), database.getUuid());
  }

  private void runValidator() {

  }

  private void update() {
    SiardService.Util.call((ValidationProgressData result) -> {
      update(result);
      if (result.getFinished() && result.getRequirementsList().size() <= lastPosition) {
        numberOfWarnings = result.getNumberOfWarnings();
        numberOfFailedRequirements = result.getNumberOfFailed();
        countPassed = result.getNumberOfPassed();
        countSkipped = result.getNumberOfSkipped();
        countErrors = result.getNumberOfErrors();
        stopUpdating();
        SIARDManagerPage.getInstance(database).refreshInstance(database.getUuid());
        // ValidationNavigationPanel.getInstance(database).update(database);
        if (numberOfFailedRequirements > 0) {
          Dialogs.showErrors(messages.validatorPageTextForTitle(),
            messages.validatorPageTextForDialogFailureInformation(database.getMetadata().getName(), countErrors),
            messages.basicActionClose());
        } else {
          Dialogs.showInformationDialog(messages.validatorPageTextForTitle(),
            messages.validatorPageTextForDialogSuccessInformation(database.getMetadata().getName()),
            messages.basicActionClose(), "btn btn-link");
        }
      }
    }).getValidationProgressData(database.getUuid(), database.getUuid());
  }

  private void update(ValidationProgressData validationProgressData) {
    List<ValidationRequirement> requirementList = validationProgressData.getRequirementsList(lastPosition);
    lastPosition += requirementList.size();
    for (ValidationRequirement requirement : requirementList) {

      if (requirement.getType().equals(ValidationRequirement.Type.REQUIREMENT)) {
        FlowPanel panel = new FlowPanel();
        panel.addStyleName("validation-panel title");
        panel.add(new Label(requirement.getId()));
        panel.add(new Label(requirement.getMessage()));
        content.add(panel);
      }

      if (requirement.getType().equals(ValidationRequirement.Type.MESSAGE)
        && !requirement.getStatus().equals(Status.FINISH.name())) {
        FlowPanel panel = new FlowPanel();
        FlowPanel panelTitle = new FlowPanel();
        panel.addStyleName("validation-panel requirement path text-muted");
        panelTitle.addStyleName("title");
        panelTitle.add(new Label(requirement.getId()));
        Label message = new Label(requirement.getMessage());
        message.addStyleName("description text-muted");
        panel.add(panelTitle);
        panel.add(message);
        content.add(panel);
      }

      if (ValidationRequirement.Type.SPARSE_PROGRESS.equals(requirement.getType())) {
        FlowPanel panel = new FlowPanel();
        panel.addStyleName("validation-panel requirement path text-muted");
        Label path = new Label("Completed " + requirement.getMessage() + " rows");
        path.addStyleName("description text-muted sparse-progress");
        panel.add(path);
        content.add(panel);
      }

      if (requirement.getType().equals(ValidationRequirement.Type.PATH)) {
        FlowPanel panel = new FlowPanel();
        FlowPanel panelTitle = new FlowPanel();
        panel.addStyleName("validation-panel requirement path text-muted");
        panelTitle.addStyleName("title");
        panelTitle.add(new Label(requirement.getId()));
        Label path = new Label("Validation running on path: " + requirement.getMessage());
        path.addStyleName("description text-muted");
        panel.add(panelTitle);
        panel.add(path);
        content.add(panel);
      }

      if (ValidationRequirement.Type.PATH_COMPLETE.equals(requirement.getType())) {
        FlowPanel panel = new FlowPanel();
        FlowPanel panelTitle = new FlowPanel();
        panel.addStyleName("validation-panel requirement text-muted");
        panelTitle.addStyleName("title");
        panelTitle.add(new Label(requirement.getId()));
        Label path = new Label("Validation finish on path: " + requirement.getMessage());
        path.addStyleName("description text-muted");
        panelTitle.add(path);
        panel.add(panelTitle);
        Label status = buildStatus(requirement.getStatus());
        panel.add(status);
        content.add(panel);
      }

      if (requirement.getType().equals(ValidationRequirement.Type.SUB_REQUIREMENT)) {
        FlowPanel panel = new FlowPanel();
        FlowPanel panelTitle = new FlowPanel();
        panel.addStyleName("validation-panel requirement");
        panelTitle.addStyleName("title");
        panelTitle.add(new Label(requirement.getId()));
        Label description = new Label(messages.validationRequirements(requirement.getId()));
        description.addStyleName("description text-muted");
        panelTitle.add(description);
        panel.add(panelTitle);
        Label status = buildStatus(requirement.getStatus());
        panel.add(status);
        content.add(panel);
      }

      if (stickToBottom) {
        contentScroll.getElement().setScrollTop(contentScroll.getElement().getScrollHeight());
      }
    }
  }

  private Label buildStatus(String status) {
    Label statusLabel = new Label(status);
    switch (Status.valueOf(status)) {
      case OK:
        statusLabel.setStyleName("label-success");
        break;
      case ERROR:
        statusLabel.setStyleName("label-danger");
        statusLabel.setTitle(messages.validatorPageTextForErrorDetails());
        break;
      case SKIPPED:
        statusLabel.setStyleName("label-default");
        break;
      default:
        statusLabel.setStyleName("label-info");
    }
    statusLabel.addStyleName("gwt-Label validation-status");
    return statusLabel;
  }

  private Label updateStatus() {
    Label statusLabel = new Label();
    String statusText = messages.humanizedTextForSIARDValidationRunning();
    statusLabel.setStyleName("label-status label-info");
    if (countErrors != 0) {
      statusText = messages.humanizedTextForSIARDValidationFailed();
      statusLabel.setStyleName("label-status label-danger");
    } else if (countPassed != 0 || countSkipped != 0) {
      statusText = messages.humanizedTextForSIARDValidationSuccess();
      statusLabel.setStyleName("label-status label-success");
    }
    statusLabel.setText(statusText);
    return statusLabel;
  }

  private void stopUpdating() {
    populateValidationInfo(true, false);
    if (autoUpdateTimer != null) {
      autoUpdateTimer.cancel();
    }
  }

  // For run again button and SIARDMainPAGE
  public static void clear(String databaseUUID) {
    if (instances.get(databaseUUID) != null) {
      instances.remove(databaseUUID);
    }
  }

  public static boolean checkInstance(String databaseUUID) {
    return instances.get(databaseUUID) != null;
  }

  private void resetInfos() {
    lastPosition = 0;
    content.clear();
    countPassed = 0;
    countErrors = 0;
    countSkipped = 0;
    numberOfWarnings = 0;
    numberOfFailedRequirements = 0;
    populateValidationInfo(false, false);
  }

  private void populateValidationInfo(Boolean enable, Boolean error) {
    validationInformation.clear();
    FlowPanel left = new FlowPanel();
    FlowPanel right = new FlowPanel();

    // Database Name
    left.add(
      validationInfoBuilder(messages.validatorPageTextForDatabaseName(), new Label(database.getMetadata().getName())));

    // counts
    left.add(validationInfoBuilder(messages.validatorPageRequirementsThatPassed(), countPassed, enable));
    left.add(validationInfoBuilder(messages.validatorPageRequirementsThatFailed(), numberOfFailedRequirements, enable));
    left.add(validationInfoBuilder(messages.numberOfValidationsErrors(), countErrors, enable));
    left.add(validationInfoBuilder(messages.numberOfValidationsWarnings(), numberOfWarnings, enable));
    left.add(validationInfoBuilder(messages.numberOfValidationsSkipped(), countSkipped, enable));

    // Validator Status
    if (error) {
      Label statusLabel = new Label(messages.alertErrorTitle());
      statusLabel.setStyleName("label-danger label-error");
      left.add(validationInfoBuilder(messages.validatorPageTextForStatus(), statusLabel));
    } else {
      left.add(validationInfoBuilder(messages.validatorPageTextForStatus(), updateStatus()));
    }

    // SIARD specification link
    Button SIARDSpecification = new Button(ViewerConstants.SIARD2_1);
    SIARDSpecification.addClickHandler(
      event -> Window.open(ViewerConstants.SIARD_SPECIFICATION_LINK, ViewerConstants.BLANK_LINK, null));

    right.add(validationInfoBuilder(messages.validatorPageTextForSIARDSpecification(), SIARDSpecification));

    // Additional checks link
    Button AdditionalSpecification = new Button(messages.basicActionOpen());
    AdditionalSpecification.addClickHandler(
      event -> Window.open(ViewerConstants.ADDITIONAL_CHECKS_SPECIFICATIONLINK, ViewerConstants.BLANK_LINK, null));

    right.add(
      validationInfoBuilder(messages.validatorPageTextForAdditionalChecksSpecification(), AdditionalSpecification));

    // Report link
    Button report = new Button(messages.basicActionOpen());
    report.addClickHandler(event -> {
      if (ApplicationType.getType().equals(ViewerConstants.APPLICATION_ENV_DESKTOP)) {
        JavascriptUtils.showItem(database.getValidatorReportPath());
      } else {
        SafeUri downloadUri = RestUtils.createFileResourceDownloadValidationReportUri(database.getUuid());
        Window.Location.assign(downloadUri.asString());
      }
    });
    report.setEnabled(enable);

    right.add(validationInfoBuilder(messages.reportFile(), report));

    validationInformation.add(left);
    validationInformation.add(right);
  }

  private FlowPanel validationInfoBuilder(String key, Label value) {
    FlowPanel panel = new FlowPanel();
    panel.addStyleName("validation-info-panel");
    Label keyLabel = new Label(key);
    keyLabel.addStyleName("title");
    panel.add(keyLabel);
    panel.add(value);

    return panel;
  }

  private FlowPanel validationInfoBuilder(String key, Button value) {
    FlowPanel panel = new FlowPanel();
    panel.addStyleName("validation-info-panel");
    Label keyLabel = new Label(key);
    keyLabel.addStyleName("title");
    value.addStyleName("btn btn-link-info");
    panel.add(keyLabel);
    panel.add(value);

    return panel;
  }

  private FlowPanel validationInfoBuilder(String key, Integer value, Boolean loading) {
    Label valueLabel = new Label();
    FlowPanel panel = new FlowPanel();
    panel.addStyleName("validation-info-panel");
    Label keyLabel = new Label(key);
    keyLabel.addStyleName("title");
    panel.add(keyLabel);
    if (!loading) {
      String iconTag = FontAwesomeIconManager.getTag(FontAwesomeIconManager.COG);
      HTML html = new HTML(SafeHtmlUtils.fromSafeConstant(iconTag));
      html.getElement().getFirstChildElement().addClassName("fa-" + FontAwesomeIconManager.SPIN);
      html.addStyleName("text-muted");
      panel.add(html);
    } else {
      valueLabel.setText(value.toString());
      panel.add(valueLabel);
    }

    return panel;
  }

  private void buildStickButton() {
    tailIndicator.setStyleName("tail tail-on");
    FocusPanel stickFocus = new FocusPanel();
    FlowPanel stickToBottomInner = new FlowPanel();
    Label tailLabel = new Label(messages.validatorPageTextForStick());
    stickToBottomInner.add(tailLabel);
    stickToBottomInner.add(tailIndicator);
    stickToBottomInner.addStyleName("stick-bottom-log");
    stickFocus.addClickHandler(event -> {
      stickToBottom = !stickToBottom;
      if (stickToBottom) {
        contentScroll.getElement().setScrollTop(contentScroll.getElement().getScrollHeight());
        tailIndicator.setStyleName("tail tail-on");
      } else {
        tailIndicator.setStyleName("tail tail-off");
      }
    });
    stickFocus.add(stickToBottomInner);
    stickToBottomPanel.add(stickFocus);
  }

  @UiHandler("btnBack")
  void setBtnBackHandler(ClickEvent e) {
    HistoryManager.gotoSIARDInfo(database.getUuid());
  }

  @UiHandler("contentScroll")
  void setContentScrollHandler(MouseWheelEvent e) {
    if (e.isNorth()) {
      stickToBottom = false;
      tailIndicator.setStyleName("tail tail-off");
    }
  }
}
