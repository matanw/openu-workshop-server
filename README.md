compile docker
docker buiild . -t web:1

run by:
mkdir /tmp/db1
docker run  --volume /tmp/db1:/target web:1 -p 8080:8080
