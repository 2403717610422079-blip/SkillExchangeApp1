FROM tomcat:9.0.122-jdk21-temurin

# Clean out default Tomcat applications so root context opens our application
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy webapp resources into ROOT webapp
COPY src/main/webapp /usr/local/tomcat/webapps/ROOT

# Copy Java source code
COPY src/main/java /app/src/main/java

# Compile Java source code into ROOT/WEB-INF/classes
RUN mkdir -p /usr/local/tomcat/webapps/ROOT/WEB-INF/classes && \
    find /app/src/main/java -name "*.java" > /tmp/sources.txt && \
    javac -cp "/usr/local/tomcat/lib/servlet-api.jar:/usr/local/tomcat/webapps/ROOT/WEB-INF/lib/*" \
    -d /usr/local/tomcat/webapps/ROOT/WEB-INF/classes \
    @/tmp/sources.txt

# Duplicate ROOT to SkillExchangeApp1 so both root "/" and "/SkillExchangeApp1" context paths work seamlessly
RUN cp -r /usr/local/tomcat/webapps/ROOT /usr/local/tomcat/webapps/SkillExchangeApp1

EXPOSE 8080

# Configure dynamic port binding for Railway ($PORT) while preserving default port 8080
CMD ["sh", "-c", "if [ -n \"$PORT\" ]; then sed -i \"s/port=\\\"8080\\\"/port=\\\"$PORT\\\"/g\" /usr/local/tomcat/conf/server.xml; fi && catalina.sh run"]