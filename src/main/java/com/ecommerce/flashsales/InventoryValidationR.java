package com.ecommerce.flashsales;


/***
 * 
 * @author wuwesley
 * the class for good inventory info
 */

public class InventoryValidationR {
	public String sessionID;
	public String goodsSKU;
	public int goodsQuantity;
	public int totalQuantity;
	public boolean isAllowed = true;
	public boolean isThrottled = false;
	public String version = "1.0";

	
	public String getSessionID() {
		return sessionID;
	}
	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}
	public String getGoodsSKU() {
		return goodsSKU;
	}
	public void setGoodsSKU(String goodsSKU) {
		this.goodsSKU = goodsSKU;
	}
	public int getGoodsQuantity() {
		return goodsQuantity;
	}
	public void setGoodsQuantity(int goodsQuantity) {
		this.goodsQuantity = goodsQuantity;
	}	
	
	public int getTotalQuantity() {
		return totalQuantity;
	}
	public void setTotalQuantity(int totalQuantity) {
		this.totalQuantity = totalQuantity;
	}	
	public void setIsAllowed(boolean isAllowed) {
		this.isAllowed = isAllowed;
	}	
	public boolean getIsAllowed() {
		return isAllowed;
	}
	public boolean getIsThrottled() {
		return isThrottled;
	}
	public void setIsThrottled(boolean isThrottled) {
		this.isThrottled = isThrottled;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	@Override
	public String toString() {
		return "InventoryValidationR [sessionID=" + sessionID + ", goodsSKU=" + goodsSKU + ", goodsQuantity="
				+ goodsQuantity + ", totalQuantity=" + totalQuantity + ", isAllowed=" + isAllowed + ", isThrottled="
				+ isThrottled + ", version=" + version + "]";
	}
	
		
}
