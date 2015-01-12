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
package org.robovm.apple.social;

/*<imports>*/
import org.robovm.rt.bro.Bro;
import org.robovm.rt.bro.annotation.GlobalValue;
import org.robovm.rt.bro.annotation.Library;
/*</imports>*/

/**
 * <div class="javadoc"></div>
 */
/*<annotations>*/@Library("Social")/*</annotations>*/
/*<visibility>*/public/*</visibility>*/ class /*<name>*/SLServiceType/*</name>*/ 
    extends /*<extends>*/Object/*</extends>*/ 
    /*<implements>*//*</implements>*/ {

    /*<ptr>*//*</ptr>*/
    /*<bind>*/static { Bro.bind(SLServiceType.class); }/*</bind>*/
    /*<constants>*//*</constants>*/
    /*<constructors>*/
    /*</constructors>*/
    /*<properties>*/
    /*</properties>*/
    /*<members>*//*</members>*/
    /*<methods>*/
    @GlobalValue(symbol="SLServiceTypeTwitter", optional=true)
    public static native String Twitter();
    @GlobalValue(symbol="SLServiceTypeFacebook", optional=true)
    public static native String Facebook();
    @GlobalValue(symbol="SLServiceTypeSinaWeibo", optional=true)
    public static native String SinaWeibo();
    @GlobalValue(symbol="SLServiceTypeTencentWeibo", optional=true)
    public static native String TencentWeibo();
    @GlobalValue(symbol="SLServiceTypeLinkedIn", optional=true)
    public static native String LinkedIn();
    /*</methods>*/
}
