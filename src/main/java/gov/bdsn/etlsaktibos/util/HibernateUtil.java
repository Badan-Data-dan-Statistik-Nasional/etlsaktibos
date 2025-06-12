package gov.bdsn.etlsaktibos.util;

import gov.bdsn.etlsaktibos.entity.Balance;
import gov.bdsn.etlsaktibos.entity.PokBos;
import gov.bdsn.etlsaktibos.entity.PokCombined;
import gov.bdsn.etlsaktibos.entity.PokSakti;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import java.io.IOException;
import java.util.Properties;

public class HibernateUtil {

    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                Configuration configuration = new Configuration();
                Properties properties = new Properties();
                properties.load(HibernateUtil.class.getClassLoader()
                        .getResourceAsStream("hibernate.properties"));
                configuration.setProperties(properties);
                configuration
                        .addAnnotatedClass(PokBos.class)
                        .addAnnotatedClass(PokSakti.class)
                        .addAnnotatedClass(PokCombined.class)
                        .addAnnotatedClass(Balance.class);
                sessionFactory = configuration.buildSessionFactory();

            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sessionFactory;
    }

}