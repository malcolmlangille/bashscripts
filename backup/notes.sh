java -jar keycloak-0.0.1-SNAPSHOT.jar \
  -Dkeycloak.migration.action=export \
  -Dkeycloak.migration.provider=dir \
  -Dkeycloak.migration.dir=./keycloak-export \
  -Dkeycloak.migration.usersExportStrategy=REALM_FILE

bootRun {
    systemProperties = [
        'keycloak.migration.action': 'export',
        'keycloak.migration.provider': 'dir',
        'keycloak.migration.dir': './export-directory',
        'keycloak.migration.usersExportStrategy': 'REALM_FILE'
    ]
}

spring:
  datasource:
    url: jdbc:h2:file:./data/customdb;DB_CLOSE_ON_EXIT=false;AUTO_SERVER=TRUE
    username: sa
    password:

