/*
    GNU GENERAL LICENSE
    Copyright (C) 2014 - 2018 Lobo Evolution

    This program is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    verion 3 of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    General License for more details.

    You should have received a copy of the GNU General Public
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
    

    Contact info: ivan.difrancesco@yahoo.it
 */

package org.loboevolution.html.domimpl;


import org.loboevolution.w3c.html.HTMLHeadElement;

/**
 * The Class HTMLHeadElementImpl.
 */
public class HTMLHeadElementImpl extends HTMLAbstractUIElement implements HTMLHeadElement {

	/**
	 * Instantiates a new HTML head element impl.
	 *
	 * @param name
	 *            the name
	 */
	public HTMLHeadElementImpl(String name) {
		super(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.loboevolution.w3c.html.HTMLHeadElement#getProfile()
	 */
	@Override
	public String getProfile() {
		return this.getAttribute(PROFILE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.loboevolution.w3c.html.HTMLHeadElement#setProfile(java.lang.String)
	 */
	@Override
	public void setProfile(String profile) {
		this.setAttribute(PROFILE, profile);

	}
}
