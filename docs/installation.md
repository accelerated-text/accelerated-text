# Prerequisites

Accelerated Text can be run on Windows, Mac OS X and Linux, but before you continue, make sure the following are installed on your system:

* [Git](https://git-scm.com/)
* [Make](https://www.gnu.org/software/make/)
* [Docker-compose](https://docs.docker.com/compose/install/)

Also, basic knowledge on how to use a terminal and command line is required.

# Downloading

Accelerated Text is an open source project on GitHub. To clone the project, paste the following in your terminal:

```
git clone https://github.com/tokenmill/accelerated-text.git
```

# Configuration

Refer to *docker-compose.yml* at the project root.

To change languages displayed in Accelerated Text UI, change the `ENABLED_LANGUAGES` environment variable.

Supported languages are:

* English
* Estonian
* German
* Latvian
* Russian
* Spanish

# Launch

Navigate to project root and type in your terminal:

```
make run-dev-env
```

After build is complete and environment is running, you should see:

```
INFO  a.server - Running server on: localhost:3001. Press Ctrl+C to stop
```

At this point, Accelerated Text will be accessible on your browser:

* **Document Plan Editor** is the main window at _http://localhost:8080_ 
* **AMR Editor** is mid level Abstract Meaning Representation editor at _http://localhost:8080/amr/_ 
* **DLG Editor** is the lowest level dealing with Domain Language Grammar at _http://localhost:8080/dlg/_

If you are new to Accelerated Text, continue to [First Steps](first-steps.md) for an introduction of basic workflow.
