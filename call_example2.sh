set -x
set -e

URL=http://localhost:8080
curl  --fail -w "\n" -u p123:123pass $URL/courses
curl  --fail -w "\n" -u p456:456pass $URL/courses
curl  --fail -w "\n" -u p123:123pass $URL/courses/1
curl  --fail -w "\n" -u p123:123pass $URL/courses/1/tasks
curl  --fail -w "\n" -u p123:123pass $URL/courses/1/tasks -X POST \
   -H "Content-Type: application/json" \
   -d '[{"id":1,"submissionDeadline":"2023-01-04","checkDeadLine":"2023-01-04", "weightInGrade":0.1},{"id":2,"submissionDeadline":"2023-01-04","checkDeadLine":"2023-01-04", "weightInGrade":0.3}]'
curl  --fail -w "\n" -u p123:123pass $URL/courses/1/tasks

TASK_FILE=/tmp/task
echo "1+1=?" > $TASK_FILE
ANSWER_FILE=/tmp/answer
echo "1+1=3" > $ANSWER_FILE
FEEDBACK_FILE=/tmp/feedback
echo "no, it's 2" > $FEEDBACK_FILE
curl  --fail -w "\n" -u p123:123pass $URL/courses/1/tasks/1/file -i -X POST -H "Content-Type: multipart/form-data"  \
  -F "file=@$TASK_FILE"
curl  --fail -w "\n" -u p123:123pass $URL/courses/1/tasks/1/file
curl  --fail -w "\n" -u s789:789pass $URL/courses
curl  --fail -w "\n" -u s789:789pass $URL/courses/1
curl  --fail -w "\n" -u s789:789pass $URL/courses/1/tasks
curl  --fail -w "\n" -u s789:789pass $URL/courses/1/tasks/1/mysubmission/file -i \
   -X POST -H "Content-Type: multipart/form-data"  \
  -F "file=@$ANSWER_FILE"
curl  --fail -w "\n" -u s789:789pass $URL/courses/1/tasks/1/mysubmission
curl  --fail -w "\n" -u p123:123pass $URL/courses/1/tasks/1/submissions
curl  --fail -w "\n" -u p123:123pass $URL/courses/1/tasks/1/submissions/789
curl  --fail -w "\n" -u p123:123pass $URL/courses/1/tasks/1/submissions/789/file
curl  --fail -w "\n" -u p123:123pass $URL/courses/1/tasks/1/submissions/789/feedbackFile -i \
   -X POST -H "Content-Type: multipart/form-data"  \
  -F "file=@$FEEDBACK_FILE"
curl  --fail -w "\n" -u s789:789pass $URL/courses/1/tasks/1/mysubmission
curl  --fail -w "\n" -u s789:789pass $URL/courses/1/tasks/1/mysubmission/feedbackFile
curl  --fail -w "\n" -u  p123:123pass $URL/courses/1/tasks/1/submissions/789/grade \
   -H "Content-Type: application/json" \
   -d '96' -X POST -v
curl  --fail -w "\n" -u s789:789pass $URL/courses/1/tasks/1/mysubmission