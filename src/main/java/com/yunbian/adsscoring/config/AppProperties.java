package com.yunbian.adsscoring.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "yunbian")
public class AppProperties {

    private String env;
    private String logLevel;
    private Scoring scoring = new Scoring();
    private Database database = new Database();

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public String getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    public Scoring getScoring() {
        return scoring;
    }

    public void setScoring(Scoring scoring) {
        this.scoring = scoring;
    }

    public Database getDatabase() {
        return database;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    public static class Scoring {
        private Long defaultSid;
        private String defaultLogDate;

        public Long getDefaultSid() {
            return defaultSid;
        }

        public void setDefaultSid(Long defaultSid) {
            this.defaultSid = defaultSid;
        }

        public String getDefaultLogDate() {
            return defaultLogDate;
        }

        public void setDefaultLogDate(String defaultLogDate) {
            this.defaultLogDate = defaultLogDate;
        }
    }

    public static class Database {
        private String host;
        private Integer port;
        private String name;
        private String user;
        private String password;
        private String schema;

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public Integer getPort() {
            return port;
        }

        public void setPort(Integer port) {
            this.port = port;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getSchema() {
            return schema;
        }

        public void setSchema(String schema) {
            this.schema = schema;
        }
    }
}