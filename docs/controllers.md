AuthController
POST /api/auth/login ➔ Public credentials validation node.

POST /api/auth/logout ➔ Secures session deletion pipeline.

ScheduleController
GET /api/calendar/student ➔ extracts thread attributes, asserts "STUDENT" access rights, and outputs the stitched data stream payload.

GET /api/map/live?time=... ➔ Publicly queries real-time room tracking details.

PUT /api/calendar/events/{id}/move ➔ Rejects "STUDENT" role, accepts manual calendar dashboard adjustments.

AdminCrudController
POST /api/admin/schedule/generate ➔ Triggers the algorithmic mass schedule generator backend.

GET | POST | DELETE /api/admin/students ➔ Baseline student catalog dashboard manipulation.

GET | POST | DELETE /api/admin/teachers ➔ Baseline faculty catalog dashboard manipulation.



/api/schedule?day=''
/api/schedule?week=''

/api/map

api/manage/users
api/manage/students
api/manage/teachers
api/manage/sections
api/manage/schedule/generate
api/manage/rooms
api/manage/semesters
