// /controllers/userController.js
const { db } = require("../config/db");

// Get All Users
exports.getUsers = async (req, res) => {
  try {
    const snapshot = await db.collection("users").get();
    const users = [];
    snapshot.forEach(doc => {
      users.push({ id: doc.id, ...doc.data() });
    });
    res.json(users);
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
};

// Ban/Unban User
exports.updateUserStatus = async (req, res) => {
  try {
    const { userId } = req.params;
    const { isActive } = req.body; // Boolean

    await db.collection("users").doc(userId).update({
      isActive: isActive
    });

    res.json({ message: `User status updated to ${isActive}` });
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
};



// @desc    Promote a user to Admin
// @route   PUT /api/admin/users/:userId/role
exports.updateUserRole = async (req, res) => {
  try {
    const { userId } = req.params;
    const { makeAdmin } = req.body; // expect { makeAdmin: true }

    if (makeAdmin) {
      // 1. Get user email from users collection
      const userDoc = await db.collection("users").doc(userId).get();
      if (!userDoc.exists) {
        return res.status(404).json({ message: "User not found" });
      }
      const userData = userDoc.data();

      // 2. Add to 'admins' collection
      await db.collection("admins").doc(userId).set({
        email: userData.email,
        role: "admin",
        createdAt: new Date().toISOString(),
        permissions: ["users", "vendors", "orders"]
      });

      // 3. Update user record to reflect role (optional but good practice)
      await db.collection("users").doc(userId).update({
        isAdmin: true
      });

      res.json({ message: "User promoted to Admin successfully" });
    } else {
      // Handle removal of admin rights if needed
      await db.collection("admins").doc(userId).delete();
      await db.collection("users").doc(userId).update({
        isAdmin: false
      });
      res.json({ message: "Admin rights removed" });
    }
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
};