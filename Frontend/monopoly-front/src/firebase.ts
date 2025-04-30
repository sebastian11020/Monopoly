import { initializeApp } from "firebase/app";
import { getAuth } from "firebase/auth";

const firebaseConfig = {
    apiKey: "AIzaSyAP4GUVR8-w9RFaF____ltHf9At4QrjI2s",
    authDomain: "monopoly-72103.firebaseapp.com",
    projectId: "monopoly-72103",
    storageBucket: "monopoly-72103.firebasestorage.app",
    messagingSenderId: "41742829462",
    appId: "1:41742829462:web:1b4ba25b022fbfb449ffb7"
};

const app = initializeApp(firebaseConfig);
export const auth = getAuth(app);