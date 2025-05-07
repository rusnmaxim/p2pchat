FROM quay.io/keycloak/keycloak:21.1.2

WORKDIR /opt/keycloak

COPY themes /opt/keycloak/themes

ENV KC_THEME=my-theme
ENV KC_THEME_DEFAULT=my-theme

ENV JAVA_OPTS_APPEND="-Dnet.bytebuddy.experimental=true"

ENTRYPOINT ["/opt/keycloak/bin/kc.sh"]
CMD ["start-dev"]