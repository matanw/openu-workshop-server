set -x

URL=http://localhost:8080

curl -w "\n" -u aaa1:bbb $URL/students?id=10
curl -w "\n" -u aaa:bbb $URL/students?id=9

echo # new line in end