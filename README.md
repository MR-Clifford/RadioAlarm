# RadioAlarm
Android Radio alarm Clock

A project written in the pure android SDK, the main project aim being to use as little external libraries as possible, achieved using the base android core & libraries. The technologies use will be JSON, Consuming RESTful services & the Alarm Scheduler. 

Dirble was used for its intuitive API to get a list of radio stations, the API is called in several cases and stored locally on the device after the restful service is consumed; The API is called on app first run, geo location change and manual user. this was to improve 



Design patterns used: 

Singleton: The most basic of design patterns, essentially allow global access to instance of class by introducing a memory leak. Ideally this will be replaced at a later stage with dependency injection (dagger 2), a great alternative.
  
Chain of responsibility: Used this to delegate/pass on messages in my app. mainly stopping media. If the class does know hot to stop it, pass it onto a class that does. 
  
Factory & builder: Used to create a various objects based on inputs & conditionals. Allows mass productions  
  
MVC: More of a Architectural pattern, this is built into androids core system with activities typically being controllers and the views being XM. The models are down to developers to create and control.

Strategy: Depending on what type of alarm currently selected changes how the app responds to use. 

Observer: Essentially all my adapters respond to this. Whenever any list view data is changed the view watching my adapter is updated.

Lazy initialization: are used on all singletons & on the media search activities.

Adapter: This is essential for any listview in android in general.  
