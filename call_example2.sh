set -x
set -e

URL=http://localhost:8080
curl  --fail -w "\n" -u p1:1p $URL/courses
curl  --fail -w "\n" -u p2:2p $URL/courses
curl  --fail -w "\n" -u p1:1p $URL/courses/1
curl  --fail -w "\n" -u p1:1p $URL/courses/1/tasks
curl  --fail -w "\n" -u p1:1p $URL/courses/1/tasks -X POST \
   -H "Content-Type: application/json" \
   -d '{"id":1,"submissionDeadline":"2023-01-04","checkDeadLine":"2023-01-04", "weightInGrade":0.1}'
curl  --fail -w "\n" -u p1:1p $URL/courses/1/tasks
curl  --fail -w "\n" -u p1:1p $URL/courses/1/tasks/1 -X PUT \
   -H "Content-Type: application/json" \
   -d '{"id":1,"submissionDeadline":"2023-01-05","checkDeadLine":"2023-01-04", "weightInGrade":0.5}'
curl  --fail -w "\n" -u p1:1p $URL/courses/1/tasks
curl  --fail -w "\n" -u p1:1p $URL/courses/1/tasks -X POST \
   -H "Content-Type: application/json" \
   -d '{"id":2,"submissionDeadline":"2023-01-04","checkDeadLine":"2023-01-04", "weightInGrade":0.1}'
curl  --fail -w "\n" -u p1:1p $URL/courses/1/tasks
curl  --fail -w "\n" -u p1:1p $URL/courses/1/tasks/2 -X DELETE
curl  --fail -w "\n" -u p1:1p $URL/courses/1/tasks
TASK_FILE=/tmp/task
echo "1+1=?" > $TASK_FILE
ANSWER_FILE=/tmp/answer
echo "1+1=3" > $ANSWER_FILE
FEEDBACK_FILE=/tmp/feedback
echo "no, it's 2" > $FEEDBACK_FILE
curl  --fail -w "\n" -u p1:1p $URL/courses/1/tasks/1/file -i -X POST -H "Content-Type: multipart/form-data"  \
  -F "file=@$TASK_FILE"
curl  --fail -w "\n" -u p1:1p $URL/courses/1/tasks/1/file -i -X PUT -H "Content-Type: multipart/form-data"  \
  -F "file=@$TASK_FILE"
curl  --fail -w "\n" -u p1:1p $URL/courses/1/tasks/1/file
curl  --fail -w "\n" -u s3:3p $URL/courses
curl  --fail -w "\n" -u s3:3p $URL/courses/1
curl  --fail -w "\n" -u s3:3p $URL/courses/1/tasks
curl  --fail -w "\n" -u s3:3p $URL/courses/1/tasks/1/mysubmission/file -i \
   -X POST -H "Content-Type: multipart/form-data"  \
  -F "file=@$ANSWER_FILE"
curl  --fail -w "\n" -u s3:3p $URL/courses/1/tasks/1/mysubmission
curl  --fail -w "\n" -u p1:1p $URL/courses/1/tasks/1/submissions
curl  --fail -w "\n" -u p1:1p $URL/courses/1/tasks/1/submissions/3
curl  --fail -w "\n" -u p1:1p $URL/courses/1/tasks/1/submissions/3/file
curl  --fail -w "\n" -u p1:1p $URL/courses/1/tasks/1/submissions/3/feedbackFile -i \
   -X POST -H "Content-Type: multipart/form-data"  \
  -F "file=@$FEEDBACK_FILE"
curl  --fail -w "\n" -u s3:3p $URL/courses/1/tasks/1/mysubmission
curl  --fail -w "\n" -u s3:3p $URL/courses/1/tasks/1/mysubmission/feedbackFile
curl  --fail -w "\n" -u  p1:1p $URL/courses/1/tasks/1/submissions/3/grade \
   -H "Content-Type: application/json" \
   -d '96' -X POST -v
curl  --fail -w "\n" -u s3:3p $URL/courses/1/tasks/1/mysubmission