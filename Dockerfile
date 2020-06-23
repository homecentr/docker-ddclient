FROM homecentr/cron-base:1.2.1

ARG DDCLIENT_VERSION="3.9.1"

ENV DDCLIENT_ARGS=""
ENV CRON_SCHEDULE="30 * * * *"

# Install ddclient
# Note: the script is downloaded from a specific commit, because the latest official release did not contain support for the Cloudflare API tokens
ADD https://raw.githubusercontent.com/ddclient/ddclient/7a9991966bd7c40c423da0e3bdc82b41f1980655/ddclient /usr/sbin/ddclient

RUN chmod a+rx /usr/sbin/ddclient && \
    mkdir /var/cache/ddclient && \
    # Install Perl
    apk add --no-cache \
      perl=5.30.1-r0 \
      openssl=1.1.1g-r0 \
      perl-net-ssleay=1.88-r0 \
      perl-socket6=0.29-r1 && \
    # Install temporary dependencies required to download and install Perl modules
    apk add --no-cache --virtual cpanm_deps \ 
      make=4.2.1-r2 \
      wget=1.20.3-r0 \
      perl-app-cpanminus=1.7044-r1 \
      libssl1.1=1.1.1g-r0 \
      openssl-dev=1.1.1g-r0 \
      gcc=9.2.0-r4 \
      perl-dev=5.30.1-r0 \
      musl-dev=1.1.24-r2 && \
    # Install required Perl modules (Current version of the libraries can be found on https://metacpan.org/)
    cpanm DROLSKY/Data-Validate-IP-0.27.tar.gz && \
    cpanm SULLR/IO-Socket-SSL-2.068.tar.gz && \
    cpanm ISHIGAKI/JSON-PP-4.04.tar.gz && \
    cpanm SHLOMIF/IO-Socket-INET6-2.72.tar.gz --force && \
    # Remove temporary dependencies
    apk del cpanm_deps

COPY ./fs/ /  

VOLUME [ "/config" ]