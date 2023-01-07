set -x
set -e

URL=http://localhost:8080
echo aaa >a_file.txt

#real upload
rm -f files/a_file.txt #hack:remove file from serverx
curl  --fail -w "\n" -u aaa:bbb $URL/realUpload -i -X POST -H "Content-Type: multipart/form-data"  \
  -F "file=@a_file.txt"

echo " ******auth example*****"
curl  --fail -w "\n" -u aaa:bbb $URL/courses
curl  -w "\n" -u aaa1:bbb $URL/courses
curl  -w "\n" $URL/courses

echo "*****GET courses(s/p)****"
curl  --fail -w "\n" -u aaa:bbb $URL/courses
echo "*****GET 1 course(s/p)****"
curl  --fail -w "\n" -u aaa:bbb $URL/courses/4
echo "****POST tasks (p) *****"
curl  --fail -w "\n" -u aaa:bbb $URL/courses/8/tasks -X POST \
   -H "Content-Type: application/json" \
   -d '[{"id":1,"deadline":"2023-01-04T19:20:17.226+00:00"},{"id":2,"deadline":"2023-01-04T19:20:17.226+00:00"}]'
echo "****Get Tasks (s,p)*****"
curl  --fail -w "\n" -u aaa:bbb $URL/courses/6/tasks
echo "*****Get task file (s/ p) *****"
curl  --fail -w "\n" -u aaa:bbb $URL/courses/8/tasks/7/file
echo "*****Post task file (p) *****"
curl  --fail -w "\n" -u aaa:bbb $URL/courses/8/tasks/7/file -i -X POST -H "Content-Type: multipart/form-data"  \
  -F "file=@a_file.txt"
echo "*****Put task file (p) *****"
curl  --fail  --fail -w "\n" -u aaa:bbb $URL/courses/8/tasks/7/file -i -X PUT -H "Content-Type: multipart/form-data"  \
  -F "file=@a_file.txt"

echo "*****Get submissions (p) *****"
curl  --fail -w "\n" -u aaa:bbb $URL/courses/8/tasks/7/submissions
echo "*****Get submission (p) *****"
curl  --fail -w "\n" -u aaa:bbb $URL/courses/8/tasks/7/submissions/s123
echo "*****Get submission file (p) *****"
curl  --fail -w "\n" -u aaa:bbb $URL/courses/8/tasks/7/submissions/s123/file

echo "*****Get my submission (s) *****"
curl  --fail -w "\n" -u aaa:bbb $URL/courses/8/tasks/7/mysubmission
echo "*****Get my submission file(s) *****"
curl  --fail -w "\n" -u aaa:bbb $URL/courses/8/tasks/7/mysubmission/file
echo "*****Post my submission file (s) *****"
curl  --fail -w "\n" -u aaa:bbb $URL/courses/8/tasks/7/mysubmission/file -i -X POST -H "Content-Type: multipart/form-data"  \
  -F "file=@a_file.txt"
echo "*****Put my submission file (s) *****"
curl  --fail -w "\n" -u aaa:bbb $URL/courses/8/tasks/7/mysubmission/file -i -X PUT -H "Content-Type: multipart/form-data"  \
  -F "file=@a_file.txt"


echo "*****Get my submission feedback file (s) *****"
curl  --fail -w "\n" -u aaa:bbb $URL/courses/8/tasks/7/mysubmission/feedbackFile
echo "*****Get feedback file (p) *****"
curl  --fail -w "\n" -u aaa:bbb $URL/courses/8/tasks/7/submissions/s123/feedbackFile
echo "*****Post submission feedback file (p) *****"
curl  --fail -w "\n" -u aaa:bbb $URL/courses/8/tasks/7/submissions/s123/feedbackFile -i -X POST -H "Content-Type: multipart/form-data"  \
  -F "file=@a_file.txt"
echo "*****Put submission feedback file (p) *****"
curl  --fail -w "\n" -u aaa:bbb $URL/courses/8/tasks/7/submissions/s123/feedbackFile -i -X PUT -H "Content-Type: multipart/form-data"  \
  -F "file=@a_file.txt"
echo "***Post grade (p)"
curl  --fail -w "\n" -u aaa:bbb $URL/courses/8/tasks/12/submissions/s123/grade \
   -H "Content-Type: application/json" \
   -d '96' -X POST -v
echo "***Put grade (p)"
curl  --fail -w "\n" -u aaa:bbb $URL/courses/8/tasks/12/submissions/s123/grade \
   -H "Content-Type: application/json" \
   -d '96' -X PUT -v