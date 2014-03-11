package com.mopon.webservice;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.ws.rs.PathParam;

import com.mopon.entity.Order;

@WebService
@SOAPBinding(style = Style.RPC)
public interface IOrderProcess {
	
	public Order getOrder(@PathParam("id") String orderId);
	
}
