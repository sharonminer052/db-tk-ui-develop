package com.databasepreservation.server.client.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.databasepreservation.common.client.BrowserService;
import com.databasepreservation.common.shared.ViewerStructure.IsIndexed;
import com.databasepreservation.common.shared.ViewerStructure.ViewerDatabase;
import com.databasepreservation.common.shared.ViewerStructure.ViewerSIARDBundle;
import com.databasepreservation.common.shared.client.breadcrumb.BreadcrumbItem;
import com.databasepreservation.common.shared.client.common.ContentPanel;
import com.databasepreservation.common.shared.client.common.RightPanel;
import com.databasepreservation.common.shared.client.common.utils.ContentPanelLoader;
import com.databasepreservation.common.shared.client.common.utils.JavascriptUtils;
import com.databasepreservation.common.shared.client.common.utils.RightPanelLoader;
import com.databasepreservation.common.shared.client.common.visualization.browse.*;
import com.databasepreservation.common.shared.client.common.visualization.browse.foreignKey.ForeignKeyPanel;
import com.databasepreservation.common.shared.client.common.visualization.browse.information.DatabaseInformationPanel;
import com.databasepreservation.common.shared.client.common.visualization.browse.ingest.IngestPage;
import com.databasepreservation.common.shared.client.common.visualization.browse.manager.SIARDPanel.SIARDManagerPage;
import com.databasepreservation.common.shared.client.common.visualization.browse.manager.databasePanel.Manage;
import com.databasepreservation.common.shared.client.common.visualization.browse.metadata.MetadataPanel;
import com.databasepreservation.common.shared.client.common.visualization.browse.metadata.MetadataPanelLoad;
import com.databasepreservation.common.shared.client.common.visualization.browse.metadata.SIARDEditMetadataPage;
import com.databasepreservation.common.shared.client.common.visualization.browse.metadata.information.MetadataInformation;
import com.databasepreservation.common.shared.client.common.visualization.browse.metadata.schemas.routines.MetadataRoutinePanel;
import com.databasepreservation.common.shared.client.common.visualization.browse.metadata.schemas.tables.MetadataTablePanel;
import com.databasepreservation.common.shared.client.common.visualization.browse.metadata.schemas.views.MetadataViewPanel;
import com.databasepreservation.common.shared.client.common.visualization.browse.metadata.users.MetadataUsersPanel;
import com.databasepreservation.common.shared.client.common.visualization.browse.table.TablePanel;
import com.databasepreservation.common.shared.client.common.visualization.browse.table.TableSavedSearchEditPanel;
import com.databasepreservation.common.shared.client.common.visualization.browse.table.TableSavedSearchPanel;
import com.databasepreservation.common.shared.client.common.visualization.browse.technicalInformation.ReportPanel;
import com.databasepreservation.common.shared.client.common.visualization.browse.technicalInformation.RoutinesPanel;
import com.databasepreservation.common.shared.client.common.visualization.browse.technicalInformation.UsersPanel;
import com.databasepreservation.common.shared.client.common.visualization.browse.validate.ValidatorPage;
import com.databasepreservation.common.shared.client.tools.FontAwesomeIconManager;
import com.databasepreservation.common.shared.client.tools.HistoryManager;
import com.databasepreservation.common.shared.client.widgets.wcag.AccessibleFocusPanel;
import com.databasepreservation.server.client.browse.HomePanel;
import com.databasepreservation.server.client.browse.LoginPanel;
import com.databasepreservation.server.client.browse.UploadPanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import config.i18n.client.ClientMessages;

/**
 * @author Bruno Ferreira <bferreira@keep.pt>
 */
public class MainPanel extends Composite {
  private static final ClientMessages messages = GWT.create(ClientMessages.class);

  // databaseUUID, databaseName
  private static Map<String, String> databaseNames = new HashMap<>();

  interface MainPanelUiBinder extends UiBinder<Widget, MainPanel> {
  }

  private static MainPanelUiBinder binder = GWT.create(MainPanelUiBinder.class);

  @UiField
  SimplePanel contentPanel;

  @UiField
  AccessibleFocusPanel homeLinkArea;

  @UiField
  SimplePanel bannerLogo;

  MainPanel() {
    initWidget(binder.createAndBindUi(this));
    reSetHeader();
  }

  /*
   * History change handling
   *
   * (switching to another page = using a different RightPanel)
   * ____________________________________________________________________________________________________________________
   */
  public void setContent(RightPanelLoader rightPanelLoader) {
    setContent(null, rightPanelLoader);
  }

  public void setContent(ContentPanelLoader panel) {
    setContent(null, panel);
  }

  private void setContent(String databaseUUID, RightPanelLoader rightPanelLoader) {
    GWT.log("setContent, dbuid " + databaseUUID);
    DatabasePanel databasePanel = DatabasePanel.getInstance(databaseUUID, true);
    databasePanel.setTopLevelPanelCSS("browseContent wrapper skip_padding server");
    contentPanel.setWidget(databasePanel);
    JavascriptUtils.scrollToElement(contentPanel.getElement());
    databasePanel.load(rightPanelLoader, "");
  }

  private void setContent(String databaseUUID, ContentPanelLoader panel) {
    GWT.log("LOADER ::: setContent, dbuid " + databaseUUID);
    ContainerPanel containerPanel = ContainerPanel.getInstance(databaseUUID, true);
    containerPanel.setTopLevelPanelCSS("browseContent wrapper skip_padding server");
    contentPanel.setWidget(containerPanel);
    containerPanel.load(panel);
  }

  private void setMetadataRightPanelContent(String databaseUUID, String sidebarSelected,
                                            MetadataPanelLoad rightPanelLoader) {
    SIARDEditMetadataPage instance = SIARDEditMetadataPage.getInstance(databaseUUID);
    instance.setTopLevelPanelCSS("browseContent wrapper skip_padding server");
    contentPanel.setWidget(instance);
    instance.load(rightPanelLoader, sidebarSelected);
  }

  public void onHistoryChanged(String token) {
    List<String> currentHistoryPath = HistoryManager.getCurrentHistoryPath();
    List<BreadcrumbItem> breadcrumbItemList = new ArrayList<>();

    reSetHeader(currentHistoryPath);
    if (currentHistoryPath.isEmpty()) {
      // #
      HistoryManager.gotoHome();

    } else if (HistoryManager.ROUTE_UPLOADS.equals(currentHistoryPath.get(0))) {
      // #uploads
      // #uploads/...

      if (currentHistoryPath.size() == 2) {
        if (currentHistoryPath.get(1).equals(HistoryManager.ROUTE_UPLOADS_NEW)) {
          // #uploads/new
//          setContent(new RightPanelLoader() {
//            @Override
//            public RightPanel load(ViewerDatabase database) {
//              return NewUploadPanel.getInstance();
//            }
//          });
          setContent(new RightPanelLoader() {
            @Override
            public RightPanel load(ViewerDatabase database) {
              return UploadPanel.getInstance();
            }
          });
        } else {
          // #uploads/<uuid>
          String databaseUUID = currentHistoryPath.get(1);
          setContent(databaseUUID, new RightPanelLoader() {
            @Override
            public RightPanel load(ViewerDatabase database) {
              final com.databasepreservation.common.shared.client.common.visualization.browse.UploadPanel instance = com.databasepreservation.common.shared.client.common.visualization.browse.UploadPanel.createInstance(database);
              instance.setTitleText(messages.uploadedSIARD());

              return instance;
            }
          });
        }
      } else {
        HistoryManager.gotoDatabaseList();
      }
    } else if (HistoryManager.ROUTE_HOME.equals(currentHistoryPath.get(0))) {
      // #home
      setContent(new RightPanelLoader() {
        @Override
        public RightPanel load(ViewerDatabase database) {
          return HomePanel.getInstance();
        }
      });

    } else if (HistoryManager.ROUTE_LOGIN.equals(currentHistoryPath.get(0))) {
      // #login
      setContent(new RightPanelLoader() {
        @Override
        public RightPanel load(ViewerDatabase database) {
          return LoginPanel.getInstance();
        }
      });

    } else if (HistoryManager.ROUTE_SIARD_INFO.equals(currentHistoryPath.get(0))) {
      String databaseUUID = currentHistoryPath.get(1);
      setContent(databaseUUID, new ContentPanelLoader() {
        @Override
        public ContentPanel load(ViewerDatabase database) {
          return SIARDManagerPage.getInstance(database);
        }
      });
    } else if (HistoryManager.ROUTE_UPLOAD_SIARD_DATA.equals(currentHistoryPath.get(0))) {
      String databaseUUID = currentHistoryPath.get(1);
      setContent(databaseUUID, new ContentPanelLoader() {
        @Override
        public ContentPanel load(ViewerDatabase database) {
          return IngestPage.getInstance(database);
        }
      });
    } else if (HistoryManager.ROUTE_SIARD_VALIDATOR.equals(currentHistoryPath.get(0))) {
      final String databaseUUID = currentHistoryPath.get(1);
      setContent(databaseUUID, new ContentPanelLoader() {
        @Override
        public ContentPanel load(ViewerDatabase database) {
          return ValidatorPage.getInstance(database);
        }
      });
    } else if (HistoryManager.ROUTE_DATABASE.equals(currentHistoryPath.get(0))) {
      if (currentHistoryPath.size() == 1) {
        // #database
        setContent(new ContentPanelLoader() {
          @Override
          public ContentPanel load(ViewerDatabase database) {
            return Manage.getInstance();
          }
        });

      } else if (currentHistoryPath.size() == 2) {
        // #database/<database_uuid>
        String databaseUUID = currentHistoryPath.get(1);
        setContent(databaseUUID, new RightPanelLoader() {
          @Override
          public RightPanel load(ViewerDatabase database) {
            return DatabaseInformationPanel.getInstance(database);
          }
        });

      } else if (currentHistoryPath.size() == 3
        && currentHistoryPath.get(2).equals(HistoryManager.ROUTE_DATABASE_REPORT)) {
        // #database/<id>/report
        String databaseUUID = currentHistoryPath.get(1);
        setContent(databaseUUID, new RightPanelLoader() {
          @Override
          public RightPanel load(ViewerDatabase database) {
            return ReportPanel.getInstance(database);
          }
        });

      } else if (currentHistoryPath.size() == 3
        && currentHistoryPath.get(2).equals(HistoryManager.ROUTE_DATABASE_USERS)) {
        // #database/<id>/users
        String databaseUUID = currentHistoryPath.get(1);
        setContent(databaseUUID, new RightPanelLoader() {
          @Override
          public RightPanel load(ViewerDatabase database) {
            return UsersPanel.getInstance(database);
          }
        });

      } else if (currentHistoryPath.size() == 3
        && currentHistoryPath.get(2).equals(HistoryManager.ROUTE_DATABASE_SEARCH)) {
        // #database/<id>/search
        String databaseUUID = currentHistoryPath.get(1);
        setContent(databaseUUID, new RightPanelLoader() {
          @Override
          public RightPanel load(ViewerDatabase database) {
            return DatabaseSearchPanel.getInstance(database);
          }
        });

      } else if (currentHistoryPath.size() == 3
              && currentHistoryPath.get(2).equals(HistoryManager.ROUTE_SCHEMA_ROUTINES)) {
        // #database/<database_uuid>/routines
        String databaseUUID = currentHistoryPath.get(1);
        setContent(databaseUUID, new RightPanelLoader() {
          @Override
          public RightPanel load(ViewerDatabase database) {
            return RoutinesPanel.getInstance(database);
          }
        });
      } else {
        // #database/...
        handleErrorPath(currentHistoryPath);
      }
    } else if (HistoryManager.ROUTE_SCHEMA.equals(currentHistoryPath.get(0))) {
      if (currentHistoryPath.size() == 3) {
        // #schema/<databaseUUID>/<schema_uuid>
        String databaseUUID = currentHistoryPath.get(1);
        final String schema_uuid = currentHistoryPath.get(2);
        setContent(databaseUUID, new RightPanelLoader() {
          @Override
          public RightPanel load(ViewerDatabase database) {
            return SchemaStructurePanel.getInstance(database, schema_uuid);
          }
        });

      } else if (currentHistoryPath.size() == 4) {
        // #schema/<databaseUUID>/<schema_uuid>/structure
        // #schema/<databaseUUID>/<schema_uuid>/routines
        // #schema/<databaseUUID>/<schema_uuid>/triggers
        // #schema/<databaseUUID>/<schema_uuid>/views
        String databaseUUID = currentHistoryPath.get(1);
        final String schema_uuid = currentHistoryPath.get(2);
        String pageSpec = currentHistoryPath.get(3);

        switch (pageSpec) {
          case HistoryManager.ROUTE_SCHEMA_STRUCTURE:
            setContent(databaseUUID, new RightPanelLoader() {
              @Override
              public RightPanel load(ViewerDatabase database) {
                return SchemaStructurePanel.getInstance(database, schema_uuid);
              }
            });
            break;
         /* case HistoryManager.ROUTE_SCHEMA_ROUTINES:
            setContent(databaseUUID, new RightPanelLoader() {
              @Override
              public RightPanel load(ViewerDatabase database) {
                return SchemaRoutinesPanel.getInstance(database, schema_uuid);
              }
            });
            break;*/
          /*
           * case HistoryManager.ROUTE_SCHEMA_TRIGGERS: setContent(databaseUUID, new
           * RightPanelLoader() {
           *
           * @Override public RightPanel load(ViewerDatabase database) { return
           * SchemaTriggersPanel.getInstance(database, schema_uuid); } }); break; case
           * HistoryManager.ROUTE_SCHEMA_VIEWS: setContent(databaseUUID, new
           * RightPanelLoader() {
           *
           * @Override public RightPanel load(ViewerDatabase database) { return
           * SchemaViewsPanel.getInstance(database, schema_uuid); } }); break;
           */
          /*
           * case HistoryManager.ROUTE_SCHEMA_CHECK_CONSTRAINTS: setContent(databaseUUID,
           * new RightPanelLoader() {
           *
           * @Override public RightPanel load(ViewerDatabase database) { return
           * TableCheckConstraintsPanel.getInstance(database, schema_uuid); } }); break;
           */
          /*case HistoryManager.ROUTE_SCHEMA_DATA:
            setContent(databaseUUID, new RightPanelLoader() {
              @Override
              public RightPanel load(ViewerDatabase database) {
                return SchemaDataPanel.getInstance(database, schema_uuid);
              }
            });
            break;*/
          default:
            // #schema/<databaseUUID>/<schema_uuid>/*invalid-page*
            handleErrorPath(currentHistoryPath);
        }

      } else {
        // #schema/...
        handleErrorPath(currentHistoryPath);

      }
    } else if (HistoryManager.ROUTE_TABLE.equals(currentHistoryPath.get(0))) {
      if (currentHistoryPath.size() == 3) {
        // #table/<databaseUUID>/<tableUUID>
        String databaseUUID = currentHistoryPath.get(1);
        final String tableUUID = currentHistoryPath.get(2);
        setContent(databaseUUID, new RightPanelLoader() {
          @Override
          public RightPanel load(ViewerDatabase database) {
            return TablePanel.getInstance(database, tableUUID, currentHistoryPath.get(0));
          }
        });

      } else if (currentHistoryPath.size() == 4) {
        // #table/<databaseUUID>/<tableUUID>/<searchInfoJSON>
        String databaseUUID = currentHistoryPath.get(1);
        final String tableUUID = currentHistoryPath.get(2);
        final String searchInfo = currentHistoryPath.get(3);
        setContent(databaseUUID, new RightPanelLoader() {
          @Override
          public RightPanel load(ViewerDatabase database) {
            return TablePanel.getInstance(database, tableUUID, searchInfo);
          }
        });

      } else {
        // #table/...
        handleErrorPath(currentHistoryPath);

      }
    } else if (HistoryManager.ROUTE_RECORD.equals(currentHistoryPath.get(0))) {
      if (currentHistoryPath.size() == 4) {
        // #record/<databaseUUID>/<tableUUID>/<recordUUID>
        String databaseUUID = currentHistoryPath.get(1);
        final String tableUUID = currentHistoryPath.get(2);
        final String recordUUID = currentHistoryPath.get(3);
        setContent(databaseUUID, new RightPanelLoader() {
          @Override
          public RightPanel load(ViewerDatabase database) {
            return RowPanel.createInstance(database, tableUUID, recordUUID);
          }
        });

      } else {
        // #record/...
        handleErrorPath(currentHistoryPath);

      }
    } else if (HistoryManager.ROUTE_REFERENCES.equals(currentHistoryPath.get(0))) {
      if (currentHistoryPath.size() == 5) {
        // #references/<databaseUUID>/<tableUUID>/<recordUUID>/<columnIndex>
        String databaseUUID = currentHistoryPath.get(1);
        final String tableUUID = currentHistoryPath.get(2);
        final String recordUUID = currentHistoryPath.get(3);
        final String columnIndex = currentHistoryPath.get(4);
        setContent(databaseUUID, new RightPanelLoader() {
          @Override
          public RightPanel load(ViewerDatabase database) {
            return ReferencesPanel.getInstance(database, tableUUID, recordUUID, columnIndex);
          }
        });

      } else {
        // #references/...
        handleErrorPath(currentHistoryPath);

      }
    } else if (HistoryManager.ROUTE_FOREIGN_KEY.equals(currentHistoryPath.get(0))) {
      if (currentHistoryPath.size() >= 5) {
        // #foreignkey/<databaseUUID>/<tableUUID>/<col1>/<val1>/<col2>/<val2>/<colN>/<valN>/...
        // minimum: #foreignkey/<databaseUUID>/<tableUUID>/<col1>/<val1>
        final String databaseUUID = currentHistoryPath.get(1);
        final String tableUUID = currentHistoryPath.get(2);
        final List<String> columnsAndValues = currentHistoryPath.subList(3, currentHistoryPath.size());
        setContent(databaseUUID, new RightPanelLoader() {
          @Override
          public RightPanel load(ViewerDatabase database) {
            return ForeignKeyPanel.createInstance(database, tableUUID, columnsAndValues);
          }
        });

      } else {
        handleErrorPath(currentHistoryPath);
      }
    } else if (HistoryManager.ROUTE_SAVED_SEARCHES.equals(currentHistoryPath.get(0))) {
      if (currentHistoryPath.size() == 2) {
        // #searches/<databaseUUID>
        final String databaseUUID = currentHistoryPath.get(1);
        setContent(databaseUUID, new RightPanelLoader() {
          @Override
          public RightPanel load(ViewerDatabase database) {
            return DatabaseSearchesPanel.createInstance(database);
          }
        });

      } else if (currentHistoryPath.size() == 3) {
        // #searches/<databaseUUID>/<searchUUID>
        final String databaseUUID = currentHistoryPath.get(1);
        final String searchUUID = currentHistoryPath.get(2);
        setContent(databaseUUID, new RightPanelLoader() {
          @Override
          public RightPanel load(ViewerDatabase database) {
            return TableSavedSearchPanel.createInstance(database, searchUUID);
          }
        });

      } else if (currentHistoryPath.size() == 4
        && HistoryManager.ROUTE_SAVED_SEARCHES_EDIT.equals(currentHistoryPath.get(3))) {
        // #searches/<databaseUUID>/<searchUUID>/edit
        final String databaseUUID = currentHistoryPath.get(1);
        final String searchUUID = currentHistoryPath.get(2);
        setContent(databaseUUID, new RightPanelLoader() {
          @Override
          public RightPanel load(ViewerDatabase database) {
            return TableSavedSearchEditPanel.createInstance(database, searchUUID);
          }
        });

      } else {
        handleErrorPath(currentHistoryPath);
      }
    }  else if (HistoryManager.ROUTE_SIARD_EDIT_METADATA.equals(currentHistoryPath.get(0))) {
      String databaseUUID =  currentHistoryPath.get(1);
      if (currentHistoryPath.size() == 2) {
        setMetadataRightPanelContent(databaseUUID, databaseUUID, new MetadataPanelLoad() {
          @Override
          public MetadataPanel load(ViewerDatabase database, ViewerSIARDBundle SIARDbundle) {
            return MetadataInformation.getInstance(database, SIARDbundle);
          }
        });
      } else if (currentHistoryPath.size() == 3) {
        final String user = currentHistoryPath.get(2);
        setMetadataRightPanelContent(databaseUUID, user, new MetadataPanelLoad() {
          @Override
          public MetadataPanel load(ViewerDatabase database, ViewerSIARDBundle SIARDbundle) {
            return MetadataUsersPanel.getInstance(database, SIARDbundle);
          }
        });
      }
    } else if (HistoryManager.ROUTE_DESKTOP_METADATA_TABLE.equals(currentHistoryPath.get(0))) {
      if (currentHistoryPath.size() == 3) {
        String databaseUUID = currentHistoryPath.get(1);
        final String tableUUID = currentHistoryPath.get(2);

        setMetadataRightPanelContent(databaseUUID, tableUUID, new MetadataPanelLoad() {
          @Override

          public MetadataPanel load(ViewerDatabase database, ViewerSIARDBundle SIARDbundle) {
            return MetadataTablePanel.getInstance(database, SIARDbundle, tableUUID);
          }
        });
      }
    } else if (HistoryManager.ROUTE_DESKTOP_METADATA_VIEW.equals(currentHistoryPath.get(0))) {
      if (currentHistoryPath.size() == 4) {
        String databaseUUID = currentHistoryPath.get(1);
        final String schemaUUID = currentHistoryPath.get(2);
        final String viewUUID = currentHistoryPath.get(3);

        setMetadataRightPanelContent(databaseUUID, viewUUID, new MetadataPanelLoad() {
          @Override
          public MetadataPanel load(ViewerDatabase database, ViewerSIARDBundle SIARDbundle) {
            return MetadataViewPanel.getInstance(database, SIARDbundle, schemaUUID, viewUUID);
          }
        });
      }
    } else if (HistoryManager.ROUTE_DESKTOP_METADATA_ROUTINE.equals(currentHistoryPath.get(0))) {
      if (currentHistoryPath.size() == 4) {
        String databaseUUID = currentHistoryPath.get(1);
        final String schemaUUID = currentHistoryPath.get(2);
        final String routineUUID = currentHistoryPath.get(3);

        setMetadataRightPanelContent(databaseUUID, routineUUID, new MetadataPanelLoad() {
          @Override
          public MetadataPanel load(ViewerDatabase database, ViewerSIARDBundle SIARDbundle) {
            return MetadataRoutinePanel.getInstance(database, SIARDbundle, schemaUUID, routineUUID);
          }
        });
      }
    }else {
      handleErrorPath(currentHistoryPath);
    }
  }

  private void handleErrorPath(List<String> currentHistoryPath) {
    if (currentHistoryPath.size() >= 2) {
      String databaseUUID = currentHistoryPath.get(1);
      reSetHeader(databaseUUID);
      HistoryManager.gotoDatabase(databaseUUID);
    } else {
      HistoryManager.gotoHome();
    }
  }

  /*
   * Header related methods
   * ____________________________________________________________________________________________________________________
   */
  private void reSetHeader() {
    HTMLPanel headerText = new HTMLPanel(SafeHtmlUtils.fromSafeConstant(
      FontAwesomeIconManager.getTag(FontAwesomeIconManager.DATABASE) + " Database Visualization Toolkit"));
    headerText.addStyleName("homeText");

    // HTMLPanel headerContent = new HTMLPanel(
    // SafeHtmlUtils
    // .fromSafeConstant("<img title=\"Database Visualization Toolkit\"
    // class=\"homeLogo\" src=\"/img/dbptk_logo.png\"/>\n"
    // + "<div class=\"homeText\">DBVTK</div>"));

    bannerLogo.setWidget(headerText);

    homeLinkArea.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        HistoryManager.gotoHome();
      }
    });

    homeLinkArea.setTitle(messages.goHome());
  }

  private void reSetHeader(final String databaseUUID, String databaseName) {
    HTMLPanel headerText = new HTMLPanel(SafeHtmlUtils.fromSafeConstant(
      FontAwesomeIconManager.getTag(FontAwesomeIconManager.DATABASE) + " " + SafeHtmlUtils.htmlEscape(databaseName)));
    headerText.addStyleName("homeText");
    bannerLogo.setWidget(headerText);

    homeLinkArea.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        HistoryManager.gotoDatabase(databaseUUID);
      }
    });

    homeLinkArea.setTitle(messages.goToDatabaseInformation());
  }

  private void reSetHeader(final String databaseUUID) {
    if (databaseUUID == null) {
      reSetHeader();
    } else if (databaseNames.containsKey(databaseUUID)) {
      reSetHeader(databaseUUID, databaseNames.get(databaseUUID));
    } else {
      reSetHeader();

      BrowserService.Util.getInstance().retrieve(databaseUUID, ViewerDatabase.class.getName(), databaseUUID,
        new AsyncCallback<IsIndexed>() {
          @Override
          public void onSuccess(IsIndexed result) {
            ViewerDatabase database = (ViewerDatabase) result;
            String databaseName = database.getMetadata().getName();

            databaseNames.put(databaseUUID, databaseName);
            reSetHeader(databaseUUID, databaseName);
          }

          @Override
          public void onFailure(Throwable caught) {
            // do nothing. header has been set correctly
          }
        });
    }
  }

  private void reSetHeader(List<String> currentHistoryPath) {
    if (currentHistoryPath.size() >= 2) {
      reSetHeader(currentHistoryPath.get(1));
    } else {
      reSetHeader((String) null);
    }
  }
}
