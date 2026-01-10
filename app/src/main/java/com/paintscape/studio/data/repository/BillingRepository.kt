package com.paintscape.studio.data.repository

import android.app.Activity
import android.content.Context
import com.paintscape.studio.billing.BillingClientWrapper
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BillingRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val billingClientWrapper: BillingClientWrapper
) {
    // This tells the UI if the user is a Premium member or not
    val premiumStatus: Flow<Boolean> = billingClientWrapper.isPremiumUser

    // This is a placeholder for Art Packs (like "The Galaxy Pack")
    // For now, we'll keep it empty
    val purchasedArtPacks: Flow<Set<String>> = MutableStateFlow(emptySet())

    fun launchPurchaseFlow(activity: Activity, productId: String) {
        billingClientWrapper.launchPurchaseFlow(activity, productId)
    }

    fun restorePurchases() {
        billingClientWrapper.queryPurchases()
    }
}