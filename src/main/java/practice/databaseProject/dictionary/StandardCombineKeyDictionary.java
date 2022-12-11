package practice.databaseProject.dictionary;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StandardCombineKeyDictionary {
    Set<String> dict;
    public void init() {
        dict = new HashSet<>();
        dict.add("주민등록번호");
        dict.add("전화번호");
        dict.add("차량번호");
        dict.add("이메일 주소");
        dict.add("IP");
    }
    public List<String> values() {
        return Arrays.asList(dict.toArray(String[]::new));
    }
    public void add(String attribute) {
        dict.add(attribute);
    }
}
