// // Start writing Firebase Functions
// // https://firebase.google.com/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// })

'use strict'
var functions = require('firebase-functions');

const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

// http triggered cloud function
exports.triggerHttp = functions.https.onRequest((req, resp) => {
  // Forbid PUT requests
  if (req.method === 'PUT') {
    console.log("Forbidden response sent");
    resp.status(403).send('Forbidden!');
  }
  if (req.body.name) {
    return admin.database().ref('/users').once('value').then(allUsers => {
      console.log("NAMED post body");
      resp.send('hi '+req.body.name+'. Total users: ' + allUsers.numChildren());
    });
  } else {
    return admin.database().ref('/users').once('value').then(allUsers => {
      console.log("no name post body. user count sent");
      resp.send('Empty post body. Total users: ' + allUsers.numChildren());
    });
  }
});

// Sanitizing comments.
exports.sanitizeComments = functions.database.
      ref('/comments/{commentId}').onWrite(event => {

  const commentId = event.params.commentId;
  const post = event.data.val();
  console.log('commentId: ' + commentId + ": " + post.text);

  if(post.sanitized){
    return;
  }

  post.sanitized = true;
  post.text = sanitize(post.text);
  return event.data.ref.set(post);
});

function sanitize(s) {
  var sanitizedText = s;
  sanitizedText = sanitizedText.replace(/\bstupid\b/ig, "wonderful");
  sanitizedText = sanitizedText.replace(/\bcrazy\b/ig, "lovely");
  return sanitizedText
}

/**
* Triggers when a new pic (pic details) is uploaded in firebase database
*
*/
exports.sendNewPicNotification = functions.database
    .ref('/photos/{picId}').onWrite(event => {

  const newPicChecker = event.data;

  // if the data is empty exit from the function.
  if(!newPicChecker.val()){
    return console.log('No new picture data found');
  }

  // Only send notification for new picture not any update in the database
  if(newPicChecker.previous.val()){
    return;
  }
  const picId = event.params.picId;
  console.log('New picture added to database. '+picId+'->' + newPicChecker.val().photoCredit);

  var payload = {
    notification: {
      title: 'New Vidhi pics available',
      body: 'Dear Vivid Vidhi user!! New artifacts are available for viewing. Tap to open.'
    }
  };

  return admin.database().ref('/users').once('value').then(allUsers => {
    //console.log('all users: ' + allUsers);
    var tokens = new Array();
    allUsers.forEach(function(snapshot){
      var key = snapshot.key;
      var userDisplayName = snapshot.val().name;
      console.log('user: key: ' + key + ', name: ' + userDisplayName);
      var singleToken = snapshot.val().fcmToken;
      if(singleToken){
        tokens.push(singleToken);
        admin.messaging().sendToDevice(singleToken, payload).then(response => {
          console.log('gcms sent successfully to ' + userDisplayName);
        });
      }
    });
    // return admin.messaging().sendToDevice(tokens, payload).then(response => {
    //   console.log('gcms sent successfully: ' + response);
    // });
  });

  //console.log('userList: ' + userList);

})









// exports.sendWelcomeEmail = functions.auth.user().onCreate(event => {
//   const user = event.data; // The Firebase user.
//   const email = user.email; // The email of the user.
//   const displayName = user.displayName; // The display name of the user.
//   return sendWelcomeEmail(email, displayName);
// });
// 
// function sendWelcomeEmail(email, displayName) {
//   const mailOptions = {
//     from: '"MyCompany" <noreply@firebase.com>',
//     to: email
//   };
//   mailOptions.subject = `Welcome to ${APP_NAME}!`;
//   mailOptions.text = `Hey ${displayName}!, Welcome to ${APP_NAME}."
//     + " I hope you will enjoy our service.`;
//   return mailTransport.sendMail(mailOptions).then(() => {
//     console.log('New welcome email sent to:', email);
//   });
// }
