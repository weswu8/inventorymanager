package com.ecommerce.flashsales;


/***
 * 
 * @author wuwesley
 * the class for good inventory info
 */

public class GoodsInventory {
	public String goodsSKU;
	public int goodsQuantity;
	public double goodsPrice;
	
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
	public double getGoodsPrice() {
		return goodsPrice;
	}
	public void setGoodsPrice(double goodsPrice) {
		this.goodsPrice = goodsPrice;
	}
	@Override
	public String toString() {
		return "GoodsInventory [goodsSKU=" + goodsSKU + ", goodsQuantity=" + goodsQuantity + ", goodsPrice="
				+ goodsPrice + "]";
	}
	
}
