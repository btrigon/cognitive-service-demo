package com.example.facetest.utils;

import com.example.facetest.network.apimodels.FaceEmotion;

import java.util.Comparator;

/**
 * Created by Benjamin on 15/05/16.
 */
public class EmotionComporators {

    public static class CurrentCategoryComparator implements Comparator<FaceEmotion> {
        public int compare(FaceEmotion face1, FaceEmotion face2) {
            return Double.compare(face1.getScores().getCurrentCategoryScore(), face2.getScores().getCurrentCategoryScore());
        }
    }

    public static class AngerComparator implements Comparator<FaceEmotion> {
        public int compare(FaceEmotion face1, FaceEmotion face2) {
            return Double.compare(face1.getScores().getAnger(), face2.getScores().getAnger());
        }
    }

    public static class DisgustComparator implements Comparator<FaceEmotion> {
        public int compare(FaceEmotion face1, FaceEmotion face2) {
            return Double.compare(face1.getScores().getDisgust(), face2.getScores().getDisgust());
        }
    }

    public static class ContemptComparator implements Comparator<FaceEmotion> {
        public int compare(FaceEmotion face1, FaceEmotion face2) {
            return Double.compare(face1.getScores().getContempt(), face2.getScores().getContempt());
        }
    }

    public static class FearComparator implements Comparator<FaceEmotion> {
        public int compare(FaceEmotion face1, FaceEmotion face2) {
            return Double.compare(face1.getScores().getFear(), face2.getScores().getFear());
        }
    }

    public static class HappinessComparator implements Comparator<FaceEmotion> {
        public int compare(FaceEmotion face1, FaceEmotion face2) {
            return Double.compare(face1.getScores().getHappiness(), face2.getScores().getHappiness());
        }
    }

    public static class NeutralComparator implements Comparator<FaceEmotion> {
        public int compare(FaceEmotion face1, FaceEmotion face2) {
            return Double.compare(face1.getScores().getNeutral(), face2.getScores().getNeutral());
        }
    }

    public static class SadnessComparator implements Comparator<FaceEmotion> {
        public int compare(FaceEmotion face1, FaceEmotion face2) {
            return Double.compare(face1.getScores().getSadness(), face2.getScores().getSadness());
        }
    }

    public static class SurpriseComparator implements Comparator<FaceEmotion> {
        public int compare(FaceEmotion face1, FaceEmotion face2) {
            return Double.compare(face1.getScores().getSurprise(), face2.getScores().getSurprise());
        }
    }




}
