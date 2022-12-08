package practice.databaseProject.dto;

import lombok.Data;

@Data
public class DBConnectionRequest {
    private String host;
    private String port;
    private String database;
    private String user;
    private String password;

}
