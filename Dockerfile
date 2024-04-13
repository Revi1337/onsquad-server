FROM crayonman/java17-alpine-ocr as onsquad

ARG FILE=onsquad
ARG PROFILE=dev

ENV FILE=${FILE}
ENV PROFILE=${PROFILE}

COPY ./build/libs/$FILE.jar /project/
WORKDIR /project
RUN chmod +x /project/$FILE.jar

EXPOSE 8080
CMD java -jar -Dspring.profiles.active=${PROFILE} ${FILE}.jar
