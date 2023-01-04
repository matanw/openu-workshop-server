set -x

URL=http://localhost:8080

curl -w "\n" -u aaa:bbb $URL/students?id=10
curl -w "\n" -u aaa1:bbb $URL/students?id=9
curl -w "\n" $URL/students?id=9
curl -w "\n" -u aaa:bbb $URL/courses
curl -w "\n" -u aaa:bbb $URL/courses/8

curl -w "\n" -u aaa:bbb $URL/courses/8/tasks
curl -w "\n" -u aaa:bbb $URL/courses/8/tasks/7/file -v
