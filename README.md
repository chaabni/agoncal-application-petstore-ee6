# Vaadinized Petstore Java EE 6 


This is a work in progress port of [a Petstore example app by Antonio Goncalves](https://github.com/agoncal/agoncal-application-petstore-ee6). The differece here is that the UI is written with [Vaadin](https://vaadin.com) instead of JSF. Current version is quickly hacked prototype that mostly just reuses controller classes from JSF.

Note, that although UI now provides pretty much same features as the original example, the project is by no means "ready for production". Code still needs some refactoring and cleanup. Also some security stuff is really not that well thought even in the original project (e.g. passwords are not hashed).

