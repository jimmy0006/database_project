package practice.databaseProject.dto;

import lombok.Data;

import java.util.List;

@Data
public class GetJoinedTableResponse {

    private List<JoinResult> joinResults;


    public GetJoinedTableResponse(List<JoinResult> joinResults) {
        this.joinResults = joinResults;
    }

}
