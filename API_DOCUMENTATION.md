# Meals REST API


## Get All Meals
Retrieves a list of all meals for the authorized user.

curl -X GET "http://localhost:8080/topjava/rest/meals/" 
     -H "Accept: application/json"  


## Create a Meal
Creates a new meal with the specified details.
Replace the date, time, description, and calories with the actual values for the meal you want to create.

curl -X POST "http://localhost:8080/topjava/rest/meals/" \
     -H "Content-Type: application/json" \
     -d '{"dateTime": "2024-02-03T20:25", "description": "Lunch", "calories": 500}'


## Delete a Meal
Deletes the meal with the specified ID.
Replace '100003' with the actual ID of the meal you want to delete.

curl -X DELETE "http://localhost:8080/topjava/rest/meals/100003"


## Update a Meal
Updates the meal with the specified ID.
Replace '100004' with the actual ID of the meal you want to update and the date, time, description, and calories with the new values.

curl -X PUT "http://localhost:8080/topjava/rest/meals/100004" \
     -H "Content-Type: application/json" \
     -d '{"dateTime": "2024-02-03T21:25", "description": "Lunch second", "calories": 500}'


## Get Meals Between Dates
Retrieves a list of meals between the specified start and end dates.
Make sure to replace startDate and endDate with the actual values for your query.

curl -X GET "http://localhost:8080/topjava/rest/meals/filter?startDate=2020-01-30&endDate=2020-01-30" \
     -H "Accept: application/json"


## Get Meals Between Times
Retrieves a list of meals between the specified start and end times on a given day.
Make sure to replace startTime and endTime with the actual values for your query.

curl -X GET "http://localhost:8080/topjava/rest/meals/filter?startTime=10:00&endTime=13:00" \
     -H "Accept: application/json"


## Get Meals Between Date and Time Ranges
Retrieves a list of meals between the specified date and time ranges. 
Replace startDate, endDate, startTime and endTime with the actual values for your query.

curl -X GET "http://localhost:8080/topjava/rest/meals/filter?startDate=2020-01-30&&endDate=2020-01-31&startTime=13:00&endTime=20:00" \
     -H "Accept: application/json"


## Creating a Meal: Duplicate DateTime Exception
Creating or updating a meal with a datetime that already exists will result in a server exception "ConstraintViolationException" (meal_unique_user_datetime_idx).

curl -X POST "http://localhost:8080/topjava/rest/meals/" \
     -H "Content-Type: application/json" \
     -d '{"dateTime": "2020-01-31T10:00", "description": "Duplicate DateTime", "calories": 500}'
