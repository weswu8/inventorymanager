package com.ecommerce.flashsales;
/***
 * @author wuwesley
 * This function throttles the method calls within n seconds
 */
public class RateLimiter{
	private final double fillRatePerMs;
	private final double maxBudget;
	private double currentBudget;
	private long lastUpdateTime;
	
	public RateLimiter(double maxBudget, double fillRatePerMs){
		this.fillRatePerMs = fillRatePerMs;
		this.maxBudget = maxBudget;
		this.currentBudget = maxBudget;
		this.lastUpdateTime = System.currentTimeMillis();
	}
	
	public boolean consume(double amount){
		long msSinceLastUpdate = System.currentTimeMillis() - lastUpdateTime;
		currentBudget = Math.min(maxBudget, currentBudget + msSinceLastUpdate * fillRatePerMs);
		lastUpdateTime += msSinceLastUpdate;
		if (currentBudget >= amount) {
		      currentBudget -= amount;
		      return true;
		} else {
		      return false;
		}		
	}
	
}
