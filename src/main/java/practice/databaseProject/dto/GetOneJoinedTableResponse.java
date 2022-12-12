package practice.databaseProject.dto;

import lombok.Data;

@Data
public class GetOneJoinedTableResponse {

    private JoinResult joinResult;


    public GetOneJoinedTableResponse(JoinResult joinResult) {
        this.joinResult = joinResult;
    }

}
