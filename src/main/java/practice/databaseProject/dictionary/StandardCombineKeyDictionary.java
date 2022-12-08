package practice.databaseProject.dictionary;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class StandardCombineKeyDictionary {
    Set<String> dict;
    public void init() {
        dict = new HashSet<>();
        dict.addAll(List.of(new String[]{"주민등록번호", "전화번호", "차량번호", "이메일 주소", "IP"}));
    }
    public List<String> values() {
        return Arrays.asList(dict.toArray(String[]::new));
    }
    public void add(String attribute) {
        dict.add(attribute);
    }
    public void addAll(String[] attributes) {
        for (String attribute : attributes) {
            dict.add(attribute);
        }
    }
}
