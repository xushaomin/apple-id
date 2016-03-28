package com.appleframework.id;

public class QndJdbcIdGenerator {

    public static void main(String[] args) {
        JdbcIdGenerator idGenerator = JdbcIdGenerator.getInstance("com.mysql.jdbc.Driver",
                "jdbc:mysql://localhost:3306/temp", "test", "test", "id_server");
        System.out.println(idGenerator.currentId("default"));
        System.out.println(idGenerator.nextId("default"));
        System.out.println(idGenerator.currentId("default"));
        System.out.println(idGenerator.nextId("default"));
        System.out.println(idGenerator.currentId("default"));
    }
}
