# Vividity
A simple picture sharing app

## Introduction
This is a simple pic sharing android application that uses firebase as backend. It lets users add photos from gallery or camera and it is automatically shared app users. Currently there is no concept of private collections. I created this as a sample app for learning firebase and it's features. specifically cloud functions. The following firebase features are used:

- Realtime database
- Storage
- Authentication
- Crash reporting
- Cloud messaging
- firebase cloud functions

## How to use
Directly cloning this repo will **NOT** work as I have omitted some mandatory files from the repo. Here are the steps on how to make this codebase work and get relevant firebase files:

- Clone the repo and change package name
- Log on to https://firebase.google.com/
- Create a new project in firebase console with your new package (com.your.new.package).
- Enable google login from authorizations tab
- Follow the instructions and add your computer's SHA1 fingerprint.
- download ***google-services.json*** when prompted and put it in app folder.

you will need a basic setup in you realtime database. just add the following node under root, rest of the database is supposed to be created with usage.

```javascript
password is all numeric with atleast 6 digits
{
"users" : {
  "admin01" : {
    "admin" : true,
    "name" : "name",
    "password" : "999999",
    "picName" : "any_image_in_storage_just_name",
    "relationShip" : "with the person in the pic",
    "username" : "pic an alpha numeric username"
  },
  "admin02" : {
    "admin" : true,
    "name" : "name2",
    "password" : "999999",
    "picName" : "any_image_in_storage_just_name",
    "relationShip" : "with the person in the pic",
    "username" : "pic an alpha numeric username"
  },
  "user01" : {
    "admin" : false,
    "name" : "name",
    "password" : "999999",
    "picName" : "any_image_in_storage_just_name",
    "relationShip" : "with the person in the pic",
    "username" : "pic an alpha numeric username"
  },
}
```

### Got any queries or suggestions
- drop me a mail heavenly.devil09@gmail.com
- checkout my blog: https://androiddevsimplified.wordpress.com/
- Connect on twitter, stackoverflow, github: @drulabs
- Connect on Google: https://plus.google.com/+KaushalDhruw
