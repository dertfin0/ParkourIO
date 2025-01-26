package ru.dfhub.parkourio.common;

import lombok.Getter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.MySQLDialect;
import ru.dfhub.parkourio.util.Config;

public class Database {

    @Getter
    private static SessionFactory sessionFactory;

    @Getter
    private static Session session;

    public static void init(Class<?>... annotatedClasses) {
        Configuration configuration = new Configuration();
        //Configuration configuration = new Configuration().configure();
        configuration.setProperty("hibernate.connection.username", Config.getConfig().getJSONObject("database").getString("username"));
        configuration.setProperty("hibernate.connection.password", Config.getConfig().getJSONObject("database").getString("password"));
        configuration.setProperty("hibernate.dialect", MySQLDialect.class);
        configuration.setProperty("hibernate.connection.url", Config.getConfig().getJSONObject("database").getString("connection-url"));
        configuration.setProperty("hibernate.connection.driver_class", com.mysql.cj.jdbc.Driver.class);
        for (Class<?> clazz : annotatedClasses) {
            configuration.addAnnotatedClass(clazz);
        }
        sessionFactory = configuration.buildSessionFactory();
        session = sessionFactory.openSession();
    }

    public static Session openNewSession() {
        return sessionFactory.openSession();
    }
}
