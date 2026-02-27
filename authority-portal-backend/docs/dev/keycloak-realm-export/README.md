## About This Script

### What does this script do?

This Shell script will replace the [realm.json](../../../authority-portal-quarkus/src/main/resources/realm.json) 
from the running Quarkus Devservice Keycloak Container, exporting the realm "authority-portal"

### How to execute this script?

To run this script, execute the following command from the backend project's root.

 ```shell
# Bash or WSL
./docs/dev/keycloak-realm-export/export-realm.sh

# Git Bash
MSYS_NO_PATHCONV=1 ./docs/dev/keycloak-realm-export/export-realm.sh
```

After the script has run, the exported [realm.json](../../../authority-portal-quarkus/src/main/resources/realm.json) may contain unrecognized fields which leads to an import error on next startup.
Quarkus indicates those with an error message on startup like this:

```
2024-12-20 10:08:44,484 ERROR [io.qua.oid.dep.dev.key.KeycloakDevServicesProcessor] (build-57) Realm realm.dev.json resource can not be opened: Unrecognized field "firstBrokerLoginFlow" (class org.keycloak.representations.idm.RealmRepresentation), not marked as ignorable [...]
```

To fix this, delete the lines with unrecognized fields from the exported [realm.json](../../../authority-portal-quarkus/src/main/resources/realm.json) file.

### Why do we need this script?

Manually keeping track of the Realm Config JSON is very tedious. Editing the realm through Keycloak remains the easiest way
