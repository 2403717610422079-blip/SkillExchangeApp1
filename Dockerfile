FROM tomcat:9.0.122-jdk21-temurin

COPY src/main/webapp /usr/local/tomcat/webapps/SkillExchangeApp1

COPY src/main/java /app/src/main/java

RUN find /app/src/main/java -name "*.java" > /tmp/sources.txt && \
    javac -cp "/usr/local/tomcat/lib/servlet-api.jar:/usr/local/tomcat/webapps/SkillExchangeApp1/WEB-INF/lib/mysql-connector-j-26.7.0.jar" \
    -d /usr/local/tomcat/webapps/SkillExchangeApp1/WEB-INF/classes \
    @/tmp/sources.txt

EXPOSE 8080