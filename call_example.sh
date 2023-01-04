set -x

URL=http://localhost:8080

curl -w "\n" -u aaa:bbb $URL/students?id=10
curl -w "\n" -u aaa1:bbb $URL/students?id=9
curl -w "\n" $URL/students?id=9
curl -w "\n" -u aaa:bbb $URL/courses
curl -w "\n" -u aaa:bbb $URL/courses/8

curl -w "\n" -u aaa:bbb $URL/courses/8/tasks
curl -w "\n" -u aaa:bbb $URL/courses/8/tasks/7/file -v

rm -f files/a_file.txt #hack:remove file from server
echo aaa > a_file.txt
curl -w "\n" -u aaa:bbb $URL/courses/8/tasks/7/file -i -X POST -H "Content-Type: multipart/form-data"  \
  -F "file=@a_file.txt"
rm a_file.txt

curl -w "\n" -u aaa:bbb $URL/courses/8/tasks \
   -H "Content-Type: application/json" \
   -d '[{"id":1,"deadline":"2023-01-04T19:20:17.226+00:00"},{"id":2,"deadline":"2023-01-04T19:20:17.226+00:00"}]'



rm -f files/a_file.txt #hack:remove file from server
echo aaa > a_file.txt
curl -w "\n" -u aaa:bbb $URL/courses/8/tasks/7/mysubmissionfile -i -X POST -H "Content-Type: multipart/form-data"  \
  -F "file=@a_file.txt"
rm a_file.txt



curl -w "\n" -u aaa:bbb $URL/courses/8/tasks/7/submissions