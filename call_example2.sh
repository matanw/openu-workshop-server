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
