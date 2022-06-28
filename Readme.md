# Catcher Base [![test](https://github.com/comtihon/catcher_base/actions/workflows/test.yml/badge.svg?branch=master)](https://github.com/comtihon/catcher_base/actions/workflows/test.yml)

Service for carrying end-to-end tests with [Catcher](https://github.com/comtihon/catcher).  
Requires Postgres.

## Run

### Local
To run it locally or via the IDE you must ensure you have python installed, as this is a requirement for the catcher-core.
On the first run application will detect python and install catcher itself if it is not installed.
It is recommended to use `venv` _(recommended)_ or `conda` instead of system python when running locally.

If you are catcher_base via IDE with `conda` executor you and you set it up as __native_executable__ - you must add it's PATH to the IDE's run config.

### Docker
Application already dockerized. You can run it standalone or via docker-compose. It can be configured via env vars or by 
mounting your application configuration to `/usr/app/config`.

## Configuration
Service can be configured via environmental variables, java args or by placing your own `application.yml`/`application.properties` 
to the default Spring's search path.

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
__ADMIN_USER__ - admin user email/name. Will be created on startup, if there are no other admin users in the database.  
__ADMIN_PASS__ - admin user password.  
__JWT_PRIV__ - set your own secret key for jwt token encryption (highly recommended).

### Executors
#### Local
Local executor run tests on the same machine with Base is running.  
You can configure it:  
__MAX_POOL_SIZE__ - executor's max pool size (default is 2)    
__CORE_POOL_SIZE__ - executor's core pool size (default is 1)  
#### Kubernetes
K8s executor runs tests in spawned by demand k8s agents. To select it over the local executor
you need to wait till I implement it :/

### Tools
Python 3.7+ is needed for tests to run, as well as catcher itself. You can specify different python
providers, such as `Conda`, `Venv` or python used in the `system`.  
For `Conda` specify `catcher.system.conda_name`.  
For `Venv` specify `catcher.system.venv_name`.  
For `system` specify both `catcher.system.native_executable` and `catcher.system.pip_executable`.  
Remember, that only one of them should be specified.  
Catcher will be installed automatically on Base startup.  
<TODO> switch to env vars!