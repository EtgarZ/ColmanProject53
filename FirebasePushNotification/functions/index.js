'use-strict'

const functions = require('firebase-functions');
const admin = require('firebase-admin');

admin.initializeApp(functions.config().firebase);

exports.sendNotification = functions.database.ref("/Users/{user_id}/Shares/{share_id}").onCreate((snapshot, context) => {
    const data = snapshot.val();
    console.log("data = " + data);
    const to_user_id = context.params.user_id;
    const share_id = context.params.share_id;

    console.log("User ID: " + to_user_id + " | Share ID: " + share_id);

    const shareRef = admin.database().ref("/Users/" + to_user_id + "/Shares");

    return shareRef.child(share_id).once("value").then( resp => {
        const from_user_id = resp.val().sender;
        
        console.log("from_user_id = " + from_user_id);

        const user_from = admin.database().ref("/Users").child( from_user_id).once("value");
        const to_user = admin.database().ref("/Users").child(to_user_id).once("value");

        return Promise.all([user_from, to_user]).then(result => {
            console.log("in promise all");
            console.log("result[0] = " + result[0]);
            console.log("result[0].val = " + result[0].val);
            console.log("result[0].val() = " + result[0].val());
            console.log("result[1] = " + result[1]);
            console.log("result[1].val = " + result[1].val);
            console.log("result[1].val() = " + result[1].val());
            const from_name = result[0].val().name;
            const token_id = result[1].val().token;

            const payload = {
                data : {
                    from_user_id: from_user_id,
                    to_user_id: to_user_id,
                    title : "notification from " + from_name,
                    body: "I want to share my contacts with you",
                    icon: "default"
                }
            };

            return admin.messaging().sendToDevice(token_id, payload).then(result => {
                console.log("Notification sent");
            });
        });

    });
});
