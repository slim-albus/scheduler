
the ui should follow the proper reccomendsations in react for state and proper use of hooks for fetching, forms, and overall state management(useReducer + Context if really needed), and teh ui should be organized into components that make sense.

use shad cn ui if possible.


the frontend should have a root layout(for all users) that contains a top nav bar that shows the current logged in user, and date time, and on the left there should be a sidebar with the map of a building not literally a building but a card grid of rooms occupations fetched from the backend. and hovering on the cards will show additional info from the data, and one row of cards should corespond a level on the building. so teh front end will fetch the map every 1- 2 minutes to update the data.

then on the main space we render the admin dashboard or the schedule time table for the user(teeacher or student)

the admin dashboard will have 2 tabs, one for schedule timetable(with dropdown selector for section) and a clean timetable(with week navigation for thw whole semester) with event cards that they can click to do actions(reschedule, set to canceled(canceled events will appear differently )) and a book/create event button to open a modal of form for creating a new event to the section concerned. then there will be a manage tab, where there will be a table view for our resources (rooms, instructors, sections, etc.) and we can add, edit, delete, and update them. crud basically.. there will be a new button on top for create, clicking on an item will a sidebar on the left(thats a part of the table view, and it will auto load the row data into inputs and we can update the info and view them cleanly at the same time)(confirmations included)

but for the teachers and students we only render a timetable that concerns them. no admin tabs for them. just a timetable view and the room occupation map on the sidebar. (and no section dropdown as the backend will figure out that fron their login) 
same goes for teacchers but they will find their classes for the week (all sections assigned to that teacher) and they can do actions on them(reschedule, set to canceled).


