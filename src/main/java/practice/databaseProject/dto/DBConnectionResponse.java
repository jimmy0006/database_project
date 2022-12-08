package practice.databaseProject.dto;

import lombok.Data;

@Data
public class DBConnectionResponse {
    private boolean connected;

    public DBConnectionResponse(boolean connected) {
        this.connected = connected;
    }
}
