# Catcher Base

Service for carrying end-to-end tests with [Catcher](https://github.com/comtihon/catcher).  
Requires Postgres and Catcher to be installed locally.  

## Configuration
Service can be configured via environmental variables (although you can use java args).

### Database
__DB_HOST__ - database host. `Localhost` is default.  
__DB_PORT__ - database port. `5432` is default.  
__DB_NAME__ - database name. `catcher` is default.  
__DB_USER__ - database user. `test` is default.  
__DB_PASS__ - database password. `test` is default.  

### Service
__LOCAL_DIR__ - local directory for projects storage. `./` is default. Only local filesystem
paths are supported right now.  
__SERVER_PORT__ - which port to listen. `8080` is default. You can also use `-Dserver.port` java arg.  