/*
    CovApp, a tracking based messaging app preserving privacy
    Copyright (C) 2020 DI Michael Kuen, http://www.xudis.com/

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.xudis.msg;

import java.io.IOException;
import java.io.PrintWriter;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

@WebServlet("/Gate")
@MultipartConfig(fileSizeThreshold=1024*300, 	
                 maxFileSize=1024*300,      	
                 maxRequestSize=1024*300)   	
public class Gate extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

    /**
     * Default constructor. 
     */
    public Gate() {
    	super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    private String getArg(HttpServletRequest request, String name) throws IOException, ServletException {
	    	
		Part part = request.getPart(name);
		int i = (int)part.getSize();
		
		byte[] data = new byte[i];
		part.getInputStream().read(data);
		
		return new String(data);
    }
	    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
    	PrintWriter pw = response.getWriter();

		response.setStatus(HttpServletResponse.SC_OK);

		String action;
		
		boolean dbg = false;
		
		String strId = request.getParameter("id");
		if(strId!=null) {
			action = "READ";
		}
		else
			action = getArg(request,"action");
		
		try {
			InitialContext ic = new InitialContext();
			javax.naming.Context xmlContext = (javax.naming.Context) ic.lookup("java:app/XudisMessaging"); 

			Messages messages = (Messages)xmlContext.lookup("Messages");

			if(action.equalsIgnoreCase("BCAST")) {
				int id = messages.send(getArg(request,"msg"));
				response.setContentType("application/json");
				pw.print(id);
			}
			else if(action.equalsIgnoreCase("GETMAX")) {
				
				Object[] minMax = messages.getMax();
				response.setContentType("application/xml");
				
				pw.print("<min>"+minMax[0]+"</min><max>"+minMax[1]+"</max>");
			}				
			else if(action.equalsIgnoreCase("READ")) {
				
				int id;
				
				if(strId!=null)
					id = Integer.parseInt(strId);
				else
					id = Integer.parseInt(getArg(request,"id"));
				
				String msg = messages.read(id);
				
				response.setContentType("application/xml");
				pw.print(msg);
			}				
			
		} catch (NamingException e) {
			e.printStackTrace();
		}
		pw.flush();
	    response.flushBuffer();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
