package com.databasepreservation.common.shared.client.common.visualization.validation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.databasepreservation.common.client.BrowserService;
import com.databasepreservation.common.shared.ValidationProgressData;
import com.databasepreservation.common.shared.ViewerConstants;
import com.databasepreservation.common.shared.ViewerStructure.IsIndexed;
import com.databasepreservation.common.shared.ViewerStructure.ViewerDatabase;
import com.databasepreservation.common.shared.client.breadcrumb.BreadcrumbItem;
import com.databasepreservation.common.shared.client.breadcrumb.BreadcrumbPanel;
import com.databasepreservation.common.shared.client.common.ContentPanel;
import com.databasepreservation.common.shared.client.common.DefaultAsyncCallback;
import com.databasepreservation.common.shared.client.common.dialogs.Dialogs;
import com.databasepreservation.common.shared.client.common.utils.ApplicationType;
import com.databasepreservation.common.shared.client.common.utils.JavascriptUtils;
import com.databasepreservation.common.shared.client.common.visualization.manager.SIARDPanel.SIARDManagerPage;
import com.databasepreservation.common.shared.client.tools.BreadcrumbManager;
import com.databasepreservation.common.shared.client.tools.FontAwesomeIconManager;
import com.databasepreservation.common.shared.client.tools.HistoryManager;
import com.databasepreservation.common.shared.client.tools.RestUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.http.client.URL;
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
import com.google.gwt.user.client.ui.Widget;

import config.i18n.client.ClientMessages;

/**
 * @author Gabriel Barros <gbarros@keep.pt>
 */
public class ValidatorPage extends ContentPanel {
  private static final ClientMessages messages = GWT.create(ClientMessages.class);
  private static Map<String, ValidatorPage> instances = new HashMap<>();
  private String databaseUUID;
  private String reporterPath;
  private String udtPath = null;
  private ViewerDatabase database;
  private int lastPosition = 0;
  private Integer countErrors = 0;
  private Integer countPassed = 0;
  private Integer countSkipped = 0;
  private Integer numberOfWarnings = 0;
  private Integer numberOfErrors = 0;
  private Boolean stickToBottom = true;
  private FlowPanel tailIndicator = new FlowPanel();
  private BreadcrumbPanel breadcrumb;

  @Override
  public void handleBreadcrumb(BreadcrumbPanel breadcrumb) {
    this.breadcrumb = breadcrumb;
  }

  private enum Status {
    OK, ERROR, SKIPPED, FINISH
  }

  private Timer autoUpdateTimer = new Timer() {
    @Override
    public void run() {
      ValidatorPage.this.update();
    }
  };

  interface ValidatorUiBinder extends UiBinder<Widget, ValidatorPage> {
  }

  private static ValidatorUiBinder binder = GWT.create(ValidatorUiBinder.class);

  //For server
  public static ValidatorPage getInstance(ViewerDatabase database) {
    return ValidatorPage.getInstance(database, null, null);
  }

  public static ValidatorPage getInstance(ViewerDatabase database, String reporterPath) {
    return ValidatorPage.getInstance(database, reporterPath, null);
  }

  public static ValidatorPage getInstance(ViewerDatabase database, String reporterPath, String udtPath) {
    String databaseUUID = database.getUUID();
    if (instances.get(databaseUUID) == null) {
      ValidatorPage instance = new ValidatorPage(databaseUUID, reporterPath, udtPath);
      instances.put(databaseUUID, instance);
    }

    return instances.get(databaseUUID);
  }

  @UiField
  FlowPanel container;

  @UiField
  FlowPanel content;

  @UiField
  FlowPanel validationInformation;

  @UiField
  FlowPanel stickToBottomPanel;

  @UiField
  Label title;

  @UiField
  FocusPanel contentScroll;

  @UiField
  Button btnBack;

  @UiField
  Button btnRunAgain;

  private ValidatorPage(String databaseUUID, String reporterPath, String udtPath) {
    this.databaseUUID = databaseUUID;
    if(reporterPath != null){
      this.reporterPath = URL.decodePathSegment(reporterPath);
    }
    if (udtPath != null) {
      this.udtPath = URL.decodePathSegment(udtPath);
    }
    initWidget(binder.createAndBindUi(this));

    title.setText(messages.validatorPageTextForTitle());
    buildStickButton();

    BrowserService.Util.getInstance().retrieve(databaseUUID, ViewerDatabase.class.getName(), databaseUUID,
      new DefaultAsyncCallback<IsIndexed>() {
        @Override
        public void onSuccess(IsIndexed result) {
          database = (ViewerDatabase) result;
          List<BreadcrumbItem> breadcrumbItems = BreadcrumbManager.forSIARDValidatorPage(database.getUUID(),
                  database.getMetadata().getName());
          BreadcrumbManager.updateBreadcrumb(breadcrumb, breadcrumbItems);

          populateValidationInfo(false, false);

          BrowserService.Util.getInstance().clearValidationProgressData(databaseUUID, new DefaultAsyncCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
              content.clear();
              initProgress();
            }
          });
        }
      });
  }

  private void initProgress() {
    BrowserService.Util.getInstance().getValidationProgressData(databaseUUID,
      new DefaultAsyncCallback<ValidationProgressData>() {
        @Override
        public void onSuccess(ValidationProgressData result) {
          resetInfos();
          populateValidationInfo(false, false);
          autoUpdateTimer.scheduleRepeating(250);
          autoUpdateTimer.run();
          runValidator();
        }
      });
  }

  private void runValidator() {
    BrowserService.Util.getInstance().validateSIARD(database.getUUID(), database.getSIARDPath(), reporterPath, udtPath,
      new DefaultAsyncCallback<Boolean>() {
        @Override
        public void onSuccess(Boolean result) {
          // Do nothing, wait for update finish
        }

        @Override
        public void onFailure(Throwable caught) {
          stopUpdating();
          Dialogs.showErrors(messages.validatorPageTextForTitle(), caught.getMessage(), messages.basicActionClose());
          BrowserService.Util.getInstance().updateStatusValidate(databaseUUID, ViewerDatabase.ValidationStatus.ERROR,
            new DefaultAsyncCallback<Void>() {
              @Override
              public void onSuccess(Void result) {
                populateValidationInfo(false, true);
              }
            });
        }
      });
  }

  private void update() {
    BrowserService.Util.getInstance().getValidationProgressData(databaseUUID,
      new DefaultAsyncCallback<ValidationProgressData>() {
        @Override
        public void onSuccess(ValidationProgressData result) {
          update(result);
          if (result.isFinished() && result.getRequirementsList().size() <= lastPosition) {
            numberOfWarnings = result.getNumberOfWarnings();
            numberOfErrors = result.getNumberOfErrors();
            stopUpdating();
            SIARDManagerPage.getInstance(database).refreshInstance(database.getUUID());
            if (numberOfErrors > 0) {
              Dialogs.showErrors(messages.validatorPageTextForTitle(),
                messages.validatorPageTextForDialogFailureInformation(database.getMetadata().getName(), numberOfErrors),
                messages.basicActionClose());
            } else {
              Dialogs.showInformationDialog(messages.validatorPageTextForTitle(),
                messages.validatorPageTextForDialogSuccessInformation(database.getMetadata().getName()),
                messages.basicActionClose(), "btn btn-link");
            }
          }
        }
      });
  }

  private void update(ValidationProgressData validationProgressData) {
    List<ValidationProgressData.Requirement> requirementList = validationProgressData.getRequirementsList(lastPosition);
    lastPosition += requirementList.size();
    for (ValidationProgressData.Requirement requirement : requirementList) {

      if (requirement.getType().equals(ValidationProgressData.Requirement.Type.REQUIREMENT)) {
        FlowPanel panel = new FlowPanel();
        panel.addStyleName("validation-panel title");
        panel.add(new Label(requirement.getID()));
        panel.add(new Label(requirement.getMessage()));
        content.add(panel);
      }

      if (requirement.getType().equals(ValidationProgressData.Requirement.Type.MESSAGE)
        && !requirement.getStatus().equals(Status.FINISH.name())) {
        FlowPanel panel = new FlowPanel();
        FlowPanel panelTitle = new FlowPanel();
        panel.addStyleName("validation-panel requirement path text-muted");
        panelTitle.addStyleName("title");
        panelTitle.add(new Label(requirement.getID()));
        Label message = new Label(requirement.getMessage());
        message.addStyleName("description text-muted");
        panel.add(panelTitle);
        panel.add(message);
        content.add(panel);
      }

      if (requirement.getType().equals(ValidationProgressData.Requirement.Type.PATH)) {
        FlowPanel panel = new FlowPanel();
        FlowPanel panelTitle = new FlowPanel();
        panel.addStyleName("validation-panel requirement path text-muted");
        panelTitle.addStyleName("title");
        panelTitle.add(new Label(requirement.getID()));
        Label path = new Label("Validation running on path: " + requirement.getMessage());
        path.addStyleName("description text-muted");
        panel.add(panelTitle);
        panel.add(path);
        content.add(panel);
      }

      if (requirement.getType().equals(ValidationProgressData.Requirement.Type.SUB_REQUIREMENT)) {
        FlowPanel panel = new FlowPanel();
        FlowPanel panelTitle = new FlowPanel();
        panel.addStyleName("validation-panel requirement");
        panelTitle.addStyleName("title");
        panelTitle.add(new Label(requirement.getID()));
        Label description = new Label(messages.validationRequirements(requirement.getID()));
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
        countPassed++;
        statusLabel.setStyleName("label-success");
        break;
      case ERROR:
        countErrors++;
        statusLabel.setStyleName("label-danger");
        statusLabel.setTitle(messages.validatorPageTextForErrorDetails());
        break;
      case SKIPPED:
        countSkipped++;
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
    statusLabel.setStyleName("label-info");
    if (countErrors != 0) {
      statusText = messages.humanizedTextForSIARDValidationFailed();
      statusLabel.setStyleName("label-danger");
    } else if (countPassed != 0 || countSkipped != 0) {
      statusText = messages.humanizedTextForSIARDValidationSuccess();
      statusLabel.setStyleName("label-success");
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
    numberOfErrors = 0;
    populateValidationInfo(false, false);
  }

  private void populateValidationInfo(Boolean enable, Boolean error) {
    validationInformation.clear();
    btnRunAgain.setEnabled(enable);
    FlowPanel left = new FlowPanel();
    FlowPanel right = new FlowPanel();

    // Database Name
    left.add(validationInfoBuilder(messages.managePageTableHeaderTextForDatabaseName(),
      new Label(database.getMetadata().getName())));

    // counts
    left.add(validationInfoBuilder(messages.validatorPageRequirementsThatFailed(), countErrors, enable));
    left.add(validationInfoBuilder(messages.numberOfValidationsErrors(), numberOfErrors, enable));
    left.add(validationInfoBuilder(messages.numberOfValidationsPassed(), countPassed, enable));
    left.add(validationInfoBuilder(messages.numberOfValidationsWarnings(), numberOfWarnings, enable));
    left.add(validationInfoBuilder(messages.numberOfValidationsSkipped(), countSkipped, enable));

    // Validator Status
    if (error) {
      Label statusLabel = new Label(messages.alertErrorTitle());
      statusLabel.setStyleName("label-danger label-error");
      left.add(validationInfoBuilder(messages.managePageTableHeaderTextForDatabaseStatus(), statusLabel));
    } else {
      left.add(validationInfoBuilder(messages.managePageTableHeaderTextForDatabaseStatus(), updateStatus()));
    }

    // SIARD Version
    right.add(validationInfoBuilder(messages.validatorPageTextForSIARDVersion(), new Label(ViewerConstants.SIARD2_1)));

    // SIARD specification link
    Button SIARDSpecification = new Button(ViewerConstants.SIARD2_1);
    SIARDSpecification.addClickHandler(event -> {
      Window.open(ViewerConstants.SIARD_SPECIFICATION_LINK, ViewerConstants.BLANK_LINK, null);
    });

    right.add(validationInfoBuilder(messages.validatorPageTextForSIARDSpecification(), SIARDSpecification));

    // Additional checks link
    Button AdditionalSpecification = new Button(messages.basicActionOpen());
    AdditionalSpecification.addClickHandler(event -> {
      Window.open(ViewerConstants.ADDITIONAL_CHECKS_SPECIFICATIONLINK, ViewerConstants.BLANK_LINK, null);
    });

    right.add(
      validationInfoBuilder(messages.validatorPageTextForAdditionalChecksSpecification(), AdditionalSpecification));

    // Report link
    Button report = new Button(messages.basicActionOpen());
    report.addClickHandler(event -> {
      if (ApplicationType.getType().equals(ViewerConstants.DESKTOP)) {
        JavascriptUtils.showItem(reporterPath);
      } else {
        SafeUri downloadUri = RestUtils.createFileResourceDownloadValidationReportUri(database.getUUID());
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
    HistoryManager.gotoSIARDInfo(databaseUUID);
  }

  @UiHandler("btnRunAgain")
  void setBtnRunAgainHandler(ClickEvent e) {
    clear(databaseUUID);
    BrowserService.Util.getInstance().clearValidationProgressData(databaseUUID, new DefaultAsyncCallback<Void>() {
      @Override
      public void onSuccess(Void result) {
        initProgress();
      }
    });
  }

  @UiHandler("contentScroll")
  void setContentScrollHandler(MouseWheelEvent e) {
    if (e.isNorth()) {
      stickToBottom = false;
      tailIndicator.setStyleName("tail tail-off");
    }
  }
}
