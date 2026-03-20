// /middleware/auth.js
const { db, admin } = require("../config/db");

const protect = async (req, res, next) => {
  let token;
  
  if (
    req.headers.authorization &&
    req.headers.authorization.startsWith("Bearer ")
  ) {
    try {
      // 1. Extract Token
      token = req.headers.authorization.split(" ")[1];

      // 2. Verify Token with Firebase
      const decodedToken = await admin.auth().verifyIdToken(token);
      const uid = decodedToken.uid;

      // 3. Check if user exists in 'admins' collection
      const adminDoc = await db.collection("admins").doc(uid).get();

      if (!adminDoc.exists) {
        return res.status(403).json({ message: "Access Denied. Not an Admin." });
      }

      // 4. Attach admin data to request
      req.user = { 
        uid: uid, 
        ...adminDoc.data() 
      };
      
      next();
    } catch (error) {
      console.error("Auth Error:", error);
      res.status(401).json({ message: "Invalid Token", error: error.message });
    }
  } else {
    res.status(401).json({ message: "No token provided" });
  }
};

module.exports = { protect };