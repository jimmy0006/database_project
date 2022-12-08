package practice.databaseProject.dbConnector;

import org.springframework.stereotype.Component;

@Component
public class MariaConfig implements DBConfig {
    public MariaConfig() {}

    @Override
    public String getAddress() {
        return "127.0.0.1:3306/proj";
    }

    @Override
    public String getUsername() {
        return "root";
    }

    @Override
    public String getPassword() {
        return "root";
    }
}
