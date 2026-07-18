- log insert
- make proper api end points
- test the api with bruno

- setup frontend instructions
- 

[GET]
/schedule

[POST]
/schedule


/admin/student
        /teacher



lets refine the api endpoints/controllers
admin actions  and crud should be under /api/admin
and the generate will be on /api/admin/generate and all the flows of generating the schedules will go with that


and theschedule query will be one /schedule for all users
and the app should figure out who is making the request based on their token, and the query response should be different for student and teacher and admin

- for student : only events related to them , for teacher only events related to them, for admin all events
- for actions like book, reschedule, cancel the app should figure out the role based on the token and should only allow the valid actions for that role.
so this means the student only get events that their section has , and the teacher get events that their are assigned to, and admin get all events(they can send params to get a sections events or a teachers events). there are also actions that admins and teachers are able to do thru that, like they can book/cancel/move events for other students or teachers. or instructors can book/cancel/move events for their students and themselves.
and the actions: 
    -move(orchestrates the flow of rescheduling, like looking for new time slots for an event, then returning them, then the user returns their choice of slot, then the app moves the event to the new slot if it is valid , else returns an error response)
    -book(handle booking of a new event just like moving, but instead of returning the event to be moved, it returns open slots for the event to be booked if there are no conflicts, else returns an error response)
    -cancel(set the event to canceled so the room at which the event is happening is open for others)
so we have the admin crud api for the school stuff, the schedule one for everything event related, 

and also i needed an endpoint to return an array of all the rooms with their current occupation and otherinfo like the course, teh section there, teacher...to display it in a live map of the building in a sidebar maybe like /map ?

