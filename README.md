[![Project status](https://badgen.net/badge/project%20status/stable%20%26%20actively%20maintaned?color=green)](https://github.com/homecentr/docker-ddclient/graphs/commit-activity) [![](https://badgen.net/github/label-issues/homecentr/docker-ddclient/bug?label=open%20bugs&color=green)](https://github.com/homecentr/docker-ddclient/labels/bug) [![](https://badgen.net/github/release/homecentr/docker-ddclient)](https://hub.docker.com/repository/docker/homecentr/ddclient)
[![](https://badgen.net/docker/pulls/homecentr/ddclient)](https://hub.docker.com/repository/docker/homecentr/ddclient) 
[![](https://badgen.net/docker/size/homecentr/ddclient)](https://hub.docker.com/repository/docker/homecentr/ddclient)

![CI/CD on master](https://github.com/homecentr/docker-ddclient/workflows/CI/CD%20on%20master/badge.svg)
![Regular Docker image vulnerability scan](https://github.com/homecentr/docker-ddclient/workflows/Regular%20Docker%20image%20vulnerability%20scan/badge.svg)


# HomeCentr - ddclient
This docker image contains the latest version of the [ddclient](https://github.com/ddclient/ddclient) (the fork on GitHub, not the original SourceForge hosted project) compliant with the HomeCenter docker images standard (S6 overlay, privilege drop etc. The image is as minimal as possible, but contains all Perl dependencies required for ddclient.

## Usage

```yml
version: "3.7"
services:
  ddclient:
    build: .
    image: homecentr/ddclient
    environment:
      CRON_SCHEDULE: "* * * * *"
      DDCLIENT_ARGS: "-verbose -debug"
    volumes:
      - ./example/ddclient.conf:/config/ddclient.conf
```

## Environment variables

| Name | Default value | Description |
|------|---------------|-------------|
| PUID | 7077 | UID of the user ddclient should be running as. The UID must have sufficient rights to read the ddclient.conf file. |
| PGID | 7077 | GID of the user ddclient should be running as. The GID must have sufficient rights to read the ddclient.conf file. |
| CRON_SCHEDULE | 30 * * * * (every half hour) | Sets how often the ddclient will be executed. You can use [Cron expression](https://crontab.guru/) to create custom schedules if you are not familiar with the cron syntax. |
| DDCLIENT_ARGS |  | Additional command line arguments passed to the ddclient. Please note, the config path is already passed and that ddclient is NOT running as daemon (and shouldn't be as that would break the cron logic). |

> When debugging the config (ddclient unfortunately doesn't have much of config validation, so it's trial and error...), use the `-verbose -debug` command line args to dump the state and HTTP requests to output.

## Exposed ports

This container does not expose any ports.

## Volumes

| Container path | Description |
|------------|---------------|
| /config | Configuration directory, should contain the ddclient.conf. Please see the [examples](https://github.com/ddclient/ddclient/blob/master/sample-etc_ddclient.conf) in ddclient GitHub repository |

## Security
The container is regularly scanned for vulnerabilities and updated. Further info can be found in the [Security tab](https://github.com/homecentr/docker-ddclient/security).

### Container user
The container supports privilege drop. Even though the container starts as root, it will use the permissions only to perform the initial set up. The ddclient process runs as UID/GID provided in the PUID and PGID environment variables.

:warning: Do not change the container user directly using the `user` Docker compose property or using the `--user` argument. This would break the privilege drop logic.