package com.sinhviencafemanagement.command.login;

import com.sinhviencafemanagement.command.Command;
import com.sinhviencafemanagement.dao.UserDAO;
import com.sinhviencafemanagement.models.User;
import com.sinhviencafemanagement.utils.FaceRecognitionHelper;

import java.util.List;

public class FaceIdCommand implements Command {
    private final float[] faceEmbedding;
    private final UserDAO userDAO;
    private final double THRESHOLD = 0.6; // Cosine similarity threshold

    public FaceIdCommand(float[] faceEmbedding, UserDAO userDAO) {
        this.faceEmbedding = faceEmbedding;
        this.userDAO = userDAO;
    }

    @Override
    public boolean execute() {
        List<User> admins = userDAO.getAllAdmins();
        for (User user : admins) {
            float[] dbEmbedding = userDAO.stringToEmbedding(user.getFaceEmbedding());
            if (dbEmbedding != null) {
                double similarity = new FaceRecognitionHelper(null).calculateCosineSimilarity(faceEmbedding, dbEmbedding);
                if (similarity >= THRESHOLD) {
                    return true;
                }
            }
        }
        return false;
    }
}

