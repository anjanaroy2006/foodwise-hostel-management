package com.foodwise.service;

import java.util.List;

/**
 * PredictionModule handles intelligent meal forecasting based on:
 * 1. Confirmed student meal intents (YES count)
 * 2. Historical demand from previous similar days
 * 3. Contextual factor (attendance patterns, day of week buffer)
 * 4. Safety buffer for kitchen preparation
 *
 * Implements the formula described in FOODWISE methodology:
 * Predicted Demand = Weighted blend of confirmed intent & historical average
 * Recommended Preparation = Predicted Demand + Safety buffer (preventing shortage)
 */
public class PredictionModule {
    private int historicalDemand;
    private int predictedDemand;
    private int recommendedPreparation;

    public PredictionModule() {
    }

    public PredictionModule(int historicalDemand, int predictedDemand, int recommendedPreparation) {
        this.historicalDemand = historicalDemand;
        this.predictedDemand = predictedDemand;
        this.recommendedPreparation = recommendedPreparation;
    }

    /**
     * Calculates the expected demand based on confirmed counts and historical averages.
     * Matches sample from slides: 320 confirmed -> 335 expected -> 340 recommended
     */
    public int calculatePredictedDemand(int confirmedCount, List<Integer> historicalDaysDemand) {
        if (historicalDaysDemand == null || historicalDaysDemand.isEmpty()) {
            this.historicalDemand = confirmedCount;
            this.predictedDemand = (int) Math.round(confirmedCount * 1.05); // 5% default walk-in buffer
            return this.predictedDemand;
        }

        // 1. Calculate historical average
        int sum = 0;
        for (int count : historicalDaysDemand) {
            sum += count;
        }
        this.historicalDemand = sum / historicalDaysDemand.size();

        // 2. Blend: 70% confirmed intent weight + 30% historical pattern weight
        if (confirmedCount > 0) {
            double blended = (confirmedCount * 0.70) + (this.historicalDemand * 0.30);
            this.predictedDemand = (int) Math.round(blended);
        } else {
            this.predictedDemand = this.historicalDemand;
        }

        return this.predictedDemand;
    }

    /**
     * Calculates kitchen preparation recommendation with optimal safety buffer.
     * Safety buffer prevents food shortages while minimizing waste.
     */
    public int calculateRecommendedPreparation(int expectedDemand) {
        // Buffer margin of ~5 to 10 extra plates depending on expected volume
        int safetyBuffer = (expectedDemand > 200) ? 5 : 3;
        this.recommendedPreparation = expectedDemand + safetyBuffer;
        return this.recommendedPreparation;
    }

    // Getters and Setters
    public int getHistoricalDemand() {
        return historicalDemand;
    }

    public void setHistoricalDemand(int historicalDemand) {
        this.historicalDemand = historicalDemand;
    }

    public int getPredictedDemand() {
        return predictedDemand;
    }

    public void setPredictedDemand(int predictedDemand) {
        this.predictedDemand = predictedDemand;
    }

    public int getRecommendedPreparation() {
        return recommendedPreparation;
    }

    public void setRecommendedPreparation(int recommendedPreparation) {
        this.recommendedPreparation = recommendedPreparation;
    }
}
