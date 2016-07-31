package com.ecommerce.flashsales;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.exception.MemcachedException;

/***
 * 
 * @author wuwesley
 * The inventory management service for the whole system.
 */
@RestController
@RequestMapping("/")
public class  InventoryManager {	
    @Autowired
    private MemcachedClient memcachedClient;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String allItemsKey = "xAllInventoryKey";
    private final String xNameSpace = "InventoryManager";
	FlashSalesAccessLogger fsAccessLogger = new FlashSalesAccessLogger();
	/*** rate limiter setting ***/
    @Value("${ratelimiter.consumeCount}")
	public double consumeCount;
    /***
     * Generate the md5 value for the pair of GoodsSku and Inventory no.
     * @param badguy
     * @return
     * @throws NoSuchAlgorithmException 
     */
    public String md5Hashing (String xNameSpace,String goodsSku) throws NoSuchAlgorithmException{
		String md5String = null;
		String clientPair = null;
		
		clientPair = goodsSku + ":" + xNameSpace;
		
		MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(clientPair.toString().getBytes());
        
        byte byteData[] = md.digest();
 
        //convert the byte to hex format method 1
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
         sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
        //System.out.println("Digest(in hex format):: " + sb.toString());
        md5String = sb.toString();
		return md5String;
	}
    /***
	 * Create a new the goods's inventory info
	 * Request sample : {"goodsSKU":"QT3456","goodsQuantity":100,"goodsPrice":99.9} and need set the customer header -H 'Content-Type:application/json'
	 * Response sample : {"goodsSKU":"QT3456","goodsQuantity":100,"goodsPrice":99.9}
     * @throws ParseException 
     * @throws NoSuchAlgorithmException 
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(method=RequestMethod.POST, value="/add", headers = "Accept=application/json")
	public GoodsInventory addGoods(@RequestBody GoodsInventory goodsInventory) throws ParseException, NoSuchAlgorithmException {
		long timeMillis = System.currentTimeMillis();
		long timeSeconds = TimeUnit.MILLISECONDS.toSeconds(timeMillis);
		int expirationValue = (int) (timeSeconds + 24*60*60*365);
		JSONObject jObj = new JSONObject();
		if (goodsInventory.getGoodsSKU().length() > 0 && goodsInventory.getGoodsSKU() != null){
 			try {
 				jObj.put("goodsSKU",goodsInventory.getGoodsSKU());
 				jObj.put("goodsQuantity",goodsInventory.getGoodsQuantity());
 				jObj.put("goodsPrice",goodsInventory.getGoodsPrice());
 				memcachedClient.set(md5Hashing(xNameSpace, goodsInventory.getGoodsSKU()), expirationValue, jObj.toString());
				updateAllItemsKey(goodsInventory.getGoodsSKU(),"ADD");
			} catch (TimeoutException e) {
				logger.error("TimeoutException");
			} catch (InterruptedException e) {
				logger.error("InterruptedException");
			} catch (MemcachedException e) {
				logger.error("MemcachedException");
			}
 		}
		return goodsInventory;
	}
	
	/***
	 * Get the goods's inventory info by the sku No.
	 * Request sample : http://localhost:8080/sku/{sku}
	 * Response sample : {"goodsSKU":"QT3456","goodsQuantity":100,"goodsPrice":99.9}
	 * @throws NoSuchAlgorithmException 
	 * @throws ParseException
	 */
	@RequestMapping(method = RequestMethod.GET, value="/sku/{sku}")	
	public GoodsInventory getGoods(@PathVariable("sku") String sku) throws NoSuchAlgorithmException, ParseException {
		Object mObject = null;
		GoodsInventory goodsInventory = new GoodsInventory() ;
		if (sku.length() > 0 && sku != null){
 			try {
 				mObject = memcachedClient.get(md5Hashing(xNameSpace,(String)sku));
 				if (mObject != null){
 					JSONParser parser = new JSONParser();
 					JSONObject json = (JSONObject) parser.parse(mObject.toString());
 					goodsInventory.setGoodsSKU(json.get("goodsSKU").toString());
 					goodsInventory.setGoodsQuantity(Integer.parseInt(json.get("goodsQuantity").toString()));
 					goodsInventory.setGoodsPrice(Double.parseDouble(json.get("goodsPrice").toString()));
 				}				
			} catch (TimeoutException e) {
				logger.error("TimeoutException");
			} catch (InterruptedException e) {
				logger.error("InterruptedException");
			} catch (MemcachedException e) {
				logger.error("MemcachedException");
			}
 		}
		return goodsInventory;
	}
	/***
	 * update the goods's inventory info
	 * Request sample : {"goodsSKU":"QT3456","goodsQuantity":100} and need set the customer header -H 'Content-Type:application/json'
	 * Response sample : {"goodsSKU":"QT3456","goodsQuantity":100}
	 * @throws NoSuchAlgorithmException 
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(method = RequestMethod.PUT,value="/update",headers = "Accept=application/json")	
	public GoodsInventory updateGoods(@RequestBody GoodsInventory goodsInventory) throws NoSuchAlgorithmException {
		long timeMillis = System.currentTimeMillis();
		long timeSeconds = TimeUnit.MILLISECONDS.toSeconds(timeMillis);
		int expirationValue = (int) (timeSeconds + 24*60*60*365);
		JSONObject jObj = new JSONObject();
		if (goodsInventory.getGoodsSKU().length() > 0 && goodsInventory.getGoodsSKU() != null){
 			try {
 				jObj.put("goodsSKU",goodsInventory.getGoodsSKU());
 				jObj.put("goodsQuantity",goodsInventory.getGoodsQuantity());
 				jObj.put("goodsPrice",goodsInventory.getGoodsPrice());
 				memcachedClient.replace(md5Hashing(xNameSpace, goodsInventory.getGoodsSKU()), expirationValue, jObj.toString());
			} catch (TimeoutException e) {
				logger.error("TimeoutException");
			} catch (InterruptedException e) {
				logger.error("InterruptedException");
			} catch (MemcachedException e) {
				logger.error("MemcachedException");
			}
 		}
		return goodsInventory;
	}
	/***
	 * Delete the goods's inventory info
	 * Request sample : http://localhost:8080/delete/sku/{sku}
	 * Response sample : {"goodsSKU":"NULL","goodsQuantity":NULL}
	 * @throws ParseException 
	 * @throws NoSuchAlgorithmException 
	 */
	@RequestMapping(method=RequestMethod.DELETE, value="/delete/sku/{sku}")
	public GoodsInventory removeGoods(@PathVariable("sku") String sku) throws ParseException, NoSuchAlgorithmException {
		GoodsInventory goodsInventory = new GoodsInventory() ;
		if (sku.length() > 0 && sku != null){
 			try {
				memcachedClient.delete(md5Hashing(xNameSpace, sku));
				updateAllItemsKey(sku,"DELETE");
			} catch (TimeoutException e) {
				logger.error("TimeoutException");
			} catch (InterruptedException e) {
				logger.error("InterruptedException");
			} catch (MemcachedException e) {
				logger.error("MemcachedException");
			}
 		}
		return goodsInventory;
	}
	/***
	 * find all items
	 * Request sample : http://localhost:8080/all
	 * Response sample : {"goodsSKU":"NULL","goodsQuantity":NULL}
	 * @throws ParseException 
	 * @throws NoSuchAlgorithmException 
	 */
	@RequestMapping(method = RequestMethod.GET, value="/all")
	public List<GoodsInventory> findAllItems() throws NoSuchAlgorithmException, ParseException{
		List<GoodsInventory> glist = new ArrayList<>();
		List<String> mlist = null;
		Object mObject = null;
		try {
			mObject = memcachedClient.get(allItemsKey);
			if (mObject != null){
				mlist = new ArrayList<String>(Arrays.asList(mObject.toString().split(",")));
				for(String mSku:mlist){
					if (mSku.trim().length() > 0) {glist.add(getGoods(mSku));}
				}
			}else{
				glist.add(new GoodsInventory());
			}
		} catch (TimeoutException e) {
			logger.error("TimeoutException");
		} catch (InterruptedException e) {
			logger.error("InterruptedException");
		} catch (MemcachedException e) {
			logger.error("MemcachedException");
		}
		return glist;
		
	}
	/***
	 * store the key index for the whole inventory system
	 * allItemsKey(xAllItemsKey):xxx,xxxx,xxxx
	 * @throws ParseException 
	 */
	public void updateAllItemsKey(String theItemKey,String mode) throws ParseException {
		Object mObject = null;
		List<String> mlist = null;
		String tmpItemsKey = null;
		long timeMillis = System.currentTimeMillis();
		long timeSeconds = TimeUnit.MILLISECONDS.toSeconds(timeMillis);
		int expirationValue = (int) (timeSeconds + 24*60*60*365);
		try {
			mObject = memcachedClient.get(allItemsKey);
			if (mObject != null){
				mlist = new ArrayList<String>(Arrays.asList(mObject.toString().split(",")));
				if (mode == "ADD"){
					//avoid the duplicated key issue
					if (mlist.contains(theItemKey) == false){mlist.add(theItemKey);}
				}else{
					mlist.remove(theItemKey);
				}
				tmpItemsKey = mlist.toString().replace("[", "").replace("]", "").replace(" ", "").trim() + ",";
				memcachedClient.replace(allItemsKey, expirationValue, tmpItemsKey);
			}else{
				tmpItemsKey = theItemKey + ",";
				memcachedClient.add(allItemsKey, expirationValue, tmpItemsKey);
			}
		} catch (TimeoutException e) {
			logger.error("TimeoutException");
		} catch (InterruptedException e) {
			logger.error("InterruptedException");
		} catch (MemcachedException e) {
			logger.error("MemcachedException");
		}
	}
	/***
	 * do the inventory validation
	 * Request sample : http://localhost:8080/validate/sid/{sid}/sku/{sku}/quantity/{quantity}
	 * Response sample : {"goodsSKU":"QT3456","goodsQuantity":100,"isAllowed":true}
	 * @throws ParseException 
	 * @throws JsonProcessingException 
	 * @throws NoSuchAlgorithmException 
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(method = RequestMethod.GET,value = "/validate/sid/{sid}/sku/{sku}/quantity/{quantity}")
	public InventoryValidationR doInventoryValidation(HttpServletRequest httpRequest, HttpServletResponse httpResponse, @PathVariable("sid") String sid, @PathVariable("sku") String sku, @PathVariable("quantity") int quantity) throws ParseException, JsonProcessingException{
		GoodsInventory goodsInventory = null;
		InventoryValidationR inventoryValidationR = new InventoryValidationR();
		inventoryValidationR.setSessionID(sid);
		inventoryValidationR.setGoodsSKU(sku);
		inventoryValidationR.setGoodsQuantity(quantity);
		inventoryValidationR.setIsAllowed(false);
		long startTime = System.currentTimeMillis();
		/*** generate request parameters */
		JSONObject paramsJSON = new JSONObject();
		paramsJSON.put("sid", sid);
		paramsJSON.put("sku", sku);
		paramsJSON.put("quantity", quantity);
		
		if (inventoryValidationR.getSessionID().length() >0 && inventoryValidationR.goodsSKU.length() > 0 && inventoryValidationR.getGoodsQuantity() > 0 ){
			/*** rate limiter checking ***/
			if (InventoryManagerApplication.rateLimiter.consume(consumeCount) == false){
				inventoryValidationR.setIsThrottled(true);
				long endTime = System.currentTimeMillis();
				fsAccessLogger.doAccessLog(httpRequest, httpResponse, inventoryValidationR.getSessionID(), CurrentStep.INVENTORYMANAGER.msgBody(), paramsJSON.toString(), endTime-startTime, inventoryValidationR);
				return inventoryValidationR;
			}
			try {
				goodsInventory = getGoods(sku);				
				if (goodsInventory.getGoodsQuantity() >= inventoryValidationR.getGoodsQuantity()){
					inventoryValidationR.setTotalQuantity(goodsInventory.getGoodsQuantity());
					inventoryValidationR.setIsAllowed(true);
				} 
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				logger.error("NoSuchAlgorithmException:" + inventoryValidationR.getSessionID());
			}finally{
				long endTime = System.currentTimeMillis();
				fsAccessLogger.doAccessLog(httpRequest, httpResponse, inventoryValidationR.getSessionID(), CurrentStep.INVENTORYMANAGER.msgBody(), paramsJSON.toString(), endTime-startTime, inventoryValidationR);
			}
 		}else{
 			long endTime = System.currentTimeMillis();
			fsAccessLogger.doAccessLog(httpRequest, httpResponse, inventoryValidationR.getSessionID(), CurrentStep.INVENTORYMANAGER.msgBody(), paramsJSON.toString(), endTime-startTime, inventoryValidationR);
 		}
		return inventoryValidationR;
	}
}
