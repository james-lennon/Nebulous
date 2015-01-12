package com.jameslennon.nebulous;

import java.util.HashMap;

import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.foundation.NSError;
import org.robovm.apple.storekit.SKPaymentTransaction;
import org.robovm.apple.storekit.SKProduct;
import org.robovm.apple.storekit.SKRequest;
import org.robovm.bindings.inapppurchase.InAppPurchaseListener;
import org.robovm.bindings.inapppurchase.InAppPurchaseManager;

import com.jameslennon.nebulous.game.Globals;
import com.jameslennon.nebulous.util.IAPClient;
import com.jameslennon.nebulous.util.ShipData;
import com.jameslennon.nebulous.util.UserData;

public class IAPManager implements IAPClient {

	private static final int BUY = 0, RESTORE = 1;

	private InAppPurchaseManager iapManager;
	private HashMap<String, SKProduct> appStoreProducts;
	private int intent;

	@Override
	public void init() {
		if (iapManager == null) {
			iapManager = new InAppPurchaseManager(new InAppPurchaseListener() {

				@Override
				public void productsReceived(SKProduct[] products) {
					System.out.println("received products...");
					appStoreProducts = new HashMap<String, SKProduct>();
					for (int i = 0; i < products.length; i++) {
						appStoreProducts.put(products[i].getProductIdentifier()
								.toString(), products[i]);
						System.out.println(products[i].getProductIdentifier()
								+ "," + products[i].getPrice());
					}
					if (intent == BUY) {
						System.out.println("buying...");
						if (iapManager.canMakePayments()
								&& appStoreProducts != null) {
							iapManager.purchaseProduct(appStoreProducts
									.get("NebulousAllShips"));
						}
					}else if(intent == RESTORE){
						System.out.println("restoring...");
						iapManager.restoreTransactions();
					}
					intent = -1;
				}

				@Override
				public void productsRequestFailed(SKRequest request,
						NSError error) {
					System.out.println("request failed: " + error);
					Globals.getNebulous().setState(Nebulous.STATE_PURCHASE);
				}

				@Override
				public void transactionCompleted(
						SKPaymentTransaction transaction) {
					String productId = transaction.getPayment()
							.getProductIdentifier().toString();
					System.out.println("finished transaction: " + productId);
					if (productId.equals("NebulousAllShips")) {
						System.out.println("unlocking pro!");
						UserData.setPro(true);
						ShipData.init();
					}
					Globals.getNebulous().setState(Nebulous.STATE_PURCHASE);
				}

				@Override
				public void transactionFailed(SKPaymentTransaction transaction,
						NSError error) {
					System.out.println("transaction failed: " + error);
					Globals.getNebulous().setState(Nebulous.STATE_PURCHASE);
				}

				@Override
				public void transactionRestored(SKPaymentTransaction transaction) {
					String productId = transaction.getPayment()
							.getProductIdentifier().toString();
					System.out.println("restoring transaction: " + productId);
					if (productId.equals("NebulousAllShips")) {
						System.out.println("unlocking pro!");
						UserData.setPro(true);
						ShipData.init();
						UserData.save();
					}
					Globals.getNebulous().setState(Nebulous.STATE_PURCHASE);
				}

				@Override
				public void transactionRestoreFailed(
						NSArray<SKPaymentTransaction> transactions,
						NSError error) {
					Globals.getNebulous().setState(Nebulous.STATE_PURCHASE);
				}

			});
			intent = -1;
		}
	}

	@Override
	public void goPro() {
		init();
		intent = BUY;
		iapManager.requestProducts("NebulousAllShips");
	}

	@Override
	public void restore() {
		init();
		intent = RESTORE;
		iapManager.requestProducts("NebulousAllShips");
	}

}
