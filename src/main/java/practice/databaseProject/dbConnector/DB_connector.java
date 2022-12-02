package practice.databaseProject.dbConnector;

import practice.databaseProject.dto.Keys;

import java.sql.*;

public class DB_connector {

    public static void main(String[] args) throws SQLException {
        // DB 접속 객체선언
        Connection conn = null;

        // sql 실행할 객체 선언
        Statement stmt = null;

        // sql 실행결과를 담을 객체 선언
        ResultSet rs = null;

        try {
            // Maria db 드라이버 로드
            Class.forName("org.mariadb.jdbc.Driver");
            // 데이터베이스 접속
            conn = DriverManager.getConnection("jdbc:mariadb://127.0.0.1:3305/test", "root", "1234");

            stmt = conn.createStatement(); // statement 객체생성
            rs = stmt.executeQuery("SELECT * FROM meta_table;"); // 실행결과 객체에 slq문 실행결과를 저장

            System.out.println("id\tname");
            while (rs.next()) {
                System.out.println(rs.getString("id") + "\t  " + rs.getString("table_name"));
            }

        } catch (Exception e) {
            System.out.println(e.toString());
        } finally {
            try {conn.close();} catch (Exception e) {} // DB에 연동된 객체들은 모두 close() 해줘야한다.
            try {stmt.close();} catch (Exception e) {}
            try {rs.close();} catch (Exception e) {}
        } // end finally

        if (conn != null) {
            System.out.println("접속성공");
        }

    }

    public ResultSet query(){
        // DB 접속 객체선언
        Connection conn = null;
        // sql 실행할 객체 선언
        Statement stmt = null;
        // sql 실행결과를 담을 객체 선언
        ResultSet rs = null;
        try {
            // Maria db 드라이버 로드
            Class.forName("org.mariadb.jdbc.Driver");
            // 데이터베이스 접속
            conn = DriverManager.getConnection("jdbc:mariadb://127.0.0.1:3305/test", "root", "1234");

            stmt = conn.createStatement(); // statement 객체생성
            rs = stmt.executeQuery("SELECT * FROM meta_table;"); // 실행결과 객체에 slq문 실행결과를 저장
        } catch (Exception e) {
            System.out.println(e.toString());
        } finally {
            try {conn.close();} catch (Exception e) {} // DB에 연동된 객체들은 모두 close() 해줘야한다.
            try {stmt.close();} catch (Exception e) {}
            try {rs.close();} catch (Exception e) {}
        } // end finally
        return rs;
    }

}