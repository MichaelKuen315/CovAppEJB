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

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class Messages {

	@PersistenceContext(unitName="xudis.msg")
    EntityManager em;

	public int send(String xmlString) {
		
	  Message msg = new Message();
	  msg.setMessage(xmlString);
	  
      em.persist(msg);
      em.flush();
      
      return msg.getId();
	}
	
	public Object[] getMax() {

		Query q = em.createQuery("SELECT min(a.id),max(a.id) FROM Message a");
    	
		Object o = q.getSingleResult();
		
		return (Object[])o;
	}
	
	public String read(int id) {
		
		
		Query q = em.createQuery("SELECT a FROM Message a where a.id = :id");
		q.setParameter("id",id);
		
		Message msg = (Message)q.getSingleResult();
		
		return msg.getMessage(); //(String)o;
	}
}
