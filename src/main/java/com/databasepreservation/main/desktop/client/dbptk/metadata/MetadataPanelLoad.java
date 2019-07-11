package com.databasepreservation.main.desktop.client.dbptk.metadata;

import com.databasepreservation.main.common.shared.ViewerStructure.ViewerDatabase;
import com.databasepreservation.main.common.shared.ViewerStructure.ViewerSIARDBundle;

import java.util.Map;

public abstract class MetadataPanelLoad {
    public abstract MetadataPanel load(ViewerDatabase database, ViewerSIARDBundle SIARDbundle);
}