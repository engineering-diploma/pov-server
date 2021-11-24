FROM rabbitmq:3-management

# configs
COPY ca.pem /etc/rabbitmq
COPY ca-key.pem /etc/rabbitmq
COPY enabled_plugins /etc/rabbitmq
COPY rabbitmq.conf /etc/rabbitmq
COPY server-cert.pem /etc/rabbitmq
COPY server-key.pem /etc/rabbitmq
COPY server-req.pem /etc/rabbitmq
COPY definitions.json /etc/rabbitmq

EXPOSE 5671
EXPOSE 5672
EXPOSE 15672

VOLUME /var/lib/rabbitmq