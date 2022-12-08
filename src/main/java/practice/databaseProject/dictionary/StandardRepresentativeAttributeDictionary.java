package practice.databaseProject.dictionary;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StandardRepresentativeAttributeDictionary {
    Set<String> dict;

    public void init() {
        dict = new HashSet<>();
        dict.add("학업정보");
        dict.add("금융정보");
        dict.add("회원정보");
        dict.add("건강정보");
    }

    public List<String> values() {
        return Arrays.asList(dict.toArray(String[]::new));
    }
    public void add(String attribute) {
        dict.add(attribute);
    }

}
