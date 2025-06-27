# fitness app
Fitness app for loging exercise performance and tracking workouts. This app has 2 interesting branches. The [xml](https://github.com/HauntedMilkshake/fitness_app/tree/xml_version) version and the [compose](https://github.com/HauntedMilkshake/fitness_app/tree/main) version.
  
# Context
The aforementioned **xml version** was developed as my course work for graduating 12th grade. The **compose version** was developed during my internship at [Tumba Solutions](https://www.tumba.solutions/) in collaboration with [Vladimir Tomashevich](https://github.com/Gotalicp) under the direct supervistion of [Dimitar Stoyanov](https://github.com/DimitarStoyanoff) and [Ivan Trifonov](https://github.com/trifonov-ivan) as mentors.
   
# Functionality
 * Charts for weekly workouts, progression on exercises
 * Start workouts from either templates or empty ones that can be filled up with exercises
 * Track an entire workout with the ability to use rest timers and fill in sets for each exercise
 * Look through history of workouts and some informtive data
 * Create new exercises that fit your needs
 * Track progress and get interesting and informative data for each exercise (features a lot of charts)
 * Track and log body measurements (charts featured for measurements)
  
# Design 
**Manifest**(subject to change) - https://www.figma.com/file/SSntGNR87kJSDhcQptSjkw/Untitled?type=design&node-id=0-1&mode=design&t=AiSZ9VobMNEc2lvd-0
App was heavily inspired by [Strong](https://www.strong.app/)

# Instalation
The app is not published to the play store and because of the limit of firebase services as of **27/6/2025** are not active. So here is a relative guide to run the app.

## Prerequisites
* Android studio(Best bet on the latest version)
* Create a [firebase project](https://console.firebase.google.com/u/0/?pli=1) and use firestore as the db of choice. After that you need to generate a **google.services.json** file that needs to be pasted in the repo.

# Tech info
This is considering the compose version as my concern for the xml version was a working demo rather than trying to adhere to principles

## Architecture: [MVVM (Model-View-ViewModel)](https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93viewmodel)
This project follows the MVVM (Model-View-ViewModel) architecture to create a clear separation of concerns, which makes the codebase more modular, testable, and maintainable.

> View (UI Layer): Built entirely with Jetpack Compose. The UI observes state changes from the ViewModel via Kotlin Flows and is responsible for rendering the app's visuals. It communicates user events to the ViewModel.
> ViewModel: Contains the presentation logic and exposes UI state. It survives configuration changes and fetches data from the Model layer, ensuring the UI has the data it needs without being aware of the data source.
> Model (Data Layer): Managed by a Repository pattern which acts as a single source of truth. The repository abstracts the data sources
    
## Dependency Injection: [Hilt](https://developer.android.com/training/dependency-injection/hilt-android)
Hilt is used for dependency injection to reduce boilerplate, manage dependency lifecycles, and facilitate easier testing. It's integrated seamlessly with other Jetpack libraries.

> Dependencies are provided to Android components like Activities and ViewModels using annotations like @AndroidEntryPoint and @HiltViewModel.
> Custom Hilt Modules (@Module) are used to provide instances of dependencies like Retrofit or database handlers.


## Guiding Principles: [SOLID](https://www.baeldung.com/solid-principles)
The design of this app strives to adhere to SOLID principles to create a robust and scalable architecture.
> Single Responsibility Principle: Each class has a distinct responsibility. For instance, the UserRepository is solely responsible for managing user data
> Dependency Inversion Principle: We depend on abstractions, not on concretions. ViewModels depend on repository interfaces rather than their concrete implementations. This, combined with Hilt, allows for easy swapping of implementations, especially for testing (e.g., providing a FakeUserRepository).

# Roadmap
The app is considered finished :).
