Senior Project Final Submission 
Yu Sun
CS 4610
Eldrin Pile

Project: EatHeavy, Calorie Tracker Android App

What:
EatHeavy is a food logging app that allows users to keep track of their daily food intake. Users can plan or record their meals within
a food diary which is easily created in the app. The diary tracks the total calories, protein, fats, and carbohydrates from each food logged for that day.
The quantity and servings of each food can be logged as well within the diary. These foods can be managed in the app as well 
and created in the food manager. Users can create custom foods and log the nutrients that each one gives. Users can also delete, search, and manage their foods
accordingly very easily.

Why: The purpose and main goal of the project was to familiarize myself with mobile app development as this is the first major mobile app project I've developed
I've developed previous projects in the past with React and was curious to explore how mobile app development differs. I chose this topic as a calorie counter
app is very relevant to my lifestyle as I enjoy logging my meals and ensuring I've eaten the right amount of calories. The goal of the app was to emulate the basics
and functionality of other popular calorie counter app such as MyFitnessPal. 


Programming Language: Kotlin 

Technical Details: The application was written in Kotlin using Android Studio. The structure of the app was essentially split into two different segments, 
Diary classes and Food classes. Each of these classes utilized Kotlin's Viewmodels, Fragments, and Room to store data in a local database.

Each of the initial screens that display the list of days and the list of foods utilize RecyclerViews that add and delete the diary/food objects
depending on the user's actions. The structure of each of the classes are separated into fragments, viewmodels, and a repository for the diary/food data types
The fragment classes are in control of displaying the data as well as providing user interaction to the screens. These fragment classes make use of the appropriate
view models.
The repository classes make use of a singleton pattern where there is a single repository for each database. The repository classes are developed using
the Room database builder and utilizes LiveData which allows for observers to watch and listen for changes in the data. 
The viewmodel classes are what connects fragment and repository classes together as it is a reusable viewmodel that can be called to call on CRUD functions
from the repository. The purpose of viewmodel classes is also to keep the data in memory during the lifecycle of the fragments. 

  Features: 
  CRUD Functionality for Food, Diary Logging
  Add and log food individually to each diary
  Sorting diaries in chronological order
  Change quantity/serving size of food 
  Search function for Food
  Date/Calendar feature to change data of food diary
  Local database

  Current Bugs and Todo:
    Bugs: Some fragments do not return to the appropriate screen when pressing back (Food Creation Fragment)
          Wrong or inappropriate menu item at top of screen appears in the wrong fragments
    Todo: Store database nonlocally in a separate repository
          Categorize foods into breakfast, lunch, dinner
          Connect food database to a larger and managed database through an API
          More sorting options regarding foods and diary
          Uploadable images and image thumbnails for food
          
  Difficulties Faced: The most significant difficulty faced while developing the app was figuring out how to manage fragments between one another and create child                           fragments. I also struggled to figure out how to send the right data between fragments. The front end development of the app was also somewhat
                      tedious but is still very familiar with what I've worked with in the past. One of the main struggles regarding the front end had to also do with
                      the fragments. Fragments kept overlapping on one another and was essentially unusable as too many screens and information would appear on the
                      screen at once.
                      
  Overall, developing this application brought me a lot of experience regarding mobile app development. Applying the skills and knowledge from my classes into mobile app development was interesting and an overall enjoyable project to work on.
                      
                      
    
  
