package com.databasepreservation.main.desktop.shared.models.wizardParameters;

import com.databasepreservation.main.desktop.shared.models.JDBCParameters;
import com.databasepreservation.main.desktop.shared.models.SSHConfiguration;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Miguel Guimarães <mguimaraes@keep.pt>
 */
public class ConnectionParameters implements Serializable {

  private String moduleName;
  private JDBCParameters connection;
  private boolean ssh = false;
  private SSHConfiguration sshConfiguration;


  public ConnectionParameters() {
  }

  public ConnectionParameters(String moduleName, JDBCParameters connection, SSHConfiguration sshConfiguration) {
    this.moduleName = moduleName;
    this.connection = connection;
    this.sshConfiguration = sshConfiguration;
  }

  public String getModuleName() {
    return moduleName;
  }

  public void setModuleName(String moduleName) {
    this.moduleName = moduleName;
  }

  public JDBCParameters getJDBCConnectionParameters() {
    return connection;
  }

  public void setJDBCConnectionParameters(JDBCParameters connection) {
    this.connection = connection;
  }

  public SSHConfiguration getSSHConfiguration() {
    return sshConfiguration;
  }

  public void setSSHConfiguration(SSHConfiguration sshConfiguration) {
    this.sshConfiguration = sshConfiguration;
  }

  public boolean doSSH() {
    return ssh;
  }

  public void doSSH(boolean value) {
    this.ssh = value;
  }

  public String getURLConnection() {
    StringBuilder sb = new StringBuilder();

    sb.append("jdbc:").append(moduleName).append("://");
    for (Map.Entry<String, String> entry : connection.getConnection().entrySet()) {
      sb.append(entry.getKey()).append("=").append(entry.getValue());
    }

    return sb.toString();
  }
}
