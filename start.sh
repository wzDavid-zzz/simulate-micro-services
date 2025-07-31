nohup java -jar time-service-1.0-SNAPSHOT.jar > time-1.log 2>&1 &
nohup java -jar time-service-2-1.0-SNAPSHOT.jar > time-2.log 2>&1 &
nohup java -jar client-service-1.0-SNAPSHOT.jar > client-1.log 2>&1 &
nohup java -jar client-service-2-1.0-SNAPSHOT.jar > client-2.log 2>&1 &