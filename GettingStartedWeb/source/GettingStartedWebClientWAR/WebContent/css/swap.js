/**
 * COPYRIGHT LICENSE:  This information contains sample code provided in source code form. 
 * You may copy, modify, and distribute these sample programs in any form without payment to I
 * BM for the purposes of developing, using, marketing or distributing application programs conforming 
 * to the application programming interface for the operating platform for which the sample code is written. 
 * Notwithstanding anything to the contrary,  IBM PROVIDES THE SAMPLE SOURCE CODE ON AN "AS IS" BASIS AND IBM 
 * DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, ANY IMPLIED WARRANTIES OR 
 * CONDITIONS OF MERCHANTABILITY, SATISFACTORY QUALITY, FITNESS FOR A PARTICULAR PURPOSE, TITLE, AND ANY WARRANTY 
 * OR CONDITION OF NON-INFRINGEMENT.  IBM SHALL NOT BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL OR 
 * CONSEQUENTIAL DAMAGES ARISING OUT OF THE USE OR OPERATION OF THE SAMPLE SOURCE CODE.  IBM HAS NO OBLIGATION TO 
 * PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS OR MODIFICATIONS TO THE SAMPLE SOURCE CODE.  
 *
 * This sample program is provided AS IS and may be used, executed, copied and
 * modified without royalty payment by customer (a) for its own instruction and
 * study, (b) in order to develop applications designed to run with an IBM
 * WebSphere product, either for customer's own internal use or for redistribution
 * by customer, as part of such an application, in customer's own products. 
 * 
 * 5724-X67, 5655-V66 (C) COPYRIGHT International Business Machines Corp. 2012
 * All Rights Reserved * Licensed Materials - Property of IBM
 */

function displayDiv(myDiv) {
	
	// clear all divs
	var e1 = document.getElementById("panelEndpoints1");
	var e2 = document.getElementById("panelDomain1");
	
	if (e1 != null) {
		e1.style.display="none";
	}
	if (e2 != null) {
		e2.style.display="none";
	}
	
	// display only the div we want
	var div = myDiv+"1";
	var myElement = document.getElementById(div);
	if (myElement != null) {
		myElement.style.display="block";
	}
	
	// clear all radio buttons
	var r1 = document.getElementById("panelEndpoints2");
	var r2 = document.getElementById("panelDomain2");
	
	r1.checked = false;
	r2.checked = false;
	
	// check only the radio button we want
	var div = myDiv+"2";
	var myRadio = document.getElementById(div);
	if (myRadio != null) {
		myRadio.checked = true;
	}
}