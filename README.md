lightweight_jboss
=================

# Deploy

```
cp sample.war JBOSS_HOME/standalone/deployments/
touch JBOSS_HOME/standalone/deployments/sample.war.dodeploy
```

#Access

```
curl http://localhost:8080/sample/hello.jsp
curl http://localhost:8080/sample/sample.jsp
```
