package practice.databaseProject.dto;

import lombok.Data;

import java.util.List;

@Data
public class GetMultipleJoinedTableResponse {

    private List<JoinResult> joinResults;


    public GetMultipleJoinedTableResponse(List<JoinResult> joinResults) {
        this.joinResults = joinResults;
    }

}
