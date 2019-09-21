package com.erdemsiyam.memorizeyourwords.util;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClient.BillingResponseCode;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import java.util.ArrayList;
import java.util.List;

public class DonationPurchaseHelper {

    private static BillingClient mBillingClient;

    public static void start(AppCompatActivity activity, DonationType selectedDonation) {

        mBillingClient = BillingClient.newBuilder(activity).setListener(new PurchasesUpdatedListener() {
            @Override
            public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {
                if (billingResult.getResponseCode() == BillingResponseCode.OK && purchases != null) {

                    // Satın alma sonu.

                } else if (billingResult.getResponseCode() == BillingResponseCode.USER_CANCELED) {

                    // kullanıcı iptali

                } else {

                    // türlü diğer sorunlar.

                }
            }
        })
        .enablePendingPurchases()
        .build();

        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {

                /* Google play' ulaşım sağlanılırsa. */
                if (billingResult.getResponseCode() == BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    List<String> skuList = new ArrayList<>();
                    skuList.add(selectedDonation.value);
                    SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
                    params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
                    mBillingClient.querySkuDetailsAsync(params.build(), new SkuDetailsResponseListener() {
                        @Override
                        public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> skuDetailsList) {

                            /* Satın alınacak öğe google play'den kontrol edilir harbi o mu. */
                            if (billingResult.getResponseCode() == BillingResponseCode.OK && skuDetailsList != null) {

                                SkuDetails selectedSku = null;
                                for (SkuDetails skuDetails : skuDetailsList) {
                                    if(selectedDonation.value.equalsIgnoreCase(skuDetails.getSku())){
                                        selectedSku = skuDetails;
                                        break;
                                    }
                                }
                                if(selectedSku == null) return; // MSG : Öğe PlayStore'dan silinmiş.

                                // Retrieve a value for "skuDetails" by calling querySkuDetailsAsync().
                                BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                                        .setSkuDetails(selectedSku) // alınacak öğe eklendi
                                        .build();

                                // kişiye satın alma penceresi çıkarıldı
                                BillingResult billingResult2 = mBillingClient.launchBillingFlow(activity,flowParams);

                                // alma sonucu
                                if(billingResult2.getResponseCode() == BillingResponseCode.OK){
                                    // Başarılı alım ?
                                }

                            }
                        }
                    });
                }
            }
            @Override
            public void onBillingServiceDisconnected() {
                // MSG : Ödeme sistemi şuanda geçerli değil
            }
        });
    }
}
