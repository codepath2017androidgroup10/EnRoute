# EnRoute

# Group Project

**EnRoute** is an android app that allows a user find places along his route.

Time spent: **108** hours spent in total

## User Stories

The following required functionality is completed:

* [X]	User can **view search results**
  * [X] User can enter a to and from destination, with current location as default from destination. Address validity is checked.
  * [X] User can choose options from the category, namely  Gas Station, Restaurant, Coffee Shop (through a floating action button's options) 
  * [X] User can enter more deatiled search creteria in toobar (such as thai restaurant).
  * [X] Map view shows the pins(marker) of locations along the route and on tap of a pin, shows a dialog of the name of the place, category.
  * [X] List view shows a list of locations, with each item showing the name of the place, How far from the route, if open now, category, rating, price level.
* [X] User can toggle between a map view and a list view. 
* [X] User can then click on a detailed view from the map or the list view to view more detailed information about the location.
  * [X] User can call the location from the detailed view
  * [X] User can open maps to get directions from the detailed view. 
* [X] User can save settings from the tool bar menu item.
  * [X] User can choose the search range away from the route.
  * [X] User can choose minimum rating to limit search resutls.
  * [X] User can reset to factory settings. 
  * [X] User can erase autocomplete location history 
* [X] User can take a picture with camera or from gallery from the detailed view. Save it with firebase and also show it at detailed view.

The following bonus features are completed:
* [X] User can choose from autocompelete location history when user enter from and to destination.
* [X] Once clicked, floating action button can show or hide three button choices with animation
* [ ] User can choose from autocomplete search history when user enter search creteria.
* [x] marker can show more information about open or not, rating etc.
* [x] User can write review and save it with firebase.
* [ ] night mode
* [ ] robust internet handling
* [ ] business open until
* [ ] User can choose from advanced features.
  * [ ] User can upgrade to advanced features to get real time data, as he moves along the route. 
  * [ ] User can choose from advanced features to choose different transportation choices(bike or on foot).
  * [ ] User can choose minimum/maximum price level.
  * [ ] User can revoke current location access.

  

## Video Walkthrough

Here's a walkthrough of implemented user stories:

[Video Walkthrough]https://www.dropbox.com/s/g4uv68hednta4xx/EnRoute-Sprint3Demo.mp4?dl=0

## Notes


## Open-source libraries most likely we will be using 

- [Android Async HTTP](https://github.com/loopj/android-async-http) - Simple asynchronous HTTP requests with JSON parsing
- [Glide](https://github.com/bumptech/glide) - Image loading and caching library for Android
- [DBFlow] (https://github.com/Raizlabs/DBFlow)
- [Parceler]https://github.com/johncarl81/parceler
- [AwesomeSplash] https://github.com/ViksaaSkool/AwesomeSplash - Splash screen

## License

    Copyright [2017] [codepath2017androidgroup10]

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
