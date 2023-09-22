# WeatherTracker
### App to Track Weather in the US. Retrieve your current live weather by default if granted location permission, search for any city of your choice, get all the various data and info you need.
Auto-Load last searched city upon app launch. Fetch and Cache weather icon of the city searched. Search is only populated for correct entry. UI to be updated. 

## Tech Stack: HILT for DI, MVVM, Live Data, DataBinding, Kotlin Coroutine, Retrofit, Junit, Mockito, Jetpack Navigation, SharedPreference, Repository pattern, Google play service, Error States

![app_newyork](https://github.com/RedGreen-Android/WeatherTracker/assets/83381250/17680237-0528-4936-8712-b427454437ba)
Screenshot 1: Once user has granted access up first launch of app. Fetch and display data of current location

![app_atlanta](https://github.com/RedGreen-Android/WeatherTracker/assets/83381250/f073a66c-9262-4238-983c-891e6b9db669)
Screenshot 2: When an User types "Atlanta" in the click search icon, populate the data for Atlanta in the US. In the next launch of the app, it will auto populated the same infomation for Atlanta. 

![enter_valid_city](https://github.com/RedGreen-Android/WeatherTracker/assets/83381250/be546c57-ab4a-4642-88fd-e8966ad09ce8)
Screenshot 3: When an User types Unaccepted keys such as numbers and invalid characters, it prompts the user to type correct US city
