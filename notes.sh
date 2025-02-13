java -jar keycloak-0.0.1-SNAPSHOT.jar \
  -Dkeycloak.migration.action=export \
  -Dkeycloak.migration.provider=dir \
  -Dkeycloak.migration.dir=./keycloak-export \
  -Dkeycloak.migration.usersExportStrategy=REALM_FILE
