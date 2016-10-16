FROM java:8
VOLUME /tmp

RUN apt-get update && apt-get install -y netcat

ADD start.sh start.sh
RUN chmod +x start.sh

ADD pep-container-*.jar pep-container.jar
RUN bash -c 'touch /pep-container.jar'

CMD ["/start.sh"]

EXPOSE 9050