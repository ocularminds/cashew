package cashew;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import javax.sql.DataSource;
import java.util.Properties;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Primary;

/**
 * This configuration class configures the persistence layer of our example
 * application and enables annotation driven transaction management.
 * <p>
 * This configuration is put to a single class because this way we can write
 * integration tests for our persistence layer by using the configuration used
 * by the application.
 *
 * In other words, we can ensure that the persistence layer of the application
 * works as expected.
 *
 * @author Jejelowo B. Festus
 */
@Configuration
@EnableAspectJAutoProxy
@ComponentScan("cashew.service")
@EnableJpaRepositories(basePackages = {"cashew.repository"})
@PropertySource(value = {"file:config/application.properties"})
public class Db {

    private static final String[] ENTITY_PACKAGES = {"cashew.domain"};
    private static final String DB_DRIVER = "app.db.class";
    private static final String DB_PASS = "app.db.pass";
    private static final String DB_NAME = "app.db.name";
    private static final String DB_ADDR = "app.db.addr";
    private static final String DB_USER = "app.db.user";
    private static final String DB_PORT = "app.db.port";
    private static final String DB_URL  = "app.db.url";
    private static final String DB_INST = "app.db.inst";
    private static final String HIBERNATE_DIALECT = "hibernate.dialect";
    private static final String HIBERNATE_FORMAT_SQL = "hibernate.format_sql";
    private static final String HIBERNATE_HBM2DDL_AUTO = "hibernate.hbm2ddl.auto";
    private static final String HIBERNATE_NAMING_STRATEGY = "hibernate.ejb.naming_strategy";
    private static final String HIBERNATE_SHOW_SQL = "hibernate.show_sql";

    @Autowired
    private Environment env;

    public Environment getEnv(){
        return this.env;
    }

    public void setEnv(Environment e){
        this.env = e;
    }

    /**
     * Creates and configures the HikariCP data source bean.
     *
     * @param env The run time environment of our application.
     * @return
     */
    @Bean(destroyMethod = "close")
    DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        String datasource = env.getRequiredProperty(DB_DRIVER);
        config.addDataSourceProperty("user", env.getRequiredProperty(DB_USER));
        config.addDataSourceProperty("password", env.getRequiredProperty(DB_PASS));
        if (datasource.contains("h2")) {
            config.setDataSourceClassName(env.getRequiredProperty(DB_DRIVER));
            config.setConnectionTestQuery("VALUES 1");
            config.addDataSourceProperty("URL", env.getRequiredProperty(DB_URL));
        } else {
            String user = props(env, DB_USER);
            config.setDriverClassName(props(env, DB_DRIVER));
            String environ = System.getenv("DATABASE_URL");
            System.out.println("datasource url: " + environ);
            if (user == null || user.trim().isEmpty()) {
                String[] envs = props(env, DB_URL).split(":");
                user = envs[1].substring(2, envs[1].length());
                String pass = envs[2].substring(0, envs[2].indexOf("@"));
                String server = envs[2].substring(envs[2].indexOf("@") + 1);
                String dbname = envs[3].substring(5, envs[3].length());
                String S = "jdbc:postgresql://%s/%s?user=%s&password=%s";
                String url = String.format(S, server, dbname, user, pass);
                config.setJdbcUrl(url);
                config.addDataSourceProperty("user", user);
                config.addDataSourceProperty("serverName", server);
                config.addDataSourceProperty("password", pass);
                config.addDataSourceProperty("port", envs[3].substring(0, 4));
                config.addDataSourceProperty("databaseName", dbname);
            } else {
                config.addDataSourceProperty("serverName", props(env, DB_ADDR));
                if (props(env, DB_DRIVER).contains("microsoft")) {
                    config.addDataSourceProperty("portNumber", props(env, DB_PORT));
                } else {
                    config.addDataSourceProperty("port", props(env, DB_PORT));
                }
                if (props(env, DB_INST) != null) {
                    config.addDataSourceProperty("instanceName", props(env, DB_INST));
                }
                config.addDataSourceProperty("databaseName", props(env, DB_NAME));
                config.addDataSourceProperty("user", props(env, DB_USER));
                config.addDataSourceProperty("password", props(env, DB_PASS));
            }
        }

        config.setMaximumPoolSize(Math.max(Runtime.getRuntime().availableProcessors(), 4));
        return new HikariDataSource(config);
    }

    /**
     * Creates the bean that creates the JPA entity manager factory.
     *
     * Configures the used database dialect. This allows Hibernate to create SQL
     * that is optimized for the used database.
     *
     * Specifies the action that is invoked to the database when the Hibernate
     * SessionFactory is created or closed.
     *
     * Configures the naming strategy that is used when Hibernate creates new
     * database objects and schema elements.
     *
     * @param dataSource The data source that provides the database connections.
     * @param env The runtime environment of our application.
     * @return {@code LocalContainerEntityManagerFactoryBean}
     */
    @Primary
    @Bean(name = "entityManagerFactory")
    LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource,
            Environment env) {
        Properties properties = new Properties();
        properties.put(HIBERNATE_DIALECT, env.getRequiredProperty(HIBERNATE_DIALECT));
        properties.put(HIBERNATE_HBM2DDL_AUTO, env.getProperty(HIBERNATE_HBM2DDL_AUTO));
        properties.put(HIBERNATE_NAMING_STRATEGY,
                env.getRequiredProperty(HIBERNATE_NAMING_STRATEGY));
        properties.put(HIBERNATE_SHOW_SQL, env.getRequiredProperty(HIBERNATE_SHOW_SQL));
        properties.put(HIBERNATE_FORMAT_SQL, env.getRequiredProperty(HIBERNATE_FORMAT_SQL));
        properties.put("hibernate.jdbc.batch_size", 250);
        properties.put("spring.jpa.hibernate.temp.use_jdbc_metadata_defaults", "false");
        properties.put("spring.jpa.hibernate.ddl-auto", "none");
        properties.put("hibernate.order_inserts", true);
        properties.put("hibernate.order_updates", true);

        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setJpaProperties(properties);
        emf.setDataSource(dataSource);
        emf.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        emf.setPackagesToScan(ENTITY_PACKAGES);
        emf.setPersistenceUnitName("PGPU"); // <- giving 'default' as name
        emf.afterPropertiesSet();
        return emf;
    }

    public static String nextId(){
      String s1 = Long.toString((long) ((Math.random() * 10000) + 10000));
      String s2 = Long.toString(System.currentTimeMillis());
      return new StringBuilder(s2).reverse().substring(0, 5) + s1;
    }
  
    private String props(Environment env, String key) {
      return env.getRequiredProperty(key);
    }
}
