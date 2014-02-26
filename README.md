# Vaadinized Petstore Java EE 6 


This is a work in progress port of [a Petstore example app by Antonio Goncalves](https://github.com/agoncal/agoncal-application-petstore-ee6) that uses [Vaadin](https://vaadin.com) on UI layer instead of faces. Current version is a quickly hacked prototype that mostly just reuses controllers from JSF implementation.

Note, that although the UI now provides pretty much same features as the original example, the project is by no means "ready for production". Code still needs some refactoring and cleanup. Also some security stuff is really not that well thought, even in the original project (e.g. passwords are not hashed).

