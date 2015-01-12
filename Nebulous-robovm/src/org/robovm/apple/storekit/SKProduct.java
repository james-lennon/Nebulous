/*
 * Copyright (C) 2014 Trillian Mobile AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.robovm.apple.storekit;

/*<imports>*/
import org.robovm.apple.foundation.NSArray;
import org.robovm.apple.foundation.NSDecimalNumber;
import org.robovm.apple.foundation.NSLocale;
import org.robovm.apple.foundation.NSNumber;
import org.robovm.apple.foundation.NSObject;
import org.robovm.objc.ObjCRuntime;
import org.robovm.objc.annotation.NativeClass;
import org.robovm.objc.annotation.Property;
import org.robovm.rt.bro.annotation.Library;
import org.robovm.rt.bro.ptr.Ptr;
/*</imports>*/

/**
 * <div class="javadoc"></div>
 */
/*<annotations>*/@Library("StoreKit") @NativeClass/*</annotations>*/
/*<visibility>*/public/*</visibility>*/ class /*<name>*/SKProduct/*</name>*/ 
    extends /*<extends>*/NSObject/*</extends>*/ 
    /*<implements>*//*</implements>*/ {

    /*<ptr>*/public static class SKProductPtr extends Ptr<SKProduct, SKProductPtr> {}/*</ptr>*/
    /*<bind>*/static { ObjCRuntime.bind(SKProduct.class); }/*</bind>*/
    /*<constants>*//*</constants>*/
    /*<constructors>*/
    public SKProduct() {}
    protected SKProduct(SkipInit skipInit) { super(skipInit); }
    /*</constructors>*/
    /*<properties>*/
    @Property(selector = "localizedDescription")
    public native String getLocalizedDescription();
    @Property(selector = "localizedTitle")
    public native String getLocalizedTitle();
    @Property(selector = "price")
    public native NSDecimalNumber getPrice();
    @Property(selector = "priceLocale")
    public native NSLocale getPriceLocale();
    @Property(selector = "productIdentifier")
    public native String getProductIdentifier();
    @Property(selector = "isDownloadable")
    public native boolean isDownloadable();
    @Property(selector = "downloadContentLengths")
    public native NSArray<NSNumber> getDownloadContentLengths();
    @Property(selector = "downloadContentVersion")
    public native String getDownloadContentVersion();
    /*</properties>*/
    /*<members>*//*</members>*/
    /*<methods>*/
    /*</methods>*/
}
